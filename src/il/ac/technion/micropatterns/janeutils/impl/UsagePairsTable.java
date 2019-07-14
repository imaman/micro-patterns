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
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

public class UsagePairsTable
{
   private static class BadFormat extends Exception 
   { 
      public BadFormat(String s) 
      {
         super(s);
      }
   }
   

   private Vector pairs_ = new Vector();
   private Report report_;
   
   public static class Pair
   {
      public String t1_;
      public String t2_;
      
      public Pair(String t1, String t2)
      {
         t1_ = t1.trim();
         t2_ = t2.trim();
      }
   }

   public String get_lib()
   {
      return report_.get_lib();
   }
   
   public static Vector build(File f) throws Exception 
   {
      Vector result = new Vector();
      BufferedReader fr = new BufferedReader(new FileReader(f));

      while(true)
      {
         Report r = new Report(fr, "* Report: usage-pairs");
         
         if(r.size() <= 0)
            return result;
      
         UsagePairsTable upt = new UsagePairsTable(r);
         result.add(upt);
      }
   }
   
   private UsagePairsTable(Report r) throws BadFormat 
   {
      report_ = r;
      System.out.println("Reading");
      
      Iterator lines = r.lines();
      while(lines.hasNext())
      {
         String line = (String) lines.next();
         
         line = line.trim();
         if(line.length() <= 0)
            continue;
         
      
         StringTokenizer st = new StringTokenizer(line, ",");
         
         if(!st.hasMoreTokens())
            throw new BadFormat("Not enoguh tokens in " + line);        
         String t1 = st.nextToken();

         if(!st.hasMoreTokens())
            throw new BadFormat("Not enoguh tokens in " + line);         
         String t2 = st.nextToken();
         
         Pair p = new Pair(t1, t2);
         pairs_.add(p);
      }      
   }
   
   public int get_total()
   {
      return pairs_.size();
   }
   
   public Iterator pairs()
   {
      return pairs_.iterator();
   }

   public String toString()
   {
      String result = report_.get_lib();
      if(result == null)
         return "UsagePairsTable()";
      
      // ...Else:
      result = "UsagePairsTable(" + result.trim() + "/" 
         + report_.get_prop("Desc") + ")";
      return result;
   }
   
   public static void main(String[] args)
   {
      
   }
}
