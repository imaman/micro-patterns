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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import il.ac.technion.micropatterns.tables.Builder;
import il.ac.technion.micropatterns.tables.Table;

public class ClassCut
{
   public static void go(String csv_fn) throws Throwable
   {
      Core core = new Core(csv_fn, Env.fileName("jars"));
      Table jres = core.jres;
      
      HashSet libs = jres.uniqueValuesAtCol(core.col_lib);
      HashSet classes = jres.uniqueValuesAtCol(core.col_fqn);
      int[] patts = core.patts;
      
      Builder b = new Builder();
      b.add("class");

      for(int p = 0; p < patts.length; ++p)
      {
         int currPatt = patts[p];
         String pattName = core.head.at(currPatt, 0);

         for(Iterator i = libs.iterator(); i.hasNext(); )
         {  
            String lib = (String) i.next();            
            b.add(pattName + '@' + lib);
         }
      }
      
      b.newLine();
      
                  
      for(Iterator i = classes.iterator(); i.hasNext(); )
      {
         String fqn = (String) i.next();
         b.add(fqn);
         
         Table t = jres.rowsWhereEqStr(core.col_fqn, fqn); 

         Vector subtables = new Vector();
         for(Iterator j = libs.iterator(); j.hasNext(); )
         {
            String lib = (String) j.next();
            Table t2 = t.rowsWhereEqStr(core.col_lib, lib);
            if(t2.numRows() < 0 || t2.numRows() > 1)
               throw new Exception("rows in t2 == " + t2.numRows() + ", lib=" + lib);
               
            subtables.add(t2);
         }

            //System.out.println("fqn=" + fqn + ", lib=" + lib + ", rows=" + t2.numRows());

            
         for(int p = 0; p < patts.length; ++p)
         {
            int currPatt = patts[p];
            for(Iterator j = subtables.iterator(); j.hasNext(); )
            {
               Table t2 = (Table) j.next();

               if(t2.numRows() == 0)
                  b.add("-");
               else
               {
                  String s = t2.at(currPatt, 0);
                  b.add(s);                  
               }
            }            
         }      
         
         b.newLine();
      }
      
      b.result().print(System.out);
   }
   
   
   public static void main(String[] args) throws Throwable
   {
      String s = Env.fileName("jars/vectors.csv");
      if(args.length >= 1)
         s = args[0];
         
      go(s);
   }
}
