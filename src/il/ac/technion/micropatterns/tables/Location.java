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








package il.ac.technion.micropatterns.tables;

import il.ac.technion.jima.JimaMisc;

public class Location
{
   public static final int ROW = 1;
   public static final int COLUMN = 2;
   public static final int BOTH = 3;
   public static final int LEFT = 4;
   public static final int RIGHT = 5;
   public static final int UP = 6;
   public static final int DOWN = 7;
   
   public int row_ = -1;
   public int col_ = -1;
   public int flags_ = 0;

   public Location(String s, int flags) throws BadReference
   {
      flags_ = flags;
      
      String t = setColumnAndTrim(s);
      
      try
      {
         row_ = Integer.parseInt(t) - 1;
      }
      catch (NumberFormatException e)
      {
         throw new BadReference("Expected a row number in " + s);
      }      
   }
   
   public Location(int row)
   {
      flags_ = ROW;
      row_ = row - 1;
   }
   
   public Location(String s)
   {
      s = s.toLowerCase();
      JimaMisc.ensure(s.length() != 0);
      if(s.equals("right"))
      {
         flags_ = RIGHT;
         return;
      }
      if(s.equals("left"))
      {
         flags_ = LEFT;
         return;
      }
      if(s.equals("up"))
      {
         flags_ = UP;
         return;
      }
      if(s.equals("down"))
      {
         flags_ = DOWN;
         return;
      }
      
      flags_ = COLUMN;
      String t = setColumnAndTrim(s);
      
      JimaMisc.ensure(t.length() == 0);
   }
   
   private String setColumnAndTrim(String s)
   {
      s = s.toLowerCase();
      col_ = 0;
      for(int i = 0; i < s.length(); ++i)
      {
         char c = s.charAt(i);
         if(Character.isDigit(c))
            return s.substring(i);
         
         int n = c - 'a';
         col_ = col_ * 26 + n;
      }
      
      col_ -= 1;
      
      return "";      
   }
   
   
}
