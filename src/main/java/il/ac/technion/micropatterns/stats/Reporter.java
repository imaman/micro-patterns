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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;
import java.util.jar.JarFile;

import il.ac.technion.micropatterns.tables.Bank;
import il.ac.technion.micropatterns.tables.Table;

public class Reporter
{
   private JarFile input;

   private static String outputDir = System.getProperty("user.dir");

   public static void runReporter(Vector names, String outDir, String prefix) throws Throwable
   {    
      if(outDir != null)
         outputDir = outDir;
      
      Table table = null;      
      for(Iterator i = names.iterator(); i.hasNext(); )
      {
         String curr = (String) i.next();
         Table rhs = Bank.lookup(curr);
         table = Table.add(table, rhs);         
      }
               
      Core c = new Core(table, null);

      Table tabPrvl = c.calculatePrevalence(true).result();      
      tabPrvl.print(newOutput(prefix + ".prvl.csv"));
      
      Table tabCount = c.calculateCount(true).result();      
      tabCount.print(newOutput(prefix + ".count.csv"));
   }
   
   private static PrintStream newOutput(String name) 
      throws FileNotFoundException
   {
      File f = new File(outputDir, name);
      return new PrintStream(new FileOutputStream(f));
   }
   
   
   
   public static void usage()
   {
      System.out.println("Reporter - Implementation Pattern Stats tool");
      System.out.println();
      System.out.println("Usage: JarReporter <prefix> <name-1>, [<name-2>, ...]");
      System.out.println("    <prefix>   Prefix of output files");
      System.out.println("    <namen-n>  Name of a deposited collection to be analysed");
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
      
      String prefix = args[0];
      Vector v = new Vector();
      for(int i = 1; i < args.length; ++i)
         v.add(args[i]);
         
      try
      {
         runReporter(v, null, prefix);
      }
      catch (Throwable e)
      {
         e.printStackTrace();
      }
   }

}
