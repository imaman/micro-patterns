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

import il.ac.technion.micropatterns.jane.model.IElement;
import il.ac.technion.micropatterns.jane.model.IHandle;
import il.ac.technion.micropatterns.jane.model.ITable;
import il.ac.technion.micropatterns.jane.model.IVisitor;
import il.ac.technion.micropatterns.jane.typedmodel.IClassSpecProvider;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

public class ClassHandle extends AbstractHandle
{
   public static final ClassHandle PROTOTYPE = new ClassHandle();
   public static final ClassSpec TARGET = ClassSpec.new_instance();
   

   /**
    * 
    */
   public ClassHandle() { }

   /**
    * @param h
    */
   public ClassHandle(UniHandle h, String class_name)
   {
      super(h);
      set_name(class_name);
   }
   
   protected IElement target()
   {
      return TARGET;
   }
   
   
//   public ClassProxy to_class_proxy(TypedModel m)
//   {
//      return m.get_class_proxy(this);
//   }   
   
   public IHandle new_one(UniHandle u)
   {
      return new ClassHandle(u, null);
   }

   /**
    * @see il.ac.technion.micropatterns.jane.model.IHandle#table(il.ac.technion.micropatterns.jane.typedmodel.TypedModel)
    */
   public ITable table(TypedModel m)
   {
      return m.class_table_;
   }
   
   private static final IElement FAKED = new IElement()
      {
         public void set_model(TypedModel m) { }
         public IHandle get_handle() { return null; }
         public void set_handle(IHandle h) { }
         
         public IElement new_one() {return null; }
      };
   
   public ClassSpec typed_value(IClassSpecProvider csp)
   {
      TypedModel m = (TypedModel) csp;
      
      ClassSpec result = (ClassSpec) value(m);
      if(result != null)
         result.set_name(name_);

//      this.table(m).add(m, name_, FAKED);
            
      return result;
   }
   
   
//   public String name(TypedModel m)
//   {
//      value(m).
//   }
   /**
    * @see il.ac.technion.micropatterns.jane.model.IHandle#accept(il.ac.technion.micropatterns.jane.model.IVisitor)
    */
   public void accept(IVisitor v)
   {
      v.visit(this);
   }
   
   public String toString()
   {
      return "ClassHandle[" + this.to_integer() + "," + this.name_ + "]";
   }

}
