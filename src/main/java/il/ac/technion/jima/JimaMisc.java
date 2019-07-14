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


package il.ac.technion.jima;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

public class JimaMisc 
{
   private static PrintStream log_;

   static
   {
      OutputStream temp = System.out;
      try
      {
         File f = new File(System.getProperty("user.home"), "jima.log");
         System.err.println("Log file=" + f);
         temp = new FileOutputStream(f);
      }
      catch(FileNotFoundException e)
      {
         // Absorb
      }
      
//      temp = new LineNumberedStream(temp);
      log_ = new PrintStream(temp, true);
      log_.println("== Starting at " + Calendar.getInstance().getTime() 
         + " ==");
   }

   public static PrintStream log()
   {
      return log_;
   }

   /**
   * @deprecated Replaced by ensure()
   */
   public static void check(boolean cond, String msg)
   {
      ensure(cond, msg);
   }

   /**
   * @deprecated Replaced by ensure()
   */
   public static void check(boolean cond)
   {
      ensure(cond);
   }

   public static void wrong_way()
   {
      wrong_way("Under Construction");
   }

   public static void wrong_way(String msg)
   {
      ensure(false, msg);
   }

   public static void ensure(boolean cond, String msg)
	{
		if(cond)
			return;

      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      
      pw.println(msg);
      pw.println();

      try
      {
         throw new Exception("");
      }         
      catch(Throwable t)
      {
         t.printStackTrace(pw);
      }
      
      String text = "Assertion failure: " + sw.getBuffer().toString();
      
      System.err.println(text);
      log().println(text);
      
//      JOptionPane.showMessageDialog(null, text,
//         "Assertion failed", JOptionPane.ERROR_MESSAGE);
                 
      System.exit(-1);                                        
	}
   

   public static void ensure(boolean cond)
   {
      ensure(cond, "[Unspecified]");
   }

   public static void stop(Throwable t)
   {
      print(t);
      System.exit(-1);
   }

   public static void stop(String s)
   {
      JOptionPane.showMessageDialog(null, s, "", JOptionPane.ERROR_MESSAGE);
   }
   
   public static void print(Throwable t)
   {
      print_impl(System.err, t);
      print_impl(JimaMisc.log(), t);
   }

   private static void print_impl(PrintStream out, Throwable t)
   {
      out.println();
      out.println("******* Error *******");
      out.println("   What: " + t.getMessage());
      t.printStackTrace(out);
      out.println("*********************");
      out.println();
   }


   public static void wrongWay()
   {
      check(false, "You cannot invoke this piece of code");
   }
   
   public static void createPath(File path)
   {
      if(!path.isDirectory())
         path = path.getParentFile();

      path.mkdirs();         
   }
   
   public static void throwIOIfFailed(boolean cond, String s) 
      throws IOException
   {
      if(!cond)
         throw new IOException(s);
   }


   public static void fileCopy(File from_file, File to_dir, String to_name) 
      throws IOException
   {
      fileCopyImpl(from_file, new File(to_dir, to_name));
   }
   
   public static void fileCopy(File from_file, File to_path)
      throws IOException
   {
      if(to_path.isFile())
      {
         fileCopyImpl(from_file, to_path);
         return;
      }
      
      // ...Else:
      String name = from_file.getName();
      fileCopy(from_file, to_path, name);
   }
   
   private static void fileCopyImpl(File from_file, File to_file)
      throws IOException
   {      
      FileInputStream from = null;
      FileOutputStream to = null;
      
      try
      {
         from = new FileInputStream(from_file);
      }
      catch (FileNotFoundException e_from)
      {
         throw new IOException("Cannot open source file: " 
            + from_file.getAbsolutePath());
      }
      
      try
      {
         to = new FileOutputStream(to_file);
      }
      catch (FileNotFoundException e_to)
      {
         throw new IOException("Cannot open output file: " 
            + to_file.getAbsolutePath());
      }

      try
      {
         byte[] buffer = new byte[4096];
         int bytes_read;

         // Read a chunk of bytes into the buffer, then write them out,
         // looping until we reach the end of the file (when read() returns -1).
         while(true)
         {
            bytes_read = from.read(buffer);
            if(bytes_read < 0)
               break; // Stop at EOF
               
            to.write(buffer, 0, bytes_read);
         }
      }
      finally
      {
         // Cleanup
         if(from != null)
         {
            try
            {
               from.close();
            }
            catch(IOException e)
            {
               // Absorb
            }
         }
         if(to != null)
         {
            try
            {
               to.close();
            }
            catch(IOException e)
            {
               // Absorb
            }
         }
      }
   }
   
   public static String change_suffix(File f, String suffix)
   {
      return change_suffix(f.getAbsolutePath(), suffix);
   }
   
   public static String change_suffix(String s, String suffix)
   {
      int index = s.lastIndexOf('.');
      if(index > 0)
         s = s.substring(0, index);
         
      return s + suffix;         
   }
   
   private static class HashMapIterator implements Iterator
   {
      public HashMapIterator(HashMap m)
      {
         inner_ = m.keySet().iterator();
         map_ = m;
      }
      
      private HashMap map_;
      private Iterator inner_;
      

      /**
       * @return
       */
      public boolean hasNext()
      {
         return inner_.hasNext();
      }

      /**
       * @return
       */
      public Object next()
      {
         Object temp = inner_.next();
         Object result = map_.get(temp);
                  
         return result;
      }

      /**
       * 
       */
      public void remove() 
      {
         inner_.remove();
      }
   }
   
   public static Iterator makeIter(HashMap m)
   {
      return new HashMapIterator(m);
   }
   
   public static long tick_count()
   {
      return System.currentTimeMillis();
   }
   
   public static String align_number(int n, int digits)
   {
      return align_number(n, digits, ' ');
   }
   

   public static String align_number(int n, int digits, char c)
   {
      String temp = Integer.toString(n);      
      int len = temp.length();
      
      String result = "";
      int left = Math.max(digits - len, 0);
      for(int i = 0; i < left; ++i)
         result += Character.toString(c);

      result += temp;      
      return result;
   }
   
   
}
