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

import il.ac.technion.jima.JimaMisc;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


import org.xml.sax.Attributes;


public class XmlNode
{
   public interface IWalkAction
   {
      void accept(XmlNode n, int depth);   
   }
   
   
   private static PrintStream mylog = null;
   
  
   public XmlNode() { }
   
   public XmlNode(String name)
   {
      this(name, null);
   }
   
   public XmlNode(String name, Attributes a)
   {
      name_ = name;
      
      if(a == null)
         return;
         
      for(int i = 0; i < a.getLength(); ++i)
         attributes_.put(a.getQName(i), a.getValue(i));                     
   }

   public String name_ = null;
   public String text_ = "";
   
   private Vector children_ = new Vector();
   private HashMap attributes_ = new HashMap();
   
   public void save(PrintWriter out)
   {
      out.print("<" + name_);

      Iterator j = attributes_iterator();
      while(j.hasNext())
      {
         String k = (String) j.next();
         String d = get_attribute(k);
         
         out.print(' ' + k + "=\"" + d + "\"");
      }      
      
      if(text_.length() == 0 && this.num_of_children() == 0)
      {
         out.println("/>");
         return;
      }

      // ...Else:
      out.print(">");
               
      if(text_.length() > 0)
         out.print(text_);
      
      Iterator i = children();
      while(i.hasNext())
      {
         XmlNode curr = (XmlNode) i.next();
         curr.save(out);
      }
         
      out.println("</" + name_ + ">");
   }
   

   
   public static void move_children(XmlNode from, XmlNode to)
   {
      Iterator iter = from.children();
      while(iter.hasNext())
         to.add_child((XmlNode) iter.next());
         
      from.drop_children();         
   }
   
   public Iterator attributes_iterator()
   {
      return attributes_.keySet().iterator();
   }
   
   public void remove_attribute(String k)
   {
      attributes_.remove(k);
   }
   
   public String get_attribute(String k)
   {
      return (String) attributes_.get(k);
   }
   
   public void set_attribute(String k, String d)
   {
      attributes_.put(k, d);
   }

   public Iterator children()
   {
      return children_.iterator();
   }   
   
   public int num_of_children()
   {
      return children_.size();
   }
   
   public XmlNode child(int index)
   {
      return (XmlNode) children_.elementAt(index);
   }
   
   public XmlNode child(String name)
   {
      Iterator iter = children_.iterator();
      while(iter.hasNext())
      {
         XmlNode curr = (XmlNode) iter.next();
         if(curr.name_.equals(name))
            return curr;
      }
      
      return null;
   }
   
   public void add_child(XmlNode n)
   {
      JimaMisc.ensure(n != null, "Child node is null");
      children_.add(n);
   }
   
   public void drop_child(XmlNode child)
   {
      children_.remove(child);
   }
   
   public void drop_children()
   {
      children_ = new Vector(); 
   }
   
   public void append_text(String s)
   {
      text_ = text_ + s;
   }
   
   public XmlNode path(String path) throws Exception
   {
      XmlNode result = this;
      
      StringTokenizer st = new StringTokenizer(path, "/");
      while(st.hasMoreTokens())
      {
         String curr = st.nextToken();
         result = result.child(curr);
         if(result == null)
            throw new Exception("Path (" + path + ") leads to a deadend");                  
      }
      
      return result;
   }


   public VectorOfNodes search(String query)
   {
      VectorOfNodes result = new VectorOfNodes();
      result.add(this);
      
      StringTokenizer st = new StringTokenizer(query, "/");
      while(st.hasMoreTokens())
      {
         String curr_type = st.nextToken();
         boolean is_wildcard = curr_type.equals("*");
         
         VectorOfNodes new_result = new VectorOfNodes();
         
         int count = result.size();
         for(int i = 0; i < count; ++i)
         {
            XmlNode curr = result.nodeAt(i);

            int num_children = curr.num_of_children();
            for(int j = 0; j < num_children; ++j)
            {
               XmlNode child = curr.child(j);
               if(is_wildcard || child.type_is(curr_type))
                  new_result.add(child);                 
            }
         }
         
         result = new_result;                           
      }
      
      return result;      
   }
   
   public boolean type_is(String s)
   {
      if(name_ == null)
         return s == null;
         
      // ...Else:
      return name_.equals(s);
   }

   public void walk(String which_type, IWalkAction action)
   {
      walk(which_type, action, 0);
   }

   private void walk(String which_type, IWalkAction action, int depth)
   {
      if(this.type_is(which_type))
         action.accept(this, depth);
         
      for(int i = 0; i < num_of_children(); ++i)
         child(i).walk(which_type, action, depth + 1);
                        
   }
   
   /**
    * Shallow copy the content of the specified node into the current 
    * node <code>this</code>.
    * @param src node to copy from
    */
   public void copy_from(XmlNode src)
   {
      this.text_ = src.text_;
      this.name_ = src.text_;
      this.children_ = src.children_;
      this.attributes_ = src.attributes_;      
   }
   
   
      
   public String toString()
   {
      return "<XmlNode type=\"" + name_ + "\" text=\"" + text_ 
         + "\" ChildCount=" + num_of_children() + "\" >";
   }
      
   public static void main(String[] args)
   {
      // app_assert(true, "Not here");         
   }
}
