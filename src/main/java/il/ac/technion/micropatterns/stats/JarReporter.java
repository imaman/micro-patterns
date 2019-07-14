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

import il.ac.technion.micropatterns.jane.lib.ClassPathSpecifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;
import java.util.jar.JarFile;

import il.ac.technion.micropatterns.jungle.Jungle;
import il.ac.technion.micropatterns.tables.Table;

public class JarReporter
{
   private JarFile input;

   public static void runReporter(String input, String jrePath, 
      String outputFile) throws Throwable
   {
      Vector vec = new Vector();
      
      if(isJar(input))
         vec.add(input);
      else      
      {
         File f = new File(input);
         if(!f.isDirectory())
         {
            throw new Exception("Input file must be either a jar file or"
               + " a directory");
         }
         
         
         vec = scan(new Vector(), f);         
      }
         
      System.out.println("input=" + vec);
      ClassPathSpecifier jre = new ClassPathSpecifier(jrePath);
      
      
      for(Iterator i = vec.iterator(); i.hasNext(); )
      {
         String curr = (String) i.next();
         System.out.println("Processing: " + curr);
         
         ClassPathSpecifier prg = new ClassPathSpecifier(curr);
         
         File f = File.createTempFile("ip-", ".csv");
         f.deleteOnExit();
         
         Jungle j = new Jungle(prg, jre, null);
         
         PrintStream ps = new PrintStream(new FileOutputStream(f));
         j.set_output_file(ps);
         j.setNameChecking(false);
         j.go();
         ps.close();
         
         Core c = new Core(f.getAbsolutePath(), null);
         
         Table tab = c.calculateArticleResults();
         
         ps = System.out;
         if(outputFile != null)
         {
            FileOutputStream fos = new FileOutputStream(outputFile, true);
            ps = new PrintStream(fos);
         }
         
         tab.print(ps);
         ps.flush();
      }
   }
   
   private static boolean isJar(String s)
   {
      return s.endsWith(".jar");
   }
   
   private static Vector scan(Vector result, File f)
   {
      File[] files = f.listFiles();
      for(int i = 0; i < files.length; ++i)
      {
         File curr = files[i];
         if(isJar(curr.getName()))
            result.add(curr.getAbsolutePath());
      }

      for(int i = 0; i < files.length; ++i)
      {
         File curr = files[i];
         if(curr.isDirectory())
            scan(result, curr);
      }
      
      return result;
   }
   
   
   public static void usage()
   {
      System.out.println("JarReporter - Implementation Pattern Stats tool");      
      System.out.println("Usage: JarReporter <input> <jre> [<output>]");
      System.out.println("   <input>  Full path to a jar file or directory");
      System.out.println("            If a directory, will (recursively) scan"
         + " all jar files");
      System.out.println("   <jre>    Full path to a JRE library (jar file)");
      System.out.println("   <output> Full path of output file. If omitted,"
         + " output is sent to stdout");
      System.out.println();
      System.exit(-1);
   }
   
   /**
    * @param args
    * @throws Throwable 
    */
   public static void main(String[] args) throws Throwable
   {
      
      if(args.length < 2 || args.length > 3)
         usage(); // Program terminates
      
      String arg2 = null;
      if(args.length == 3)
         arg2 = args[2];
      
      try
      {
         runReporter(args[0], args[1], arg2);
      }
      catch (Throwable e)
      {
         e.printStackTrace();
      }
   }

}
