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









package il.ac.technion.micropatterns.jane.lib;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.typedmodel.IClassSpecProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEVIRTUAL;

public class ProvidedMethods
{
   private static class TaggedMethodSelector extends MethodSelector
   {
      public static final int UNKNOWN = -1;
      public static final int IMPLEMENTED = 0;
      public static final int ABSTRACT = 1;
   
      private int tag_ = UNKNOWN;
      private boolean is_interface_define_ = false;
      private boolean is_private_ = false;
      private boolean is_final_ = false;

      protected TaggedMethodSelector(String name, String sig)
      {
         super(name, sig);
      }
      
      public TaggedMethodSelector(INVOKEVIRTUAL iv, ConstantPoolGen cpg)
      {
         super(iv, cpg);
      }

      public TaggedMethodSelector(JavaClass jc, Method m)
      {
         super(m);

         is_final_ = m.isFinal();
         is_private_ = m.isPrivate();
         is_interface_define_ = jc.isInterface();
         
         if(m.isAbstract())
            tag_ = ABSTRACT;
         else 
            tag_ = IMPLEMENTED;
      }
      
      public boolean is_abstract()
      {
         return tag_ == ABSTRACT;
      }

      public boolean is_implemented()
      {
         return tag_ == IMPLEMENTED;
      }            
      
      public boolean is_unknown()
      {
         return tag_ == UNKNOWN;
      }
      
      public boolean is_interface_defined()
      {
         return is_interface_define_;
      }
      
      public boolean is_private()
      {
         return is_private_;
      }

      public boolean is_final()
      {
         return is_final_;
      }
   }
   
   private HashMap selectors_ = new HashMap();
   private HashSet types_ = new HashSet();
   private String name_;
   
   private int num_of_impls_ = 0;

   public ProvidedMethods(IClassSpecProvider tm, ClassHandle ch, 
      boolean ignore_current_methods)
   {
      this(tm, ch, ignore_current_methods, null);
   }
   
   public ProvidedMethods(IClassSpecProvider tm, ClassHandle ch, 
      boolean ignore_current_methods, String stop_at_class)
   {
      name_ = ch.get_name();
      
      ClassSpec cs = ch.typed_value(tm);
      for(Iterator i = cs.get_all_interfaces(); i.hasNext(); )
      {
         ClassHandle curr = (ClassHandle) i.next();
         types_.add(curr.get_name());
         
         ClassSpec temp = curr.typed_value(tm);         
         add(temp);
      }
                 
      Vector super_types = new Vector();
      
      while(true)
      {
         String type_name = ch.get_name();
         if(type_name.equals(stop_at_class))
            break;
         
         types_.add(ch.get_name());
         
         super_types.add(cs);         
         if(cs.is_java_object())
            break;
         
         ch = cs.get_first_super();
         if(ch == null)
         {
            JimaMisc.log().println("No super class found for " + cs);
            break;
         }
         
         cs = ch.typed_value(tm);
         if(cs == null)
            break;
      }

      int stop_index = 0;
      if(ignore_current_methods)
         stop_index = 1;
      
      for(int i = super_types.size() - 1; i >= stop_index; --i)
      {
         ClassSpec curr = (ClassSpec) super_types.elementAt(i);
         add(curr);
      }      
   }
   
   public ProvidedMethods(IClassSpecProvider tm, ClassHandle ch) 
   {
      this(tm, ch, false);
   }
      
   public boolean has_class(String class_name)
   {
      return types_.contains(class_name);
   }
   
   private void add(ClassSpec cs)
   {
      JavaClass jc = cs.jc();
      
      Method[] methods = jc.getMethods();
      for(int i = 0; i < methods.length; ++i)
      {
         Method curr = methods[i];         
         if(curr.isStatic())
            continue;
         
         if(JavaSpec.is_constructor(curr))
            continue;
      
         num_of_impls_ += 1;
         
         TaggedMethodSelector ms = new TaggedMethodSelector(jc, curr);
         selectors_.put(ms, ms);
      }               
   }
   
   public int num_of_impls()
   {
      return num_of_impls_;
   }
   
   public int num_of_selectors()
   {
      return selectors_.size();      
   }
   
   public boolean is_concrete(MethodSelector ms)
   {
      TaggedMethodSelector tms = (TaggedMethodSelector) selectors_.get(ms);
      
//      if(tms == null)
//      {
//         for(Iterator i = known_selectors_.keySet().iterator(); i.hasNext(); )
//            System.err.println(i.next());
//      }
      
      JimaMisc.ensure(tms != null, "ms=" + ms + " of " + name_);
      return tms.is_implemented();
   }

   public boolean is_abstract(MethodSelector ms)
   {
      TaggedMethodSelector tms = (TaggedMethodSelector) selectors_.get(ms);      
      return tms.is_abstract();
   }
   
   public boolean is_provided(MethodSelector ms)
   {
      TaggedMethodSelector tms = (TaggedMethodSelector) selectors_.get(ms);      
      return tms != null;
   }
   
   public boolean is_private(MethodSelector ms)
   {
      TaggedMethodSelector tms = (TaggedMethodSelector) selectors_.get(ms);      
      return tms.is_private();
   }

   public boolean is_final(MethodSelector ms)
   {
      TaggedMethodSelector tms = (TaggedMethodSelector) selectors_.get(ms);      
      return tms.is_final();
   }
}
