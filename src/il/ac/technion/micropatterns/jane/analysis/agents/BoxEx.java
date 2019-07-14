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










package il.ac.technion.micropatterns.jane.analysis.agents;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.analysis.misc.AllTypesAgent;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.util.HashMap;

import org.apache.bcel.classfile.Field;


public class BoxEx extends AllTypesAgent
{
   private HashMap class_2_box_data_ = new HashMap();
   
   private static class BoxData implements Cloneable
   {
      public int primitive_fields_ = 0;
      public int class_fields_ = 0;

      public BoxData() { }
      
      public BoxData(int primitive_fields, int class_fields)
      {
         primitive_fields_ = primitive_fields;
         class_fields_ = class_fields;
      }
      
      public Object clone()
      {
         return new BoxData(primitive_fields_, class_fields_);
      }
   }
   
   public static final String NAME = "box2";

   public BoxEx()
   {
      super(NAME, DescCP.BX2);
   }
   
      
      

   public void visit(ClassHandle h)
   {
      boolean b = check(h);
      this.main_result_.set_decision(h, b);
   }
   
   private void put(ClassHandle h, BoxData bd)
   {
      class_2_box_data_.put(h, bd);
   }
   
   private BoxData get(ClassHandle h)
   {
      BoxData result = (BoxData) class_2_box_data_.get(h);
      return result;
   }
   
   private boolean check(ClassHandle h)
   {
      ClassHandle spr = h.typed_value(xmodel_).get_first_super();
           
      if(spr == null)
      {
         put(h, new BoxData());
         return false;
      }

      BoxData inherited = get(spr);
      JimaMisc.ensure(inherited != null, "No super data for "  + spr);
      
      
      BoxData curr = (BoxData) inherited.clone();
      ClassSpec ce = h.typed_value(xmodel_);
      
      Field[] f = ce.jc().getFields();
      for(int i = 0; i < f.length; ++i)
      {              
         if(f[i].isStatic())
            continue;
            
         String sig = f[i].getSignature();
         if(JavaSpec.is_primitive(sig))
            curr.primitive_fields_ += 1;
         else
            curr.class_fields_ += 1;
      }
      
      put(h, curr);
      boolean result = (curr.class_fields_ == 1) 
         && (curr.primitive_fields_ >= 1);
      
      return result;
      
   }
}  
