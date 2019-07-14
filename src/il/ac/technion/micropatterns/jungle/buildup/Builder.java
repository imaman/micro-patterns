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








package il.ac.technion.micropatterns.jungle.buildup;

import il.ac.technion.micropatterns.jane.lib.ClassPathSpecifier;
import il.ac.technion.micropatterns.jane.lib.CollectionScanner;
import il.ac.technion.micropatterns.jane.lib.CollectionScanner.ScannerError;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import il.ac.technion.micropatterns.jungle.model.Ensemble;
import il.ac.technion.micropatterns.jungle.model.JavaCollection;
import il.ac.technion.micropatterns.jungle.model.ClassInfo.ClassInfoError;
import il.ac.technion.micropatterns.jungle.model.Ensemble.EnsembleError;

public class Builder
{
   private ClassPathSpecifier program_;
   private ClassPathSpecifier libs_ = new ClassPathSpecifier();
   private ClassPathSpecifier optional_jre_;
   
   private HashSet collections_ = new HashSet();
   
   public Builder(ClassPathSpecifier program, ClassPathSpecifier libs, 
      ClassPathSpecifier optional_jre)
   {
      optional_jre_ = optional_jre;
      program_ = program;
      if(libs != null)
         libs_ = libs;
   }
   
   public Ensemble go() throws ScannerError, EnsembleError, ClassInfoError
   {      
      ClassPathSpecifier full_cp = new ClassPathSpecifier();

      Vector prg_coll = build_collections(this.program_);      
      full_cp.add(this.program_);

      boolean has_jre = false;
      for(Iterator i = prg_coll.iterator();  i.hasNext(); )
      {
         JavaCollection curr = (JavaCollection) i.next();
         if(curr.is_jre())
         {
            has_jre = true;
            break;
         }            
      }
      
      Vector lib_coll = build_collections(this.libs_);
      for(Iterator i = lib_coll.iterator();  !has_jre && i.hasNext(); )
      {
         JavaCollection curr = (JavaCollection) i.next();
         if(curr.is_jre())
         {
            has_jre = true;
            break;
         }            
      }
      
      if(!has_jre)
      {
         ClassPathSpecifier temp = new ClassPathSpecifier();
         temp.add(this.libs_);
         temp.add(this.optional_jre_);

         Vector jre_coll = build_collections(this.optional_jre_);
         lib_coll.addAll(jre_coll);

         this.libs_ = temp;
      }
         
      full_cp.add(this.libs_);
      
      

      Ensemble result = new Ensemble(prg_coll.iterator(), 
         lib_coll.iterator(), full_cp);
      return result;
   }
   
   private Vector build_collections(ClassPathSpecifier cps)
      throws ScannerError
   {
      Vector result = new Vector();
      
      for(Iterator i = cps.files(); i.hasNext(); )
      {
         File curr = (File) i.next();
         Vector names = CollectionScanner.type_names_of(curr);

         JavaCollection c = new JavaCollection(curr, names);
//         System.out.println("New collection built: " + curr + "(" + names.size() + " types)");
         result.add(c);
      }
      
      return result;      
   }
   
   private void build(File f, Vector types)
   {
      
   }
}
