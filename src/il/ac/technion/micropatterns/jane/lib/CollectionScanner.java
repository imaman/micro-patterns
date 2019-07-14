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

import il.ac.technion.jima.JimaMisc;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class CollectionScanner
{
   
   public static class ScannerError extends Exception
   {
      public ScannerError(String s) { super(s); }
   }
   
   private static final String CLASS_SUFFIX = ".class";
   
   /**
    * Extract names of all classes from a jar file.
    * 
    * @param f Specifies a Jar file to scan
    * @return A Vector of fully-qualified-names (Strings) of all classes within 
    * the jar file 
    * @throws ScannerError
    */
   public static Vector type_names_of(File jar_file) throws ScannerError
   {
      Vector result = new Vector();
      
      CollectionScanner cs = new CollectionScanner();
      cs.build(jar_file, result);
      return result;
   }

   private void build(File f, Vector types) throws ScannerError
   {
      if(f.isDirectory())
         build_from_dir("", f, types);
      else 
      {
         try
         {
            JarFile jf = new JarFile(f);
            build_from_jar(jf, types);
         }
         catch(IOException e)
         {
            e.printStackTrace();
            throw new ScannerError("Problem with file " + f.getAbsolutePath());
         }
      }
   }

   private void build_from_dir(String path, File dir, Vector types)
   {
      File[] entries = dir.listFiles();
      
      for(int i = 0; i < entries.length; ++i)
      {
         File f = entries[i];
         
         String prefix = "";
         if(path.length() > 0)
            prefix = path + ".";

         if(f.isDirectory())         
         {
            String temp = prefix + f.getName();
            build_from_dir(temp, f, types);
         }
         else if(f.getName().endsWith(".class"))
         {
            String temp = prefix + JimaMisc.change_suffix(f.getName(), "");
            add_to_vec(types, temp);            
         }
      }
   }

   private void build_from_jar(JarFile jar, Vector types) 
   {
      Enumeration iter = jar.entries();
      while(iter.hasMoreElements())
      {         
         JarEntry curr = (JarEntry) iter.nextElement();
         
         String temp = translate_name(curr.getName());
         add_to_vec(types, temp);
      }               
   }
   
   private static void add_to_vec(Vector v, String s)
   {
      if(s != null)
         v.add(s);
   }
   
   private static String translate_name(String class_name)
   {
      if(!class_name.endsWith(CLASS_SUFFIX))
         return null;
         
      String result = class_name.replace('/', '.');
      return result.substring(0, result.length() - CLASS_SUFFIX.length());
   }
     
}
