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









package il.ac.technion.micropatterns.jane.lib;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;

public class JavaSpec
{
   
   public static final String NULL_CLASS = "java.io.Serializable";
   private static final String CTOR_SUFFIX = "init>";
   public static final String JAVA_LANG_OBJECT = "java.lang.Object";
   private static final String EQUALS = "equals";
   
   
   

   public static boolean is_java_lang_object(String class_name)
   {      
      return class_name.equals(JAVA_LANG_OBJECT);    
   }
   
   public static boolean is_equals(String method_name)
   {
      return method_name.equals(EQUALS);
   }

   public static boolean is_returning_void(Method m)
   {
      String sig = m.getSignature();
      int index = sig.indexOf(')');
      JimaMisc.ensure(index >= 0);
      
      char c = sig.charAt(index + 1);
      
      if(c == 'V')
         return true;
      else
         return false;         
   }
   
   public static boolean is_main_method(Method m)
   {
      return m.isStatic() && m.getName().equals("main");
   }

   public static boolean is_constructor(Method m)
   {
      return JavaSpec.is_constructor(m.getName());
   }

   public static boolean is_returning_class(Method m)
   {
      String sig = m.getSignature();
      int index = sig.indexOf(')');
      JimaMisc.ensure(index >= 0);
      
      char c = sig.charAt(index + 1);
      
      if(c == 'L')
         return true;
      else
         return false;         
   }

   public static boolean is_primitive(String sig)
   {
      JimaMisc.ensure(sig.length() > 0);
      
      char c = sig.charAt(0);
      
      if(c == 'L' || c == '[')
         return false;
      else
         return true;               
   }
   
   public static String remove_array_indication(String sig)
   {
      StringBuffer result = new StringBuffer();
      
      int top = sig.length();
      for(int i = 0; i < top; ++i)
      {
         char c = sig.charAt(i);
         if(c != '[')
            result.append(c);                  
      }
      
      return result.toString();
   }

   public static boolean is_constructor(String method_name)
   {
      boolean result = method_name.endsWith(CTOR_SUFFIX);      
      return result;
   }

   public static int num_of_args(String method_sig)
   {
      String temp = JaneMisc.signature_to_text(method_sig);
      
      
      int pos1 = temp.indexOf('(');
      int pos2 = temp.indexOf(')');
      
      JimaMisc.ensure(pos1 >= 0);
      JimaMisc.ensure(pos2 > pos1);
      
      temp = temp.substring(pos1 + 1, pos2);
      if(temp.length() == 0)
         return 0;
      
      int result = 1;
      for(int i = 0; i < temp.length(); ++i)
      {
         if(temp.charAt(i) == ',')
            result += 1;
      }
      
      return result;      
   }
   
   public static Iterator get_signatures_of_args(String method_sig)
   {
      Vector result = new Vector();
      
      String temp = JaneMisc.signature_to_text(method_sig);
      
      
      int pos1 = temp.indexOf('(');
      int pos2 = temp.indexOf(')');
      
      JimaMisc.ensure(pos1 >= 0);
      JimaMisc.ensure(pos2 > pos1);
      
      temp = temp.substring(pos1 + 1, pos2);
      if(temp.length() == 0)
         return result.iterator();

      StringTokenizer st = new StringTokenizer(temp, ",");
      while(st.hasMoreTokens())
      {
         String curr = st.nextToken();
         result.add(curr);
      }

      return result.iterator();
   }

   public static int num_of_args(Method m)
   {
      return num_of_args(m.getSignature());      
   }
   
   
   public static final String DEFAULT_PACKAGE = "<Default-Package>";
   
   public static String package_name_of(String type_name)
   {
      JimaMisc.ensure(type_name.length() >= 2);

      int pos = type_name.lastIndexOf('.');
      if(pos < 0)
         return DEFAULT_PACKAGE;
      
      String result = type_name.substring(0, pos);     
      return result;      
   }
   
   private static Collection new_collection()
   {
      return new Vector();
//      return new HashSet();
   }
   
   public static Iterator associated_classes(JavaClass jc, 
      boolean include_primitives)
   {
      Collection result = new_collection();
      
      ConstantPool cp = jc.getConstantPool();
      for(int i = 0; i < cp.getLength(); ++i)
      {
         Constant c = cp.getConstant(i);
         if(c instanceof ConstantClass)
         {
            ConstantClass cc = (ConstantClass) c;
            int index = cc.getNameIndex();
            
            ConstantUtf8 name = (ConstantUtf8) cp.getConstant(index);
            String str = name.getBytes().replace('/', '.');
            
            result.add(str);            
         }
      }
      
      return result.iterator();      
   }

   public static Iterator aggregated_classes(JavaClass jc)
   {
      return aggregated_classes(jc, true);
   }
   
   public static Iterator direct_sub_class(JavaClass jc)
   {
      Collection result = new_collection();
      
      String super_type = jc.getSuperclassName();
      if(!is_java_lang_object(super_type))
         result.add(super_type);
      
      return result.iterator();      
   }
   
   public static Iterator method_invocations(ClassSpec cs, TypedModel m, 
      boolean only_from_ctors) 
   {
      Collection result = new_collection();
      
      ConstantPoolGen cpg = cs.constant_pool();
      ProvidedMethods pm = new ProvidedMethods(m, (ClassHandle) cs.get_handle());
      
      Method[] methods = cs.jc().getMethods();
      for(int i = 0; i < methods.length; ++i)
      {
         Method curr = methods[i];

         boolean is_ctor = JavaSpec.is_constructor(curr);
         if(only_from_ctors && !is_ctor)
            continue;

         if(!only_from_ctors && is_ctor)
            continue;
         
         if(curr.isStatic())
            continue;
         
         InstructionParser ip = new InstructionParser(curr); 
         
         Iterator j;
         try
         {
            j = ip.iterator();
         }
         catch(RuntimeException e1)
         {
            JimaMisc.log().println("Bytecode parsing problem cs=" + cs 
                     + ", method=" + curr);
            continue;
         }

         while(j.hasNext())
         {
            Instruction ins = (Instruction) j.next();
            
            if(!(ins instanceof INVOKEVIRTUAL))
               continue;
            
            INVOKEVIRTUAL iv = (INVOKEVIRTUAL) ins;
            
            String receiver_type = iv.getClassName(cpg);
            boolean found = pm.has_class(receiver_type);
            if(found)
               continue;

            // ...Else: found is false => receiver is not a super-classes
            result.add(receiver_type);
         }
      }
      
      return result.iterator();     
   }
   
   public static boolean is_serialization_stuff(Field f)
   {
      boolean b = f.getName().equals("serialVersionUID");
      return b;
   }
   
   public static Iterator aggregated_classes(JavaClass jc, 
      boolean include_primitives)
   {
      Collection result = new_collection();
            
      Field[] fields = jc.getFields(); 
      for(int i = 0; i < fields.length; ++i)
      {
         Field f = fields[i];
         if(f.isStatic())
            continue;
         
         String s = JavaSpec.remove_array_indication(f.getSignature());
         
         if(!include_primitives)
         {
            if(JavaSpec.is_primitive(s))
               continue;
         }
         
         s = JaneMisc.signature_to_text(s, false);         
         result.add(s);
      }
      
      return result.iterator();      
   }
   
   public static void main(String[] args)
   {
      Iterator i = get_signatures_of_args("([Ljava/lang/String;)V");
      while(i.hasNext())
         System.out.println(i.next());
      
      i = get_signatures_of_args("([BII)V");
      while(i.hasNext())
         System.out.println(i.next());
   }
   
}
