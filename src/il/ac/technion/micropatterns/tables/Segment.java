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








package il.ac.technion.micropatterns.tables;

import java.util.Iterator;
import java.util.Vector;

public class Segment
{
   public Table t_;
   public int c0_;
   public int r0_;
   public int c1_;
   public int r1_;
   
   public Segment(Table t, int c0, int r0, int c1, int r1)
   {
      t_ = t;
      c0_ = c0;
      c1_ = c1;
      r0_ = r0;
      r1_ = r1;
   }
   
   public int rowCount()
   {
      return r1_ - r0_ + 1;
   }
   
   public int colCount()
   {
      return c1_ - c0_ + 1;
   }
   
   public Iterator makeEntries()
   {
      Vector result = new Vector();
      for(int r = r0_; r <= r1_; ++r)
      {
         for(int c = c0_; c <= c1_; ++c)
         {
            String s = t_.at(c, r);
            Entry e = new Entry(s, c - c0_, r - r0_);
            result.add(e);
         } 
      }
      
      return result.iterator();
   }
}
