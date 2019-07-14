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

import il.ac.technion.micropatterns.jane.elements.MethodHandle;
import il.ac.technion.micropatterns.jane.elements.MethodSpec;

import java.io.Serializable;
import java.util.HashMap;

public class MapOfMethods  implements Serializable
{
   private HashMap map_ = new HashMap();
   
   public void put(MethodHandle key, Serializable data)
   {
      map_.put(key, data);
   }
   
   public void put(MethodSpec key, Serializable data)
   {
      map_.put((MethodHandle) key.get_handle(), data);
   }
   
   public Serializable get(MethodHandle key)
   {
      Serializable result = (Serializable) map_.get(key);
      return result;
   }
   
   public int size()
   {
      return map_.size();
   }
   
   public boolean contains(MethodHandle key)
   {
      return map_.containsKey(key);
   }
   
   public String toString()
   {
      return map_.toString();
   }
}
