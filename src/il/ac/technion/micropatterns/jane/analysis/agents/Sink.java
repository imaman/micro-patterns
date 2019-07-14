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










package il.ac.technion.micropatterns.jane.analysis.agents;

import il.ac.technion.micropatterns.jane.analysis.misc.AllTypesAgent;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.InstructionParser;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.typedmodel.ModelDefinitions;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.util.Iterator;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InvokeInstruction;

public class Sink extends AllTypesAgent
{
   public static final String NAME = ModelDefinitions.SINK_SUBSET_NAME;
   
   public Sink()
   {
      super(NAME, DescCP.SNK);
   }

   
   private class MyVis extends EmptyVisitor
   {
      public int invoke_count_ = 0;
      
      public void visitInvokeInstruction(InvokeInstruction ii)
      {
         invoke_count_ += 1;         
      }         
   }
   
   public void visit(ClassHandle cp)
   {
      boolean b = check(cp);
      this.main_result_.set_decision(cp, b);
   }
         
   private boolean check(ClassHandle cp)
   {
      ClassSpec ce = cp.typed_value(xmodel_);

      if(ce.jc().isInterface())
         return false;
      

      Method[] methods = ce.jc().getMethods();
      for(int i = 0; i < methods.length; ++i)
      {
         Method m = methods[i];
         if(JavaSpec.is_constructor(m))
            continue; // Ignore "invoke" instructions within constructors

         InstructionParser ip = new InstructionParser(m);
         for(Iterator j = ip.iterator(); j.hasNext(); )
         {
            Instruction curr = (Instruction) j.next();
            
            if(!(curr instanceof InvokeInstruction))
               continue;
            
            // ...Else: Found a invocation
            return false;
         }                     
      }         
      
      return true;
   }         
}
