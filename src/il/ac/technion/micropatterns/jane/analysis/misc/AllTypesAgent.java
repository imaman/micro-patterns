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

import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.model.IVisitor;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

public abstract class AllTypesAgent extends InheritanceDescendingAgent 
   implements IVisitor
{
//   protected Result main_result_;

   
   public AllTypesAgent(String agent_name, DescCP dcp)
   {
      super(agent_name, dcp);
//      main_result_ = Result.new_result(this, result_name, initials);
   }


   private void set_decision(ClassHandle h, boolean yes_no)
   {
      main_result_.set_decision(h, yes_no);
   }
      
   protected Decision get_decision(ClassHandle h)
   {
      return main_result_.get_decision(h);
   }
   
   protected void reject(ClassHandle h)
   {
      set_decision(h, false);
   }
   
   protected void confirm(ClassHandle h)
   {
      set_decision(h, true);
   }

//   public void run(TypedModel m, IProgressListener pl) throws Throwable
//   {
//      xmodel_ = m;
//      
//      TreeWalker w = m.get_inheritance_walker();
//      
//      JimaMisc.log().println("Invoking agent. size=" + w.size());
//      for(Iterator i = w.create_linear_order(); i.hasNext(); )
//      {
//         IHandle curr = (IHandle)i.next();
//         curr.accept(this);
//         
//         pl.add_to_current(1);
//      }           
//      
//      Result.save(this, m);
//   }   
   
   
   /**
    * @see il.ac.technion.micropatterns.jane.analysis.misc.InheritanceDescendingAgent#inspect(il.ac.technion.micropatterns.jane.elements.ClassHandle)
    */
   protected boolean inspect(ClassHandle h)
   {
      h.accept(this);
      return true;
   }
}
