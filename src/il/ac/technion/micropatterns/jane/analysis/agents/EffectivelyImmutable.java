// Copyright (c) 2004-2005, Yossi Gil, Itay Maman
// The Department of Computer Science
// Technion - Israel Institute of Technology
//
// Redistribution and use in source and binary forms, with or without 
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, 
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice, 
// this list of conditions and the following disclaimer in the documentation 
// and/or other materials provided with the distribution.
//
//
// THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, 
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
// FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY 
// DIRECT,  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.










package il.ac.technion.micropatterns.jane.analysis.agents;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.analysis.misc.Decision;
import il.ac.technion.micropatterns.jane.analysis.misc.InheritanceDescendingAgent;
import il.ac.technion.micropatterns.jane.analysis.misc.Result;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.InstructionParser;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.lib.ObjectToInt;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.util.Iterator;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.PUTFIELD;

public class EffectivelyImmutable extends InheritanceDescendingAgent
{
   private ObjectToInt field_count_ = new ObjectToInt()
      {
         public void put(Object k, int n)
         {
//            JimaMisc.log().println("MAP(" + k + ") <-" + n);
            super.put(k, n);
         }
      };
      
   private static final int UNDEFINED_YET = -1000;
   
   private Result box_;
   private Result imm_box_;
   private Result imm_;
   

   public static final String NAME = "immutable";

   public EffectivelyImmutable()
   {
      super(NAME, DescCP.IMM);
      imm_ = main_result_;
      box_ = Result.new_result(this, DescCP.BOX);
      imm_box_ = Result.new_result(this, DescCP.IMB);            
   }

   
   /**
    * @see il.ac.technion.micropatterns.jane.analysis.misc.InheritanceDescendingAgent#inspect(il.ac.technion.micropatterns.jane.elements.ClassHandle)
    */
   protected boolean inspect(ClassHandle h)
   {
      check(h);
      return true;
   }
         
   private void check(ClassHandle ch) 
   {
      ClassSpec ce = ch.typed_value(xmodel_);
      
      if(ce.is_java_object())
      {
         field_count_.put(ch, 0);
         
         imm_.set_decision(ch, true);
         imm_box_.set_decision(ch, false);
         box_.set_decision(ch, false);
         
         return;
      }            

      boolean is_imm = true;
      
      // ...Else:      
      if(ce.jc().isInterface())
      {
         field_count_.put(ch, 0);
         is_imm = false;
      }         


      // Find declared fields
      int non_private_fields = 0;
      int declared_fields = 0;
      Field[] fields = ce.jc().getFields();
      for(int i = 0; i < fields.length; ++i)
      {
         Field curr = fields[i];
         if(curr.isStatic())
            continue;
            
         if(!curr.isPrivate())
            non_private_fields += 1;
         
         declared_fields += 1;            
      }         


      // Find inherited fields
      ClassHandle spr = ce.get_first_super();
      int inherited_fields = field_count_.get(spr, UNDEFINED_YET);
      JimaMisc.ensure(inherited_fields != UNDEFINED_YET, "cp=" + ch 
         + ", spr=" + spr + ", size=" + field_count_.size());      
            
      int total = declared_fields + inherited_fields;
      field_count_.put(ch, total);

      boolean is_box = (total == 1);
      
      if(total == 0)
      {
         // No fields => It is a stateless
         box_.set_decision(ch, false);
         imm_box_.set_decision(ch, false);
         imm_.set_decision(ch, false);
         
         return;
      }

      if(non_private_fields > 0)
         is_imm = false;
      if(inherited_fields > 0)
      {
         Decision d1 = imm_.get_decision(spr);
         JimaMisc.ensure(d1.is_defined());

         Decision d2 = imm_box_.get_decision(spr);
         JimaMisc.ensure(d2.is_defined());
         
         if(d1.is_negative() && d2.is_negative())
            is_imm = false;
      }         

      //
      // If super-types are immutable, make sure the fields of the "current"
      // class are only changed by the constructor
      //
      
      int putfield_count = 0;

      if(is_imm)
      {
         ConstantPoolGen cpg = new ConstantPoolGen(ce.jc().getConstantPool());
               
         Method[] m = ce.jc().getMethods();
         for(int i = 0; i < m.length; ++i)
         {
            Method curr = m[i];
            if(JavaSpec.is_constructor(curr))
               continue; // Ignore constructors
   
            InstructionParser ip = new InstructionParser(curr); 
         
            for(Iterator j = ip.iterator(); j.hasNext(); )
            {
               Instruction ins = (Instruction) j.next();
               if(!(ins instanceof PUTFIELD))
                  continue;
                  
               PUTFIELD pf = (PUTFIELD) ins;
               String class_name = pf.getClassName(cpg);
               
               if(ch.get_name().equals(class_name))
                  putfield_count += 1;
            }                           
         }         
      }
      
      if(putfield_count > 0)
         is_imm = false;

      boolean is_imm_box = is_box && is_imm;
      
      imm_box_.set_decision(ch, is_imm_box);
      
      boolean b = !is_imm_box && is_box;
      box_.set_decision(ch, b);
      
      b = !is_imm_box && is_imm;
      imm_.set_decision(ch, b);
   }      

}
