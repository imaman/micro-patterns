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

import java.util.HashMap;
import java.util.Iterator;

public class KeyedNode implements INode
{
   public KeyedNode(KeyedNode par, Object key, Object data)
   {
      key_ = key;
      data_ = data;
      par_ = par;
      
      children_ = new HashMap();      
   }
   
   private Object key_;
   private Object data_;
   private HashMap children_;
   private KeyedNode par_;

   public Object getKey()
   {
      return key_;
   }
   
   public Object getData()
   {
      return data_;
   }

   public void setData(Object newData)
   {
      data_ = newData;
   }
    
   public KeyedNode newChild(Object key, Object dataOfChild)
   {
      KeyedNode result = new  KeyedNode(this, key, dataOfChild);
      children_.put(result.getKey(), result);
      
      return result;      
   }
   
   public KeyedNode getChild(Object key)
   {
      KeyedNode result = (KeyedNode) children_.get(key);
      return result;      
   }
   
   public INode getParent() 
   { 
      return par_;
   }
   
   public Iterator getChildrenIter() 
   {
      return children_.values().iterator();
   }
   
   

   private static KeyedNode create(KeyedNode par, String name, int num)
   {
      Integer anInt = new Integer(num);
      if(par == null)
      {
         // Create the root node
         KeyedNode result = new KeyedNode(par, name, anInt);
         return result;        
      }
      
      // ...Else:
      KeyedNode result = par.newChild(name, anInt);
      return result;
   }
   
   public static void main(String[] args)
   {
      KeyedNode root = create(null, "itay", 965288);

      KeyedNode chen = create(root, "chenik", 673585);
      KeyedNode t1 = create(root, "t10", 10010);
      KeyedNode t2 = create(root, "t20", 10020);
      KeyedNode t3 = create(root, "t15", 10015);
      KeyedNode t4 = create(root, "t18", 10018);
      
      KeyedNode yon = create(root, "yon", 944849);
      KeyedNode shakhaf = create(yon, "shakhaf", -1);
      KeyedNode tzoofa = create(shakhaf, "tzoofa", 796591);
      
      KeyedNode n = root.getChild("yon");
      n = n.getChild("shakhaf");
      n = n.getChild("tzoofa");
      
      System.out.println(n.getKey() + ": " + n.getData().toString());
      
      BasicTree br = new BasicTree(root);
      
      
      System.out.println("===============================");
      Iterator iter = br.preOrderWalk();      
      while(iter.hasNext())
      {
         KeyedNode kn = (KeyedNode) iter.next();
         System.out.println(kn.getKey() + "," + kn.getData());         
      }
      
      System.out.println("===============================");
      br.print_pre_order();
      
   }
}
