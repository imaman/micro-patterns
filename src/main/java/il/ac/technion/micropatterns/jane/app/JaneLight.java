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

import il.ac.technion.jima.CommandLine;
import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.analysis.misc.Agents;
import il.ac.technion.micropatterns.jane.elements.SubsetElement;
import il.ac.technion.micropatterns.jane.elements.SubsetHandle;
import il.ac.technion.micropatterns.jane.lib.IProgressListener;
import il.ac.technion.micropatterns.jane.lib.JaneMisc;
import il.ac.technion.micropatterns.jane.model.IHandle;
import il.ac.technion.micropatterns.jane.model.XModel;
import il.ac.technion.micropatterns.jane.typedmodel.ModelBuilder;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.io.File;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;

import org.apache.bcel.util.ClassPath;

public class JaneLight
{
   private File jar_file_;
   private File model_file_;
   
   public JaneLight(String jar_file, File model_file)
   {
      jar_file_ = new File(jar_file);      
      model_file_ = model_file;
   }
   
   public void run() throws Throwable
   {
      ModelBuilder mb = new ModelBuilder();
      mb.add_classpath(jar_file_);
      
//      Repository.class_path = new ClassPath(jar_file_.getAbsolutePath());

      mb.build(new XModel(model_file_, false));
      
      TypedModel m = mb.result();
      
         Agents.run_agent(m, IProgressListener.EMPTY);
         

      
      Vector temp = new Vector();
      for(Iterator iter = m.subset_table_.all_handles(); iter.hasNext(); )
         temp.add(iter.next());         

      SubsetHandle[] sets = new SubsetHandle[temp.size()];
      
      for(int i = 0; i < sets.length; ++i)         
      {
         sets[i] = (SubsetHandle) temp.elementAt(i);
         JimaMisc.log().println("sets[" + i + "]=" + sets[i]);
      }         
      
      int[][] array = new int[sets.length][sets.length];
      
      for(int i = 0; i < sets.length; ++i)
      {
         for(int j = 0; j < sets.length; ++j)
            array[i][j] = compare_two(sets, i, j, m);
      }

      PrintStream out = JimaMisc.log();      
      out.print(" ,");
      for(int i = 0; i < sets.length; ++i)
         out.print(sets[i].pretty_name() + ", ");
      out.println();
      
      for(int i = 0; i < sets.length; ++i)
      {
         out.print(sets[i].pretty_name() + ", ");
         for(int j = 0; j < sets.length; ++j)
            out.print(array[i][j] + ", ");
         out.println();            
      }      
   }
   
   private int compare_two(SubsetHandle[] sets, int l, int r, TypedModel m)
   {                    
      SubsetElement lhs = m.get_subset_element(sets[l]);
      SubsetElement rhs = m.get_subset_element(sets[r]);
      
      IHandle[] handles = SubsetElement.intersection(lhs, rhs);         

      if(l != r)
      {            
         JimaMisc.log().println("Intersection of: ");
                  
         JimaMisc.log().println("   " + sets[l].pretty_name());
         JimaMisc.log().println("   " + sets[r].pretty_name());
         JimaMisc.log().println("   size=" + handles.length + "  (" 
            + JaneMisc.to_percent(handles.length, lhs.size()) + ")");
         JimaMisc.log().println("=======================================");
      }
      else
      {
         JimaMisc.log().println("Content of: ");
         JimaMisc.log().println("   " + sets[l].pretty_name());
         JimaMisc.log().println("   size=" + handles.length + "  ("
            + JaneMisc.to_percent(handles.length, lhs.size()) + ")");
         JimaMisc.log().println("=======================================");
      }                  


      System.out.println();
      System.out.println();
      System.out.println();
      
      return handles.length;
   }
   
   
   private static void usage()
   {
      System.out.println("JaneLigt: Coding Pattern Detection Utility");
      System.out.println();
      System.out.println("Usage: JaneLight [-h] -jar <jar-file>");
      System.out.println("   -h    Show this help message");
      System.out.println("   -jar  Run detection on the given jar file");
      System.exit(-1);
   }
   
   public static void main(String[] args)
   {
      try
      {
         Calendar cal = Calendar.getInstance();
         int day = cal.get(Calendar.DAY_OF_WEEK);
         int n = (int) System.currentTimeMillis() % 1000;
         String fn = "day" + day + "-" + n;
         File model_file = new File(fn).getAbsoluteFile();
         
         CommandLine clp = new CommandLine(args, "-h", "-jar");
         if(clp.has("-h"))
            usage(); // End of Program
            
         CommandLine.Entry e = clp.get_required("-jar");
         if(e == null)
            usage(); // End of program
         
         String[] jar_files = e.to_array();
         for(int i = 0; i < jar_files.length; ++i)
         {
            System.out.println("jar file=" + jar_files[i]);            
            JaneLight app = new JaneLight(jar_files[i], model_file);
            app.run();
         }            
         
         System.out.println("-The End-");
      }
      catch(CommandLine.ParserError e)
      {
         System.err.println("Error: " + e);
         usage(); // End of program
      }
      catch(Throwable t)
      {
         System.err.println("Error: " + t);
         t.printStackTrace(JimaMisc.log());
         System.exit(-1);
      }
      
   }
}
