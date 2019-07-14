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
 * Created on Nov 18, 2004
 * Written by spiderman
 * Project: JarScan
 */

package il.ac.technion.micropatterns.janeutils.impl;

import il.ac.technion.jima.JimaMisc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;


public class CodingPattern implements Comparable, Cloneable
{
   private HashSet set_ = new HashSet();
   private DescCP desc_;
   private String x_name_;
   private String lib_;
   
   
   /**
    * @see java.lang.Comparable#compareTo(T)
    */
   public int compareTo(Object other)
   {
      if(other == null)
         return 1;
      
      CodingPattern rhs = (CodingPattern) other;
      return this.desc_.get_id() - rhs.desc_.get_id();
   }
   
   public String toString()
   {
      return full();
   }
   
   public String full()
   {
      return lib_ + "/" + desc_.out_name();
   }
   
   public String out_name()
   {
      return desc_.out_name();
   }
   
   public void set_lib(String lib)
   {
      lib_ = lib;
   }  

   public CodingPattern(DescCP desc)
   {
      this(desc, "");
   }

   public CodingPattern(DescCP desc, String lib)
   {
      this(desc, lib, new Vector().iterator());
   }

   public CodingPattern(DescCP desc, String lib, Iterator types)
   {
      desc_ = desc;
//      name_ = desc_.get_name();
      lib_ = lib.toLowerCase().trim();
      
      while(types.hasNext())
         set_.add(types.next());
   }
   
   public void add(Object o)
   {
      set_.add(o);
   }
   
   public HashSet as_set()
   {
      return set_;
   }
   
   public DescCP get_desc()
   {
      return this.desc_;
   }
//   public String name()
//   {
//      return desc_.
//   }
   
   public CodingPattern reduce(HashSet negative_world)
   {
      CodingPattern result = (CodingPattern) this.clone();
      result.set_ = Sets.difference(this.set_, negative_world);
      
      return result;
   }
   
   public int size()
   {
      return this.as_set().size();
   }
   
   public Object clone()
   {
      try
      {
         CodingPattern result = (CodingPattern) super.clone();
         result.set_ = (HashSet) this.set_.clone();
         return result;
      }
      catch(CloneNotSupportedException e)
      {
         JimaMisc.ensure(false);
         return null; // Faked
      }
   }
}
