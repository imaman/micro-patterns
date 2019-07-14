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











package il.ac.technion.micropatterns.jane.analysis.misc;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.analysis.agents.*;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.elements.EmptyVisitor;
import il.ac.technion.micropatterns.jane.elements.SubsetElement;
import il.ac.technion.micropatterns.jane.elements.SubsetHandle;
import il.ac.technion.micropatterns.jane.lib.IProgressListener;
import il.ac.technion.micropatterns.jane.model.IHandle;
import il.ac.technion.micropatterns.jane.model.IVisitor;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.Box;


public class Agents
{
   private static final String NAME = "field-count-";
   
//   public static final String SINGLETON_NAME = "singleton";
   public static final String DEADEND_NAME = "dead-end";


   public static void run_std(TypedModel m, IProgressListener pl) 
      throws Throwable
   {
      pl.start(m.type_count() * 3);
      
      Sink de = new Sink();
      run_agent_impl(m, pl, de);
      
      RestrictedCreation si = new RestrictedCreation();
      run_agent_impl(m, pl, si);
   }

   public static void run_three_agents(TypedModel m, IProgressListener pl) 
      throws Throwable
   {
      Agents.run_agent(m, pl , Pool.class);
      Agents.run_agent(m, pl , Box.class);
      Agents.run_agent(m, pl , EffectivelyImmutable.class);
   }

   public static void run_agent(TypedModel m, IProgressListener pl) 
      throws Throwable
   {
      long t0 = System.currentTimeMillis();
      
      Agents.run_agent(m, pl , EffectivelyImmutable.class);
      Agents.run_agent(m, pl , BoxEx.class);
      Agents.run_agent(m, pl , Functions.class);
      Agents.run_agent(m, pl , RestrictedCreation.class);
      Agents.run_agent(m, pl , Reimplementor.class);      
      Agents.run_agent(m, pl , PseudoInterface.class);
      Agents.run_agent(m, pl , Pool.class);
      Agents.run_agent(m, pl , Sink.class);
      Agents.run_agent(m, pl , Stateless.class);
      Agents.run_agent(m, pl , Monostate.class);
      Agents.run_agent(m, pl , TemplateMethod.class);
      
//      Agents.run_agent(m, pl, Faked.class);
      
      long t1 = System.currentTimeMillis();
      
      long secs = (t1 - t0) / 1000;
      JimaMisc.log().println("Timing of All agents : " + secs + "[sec]");

   }


   
   public static void run_agent(TypedModel m, IProgressListener pl, 
      Class c) throws Throwable   
   {
      InheritanceDescendingAgent a = (InheritanceDescendingAgent) 
         c.newInstance();
      
      JimaMisc.log().println("Starting agent: " + a.name());

      long secs = run_agent_impl(m, pl, a);

      JimaMisc.log().println("Agent completed: " + a.name() 
         + " in " + secs + " [sec]");
   }
   
   private static long run_agent_impl(TypedModel m, IProgressListener pl, 
      InheritanceDescendingAgent agent) throws Throwable
   {
      String s = agent.first_result().all_initials("");
      pl.set_text("Agent: " + agent.name() + "\nPattern(s): " + s);
      pl.start(m.type_count());
      
      long t0 = System.currentTimeMillis();
      
      GenericAgentInvoker.invoke(agent, m, pl, m.get_inheritance_walker(), m);
      
      long t1 = System.currentTimeMillis();
      
      pl.done();
      
      long secs = (t1 - t0) / 1000;
      return secs;
   }

   
   private static void add_subset(TypedModel m, String name, Vector handles, 
      IProgressListener pl) 
   {
      SubsetHandle sh = m.get_subset_handle(name);
      SubsetElement se = new SubsetElement();
      
      for(Iterator i = handles.iterator(); i.hasNext(); )
      {
         IHandle curr = (IHandle) i.next();
         se.add(curr);
         
         if(pl != null)
            pl.add_to_current(1);
      }
      
      m.subset_table_.set(sh, se);
   }

   private static void run_std(TypedModel m, IVisitor v, IProgressListener pl) 
   {
      for(Iterator i = m.all_types(); i.hasNext(); )
      {
         ClassHandle curr = (ClassHandle) i.next();
         curr.accept(v);         
         
         pl.add_to_current(1);
      }                 
   }      
   
   public static String name(int n)
   {
      return NAME + Integer.toString(n);
   }
   
   public static void run_sample(TypedModel m, IProgressListener pl) 
   {
      pl.start(m.type_count() * 8);
      
      run_sample(m, 20, pl, 1);
      run_sample(m, 12, pl, 1);
      run_sample(m, 10, pl, 1);
      run_sample(m, 4, pl, 1);
   }

   public static void run_sample_one(TypedModel m, IProgressListener pl) 
   {
      pl.start(m.type_count() * 2);      
      run_sample(m, 20, pl, 1);      
   }
   
   public static void run_sample(TypedModel m, int n, IProgressListener pl, int step) 
   {
      String name = name(n);
      SubsetHandle sh = m.get_subset_handle(name);      
      SubsetElement se = new SubsetElement();
      
      MyVis v = new MyVis(m, n);
      
     
      for(Iterator i = m.all_types(); i.hasNext(); )
      {
         ClassHandle curr = (ClassHandle) i.next();
         curr.accept(v);      
            
         pl.add_to_current(step);
      }                 
      
      for(Iterator i = v.result.iterator(); i.hasNext(); )
      {
         IHandle curr = (IHandle) i.next();
         se.add(curr);
      
         pl.add_to_current(step);
      }
      
      m.subset_table_.set(sh, se);
   }
   
   private static class MyVis extends EmptyVisitor
   {
      public Vector result = new Vector();
      private TypedModel m_;
      private int n_;
      
      
      public MyVis(TypedModel m, int n) 
      {
         m_ = m;
         n_ = n;
      }
      
      public void visit(ClassHandle h) 
      {
         ClassSpec ce = m_.get_class_element(h);
         
         int n = ce.jc().getFields().length;
         if(n == n_)
            result.add(h);
      }
   }
}
