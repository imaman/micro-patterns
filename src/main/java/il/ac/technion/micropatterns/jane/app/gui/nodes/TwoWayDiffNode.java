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











package il.ac.technion.micropatterns.jane.app.gui.nodes;

import il.ac.technion.jima.IWidthListener;
import il.ac.technion.micropatterns.jane.app.gui.IView;
import il.ac.technion.micropatterns.jane.app.gui.rows.AbstractRow;
import il.ac.technion.micropatterns.jane.app.gui.rows.TwoWayDiffRow;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.SubsetHandle;
import il.ac.technion.micropatterns.jane.typedmodel.SetOfClasses;

import java.util.Iterator;


public class TwoWayDiffNode extends AbstractNode
{   
   public SubsetHandle lhs_;
   public SubsetHandle rhs_;
   public SetOfClasses soc_lhs_;
   public SetOfClasses soc_rhs_;
   
   public int num_of_commons_;
   public int num_of_left_only_;
   public int num_of_right_only_;
   public int num_of_left_;
   
   public int universe_size_;

      
   public TwoWayDiffNode(SetOfClasses soc_lhs, SetOfClasses soc_rhs, 
      int universe_size) 
   {
      universe_size_ = universe_size;
      
      soc_lhs_ = soc_lhs;
      soc_rhs_ = soc_rhs;
      
      lhs_ = soc_lhs_.sh_;
      rhs_ = soc_rhs_.sh_;
   
      num_of_commons_ = find_common().size();     
      num_of_left_only_ = find_a_minus_b(soc_lhs_, soc_rhs_).size();
      num_of_right_only_ = find_a_minus_b(soc_rhs_, soc_lhs_).size();
      num_of_left_ = soc_lhs_.size();
   }
   
   private SetOfClasses find_common()
   {
      SetOfClasses result = soc_lhs_.intersection(soc_rhs_);
      return result;
   }

   private SetOfClasses  find_a_minus_b(SetOfClasses a, SetOfClasses b)
   {
      SetOfClasses result = a.diff(b);
      return result;
   }
   
   public boolean match(String s)
   {
      return false;
   }
   
   public AbstractRow create_row(IView v, int row_n, IWidthListener wl)
   {
      return new TwoWayDiffRow(this, v, get_depth(), row_n, wl);
   }

   public void right_types_on_off(IView v)
   {
      Class c = RightTypeNode.class;    
      
      if(this.is_expanded(c))
      {
         v.collapse(this, c);
         return;
      }

      // ...Else: Expand
               
      SetOfClasses soc = find_a_minus_b(soc_rhs_, soc_lhs_);
      
      for(Iterator i = soc.class_handles(); i.hasNext(); )
      {
         ClassHandle curr = (ClassHandle) i.next();
         
         AbstractNode new_one = new RightTypeNode(curr);
         this.add_child(new_one);
      }
         
      v.expand(this, c);
   }

   public void left_types_on_off(IView v)
   {
      Class c = LeftTypeNode.class;    
      
      if(this.is_expanded(c))
      {
         v.collapse(this, c);
         return;
      }

      // ...Else: Expand               
      SetOfClasses soc = find_a_minus_b(soc_lhs_, soc_rhs_);      
      for(Iterator i = soc.class_handles(); i.hasNext(); )
      {
         ClassHandle curr = (ClassHandle) i.next();
         
         AbstractNode new_one = new LeftTypeNode(curr);
         this.add_child(new_one);
      }
         
      v.expand(this, c);

   }
   
   public void types_on_off(IView v)
   {
      Class c = ClassNode.class;
      
      if(this.is_expanded(c))
      {
         v.collapse(this, c);
         return;
      }
      
      // ...Else: Expand
      SetOfClasses soc = find_common();
      for(Iterator i = soc.class_handles(); i.hasNext(); )
      {
         ClassHandle curr = (ClassHandle) i.next();

         ClassNode new_one = new ClassNode(curr);
         this.add_child(new_one);
      }
         
      v.expand(this, c);
   }
}
