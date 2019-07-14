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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class JarScan
{
   private Vector types = new Vector();
   private static PrintStream log;
   private JarFile jar_;
   private ClassPathSpecifier cps;
   private String jarName;

   
   static
   {
      log = System.err;
      try
      {
         log = new PrintStream(new FileOutputStream("c:/temp/js.log"));
      }
      catch(Throwable t)
      {
         // Do nothing
      }         
   }
   

   public JarScan(String path_to_jar) throws IOException
   {
      
      this.jarName = new File(path_to_jar).getName();
      cps = new ClassPathSpecifier(path_to_jar);
      try
      {
         jar_ = new JarFile(path_to_jar);
      }
      catch (IOException e)
      {
         System.out.println(path_to_jar);
         e.printStackTrace();
         throw e;
      }      
   }
   
   public void scan() 
   {
      int now = -1;
      
      Enumeration iter = jar_.entries();
      while(iter.hasMoreElements())
      {
         ++now;
         
         JarEntry curr = (JarEntry) iter.nextElement();
         
         scan(curr);         
      }         
      
      JimaMisc.log().println(types.size() + " classes found in " + jarName);
   }
   
   private static String translate_name(String class_name)
   {
      if(!class_name.endsWith(CLASS_SUFFIX))
         return null;
         
      StringBuffer result = new StringBuffer(class_name);
      
      for(int i = 0; i < class_name.length(); ++i)
      {
         char c = class_name.charAt(i);
         if(c == '/')
            c = '.';
            
         result.setCharAt(i, c);
      } 
      
      return result.substring(0, class_name.length() - CLASS_SUFFIX.length());
   }
   
   private static final String CLASS_SUFFIX = ".class";
   
   private void scan(JarEntry je) 
   {
      String name = translate_name(je.getName());
      if(name == null)
         return;
      
      types.add(new ClassPtr(name, jarName));
   }

   public static Vector typesOf(String pathToJar) 
      throws IOException
   {
      return typesOf(new ClassPathSpecifier(pathToJar));
   }
   
   public static Vector typesOf(ClassPathSpecifier cps) 
      throws IOException
   {
      Vector result = new Vector();
      
      for(Iterator i = cps.files(); i.hasNext(); )
      {
         File curr = (File) i.next();
         JarScan js = new JarScan(curr.getAbsolutePath());
         
         js.scan();
         result.addAll(js.types);
      }
      return result;
   }
   
   public static void main(String[] args) 
      throws IOException
   {
      for(int argi = 0; argi < args.length; ++argi)
      {
         Vector v = typesOf(args[argi]);
         for(Iterator i = v.iterator(); i.hasNext(); )
            System.out.println(i.next());
      }
   }
}
