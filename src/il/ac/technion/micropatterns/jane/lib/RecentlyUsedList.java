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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

public class RecentlyUsedList 
{
   private String name_;
   private int size_;
   private LinkedList items_ = new LinkedList();
   private HashSet set_ = new HashSet();
   
   public RecentlyUsedList(String name, int size)
   {
      size_ = size;
      name_ = name;
   }
   
   private void update()
   {
      HashSet temp = new HashSet();
      for(Iterator i = iterator(); i.hasNext(); )
         temp.add(i.next());
         
      set_ = temp;         
   }
   
   public void clear()
   {
      items_.clear();
      update();
   }
   
   public boolean contains(String s)
   {
      return set_.contains(s);
   }
   
   public void add(String s)
   {
      items_.remove(s);
      items_.addFirst(s);
      
      while(items_.size() > size_)
         items_.removeLast();
         
      update();         
   }
   
   public void save(Properties p)
   {
      int n = -1;
      for(Iterator i = items_.iterator(); i.hasNext(); )
      {
         n += 1;
         
         String curr = i.next().toString();
         
         String key = name_ + ".rul." + Integer.toString(n);
         p.setProperty(key, curr);         
      }         
   }
   
   public void load(Properties p)
   {
      LinkedList temp = new LinkedList();

      while(true)
      {
         int n = temp.size();
         String key = name_ + ".rul." + Integer.toString(n);
         String curr = p.getProperty(key);
         
         if(curr == null)
            break;
            
         // ...Else:
         temp.addLast(curr);
      }         
      
      items_ = temp;
      update();
   }
   
   public Iterator iterator()
   {
      return items_.iterator();
   }
   
}  
