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











package il.ac.technion.micropatterns.jane.app.gui;

import il.ac.technion.jima.IWidthListener;
import il.ac.technion.jima.JimaMisc;
import il.ac.technion.jima.util.VectorOfInts;

public class WidthManager
{
   private VectorOfInts widths_ = new VectorOfInts();
      
   public WidthManager() { }
   
   public WidthManager(int[] widths) 
   { 
      widths_ = new VectorOfInts(widths);
   }
   
   public static IWidthHandle new_fixed_width(int w)
   {
      return new FixedWidthHandle(w);
   }
   
   
   public class Handle implements IWidthHandle
   {
      protected int index_;
      private IWidthListener wl_;
      
      public Handle(int index, IWidthListener wl)
      {
         index_ = index;
         wl_ = wl;
      }
      
      public int get()
      {
         return widths_.intAt(index_);
      }
      
      public void set(int new_width)
      {
         assign_width(index_, new_width, wl_);
      }
   }

   public static class FixedWidthHandle implements IWidthHandle
   {
      private int w_;
      
      public FixedWidthHandle(int w)
      {
         w_ = w;
      }
      
      public int get()
      {
         return w_;
      }
      
      public void set(int new_width)
      {
         // Do nothing
      }
   }
   
   public HandleProvider new_provider(IWidthListener wl)
   {
      return new HandleProvider(wl);
   }
   
   
   public class HandleProvider
   {
      private int index_ = -1;
      private IWidthListener wl_;
      
      private HandleProvider(IWidthListener wl) 
      { 
         wl_ = wl;
      }
      
      public IWidthHandle next_handle(int default_width)
      {
         index_ += 1;
         Handle result = make_handle(index_, default_width, wl_);
         
         return result;
      }

      public IWidthHandle next_handle()
      {
         index_ += 1;
         Handle result = make_handle(index_, wl_);
         
         return result;
      }
   }
   
   private synchronized void assign_width(int index, int new_width, 
      IWidthListener wl)
   {
      widths_.putIntAt(new_width, index);
      
      int tw = total_width();         
      wl.width_changed(tw);
   }

   private synchronized int total_width()
   {
      int result = 0;
      for(int i = 0; i < widths_.size(); ++i)
         result += widths_.intAt(i);
         
      return result;
   }
   
   private synchronized Handle make_handle(int index, int default_width, 
      IWidthListener wl)
   {
      JimaMisc.ensure(index <= widths_.size());
      if(index == widths_.size())         
         widths_.addInt(default_width);
      
      Handle result = new Handle(index, wl);
      return result;
   }
   
   private synchronized Handle make_handle(int index, IWidthListener wl)
   {
      JimaMisc.ensure(index < widths_.size(), "Width index (" + index 
         + " out of range");
      
      Handle result = new Handle(index, wl);
      return result;
   }
}
