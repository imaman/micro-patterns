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



package il.ac.technion.jima.util;

import il.ac.technion.jima.JimaMisc;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;



public class TreeWalker implements Serializable
{
   private Vector compact_nodes_;
   private int pos_ = 0;
   
   private static final TreeWalker NULL_WALKER = new TreeWalker(new Vector());
   
   
   public TreeWalker(Vector compact_nodes)
   {
      this(compact_nodes, 0);
   }
   
   private TreeWalker(Vector compact_nodes, int pos)
   {
      compact_nodes_ = compact_nodes;
      
      pos_ = pos;
      if(pos_ >= compact_nodes_.size())
         pos_ = -1;
   }

   public TreeWalker root()
   {
      return new TreeWalker(compact_nodes_);
   }
   
   public int offset()
   {
      return pos_;
   }
   
   public int size()
   {
      return compact_nodes_.size();
   }
   
   private class MyIter implements Iterator
   {
      private int pos_;
      private int end_;
      
      public MyIter(int begin, int end)
      {
         pos_ = begin;
         end_ = end;
      }
      
      public boolean hasNext()
      {
         return pos_ < end_;
      }
      
      public void remove()
      {
         JimaMisc.ensure(false);
      }
      
      public Object next()
      {         
         TreeBuilder.CompactNode cn 
            = (TreeBuilder.CompactNode) compact_nodes_.elementAt(pos_);
         return cn.key_;
      }
   }
   
   public Iterator create_linear_order()
   {
      JimaMisc.ensure(ok());
            
      Vector result = new Vector();
      linear_impl(result);
      
      return result.iterator();
   }
   
   private void linear_impl(Vector result)
   {
      if(!ok())
         return;
      
      result.add(this.data());
      for(TreeWalker w = this.first_child(); w.ok(); w = w.next_brother())
         w.linear_impl(result);
   }

   
   private TreeBuilder.CompactNode current()
   {
      if(!ok())
         return null;
         
      return (TreeBuilder.CompactNode) compact_nodes_.elementAt(pos_);
   }
   
   public boolean ok()
   {
      return pos_ >= 0;
   }
   
   public TreeWalker next_brother()
   {
      if(!ok())
         return NULL_WALKER;
         
      int next = current().next_brother_;
      return new TreeWalker(compact_nodes_, next);
   }
   
   public TreeWalker first_child()
   {
      if(!ok())
         return NULL_WALKER;
         
      int next = current().first_child_;
      return new TreeWalker(compact_nodes_, next);         
   }
   
   public Object data()
   {
      if(!ok())
         return null;
                  
      TreeBuilder.CompactNode temp = current();
      JimaMisc.ensure(temp != null);
      
      Object result = temp.key_;
      return result;
   }
}
