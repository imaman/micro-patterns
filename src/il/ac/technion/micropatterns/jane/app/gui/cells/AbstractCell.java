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











package il.ac.technion.micropatterns.jane.app.gui.cells;

import il.ac.technion.jima.powergui.IPowerCell;
import il.ac.technion.micropatterns.jane.app.gui.ICommand;
import il.ac.technion.micropatterns.jane.app.gui.IWidthHandle;
import il.ac.technion.micropatterns.jane.app.gui.rows.Options;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;


public abstract class AbstractCell implements IPowerCell
{
   private static final Cursor STD_CURSOR = null;
   protected static final Font DEFAULT_CELL_FONT = Options.DEFAULT_CELL_FONT;

   protected Color bg_ = null;
   protected Color fg_ = Color.BLACK;
   
   
   
   protected String s_ = "";
   protected Font font_ = DEFAULT_CELL_FONT;   
   
   private IWidthHandle width_handle_;

   private ICommand click_cmd_;
   private IPaintable image_ = null;
   private Cursor cursor_ = STD_CURSOR;

   public AbstractCell() { }

   public AbstractCell(ICommand click_cmd)
   {
      click_cmd_ = click_cmd;
   }      

   public AbstractCell(ICommand click_cmd, Color bg_color)
   {
      click_cmd_ = click_cmd;
      bg_ = bg_color;
   }      

   public AbstractCell(Color bg_color)
   {
      click_cmd_ = null;
      bg_ = bg_color;
   }      

   public AbstractCell(String s)
   {
      set_text(s);
   }
   
   public Cursor get_cursor()
   {
      return cursor_;
   }
   
   public AbstractCell set_cursor(int cursor_type)
   {
      cursor_ = Cursor.getPredefinedCursor(cursor_type);
      return this;
   }

   public AbstractCell set_font(Font f)
   {
      font_ = f;
      return this;
   }
   
   public AbstractCell set_text(boolean b)
   {
      return set_text(b ? "Yes" : "-");
   }

   public AbstractCell set_text(int n)
   {
      return set_text(Integer.toString(n));
   }

   public AbstractCell set_text(String s)
   {
      s_ = s;
      return this;
   }      

   public AbstractCell set_click_cmd(ICommand click_cmd)
   {
      click_cmd_ = click_cmd;
      return this;
   }
   
   public AbstractCell set_image(IPaintable p)
   {
      image_ = p;
      return this;
   }
   
   
   public void assign_width_handle(IWidthHandle h)
   {
      width_handle_ = h;
   }
   
   public AbstractCell set_bg_color(Color bg_color)
   {
      bg_ = bg_color;
      return this;
   }

   public AbstractCell set_fg_color(Color fg_color)
   {
      fg_ = fg_color;
      return this;
   }
   
   public int get_width()
   {
      return width_handle_.get();
   }
   
   public void set_width(int new_width)
   {
      if(width_handle_ == null)
         return; // Non resizeable cell
         
      width_handle_.set(new_width);
   }               

   public void brighter()
   {
      bg_ = bg_.brighter();
   }
   
   public void paint(Graphics g, int x, int y, int width, int height)
   {
      g.setFont(font_);
      g.setColor(bg_);
      g.fillRect(x, y, width, height);
      
      x += 10;

      if(image_ != null)
      {         
         image_.paint(g, x, y);
         x += image_.width();
      }         

      g.setColor(fg_);
      g.drawString(s_, x, y + 15);
   }

   public String tooltip_text()
   {
      return s_;
   }

   public void cell_clicked()
   {
      if(click_cmd_ != null)
         click_cmd_.execute();
//            JOptionPane.showMessageDialog(null, "text=" + s_);
   }
   
}
