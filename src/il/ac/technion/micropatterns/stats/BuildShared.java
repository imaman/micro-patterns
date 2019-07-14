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








package il.ac.technion.micropatterns.stats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;

public class BuildShared
{

 
   private static Row header;
   private static Vector rows = new Vector();
   
   private static class Row
   {
      public String[] items;
      
      public Row(String s)
      {
         Vector vec = new Vector();
         
         StringTokenizer st = new StringTokenizer(s, ",");
         while(st.hasMoreTokens())
            vec.add(st.nextToken());
         
         items = new String[vec.size()];
         for(int i = 0; i < items.length; ++i)
            items[i] = (String) vec.get(i);
      }
      
      public String type()
      {
         return items[0];
      }
      
      public String lib()
      {
         return items[1];
      }
      
      public void lib(String l)
      {
         items[1] = l;
      }
      
      public String toString()
      {
         StringBuffer sb = new StringBuffer(items[0]);
         for(int i = 1; i < items.length; ++i)
            sb.append("," + items[i]);
         
         return sb.toString();
      }
   }
   
   public static void read(InputStream is) throws IOException
   {
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      while(true)
      {
         String line = br.readLine();
         if(line == null)
            break;
         
         line = line.trim();
         Row r = new Row(line);
         if(header == null)
            header = r;
         else
            rows.add(r);
      }      
   }
   
   private static void sort()
   {
      Collections.sort(rows, COMP); 
   }
   
   private static final Comparator COMP = new Comparator()
      {
         public int compare(Object o1, Object o2)
         {
            Row lhs = (Row) o1;
            Row rhs = (Row) o2;
            
            return lhs.type().compareTo(rhs.type());
         }         
      };
      
   private static void assignToShared(PrintStream ps)
   {
      ps.println(header);
      for(int i = 0; i < rows.size() - 1; ++i)
      {
         Row curr = (Row) rows.get(i);
         Row next = (Row) rows.get(i+1);
         
         if(COMP.compare(curr,next) != 0)
         {
            ps.println(curr);
            continue;
         }
         
         // ...Else:
         next.lib("Shared");
      }
      
      ps.println(rows.get(rows.size()-1));
   }
   
   
   /**
    * @param args
    * @throws IOException 
    */
   public static void main(String[] args) throws IOException
   {
      read(System.in);
      sort();
      assignToShared(System.out);
      
      // TODO Auto-generated method stub

   }

}
