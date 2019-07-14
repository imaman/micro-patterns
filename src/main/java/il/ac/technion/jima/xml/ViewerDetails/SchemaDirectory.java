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


package il.ac.technion.jima.xml.ViewerDetails;

import il.ac.technion.jima.JimaMisc;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


public class SchemaDirectory
{
   public static class CellElement
   {
      public CellElement(int id, String type_name, int parent_tid)
      {
         id_ = id;
         type_name_ = type_name;

         Color col = new Color(0, (parent_tid % 10) * 15 + 100, 
            240 - (Math.abs(parent_tid) % 4) * 60);            
            
//         cell_prototype_ = new CellPrototype(80, col, Color.BLACK);
      }
            
      public Iterator sub_elements()
      {
         return new Iterator()
         {
            private int next_ = 0;
            
            public boolean hasNext()
            {
               return next_ != SubElements_.size();
            }
            
            public Object next()
            {
               Object result = SubElements_.elementAt(next_);
               next_ += 1;
               
               return result;
            }
            
            public void remove()
            {
               JimaMisc.ensure(false, "Illegal operation");
            }         
         };
      }
      
      public int getWidth(SchemaDirectory sd)
      {
         int result = 0;
         for(Iterator i = SubElements_.iterator(); i.hasNext(); )
         {
            int tid = ((Integer) i.next()).intValue();
            CellElement e = (CellElement) sd.elements_.elementAt(tid);
            result += e.width_;
         }            
         
         return result;
      }
            
  //    public CellPrototype cell_prototype_;            
      public int id_;
      public String type_name_;
      public int width_;
      public Vector SubElements_ = new Vector();
   }
   
   private HashMap map_ = new HashMap();
   private Vector elements_ = new Vector();
   
   
   public SchemaDirectory()
   {
      CellElement temp = new CellElement(0, "", -1);      
      elements_.add(temp);
   }
   
   int count()
   {
      return elements_.size();
   }   
      
   public int add_root_type(String type_name)
   {
      return add_type(0, type_name);
   }

   synchronized public int add_type(int parent_tid, String type_name)
   {
      CellElement p = (CellElement) elements_.elementAt(parent_tid);
      
      int top = p.SubElements_.size();
      for(int i = 0; i < top; ++i)
      {
         int tid = ((Integer) p.SubElements_.elementAt(i)).intValue();
         CellElement curr = (CellElement) elements_.elementAt(tid);
         if(!curr.type_name_.equals(type_name))
            continue;
            
         // ...Else: Match found
         return tid;
      }     
      
      // Add a new subelement 
      
      int tid = elements_.size();
      CellElement new_one = new CellElement(tid, type_name, parent_tid);
      
      elements_.add(new_one);
      p.SubElements_.add(new Integer(tid));

      return tid;      
   }
   
   public CellElement prototype_of(int tid)
   {
      CellElement e = (CellElement) elements_.elementAt(tid);
      return e;
   }
   
   public String type_name_of(int tid)
   {
      CellElement e = (CellElement) elements_.elementAt(tid);
      return e.type_name_;
   }
   
   public int num_of_sub_types(int tid)
   {
      CellElement e = (CellElement) elements_.elementAt(tid);
      return e.SubElements_.size();
   }
   
   public Iterator names_of_sub_types(int tid)
   {
      CellElement e = (CellElement) elements_.elementAt(tid);
      return e.sub_elements();
   }
   
   public int getMaxWidth()
   {
      int result = 0;
      int top = elements_.size();
      for(int i = 0; i < top; ++i)
      {
         CellElement e = (CellElement) elements_.elementAt(i);
         result = Math.max(result, e.getWidth(this));
      }
      
      return result;
   }
}
