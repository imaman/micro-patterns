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








package il.ac.technion.micropatterns.jungle.model;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.lib.ClassPathSpecifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.SyntheticRepository;

public class ClassFactory
{   
   private ClassPath class_path_ = new ClassPath();
   private HashMap name2class_ = new HashMap();
   private final SyntheticRepository repository;
   private static final int LIMIT = 500;


   /** @return class object for given fully qualified class name.
    * @throws IOException
    */
   
   public JavaClass create_java_class(String class_name) throws ClassFactoryError
   {
      try
      {
         JavaClass result = create_java_class_impl(class_name);
         return result;         
      }
      catch(IOException e)
      {
         throw new ClassFactoryError("Loading of " + class_name 
                  + " has failed", e);
      }
   }
   
   private JavaClass create_java_class_impl(String class_name) throws IOException
   {
      JimaMisc.ensure(class_name != null && class_name.length() > 0);

      class_name = class_name.replace('/', '.');
      JavaClass clazz = (JavaClass) name2class_.get(class_name);

      if (clazz != null)
         return clazz;
      
      InputStream is = class_path_.getInputStream(class_name);
      clazz = new ClassParser(is, class_name).parse();
      clazz.setRepository(repository);
      class_name = clazz.getClassName();

      if(name2class_.size() > LIMIT)
         name2class_.clear();
      
      name2class_.put(class_name, clazz);
      return clazz;
   }
   
   public ClassFactory(ClassPathSpecifier cps)
   {
      this(cps.toString());
   }
   
   public ClassFactory(String cp)
   {
      class_path_ = new ClassPath(cp);
      repository = SyntheticRepository.getInstance(class_path_);      
   }

   public static class ClassFactoryError extends Exception
   {
      public ClassFactoryError(String s) { super(s); }
      public ClassFactoryError(String s, Exception e) { super(s,e); }
   }   
}
