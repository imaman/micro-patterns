package il.ac.technion.micropatterns.jane.app.gui.rows;

import java.lang.reflect.Method;

import javax.swing.JEditorPane;

import org.apache.bcel.classfile.JavaClass;

public class DecompilerInvoker
{
   private static Class implClass;
   
   static {
      try
      {
         implClass 
            = Class.forName("il.ac.technion.micropatterns.jane.lib.decompiler.DecompilerImpl");
      }
      catch (ClassNotFoundException e)
      {
         // Absorb
      }      
   }
   
   public static void show(JavaClass jc) {
      if(implClass == null)
         return;
            
      try
      {
         Method m = implClass.getMethod("show", new Class[] { JavaClass.class } );
         m.invoke(null, new Object[] { jc });
      }
      catch(Throwable t)
      {
         throw new RuntimeException(t);
      }
   }

   public static void show(String methodName, JavaClass jc) {
      if(implClass == null)
         return;
            
      try
      {
         Method m = implClass.getMethod("show", 
            new Class[] { String.class, JavaClass.class } );
         m.invoke(null, new Object[] { methodName, jc });
      }
      catch(Throwable t)
      {
         throw new RuntimeException(t);
      }
   }
   
   public static void fillEditor(JEditorPane editor, JavaClass jc) {
      if(implClass == null)
         return;
            
      try
      {
         Method m = implClass.getMethod("fillEditor", 
            new Class[] { JEditorPane.class, JavaClass.class } );
         m.invoke(null, new Object[] { editor, jc });
      }
      catch(Throwable t)
      {
         throw new RuntimeException(t);
      }
   }
}
