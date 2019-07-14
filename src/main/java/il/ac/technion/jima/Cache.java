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



package il.ac.technion.jima;

import java.util.HashMap;

public class Cache
{
   private Item head_ = new Item();
   private HashMap key2item_ = new HashMap();
   private int limit_;
   
   public Cache(int limit)
   {
      JimaMisc.ensure(limit > 4);
      limit_ = limit;
   }
   
   private static class Item
   {
      public Object k_;
      public Object d_;
      
      public Item next_ = null;
      public Item prev_ = null;

      public Item()
      {
         this(null, null);
      }
      
      public Item(Object k, Object d)
      {
         k_ = k;
         d_ = d;      
         next_ = prev_ = this;
      }
      
      public static void attach(Item a, Item new_one)
      {
         Item b = a.next_;

         new_one.next_ = b;
         new_one.prev_ = a;
         
         b.prev_ = new_one;
         a.next_ = new_one;
      }            
      
      public static void remove(Item item)
      {
         Item a = item.prev_;
         Item b = item.next_;
         
         a.next_ = b;
         b.prev_ = a;
         
         item.next_ = item.prev_ = item;
      }
   }
   
   public synchronized Object get(Object k)
   {
      Item result = get_impl(k);
      if(result == null)
         return null;

      // ...Else:         
      return result.d_;
   }


   private Item get_impl(Object k)
   {
      Item result = (Item) key2item_.get(k);
      if(result == null)
         return null;
         
      // ...Else:
      Item.remove(result);
      Item.attach(head_, result);
      
      return result;               
   }
   
   public synchronized void put(Object k, Object d)
   {
      Item it = get_impl(k);
      if(it != null)
      {
         it.d_ = d;
         return;
      }         

      while(size() >= limit_)
      {
         Item last = head_.prev_;

         Item.remove(last);
         key2item_.remove(last.k_);
      }      
      
      it = new Item(k, d);
      key2item_.put(k, it);
      Item.attach(head_, it);
   }
   
   public synchronized int size()
   {
      return key2item_.size();
   }
}
