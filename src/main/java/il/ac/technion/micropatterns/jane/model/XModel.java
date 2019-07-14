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

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.elements.UniHandle;
import il.ac.technion.micropatterns.jane.lib.ClassPathSpecifier;
import il.ac.technion.micropatterns.jane.lib.JavaClassFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.bcel.classfile.JavaClass;

public class XModel
{
   private static final String FILE_NAME = "janemodel.dat";

   private Cache cache_ = new Cache();
   private JavaClassFactory class_factory_;
   private Persistent persistent_ = new Persistent();

   public static class ModelInitError extends Exception
   {   
      public ModelInitError(Throwable e) 
      {
         super(e);
      }
   }
   
   private static class Persistent implements Serializable
   {
      public Persistent() { }
      
      public VectorOfHandles all_types_ = new VectorOfHandles();
      public VectorOfHandles all_handles_ = new VectorOfHandles();
      
      private HandleFactory handle_factory_ = new HandleFactory();
      private HashMap obj2proxy_ = new HashMap();
      private ClassPathSpecifier class_path_ = new ClassPathSpecifier();
   }
   
   private static class ModelRequest implements IModelRequest 
   { 
      private XModel m_;
      
      public ModelRequest(XModel m)
      {
         m_ = m;
      }
      
      public XModel model()
      {
         return m_;
      }
   }
   
   private ModelRequest model_request_;
   
   private String model_name_;
   private File dir_;
   private File model_file_;
   

   public XModel() 
   { 
      model_request_ = new ModelRequest(this);
   }

   public XModel(File f) throws ModelInitError
   {
      this(f, true);
   }
   
   public XModel(File f, boolean reload) throws ModelInitError
   {
      
      model_file_ = f.getAbsoluteFile();
      dir_ = model_file_.getParentFile();
      model_name_ = model_file_.getName();
      
      if(dir_ == null)
      {
         throw new ModelInitError(new Exception("Invalid file " 
            + f.getAbsolutePath()));
      }         

      model_request_ = new ModelRequest(this);
      
      try
      {
         if(reload)
            load();
      
         init_factory();
      }
      catch(Throwable t)
      {
         throw new ModelInitError(t);
      }
   }
   
   public String class_path_string()
   {
      return persistent_.class_path_.toString();
   }
   
   public void add_class_path(ClassPathSpecifier cps) throws Exception
   {
      persistent_.class_path_.add(cps);
      init_factory();
   }
   
   private void init_factory() throws Exception
   {
      class_factory_ = new JavaClassFactory(persistent_.class_path_);
   }
   
   public UniHandle next_unihandle()
   {
      return persistent_.handle_factory_.next_unihandle();      
   }
   
   public void load() throws IOException, ClassNotFoundException
   {
      File f = model_file_;
      FileInputStream fis = null;
      try
      {
         if(!f.exists())
            throw new FileNotFoundException(f.getAbsolutePath());
         
         fis = new FileInputStream(f);
         ObjectInputStream ois = new ObjectInputStream(fis);
         
         persistent_ = (Persistent) ois.readObject();      
      }
      finally
      {
         if(fis != null)
            fis.close();
      }               
   }
   
   public File get_file()
   {
      return model_file_;
   }
   
   public void save() throws IOException
   {
      File f = model_file_;
      FileOutputStream fis = null;
      
      try
      {
         fis = new FileOutputStream(f);
         ObjectOutputStream oos = new ObjectOutputStream(fis);
         
         oos.writeObject(this.persistent_);
      }
      finally
      {
         if(fis != null)
            fis.close();
      }
   }
   
//   public synchronized UniHandle xadd_impl(IProxy p)
//   {
//      JimaMisc.ensure(p.get_handle() == null);
//      
//      IProxy temp = get_proxy(p.name());
//      if(temp != null)
//         return temp.get_handle();
//      
//      // ...Else:   
//      UniHandle result = persistent_.handle_factory_.next_handle();      
//      
//      persistent_.obj2proxy_.put(result, p);      
//      persistent_.all_handles_.add(result);
//      
//      return result;
//   }
   
   public void add_type(IHandle h)
   {
      
      persistent_.all_types_.add(h);      
//      JimaMisc.log().println("all_types_.size()=" 
//         + persistent_.all_types_.size());
   }
   
   /**
    * Associate a proxy object with a key.
    * Previous association of this key is overwritten.
    * @param k Key with which the specified proxy is associated.
    * @param p Proxy to be associated with the specified key.
    * @return true if the key had a previous association. Otherwise - false.
    */
   public boolean associate(Object k, IProxy p)
   {
      JimaMisc.ensure(p != null);
      JimaMisc.ensure(k != null);

      Object previous = persistent_.obj2proxy_.put(k, p);     
      return previous != null;
   }
   
   /**
    * Obtains the handles of all types held by the model.
    * @return Iterator to a collection of IHandle objects
    */
   public Iterator all_types()
   {
      return persistent_.all_types_.iterator();
   }
   
   public void set_all_types_handles(Iterator class_handles)
   {
      VectorOfHandles temp = new VectorOfHandles();
      
      while(class_handles.hasNext())
      {
         IHandle curr = (IHandle) class_handles.next();         
         temp.add(curr);
      }         
      
      persistent_.all_types_ = temp;
//      JimaMisc.log().println("set_all_types_handles(). size=" 
//         + persistent_.all_types_.size());
   }
   
         
   public synchronized IElement get_element(IProxy p)
   {
      IHandle h = p.get_handle();
      
      IElement result = (IElement) cache_.get(h);
      if(result != null)
         return result;
         
      result = p.create(model_request_);
      JimaMisc.ensure(result != null);
      
      this.cache_.put(h, result);
      
      return result;      
   }   

   public synchronized IElement get_element(Object o)
   {
      IProxy p = get_proxy(o);
      if(p == null)
         return null;
         
      return get_element(p);                  
   }   

   public synchronized IProxy get_proxy(Object o)
   {
      IProxy result = (IProxy) this.persistent_.obj2proxy_.get(o);      
      return result;      
   }   
   
   public synchronized IProxy get_proxy(IElement e)
   {
      IHandle h = e.get_handle();
      JimaMisc.ensure(h != null);

      IProxy result = (IProxy) this.persistent_.obj2proxy_.get(h);
      JimaMisc.ensure(result != null);
      
      return result;
   }
   
   public boolean is_empty()
   {
      return persistent_.handle_factory_.is_empty();
   }
   
   public int type_count()
   {
      return persistent_.all_types_.size();
   }
   
   private File resolve_file(String local_name)
   {
      return new File(dir_, model_name_ + ".janemodel." + local_name + ".dat");
   }
   
   public Serializable fetch(String name) throws Throwable
   {
      FileInputStream fis = null;

      try
      {
         File f = resolve_file(name);
         
         
         fis = new FileInputStream(f);
         BufferedInputStream bis = new BufferedInputStream(fis);
         
         ObjectInputStream ois = new ObjectInputStream(bis);
         
         Serializable result = (Serializable) ois.readObject();
         JimaMisc.log().println("Fetching from file " + f.getAbsolutePath()
            + " result=" + result);
            
         return result;
      }
      finally
      {
         if(fis != null)
            fis.close();
      }         
   }
   
   public void store(String name, Serializable obj) throws IOException
   {
      FileOutputStream fos = null;

      try
      {
         File f = resolve_file(name);
         
         fos = new FileOutputStream(f);
         BufferedOutputStream bos = new BufferedOutputStream(fos);
         
         ObjectOutputStream oos = new ObjectOutputStream(bos);
         
         oos.writeObject(obj);
         oos.close();
         
         JimaMisc.log().println("Store completed successfully at " 
            + f.getAbsolutePath() + " obj=" + obj);
      }
      finally
      {
         if(fos != null)
            fos.close();
      }         
   }
   
//   public ClassProxy get_first_super(ClassProxy cp)
//   {
//      ClassElement ce = (ClassElement) this.get_element(cp);
//      if(ce.is_java_object())
//         return null;
//         
//      String s = ce.super_class_name();            
//      ClassProxy result = (ClassProxy) this.get_proxy(s);
//      
//      return result;
//   }

//   public String get_first_super(ClassSpec ce)
//   {
//      if(ce.is_java_object())
//         return null;
//         
//      String result = ce.get_first_super().get_name();            
//      return result;
//   }
   
   public IVisitor accept(IVisitor v)
   {
      for(Iterator i = this.persistent_.all_handles_.iterator(); i.hasNext(); )
      {
         IHandle h = (IHandle) i.next();
         h.accept(v);         
      }              
      
      return v;   
   }
   
   public JavaClass class_for_name(String class_name)
   {
      JavaClass result = class_factory_.create(class_name);
      return result;
   }      
}
