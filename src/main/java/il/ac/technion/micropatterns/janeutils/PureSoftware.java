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









package il.ac.technion.micropatterns.janeutils;

import il.ac.technion.jima.CommandLine;
import il.ac.technion.jima.JimaMisc;
import il.ac.technion.jima.CommandLine.Entry;
import il.ac.technion.micropatterns.janeutils.impl.AssocMat;
import il.ac.technion.micropatterns.janeutils.impl.CodingPattern;
import il.ac.technion.micropatterns.janeutils.impl.CollectionInfo;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;
import il.ac.technion.micropatterns.janeutils.impl.Record;
import il.ac.technion.micropatterns.janeutils.impl.Reduction;
import il.ac.technion.micropatterns.janeutils.impl.SetOfReductions;
import il.ac.technion.micropatterns.janeutils.impl.Sets;
import il.ac.technion.micropatterns.janeutils.impl.TableBuilder;
import il.ac.technion.micropatterns.jungle.model.Cluster;
import il.ac.technion.micropatterns.jungle.model.Clusterization;
import il.ac.technion.micropatterns.jungle.model.CollectionDesc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class PureSoftware extends AbstractReportMaker
{
   private HashSet common_ = new HashSet();
   private HashMap class2cps_ = new HashMap();
//   private HashSet all_ = new HashSet();
   
   private AssocMat sund_ = null;
   private Clusterization pruned_dataset_ = new Clusterization();
   
      
   private void add_to_all(AssocMat a)
   {
//    JimaMisc.log().print("Adding: " + a.get_lib() + "(sz=" + a.size() + "): "
//       + all_.size() + " => ");
//      all_ = Sets.union(all_, a.as_set_of_classes());
//      JimaMisc.log().println(all_.size());
//      JimaMisc.log().println();
//      JimaMisc.log().println();
//
   }
   
   private static final String IN_FILE 
      = System.getProperty("user.dir");
   
   private static final String BASE = "freq";
      
   
   private String out_dir_;
   private Vector ams_;
   private SetOfReductions reductions_ = new SetOfReductions();
   
   public PureSoftware(String[] args) throws Exception
   {
      super(BASE, args);
      
      try
      {
         init(args);
      }
      catch(Exception e)
      {
         e.printStackTrace(log());
         throw e;
      }
   }
   
   private void init(String[] args) throws Exception
   {
      
      CommandLine cl = new CommandLine(args, "-h", "-o");
      if(cl.has("-h"))
         usage(); // Terminates the program 
            
      Entry e = cl.get_optional(null, IN_FILE);
      if(e.param_count() != 1)
         usage();

      String fn = e.param_at(0);
      
      out_dir_ = new File(fn).getAbsoluteFile().getParent();
      out_dir_ = cl.get_optional("-o", out_dir_).param_at(0);
      log().println("out_dir=" + out_dir_);

      set_dir(out_dir_);
      
      log().println("Reading c/p associations from " + fn);
      ams_ = AssocMat.build_from_dir(new File(fn));
      
      find_common();      
      build_pruned();
   }
   
   
   private void find_common()
   {  
      int count = 0;
      int blanks = 0;

      AssocMat sund = null;
      AssocMat suna = null;
      
      HashSet rejected = new HashSet();
      
      
      int top = ams_.size();
      for(int i = 0; i < top; ++i)
      {         
         AssocMat a = (AssocMat) ams_.get(i);
         boolean jrea = a.get_desc().is_jre_;

         if(CollectionDesc.get(a.get_lib()) == CollectionDesc.LIB_SUND)
            sund = a;
         
         if(CollectionDesc.get(a.get_lib()) == CollectionDesc.LIB_SUN0)
            suna = a;
                  
         for(int j = 0; j < top; ++j)
         {
            AssocMat b = (AssocMat) ams_.get(j);
            

            if(a.get_lib().equals(b.get_lib()))               
               continue;

            System.out.print("a,b=" + a.get_desc() + "," + b.get_desc() + "  ");
            
            boolean jreb = b.get_desc().is_jre_;
            if(jreb)
            {
               System.out.println("Skipping B is JRE");
               continue;
            }
            
                                    
            if(jrea && jreb)
            {               
               System.out.println("      <<<<< TWO JRES. SKIPPING");
               continue;
            }
            System.out.println();
            
            add_to_all(a);
            
            HashSet shared_ab = Sets.intersect(a.as_set_of_classes(), 
               b.as_set_of_classes());

            JimaMisc.log().println("intersection " + a.get_desc() + ", " 
               + b.get_desc() + " = " + shared_ab.size());
            
            common_ = Sets.union(common_, shared_ab);
                        
            for(Iterator k = shared_ab.iterator(); k.hasNext(); )
            {
               String curr = ((Record) k.next()).type_name();
               if(rejected.contains(curr))
                  continue;

               Record ra = a.record_of(curr);
               Record rb = b.record_of(curr);

               Record temp = (Record) class2cps_.get(curr);
               
               if(ra == null || rb == null)
               {
                  throw new RuntimeException("Illegal condition." 
                     + " ra=" + ra + " rb = " + rb + " curr=" + curr);
               }
               
               boolean ok = are_same(ra, rb) &&  are_same(rb, temp) 
                  && are_same(temp, ra);
               
               if(!ok)
               {
                  if(temp != null && !temp.is_none())
                     count -= 1;
                  
                  if(temp != null && temp.is_none())
                     blanks -= 1;
                  
                  rejected.add(curr);
                  class2cps_.remove(curr);
               }
               else
               {
                  // new record matches
                  class2cps_.put(curr, ra);
                  if(temp == null && !ra.is_none())
                     count += 1;
                  
                  if(temp == null && ra.is_none())
                     blanks += 1;
               }
            }
         }         
      }
      
      if(sund == null)
         sund = suna;
      
      JimaMisc.ensure(sund != null);
      
      sund_ = sund;
      
      
      add_to_all(sund);
  
      log().println();
      log().println();
      log().println("List of non none");
      Vector sorted = new Vector();
      for(Iterator i = class2cps_.values().iterator(); i.hasNext(); )
      {
         Record r = (Record) i.next();
         if(!r.is_none())
            sorted.add(r.type_name());
      }

      
      Reduction core = new Reduction(Defs.SHARED_COLLECTION, 
         class2cps_.values().iterator(), common_);
      
      reductions_.add(core);
      
      log().println("classified = " + sorted.size() + "," 
            + core.sorted_.size());
      log().println("core.get_coverage() = " + core.get_coverage());
      
      log().println("Rejected = " + rejected.size());
      log().println("count = " + count + ", blanks=" + blanks);
      log().println("Size of common_=" + common_.size());
      log().println("Size of class2cps_=" + class2cps_.size());
   }
   
   
   private AssocMat get(int i)
   {
      return (AssocMat) ams_.get(i);
   }
   
   private static boolean are_same(Record a, Record b)
   {
      if(a == null || b == null)
         return true;
      
      return a.is_same(b);
   }

   private static double  percent(int part, int whole)
   {
      float f = part * 10000.0f / whole;
      long l = Math.round(f);
      
      double d = l / 100.0;
      return d;
   }
   
   private void make_pairwise_comp(Clusterization c) 
   {
//      start_new_report(Defs.PAIRWISE_COMP);
//      
//      for(Iterator i = DescCP.all(); i.hasNext(); )
//      {
//         DescCP dcp = (DescCP) i.next();
//         
//         for(Iterator j = c.clusters(); j.hasNext(); )
//         {
//            Cluster cj = (Cluster) j.next();
//            
//            for(Iterator k = c.clusters(); k.hasNext(); )
//            {
//               Cluster ck = (Cluster) k.next();
//               if(ck == cj)
//                  break;
//               
//               println(dcp.get_initials() + "," + 
//            }
//         }
//      }
      
   }
   
   
//      int universe = c.size();
//      
//      start_new_report(Defs.PAIRWISE_COMP);
//      
//      println("Grand Grand Total:," + universe);
//      println("Grand Total:," + c.size());
//      
//      for(Iterator i = c.clusters(); i.hasNext(); )
//      {
//         Cluster ci = (Cluster) i.next();
////         tb.add_cell(Defs.mp_to_latex(ci.get_desc()));
//         
//         for(Iterator j = c.clusters(); j.hasNext(); )
//         {
//            Cluster cj = (Cluster) j.next();
//                        
//            HashSet lhs = ci.as_set_of_names();
//            HashSet rhs = cj.as_set_of_names();
//            
//            float n = universe;
//            
//            int nx = lhs.size();
//            int ny = rhs.size();
//            
//            HashSet intersection = Sets.intersect(lhs, rhs);
//            int nxy = intersection.size();
//            
//            println(ci.get_desc().get_initials() + "," 
//               + cj.get_desc().get_initials() + "," 
//               + nx + "," 
//               + ny + "," 
//               + nxy);               
//         }
//      }      
   
   private void build_pruned()
   {
      //
      // Build the pruned collections (reductions)
      //
      
      Collections.sort(ams_); //, new CollectionSorter());
      for(Iterator i = ams_.iterator(); i.hasNext(); )
      {         
         AssocMat a = (AssocMat) i.next();
         
//         if(a.get_desc()
         
         HashSet left = Sets.difference(a.as_set_of_classes(), common_);
         log().println("left(" + a.get_lib() + " = " + left.size() 
            + "      [ " + a.size() + " ]");
         

         Reduction r = null;
         if(a != this.sund_ && a.get_desc().is_jre_)
            r = new Reduction(a);
         else
            r = new Reduction(a, common_);
         
         reductions_.add(r);
      }
      
      reductions_.sort();

      //
      // Build the clusterization object of the pruned data set
      //
      
      Clusterization c = new Clusterization();
      
      for(Iterator k = reductions_.iterator(); k.hasNext(); )
      {     
         Reduction curr = (Reduction) k.next();
         System.out.println(".....curr=" + curr + ", size=" + curr.size());
         
         if(curr.get_coll_desc().is_jre_)
         {
            if(curr.get_coll_desc() != CollectionDesc.LIB_SUND)
               continue;            
         }
                  
         int overlappings = c.extract_from(curr);
         System.out.println("Adding " + curr + " to c. new size=" + c.size());
         JimaMisc.ensure(overlappings == 0, "curr=" + curr 
            + ", overlappings=" + overlappings);
      }
      
      pruned_dataset_ = c;
   }
   
   private void make_all_correl_report() 
   {            
//      make_pairwise_comp(pruned_dataset_);      
//      
//      this.start_new_report(Defs.ALL_CORREL);      
//      dump_correlations(pruned_dataset_);
//      
//      System.out.println("size of pruned dataset = " 
//         + pruned_dataset_.size());
//      
////      Vector v = new Vector();
////      jima.util.Collections.addAll(v, reductions_.iterator());
////      
////      for(int k = 0; k < v.size(); ++k)
////      {
////         Reduction rk = (Reduction) v.get(k);
////         for(int l = 0; l < k; ++l)
////         {
////            Reduction rl = (Reduction) v.get(l);
////            if(rl == rk)
////               break;
////
////            Clusterization cl = new Clusterization();
////            cl.extract_from(rk);
////            cl.extract_from(rl);
////            
////            int combined_size = Reduction.size_of_union(rk, rl);
////            
////            println("% " + rk.get_lib_init() + " and " + rl.get_lib_init());
////            dump_correlations(cl, combined_size);
////         }
////      }
   }
   
   private void dump_correlations(Clusterization c)
   {
      TableBuilder tb = new TableBuilder();
      
      tb.add_cell("\\MPs");
      for(Iterator i = c.clusters(); i.hasNext(); )
      {
         Cluster curr = (Cluster) i.next();
         tb.add_cell(Defs.mp_to_latex(curr.get_desc()));
      }
      tb.new_line();
      
      for(Iterator i = c.clusters(); i.hasNext(); )
      {
         Cluster ci = (Cluster) i.next();
         tb.add_cell(Defs.mp_to_latex(ci.get_desc()));
         
         for(Iterator j = c.clusters(); j.hasNext(); )
         {
            Cluster cj = (Cluster) j.next();
                        
            HashSet lhs = ci.as_set_of_names();
            HashSet rhs = cj.as_set_of_names();
            
            double d = Sets.correl(lhs, rhs, c.size());
            tb.add_cell(d);
         }
         
         tb.new_line();
      }
      
      tb.print(out_);

      println();
      println();
      println();
   }
   
   private void go() throws Exception
   {

//      int total_classes = 0;
//      for(Iterator i = reductions_.iterator(); i.hasNext(); )
//      {         
//         Reduction r = (Reduction) i.next();
//         total_classes += r.size();
//      }
//
      
      this.start_new_report(Defs.FREQ_REPORT);      

      TableBuilder tb = new TableBuilder();
      
      tb.add_cell("Collection");
      for(Iterator k = reductions_.iterator(); k.hasNext(); )
      {         
         Reduction curr = (Reduction) k.next();
         tb.add_cell(curr.get_lib_init());
      }
      tb.new_line();
      
      tb.add_cell("[SIZE]");
      for(Iterator k = reductions_.iterator(); k.hasNext(); )
      {         
         Reduction curr = (Reduction) k.next();
         tb.add_cell(curr.size());
      }
      tb.new_line();
      
      
            
      
      for(int j = 0; j < DescCP.size(); ++j)
      {  
         DescCP dcp = DescCP.get(j);         
         tb.add_cell(dcp.toString());

         int n = 0;
         for(Iterator i = reductions_.iterator(); i.hasNext(); )
         {         
            Reduction r = (Reduction) i.next();
            
            CodingPattern curr = r.get(j);
            n += curr.size();
            
            tb.add_cell(curr.size());
         }
         
         tb.new_line();
      }            

      tb.add_cell("All");
      for(Iterator i = reductions_.iterator(); i.hasNext(); )
      {         
         Reduction r = (Reduction) i.next();
         int covered = r.get_absolute_coverage();
         tb.add_cell(covered);
      }
      tb.new_line();

      
//      tb.print_transpose(out_);
      tb.print(out_);
      
      int rtop = reductions_.size();
      log().println("rtop=" + rtop);
      

      this.start_new_report(Defs.REDUCED_COLLECTIONS);
      for(Iterator i = reductions_.iterator(); i.hasNext(); )
      {
         Reduction curr = (Reduction) i.next();
         CollectionInfo ci = new CollectionInfo(curr.get_lib_init(),
            curr.iterator());
         
         println(ci.name_ + "," + ci.packages_ + "," + ci.classes_ 
            + "," + ci.methods_);
      }
      
      this.start_new_report(Defs.SIZE_OF_COLLECTIONS);
      for(Iterator i = ams_.iterator(); i.hasNext(); )
      {
         AssocMat curr = (AssocMat) i.next();
         CollectionInfo ci = new CollectionInfo(curr.get_lib_init(),
            curr.iterator());
         
         println(ci.name_ + "," + ci.packages_ + "," + ci.classes_ 
            + "," + ci.methods_);
      }

      this.start_new_report(Defs.PATTS_IN_A_TYPE);
      
      int top = 10;
      for(Iterator i = reductions_.iterator(); i.hasNext(); )
      {
         int max_per_type = 0;
         int[] buckets = new int[DescCP.size() + 40];      

         Reduction curr = (Reduction) i.next();
         for(Iterator j = curr.iterator(); j.hasNext(); )
         {
            Record r = (Record) j.next();
            int n = r.get_on().size();
            
            buckets[n] += 1;
            max_per_type  = Math.max(max_per_type, n);
         }
         
         JimaMisc.ensure(max_per_type < top);
            
         print(curr.get_lib_init() + "," + curr.size());
         for(int k = 0; k <= top; ++k)
            print("," + buckets[k]);
         println();
      }
      
      
      make_all_correl_report();
      
      this.out_.flush();
      
         

      
      System.err.println("???????????????????????");
      
      
   }
   
 
   
   private static void usage()
   {
      System.err.println("PureSoftware: Compute CP frequency on the "
         + " collection's unique classes");
      System.err.println();
      System.err.println("Usage:");
      System.err.println("   PureSoftware [-h] -o <out-dir> <in-dir>");
      System.err.println();
      System.err.println(" <in-dir>   Directory where input files (*" 
         + Defs.CLUS_FILE_SUFFIX + ") are located");
      System.err.println("   -h       Help");
      System.err.println("   -o       Send output to directory");
      System.err.println();
      System.err.println("   Default input file = " + IN_FILE);
      System.err.println();
      System.err.println(); 
      System.exit(0);
   }
   
   
   public static void main(String[] args) throws IOException
   {
      try
      {
         PureSoftware ps = new PureSoftware(args);
         ps.go();
         
         System.err.println("Output directory: " + ps.out_dir_);
         System.err.print("-The End- [Press Enter]");
         System.err.flush();
                  
         BufferedReader br 
            = new BufferedReader(new InputStreamReader(System.in));
         br.readLine();
      }
      catch(Exception e)
      {
         e.printStackTrace();
         BufferedReader br 
            = new BufferedReader(new InputStreamReader(System.in));
         
         System.out.println("[Press Enter]");
         br.readLine();
         
         System.exit(-1);
      }      
   }
}
