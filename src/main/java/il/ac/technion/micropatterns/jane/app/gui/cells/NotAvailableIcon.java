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

import java.awt.Color;
import java.awt.Graphics;

public class NotAvailableIcon implements IPaintable
{
   public static final IPaintable INSTANCE = new NotAvailableIcon();
   /**
    * @see il.ac.technion.micropatterns.jane.app.gui.cells.IPaintable#paint(java.awt.Graphics, int, int)
    */
   public void paint(Graphics g, int x, int y)
   {
      g.setColor(Color.BLACK);

      x += 0;
      y += 6;

      g.drawLine(x, y + 1, x + 1, y);
      g.drawLine(x + 1, y, x + 3, y);
      g.drawLine(x + 4, y + 1, x + 4, y + 3);
      g.drawLine(x + 3, y + 4, x + 2, y + 5);
      g.drawLine(x + 2, y + 7, x + 1, y + 7);

      g.drawRect(x - 4, y - 3, 13, 13);
   }

   /**
    * @see il.ac.technion.micropatterns.jane.app.gui.cells.IPaintable#width()
    */
   public int width()
   {
      return 16;
   }

   /**
    * @see il.ac.technion.micropatterns.jane.app.gui.cells.IPaintable#height()
    */
   public int height()
   {
      return 15;
   }

}
