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
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.FactoryConfigurationError;

public class XmlTransformer
{

   public XmlTransformer(String s)
   {
      StringTokenizer st = new StringTokenizer(s, "/");
      
      while(st.hasMoreTokens())
      {
         String t = st.nextToken();
         tokens_.add(t);         
      }               
   }
   
   private Vector tokens_ = new Vector();

   private static class MyTokenzier
   {
      MyTokenzier(String s, String delim)
      {
         delim_ = delim;
         s_ = s;
      }
      
      public String next()
      {
         while(true)
         {
            curr_ += 1;
            if(curr_ >= s_.length())
            {
               String result = s_.substring(start_, curr_);
               start_ = curr_;
               
               return result;
            }               
            
            char c  = s_.charAt(curr_);
            if(delim_.indexOf(c) < 0)
               continue;

            // Delimiter found:
            String result = s_.substring(start_, curr_);
            start_ = curr_;
            
            return result;                                    
         }            
      }
      
      public boolean has_next()
      {
         return start_ < s_.length();
      }
      
      
      private int curr_ = 0;
      private int start_ = 0;
      private String s_;
      private String delim_;
   }
   
   private static boolean compare(String s, String expr)
   {
      int plus_count = 0;         
      boolean yes = false;
      boolean no = true;

      if(s == null)
         s = "";
               
      MyTokenzier mt = new MyTokenzier(expr, "+-");
      while(mt.has_next())
      {
         String t = mt.next();
         if(t.length() == 0)
            continue;
            
         boolean plus = true;
         char c = t.charAt(0);
         
         if(c == '-')
            plus = false;
         else            
            plus_count += 1;
                     
            
         if(c == '+' || c == '-')
            t = t.substring(1);
            
         boolean temp = s.equals(t);
         if(plus)
            yes |= temp;
         else
            no &= !temp;
      }         
      
      if(plus_count == 0)
         yes = true;
         
      boolean result = no && yes;
      return result;
   }
      
   private boolean dfs_impl(XmlNode root, XmlNode curr_parent, 
      XmlNode node, int depth)
   {
      boolean is_last = depth == (tokens_.size() - 1);

      String t = "*";
      if(depth >= 0)
         t = (String) tokens_.elementAt(depth);

      XmlNode new_parent = curr_parent;
         
      if(t.startsWith("@"))
      {         
         new_parent = node;
         t = t.substring(1);
      }

      int num_children = node.num_of_children();

      Iterator iter = node.children(); 
      node.drop_children();
      
      boolean must_have_children = t.equals(".");
      boolean is_wildcard = t.equals("*");
      boolean is_super_wiladcard = t.equals("**");

      
      boolean comp_result = compare(node.name_, t);
      
      if(!is_wildcard && !is_super_wiladcard && !comp_result)
         return false;

      if(must_have_children && num_children == 0)
         return false;
         
      boolean result = false;
            
      if(is_super_wiladcard)
      {
         while(iter.hasNext())
            node.add_child((XmlNode) iter.next());
            
         result = true;            
      }               
      
      if(num_children == 0)         
         result = true;

                  
      if(!is_last)
      {
         while(iter.hasNext())
         {
            XmlNode child = (XmlNode) iter.next();            
         
            boolean temp = dfs_impl(root, new_parent, child, depth + 1);
            if(temp && new_parent != null)
               new_parent.add_child(child);
               
            result = result || temp;            
         }         
      }         
      
      if(result && curr_parent == null && new_parent != null)
         root.add_child(node);

      return result;         

   }
   
   public XmlNode accept(XmlNode n)
   {
      XmlNode result = new XmlNode();
      result.name_ = "result";
      
      dfs_impl(result, null, n, -1);
      
      return result;
   }
   
   public static void print(XmlNode n, PrintStream out)
   {
      String cc = "";
      if(n.num_of_children() > 0)
         cc = " n=\"" + n.num_of_children() + "\"";
      out.println("<" + n.name_ + cc + ">");
      out.println(n.text_);
      
      for(Iterator i = n.children(); i.hasNext(); )
      {
         XmlNode c = (XmlNode) i.next();
         print(c, out);
      }         
      
      out.println("</" + n.name_ + ">");
   }
   
   public static void main(String[] args) 
      throws FactoryConfigurationError, Exception
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
         XmlTransformer xt = new XmlTransformer(q);
               
         XmlNode result = xt.accept(root);
         root = result;
      }                  
//      result.set_attribute("q", q);

      PrintStream out = System.out;
      if(out_file != null)
         out = new PrintStream(new FileOutputStream(out_file));
               
      print(root, out);
   }
}
