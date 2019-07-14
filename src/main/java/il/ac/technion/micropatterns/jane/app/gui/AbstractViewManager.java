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











package il.ac.technion.micropatterns.jane.app.gui;

import il.ac.technion.jima.IWidthListener;
import il.ac.technion.jima.JimaMisc;
import il.ac.technion.jima.powergui.IPowerTableProvider;
import il.ac.technion.jima.powergui.PowerTable;
import il.ac.technion.micropatterns.jane.app.gui.nodes.AbstractNode;
import il.ac.technion.micropatterns.jane.app.gui.nodes.INode;
import il.ac.technion.micropatterns.jane.app.gui.rows.AbstractRow;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.util.Iterator;
import java.util.Vector;


public abstract class AbstractViewManager implements IView, IPowerTableProvider
{   
   protected AbstractNode root_ = new AbstractNode()
      {
         public AbstractRow create_row(IView v, int index, IWidthListener wl)
         {
            JimaMisc.ensure(false); // Wrong Way
            return null; // Faked
         }                        
         
         public boolean match(String s)
         {
            JimaMisc.ensure(false); // Wrong Way
            return false; // Faked
         }
      };
   
   protected IWidthListener width_listener_;
   protected RowFactory row_factory_ = new RowFactory();
   protected IJaneViewListener listener_;
   private TypedModel model_;
   protected Vector seq_ = new Vector();
   protected PowerTable pt_;

   public AbstractViewManager(PowerTable pt, IJaneViewListener listener, 
      IWidthListener width_listener)
   {
      width_listener_ = width_listener;
      listener_ = listener;
      pt_ = pt;
      
//      vsb_.setMaximum(pt_.size());
//      
   }
   
   abstract protected void assign_model_impl(TypedModel m);
   
   public void assign_model(TypedModel m)
   {      
      if(m == null)
         return;
         
      assign_model_impl(m);     
      set_model(m);

      reorder(false);
   }
   
   public TypedModel model()
   {
      return model_;
   }
   
   protected void set_model(TypedModel m)
   {
      model_ = m;
   }
   
   public Iterator get_iterator(int row_begin, int row_end)
   {
      Vector result = new Vector();

      Class last_row_class = null;
      Class last_node_class = null;
      
      boolean header_shown = false;
      
      int i = row_begin;      
      while(true)
      {
         if(i >= row_end)
            break;
            

         INode curr = (INode) seq_.elementAt(i);
         i += 1;
         
         int depth = curr.get_depth();
//         if(depth < last_depth)
//            result.add(row_factory_.empty_row());
            
         AbstractRow pr = curr.create_row(this, i, width_listener_);

         Class row_class = pr.getClass();
         Class node_class = curr.getClass();
         
         if(node_class != last_node_class || row_class != last_row_class)
         {
            if(depth != 0 || !header_shown)
               result.add(pr.create_header());
            
            last_node_class = node_class;
            last_row_class = row_class;
            
            if(depth == 0)
               header_shown = true;
         }            
         
         result.add(pr);                 
      }            
      
      return result.iterator();   
   }
   
   private class SearchIter implements Iterator
   {
      private String s_;
      private int index_ = -1;
      
      public SearchIter(String s)
      {
         s_ = s.toLowerCase();
         index_ = move_on(0);
      }
         /**
       * @see java.util.Iterator#hasNext()
       */
      public boolean hasNext()
      {
         return index_ != -1;
      }

      /**
       * @see java.util.Iterator#next()
       */
      public Object next()
      {
         Integer result = new Integer(index_);
         index_ = move_on(index_ + 1);
         
         return result;
      }
      
      private int move_on(int start)
      {
         for(int i = start; i < seq_.size(); ++i)
         {
            INode curr = (INode) seq_.elementAt(i);
            if(curr.match(s_))
               return i;
         }
         
         return -1;
      }

      /**
       * @see java.util.Iterator#remove()
       */
      public void remove()
      {
         JimaMisc.ensure(false);
      }

}

   public Iterator find(String s)
   {      
      return new SearchIter(s);      
   }
   
   public void assign_vert_offset(int offset)
   {
      this.listener_.vert_scroll_to(offset);
      this.pt_.set_vertical_offset(offset);
      this.pt_.as_component().repaint();         
   }
   
   public void repaint()
   {
      pt_.as_component().getParent().repaint();
   }

   
   public void reorder(boolean immediate_repaint)
   {
      Vector temp = new Vector();
      reorder_impl(temp, root_);
      
      seq_ = temp;
                  
      int top = this.seq_.size();      
      pt_.set_data(this, top);                  
      this.listener_.set_vert_range(top);

      if(immediate_repaint)
      {
         int w = pt_.as_component().getWidth();
         int h = pt_.as_component().getHeight();
         
         this.pt_.as_component().paintImmediately(0, 0, w, h);
      }
      else
      {
         this.pt_.as_component().repaint();
      }            
   }

   private void reorder_impl(Vector result, INode n)
   {
      for(Iterator i = n.children(); i.hasNext(); )
      {
         INode curr = (INode) i.next();
         result.add(curr);
         
         reorder_impl(result, curr);
      }
   }

   public void collapse(INode n, Class c)
   {
      n.set_expanded(c, INode.EXPAND_OFF, this);
   }
   
   public void expand(INode n, Class c)
   {
      n.set_expanded(c, INode.EXPAND_ON, this);
//      this.reorder();      
   }

}
