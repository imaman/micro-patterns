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


package il.ac.technion.jima.xml;

import il.ac.technion.jima.IWidthListener;
import il.ac.technion.jima.powergui.IPowerCell;
import il.ac.technion.jima.powergui.IPowerRow;
import il.ac.technion.jima.powergui.IPowerTableProvider;
import il.ac.technion.jima.powergui.PowerTable;
import il.ac.technion.jima.util.VectorOfInts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.ToolTipManager;



public class XDocViewer extends JFrame 
   implements IPowerTableProvider , IWidthListener
{

   private PowerTable pt_;
   private JScrollBar vsb_;
   private JScrollBar hsb_;
   private XmlDoc doc_ = new XmlDoc();

   private int max_horz_scroll_ = 0;

   private static final int ROW_HEIGHT = 20;
   
   /**
   * 
   */
   public XDocViewer()
   {
      ToolTipManager.sharedInstance().setInitialDelay(250);
      ToolTipManager.sharedInstance().setReshowDelay(900);

      this.getContentPane().setLayout(new BorderLayout());

      vsb_ = new JScrollBar();
      this.getContentPane().add(vsb_, BorderLayout.EAST);

      vsb_.addAdjustmentListener(new AdjustmentListener()
         {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
               pt_.set_vertical_offset(e.getValue());
               pt_.as_component().repaint();
            }
         });

      hsb_ = new JScrollBar(JScrollBar.HORIZONTAL);
      this.getContentPane().add(hsb_, BorderLayout.SOUTH);

      hsb_.addAdjustmentListener(new AdjustmentListener()
         {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
               pt_.set_horizontal_offset(e.getValue());
               pt_.as_component().repaint();
            }
         });

      

      pt_ = new PowerTable(ROW_HEIGHT);

      this.getContentPane().add(pt_.as_component(), BorderLayout.CENTER);
      pt_.as_component().addComponentListener(new ComponentAdapter()
         {
            public void componentResized(ComponentEvent e)
            {
               table_resized();
            }
         });
         
           
      pt_.set_data(this, 0);
      vsb_.setMaximum(pt_.size());
   }
   
   public void load(String xml_file) throws Exception
   {
      doc_ = new XmlDoc(xml_file);

      this.pt_.set_data(this, doc_.root.child(0).num_of_children());
      vsb_.setMaximum(pt_.size());      
   }


   private void table_resized()
   {
      // Readjust value, extent of the veritcal scroll bar
      int ne = pt_.num_of_visible_rows();
      int max = vsb_.getModel().getMaximum();
   
      int val = vsb_.getModel().getValue();
      val = Math.min(val, max - ne);

      vsb_.getModel().setValue(val);
      vsb_.getModel().setExtent(ne);

      // Readjust value, extent of the horizontal scroll bar
      max = max_horz_scroll_;
      ne = Math.min(this.pt_.as_component().getWidth(), max);

      val = hsb_.getModel().getValue();
      val = Math.min(val, max - ne);

//      Misc.log.println("max=" + max + ", ne=" + ne + ", val=" + val);
//      Misc.log.flush();

      hsb_.getModel().setExtent(ne);      
      hsb_.getModel().setValue(val);
   }


   public void width_changed(int new_width)
   {
      max_horz_scroll_ = new_width + 100;
      this.hsb_.getModel().setMaximum(max_horz_scroll_);
      
//      Misc.log.println("hsb.max=" + new_width);
//      Misc.log.flush();

      table_resized();
   }
   
   public Iterator get_iterator(int row_begin, int row_end)
   {
      Vector result = new Vector();
      if(doc_.root.num_of_children() <= 0)
         return result.iterator();
               
      XmlNode main_node = doc_.root.child(0);
            
      for(int i = row_begin; i < row_end; ++i)
         result.add(new MyRow(i, main_node.child(i), this));
      
      return result.iterator();   
   }

   private static class MyRow implements IPowerRow
   {
      private int n_;
      private IWidthListener wl_;
      private XmlNode node_;

      public MyRow(int index, XmlNode node, IWidthListener wl)
      {
         n_ = index;
         wl_ = wl;
         node_ = node;
      }

      public int left_gap()
      {
         return 60;
      }


      private static class MyCell implements IPowerCell
      {
         private static final Cursor STD_CURSOR = Cursor.getDefaultCursor();

         private IWidthListener wl_;
         private String s_;
         private int cell_n_;

         private Color bg_;

         private static Font font_ = new Font("Monospaced", Font.PLAIN, 14);
         private static VectorOfInts widths = new VectorOfInts();

         private static int total_width()
         {
            int result = 0;
            for(int i = 0; i < widths.size(); ++i)
               result += widths.intAt(i);

            return result;
         }

         public MyCell(String s, int cell_n, IWidthListener wl)
         {
            wl_ = wl;
            cell_n_ = cell_n;
            s_ = s;
            
            bg_ = new Color(194, 130, (80 + cell_n * 40) % 256);
         }


         public void paint(Graphics g, int x, int y, int width, int height)
         {
            g.setFont(font_);
            g.setColor(bg_);
            g.fillRect(x, y, width, height);

            g.setColor(Color.BLACK);
            g.drawString(s_, x + 10, y + 15);
         }

         public String tooltip_text()
         {
            return "tip: '" + s_ + "'";
         }
         
         public Cursor get_cursor()
         {
            return STD_CURSOR;
         }

         public int get_width()
         {
            reserve(cell_n_);
            return widths.intAt(cell_n_);
         }
         
         private static void reserve(int cell_n)
         {
            synchronized(widths)
            {               
               for(int i = widths.size(); i <= cell_n; ++i)
                  widths.addInt(60);
            }               
         }

         public void set_width(int new_width)
         {
            reserve(cell_n_);
            widths.putIntAt(new_width, cell_n_);

            wl_.width_changed(total_width());
         }
                  
         public void cell_clicked()
         {
            JOptionPane.showMessageDialog(null, "text=" + s_);
         }
      }


      public Iterator cells()
      {
         Vector result = new Vector();

         for(int i = 0; i < node_.num_of_children(); ++i)
         {
            XmlNode curr = node_.child(i);

            result.add(new MyCell(curr.text_, i, wl_));
         }

         return result.iterator();                  
      }
   }
   

   public static void main(String[] args) throws Exception
   {              
      XDocViewer f = new XDocViewer();
//      Misc.init_application(f.getClass());

      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      f.pack();
      f.setSize(400, 400);

      f.setVisible(true);
      
      f.load("c:/temp/a.xml");
   }
}
