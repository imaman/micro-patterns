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








package il.ac.technion.micropatterns.jane.typedmodel;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.jima.util.Collections;
import il.ac.technion.jima.util.StringIterator;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.elements.SubsetElement;
import il.ac.technion.micropatterns.jane.elements.SubsetHandle;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.model.IHandle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.apache.bcel.classfile.Method;

public class SetOfClasses implements Cloneable, Comparable
{
   private HashSet items_ = new HashSet();
   public SubsetHandle sh_ = null;
   private boolean is_library_ = false;
   
   private static final String ANONYMOUS = "anonymous";
   private static final String ANONY_INITIALS = "ano";
   
   private String name_ = ANONYMOUS;
   private String initials_ = ANONY_INITIALS;

   
   public SetOfClasses() { }

   public SetOfClasses(SubsetHandle sh, TypedModel m)
   {
      sh_ = sh;
      
      SubsetElement se = get_subset_element(m);      
      init(se.is_library(), se, sh.get_name(), se.get_initials());
   }   
   
//   public SetOfClasses(DescCP dcp, SubsetElement se, boolean is_lib)
//   {
//      init(is_lib, se, dcp.get_name(), dcp.get_initials());      
//   }
   
   private void init(boolean is_lib, SubsetElement se, 
      String name, String initials)
   {
      initials_ = se.get_initials();
      name_ = name;
      
      is_library_ = is_lib;
      SubsetElement.add_to(se, items_);            
   }
   
   
   
   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(T)
    */
   public int compareTo(Object other)
   {
      SetOfClasses rhs = (SetOfClasses) other;
      
      String a = this.initials_;
      if(a == null)
         a = this.name_;
      
      String b = rhs.initials_;
      if(b == null)
         b = rhs.name_;
      
      return a.compareTo(b);
   }
   
   public int size()
   {
      return this.items_.size();
   }
   
   public String get_name()
   {
      return name_;
   }
   
   public String get_initials()
   {
      return initials_;
   }
   
   public Iterator class_handles()
   {
      return items_.iterator();      
   }
   
   public boolean contains(IHandle ch)
   {
      return items_.contains(ch);
   }
   
   public boolean is_library()
   {
      return is_library_;
   }
   
   public SubsetElement get_subset_element(TypedModel m)
   {
      return sh_.typed_value(m);
   }
   
   
   /* (non-Javadoc)
    * @see java.lang.Object#clone()
    */
   public Object clone() 
   {
      try
      {
         SetOfClasses result =  (SetOfClasses) super.clone();
         result.items_ = (HashSet) this.items_.clone();
         return result;
     }
     catch(CloneNotSupportedException e)
     {
         JimaMisc.stop(e);
         return null; // Faked
     }         
   }

   public SetOfClasses union(SetOfClasses rhs)
   {
     SetOfClasses result = (SetOfClasses) this.clone();
     result.items_.addAll(rhs.items_);
     
     return result;
   }
   
   public SetOfClasses intersection(SetOfClasses rhs)
   {
     SetOfClasses result = (SetOfClasses) this.clone();
     result.items_.retainAll(rhs.items_);
     
     return result;
   }

   public SetOfClasses diff(SetOfClasses rhs)
   {
     SetOfClasses result = (SetOfClasses) this.clone();
     result.items_.removeAll(rhs.items_);
     
     return result;
   }

   
   public String toString()
   {
      return '<' + name_ + " " + size() + " elements>";
   }
   
   public static double find_correl(SetOfClasses x, SetOfClasses y, 
      int universe_size)
   {
      float n = universe_size;
      
      float px = x.size() / n;
      double sdx = Math.sqrt(px-px*px);

      float py = y.size() / n;      
      double sdy = Math.sqrt(py-py*py);
      
      SetOfClasses intersection = x.intersection(y);
      float pxy = intersection.size() / n;
      
      double result = (float) (pxy - px*py) / (sdx*sdy);      
      return result;
   }
   
   public StringIterator all_packages()
   {
      HashSet packages = new HashSet();
      
      JimaMisc.log().println("Building packages " + this);
            
      for(Iterator i = this.class_handles(); i.hasNext(); )
      {
         ClassHandle curr = (ClassHandle) i.next();
         String str = curr.get_name();
         String package_name = JavaSpec.package_name_of(str);
         
         packages.add(package_name);
      }
      
      Object[] temp = packages.toArray();
      Arrays.sort(temp);
      
      return new StringIterator(Collections.makeIter(temp));            
   }
   
   public StringIterator all_methods(TypedModel m)
   {
      Vector result = new Vector();
      
      for(Iterator i = this.class_handles(); i.hasNext(); )
      {
         ClassHandle curr = (ClassHandle) i.next();
         ClassSpec cs = m.get_class_element(curr);
         Method[] methods = cs.jc().getMethods();

         for(int j = 0; j < methods.length; ++j)
            result.add(methods[j].getName());         
      }
      
      return new StringIterator(Collections.makeIter(result));      
   }
   
   
   
   public static void main(String[] args)
   {
      SetOfClasses x = new SetOfClasses();
      x.items_.add("1");
      x.items_.add("2");
      x.items_.add("3");
      x.items_.add("6");
      x.items_.add("7");
      x.items_.add("8");
      x.items_.add("9");
      
      SetOfClasses y = new SetOfClasses();
      y.items_.add("1");
      y.items_.add("2");
      y.items_.add("6");
      y.items_.add("7");
      y.items_.add("8");

      // Expected result = 0.6546536837147768
      System.out.println(SetOfClasses.find_correl(x, y, 10));      
   }  
}
