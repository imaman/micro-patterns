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

import il.ac.technion.micropatterns.jane.analysis.misc.AllTypesAgent;
import il.ac.technion.micropatterns.jane.analysis.misc.Result;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.lib.MethodSelector;
import il.ac.technion.micropatterns.jane.lib.ProvidedMethods;
import il.ac.technion.micropatterns.jane.typedmodel.ModelDefinitions;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;
import org.apache.bcel.classfile.Method;

public class Reimplementor extends AllTypesAgent
{
   public static final String NAME = ModelDefinitions.REIMPLEMENTOR_SUBSET_NAME;

   private Result effector_;
   
   public Reimplementor()
   {
      super(NAME, DescCP.RIM);
      
      effector_ = Result.new_result(this, DescCP.IMP);
   }
      

   public void visit(ClassHandle cp)
   {
      ClassSpec ce = cp.typed_value(xmodel_);
      
      ProvidedMethods pm = new ProvidedMethods(xmodel_, cp, true);
      
      Method[] methods = ce.jc().getMethods();
      
      int count = 0;
      int effection = 0;
      int reimpl = 0;
      for(int i = 0; i < methods.length; ++i)
      {      
         Method curr = methods[i];        

         if(JavaSpec.is_constructor(curr))            
            continue;

         if(curr.isStatic())
            continue;
                
         if(curr.isPrivate())
            continue;
            
         count += 1;

         //
         //  Use the following condition if you want to eliminate
         //  The following two antipatterns:
         //  (1) An interface which defines methods of java.lang.Object
         //      as abstract => Matches the Reimplementor pattern
         //
         //  (2) An interface which defines abstract methods inherited
         //      from its super-interface as abstract 
         //      => Matches the Implementor pattern
         //
//         if(curr.isAbstract())
//            continue;
                     
         MethodSelector ms = new MethodSelector(curr);
         
         if(!pm.is_provided(ms))
            continue;
         
         if(curr.isAbstract())
            continue;
         

         if(pm.is_concrete(ms))           
            reimpl += 1;                     

         if(pm.is_abstract(ms))
            effection += 1;

      }
      
      if(count == 0)
         return;
      
      main_result_.set_decision(cp, reimpl == count);
      effector_.set_decision(cp, effection == count);
   }    
}
