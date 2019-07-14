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

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.lib.ClassPathSpecifier;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import il.ac.technion.micropatterns.jungle.model.ClassFactory.ClassFactoryError;
import org.apache.bcel.classfile.JavaClass;


public class ListJar
{
   
   public ListJar()
   {
   }

   private static final String JAR_SUFFIX = ".jar";

      
   public static void main(String[] args) 
   {
      JimaMisc.log().println("Starting. args=" + args);
      try
      {
         go(args);         
      }
      catch(Throwable t)
      {
         t.printStackTrace();
      }
   }
   
   
//   private static final String JARS_DIR = "jars";
   
   public static void go(String[] args) 
      throws IOException, ClassFactoryError
   {
      Vector jars = new Vector();
      for(int i = 0; i < args.length; ++i)
      {
         String fn = args[i];
         if(!fn.endsWith(JAR_SUFFIX))
            fn = fn + JAR_SUFFIX;
         
         File f = new File(fn);
         fn = f.getAbsolutePath();
         
         jars.add(fn);
      }
      
//      String str = jars.size() == 1 ? "" : "s";      
//      System.err.println("Reading " + jars.size() + " jar file" + str);
//      for(Iterator i = jars.iterator(); i.hasNext(); )
//         System.err.println("   " +i.next());
//      System.err.println();
      
      
      ClassPathSpecifier cps = new ClassPathSpecifier();
      for(Iterator i = jars.iterator(); i.hasNext(); )
      {
         String curr = (String) i.next();
         cps.add(curr);
      }
      
      
      System.out.println("Package,Name,Super,Abstract?,Interface?,Final?,"
         + "Static?,FullName");
      
      JarEngine je = new JarEngine(cps);
      for(Iterator i = je.allTypes(); i.hasNext(); )
      {
         ClassPtr curr = (ClassPtr) i.next();
         JavaClassRep jcr = je.classForPtr(curr);
         JavaClass jc = jcr.jc();
         String fqn = jc.getClassName();
         String name = fqn.substring(fqn.lastIndexOf('.') + 1);
         
         System.out.println(jc.getPackageName()
            + "," + name 
            + "," + jc.getSuperclassName() 
            + "," + bool2str(jc.isAbstract())
            + "," + bool2str(jc.isInterface()) 
            + "," + bool2str(jc.isFinal())
            + "," + bool2str(jc.isStatic())
            + "," + fqn);
      }      
   }
   
   private static String bool2str(boolean b)
   {
      if(b)
         return "Yes!";
      else
         return "N";
   }
}
