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

import il.ac.technion.micropatterns.jane.analysis.misc.Decision;
import il.ac.technion.micropatterns.jane.analysis.misc.InheritanceDescendingAgent;
import il.ac.technion.micropatterns.jane.analysis.misc.Result;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;

public class PseudoInterface extends InheritanceDescendingAgent
{
   private Result interface_;
   private Result pseudo_class_;
   
   public static final String NAME = "pseudo-interface";

   public PseudoInterface()
   {
      super(NAME, DescCP.INTR);
      interface_ = main_result_;      
      pseudo_class_ = Result.new_result(this, DescCP.PCLS);      
   }
   
   public boolean inspect(ClassHandle cp)
   {
      ClassHandle spr = cp.typed_value(xmodel_).get_first_super();
      if(spr == null)
         return true;

            
      ClassSpec ce = cp.typed_value(xmodel_);
      
      if(ce.jc().isInterface())
         this.interface_.set_decision(cp, true);
      
      boolean b = is_pseudo_class(cp, ce, spr);      
      pseudo_class_.set_decision(cp, b);

      return true;
   }

   private boolean is_pseudo_class(ClassHandle cp, ClassSpec ce, 
      ClassHandle spr)
   {               
      Decision d = pseudo_class_.get_decision(spr);                
      if((d != null) && d.is_negative())
         return false;
               
      if(ce.jc().isInterface())
         return false;
      
      if(!ce.jc().isAbstract())
         return false;
      
      
      int static_fields = 0;
      
      Field[] f = ce.jc().getFields();
      for(int i = 0; i < f.length; ++i)
      {              
         if(!f[i].isStatic())
            return false;
         
         static_fields += 1;
      }
      
      if(static_fields >= 1)
         return false;

      Method[] m = ce.jc().getMethods();
      for(int i = 0; i < m.length; ++i)
      {              
         if(m[i].isStatic())
            continue;

         if(JavaSpec.is_constructor(m[i]))            
            continue;

         if(!m[i].isAbstract())            
            return false;         
      }      
            
      return true;
   }      
}
