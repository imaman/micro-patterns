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
import il.ac.technion.micropatterns.jane.lib.JaneMisc;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;

public class RestrictedCreation extends AllTypesAgent
{
   private Result restricted_;
   private Result shared_;
    
   public static final String NAME = "restricted-creation";
   public static final String NAME_SHARED = "flyweight";

   public RestrictedCreation()
   {
      super(NAME, DescCP.RC);
      restricted_ = main_result_;
      shared_ = Result.new_result(this, DescCP.FLY);
   }
   
   private void add_sig(HashSet target, ClassHandle h)
   {
      String name = h.get_name();
      if(JavaSpec.is_java_lang_object(name))
         return;
      
      String sig = JaneMisc.class_to_bc_format(name);
      target.add(sig);
   }
   
   public void visit(ClassHandle h)
   {
      ClassSpec ce = h.typed_value(xmodel_);
      
      HashSet super_types = new HashSet();      
      
      
      add_sig(super_types, h);
      
      ClassHandle first_super = ce.get_first_super();
      if(first_super != null)
         add_sig(super_types, first_super);

      
      Iterator k = ce.get_all_interfaces();
      while(k.hasNext())
      {
         ClassHandle curr = (ClassHandle) k.next();
         add_sig(super_types, curr);
      }         
      
      int n = 0;      
      Field[] f = ce.jc().getFields();
      
      for(int i = 0; i < f.length; ++i)
      {              
         if(!f[i].isStatic())
            continue;
                            
         String sig = f[i].getSignature();
         if(super_types.contains(sig))
            n += 1;
      }
      
      if(n == 0)   
         return;


      int public_constructors = 0;
      
      // count constructors 
      Method[] m = ce.jc().getMethods();
      for(int i = 0; i < m.length; ++i)
      {          
         if(!JavaSpec.is_constructor(m[i]))
            continue;
             
         boolean is_public = m[i].isPublic();
         if(is_public)
            public_constructors += 1;
      }

      if(public_constructors == 0)
         restricted_.set_decision(h, true);
      else
         shared_.set_decision(h, true);         
   }    
}
