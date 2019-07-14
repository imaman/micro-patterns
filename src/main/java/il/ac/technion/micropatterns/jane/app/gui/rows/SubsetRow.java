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
import il.ac.technion.micropatterns.jane.app.gui.nodes.INode;
import il.ac.technion.micropatterns.jane.app.gui.nodes.LeftTypeNode;
import il.ac.technion.micropatterns.jane.app.gui.nodes.RightTypeNode;
import il.ac.technion.micropatterns.jane.app.gui.nodes.SubsetNode;
import il.ac.technion.micropatterns.jane.app.gui.nodes.TwoWayDiffNode;
import il.ac.technion.micropatterns.jane.elements.SubsetElement;
import il.ac.technion.micropatterns.jane.elements.SubsetHandle;
import il.ac.technion.micropatterns.jane.lib.JaneMisc;

public class SubsetRow extends AbstractRow
{

   private AbstractCell members_cell_;
   private SubsetElement se_;
   private int row_n_;
   private IWidthListener wl_;
   
   private AbstractCell intersections_cell_;
   private AbstractCell unique_cell_;
   
   private static WidthManager width_man_ 
      = new WidthManager(new int[] { 60, 140, 60, 130, 130, 80 });

   /**
    * @param node
    * @param depth
    * @param view
    * @param provider
    * @param bg_color
    */
   public SubsetRow(INode node, IView view, int depth, int row_n, 
      IWidthListener wl, SubsetHandle sp)
   {
      super(node, depth, view, width_man_.new_provider(wl), 
         Options.SUBSET_ROW_BG);
         
      try
      {
         se_ = sp.typed_value(view.model());
         row_n_ = row_n;
         wl_ = wl;
      }
      catch (RuntimeException e)
      {
         JimaMisc.log().println("sp=" + sp);
         JimaMisc.stop(e);
      }
      
      push_back(new_cell().set_text(row_n_));
      push_back(new_cell().set_text(sp.pretty_name()));      
//    push_back(new_cell().set_text(sp.typed_value(view.model()).sp.pretty_name()));
      
      members_cell_ = push_back(new_cell().set_text(se_.size()));
      members_cell_.set_click_cmd(new ICommand()
         {
            public void execute()
            {
               mine().members_on_off(view_);                  
            }
         });
      select_triangle(members_cell_, LeftTypeNode.class, se_.size());

      int covered = mine().covered_;
      String s = JaneMisc.to_percent(covered, se_.size());
      s = s + " (" + covered + ")";      
      intersections_cell_ = push_back(new_cell()).set_text(s)
         .set_click_cmd(new ICommand()
         {
            public void execute()
            {
               mine().intersections_on_off(view_);
            }
         });         
      select_triangle(intersections_cell_, TwoWayDiffNode.class);

      int whole = se_.size();
      int uncovered = Math.max(whole - covered, 0);
      s = JaneMisc.to_percent(uncovered, whole);
      s = s + " (" + uncovered + ")";      
      unique_cell_ = push_back(new_cell()).set_text(uncovered)
         .set_click_cmd(new ICommand()
         {
            public void execute()
            {
               mine().unqiue_on_off(view_);
            }
         });         
      select_triangle(unique_cell_, RightTypeNode.class, uncovered);
      
      push_back(new_cell()).set_text(se_.is_library());

   }

   private SubsetNode mine()
   {
      return (SubsetNode) node_;
   }

   /**
    * @see il.ac.technion.micropatterns.jane.app.gui.rows.AbstractRow#create_header()
    */
   public AbstractRow create_header()
   {
      BasicHeaderRow result = new BasicHeaderRow(depth_, view_, 
         null, width_man_.new_provider(wl_), "SUBSETS", 80,
         Options.SUBSET_HEADER_BG);

      result.push_back(new_cell().set_text("#"));
      result.push_back(new_cell().set_text("Name"));
      result.push_back(new_cell().set_text("Types"));
      result.push_back(new_cell().set_text("Classified"));
      result.push_back(new_cell().set_text("Unclassified"));
      result.push_back(new_cell().set_text("Library?"));

      return result;
   }

}
