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










package il.ac.technion.micropatterns.jane.analysis.agents;

import il.ac.technion.micropatterns.jane.analysis.misc.InheritanceDescendingAgent;
import il.ac.technion.micropatterns.jane.analysis.misc.Result;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import org.apache.bcel.classfile.JavaClass;

public class Faked extends InheritanceDescendingAgent
{

   private Result md4_;
   private Result fd4_;
   private Result mpr_;
   private Result fpr_;
   private Result cnd4_;
   private Result cnd5_;
   private Result cnpr_;
   
   public Faked()
   {
      super("Faked", null); //DescCP.METHODS_DIV_4);
      
      md4_ = main_result_;
      
//      fd4_ = Result.new_result(this, DescCP.FIELDS_DIV_4);
//      mpr_ = Result.new_result(this, DescCP.METHODS_IS_PRIME);
//      fpr_ = Result.new_result(this, DescCP.FIELDS_IS_PRIME);
//      cnd4_ = Result.new_result(this, DescCP.CLASS_NAME_DIV_4);
//      cnd5_ = Result.new_result(this, DescCP.CLASS_NAME_DIV_5);
//      cnpr_ = Result.new_result(this, DescCP.CLASS_NAME_IS_PRIME);
   }
   
   

   /**
    * @see il.ac.technion.micropatterns.jane.analysis.misc.InheritanceDescendingAgent#inspect(il.ac.technion.micropatterns.jane.elements.ClassHandle)
    */
   protected boolean inspect(ClassHandle h)
   {
      ClassSpec cs = h.typed_value(xmodel_);
      JavaClass jc = cs.jc();
      
      
      if(jc.getMethods().length % 4 == 0)
         this.md4_.set_decision(h, true);
      
      if(jc.getFields().length % 4 == 0)
         this.fd4_.set_decision(h, true);
      
      if(isprime(jc.getMethods().length))
         this.mpr_.set_decision(h, true);
      
      if(isprime(jc.getFields().length))
         this.fpr_.set_decision(h, true);
   
      String s = jc.getClassName();
      int len = s.length() - jc.getPackageName().length();
      
      
      if(len % 4 == 0)
         this.cnd4_.set_decision(h, true);
      
      if(len % 5 == 0)
         this.cnd5_.set_decision(h, true);
      
      
      if(isprime(len))
         this.cnpr_.set_decision(h, true);
     
      
      return true;
      
   }
   
   private static boolean isprime(int n)
   {
      if(n <= 2)
         return false;
      
      for(int d = 2; d * d <= n; ++d)
      {
         if(n % d == 0)
            return false;
      }
      
      return true;
   }
}
