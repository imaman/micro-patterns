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








package il.ac.technion.micropatterns.jungle.model;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.elements.UniHandle;
import il.ac.technion.micropatterns.jane.model.IElement;
import il.ac.technion.micropatterns.jane.model.IHandle;
import il.ac.technion.micropatterns.jane.model.ITable;
import il.ac.technion.micropatterns.jane.model.IVisitor;
import il.ac.technion.micropatterns.jane.typedmodel.IClassSpecProvider;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.lang.ref.WeakReference;

import il.ac.technion.micropatterns.jungle.model.ClassFactory.ClassFactoryError;
import org.apache.bcel.classfile.JavaClass;

public class ClassInfo 
{
   private WeakReference jc_ = new WeakReference(null);
   private Ensemble ensemble_;
   
   
   private String type_name_;
   private ClassSpecShadow cs_shadow_;
   private ClassHandleShadow ch_shadow_;
   private int method_count_;
   public boolean is_interface_;
   
   public static class ClassInfoError extends Exception
   {
      public ClassInfoError(String s) { super(s); }
   }
   
   public ClassInfo(Ensemble ensemble, String type_name) 
      throws ClassInfoError
   {
      ensemble_ = ensemble;
      type_name_ = type_name;
      
      cs_shadow_ = new ClassSpecShadow();
      ch_shadow_ = new ClassHandleShadow();
      
      JavaClass jc = get_jc();
      if(jc == null)
      {         
         throw new ClassInfoError("Class info is not accessible [" + type_name 
            + "]");
      }
      
      is_interface_ = jc.isInterface();
      method_count_ = jc.getMethods().length;
   }
   
   public int get_method_count()
   {
      return method_count_;
   }
    
   
   public synchronized JavaClass get_jc()
   {
      JavaClass result = (JavaClass) jc_.get();
      if(result != null)
         return result;
      
      try
      {
         result = ensemble_.create_java_class(type_name_);
      }
      catch(ClassFactoryError e)
      {
         JimaMisc.log().println("type_name_=" + type_name_);
         e.printStackTrace(JimaMisc.log());
         return null;
      }
      
      jc_ = new WeakReference(result);
      
      return result;      
   }
   
   private class ClassHandleShadow extends ClassHandle
   {      
      public ClassHandleShadow()
      {
         name_ = ClassInfo.this.type_name_;
      }
      
      public void accept(IVisitor v) 
      {
         v.visit(this);
      }

      public IHandle new_one(UniHandle u) { return null; }
      
      public ITable table(TypedModel m) { return null; }

      protected IElement target() { return null; }

      public String toString() { return type_name_; }

      public ClassSpec typed_value(IClassSpecProvider  m)
      {
         return as_cs();
      }
      
      public int hashCode()
      {
         return name_.hashCode();
      }
      
      public boolean equals(Object other)
      {
         if(other == null)
            return false;
         
         if(other == this)
            return true;
         
         JimaMisc.ensure(other.getClass() == this.getClass());
         
         ClassHandleShadow that = (ClassHandleShadow) other;
         return this.name_.equals(that.name_);
      }
   }
   
   public class ClassSpecShadow extends ClassSpec
   {
      public ClassSpecShadow()
      {
         super();
         set_name(type_name_);
      }
      
      public JavaClass get_jc()
      {
         return ClassInfo.this.get_jc();
      }
      
      public ClassHandle to_class_handle()
      {
         return ClassInfo.this.as_ch();
      }
      

      protected ClassHandle get_class_handle_of(String class_name)
      {
         try
         {
            ClassInfo other = ensemble_.create_class(class_name);
            ClassHandle result = other.as_ch();
            
            return result;
         }
         catch(ClassInfoError e)
         {
            return null;
         }         
      }
      
      public IElement new_one()
      {
         return new ClassSpecShadow();
      }

      public int hashCode()
      {
         return name().hashCode();
      }
      
      public boolean equals(Object other)
      {
         if(other == null)
            return false;
         
         if(other == this)
            return true;
         
         JimaMisc.ensure(other.getClass() == this.getClass());
         
         ClassSpecShadow that = (ClassSpecShadow) other;
         return this.name().equals(that.name());
      }
   
   }
   
   public ClassHandle as_ch()
   {
      return ch_shadow_;
   }
   
   public ClassSpec as_cs()
   {
      return cs_shadow_;      
   }
}
