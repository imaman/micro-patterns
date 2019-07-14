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

public class OptionalYesNo implements Serializable
{   
   public static final OptionalYesNo POSITIVE = new OptionalYesNo(true);
   public static final OptionalYesNo NEGATIVE = new OptionalYesNo(false);
   public static final OptionalYesNo NEITHER = new OptionalYesNo();
   
   private static final int YES = 1;
   private static final int NO = 0;
   private static final int UNKNOWN = -1;
   
   
   private static final String POSITIVE_TEXT = "Positive";
   private static final String NEGATIVE_TEXT = "Negative";
   private static final String NEITHER_TEXT = "Neither";

   private final int value_;

   public OptionalYesNo()
   {
      value_ = UNKNOWN;
   }
   
   private OptionalYesNo(boolean yes_no)
   {
      if(yes_no)
         value_ = YES;
      else 
         value_ = NO;                  
   }
   
   public static OptionalYesNo select(boolean yes_no)
   {
      if(yes_no)
         return POSITIVE;
      else
         return NEGATIVE;         
   }
   
   public static OptionalYesNo select(OptionalYesNo other)
   {
      if(other == null || other.is_neither())
         return NEITHER;
         
      if(other.is_positive())
         return POSITIVE;
      else
         return NEGATIVE;         
   }
   
   
   public static OptionalYesNo select(Boolean yes_no)
   {
      if(yes_no == null)
         return NEITHER;
         
      // ...Else:
      return select(yes_no.booleanValue());
   }
   
   public static OptionalYesNo next(OptionalYesNo oys)
   {
      if(oys.is_negative())
         return POSITIVE;

      if(oys.is_positive())
         return NEITHER;
         
      // ...Else:
      return NEGATIVE;     
   }
   
   public boolean is_neither()
   {
      return value_ == UNKNOWN;
   }
   
   public boolean is_positive()
   {
      return value_ == YES;
   }
   
   public boolean is_negative()
   {
      return value_ == NO;
   }
   
   public String toString()
   {
      if(is_negative())
         return NEGATIVE_TEXT;
         
      if(is_positive())
         return POSITIVE_TEXT;
         
      // ...Else:
      return NEITHER_TEXT;                  
   }
   
   public int hashCode()
   {
      return value_;
   }
   
   public boolean equals(Object other)
   {
      if(other == null)
         return false;
         
      if(other == this)
         return true;
         
      if(other.getClass() != this.getClass())
         return false;
         
      OptionalYesNo rhs = (OptionalYesNo) other;
      return this.value_ == rhs.value_;                        
   }
}
