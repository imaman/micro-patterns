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








package il.ac.technion.micropatterns.stats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.tables.BadReference;
import il.ac.technion.micropatterns.tables.Builder;
import il.ac.technion.micropatterns.tables.Mutator;
import il.ac.technion.micropatterns.tables.Table;

public class Core
{
   public int[] patts;
   
   public Table apps;
   public Table jres;
   public Table all;
   
   public Collections colls = new Collections();
   
   public int col_cbox;
//   public int col_tmpl;
   public int col_box;
   public int col_flyw;
   public int col_imbx;
   public int col_immu;
   public int col_impl;
   public int col_intr;
   public int col_pcls;
   public int col_pool;
   public int col_rscr;
   public int col_reim;
   public int col_sink;
   public int col_stls;
   public int col_mono;
   public int col_tmpl;
   public int col_fptr;
   public int col_fobj;   
   public int col_join;
   public int col_dsgn;
   public int col_rcrd;
   public int col_txnm;
   public int col_type;   
   public int col_enum;
   public int col_extn;
   public int col_data;
   public int col_mold;
   public int col_algo;
   public int col_stdy;

   public int col_package;
   public int col_name;
   public int col_fqn;
   public int col_methods;
   public int col_isinterface;
   public int col_lib;
   
   public int col_dsb;
   public int col_base;
   public int col_ds;
   public int col_inheritors;
   public int col_db;
   public int col_dataman;
   public int col_wrap;
   public int col_controlled;

   private Table in;
   private File outputDir;
   public Table head; 
   
   public Core(String inputFile, String outputDir) 
      throws IOException, BadReference
   {
      this(new Table(inputFile), outputDir);
   }
   
  
   public Core(Table inputTable, String outputDir) throws BadReference
   {
      this.in = inputTable;
      if(outputDir == null)
         outputDir = System.getProperty("user.dir");
      
      this.outputDir = new File(outputDir);

      all = Mutator.make(in).insertCol(0).insertCol(1).insertCol(6)
         .insertCol(7).insertCol(8).insertCol(9).insertCol(10)
         .insertCol(11).insertCol(12).insertCol(13).result();

      col_fqn = findCol(all, "C/P:*");
      col_methods = findCol(all, "Methods");
      col_isinterface = findCol(all, "^IsInterface.*");
      col_lib = findCol(all, "Collection");
      
      head = all.rowsWhereEq(col_fqn, "C/P:").mutator()
         .consolidateOnCol(col_fqn).result();
      head.printRow(0);
      
      all = all.rowsWhereNotEq(col_fqn, "C/P:");
      
      
      col_package = 0;
      head.putAt(col_package, 0, "Package");
      
      col_name = 1;
      head.putAt(col_name, 0, "name");
      
      col_dsb = 6;
      head.putAt(col_dsb, 0, "dSB");

      col_base = 7;      
      head.putAt(col_base, 0, "bASEcLASSES");

      col_ds = 8;      
      head.putAt(col_ds, 0, "dS");
      
      col_inheritors = 9;      
      head.putAt(col_inheritors, 0, "iNHERITORS");
      
      col_wrap = 10;  
      head.putAt(col_wrap, 0, "wRAP");
      
      col_dataman = 11;      
      head.putAt(col_dataman, 0, "dATAMAN");
      
      col_db = 12;      
      head.putAt(col_db, 0, "dB");
      
      col_controlled = 13;      
      head.putAt(col_controlled, 0, "cONTROLLED");
      
      
      head.printRow(0);

      patts = new int[]
         {
            col_cbox = findCol(head, "\\\\BCBOX"),      
            col_box = findCol(head, "\\\\BBOX"),
            col_flyw = findCol(head, "\\\\BFLYW"),
            col_imbx = findCol(head, "\\\\BIMBX"),
            col_immu = findCol(head, "\\\\BIMMU"),
            col_impl = findCol(head, "\\\\BIMPL"),
            col_intr = findCol(head, "\\\\BINTR"),
            col_pcls = findCol(head, "\\\\BPCLS"),
            col_pool = findCol(head, "\\\\BPOOL"),
            col_rscr = findCol(head, "\\\\BRSCR"),
            col_reim = findCol(head, "\\\\BREIM"),
            col_sink = findCol(head, "\\\\BSINK"),
            col_stls = findCol(head, "\\\\BSTLS"),
            col_mono = findCol(head, "\\\\BMONO"),
            col_tmpl = findCol(head, "\\\\BTMPL"),
            col_fptr = findCol(head, "\\\\BFPTR"),
            col_fobj = findCol(head, "\\\\BFOBJ"),   
            col_join = findCol(head, "\\\\BJOIN"),
            col_dsgn = findCol(head, "\\\\BDSGN"),
            col_rcrd = findCol(head, "\\\\BRCRD"),
            col_txnm = findCol(head, "\\\\BTXNM"),
            col_type = findCol(head, "\\\\BTYPE"),   
            col_enum = findCol(head, "\\\\BENUM"),
            col_extn = findCol(head, "\\\\BEXTN"),
            col_data = findCol(head, "\\\\BDATA"),
            col_mold = findCol(head, "\\\\BMOLD"),
            col_algo = findCol(head, "\\\\BALGO"),
            col_stdy = findCol(head, "\\\\BSTDY"),
         };
      JimaMisc.ensure(col_box == 14, "col_box=" + col_box);
         

            
      for(int i = 1; i < all.numRows(); ++i)
      {
         String s = all.at(col_fqn, i);         
         int pos = s.lastIndexOf('.');
         
         if(pos < 0)
         {
            all.putAt(col_package, i, "");
            all.putAt(col_name, i, s);           
         }
         else
         {         
            all.putAt(col_package, i, s.substring(0, pos));
            all.putAt(col_name, i, s.substring(pos+1));
         }
      }
      
      make_all(all);

   }
   
   
   public Builder calculatePrevalence(boolean doAll) throws BadReference
   {
      HashSet libs = all.uniqueValuesAtCol(col_lib);

      Builder b = newResultBuilder();
      
      for(Iterator i = libs.iterator(); i.hasNext(); )
      {
         String name = (String) i.next();
         Table table = findRows(all, name);
         print_prevelance(b, table);
      }
      
      if(doAll)
         print_prevelance(b, all, "all");
      
      return b;
   }

   public Builder calculateCount(boolean doAll) throws BadReference
   {
      return calculateCount(all, doAll);
   }
   
   public Builder calculateCount(Table src, boolean doAll) throws BadReference
   {
      HashSet libs = src.uniqueValuesAtCol(col_lib);

      Builder b = newResultBuilder();     
      for(Iterator i = libs.iterator(); i.hasNext(); )
      {
         String name = (String) i.next();
         Table table = findRowsStr(src, name);
         print_count(b, table);
      }
      
      if(doAll)
         print_count(b, src);
      
      return b;
   }
   
   public Table calculateArticleResults() 
      throws BadReference, FileNotFoundException
   {   
      jres = findRows(all,
         "\\\\SUNa|\\\\SUNA|\\\\SUNB|\\\\SUNC|\\\\SUND|\\\\KAFFEA|\\\\KAFFEB");
      
      apps = findRows(all,
         "\\\\ANT|\\\\JBOSS|\\\\JEDIT|\\\\MJC|\\\\POSEIDON|"
         +"\\\\SCALA|\\\\TOMCAT|\\\\SUND");

      Vector v = apps.colAsVector(col_fqn);
      java.util.Collections.sort(v);
      
      
      HashSet hs = new HashSet();
      for(int i = 1; i < v.size(); ++i)
      {
         if(v.get(i).equals(v.get(i-1)))
            hs.add(v.get(i));            
      }
      
      Table temp = Mutator.make(apps).excludeOnCol(hs, col_fqn).result();      
      Table shared = apps.mutator().intersectOnCol(hs, col_fqn).mutator().consolidateOnCol(col_fqn).result();
      
      for(int i = 0; i < shared.numRows(); ++i)
         shared.putAt(col_lib, i, "\\SHARED");
      
      System.out.println("shared.rows=" + shared.numRows());
      System.out.println("temp.rows=" + temp.numRows());
      
      System.out.println("shared unique=" + shared.uniqueValuesAtCol(col_fqn).size());
      apps = temp.add(shared);
      
      
      apps.print(newFile("apps.csv"), head);
      System.out.println("apps.rows=" + apps.numRows());
      
      
      Builder b1 = calculateCount(apps, true);
      b1.transpose().result().print(newFile("apps.count.csv"));
      
      
      colls.ANT = findRows(apps, "\\\\ANT");
      colls.JBOSS = findRows(apps, "\\\\JBOSS");
      colls.JEDIT = findRows(apps, "\\\\JEDIT");
      colls.MJC = findRows(apps, "\\\\MJC");
      colls.POSEIDON = findRows(apps, "\\\\POSEIDON");
      colls.SCALA = findRows(apps, "\\\\SCALA");
      colls.TOMCAT = findRows(apps, "\\\\TOMCAT");
      colls.SHARED = findRows(apps, "\\\\SHARED");
      colls.SUND_TAG = findRows(apps, "\\\\SUND");

      colls.KAFFEA = findRows(jres, "\\\\KAFFEA");
      colls.KAFFEB = findRows(jres, "\\\\KAFFEB");
      colls.SUNA = findRows(jres, "\\\\SUNA");
      colls.SUNa = findRows(jres, "\\\\SUNa");
      colls.SUNB = findRows(jres, "\\\\SUNB");
      colls.SUNC = findRows(jres, "\\\\SUNC");
      colls.SUND = findRows(jres, "\\\\SUND");
      
      Builder b = newResultBuilder();
            
      print_prevelance(b, colls.SUND_TAG);
      print_prevelance(b, colls.SCALA);
      print_prevelance(b, colls.MJC);
      print_prevelance(b, colls.JEDIT);
      print_prevelance(b, colls.TOMCAT);
      print_prevelance(b, colls.ANT);
      print_prevelance(b, colls.POSEIDON);
      print_prevelance(b, colls.JBOSS);
      print_prevelance(b, colls.SHARED);
      print_prevelance(b, apps);
      
      Table out = b.transpose().result();
      out.print(newFile("out-apps.csv"));
      

      b = newResultBuilder();

      print_prevelance(b, colls.SUNa);
      print_prevelance(b, colls.SUNA);
      print_prevelance(b, colls.SUNB);
      print_prevelance(b, colls.SUNC);
      print_prevelance(b, colls.SUND);
      print_prevelance(b, colls.KAFFEA);
      print_prevelance(b, colls.KAFFEB);
      print_prevelance(b, jres);
      
      return b.transpose().result();
   }
   
   public Builder newResultBuilder()
   {
      return new Builder().add("").add(head, 0, col_dsb).newLine();      
   }

   
   public void calculateResults() throws FileNotFoundException, BadReference
   {
      Table b = calculateArticleResults();
      b.print(newFile("out-jres.csv"));
      
      System.out.println("app ds. classes=" + apps.uniqueValuesAtCol(col_fqn).size());
      System.out.println("app ds. packages=" + apps.uniqueValuesAtCol(col_package).size());
      
//      System.out.println("jre ds. classes=" + jres.numRows());
      System.out.println("all ds. classes=" + all.numRows());
   }
   
   private void make_all(Table t)
   {
      make_dsb(t);
      make_base(t);
      make_ds(t);
      make_inher(t);
      make_wrap(t);
      make_db(t);
      make_dataman(t);
      make_controlled(t);      
   }
   
   private PrintStream newFile(String name) throws FileNotFoundException
   {
      FileOutputStream fos = new FileOutputStream(new File(outputDir, name));
      return new PrintStream(fos);
   }
   
   private void make_dsb(Table t)
   {
      for(int i = 0; i < t.numRows(); ++i)
      {
         int n = t.intAt(col_dsgn, i) + t.intAt(col_txnm, i) + t.intAt(col_join, i) 
            + t.intAt(col_pool, i) + t.intAt(col_stdy, i) + t.intAt(col_type, i)
            + t.intAt(col_enum, i) + t.intAt(col_pcls, i);
         
         if(n != 0) 
            n = 1;
         
         t.putAt(col_dsb, i, n);
      }
   }
   
   
   private void make_base(Table t)
   {
      for(int i = 0; i < t.numRows(); ++i)
      {
         int n = t.intAt(col_tmpl, i) + t.intAt(col_mold, i)
            + t.intAt(col_stdy, i) + t.intAt(col_type, i)
            + t.intAt(col_enum, i) + t.intAt(col_pcls, i);
         
         if(n != 0) 
            n = 1;
         
         t.putAt(col_base, i, n);
      }
   }
   

   private void make_ds(Table t)
   {
      for(int i = 0; i < t.numRows(); ++i)
      {
         int n = t.intAt(col_mold, i)
            + t.intAt(col_stls, i) 
            + t.intAt(col_mono, i)
            + t.intAt(col_imbx, i)
            + t.intAt(col_immu, i);
         
         if(n != 0) 
            n = 1;
         
         t.putAt(col_ds, i, n);
      }
   }

   private void make_inher(Table t)
   {
      for(int i = 0; i < t.numRows(); ++i)
      {
         int n = 
              t.intAt(col_impl, i)
            + t.intAt(col_extn, i) 
            + t.intAt(col_reim, i);
         
         if(n != 0) 
            n = 1;
         
         t.putAt(col_inheritors, i, n);
      }
   }
   
   private void make_wrap(Table t)
   {
      for(int i = 0; i < t.numRows(); ++i)
      {
         int n = 
              t.intAt(col_box, i)
            + t.intAt(col_cbox, i); 
         
         if(n != 0) 
            n = 1;
         
         t.putAt(col_wrap, i, n);
      }
   }
   
   private void make_db(Table t)
   {
      for(int i = 0; i < t.numRows(); ++i)
      {
         int n = t.intAt(col_fobj, i)
           + t.intAt(col_fptr, i)
           + t.intAt(col_algo, i)
           + t.intAt(col_rcrd, i)
           + t.intAt(col_data, i);
         
         if(n != 0) 
            n = 1;
         
         t.putAt(col_db, i, n);
      }
   }
   

   private void make_dataman(Table t)
   {
      for(int i = 0; i < t.numRows(); ++i)
      {
         int n = t.intAt(col_sink, i)
           + t.intAt(col_rcrd, i)
           + t.intAt(col_data, i);
         
         if(n != 0) 
            n = 1;
         
         t.putAt(col_dataman, i, n);
      }
   }

   private void make_controlled(Table t)
   {
      for(int i = 0; i < t.numRows(); ++i)
      {
         int n = t.intAt(col_rscr, i)
           + t.intAt(col_flyw, i);
         
         if(n != 0) 
            n = 1;
         
         t.putAt(col_controlled, i, n);
      }
   }
   
   
   private void print_count(Builder b, Table t)
   {
      b.add(t.at(col_lib, 0));
      for(int i = col_dsb; i <= col_stdy; ++i)
         b.add(find_count(t, i));
      
      b.newLine();
   }
   
   private int find_count(Table t, int column)
   {
      int count = 0;
      for(int r = 0; r < t.numRows(); ++ r)
         count += t.intAt(column, r);
      
      return count;
   }
   

   private void print_prevelance(Builder b, Table t)
   {
      print_prevelance(b, t, t.at(col_lib, 0));
   }
   
   private void print_prevelance(Builder b, Table t, String name)
   {
      b.add(name);
      for(int i = col_dsb; i <= col_stdy; ++i)
         b.add(find_prevelance(t, i));
      
      b.newLine();
   }
   
   
   private static final double FACTOR = 10000.0; // 4 dceimal digits
   
   private double find_prevelance(Table t, int column)
   {
      int count = find_count(t, column);
      int temp = (int) (FACTOR * count) / t.numRows();
      return temp / FACTOR;      
   }

   private Table findRowsStr(Table t, String s) throws BadReference
   {
      Table result = t.rowsWhereEqStr(col_lib, s);
      JimaMisc.ensure(result.numRows() > 0, s);
      
      return result;
   }
   
   private Table findRows(Table t, String regexp) throws BadReference
   {
      Table result = t.rowsWhereEq(col_lib, regexp);
      JimaMisc.ensure(result.numRows() > 0, regexp);
      
      return result;
   }
   
   private int findCol(Table t, String s)
   {
      int result = t.indexOfColEq(0, s);
      if(result >= 0)
         return result;
      
      // ...Else:
      if(s.startsWith("\\\\B"))
      {
         s = "\\\\" + s.substring(3);
         result = t.indexOfColEq(0, s);         
      }
      
      JimaMisc.ensure(result >= 0, s);      
      return result;        
   }
   
   /**
    * @param args
    * @throws BadReference 
    * @throws IOException 
    */
   public static void main(String[] args) throws IOException, BadReference
   {  
      String in = Env.fileName("vectors.csv");
      String out = Env.fileName("jars");
      System.out.println("Starting anlysis.");
      System.out.println("   Input file: " + in);
      System.out.println("   Output dir: " + out);
      
      Core core = new Core(in, out);
      core.calculateResults();
   }

}
