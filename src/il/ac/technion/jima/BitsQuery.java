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

import il.ac.technion.jima.util.VectorOfInts;

import java.util.Iterator;
import java.util.Vector;

public class BitsQuery
{
   public static final int ON = 1;
   public static final int OFF = 2;
   public static final int DC = 3;
   
   
   private Vector items_ = new Vector();
   
   private static class Item
   {
      public Item(int bit, int mode)
      {
         bit_ = bit;
         mode_ = mode;
      }
      
      public boolean insepct(int bits)
      {
         if(mode_ == DC)
            return true;
            
         boolean is_on = (bits & bit_) != 0;

         if(is_on && mode_ == ON)
            return true;
            
         if(!is_on && mode_ == OFF)
            return true;
            
         // ...Else:
         return false;                        
      }
      
      private int bit_;
      private int mode_;
   }
   
   public BitsQuery add(int bit, int mode)
   {
      items_.add(new Item(bit, mode));   
      return this;
   }
   
   
   public boolean inspect(int bits)
   {
      Iterator iter = items_.iterator();
      while(iter.hasNext())
      {
         Item curr = (Item) iter.next();
         if(!curr.insepct(bits))
            return false;                                 
      }                  
      
      return true;
   }
   
   public int count(VectorOfInts voi)
   {
      int result = 0;
      for(int i = 0; i < voi.size(); ++i)
      {
         if(inspect(voi.intAt(i)))
            result +=  1;
      }         
      
      return result;
   }

}
