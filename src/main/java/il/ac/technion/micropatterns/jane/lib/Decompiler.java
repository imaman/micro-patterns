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








//package il.ac.technion.micropatterns.jane.lib;
//
///*
// * Created on 22/06/2004
// * Written by Itay
// * Project: JarScan
// */
//
//
//
//
//import il.ac.technion.micropatterns.jane.app.Environment;
//
//import java.awt.BorderLayout;
//import java.awt.Component;
//import java.awt.Rectangle;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.io.Reader;
//import java.io.StringWriter;
//import java.util.Iterator;
//import java.util.Stack;
//
//import javax.swing.BoxLayout;
//import javax.swing.JButton;
//import javax.swing.JEditorPane;
//import javax.swing.JFrame;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//
//import jima.JimaHack;
//import jima.JimaMisc;
//
//import org.kohsuke.gsc.sample.java.JavaColorizer;
//
//import org.apache.bcel.Repository;
//import org.apache.bcel.classfile.ConstantPool;
//import org.apache.bcel.classfile.EmptyVisitor;
//import org.apache.bcel.classfile.Field;
//import org.apache.bcel.classfile.JavaClass;
//import org.apache.bcel.classfile.Method;
//import org.apache.bcel.classfile.Synthetic;
//import org.apache.bcel.generic.ConstantPoolGen;
//import org.apache.bcel.generic.Instruction;
//
///**
// * One liner.
// * Full description.
// *
// * @author Itay
// * @since 22/06/2004
// */
//
//
//
//public class Decompiler
//{
//   private JFrame frame_;
//   private JavaClass start_class_;
//   private Stack hierarchy_;
//   private JEditorPane editor_ = new JEditorPane();
//   private JScrollPane scroll_pane_;
//   private String other_text_ = "";
//   
//   private static File jad_file_;
//   public static Environment env_;
//   
//   
//   static
//   {
//      InputStream in = null;
//      OutputStream out = null;
//      
//      try
//      {
//         File out_file = File.createTempFile("jane", ".exe");
//         out_file.deleteOnExit();
//         
//         
//         in = new BufferedInputStream(
//            Decompiler.class.getResourceAsStream("xjad.exe"));
//         
//         out = new BufferedOutputStream(new FileOutputStream(out_file));
//            
//         while(true)
//         {
//            int c = in.read();
//            if(c < 0)
//               break;
//
//            out.write(c);            
//         }
//         
//         jad_file_ = out_file;
//      }
//      catch(Throwable t)
//      {
//         JimaMisc.stop(t);
//      }  
//      finally    
//      {
//         try
//         {
//            if(out != null)
//               out.close();
//               
//            if(in != null)            
//               in.close();
//         }
//         catch(IOException e)
//         {
//            JimaMisc.stop(e);
//         }
//      }       
//   }
//   
//   public Decompiler(Iterator class_names)
//   {                    
//      int len = 0;
//      StringBuffer sb = new StringBuffer();      
//      StringBuffer names = new StringBuffer();
//      
//      while(class_names.hasNext())
//      {
//         String curr = (String) class_names.next();
//         
//         JavaClass jc = Repository.lookupClass(curr);         
//         
//         String temp = jc.getClassName() + ' ';
//         names.append(temp);
//         len += temp.length();
//         
//         if(len > 60)
//         {            
//            JimaHack.append(names, '\n');
//            len = 0;
//         }
//                              
//         sb.append(jc.toString() + sep);                  
//      }         
//      
//      init("Classes", names.toString() + sep + sb.toString());
//   }
//   
//   private static final String sep = "\n=================================\n\n";
//   
//   public Decompiler(JavaClass jc)
//   {
//      this("", "");
//      set_start_class(jc);
//   }
//   
//   public Decompiler(String title, String s)
//   {
//      init(title, s);         
//   }
//    
//   private void init(String title, String s)
//   {
//      frame_ = new JFrame("");
//      frame_.getContentPane().add(setup(s), BorderLayout.CENTER);
//   
//      frame_.pack();
//      
//      frame_.setSize(720,600);
//      
//      editor_.setEditable(false);
//      editor_.setContentType("text/html");
//   }
//   
//   
//   public JFrame as_frame()
//   {
//      return frame_;
//   }
//   
//   private Component setup(String s)
//   {
//      editor_.setText("<html>" + s + "</html>");
////      editor_.setFont(new Font("Courier New", Font.PLAIN, 16));
//
//      JPanel jp = new JPanel();
//      jp.setLayout(new BorderLayout());
//      
//      scroll_pane_ = new JScrollPane(editor_);
//      jp.add(scroll_pane_, BorderLayout.CENTER);
//      
//      
//      JPanel top = new JPanel();
//      top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
//      jp.add(top, BorderLayout.NORTH);
//      
//      JButton up = new JButton("Up");
//      top.add(up);
//      up.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               try
//               {
//                  move_up();
//               }
//               catch(Throwable t)
//               {
//                  error(t);
//               }
//            }
//         });
//
//
//      JButton down = new JButton("Down");
//      top.add(down);
//      down.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               if(hierarchy_.size() <= 1)
//                  return;
//                  
//               hierarchy_.pop();
//               try
//               {
//                  adjust();
//               }
//               catch(Throwable t)
//               {
//                  error(t);
//               }
//            }
//         });
//
//      JButton flip = new JButton("Flip");
//      top.add(flip);
//      flip.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               String temp = editor_.getText();
//               editor_.setText(other_text_);
////               ta_.repaint();
//               
//               other_text_ = temp;
//               editor_.requestFocus();
//            }
//         });
//      
//      return jp;
//   }
//   
//   public void terminate_on_exit()
//   {
//      as_frame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//   }
//   
//   public void set_start_class(JavaClass jc)
//   {
//      start_class_ = jc;
//      
//      hierarchy_ = new Stack();
//      hierarchy_.push(start_class_);
//   }
//   
//   private void move_up() throws Throwable
//   {
//      JavaClass jc = (JavaClass) hierarchy_.peek();
//      String scn = jc.getSuperclassName();
//      if(scn.equals(jc.getClassName()))
//         return;
//         
//      JavaClass par = Repository.lookupClass(scn);
//
//      hierarchy_.push(par);
//      try
//      {
//         adjust();
//      }
//      catch(Throwable t)
//      {
//         hierarchy_.pop();
//         throw t;
//      }      
//   }
//   
//   
//   private class ReadOutputTask extends Thread
//   {
//      private Process p_;
//      private Throwable error_ = null;
//      public File output_ = null;
//      
//      public ReadOutputTask(Process p)
//      {
//         p_ = p;                  
//      }
//      
//      public void run()
//      {
//         try
//         {
//            run_impl();
//         }
//         catch (IOException e)
//         {
//            error_ = e;
//         }
//      }
//
//      private void run_impl() throws IOException
//      {
//         BufferedInputStream bis = new BufferedInputStream(p_.getInputStream());
//         BufferedReader isr = new BufferedReader(new InputStreamReader(bis));
//         
//         // Copy content of isr
//         File f = File.createTempFile("jcd", ".java");
//         f.deleteOnExit();
//                  
//         PrintWriter temp = new PrintWriter(new FileWriter(f));
//         
//         String eof = "}";
//         
//         while(true)
//         {
//            String line = isr.readLine();
//            if(line == null)
//               break;
//               
//            temp.println(line); 
//            if(line.equals(eof))
//               break;               
//         }
//         
//         temp.close(); 
//         output_ = f;
//      }
//   }
//
//   private Reader run_jad(JavaClass jc) throws Throwable
//   {
//      File f = File.createTempFile("jcd", ".class");
//      jc.dump(f);
//      f.deleteOnExit();
//      
//      String[] cmdarray = new String[6];
//      cmdarray[0] = jad_file_.getAbsolutePath();
//      cmdarray[1] = "-p";
//      cmdarray[2] = "-t3";
//      cmdarray[3] = "-stat";
//      cmdarray[4] = "-ff";
//      cmdarray[5] = f.getAbsolutePath();
//      
//      
//      Process p = Runtime.getRuntime().exec(cmdarray);
//      
//      ReadOutputTask rot = new ReadOutputTask(p);
//      rot.start();
//      
//      rot.join(10 * 1000);
//      if(rot.output_ != null)
//         return new FileReader(rot.output_); // Success
//         
//      // ... Else: Failure         
//      p.destroy();         
//      
//      if(rot.error_ != null)
//         throw rot.error_;
//      else
//         throw new Exception("Decompilation process has not completed");   
//               
//   }
//      
//   public void adjust() throws Throwable
//   {      
//
//      JavaClass jc = (JavaClass) hierarchy_.peek();
//                  
//      editor_.requestFocus();
//      editor_.setSelectionStart(0);
//      editor_.setSelectionEnd(1);
//      editor_.scrollRectToVisible(new Rectangle(0,0,1,1));
//      
//      frame_.setTitle("Synopsis of: " + jc.getClassName());
//      
//      StringWriter sw = new StringWriter();
//      PrintWriter pw = new PrintWriter(sw);
//      
//      partial_decompile(pw, jc);
//      
//      other_text_ = "<html><font face=\"Courier new\" size=3 color=black>" 
//         + text_to_html(sw.toString())
//         + "</font></html>";
//      
//      Reader reader = run_jad(jc);
//      
//      StringWriter listing = new StringWriter();
//      PrintWriter lstw = new PrintWriter(listing);
//      
//                        
//      JavaColorizer jcol = new JavaColorizer(""); 
//      jcol.colorize(reader, lstw);
//               
//      String s = process_colors(listing.toString());
//      
//      JimaMisc.log().println("s=" + s);
//                        
//      editor_.setContentType("text/html");
//      editor_.setText(s);      
//      
//      editor_.requestFocus();
//   }
//   
//   private static void partial_decompile(PrintWriter pw, JavaClass jc)
//   {
//      // pw.println(jc.toString());
//      
//      ConstantPool const_pool = jc.getConstantPool();
//      ConstantPoolGen cpg = new ConstantPoolGen(const_pool);
//
//      pw.println(jc.getClassName());
//      pw.println("{");
//      
//      Field[] fields = jc.getFields();
//      for(int i = 0; i < fields.length; ++i)
//      {
//         Field curr = fields[i];
//         
//         pw.println("   " + curr);
//      }
//      pw.println();
//      pw.println();
//      
//      Method[] methods = jc.getMethods();
//      for(int i = 0; i < methods.length; ++i)
//      {
//         Method curr = methods[i];
//         
//         pw.println("   " + curr);
//         pw.println("   {");
//         InstructionParser ip = new InstructionParser(curr);
//         for(Iterator j = ip.iterator(); j.hasNext(); )
//         {
//            Instruction ins = (Instruction) j.next();            
//            pw.println("      " + ins.toString(const_pool));
//         }            
//         pw.println("   }");
//         pw.println();         
//      }
//      pw.println("}");
//      
//   }
//   
//   private static int starts_with_count(String s, String t)
//   {            
//      if(s.startsWith(t))
//         return t.length();
//
//      // ...Else:
//      return -1;         
//   }
//
//   private static final String FONT_STYLE="<FONT face=\"Courier new\" size=4 ";
//      
//   private static String process_colors(String s)
//   {
//      StringWriter result = new StringWriter();
//      PrintWriter out = new PrintWriter(result);
//
//      boolean bold = false;
//      
//      int len = s.length();      
//      for(int i = 0; i < len; ++i)
//      {
//         char c = s.charAt(i);
//         if(c != '<')
//         {
//            out.print(c);
//            continue;
//         }           
//         
//         // ...Else:
//         
//         int e = i + 40;
//         if(e >= len)
//            e = len;
//            
//         String temp = s.substring(i, e).toLowerCase();
//         
//         
//         int skip = 0;
//
//         if((skip = starts_with_count(temp, "<pre>")) >= 0)
//         {            
//            out.print("<pre>" + FONT_STYLE + "color=#000000>");
//            i += skip - 1;
//            
//            continue;
//         }           
//
//         if((skip = starts_with_count(temp, "</pre>")) >= 0)
//         {            
//            out.print("</font></pre>");
//            i += skip - 1;
//            
//            continue;
//         }           
//
//
////comment 63,127,95
////keyowrd 127,0,85
////string  42,0,255
////linenumber 255,83,0
////normal 0,0,0
////type 128,0,255
////javadoc 63,95,191
//         
//         if((skip = starts_with_count(temp, "<span class='type'>")) >= 0)
//         {            
//            out.print(FONT_STYLE + "color=#8000ff>");
//            i += skip - 1;
//            
//            continue;
//         }           
//
//         if((skip = starts_with_count(temp, "<span class='javadoc'>")) >= 0)
//         {            
//            out.print(FONT_STYLE + "color=#3f5fbf>");
//            i += skip - 1;
//            
//            continue;
//         }           
//         
//         if((skip = starts_with_count(temp, "<span class='keyword'>")) >= 0)
//         {            
//            out.print(FONT_STYLE + "color=#7f0055><b>");
//            bold = true;
//            i += skip - 1;
//            
//            continue;
//         }           
//         
//         if((skip = starts_with_count(temp, "<span class='string'>")) >= 0)
//         {
//            out.print(FONT_STYLE + "color=#2a00ff>");
//            i += skip - 1;
//            
//            continue;
//         }            
//         
//         if((skip = starts_with_count(temp, "<span class='comment'>")) >= 0)
//         {
//            out.print(FONT_STYLE + "color=#3f7f5f>");
//            i += skip - 1;
//            
//            continue;
//         }            
//         
//         if((skip = starts_with_count(temp, "<span class='linenumber'>")) >= 0)
//         {
//            out.print(FONT_STYLE + "color=#ff5300>");
//            i += skip - 1;
//            
//            continue;
//         }            
//         
//         if((skip = starts_with_count(temp, "</span>")) >= 0)
//         {
//            if(bold)
//            {               
//               out.print("</b>");
//               bold = false;
//            }
//            out.print("</font>");
//                           
//            i += skip - 1;            
//            
//            continue;
//         }            
//         
//         
//         // ...Else:
//         out.print(c);
//         
//      }
//      
//      out.close();
//      return result.toString();
//   }
//   
//   public static class Abcd extends EmptyVisitor 
//   {
//      public void visitSynthetic(Synthetic attribute)            
//      {
//         System.out.println("      Synthetic: " + attribute.toString());
//      }
//   
//      public void visitField(Field obj)
//      {
//         System.out.println("   Field: " + obj.toString());
//      }
//      
//      public void visitJavaClass(JavaClass obj) 
//      {
//         System.out.println("jc: " + obj.getClassName());
//      }
//      
//      
//   }
//   
//   
//   private static class Shower implements Runnable
//   {
//      private JFrame f_;
//      
//      public Shower(JFrame f)
//      {
//         f_ = f;
//      }
//      
//      public void run()
//      {
//         f_.setVisible(true);
//      }
//   }
//
//
//   private static String text_to_html(String text)
//   {
//      StringWriter sw = new StringWriter();
//      PrintWriter pw = new PrintWriter(sw);
//      
//      int len = text.length();
//      for(int i = 0; i < len; ++i)
//      {
//         char c = text.charAt(i);
//         if(c == '\n')
//         {
//            pw.println("<br>");
//            continue;
//         }           
//         
//         if(c == ' ')
//         {            
//            pw.print("&nbsp;");
//            continue;
//         }
//         
//         if(c == '&')
//         {
//            pw.print("&amp;");
//            continue;
//         }           
//         
//         if(c == '<')
//         {
//            pw.print("&lt;");
//            continue;
//         }           
//         
//         if(c == '>')
//         {
//            pw.print("&gt;");
//            continue;
//         }           
//         
//         pw.print(c);
//      }
//      
//      pw.close();
//      return sw.toString();
//   }
//   
//   public void error(Throwable t)
//   {
//      t.printStackTrace();
//      JOptionPane.showMessageDialog(this.as_frame(), t.getMessage());      
//   }
//   
//   public static void show(JavaClass jc)
//   {
//      Environment.instance().state().touched_types().add(jc.getClassName());
//      
//      Decompiler v = new Decompiler(jc);
//
//      try
//      {
//         v.adjust();
//         v.as_frame().setVisible(true);               
//      }
//      catch(Throwable t)
//      {
//         v.error(t);
//         v.as_frame().setVisible(false);
//      }         
//   }
//}
