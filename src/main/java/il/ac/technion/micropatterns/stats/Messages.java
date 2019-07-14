package il.ac.technion.micropatterns.stats;

import il.ac.technion.jima.BuildManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Messages
{
   
   public static final String VERSION;
   public static final int BUILD_NUMBER;
   public static final String BUILD_DATE;
   public static final String BUILD_TIME;
   
   static
   {
      String ver = "<?>";
      int bn = 0;
      String bd = "<?>";
      String bt = "<?>";
      try
      {
         BuildManager bm = new BuildManager(
            Messages.class.getResourceAsStream("build.props"));
         
         ver = bm.getVersion();
         bn = bm.getBuild();
         bd = bm.getBuildDate();
         bt = bm.getBuildTime();
      }
      catch(Exception e)
      {
         // Absorb
      }

      VERSION = ver;
      BUILD_NUMBER = bn;
      BUILD_DATE = bd;
      BUILD_TIME = bt;      
   }
   
   public static String aboutText() {
      String version = Messages.VERSION;
      String build = Messages.BUILD_NUMBER + "-" + Messages.BUILD_DATE;

      String result = "Version: " + version + " Build-id: " + build  + "\n\n"
         + read("about");
      
      return result;
   }
      
   private static String read(String messageName) {
      
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      
      InputStream is 
         = Messages.class.getResourceAsStream(messageName + ".txt");
   
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      while(true)
      {
         try
         {
            String line = br.readLine();
            if(line == null)
               break;
            
            pw.println(line);
         }
         catch(IOException e)
         {
            break;
         }
      }
      
      pw.flush();
      return sw.toString();      
   }

}
