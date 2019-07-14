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

import java.util.Stack;

public class XmlBuilder
{

   public XmlDoc doc = new XmlDoc();
   private Stack nodes_ = new Stack();
   
   public XmlBuilder() { }

   public XmlBuilder begin_element(String name)
   {
      if(nodes_.size() == 0)
      {         
         doc.root.name_ = name;
         nodes_.push(doc.root);
         return this;
      }         

      // ...Else:      
      XmlNode parent = (XmlNode) nodes_.peek();
      XmlNode new_one = new XmlNode(name);
      parent.add_child(new_one);
      nodes_.push(new_one);
      
      return this;
   }   

   public XmlBuilder end_element()
   {
      nodes_.pop();
      return this;
   } 
   
   public XmlBuilder begin_end_element(String name, String text)
   {
      begin_element(name);
      set_text(text);
      end_element();
      
      return this;
   }

   public XmlBuilder begin_end_element(String name, int int_value)
   {
      return begin_end_element(name, Integer.toString(int_value));
   }

   public XmlBuilder begin_end_element(String name, boolean b)
   {
      return begin_end_element(name, Boolean.toString(b));
   }
   
   public XmlBuilder set_attribute(String k, String d)
   {
      XmlNode n = (XmlNode) nodes_.peek();
      n.set_attribute(k, d);
      
      return this;
   }  
   
   public XmlBuilder set_text(String text)
   {
      XmlNode n = (XmlNode) nodes_.peek();
      n.text_ = text;
      
      return this;
   }
}
