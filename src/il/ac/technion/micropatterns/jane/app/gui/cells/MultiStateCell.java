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











package il.ac.technion.micropatterns.jane.app.gui.cells;

import il.ac.technion.micropatterns.jane.app.gui.ICommand;


public class MultiStateCell extends AbstractCell
{
   private IPaintable[] images_;
   private int current_state_;
   private ICommand next_cmd_ = ICommand.NOP_COMMAND;

   public MultiStateCell() 
   { 
      this(new IPaintable[0]);
   }

   /**
    * 
    */
   public MultiStateCell(IPaintable[] images) 
   { 
      images_ = images;
      current_state_ = 0;
      
      super.set_click_cmd(new ICommand()
         {
            public void execute()
            {
               switch_state();               
               next_cmd_.execute();
            }
         });
   }
   
   public AbstractCell set_click_cmd(ICommand c)
   {
      next_cmd_ = c;
      return this;
   }
   
   private void switch_state()
   {
      if(images_.length == 0)
         return; // Do nothing if no images are set
         
      int new_state = (current_state_ + 1) % images_.length;
      set_current_state(new_state);
   }
   
   public int get_current_state()
   {
      return current_state_;
   }
   
   public MultiStateCell set_current_state(int new_state)
   {
      set_image(images_[new_state]);
      
      current_state_ = new_state;
      return this;
   }   
}
