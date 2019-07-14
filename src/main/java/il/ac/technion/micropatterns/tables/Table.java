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

import il.ac.technion.jima.JimaMisc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Hashtable;



public class Table
{   
   public static Table add(Table lhs, Table rhs) throws BadReference
   {
      if(lhs == null)
         return rhs;
      if(rhs == null)
         return lhs;
      
      return lhs.add(rhs);
   }
   
   public void printRow(int r)
   {
      for(int c = 0; c < numCols(); ++c)
      {
         if(c > 0)
            System.out.print(',');                       
         System.out.print(this.at(c,r));         
      }
      System.out.println();
   }
   
   public Mutator mutator() { return Mutator.make(this); }
   
   public static final String EMPTY_STR = "";
//   private Vector entries_ = new Vector();
   private String[] cells_;
   private int num_rows_;
   private int num_cols_;
   
   private static Hashtable strings = new Hashtable();

   public Table(String s) throws IOException, BadReference
   {
      this(new File(s));
   }

   public Table(File f) throws IOException, BadReference
   {
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      
      Vector entries = new Vector();

      int row_n = -1;
      while(true)
      {
         String line = br.readLine();
         if(line == null)
            break;
         
         line = line.trim();
         if(line.length() == 0)
            continue;
         
         if(line.startsWith(";"))
            continue;
         
         
         ++row_n;         
         processLine(line, entries, row_n);
      }

      init(entries);      
      process();
   }
   
   private void processLine(String line, Vector entries, int row_n)
   {
      
      int col_n = -1;
      
      StringTokenizer tokenizer = new StringTokenizer(line, ",");
      while(tokenizer.hasMoreTokens())
      {
         ++col_n;
         String token = tokenizer.nextToken().trim();
         token = str(token);
         
         Entry e = new Entry(token, col_n, row_n);
         entries.add(e);
      }
   }
   
   public Table(Vector entries) throws BadReference
   {
      init(entries);
      process();
   }
   
   public Vector asEntries()
   {
      Vector result = new Vector();
      for(int r = 0; r < num_rows_; ++r)
      {
         for(int c = 0; c < num_cols_; ++c)
         {
            String s = this.at(c, r);
            Entry e = new Entry(s, c, r);
            result.add(e);
         }
      }
      
      return result;
   }
   
   private void process() throws BadReference
   {
      while(true)
      {
         boolean hasRef = resolveFirstReference();
         if(!hasRef)
            break;
      }
   }
   
   private boolean resolveFirstReference() throws BadReference
   {
      for(int r = 0; r < num_rows_; ++r)
      {
         for(int c = 0; c < num_cols_; ++c)
         {
            String s = this.at(c, r);
            if(isReference(s))
            {
               substitute(s, c, r);
               return true;
            }
         }                  
      }
      
      return false;
   }
   
   private void substitute(String s, int c, int r) throws BadReference
   {
      JimaMisc.ensure(isReference(s));
      
      s = s.substring(1);
      Vector v = new Vector();
      StringTokenizer st = new StringTokenizer(s, ":");
      while(st.hasMoreTokens())
         v.add(st.nextToken());
      
      if(v.size() < 2 || v.size() > 3)
         throw new BadReference(s);
      
      try
      {
         Segment seg = null;
         
         if(v.size() == 2)
            seg = parse(s, (String) v.get(0), (String) v.get(1));
         else 
         {
            seg = parse(s, (String) v.get(0), (String) v.get(1), 
               (String) v.get(2));
         }
         
         JimaMisc.ensure(seg != null);
         this.insertAt(c, r, seg);
      }
      catch (BadReference e)
      {
         e.printStackTrace();
         throw new BadReference(s);
      }      
   }
   
   private Segment parse(String fullRef, String a, String b, String c) 
      throws BadReference
   {
      File f = new File(a);
      if(!f.exists())
         throw new BadReference("File not found: " + a);
      
      Location l1 = makeLocation(fullRef, b);
      Location l2 = makeLocation(fullRef, c);
      
      Segment s;      
      Table t = null;
      
      try
      {
         t = new Table(f);
      }
      catch (IOException e)
      {
         throw new BadReference("Cannot open file " + f);
      }
      
      if(l1.flags_ == Location.BOTH && l2.flags_ == Location.BOTH)
         s = new Segment(t, l1.col_, l1.row_, l2.col_, l2.row_);
      else if(l1.flags_ == Location.BOTH && l2.flags_ == Location.RIGHT)
         s = new Segment(t, l1.col_, l1.row_, t.num_cols_ - 1, l1.row_);
      else if(l1.flags_ == Location.BOTH && l2.flags_ == Location.LEFT)
         s = new Segment(t, l1.col_, l1.row_, 0, l1.row_);
      else if(l1.flags_ == Location.BOTH && l2.flags_ == Location.UP)
         s = new Segment(t, l1.col_, l1.row_, l1.col_, 0);
      else if(l1.flags_ == Location.BOTH && l2.flags_ == Location.DOWN)
         s = new Segment(t, l1.col_, l1.row_, l1.col_, t.num_rows_ - 1);
      else 
         throw new BadReference(fullRef);
      
      return s;
      
   }
   
   private Segment parse(String fullRef, String a, String b) 
      throws BadReference
   {
      File f = new File(a);
      if(!f.exists())
         throw new BadReference("File not found: " + a);
      
      Location l = makeLocation(fullRef, b);
      
      Segment s;      
      Table t = null;
            
      try
      {
         t = new Table(f);
      }
      catch (IOException e)
      {
         throw new BadReference("Cannot open file " + f);
      }
      
      if(l.flags_ == Location.BOTH)
         s = new Segment(t, l.col_, l.row_, l.col_, l.row_);
      else if(l.flags_ == Location.COLUMN)
         s = new Segment(t, l.col_, 0, l.col_, t.num_rows_ - 1);
      else if(l.flags_ == Location.ROW)
         s = new Segment(t, 0, l.row_, t.num_cols_ - 1, l.row_);
      else 
         throw new BadReference(fullRef);
      
      return s;
   }
   
   private static Location makeLocation(String fullRef, String loc) 
      throws BadReference
   {
      if(loc.length() == 0)
         throw new BadReference(fullRef);
      
      boolean isDigit = false;
      if(Character.isDigit(loc.charAt(0)))
         isDigit = true;
      
      boolean allSame = true;
      for(int i = 0; i < loc.length(); ++i)
      {
         char c = loc.charAt(i);
         if(isDigit && !Character.isDigit(c))
            allSame = false;
            
         if(!isDigit && Character.isDigit(c))
            allSame = false;
      }
      
      
      if(!allSame)
         return new Location(loc, Location.BOTH);
      else if(isDigit)
         return new Location(Integer.parseInt(loc));
      else // !isDigit
         return new Location(loc);
               
   }
   
   private boolean isReference(String s)
   {
      return s.startsWith("!");
   }
   
   public void insertAt(int c, int r, Segment s)
   {
      int rowCount = s.rowCount();
      int colCount = s.colCount();
      
      
//      if(rowCount == 1 && colCount == 1)
//      {
//         Iterator i = s.makeEntries(); 
//         JimaMisc.ensure(i.hasNext());
//        
//         Entry e= (Entry) i.next();         
//         this.putAt(c, r, e.s_);
//         
//         return;
//      }
      
      Vector entries = this.asEntries();
      for(Iterator i = entries.iterator(); i.hasNext(); )
      {
         Entry curr = (Entry) i.next();
         
         if(curr.c_ <= c && curr.r_ == r)
            continue;
         
         if(curr.c_ >= c)
            curr.c_ += colCount - 1;

         if(curr.r_ >= r)
            curr.r_ += rowCount - 1;         
      }
      
      init(entries);   
      
      for(Iterator i = s.makeEntries(); i.hasNext(); )
      {
         Entry curr = (Entry) i.next();
         this.putAt(c + curr.c_, r + curr.r_, curr.s_);         
      }
      
//      this.print(System.out);
//      System.out.println();
//      System.out.println();
   }
   
   public void putAt(int c, int r, int value)
   {
      putAt(c, r, Integer.toString(value));
   }
   
   public static String str(String s)
   {
      String result = (String) strings.get(s);
      if(result != null)
         return result;
      
      strings.put(s, s);
//      if(strings.size() % 1000 == 0)
//         System.out.println("strings.size() = " + strings.size());
      return s;
   }
   
   public void putAt(int c, int r, String value)
   {
      value = str(value);
      cells_[c + r * num_cols_] = value;
   }
   
   public String at(int c, int r)
   {
      return cells_[c + r * num_cols_];
   }
   
   public int intAt(int c, int r)
   {
      String temp = at(c, r);
      try
      {
         return Integer.parseInt(temp);
      }
      catch(NumberFormatException e)
      {
         JimaMisc.ensure(false, "c=" + c + ", r=" + r + ", temp=" + temp);
         return 0;
      }
   }
   
   private void init(Vector entries)
   {
      int max_row = -1;
      int max_col = -1;

      for(Iterator i = entries.iterator(); i.hasNext(); )
      {
         Entry curr = (Entry) i.next();
         max_row = Math.max(max_row, curr.r_);
         max_col = Math.max(max_col, curr.c_);
      }

      num_rows_ = max_row + 1;
      num_cols_ = max_col + 1;
      
      cells_ = new String[num_rows_ * num_cols_];
      for(int i = 0; i < cells_.length; ++i)
         cells_[i] = EMPTY_STR;
      
      for(Iterator i = entries.iterator(); i.hasNext(); )
      {
         Entry curr = (Entry) i.next();
         putAt(curr.c_, curr.r_, curr.s_);
      }
   }
   
   public PrintStream print(PrintStream ps, Table h)
   {
      h.print(ps);
      return this.print(ps);
   }
   
   public PrintStream print(PrintStream ps)
   {
      for(int i = 0; i < this.num_rows_; ++i)
      {
         for(int j = 0; j < this.num_cols_; ++j)
         {
            String s = this.at(j, i);
            if(j > 0)
               s = "," + s;
            ps.print(s);            
         }
         
         ps.println();
      }
      
      ps.flush();
      
      return ps;
   }
   
   public HashSet colAsSet(int col)
   {
      return new HashSet(colAsVector(col));
   }
   
   public Hashtable colAsMap(int col)
   {
      Hashtable  result = new Hashtable();
      for(int i = 0; i < numRows(); ++i)
         result.put(at(col, i), new Integer(i));
      
      return result;
   }
   
   public Vector colAsVector(int col)
   {
      Vector result = new Vector();
      for(int i = 0; i < numRows(); ++i)
         result.add(at(col, i));
      
      return result;
   }
   
   public int numRows()
   {
      return num_rows_;
   }
   
   public int numCols()
   {
      return num_cols_;
   }
   
   public Table rowsWhereEq(int col, String regexp) throws BadReference
   {
      RegExp re = RegExp.make(regexp);
      
      Mutator m = new Mutator(this);
      for(int r = 0; r < numRows(); ++r)
      {
         String value = at(col, r);
         if(!re.matches(value))
            m.removeRow(r);           
      }
      
      return m.result();
   }

   public Table rowsWhereEqStr(int col, String s) throws BadReference
   {
      Mutator m = new Mutator(this);
      for(int r = 0; r < numRows(); ++r)
      {
         String value = at(col, r);
         if(!s.equals(value))
            m.removeRow(r);
      }
      
      return m.result();
   }

   
   private static class RegExp
   {
      
      public static RegExp make(String regexp)
      {
         return new RegExp(regexp);
      }
      
      private Pattern p;
      public RegExp(String regexp)
      {
         p = Pattern.compile(regexp);
      }
      
      public boolean matches(String s)
      {
         Matcher m = p.matcher(s);
         boolean b = m.matches();
         return b;
      }
   }

   public Table rowsWhereNotEqStr(int col, String s) throws BadReference
   {
      Mutator m = new Mutator(this);
      for(int r = 0; r < numRows(); ++r)
      {
         String value = at(col, r);
         if(s.equals(value))
            m.removeRow(r);           
      }
      
      return m.result();
   }
   
   public Table rowsWhereNotEq(int col, String regexp) throws BadReference
   {
      RegExp re = RegExp.make(regexp);

      Mutator m = new Mutator(this);
      for(int r = 0; r < numRows(); ++r)
      {
         String value = at(col, r);
         if(re.matches(value))
            m.removeRow(r);           
      }
      
      return m.result();
   }
   
   public Table colsWhereNotEq(int row, String regexp) throws BadReference
   {
      RegExp re = RegExp.make(regexp);

      Mutator m = new Mutator(this);
      for(int c = 0; c < numCols(); ++c)
      {
         String value = at(c, row);
         if(re.matches(value))
            m.removeCol(c);           
      }
      
      return m.result();
   }
   
   
   public Table colsWhereEq(int row, String regexp) throws BadReference
   {
      RegExp re = RegExp.make(regexp);

      Mutator m = new Mutator(this);
      for(int c = 0; c < numCols(); ++c)
      {
         String value = at(c, row);
         if(!re.matches(value))
            m.removeCol(c);           
      }
      
      return m.result();
   }
   
   
   public Table add(Table that) throws BadReference
   {
      Builder b = new Builder();
      b.read(this).read(that);
      
      return b.result();     
   }

   public int indexOfColEqStr(int row, String s)
   {
      for(int c = 0; c < this.numCols(); ++c)
      {
         String value = at(c, row);
         if(s.equals(value))
            return c;
      }
      
      return -1;
   }
   
   public int indexOfColEq(int row, String regexp)
   {
      RegExp re = RegExp.make(regexp);
      
      for(int c = 0; c < this.numCols(); ++c)
      {
         String value = at(c, row);
         if(re.matches(value))
            return c;
      }
      
      return -1;
   }
   
   
   public int indexOfColNEq(int row, String regexp)
   {
      RegExp re = RegExp.make(regexp);
      
      for(int c = 0; c < this.numCols(); ++c)
      {
         String value = at(c, row);
         if(!re.matches(value))
            return c;
      }
      
      return -1;
   }
   
   public HashSet uniqueValuesAtCol(int c)
   {
      HashSet values = new HashSet();
      for(int r = 0; r < numRows(); ++r)
      {
         String v = at(c, r);
         values.add(v);
      }
      
      return values;
   }
   
   
   
   public static void main(String[] args) throws IOException, BadReference
   {
      Table t1 = new Table(new File("etc/t1.csv"));
      
      t1.print(System.out);
   }
}
