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

import il.ac.technion.jima.util.TreeWalker;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.lib.IProgressListener;
import il.ac.technion.micropatterns.jane.typedmodel.IResultKeeeper;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.util.HashSet;


public abstract class InheritanceDescendingAgent extends AbstractAgent
{
   protected Result main_result_;  
   private IProgressListener pl_;                              
   protected AvailableFeatures af_ = new AvailableFeatures(); 


   protected abstract boolean inspect(ClassHandle h);

   public InheritanceDescendingAgent(String agent_name, DescCP dcp)
   {
      super(agent_name);
      main_result_ = Result.new_result(this, dcp);      
   }
   
   private static InheritanceDescendingAgent[] dup(
      InheritanceDescendingAgent[] src)
   {
      InheritanceDescendingAgent[] result 
         = new InheritanceDescendingAgent[src.length];
      
      for(int i = 0; i < src.length; ++i)
         result[i] = src[i];
      
      return result;
   }
   
   public static void runSeveralAgents(InheritanceDescendingAgent[] agents, 
      TypedModel m, IProgressListener pl, IResultKeeeper rk, TreeWalker w,
      HashSet markedTypes)
      throws Throwable
   {
      AvailableFeatures af = new AvailableFeatures();
      
      for(int i = 0; i < agents.length; ++i)
      {
         agents[i].pl_ = pl;
         agents[i].xmodel_ = m;
         agents[i].af_ = af;
      }
            
      InheritanceDescendingAgent[] copy = dup(agents);
      
      scanSeveralAgents(copy, pl, w, af, m, markedTypes);

      // d/o
//      for(Iterator i = markedTypes.iterator(); i.hasNext(); )
//         JimaMisc.log().println("<MT>: " + i.next());
      
//      System.err.println("agents.length=" + agents.length);
//      System.err.println("agents=" + agents);
      for(int i = 0; i < agents.length; ++i)
      {
         Result.save(agents[i], rk);
         agents[i].agent_teardown();
      }      
   }
   
   private static void scanSeveralAgents(InheritanceDescendingAgent[] agents, 
      IProgressListener pl, TreeWalker w, AvailableFeatures af, 
      TypedModel xmodel, HashSet markedTypes)
   {
      pl.set_current(w.offset());
      
      if(!w.ok())
         return;
         
      ClassHandle h = (ClassHandle) w.data();
      boolean ismarked = markedTypes.contains(h);
      if(!ismarked)
         return;
      
      af.push(h, xmodel);
      
      InheritanceDescendingAgent[] copy = dup(agents);
      
      for(int i = 0; i < agents.length; ++i)
      {
         if(copy[i] != null)
         {
            boolean continue_deeper = agents[i].inspect(h);
            if(!continue_deeper)
               copy[i] = null;
         }
      }
         
      for(TreeWalker c = w.first_child(); c.ok(); c = c.next_brother())
         scanSeveralAgents(copy, pl, c, af, xmodel, markedTypes);
      
      af.pop();      
   }
      
   public void run(TypedModel m, IProgressListener pl) throws Throwable
   {
      run(m, pl, m.get_inheritance_walker(), m);
   }
   
   public void run(TypedModel m, IProgressListener pl, TreeWalker w, 
      IResultKeeeper rk) 
      throws Throwable
   {
      xmodel_ = m;
      pl_ = pl;
      
      scan_impl(w); 
      
      
      // Save the result(s)
      Result.save(this, rk);
      this.agent_teardown();

   }
   
   private void scan_impl(TreeWalker w)
   {      
      pl_.set_current(w.offset());
      
      if(!w.ok())
         return;
         
      ClassHandle h = (ClassHandle) w.data();
      af_.push(h, xmodel_);
      
      boolean continue_deeper = inspect(h);
      if(continue_deeper)
      {
         for(TreeWalker c = w.first_child(); c.ok(); c = c.next_brother())
            scan_impl(c);
      }
      
      af_.pop();
   }      
}
