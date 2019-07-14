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









package il.ac.technion.micropatterns.janeutils.impl;

import java.io.PrintStream;

public class TableBuilder
{
   private static class Cell
   {
      public String s_ = "";

      public Cell() { }

      public String toString()
      {
         return s_;
      }
      
      public Cell(int n)
      {
         this(Integer.toString(n));
      }

      public Cell(double n)
      {
         double m = Math.round(n * 10000) / 10000.0;
//         System.err.println(" Roundoff: " + n + " => " + m);
         init(Double.toString(m));
         
      }
      
      public Cell(String s)
      {
         init(s);
      }
      
      private void init(String s)
      {
         if(s == null)
            s = "";
         
         boolean is_percent = false;
         s_ = s;
         
         if(s_.endsWith("%"))
         {
            is_percent = true;
            s_ = s_.substring(0, s_.length() - 1);
         }
       
         try
         {
            s_ = fp_to_str(Double.parseDouble(s_));
         }
         catch(NumberFormatException e)
         {
         }
         
         
         try
         {
            s_ = int_to_str(Integer.parseInt(s_));
         }
         catch(NumberFormatException e)
         {
         }            
         
         if(is_percent)
            s_ = s_ + "\\%";
         
      }
      
      public static String int_to_str(int n)
      {
         StringBuffer result = new StringBuffer();

         if(n < 0)
         {
            result.append('-');
            n = -n;
         }
         
         String s = Integer.toString(n);
        
         int pos = s.length() - 1;
         for(int i = 0; i < s.length(); ++i)
         {
            result.append(s.charAt(i));
//            if(pos % 3 == 0 && pos > 0)
//               result.append(',');
               
            pos -= 1;                  
         }

         return result.toString();
      }
      
      
      public String fp_to_str(double fp)
      {
         StringBuffer result = new StringBuffer();
         
         if(fp < 0)
         {
            fp = -fp;
            result.append('-');
         }
           
         double x = Math.floor(fp);
         double y = fp - x;
                    
         String s = int_to_str((int) x);
         result.append(s);
        
         int n = (int) Math.round(y * 100);
         if(n != 0)
         {
            s = Integer.toString(n);
            if(n < 10)
              s = '0' + s;
            result.append('.' + s);
         }               

         return result.toString();                           
      }
   }
   
   private static final int LIMIT = 100;   
   private Cell[] cells_ = new Cell[LIMIT * LIMIT];
   
   private int max_col_ = -1;
   private int max_row_ = -1;
   
   private int next_col_ =0;
   private int next_row_ = -1;

   public TableBuilder()
   {
      for(int i = 0; i < cells_.length; ++i)
         cells_[i] = new Cell();
      
      new_line();
   }
   
   private TableBuilder add_cell(Cell c)
   {
      max_col_ = Math.max(max_col_, next_col_);
      put_at(next_col_, next_row_, c);
          
      next_col_ += 1;      
      return this;
   }

   public TableBuilder add_cell(String s)
   {
      return add_cell(new Cell(s));
   }
   
   public TableBuilder add_cell(double d)
   {
      return add_cell(d, false);
   }

   public TableBuilder add_cell(double d, boolean verbose)
   {
      Cell c = new Cell(d);
      if(verbose)
         System.err.println("               d=" + d + " c=" + c.s_);
      return add_cell(c);
   }
   
   public TableBuilder add_cell(int n)
   {
      return add_cell(new Cell(n));
   }
   
   public TableBuilder new_line()
   {
      next_row_ += 1;
      max_row_ = Math.max(max_row_, next_row_);

      next_col_ = 0;      
      
      return this;
   }
   
   private static int xy2index(int c, int r)
   {
      return c + r * LIMIT;
   }
   
   private Cell at(int c, int r)
   {
      return cells_[xy2index(c,r)];
   }
   
   private void put_at(int c, int r, Cell cell)
   {
//      System.err.println("placing at " + c + ", " + r + ". cell=" + cell);
      cells_[xy2index(c,r)] = cell;
   }

   public void print(PrintStream ps)
   {
      for(int r = 0; r <= max_row_; ++r)
      {
         for(int c = 0; c <= max_col_; ++c)
         {
            Cell cell = at(c,r);
            if(c > 0)
               ps.print(",");
            
            ps.print(cell.toString());
         }
                  
         ps.println();         
      }      
      ps.flush();
   }
   
   public void print_latex(PrintStream ps)
   {
      ps.println("max_row_=" + max_row_);
      ps.println("max_col_=" + max_col_);
      
      for(int r = 0; r <= max_row_; ++r)
      {
         if(r == 0)
            ps.println(" \\hline");
         
         for(int c = 0; c <= max_col_; ++c)
         {
            Cell cell = at(c,r);
            if(c > 0)
               ps.print(",");
            
            ps.print(cell.toString());
         }
                  
         ps.println(" \\\\");
         
         if(r == 0)
            ps.println(" \\hline");
         
      }
      
      ps.println(" \\hline");
      ps.flush();
   }
   
   public void print_transpose(PrintStream ps) throws Exception
   {
      for(int c = 0; c <= max_col_; ++c)
      {
         for(int r = 0; r <= max_row_; ++r)
         {
            Cell cell = at(c,r);
            if(r > 0)
               ps.print(",");            
            
            if(cell == null)
               throw new Exception("cell==null, c=" + c + ", r=" + r);
            ps.print(cell.toString());
         }

         ps.println();
      }
      ps.flush();
   }
   
   public void print_latex_transpose(PrintStream ps) throws Exception
   {
      for(int c = 0; c <= max_col_; ++c)
      {
         if(c == 0)
            ps.println(" \\hline");
         
         for(int r = 0; r <= max_row_; ++r)
         {
            Cell cell = at(c,r);
            if(r > 0)
               ps.print(",");            
            
            if(cell == null)
               throw new Exception("cell==null, c=" + c + ", r=" + r);
            ps.print(cell.toString());
         }

//         ImplUtils.read_line();
         
         ps.println(" \\\\");
         
         if(c == 0)
            ps.println(" \\hline");
      }
      
      ps.println(" \\hline");
      ps.flush();
   }
   
   public static void main(String[] args) throws Exception
   {
      TableBuilder tb = new TableBuilder();
      tb.add_cell("--").add_cell("ht1").add_cell("ht2").add_cell("ht3");
      tb.new_line();
      tb.add_cell("hl1").add_cell("d11").add_cell("d21").add_cell("d31");
      tb.new_line();
      tb.add_cell("hl2").add_cell("d12").add_cell("d22").add_cell("d32");
      
      tb.print(System.out);
      System.out.println();
      System.out.println();
      tb.print_transpose(System.out);
   }
}
