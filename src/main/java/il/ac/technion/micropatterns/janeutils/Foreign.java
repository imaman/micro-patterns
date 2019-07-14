package il.ac.technion.micropatterns.janeutils;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;

public class Foreign
{

   public static RuntimeException translate(Exception e)
   {
      if(e instanceof RuntimeException)
         return (RuntimeException) e;
      
      RuntimeException re = new RuntimeException(e);
      re.setStackTrace(e.getStackTrace());
      return re;
   }
   
   public static JavaClass lookupClass(String s)
   {
      try
      {
         return Repository.lookupClass(s);
      }
      catch (ClassNotFoundException e)
      {
         throw translate(e);
      }
   }

   public static JavaClass[] getInterfaces(JavaClass jc)
   {
      try
      {
         return jc.getInterfaces();
      }
      catch (ClassNotFoundException e)
      {
         throw translate(e);
      }
   }

}
