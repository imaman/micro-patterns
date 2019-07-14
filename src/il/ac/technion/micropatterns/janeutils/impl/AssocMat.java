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
 * Created on Nov 10, 2004
 * Written by spiderman
 * Project: JarScan
 */

package il.ac.technion.micropatterns.janeutils.impl;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.janeutils.Defs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import il.ac.technion.micropatterns.jungle.model.CollectionDesc;


public class AssocMat implements Comparable
{      
   /**
    * @see java.lang.Comparable#compareTo(T)
    */
   public int compareTo(Object o)
   {
      AssocMat that = (AssocMat) o;
      return CollectionSorter.compare_impl(this.get_lib_init(), 
         that.get_lib_init());
   }
   
   private static HashSet banned_ = new HashSet();
   
   static
   {
//      System.out.println("Hi");
      try
      {
         BufferedReader br = null;
         
         try
         {
            FileReader fr = new FileReader("c:\\temp\\ban.txt");
            br = new BufferedReader(fr);
         }
         catch(FileNotFoundException fnfe)
         {
            // br is now null, so the following loop is voided
         }

         
         while(br != null)
         {
            String line = br.readLine();
            if(line == null)
               break;
            
            line = line.trim();
            if(line.length() <= 0)
               continue;
            
            if(line.charAt(0) == ';')
               continue;
            
            banned_.add(line.toLowerCase());             
         }
      }
      catch(Throwable t)
      {
         try
         {
            t.printStackTrace();
            System.in.read();
         }
         catch(IOException e)
         {
         }
         System.exit(-1);
      }
   }
   
   private Vector ordered_descriptors_ = new Vector();
   public Report report_;
   private CollectionDesc coll_desc_;
   private Vector coding_patterns_ = Defs.empty_coding_patterns();
   private HashMap all_ = new HashMap();
   
   public Vector names_ = new Vector();
   

   /**
    * @see java.lang.Iterable#iterator()
    */
   public Iterator iterator() 
   {
      return this.all_.values().iterator();
   }

   /**
    * @see java.util.Collection#size()
    */
   public int size()
   {
      return this.all_.size();
   }
   
   public HashSet as_set_of_classes()
   {
      HashSet result = new HashSet();
      result.addAll(all_.values());
      return result;
   }
   
   public String get_lib_init()
   {
      return report_.get_lib_init();
   }
   
   public String get_lib()
   {
      return report_.get_lib();
   }
   
   public Record record_of(String class_name)
   {
      Record result = (Record) all_.get(class_name);
//      JimaMisc.ensure(result != null, "class_name=" + class_name);
      
      return result;
   }

   public static Vector build_from_dir(File f) throws Exception 
   {
      File[] files = f.listFiles(new FilenameFilter()
         {
            public boolean accept(File file, String fn)
            {
               if(fn.endsWith(Defs.CLUS_FILE_SUFFIX))
                  return true;
               else
                  return false;
            }
         });
      
      Vector result = new Vector();
      
      for(int i = 0; i < files.length; ++i)
      {
         Vector temp = x_build(files[i]);
         result.addAll(temp);
      }
      
      return result;
   }
   
   public static Vector x_build(File f) throws Exception 
   {
      Vector result = new Vector();
      BufferedReader br = new BufferedReader(new FileReader(f));

      HashSet sofar = new HashSet();
      while(true)
      {
         
         try
         {
            Report r = new Report(br, "* Report: assocation-matrix");
            
            if(r.size() <= 0)
            {
               Collections.sort(result);
               return result;
            }
         
            AssocMat am = new AssocMat(r);
            String temp = am.get_lib().toLowerCase();
            
            if(banned_.contains(temp))
               continue;
            
            if(sofar.contains(temp))
               continue;
            
            sofar.add(temp);
            
            result.add(am);
         }
         catch(BadFormat e)
         {
            throw new Exception(e.getMessage() + " [in file: " + f + "]");
         }
      }
   }
   
   private int[] check_names() throws BadFormat
   {
      int[] result = new int[DescCP.size()];
      
      Iterator j = DescCP.all();
      Iterator i = names_.iterator();
      
      int in_index = -1;
      while(i.hasNext() && j.hasNext())
      {
         in_index += 1;
         
         String name = (String) i.next();
         if(name.startsWith("\\B"))
            name = "\\" + name.substring(2);
         
         DescCP which = null;         
         int out_index = 0;
         
         for(Iterator k = DescCP.all(); k.hasNext(); ++out_index)
         {
            DescCP curr = (DescCP) k.next();
            if(curr.match_name(name))
            {               
               which = curr;
               break;
            }
         }
                          
         if(which == null)
            throw new BadFormat("DescCP not found. name=" + name);
         
         // ...else:
         result[in_index] = out_index;
      }
      
      if(i.hasNext() || j.hasNext())
         throw new BadFormat("invalid c/p count (" + names_.size() + ")");
      
      return result;
   }
   
   private AssocMat(Report r) throws BadFormat 
   {
      coll_desc_ = CollectionDesc.get(r.get_lib());
      report_  = r;
      
      Iterator lines = r.lines();
      int[] order_translation = null;
      
      while(lines.hasNext())
      {
         String line = (String) lines.next();
         
         if(line.startsWith("C/P:,"))
         {
            StringTokenizer st = new StringTokenizer(line.substring(5), ",");
            while(st.hasMoreTokens())
            {
               String temp = st.nextToken();
               names_.add(temp.trim());
            }
                        
            order_translation = check_names();            
            continue;
         }
         
         // ...Else:
         Record new_one = new Record(line, order_translation);
         Object prev_val = all_.put(new_one.type_name(), new_one);
         JimaMisc.ensure(prev_val == null);
      }
      
      for(Iterator i = all_.values().iterator(); i.hasNext(); )
      {
         Record curr = (Record) i.next();
         curr.register(coding_patterns_);
      }


      String lib = get_lib();
      for(Iterator i = coding_patterns_.iterator(); i.hasNext(); )
      {
         CodingPattern curr = (CodingPattern) i.next();
         curr.set_lib(lib);
         
      }

      //
      // Shuffle the random-varibles array 
      // This would produce random comparisons
      // which should yield low correlation values
      //
//      Vector temp = new Vector();
//      for(int i = 0; i < vars_.length; ++i)
//         temp.add(vars_[i]);
//      Collections.shuffle(temp);
//      
//      for(int i = 0; i < vars_.length; ++i)
//      {
//         HashSet curr = (HashSet) temp.elementAt(i);
//         vars_[i] = curr;
//      }
   }

   
   public CodingPattern coding_pattern(int i)
   {
      CodingPattern result = (CodingPattern) coding_patterns_.elementAt(i);
      return result;
   }
   
   public String toString()
   {
      String result = get_lib();
      if(result == null)
         return "AssocMat()";
      
      // ...Else:
      return "AssocMat(" + result.trim() + ")";
   }
   
   public int hashCode()
   {
      return this.get_lib().hashCode();
   }
   
   public boolean equals(Object other)
   {
      if(other == null)
         return false;
      
      if(other.getClass() != this.getClass())
         return false;
      
      AssocMat rhs = (AssocMat) other;
      
      return this.get_lib().equals(rhs.get_lib());
   }
   
   public CollectionDesc get_desc()
   {
      return coll_desc_;
   }
}
