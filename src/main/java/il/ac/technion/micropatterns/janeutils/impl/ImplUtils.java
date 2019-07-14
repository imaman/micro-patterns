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
import java.io.InputStreamReader;

public class ImplUtils
{
   private static final String JRE_PREIFX = "rt-";

   public static boolean is_jre(AssocMat am)
   {
      String temp = am.get_lib().toLowerCase();
      
      boolean result = temp.startsWith(JRE_PREIFX);
      if(!result)
         result = result || (temp.indexOf("kaffe") >= 0);
      
      return result;
   }
   
   
   private static BufferedReader stdin 
      = new BufferedReader(new InputStreamReader(System.in));
   
   public static String read_line() throws IOException   
   {
      return stdin.readLine();
   }
   
   

   
   public static String next_line(BufferedReader br) throws IOException
   {
      while(true)
      {
         String line = br.readLine();
         if(line == null)
            return null;

         line = line.trim();
         if(line.length() <= 0)
            continue;

         char c = line.charAt(0);
         if(c == ';')
            continue;
         
         if(c == '*')
            System.err.println(line);

         return line;
      }
   }
   
   public static double to_percent(double a, double b)
   {
      double result = Math.round(10000.0 * a / b);
      result = result / 100.0;
      
      return result;
   }

   public static double to01(double d)
   {
      d = d * 100;
      long l = Math.round(d);
      
      d = l / 100.0;
      return d;
   }
   
   
   private static final String PREFIX = "lib=";
   private static final String SUFFIX = ".jar";
   private static final String ALL = "-all";

   public static String fix_lib_name(String lib)
   {
      lib = lib.toLowerCase();
      if(lib.startsWith(PREFIX))
         lib = lib.substring(PREFIX.length());
      
      if(lib.endsWith(SUFFIX))
         lib = lib.substring(0, lib.length() - SUFFIX.length());
      
      if(lib.endsWith(ALL))
         lib = lib.substring(0, lib.length() - ALL.length());
      
      return lib;
      
   }
   
   
}
