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









package il.ac.technion.micropatterns.janeutils.impl;

import il.ac.technion.micropatterns.janeutils.Defs;
import il.ac.technion.micropatterns.jungle.model.CollectionDesc;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class Reduction extends AbstractSet implements Comparable
{
   private Vector coding_patterns_ = new Vector();
   
   private final CollectionDesc coll_desc_;
   private String lib_;
   private String lib_init_;
   private HashSet classes_;
   private int method_count_ = 0;
   private HashSet covered_ = new HashSet();

   public Vector sorted_ = new Vector();
   
   
   public CollectionDesc get_coll_desc()
   {
      return coll_desc_;
   }

   public Reduction(AssocMat am)
   {
      this(am, new HashSet());
   }

   public Reduction(AssocMat am, HashSet negative_world)
   {
//      this.lib_ = am.get_lib();
      
      coll_desc_ = CollectionDesc.get(am.get_lib());
      this.lib_ = coll_desc_.get_base_name();
      
      lib_init_ = coll_desc_.get_latex_name(); // Defs.lib_to_latex(this.lib_);
      
      for(int i = 0; i < DescCP.size(); ++i)
      {
         CodingPattern cp = am.coding_pattern(i);        
         CodingPattern temp = cp.reduce(negative_world);
         
         coding_patterns_.add(temp);
      }
      
      this.covered_ = compute_coverage(this.coding_patterns_);
      
      classes_ = Sets.difference(am.as_set_of_classes(), negative_world);
      
      for(Iterator i = classes_.iterator(); i.hasNext(); )
      {
         Record curr = (Record) i.next();
         method_count_ += curr.get_method_count();
      }
   }
   
   public int get_method_count()
   {
      return method_count_;
   }
   
   private static HashSet compute_coverage(Vector cps)
   {
      HashSet result = new HashSet();
      
      for(int i = 0; i < DescCP.size(); ++i)
      {
         CodingPattern cp = (CodingPattern) cps.get(i);
         result = Sets.union(result, cp.as_set());
      }
      
      return result;
   }
   
   public Reduction(String lib_name, Iterator records, Collection world)
   {      
      this.classes_ = new HashSet();
      this.classes_.addAll(world);
      
      coll_desc_ = CollectionDesc.get(lib_name);
      this.lib_ = coll_desc_.get_base_name(); // lib_name;
      this.lib_init_ = coll_desc_.get_latex_name(); // Defs.lib_to_latex(this.lib_);
      
      coding_patterns_ = Defs.empty_coding_patterns();
      
            
      while(records.hasNext())
      {
         Record r = (Record) records.next();
         
         r.register(coding_patterns_);
      }
      
      this.covered_ = compute_coverage(this.coding_patterns_);
      
      
      for(Iterator i = this.covered_.iterator(); i.hasNext(); )
         sorted_.add(((Record) i.next()).type_name());
      
      Collections.sort(sorted_);
   }

   public int get_absolute_coverage()
   {
      int a = covered_.size();
      return a;
   }
   
   public double get_coverage()
   {
      int a = covered_.size();
      int b = classes_.size();
      
      return ImplUtils.to_percent(a, b);
   }
   
   public double coverge_of_cp(DescCP dcp)
   {
      CodingPattern cp = (CodingPattern) coding_patterns_.get(dcp.get_id());
      double d = (double) cp.size() / this.size();
      
      d = ImplUtils.to01(d);      
      return d;
   
   
   }
   
   public int size_of_cp(DescCP dcp)
   {
      CodingPattern cp = (CodingPattern) coding_patterns_.get(dcp.get_id());
      return cp.size();
   }
   
   
   
   
   public String toString()
   {
      return "Reduction of " + lib_;
   }
   
   public String get_lib()
   {
      return lib_;
   }
   
   public String get_lib_init()
   {
      return lib_init_;
   }
   
   public int hashCode()
   {
      return lib_.hashCode();
   }
   
   public boolean equals(Object other)
   {
      if(other == null)
         return false;
      
      if(other.getClass() != this.getClass())
         return false;
      
      
      return this.compareTo(other) == 0;
   }
   
   public CodingPattern get(int i)
   {
      return (CodingPattern) coding_patterns_.get(i);
   }
   
   public CodingPattern get(DescCP dcp)
   {
      return get(dcp.get_id());
   }
   
   public int compareTo(Object other)
   {
      Reduction that = (Reduction) other;
      
//      String s1 = this.get_lib_init();
//      String s2 = that.get_lib_init();
//      if(s1 == null || s2 == null)
//         throw new RuntimeException("s1=" + s1 + ", s2=" + s2 + ", this=" + this + ", that=" + that);
//
//      // ...else:
//      return s1.compareTo(s2);
      
      
      int result = CollectionSorter.compare_impl(this.get_lib_init(), 
         that.get_lib_init());
      
      return result;
      
   }
   
   
   
   /**
    * @see java.lang.Iterable#iterator()
    */
   public Iterator iterator() 
   {
      return classes_.iterator();
   }

   /**
    * @see java.util.Collection#size()
    */
   public int size()
   {
      return classes_.size();
   }
   
   public double angle_with(Reduction other)
   {
      double result = 0;
      double sa = 0;
      double sb = 0;
      for(int i = 0; i < DescCP.size(); ++i)
      {
         DescCP curr = DescCP.get(i);
         
         double a = this.coverge_of_cp(curr);
         double b = other.coverge_of_cp(curr);
         
         result += a*b;
         
         sa += a*a;
         sb += b*b;
      }
      
      sa = Math.sqrt(sa);
      sb = Math.sqrt(sb);
      
      result = result / (sa*sb);      
      return result;
   }
   
   public double size_with(Reduction other)
   {
      double result = 0;
      double sa = 0;
      double sb = 0;
      for(int i = 0; i < DescCP.size(); ++i)
      {
         DescCP curr = DescCP.get(i);
         
         double a = this.coverge_of_cp(curr);
         double b = other.coverge_of_cp(curr);
         
         result += a*b;
         
         sa += a*a;
         sb += b*b;
      }
      
      sa = Math.sqrt(sa);
      sb = Math.sqrt(sb);
      
      result = result / (sb * sb);
      result = Math.abs(result - 1);
      return result;
   }
   
   public static int size_of_union(Reduction lhs, Reduction rhs)
   {
      return Sets.union(lhs.classes_, rhs.classes_).size();
   }
   
}
