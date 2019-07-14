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
import il.ac.technion.jima.util.OptionalYesNo;
import il.ac.technion.micropatterns.jane.app.gui.IView;
import il.ac.technion.micropatterns.jane.app.gui.rows.AbstractRow;
import il.ac.technion.micropatterns.jane.app.gui.rows.CheckableMethodRow;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.MethodSpec;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

public class CheckableMethodNode extends MethodNode
{
   private ClassHandle ch_;
   private OptionalYesNo delegate_state_;
   
   /**
    * @param m
    * @param cp
    */
   public CheckableMethodNode(TypedModel model, MethodSpec ms, 
      OptionalYesNo is_delegate)
   {
      super(model, ms);
            
      delegate_state_ = is_delegate;            
      ch_ = ms.class_handle();
   }
   
   public AbstractRow create_row(IView v, int index, 
      IWidthListener wl)
   {
      return new CheckableMethodRow(this, v, get_depth(), index, wl);
   }   
   
   public String get_class_name()
   {
      return ch_.get_name();
   }
   
   public void delegates_changed(IView v)
   {
      delegate_state_ = OptionalYesNo.next(delegate_state_);
      v.model().get_delegates_map().put(this.method_spec_, delegate_state_);
      v.repaint();
   }
   
   public OptionalYesNo get_delegate_state()
   {
      return delegate_state_;
   }
}
