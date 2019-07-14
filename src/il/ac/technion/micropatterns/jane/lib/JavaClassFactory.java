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

import il.ac.technion.jima.Cache;
import il.ac.technion.jima.JimaMisc;

import java.io.IOException;
import java.io.InputStream;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;

public class JavaClassFactory
{
   private ClassPath class_path_;
   private final Cache classes_ = new Cache(500);

   public JavaClassFactory() throws Exception
   {
      this(new ClassPathSpecifier());
   }

   public JavaClassFactory(ClassPathSpecifier class_path) throws Exception
   {
      class_path_ = new ClassPath(class_path.toString());
   }
   
   /** @return class object for given fully qualified class name.
    */
   public synchronized JavaClass create(String class_name)
   {         
      JimaMisc.ensure(class_name != null && !class_name.equals(""));
                  
      class_name = class_name.replace('/', '.');      
      JavaClass result = (JavaClass) classes_.get(class_name);

      if(result != null)
         return result;
         
      // ...Else:
      try
      {
         InputStream is = class_path_.getInputStream(class_name);
         result = new ClassParser(is, class_name).parse();
         class_name = result.getClassName();
      }
      catch (IOException e)
      {
         return null;
      }

      classes_.put(class_name, result);
      return result;
   }
}
