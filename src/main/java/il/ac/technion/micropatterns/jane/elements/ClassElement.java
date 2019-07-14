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

import il.ac.technion.micropatterns.jane.model.AbstractElement;

public class ClassElement extends AbstractElement 
{
//   private JavaClass jc_;
//   private ConstantPoolGen cpg_ = null;
//   
//   public ClassElement(JavaClass jc, IHandle h) 
//   { 
//      super(h);
//      
//      JimaMisc.ensure(jc != null);      
//      jc_ = jc;      
//   }
//   
//   public String name()
//   {
//      return jc_.getClassName();
//   }
//   
//   public ClassProxy to_proxy(TypedModel m)
//   {
//      return m.get_class_proxy(name());
//   }
//   
//   public ClassHandle to_class_handle()
//   {
//      return new ClassHandle(this.get_handle());
//   }
//   
//   public int method_count()
//   {
//      return jc_.getMethods().length;
//   }
//
//   public String super_class_name()
//   {
//      String result = jc_.getSuperclassName();
//      return result;
//   } 
//   
//   public JavaClass jc()
//   {
//      return jc_;  
//   }
//   
//   public synchronized ConstantPoolGen constant_pool()
//   {
//      if(cpg_ != null)
//         return cpg_;
//      
//      // ...Else:
//      cpg_ = new ConstantPoolGen(jc().getConstantPool());
//      return cpg_;                  
//   }
//   
//   public boolean is_java_object()
//   {
//      return jc_.getClassName().equals("java.lang.Object");
//   }
//   
//   /**
//    * Get a list of ClassElements which are the super classes of the
//    * class represented by this. (Including this)
//    */
//   public Iterator super_classes(TypedModel m)
//   {
//      Vector result = new Vector();
//      
//      ClassElement ce = this;
//      while(true)
//      {
//         result.add(ce);
//         if(ce.is_java_object())
//            break;
//            
//         String s = ce.super_class_name();            
//         ce = m.get_class_element(m.get_class_proxy(s));
//         if(ce == null)
//            break;
//      }
//      
//      return result.iterator();
//   }
//   
//   public Iterator all_interfaces(TypedModel m)
//   {
//      return m.get_all_interfaces(this.to_proxy(m));
//   }
//   
//   
//   public Iterator all_methods()
//   {
//      Vector result = new Vector();
//
//      Method[] methods = this.jc().getMethods();
//      for(int j = 0; j < methods.length; ++j)
//      {
//         MethodSpec temp = new MethodSpec(this, j);
//         result.add(temp);
//      }                   
//      
//      return result.iterator();        
//   }
//   
//   public String toString()
//   {
//      return "ClassElement(" + name() + ")";
//   }
}
