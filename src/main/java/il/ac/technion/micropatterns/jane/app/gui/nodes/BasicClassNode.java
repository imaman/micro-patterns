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

import il.ac.technion.micropatterns.jane.app.gui.IView;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.elements.MethodSpec;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.util.Iterator;

import org.apache.bcel.classfile.Field;

public abstract class BasicClassNode extends AbstractNode
{
   protected void fields_on_off_impl(IView v, ClassSpec ce)
   {
      Class c = FieldNode.class;
      
      if(is_expanded(c))
         v.collapse(this, c);
      else
      {
//         ClassElement ce = (ClassElement) v.model().get_element(cp_);
         
         Field[] fields = ce.jc().getFields();
         
         for(int i = 0; i < fields.length; ++i)
         {
            Field f = fields[i];
            FieldNode new_one = new FieldNode(f);
            
            this.add_child(new_one);
         }         
         v.expand(this, c);
      }            
   }
   
   protected void methods_on_off_impl(IView v, ClassSpec ce)
   {
      Class c = MethodNode.class;
      
      if(is_expanded(c))
         v.collapse(this, c);
      else
      {
         TypedModel model = v.model();
         
         for(Iterator i = ce.all_methods(); i.hasNext(); )
         {
            MethodSpec curr = (MethodSpec) i.next();
            MethodNode new_one = new MethodNode(model, curr);
            
            this.add_child(new_one);
         }
                     
//         Method[] methods = ce.jc().getMethods();
//         
//         for(int i = 0; i < methods.length; ++i)
//         {
//            MethodSpec temp = new MethodSpec(ce, i);
//            MethodNode new_one = new MethodNode(model, temp);
//            
//            this.add_child(new_one);
//         }         

         v.expand(this, c);
      }            
   }   
}
