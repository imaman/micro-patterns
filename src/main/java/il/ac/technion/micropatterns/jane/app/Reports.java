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










/*
 * Created on Nov 6, 2004
 * Written by spiderman
 * Project: JarScan
 */

package il.ac.technion.micropatterns.jane.app;

import il.ac.technion.micropatterns.jane.app.gui.views.SubsetView.MyComp;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.elements.SubsetHandle;
import il.ac.technion.micropatterns.jane.lib.ClassMetric;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.typedmodel.SetOfClasses;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import il.ac.technion.micropatterns.jungle.model.ClassInfo;
import il.ac.technion.micropatterns.jungle.model.Cluster;
import il.ac.technion.micropatterns.jungle.model.Clusterization;
import il.ac.technion.micropatterns.jungle.model.CollectionDesc;
import il.ac.technion.micropatterns.jungle.model.Ensemble;
import il.ac.technion.micropatterns.jungle.model.JavaCollection;
import il.ac.technion.micropatterns.jungle.model.ClassInfo.ClassInfoError;
import il.ac.technion.micropatterns.tables.BadReference;
import il.ac.technion.micropatterns.tables.Builder;
import il.ac.technion.micropatterns.tables.Table;

/**
 * One liner. Full description.
 * 
 * @author spiderman
 * @since Nov 6, 2004
 */
public class Reports
{
   private static PrintStream out_;
   
   static 
   {
      init(Environment.instance().home_dir());
   }
   
   public static void init(String dir)
   {
      File f = new File(dir, "reports.txt");
      init(f);
   }
   
   public static void init(File f)
   {
      try
      {
         init(new PrintStream(new FileOutputStream(f, true)));
      }
      catch(FileNotFoundException e)
      {
         out_ = System.out;
      }            
   }
   
   public static void init(PrintStream out)
   {
      out_ = out;
   }
   
   private static void end_report()
   {
      out_.println("</report>");
      out_.println();
      out_.println();
      out_.println();
      out_.flush();
   }
   
   private static void start_report(String report_name)
   {
      start_report(report_name, null, null, new Properties());
   }

   private static void start_report(String report_name, String comment, 
      String desc)
   {
      start_report(report_name, comment, desc, new Properties());
   }
   
   private static void start_report(String report_name, String comment, 
      String desc, Properties props)
   {
      out_.println();
      out_.println();
      out_.println();
      out_.println("<report>");
      out_.println("*==============================================");
      out_.println("* Report: " + report_name);
      if(comment != null)
         out_.println("* Comment: " + comment);
      if(desc != null)
         out_.println("* desc: " + desc);
         
      out_.println("* Time: " + new Date());
      for(Enumeration e = props.keys(); e.hasMoreElements(); )
      {
         String k = (String) e.nextElement();
         String s = props.getProperty(k);
         out_.println("* " + k + ": " + s);
      }
         
      out_.println("*==============================================");
   }

   public static void cp_profile(TypedModel m)
   {      
      start_report("C/P Profile");
            
      Vector sets = new Vector();
      for(Iterator i = m.subset_table_.all_handles(); i.hasNext(); )
      {
         SubsetHandle curr = (SubsetHandle) i.next();
         SetOfClasses soc = new SetOfClasses(curr, m);
         sets.add(soc);
      }
      
      Collections.sort(sets, new MyComp(m));
      
      //
      // Find the size of our "universe"
      //
      int pass_n = 0;      
      for(Iterator j = sets.iterator(); j.hasNext(); )
      { 
         if(pass_n == 0)
            break;
      }

      out_.print("Order of libraries: ");
      int col_n = 0;
      for(Iterator j = sets.iterator(); j.hasNext(); ++col_n)
      {
         SetOfClasses curr_lib = (SetOfClasses) j.next();
         if(!curr_lib.is_library())
            continue;
         
         if(col_n > 0)
            out_.print(", ");
         out_.print(curr_lib.get_name());
      }
      out_.println();
      out_.println();
      
      // 
      // Compute data for each <set,library> pair
      //
      for(Iterator i = sets.iterator(); i.hasNext(); ++pass_n)
      {
         SetOfClasses curr = (SetOfClasses) i.next();
         if(curr.is_library())
            continue;
         
         out_.print("[" + curr.get_initials() + "] ");
   
         col_n = 0;
         for(Iterator j = sets.iterator(); j.hasNext(); ++col_n)
         {
            SetOfClasses curr_lib = (SetOfClasses) j.next();
            if(!curr_lib.is_library())
               continue;
         
            SetOfClasses common = curr_lib.intersection(curr);
            
            double percent = common.size() * 100.0 / curr_lib.size();
            percent = Math.round(percent * 10) / 10.0;
            
            if(col_n > 0)
               out_.print(",");
            
            out_.print(percent + "%");
         }
         
         out_.println();
      }      

      end_report();
   }
   
   public static void library_profile(TypedModel m)
   {
      start_report("Library Profile");
      
      Vector libs = new Vector();
      for(Iterator i = m.subset_table_.all_handles(); i.hasNext(); )
      {
         SubsetHandle curr = (SubsetHandle) i.next();
         SetOfClasses soc = new SetOfClasses(curr, m);
         
         if(!soc.is_library())
            continue;
         
         libs.add(soc);
      }
      
      Collections.sort(libs, new MyComp(m));

      out_.println(", packages, classes, methods, " + ClassMetric.title_csv());

      for(Iterator j = libs.iterator(); j.hasNext(); )
      {
         SetOfClasses lib = (SetOfClasses) j.next();
         
         int methods = 0;
         int classes = 0;
         
         ClassMetric total = new ClassMetric();
         HashSet packages = new HashSet();

         for(Iterator i = lib.class_handles(); i.hasNext(); ++classes)
         {
            ClassHandle curr = (ClassHandle) i.next();
            ClassSpec cs = curr.typed_value(m);
            
            String str = curr.get_name();
            String package_name = JavaSpec.package_name_of(str);
            
            packages.add(package_name);

            ClassMetric cm = new ClassMetric(cs);
            total.add(cm);
            
            methods += cs.jc().getMethods().length;
         }
         
         out_.println(lib.get_name() + ", " + packages.size()
            + ", " + classes 
            + ", " + methods
            + ", " + total.to_csv(classes));   
      }      
      
      end_report();
   }
   

   public static void library_metrics(TypedModel m)
   {
      start_report("Metrics Profile");
      
      Vector libs = new Vector();
      for(Iterator i = m.subset_table_.all_handles(); i.hasNext(); )
      {
         SubsetHandle curr = (SubsetHandle) i.next();
         SetOfClasses soc = new SetOfClasses(curr, m);
         
         if(!soc.is_library())
            continue;
         
         libs.add(soc);
      }
      
      Collections.sort(libs, new MyComp(m));

      out_.println(", " + ClassMetric.title_csv());
      
      for(Iterator j = libs.iterator(); j.hasNext(); )
      {
         SetOfClasses lib = (SetOfClasses) j.next();
         
         int classes = 0;
         ClassMetric total = new ClassMetric();
         
         for(Iterator i = lib.class_handles(); i.hasNext(); ++classes)
         {
            ClassHandle curr = (ClassHandle) i.next();
            ClassSpec cs = curr.typed_value(m);

            ClassMetric cm = new ClassMetric(cs);
            total.add(cm);
         }
         
         out_.println(total.to_csv(classes));         
      }      
      
      end_report();
   }

   public static void pair_wise_class_association(TypedModel m, 
      IAssociationProvider ap) 
   {
      Vector libs = new Vector();
      for(Iterator i = m.subset_table_.all_handles(); i.hasNext(); )
      {         
         SubsetHandle curr = (SubsetHandle) i.next();
         SetOfClasses soc = new SetOfClasses(curr, m);
         if(soc.get_name().indexOf("-all-types") >= 0)
            continue;
         
         if(soc.size() <= 0)
            continue;
         
         if(soc.is_library())
            libs.add(soc);
      }
      
      
      for(Iterator k = libs.iterator(); k.hasNext(); )
      {
         SetOfClasses lib = (SetOfClasses) k.next();
         start_report("usage-pairs", "lib=" + lib.get_name(), ap.name());
         
         for(Iterator i = lib.class_handles(); i.hasNext(); )
         {
            ClassHandle curr = (ClassHandle) i.next();
            ClassSpec cs = curr.typed_value(m);
            
            Iterator j = ap.associations_of(cs, m);
            
            
            
//            Iterator j = JavaSpec.aggregated_classes(jc, false); 
//            Iterator j = JavaSpec.associated_classes(jc); 
            
            while(j.hasNext())
            {
               String other_class = (String) j.next();
               out_.println(curr.get_name() + ", " + other_class);               
            }                      
         }
         end_report();
      }      
   }
   
   
   public static void association_matrix(Clusterization c, JavaCollection jcoll, 
      Ensemble e) throws ClassInfoError
   {
      association_matrix(c, jcoll, e, true);
   }

   public static void association_matrix(Clusterization c, JavaCollection jcoll, 
      Ensemble e, boolean checkName) throws ClassInfoError
   {
      association_matrix(c, jcoll, e, false, out_, checkName);
   }
   
   public static Table vectors_of(Clusterization c, JavaCollection jcoll, 
      Ensemble e, boolean checkName, boolean fullNames) 
      throws ClassInfoError, BadReference
   {
      
      Builder b = new Builder();
      
      b.add("C/P:").add("Collection").add("IsInterface?").add("Methods");
      
      for(Iterator j = c.clusters(); j.hasNext(); )
      {
         Cluster curr = (Cluster) j.next();
         DescCP d = curr.get_desc();
         
         String name = d.toString();
         if(fullNames)
            name = d.get_full_name();
         
         b.add(name);
      }
      b.newLine();
         
      String str = jcoll.get_name();
      if(str.endsWith(".jar"))
         str = str.substring(0, str.length() - 4);
      
      String coll_name = str;
      if(checkName)
         coll_name = CollectionDesc.get(str).get_latex_name();
      
      for(Iterator j = jcoll.type_names(); j.hasNext(); )
      {
         String type_name = (String) j.next();
         ClassInfo ci = e.create_class(type_name);
         
         b.add(type_name).add(coll_name).add(ci.is_interface_ ? "Yes" : "N");
         b.add(ci.get_method_count());

         for(Iterator k = c.clusters(); k.hasNext(); )
         {
            Cluster curr = (Cluster) k.next();

            int temp = curr.has(type_name) ? 1 : 0;
            String s = Integer.toString(temp);
            
            b.add(s);
         }
         
         b.newLine();
      }
      
      return b.result();
   }
   
   public static void association_matrix(Clusterization c, JavaCollection jcoll, 
      Ensemble e, boolean short_format, PrintStream out, boolean checkName) 
      throws ClassInfoError
   {      
      if(!short_format)
         start_report("assocation-matrix", "lib=" + jcoll.get_name(), null);
      
      out.print("C/P:");
      if(short_format)
         out.print(",Collection,IsInterface?,Methods");
      
      for(Iterator j = c.clusters(); j.hasNext(); )
      {
         Cluster curr = (Cluster) j.next();
         out.print(", " + curr.get_desc());
      }
      out.println();
         
      String str = jcoll.get_name();
      if(str.endsWith(".jar"))
         str = str.substring(0, str.length() - 4);
      
      String coll_name = str;
      if(checkName)
         coll_name = CollectionDesc.get(str).get_latex_name();
      
      for(Iterator j = jcoll.type_names(); j.hasNext(); )
      {
         String type_name = (String) j.next();
         ClassInfo ci = e.create_class(type_name);
         
         StringBuffer sb = new StringBuffer();
         sb.append("," + coll_name);
         sb.append("," + (ci.is_interface_ ? "Yes" : "N"));
         sb.append(",");
         
         if(!short_format)
            sb.append(Common.METHOD_COUNT_PREFIX);
         
         sb.append(ci.get_method_count());

         for(Iterator k = c.clusters(); k.hasNext(); )
         {
            Cluster curr = (Cluster) k.next();

            int temp = curr.has(type_name) ? 1 : 0;
            String s = Integer.toString(temp);
            
            sb.append("," + s);
         }
         
         out.println(type_name + sb);
      }

      if(short_format)
         out.flush();
      else
         end_report();
   }
   
   public static void association_matrix(TypedModel m)
   {      
      Vector libs = new Vector();
      Vector coding_patterns = new Vector();
      
      int pass_n = 0;
      for(Iterator i = m.subset_table_.all_handles(); i.hasNext(); ++pass_n)
      {         
         SubsetHandle curr = (SubsetHandle) i.next();
         SetOfClasses soc = new SetOfClasses(curr, m);
         if(soc.get_name().indexOf("-all-types") >= 0)
            continue;
         
         if(soc.size() <= 0)
            continue;
         
         if(soc.is_library())
            libs.add(soc);
         else
            coding_patterns.add(soc);
      }         
      
      Collections.sort(coding_patterns);

      for(Iterator i = libs.iterator(); i.hasNext(); )
      {
         SetOfClasses lib = (SetOfClasses) i.next();
         start_report("assocation-matrix", "lib=" + lib.get_name(), null);

         out_.print("C/P:");
         for(Iterator j = coding_patterns.iterator(); j.hasNext(); )
         {
            SetOfClasses curr = (SetOfClasses) j.next();
            out_.print(", " + curr.get_initials());         
         }
         out_.println();
         
         for(Iterator j = lib.class_handles(); j.hasNext(); )
         {
            ClassHandle ch = (ClassHandle) j.next();
            ClassSpec cs = ch.typed_value(m);
            
            int num_methods = cs.jc().getMethods().length;                                    
            StringBuffer sb = new StringBuffer();
            sb.append(", " + Common.METHOD_COUNT_PREFIX + num_methods);

            for(Iterator k = coding_patterns.iterator(); k.hasNext(); )
            {
               SetOfClasses curr = (SetOfClasses) k.next();

               int temp = curr.contains(ch) ? 1 : 0;
               String s = Integer.toString(temp);
               
               sb.append("," + s);
            }
            
            out_.println(ch.get_name() + sb);
         }

         end_report();
      }
   }
   
   public static void correlation_report(TypedModel m)
   {
      start_report("Correlation");
      
      int limit_ = -1;
      int[] buckets_ = new int[100];
      HashMap type2count_ = new HashMap();
      Vector all_sets_ = new Vector();
      Vector all_libs_ = new Vector();
      
      SetOfClasses all_types = null;
      
      int total = 0;
      
      for(Iterator i = m.subset_table_.all_handles(); i.hasNext(); )
      {         
         SubsetHandle curr = (SubsetHandle) i.next();
         SetOfClasses soc = new SetOfClasses(curr, m);
         
         if(all_types == null)
            all_types = soc;
         
         if(soc.size() <= 0)
            continue;
         
         total = Math.max(total, soc.size());

         if(soc.is_library())
            all_libs_.add(soc);
         else
            all_sets_.add(soc);
      }         
      
      Collections.sort(all_sets_);
      Collections.sort(all_libs_);

      
//      out_.print("Libs");
//      for(Iterator i = all_libs_.iterator(); i.hasNext(); )
//      {
//         SetOfClasses curr = (SetOfClasses) i.next();
//         out_.print(", " + curr.get_initials());            
//      }
//
//      out_.print("Class");
//      for(Iterator i = all_sets_.iterator(); i.hasNext(); )
//      {
//         SetOfClasses curr = (SetOfClasses) i.next();
//         out_.print(", " + curr.get_initials());            
//      }
//      
//      out_.println();
//      
      
      for(Iterator i = all_types.class_handles(); i.hasNext(); )
      {
         ClassHandle h = (ClassHandle) i.next();
         
//         out_.print(cs.name());
         int n = 0;
         
         for(Iterator j = all_sets_.iterator(); j.hasNext(); )
         {
            SetOfClasses curr = (SetOfClasses) j.next();
   
            if(curr.contains(h))
               n += 1;
                             
//            out_.print(", " + zero_or_one);
         }
         
//         out_.println();

         type2count_.put(h, new Integer(n));      
         buckets_[n] += 1;
      
         limit_ = Math.max(n, limit_);
      }
      
      for(Iterator j = all_libs_.iterator(); j.hasNext(); )
      {
         SetOfClasses lib = (SetOfClasses) j.next();
         print_result_of(lib, all_sets_, total);
      }
      

      
      //
      // Compute general params table for each library
      //     
      print_blank_line();
      
      out_.println("\\begin{table}[htbp]"
         + "\\begin{tabular}{|c|c|c|c|} \\hline "
         + "Library & Packages & Classes & Methods\\\\ \\hline");
      for(Iterator j = all_libs_.iterator(); j.hasNext(); )
      {
         SetOfClasses lib = (SetOfClasses) j.next();
         
         int pn = 0;
         for(Iterator p = lib.all_packages(); p.hasNext(); )
         {
            p.next();
            pn += 1;            
         }
         
         int mn = 0;
         for(Iterator mi = lib.all_methods(m); mi.hasNext(); )
         {
            mi.next();
            mn += 1;
         }
         
         
         out_.println(lib.get_name() + " & " + pn + " & " + lib.size() 
            + " & " + mn + "\\\\");          
      }

      out_.println("\\hline \\end{tabular} \\caption{this is the table}" 
            + " \\label{table-xyz}\\end{table}");
            
      //
      // Compute frequency table (coding-pattern occurences x libs) 
      // Use absolute values
      //     
      print_blank_line();
      
      out_.println("\\begin{table}[htbp]"
         + "\\begin{tabular}{|l|r|r|r|r|r|r|r|r|} \\hline Library ");
            
      for(int i = 0; i <= limit_; ++i)
         out_.print("& " + i);
      
      out_.println("\\\\ \\hline");
      
      for(Iterator j = all_libs_.iterator(); j.hasNext(); )
      {
         SetOfClasses lib = (SetOfClasses) j.next();

         int[] buckets = new int[limit_ + 1];
         for(Iterator i = lib.class_handles(); i.hasNext(); )
         {
            ClassHandle curr = (ClassHandle) i.next();
            
            Integer n = (Integer) type2count_.get(curr);
            
            int index = 0;
            if(n != null)
               index = n.intValue();
                        
            buckets[index] += 1;         
         }
         
         
         out_.print(lib.get_name());
         for(int i = 0; i <= limit_; ++i)
            out_.print("& " + to_latex(buckets[i]));
         
         out_.println("\\\\");
      }
      
      out_.println("\\hline \\end{tabular} "
         + "\\caption{Pattern Occurrences in a Class}"
         + "\\label{tab:pat-occ-in-class}\\end{table}");

      
      //
      // Compute frequency table (coding-pattern occurences x libs) 
      // Use Percent value
      //     
      print_blank_line();
      
      out_.print("\\begin{table}[htbp]\\begin{tabular}{|l|");
      for(int i = 0; i <= limit_; ++i)
         out_.print("r@{.}l|");
      
      out_.println("} \\hline Library ");            
      for(int i = 0; i <= limit_; ++i)
         out_.print("& \\multicolumn{2}{c|}{" + i + "}");
      
      out_.println("\\\\ \\hline");
      
      for(Iterator j = all_libs_.iterator(); j.hasNext(); )
      {
         SetOfClasses lib = (SetOfClasses) j.next();
         int whole = lib.size();

         int[] buckets = new int[limit_ + 1];
         for(Iterator i = lib.class_handles(); i.hasNext(); )
         {
            ClassHandle curr = (ClassHandle) i.next();
            
            Integer n = (Integer) type2count_.get(curr);
            
            int index = 0;
            if(n != null)
               index = n.intValue();
                        
            buckets[index] += 1;         
         }
         
         
         out_.print(lib.get_name());
         for(int i = 0; i <= limit_; ++i)
         {
            float f = buckets[i] * 100.0f / whole;
            out_.print("& " + to_latex(f, 1, 1) + "\\%");
         }
         
         out_.println("\\\\");
      }
      
      out_.println("\\hline \\end{tabular} "
         + "\\caption{Pattern Occurrences in a Class - Percentage}"
         + "\\label{tab:percent-occ-in-class}\\end{table}");
      
      end_report();
   }
   
   static private Vector slice_sets_with(SetOfClasses lib, Vector all_sets_)
   {      
      Vector result = new Vector();

      for(Iterator i = all_sets_.iterator(); i.hasNext(); )
      {
         SetOfClasses curr = (SetOfClasses) i.next();
         SetOfClasses sliced = curr.intersection(lib);
         
         result.add(sliced);
      }               
      
      return result;
   }
   
   static private void comment(String s)
   {
      out_.println("%" + s);
   }
   
   static private void print_result_of(SetOfClasses lib, Vector all_sets_, 
      int total)
   {          
      print_blank_line();
      comment("***************************************************");
      comment("*                                                 *");
      comment("* Library=" + lib);
      comment("*                                                 *");
      comment("***************************************************");
      print_blank_line();
      
      
      
      Vector sets = slice_sets_with(lib, all_sets_);
      
//      // 
//      // Print frequency table
//      //
//      for(int i = 0; i <= limit_; ++i)
//         out_.println(i + ", " +  buckets_[i]); 
//      
//      print_blank_line();

      //
      // Print coverage/unique/shared table
      //
      
      out_.println("\\begin{table}[htbp] \\begin{center}" +
            " \\begin{tabular}{|l|r r@{.}l|r r@{.}l|r r@{.}l|} \\hline" +
            " Coding pattern" + 
            " & \\multicolumn{3}{c|}{\\#Members}" +
            " & \\multicolumn{3}{c|}{\\#Shared}" +
            " & \\multicolumn{3}{c|}{\\#Unique}" +
            "\\\\ \\hline");
    
      for(Iterator i = sets.iterator(); i.hasNext(); )
      {
         SetOfClasses si = (SetOfClasses) i.next();
         SetOfClasses overlapped = new SetOfClasses(); //(SetOfClasses) si.clone();
         
         for(Iterator j = sets.iterator(); j.hasNext(); )
         {
            SetOfClasses sj = (SetOfClasses) j.next();
            if(sj == si)
               continue;
            
            overlapped = overlapped.union(sj);            
         }
         
         overlapped = si.intersection(overlapped);
         SetOfClasses diff = si.diff(overlapped);
         
         float mem_per = si.size() * 100.0f / total;
         float sh_per = overlapped.size() * 100.0f / si.size();
         float unique_per = diff.size() * 100.0f / si.size();
         
         
         out_.println(si.get_initials() + " & " + to_latex(si.size())
            + " & (" + to_latex(mem_per, 1, 1) + "\\%)"                      
            + " & " + to_latex(overlapped.size())
            + " & (" + to_latex(sh_per, 1, 1) + "\\%)"
            + " & " + to_latex(diff.size()) 
            + " & (" + to_latex(unique_per, 1, 1) + "\\%)"
            + "\\\\"); 
      }

      out_.println("\\hline \\end{tabular} " 
            + "\\caption{Size of coding-patterns in " + lib.get_name() 
            + ".} \\label{tab:cpin-" + lib.get_name() + "}" 
            + "\\end{center} \\end{table}");
      
      print_blank_line();
      
      //
      // Print pair-wise correlation factor
      //
      
      out_.print("\\begin{table}[htbp] \\begin{tabular}{|l|");
      for(Iterator i = sets.iterator(); i.hasNext(); i.next())
         out_.print("r|");
      
      out_.println("}");
      
      out_.print("\\hline");
      for(Iterator i = sets.iterator(); i.hasNext(); )
      {
         SetOfClasses curr = (SetOfClasses) i.next();
         out_.print(" & " + curr.get_initials());         
      }
      out_.println("\\\\ \\hline");
      
      for(Iterator i = sets.iterator(); i.hasNext(); )
      {
         SetOfClasses si = (SetOfClasses) i.next();         
         out_.print(si.get_initials()); // si.get_name());
         
         boolean skip = false;
         for(Iterator j = sets.iterator(); j.hasNext(); )
         {
            SetOfClasses sj = (SetOfClasses) j.next();
            
            if(skip)
            {
               out_.print(" & ");
               continue;
            }
            
            // ... Else:           
            double correl = SetOfClasses.find_correl(si, sj, total);
            correl = Math.round(correl * 10.0) / 10.0;
   
            out_.print("& " + correl);      

            if(sj == si)
               skip = true;
         }
         
         out_.println("\\\\");      
      }

      out_.println("\\hline \\end{tabular} \\caption{Pair-wise correlation in " 
         + lib.get_name() + "} \\label{tab:w1" + lib.get_name() + "} \\end{table}");
      print_blank_line();      
   }

   private static void print_blank_line()
   {
      out_.println("%");
      out_.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
      out_.println("%");
   }

   
   private static String to_latex(int n)
   {
      return to_latex(n, 0, 0);
   }

   private static String to_latex(float f)
   {
      return to_latex(f, 1);
   }


   private static String to_latex(float f, int decimal_digits, 
         int min_decimal_digits)
   {
      NumberFormat formatter = NumberFormat.getNumberInstance();
      formatter.setMinimumFractionDigits(min_decimal_digits);
      formatter.setMaximumFractionDigits(decimal_digits);
      
      String result = formatter.format(f);
      
      result = result.replace('.', '&');
      
      return result;
      
   }
   
   private static String to_latex(float f, int decimal_digits)
   {
      return to_latex(f, decimal_digits, 0);
   }
   
}
