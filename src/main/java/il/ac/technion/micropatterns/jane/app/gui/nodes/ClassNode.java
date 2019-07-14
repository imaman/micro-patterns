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











package il.ac.technion.micropatterns.jane.app.gui.nodes;

import il.ac.technion.jima.IWidthListener;
import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.app.gui.IView;
import il.ac.technion.micropatterns.jane.app.gui.SortSelector;
import il.ac.technion.micropatterns.jane.app.gui.rows.AbstractRow;
import il.ac.technion.micropatterns.jane.app.gui.rows.ClassRow;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.typedmodel.IClassSpecProvider;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.util.Iterator;

import org.apache.bcel.classfile.JavaClass;

public class ClassNode extends BasicClassNode
{
   protected ClassHandle ch_;
   private boolean fields_expanded_ = false;
   private boolean methods_expanded_ = false;
   private boolean super_classes_expanded_ = false;
   
   public Comparable comp_ = null;
   
   public ClassNode(ClassHandle ch)
   {
      ch_ = ch;
   }
   
   public AbstractRow create_row(IView v, int index, IWidthListener wl)
   {
      return new ClassRow(this, v, get_depth(), index, ch_, wl);
   }
   
   public void methods_on_off(IView v)
   {
      ClassSpec cs = ch_.typed_value(v.model());
      methods_on_off_impl(v, cs);
   }

   public void fields_on_off(IView v)
   {
      ClassSpec cs = ch_.typed_value(v.model());
      fields_on_off_impl(v, cs);      
   }
   
   public boolean match(String s)
   {      
      return ch_.get_name().toLowerCase().indexOf(s) >= 0;
//      return cp_.name().compareToIgnoreCase(s) == 0;
   }
   
   
   
   public void super_class_on_off(IView v)
   {
      Class c = SuperClassNode.class;
      
      if(this.is_expanded(c))
         v.collapse(this, c);
      else
      {
         TypedModel m = v.model();
         ClassSpec cs = ch_.typed_value(m);
         
         Iterator k = cs.super_classes();
         if(k.hasNext())
            k.next(); // Start from first parent
            
         while(k.hasNext())
         {
            ClassSpec curr = (ClassSpec) k.next();
            SuperClassNode new_one = new SuperClassNode(curr);
            
            this.add_child(new_one);
         }
         
         for(Iterator q = cs.get_all_interfaces(); q.hasNext(); )
         {
            ClassHandle temp = (ClassHandle) q.next();
            ClassSpec curr = m.get_class_element(temp);
            
            SuperClassNode new_one = new SuperClassNode(curr);            
            this.add_child(new_one);
         }            
         
         v.expand(this, c);
      }            
   }
      
   private Comparable get_compare_object(IClassSpecProvider csp)
   {
      if(this.comp_ != null)
         return this.comp_;
      
      // ...Else:
      
      int so = SortSelector.get(8);      
      
      if(so == 0)
         return ComparableBoolean.TRUE; // All results are equal => Use natural sorting
      if(so == 1)
         comp_ = this.ch_.get_name();
      else if(so == 2)
         comp_ = JavaSpec.package_name_of(ch_.get_name());

      if(comp_ != null)
         return comp_;
      
      JavaClass jc = ch_.typed_value(csp).jc();
      if(so == 3)
         comp_ = new Integer(jc.getFields().length);
      else if(so == 4)
         comp_ = new Integer(jc.getMethods().length);
      else if(so == 5)
         comp_ = new ComparableBoolean(jc.isInterface());
      else if(so == 6)
         comp_ = new ComparableBoolean(jc.isFinal());
      else
         comp_ = new ComparableBoolean(jc.isAbstract());
      
      JimaMisc.ensure(comp_ != null);
      return comp_;
   }

   public int compare_to(AbstractNode other, IClassSpecProvider csp)
   {
      JimaMisc.ensure(other instanceof ClassNode);
      ClassNode that = (ClassNode) other;
      
      Comparable lhs = this.get_compare_object(csp);
      Comparable rhs = that.get_compare_object(csp);

      return lhs.compareTo(rhs);
   }
   
   private static class ComparableBoolean implements Comparable
   {
      public final static ComparableBoolean TRUE = new ComparableBoolean(true);
      public final static ComparableBoolean FALSE = new ComparableBoolean(false);
      
      private final boolean b;
      public ComparableBoolean(boolean b) {
         this.b = b;
      }
      
      private int asInt() { return b ? 1 : 0; }

      public int compareTo(Object o)
      {
         ComparableBoolean other = (ComparableBoolean) o;
         return asInt() - other.asInt();
      }
      
      public String toString() 
      {
         return Boolean.toString(b);
      }
   }
   
}
