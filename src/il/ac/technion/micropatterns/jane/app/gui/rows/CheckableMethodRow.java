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
import il.ac.technion.jima.util.OptionalYesNo;
import il.ac.technion.micropatterns.jane.app.commands.ShowMethodCmd;
import il.ac.technion.micropatterns.jane.app.gui.ICommand;
import il.ac.technion.micropatterns.jane.app.gui.IView;
import il.ac.technion.micropatterns.jane.app.gui.IWidthHandle;
import il.ac.technion.micropatterns.jane.app.gui.WidthManager;
import il.ac.technion.micropatterns.jane.app.gui.cells.AbstractCell;
import il.ac.technion.micropatterns.jane.app.gui.cells.CloseIcon;
import il.ac.technion.micropatterns.jane.app.gui.cells.EmptyIcon;
import il.ac.technion.micropatterns.jane.app.gui.cells.IPaintable;
import il.ac.technion.micropatterns.jane.app.gui.cells.JavaCodeIcon;
import il.ac.technion.micropatterns.jane.app.gui.cells.NotAvailableIcon;
import il.ac.technion.micropatterns.jane.app.gui.nodes.CheckableMethodNode;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.elements.MethodSpec;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.awt.Cursor;


public class CheckableMethodRow extends AbstractRow
{
//   private static final IPaintable[] OPTIONAL_BOOLEAN 
//      = new IPaintable[] 
//      { 
//         NotAvailableIcon.INSTANCE, 
//         CloseIcon.IMAGE, 
//         EmptyIcon.IMAGE 
//      };

   private static final IWidthHandle TITLE_WIDTH 
      = WidthManager.new_fixed_width(50);
     
   private static WidthManager width_man_ = new WidthManager(
      new int[] { 60, 120, 240, 120, 80, 80, 80, 80 });
      
   private IWidthListener wl_;
   private int n_;
   
   private AbstractCell delegates_cell_;
   
   private MethodSpec method_spec_;
   private ClassSpec ce_;
   

   /**
    * @param mn
    * @param v
    * @param depth
    * @param index
    * @param wl
    */
   public CheckableMethodRow(CheckableMethodNode mn, IView v, int depth, 
      int index, IWidthListener wl)
   {
      super(mn, depth, v, width_man_.new_provider(wl), 
         Options.CHECKABLE_METHOD_ROW_BG);
      
      wl_ = wl;      
      n_ = index;
      
      TypedModel model = v.model();
      method_spec_ = mn.method_spec();
      ce_ = method_spec_.get_class_element(model);

      push_back(new_cell().set_text(index));
      IPaintable icon = JavaCodeIcon.inst;
         
      AbstractCell c = new_cell();
      c.set_text(method_spec_.get_name(model) + "()")
         .set_font(Options.JAVA_CODE_FONT)
         .set_image(icon)
         .set_cursor(Cursor.HAND_CURSOR)
         .set_click_cmd(new ShowMethodCmd(method_spec_, view_));
      push_back(c);
      
      
      String s = method_spec_.get_declaration(model);      
      push_back(new_cell().set_text(s));      

      String class_name = method_spec_.get_class_element(model).name();   
      push_back(new_cell().set_text(class_name));
      
      push_back(new_cell().set_text(mn.is_static()));      
      push_back(new_cell().set_text(mn.is_abstract()));      
      push_back(new_cell()).set_text(mn.is_private());
      
      
      delegates_cell_ = new_cell();
         
      OptionalYesNo oys = mine().get_delegate_state();
      icon = EmptyIcon.IMAGE;
      if(oys.is_positive())
         icon = CloseIcon.IMAGE;

      if(oys.is_negative())
         icon = NotAvailableIcon.INSTANCE;         

      push_back(delegates_cell_)
         .set_image(icon)
         .set_bg_color(bg_color_)
         .set_click_cmd(new ICommand()
            {
               public void execute()
               {                  
                  mine().delegates_changed(view_);
               }
            });
   }


   public AbstractRow create_header()
   {
      BasicHeaderRow result = new BasicHeaderRow(depth_, view_, 
         close_cmd_, width_man_.new_provider(wl_), "METHODS", 75, 
         bg_color_.darker());

      result.push_back(new_cell().set_text("Row"));
      result.push_back(new_cell().set_text("Name"));
      result.push_back(new_cell().set_text("Signature"));      
      result.push_back(new_cell().set_text("Class"));      
      result.push_back(new_cell().set_text("Static?"));
      result.push_back(new_cell().set_text("Abstract?"));
      result.push_back(new_cell().set_text("Private?"));
      result.push_back(new_cell().set_text("Delegates?"));
      
      return result;
   }

   private CheckableMethodNode mine()
   {
      return (CheckableMethodNode) node_;
   }

}
