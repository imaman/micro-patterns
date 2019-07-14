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











package il.ac.technion.micropatterns.jane.app.gui.rows;

import java.awt.Color;
import java.awt.Font;

public class Options
{   
   public static final Font JAVA_CODE_FONT;
   public static final Font DEFAULT_CELL_FONT; 

//   public static final Font DEFAULT_CELL_FRAME_COLOR = Color.BLACK;

   public static final Color CHECKABLE_METHOD_ROW_BG = Color.WHITE;
   
   public static final Color TOUCHED_FG_COLOR = new Color(80, 80, 80);
   
   public static final Color CLASS_ROW_BG = new Color(194, 130, 240).brighter();
   public static final Color LEFT_TYPE_ROW_BG = new Color(7, 180, 188); // Green
   public static final Color RIGHT_TYPE_ROW_BG = new Color(7, 169, 19); // Green
   public static final Color RIGHT_TYPE_HEADER_BG = new Color(73, 235, 85); // Green

   public static final Color TWO_SETS_ROW_BG = new Color(253, 190, 43); // Orange 
   public static final Color TWO_SETS_HEADER_BG = new Color(255, 255, 89); // Yellow

//   public static final Color SUBSET_ROW_BG = new Color(180, 180, 180); // Gray
//   public static final Color SUBSET_HEADER_BG = new Color(157, 157, 157); // Gray

   public static final Color SUBSET_ROW_BG = new Color(235,235,235);
   public static final Color SUBSET_HEADER_BG = new Color(150,150,190); // Gray
   
   public static final Color METHOD_ROW_BG = new Color(125, 160, 190).brighter();
   public static final Color FIELD_ROW_BG = new Color(160, 190, 125).brighter();
   public static final Color SUPER_CLASS_ROW_BG = Color.PINK;
   
   public static final Color PACKAGE_ROW_BG = new Color(200, 100, 0);
 
   static
   {
      DEFAULT_CELL_FONT = new Font("Arial", Font.PLAIN, 14);      
      JAVA_CODE_FONT = DEFAULT_CELL_FONT ; 
   }
   
}
