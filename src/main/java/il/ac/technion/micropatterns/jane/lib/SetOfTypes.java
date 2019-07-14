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

import il.ac.technion.micropatterns.jane.elements.ClassHandle;


public class SetOfTypes
{
   private int[] arr_;
   private int len_ = 0;

   public SetOfTypes(int limit) 
   { 
      arr_ = new int[limit];
   }
   
   public void add(SetOfTypes other)
   {
      for(int i = 0; i < other.len_; ++i)
         add(other.arr_[i]);
   }
   
   public boolean full()
   {
      return len_ == arr_.length;
   }

   public void add(ClassHandle h)
   {
      if(h == null)
         return;
         
      add(h.to_integer());         
   }
      
   private void add(int n)
   {
      if(full())
         return;
         
      for(int i = 0; i < len_; ++i)
      {
         if(arr_[i] == n)
            return;
      }  
      
      arr_[len_] = n;
      len_ += 1;          
   }      
   
   public int size()
   {
      return len_;
   }
}
