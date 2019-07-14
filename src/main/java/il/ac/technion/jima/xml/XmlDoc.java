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

import il.ac.technion.jima.JimaHack;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Vector;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.xml.sax.SAXException;

public class XmlDoc
{
   public XmlNode root;

   public XmlDoc()
   {
      root = new XmlNode();
   }
   
   public XmlDoc(InputStream is) throws Exception
   {
      try
      {                  
         init(is);
      }
      catch(Throwable t)
      {
         throw new Exception(t);
      }         
   }
   
   public XmlDoc(String file_name) throws Exception
   {
      init(new FileInputStream(file_name));
   }
   
   public static String escapify(String s)   
   {
      StringBuffer result = new StringBuffer(s.length());

      int len = s.length();      
      for(int i = 0; i < len; ++i)
      {
         char c = s.charAt(i);
         if(c == '<')
            result.append("&lt;");
         else if(c == '>')            
            result.append("&gt;");
         else if(c == '&')            
            result.append("&amp;");
         else if(c == '"')            
            result.append("&quot;");
         else
            JimaHack.append(result, c);
      }
      return result.toString();
   }
   
   
   private void init(InputStream is) throws IOException, ParserConfigurationException, 
      SAXException, FactoryConfigurationError
   {
      SAXParser p = SAXParserFactory.newInstance().newSAXParser();
      XmlDocMaker maker = new XmlDocMaker();
           
      p.parse(is, maker);
      
      root = maker.root_;      
   }
   
   public void save(PrintStream ps)
   {
      PrintWriter pw = new PrintWriter(ps);
      save(pw);
      pw.close();
   }
   
   public void write_xml_file(PrintWriter out)
   {
      out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      save(out);
   }
   
   public void save(PrintWriter out)
   {
      root.save(out);
      out.flush();
   }
   
   
   public static void main(String[] args) throws Throwable
   {
      XmlDoc d = new XmlDoc(args[0]);
      
      Vector v = d.root.search(args[1]);
      for(int i = 0; i < v.size(); ++i)
         System.out.println(i + ") " + v.elementAt(i));            
   }
}
