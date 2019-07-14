import il.ac.technion.micropatterns.jane.lib.InstructionParser;

import java.util.Iterator;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;


public class Main
{
   public static void main(String[] args) throws ClassNotFoundException
   {
      Class cls = java.awt.Container.class;
      String methodName = "getContainerListeners";
      
      JavaClass jc = Repository.lookupClass(cls.getName());
      Method[] ms = jc.getMethods();
      for(int i = 0; i < ms.length; ++i)
      {
         Method m = ms[i];
         if(!m.getName().equals(methodName))
            continue;
         
         InstructionParser ip = new InstructionParser(m);
         for(Iterator iter = ip.iterator(); iter.hasNext(); )
         {
            System.out.println(iter.next());
         }
//         System.out.println(m);
      }
      
   }
}
