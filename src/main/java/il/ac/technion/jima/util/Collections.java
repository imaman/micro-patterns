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

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Vector;



public class Collections
{
   public static Iterator makeIter(Object[] array)
   {
      return new ArrayIter(array);
   }
   
   public static Iterator makeIter(Vector v)
   {
      return v.iterator();
   }
   
   public static Iterator sort(Iterator elements)
   {
      Vector v = new Vector();
      addAll(v, elements);
      
      java.util.Collections.sort(v);
      return v.iterator();
   }
   
   public static AbstractCollection addAll(AbstractCollection dst, Iterator src)
   {
      while(src.hasNext())
         dst.add(src.next());
      
      return dst;
   }

   public static AbstractCollection addAll(AbstractCollection dst, 
      AbstractCollection src)
   {
      dst.addAll(src);
      return dst;
   }
   
   private static class ArrayIter implements Iterator
   {
      public ArrayIter(Object[] array)
      {
         array_ = array;
         pos_ = -1;
      }

      private Object[] array_;
      private int pos_;

      public boolean hasNext()
      {
         return (pos_ + 1) < array_.length;
      }

      public Object next()
      {
         pos_ += 1;
         return array_[pos_];
      }

      public void remove()
      {
         JimaMisc.wrongWay();
      }
   }
}
