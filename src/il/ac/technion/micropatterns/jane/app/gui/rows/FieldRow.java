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











package il.ac.technion.micropatterns.jane.app.gui.rows;

import il.ac.technion.jima.IWidthListener;
import il.ac.technion.micropatterns.jane.app.gui.ICommand;
import il.ac.technion.micropatterns.jane.app.gui.IView;
import il.ac.technion.micropatterns.jane.app.gui.WidthManager;
import il.ac.technion.micropatterns.jane.app.gui.cells.AbstractCell;
import il.ac.technion.micropatterns.jane.app.gui.cells.NotAvailableIcon;
import il.ac.technion.micropatterns.jane.app.gui.nodes.ClassNode;
import il.ac.technion.micropatterns.jane.app.gui.nodes.FieldNode;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.lib.JaneMisc;
import org.apache.bcel.classfile.Field;

public class FieldRow extends AbstractRow
{
   private static WidthManager width_man_ = new WidthManager(
      new int[] { 60, 120, 240, 80, 80, 80 });
      
   private IWidthListener wl_;
   private AbstractCell field_type_cell_;
   
   public FieldRow(FieldNode fn, IView v, int depth, int index, Field f, 
      IWidthListener wl)
   {
      super(fn, depth, v, width_man_.new_provider(wl), Options.FIELD_ROW_BG);
      
      wl_ = wl;      

      push_back(new_cell().set_text(index));
      push_back(new_cell().set_text(f.getName()));
      
      String s = JaneMisc.signature_to_text(f.getSignature());
      
      ClassHandle cp = v.model().class_handle_of(s);

      field_type_cell_ = push_back(new_cell()).set_text(s);
      if(cp == null)
         field_type_cell_.set_image(NotAvailableIcon.INSTANCE);
      else
      {
         field_type_cell_.set_click_cmd(
            new ICommand()
            {
               public void execute()
               {
                  mine().type_on_off(view_);
               }
            });         
         select_triangle(field_type_cell_, ClassNode.class);
      }
      
      push_back(new_cell()).set_text(f.isStatic());
      push_back(new_cell()).set_text(f.isFinal());
      push_back(new_cell()).set_text(f.isPrivate());
   }

   private FieldNode mine()
   {
      return (FieldNode) node_;
   }
   
   
   
   public AbstractRow create_header()
   {
      BasicHeaderRow result = new BasicHeaderRow(depth_, view_, 
         close_cmd_, width_man_.new_provider(wl_), "FIELDS", 70,
         bg_color_.darker());

      result.push_back(new_cell().set_text("Row"));
      result.push_back(new_cell().set_text("Name"));
      result.push_back(new_cell().set_text("Type"));
      result.push_back(new_cell().set_text("Static?"));
      result.push_back(new_cell().set_text("Final?"));
      result.push_back(new_cell().set_text("Private?"));

      return result;
   }
}
