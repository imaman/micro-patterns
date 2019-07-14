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
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class Pool extends InheritanceDescendingAgent
{
   public static final String NAME = "pool";
   protected Result enum_result_;

//   private Result nomethods_;

   public Pool()
   {
      super(NAME, DescCP.POO);
      enum_result_ = Result.new_result(this, DescCP.ENUMTYPE);
      
//      nomethods_ = Result.new_result(this, DescCP.METHODS0);
   }

   
   private boolean inspect_impl(ClassHandle cp)
   {      
      ClassSpec ce = cp.typed_value(xmodel_);

      int method_count = 0;
      
      Method[] m = ce.jc().getMethods();
      for(int i = 0; i < m.length; ++i)
      {
         Method curr = m[i];
         if(JavaSpec.is_constructor(curr))
            continue;
         
         method_count += 1;
      }

      int n = 0;
      
      Field[] f = ce.jc().getFields();
      for(int i = 0; i < f.length; ++i)
      {              
         if(!f[i].isStatic())            
            return false;
            
         if(f[i].isPrivate())
            continue;            
            
         if(!f[i].isFinal())            
            continue; // return false;            

         if(JavaSpec.is_serialization_stuff(f[i]))
            continue;
            
         n += 1;            
      }         
      
      
      Method[] methods = ce.jc().getMethods();     
      for(int i = 0; i < methods.length; ++i)
      {
         if(JavaSpec.is_constructor(methods[i]))
            continue;
         
         if(methods[i].isStatic())
            continue;
      
         // ...Else:
         return false;
      }
         
      
      return n >= 1;
   }         
   
   /**
    * @see il.ac.technion.micropatterns.jane.analysis.misc.InheritanceDescendingAgent#inspect(il.ac.technion.micropatterns.jane.elements.ClassProxy)
    */
   protected boolean inspect(ClassHandle cp)
   {
      boolean b = inspect_impl(cp);
      main_result_.set_decision(cp, b);
      
      b = is_enum(cp);
      if(b)
         enum_result_.set_decision(cp, b);
      
      return true;
   }
   
   
   public boolean is_enum(ClassHandle cp)
   {      
      ClassSpec ce = cp.typed_value(xmodel_);
      JavaClass jc = ce.jc();
      if(!jc.isAbstract())
         return false;
      
      if(af_.concrete_methods().size() > 0)
         return false;
      
      int method_count = 0;
      
      Method[] m = ce.jc().getMethods();
      for(int i = 0; i < m.length; ++i)
      {
         Method curr = m[i];
         if(JavaSpec.is_constructor(curr))
            continue;
         
         if(curr.isStatic())
            continue;
         
         method_count += 1;
      }
      
      if(method_count == 0)
         return false;

      int n = 0;
      
      
      String sig = null;
      
      VectorOfFields vof = new VectorOfFields(ce.jc().getFields());
      for(int i = 0; i < vof.size(); ++i)
      {         
         Field f = vof.at(i);
         
         if(!f.isStatic())            
            return false;
            
         if(f.isPrivate())
            return false;            

         if(JavaSpec.is_serialization_stuff(f))
            continue;
            
         if(!f.isFinal())            
            return false;            

       
         String s = f.getSignature();
         if(sig == null)
            sig = s;
         else if(!sig.equals(s))
            return false;

         n += 1;
      }         
            
               
      return n >= 2;
   }         
}
