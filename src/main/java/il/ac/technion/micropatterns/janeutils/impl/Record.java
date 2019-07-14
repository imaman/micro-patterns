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








/*
 * Created on Nov 10, 2004
 * Written by spiderman
 * Project: JarScan
 */

package il.ac.technion.micropatterns.janeutils.impl;

import il.ac.technion.micropatterns.jane.app.Common;

import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;


public class Record
{
   private int[] values_ = new int[DescCP.size()];
   private String class_name_;
   private String collection_name_;   
   private int method_count_ = -1;
   private boolean is_interface_;
      
   public Record(String line, int[] order_translation)
   {
      int pass_n = -1;
      int index = -1;
      
      StringTokenizer st = new StringTokenizer(line, ",");
      while(st.hasMoreTokens())
      {
         pass_n += 1;
         
         String curr = st.nextToken().trim();
//         System.out.println(curr);
         
         if(pass_n == 0)
            class_name_ = curr;
         if(pass_n == 1)
            collection_name_ = curr;
         if(pass_n == 2)
            is_interface_ = !curr.toLowerCase().equals("n");
         if(pass_n == 3)
         {
            if(curr.startsWith(Common.METHOD_COUNT_PREFIX))
            {
               String temp 
                  = curr.substring(Common.METHOD_COUNT_PREFIX.length());
               method_count_ = Integer.parseInt(temp);
            }
            else
               method_count_ = Integer.parseInt(curr);                              
         }
         
         if(pass_n <= 3)
            continue;         
                  
         // ...Else: 
         index += 1;         
         values_[index] = Integer.parseInt(curr);
      }
   }

   public boolean is_interface()
   {
      return is_interface_;
   }
   
   public int get_method_count()
   {
      return method_count_;
   }
   
   public String get_collection_name()
   {
      return collection_name_;
   }
   
   public boolean is_same(Record that)
   {
      for(int i = 0; i < values_.length; ++i)
      {
         if(this.values_[i] != that.values_[i])
            return false;
      }
      
      return true;
   }
   
   public HashSet get_on()
   {
      HashSet result = new HashSet();
      
      for(int i = 0; i < values_.length; ++i)
      {
         if(values_[i] == 0)
            continue;
         
         result.add(DescCP.get(i));
      }
      
      return result;
   }
   
   public boolean is_none()
   {
      for(int i = 0; i < values_.length; ++i)
      {
         if(values_[i] != 0)
            return false;
      }
      
      return true;            
   }
   
   public String type_name()
   {
      return class_name_;
   }
   
   public String toString()
   {
      return class_name_;
   }
   
   public boolean equals(Object other)
   {
      if(other == null)
         return false;
      
      if(other == this)
         return true;
      
      if(other.getClass() != this.getClass())
         return false;
     
      Record rhs = (Record) other;
      
      boolean result = this.class_name_.equals(rhs.class_name_);
      return result;            
   }
   
   public int hashCode()
   {
      return class_name_.hashCode();
   }
   
   public void register(Vector coding_patterns)
   {
      
      for(int i = 0; i < values_.length; ++i)
      {
         if(values_[i] == 0)
            continue;
         
         CodingPattern cp = (CodingPattern) coding_patterns.elementAt(i);
         cp.add(this);
      }         
   }
}
