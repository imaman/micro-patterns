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











package il.ac.technion.micropatterns.jane.analysis.misc;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.elements.SubsetElement;
import il.ac.technion.micropatterns.jane.model.IHandle;
import il.ac.technion.micropatterns.jane.typedmodel.IResultKeeeper;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Result
{
   private Result next_ = null;
   private HashMap chosen_ = null;
//   private String result_name_ = null;
//   private String initials_ = null;
   private DescCP dcp_;

   public Result() { }
         
   public static Result new_result(IAgent agent, DescCP dcp)
   {
      Result result = new Result();
      
      result.chosen_ = new HashMap();
      result.dcp_ = dcp;
      
//      result.result_name_ = result_name;
//      result.initials_ = initials;
      
      for(Result curr = agent.first_result(); true; curr = curr.next_)
      {
         if(curr.next_ != null)                              
            continue;

         // ...Else:            
         curr.next_ = result;
         break;
      }
      
      return result;
   }
   
   public String all_initials(String prefix)
   {
      String lhs = "";
      if(this.dcp_ == null)
      {
         if(this.next_ != null)
            return this.next_.all_initials(prefix);
         else
            return prefix;
      }
      
         
      lhs = this.dcp_.get_initials();      
      if(this.next_ == null)
         return prefix + lhs;
      
      // ...Else:
      return this.next_.all_initials(prefix + lhs + ",");
   }
   
   public boolean is_null_result()
   {
      return chosen_ == null;
   }
   
   public void set_decision(IHandle h, boolean yes_no)
   {
      chosen_.put(h, yes_no ? Boolean.TRUE : Boolean.FALSE);
   }
   
   public Decision get_decision(IHandle h)
   {
       return new Decision((Boolean) chosen_.get(h));
   }

   public static void save(IAgent agent, IResultKeeeper m) throws IOException
   {
      agent.first_result().save(m);
   }
   
   
   private void save_impl(IResultKeeeper m) 
   {
//      SubsetHandle h = m.get_subset_proxy(result_name_);      

      SubsetElement se = new SubsetElement();
      se.set_initials(dcp_.get_initials());
      
      int n = 0;
      for(Iterator i = chosen_.entrySet().iterator(); i.hasNext(); )
      {
         Map.Entry curr = (Map.Entry) i.next();
         
         IHandle h = (IHandle) curr.getKey();
         Decision d = new Decision((Boolean) curr.getValue());
         
         if(d.is_positive())
         {
            se.add(h);
            n += 1;
         }
      }
      
      JimaMisc.log().println("Patt " + this.dcp_.get_initials() + ": " + n 
         + " members");

      m.add_result_set(this.dcp_, se);
//      m.subset_table_.add(m, result_name_, se);
   }
   
   public void save(IResultKeeeper m) throws IOException
   {            
      if(!is_null_result())
         save_impl(m);
               
      if(next_ != null)
         next_.save(m);
   }
   
   
   
}
