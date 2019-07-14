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
import il.ac.technion.micropatterns.jane.lib.RecentlyUsedList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;


public class AppState
{
   private Properties props_ = new Properties();
   private File dir_;
   
   private RecentlyUsedList opened_files_ 
      = new RecentlyUsedList("opened.files", 8);
      
   private RecentlyUsedList touched_types_ 
      = new RecentlyUsedList("touched.types", 100);

   private File image_file_;
   
   public AppState(File dir) throws IOException
   {
      dir_ = dir;
      image_file_ = new File(dir, "janeapp.settings.txt");      
      if(!image_file_.exists())
         return;
         
      FileInputStream fis = new FileInputStream(image_file_);   
      props_.load(fis);
      fis.close();

      opened_files_.load(props_);
      touched_types_.load(props_);
      
      JimaMisc.log().println("loaded from " + image_file_.getAbsolutePath());
   }
   
   public void save() throws IOException
   {
      opened_files_.save(props_);
      touched_types_.save(props_);
      
      FileOutputStream fos = new FileOutputStream(image_file_);
      props_.store(fos, new Date().toString());
      
      JimaMisc.log().println("Saved to " + image_file_.getAbsolutePath());
      
      fos.close();
   }
            
   public String get_file_chooser_dir()
   {
      return read("file.chooser.dir", dir_.getAbsolutePath());
   }

   public void set_file_chooser_dir(File f)
   {
      if(!f.isDirectory())
         f = f.getParentFile();

      props_.setProperty("file.chooser.dir", f.getAbsolutePath());
      
//      props_.list(System.out);
   }

   public String get_jar_chooser_dir()
   {
      return read("jar.chooser.dir", dir_.getAbsolutePath());
   }
   
   public void set_jar_chooser_dir(File f)
   {
      if(!f.isDirectory())
         f = f.getParentFile();

      props_.setProperty("jar.chooser.dir", f.getAbsolutePath());
      
//      props_.list(JimaMisc.log());
   }

   
   public void file_was_opened(File f)
   {
      opened_files_.add(f.getAbsolutePath());
   }
   
   public Iterator get_opened_files()
   {
      return opened_files_.iterator();
   }
   
   public RecentlyUsedList touched_types()
   {
      return touched_types_;
   }
   
   
   
   private String read(String key, String default_value)
   {         
      String result = props_.getProperty(key);
      if(result == null)
         result = default_value;
         
      System.out.println("appstate.read(" + key + ")=" + result);
      return result;         
   }
   
}
