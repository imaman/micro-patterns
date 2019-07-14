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








package il.ac.technion.micropatterns.mini;

import il.ac.technion.micropatterns.janeutils.Foreign;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;

public class CheckConstantPool
{

   private static int dups = 0;
   /**
    * @param args
    */
   public static void main(String[] args)
   {
      JavaClass jc = Foreign.lookupClass("il.ac.technion.micropatterns.mini.TestA");
      
      HashSet set1 =dump(jc);
      
      jc = Foreign.lookupClass("il.ac.technion.micropatterns.mini.TestB");
      HashSet set2 =dump(jc);
      
      HashSet shared = new HashSet(set1);
      shared.retainAll(set2);
      
      
      HashSet unique = new HashSet(set1);
      unique.addAll(set2);      
      unique.removeAll(shared);
      
      print("unique=", unique);
      print("shared=", shared);
   }
   
   private static void print(String s, HashSet set)
   {
      System.out.println(s);
      for(Iterator i = set.iterator(); i.hasNext(); )
         System.out.println(i.next());
      System.out.println();
      System.out.println();
   }
   
   public static HashSet dump(JavaClass jc)
   {
      HashSet set = new HashSet();
      
      ConstantPool cp = jc.getConstantPool();
      System.out.println("pool of " + jc.getClassName() 
         + ", size=" + cp.getLength());

      for(int i = 0; i < cp.getLength(); ++i)
      {
         Constant c = cp.getConstant(i);
         if(c == null)
            continue;
   
         String s = c.toString();
         set.add(s);
      }
      
      return set;
   }
}
