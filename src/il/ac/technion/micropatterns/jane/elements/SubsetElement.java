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

import il.ac.technion.micropatterns.jane.model.AbstractElement;
import il.ac.technion.micropatterns.jane.model.IElement;
import il.ac.technion.micropatterns.jane.model.IHandle;
import il.ac.technion.micropatterns.jane.model.XModel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class SubsetElement extends AbstractElement 
{
   private Vector handles_ = new Vector();
   private String name_;
   private String initials_;
   private boolean is_library_ = false;

   public SubsetElement() 
   { 
      super(null);
   }
   
   public SubsetElement(IHandle h, String name)
   {
      super(h);
      name_ = name;
      initials_ = name;
   }
   
   public void set_initials(String new_initials)
   {
      initials_ = new_initials;
   }

   public String get_initials()
   {
      return initials_;
   }
   
   public boolean is_library()
   {
      return is_library_;
   }
   
   public void set_is_library(boolean b)
   {
      is_library_ = b;
   }
   
   
   public void add_all(Iterator handles)
   {
      HashSet temp = new HashSet(handles_);
      while(handles.hasNext())
         temp.add(handles.next());
         
      handles_ = new Vector(temp);         
   }
   
   public void reorder(Iterator ordered_handles)
   {
      HashSet temp = new HashSet();         
      add_to(this, temp);


      Vector new_order = new Vector();
      
      while(ordered_handles.hasNext())
      {
         IHandle curr = (IHandle) ordered_handles.next();
         if(temp.contains(curr))
            new_order.add(curr);
      }
      
      handles_ = new_order;      
   }
   
   public void clear()
   {
      handles_.clear();
   }
      
   public void add(IHandle h)
   {
      handles_.add(h);
   }
   
   public int size()
   {
      return handles_.size();
   }
   
   public IHandle handle_at(int index)
   {
      IHandle result = (IHandle) handles_.elementAt(index);
      return result;
   }
   
//   public ClassProxy proxy_at(int index, TypedModel m)
//   {
//      IHandle h = handle_at(index);
//      ClassProxy result = m.get_class_proxy(h);
//      
//      return result;
//   }
   
   public IElement element_at(int index, XModel m)
   {
      IHandle h = handle_at(index);
      IElement result = m.get_element(h);
      
      return result;
   }   
   
//   public void store(TypedModel m) throws IOException
//   {
//      m.store(name_, this);
//   }
   
   public static void add_to(SubsetElement src, HashSet trg)
   {
      for(Iterator i = src.handles_.iterator(); i.hasNext(); )
         trg.add(i.next());         
   }
   
   public String toString()
   {
      return '[' + this.getClass().getName() + "] size=" + size();
   }
   
   public static void join(HashSet lhs, SubsetElement s2)
   {
      HashSet rhs = new HashSet();         
      add_to(s2, rhs);

      for(Iterator i = rhs.iterator(); i.hasNext(); )
         lhs.add(i.next());
   }

   public static IHandle[] intersection(SubsetElement s1, SubsetElement s2)
   {
      HashSet lhs = new HashSet();
      add_to(s1, lhs);

      HashSet rhs = new HashSet();
      add_to(s2, rhs);
         
      // Make sure lhs is the smaller of the two            
      if(lhs.size() > rhs.size())
      {
         HashSet temp = lhs;
         
         lhs = rhs;
         rhs = temp;
      }
                     
                        
      for(Iterator i = lhs.iterator(); i.hasNext(); )
      {
         if(!rhs.contains(i.next()))
            i.remove();
      }

      IHandle[] result = new IHandle[lhs.size()];
      
      int index = 0;
      for(Iterator i = lhs.iterator(); i.hasNext(); ++index)
         result[index] = (IHandle) i.next();
         
      return result;         
   }


   public static HashSet diff(SubsetElement s1, HashSet rhs)
   {
      HashSet result = new HashSet();         
      add_to(s1, result);
      
      for(Iterator i = result.iterator(); i.hasNext(); )
      {
         if(rhs.contains(i.next()))
            i.remove();            
      }        
      
      return result;             
   }

   public static IHandle[] diff(SubsetElement s1, SubsetElement s2)
   {
      HashSet rhs = new HashSet();         
      add_to(s2, rhs);
      
      HashSet temp = diff(s1, rhs);    
      IHandle[] result = new IHandle[temp.size()];
      
      int index = 0;
      for(Iterator i = temp.iterator(); i.hasNext(); ++index)
         result[index] = (IHandle) i.next();
         
      return result;         
   }
   
   public Iterator handles()
   {
      return handles_.iterator();
   }
   
}
