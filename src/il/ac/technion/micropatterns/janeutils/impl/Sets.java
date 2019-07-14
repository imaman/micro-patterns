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








/*
 * Created on Nov 10, 2004
 * Written by spiderman
 * Project: JarScan
 */

package il.ac.technion.micropatterns.janeutils.impl;

import il.ac.technion.jima.JimaMisc;

import java.util.HashSet;
import java.util.Iterator;


public class Sets
{
   public static HashSet dup(HashSet s)
   {
      HashSet result = new HashSet();
      result.addAll(s);
      
      return result;
   }
   
   public static HashSet intersect(HashSet lhs, HashSet rhs)
   {
      HashSet result = dup(lhs);
      for(Iterator i = result.iterator(); i.hasNext(); )
      {
         Object curr = i.next();
         
         if(!rhs.contains(curr))
            i.remove();
      }
//      result.retainAll(rhs);
      
      return result;
   }
   
   public static HashSet union(HashSet lhs, HashSet rhs)
   {
      HashSet result = dup(lhs);
      result.addAll(rhs);
      
      return result;
   }
   
   public static HashSet difference(HashSet lhs, HashSet rhs)
   {
      HashSet result = dup(lhs);
      result.removeAll(rhs);
      
      return result;
   }
   
   public static double correl(HashSet x, HashSet y, int universe_size)
   {   
      float n = universe_size;
      
      float px = x.size() / n;
      double sdx = Math.sqrt(px-px*px);

      float py = y.size() / n;      
      double sdy = Math.sqrt(py-py*py);
      
      HashSet intersection = intersect(x, y);
      float pxy = intersection.size() / n;
      
      
//      System.out.println(x.size() + "," + y.size() + "," + intersection.size());
      
      double result = (float) (pxy - px*py) / (sdx*sdy);      
      return result;
   }   
   
   
   public static void main(String[] args)
   {
      HashSet x = new HashSet();
      x.add("1");
      x.add("2");
      x.add("3");
      x.add("6");
      x.add("7");
      x.add("8");
      x.add("9");
      
      HashSet y = new HashSet();
      y.add("1");
      y.add("2");
      y.add("6");
      y.add("7");
      y.add("8");

      // Expected result = 0.6546536837147768
      double d = correl(x, y, 10);
      System.out.println(d);
      JimaMisc.ensure(Double.compare(0.6546536837147768, d) == 0);
      
      HashSet z = Sets.union(x, y);
      System.out.println("z=" + z);
      JimaMisc.ensure(z.size() == 7);
      JimaMisc.ensure(z.contains("1"));
      JimaMisc.ensure(z.contains("2"));
      JimaMisc.ensure(z.contains("3"));
      JimaMisc.ensure(z.contains("6"));
      JimaMisc.ensure(z.contains("7"));
      JimaMisc.ensure(z.contains("8"));
      JimaMisc.ensure(z.contains("9"));
   }     
}
