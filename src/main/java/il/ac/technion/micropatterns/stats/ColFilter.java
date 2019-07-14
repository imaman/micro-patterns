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








package il.ac.technion.micropatterns.stats;

import java.util.Iterator;
import java.util.Vector;

import il.ac.technion.micropatterns.tables.Table;

public class ColFilter
{
   public static void runReport(Vector names, String input) throws Throwable
   {    
      
      String re = "";
      for(Iterator i = names.iterator(); i.hasNext(); )
      {
         String curr = (String) i.next();
         if(re.length() > 0)
            re = re + "|";
            
         re = re + curr;
      }
      
      Table t = new Table(input);
      t = t.colsWhereEq(0, re);
      
      t.print(System.out);
   }
   
   public static void usage()
   {
      System.out.println("ColFilter - Table Columns Filtering");
      System.out.println();
      System.out.println("Usage: JarReporter <input> <col-1>, [<col-2>, ...]");
      System.out.println("    <input>   Name of input table");
      System.out.println("    <col-n>   Name of column (regexp)");
      System.out.println();
      System.exit(-1);
   }
   
   /**
    * @param args
    * @throws Throwable 
    */
   public static void main(String[] args) throws Throwable
   {
      if(args.length < 2)
         usage(); // Program terminates
      
      String in = args[0];
      Vector v = new Vector();
      for(int i = 1; i < args.length; ++i)
         v.add(args[i]);
         
      try
      {
         runReport(v, in);
      }
      catch (Throwable e)
      {
         e.printStackTrace();
      }
   }

}
