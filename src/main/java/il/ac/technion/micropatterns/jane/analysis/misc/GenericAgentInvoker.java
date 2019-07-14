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
import il.ac.technion.micropatterns.jane.lib.IProgressListener;
import il.ac.technion.micropatterns.jane.typedmodel.IResultKeeeper;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

public class GenericAgentInvoker
{

   private IResultKeeeper rk_;
   private InheritanceDescendingAgent a_;
   private TypedModel m_;
   private IProgressListener pl_;
   private TreeWalker root_;
   
   public static void invoke(InheritanceDescendingAgent a, TypedModel m, 
      IProgressListener pl, TreeWalker root, IResultKeeeper rk) 
      throws Throwable
   {
      GenericAgentInvoker gai = new GenericAgentInvoker(a, m, pl, root, rk);
      gai.run();
   }

   public GenericAgentInvoker(InheritanceDescendingAgent a, TypedModel m, 
      IProgressListener pl, TreeWalker root) throws Throwable
   {
      this(a, m, pl, root, m);
   }
   
   public GenericAgentInvoker(InheritanceDescendingAgent a, TypedModel m, IProgressListener pl, 
      TreeWalker root, IResultKeeeper rk) throws Throwable
   {      
      a.init(m);
      
      root_ = root;
      a_ = a;
      pl_ = pl;
      m_ = m;
      rk_ = rk;
   }
   
   public void run() throws Throwable
   {
      a_.run(m_, pl_, root_, rk_);
      a_.theend();          
   }   
}
