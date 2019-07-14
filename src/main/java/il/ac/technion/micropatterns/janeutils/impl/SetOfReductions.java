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









package il.ac.technion.micropatterns.janeutils.impl;

import il.ac.technion.jima.JimaMisc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;


public class SetOfReductions 
{
   private HashSet set_ = new HashSet();
   private Vector vec_ = new Vector();
   
   public SetOfReductions() { }
      
   public void add(Reduction r)
   {
      if(set_.contains(r))
         return;
      
      JimaMisc.log().println("Adding reduction " + r);
      
      set_.add(r);
      vec_.add(r);

      Collections.sort(vec_);      
   }
   
   public void sort()
   {
      Collections.sort(vec_);
   }
   
   public int size()
   {
      return set_.size();
   }
   
   public Reduction get(int i)
   {
      return (Reduction) vec_.get(i);
   }
   
   public Iterator iterator()
   {
      Collections.sort(vec_);
      return vec_.iterator();
   }
}
