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


import il.ac.technion.jima.JimaMisc;
import il.ac.technion.jima.powergui.IPowerRow;
import il.ac.technion.micropatterns.jane.app.gui.ICommand;
import il.ac.technion.micropatterns.jane.app.gui.IView;
import il.ac.technion.micropatterns.jane.app.gui.IWidthHandle;
import il.ac.technion.micropatterns.jane.app.gui.WidthManager;
import il.ac.technion.micropatterns.jane.app.gui.cells.AbstractCell;
import il.ac.technion.micropatterns.jane.app.gui.cells.DownTriangle;
import il.ac.technion.micropatterns.jane.app.gui.cells.StdCell;
import il.ac.technion.micropatterns.jane.app.gui.cells.UpTriangle;
import il.ac.technion.micropatterns.jane.app.gui.nodes.INode;

import java.awt.Color;
import java.awt.Cursor;
import java.util.Iterator;
import java.util.Vector;


public abstract class AbstractRow implements IPowerRow
{
   protected INode node_;
   protected IView view_;
   private Vector cells_ = new Vector();
   protected Color bg_color_ = Color.MAGENTA;
   
   protected int depth_;
   private WidthManager.HandleProvider provider_;
   
   protected ICommand close_cmd_ = null;
   int cell_count_ = 0;

   public abstract AbstractRow create_header();
   
   public AbstractRow(INode node, int depth, IView view, 
      WidthManager.HandleProvider provider, Color bg_color)
   {
      bg_color_ = bg_color;
      view_ = view;
      depth_ = depth;
      provider_ = provider;
      node_ = node;
      
      if(node != null)
         close_cmd_ = new SubTableCloseCmd(view, node);
   }
   
   protected AbstractCell push_back(AbstractCell c, int default_width)
   {
      IWidthHandle h = provider_.next_handle(default_width);
      return push_at(c, cells_.size(), h);
   }

   protected AbstractCell push_back(AbstractCell c)
   {
      IWidthHandle h = provider_.next_handle();
      return push_at(c, cells_.size(), h);
   }

   protected AbstractCell push_at(AbstractCell c, int index, IWidthHandle h)
   {
      JimaMisc.ensure(index >= 0 && index <= cells_.size());
      
      c.assign_width_handle(h);    
      c.set_bg_color(bg_color_);
      cells_.add(index, c);
      
      return c;
   }

   protected void select_triangle(AbstractCell cell, Class c)
   {
      // Forward with n set to 1 => will show a triangle icon
      select_triangle(cell, c, 1);
   }
   
   protected void select_triangle(AbstractCell cell, Class c, int n)
   {
      JimaMisc.ensure(n >= 0);
      if(n == 0)
         return;
         
      if(this.node_.is_expanded(c))
         cell.set_image(new UpTriangle());
      else
         cell.set_image(new DownTriangle());
         
      cell.set_cursor(Cursor.HAND_CURSOR);
   }
   

   public int left_gap()
   {
      return 70 + depth_ * 20;
   }
   
   public Iterator cells()
   {
      return this.cells_.iterator();
   }
   
   void add_cell(AbstractCell pc)
   {
      cells_.add(pc);
   }
   
   protected AbstractCell new_cell()
   {
      return new StdCell(bg_color_);
   }
}
