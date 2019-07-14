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








package il.ac.technion.micropatterns.jungle.model;

import il.ac.technion.micropatterns.janeutils.Defs;
import il.ac.technion.micropatterns.janeutils.impl.CollectionSorter;

import java.util.HashMap;
import java.util.Iterator;

public class CollectionDesc
{
   public boolean is_jre_ = false;
   private String jar_file_name_;
   private String initials_;
   private String base_name_;
   
   private static final HashMap lib2lib_ = new HashMap();
   
   private static CollectionDesc makejre(String a, String b)
   {
      CollectionDesc r = make(a,b);
      r.is_jre_ = true;
      
      return r;
   }

   
   private static CollectionDesc make(String a, String b)
   {
      CollectionDesc result = new CollectionDesc(a, b);      
      
      return result;
   }
   
   
   public static int size()
   {
      return lib2lib_.size();
   }
   
   public static Iterator iterator()
   {
      return lib2lib_.values().iterator();
   }
   
   public static CollectionDesc get(String lib_name)
   {
      lib_name = lib_name.toLowerCase();
      CollectionDesc result = (CollectionDesc) lib2lib_.get(lib_name);
      if(result != null)
         return result;
      
      result = (CollectionDesc) lib2lib_.get(lib_name + ".jar");
      if(result != null)
         return result;
      
      System.err.println(lib2lib_.toString());
      System.out.println("Lib not found: " + lib_name);
      throw new RuntimeException("lib not found " + lib_name);
   }
   
   
   public static final CollectionDesc LIB_KAFFEA = makejre("rt-kaffe-1.1", "KAFFEA");   
   public static final CollectionDesc LIB_KAFFEB = makejre("rt-kaffe-1.1.4", "KAFFEB");
   public static final CollectionDesc LIB_SUN0 = makejre("rt-sun-1.1", "SUNa");
   public static final CollectionDesc LIB_SUNA = makejre("rt-sun-1.2", "SUNA");
   public static final CollectionDesc LIB_SUNB = makejre("rt-sun-1.3", "SUNB");
   public static final CollectionDesc LIB_SUNC = makejre("rt-sun-1.4.1", "SUNC");
   public static final CollectionDesc LIB_SUND = makejre("rt-sun-1.4.2", "SUND");
   public static final CollectionDesc LIB_IBM = makejre("rt-ibm-1.4.2", "IBM");
   public static final CollectionDesc LIB_HP = makejre("rt-hp-1-4-2", "HP");

   public static final CollectionDesc LIB_SCALA = make("scala-1-3-0-4", "SCALA");
   public static final CollectionDesc LIB_MJC = make("mjc-1_3", "MJC");
   public static final CollectionDesc LIB_ANT = make("ant", "ANT");
   public static final CollectionDesc LIB_JEDIT = make("jedit-4.2", "JEDIT");

   public static final CollectionDesc LIB_JUNIT = make("junit-3.8.1", "JUNIT");
   public static final CollectionDesc LIB_JGRAPH = make("jgraph-5.2", "GRAPH");
   public static final CollectionDesc LIB_GANTPROJECT = make("ganttproject-1.10.3", "GANTPROJECT");
   public static final CollectionDesc LIB_SQUIRREL = make("squirrel-sql-1.1", "SQUIRREL");
   public static final CollectionDesc LIB_MEGAMEK = make("MegaMek", "MEGAMEK");
   public static final CollectionDesc LIB_FREEMIND = make("freemind", "FREEMIND");
   public static final CollectionDesc LIB_AZUEREUS = make("Azureus2.2.0.0", "AZUREUS");
   public static final CollectionDesc LIB_BCEL = make("BCEL", "BCEL");
      
   
   public static final CollectionDesc LIB_TOMCAT = make("tomcat-5-0-28", "TOMCAT");   
   public static final CollectionDesc LIB_TOMCAT3 = make("tomcat-3-3-2", "TOMCATC");
   public static final CollectionDesc LIB_TOMCAT4 = make("tomcat-4-1-31", "TOMCATD");

   public static final CollectionDesc LIB_POSEIDON = make("poseidon", "POSEIDON");   
   public static final CollectionDesc LIB_JBOSS = make("jboss3-2-6", "JBOSS");

   public static final CollectionDesc LIB_ANT152 = make("ant-1-5-2", "ANT-152");
   public static final CollectionDesc LIB_ANT153 = make("ant-1-5-3", "ANT-153");
   public static final CollectionDesc LIB_ANT154 = make("ant-1-5-4", "ANT-154");
   public static final CollectionDesc LIB_ANT160 = make("ant-1-6-0", "ANT-160");
   public static final CollectionDesc LIB_ANT161 = make("ant-1-6-1", "ANT-161");

   public static final CollectionDesc LIB_SHARED = make(Defs.SHARED_COLLECTION, 
      "SHARED");

   private CollectionDesc(String base_name, String initials)
   {
      base_name = base_name.toLowerCase();
      
      base_name_ = base_name;
      jar_file_name_ = base_name_ + ".jar";
      initials_  = initials;      
      
      CollectionSorter.register(this);
      
      lib2lib_.put(base_name, this);      
   }
   
   public String get_base_name()
   {
      return base_name_;      
   }
   
   public String get_jar_file_name()
   {
      return jar_file_name_;
   }
   
   public String get_initials()
   {
      return initials_;      
   }
   
   public String get_latex_name()
   {
      return "\\" + initials_;
   }
   
   public String toString()
   {
      return get_initials();
   }
}
