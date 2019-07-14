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








package il.ac.technion.micropatterns.tables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import il.ac.technion.micropatterns.stats.Env;

public class Bank
{
   private static File dir = new File(Env.fileName("ipbank"));

   static
   {
      try
      {
         if(!dir.exists())
            dir.mkdirs();
      }
      catch(Throwable t)
      {
         stop(t);
      }
   }
   
   public static void stop(Throwable t)
   {
      System.err.println("Failure:");
      t.printStackTrace();
      System.exit(-1);
   }
   
   public static File resolve(String name)
   {
      return new File(dir, name);
   }
   
   public static Table lookup(String name) throws Throwable
   {
      File f = resolve(name);
      Table t = new Table(f);
      
      return t;      
   }
   
   public static boolean has(String name)
   {
      return resolve(name).exists();
   }
   
   public static void put(String name, Table t) throws Throwable
   {
      File f = resolve(name);
      PrintStream ps = new PrintStream(new FileOutputStream(f));
      
      t.print(ps);
      ps.close();
   }
   
}
