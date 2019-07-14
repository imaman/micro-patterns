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
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;
import org.apache.bcel.classfile.Field;

public class Monostate extends InheritanceDescendingAgent
{
   private Result monostate_;
   
   public Monostate()
   {
      super("monostate", DescCP.ST1);
      monostate_ = main_result_;
   }
         
   public boolean inspect(ClassHandle cp)
   {         
      ClassSpec ce = cp.typed_value(xmodel_);
      
//      JimaMisc.log().println("MONO: " + ce);

      if(ce.is_java_object())
         return true;
               
      if(ce.jc().isInterface())
         return false;

      if(af_.abstract_methods().size() != 0 || af_.concrete_methods().size() != 0)
         return false;
      
         
      Field[] f = ce.jc().getFields();

      int static_fields = 0;
      for(int i = 0; i < f.length; ++i)
      {              
         if(!f[i].isStatic())
            return false;
         
         if(f[i].isFinal())
            continue;
         
         if(JavaSpec.is_serialization_stuff(f[i]))
            continue;
         
         static_fields += 1;
      }
      
      if(static_fields == 0)
         return false;
         
      monostate_.set_decision(cp, true);
      return true;
   }    
}
