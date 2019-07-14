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











package il.ac.technion.micropatterns.jane.app;

import il.ac.technion.jima.IWidthListener;
import il.ac.technion.jima.JimaMisc;
import il.ac.technion.jima.powergui.PowerTable;
import il.ac.technion.micropatterns.jane.analysis.agents.*;
import il.ac.technion.micropatterns.jane.analysis.misc.Agents;
import il.ac.technion.micropatterns.jane.app.Environment.CommandLineError;
import il.ac.technion.micropatterns.jane.app.gui.AbstractViewManager;
import il.ac.technion.micropatterns.jane.app.gui.ICommand;
import il.ac.technion.micropatterns.jane.app.gui.IJaneViewListener;
import il.ac.technion.micropatterns.jane.app.gui.SortSelector;
import il.ac.technion.micropatterns.jane.app.gui.views.ClassView;
import il.ac.technion.micropatterns.jane.app.gui.views.MethodView;
import il.ac.technion.micropatterns.jane.app.gui.views.PackageView;
import il.ac.technion.micropatterns.jane.app.gui.views.SubsetView;
import il.ac.technion.micropatterns.jane.lib.IProgressListener;
import il.ac.technion.micropatterns.jane.model.XModel;
import il.ac.technion.micropatterns.jane.model.XModel.ModelInitError;
import il.ac.technion.micropatterns.jane.typedmodel.ModelBuilder;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;
import il.ac.technion.micropatterns.stats.Messages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;


public class JaneGui extends JFrame 
   implements IWidthListener, IJaneViewListener
{
   
   
   private static final String PATTERNS_MENU = "Patterns";
   private static JaneGui the_app_;

   private JMenu file_menu_;
   private JMenu etc_menu_;
   private JMenu view_menu_;
   private JMenu edit_menu_;
//   private JMenu tools_menu_;
   
//   private JMenuItem file_addcp_item_;
   private JMenuItem file_save_item_;
   
   private Iterator search_iter_;
   private String last_find_str_;
   
   private AbstractViewManager curr_view_;
   
   private final JFileChooser file_chooser_ = new JFileChooser();
   private final JFileChooser jar_chooser_ = new JFileChooser();

   private static final String JANE_SUFFIX = ".mpprj";
//   private static final String DEFAULT_FILE_NAME = "untitled.jane";

   private ModelBuilder model_builder_ = new ModelBuilder();
   private TypedModel ymodel_;
   private File yfile_= null;
   private static Environment env_ = Environment.instance();

   
   private PowerTable pt_;
   private JScrollBar vsb_;
   private JScrollBar hsb_;
   private JPanel main_panel_;
   

   private int max_horz_scroll_ = 0;

   private static final int ROW_HEIGHT = 20;
   
   private final static FileFilter DEFAULT_FILTER = new FileFilter()
      {
          public boolean accept(File pathname)
          {
             if(pathname.isDirectory())
                return true;
                
             String s = pathname.getAbsolutePath();
             if(s.endsWith(JANE_SUFFIX))
                return true;

             // ...Else:
             return false;
          }          

          public String getDescription()
          {
             return "Jane model files";
          }
      };
      
      
   private final static FileFilter JAR_FILTER = new FileFilter()
      {
          public boolean accept(File pathname)
          {
             if(pathname.isDirectory())
                return true;
                
             String s = pathname.getAbsolutePath();
             if(s.endsWith(".jar"))
                return true;

             // ...Else:
             return false;
          }          

          public String getDescription()
          {
             return "Java Jar file";
          }
      };

   private IProgressListener pl_ = new IProgressListener()
      {
         private int curr_ = 0;
         private String text_ = "A long task is in progress";
         
         
         public void set_text(String text)
         {
            text_ = text;
         }
         
         
         public void done()
         {
            for(int i = curr_; i <= prog_mon_.getMaximum(); ++i)
               prog_mon_.setProgress(i);
            prog_mon_.close();
         }
         
         public void start(int total)
         {
            curr_ = 0;
            prog_mon_ = new ProgressMonitor(main_form(),
               text_, "", 0, total);
            prog_mon_.setProgress(curr_);
            prog_mon_.setMillisToDecideToPopup(10); 
            prog_mon_.setMillisToDecideToPopup(0); 
         }
         
         public void set_current(int pos)
         {
            curr_ = pos;
            prog_mon_.setProgress(curr_);            
         }
         
         public void add_to_current(int n)
         {
            curr_ += n;
            prog_mon_.setProgress(curr_);            
         }
      };   
   
   private ProgressMonitor prog_mon_;
   
   private class FileOpenAction implements ActionListener
   {
      private File f_;
      
      public FileOpenAction(File f)
      {
         f_ = f;
      }
      
      public void actionPerformed(ActionEvent e)
      {
        load(f_);          
      }      
   }
   
   /**
   * 
   */
   public JaneGui(String[] args) 
      throws CommandLineError, IOException
   {      
      Environment.instance().init(args);

      System.out.println("JaneGui is starting.");       
      
      file_chooser_.addChoosableFileFilter(JaneGui.DEFAULT_FILTER);
      build_menus();
            
      ToolTipManager.sharedInstance().setInitialDelay(250);
      ToolTipManager.sharedInstance().setReshowDelay(900);

      this.getContentPane().setLayout(new BorderLayout());
      
      JPanel upper = new JPanel();
      upper.setLayout(new BoxLayout(upper, BoxLayout.X_AXIS));
      this.getContentPane().add(upper, BorderLayout.NORTH);
      
      JPanel jp = new JPanel();
      main_panel_ = jp;
      jp.setLayout(new BorderLayout());
      this.getContentPane().add(jp, BorderLayout.CENTER);

      vsb_ = new JScrollBar();
      jp.add(vsb_, BorderLayout.EAST);

      vsb_.addAdjustmentListener(new AdjustmentListener()
         {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
               pt_.set_vertical_offset(e.getValue());
               pt_.as_component().repaint();
            }
         });

      hsb_ = new JScrollBar(JScrollBar.HORIZONTAL);
      jp.add(hsb_, BorderLayout.SOUTH);

      hsb_.addAdjustmentListener(new AdjustmentListener()
         {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
               pt_.set_horizontal_offset(e.getValue());
               pt_.as_component().repaint();
            }
         });

      

      pt_ = new PowerTable(ROW_HEIGHT);

      jp.add(pt_.as_component(), BorderLayout.CENTER);
      pt_.as_component().addComponentListener(new ComponentAdapter()
         {
            public void componentResized(ComponentEvent e)
            {
               table_resized();
            }
         });
         

      this.addWindowListener(new WindowAdapter() 
         {
            public void windowClosing(WindowEvent e) 
            {
               app_exit(0);
            }
         });
          
      // Restore jane directory          
      String temp = env_.state().get_file_chooser_dir();
      
      File f = new File(temp).getAbsoluteFile();      
      JimaMisc.log().println("Setting jane dir to " + f);
      JimaMisc.log().println("f.exists()=" + f.exists());
      
      try
      {
         file_chooser_.setCurrentDirectory(f);
      }
      catch(ArrayIndexOutOfBoundsException aiobe)
      {
         JimaMisc.log().println(aiobe);
      }

      // Restore jar directory
      temp = env_.state().get_jar_chooser_dir();

      f = new File(temp).getAbsoluteFile();      
      JimaMisc.log().println("Setting (jars) dir to " + f);
      JimaMisc.log().println("f.exists()=" + f.exists());

      try
      {
         jar_chooser_.setCurrentDirectory(f);
      }
      catch(ArrayIndexOutOfBoundsException aiobe)
      {
         JimaMisc.log().println(aiobe);
      }

      set_model(null); //new Model(new File("untitled.jane"), false));
      

   }
   
   private TypedModel get_model()
   {
      return ymodel_;
   }
   
   private void set_model(TypedModel m) 
   {
      view_menu_.setEnabled(m != null);
      etc_menu_.setEnabled(m != null);
      edit_menu_.setEnabled(m != null);
//      tools_menu_.setEnabled(m != null);
      
  //    file_addcp_item_.setEnabled(m != null);
      file_save_item_.setEnabled(m != null);
      
      this.main_panel_.setVisible(m != null);
      this.main_form().getContentPane().setBackground(
         m == null ? Color.LIGHT_GRAY : Color.WHITE);
         
      ymodel_ = m;
      set_file();
      
      try
      {
         if(m != null)
            m.load();
      }
      catch(Throwable e)
      {
         JimaMisc.stop(e);
      }
      
      if(m != null && m.subset_table_.size() > 0)
         switch_to_subset_view();      
      else
         switch_to_class_view();         
   }
   
   private void build_file_menu(JMenu file_menu, boolean isEmpty)
   {
      file_menu.removeAll();
      
      JMenuItem item = new JMenuItem("New Project...", KeyEvent.VK_N);
      item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 
         ActionEvent.CTRL_MASK));
      file_menu.add(item);      
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               file_new();               
            }            
         });
      
      item = new JMenuItem("Open Project...", KeyEvent.VK_O);
      item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 
         ActionEvent.CTRL_MASK));
      file_menu.add(item);      
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               int ret_code = chooser("Open Project (*." + JANE_SUFFIX + ")")
                  .showOpenDialog(main_form()); 
               if(ret_code != JFileChooser.APPROVE_OPTION) 
                  return;
                  
               File file = file_chooser_.getSelectedFile();
               if(file == null)
                  return;
                  
               load(file);
            }                  
         });
      
      item = new JMenuItem("Save Project", KeyEvent.VK_S);
      item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
         ActionEvent.CTRL_MASK));
      item.setEnabled(!isEmpty);
      file_menu.add(item);      
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               try
               {
                  save_model();
               }
               catch(Throwable t)
               {
                  error(t);
               }
            }
         });
      file_save_item_ = item;
      
      item = new JMenuItem("Close Project", KeyEvent.VK_C);
      item.setEnabled(!isEmpty);
      file_menu.add(item);      
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               try
               {
                  set_model(null);
               }
               catch (Throwable t)
               {
                  error(t);
               }
            }
         });

    //  file_addcp_item_ = item;
         
      file_menu.addSeparator();
      item = new JMenuItem("Load *.Jar into Project...", KeyEvent.VK_L);
      item.setEnabled(!isEmpty);
      file_menu.add(item);      
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               add_classpath();
            }
         });
      
      int n = 0;
      for(Iterator i = env_.state().get_opened_files(); i.hasNext(); )
      {
         String curr = i.next().toString();
         File f = new File(curr);
         if(!f.exists())
            continue;

         n += 1;
         
         if(n == 1)
            file_menu.addSeparator();
                        
         item = new JMenuItem(Integer.toString(n) + ' ' + curr);
         file_menu.add(item);      
         item.addActionListener(new FileOpenAction(f));
      }
      
      file_menu.addSeparator();
      item = new JMenuItem("Exit", KeyEvent.VK_X);
      file_menu.add(item);      
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               app_exit(0);
            }            
         });
      
   }
   
   private void build_menus()
   {
      JMenuBar main_menu = new JMenuBar();
      
      JMenu file_menu = new JMenu("File");
      file_menu.setMnemonic(KeyEvent.VK_F);
      main_menu.add(file_menu);        
      
      build_file_menu(file_menu, true);

      edit_menu_ = new JMenu("Edit");
      edit_menu_.setMnemonic(KeyEvent.VK_E);
      main_menu.add(edit_menu_);        


      JMenuItem item = new JMenuItem("Find...", KeyEvent.VK_F);
      edit_menu_.add(item);            
      item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, 
         ActionEvent.CTRL_MASK));        
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               find_requested();
            }            
         });

      item = new JMenuItem("Find next", KeyEvent.VK_N);
      edit_menu_.add(item);            
      item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               find_next_requested();
            }            
         });

      item = new JMenuItem("Change Sort Order");
      edit_menu_.add(item);      
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               SortSelector.change();
               curr_view_.assign_model(get_model());               
            }
         });

      item = new JMenuItem("Reset Sort Order");
      edit_menu_.add(item);      
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               SortSelector.reset();
               curr_view_.assign_model(get_model());               
            }
         });
      
      edit_menu_.addSeparator();         
      item = new JMenuItem("Clear history", KeyEvent.VK_H);
      edit_menu_.add(item);            
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               Environment.instance().state().touched_types().clear();
               pt_.as_component().repaint();                              
            }            
         });

      
//      edit_menu_.addSeparator();
//      item = new JMenuItem("Add sun 1.3");
//      edit_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               assign_jars(PredefinedJars.SUN_13);
//            }
//         });
//
//      
//      item = new JMenuItem("Add Sun 1.4.1");
//      edit_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               assign_jars(PredefinedJars.SUN_141);
//            }
//         });
//
//      
//      item = new JMenuItem("Add Mini");
//      edit_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               assign_jars(PredefinedJars.MINI);
//            }
//         });
//      
//      item = new JMenuItem("Add Ant,Az");
//      edit_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               assign_jars(PredefinedJars.APPS);
//            }
//         });
//
//      item = new JMenuItem("Add Poseidon");
//      edit_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               assign_jars(PredefinedJars.POSEIDON);
//            }
//         });
//      
//      item = new JMenuItem("Add JBoss");
//      edit_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               assign_jars(PredefinedJars.JBOSS);
//            }
//         });

      
      view_menu_ = new JMenu("View");
      view_menu_.setMnemonic(KeyEvent.VK_V);
      main_menu.add(view_menu_);        

      item = new JMenuItem("Class View", KeyEvent.VK_C);
      view_menu_.add(item);      
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               switch_to_class_view();
            }            
         });      

      item = new JMenuItem("Subset View", KeyEvent.VK_S);
      view_menu_.add(item);      
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               switch_to_subset_view();
            }            
         });      

      item = new JMenuItem("Method View", KeyEvent.VK_M);
      view_menu_.add(item);      
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               switch_to_method_view();
            }            
         });      

      item = new JMenuItem("Package View", KeyEvent.VK_P);
      view_menu_.add(item);      
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               switch_to_package_view();
            }            
         });      

      
      etc_menu_ = new JMenu(PATTERNS_MENU);
      etc_menu_.setMnemonic(KeyEvent.VK_P);
      main_menu.add(etc_menu_);        


      add_agent_item(etc_menu_, "Run Box/Immutable (BOX,IMMU,IMBX)",
         EffectivelyImmutable.class, KeyEvent.VK_E);
      add_agent_item(etc_menu_, "Run Box2 (CBOX)", BoxEx.class);
      add_agent_item(etc_menu_, "Run Functions", Functions.class);
      add_agent_item(etc_menu_, "Run Restricted Creation (FLYW,RSCR)", 
         RestrictedCreation.class);

      add_agent_item(etc_menu_, "Run Re/Implementors (IMPL,REIM)", Reimplementor.class);
            
      add_agent_item(etc_menu_, "Run Pseudo-Interfaces (PCLS)", PseudoInterface.class, 
         KeyEvent.VK_P);
      add_agent_item(etc_menu_, "Run Pools (POOL)", Pool.class, KeyEvent.VK_L);
      
      add_agent_item(etc_menu_, "Run Sinks (SINK)", Sink.class, KeyEvent.VK_K);
      add_agent_item(etc_menu_, "Run Stateless (STLS)", Stateless.class);
      add_agent_item(etc_menu_, "Run Monostates (MONO)", Monostate.class, KeyEvent.VK_M);
      add_agent_item(etc_menu_, "Run Template-Method (TMPL)", TemplateMethod.class);
      add_agent_item(etc_menu_, "Run Self (LSLF, RCUR)", Self.class);
//      etc_menu_.addSeparator();
//      add_agent_item(etc_menu_, "Run Useless ", UselessField.class);
      etc_menu_.addSeparator();
      add_agent_item(etc_menu_, "Run All", null);
      
//      add_agent_item(etc_menu_, "Run Complex (COM)", Complex.class);
//      add_agent_item(etc_menu_, "Run Toolkits (TK)", Toolkit.class);
//      add_agent_item(etc_menu_, "Run Hand Shakers", HandShakers.class);
//      add_agent_item(etc_menu_, "Run Forwarders", Forwarder.class, KeyEvent.VK_W);
//      add_agent_item(etc_menu_, "Run Decorators", Decorator.class, KeyEvent.VK_C);
//      add_agent_item(etc_menu_, "Run Active-Objects", ActiveObject.class);
//      etc_menu_.addSeparator();
//      add_agent_item(etc_menu_, "Run Sink-user", SinkUser.class);
//      add_agent_item(etc_menu_, "Run Refiners", Refiner.class);
//      add_agent_item(etc_menu_, "Refiner Plus", RefinerPlus.class);
//      add_agent_item(etc_menu_, "Run Cand1", Cand1.class);
//      add_agent_item(etc_menu_, "Run Cand2", Cand2.class);
//      add_agent_item(etc_menu_, "Run Strict-Super", StrictSuperClass.class);
//      etc_menu_.addSeparator();
//    add_agent_item(etc_menu_, "Run Miner", Miner.class);

      
      
//      tools_menu_ = new JMenu("Tools");
//      tools_menu_.setMnemonic(KeyEvent.VK_T);
//      main_menu.add(tools_menu_);        
//
//      
//      item = new JMenuItem("C/P profile", KeyEvent.VK_C);
//      tools_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               Reports.cp_profile(JaneGui.this.get_model());
//            }               
//         });      
//      
//
//      item = new JMenuItem("Library profile", KeyEvent.VK_L);
//      tools_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               Reports.library_profile(JaneGui.this.get_model());
//            }               
//         });      
//
//      item = new JMenuItem("Correlation report", KeyEvent.VK_R);
//      tools_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               Reports.correlation_report(JaneGui.this.get_model());
//            }               
//         });      
//      
//      item = new JMenuItem("C/P Association report", KeyEvent.VK_A);
//      tools_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               Reports.association_matrix(JaneGui.this.get_model());
//            }               
//         });      
//
//      item = new JMenuItem("Pair-wise composition");
//      tools_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               Reports.pair_wise_class_association(get_model(), 
//                  IAssociationProvider.COMPOSITION);
//            }               
//         });      
//
//      item = new JMenuItem("Pair-wise inheritance");
//      tools_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               Reports.pair_wise_class_association(get_model(), 
//                  IAssociationProvider.INHERITANCE);
//            }               
//         });      
//      
//      item = new JMenuItem("Pair-wise method invocation");
//      tools_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               Reports.pair_wise_class_association(get_model(), 
//                  IAssociationProvider.METHOD_CALLS);
//            }               
//         });      
//            
//      tools_menu_.addSeparator();
//      item = new JMenuItem("All reports");
//      tools_menu_.add(item);      
//      item.addActionListener(new ActionListener()
//         {
//            public void actionPerformed(ActionEvent e)
//            {
//               Reports.cp_profile(JaneGui.this.get_model());
//               Reports.library_profile(JaneGui.this.get_model());
//               Reports.correlation_report(JaneGui.this.get_model());
//               Reports.association_matrix(JaneGui.this.get_model());
//
//            }               
//         });      
      
      
      JMenu help_menu = new JMenu("Help");
      help_menu.setMnemonic(KeyEvent.VK_H);
      main_menu.add(help_menu);        

      item = new JMenuItem("About", KeyEvent.VK_A);
      help_menu.add(item);      
      item.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {               
               String text = "MPgui: Micro-Patterns Browsing Tool\n" 
                  + Messages.aboutText();
               JOptionPane.showMessageDialog(main_form(), text, "About", 
                  JOptionPane.INFORMATION_MESSAGE); 
            }               
         });      

      
      setJMenuBar(main_menu);
      file_menu_ = file_menu;
      
      this.repaint();
   }
   
   private class RunAgentAction implements ActionListener
   {
      private Class c_;
      
      public RunAgentAction(Class c)
      {
         c_ = c;
      }
      
      public void actionPerformed(ActionEvent e)
      {
         run_agent(c_);
      }      
   }

   private JMenuItem add_agent_item(JMenu m, String text, Class agent_class)
   {
      return add_agent_item(m, text, agent_class, -1);
   }
   
   private JMenuItem add_agent_item(JMenu m, String text, Class agent_class, 
      int shortcut_key)
   {
      JMenuItem result = null;
      if(shortcut_key < 0)
         result =  new JMenuItem(text);
      else
         result =  new JMenuItem(text, shortcut_key);
         
      m.add(result);      
      result.addActionListener(new RunAgentAction(agent_class));
      
      return result;
   }
   
   
   private class RunAgentCmd implements ICommand
   {
      private Class c_;
      public RunAgentCmd(Class c)
      {
         c_ = c;
      }

      public void execute()
      {
         try
         {
            if(c_ == null)
               Agents.run_agent(get_model(), pl_);
            else               
               Agents.run_agent(get_model(), pl_, c_);
               
            switch_to_subset_view();
         }
         catch(Throwable t)
         {
            error(t);
         }                           
      }
   }
   
   private void run_agent(Class agent_class)
   {
      run_task("", new RunAgentCmd(agent_class));
   }
   
   
   private void save_model() throws Throwable 
   {
//      if(get_model().get_file().getName().equals(DEFAULT_FILE_NAME))
//         return;

      TypedModel m = get_model();
      if(m != null)
         m.save();         
   }
   
   private void app_exit(int exit_code)
   {      
      try
      {
         save_model();
         System.out.println("WindowClosed");
         env_.state().save();

      }
      catch(Throwable t)
      {
         // Absorb
      }
      System.exit(exit_code);
   }
   
   
   private void set_file()
   {
      TypedModel m = get_model();
      if(m == null)
      {
         this.setTitle("Empty");
         return;         
      }
      
      File f = m.get_file().getAbsoluteFile();
      
      this.setTitle(f.getAbsolutePath());
      env_.state().file_was_opened(f);
      env_.state().set_file_chooser_dir(f);
      
      build_file_menu(file_menu_, m == null);
   }
   
   private JFrame main_form()
   {
      return this;
   }
   
   private JFileChooser chooser(String title)
   {
      file_chooser_.resetChoosableFileFilters();
      file_chooser_.addChoosableFileFilter(DEFAULT_FILTER);
      file_chooser_.setFileFilter(DEFAULT_FILTER);
      file_chooser_.setFileSelectionMode(JFileChooser.FILES_ONLY);
      
      if(title != null)
         file_chooser_.setDialogTitle(title);
      
      return file_chooser_;
   }

   private JFileChooser jar_chooser()
   {
      jar_chooser_.resetChoosableFileFilters();
      jar_chooser_.addChoosableFileFilter(JAR_FILTER);
      jar_chooser_.setFileFilter(JAR_FILTER);
      jar_chooser_.setFileSelectionMode(JFileChooser.FILES_ONLY);
      jar_chooser_.setMultiSelectionEnabled(true);
      
      return jar_chooser_;
   }
   
   public void file_new()
   {
      try
      {
         save_model();
      }
      catch (Throwable t)
      {
         error(t);
         return;
      }
      
      JFileChooser fc = chooser("New project (*." + JANE_SUFFIX + ")");
      File suggested_new_file = pick_new_file(fc.getSelectedFile(), 
         fc.getCurrentDirectory());
      fc.setSelectedFile(suggested_new_file);
      
      int ret_code = fc.showDialog(main_form(), "Create"); 
      
      if(ret_code != JFileChooser.APPROVE_OPTION) 
         return;
         
      File file = file_chooser_.getSelectedFile();
      if(!file.getName().endsWith(JANE_SUFFIX))
      {
         error("File name must have a " + JANE_SUFFIX 
            + " extension");
            
         return;  
      }                                  

      try
      {
         XModel xm = new XModel(file, false);
         model_builder_ = new ModelBuilder(xm);
         
         set_model(TypedModel.create(xm));
      }
      catch(ModelInitError e)
      {
         error(e);
      }      
   }
   
   private static File pick_new_file(File f, File dir)
   {
      JimaMisc.ensure(dir != null);
      if(f != null)
         dir = f.getAbsoluteFile().getParentFile();
         
      dir = dir.getAbsoluteFile();         
         
      Calendar now = Calendar.getInstance();
      
      int rand = (int) ((System.currentTimeMillis() / 100) % 900 + 100);
      rand = Math.abs(rand);
      
      int doy = now.get(Calendar.DAY_OF_YEAR);
      int year = now.get(Calendar.YEAR);
      
      
      JimaMisc.log().println("rand=" + rand + ", doy=" + doy 
         + ", year=" + year);
      String result = "file-" + JimaMisc.align_number(rand, 3, '0') 
         + "-" + JimaMisc.align_number(doy, 3, '0')
         + "-" + JimaMisc.align_number(year, 4, '0')
         + JANE_SUFFIX;
         
      return new File(dir, result);
   }
      
   public void add_classpath()
   {
      JimaMisc.ensure(get_model() != null);
      
      try
      {                  
         JFileChooser fc = jar_chooser();
         int ret_code = fc.showOpenDialog(this.main_form());
         if(ret_code != JFileChooser.APPROVE_OPTION)
            return;

         // ...Else:            
         File[] f = fc.getSelectedFiles();
         if(f == null || f.length == 0)
            return;
            
         env_.state().set_jar_chooser_dir(f[0]);

         assign_jars_impl(f);
         
         int yesNo = JOptionPane.showConfirmDialog(this, 
            "\n\nDo you want to run a Micro-Pattern analysis on your project?\n\n"
            + "(Alternatively, you can run this analysis from the '" 
            + PATTERNS_MENU + "' menu)\n\n",
            "Loading completed successfully",
            JOptionPane.YES_NO_OPTION);
         
         if(yesNo == JOptionPane.YES_OPTION)
            run_agent(null);
      }
      catch(Throwable t)
      {
         error(t);
      }
   }

   private void assign_jars(String[] jar_files) 
   {
      File cd = jar_chooser_.getCurrentDirectory();

      File[] files = new File[jar_files.length];
      for(int i = 0; i  < jar_files.length; ++i)
         files[i] = new File(cd, jar_files[i]);

      try
      {
         assign_jars_impl(files);
      }
      catch(Throwable e)
      {
         error(e);
      }
   }

   private void assign_jars_impl(File[] jar_files) throws Throwable
   {
      TypedModel m = get_model();
      save_model();
                  
      for(int i = 0; i < jar_files.length; ++i)
         model_builder_.add_classpath(jar_files[i]);
         
      model_builder_.build(m.inner());
      
      set_model(model_builder_.result());
   }

   
   public void load(File f)
   {
      JimaMisc.ensure(f != null);
      try
      {
         save_model();
         
         XModel m = new XModel(f);         
         f = m.get_file();

         set_model(TypedModel.create(m));
      }
      catch(Throwable e)
      {
         error("Error f=" + f + ", exception=" + e);
      }      
   }
   
   private class Task extends Thread
   {
      private ICommand cmd_;
      private String s_;
      private IProgressListener pl_;
      
      public Task(IProgressListener pl,  String s, ICommand c)
      {
         s_ = s;
         cmd_ = c;
         pl_ = pl;
      }
      
      public void run()
      {
         SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  main_form().setEnabled(false);
               }
            });

         pl_.set_text(s_);
         cmd_.execute();
         pl_.done();

         SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  main_form().setEnabled(true);
               }
            });
      }
   }
   
   private void run_task(String s, ICommand c)
   {
      try
      {
         Thread t = new Task(pl_, s, c);
         t.start();
      }
      catch(Throwable t)
      {
         error(t);
      }
   }
   
   private void switch_to_class_view()
   {
      this.curr_view_ = new ClassView(this.pt_, this, this); 
      this.curr_view_.assign_model(get_model());
   }
   
   private void switch_to_subset_view()
   {
      this.curr_view_ = new SubsetView(this.pt_, this, this); 
      this.curr_view_.assign_model(get_model());
   }

   private void switch_to_method_view()
   {
      this.curr_view_ = new MethodView(this.pt_, this, this);
      this.curr_view_.assign_model(get_model());
   }
   
   private void switch_to_package_view()
   {
      this.curr_view_ = new PackageView(this.pt_, this, this);
      this.curr_view_.assign_model(get_model());      
   }
   
   private void load_last()
   {
      for(Iterator i = env_.state().get_opened_files(); i.hasNext(); )
      {
         String curr = i.next().toString();
         File f = new File(curr);
         if(f.exists())
         {
            JimaMisc.log().println("Loading last file: " + f.getAbsolutePath());
            this.load(f);
            break;
         }            
      }
   }

   
   private void error(Throwable e)
   {      
      e.printStackTrace(JimaMisc.log());

      if(e instanceof OutOfMemoryError)
         error("no more memory error");
      else
         error(e.getMessage());
   }

   private void error(String s)
   {
      JimaMisc.log().println("Error caught: " + s);
      JOptionPane.showMessageDialog(this, s);
   }

   public void find_requested()
   {
      String s = (String) JOptionPane.showInputDialog(this, "Find what:", 
         "Find",  JOptionPane.QUESTION_MESSAGE, null, null, last_find_str_);
      
      if(s == null)
         return;
         
      last_find_str_ = s;         
      search_iter_ = curr_view_.find(s);
      
      find_next_requested();
   }
   
   public void find_next_requested()
   {
      if(search_iter_ == null)
         return;
                  
      if(!search_iter_.hasNext())
      {
         error("Cannot find \"" + last_find_str_ + "\"");
         return;
      }
      
      Integer n = (Integer) search_iter_.next();
      this.curr_view_.assign_vert_offset(n.intValue());
   }
      
   public void vert_scroll_to(int value)
   {
      this.vsb_.setValue(value);
   }
   
   public void set_vert_range(int num_of_rows)
   {
      this.vsb_.setMaximum(num_of_rows - 1);
   }
   
      
   private void table_resized()
   {
      // Readjust value, extent of the veritcal scroll bar
      int ne = pt_.num_of_visible_rows();
      int max = vsb_.getModel().getMaximum();
   
      int val = vsb_.getModel().getValue();
      val = Math.min(val, max - ne);

      vsb_.getModel().setValue(val);
      vsb_.getModel().setExtent(ne);

      // Readjust value, extent of the horizontal scroll bar
      max = max_horz_scroll_;
      ne = Math.min(this.pt_.as_component().getWidth(), max);

      val = hsb_.getModel().getValue();
      val = Math.min(val, max - ne);

//      Misc.log.println("max=" + max + ", ne=" + ne + ", val=" + val);
//      Misc.log.flush();

      hsb_.getModel().setExtent(ne);      
      hsb_.getModel().setValue(val);
   }


   public void width_changed(int new_width)
   {
      max_horz_scroll_ = new_width + 100;
      this.hsb_.getModel().setMaximum(max_horz_scroll_);
      
      table_resized();
   }
   
   
   
   
   
   public static void main(String[] args) throws Exception
   {       
      try
      {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Throwable t)
      {
         // Absorb
      }
      
      the_app_ = new JaneGui(args);
            
      try
      {
         File f = Environment.instance().file();
         
//         if(f == null)
//            the_app_.load_last();

         if(f != null)
            the_app_.load(f);
      }
      catch(Throwable e)
      {
         System.err.println(e);
         e.printStackTrace(JimaMisc.log());
      }

      the_app_.pack();
      the_app_.setSize(800, 600);
      the_app_.setLocation(80, 100);

      the_app_.setVisible(true);      
   }
   
   
//   public static void set_title(String s)
//   {
//      the_frame_.setTitle(s);
//   }
}
