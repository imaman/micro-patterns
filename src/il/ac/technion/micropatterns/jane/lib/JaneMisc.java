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









package il.ac.technion.micropatterns.jane.lib;

import il.ac.technion.jima.JimaMisc;

public class JaneMisc
{
   private static class Sub
   {
      public String s_;
      public int b_;
      public int e_;

      public Sub(String s)
      {
         this(s, 0, s.length());
      }
      
      public Sub(String s, int b, int e)
      {
         s_ = s;
         b_ = b;
         e_ = e;
      }
      
      public String eat_to(char c)
      {
         int index = s_.indexOf(c, b_);
         if(index < 0)
            return null;
            
         if(index >= e_)
            return null;
            
         String result = s_.substring(b_, index);
         this.b_ = index + 1;
         
         return result;
      }
      
      public String toString()
      {
         return s_.substring(b_, e_);
      }
      
      public int length()
      {
         return e_ - b_;
      }
      
      public char charAt(int i)
      {
         return s_.charAt(b_ + i);
      }
                 
      public Sub move(int db)
      {
         this.b_ += db;
         return this;
      }      
   }

   public static String signature_to_text(String sig)
   {
      return signature_to_text(sig, true);
   }
   
   public static String signature_to_text(String sig, boolean array_indication)
   {
      return parse_impl(new Sub(sig), array_indication);
   }

   private static String parse_impl(Sub sig, boolean array_indication)
   {
      JimaMisc.ensure(sig.length() > 0);
      char c = sig.charAt(0);
        
      if(c == '[')
      {         
         JimaMisc.ensure(sig.length() > 1);
         
         if(array_indication)
            return parse_impl(sig.move(1), array_indication) + "[]";
         else            
            return parse_impl(sig.move(1), array_indication);
      }          
    

      switch(c)
      {
         case '(':
            return parse_function(sig, array_indication);
            
         case 'L':
            return parse_class_name(sig);
         
         case 'Z':
            sig.move(1);
            return "boolean";

         case 'B':            
            sig.move(1);
            return "byte";
            
         case 'C':
            sig.move(1);
            return "char";
            
         case 'D':
            sig.move(1);
            return "double";
            
         case 'F':
            sig.move(1);
            return "float";
            
         case 'I':            
            sig.move(1);
            return "int";
            
         case 'J':            
            sig.move(1);
            return "long";
                        
         case 'S':
            sig.move(1);
            return "short";
                        
         case 'V':            
            sig.move(1);
            return "void";
      }

      JimaMisc.ensure(false, sig.toString());
      return null; // Faked
   }      
   
//Type     Chararacter 
//boolean      Z 
//byte         B 
//char         C 
//double       D 
//float        F 
//int          I 
//long         J 
//object       L 
//short        S 
//void         V 
//array        [       
        
   public static String parse_function(Sub sig, boolean array_indication)
   {
      JimaMisc.ensure(sig.length() > 2, sig.toString());
      JimaMisc.ensure(sig.charAt(0) == '(', sig.toString());

      
      String inner = sig.move(1).eat_to(')');
      
      if(inner == null)
         inner = sig.eat_to(')');
         
      JimaMisc.ensure(inner != null, sig.toString());
      
      
      String return_type = parse_impl(sig, array_indication);
      
      Sub temp = new Sub(inner);                           
      StringBuffer result = new StringBuffer();
      
      int n = 0;
      while(temp.length() >= 1)
      {
         ++n;
         
         String a = parse_impl(temp, array_indication);
         
         if(n == 1)
            result.append(a);
         else
            result.append(',' + a);
      }
                  
      return '(' + result.toString() + ") => " + return_type;
   }
          
   public static String parse_class_name(Sub sig)
   {
      JimaMisc.ensure(sig.length() > 2, sig.toString());
      JimaMisc.ensure(sig.charAt(0) == 'L', sig.toString());
      
      String s = sig.move(1).eat_to(';');
      JimaMisc.ensure(s != null, sig.toString());
      
      StringBuffer sb = new StringBuffer(s);
      for(int i = 0; i < sb.length(); ++i)
      {
         char d = sb.charAt(i);
         if(d == '/')
            sb.setCharAt(i, '.');
      }         
      
      return sb.toString();
   }
   
   public static String class_to_bc_format(String class_name)
   {
      StringBuffer result = new StringBuffer("L" + class_name + ";");
      for(int i = 0; i < result.length(); ++i)
      {
         char c = result.charAt(i);
         if(c == '.')
            result.setCharAt(i, '/');            
      }            
      
      return result.toString();
   }
   
   
   private static final String SPACES = "                                    ";
   public static String to_percent(int a, int whole)
   {
      JimaMisc.ensure(a >= 0, "a=" + a + ", whole=" + whole);
      JimaMisc.ensure(whole >= 0, "a=" + a + ", whole=" + whole);
      
      if(whole == 0)
         return "--";
         
      int temp = (a * 1000 / whole);
//      boolean is_100 = (a == whole);
      float per = temp / 10.0f;
      
//      String s = "";
//      if(!is_100)
//         s = "  ";

      String f = Float.toString(per);
      int needed_spaces = 2 * (6 - f.length());
      JimaMisc.ensure(needed_spaces >= 0, "needed_spaces=" + needed_spaces 
         + ", f='" + f + "', temp=" + temp + ", a=" + a + ", whole=" + whole);
      String result  = SPACES.substring(0, needed_spaces)+ f + "%";
      return result;
   }
   
   public static boolean isWindows() 
   {
      String os = System.getProperty("os.name").toLowerCase();
      if(os.indexOf("windows") >= 0)
         return true;
      return false;      
   }
   
}
