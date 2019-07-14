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
import il.ac.technion.jima.util.TreeWalker;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.elements.EmptyVisitor;
import il.ac.technion.micropatterns.jane.elements.LazyVectorElement;
import il.ac.technion.micropatterns.jane.elements.MethodHandle;
import il.ac.technion.micropatterns.jane.elements.SubsetElement;
import il.ac.technion.micropatterns.jane.elements.SubsetHandle;
import il.ac.technion.micropatterns.jane.elements.SubsetProxy;
import il.ac.technion.micropatterns.jane.elements.UniHandle;
import il.ac.technion.micropatterns.jane.elements.VectorElement;
import il.ac.technion.micropatterns.jane.elements.VectorHandle;
import il.ac.technion.micropatterns.jane.lib.ClassPathSpecifier;
import il.ac.technion.micropatterns.jane.lib.MapOfMethods;
import il.ac.technion.micropatterns.jane.model.IHandle;
import il.ac.technion.micropatterns.jane.model.ITable;
import il.ac.technion.micropatterns.jane.model.Table;
import il.ac.technion.micropatterns.jane.model.XModel;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;


public class TypedModel implements IResultKeeeper, IClassSpecProvider
{
   private XModel m_;
   private VectorHandle std_type_props_handle_;
   
   public Table class_table_;
   public Table method_table_;
   public Table subset_table_;
   public Table vector_table_;
   
   public HashSet saveables_ =  new HashSet();
   
   private TypedModel(XModel m) throws Throwable
   {
      class_table_ = new Table("class-table", ClassHandle.PROTOTYPE, this);
      method_table_ = new Table("method-table", MethodHandle.PROTOTYPE, this);
      subset_table_ = new Table("subset-table", SubsetHandle.PROTOTYPE, this);
      vector_table_ = new Table("vector-table", VectorHandle.PROTOTYPE, this);
      
      m_ = m;
      
      make_std_definitions();      
      std_type_props_handle_ = get_vector_handle(ModelDefinitions.STD_TYPE_PROPS);
   }


   private void save_table(ITable t) throws IOException
   {
      this.m_.store(t.get_name(), t);
   }
   
   public void save() throws IOException
   {
      JimaMisc.log().println("Model.save.post.Methods=" + get_delegates_map());

      
      for(Iterator i = saveables_.iterator(); i.hasNext(); )
      {
         Table curr = (Table) i.next();
         save_table(curr);
      }         
      
      m_.save();
      JimaMisc.log().println(" Model.save.pre.Methods=" + get_delegates_map());
   }
   
   
   
   public void load() throws Throwable
   {
      try
      {
         class_table_ = load_table(class_table_.get_name());
         method_table_ = load_table(method_table_.get_name());
         subset_table_ = load_table(subset_table_.get_name());
         vector_table_ = load_table(vector_table_.get_name());
      }
      catch(FileNotFoundException e)
      {
         // Absorb
      }
   }

   
   /* (non-Javadoc)
    * @see il.ac.technion.micropatterns.jane.typedmodel.IClassSpecProvider#class_for_name(java.lang.String)
    */
   public ClassHandle class_for_name(String type_name)
   {
      ClassHandle result = this.class_handle_of(type_name);
      return result;
   }
   
   private Table load_table(String table_name) throws Throwable
   {
      Table result = (Table) m_.fetch(table_name);
      result.set_model(this);
      
      return result;
   }
   
   private void make_std_definitions()
   {
      VectorHandle.PROTOTYPE.produce_impl(this, 
         ModelDefinitions.STD_TYPE_PROPS, new TypeProps());

//      VectorHandle.PROTOTYPE.produce(this, ModelDefinitions.ALL_METHODS);
//      VectorHandle.PROTOTYPE.produce(this, ModelDefinitions.INHERITANCE_TREE);
//                  
//      SubsetHandle.PROTOTYPE.produce(this, ModelDefinitions.ALL_TYPES_SUBSET);
//      
//      SubsetHandle.PROTOTYPE.produce(this, 
//         ModelDefinitions.REIMPLEMENTOR_SUBSET_NAME);
//      SubsetHandle.PROTOTYPE.produce(this, ModelDefinitions.SINK_SUBSET_NAME);
   }
   
   public static TypedModel create(XModel m) 
   {
      try
      {
         return new TypedModel(m);
      }
      catch (Throwable e)
      {
//         JimaMisc.stop(e);
         return null; // Faked
      }
   }
   
   public void add_result_set(DescCP dcp, SubsetElement se) 
   {
      this.subset_table_.add(this, dcp.get_full_name(), se);      
   }
   
   
   public ClassHandle class_handle_of(String class_name)
   {
      return (ClassHandle) class_table_.lookup_handle(class_name);
   }
   

//   public ClassProxy get_class_proxy(Object o)
//   {
//      return (ClassProxy) m_.get_proxy(o);
//   }
//
//   public ClassProxy get_class_proxy(ClassHandle h)
//   {
//      return get_class_proxy((Object) h);
//   }
   
//   public ClassSpec get_class_element(ClassHandle h)
//   {
//      return null;   
//   }

   public ClassSpec get_class_element(ClassHandle ch)
   {
      return ch.typed_value(this);
   }

   public ClassSpec get_class_element(String class_name)
   {      
      ClassHandle temp = class_handle_of(class_name);
      return temp.typed_value(this);
   }
   
   public SubsetHandle get_subset_handle(String subset_name)
   {
      SubsetHandle result 
         = (SubsetHandle) subset_table_.lookup_handle(subset_name);
      return result;         
   }

   public SubsetProxy get_subset_proxy(IHandle h)
   {
      return (SubsetProxy) m_.get_proxy(h);
   }
   
   public SubsetElement get_subset_element(SubsetHandle h)
   {
      return (SubsetElement) h.typed_value(this);
   }


   public VectorHandle get_vector_handle(String vector_name)
   {
      VectorHandle result 
         = (VectorHandle) vector_table_.lookup_handle(vector_name);
      return result;
   }
   
   public synchronized VectorElement get_vector_element(String name)
   {
      VectorHandle temp = get_vector_handle(name);
      if(temp == null)
      {
         temp = (VectorHandle) this.vector_table_.add(this, name,
            new VectorElement());
      }         
      
      VectorElement result = temp.typed_value(this);
      return result;
   }   

   public SubsetHandle get_all_types()
   {
      return get_subset_handle(ModelDefinitions.ALL_TYPES_SUBSET);
   }
   
   /**
    * Returns an iterator over all MethodSpec elements.
    * @return An iterator over all MethodSpec elements.
    */
   public Iterator get_all_methods()
   {
      return get_vector_element(ModelDefinitions.ALL_METHODS).items();
   }
   
   public TreeWalker get_inheritance_walker()
   {
      VectorElement ge = get_vector_element(ModelDefinitions.INHERITANCE_TREE);
         
      TreeWalker result = (TreeWalker) ge.at(0);
      return result;
   }
   
   public int type_count()
   {
      return m_.type_count();
   }
   
   public Iterator all_types()
   {
      return this.class_table_.all_handles();
   }
   
   public void store(String name, SubsetElement se) throws IOException
   {
      m_.store(name, se);
   }

   public void store(String name, VectorElement ve) throws IOException
   {
      m_.store(name, ve);
   }
   
   public void store(String name, LazyVectorElement lve) throws IOException
   {
      m_.store(name, lve);
   }
   
   
   public XModel inner()
   {
      return m_;
      
   }
   
   public void add_class_path(ClassPathSpecifier cps) throws Exception
   {
      m_.add_class_path(cps);
   }
   
   public String class_path_string()
   {
      return m_.class_path_string();
   }
   
//   public ClassProxy get_first_super(ClassProxy cp)
//   {
//      ClassSpec ce = get_class_element(cp);
//      return get_first_super(ce);
//   }
//
//   public ClassProxy get_first_super(ClassSpec ce)
//   {
//      String temp = m_.get_first_super(ce);
//      if(temp == null)
//         return null;
//
//      ClassProxy result = get_class_proxy(temp);
//      return result;
//   }
   
   public void set_all_types_handles(Iterator class_handles)
   {
      m_.set_all_types_handles(class_handles);
   }
    
   private static class AllSubsetsVisitor extends EmptyVisitor
   {
      public Vector result_ = new Vector();
      
      public void visit(SubsetProxy sp) 
      { 
         result_.add(sp);
      }
   }   
      
//   public SubsetProxy[] all_subsets()
//   {
//      AllSubsetsVisitor vis = new AllSubsetsVisitor();
//      m_.accept(vis);
//      
//      SubsetProxy[] result = new SubsetProxy[vis.result_.size()];
//      for(int i = 0; i < result.length; ++i)
//         result[i] = (SubsetProxy) vis.result_.elementAt(i);
//         
//      return result;
//   }
   
   public synchronized TypeProps get_std_props()
   {
      VectorElement ve = get_vector_element(ModelDefinitions.STD_TYPE_PROPS);
      if(ve.size() == 0)
         ve.add_item(new TypeProps());
      TypeProps result = (TypeProps) ve.at(0);
      
      return result;      
   }
   
   public synchronized void set_std_props(TypeProps tp) 
   {
      VectorElement ve = new VectorElement();
      ve.add_item(tp);
      
      this.vector_table_.add(this, ModelDefinitions.STD_TYPE_PROPS, ve);
         
   }
   
   public synchronized MapOfMethods get_delegates_map()
   {
      VectorElement temp 
         = get_vector_element(ModelDefinitions.STD_METHOD_PROPS);
         
      if(temp.size() == 0)
         temp.add_item(new MapOfMethods());
         
      return (MapOfMethods) temp.at(0);         
   }
   

   /**
    * @return
    */
   public File get_file()
   {
      return m_.get_file();
   }
   

   public UniHandle next_unihandle()
   {
      return this.m_.next_unihandle();
   }   
}
