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








package il.ac.technion.micropatterns.jane.lib.decompiler;





import il.ac.technion.jima.JimaHack;
import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.app.Environment;
import il.ac.technion.micropatterns.jane.lib.InstructionParser;
import il.ac.technion.micropatterns.jane.lib.JaneMisc;
import il.ac.technion.micropatterns.janeutils.Foreign;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.generic.Instruction;
//import org.kohsuke.gsc.sample.java.JavaColorizer;




public class DecompilerImpl
{
   private static final String LOC_TOP = "line1";
   
   private static IBytecodeHighligther highlighter_ 
      = IBytecodeHighligther.INVOKE_VIRTUAL;

   private JFrame frame_;
   private JavaClass start_class_;
   private Stack hierarchy_;
   private JEditorPane editor_ = new JEditorPane();
   private JScrollPane scroll_pane_;
   
   private String java_src_text_ = "";
   private String bytecode_text_ = "";
   private int selected_text_ = 0;
   
   private Iterator search_iter_;
   private String search_string_;
      
   private static File x_jad_file_;
   public static Environment env_;
      
   static   
   {
      InputStream in = null;
      OutputStream out = null;
      
      try
      {
         File out_file = File.createTempFile("jane", ".exe");
         out_file.deleteOnExit();
                  
            
         boolean isWindows = JaneMisc.isWindows();
         InputStream is = DecompilerImpl.class.getResourceAsStream("jad.exe");
         if(is != null && isWindows) 
         {
            in = new BufferedInputStream(is);            
            out = new BufferedOutputStream(new FileOutputStream(out_file));
               
            while(true)
            {
               int c = in.read();
               if(c < 0)
                  break;

               out.write(c);            
            }
            
            x_jad_file_ = out_file.getAbsoluteFile();    
            out.flush();
         }
         else
         {
            x_jad_file_ = new File(Environment.instance().jad_);
         }         
      }
      catch(Throwable t)
      {
         JimaMisc.stop(t);
      }  
      finally    
      {
         try
         {
            if(out != null)
               out.close();
               
            if(in != null)            
               in.close();
         }
         catch(IOException e)
         {
            JimaMisc.stop(e);
         }
      }       
   }
   
   private static final String ERROR_MESSAGE = "Inconcieveable";

   private TextLocation char_index_to_loc(int pos) throws Exception
   {
      int begin = java_src_text_.indexOf(LINE_START, pos);
      if(begin < 0)
         throw new Exception(ERROR_MESSAGE);
         
      begin += LINE_START.length();      
      int end = java_src_text_.indexOf("'>", begin);
      if(end < 0)
         throw new Exception(ERROR_MESSAGE);
      
      String ln = java_src_text_.substring(begin, end);

      TextLocation result = new TextLocation(Integer.parseInt(ln) - 1,
         begin, end);
      return result; 
   }
   
   private class TextLocation
   {
      public int ln_;
      public int begin_index_;
      public int end_index_;

      public TextLocation(int ln)
      {
         this(ln, 0, -1);
      }

      public TextLocation(int ln, int begin_index, int end_index)
      {
         ln_ = ln;
         begin_index_ = begin_index;
         end_index_ = end_index;
      }
   }
   
   private class MyIter implements Iterator
   {
      private String s_;
      private int pos_ = -1;
      private TextLocation loc_ = null;
      
      public MyIter(String s)
      {
         s_ = s;
         move();
      }
      
            
      /**
       * @see java.util.Iterator#hasNext()
       */
      public boolean hasNext()
      {
         return pos_ >= 0;
      }
      
      private void move()
      {
         pos_ = java_src_text_.indexOf(s_, pos_ + 1);
         try
         {
            loc_ = char_index_to_loc(pos_);
         }
         catch (Exception e)
         {
            pos_ = -1;
         }
      }

      /**
       * @see java.util.Iterator#next()
       */
      public Object next()
      {
         TextLocation result = loc_;
         move();
                  
         return result;
      }

      /**
       * @see java.util.Iterator#remove()
       */
      public void remove()
      {
         JimaMisc.ensure(false);         
      }
   }   
   
   public DecompilerImpl(Iterator class_names)
   {                    
      int len = 0;
      StringBuffer sb = new StringBuffer();      
      StringBuffer names = new StringBuffer();
      
      while(class_names.hasNext())
      {
         String curr = (String) class_names.next();
         
         JavaClass jc = Foreign.lookupClass(curr);         
         
         String temp = jc.getClassName() + ' ';
         names.append(temp);
         len += temp.length();
         
         if(len > 60)
         {            
            JimaHack.append(names, '\n');
            len = 0;
         }
                              
         sb.append(jc.toString() + sep);                  
      }         
      
      init("Classes", names.toString() + sep + sb.toString());
   }
   
   private static final String sep = "\n=================================\n\n";

   public DecompilerImpl(JavaClass jc)
   {
      this(jc, null);
   }
   
   public DecompilerImpl(JavaClass jc, JPanel parent)
   {
      this("", "", parent);
      set_start_class(jc);
   }
   
   public DecompilerImpl(String title, String s, JPanel parent)
   {
      init(title, s, parent);
   }

   private void init(String title, String s)
   {
      init(title, s, null);
   }
   
   private void init(String title, String s, Container outer)
   {
      Container p = null;
      
      if(outer == null)
      {
         frame_ = new JFrame("");
         outer = frame_.getContentPane();
         outer.setLayout(new BorderLayout());
         p = outer;
      }
      else
      {
         outer.add(p,BorderLayout.CENTER);
         p = new JPanel();
         frame_ = null;
      }
      
      
      init(p, s);
   
      if(frame_ != null)
      {
         frame_.pack();      
         frame_.setSize(720,600);
      }      
   }

   private void init(Container parent, String s)
   {
      Component panel = setup(s);
      parent.add(panel, BorderLayout.CENTER);   

      editor_.setEditable(false);
      editor_.setContentType("text/html");   
   }
   
   private void start_search(String s)
   {
      search_string_ = s;
      search_iter_ = new MyIter(s);      
   }
   

   private static final String LINE_START = "a name='line";

//   public void go_to_method(String method_name)
//   {
//      String s = java_src_text_;
//      
//      String q = " " + method_name + "(";
//
//      int temp = s.indexOf(q);
//      if(temp < 0)
//         return;
//         
//      int begin = s.indexOf(LINE_START, temp);
//      if(begin < 0)
//         return;
//         
//      begin += LINE_START.length();      
//      int end = s.indexOf("'>", begin);
//      if(end < 0)
//         return;
//      
//      String ln = s.substring(begin, end);
//      int target_ln = Integer.parseInt(ln);
//            
//      scroll_to_loc(target_ln);
//   }
   
   private class ScrollCmd implements Runnable
   {
      private TextLocation loc_;
      
      public ScrollCmd(TextLocation loc)
      {
         loc_ = loc;
      }
      
      public void run()
      {         
//         editor_.select(loc_.begin_index_, loc_.end_index_);

         String s = "line" + loc_.ln_;
         editor_.scrollToReference(s);
         
         JimaMisc.log().println("Scrolling to: ln=" + loc_.ln_ + ", range=" 
            + loc_.begin_index_ + ".." + loc_.end_index_);
            
//         try
//         {
//            editor_.getHighlighter().addHighlight(loc_.begin_index_, 
//               loc_.end_index_, DefaultHighlighter.DefaultPainter);
//         }
//         catch (BadLocationException e)
//         {
//            JimaMisc.log().println(e);
//         }
      }
   }
   
      
   public void scroll_to_loc(TextLocation loc)
   {
      SwingUtilities.invokeLater(new ScrollCmd(loc));
   }
   
   public void scroll_to_loc(int ln)
   {
      scroll_to_loc(new TextLocation(ln)); // "line" + ln);
   }
   
   public JFrame as_frame()
   {
      return frame_;
   }
   
   private Component setup(String s)
   {
      editor_.setText("<html>" + s + "</html>");
//      editor_.setFont(new Font("Courier New", Font.PLAIN, 16));

      JMenuBar main_menu = new JMenuBar();
      
      JMenuItem edit_menu = new JMenu("Edit");
      edit_menu.setMnemonic(KeyEvent.VK_E);
      main_menu.add(edit_menu);        


      JMenuItem item = new JMenuItem("Find...", KeyEvent.VK_F);
      edit_menu.add(item);            
      item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, 
         ActionEvent.CTRL_MASK));        
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               String s = (String) JOptionPane.showInputDialog(frame_, 
                  "Find what:", "Find",  JOptionPane.QUESTION_MESSAGE, 
                  null, null, search_string_);
               
               if(s == null)
                  return;
                  
               start_search(s);
               find_next();
            }            
         });

      item = new JMenuItem("Find next", KeyEvent.VK_N);
      edit_menu.add(item);            
      item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               find_next();
            }            
         });
         
      if(frame_ != null)
         frame_.setJMenuBar(main_menu);
         


      JPanel jp = new JPanel();
      jp.setLayout(new BorderLayout());
      
      scroll_pane_ = new JScrollPane(editor_);
      jp.add(scroll_pane_, BorderLayout.CENTER);
      
      
      JPanel top = new JPanel();
      top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
      jp.add(top, BorderLayout.NORTH);
      
      JButton up = new JButton("Up");
      top.add(up);
      up.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               try
               {
                  move_up();
               }
               catch(Throwable t)
               {
                  error(t);
               }
            }
         });


      JButton down = new JButton("Down");
      top.add(down);
      down.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               if(hierarchy_.size() <= 1)
                  return;
                  
               hierarchy_.pop();
               try
               {
                  adjust();
               }
               catch(Throwable t)
               {
                  error(t);
               }
            }
         });

      JButton flip = new JButton("Flip");
      top.add(flip);
      flip.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               do_flip();
            }
         });
         
      return jp;
   }
   
   private void find_next()
   {
      if(search_iter_ == null)
         return;
         
      if(!search_iter_.hasNext())                  
         return;
         
      TextLocation loc = (TextLocation) search_iter_.next();
      scroll_to_loc(loc);
   }
   
   private void do_flip()
   {
      selected_text_ = 1 - selected_text_;
      if(selected_text_ == 1)
         editor_.setText(bytecode_text_);
      else
         editor_.setText(java_src_text_);
         
      editor_.requestFocus();
   }
   
   public void terminate_on_exit()
   {
      as_frame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }
   
   public void set_start_class(JavaClass jc)
   {
      start_class_ = jc;
      
      hierarchy_ = new Stack();
      hierarchy_.push(start_class_);
   }
   
   private void move_up() throws Throwable
   {
      JavaClass jc = (JavaClass) hierarchy_.peek();
      String scn = jc.getSuperclassName();
      if(scn.equals(jc.getClassName()))
         return;
         
      JavaClass par = Repository.lookupClass(scn);

      hierarchy_.push(par);
      try
      {
         adjust();
      }
      catch(Throwable t)
      {
         hierarchy_.pop();
         throw t;
      }      
   }
   
   
   private static class ReadOutputTask extends Thread
   {
      private Process p_;
      private Throwable error_ = null;
      public File output_ = null;
      
      public ReadOutputTask(Process p)
      {
         p_ = p;                  
      }
      
      public void run()
      {
         try
         {
            run_impl();
         }
         catch (IOException e)
         {
            error_ = e;
         }
      }

      private void run_impl() throws IOException
      {
         BufferedInputStream bis = new BufferedInputStream(p_.getInputStream());
         BufferedReader isr = new BufferedReader(new InputStreamReader(bis));
         
         // Copy content of isr
         File f = File.createTempFile("jcd", ".java");
         f.deleteOnExit();
                  
         PrintWriter temp = new PrintWriter(new FileWriter(f));
         
         String eof = "}";
         
         while(true)
         {
            String line = isr.readLine();
            if(line == null)
               break;
               
            temp.println(line); 
            if(line.equals(eof))
               break;               
         }
         
         temp.close(); 
         output_ = f;
      }
   }
   
   private static class DecompilationFaiulure extends Exception { }
   

   private static Reader run_jad(JavaClass jc) throws Throwable
   {
      File f = File.createTempFile("jcd", ".class");
      jc.dump(f);
      f.deleteOnExit();
      
      String[] cmdarray = new String[6];
      cmdarray[0] = x_jad_file_.getPath();
      cmdarray[1] = "-p";
      cmdarray[2] = "-t3";
      cmdarray[3] = "-stat";
      cmdarray[4] = "-ff";
      cmdarray[5] = f.getAbsolutePath();
      
      
      String fail = null;
      Process p = null;
      ReadOutputTask rot = null;
      try
      {
         p = Runtime.getRuntime().exec(cmdarray);
         
         rot = new ReadOutputTask(p);
         rot.start();
         
         rot.join(10 * 1000);
         if(rot.output_ != null)
            return new FileReader(rot.output_); // Success
      }
      catch(Throwable t) 
      {
         t.printStackTrace(JimaMisc.log());
         fail = t.getMessage();
      }
            
      // ... Else: Failure
      if(p != null)
         p.destroy();         

      String s = "Decompilation command= [";
      for(int i = 0; i < cmdarray.length; ++i) {
         if(i > 0)
            s += ",";
         s += cmdarray[i];
      }
      s += "] ";
      
      if(fail != null)
         s += " error: " + fail;
      else if(rot != null && rot.error_ != null)
         s += " error: " + rot.error_.getMessage();
      else
         s += " not completed";
      
      JimaMisc.log().println(s);  
      
      throw new DecompilationFaiulure();
   }
      
   public void adjust() throws Throwable
   {      

      JavaClass jc = (JavaClass) hierarchy_.peek();
                  
      
      if(frame_ != null)
         frame_.setTitle("Source code: " + jc.getClassName());
      
      
      DecompilationResult result = fillEditor(editor_, jc);
      java_src_text_ = result.java;
      bytecode_text_ = result.bytecode;
      
      scroll_to_loc(1);
      selected_text_ = 0;
      
   }
   
   
   private static class DecompilationResult 
   {
      public String java;
      public String bytecode;
      
      public DecompilationResult() { this("", ""); }
      
      public DecompilationResult(String java, String bytecode)
      {
         this.java = java;
         this.bytecode = bytecode;
      }
   }
   
   public static DecompilationResult fillEditor(JEditorPane editor, 
      JavaClass jc) throws Throwable
   {    
      editor.requestFocus();
      editor.setSelectionStart(0);
      editor.setSelectionEnd(1);
      editor.scrollRectToVisible(new Rectangle(0,0,1,1));
      
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      
      DecompilationResult result = new DecompilationResult();
      
      partial_decompile(pw, jc);
      
      result.bytecode = "<html><font face=\"Courier new\" size=3 color=black>" 
         + text_to_html(sw.toString())
         + "</font></html>";
      
      result.java = result.bytecode;

      editor.setContentType("text/html");
      editor.setText(result.java);
      
      
      editor.requestFocus();
      
      return result;
      
   }
   
   
   private static String decorate(String decoration, String text)
   {
      if(decoration == null)
         return text;

      return "\\&" + decoration + "\\&" + text + "\\&</font>\\&";
      
   }
   
   private static void partial_decompile(PrintWriter pw, JavaClass jc) 
      throws Exception
   {
      // pw.println(jc.toString());
      
      ConstantPool const_pool = jc.getConstantPool();

      pw.println(jc.getClassName());
      pw.println("{");
      
      Field[] fields = jc.getFields();
      for(int i = 0; i < fields.length; ++i)
      {
         Field curr = fields[i];
         
         pw.println("   " + curr);
      }
      pw.println();
      pw.println();
      
      Method[] methods = jc.getMethods();
      for(int i = 0; i < methods.length; ++i)
      {
         Method curr = methods[i];
         
         pw.println("   "  
            + decorate(highlighter_.decorate(curr), curr.toString()));
         
         pw.println("   {");
         InstructionParser ip = new InstructionParser(curr);
         for(Iterator j = ip.iterator(); j.hasNext(); )
         {
            Instruction ins = (Instruction) j.next();            
            
            String str = decorate(highlighter_.decorate(ins), 
               ins.toString(const_pool));

            pw.println("      " + str);
         }            
         pw.println("   }");
         pw.println();         
      }
      pw.println("}");
      
   }
   
   private static int starts_with_count(String s, String t)
   {            
      if(s.startsWith(t))
         return t.length();

      // ...Else:
      return -1;         
   }

   private static final String FONT_STYLE="<FONT face=\"Courier new\" size=4 ";
      
   private static String process_colors(String s)
   {
      StringWriter result = new StringWriter();
      PrintWriter out = new PrintWriter(result);
      
      boolean bold = false;
      
      int len = s.length();      
      for(int i = 0; i < len; ++i)
      {
         char c = s.charAt(i);
         if(c != '<')
         {
            out.print(c);
            continue;
         }           
         
         // ...Else:         
         int e = i + 40;
         if(e >= len)
            e = len;
            
         String temp = s.substring(i, e).toLowerCase();
                  
         int skip = 0;
         if((skip = starts_with_count(temp, "<pre>")) >= 0)
         {            
            out.print("<pre>" + FONT_STYLE + "color=#000000>");
            i += skip - 1;
            
            continue;
         }           

         if((skip = starts_with_count(temp, "</pre>")) >= 0)
         {            
            out.print("</font></pre>");
            i += skip - 1;
            
            continue;
         }           
         
         if((skip = starts_with_count(temp, "<span class='type'>")) >= 0)
         {            
            out.print(FONT_STYLE + "color=#8000ff>");
            i += skip - 1;
            
            continue;
         }           

         if((skip = starts_with_count(temp, "<span class='javadoc'>")) >= 0)
         {            
            out.print(FONT_STYLE + "color=#3f5fbf>");
            i += skip - 1;
            
            continue;
         }           
         
         if((skip = starts_with_count(temp, "<span class='keyword'>")) >= 0)
         {            
            out.print(FONT_STYLE + "color=#7f0055><b>");
            bold = true;
            i += skip - 1;
            
            continue;
         }           
         
         if((skip = starts_with_count(temp, "<span class='string'>")) >= 0)
         {
            out.print(FONT_STYLE + "color=#2a00ff>");
            i += skip - 1;
            
            continue;
         }            
         
         if((skip = starts_with_count(temp, "<span class='comment'>")) >= 0)
         {
            out.print(FONT_STYLE + "color=#3f7f5f>");
            i += skip - 1;
            
            continue;
         }            
         
         if((skip = starts_with_count(temp, "<span class='linenumber'>")) >= 0)
         {
            out.print(FONT_STYLE + "color=#ff5300>");
            i += skip - 1;
            
            continue;
         }            
         
         if((skip = starts_with_count(temp, "</span>")) >= 0)
         {
            if(bold)
            {               
               out.print("</b>");
               bold = false;
            }
            out.print("</font>");
                           
            i += skip - 1;            
            
            continue;
         }            
         
         
         // ...Else:
         out.print(c);
         
      }
      
      out.close();
      return result.toString();
   }
   
   public static class Abcd extends EmptyVisitor 
   {
      public void visitSynthetic(Synthetic attribute)            
      {
         System.out.println("      Synthetic: " + attribute.toString());
      }
   
      public void visitField(Field obj)
      {
         System.out.println("   Field: " + obj.toString());
      }
      
      public void visitJavaClass(JavaClass obj) 
      {
         System.out.println("jc: " + obj.getClassName());
      }
      
      
   }
   
   
   private static class Shower implements Runnable
   {
      private JFrame f_;
      
      public Shower(JFrame f)
      {
         f_ = f;
      }
      
      public void run()
      {
         f_.setVisible(true);
      }
   }


   private static String text_to_html(String text)
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      
      boolean escape = false;
      
      int len = text.length();
      for(int i = 0; i < len; ++i)
      {
         char c = text.charAt(i);
         
         char d = ' ';
         if(i < len - 1)
            d = text.charAt(i + 1);

         if(c == '\\' && d == '&')
         {
            i += 1;
            escape = !escape;
            continue;
         }
         
         if(escape)
         {
            pw.print(c);
            continue;
         }
                     
         if(c == '\n')
         {
            pw.println("<br>");
            continue;
         }           
         
         if(c == ' ')
         {            
            pw.print("&nbsp;");
            continue;
         }
         
         if(c == '&')
         {
            pw.print("&amp;");
            continue;
         }           
         
         if(c == '<')
         {
            pw.print("&lt;");
            continue;
         }           
         
         if(c == '>')
         {
            pw.print("&gt;");
            continue;
         }           
         
         pw.print(c);
      }
      
      pw.close();
      return sw.toString();
   }
   
   public void error(Throwable t)
   {
      t.printStackTrace();
      JOptionPane.showMessageDialog(this.as_frame(), t.getMessage());      
   }

   public static void show(String method_name, JavaClass jc)
   {
      DecompilerImpl v = show(jc);
      
      v.start_search(method_name);
      v.find_next();
//      v.go_to_method(method_name);
   }
   
   
   public static DecompilerImpl show(JavaClass jc)
   {
//      System.out.println(jc.getClassName());
      Environment.instance().state().touched_types().add(jc.getClassName());
      return showSimple(jc, null);
   }
   
   public static DecompilerImpl showSimple(JavaClass jc, JPanel parent)
   {     
      DecompilerImpl v = new DecompilerImpl(jc, parent);

      try
      {
         v.adjust();
         JFrame f = v.as_frame();
         if(f != null)
            f.setVisible(true);               
      }
      catch(Throwable t)
      {
         v.error(t);
         JFrame f = v.as_frame();
         if(f != null)
            f.setVisible(false);
      }         
      
      return v;
   }   
}
