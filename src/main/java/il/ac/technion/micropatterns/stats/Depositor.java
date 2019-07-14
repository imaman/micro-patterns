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
import il.ac.technion.micropatterns.jungle.Jungle;
import il.ac.technion.micropatterns.tables.Bank;
import il.ac.technion.micropatterns.tables.Table;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

public class Depositor
{
   private static boolean useDefaultJre = true;
   private static String jrePath = Env.fileName("");
   
   public static void deposit(String name, Vector inputs) throws Throwable
   {
      if(Bank.has(name))
      {
         System.err.println("Collection " + name + " is already in the bank. "
            + " Deposition was cancelled");
         return;
      }
      
      Vector vec = new Vector();
      
      for(Iterator i = inputs.iterator(); i.hasNext(); )
      {
         String input = (String) i.next();
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
      }
         
      System.out.println("input=" + vec);
      
      File[] allFiles = new File(jrePath).listFiles();
      File defJreFile = null;
      int found = 0;
      for(int i = 0; i < allFiles.length; ++i)
      {
         if(allFiles[i].getAbsolutePath().endsWith(DEFAULT_JRE_SUFFIX))
         {
            found += 1;
            defJreFile = allFiles[i];
         }
      }
      
      if(found > 1)
      {
         throw new Exception("Found more than one *." + DEFAULT_JRE_SUFFIX 
            + " file in directory " + jrePath);
      }
      
      if(found == 0)
      {
         throw new Exception("No *." + DEFAULT_JRE_SUFFIX + " file was found"
            + " in directory " + jrePath);
      }
      
//      File f = new File(jrePath, DEFAULT_JRE);
      ClassPathSpecifier jre = new ClassPathSpecifier();
      if(useDefaultJre)
         jre = new ClassPathSpecifier(defJreFile.getAbsolutePath());
      
      Table table = null;      
      for(Iterator i = vec.iterator(); i.hasNext(); )
      {
         String curr = (String) i.next();
         ClassPathSpecifier prg = new ClassPathSpecifier(curr);
                  
         Jungle j = new Jungle(prg, jre, null);
         j.setWriteToFiles(false);
         j.setNameChecking(false);
         Table rhs = j.go();
         table = Table.add(table, rhs);         
      }
      
      int col_lib = table.indexOfColEqStr(0, "Collection");
      if(useDefaultJre)
         table = table.rowsWhereNotEqStr(col_lib, defJreFile.getAbsolutePath());
         
      for(int r = 1; r < table.numRows(); ++r)
         table.putAt(col_lib, r, name);
      
      Bank.put(name, table);
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

   private static final String DEFAULT_JRE_SUFFIX = ".defjar";
   
   public static void usage()
   {
      System.out.println("Depositor - Implementation Pattern Jar depsoit tool");
      System.out.println("uses a *." + DEFAULT_JRE_SUFFIX + " file in the"
         + " current directory, as the JRE library");
      System.out.println();
      System.out.println("Usage: Dpositor [-no] <name> <input>");
      System.out.println("    -no      Do not use the default JRE library");
      System.out.println("    <name>   Name under which the input will be placed");
      System.out.println("    <input>  Full path to a jar file or directory");
      System.out.println("             If a directory, will (recursively) scan"
         + " all jar files");
      System.out.println();
      System.exit(-1);
   }
   
   
   /**
    * @param args
    * @throws Throwable 
    */
   public static void main(String[] args) throws Throwable
   {
      int from = 0;
      int len = args.length;
      
      if(len == 0)
         usage(); // Program terminates

      if(args[0].equals("-no"))
      {
         useDefaultJre = false;
         len -= 1;
         from = 1;
      }
         
      if(len < 2)
         usage(); // Program terminates
      
      Vector v = new Vector();
      for(int i = from + 1; i < args.length; ++i)
         v.add(args[i]);
         
      try
      {
         deposit(args[from], v);
      }
      catch (Throwable e)
      {
         e.printStackTrace();
      }
   }
}
