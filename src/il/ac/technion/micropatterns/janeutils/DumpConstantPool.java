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








package il.ac.technion.micropatterns.janeutils;

import il.ac.technion.micropatterns.jane.lib.JavaSpec;

import java.util.Iterator;

import org.apache.bcel.classfile.JavaClass;

public class DumpConstantPool
{
   public static void main(String[] args)
   {
      JavaClass jc = Foreign.lookupClass("java.util.HashMap");
      
      
      for(Iterator i = JavaSpec.associated_classes(jc, false); i.hasNext(); )
         System.out.println(i.next());

      System.out.println("======================================");
      for(Iterator i = JavaSpec.aggregated_classes(jc); i.hasNext(); )
         System.out.println(i.next());
      
//      
//      ConstantPool cp = jc.getConstantPool();
//      for(int i = 0; i < cp.getLength(); ++i)
//      {
//         Constant c = cp.getConstant(i);
//         if(c instanceof ConstantClass)
//         {
//            ConstantClass cc = (ConstantClass) c;
//            int index = cc.getNameIndex();
//            
//            Constant cnat = cp.getConstant(index);            
//            System.out.println(i + ") " + cc + ", " + cnat);            
//         }
//      }
   }
}
