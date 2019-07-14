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

import il.ac.technion.micropatterns.jane.app.gui.IView;
import il.ac.technion.micropatterns.jane.typedmodel.IClassSpecProvider;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;


public abstract class AbstractNode implements INode
{
   private Vector children_ = new Vector();
   private int depth_ = -1;
   private INode parent_ = null;
   
   private HashSet expanded_subtables_ = new HashSet();
   
   private static class MyComparator implements Comparator
   {
      private IClassSpecProvider csp_;
      public MyComparator(IClassSpecProvider csp)
      {            
         csp_ = csp;
      }
      
      public int compare(Object lhs, Object rhs)
      {
         int comp = lhs.getClass().getName().compareTo(
            rhs.getClass().getName());
         if(comp != 0)
            return comp;
         
         AbstractNode a = (AbstractNode) lhs;
         AbstractNode b = (AbstractNode) rhs;
         
         return a.compare_to(b, csp_);
      }
   }
   
   public AbstractNode() { } 
   
   public int compare_to(AbstractNode other, IClassSpecProvider csp)
   {
      return 0;
   }
   
   public synchronized boolean is_expanded(Class c)
   {
      return expanded_subtables_.contains(c);
   }
   
   private void sort_children(IClassSpecProvider csp)
   {
      Collections.sort(children_, new MyComparator(csp));
   }
   
   public synchronized void set_expanded(Class c, int expand_option, IView v)
   {
      if(expand_option == EXPAND_TOGGLE)
         expand_option = is_expanded(c) ? EXPAND_OFF : EXPAND_ON;
         
      if(expand_option == EXPAND_ON)
      {         
         expanded_subtables_.add(c);
         sort_children(v.model());
         v.reorder(true);
      }         
      else
      {
         expanded_subtables_.remove(c);         
         remove(c, v);
      }  
      
      v.repaint();                   
   }
   
   public synchronized void add_child(INode n)
   {
      children_.add(n);
      n.set_parent(this);
   }
   
   public INode get_parent()
   {
      return parent_;
   }
   
   public void set_parent(INode parent)
   {
      parent_ = parent;
      depth_ = parent_.get_depth() + 1;
   }
   
   public int get_depth()
   {
      return depth_;
   }
   
   
   public Iterator children()
   {
      return children_.iterator();
   }
   
   public synchronized int num_of_children()
   {
      return children_.size();
   }
   
   public synchronized INode child_at(int index)
   {
      INode result = (INode) children_.elementAt(index);
      return result;
   }
   
   public synchronized void remove()
   {
      children_.clear();
   }
   
   private synchronized void remove(Class c, IView v)
   {
      int n = 0;
      for(Iterator i = this.children(); i.hasNext(); )
      {
         if(i.next().getClass() == c)
            n += 1;
      }
   
      int each = n / 3;   
      if(each == 0)
         each = 1;
         
      int j = 0;                              
      for(Iterator i = this.children(); i.hasNext(); )
      {
         if(i.next().getClass() != c)
            continue;
            
         j += 1;            
         i.remove();
         
         if(j < 2 || j >= n -2 || j % each == 0)
            v.reorder(true);
      }      
   }   
}
