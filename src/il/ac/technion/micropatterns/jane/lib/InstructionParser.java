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









package il.ac.technion.micropatterns.jane.lib;

import il.ac.technion.jima.JimaMisc;

import java.util.Iterator;
import java.util.Vector;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.Visitor;

public class InstructionParser implements org.apache.bcel.Constants
{
   private Method m_;
   private Instruction[] instructions_ = new Instruction[0];
   
   public InstructionParser(Method m) 
   {
      m_ = m;
      
      InstructionList il;
      
      try
      {
         Code c = m_.getCode();
         if(c == null)
            il = new InstructionList();
         else         
            il = new InstructionList(c.getCode());

         instructions_ = il.getInstructions();
      }
      catch(ClassGenException e)
      {
         JimaMisc.log().println("Error while insepcting method " + m 
            + ", error= " + e);
      }
   }
   
   public void accept(Visitor v)
   {      
      for(Iterator i = iterator(); i.hasNext(); )
      {
         Instruction curr = (Instruction) i.next();
         curr.accept(v);
      }         
   }
   
   public Iterator iterator()
   {
      Iterator result = new ArrayIterator(instructions_);      
      return result;
   }


   private static class ArrayIterator implements Iterator
   {
      private Object[] a_;
      int pos_ = 0;
      
      public ArrayIterator(Object[] a)
      {
         a_ = a;
      }
      
      public boolean hasNext()
      {
         return pos_ < a_.length;
      }
      
      public Object next()
      {
         Object result = a_[pos_];
         pos_ += 1;
         
         return result;
      }
      
      public void remove()
      {
         // Dead end
         JimaMisc.ensure(false);
      }      
   }
   
   
   public static void main(String[] args) throws Exception
   {      
      String cn = "il.ac.technion.micropatterns.jane.lib.Jas";
//      cn = "com.sun.corba.se.ActivationIDL._ActivatorStub";
      
      JavaClass jc = Repository.lookupClass(cn);
      
      ConstantPoolGen cpg = new ConstantPoolGen(jc.getConstantPool());

      Vector vec = new Vector();
      Method[] methods = jc.getMethods();
      for(int i = 0; i < methods.length; ++i)
      {
         vec.clear();
         Method curr = methods[i];
            
         System.out.println("=====================================");
         System.out.println("Method=" + curr.getName());
         
         InstructionParser ip = new InstructionParser(curr);

         boolean last_is_aload0 = false;
         for(Iterator k = ip.iterator(); k.hasNext(); )
         {
            boolean after_aload0 = last_is_aload0;
            last_is_aload0 = false;
            
            Instruction ins = (Instruction) k.next();
            
            if(ins instanceof NEW)
            {
               NEW newins = (NEW) ins;
               Type type = newins.getType(cpg);
               String sig = type.getSignature();
               System.out.println("new " + sig + "(..)");
               continue;
            }
            if(ins instanceof ALOAD)
            {
               ALOAD al = (ALOAD) ins;
               if(al.getIndex() == 0)
                  last_is_aload0 = true;
                  
    //           continue;                  
            }
            
            if(ins instanceof LoadInstruction)
            {
               vec.add(ins);
            }
            
            if(after_aload0 && (ins instanceof GETFIELD))
            {   
               vec.remove(vec.size() - 1);
               GETFIELD gf = (GETFIELD) ins;
               vec.add(gf);
  //             continue;
            }               
                        
            if(ins instanceof INVOKEVIRTUAL)
            {
               INVOKEVIRTUAL iv = (INVOKEVIRTUAL) ins;
               String mn = iv.getMethodName(cpg);
               System.out.println("mn=" + mn);
               int nargs = iv.getArgumentTypes(cpg).length;
               
               int index = vec.size() - (nargs + 1);
               if(index < 0)
                  continue;
                  
               GETFIELD gf = (GETFIELD) vec.elementAt(index);
               
               System.out.println("=> Invoking " + gf.getFieldName(cpg)+ "." 
                  + mn + "()");
                  
               vec.clear();                  
//               continue;                  
            }
            System.out.println(ins.toString());
         }            
      }      
   }
}
