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
import il.ac.technion.micropatterns.jane.app.gui.IWidthHandle;
import il.ac.technion.micropatterns.jane.app.gui.WidthManager;
import il.ac.technion.micropatterns.jane.app.gui.cells.AbstractCell;
import il.ac.technion.micropatterns.jane.app.gui.nodes.ClassNode;
import il.ac.technion.micropatterns.jane.app.gui.nodes.LeftTypeNode;
import il.ac.technion.micropatterns.jane.app.gui.nodes.RightTypeNode;
import il.ac.technion.micropatterns.jane.app.gui.nodes.TwoWayDiffNode;
import il.ac.technion.micropatterns.jane.lib.JaneMisc;
import il.ac.technion.micropatterns.jane.typedmodel.SetOfClasses;

public class TwoWayDiffRow extends AbstractRow
{
   private static final IWidthHandle TITLE_WIDTH 
      = WidthManager.new_fixed_width(50);
  
   private int row_n_;
   
   private static WidthManager width_man_ = new WidthManager(
      new int[] { 60, 90, 120, 100, 100, 90, 120, 90 });
      
   private IWidthListener wl_;
   private AbstractCell common_cell_;
   
   private static final float CORREL_ROUND_FACTOR = 1000; // 3 decimal places
   
   public TwoWayDiffRow(TwoWayDiffNode tsn, IView v, int depth, int row_n, 
      IWidthListener wl)
   {
      super(tsn, depth, v, width_man_.new_provider(wl), 
         Options.TWO_SETS_ROW_BG);
      
      wl_ = wl;      
      row_n_ = row_n;
      
      push_back(new_cell().set_text(row_n_));

      int left_only = tsn.num_of_left_only_;
      AbstractCell c = push_back(new_cell().set_text(left_only))
         .set_bg_color(Options.LEFT_TYPE_ROW_BG)
         .set_click_cmd(new ICommand()
         {
            public void execute()
            {
               mine().left_types_on_off(view_);
            }
         });
      select_triangle(c, LeftTypeNode.class, left_only);
      
              
      push_back(new_cell().set_text(tsn.lhs_.pretty_name()))
         .set_bg_color(Options.LEFT_TYPE_ROW_BG);


      String s= JaneMisc.to_percent(tsn.num_of_commons_, tsn.num_of_left_);
      push_back(new_cell().set_text(s))
         .set_bg_color(Options.LEFT_TYPE_ROW_BG);
      
      double correl = SetOfClasses.find_correl(tsn.soc_lhs_, tsn.soc_rhs_,
         tsn.universe_size_);
      long correl_int = Math.round(correl * CORREL_ROUND_FACTOR); 
      String correl_str = Float.toString(correl_int / CORREL_ROUND_FACTOR);
      push_back(new_cell().set_text(correl_str));

      int val = tsn.num_of_commons_;
      common_cell_ = push_back(new_cell().set_text(val))
         .set_click_cmd(new ICommand()
         {
            public void execute()
            {
               mine().types_on_off(view_);
            }
         });
      select_triangle(common_cell_, ClassNode.class, val);
      
      push_back(new_cell().set_text(tsn.rhs_.pretty_name()))
         .set_bg_color(Options.RIGHT_TYPE_ROW_BG);

      int right_only = tsn.num_of_right_only_;
      c = push_back(new_cell().set_text(right_only))
         .set_bg_color(Options.RIGHT_TYPE_ROW_BG)
         .set_click_cmd(new ICommand()
         {
            public void execute()
            {
               mine().right_types_on_off(view_);
            }
         });
      select_triangle(c, RightTypeNode.class, right_only);
   }
   
   
   public AbstractRow create_header()
   {
      BasicHeaderRow result = new BasicHeaderRow(depth_, view_, 
         close_cmd_, width_man_.new_provider(wl_), "COVERAGE", 85, 
         Options.TWO_SETS_HEADER_BG);

      result.push_back(new_cell().set_text("#"));
      result.push_back(new_cell().set_text("Left only"));
      result.push_back(new_cell().set_text("Left Subset"));
      result.push_back(new_cell().set_text("% Common"));
      result.push_back(new_cell().set_text("Correl"));
      result.push_back(new_cell().set_text("Common"));
      result.push_back(new_cell().set_text("Right Subset"));
      result.push_back(new_cell().set_text("Right only"));
      
      return result;
   }

   private TwoWayDiffNode mine()
   {
      return (TwoWayDiffNode) node_;
   }
}
