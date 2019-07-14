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
import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.app.gui.IView;
import il.ac.technion.micropatterns.jane.app.gui.rows.AbstractRow;
import il.ac.technion.micropatterns.jane.app.gui.rows.FieldRow;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.lib.JaneMisc;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import javax.swing.JOptionPane;

import org.apache.bcel.classfile.Field;

public class FieldNode extends AbstractNode
{
   private Field f_;
   
   public FieldNode(Field f)
   {
      f_ = f;
   }

   public boolean match(String s)
   {
      return f_.getName().compareToIgnoreCase(s) == 0;
   }
   
   public AbstractRow create_row(IView v, int index, 
      IWidthListener wl)
   {
      return new FieldRow(this, v, get_depth(), index, f_, wl);
   }
   
   public void type_on_off(IView v)
   {
      Class c = ClassNode.class;
      if(is_expanded(c))
         v.collapse(this, c);
      else
      {         
         String tn = JaneMisc.signature_to_text(f_.getSignature());
         TypedModel m = v.model();
   
         ClassHandle ch = (ClassHandle) m.class_table_.lookup_handle(tn);
         if(ch == null)
         {            
            JOptionPane.showMessageDialog(null, "Not found");
            return;
         }
                     
         JimaMisc.ensure(ch != null);
           
         ClassNode new_one = new ClassNode(ch);   
         this.add_child(new_one);
         
         v.expand(this, c);
      }
   }
}
