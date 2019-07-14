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









package il.ac.technion.micropatterns.jungle.model;

import il.ac.technion.jima.util.Collections;
import il.ac.technion.micropatterns.janeutils.impl.CodingPattern;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;
import il.ac.technion.micropatterns.janeutils.impl.Record;
import il.ac.technion.micropatterns.janeutils.impl.Reduction;

import java.util.HashSet;
import java.util.Iterator;


public class Clusterization
{
   private Cluster[] clusters_ = new Cluster[DescCP.size()];
   private HashSet type_names_ = new HashSet();
   
   
   public Clusterization()
   {
      for(int i = 0; i < clusters_.length; ++i)
         clusters_[i] = new Cluster(DescCP.get(i));
   }
   
   public int size()
   {
      return type_names_.size();
   }
   
   public Cluster get_cluster(DescCP dcp)
   {      
      return clusters_[dcp.get_id()];      
   }
   
   public Iterator clusters()
   {
      return Collections.makeIter(clusters_);
   }
   
   public int extract_from(Reduction r)
   {
      HashSet new_ones = new HashSet();
      
      for(Iterator i = this.clusters(); i.hasNext(); )
      {
         Cluster trg = (Cluster) i.next();
         DescCP dcp = trg.get_desc();
         
         CodingPattern cp = r.get(dcp);
         
         for(Iterator j = cp.as_set().iterator(); j.hasNext(); )
         {
            Record rec = (Record) j.next();
            
            trg.add(rec.type_name());            
            new_ones.add(rec.type_name());
         }
      }
      
      int result = 0;
      for(Iterator i = r.iterator(); i.hasNext(); )
      {
         Record rec = (Record) i.next();
         if(type_names_.add(rec.type_name()))
            continue;
         result += 1;
      }
      
      return result;
   }   
}
