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



public abstract class AbstractElement implements IElement
{
   private IHandle handle_;
   private TypedModel model_;

   protected AbstractElement()
   {
      this(null);
   }

   public AbstractElement(IHandle h, TypedModel m)
   {
      this(h);
      set_model(m);
   }
   
   public AbstractElement(IHandle h)
   {
      handle_ = h;
   }
   
   private void writeObject(ObjectOutputStream out)
      throws IOException
   {
      out.writeObject(handle_);
   }
        
   private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException
   {
      handle_ = (IHandle) in.readObject();
   }
         
   
   public void set_model(TypedModel m)
   {
      model_ = m;
   }
   
   protected TypedModel get_model()
   {
      return model_;
   }
   
   public void set_handle(IHandle h)
   {
      handle_ = h;
   }
   
   public IHandle get_handle()
   {
      return handle_;
   }  
   
    
   /**
    * @see il.ac.technion.micropatterns.jane.model.IElement#new_one(il.ac.technion.micropatterns.jane.model.IHandle)
    */
   public IElement new_one()
   {
      try
      {
         IElement result = (IElement) this.getClass().newInstance();         
         return result;
      }
      catch(Throwable t)
      {
         JimaMisc.stop(t);
         return null; // Faked
      }
   }

}
