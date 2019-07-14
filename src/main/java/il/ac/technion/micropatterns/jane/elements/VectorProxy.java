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
import il.ac.technion.micropatterns.jane.model.AbstractProxy;
import il.ac.technion.micropatterns.jane.model.IElement;
import il.ac.technion.micropatterns.jane.model.IModelRequest;
import il.ac.technion.micropatterns.jane.model.XModel;

import java.io.InvalidClassException;


public class VectorProxy extends AbstractProxy
{
   private static final String PREFIX = "sys-generic-";

   private static String decorate(String name)
   {
      return PREFIX + name;
   }
   
   private static String undecorate(String s)
   {
      return s.substring(PREFIX.length());
   }

   public VectorProxy(VectorHandle h, String name)
   {
      super(h, decorate(name));
      
//      m.associate(name(), this);      
//      
//      JimaMisc.log().println("Generic proxy: " + name + " -> " + get_handle());
   }
//   /**
//    * @see il.ac.technion.micropatterns.jane.model.IProxy#accept(il.ac.technion.micropatterns.jane.model.IVisitor)
//    */
//   public void accept(IVisitor v)
//   {
//      v.visit(this);
//   }

   /**
    * @see il.ac.technion.micropatterns.jane.model.IProxy#create(il.ac.technion.micropatterns.jane.model.IModelRequest)
    */
   public IElement create(IModelRequest mr)
   {
      JimaMisc.ensure(mr != null);
      
      XModel m = mr.model();
      try
      {
         IElement result = (IElement) m.fetch(this.name());
         JimaMisc.log().println("Loaded: " + this);
         return result;
      }
      catch(InvalidClassException ice)
      {
         JimaMisc.stop(ice);
         return null; // Faked
      }
      catch(Throwable e)
      {
         IElement result = new VectorElement(get_handle(), this.name());
         JimaMisc.log().println("Created: " + this);
         return result;
      }
   }
   
   public String pretty_name()
   {
      return undecorate(name());
   }
   
   public String toString()
   {
      return "GenericProxy(" + pretty_name() + ", " + get_handle() + ")";
   }
   
   
}
