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

import il.ac.technion.micropatterns.jane.lib.JavaSpec;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class JavaCollection
{
   private String collection_name_;
   private HashSet type_names_ = new HashSet();
   private boolean is_jre_;
   
   public JavaCollection(File jar_file, Vector names_of_classes)
   {
      type_names_.addAll(names_of_classes);
      is_jre_ = has(JavaSpec.JAVA_LANG_OBJECT);
            
      collection_name_ = jar_file.getName();
   }
   
   public boolean is_jre()
   {
      return is_jre_;
   }
      
   public boolean has(String type_name)
   {
      return type_names_.contains(type_name);
   }
   
   public Iterator type_names()
   {
      return type_names_.iterator();
   }
   
   public int size()
   {
      return type_names_.size();
   }   
   
   public String get_name()
   {
      return this.collection_name_;
   }
}
