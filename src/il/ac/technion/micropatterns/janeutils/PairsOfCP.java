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
import il.ac.technion.micropatterns.janeutils.impl.DescCP;
import il.ac.technion.micropatterns.janeutils.impl.ImplUtils;
import il.ac.technion.micropatterns.janeutils.impl.Record;
import il.ac.technion.micropatterns.janeutils.impl.UsagePairsTable;
import il.ac.technion.micropatterns.janeutils.impl.UsagePairsTable.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;


public class PairsOfCP
{
   private static final String IN_FILE
      = "pairs.in.txt";
//      = "d:/local/data-research/pairs.in.txt";

   private static PrintStream out_ = System.out;
   private static HashMap couple2couple_;
   private static int added_;
   private static double threshold_ = 0.5;
   
   private static void init()
   {
      couple2couple_ = new HashMap();
      added_ = 0;

      for(int i = -1; i < DescCP.size(); ++i)
      {
         DescCP a = DescCP.get(i);
         for(int j = -1; j < DescCP.size(); ++j)
         {
            DescCP b = DescCP.get(j);
            
            CoupleOfCP k = new CoupleOfCP(a,b);
            couple2couple_.put(k,k);
         }
      }      
   }
   
   static
   {
      init();
   }
   
   
   private static class CoupleOfCP
   {
      private DescCP c_;
      private DescCP p_;
      private int count_ = 0;
      
      public CoupleOfCP(DescCP c, DescCP p)
      {
         c_ = c;
         p_ = p;
      }
      
      public int hashCode()
      {
         return c_.hashCode() ^ p_.hashCode();
      }
      
      public boolean equals(Object other)
      {
         if(other == null)
            return false;
         
         if(other == this)
            return true;
         
         if(other.getClass() != this.getClass())
            return false;
         
         CoupleOfCP rhs = (CoupleOfCP) other;
         return this.c_.equals(rhs.c_) && this.p_.equals(rhs.p_);
      }
      
      public String toString()
      {
         return "(CoupleOfCP: " + c_ + " " + p_ + ") x " + count_;
      }      
   }
   
   public static void add_pair(DescCP c, DescCP p)
   {
//      if(c == DescCP.NONE || p == DescCP.NONE)
//         out_.println("adding pair: " + c + "," + p);
//      
      added_ += 1;
      
      CoupleOfCP k = new CoupleOfCP(c,p);
      CoupleOfCP val = (CoupleOfCP) couple2couple_.get(k);
      JimaMisc.ensure(val != null);

      // ...Else:
      val.count_ += 1;
   }
      
   
   private static void usage()
   {
      System.err.println("PairsOfCP: Compute frequent CP pairs");
      System.err.println("           in pairs of client-provider classes");
      System.err.println();
      System.err.println("Usage:");
      System.err.println("   PairsOfCP [-h] [-t <threshold>] [-o <out-file>]"
         + " <in-dir>");
      System.err.println();
      System.err.println("   <in-dir>   Directory of *" + Defs.CLUS_FILE_SUFFIX 
         + " files");
      System.err.println("   -h         Help");
      System.err.println("   -o         Send output to file");
      System.err.println("   -t         Minimal percentile (default: 50)");
      System.err.println();
      System.err.println("   Default input file = " + IN_FILE);
      System.err.println();
      System.err.println(); 
      System.exit(0);
   }
   
   public static void main(String[] args) throws Exception
   {
      CommandLine cl = new CommandLine(args, "-h", "-o,-t");
      if(cl.has("-h"))
         usage(); // Terminates the program 
      
      try
      {
         String s = cl.get_optional("-t", "50").param_at(0);
         int int_val = Integer.parseInt(s);
         threshold_ = int_val / 100.0;         
      }
      catch(NumberFormatException e1)
      {
         usage(); // Terminates the program
      }
      
      String str_out = cl.get_optional("-o", null).param_at(0);
      System.err.println("str_out=" + str_out);

      if(str_out != null)
         out_ = new PrintStream(new FileOutputStream(str_out));
      
      
      JimaMisc.ensure(true);
      
      Entry e = cl.get_optional(null, IN_FILE);
      if(e.param_count() != 1)
         usage();
      
      String fn = e.param_at(0);
      out_.println("Reading c/p associations from " + fn);
      Vector ams = AssocMat.build_from_dir(new File(fn));

      out_.println("Reading pairs from " + fn);
      Vector up_tables = UsagePairsTable.build(new File(fn));
//      out_.println("upts=" + up_tables + ", ams=" + ams);
      
      for(Iterator i = ams.iterator(); i.hasNext(); )
      {
         AssocMat am = (AssocMat) i.next();
         for(Iterator j = up_tables.iterator(); j.hasNext(); )
         {
            UsagePairsTable upt = (UsagePairsTable) j.next();
            if(upt.get_lib().equals(am.get_lib()))
               process(upt, am);            
         }
      }
      
      out_.println("-The End-");
      System.err.println("-The End-");
   }

   public static void cartesian_product(Record l, HashSet lhs, 
      Record r, HashSet rhs)
   {
      if(lhs.size() == 0 || rhs.size() == 0)
      {
         if(lhs.size() == 0)
            lhs.add(DescCP.NONE);
         
         if(rhs.size() == 0)
            rhs.add(DescCP.NONE);
         
//         out_.println("sizes: " + lhs.size() + ", " + rhs.size());
      }
      
      // ...Else:
      for(Iterator i = lhs.iterator(); i.hasNext(); )
      {
         DescCP a = (DescCP) i.next();
         for(Iterator j = rhs.iterator(); j.hasNext(); )
         {
            DescCP b = (DescCP) j.next();
            add_pair(a, b);
         }
      }
   }
   
   
   private static void process(UsagePairsTable upt, AssocMat am)
   {
      init();
      
      out_.println("Pair wise analysis: " + am + " with " + upt);
//      out_.println("Total possible pairs=" + upt.get_total());
      int total = upt.get_total();

      for(Iterator i = upt.pairs(); i.hasNext(); )
      {
         Pair pair = (Pair) i.next();
         
         Record rclient = am.record_of(pair.t1_);
         Record rprovider = am.record_of(pair.t2_);
         
         if(rclient == null || rprovider == null)
         {
//            String s = pair.t1_;
//            if(rprovider == null)
//               s = pair.t2_;
//            out_.println("s=" + s);
            continue;
         }
         
         HashSet client_cps = rclient.get_on();         
         HashSet provider_cps = rprovider.get_on();
         
         if(provider_cps.contains(DescCP.SNK))
            JimaMisc.log().println("c=" + rclient + ", p=" + rprovider);
         
         cartesian_product(rclient, client_cps, rprovider, provider_cps);
      }
      
      Vector vec = new Vector();
      vec.addAll(couple2couple_.values());
      Collections.sort(vec, new Comparator()
         {
            public int compare(Object o1, Object o2)
            {
               CoupleOfCP lhs  = (CoupleOfCP) o1;
               CoupleOfCP rhs  = (CoupleOfCP) o2;
               
               return lhs.count_ - rhs.count_;
            }
         });

//      int total = 0;
//      for(int i = 0; i < vec.size(); ++i)
//      {
//         CoupleOfCP curr = (CoupleOfCP) vec.get(i);
//         total += curr.count_;
//      }
      out_.println("Total Class-Class pairs=" + total);
      out_.println("Total C/P-C/P pairs=" + added_);
      out_.println("Average per couple=" + (double) added_ / vec.size());
      
      long so_far = added_;
      long stop_value = Math.round(added_ * threshold_);
      
      out_.println(", Ids, ,Names, ,#, %, left%");
      for(int i = vec.size() - 1; i >= 0 ; --i)
      {
         if(so_far < stop_value)
            break;
         
         CoupleOfCP curr = (CoupleOfCP) vec.get(i);
         double percent = ImplUtils.to_percent(curr.count_, added_);
         double left = ImplUtils.to_percent(so_far, added_);
          
         DescCP l = curr.c_;
         DescCP r = curr.p_;
         
         out_.println(i + ", " + l.get_id() + "," + r.get_id() + ", " 
            + l + "," + r + ", " +
            + curr.count_ + ", " + percent + "%, " + left + "%");
         so_far -= curr.count_;
      }
      
      out_.println("so_far (should be zero) =" + so_far);
      out_.println("added=" + added_);
      
   }   
}
