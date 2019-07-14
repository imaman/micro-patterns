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











package il.ac.technion.micropatterns.jane.analysis.misc;


import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.lib.MethodSelector;
import il.ac.technion.micropatterns.jane.typedmodel.IClassSpecProvider;

import java.util.Iterator;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class Features
{
   private VectorOfMethods methods_= new VectorOfMethods();
   private VectorOfMethods interface_methods_= new VectorOfMethods();
   private VectorOfFields fields_ = new VectorOfFields();
   private JavaClass jc_;

   public Features()  { }
   
   public Features(ClassSpec cs, IClassSpecProvider csp)
   {
      jc_ = cs.jc();
      
      methods_ = new VectorOfMethods(jc_.getMethods());            
      fields_ = new VectorOfFields(jc_.getFields());
      
      for(Iterator i = cs.get_all_interfaces(); i.hasNext(); )
      {
         ClassHandle ch = (ClassHandle) i.next();
         ClassSpec curr = ch.typed_value(csp);
         JimaMisc.ensure(curr != null);

         VectorOfMethods temp = new VectorOfMethods(curr.jc().getMethods());
         for(int j = 0; j < temp.size(); ++j)
         {
            Method m = temp.at(j);
            if(m.isStatic())
               this.methods_.add(m);
            else
            {
               JimaMisc.ensure(m.isAbstract());
               this.interface_methods_.add(m);
            }
         }

         fields_.addAll(curr.jc());
      }
   }
   
   public void add_inherited_features(Features src)
   {
      this.interface_methods_.addAll(src.interface_methods_);
      
      for(int i = 0; i < src.fields_.size(); ++i)
      {
         Field curr = src.fields_.at(i);
         if(curr.isPrivate())
            continue;
    
         this.fields_.add(curr);
      }

      for(int i = 0; i < src.methods_.size(); ++i)
      {
         Method curr = src.methods_.at(i);
         if(curr.isPrivate())
            continue;
         
         if(JavaSpec.is_constructor(curr))
            continue;
         
         this.methods_.add(curr);
      }
   }
   
   public boolean is_effective(MethodSelector ms)
   {
      Method m = this.methods_.lookup(ms);
      if(m == null)
         return false;
      
      if(m.isAbstract())
         return false;
      
      // ...Else:
      return true;
   }
   
   public boolean is_defined(MethodSelector ms)
   {
      Method m = this.methods_.lookup(ms);
      if(m != null)
         return true;
      
      m = this.interface_methods_.lookup(ms);
      if(m != null)
         return true;

      // ...Else:
      return false;
   }

   public void add_all_features(Features src)
   {
      this.interface_methods_.addAll(src.interface_methods_);
      this.fields_.addAll(src.fields_);
      this.methods_.addAll(src.methods_);
   }
   
   public VectorOfFields get_fields()
   {
      return fields_;
   }
   
   public VectorOfMethods get_methods()
   {
      return methods_;
   }
   
   public VectorOfMethods get_interface_methods()
   {
      return interface_methods_;
   }
   
   public boolean has_instance_methods()
   {
      if(this.methods_.num_of_instance_methods() > 0)
         return true;

      if(this.interface_methods_.num_of_instance_methods() > 0)
         return true;
      
      // ...Else:
      return false;
   }
}
