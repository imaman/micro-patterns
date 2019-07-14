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











package il.ac.technion.micropatterns.checking;

import il.ac.technion.micropatterns.jane.lib.InstructionParser;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;

import java.util.Iterator;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.PUTFIELD;

public class Mini1
{
   public static void g2(JavaClass jc)
   {
      ConstantPoolGen cpg = new ConstantPoolGen(jc.getConstantPool());
      
      
      Method[] methods = jc.getMethods();
      String spr = jc.getSuperclassName();
      
      int refinements = 0;
      int valid_methods = 0;

      for(int i = 0; i < methods.length; ++i)
      {
         if(methods[i].isStatic())
            continue;
            
         if(methods[i].isPrivate())
            continue;            

         if(methods[i].isProtected())
            continue;            

         String sig = methods[i].getSignature();

         if(JavaSpec.is_constructor(methods[i]))
            continue; // Ignore constructors

         valid_methods += 1;
                     
         InstructionParser ip = new InstructionParser(methods[i]);         
         for(Iterator j = ip.iterator(); j.hasNext(); )
         {
            Instruction curr = (Instruction) j.next();

            if(!(curr instanceof INVOKESPECIAL))
               continue;

            INVOKESPECIAL is = (INVOKESPECIAL) curr;
            String receiver_type = is.getClassName(cpg);
            if(!receiver_type.equals(spr))
               continue;

            String invoked_sig = is.getSignature(cpg);
            if(!sig.equals(invoked_sig))
               continue;
            
            refinements += 1;
            break;
         }            
      }      
      
      int threshold = Math.max(valid_methods / 4, 1);
      boolean b = refinements >= threshold;
      System.out.println("threshold=" + threshold 
         + ", refinements=" + refinements + ", Decision=" + b);
   }

   public static void g1(JavaClass jc)
   {
      ConstantPoolGen cpg = new ConstantPoolGen(jc.getConstantPool());
      
      Method[] methods = jc.getMethods();
      
      for(int i = 0; i < methods.length; ++i)
      {
         InstructionParser ip = new InstructionParser(methods[i]);
         
         System.out.println("Method=" + methods[i]);
         for(Iterator j = ip.iterator(); j.hasNext(); )
         {
            Instruction curr = (Instruction) j.next();
            System.out.println(curr);
            
            if(curr instanceof PUTFIELD)
            {
               PUTFIELD pf = (PUTFIELD) curr;
               System.out.println("      <<<<<<<< " + pf.getClassName(cpg));
               
            }

            if(curr instanceof INVOKEVIRTUAL)
            {
               INVOKEVIRTUAL iv = (INVOKEVIRTUAL) curr;
               System.out.println("      <<<<<<<< " + iv.getClassName(cpg) 
                  + "." + iv.getMethodName(cpg));
            }
            
            if(curr instanceof INVOKESPECIAL)
            {
               INVOKESPECIAL is = (INVOKESPECIAL) curr;
               System.out.println("      <<<<<<<< " + is.getClassName(cpg)
                  + "." + is.getMethodName(cpg) 
                  + "[" + is.getSignature(cpg) + "]");
               
            }
            
         }            
         System.out.println("========================");            
      }         
   }

   public static void main(String[] args) throws ClassNotFoundException
   {
      String s = "java.lang.Object";

      s = "com.sun.java.swing.plaf.motif.MotifMenuItemUI";
      s = "il.ac.technion.micropatterns.checking.Base";
      s = "il.ac.technion.micropatterns.checking.Jas";
      s = "il.ac.technion.micropatterns.checking.Factory";
      s = "org.apache.crimson.tree.ParentNode";
      s = "il.ac.technion.micropatterns.checking.visitor.Node1";
      s = "de.fub.bytecode.classfile.Method";
      s = "com.sun.corba.se.internal.iiop.MessageMediator";
      
      JavaClass jc = Repository.lookupClass(s);   
      g1(jc);
      g2(jc);
   }
      

}
