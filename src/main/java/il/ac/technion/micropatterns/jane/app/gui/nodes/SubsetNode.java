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
import il.ac.technion.micropatterns.jane.app.gui.rows.SubsetRow;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.SubsetElement;
import il.ac.technion.micropatterns.jane.elements.SubsetHandle;
import il.ac.technion.micropatterns.jane.typedmodel.SetOfClasses;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.util.Iterator;


public class SubsetNode extends AbstractNode
{
   private SubsetHandle sp_;
   private SetOfClasses[] subsets_;
   public int covered_ = 0;
   private SetOfClasses me_;
   private int universe_size_ = 0;
   
   public SubsetNode(SetOfClasses soc, SetOfClasses[] subsets, TypedModel m)
   {
      subsets_ = subsets;
      sp_ = soc.sh_;
      me_ = soc;
      
      SetOfClasses join = new SetOfClasses();
      for(int i = 0; i < subsets_.length; ++i)
      {
         SetOfClasses curr = subsets_[i];
         
         // Assume the "all-types" set is the first one
         if(i == 0)
            universe_size_ = curr.size(); 
         
         if(curr == soc)
            continue;

         if(curr.is_library())
            continue;

         SetOfClasses common = soc.intersection(curr);
         
         join = join.union(common);

//          JimaMisc.log().println("join += " + curr + ". added items=" 
//             + common.size());                   
      }
            
      covered_ = join.size();
   }
   
   /**
    * @see il.ac.technion.micropatterns.jane.app.gui.nodes.INode#create_row(il.ac.technion.micropatterns.jane.app.gui.IView, int, il.ac.technion.jima.IWidthListener)
    */
   public AbstractRow create_row(IView v, int index, IWidthListener wl)
   {
      return new SubsetRow(this, v, get_depth(), index, wl, sp_);      
   }

   /**
    * @see il.ac.technion.micropatterns.jane.app.gui.nodes.INode#match(java.lang.String)
    */
   public boolean match(String s)
   {
      int pos = this.sp_.pretty_name().toLowerCase().indexOf(s);
      return pos >= 0;
   }
   
   public void members_on_off(IView v)
   {
      Class c = LeftTypeNode.class;
      
      if(this.is_expanded(c))
      {
         v.collapse(this, c);
         return;
      }
      
      // ...Else:
      SubsetElement se = sp_.typed_value(v.model());

      for(Iterator i = se.handles(); i.hasNext(); )
      {
         ClassHandle curr = (ClassHandle) i.next();
         LeftTypeNode new_one = new LeftTypeNode(curr);
         
         this.add_child(new_one);
      }
      
      v.expand(this, c);
   }
   
   
   public void intersections_on_off(IView v)
   {
      Class c = TwoWayDiffNode.class;
            
      if(this.is_expanded(c))
      {
         v.collapse(this, c);
         return;
      }
               
      // ...Else:               
      for(int i = 0; i < subsets_.length; ++i)
      {
         SetOfClasses curr = subsets_[i];            
         
         if(curr == this.me_)
            continue;

         if(curr.is_library())
            continue;
                        
         TwoWayDiffNode new_one = new TwoWayDiffNode(me_, curr, universe_size_);
         this.add_child(new_one);
      }
               
      v.expand(this, c);
   }
   
   public void unqiue_on_off(IView v)
   {
      Class c = RightTypeNode.class;
      
      if(this.is_expanded(c))
      {
         v.collapse(this, c);
         return;
      }
      
      
      // ...Else:      
      SetOfClasses join = new SetOfClasses();
      for(int i = 0; i < subsets_.length; ++i)
      {
         SetOfClasses curr = subsets_[i];            
         
         if(curr == this.me_)
            continue;
         
         if(curr.is_library())
            continue;
         
         join = join.union(curr);
      }
      
      SetOfClasses diff = me_.diff(join);

      for(Iterator i = diff.class_handles(); i.hasNext(); )
      {
         ClassHandle h = (ClassHandle) i.next();
         RightTypeNode new_one = new RightTypeNode(h);
         
         this.add_child(new_one);
      }
               
      v.expand(this, c);
   }
}
