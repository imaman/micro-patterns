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


package il.ac.technion.jima.forest;

import java.util.Iterator;
import java.util.Vector;

public class BasicNode implements INode
{
   public BasicNode(BasicNode par, Object data)
   {
      par_ = par;
      children_ = new Vector();
      data_ = data;
   }
   
   private BasicNode par_;
   private Vector children_;
   private Object data_;
   
   public INode getParent()
   {
      return par_;
   }
   
   public Iterator getChildrenIter()
   {
      return children_.iterator();
   }
   
   public BasicNode newChild(Object dataOfChild)
   {
      BasicNode result = new BasicNode(this, dataOfChild);
      children_.add(result);
      
      return result;
   }
   
   public Object getData()
   {
      return data_;
   }
   
   public void setData(Object newData)
   {
      data_ = newData;
   }
   
   public static BasicNode create(BasicNode parent, Object data)
   {
      if(parent == null)
         return new BasicNode(parent, data);
      
      // ...Else:
      BasicNode result = parent.newChild(data);
      return result;
   }

   public static void main(String[] args)
   {
      BasicNode root = new BasicNode(null, "Itay");
      BasicNode yon = root.newChild("Yon");
      BasicNode chenik = root.newChild("Chenik");
      
      BasicNode shakhaf = yon.newChild("Shakhaf");
      BasicNode daph = yon.newChild("Daph");

      BasicNode elad = daph.newChild("Elad");
      
      BasicTree bt = new BasicTree(root);
      bt.print_pre_order();
   }
}
