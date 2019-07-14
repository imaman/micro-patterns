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
 * Created on Nov 20, 2004
 * Written by spiderman
 * Project: JarScan
 */

package il.ac.technion.micropatterns.jane.lib;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEVIRTUAL;

public class MethodSelector
{
   private String name_;
   private String signature_;
   
   protected MethodSelector(String name, String sig)
   {
      name_ = name;
      signature_ = sig;
   }
   
   public MethodSelector(Method m)
   {
      name_ = m.getName();
      signature_ = m.getSignature();
   }

   public MethodSelector(INVOKEVIRTUAL iv, ConstantPoolGen cpg)
   {
      this(iv.getMethodName(cpg), iv.getSignature(cpg));
   }
   
   public String get_name()
   {
      return name_;
   }
   
   public String get_signature()
   {
      return signature_;
   }
   
   public int hashCode()
   {
      int result = name_.hashCode() ^ signature_.hashCode();
      return result;
   }


   public boolean matches(Method m)
   {
      MethodSelector temp = new MethodSelector(m);
      boolean result = temp.equals(this);
      
      return result;
   }
   
   public boolean equals(Object other)
   {
      if(other == null)
         return false;
      
      if(!(other instanceof MethodSelector))
         return false;
      
      MethodSelector rhs = (MethodSelector) other;
      boolean result = this.name_.equals(rhs.name_) 
         && this.signature_.equals(rhs.signature_);
      
      return result;
   }
   
   public String toString()
   {
      return "MethodSelector(" + name_ + " " + signature_ + ")";
   }
}
