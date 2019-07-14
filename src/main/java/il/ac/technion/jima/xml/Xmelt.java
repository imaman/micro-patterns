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


package il.ac.technion.jima.xml;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

public class Xmelt
{

   /**
    * 
    */
   public Xmelt(String inst) throws Exception
   {
      System.out.println("inst=" + inst);

      StringTokenizer semicolons = new StringTokenizer(inst, ";");      
      while(semicolons.hasMoreTokens())
      {               
         Vector items = new Vector();
                           
         String sub = semicolons.nextToken();                           
         StringTokenizer slashes = new StringTokenizer(sub, "/");
         while(slashes.hasMoreTokens())
         {
            String curr = slashes.nextToken();         
            items.add(new Item(curr));         
         }       
         
         expressions_.add(items);  
      }      
   }
   
   private Vector expressions_ = new Vector();

   private class Worker
   {
      public Worker(Vector items)
      {
         expression_ = items;
      }
      private Vector paths_ = new Vector();      
      private Vector expression_;

      private void run_actions(Stack path) throws Exception
      {
         int top = path.size();
         
         for(int i = top - 1; i >= 0; --i)
         {
            XmlNode n = (XmlNode) path.elementAt(i);
            Item item = (Item) expression_.elementAt(i);
            
            item.invoke(path, i);
         }         
      }

      private void dfs_impl(Stack path, XmlNode n, int depth, boolean positive) 
         throws Exception
      {      
         if(depth == expression_.size())
         {
            if(positive)
               paths_.add(path.clone()); 
            return;
         }
   
         Item item = (Item) expression_.elementAt(depth);
            
         // ...Else:               
         
         int count = 0;
         int num_children = n.num_of_children();
         for(int i = 0; i < num_children; ++i)
         {
            XmlNode child = n.child(i);         
            if(!item.relevant(child))
               continue;
   
            count += 1;
            path.push(child);            
            dfs_impl(path, child, depth + 1, positive && item.is_positive);
            path.pop();
         }             
         
         if(item.is_and)
         {            
            if(item.is_positive && count == item.and_count)
               paths_.add(path.clone());
            else if(!item.is_positive && count != item.and_count)
               paths_.add(path.clone());
         }
                        
         
         if(count == 0 && !item.is_positive)
            paths_.add(path.clone());
      }
      
      public void process(XmlNode root) throws Exception
      {
         dfs_impl(new Stack(), root, 0, true);
         
         Iterator iter = paths_.iterator();
         while(iter.hasNext())
            run_actions((Stack) iter.next());                        
      }
   }
   
   private static class Item
   {
      public Item(String sub_inst) throws Exception
      {
         int index = sub_inst.indexOf(':');
         if(index < 0)
         {
            match_ = sub_inst;
         }         
         else
         {
            match_ = sub_inst.substring(index + 1);
            action_ = sub_inst.substring(0, index);
            
         }
         
         if(match_.length() <= 0)
            throw new Exception("incorrect match_ value");

         if(match_.startsWith("!"))
         {
            match_ = match_.substring(1);
            is_positive = false;
         }
         
         if(match_.startsWith("-"))
         {
            match_ = match_.substring(1);
            is_not = false;
         }
         
         if(match_.indexOf('&') >= 0)
         {
            add_words(match_, "&");
            is_last = true;
            is_and = true;
            and_count = words_.size();
         }            
         else 
            add_words(match_, "|");

         System.out.println("m=" + match_ + ", a=" + action_);
      }
      
      public String match_ = null;
      public String action_ = null;
      public boolean is_positive = true;

      public boolean is_last = false;
      public boolean is_and = false;
      public boolean is_not = false;
      public int and_count = -1;
      
      private Vector words_ = new Vector();
      
      private void add_words(String s, String delimiters)
      {
         StringTokenizer st = new StringTokenizer(s, delimiters);
         while(st.hasMoreTokens())
         {
            String curr = st.nextToken();
            words_.add(curr);
         }            
      }
      
      public void invoke(Stack path, int path_index) throws Exception
      {
         if(action_ == null)
            return; // NOP
                                 
         XmlNode n = (XmlNode) path.elementAt(path_index);

         XmlNode parent = null;
         if(path_index > 0)
            parent = (XmlNode) path.elementAt(path_index - 1);
          
         XmlNode grandpa = null;
         if(path_index > 1)
            grandpa = (XmlNode) path.elementAt(path_index - 2);
                                    
         if(action_.equals("d"))
         {
            if(parent == null)
               throw new Exception("Cannot apply delete tree on root");
            parent.drop_child(n);
         }       
         else if(action_.equals("e"))            
         {
            System.out.println("action_=" + action_ + ", node=" + n.name_);
            if(parent == null)
               throw new Exception("Cannot delete root");
               
            XmlNode.move_children(n, parent);
            parent.drop_child(n);
            
            System.out.println("nc=" + n.num_of_children() 
               + " pc=" + parent.num_of_children());
         }            
         else if(action_.equals("u"))
         {      
            if(grandpa == null)
               throw new Exception("No grandparent to move up to");

            grandpa.add_child(n);
            parent.drop_child(n);                                             
         }            
      }
            
      public boolean relevant(XmlNode n)
      {
         boolean result = true;

         String temp = match_;
         
         if(temp.equals("*"))
            result = true;
         else if(temp.equals("."))
         {
            result = n.num_of_children() == 0;
            if(result)
               System.out.println(". match for " + n.name_);            
         }            
         else
            result = pattern_matches(n.name_);
                           
         if(is_not)
            result = !result;
                                       
         return result;                                                      
      }
      
      private boolean pattern_matches(String s)
      {
         int count = 0;
         for(Iterator iter = words_.iterator(); iter.hasNext(); )
         {            
            if(iter.next().equals(s))
               count += 1;
         }
         
         if(is_and)
         {
            System.out.print("Comparing: " + s + " with: ");
            for(int i = 0; i < words_.size(); ++i)
               System.out.print(words_.elementAt(i) + " & ");
            System.out.println();               
            return count > 0;
         }            
            
         // ...Else:
         return count > 0;
      }      
   }
      
   public void process(XmlNode root) throws Exception
   {
      for(int i = 0; i < expressions_.size(); ++i)
      {
         Worker w = new Worker((Vector) expressions_.elementAt(i));
         w.process(root);
      }      
   }      

   public static void main(String[] args) throws Exception
   {
      Vector v = new Vector();
      for(int i = 0; i < args.length; ++i)
         v.add(args[i]);
         
      String in_file = null;
      String out_file = null;
               
      for(Iterator iter = v.iterator(); iter.hasNext(); )
      {
         String curr = (String) iter.next();
         if(curr.startsWith("-i"))
         {            
            in_file = curr.substring(2);
            iter.remove();
         }            
         else if(curr.startsWith("-o"))
         {
            out_file = curr.substring(2);
            iter.remove();
         }                     
      }                  
      
      if(in_file == null)
         throw new Exception("No input file is specified");
         
      XmlDoc doc = new XmlDoc(in_file);
      XmlNode root = doc.root;
      
      for(Iterator iter = v.iterator(); iter.hasNext(); )
      {
         String q = (String) iter.next();
         System.out.println("q=" + q);
         Xmelt xm = new Xmelt(q);
               
         xm.process(root);
      }                  

      PrintStream out = System.out;
      if(out_file != null)
         out = new PrintStream(new FileOutputStream(out_file));
               
      XmlTransformer.print(root, out);
   }
}
