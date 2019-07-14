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











package il.ac.technion.micropatterns.jane.app.gui.nodes;

import il.ac.technion.jima.IWidthListener;
import il.ac.technion.micropatterns.jane.app.gui.IView;
import il.ac.technion.micropatterns.jane.app.gui.rows.AbstractRow;
import il.ac.technion.micropatterns.jane.app.gui.rows.SuperClassRow;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;

public class SuperClassNode extends BasicClassNode
{
   private ClassSpec ce_;
   
   public SuperClassNode(ClassSpec ce)
   {
      ce_ = ce;
   }

   public boolean match(String s)
   {
      return ce_.name().compareToIgnoreCase(s) == 0;
   }

   public AbstractRow create_row(IView v, int index, 
      IWidthListener wl)
   {
      return new SuperClassRow(this, v, get_depth(), index, ce_, wl);
   }
   
   public void methods_on_off(IView v)
   {
      methods_on_off_impl(v, ce_);
   }

   public void fields_on_off(IView v)
   {
      fields_on_off_impl(v, ce_);      
   }
}
