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

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

public class Mutator
{
   
   public static Mutator make(Table t)
   {
      return new Mutator(t);
   }
   
   private HashSet rowsToRemove = new HashSet();
   private HashSet colsToRemove = new HashSet();

   private HashSet colsToAdd = new HashSet();
   private HashSet rowsToAdd = new HashSet();
   
   private Table table_;
   
   public Mutator(Table t)
   {
      table_ = t;
   }
   
   public Mutator mutator() throws BadReference
   {
      return result().mutator();
   }
   
   public Mutator consolidateOnCol(int col)
   {
      Hashtable ht = table_.colAsMap(col);
      
      for(int r = 0; r < table_.numRows(); ++r)
      {
         Integer otherRow = (Integer) ht.get(table_.at(col, r));
         if(otherRow.intValue() != r)
            removeRow(r);
      }
      
      return this;
   }
   
   public Mutator intersectOnCol(HashSet hs, int col)
   {
      for(int i = 0; i < table_.numRows(); ++i)
      {
         String s = table_.at(col, i);
         if(!hs.contains(s))
            removeRow(i);
      }
      return this;
   }
   
   public Mutator excludeOnCol(HashSet hs, int col)
   {
      for(int i = 0; i < table_.numRows(); ++i)
      {
         String s = table_.at(col, i);
         if(hs.contains(s))
            removeRow(i);
      }
      return this;
   }

   public Mutator insertRow(int r)
   {
      rowsToAdd.add(new Integer(r));
      return this;
   }
   
   public Mutator insertCol(int c)
   {
      colsToAdd.add(new Integer(c));
      return this;
   }
   
   public Mutator  removeRow(int r)
   {
      rowsToRemove.add(new Integer(r));
      return this;
   }
   
   public Mutator  removeCol(int c)
   {
      colsToRemove.add(new Integer(c));
      return this;
   }
   
   public Table result() throws BadReference
   {
//      Collections.sort(rowsToRemove);
//      Collections.sort(colsToRemove);
      
      
      Builder b = new Builder();
      
      int newRow = -1;
      for(int r = 0; r < table_.numRows(); ++r)
      {
         while(true)
         {
            newRow += 1;
            
            if(!rowsToAdd.contains(new Integer(newRow)))
               break;
            
            b.newLine();
         }
         
         Integer tempRow = new Integer(r);
         if(rowsToRemove.contains(tempRow))
            continue;
         
         int newCol = -1;
         for(int c = 0; c < table_.numCols(); ++c)
         {
            while(true)
            {
               newCol += 1;
               
               if(!colsToAdd.contains(new Integer(newCol)))
                  break;
               
               b.add(Table.EMPTY_STR);
            }

            Integer tempCol = new Integer(c);                        
            if(colsToRemove.contains(tempCol))
               continue;
            
            
            b.add(table_.at(c, r));
         }
         
         b.newLine();
      }
      
      return b.result();
   }
   
   
   
   
   
   /**
    * @param args
    * @throws BadReference 
    * @throws IOException 
    */
   public static void main(String[] args) throws IOException, BadReference
   {
      Table t = new Table("etc/t1.csv");
      
      Mutator m = new Mutator(t);
      m.removeCol(2);
      m.removeRow(1);
      
      t.print(System.out);
      m.result().print(System.out);
   }

}
