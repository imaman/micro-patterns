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
import il.ac.technion.micropatterns.jane.model.AbstractElement;
import il.ac.technion.micropatterns.jane.model.IHandle;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;


public class VectorElement extends AbstractElement 
{
   private String name_;
   private Vector items_ = new Vector();
   
   public VectorElement() 
   { 
      super(null);
   }
   
   public VectorElement(IHandle h, String name)
   {
      super(h);
      name_ = name;
   }

   public void clear()
   {
      items_.clear();
   }
   
   public void put_item(int index, Serializable item)
   {
      items_.setElementAt(item, index);
   }
   
   
   public void add_item(Serializable item)
   {
      items_.add(item);
   }
      
   public Iterator items()
   {
      return items_.iterator();
   }
   
   public int size()
   {
      return items_.size();
   }
   
   public Serializable at(int index)
   {
      Serializable result = (Serializable) items_.elementAt(index);
      return result;
   }
      
   public void store(TypedModel m) throws IOException
   {
      JimaMisc.log().println("Loading " + name_);
      m.store(name_, this);
      JimaMisc.log().println("finished succesfully (" + name_ + ")");
   }
}
