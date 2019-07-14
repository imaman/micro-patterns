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


package il.ac.technion.jima.powergui;


import il.ac.technion.jima.JimaMisc;
import il.ac.technion.jima.powergui.details.Locator;
import il.ac.technion.jima.powergui.details.Locator.LocatorError;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;


public class PowerTable
{
   /**
   * 
   */
   
   private boolean locked_ = false;
   private JPanel canvas_;
   private Locator locator_ = null;
    
   private IPowerTableProvider provider_;
   private int size_;
   
   private int horz_offset_;
   private int vert_offset_;
   
   private IPowerCell resized_cell_ = null;
   private int drag_start_x_ = 0;
   private int drag_start_wi_ = 0;
   
   private int height_of_row_;
   
   
   private static final Cursor CELL_RESIZE_CURSOR 
      = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);

   private static final Cursor NORMAL_CURSOR 
      = Cursor.getDefaultCursor();
   
   public PowerTable(int height_of_row)
   {
      height_of_row_ = height_of_row;
      canvas_ = new JPanel()
         {
            public void paintComponent(Graphics g) 
            {
               super.paintComponent(g);
               paint_requested(g);            
            }     
            
         };
         
      canvas_.addMouseListener(new MouseAdapter()
         {
            public void mouseClicked(MouseEvent e)
            {
               try
               {
                  IPowerCell c = locator_.cell_at(e.getX(), e.getY());
                  if(c == null)
                     return;
                  
                  c.cell_clicked();                  
               }
               catch(LocatorError t)
               {
                  JimaMisc.stop(t);
               }               
            }  
            
            public void mousePressed(MouseEvent e)
            {
               mouse_pressed(e);
            }
            
            public void mouseReleased(MouseEvent e)
            {
               resized_cell_ = null;
            }
         });
         
      canvas_.addMouseMotionListener(new MouseMotionAdapter()
         {
            public void mouseMoved(MouseEvent e)
            {
               try
               {
                  mouse_moved(e);
               }
               catch (RuntimeException e1)
               {
                  // Absorb
               }               
            }            

            public void mouseDragged(MouseEvent e)
            {
               try
               {
                  mouse_dragged(e);
               }
               catch (RuntimeException e1)
               {
                  // Absorb
               }               
            }            

         });
      
      provider_ = new IPowerTableProvider()
         {
            public Iterator get_iterator(int row_begin, int row_end)
            {
               return Collections.EMPTY_LIST.iterator();
            }               
         };
    
      
      this.as_component().setToolTipText("");
               
      size_ = 0;         
   }

   public void set_locked(boolean b)
   {
      locked_ = b;
   }
   
   public boolean is_locked()
   {
      return locked_;
   }
   
   private void mouse_moved(MouseEvent e)
   {
      if(is_locked())
         return;
         
      Cursor new_cursor = null;
      String tip_text = "";

      try
      {
         int x = e.getX();
         int y = e.getY();
         IPowerCell c = locator_.left_of_border(x, y);
         
         if(c != null)
            new_cursor = CELL_RESIZE_CURSOR;
         else
         {
            c = locator_.cell_at(x, y);
            if(c != null)
            {               
               tip_text = c.tooltip_text();
               new_cursor = c.get_cursor();
            }               
         }            
      }
      catch(LocatorError t)
      {
         JimaMisc.stop(t);
      }                     
            
      JComponent c = this.as_component();            
      
      c.setToolTipText(tip_text);            
      c.setCursor(new_cursor);                              
   }
   
   private void mouse_dragged(MouseEvent e)
   {
      if(resized_cell_ == null)
         return;
         
      int diff = e.getX() - drag_start_x_;
      resized_cell_.set_width(drag_start_wi_ + diff);   
      
      this.as_component().repaint();   
   }
   
   private void mouse_pressed(MouseEvent e)
   {
      try
      {
         int x = e.getX();

         IPowerCell c = locator_.left_of_border(x, e.getY());
         if(c == null)
            return;
            
         resized_cell_ = c;
         drag_start_x_ = x;
         drag_start_wi_ = c.get_width();                    
      }
      catch(LocatorError t)
      {
         JimaMisc.stop(t);
      }                           
   }
   
   
   
   public int size()
   {
      return size_;
   }
   
   public int num_of_visible_rows()
   {
      return canvas_.getHeight() / height_of_row(); 
   }
   
   public void set_data(IPowerTableProvider new_provider, int size)
   {
      provider_ = new_provider;
      size_ = size;
   }
   
   public void set_vertical_offset(int rows)
   {
      vert_offset_ = rows;
   }
   
   public int get_vertical_offset()
   {
      return vert_offset_;
   }
         
   public void set_horizontal_offset(int pixels)
   {
      horz_offset_ = pixels;
   }
   
   public int get_horizontal_offset()
   {
      return horz_offset_;
   }
   
   public int height_of_row()
   {
      return height_of_row_;
   }
   
   
   public JComponent as_component()
   {
      return canvas_;
   }

   private void paint_requested(Graphics g)
   {
      try
      {
         paint_requested_impl(g);
      }
      catch(LocatorError e)
      {
         JimaMisc.stop(e);              
      }
   }

   private void paint_requested_impl(Graphics g) throws Locator.LocatorError
   {      
      Locator locator = new Locator(height_of_row(), num_of_visible_rows() + 50);
      
      int b = Math.max(vert_offset_, 0);
      int e = b + num_of_visible_rows();
      e = Math.min(e, size_);

      int rowh = height_of_row();           
      int y = 0;
      
      Rectangle orig = g.getClipBounds();
      
      for(Iterator iter = provider_.get_iterator(b, e); iter.hasNext(); )
      {              
         IPowerRow curr_row = (IPowerRow) iter.next();
         
         int x = curr_row.left_gap() - horz_offset_;
         for(Iterator cells = curr_row.cells(); cells.hasNext(); )
         {
            IPowerCell curr_cell = (IPowerCell) cells.next();
            int wi = curr_cell.get_width();
            
            locator.add_cell(curr_cell, x, y, wi);
            
            g.setClip(x, y, wi, rowh);
            curr_cell.paint(g, x, y, wi, rowh);
            g.setClip(orig.x, orig.y, orig.width, orig.height);
            
            g.setColor(Color.BLACK);
            g.drawRect(x, y, wi, rowh);           

            x += wi;                        
         }            
         
         locator.add_row(curr_row, y);
         
         y += rowh;                     
      }
      
      locator_ = locator;
   }            
   

   public static void main(String[] args)
   {
   }
}
