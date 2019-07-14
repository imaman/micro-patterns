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

import java.util.HashSet;
import java.util.Vector;

import org.apache.bcel.classfile.Method;

public class AvailableFeatures
{
   private Vector features_ = new Vector();
   private int top_ = 0;

   private Features effective_ = new Features();
   private Features inherited_ = new Features();
   private Features all_ = new Features();
   
   private HashSet abstract_selectors_ = new HashSet();
   private HashSet concrete_selectors_ = new HashSet();
   
   private boolean include_object_features_ = false;

   private VectorOfMethods static_methods_  = new VectorOfMethods();
   private VectorOfMethods conc_methods_ = new VectorOfMethods();
   private VectorOfMethods abs_methods_ = new VectorOfMethods();
   private VectorOfMethods constructors_ = new VectorOfMethods();
   
   

   public void push(ClassHandle ch, IClassSpecProvider csp)
   {
      ClassSpec cs = ch.typed_value(csp);
      JimaMisc.ensure(cs != null, "ch=" + ch);

      push(cs, csp);
   }
   
   
   public void push(ClassSpec cs, IClassSpecProvider csp)
   {
      features_.add(new Features(cs, csp));
      top_ += 1;
          
//      if(cs.name().endsWith("ImagePrinter"))
//         System.out.println(cs.toString());
      build();      
   }
   
   public void pop()
   {
      features_.remove(top_ - 1);
      top_ -= 1;
      
      build();
   }

   private void build()
   {
      inner_build();

      HashSet abs = new HashSet();
      HashSet conc = new HashSet();

      for(int i = 0; i < effective_.get_methods().size(); ++i)
      {
         Method curr = effective_.get_methods().at(i);
         MethodSelector ms = new MethodSelector(curr);
         
         if(curr.isAbstract())
         {            
            abs.add(ms);
            conc.remove(ms);
         }
         else
         {
            abs.remove(ms);
            conc.add(ms);
         }
      }
      
      
      this.static_methods_.clear();
      this.conc_methods_.clear();
      this.abs_methods_.clear();
      this.constructors_.clear();
      

      for(int i = 0; i < effective_.get_interface_methods().size(); ++i)
      {
         Method curr = effective_.get_interface_methods().at(i);
//         JimaMisc.ensure(curr.isAbstract());
//         JimaMisc.ensure(!curr.isPrivate());
         
         MethodSelector ms = new MethodSelector(curr);
         if(conc.contains(ms))
            continue;
         
         abs.add(ms);
         register_method(curr, abs, conc);
      }

      
//      for(int i = 0; i < effective_.get_interface_methods().size(); ++i)
//      {
//         Method curr = effective_.get_interface_methods().at(i);
//         register_method(curr, abs, conc);
//      }      
//      
//      
      for(int i = 0; i < effective_.get_methods().size(); ++i)
      {
         Method curr = effective_.get_methods().at(i);
         register_method(curr, abs, conc);
      }      
   }
   
   
   private void register_method(Method curr, HashSet abs, HashSet conc)
   {         
      if(JavaSpec.is_constructor(curr))
      {
         constructors_.add(curr);
         return;
      }
      
      MethodSelector ms = new MethodSelector(curr);
            
      if(curr.isStatic())
         this.static_methods_.add(curr);
      else if(conc.contains(ms))
         conc_methods_.add(curr);
      else if(abs.contains(ms))
         abs_methods_.add(curr);
      else
         JimaMisc.ensure(false, ms.toString());
   }
   
   private void inner_build()
   {
      Features eff = new Features();
      Features all = new Features();
      
      int start = 1;
      if(include_object_features_)
         start = 0;
      
      int sz = size();
      if(sz <= start)
      {
         effective_ = eff;
         all_ = all;
         return;
      }
      
      // ...Else:
      
      for(int i = start; i < sz; ++i)
         all.add_all_features(at(i));
      
      for(int i = start; i < sz - 1; ++i)
      {
         Features curr = at(i);
         eff.add_inherited_features(curr);
      }
      
      inherited_ = new Features();
//      inherited_.get_interface_methods().addAll(at(sz-1).get_interface_methods());
      inherited_.add_all_features(eff);
      
      eff.add_all_features(at(sz-1));      

      effective_ = eff;
      all_ = all;
   }
   
   public int size()
   {
      return top_;
   }
   
   private Features at(int index)
   {
      if(index >= top_)
         throw new IndexOutOfBoundsException();
      return (Features) features_.get(index);
   }
   
   public Features get_effective()
   {
      return effective_;
   }
   
   public Features get_all()
   {
      return all_;
   }
   
   public Features get_inherited()
   {
      return inherited_;
   }
      
   
   public int num_abstract_methods()
   {
      return abs_methods_.size();
   }
   
   public int num_concrete_methods()
   {
      return conc_methods_.size();
   }
   
   public VectorOfMethods static_methods()
   {
      return this.static_methods_;
   }
   
   public VectorOfMethods concrete_methods()
   {
      return this.conc_methods_;
   }

   public VectorOfMethods abstract_methods()
   {
      return this.abs_methods_;
   }   
   
   public VectorOfMethods constructors()
   {
      return this.constructors_;
   }   
}
