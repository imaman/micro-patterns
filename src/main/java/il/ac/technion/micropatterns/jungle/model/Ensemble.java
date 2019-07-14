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
import il.ac.technion.jima.util.Collections;
import il.ac.technion.jima.util.TreeBuilder;
import il.ac.technion.jima.util.TreeWalker;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.ClassPathSpecifier;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;
import il.ac.technion.micropatterns.jungle.model.ClassFactory.ClassFactoryError;
import il.ac.technion.micropatterns.jungle.model.ClassInfo.ClassInfoError;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.SyntheticRepository;

public class Ensemble
{
   private int size_ = 0;
   private TreeWalker root_;
   
   private Vector all_names_ = new Vector();
   private HashSet all_prg_names_ = new HashSet();
   
   private Vector prg_collections_ = new Vector();
   private Vector lib_collections_ = new Vector();
   private TypedModel model_ = null;
   private HashSet floatings_ = new HashSet();
   private ClassFactory cf_;
   private HashSet markedTypes_ = new HashSet();
   
   
//   private 
      

   public static class EnsembleError extends Exception
   {
      public EnsembleError(String s) { super(s); }
   }
   
   public Ensemble(Iterator prg_collections, Iterator lib_collections,
      ClassPathSpecifier cps) throws EnsembleError, ClassInfoError
   {
      cf_ = new ClassFactory(cps);
      
      Collections.addAll(lib_collections_, lib_collections);
      Collections.addAll(prg_collections_, prg_collections);
      
      Vector all = new Vector();
      Collections.addAll(all, lib_collections_);
      Collections.addAll(all, prg_collections_);
      

      
      
//      // d/o
//      System.err.println("LIB COLLS");
//      for(Iterator i = lib_collections_.iterator(); i.hasNext(); )
//      {
//         JavaCollection curr = (JavaCollection) i.next();
//         System.err.println("Colll: " + curr.get_name() + " " + curr.size());
//      }
//      System.err.println("PRG COLLS");
//      for(Iterator i = prg_collections_.iterator(); i.hasNext(); )
//      {
//         JavaCollection curr = (JavaCollection) i.next();
//         System.err.println("Colll: " + curr.get_name() + " " + curr.size());
//      }
      
      for(Iterator i = all.iterator(); i.hasNext(); )
      {         
         JavaCollection curr = (JavaCollection) i.next();
         Collections.addAll(all_names_, curr.type_names()); 
      }
      
      for(Iterator i = prg_collections_.iterator(); i.hasNext(); )
      {
         JavaCollection curr = (JavaCollection) i.next();
         Collections.addAll(all_prg_names_, curr.type_names());          
      }
      
      TreeBuilder tl = new TreeBuilder();
      int count = 0;
      for(Iterator i = all_names_.iterator(); i.hasNext(); ++count)
      {
         String type_name = (String) i.next();
         
         ClassInfo cinfo = create_class(type_name);
         
         
         ClassHandle h = cinfo.as_ch();
         
//         System.out.println("h= " + h + ", [" + h.getClass() + "]");
         JimaMisc.ensure(h != null, "type_name=" + type_name);
         
         ClassSpec cs = h.typed_value(model_);
         if(cs.is_java_object())
            continue;
         
         ClassHandle spr = cs.get_first_super();
         if(spr == null)         
         {
            floatings_.add(cs);
            continue;            
         }
         
         Iterator interfaces = cs.first_interfaces();
         if(interfaces == null)
         {
            floatings_.add(cs);
            continue;
         }
         
         JimaMisc.ensure(!cs.is_java_object(), "h=" + h + ", spr=" + spr);
         tl.add_pair(spr, h);
         
//         System.out.println("Adding: " + spr + ", " + h);
      }         
      
      for(Iterator i = floatings_.iterator(); i.hasNext(); )
         JimaMisc.log().println("Floating class=" + i.next());
      
      for(TreeWalker w = tl.create_walker(); w.ok(); w = w.next_brother())
      {
         ClassHandle ch = (ClassHandle) w.data();
//         System.out.println("Found root " + ch);
         if(ch.get_name().equals(JavaSpec.JAVA_LANG_OBJECT))
            root_ = w;          
      }
      
      markedTypes_ = findMarkedNodes(root_, all_prg_names_);
      
      ClassHandle ch = (ClassHandle) root_.data();
//      System.out.println("Actual Root=" + ch);
      
      JimaMisc.ensure(ch.get_name().equals(JavaSpec.JAVA_LANG_OBJECT));

      size_ = count;
//      System.out.println("size_=" + size_);
      
      if(root_ == null)
         throw new EnsembleError("Missing class " + JavaSpec.JAVA_LANG_OBJECT);            
   }
   
   private static HashSet findMarkedNodes(TreeWalker w, HashSet types)
   {
      HashSet result = new HashSet();
      mark(w, types, result);
      
//      System.err.println("markedtypes.size()=" + result.size());
//      System.err.println("types.size()=" + result.size());
      
      return result;
   }
      
   private static boolean mark(TreeWalker w, HashSet types, HashSet marked)
   {
      boolean result = false;
      
      ClassHandle h = (ClassHandle) w.data();
      String cn = h.get_name();
      if(types.contains(cn))
         result = true;
      
      for(TreeWalker c = w.first_child(); c.ok(); c = c.next_brother())
      {
         boolean b = mark(c, types, marked);
         if(b)
            result = true;
      }

      if(result)
         marked.add(h);
      
      return result;                  
   }
   
   public boolean type_name_in_prg(String type_name)
   {
      return all_prg_names_.contains(type_name);
   }
   
   JavaClass create_java_class(String type_name) throws ClassFactoryError
   {
      return cf_.create_java_class(type_name);
   }
   
   public ClassInfo create_class(String type_name) throws ClassInfoError
   {
//      Logger.global.info("type_name=" + type_name);
      ClassInfo result = new ClassInfo(this, type_name);
      return result;
   }
   
   public TreeWalker get_root()
   {
      return root_;
   }
   
   public int size()
   {
      return size_;
   }
   
   public Iterator get_prg_collections()
   {      
      return prg_collections_.iterator();
   }

   public Iterator get_lib_collections()
   {      
      return lib_collections_.iterator();
   }
   
   public HashSet get_marked_types()
   {
      return markedTypes_;
   }
   

}
