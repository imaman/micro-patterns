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

import il.ac.technion.micropatterns.jane.analysis.agents.Functions;
import il.ac.technion.micropatterns.jane.analysis.misc.Agents;
import il.ac.technion.micropatterns.jane.analysis.misc.CustomAgent;
import il.ac.technion.micropatterns.jane.lib.IProgressListener;
import il.ac.technion.micropatterns.jane.model.XModel;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import il.ac.technion.micropatterns.stats.Env;


public class Mogley
{
   public static void main(String[] args) throws Throwable
   {
      CustomAgent.out_ 
         = new PrintStream(new FileOutputStream(Env.fileName("customagent.csv")));
            
      String fn = "faked.jane";
      if(args.length == 1)
         fn = args[0];
      
      long t0 = System.currentTimeMillis();
      
      System.out.println("Exists=" + new File(fn).exists());
      
      XModel m = new XModel(new File(fn));         
      TypedModel tm = TypedModel.create(m);
      tm.load();
      
//      Agents.run_agent(tm, IProgressListener.EMPTY, CustomAgent.class);
//      Agents.run_agent(tm, IProgressListener.EMPTY, Monostate.class);
      Agents.run_agent(tm, IProgressListener.EMPTY, Functions.class);
//      Agents.run_agent(tm, IProgressListener.EMPTY, Faked.class);
//      Agents.run_agent(tm, IProgressListener.EMPTY, Pool.class);
      
//      SubsetHandle sh = (SubsetHandle) 
//         tm.subset_table_.lookup_handle(DescCP.CUSTOM.get_initials());
//      SetOfClasses soc = new SetOfClasses(sh, tm);
//      
//      for(Iterator i = soc.class_handles(); i.hasNext(); ) 
//      {
//         ClassHandle curr = (ClassHandle) i.next();
//         System.out.println(curr);
//      }      
      
      tm.save();
      
      CustomAgent.out_.flush();
      
      long t1 = System.currentTimeMillis();
      System.out.println("Time=" + (t1 - t0) / 1000 + " [sec]");
      System.out.println("-The End-");
   }
}
