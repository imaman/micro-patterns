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

import il.ac.technion.jima.JimaMisc;

import java.util.Iterator;
import java.util.Vector;


public class BasicTree
{
   public BasicTree(INode root)
   {
      root_ = root;
   }
   
   private INode root_;
   
   public INode gerRoot()
   {
      return root_;
   }
   
   public void print_pre_order()
   {
      print_pre_order_impl("", root_);
   }
   
   private static void print_pre_order_impl(String prefix, INode n)
   {
      if(n.getData() == null)
         return;
         
      System.out.println(prefix + n.getData().toString());
      Iterator iter = n.getChildrenIter();
      while(iter.hasNext())
      {
         INode curr = (INode) iter.next();
         print_pre_order_impl(prefix + "  ", curr);         
      }
   }

   public Iterator preOrderWalk()
   {
      Vector temp = new Vector();
      preOrderImpl(temp, root_);

      return temp.iterator();
   }

   private void preOrderImpl(Vector trg, INode n)
   {
      
      JimaMisc.ensure(n != null);
      trg.add(n);
      
      Iterator iter = n.getChildrenIter();
      while (iter.hasNext())
      {
         INode curr = (INode) iter.next();
         preOrderImpl(trg, curr);
      }
   }
   
   public static void main(String[] args)
   {
   }
}
