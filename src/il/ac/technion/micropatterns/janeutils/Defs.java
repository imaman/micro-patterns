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

package il.ac.technion.micropatterns.janeutils;

import il.ac.technion.micropatterns.janeutils.impl.CodingPattern;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

public class Defs
{
   
   public static final String CLUS_FILE_SUFFIX = ".clus.txt";
   
   public static final String PAIRWISE_COMP = "pairs";
   public static final String ALL_CORREL = "global-correl";
   public static final String PATTS_IN_A_TYPE = "multi";
   public static final String REDUCED_COLLECTIONS = "rsize";
   public static final String SIZE_OF_COLLECTIONS = "size";
   public static final String CORREL_REPORT = "correl";
   public static final String FREQ_REPORT = "freq";
   
//   public static final String COLLECTIONS_REPORT = "collections";
   
   
   
   public static final String SHARED_COLLECTION = "-Shared-";
   
//   public static final HashMap lib2lib_ = new HashMap();
//   
//   static
//   {
//      lib2lib_.put(SHARED_COLLECTION.toLowerCase(), "\\SHARED");
//      lib2lib_.put("rt-sun-1.3", "\\SUNB");
//      lib2lib_.put("rt-sun-1.3", "\\SUNB");
//      lib2lib_.put("rt-sun-1.4.1", "\\SUNC");
//      lib2lib_.put("rt-sun-1.4.2", "\\SUND");
//      lib2lib_.put("rt-sun-1.2", "\\SUNA");
//      lib2lib_.put("rt-sun-1.1", "\\SUNa");
//      lib2lib_.put("tomcat-5-0-28", "\\TOMCAT");
//      lib2lib_.put("poseidon", "\\POSEIDON");
//      lib2lib_.put("jboss3-2-6", "\\JBOSS");
//      lib2lib_.put("rt-sun-1.3", "\\SUNB");
//      lib2lib_.put("rt-ibm-1.4.2", "\\IBM");
//      lib2lib_.put("rt-kaffe-1.1.4", "\\KAFFEa");      
//      lib2lib_.put("rt-kaffe-1-2", "\\KAFFE");      
//      lib2lib_.put("kaffe", "\\KAFFE");      
//      lib2lib_.put("rt-hp-1-4-2", "\\HP");
//
//      lib2lib_.put("ant", "\\ANT");
//      lib2lib_.put("jedit-4.2", "\\JEDIT");
//      lib2lib_.put("jcvs-5-4-2", "\\JCVS");
//      lib2lib_.put("scala-1-3-0-4", "\\SCALA");
//      lib2lib_.put("jam", "\\JAM");
//      lib2lib_.put("mjc-1_3", "\\MJC");
//      lib2lib_.put("hotjava", "\\HOTJAVA");      
//      lib2lib_.put("bcel", "\\BCEL");      
//   }
   
//   public static String lib_to_latex(String lib_name)
//   {
//      lib_name = lib_name.toLowerCase();
//      
//      String result = (String) lib2lib_.get(lib_name);
//      JimaMisc.ensure(result != null, "lib_name=" + lib_name);
//      
//      return result;
//   }
   
//   public static final int CP_COUNT = DescCP.size();
   
   
   public static Vector empty_coding_patterns()
   {
      Vector result = new Vector();
  
      for(Iterator i = DescCP.all(); i.hasNext(); )
      {
         DescCP curr = (DescCP) i.next();
         result.add(new CodingPattern(curr));
      }
      
      Collections.sort(result);
      return result;
   }
   
   public static String mp_to_latex(DescCP d)
   {
      return "\\B" + d.get_initials();
   }
}
