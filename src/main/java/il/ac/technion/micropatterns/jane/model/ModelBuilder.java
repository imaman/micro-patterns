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









package il.ac.technion.micropatterns.jane.model;


public class ModelBuilder
{
   
//   private static final String CLASS_SUFFIX = ".class";
//
//   private TypedModel model_ = TypedModel.create(new XModel());
//   private ClassPathSpecifier classpath_to_add_ = new ClassPathSpecifier();
//   private SubsetProxy all_types_subset_;
//   
//   public ModelBuilder() { }
//   
//   public ModelBuilder(XModel m)
//   {
//      model_ = TypedModel.create(m);
//   }
//   
//   public void clear()
//   {
//      classpath_to_add_ = new ClassPathSpecifier();
//   }
//
//   public void add_classpath(String cp)
//   {
//      classpath_to_add_.add(cp);
//   }
//
//   public void add_classpath(File cp)
//   {
//      classpath_to_add_.add(cp.getAbsolutePath());
//   }
//   
//   public void build(XModel m) throws Throwable
//   {
//      JimaMisc.ensure(m != null);
//      
//      File f = m.get_file();      
//      model_ = TypedModel.create(m);
//      
//      // Initialize the all-types subset.
//      // Placed here since we'd like to make sure this is the VERY FIRST
//      // subset.      
//      SubsetProxy all_pr 
//         = model_.get_subset_proxy(ModelDefinitions.ALL_TYPES_SUBSET);
//      SubsetElement se = model_.get_subset_element(all_pr);
//      se.set_is_library(true);
//
//
//      Vector created_subsets = new Vector();
//     
//      classpath_to_add_.subtract(model_.class_path_string());      
//      for(Iterator i = classpath_to_add_.files(); i.hasNext(); )
//      {
//         Vector types = new Vector();
//         File curr = (File) i.next();
//
//         build(curr, types);
//
//         //
//         // Create a new subset for the current class-path component
//         //
//         SubsetProxy new_pr = model_.get_subset_proxy(curr.getName());
//         SubsetElement new_se = model_.get_subset_element(new_pr);
//         
//         new_se.set_is_library(true);
//         
//         new_se.add_all(types.iterator());
//         new_se.store(model_);
//
//         created_subsets.add(new_se);         
//      }
//      
//      
//      
//      model_.add_class_path(classpath_to_add_);
//
//      //
//      // Use TreeLinearizer to obtain the "super-class-first" order
//      //
//      
//      TreeBuilder tl = new TreeBuilder();
//      for(Iterator i = model_.all_types(); i.hasNext(); )
//      {
//         IHandle h = (IHandle) i.next();        
//         ClassProxy cp = model_.get_class_proxy(h);
//         
//         ClassProxy spr = model_.get_first_super(cp);
//         if(spr != null)         
//            tl.add_pair(spr.get_handle(), h);
//      }         
//      
//      
//      TreeWalker walker = tl.create_walker();      
//      model_.set_all_types_handles(walker.create_linear_order());
//      
//      //
//      // Reorder all existing subsets by the "super-class-first" order
//      //
//      
//      SubsetProxy[] sets = model_.all_subsets();
//      for(int i = 0; i < sets.length; ++i)
//      {
//         if(sets[i].equals(all_pr))
//            continue;
//            
//         SubsetElement curr = model_.get_subset_element(sets[i]);
//         if(!curr.is_library())
//            continue;
//                    
//         curr.reorder(model_.all_types());         
//         curr.store(model_);
//      }         
//
//      //
//      // Save the all-types subset
//      //            
//      se.add_all(model_.all_types());
//      se.reorder(model_.all_types());
//      se.store(model_);  
//      
//      //
//      // Save the inheritance tree
//      //
//      VectorProxy gp 
//         = model_.get_vector_proxy(ModelDefinitions.INHERITANCE_TREE);
//      VectorElement ge = model_.get_vector_element(gp);
//      
//      ge.clear();
//      ge.add_item(walker);      
//      ge.store(model_);
//   }
//      
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
//            ClassProxy cp = add_type(temp);            
//            add_to_vec(types, cp);            
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
//         ClassProxy cp = add_type(temp);
//         add_to_vec(types, cp);
//
////         JimaMisc.log().println("Added type=" + cp);
//      }               
//   }
//   
//   private static void add_to_vec(Vector v, ClassProxy cp)
//   {
//      if(cp != null)
//         v.add(cp.get_handle());
//   }
//   
//   private static String translate_name(String class_name)
//   {
//      if(!class_name.endsWith(CLASS_SUFFIX))
//         return null;
//         
//      String result = class_name.replace('/', '.');
//      return result.substring(0, result.length() - CLASS_SUFFIX.length());
//   }
//   
//   
//   private ClassProxy add_type(String name) throws ClassNotFoundException
//   {   
//      if(name == null)
//         return null;
//    
//      ClassProxy cp = model_.get_class_proxy(name);
//      return cp;
//   }
//   
//   public TypedModel result()
//   {
//      return model_;
//   }      
}
