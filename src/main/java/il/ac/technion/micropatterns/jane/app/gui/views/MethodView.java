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
import il.ac.technion.jima.util.OptionalYesNo;
import il.ac.technion.micropatterns.jane.app.gui.AbstractViewManager;
import il.ac.technion.micropatterns.jane.app.gui.IJaneViewListener;
import il.ac.technion.micropatterns.jane.app.gui.nodes.CheckableMethodNode;
import il.ac.technion.micropatterns.jane.elements.MethodHandle;
import il.ac.technion.micropatterns.jane.elements.MethodSpec;
import il.ac.technion.micropatterns.jane.lib.MapOfMethods;
import il.ac.technion.micropatterns.jane.typedmodel.ModelDefinitions;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.util.Iterator;


public class MethodView extends AbstractViewManager
{
   private int count_;
   private MapOfMethods delegates_decision_;

   public MethodView(PowerTable pt, IJaneViewListener listener, 
      IWidthListener width_listener)
   {
      this(pt, listener, width_listener, Integer.MAX_VALUE);
   }      
   
   public MethodView(PowerTable pt, IJaneViewListener listener, 
      IWidthListener width_listener, int count)
   {
      super(pt, listener, width_listener);   
      count_ = count;   
   }


   protected void assign_model_impl(TypedModel m)
   {
      delegates_decision_ = (MapOfMethods) 
         m.get_vector_element(ModelDefinitions.STD_METHOD_PROPS).at(0);

      root_.remove();
            
            
      JimaMisc.log().println("MethodView. Methods=" + delegates_decision_);
                  
      int n = 0;
      for(Iterator i = m.method_table_.all_handles(); i.hasNext(); ++n)
      {
         if(n >= count_)
            break;
            
         MethodHandle curr = (MethodHandle) i.next();
         MethodSpec ms = curr.typed_value(m);
         

         OptionalYesNo temp = (OptionalYesNo) delegates_decision_.get(curr);
         OptionalYesNo oys = OptionalYesNo.select(temp);
         
         root_.add_child(new CheckableMethodNode(m, ms, oys));
      }               
   }
}
