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



public abstract class AbstractProxy implements IProxy
{
   private IHandle handle_;
   private String name_;
      

   public AbstractProxy() { }

   public AbstractProxy(IHandle h, String name)
   { 
      JimaMisc.ensure(h != null, "handle is null, name=" + name);
      
      handle_ = h;
      name_ = name;
   }

//   public AbstractProxy(TypedModel m, String name, IHandle prototype)
//   {
//      name_ = name;
//      
//      ITable t = prototype.table(m);
//      handle_ = t.lookup_handle(name);
//      
//      if(handle_ != null)
//         return;
//                           
//      handle_ = t.produce(); 
////      m.xadd_impl(this);               
//   }
   
   /**
    * @see il.ac.technion.micropatterns.jane.model.IElement#name()
    */
   public String name()
   {
      return name_;
   }
   
   public IHandle get_handle()
   {
      return handle_;
   }
   
   public boolean equals(Object other)
   {
      if(other == null)
         return false;
         
      if(!this.getClass().equals(other.getClass()))
         return false;
         
      AbstractProxy rhs = (AbstractProxy) other;
      
      boolean result = this.get_handle().equals(rhs.get_handle());      
      return result;
   }
   
   public int hashCode()
   {
      return handle_.hashCode();
   }
}
