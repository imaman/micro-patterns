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








package il.ac.technion.micropatterns.jungle;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.analysis.agents.*;
import il.ac.technion.micropatterns.jane.analysis.agents.BoxEx;
import il.ac.technion.micropatterns.jane.analysis.agents.EffectivelyImmutable;
import il.ac.technion.micropatterns.jane.analysis.agents.Functions;
import il.ac.technion.micropatterns.jane.analysis.agents.Monostate;
import il.ac.technion.micropatterns.jane.analysis.agents.Pool;
import il.ac.technion.micropatterns.jane.analysis.agents.PseudoInterface;
import il.ac.technion.micropatterns.jane.analysis.agents.Reimplementor;
import il.ac.technion.micropatterns.jane.analysis.agents.RestrictedCreation;
import il.ac.technion.micropatterns.jane.analysis.agents.Sink;
import il.ac.technion.micropatterns.jane.analysis.agents.Stateless;
import il.ac.technion.micropatterns.jane.analysis.agents.TemplateMethod;
import il.ac.technion.micropatterns.jane.analysis.misc.GenericAgentInvoker;
import il.ac.technion.micropatterns.jane.analysis.misc.InheritanceDescendingAgent;
import il.ac.technion.micropatterns.jane.app.Reports;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.SubsetElement;
import il.ac.technion.micropatterns.jane.lib.ClassPathSpecifier;
import il.ac.technion.micropatterns.jane.lib.IProgressListener;
import il.ac.technion.micropatterns.jane.typedmodel.IResultKeeeper;
import il.ac.technion.micropatterns.janeutils.Defs;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;

import il.ac.technion.micropatterns.jungle.buildup.Builder;
import il.ac.technion.micropatterns.jungle.model.Cluster;
import il.ac.technion.micropatterns.jungle.model.Clusterization;
import il.ac.technion.micropatterns.jungle.model.Ensemble;
import il.ac.technion.micropatterns.jungle.model.JavaCollection;
import il.ac.technion.micropatterns.jungle.model.ClassInfo.ClassInfoError;
import il.ac.technion.micropatterns.tables.BadReference;
import il.ac.technion.micropatterns.tables.Table;

public class Jungle implements IResultKeeeper
{
   public static PrintStream out_ = null;
   
   public boolean writeToFiles = true;
   public boolean fullPatternsNames = false;
   
   private boolean name_checking_ = true;
   private final String output_dir_;
   private Ensemble e_;
   private Clusterization clusters_ = new Clusterization();
   private PrintStream ps_ = null;
   
   private long start_time_ = 0;
 
   public int ensemble_size()
   {
      return e_.size();
   }
   
   public void setWriteToFiles(boolean b)
   {
      writeToFiles = b;
   }
   
   public void set_output_file(PrintStream ps)
   {
      ps_ = ps;
   }
   
   public Jungle(ClassPathSpecifier prg, ClassPathSpecifier optional_jre, 
      String output_dir) throws Throwable
   {
      this(prg, new ClassPathSpecifier(), optional_jre, output_dir);
   }
   
   public Jungle(ClassPathSpecifier prg, ClassPathSpecifier lib, 
      ClassPathSpecifier optional_jre, String output_dir) throws Throwable
   {
      start_time_ = System.currentTimeMillis();

      output_dir_ = output_dir;
      Builder b = new Builder(prg, lib, optional_jre);
      e_ = b.go();
   }
   
   public void setNameChecking(boolean b)
   {
      name_checking_ = b;
   }
   

   
   
   
   /* (non-Javadoc)
    * @see il.ac.technion.micropatterns.jane.typedmodel.IResultKeeeper#add_result_set(java.lang.String, il.ac.technion.micropatterns.jane.elements.SubsetElement)
    */
   public void add_result_set(DescCP dcp, SubsetElement se)
   {         
      Vector temp = new Vector();
      for(Iterator i = se.handles(); i.hasNext(); )
      {
         ClassHandle ch = (ClassHandle) i.next();
         temp.add(ch.get_name());
      }
      
      clusters_.get_cluster(dcp).addAll(temp.iterator());
   }
   
   private void check(DescCP dcp, int expected_size)
   {
      Cluster c = clusters_.get_cluster(dcp);
      JimaMisc.ensure(c.size() == expected_size, 
         dcp.get_initials() + ", size is " + c.size() + ", es=" 
         + expected_size);
   }

   public void verify()
   {
      check(DescCP.BOX, 97);
      check(DescCP.BX2, 236);
      check(DescCP.FLY, 57);
      check(DescCP.IMM, 1223);
      check(DescCP.IMB, 540);
      check(DescCP.IMP, 1200);
      check(DescCP.INTR, 507);
      check(DescCP.POO, 145);
      check(DescCP.RC, 51);
      check(DescCP.RIM, 426);
      check(DescCP.SNK, 739);
      check(DescCP.ST0, 353);
      check(DescCP.ST1, 146);
      check(DescCP.TM, 74);      
   }
   
   public void dump()
   {
      for(Iterator i = clusters_.clusters(); i.hasNext(); )
      {
         Cluster curr = (Cluster) i.next();         
         curr.get_desc();

//         System.out.println(k.get_initials() + " has " + curr.size() 
//            + " members");
      }
      
//      Reports.association_matrix(mps_.values().iterator(), null);
   }


   public void run_agent(Class agent_class) throws Throwable
   {      
      InheritanceDescendingAgent a 
         = (InheritanceDescendingAgent) agent_class.newInstance();
      
      println("Starting agent: " + a.name());
      GenericAgentInvoker.invoke(a, null, IProgressListener.EMPTY, 
         e_.get_root(), this);
   }
   
   public void run_agents() throws Throwable
   {      
//      println("Size of ensemble: " + this.e_.size());
      
      InheritanceDescendingAgent[] agents = 
         {
            new BoxEx(),
            new EffectivelyImmutable(),
            new Functions(),
            new Monostate(),
            new Pool(),
            new PseudoInterface(),
            new Reimplementor(),
            new RestrictedCreation(),
            new Sink(),
            new Stateless(),
            new TemplateMethod(),
            new Self(),
         };
      
      InheritanceDescendingAgent.runSeveralAgents(agents, null, 
         IProgressListener.EMPTY, this, e_.get_root(), e_.get_marked_types());
      
//      run_agent(BoxEx.class);
//      run_agent(EffectivelyImmutable.class);
//      run_agent(Functions.class);
//      run_agent(Monostate.class);
//      run_agent(Pool.class);
//      run_agent(PseudoInterface.class);
//      run_agent(Reimplementor.class);
//      run_agent(RestrictedCreation.class);
//      run_agent(Sink.class);
//      run_agent(Stateless.class);
//      run_agent(TemplateMethod.class);
      

   //      run_agent(Faked.class);
   }

   
   
   public Table make_report() throws ClassInfoError, BadReference
   {   
      PrintStream csv_out = ps_;
      if(writeToFiles)
      {
         File csvf = new File(this.output_dir_, "vectors.csv");
         try
         {
            if(csv_out == null)
               csv_out = new PrintStream(new FileOutputStream(csvf));
         }
         catch(IOException e)
         {
            JimaMisc.stop("Cannot open file " + csvf);
            System.exit(-1);
         }
      
         JimaMisc.ensure(csv_out != null);
      }
      
      Table result = null;      
      for(Iterator i = this.e_.get_prg_collections(); i.hasNext(); )
      {
         JavaCollection curr = (JavaCollection) i.next();
         
         if(writeToFiles)
         {
            File f = new File(output_dir_, 
               curr.get_name() + Defs.CLUS_FILE_SUFFIX);
            
            PrintStream out = null;
            
            out = null;
            try
            {
               out = new PrintStream(new FileOutputStream(f));
            }
            catch(FileNotFoundException e1)
            {
               JimaMisc.stop("Cannot open file " + f);
            }         
            
            Reports.init(out);
            
            Reports.association_matrix(this.clusters_, curr, e_, name_checking_);
            Reports.association_matrix(this.clusters_, curr, e_, true, 
               csv_out, name_checking_);
         }
         
          Table t =  Reports.vectors_of(clusters_, curr, e_, name_checking_, 
             fullPatternsNames);
          result = Table.add(result, t);
      }
      
      return result;
   }
   
   
   public static void go(ClassPathSpecifier prg_cp, 
      ClassPathSpecifier optional_jre, String output_dir) throws Throwable
   {
      go(prg_cp, new ClassPathSpecifier(), optional_jre, output_dir);
   }
   
   public static Jungle go(ClassPathSpecifier prg_cp, ClassPathSpecifier lib_cp,
      ClassPathSpecifier optional_jre, String output_dir)
      throws Throwable
   {
      
      Jungle j = new Jungle(prg_cp, lib_cp, optional_jre, output_dir);
      
      int sz = j.e_.size();      
      System.out.println("Size of " + prg_cp + ": " + sz + " [classes]");

      j.go();
            
      return j;   
   }
   
   public Table go() throws Throwable
   {
      return go(true);
   }
      
   public Table go(boolean printRate) throws Throwable
   {
      run_agents();
      dump();
      Table result = make_report();

      long t1 = System.currentTimeMillis();
      long secs = (t1 - start_time_) / 1000;
            
      int sz = e_.size();      
      double rate = sz / secs;
    
      if(printRate)
         System.out.println("Current rate: " + rate + " [classes/second]");
      
      return result;
   }
   
   
   public static void println(Object o)
   {
      System.out.println(o);
   }
   
   public static void main(String[] args) throws Throwable
   {
      String cd = System.getProperty("user.dir");
      Reports.init(cd);
      
      if(args.length < 2 || args.length > 3)
      {
         System.err.println("Jungle - Coding Pattern detection tool");
         System.err.println("Usage: Jungle <jar-dir> <program-classpath>"
            + " [<library-classpath>]");
         
         System.exit(0);         
      }
      
      File jar_dir = new File(args[0]).getAbsoluteFile();
      
      ClassPathSpecifier prg_cp = new ClassPathSpecifier(jar_dir, args[1]);
      ClassPathSpecifier lib_cp = null;
      if(args.length > 2)
         lib_cp = new ClassPathSpecifier(jar_dir, args[2]);
            

      go(prg_cp, lib_cp, new ClassPathSpecifier(), 
               jar_dir.getAbsolutePath());
      System.out.println("-the end-");
   }   
}
