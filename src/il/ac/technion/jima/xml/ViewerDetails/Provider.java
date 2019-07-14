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


package il.ac.technion.jima.xml.ViewerDetails;

import il.ac.technion.jima.powergui.IPowerCell;
import il.ac.technion.jima.powergui.IPowerRow;
import il.ac.technion.jima.powergui.IPowerTableProvider;
import il.ac.technion.jima.xml.XmlNode;

import java.awt.Cursor;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.Vector;


public class Provider implements IPowerTableProvider
{

   private Vector nodes_;
   private SchemaDirectory sd_;
   
   public Provider(SchemaDirectory sd, Vector nodes)
   {
      nodes_ = nodes;
      sd_ = sd;
   }

   /* (non-Javadoc)
    * @see jima.powergui.IPowerTableProvider#get_iterator(int, int)
    */
   public Iterator get_iterator(int row_begin, int row_end)
   {
      Vector result = new Vector();
      
      for(int i = row_begin; i < row_end; ++i)
      {
         Row curr = (Row) nodes_.elementAt(i);
         add(result, curr);
      }
      
      return result.iterator();
   }

   private static class MyPowerRow implements IPowerRow
   {
      public int left_gap_ = 0;
      public Vector cells_ = new Vector();

      public int left_gap()
      {
         return left_gap_;
      }
                  
      public Iterator cells()
      {
         return cells_.iterator();
      }         
   };
   
   private static class MyPowerCell implements IPowerCell
   {
      private static final Cursor STD_CURSOR = Cursor.getDefaultCursor();

      private SchemaDirectory.CellElement ce_;
      private String text_;
      
      public MyPowerCell(String text, SchemaDirectory.CellElement ce)
      {
         ce_ = ce;
      }
      
      public void cell_clicked()
      {
         // Do nothing
      }

      public int get_width()
      {
         return ce_.width_;
      }

      /* (non-Javadoc)
       * @see jima.powergui.IPowerCell#paint(java.awt.Graphics, int, int, int, int)
       */
      public void paint(Graphics g, int x, int y, int width, int height)
      {
         g.drawString(text_, x, y);
      }

      public void set_width(int new_width)
      {
         ce_.width_ = new_width;
      }
      
      public Cursor get_cursor()
      {
         return STD_CURSOR;
      }

      public String tooltip_text()
      {
         return "";
      }
   }
   
   private void add(Vector result, Row row)
   {      
      MyPowerRow mpr = new MyPowerRow();
      mpr.left_gap_ = row.depth_ * 30;
      
      
      SchemaDirectory.CellElement ce = sd_.prototype_of(row.type_id_);
      XmlNode xn = row.node_;

      
      for(Iterator i = ce.sub_elements(); i.hasNext(); )      
      {
         int child_tid = ((Integer) i.next()).intValue();
         SchemaDirectory.CellElement child_ce = sd_.prototype_of(child_tid);
            
         String n = child_ce.type_name_;
         XmlNode child = xn.child(n);
         
         
   
      }
      
      
   }

}
