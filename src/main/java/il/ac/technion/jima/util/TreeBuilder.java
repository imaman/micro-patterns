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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


public class TreeBuilder
{   
   private HashMap key2items_ = new HashMap();
   
   static class CompactNode implements Serializable
   {
      public Object key_;
      public int first_child_ = -1;
      public int next_brother_ = -1;
      
      public CompactNode(Object k)
      {
         key_ = k;
      }
   }
   
      
   private static class Item
   {
      public Item parent_ = null;
      public Object key_;
      public Vector children_ = new Vector();
      
      public Item(Object key)
      {
         key_ = key;
      }
      
      public static void attach(Item parent, Item child)
      {
         parent.children_.add(child);
         child.parent_ = parent;         
      }
      
      public Iterator children()
      {
         return children_.iterator();
      }
      
      public int hashCode()
      {
         return key_.hashCode();
      }
      
      public boolean equals(Object other)
      {
         if(other == null)
            return false;

         if(other.getClass() != this.getClass())
            return false;
            
         Item rhs = (Item) other;
         boolean result = this.key_.equals(rhs.key_);
         return result;
      }
   }
   
   
   public TreeBuilder() 
   {
   }
   
   public void add_pair(Object parent, Object child)
   {
      Item a = get_item(parent);
      Item b = get_item(child);
      
      Item.attach(a, b);
   }

   private Item get_item(Object k)
   {
      Item result = (Item) key2items_.get(k);
      if(result != null)
         return result;
         
      result = new Item(k);
      key2items_.put(k, result);
      
      return result;         
   }


//   public Iterator create_linear_order()
//   {
//      Vector temp = create_linear_order_impl();
//      Vector data = new Vector(temp.size());
//      
//      for(Iterator i = temp.iterator(); i.hasNext(); )
//      {
//         CompactNode curr = (CompactNode) i.next();
//         data.add(curr.key_);
//      }         
//      
//      return data.iterator();
//   }
   
   public TreeWalker create_walker()
   {
      Vector vec = create_linear_order_impl();
      
      TreeWalker result = new TreeWalker(vec);
      return result;
   }
   
   private Vector create_linear_order_impl()
   {
      Vector result = new Vector();
      
      CompactNode last_brother = null;
      for(Iterator i = key2items_.values().iterator(); i.hasNext(); )
      {
         Item curr = (Item) i.next();
         if(curr.parent_ != null)
            continue;

         // ...Else: This is a root            
         int index = walk(last_brother, curr, result);

         last_brother = (CompactNode) result.elementAt(index);  
      }         

      return result;
   }
   
   private int walk(CompactNode brother, Item item, Vector order)
   {
      int index = order.size();
      CompactNode cn = new CompactNode(item.key_);
      
      order.add(cn);
      
      if(brother != null)
         brother.next_brother_ = index;

      CompactNode last_brother = null;
      for(Iterator i = item.children(); i.hasNext(); )
      {        
         Item curr = (Item) i.next();
         int child_index = walk(last_brother, curr, order);
         last_brother = (CompactNode) order.elementAt(child_index);                        

         if(cn.first_child_ == -1)
            cn.first_child_ = child_index;                   
      }
      
      return index;
   }
}
