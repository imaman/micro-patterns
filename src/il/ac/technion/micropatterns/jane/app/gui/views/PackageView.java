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
import il.ac.technion.jima.util.Collections;
import il.ac.technion.jima.util.StringIterator;
import il.ac.technion.micropatterns.jane.app.gui.AbstractViewManager;
import il.ac.technion.micropatterns.jane.app.gui.IJaneViewListener;
import il.ac.technion.micropatterns.jane.app.gui.nodes.PackageNode;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;


public class PackageView extends AbstractViewManager
{
   
   public PackageView(PowerTable pt, IJaneViewListener listener, 
      IWidthListener width_listener)
   {
      super(pt, listener, width_listener);   
   }


   protected void assign_model_impl(TypedModel m)
   {
      root_.remove();

      HashSet packages = new HashSet();
      for(Iterator i = m.class_table_.all_handles(); i.hasNext(); )
      {
         ClassHandle curr = (ClassHandle) i.next();
         String str = curr.get_name();
         String package_name = JavaSpec.package_name_of(str);
         
         packages.add(package_name);
      }
      
      Object[] temp = packages.toArray();
      Arrays.sort(temp);
      
      for(StringIterator i = new StringIterator(Collections.makeIter(temp)); 
         i.hasNext(); )
      {         
         String curr = i.nextString();
         root_.add_child(new PackageNode(curr));
      }               
   }
}
