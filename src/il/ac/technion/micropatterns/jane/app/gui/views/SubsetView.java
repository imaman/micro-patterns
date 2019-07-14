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











package il.ac.technion.micropatterns.jane.app.gui.views;

import il.ac.technion.jima.IWidthListener;
import il.ac.technion.jima.powergui.PowerTable;
import il.ac.technion.micropatterns.jane.app.gui.IJaneViewListener;
import il.ac.technion.micropatterns.jane.app.gui.nodes.SubsetNode;
import il.ac.technion.micropatterns.jane.elements.SubsetHandle;
import il.ac.technion.micropatterns.jane.typedmodel.SetOfClasses;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;


public class SubsetView extends ClassView
{

   /**
    * @param pt
    * @param listener
    * @param width_listener
    */
   public SubsetView(PowerTable pt, IJaneViewListener listener,
      IWidthListener width_listener)
   {
      super(pt, listener, width_listener);
   }
   

   public static class MyComp implements Comparator
   {   
      public MyComp(TypedModel m)
      {
         m_ = m;
      }
      
      private TypedModel m_;
      
      public int compare(Object o1, Object o2)
      {
         SetOfClasses lhs = (SetOfClasses) o1;
         SetOfClasses rhs = (SetOfClasses) o2;
         
         if(lhs.is_library() && rhs.is_library())
            return lhs.sh_.get_name().compareTo(rhs.sh_.get_name());
            
         if(!lhs.is_library() && !rhs.is_library())
            return lhs.sh_.get_name().compareTo(rhs.sh_.get_name());
         
         if(lhs.is_library())
            return -1;
            
         // ...Else:
         return 1;            
      }
   }
   
   protected void assign_model_impl(TypedModel m)
   {
      root_.remove();
      
      Vector temp = new Vector();
      for(Iterator i = m.subset_table_.all_handles(); i.hasNext(); )
      {
         SubsetHandle curr = (SubsetHandle) i.next();
         SetOfClasses soc = new SetOfClasses(curr, m);
         temp.add(soc);
      }
      
      Collections.sort(temp, new MyComp(m));

      SetOfClasses[] sets = new SetOfClasses[temp.size()];
      for(int i = 0; i < sets.length; ++i)
         sets[i] = (SetOfClasses) temp.elementAt(i);
      
      for(Iterator i = temp.iterator(); i.hasNext(); )
      {
         SetOfClasses curr = (SetOfClasses) i.next();
         SubsetNode ssn = new SubsetNode(curr, sets, m);

         root_.add_child(ssn);
      }         
   }
}
