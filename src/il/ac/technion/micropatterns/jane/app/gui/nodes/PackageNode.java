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
import il.ac.technion.micropatterns.jane.app.gui.rows.PackageRow;

public class PackageNode extends AbstractNode
{

   public String package_name_;
   
   public PackageNode(String package_name)
   {
      package_name_ = package_name;
   }
   
   
   /* (non-Javadoc)
    * @see il.ac.technion.micropatterns.jane.app.gui.nodes.INode#match(java.lang.String)
    */
   public boolean match(String s)
   {
      boolean result = package_name_.compareToIgnoreCase(s) == 0;
      return result;
   }

   /* (non-Javadoc)
    * @see il.ac.technion.micropatterns.jane.app.gui.nodes.INode#create_row(il.ac.technion.micropatterns.jane.app.gui.IView, int, jima.IWidthListener)
    */
   public AbstractRow create_row(IView v, int index, IWidthListener wl)
   {
      return new PackageRow(this, v, get_depth(), index, wl);
   }

}
