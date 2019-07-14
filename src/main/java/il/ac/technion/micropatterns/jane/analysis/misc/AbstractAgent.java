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

import il.ac.technion.micropatterns.jane.elements.EmptyVisitor;
import il.ac.technion.micropatterns.jane.typedmodel.IClassSpecProvider;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

public abstract class AbstractAgent extends EmptyVisitor implements IAgent
{
   private Result first_result_ = new Result();
   private String agent_name_;
   protected IClassSpecProvider xmodel_;
   
   public AbstractAgent(String agent_name)
   {
      agent_name_ = agent_name;
   }
   
   protected void clear_results()
   {
      first_result_ = new Result();
   }
   
   public void init(TypedModel m) throws Throwable
   {
      xmodel_ = m;
      agent_setup();
   }
   
   public void theend() throws Throwable
   {
      agent_teardown();
   }
   
//   public void ClassHandle get_class_element()
   
   public void agent_setup() throws Throwable { }
   public void agent_teardown() throws Throwable { }

//   protected HashSet get_subset(String subset_name)
//   {
//      SubsetHandle sh = xmodel_.get_subset_handle(subset_name);      
//      SubsetElement subset = sh.typed_value(xmodel_);
//      
//      HashSet temp = new HashSet();
//      for(int i = 0; i < subset.size(); ++i)
//      {
//         IHandle h = subset.handle_at(i);
//         temp.add(h);
//      }      
//      
//      return temp;
//   }


   public Result first_result()
   {
      return first_result_;
   }
      

   /**
    * @see il.ac.technion.micropatterns.jane.analysis.IAgent#name()
    */
   public String name()
   {
      return agent_name_;
   }
}
