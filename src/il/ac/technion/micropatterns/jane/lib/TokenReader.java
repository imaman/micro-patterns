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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;


public class TokenReader
{
   private BufferedReader br_;
   private String delim_;
   private StringTokenizer st_ = null;

   public TokenReader(Reader r, String delim)
   {
      br_ = new BufferedReader(r);
      delim_ = delim;
   }  
   
   public String readToken() throws IOException
   {
      while(true)
      {
         if(st_ == null)
         {
            String line = br_.readLine();
            if(line == null)
               return null;
            
            st_ = new StringTokenizer(line, delim_);
         }
         
         if(!st_.hasMoreTokens())
         {
            st_ = null;
            continue;
         }
         
         String token = st_.nextToken();
         return token;
      }
   }
   
   public String readHardToken() throws IOException
   {
      while(true)
      {
         String result = readToken();
         if(result == null)
            return null;
         
         result = result.trim();
         if(result.length() <= 0)
            continue;
         
         // ...Else:
         return result;
      }
   }
   
   public static void main(String[] args) throws IOException
   {
      String source = "ab cd,ef\n12,33\n\n44\n   \n55,66";
      StringReader sr = new StringReader(source);
      TokenReader tr = new TokenReader(sr, ",");

      JimaMisc.ensure(tr.readHardToken().equals("ab cd"));
      JimaMisc.ensure(tr.readHardToken().equals("ef"));
      JimaMisc.ensure(tr.readHardToken().equals("12"));
      JimaMisc.ensure(tr.readHardToken().equals("33"));
      JimaMisc.ensure(tr.readHardToken().equals("44"));
      JimaMisc.ensure(tr.readHardToken().equals("55"));
      JimaMisc.ensure(tr.readHardToken().equals("66"));
      JimaMisc.ensure(tr.readHardToken() == null);
      
      sr = new StringReader(source);
      tr = new TokenReader(sr, ",");

      JimaMisc.ensure(tr.readToken().equals("ab cd"));
      JimaMisc.ensure(tr.readToken().equals("ef"));
      JimaMisc.ensure(tr.readToken().equals("12"));
      JimaMisc.ensure(tr.readToken().equals("33"));
      JimaMisc.ensure(tr.readToken().equals("44"));
      JimaMisc.ensure(tr.readToken().equals("   "));
      JimaMisc.ensure(tr.readToken().equals("55"));
      JimaMisc.ensure(tr.readToken().equals("66"));
      JimaMisc.ensure(tr.readToken() == null);
      
      System.out.println("Success");
   }
}
