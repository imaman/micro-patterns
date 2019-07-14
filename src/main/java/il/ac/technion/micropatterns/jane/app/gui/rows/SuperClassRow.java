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
import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.app.gui.ICommand;
import il.ac.technion.micropatterns.jane.app.gui.IView;
import il.ac.technion.micropatterns.jane.app.gui.WidthManager;
import il.ac.technion.micropatterns.jane.app.gui.cells.AbstractCell;
import il.ac.technion.micropatterns.jane.app.gui.cells.JavaCodeIcon;
import il.ac.technion.micropatterns.jane.app.gui.nodes.FieldNode;
import il.ac.technion.micropatterns.jane.app.gui.nodes.MethodNode;
import il.ac.technion.micropatterns.jane.app.gui.nodes.SuperClassNode;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;

import java.awt.Cursor;


public class SuperClassRow extends AbstractRow
{
   private int n_;

   private static WidthManager width_man_ = new WidthManager(
      new int[] { 60, 120, 240, 80, 80, 80, 80, 80 });
   
   private IWidthListener wl_;
   
   private AbstractCell fields_cell_;
   private AbstractCell methods_cell_;
   
   private ClassSpec ce_;
   
   public SuperClassRow(SuperClassNode scn, IView v, int depth, int index, 
      ClassSpec ce, IWidthListener wl)
   {
      super(scn, depth, v, width_man_.new_provider(wl), 
         Options.SUPER_CLASS_ROW_BG);
      
      ce_ = ce;
      wl_ = wl;      
      n_ = index;
      
      String class_name = ce.name();
      int j = class_name.lastIndexOf('.');
      JimaMisc.ensure(j > 0);
      
      class_name = class_name.substring(j + 1);
      
      push_back(new_cell().set_text(index));
      push_back(new_cell().set_text(class_name))
         .set_image(JavaCodeIcon.inst)
         .set_cursor(Cursor.HAND_CURSOR)
         .set_click_cmd(new ICommand()
            {
               public void execute()
               {
                  DecompilerInvoker.show(ce_.jc());
               }
            });

      push_back(new_cell().set_text(ce.jc().getPackageName()));
      
      fields_cell_ = push_back(new_cell());
      int count = ce.jc().getFields().length;
      fields_cell_.set_text(count);
      fields_cell_.set_click_cmd(new ICommand()
            {
               public void execute()
               {
                  mine().fields_on_off(view_);
               }
            });            
      select_triangle(fields_cell_, FieldNode.class, count);
            
      methods_cell_ = push_back(new_cell());
      count = ce.jc().getMethods().length;
      methods_cell_.set_text(count);
      methods_cell_.set_click_cmd(new ICommand()
            {
               public void execute()
               {
                  mine().methods_on_off(view_);
               }
            });
      select_triangle(methods_cell_, MethodNode.class, count);
      

      push_back(new_cell().set_text(ce.jc().isInterface()));
      push_back(new_cell().set_text(ce.jc().isAbstract()));
      push_back(new_cell().set_text(ce.jc().isFinal()));      
   }
   
   public AbstractRow create_header()
   {
      BasicHeaderRow result = new BasicHeaderRow(depth_, view_, 
         close_cmd_, width_man_.new_provider(wl_), "SUPERCLASSES", 100, 
         bg_color_.darker());

      result.push_back(new_cell().set_text("#"));
      result.push_back(new_cell().set_text("Class"));
      result.push_back(new_cell().set_text("Package"));
      result.push_back(new_cell().set_text("Fields"));
      result.push_back(new_cell().set_text("Methods"));
      result.push_back(new_cell().set_text("Interface?"));
      result.push_back(new_cell().set_text("Abstract?"));
      result.push_back(new_cell().set_text("Final?"));      

      return result;
   }

   private SuperClassNode mine()
   {
      return (SuperClassNode) node_;
   }

}
