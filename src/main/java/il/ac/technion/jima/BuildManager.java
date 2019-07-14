package il.ac.technion.jima;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

public class BuildManager
{
   private static final String BUILD_ID = "build-number";
   private static final String WHEN = "build-date";
   private static final String VERSION = "version-number";
   
   private static final Locale LOCALE = Locale.UK;
   
   private final Properties p = new Properties();
   private final File f;
   private int build = 126;
   private Date when;
   private String version = "1.2";
   
   private DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, 
      DateFormat.LONG, LOCALE);
   
   public BuildManager(URL url) throws Exception 
   {
      f = new File(url.getFile());
      if(f.exists())
      {
         InputStream is = new FileInputStream(f);
         init(is);
      }         
   }

   public BuildManager(InputStream is) throws Exception
   {
      f = null;
      init(is);
   }
   
   private void init(InputStream is) throws IOException
   {
      try 
      {
         load(is);
      }
      finally
      {
         if(is != null)
            is.close();
      }
   }
   
   private void load(InputStream is) throws IOException
   {     
      p.load(is);
      try
      {
         Integer n = Integer.valueOf(p.getProperty(BUILD_ID));
         if(n != null)
            build = n.intValue();
      }
      catch(Throwable e)
      {
         // Absorb
      }
      
      try
      {
         String d = p.getProperty(WHEN);
         if(d != null)
            when = df.parse(d);
      }
      catch(Throwable e)
      {
         // Absorb
      }   
      
      try
      {
         String ver = p.getProperty(VERSION);
         if(ver != null)
            version = ver;
      }
      catch(Throwable e)
      {
         // Absorb
      }   
   }
   
   public void step(String newVersion) throws Exception
   {
      if(f == null)
         throw new Exception("I Cannot invoked step() if the " +
                "BuildManager(InputStream) constrcutor was used");
      
      if(newVersion != null)
      {
         if(!version.equals(newVersion)) 
         {
            version = newVersion;
            build = 0;
         }
      }
      
      p.setProperty(VERSION, version);
      
      build += 1;
      p.setProperty(BUILD_ID, Integer.toString(build));

      Calendar cal = newCalendar();
      when = cal.getTime();
      df.setCalendar(cal);
      String temp = df.format(when);      
      p.setProperty(WHEN, temp);
      
      
      OutputStream os = new FileOutputStream(f);
      try
      {
         p.store(os, "build-manager");
      }
      finally
      {
         if(os != null)
            os.close();
      }
   }
   
   private static Calendar newCalendar()
   {
      Calendar cal = Calendar.getInstance(LOCALE);
      cal.setTimeZone(TimeZone.getTimeZone("GMT"));
      
      return cal;      
   }
   
   public String getBuildDate()
   {
      Calendar cal = newCalendar();
      cal.setTime(when);
      
      DateFormat ddff = new SimpleDateFormat("yyyy.MM.dd");
      ddff.setCalendar(cal);
      
      return ddff.format(cal.getTime());         
   }
   
   public String getBuildTime()
   {
      Calendar cal = newCalendar();
      cal.setTime(when);
      
      DateFormat ddff = new SimpleDateFormat("HH.mm");
      ddff.setCalendar(cal);
      
      return ddff.format(cal.getTime());         
   }
   
   public int getBuild() 
   {
      return build;
   }
   
   public String getVersion()
   {
      return version;
   }
   
   private static void usage()
   {
      System.err.println("BuildManager: A Build-tracking utility");
      System.err.println("Usage: BuildManager <file-name> [<version>]");
      System.err.println("    <file-name> Name of a properties file " +
            "maintaining build information");
      System.err.println("    <version> An optional version string");      
      System.err.println("This program updates the " + BUILD_ID + ", " 
         + VERSION + " and " + WHEN + " entries at the specified file");
      System.err.println("The time zone used is GMT");
      System.err.println();
      System.exit(-1);
   }
   
   public static void main(String[] args)
   {
      if(args.length < 1 || args.length > 2)
         usage();
      
      for(int i = 0; i < args.length; ++i)
         if(args[i].startsWith("-"))
            usage();
      
      String ver = null;
      if(args.length == 2)
         ver = args[1];
      
      try
      {
         URL url = new File(args[0]).getAbsoluteFile().toURL();
         BuildManager bm = new BuildManager(url);
         
//         System.out.println(bm.getBuild());
//         System.out.println(bm.getVersion());         
//         System.out.println(bm.getBuildDate());
//         System.out.println(bm.getBuildTime());
         
         
         bm.step(ver);
         System.exit(0);
      }
      catch(Throwable t)
      {
         System.err.println("Failure: " + t.getMessage());
         System.exit(-2);
      }
   }
}
