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











package il.ac.technion.micropatterns.jane.app;

import il.ac.technion.jima.JimaMisc;

import java.io.File;
import java.io.IOException;



public class Environment
{
   private static Environment instance_ = null;
   public synchronized static Environment instance()
   {
      if(instance_ == null)
         instance_ = new Environment();
         
      return instance_;
   }
   
   public static class CommandLineError extends Exception { }
   
   private String home_dir_;
   private File file_;
   public String jad_;
   
   private AppState state_;
   
   
   
   public void init(String[] args) throws CommandLineError, IOException
   {
      jad_ = "jad.exe";
      home_dir_ = System.getProperty("user.dir");
      JimaMisc.ensure(home_dir_ != null);
      
      for(int i = 0; i < args.length; ++i)
      {
         String curr = args[i];

         if(curr.startsWith("-?"))
            Main.usage(); // Program will terminate         

         if(curr.startsWith("-h"))
            Main.usage(); // Program will terminate         
         else if(curr.equals("-d"))
            home_dir_ = get(args, ++i);
         else if(curr.equals("-jad"))
            jad_ = get(args, ++i);
         else if(curr.equals("-f"))
            file_ = new File(get(args, ++i));
         else if(curr.startsWith("-"))
            Main.usage(); // Program will terminate
      }
      
//      if(file_ == null)
//         file_ = new File(home_dir_, "untitled.janemodel");
         
      System.out.println("home=" + home_dir_);         
      state_ = new AppState(new File(home_dir_));
   }
   
   public File file()
   {
      return file_;
   }
   
   public AppState state()
   {
      return state_;
   }
   
   private static String get(String[] args, int index) throws CommandLineError
   {
      if(index >= args.length)
         throw new CommandLineError();
         
      return args[index];         
   }
   
   public String home_dir()
   {
      return home_dir_;
   }
}
