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



package il.ac.technion.jima.powergui.details;

import il.ac.technion.jima.powergui.IPowerCell;
import il.ac.technion.jima.powergui.IPowerRow;

import java.util.Iterator;
import java.util.LinkedList;


/**
 * One liner class description
 * <p> Additional description </p> 
 *
 * @author maman
 * @date May 5, 2004
 * 
 * @see SomeOtherClass
 */
public class Locator
{
   
   public static class LocatorError extends Exception
   {
      LocatorError(String s)
      {
         super(s);
      }
   }

   private int row_height_;
   private RowEntry[] rows_;
   private static final int BORDER_WIDTH = 2;
   /**
   * 
   */
   public Locator(int row_height, int num_of_rows)
   {
      row_height_ = row_height;
      rows_ = new RowEntry[num_of_rows];
   }
   
   public void add_row(IPowerRow r, int y) throws LocatorError
   {
      RowEntry re = get_row_entry(y);
      re.row_ = r;
   }
   
   public void add_cell(IPowerCell c, int x, int y, int wi) 
      throws LocatorError
   {
      RowEntry re = get_row_entry(y);
      re.add_cell(new CellEntry(c, x, y, wi));
   }
   
   public IPowerRow row_at(int y) throws LocatorError
   {
      RowEntry re = get_row_entry(y);
      return re.row_;
   }
   
   public IPowerCell cell_at(int x, int y) throws LocatorError
   {
      RowEntry re = get_row_entry(y);
      CellEntry ce = re.find_cell(x);
      
      if(ce == null)
         return null;
         
      // ...Else         
      return ce.cell_;
      
   }
   
   
   public IPowerCell left_of_border(int x, int y) throws LocatorError
   {
      RowEntry re = get_row_entry(y);
      CellEntry ce = re.find_left_of_border(x);
      
      if(ce == null)
         return null;
         
      // ...Else         
      return ce.cell_;
      
   }
   
   private RowEntry get_row_entry(int y) throws LocatorError
   {
      int index = y / row_height_;
      if(index >= rows_.length)
      {         
         String msg = "y value (" + y + ") out of range"
            + "(index=" 
            + index + ", length=" + rows_.length + ")";
                        
         throw new LocatorError(msg);
      }
            
      RowEntry result = rows_[index];
      if(result != null)
         return result;
         
      // ...Else:
      result = new RowEntry(y);
      rows_[index] = result;
      
      return result;                              
   }
   
   private static class RowEntry
   {
      public int y_;
      public IPowerRow row_;
      
      private LinkedList cells_ = new LinkedList();
      
      public RowEntry(int y)
      {
         this(null, y);
      }
      
      public RowEntry(IPowerRow row, int y)
      {
         y_ = y;
         row_ = row;
      }
      
      public void add_cell(CellEntry ce)
      {
         cells_.add(ce);
      }
      
            
      public CellEntry find_left_of_border(int x)
      {
         for(Iterator i = cells_.iterator(); i.hasNext(); )
         {
            CellEntry curr = (CellEntry) i.next();
            
            int diff = x - (curr.x_ + curr.wi_);
            if(diff <= BORDER_WIDTH && diff >= -BORDER_WIDTH)
               return curr;               
         }                  
         
         return null;
      }

      public CellEntry find_cell(int x)
      {
         for(Iterator i = cells_.iterator(); i.hasNext(); )
         {
            CellEntry curr = (CellEntry) i.next();
            
            int diff = x - curr.x_;
            if(diff >= 0 && diff < curr.wi_)
               return curr;               
         }                  
         
         return null;
      }
   }
   
   private static class CellEntry
   {
      public CellEntry(IPowerCell c, int x, int y, int wi)
      {
         cell_ = c;
         x_ = x;
         y_ = y;
         wi_ = wi;
      }
      
      public int x_;
      public int y_;
      public int wi_;
      
      public IPowerCell cell_;
   }
   
   

   public static void main(String[] args)
   {
   }
}
