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

package il.ac.technion.micropatterns.janeutils;

import il.ac.technion.jima.CommandLine;
import il.ac.technion.jima.JimaMisc;
import il.ac.technion.jima.CommandLine.Entry;
import il.ac.technion.micropatterns.janeutils.impl.AssocMat;
import il.ac.technion.micropatterns.janeutils.impl.CodingPattern;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;
import il.ac.technion.micropatterns.janeutils.impl.Sets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;





public class MatrixDiff
{  
   private static final String IN_FILE 
      = "d:/local/data-research/diff.in.txt";

   private static PrintStream out_ = System.out;
   
   private static void usage()
   {
      System.err.println("MatrixDiff: Two way set comparison utility");
      System.err.println("            Part of the Coding Pattern project");
      System.err.println();
      System.err.println("Usage:");
      System.err.println("   MatrixDiff [-h] [-o <out-file>] <in-dir>");
      System.err.println();
      System.err.println("   <in-dir>   Directory of *" + Defs.CLUS_FILE_SUFFIX 
         + " files");
      System.err.println("   -h         Help");
      System.err.println("   -o         Send output to file");
      System.err.println();
      System.err.println("   Default input file = " + IN_FILE);
      System.err.println();
      System.err.println(); 
      System.exit(0);
   }
   
   public static void main(String[] args) throws Exception
   {
      CommandLine cl = new CommandLine(args, "-h", "-o");
      if(cl.has("-h"))
         usage(); // Terminates the program 
      
      String str_out = cl.get_optional("-o", null).param_at(0);
      System.err.println("str_out=" + str_out);

      if(str_out != null)
         out_ = new PrintStream(new FileOutputStream(str_out));
      
      
      JimaMisc.ensure(true);

      Entry e = cl.get_optional(null, IN_FILE);
      if(e.param_count() != 1)
         usage();

      String fn = e.param_at(0);
      
      out_.println("Reading in " + fn);
      Vector vec = AssocMat.build_from_dir(new File(fn));      
      int top = vec.size();
      out_.println("top=" + top);

      for(int i = 0; i < top; ++i)
      {
         for(int j = i + 1; j < top; ++j)
         {
            AssocMat am1 = (AssocMat) vec.elementAt(i);
            AssocMat am2 = (AssocMat) vec.elementAt(j);
            
            out_.println("i,j=" + i + "," + j);
            out_.println("am1=" + am1.get_lib());
            out_.println("am2=" + am2.get_lib());

            process(am1, am2);
         }
      }
      
      out_.println("-The End-");
   }
   
   public static void process(AssocMat am1, AssocMat am2) throws Exception
   {
      HashSet universe = Sets.intersect(am1.as_set_of_classes(), 
         am2.as_set_of_classes());

      out_.println("Comparing: " + am1 + " <-> " + am2);
      out_.println("Universe = " + universe.size() + " elements");
      out_.println("Two Way diffs");
      
      for(int i = 0; i < DescCP.size() ; ++i)
      {
         CodingPattern cp1 = am1.coding_pattern(i);
         CodingPattern cp2 = am2.coding_pattern(i);
         
         if(cp1.compareTo(cp2) != 0)
            throw new Exception("Coding pattern mismatch: " + cp1 + ", " + cp2);
         
         HashSet lhs = Sets.intersect(universe, cp1.as_set());
         HashSet rhs = Sets.intersect(universe, cp2.as_set());
         
//         HashSet d1 = Sets.difference(lhs, rhs);
//         HashSet d2 = Sets.difference(rhs, lhs);
         
         print(lhs, rhs, cp1.full() + " - " + cp2.full(), universe.size());
         print(rhs, lhs, cp2.full() + " - " + cp1.full(), universe.size());         
      }
   }
   
   private static double to_percent(double a, double b)
   {
      double result = Math.round(10000.0 * a / b);
      result = result / 100.0;
      
      return result;
   }
   
   public static void print(HashSet l, HashSet r, String title, int universe)
   {
      HashSet d = Sets.difference(l, r);
      double part = to_percent(d.size(), l.size());
      
      out_.println("/////");
      out_.println("/////");
      out_.println("/////");
      out_.println("///// Title: " + title);
      out_.println("///// Size: " + d.size());
      out_.println("///// Part: " + part + "%");      
      out_.println("///// Correl: " 
         + (Math.round(Sets.correl(l, r, universe) * 100.0) / 100.0));
      out_.println("/////");
      out_.println("/////");
      out_.println("///// Size left: " + l.size());
      out_.println("///// Size right:              " + r.size());
      out_.println("/////");
      
      for(Iterator i = d.iterator(); i.hasNext(); )
         out_.println(i.next());
   }
}
