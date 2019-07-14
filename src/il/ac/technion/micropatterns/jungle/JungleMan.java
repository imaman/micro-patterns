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









package il.ac.technion.micropatterns.jungle;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.lib.ClassPathSpecifier;
import il.ac.technion.micropatterns.janeutils.impl.ImplUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import il.ac.technion.micropatterns.jungle.model.CollectionDesc;

public class JungleMan
{
   private static String jar(String name)
   {
      return name + ".jar";
   }
   
   private static ClassPathSpecifier make_cp(File jar_dir, String cp)
   {
      return new ClassPathSpecifier(jar_dir, cp);
   }
   

   private static void go(CollectionDesc cd, File jar_dir, File out_dir) 
      throws Throwable
   {
      Jungle.go(make_cp(jar_dir, cd.get_jar_file_name()), 
         make_cp(jar_dir, CollectionDesc.LIB_SUND.get_jar_file_name()),
         out_dir.getAbsolutePath());
   }
   
   private static void go(String jar_files, File jar_dir, File out_dir) throws Throwable
   {
      Jungle.go(make_cp(jar_dir, jar_files), make_cp(jar_dir, 
               CollectionDesc.LIB_SUND.get_jar_file_name()),
               out_dir.getAbsolutePath());
   }

   public static void go(String jar_dir, String out_dir) throws Throwable
   {

      File csv_file = new File(out_dir, "vectors.csv");
      Jungle.out_ = new PrintStream(new FileOutputStream(csv_file));

      
      File outd = new File(out_dir).getAbsoluteFile();
      File jard = new File(jar_dir).getAbsoluteFile();

      String sep = ClassPathSpecifier.SEP;
      String s = CollectionDesc.LIB_ANT.get_jar_file_name() + sep 
         + CollectionDesc.LIB_JEDIT.get_jar_file_name() + sep
         + CollectionDesc.LIB_MJC.get_jar_file_name() + sep
         + CollectionDesc.LIB_TOMCAT.get_jar_file_name() + sep
         + CollectionDesc.LIB_SCALA.get_jar_file_name();         
      go(s, jard, outd);
      
      go(CollectionDesc.LIB_JBOSS, jard, outd);
      go(CollectionDesc.LIB_POSEIDON, jard, outd);
      
            
      go(CollectionDesc.LIB_SUN0, jard, outd);
      go(CollectionDesc.LIB_SUNA, jard, outd);
      go(CollectionDesc.LIB_SUNB, jard, outd);
      go(CollectionDesc.LIB_SUNC, jard, outd);
      go(CollectionDesc.LIB_SUND, jard, outd);

      go(CollectionDesc.LIB_KAFFEA, jard, outd);
      go(CollectionDesc.LIB_KAFFEB, jard, outd);

      //
      //
      // *************************************************
      //  
      // End of "Standard" Collections
      //
      // *************************************************
      //
      //

//      s = CollectionDesc.LIB_JUNIT.get_jar_file_name() + sep +
//         CollectionDesc.LIB_JGRAPH.get_jar_file_name() + sep +
//         CollectionDesc.LIB_GANTPROJECT.get_jar_file_name() + sep +
//         CollectionDesc.LIB_SQUIRREL.get_jar_file_name() + sep +
//         CollectionDesc.LIB_MEGAMEK.get_jar_file_name() + sep +
//         CollectionDesc.LIB_FREEMIND.get_jar_file_name() + sep +
//         CollectionDesc.LIB_AZUEREUS.get_jar_file_name() + sep +
//         CollectionDesc.LIB_BCEL.get_jar_file_name() + sep;
//      go(s, jard, outd);
      
//      go(CollectionDesc.LIB_IBM, jard, outd);
//      go(CollectionDesc.LIB_HP, jard, outd);
      
//      go(CollectionDesc.LIB_TOMCAT3, jard, outd);
//      go(CollectionDesc.LIB_TOMCAT4, jard, outd);

      
//      //
//      // d/o
//      //
//      s = CollectionDesc.LIB_BCEL.get_jar_file_name() + sep +
//         CollectionDesc.LIB_MJC.get_jar_file_name();
//      il.ac.technion.micropatterns.jungle.go(make_cp(jard, s), 
//         make_cp(jard, CollectionDesc.LIB_SUNA.get_jar_file_name()),
//               outd.getAbsolutePath());
//      //
//      // End of d/o
//      //
      
      System.out.println("-The End- [Press Enter]");
      ImplUtils.read_line();
   }
   public static void main_impl(String[] args) throws Throwable
   {
      if(args.length < 1 || args.length > 2)
      {
         System.err.println("JungleMan - Micro Pattern mass detection tool");
         System.err.println("Usage: JungleMan <jar-dir> [<out-dir>]");
         System.exit(-1);
      }
      
      String jar_dir = args[0];
      String out_dir = jar_dir;
      
            
      if(args.length == 2)
         out_dir = args[1];
      
      go(jar_dir, out_dir);
   }
   
   public static void main(String[] args) 
   {
      try
      {
         main_impl(args);
         ImplUtils.read_line();
      }
      catch(Throwable e)
      {
         JimaMisc.stop(e);
         try
         {
            ImplUtils.read_line();
         }
         catch(IOException e1)
         {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
      }
   }
}
