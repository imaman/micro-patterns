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

public class OptionalYesNoMap implements Serializable
{
   private HashMap map_ = new HashMap();
   
   public OptionalYesNoMap()  { }
   
   public void set_yes_no(Object k, boolean yes_no)
   {
      map_.put(k, yes_no ? Boolean.TRUE : Boolean.FALSE);
   }

   public void set_yes_no(Object k, OptionalYesNo oys)
   {
      if(oys.is_neither())
         clear_yes_no(k);
      else
         set_yes_no(k, oys.is_positive());         
   }
   
   public OptionalYesNo get(Object k)
   {
      Boolean temp = (Boolean) map_.get(k);
      if(temp != null)
         return OptionalYesNo.select(temp.booleanValue());         
         
      return OptionalYesNo.NEITHER;
   }

   public OptionalYesNo get(Object k, OptionalYesNo default_result)
   {
      Boolean temp = (Boolean) map_.get(k);
      if(temp != null)
         return OptionalYesNo.select(temp.booleanValue());         

      // ...Else:
      set_yes_no(k, default_result);         
      return default_result;
   }
   
   
   
   public void clear_yes_no(Object k)
   {
      map_.remove(k);
   }
}
