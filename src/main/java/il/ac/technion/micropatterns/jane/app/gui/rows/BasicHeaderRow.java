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

import il.ac.technion.micropatterns.jane.app.gui.ICommand;
import il.ac.technion.micropatterns.jane.app.gui.IView;
import il.ac.technion.micropatterns.jane.app.gui.IWidthHandle;
import il.ac.technion.micropatterns.jane.app.gui.WidthManager;
import il.ac.technion.micropatterns.jane.app.gui.cells.AbstractCell;
import il.ac.technion.micropatterns.jane.app.gui.cells.IPaintable;
import il.ac.technion.micropatterns.jane.app.gui.cells.UpTriangle;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

public class BasicHeaderRow extends AbstractRow
{
   private int diff = 0;
   private ICommand close_cmd_ = null;
   private static final IPaintable CLOSE_ICON = new UpTriangle();
   
   public BasicHeaderRow(int depth, IView v, ICommand close_cmd,
      WidthManager.HandleProvider hp, String title, int title_width, 
      Color bg_color)
   {
      super(null, depth, v, hp, bg_color);
      
      if(depth > 0)
         close_cmd_ = close_cmd;
      
      AbstractCell c = new Cell(title, Cell.FONT1)
         {
            public void cell_clicked()
            {
               if(close_cmd_ != null)
                  close_cmd_.execute();
            }      
         };
      
      AbstractCell temp = push_at(c, 0, 
         WidthManager.new_fixed_width(title_width));
      temp.set_bg_color(Color.WHITE);
      temp.set_image(CLOSE_ICON);
      temp.set_cursor(Cursor.HAND_CURSOR);
         
         
      diff = title_width;
   }

   
   public AbstractRow create_header()
   {
      return null; // Must be overridden 
   }
   

   protected AbstractCell push_at(AbstractCell c, int index, IWidthHandle h)
   {
      c.set_font(Cell.FONT2);
      return super.push_at(c, index, h);
   }

   
//   protected AbstractCell push_at(AbstractCell c, int index, IWidthHandle h)
//   {
//      c.brighter();
//      return super.push_at(c, index, h);
//   }
   
   public int left_gap()
   {
      return super.left_gap() - diff;
   }    
   
   private static class Cell extends AbstractCell
   {
      private static final Font FONT1
         = DEFAULT_CELL_FONT.deriveFont(Font.ITALIC, 10.0f);

      private static final Font FONT2 = new Font("Tahoma", Font.BOLD, 12);
      
      public Cell(String s, Font f)
      {
         super(s);
         bg_ = Color.WHITE;
         font_ = f;
      }
   }
   
}
