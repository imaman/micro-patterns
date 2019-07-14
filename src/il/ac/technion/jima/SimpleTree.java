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


import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class SimpleTree implements Serializable
{
   private HashMap map_ = new HashMap();
   private static final LinkedList EMPTY_LIST = new LinkedList();
   
   private static class Item
   {
      public Object data_;
      public LinkedList children_ = new LinkedList();

      public Item(Object data)
      {
         data_ = data;
      }      
   }
   
   public void put(Object d)
   {
      put_impl(d);      
   }
   
   private synchronized Item put_impl(Object d)
   {
      Item item = new Item(d);
      
      Integer k = new Integer(map_.size());      
      map_.put(k, item);
      
      return item;
   }
   
//   public void put(int parent_key, Object d)
//   {
//      Item parent = (Item) map_.get(new Integer(parent_key));
//      
//      Item 
//      
//   }
   
   
   public Iterator get(Object k)
   {
      LinkedList list = (LinkedList) map_.get(k);
      if(list == null)
         return EMPTY_LIST.iterator();
         
      return list.iterator();      
   }
   
   public boolean has(Object k)
   {
      return count_of(k) == 0;
   }
   
   public int count_of(Object k)
   {
      LinkedList list = (LinkedList) map_.get(k);
      if(list == null)
         return 0;
         
      return list.size();
   }   
}
