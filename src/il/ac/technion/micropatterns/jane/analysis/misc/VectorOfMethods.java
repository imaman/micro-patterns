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

import il.ac.technion.micropatterns.jane.lib.MethodSelector;

import java.util.HashMap;
import java.util.Vector;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class VectorOfMethods
{
   private Vector vec_ = new Vector();
   private HashMap selector2method_ = new HashMap();
   
   
   public VectorOfMethods() { }
   
   public VectorOfMethods(Method[] methods)
   {
      for(int i = 0; i < methods.length; ++i)
         add(methods[i]);
   }
   
   public int num_of_instance_methods()
   {
      return selector2method_.size();
   }
   
   public void clear()
   {
      vec_.clear();
      selector2method_.clear();
   }
   
   public void add(Method m)
   {
      vec_.add(m);
      
      if(!m.isStatic() && !m.isPrivate())
         selector2method_.put(new MethodSelector(m), m);
   }
   
   public Method at(int n)
   {
      return (Method) vec_.get(n);      
   }
   
   public Method lookup(MethodSelector ms)
   {
      return (Method) selector2method_.get(ms);
   }
   
   
   public int size()
   {
      return vec_.size();
   }

   public void addAll(JavaClass jc)
   {
      VectorOfMethods temp = new VectorOfMethods(jc.getMethods());
      this.addAll(temp);
   }
   
   public void addAll(VectorOfMethods src)
   {
      for(int i = 0; i < src.size(); ++i)
         this.add(src.at(i));
   }

   public String toString()
   {
      return vec_.toString();
   }
}
