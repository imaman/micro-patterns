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











package il.ac.technion.micropatterns.jane.app.gui.views;

import il.ac.technion.jima.IWidthListener;
import il.ac.technion.jima.JimaMisc;
import il.ac.technion.jima.powergui.PowerTable;
import il.ac.technion.micropatterns.jane.app.gui.AbstractViewManager;
import il.ac.technion.micropatterns.jane.app.gui.IJaneViewListener;
import il.ac.technion.micropatterns.jane.app.gui.nodes.ClassNode;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.model.ITable;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.util.Iterator;


public class ClassView extends AbstractViewManager
{   
   public ClassView(PowerTable pt, IJaneViewListener listener, 
      IWidthListener width_listener)
   {
      super(pt, listener, width_listener);      
   }
   
   protected void assign_model_impl(TypedModel m)
   {
      root_.remove();
      
      ITable t = m.class_table_;
      JimaMisc.log().println("Building view " + t);
            
      for(Iterator i = t.all_handles(); i.hasNext(); )
      {
         ClassHandle curr = (ClassHandle) i.next();
         root_.add_child(new ClassNode(curr));
      }         
      
      JimaMisc.log().println("Children of root_=" + root_.num_of_children());
   }
}
