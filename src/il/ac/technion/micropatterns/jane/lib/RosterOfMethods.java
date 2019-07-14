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
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEVIRTUAL;

public class RosterOfMethods
{
   private HashMap known_selectors_ = new HashMap();
//   private HashMap class2selectors_ = new HashMap();
   
   private Vector x_classes_ = new Vector();
         
   private static class TaggedMethodSelector extends MethodSelector
   {
      public static final int UNKNOWN = -1;
      public static final int IMPLEMENTED = 0;
      public static final int ABSTRACT = 1;
   
      private int tag_ = UNKNOWN;
      private boolean is_interface_define_ = false;

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
   }
   
   private static class ClassDesc
   {
      private HashSet types_ = new HashSet();
      
      private String class_name_;
      private Vector selectors_ = new Vector();

      public ClassDesc(String class_name)     
      {
         class_name_ = class_name;
      }
      
      public ClassDesc(ClassSpec cs, TypedModel tm)
      {
         JavaClass jc = cs.jc();
                  
         class_name_ = jc.getClassName();
         types_.add(class_name_);
                                   
         HashSet all = new HashSet();         
         add_methods_of(jc, all);
         
         for(Iterator i = cs.get_all_interfaces(); i.hasNext(); )
         {
            ClassHandle curr = (ClassHandle) i.next();
            if(curr == null)
               continue;
            
            types_.add(curr.get_name());
            
            ClassSpec temp = curr.typed_value(tm);
            add_methods_of(temp.jc(), all);
         }         
      }
      
      private void add_methods_of(JavaClass jc, HashSet all)
      {
         boolean is_interface = jc.isInterface();
         
         if(jc.getClassName().indexOf("NamingContext") >= 0)
            JimaMisc.log().println(jc.toString());
         
         Method[] methods = jc.getMethods();
         for(int i = 0; i < methods.length; ++i)
         {
            Method curr = methods[i];         
            if(curr.isStatic())
               continue;
            
            if(JavaSpec.is_constructor(curr))
               continue;
         
            TaggedMethodSelector tms = new TaggedMethodSelector(jc, curr);         
            
            // Make sure interface methods do not mark class methods 
            // as "abstract"
            if(all.contains(tms) && is_interface)
               continue; 

            selectors_.add(tms);
            all.add(tms);
         }         
      }      
      
      public boolean is_direct_super_of(String type_name)
      {
         boolean result = types_.contains(type_name);
         return result;
      }
      
      public int hashCode()
      {
         return class_name_.hashCode();
      }
      
      public boolean equals(Object other)
      {
         if(other == null)
            return false;
         
         if(other == this)
            return true;
         
         if(other.getClass() != this.getClass())
            return false;
         
         
         ClassDesc rhs = (ClassDesc) other;
         boolean result = this.class_name_.equals(rhs.class_name_);
         
         return result;
      }         
      
      public String toString()
      {
         return "ClassDesc(" + class_name_ + ")";
      }
   }
   
   
   public void add_methods_of(ClassSpec cs, TypedModel m)
   {      
      ClassDesc cd = new ClassDesc(cs, m);
            
//      class2selectors_.put(cd, new_ones);      
      x_classes_.add(cd);
      
      for(Iterator j = cd.selectors_.iterator(); j.hasNext(); )
      {
         TaggedMethodSelector tms = (TaggedMethodSelector) j.next();
         
         
         // Again, interface methods should not mark already concrete methods
         // as abstract
         if(tms.is_interface_defined() && known_selectors_.containsKey(tms))
            continue;
         
         known_selectors_.put(tms, tms);
      }
   }
   
   public void pop()
   {
      int index = x_classes_.size() - 1;
//      ClassDesc cd =(ClassDesc) x_classes_.elementAt(index);
      
      x_classes_.remove(index);
//      class2selectors_.remove(cd);
      
      rebuild();
   }
   
   private void rebuild()
   {
      HashMap selectors = new HashMap();
      for(Iterator i = x_classes_.iterator(); i.hasNext(); )
      {
         ClassDesc cd = (ClassDesc) i.next();
         Vector vec = cd.selectors_;
         
         for(Iterator j = vec.iterator(); j.hasNext(); )
         {
            TaggedMethodSelector tms = (TaggedMethodSelector) j.next();
            
            // And yet again, interface methods should not mark already 
            // concrete methods as abstract
            if(tms.is_interface_defined() && selectors.containsKey(tms))
               continue;
            selectors.put(tms, tms);
         }
      }
      
      known_selectors_ = selectors;
   }
   
   public void remove_methods_of(String class_name)
   {
      ClassDesc temp = new ClassDesc(class_name);
      
//      class2selectors_.remove(temp);            
      x_classes_.remove(temp);
      
      rebuild();      
   }
   
   public boolean has_class(String class_name)
   {
      ClassDesc cd = new ClassDesc(class_name);
      boolean result = x_classes_.contains(cd);
//      result = class2selectors_.containsKey(class_name);
      
      return result;
   }
   
   public boolean is_concrete(MethodSelector ms)
   {
      TaggedMethodSelector tms = (TaggedMethodSelector) 
         known_selectors_.get(ms);
      
      if(tms == null)
      {
         for(Iterator i = known_selectors_.keySet().iterator(); i.hasNext(); )
            System.err.println(i.next());
      }
      JimaMisc.ensure(tms != null, "ms=" + ms + ", classes=" + x_classes_);
      
      
      return tms.is_implemented();
   }

   public boolean is_abstract(MethodSelector ms)
   {
      TaggedMethodSelector tms = (TaggedMethodSelector) 
         known_selectors_.get(ms);
      
      return tms.is_abstract();
   }
   
   public boolean is_declared(MethodSelector ms)
   {
      boolean result = known_selectors_.containsKey(ms);
      return result;
   }
   
   public static void main(String[] args)
   {
      TaggedMethodSelector tms = new TaggedMethodSelector("ab", "cd");
      MethodSelector ms = new MethodSelector("ab", "cd");
      
      System.out.println(tms + " eqauls " + ms + " ? " + tms.equals(ms));      
   }
}
