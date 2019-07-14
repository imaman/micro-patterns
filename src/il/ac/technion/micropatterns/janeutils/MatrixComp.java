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

import il.ac.technion.micropatterns.janeutils.impl.AssocMat;
import il.ac.technion.micropatterns.janeutils.impl.CodingPattern;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;
import il.ac.technion.micropatterns.janeutils.impl.ImplUtils;
import il.ac.technion.micropatterns.janeutils.impl.Sets;

import java.io.File;
import java.util.HashSet;
import java.util.Vector;




public class MatrixComp extends AbstractReportMaker
{
   private static final String BASE = Defs.CORREL_REPORT;
   
   public MatrixComp(String[] args)
   {
      super(BASE, args);
      
      set_dir(args[1]);
      
//      PrintStream out = new PrintStream();      
   }
   
   public void go() throws Exception
   {
      File out_dir = new File(argv_[0]);
      if(!out_dir.isDirectory())
         usage(); // Program terminates
            
      Vector vec = AssocMat.build_from_dir(out_dir);
      
      int top = vec.size();
      start_new_report(Defs.CORREL_REPORT);

      for(int i = 0; i < DescCP.size(); ++i)
         print("," + DescCP.get(i).get_initials());
      println();
         
      for(int i = 0; i < top; ++i)
      {
         for(int j = i + 1; j < top; ++j)
         {
            AssocMat am1 = (AssocMat) vec.elementAt(i);
            AssocMat am2 = (AssocMat) vec.elementAt(j);
            
            process(am1, am2);
         }
      }
      
      
//      start_new_report(Defs.COLLECTIONS_REPORT);
//      println("Collection, Classes");
//      for(int i = 0; i < top; ++i)
//      {
//         AssocMat am1 = (AssocMat) vec.get(i);
//         println(am1.get_lib_init() + ", " + am1.size());         
//      }
      
      out_.flush();
   }
      
   private void process(AssocMat am1, AssocMat am2) throws Exception
   {      
      boolean isjre = ImplUtils.is_jre(am1);
      if(!isjre)
         return;

      isjre = ImplUtils.is_jre(am2);
      if(!isjre)
         return;
      
      HashSet universe = Sets.intersect(am1.as_set_of_classes(), 
         am2.as_set_of_classes());

      log().println("Comparing: " + am1 + " <-> " + am2);
      log().println("Universe = " + universe.size() + " elements");
      log().println("Correlations:");
      
      
      print(am1.get_lib_init() + "-" + am2.get_lib_init());
      
      for(int i = 0; i < DescCP.size(); ++i)
      {
         CodingPattern c1 = am1.coding_pattern(i);
         CodingPattern c2 = am2.coding_pattern(i);
         if(c1.compareTo(c2) != 0)
            throw new Exception("Mismatch in c/p: " + c1 + ", " + c2);
         
         HashSet lhs = Sets.intersect(c1.as_set(), universe);
         HashSet rhs = Sets.intersect(c2.as_set(), universe);
         
         double d = Sets.correl(lhs, rhs, universe.size());
         print("," + Double.toString(d));
      }
      
      println();
   }
   
   
   public static void usage()
   {
      System.err.println("MatrixComp - Correlation reports utility");
      System.err.println("Usage: MatrixComp <in-dir> <out-file>");
      System.err.println("   <in-dir>   Input directory where (*." 
         + Defs.CLUS_FILE_SUFFIX + ") are located");
      
      System.exit(-1);
   }
   
   public static void main(String[] args) throws Exception
   {
      try
      {
         if(args.length != 2)
            usage(); // Program terminates
         
         MatrixComp mc = new MatrixComp(args);
         mc.go();
         
         
         System.err.print("-The End- [Press Enter]");
         System.err.flush();
         ImplUtils.read_line();
         
         System.exit(0);
      }
      catch(Throwable t)
      {
         t.printStackTrace();
         System.err.print("[Press Enter]");
         System.err.flush();
         ImplUtils.read_line();
         
         System.exit(-1);
      }      
   }   
}
