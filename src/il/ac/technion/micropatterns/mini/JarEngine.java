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








package il.ac.technion.micropatterns.mini;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.lib.ClassPathSpecifier;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import il.ac.technion.micropatterns.jungle.model.ClassFactory;
import il.ac.technion.micropatterns.jungle.model.ClassFactory.ClassFactoryError;
import org.apache.bcel.classfile.JavaClass;

public class JarEngine
{

   private Vector types = new Vector();
   private ClassPathSpecifier classPath;
   private Hashtable factories = new Hashtable();

   public JarEngine(ClassPathSpecifier cps) 
      throws IOException
   {
      this(cps, new ClassPathSpecifier());
   }
   
   public JarEngine(ClassPathSpecifier cps, ClassPathSpecifier auxLibs) 
      throws IOException
   {
      classPath  = cps;
      types = JarScan.typesOf(classPath);
      
      ClassPathSpecifier full = new ClassPathSpecifier();
      full.add(classPath);
      full.add(auxLibs);
      
      for(Iterator i = full.files(); i.hasNext(); )
      {
         File curr = (File) i.next();

         ClassFactory factory = new ClassFactory(curr.getAbsolutePath());
         String key = curr.getName();
         factories.put(key, factory);
         
//         System.out.println("Add factory: " + key + ", " + curr);
      }
      
   }
   
   public JavaClassRep classForPtr(ClassPtr cp) throws ClassFactoryError
   {
      JimaMisc.ensure(cp != null);
      JimaMisc.ensure(factories != null);
      ClassFactory factory = (ClassFactory) factories.get(cp.jar);
      JavaClass jc = factory.create_java_class(cp.name);
      JavaClassRep result = new JavaClassRep(jc);
      
      return result;
   }
   
   public Iterator allTypes()
   {
      return types.iterator();
   }

}
