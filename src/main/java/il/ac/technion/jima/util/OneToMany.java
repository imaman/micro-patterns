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
import java.util.LinkedList;
import java.util.Set;

public class OneToMany implements Serializable
{
   private HashMap map_ = new HashMap();
   private static final LinkedList EMPTY_LIST = new LinkedList();
   
   private int element_count_ = 0;
   
   public void put(Object k, Object d)
   {
      LinkedList list = (LinkedList) map_.get(k);
      if(list == null)
      {
         list = new LinkedList();
         map_.put(k, list);
      }         
      
      list.add(d);        
      element_count_ += 1;
   }
   
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
   
   public int key_count()
   {
      return map_.size();
   }
   
   public int element_count()
   {
      return element_count_;
   }
   
   public Set keySet()
   {
      return map_.keySet();
   }
}
