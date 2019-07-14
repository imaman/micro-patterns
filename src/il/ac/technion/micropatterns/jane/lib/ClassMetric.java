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

import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.janeutils.impl.ImplUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;

public class ClassMetric
{
   
   public static String title_csv()
   {
      return "ref.types, depth"; 
   }
   
   public String to_csv(int n)
   {
      double rt = (double) this.referenced_types_ / n;
      double d = (double) this.depth_ / n;
      
      return ImplUtils.to01(rt) + ", " + ImplUtils.to01(d);
   }
   
   public int referenced_types_ = 0;
   public int depth_ = 0;

   public ClassMetric() { }
   
   public void add(ClassMetric that)
   {
      referenced_types_ += that.referenced_types_;
      depth_ += that.depth_;
   }
   

   public ClassMetric(ClassSpec cs)
   {
      JavaClass jc = cs.jc();
      
      this.referenced_types_ = num_referenced_types(jc, false);
      
      ClassSpec curr = cs;

      depth_ = -1;
      for(Iterator i = curr.super_classes(); i.hasNext(); )
      {
         depth_ += 1;
         i.next();
      }
   }
   
   
   
   private static int num_referenced_types(JavaClass jc, 
      boolean include_primitives)
   {
      Collection result = new HashSet();
      
      ConstantPool cp = jc.getConstantPool();
      for(int i = 0; i < cp.getLength(); ++i)
      {
         Constant c = cp.getConstant(i);
         if(c instanceof ConstantClass)
         {
            ConstantClass cc = (ConstantClass) c;
            int index = cc.getNameIndex();
            
            ConstantUtf8 name = (ConstantUtf8) cp.getConstant(index);
            String str = name.getBytes().replace('/', '.');
            
            result.add(str);            
         }
      }
      
      return result.size();
   }
}
