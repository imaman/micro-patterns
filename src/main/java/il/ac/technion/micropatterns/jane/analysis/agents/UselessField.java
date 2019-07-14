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

import il.ac.technion.micropatterns.jane.analysis.misc.InheritanceDescendingAgent;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.InstructionParser;
import il.ac.technion.micropatterns.jane.lib.ObjectToInt;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.PUTFIELD;

public class UselessField extends InheritanceDescendingAgent
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
   
   public static final String NAME = "useless";

   public UselessField()
   {
      super(NAME, DescCP.USLS);
   }

   
   
   private HashSet touchedFields(JavaClass jc)
   {
      HashSet  result = new HashSet ();

      ConstantPoolGen cpg = new ConstantPoolGen(jc.getConstantPool());
            
      Method[] m = jc.getMethods();
      for(int i = 0; i < m.length; ++i)
      {
         Method curr = m[i];

         InstructionParser ip = new InstructionParser(curr); 
      
         for(Iterator j = ip.iterator(); j.hasNext(); )
         {
            Instruction ins = (Instruction) j.next();
            if(ins instanceof PUTFIELD)
            {               
               PUTFIELD pf = (PUTFIELD) ins;
               result.add(pf.getFieldName(cpg));
            }
            if(ins instanceof GETFIELD)
            {               
               GETFIELD gf = (GETFIELD) ins;
               result.add(gf.getFieldName(cpg));
            }
         }                           
      }
      
      return result;
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
         this.main_result_.set_decision(ch, false);
         return;
      }            
      
      HashSet fs = new HashSet();
      JavaClass jc = ce.jc();
      
      Field[] fields = jc.getFields();
      for(int i = 0; i < fields.length; ++i)
      {
         Field f = fields[i];
         if(!f.isPrivate())
            continue;
         
         if(f.isStatic())
            continue;
         
         
         // f is a private instance field
         fs.add(f.getName());
      }
      
      HashSet touched = touchedFields(jc);
      fs.removeAll(touched);
       
       if(fs.size() > 0)
          this.main_result_.set_decision(ch, true);
   }      
}
