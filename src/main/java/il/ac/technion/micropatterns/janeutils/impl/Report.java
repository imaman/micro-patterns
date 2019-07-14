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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import il.ac.technion.micropatterns.jungle.model.CollectionDesc;


public class Report
{
   public HashMap props_ = new HashMap();
   private Vector lines_;
   private String indication_;
   
   public Report(BufferedReader r, String indication) throws IOException, BadFormat
   {
      System.out.println("INDICATION=" + indication);
      indication_ = indication;
      read(r);
   }
   
   public int size()
   {
      return lines_.size();
   }
   
   public Iterator lines()
   {
      return lines_.iterator();
   }
   
   public String get_prop(String key)
   {
      String result = (String) props_.get(key.toLowerCase());
      return result;
   }
         
   public String get_lib()
   {
      String lib = get_prop("Comment");
      return ImplUtils.fix_lib_name(lib);
   }      
   
   public String get_lib_init()
   {
      
      CollectionDesc cd = CollectionDesc.get(get_lib());
      return cd.get_latex_name(); //Defs.lib_to_latex(get_lib());
   }
   

   private boolean read(BufferedReader br) throws IOException, BadFormat
   {
      lines_ = new Vector();
      while(true)
      {
         String line = ImplUtils.next_line(br);
         if(line == null)
            return false;
         
         if(!line.startsWith(indication_))
            continue;

         read_impl(br);
         return true;
      }
   }
         
   private void read_impl(BufferedReader br) throws IOException, BadFormat
   {         
      System.out.println("Reading");
      while(true)
      {
         String line = ImplUtils.next_line(br);
         if(line == null)
            break;

         char c = line.charAt(0);
         if(c == ';')
            continue;

//         if(c == '*')
//            System.err.println(line);

         if(line.startsWith("* Report: "))
            break;

         if(line.startsWith("</report>"))
         {
            System.out.println(line);
            break;
         }

         if(line.startsWith("*===="))
            continue;

         if(c == '*')
         {
            StringTokenizer st = new StringTokenizer(line, ":");

            String k = null;
            String d = null;

            if(st.hasMoreTokens())
               k = st.nextToken();

            if(st.hasMoreTokens())
               d = st.nextToken();

            if(k == null || d == null)
               throw new BadFormat(line);

            if(k.startsWith("* "))
               k = k.substring(2);
            
            // ...Else:
            k = k.toLowerCase();
            d = d.trim();
            System.out.println("<" + k + ", " + d + ">");

            if(!props_.containsKey(k))
               props_.put(k, d);
            continue;
         }
         
         lines_.add(line);            
      }
   }      
}
