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

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

public class ClassPathSpecifier implements Serializable
{
   public final static String SEP = System.getProperty("path.separator");
   
   private HashSet files_ = new HashSet();
   
   public ClassPathSpecifier() { }
   
   public ClassPathSpecifier(File jar_dir, String class_path_str) 
   {
      this(class_path_str);
      
      Vector files = new Vector();
      for(Iterator i = this.files(); i.hasNext(); )
      {
         File curr = (File) i.next();
         if(!curr.exists())
            curr = new File(jar_dir, curr.getName());
         
         files.add(curr);
      }
      
      files_ = new HashSet(files);
   }

   public ClassPathSpecifier(String class_path_str) 
   { 
      StringTokenizer st = new StringTokenizer(class_path_str, SEP);
      while(st.hasMoreTokens())
      {
         String curr = st.nextToken().trim();
         if(curr.length() <= 0)
            continue;
         
         File f = new File(curr);
         f = f.getAbsoluteFile();
         files_.add(f);
      }
   }
   
   public void add(String class_path_str)
   {
      ClassPathSpecifier temp = new ClassPathSpecifier(class_path_str);
      add(temp);
   }
   
   public void add(ClassPathSpecifier other)
   {
      for(Iterator i = other.files(); i.hasNext(); )
         files_.add(i.next());
   }


   public void subtract(String class_path_str)
   {
      ClassPathSpecifier temp = new ClassPathSpecifier(class_path_str);
      subtract(temp);
   }
   
   public void subtract(ClassPathSpecifier other)
   {
      for(Iterator i = other.files(); i.hasNext(); )
         files_.remove(i.next());      
   }
   
   
   public Iterator files()
   {
      return files_.iterator();
   }
   
   public String toString()
   {
      String result = "";
      
      int n = 0;
      for(Iterator i = files(); i.hasNext(); ++n)
      {
         File curr = (File) i.next();
         
         String sep = SEP;
         if(n == 0)
            sep = "";
            
         result = result + sep + curr.getAbsolutePath();
      }
      
      return result;
   }
}
