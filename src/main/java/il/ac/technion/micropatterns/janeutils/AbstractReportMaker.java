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









package il.ac.technion.micropatterns.janeutils;

import il.ac.technion.jima.JimaMisc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;


public class AbstractReportMaker
{
   
   private static HashMap class2ps = new HashMap();
   
   private PrintStream log_;

   private String base_ = "";
   private File dir_ = new File(".");
   protected String[] argv_;
   
   public AbstractReportMaker(String base, String[] args)
   {
      set_base(base);
      argv_ = args;
      
      String fn = System.getProperty("user.home");
      String log_name = this.getClass().getName() + ".log";
      
      log_ = (PrintStream) class2ps.get(log_name);
      if(log_ != null)
         return;
            
      File f = new File(fn, log_name);
      try
      {
         log_ = new PrintStream(new FileOutputStream(f));
      }
      catch(FileNotFoundException e)
      {
         log_ = System.err;
      }
   }
   

   protected void set_dir(String s)
   {
      File f = new File(s);
      if(!f.isDirectory())
         f = f.getParentFile();
      
      dir_ = f;
   }
   
   protected void set_base(String base)
   {
      base_ = base;
   }

   protected void start_new_report(String name) throws FileNotFoundException 
   {
      start_new_report(name, null);
   }
   
   protected void start_new_report(String name, String desc) 
      throws FileNotFoundException
   {
      
      out_.flush();
      
      name = name.replace('\\', '-');
      
//      File f = new File(dir_, "mp." + base_ + "." +  name + ".csv");
      File f = new File(dir_, name + ".csv");
      
      String str = "Staring new report: name=" + name 
         + ", file=" + f + ", time=" + new Date();
      
      log().println(str);      
      JimaMisc.log().println(str);
      
      out_ = new PrintStream(new FileOutputStream(f));      
      comment(desc);
//      comment(new Date());
   }
   
   
   protected PrintStream out_ = System.out;
   
   protected void comment(Object s)
   {
      if(s == null)
         return;
      
      out_.println("; " + s);
   }
   
   protected void println(Object o)
   {
      out_.println(o);
   }
   
   protected void println()
   {
      println("");
   }
   
   protected void print(Object o)
   {
      out_.print(o);
   }
   
   protected PrintStream log()
   {
      return log_;      
   }
   
}
