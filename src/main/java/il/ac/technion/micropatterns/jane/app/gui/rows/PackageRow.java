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
import il.ac.technion.micropatterns.jane.app.gui.IView;
import il.ac.technion.micropatterns.jane.app.gui.WidthManager;
import il.ac.technion.micropatterns.jane.app.gui.nodes.PackageNode;

public class PackageRow extends AbstractRow
{

   private static WidthManager width_man_ = new WidthManager(
            new int[] { 60, 120 });
            
   private IWidthListener wl_;
   private int row_n_;
         

   public PackageRow(PackageNode pn, IView v, int depth, int row_n, 
      IWidthListener wl)
   {
      super(pn, depth, v, width_man_.new_provider(wl), Options.PACKAGE_ROW_BG);

      row_n_ = row_n;
      wl_ = wl;
      
      push_back(new_cell().set_text(row_n_));
      push_back(new_cell().set_text(pn.package_name_));      
      
   }
   
   /* (non-Javadoc)
    * @see il.ac.technion.micropatterns.jane.app.gui.rows.AbstractRow#create_header()
    */
   public AbstractRow create_header()
   {
      BasicHeaderRow result = new BasicHeaderRow(depth_, view_, 
         close_cmd_, width_man_.new_provider(wl_), "PACKAGES", 100, 
         bg_color_.darker());

      result.push_back(new_cell().set_text("Row"));
      result.push_back(new_cell().set_text("Package"));
      
      return result;
   }

}
