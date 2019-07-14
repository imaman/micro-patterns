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











package il.ac.technion.micropatterns.jane.elements;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.model.IElement;
import il.ac.technion.micropatterns.jane.model.IHandle;
import il.ac.technion.micropatterns.jane.model.ITable;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

public abstract class AbstractHandle implements IHandle
{
   private int handle_id_;
   protected String name_;


   protected abstract IElement target();
   
   public AbstractHandle() { }

   protected AbstractHandle(int handle_id) 
   { 
      handle_id_ = handle_id;
   }
   
   public AbstractHandle(UniHandle h)
   {
      this(h.handle_id_);
   }
   
   public void set_name(String name)
   {
      name_ = name;
   }
   
   
   public void set_unihandle(UniHandle u)
   {
      handle_id_ = u.handle_id_;
   }
   
   public int hashCode()
   {
      return handle_id_;
   }
   
   public boolean equals(Object other)
   {
      if(other == null)
         return false;         

      if(other == this)
         return true;
         
      if(other.getClass() != this.getClass())
         return false;

      AbstractHandle rhs = (AbstractHandle) other;
      
      boolean result = (this.handle_id_ == rhs.handle_id_);
      return result;
   }

   
   /**
    * @see il.ac.technion.micropatterns.jane.model.IHandle#produce(il.ac.technion.micropatterns.jane.model.XModel)
    */
   public IHandle produce(TypedModel m, String name)
   {      
      IElement e = target().new_one();
      return produce_impl(m, name, e);
   }
   
   /**
    * @see il.ac.technion.micropatterns.jane.model.IHandle#produce_impl(il.ac.technion.micropatterns.jane.typedmodel.TypedModel, java.lang.String, il.ac.technion.micropatterns.jane.model.IElement)
    */
   public IHandle produce_impl(TypedModel m, String name, IElement e)
   {
      ITable t = table(m);
      IHandle result = t.add(m, name, e);
      
      return result;
   }
   
   


   /**
    * @see il.ac.technion.micropatterns.jane.model.IHandle#to_integer()
    */
   public int to_integer()
   {
      return handle_id_;
   }
   
   public String toString()
   {
      return "Handle(" + handle_id_ + ")";
   }
   /**
    * @see il.ac.technion.micropatterns.jane.model.IHandle#new_one(il.ac.technion.micropatterns.jane.elements.UniHandle)
    */
   public IHandle new_one(UniHandle u) 
   {
      try
      {
         IHandle result = (IHandle) this.getClass().newInstance();
         result.set_unihandle(u);
         return result;
      }
      catch(Throwable e)
      {
         JimaMisc.stop(e);
         return null; // Faked
      }
   }
   
   public IElement value(TypedModel m)
   {
      return table(m).get(this);
   }

   public String get_name()
   {
      return name_;
   }


}
