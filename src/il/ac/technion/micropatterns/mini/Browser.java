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








package il.ac.technion.micropatterns.mini;

import il.ac.technion.micropatterns.jane.app.gui.rows.DecompilerInvoker;
import il.ac.technion.micropatterns.jane.lib.ClassPathSpecifier;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

// import jima.JimaMisc;




public class Browser
{
   private static Browser app_;
   private static JFrame frame_ = new JFrame();
      
   private static final Font FONT = new Font("Courier New", Font.PLAIN, 14);
   
   private JPanel main_panel_ = new JPanel();
   private JEditorPane editor = new JEditorPane();

   private JTable tab_ = new JTable();
   private TableSorter tableModel;
   
   private JPanel decomPanel = new JPanel();
   
   private Vector jars_ = new Vector();
   private HashMap name2ptr = new HashMap();
   
   private JarEngine jarEngine;


   
   private abstract class Cmd
   {
      public abstract void execute();
   }
   
   
   private JTextField lab = new JTextField();
      
   public Browser(String fn) throws IOException
   {            
      File dir = new File(System.getProperty("user.dir")).getAbsoluteFile();
      dir = new File(dir, "jars");
      File[] files = dir.listFiles();

      ClassPathSpecifier cps = new ClassPathSpecifier();
      for(int i = 0; i < files.length; ++i)
      {
         File curr = files[i];
         if(curr.getAbsolutePath().endsWith(".jar"))
         {
            cps.add(curr.getAbsolutePath());
            jars_.add(curr.getName());
         }
      }
      
      tab_.setFont(FONT);
      tab_.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      jarEngine = new JarEngine(cps);
      
      for(Iterator i = jarEngine.allTypes(); i.hasNext(); )       
      {
         ClassPtr cp = (ClassPtr) i.next();
         name2ptr.put(cp.name, cp);
//          JimaMisc.log().println(cp.name);
      }
      
      System.out.println("cps=" + cps);      
      System.out.println("NTypes=" + name2ptr.size());      
      
      
      app_ = this;
      Container inner = frame_.getContentPane();
      
      frame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      
      frame_.getContentPane().setLayout(new BorderLayout());

      inner.add(main_panel_, BorderLayout.CENTER);
      
      main_panel_.setLayout(new BorderLayout());
      main_panel_.setSize(400, 400);
      
      tableModel = buildModel(fn);
      tab_.setModel(tableModel);
      tableModel.setTableHeader(tab_.getTableHeader());
      
//       tab_.setCellEditor(new TableCellEditor()
//          {
//             public Component getTableCellEditorComponent(JTable table, 
//                Object value, boolean isSelected, int row, int column)            
//             {
//                System.out.println(value);
//                return lab;
//             }
//          });
      
      
      JComponent scrollPane = new JScrollPane(tab_);

      decomPanel.setLayout(new BorderLayout());
      decomPanel.add(new JScrollPane(editor), BorderLayout.CENTER);

      JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
         scrollPane, decomPanel);
      splitPane.setDividerLocation(400);
      
      
      main_panel_.add(splitPane, BorderLayout.CENTER);

      frame_.pack();
      frame_.setSize(700, 600);
      frame_.setLocation(300, 80);
      
      frame_.setVisible(true);
   }
   
   private static class Record
   {
      public Record(String line)
      {
         if(line.charAt(0) == ',')
            items.add(" ");
         
         StringTokenizer st = new StringTokenizer(line, ",");
         while(st.hasMoreTokens())
         {
            String curr = st.nextToken();
            
            try
            {
               int n = Integer.parseInt(curr);
               items.add(new Integer(n));
            }
            catch (NumberFormatException e)
            {
               items.add(curr);      
            }
         }
      }
      
      private Vector items = new Vector();
         
         
      public String get(int index)
      {
         return (String) items.get(index);
      }
      
      public int size()
      {
         return items.size();
      }
      
      public Vector asVector()
      {
         return items;
      }
   }
   
   private TableSorter buildModel(String fn) throws IOException
   {
      InputStream is = System.in;
      
      if(fn != null)
         is = new FileInputStream(fn);
      
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      
      Vector records = new Vector();
      
      int nc = 0;
      
      while(true)
      {
         String line = br.readLine();
         if(line == null)
            break;
         
         line = line.trim();
         if(line.length() == 0)
            continue;
         
         Record r = new Record(line);
         nc = Math.max(nc, r.size());
         records.add(r);         
      }         
      
      DefaultTableModel dtm = new DefaultTableModel()
      {
         public boolean isCellEditable(int row, int column)
         {
            Object o = getValueAt(row,column);
            String type = o.toString().trim();
//            String curr = getValueAt(row,column).toString() + ".jar";

//            String jar = null;
//            for(int i = 0; i < getColumnCount(); ++i)
//            {
//               String curr = getValueAt(row,i).toString() + ".jar";
//               if(jars_.contains(curr))
//               {
//                  jar = curr;
//                  break;
//               }
//            }
//               
//            ClassPtr cp = new ClassPtr(type,jar);
            
            ClassPtr cp = (ClassPtr) name2ptr.get(type);
            if(cp == null)
            {
               System.out.println("Cannot find " + type);
               return false;
            }
            
            try
            {
               JavaClassRep jcr = jarEngine.classForPtr(cp);               
               DecompilerInvoker.fillEditor(editor, jcr.jc());
            }
            catch(Throwable e)
            {
               e.printStackTrace();
            }
                       
            return false; // false;
         }
      };
      
      dtm.addColumn("Ord.");
      Record head = (Record) records.get(0);
      for(int i = 0;  i < head.size(); ++i)
         dtm.addColumn(head.get(i));

      for(int i = 1; i < records.size(); ++i)
      {
         Record curr = (Record) records.get(i);
         Vector v = curr.asVector();
//          {
//             e.id_, e.name_, tod(pc.per_hour_),             
//             tod(pc.std_), tod(pc.overtime_),
//             tod(pc.gross_), tod(pc.irs_), tod(pc.social_security_),
//             tod(pc.net_) };
                  
         v.insertElementAt(new Integer(i), 0);
         dtm.addRow(v);
      }
      
      TableSorter sorter = new TableSorter(dtm);
      
      return sorter;
//      //JTable table = new JTable(new MyTableModel());         //OLD
//      JTable table = new JTable(sorter);             //NEW
//      sorter.setTableHeader(table.getTableHeader()); //ADDED THIS      
//      
//      return dtm;      
   }
   
   public static void show_message(String s)
   {
      JOptionPane.showMessageDialog(frame_, s);
   }

   public static void main(String[] args) throws Throwable
   {
      boolean showHelp = false;
      for(int i = 0; i < args.length; ++i)
      {
         if(args[i].startsWith("-"))
            showHelp = true;
      }
      
      if(showHelp)
         usage(); // Program terminates
      
      String fn = null;
      if(args.length > 0)
         fn = args[0];
      
      new Browser(fn);
   }
   
   
   private static void usage()   
   {
      System.err.println("Usage: Browser [<csv-file-name>]");
      System.err.println("       If csv-file-name is not specified," 
         + " uses stdin instead");
      System.err.println("       The program looks for classes in all *.jar"
         + " files in the ./jars/ directory");
      System.err.println();         
      System.exit(-1);
   }      
}
