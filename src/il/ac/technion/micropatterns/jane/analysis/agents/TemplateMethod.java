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
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.lib.MethodSelector;
import il.ac.technion.micropatterns.jane.lib.ProvidedMethods;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.util.Iterator;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;

public class TemplateMethod extends InheritanceDescendingAgent
{
   public static final String NAME = "template-method";

   public TemplateMethod()
   {
      super(NAME, DescCP.TM);
   }
   
   public String name()
   {
      return NAME;
   }
   
      
   /**
    * @see il.ac.technion.micropatterns.jane.analysis.misc.InheritanceDescendingAgent#inspect(il.ac.technion.micropatterns.jane.elements.ClassHandle)
    */
   protected boolean inspect(ClassHandle ch)
   {
      ClassSpec cs = ch.typed_value(xmodel_);
      
      if(cs.jc().isInterface())
         return false;
         
      if(cs.jc().isFinal())
         return false;
      
      if(!cs.jc().isAbstract())
         return true;
      
      
      ConstantPoolGen cpg = cs.constant_pool();
      ProvidedMethods pm = new ProvidedMethods(xmodel_, ch, false);
      
      int template_methods = 0;

      Method[] methods = cs.jc().getMethods();
      for(int i = 0; i < methods.length; ++i)
      {
         Method curr = methods[i];
         if(curr.isAbstract())
            continue;

         if(JavaSpec.is_constructor(curr))
            continue;
            
         if(JavaSpec.is_main_method(curr))
            continue;
         
         InstructionParser ip = new InstructionParser(curr); 
         
         int abs_invoked = 0;                  
         for(Iterator j = ip.iterator(); j.hasNext(); )
         {
            Instruction ins = (Instruction) j.next();
            
            if(!(ins instanceof INVOKEVIRTUAL))
               continue;
         
            INVOKEVIRTUAL iv = (INVOKEVIRTUAL) ins;
            
            String receiver_type = iv.getClassName(cpg);
            boolean found = pm.has_class(receiver_type);
            if(!found)
               continue;

            // ...Else: found is true => receiver is one of the super-classes
            MethodSelector invoked = new MethodSelector(iv, cpg);            
            boolean impl_exists = pm.is_concrete(invoked);
            if(impl_exists)
               continue;
                                       
            // ...Else: Found an abstract method invocation             
            abs_invoked += 1;               
         }  
         
         if(abs_invoked >= 1)
         {
            template_methods += 1;
            if(template_methods >= 2)
            {
               this.main_result_.set_decision(ch, true);
               return true;
            }
         }            
      }
            
      return true;
   }
}
