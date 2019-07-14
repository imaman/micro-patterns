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
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;


public class Table implements ITable  
{
   private static class Holder implements Serializable
   {
      public IElement data_;

      public Holder(IElement data)
      {
         data_ = data;
      }      
   }
   
   private String name_;
   private HashMap handle2item_ = new HashMap();
   private HashMap name2handle_ = new HashMap();
   
   private IHandle prototype_;
   private TypedModel model_;

   public Table() { }
   
   public Table(String name, IHandle prototype, TypedModel model)
   {
      model_ = model;
      name_ = name;
      prototype_ = prototype;
      
      model_.saveables_.add(this);
   }

   private void writeObject(ObjectOutputStream out)
     throws IOException
   {
      out.writeObject(name_);
      out.writeObject(handle2item_);
      out.writeObject(name2handle_);
      out.writeObject(prototype_);
   }
   
   private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException
  {
      name_ = (String) in.readObject();
      handle2item_ = (HashMap) in.readObject();
      name2handle_ = (HashMap) in.readObject();
      prototype_ = (IHandle) in.readObject();
  }
    
   
   public String get_name()
   {
      return name_;
   }
   
   
   public IElement get(IHandle h)
   {
      Holder result = (Holder) handle2item_.get(h);
      JimaMisc.ensure(result != null, "handle=" + h);
      
      if(result.data_ != null)
         result.data_.set_model(this.model_);
         
      return result.data_;
   }
   
   public void set(IHandle h, IElement e)
   {
      JimaMisc.ensure(h != null);
      
      Holder holder = (Holder) handle2item_.get(h);
      JimaMisc.ensure(holder != null);
      
      holder.data_ = e;
   }
   
   public void set(String name, IElement e)
   {
      IHandle h = lookup_handle(name);
      JimaMisc.ensure(h != null);

      set(h, e);
   }
   
   public IHandle add(TypedModel m, String name, IElement e)
   {
      IHandle h = lookup_handle(name);
      if(h == null)
         h = prototype_.new_one(m.next_unihandle());
         
      h.set_name(name);
      
      if(e != null)
         e.set_handle(h);
      
      Holder holder = new Holder(e);
      
      handle2item_.put(h, holder);
      name2handle_.put(name, h);
      
      return h;
   }
   
   public IHandle lookup_handle(String name)
   {
      IHandle result = (IHandle) name2handle_.get(name);
      return result;
   }
   
   public String toString()
   {
      return "Table-" + name_ + "(size=" + size() + ")";
   }
   


   
//   /**
//    * @see il.ac.technion.micropatterns.jane.model.ITable#produce()
//    */
//   public IHandle produce()
//   {
//      return prototype_.produce(model_);
//   }

   /**
    * @see il.ac.technion.micropatterns.jane.model.ITable#all_handles()
    */
   public Iterator all_handles()
   {
      return handle2item_.keySet().iterator();
   }
   
   public int size()
   {
      return handle2item_.size();
   }      


   /**
    * @see il.ac.technion.micropatterns.jane.model.ITable#set_model(il.ac.technion.micropatterns.jane.typedmodel.TypedModel)
    */
   public void set_model(TypedModel m)
   {
      model_ = m;
      
      model_.saveables_.remove(this);
      model_.saveables_.add(this);
   }
   
   public boolean equals(Object other)
   {
      if(other == null)
         return false;
         
      if(!(other instanceof Table))
         return false;
         
      Table rhs = (Table) other;
      return this.name_.equals(rhs.name_);
   }
   
   public int hashCode()
   {
      return this.name_.hashCode();
   }

}
