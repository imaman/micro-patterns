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
import il.ac.technion.micropatterns.jane.analysis.misc.Result;
import il.ac.technion.micropatterns.jane.analysis.misc.VectorOfFields;
import il.ac.technion.micropatterns.jane.analysis.misc.VectorOfMethods;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.InstructionParser;
import il.ac.technion.micropatterns.jane.lib.JaneMisc;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.lib.MethodSelector;
import il.ac.technion.micropatterns.jane.lib.ProvidedMethods;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.util.Iterator;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;

public class Self extends InheritanceDescendingAgent
{
   public static final String NAME = "Limited self";
   protected Result recur_result_;

//   private Result nomethods_;

   public Self()
   {
      super(NAME, DescCP.LIMITED_SELF);
      recur_result_ = Result.new_result(this, DescCP.RECURSIVE);
      
   }

   /**
    * @see il.ac.technion.micropatterns.jane.analysis.misc.InheritanceDescendingAgent#inspect(il.ac.technion.micropatterns.jane.elements.ClassProxy)
    */
   protected boolean inspect(ClassHandle cp)
   {
      boolean b = is_recursive(cp);
      recur_result_.set_decision(cp, b);
      
      b = is_limited_self(cp);
      main_result_.set_decision(cp, b);
      
      return true;
   }
   
   
   private boolean is_limited_self(ClassHandle cp)
   {      
      ClassSpec ce = cp.typed_value(xmodel_);
      
      ClassHandle spr_ch = ce.get_first_super();
      if(spr_ch == null)
         return false;
      
      if(ce.jc().isInterface())
         return false;
      
      ProvidedMethods pm = new ProvidedMethods(xmodel_, cp, false);
      
      VectorOfMethods vom = af_.get_inherited().get_methods();
      int top = vom.size();
      
//      String this_class = ce.name();
      ConstantPoolGen cpg = ce.constant_pool();
      
      Field[] fields = ce.jc().getFields();
      for(int i = 0; i < fields.length; ++i)
      {
         Field f = fields[i];
         if(!f.isStatic())
            return false;         
      }
      
      Method[] methods = ce.jc().getMethods();
      for(int i = 0; i < methods.length; ++i)
      {
         Method m = methods[i];
         if(m.isAbstract())
            continue;
         
         if(JavaSpec.is_constructor(m))
            continue; // Ignore "invoke" instructions within constructors

         InstructionParser ip = new InstructionParser(m);
         for(Iterator j = ip.iterator(); j.hasNext(); )
         {
            Instruction curr = (Instruction) j.next();
            
            if(!(curr instanceof INVOKEVIRTUAL))
               continue;
            
            // ...Else: Found an invocation
            INVOKEVIRTUAL iv = (INVOKEVIRTUAL) curr;
            
            String receiver_type = iv.getClassName(cpg);
            boolean found = pm.has_class(receiver_type);
            if(!found)
               continue;
            
            MethodSelector invoked = new MethodSelector(iv, cpg);
            for(int k = 0; k < top; ++k)
            {
               Method other_method = vom.at(k);
               boolean alreadyDefined = invoked.matches(other_method);
               if(!alreadyDefined)
                  return false;               
            }
         }                     
      }         
      
      return true;
   }         
   
   
   public boolean is_recursive(ClassHandle cp)
   {      
      ClassSpec ce = cp.typed_value(xmodel_);
      JavaClass jc = ce.jc();
      
      VectorOfFields vof = new VectorOfFields(jc.getFields());
      for(int i = 0; i < vof.size(); ++i)
      {         
         Field f = vof.at(i);
         
         if(f.isStatic())            
            continue;
                
         String sig = f.getSignature();
         String type_name = JaneMisc.signature_to_text(sig);
         if(type_name.equals(jc.getClassName()))
            return true;         
      }         
                           
      return false;
   }         
}
