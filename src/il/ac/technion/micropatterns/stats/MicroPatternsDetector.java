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

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.lib.ClassPathSpecifier;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;
import il.ac.technion.micropatterns.jungle.Jungle;
import il.ac.technion.micropatterns.tables.Table;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class MicroPatternsDetector
{
   private static boolean fullOutput = false;
   private static boolean quiet = false;
   
   private static void println(String s)
   {
      if(quiet)
         return;
      
      System.out.println(s);
   }
   
   private static String work_message(String verb, String elements, int n, 
      long t0, long t1)
   {
      double secs = Math.round((t1 - t0) / 1000.0 * 100) / 100.0;
      long rate = Math.round(n / secs); 
      
      String result = verb + " " + n + " " + elements 
         + " in " + secs + " seconds [" + rate  + " classes/sec]";
      
      return result;
   }
   public static void find(String libraryPath, String jarFile) throws Throwable
   {
      ClassPathSpecifier libs = new ClassPathSpecifier(libraryPath);      
      ClassPathSpecifier prg = new ClassPathSpecifier(jarFile);
            
      long t0 = System.currentTimeMillis();
      
      Jungle j = new Jungle(prg, libs, null);
      
      long t1 = System.currentTimeMillis();
      
      j.setWriteToFiles(false);
      j.setNameChecking(false);
      j.fullPatternsNames = true;
      
      println("");
      println("Library path: " + libraryPath);
      println("Program path: " + jarFile);
      println("");
      
          
      println(work_message("Found", "classes (program + library)", 
         j.ensemble_size(), t0, t1));
                
      long t2 = System.currentTimeMillis();      
      Table table = j.go(false);            
      long t3 = System.currentTimeMillis();
      
      println(work_message("Checked", "program classes", table.numRows() - 1, 
         t2, t3));
      println("");

      // Remove the "interface" pattern which is not an official micro-pattern 
      // (This psuedo-pattern is true if the class is an interface)
      int interfaceCol = table.indexOfColEqStr(0, DescCP.INTR.get_full_name());
      table = table.mutator().removeCol(interfaceCol).result();
      
      int np = DescCP.size();
      int[] count = new int[np];
      int coverage = 0;
      
      
      int maxlen = 0;
      int[] pid2Col = new int[np];
      for(int pid = 0; pid < np; ++pid)
      {
         DescCP curr = DescCP.get(pid);
         
         int index = -1;
         if(curr != DescCP.INTR)
         {
            String name = curr.get_full_name();
            maxlen = Math.max(maxlen, name.length());
            
            index = table.indexOfColEqStr(0, curr.get_full_name());   
            JimaMisc.ensure(index >= 0);
         }   
                  
         pid2Col[pid] = index;         
      }
      
            
      int nc = table.numCols();
      int nr = table.numRows();
      
      for(int r = 1; r < nr; ++r)
      {
         boolean covered = false;
         for(int pid = 0; pid < np; ++pid)
         {
            int c = pid2Col[pid];
            if(c < 0)
               continue;
            
            String s = table.at(c,r);
            if(s.equals("1"))
            {
               count[pid] += 1;
               covered = true;
            }
         }
         
         if(covered)
            coverage += 1;
      }
      
      if(fullOutput)
      {
         for(int r = 0; r < nr; ++r)
         {
            for(int c = 0; c < nc; ++c)
            {
               if(c >= 1)
                  System.out.print(',');
               System.out.print(table.at(c,r));
            }
            
            System.out.println();
         }
      }
      
      for(int pid = 0; pid < np; ++pid)
      {
         if(pid2Col[pid] < 0)
            continue;
         
         long percent = Math.round(count[pid] * 100.0 / (nr - 1));
         System.out.println(align(maxlen, DescCP.get(pid).get_full_name()) 
            + " " + align(3,percent) + "%");
      }

      System.out.println(align(maxlen, "") + "  " + align(3, "---")); 
      
      long percent = Math.round(coverage * 100.0 / (nr - 1));
      System.out.println(align(maxlen, "Coverage") + " " 
         + align(3,percent) + "%");
   }

   private static String align(int width, long n)
   {
      return align(width, Long.toString(n));
   }
   
   private static String align(int width, String s)
   {
      if(s.length() >= width)
         return s;
      
      StringBuffer sb = new StringBuffer();
      for(int i = 0; i < width - s.length(); ++i)
         sb.append(' ');
      
      sb.append(s);
      
      return sb.toString();
   }
   
   private static void usage()
   {
      System.err.println("mp - The Micro Patterns detection tool");
      System.err.println("");
      System.err.println("Usage: mp [options] [<library>] <program>");
      System.err.println("   library   A classpath string for the libraries " 
         + "used by the program");
      System.err.println("             If omitted, will use the JRE specifed"
         + " in the standard");
      System.err.println("             system properties.");
      System.err.println("             It should contain (at least) the path"
         + " to the JRE");
      System.err.println("   program   A classpath string specifying the Java" 
         + " program to be analyzed.");
      System.err.println("   -full     Show complete results in a table.");
      System.err.println("   -about    Copyright message.");
      System.err.println("   -q        Quiet mode.");
      System.err.println("");
      System.err.println("Example: mp d:/jre14/lib/rt.jar;d:/libs/junit.jar " +
            "d:/prg1/bin" + ClassPathSpecifier.SEP + "d:/prg2/prg2.jar");
      
      System.exit(-1);
   }
   
   
   private static void about()
   {      
      System.err.println(Messages.aboutText());
      System.exit(-1);      
   }
   
   private static String findJre()
   {
      Properties ps = System.getProperties();
      
      String cp = null;
      for(Iterator i = ps.keySet().iterator(); i.hasNext(); )
      {
         String key = (String) i.next();
         if(key.endsWith("boot.class.path"))
            cp = ps.getProperty(key);
      }
      
      if(cp == null)
         return null;
      
      
      ClassPathSpecifier cps = new ClassPathSpecifier(cp);
      ClassPathSpecifier result = new ClassPathSpecifier();
      
      for(Iterator i = cps.files(); i.hasNext(); )
      {
         File f = (File) i.next();
         if(f.exists())
            result.add(f.getAbsolutePath());
      }
      
      return result.toString(); 
   }
   
   /**
    * @param args
    */
   public static void main(String[] args)
   {
      int argc = args.length;
      if(argc == 0)
         usage();
            
      List argv = new LinkedList(Arrays.asList(args));
      for(Iterator i = argv.iterator(); i.hasNext(); )
      {
         String a = i.next().toString();

         if(a.equals("-about"))
         {
            i.remove();
            about();
         }
                  
         if(a.equals("-?") || a.equals("-help"))
         {
            i.remove();
            usage();
         }
         
         if(a.equals("-q"))
         {
            i.remove();
            quiet = true;
         }
         
         if(a.equals("-full"))
         {
            i.remove();
            fullOutput = true;
         }
      }
      
      if(argv.size() == 0 || argv.size() > 2)
         usage();
      
      if(argv.size() == 1)
      {
         String jre = findJre();
         if(jre == null)
         {
            System.err.println("I did not find a JRE in the system properties.");
            System.err.println("You must specify it in the command line"
               + " as the <library> paramter.");
            System.exit(-1);
         }
         
         argv.add(0, jre);
      }
      
      try
      {
         find(argv.get(0).toString(), argv.get(1).toString());
      }
      catch(Throwable e)
      {
         System.out.println("Error: " + e.getMessage());
         e.printStackTrace(JimaMisc.log());
         System.exit(-1);
      }
   }
}
