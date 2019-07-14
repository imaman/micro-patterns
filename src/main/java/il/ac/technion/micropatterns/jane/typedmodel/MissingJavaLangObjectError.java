package il.ac.technion.micropatterns.jane.typedmodel;

import il.ac.technion.micropatterns.jane.lib.JavaSpec;

public class MissingJavaLangObjectError extends Exception
{
   public MissingJavaLangObjectError() 
   {
      super("Class " + JavaSpec.JAVA_LANG_OBJECT + " was not found\n"
         + "You should load into the project a jar file of the standard Java " 
         + "library");
   }
}
