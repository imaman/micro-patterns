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

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XmlDocMaker extends DefaultHandler
{
   private Stack elements_ = new Stack();
   public XmlNode root_ = null; 
           
   public void startDocument()
   {
//      root_  = new XmlNode();
   }
      
   public void endDocument()
   {
//      elements_.pop();
   }
       
   public void endElement(String s1, String s2, String s3)
   {
      elements_.pop();
   }
     
   public void startElement(String s1, String s2, String s3, Attributes a)
   {     
      XmlNode n = null;    
      XmlNode parent = null;
      
      if(root_ == null)
      {         
         root_ = new XmlNode();
         n = root_;
      }         
      else
      {         
         parent = (XmlNode) elements_.peek();
         n = new XmlNode(s3, a);      
      }         

      if(parent != null)
         parent.add_child(n);
        
      elements_.push(n);
  }
     
  public void characters(char[] text, int a, int b)
  {           
     if(b == 0)
        return;
           
     String s = new String(text, a, b);
     s = s.trim();
     if(s.length() <= 0)
        return;
      
     XmlNode top = (XmlNode) elements_.peek();
     top.append_text(s);
   }                      

   public static void main(String[] args)
   {
   }
}
