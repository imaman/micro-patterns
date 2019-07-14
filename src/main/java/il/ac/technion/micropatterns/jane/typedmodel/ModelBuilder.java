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

package il.ac.technion.micropatterns.jane.typedmodel;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.jima.util.TreeBuilder;
import il.ac.technion.jima.util.TreeWalker;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.elements.MethodSpec;
import il.ac.technion.micropatterns.jane.elements.SubsetElement;
import il.ac.technion.micropatterns.jane.elements.SubsetHandle;
import il.ac.technion.micropatterns.jane.elements.SubsetProxy;
import il.ac.technion.micropatterns.jane.elements.VectorElement;
import il.ac.technion.micropatterns.jane.lib.ClassPathSpecifier;
import il.ac.technion.micropatterns.jane.lib.CollectionScanner;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.lib.MapOfMethods;
import il.ac.technion.micropatterns.jane.model.XModel;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;


public class ModelBuilder
{
   private TypedModel model_ = TypedModel.create(new XModel());
   private ClassPathSpecifier classpath_to_add_ = new ClassPathSpecifier();
   private SubsetProxy all_types_subset_;
   
   public ModelBuilder() { }
   
   public ModelBuilder(XModel m)
   {
      model_ = TypedModel.create(m);
   }
   
   public void clear()
   {
      classpath_to_add_ = new ClassPathSpecifier();
   }

   public void add_classpath(String cp)
   {
      classpath_to_add_.add(cp);
   }

   public void add_classpath(File cp)
   {
      classpath_to_add_.add(cp.getAbsolutePath());
   }
   
   public void build(XModel m) throws Throwable
   {
      JimaMisc.ensure(m != null);
      
      model_ = TypedModel.create(m);
      
      
      ClassPathSpecifier cps 
         = new ClassPathSpecifier(model_.class_path_string());
      cps.add(classpath_to_add_);

      classpath_to_add_ = cps;
      
      // Initialize the all-types subset.
      // Placed here since we'd like to make sure this is the VERY FIRST
      // subset.      
      SubsetElement all_types_x = new SubsetElement();
      SubsetHandle all_types_h = (SubsetHandle) model_.subset_table_.add(model_, 
         ModelDefinitions.ALL_TYPES_SUBSET, all_types_x);
      
      all_types_x.set_is_library(true);


      Vector created_subsets = new Vector();

      JimaMisc.log().println("mb-1");
     
//      classpath_to_add_.subtract(existing);    
      
      JimaMisc.log().println("classpath_to_add_=" + classpath_to_add_);
      
      for(Iterator i = classpath_to_add_.files(); i.hasNext(); )
      {
         File curr = (File) i.next();
         Vector names = CollectionScanner.type_names_of(curr);

         Vector types = new Vector();
         for(Iterator j = names.iterator(); j.hasNext() ;)
         {
            String type_name = (String) j.next();
            ClassHandle new_one = add_type(type_name);
            if(new_one == null)
               continue;
            
            types.add(new_one);            
         }
         
         //
         // Create a new subset for the current class-path component
         //
         SubsetElement new_se = new SubsetElement();
//         SubsetHandle.PROTOTYPE.produce_impl(model_, curr.getName(), new_se);
         model_.subset_table_.add(model_, curr.getName(), new_se);
         
         new_se.set_is_library(true);
         
         new_se.add_all(types.iterator());
//         new_se.store(model_);

         created_subsets.add(new_se);         
      }
      
      
      JimaMisc.log().println("mb-2");
      
      model_.add_class_path(classpath_to_add_);
      

      //
      // Build the 'all methods' list
      //
//      VectorElement all_methods
//         = model_.get_vector_element(ModelDefinitions.ALL_METHODS);
//      all_methods.clear();
      
      int n = -1;
      for(Iterator i = model_.all_types(); i.hasNext(); )
      {       
         ClassHandle ch = (ClassHandle) i.next();     
         ClassSpec curr = model_.get_class_element(ch);

         for(Iterator j = curr.all_methods(); j.hasNext(); )
         {
            ++n;
            MethodSpec ms = (MethodSpec) j.next();
            model_.method_table_.add(model_, Integer.toString(n), ms);
//            all_methods.add_item(ms);
         }                     
      }         

      JimaMisc.log().println("mb-3");
      
//      all_methods.store(model_);

      //
      // Use TreeLinearizer to obtain the "super-class-first" order
      //
      
      
      HashSet floatings = new HashSet();
      
      TreeBuilder tl = new TreeBuilder();
      for(Iterator i = model_.all_types(); i.hasNext(); )
      {
         ClassHandle h = (ClassHandle) i.next();        
         ClassSpec cs = h.typed_value(model_);
         if(cs.is_java_object())
            continue;
         
         ClassHandle spr = cs.get_first_super();
         if(spr == null)         
         {
            floatings.add(cs);
            continue;
            
//            cs.set_use_normal_super(false);
//            spr = cs.get_first_super();
//            JimaMisc.ensure(spr != null, "cs=" + cs);
         }
         
         Iterator interfaces = cs.first_interfaces();
         if(interfaces == null)
         {
            floatings.add(cs);
            continue;
         }

         JimaMisc.ensure(!cs.is_java_object(), "h=" + h + ", spr=" + spr);
         tl.add_pair(spr, h);
      }         

      for(Iterator i = floatings.iterator(); i.hasNext(); )
         JimaMisc.log().println("Floating class=" + i.next());
//      
//      for(Iterator i = roots.iterator(); i.hasNext(); )
//      {
//         ClassHandle curr = (ClassHandle) i.next();
//         if(curr != java_lang_object_h)
//            tl.add_pair(java_lang_object_h, curr);
//      }
//      
      JimaMisc.log().println("mb-4");
      
      
      TreeWalker root = null;
      for(TreeWalker w = tl.create_walker(); w.ok(); w = w.next_brother())
      {
         ClassHandle curr = (ClassHandle) w.data();
         if(curr.get_name().equals(JavaSpec.JAVA_LANG_OBJECT))
         {
            root = w;
            break;
         }            
      }
      
      if(root == null)
         throw new MissingJavaLangObjectError();
         
      model_.set_all_types_handles(root.create_linear_order());
      
      JimaMisc.log().println("========================");
      JimaMisc.log().println("=                      =");
      JimaMisc.log().println("===> Linear reorder <===");
      JimaMisc.log().println("=                      =");
      JimaMisc.log().println("========================");
      for(Iterator i = root.create_linear_order(); i.hasNext(); )
         JimaMisc.log().println(i.next());
      
      JimaMisc.log().println("End of linear order");
      
      //
      // Reorder all existing subsets by the "super-class-first" order
      //
      
//      SubsetProxy[] sets = model_.all_subsets();
      for(Iterator i = model_.subset_table_.all_handles(); i.hasNext(); )
      {
         SubsetHandle sh = (SubsetHandle) i.next();
         if(sh.equals(all_types_h))
            continue;
            
         SubsetElement curr = sh.typed_value(model_);
         if(!curr.is_library())
            continue;
                    
         Iterator correct_order = root.create_linear_order();
//         curr.reorder(model_.all_types());
         curr.reorder(correct_order);         
      }         
      
      JimaMisc.log().println("mb-5");

      //
      // Save the all-types subset
      //            
      all_types_x.add_all(model_.all_types());
      all_types_x.reorder(model_.all_types());
//      se.store(model_);  
      
      //
      // Save the inheritance tree
      //
//      VectorProxy gp =
//         = model_.get_vector_proxy(ModelDefinitions.INHERITANCE_TREE);
//      model_.get_vector_element(gp);

      VectorElement ge =  new VectorElement();      
      ge.add_item(root);            
      model_.vector_table_.add(model_, ModelDefinitions.INHERITANCE_TREE, ge);
      
      
      // 
      // Create the method property 
      //
      ge = new VectorElement();
      ge.add_item(new MapOfMethods());
      model_.vector_table_.add(model_, ModelDefinitions.STD_METHOD_PROPS, ge);
      
      JimaMisc.log().println("mb-6");

      
      JimaMisc.log().println("sizeof(class)=" + model_.class_table_.size());
      JimaMisc.log().println("sizeof(method)=" + model_.method_table_.size());

      model_.save();
   }
      
//   private void build(File f, Vector types)
//      throws ClassNotFoundException, Exception
//   {
//      if(f.isDirectory())
//         build_from_dir("", f, types);
//      else 
//      {
//         try
//         {
//            JarFile jf = new JarFile(f);
//            build_from_jar(jf, types);
//         }
//         catch(IOException e)
//         {
//            throw new Exception("Problem with file " + f.getAbsolutePath());
//         }
//      }
//   }
//
//   private void build_from_dir(String path, File dir, Vector types)
//      throws ClassNotFoundException
//   {
//      File[] entries = dir.listFiles();
//      
//      for(int i = 0; i < entries.length; ++i)
//      {
//         File f = entries[i];
//
//         if(f.isDirectory())         
//         {
//            String temp = path + f.getName() + ".";
//            build_from_dir(temp, f, types);
//         }
//         else if(f.getName().endsWith(".class"))
//         {
//            String temp = path + JimaMisc.change_suffix(f.getName(), "");
//            JimaMisc.log().println("temp=" + temp);
//            
//            ClassHandle ch = add_type(temp);            
//            add_to_vec(types, ch);            
//         }
//      }
//   }
//
//   private void build_from_jar(JarFile jar, Vector types) 
//      throws ClassNotFoundException
//   {
//      Enumeration iter = jar.entries();
//      while(iter.hasMoreElements())
//      {         
//         JarEntry curr = (JarEntry) iter.nextElement();
//         
//         String temp = translate_name(curr.getName());
//         if(temp == null)
//            continue;
//
//         ClassHandle ch = add_type(temp);
//         add_to_vec(types, ch);
//
////         JimaMisc.log().println("Added type=" + cp);
//      }               
//   }
   
//   private static void add_to_vec(Vector v, ClassHandle ch)
//   {
//      if(ch != null)
//         v.add(ch);
//   }
   
//   private static String translate_name(String class_name)
//   {
//      if(!class_name.endsWith(CLASS_SUFFIX))
//         return null;
//         
//      String result = class_name.replace('/', '.');
//      return result.substring(0, result.length() - CLASS_SUFFIX.length());
//   }
   
   
   private ClassHandle add_type(String name) 
   {   
      if(name == null)
         return null;
    
      ClassHandle result 
         = (ClassHandle) ClassHandle.PROTOTYPE.produce(model_, name);
         
      this.model_.inner().add_type(result);
         
//      ClassProxy cp = model_.get_class_proxy(name);
      return result;
   }
   
   public TypedModel result()
   {
      return model_;
   }      
}
