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

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.model.AbstractElement;
import il.ac.technion.micropatterns.jane.model.IElement;
import il.ac.technion.micropatterns.jane.model.XModel;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;

public class ClassSpec extends AbstractElement 
{
//   private JavaClass java_class_ = null;
   private WeakReference cpg_ = null; 
   private String name_;
   private boolean use_normal_super_ = true;
   
   private void writeObject(ObjectOutputStream out)
      throws IOException
   {
      out.writeObject(name_);
      out.writeBoolean(use_normal_super_);
   }
        
   private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException
   {
      name_ = (String) in.readObject();
      use_normal_super_ = in.readBoolean();
      cpg_ = null;
   }
   
   
   public static ClassSpec new_instance()
   {
      return new ClassSpec();
   }
   
   public static ClassSpec create_empty_instance()
   {
      return new ClassSpec();
   }
   
   protected ClassSpec() { }
   
   public ClassSpec(String name, ClassHandle h, TypedModel m)
   { 
      super(h, m);
      name_ = name;
   }
   
   public void set_use_normal_super(boolean b)
   {
      this.use_normal_super_ = b;
   }
   
   public void set_name(String name)
   {
      name_ = name;
   }
   
   
   public IElement new_one()
   {
      return new ClassSpec();
   }
   
   public String name()
   {
      return name_;
   }
   
//   public ClassProxy to_proxy(TypedModel m)
//   {
//      return m.get_class_proxy(name());
//   }
   
   public ClassHandle to_class_handle()
   {
      return (ClassHandle) this.get_handle();
   }
   
   protected synchronized JavaClass get_jc()
   {
      // ...Else:    
      XModel xm = get_model().inner();     
      String str = this.name();
      JavaClass result = xm.class_for_name(str);
      
      if(result == null)
      {
         JimaMisc.log().println("Class not found for " + this);
         result = get_model().inner().class_for_name(JavaSpec.NULL_CLASS);      
      }                  

      JimaMisc.ensure(result != null, "jc is null for class name: " + name());
      
//      this.java_class_ = result;
      return result;                
   }
   
   public int method_count()
   {
      return get_jc().getMethods().length;
   }

//   public String super_class_name()
//   {
//      String result = get_jc().getSuperclassName();
//      return result;
//   } 
   
   public JavaClass jc()
   {
      return get_jc();
   }
   
   public synchronized ConstantPoolGen constant_pool()
   {
      ConstantPoolGen result = null;
      
      if(cpg_ != null)
         result = (ConstantPoolGen) cpg_.get();
         
      if(result == null)         
      {
         result = new ConstantPoolGen(jc().getConstantPool());
         cpg_ = new WeakReference(result);
      }
      
      // ...Else:
      return result;
   }
   
   public boolean is_java_object()
   {
      return JavaSpec.is_java_lang_object(this.get_jc().getClassName());
   }
   
   /**
    * Get a list of ClassElements which are the super classes of the
    * class represented by this. (Including this)
    */
   public Iterator super_classes()
   {
      Vector result = new Vector();
      
      ClassSpec ce = this;
      while(true)
      {
         result.add(ce);
         if(ce.is_java_object())
            break;
                        
         ClassHandle ch = ce.get_first_super();
         
         if(ch == null)
            break;
         
//         JimaMisc.ensure(ch != null, "ch is null. ce=" + ce + ", this=" + this);
         ce = ch.typed_value(get_model());
         if(ce == null)
            break;
      }
      
      return result.iterator();
   }
   
   
   public ClassHandle get_first_super()
   {
      if(this.name_.equals("sun.print.PathGraphics"))
         JimaMisc.log().println("dbg=" + this);

      if(this.is_java_object())
         return null;

      if(use_normal_super_)
      {
         String spr = this.jc().getSuperclassName();
            
         ClassHandle result = get_class_handle_of(spr);

//         JimaMisc.ensure(result != null, "spr=" + spr + ", this=" + this);
         return result;
      }
      
      // ...Else: Superclass is missing, assume a direct 
      // sub-class of java.lang.Object 

      String s = JavaSpec.JAVA_LANG_OBJECT;
      ClassHandle result = get_class_handle_of(s);

      JimaMisc.ensure(result != null);      
      return result;
   }
   
   /**
    * Find all interfaces implemented by a given class and its super
    * classes and the interfaces that those interfaces extend, and so on.
    * If the given class is itself an interface it is NOT included in the 
    * result.
    * @return An iterator over a collection of ClassHandle objects representing 
    * all implemented interfaces
    */
   public Iterator get_all_interfaces()
   {
      HashSet result = new HashSet();

      LinkedList queue = new LinkedList();

      ClassHandle ch = this.to_class_handle();
      queue.add(ch);

//      JimaMisc.log().println("Finding interfaces of " + ch);

      while(!queue.isEmpty())
      {
         JimaMisc.ensure(!queue.isEmpty(), "cp=" + ch + ", queue.size()=" + queue.size());
         ClassHandle curr = (ClassHandle) queue.removeFirst();
         if(curr == null)
            continue;

         ClassSpec ce = curr.typed_value(get_model());
         
         JavaClass jc = ce.jc();
         if(jc == null)
            continue;

         if(ce.jc().isInterface())
         {            
            // Make sure that cp itself is not added (Happends if cp
            // represents an interface)
            if(!ce.equals(this))
               result.add(ce.to_class_handle());
         }
         else 
         {
            ClassHandle first_super = ce.get_first_super();
            queue.add(first_super);
         }

         String[] interfaces = ce.jc().getInterfaceNames();
         for(int i = 0; i < interfaces.length; i++)
         {
            String interface_name = interfaces[i];
            ClassHandle temp = get_class_handle_of(interface_name);

            queue.add(temp);
         }
      }

      return result.iterator();
   }
   
   /**
    * Find all interfaces directly implemented by the given 
    * If the given class is itself an interface it is NOT included in the 
    * result.
    * @return An iterator over a collection of ClassHandle objects representing 
    * all implemented interfaces. Returns null if one (or more) of the 
    * interfaces could not be located
    */
   public Iterator first_interfaces()
   {
      HashSet result = new HashSet();

      String[] interfaces = this.jc().getInterfaceNames();
      for(int i = 0; i < interfaces.length; i++)
      {
         String interface_name = interfaces[i];
         ClassHandle temp = get_class_handle_of(interface_name); 

         if(temp == null)
            return null;
         
         // ...Else:
         result.add(temp);
      }

      return result.iterator();
   }
   
   protected ClassHandle get_class_handle_of(String class_name)
   {
      ClassHandle result = (ClassHandle) 
         get_model().class_table_.lookup_handle(class_name);
      
      return result;
      
   }
   
   
   public Iterator all_methods()
   {
      Vector result = new Vector();

      JavaClass jc = this.jc();
      Method[] methods = jc.getMethods();
      for(int j = 0; j < methods.length; ++j)
      {
         MethodSpec temp = new MethodSpec(this, j);
         result.add(temp);
      }                   
      
      return result.iterator();        
   }
   
   public String toString()
   {
      return "ClassElement(" + name() + ")";
   }
}
