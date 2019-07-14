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








package il.ac.technion.micropatterns.tables;

import java.util.Iterator;
import java.util.Vector;

public class Builder
{

   private int row_ = 0;
   private int col_ = 0;
   
   private Vector entries_ = new Vector();
   
   public Builder read(Table t)
   {
      for(int r = 0; r < t.numRows(); ++r)
      {
         for(int c = 0; c < t.numCols(); ++c)
            add(t.at(c, r));

         newLine();
      }
      
      return this;
   }

   public Builder add(double value)
   {
      return add(Double.toString(value));
   }
   
   public Builder add(Table t, int row, int fromColumn)
   {
      for(int i = fromColumn; i < t.numCols(); ++i)
         add(t.at(i, row));
      
      return this;
   }
   
   public Builder add(int value)
   {
      return add(Integer.toString(value));
   }
   
   public Builder add(String s)
   {
      Entry e = new Entry(Table.str(s), col_, row_);
      col_ += 1;
      entries_.add(e);
      
      return this;
   }
   
   public Builder newLine()
   {
      col_ = 0;
      row_ += 1;
      return this;
   }
   
   public Table result() throws BadReference
   {
      Table result = new Table(entries_);
      return result;
   }
   
   public Builder transpose()
   {
      for(Iterator i = entries_.iterator(); i.hasNext(); )
      {
         Entry e = (Entry) i.next();

         int temp = e.c_;
         e.c_ = e.r_;
         e.r_ = temp;          
      }
      
      return this;
   }
   
   /**
    * @param args
    * @throws BadReference 
    */
   public static void main(String[] args) throws BadReference
   {      
      Builder b = new Builder();
      Table t = b.add(5).add(4).add(3).newLine().add(5).add(4).newLine().add(5).result();
      t.print(System.out);
   }

}
