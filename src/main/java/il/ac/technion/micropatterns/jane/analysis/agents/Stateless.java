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

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.analysis.misc.AllTypesAgent;
import il.ac.technion.micropatterns.jane.analysis.misc.Decision;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;
import org.apache.bcel.classfile.Field;

public class Stateless extends AllTypesAgent
{
   public static final String NAME = "stateless";

   public Stateless()
   {
      super(NAME, DescCP.ST0);
   }
   
   public void visit(ClassHandle cp)
   {
      boolean b = query(cp);
      this.main_result_.set_decision(cp, b);
   }
         
   private boolean query(ClassHandle cp)
   {         
      ClassHandle spr = cp.typed_value(xmodel_).get_first_super();
      if(spr == null)
         return true;
         
      Decision d = get_decision(spr);
      JimaMisc.ensure(d.is_defined(), "spr=" + spr.toString() + " cp=" + cp);
      
      if(d.is_negative())
         return false;

      ClassSpec ce = cp.typed_value(xmodel_);
      
      if(ce.jc().isInterface())
         return false;
      
      Field[] fields = ce.jc().getFields();
      if(fields.length == 0)
         return true;

      for(int i = 0; i < fields.length; ++i)
      {
         Field f = fields[i];
         if(f.isFinal() && f.isStatic())
            continue;
         
         return false;
      }
         
      return true;
   }    
}
