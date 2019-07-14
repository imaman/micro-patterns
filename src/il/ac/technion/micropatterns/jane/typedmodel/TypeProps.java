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









package il.ac.technion.micropatterns.jane.typedmodel;

import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.model.AbstractElement;
import il.ac.technion.micropatterns.jane.model.IHandle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

public class TypeProps extends AbstractElement
{
   private HashMap handle2item_ = new HashMap();
   
   public TypeProps() { }
   
   public Serializable get(ClassHandle h)
   {
      return (Serializable) handle2item_.get(h);   
   }
   
   public Serializable get(ClassSpec ce)
   {
      return get(ce.get_handle());
   }
   
   private Serializable get(IHandle h)
   {
      Serializable result = (Serializable) handle2item_.get(h);
      return result;
   }
   
   public Iterator values()
   {
      return handle2item_.values().iterator();
   }
   
   public void put(ClassHandle key, Serializable data)
   {
      handle2item_.put(key, data);
   }

   private void put(IHandle key, Serializable data)
   {
      handle2item_.put(key, data);
   }
   
   public boolean contains(ClassHandle key)
   {
      return handle2item_.containsKey(key);
   }
}
