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











package il.ac.technion.micropatterns.jane.elements;

import il.ac.technion.micropatterns.jane.lib.JavaFlags;
import il.ac.technion.micropatterns.jane.model.AbstractElement;
import il.ac.technion.micropatterns.jane.model.IElement;
import il.ac.technion.micropatterns.jane.model.IHandle;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;
import org.apache.bcel.classfile.Method;


public class MethodSpec extends AbstractElement
{
   private ClassHandle ch_;
   private int method_n_;
   private int flags_;
   
   public MethodSpec() { }
   
   public MethodSpec(ClassSpec ce, int method_n)
   {
      super(null);
      ch_ = ce.to_class_handle();
      method_n_ = method_n;      
      
      Method temp = ce.jc().getMethods()[method_n_];
      flags_ = temp.getAccessFlags();
   }   
   
   public IElement new_one(IHandle h)
   {
      IElement result = new MethodSpec();
      result.set_handle(h);
      
      return result;
   }
   
   
   public boolean equals(Object other)
   {
      if(other == null)
         return false;
         
      if(other == this)
         return true;
         
      if(!(other instanceof MethodSpec))
         return false;
         
      MethodSpec rhs = (MethodSpec) other;
      
      boolean result = this.ch_.equals(rhs.ch_) 
         && (this.method_n_ == rhs.method_n_);
      return result;
   }
   
   public int hashCode()
   {
      return ch_.hashCode();
   }

   private Method get_method_impl(TypedModel m)
   {
      ClassSpec ce = get_class_element(m);
      Method result = ce.jc().getMethods()[method_n_];
      return result;
   }
   
   public ClassSpec get_class_element(TypedModel m)
   {
      ClassSpec result = m.get_class_element(ch_);
      return result;
   }
      
   public String get_name(TypedModel m)
   {      
      Method temp = get_method_impl(m);
      return temp.getName();
   }
   
   public String get_signature(TypedModel m)
   {      
      Method temp = get_method_impl(m);
      return temp.getSignature();
   }   
   
   public String get_declaration(TypedModel m)
   {
      Method temp = get_method_impl(m);
      return temp.toString();
   }
   
   public ClassHandle class_handle()
   {
      return ch_;
   }   
   
   public boolean is_private()
   {
      return JavaFlags.is_private(flags_);
   }

   public boolean is_static()
   {
      return JavaFlags.is_static(flags_);
   }

   public boolean is_abstract()
   {
      return JavaFlags.is_abstract(flags_);
   }      
}
