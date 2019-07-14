package il.ac.technion.micropatterns.jane.app;

import il.ac.technion.jima.JimaMisc;

public class Main
{
   public static void usage()
   {
      System.out.println("Jane - Coding Pattern Detection tool");
      System.out.println();
      System.out.println("Usage:");
      System.out.println("   jane [-d <home-dir>] [-f <jane-file>]" +
         " [-jad <jad-path>] [-h]");
      System.out.println("      -d  Specify the application's home directory");
      System.out.println("      -f  Load a jane file upon startup");
      System.out.println("    -jad  Specify the location of a JAD " +
            "(the fast JAva Decompiler) executable");
      System.out.println("      -h  Show this help message");
      
      System.out.println();
      System.out.println();
      
      System.exit(-1);
   }
   
   public static void main(String[] args)
   {
      boolean help = false;
      for(int i = 0; i < args.length; ++i)
      {
         String curr = args[i];
         if(curr.startsWith("-help"))
            help = true;
         if(curr.startsWith("--help"))
            help = true;
         if(curr.startsWith("-?"))
            help = true;
         if(curr.startsWith("--?"))
            help = true;
         if(curr.startsWith("/?"))
            help = true;
      }
      
      if(help)
         usage();
      
      try
      {
         JaneGui.main(args);
      }
      catch (Exception e)
      {
         System.out.println(e.getMessage());
         e.printStackTrace(JimaMisc.log());
      }      
   }
}
