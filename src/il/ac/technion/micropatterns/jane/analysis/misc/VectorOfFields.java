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











package il.ac.technion.micropatterns.jane.analysis.misc;

import il.ac.technion.micropatterns.jane.lib.JavaSpec;

import java.util.Vector;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;

public class VectorOfFields
{
   private Vector vec_ = new Vector();

   public VectorOfFields() { }
   
   public VectorOfFields(Field[] fields)
   {
      for(int i = 0; i < fields.length; ++i)
         add(fields[i]);
   }
   
   public void add(Field f)
   {
      if(JavaSpec.is_serialization_stuff(f))
         return;
      
      vec_.add(f);
   }
   
   public Field at(int n)
   {
      return (Field) vec_.get(n);      
   }
   
   public int size()
   {
      return vec_.size();
   }
   
   public void addAll(JavaClass jc)
   {
      VectorOfFields temp = new VectorOfFields(jc.getFields());
      this.addAll(temp);
   }
   
   public void addAll(VectorOfFields src)
   {
      vec_.addAll(src.vec_);
   }
   
   public String toString()
   {
      return vec_.toString();
   }
}
