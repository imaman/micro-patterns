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
import il.ac.technion.micropatterns.jane.lib.ObjectToInt;

import java.util.Comparator;

import il.ac.technion.micropatterns.jungle.model.CollectionDesc;

public class CollectionSorter implements Comparator
{

   private static ObjectToInt lib2sn_ = new ObjectToInt();
   
   
   public static void register(CollectionDesc cd)
   {
      add(cd);
   }
   
//   static
//   {
//      add(CollectionDesc.LIB_KAFFEA);      
//      add(CollectionDesc.LIB_KAFFEB);
//
//      add(CollectionDesc.LIB_SUN0);
//      add(CollectionDesc.LIB_SUNA);
//      add(CollectionDesc.LIB_SUNB);
//      add(CollectionDesc.LIB_SUNC);
//      add(CollectionDesc.LIB_SUND);
//      add(CollectionDesc.LIB_IBM);
//      add(CollectionDesc.LIB_HP);
//
//      add(CollectionDesc.LIB_SCALA);
////      add(CollectionDesc.LIB_JAM);
//      add(CollectionDesc.LIB_MJC);
//      add(CollectionDesc.LIB_ANT);
//      add(CollectionDesc.LIB_JEDIT);
//      
//      add(CollectionDesc.LIB_TOMCAT);
//      add(CollectionDesc.LIB_POSEIDON);
//      add(CollectionDesc.LIB_JBOSS);
//      add(CollectionDesc.LIB_SHARED);
//
////      add("\\JCVS");
////      add("\\HOTJAVA");      
////      add("\\BCEL");      
//   }
   
   private static void add(CollectionDesc cd)
   {
      int n = lib2sn_.size();
      lib2sn_.put(cd.get_latex_name(), n);
   }
   
   public int compare(Object o1, Object o2)
   {
      return compare_impl(o1, o2);
   }
   
   public static int compare_impl(Object lib1, Object lib2)
   {
      Object o1 = lib1;
      Object o2 = lib2;

      int n1 = lib2sn_.get(o1, -1);
      int n2 = lib2sn_.get(o2, -1);
      
      JimaMisc.ensure(n1 >= 0 && n2 >= 0, "n1=" + n1 + ", n2=" + n2 
         + ", lib1=" + lib1 + ", lib2=" + lib2);
      
      return n1 - n2;
   }            
}
