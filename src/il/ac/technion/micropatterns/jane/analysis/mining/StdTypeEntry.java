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











package il.ac.technion.micropatterns.jane.analysis.mining;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.lib.InstructionParser;
import il.ac.technion.micropatterns.jane.lib.JaneMisc;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.lib.SetOfTypes;
import il.ac.technion.micropatterns.jane.typedmodel.IClassSpecProvider;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldOrMethod;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.PUTFIELD;



public class StdTypeEntry implements Serializable
{
   private static final int LIMIT = 50;
   private int[] data_= new int[LIMIT];
   private SetOfTypes[] buckets_ = new SetOfTypes[10];

   public static StdTypeEntry plus(StdTypeEntry lhs, StdTypeEntry rhs)
   {
      StdTypeEntry result = new StdTypeEntry();

      for(int i = 0; i < result.data_.length; ++i)
         result.data_[i] = lhs.data_[i] + rhs.data_[i];

      for(int i = 0; i < result.buckets_.length; ++i)
      {
         result.buckets_[i] = new SetOfTypes(4);
         result.buckets_[i].add(lhs.buckets_[i]);
         result.buckets_[i].add(rhs.buckets_[i]);
      }         

      return result;
   }

   
   public StdTypeEntry() 
   { 
      for(int i = 0; i < buckets_.length; ++i)
         buckets_[i] = new SetOfTypes(4);
   }
   
   public StdTypeEntry(IClassSpecProvider model, JavaClass jc)
   {
      this();
      ConstantPoolGen cpg = new ConstantPoolGen(jc.getConstantPool());
      
      Field[] fields = jc.getFields();
      for(int i = 0; i < fields.length; ++i)
      {
         Field curr = fields[i];
         if(curr.isStatic())
         {
            if(curr.isFinal())
               inc(STATIC_FINAL_FIELDS);
            else
               inc(STATIC_FIELDS);
         }
         else
         {
            String sig = curr.getSignature();
            ClassHandle ch 
               = model.class_for_name(JaneMisc.signature_to_text(sig, false));
               
            if(curr.isFinal())
            {
               place(TC_INST_F_F, ch);
               inc(INST_FINAL_FIELDS);
            }               
            else
            {
               place(TC_INST_F, ch);
               inc(INST_FIELDS);
            }               
         }
      }            

      Method[] methods = jc.getMethods();
      for(int i = 0; i < methods.length; ++i)
      {
         Method curr = methods[i];
         
         if(JavaSpec.is_constructor(curr))
         {
            if(curr.isPrivate())
               inc(PRIVATE_CTORS);
            else
               inc(NON_PRIVATE_CTORS);
         }                           
         else if(curr.isStatic())
         {               
            inc(STATIC_METHODS);
         }               
         else
         {               
            inc(METHODS);
            inspect_method_code(model, curr, cpg, jc); 
         }
         
         if(curr.isAbstract())
         {
            if(curr.isProtected())
               inc(PROT_ABS_METHODS);
            else if(curr.isPublic())
               inc(PUB_ABS_METHODS);               
         }            
      }            
   }
   
   private void inspect_method_code(IClassSpecProvider model, 
      Method m, ConstantPoolGen cpg, JavaClass jc)
   {
      String name = jc.getClassName();
      
      InstructionParser ip = new InstructionParser(m);
      for(Iterator i = ip.iterator(); i.hasNext(); )
      {
         Instruction ins = (Instruction) i.next();
         
         ClassHandle another_class = null;
         String xcn = null;
         
         if(ins instanceof FieldOrMethod)
         {
            FieldOrMethod fom = (FieldOrMethod) ins;
            xcn = fom.getClassName(cpg);
            
            another_class = model.class_for_name(xcn);
         }         
         
         if(ins instanceof INVOKEINTERFACE)
         {
            place(TC_INVOKE_INTRF, another_class);
         }   
            
         if(ins instanceof INVOKEVIRTUAL)
         {
            if(xcn.equals(name))
               inc(SELF_INVOKEVIRTUAL_INSTRUCTIONS);
            else
               inc(INVOKE_VIRTUAL_INSTRUCTIONS);
               
            place(TC_INVOKE_VIRTUAL, another_class);
         }        
         
         if(ins instanceof INVOKESPECIAL)
            place(TC_INVOKE_SPECIAL, another_class);
            
         if(ins instanceof INVOKESTATIC)
            place(TC_INVOKE_STATIC, another_class);
                
         
         if(ins instanceof GETFIELD)
         {            
            inc(GETFIELD_INSTRUCTIONS);
            place(TC_GETFIELD, another_class);
         }            
         
         if(ins instanceof PUTFIELD)
         {            
            inc(PUTFIELD_INSTRUCTIONS);
            place(TC_PUTFIELD, another_class);
         }                     
      }         
   }     

   private static final class DataDesc
   {
      private boolean is_bucket_;
      private int id_;
      private String title_;

      private static int count_ = 0;
      private static Vector all_ = new Vector();

      public DataDesc(String title)
      {
         this(title, false, false);
      }

      public DataDesc(String title, boolean is_bucket, boolean ignore)
      {
         is_bucket_ = is_bucket;
         title_ = title;
         id_ = count_;
         
         count_ += 1;

         if(!ignore)
            all_.add(this);
      }

      public static Iterator all()
      {
         return all_.iterator();
      }
   }

   private static DataDesc ig(String title)
   {
      return new DataDesc(title, false, true);
   }

   private static DataDesc ig(String title, boolean is_bucket)
   {
      return new DataDesc(title, is_bucket, true);
   }

   private static DataDesc dd(String title, boolean is_bucket)
   {
      return new DataDesc(title, is_bucket, false);
   }

   private static DataDesc dd(String title)
   {
      return dd(title, false);
   }

   private static DataDesc bucket(String title)
   {
      return dd(title, true);
   }

   public static void write_header(PrintWriter pw)
   {      
      pw.println("@relation jane");
      for(Iterator i = DataDesc.all(); i.hasNext(); )
         write_att(pw, (DataDesc) i.next());

      pw.println("@data");
   }

   public static final DataDesc TC_INVOKE_VIRTUAL
      = dd("tc-invoke-virtual", true);

   public static final DataDesc TC_INVOKE_SPECIAL
      = dd("tc-invoke-special", true);

   public static final DataDesc TC_INVOKE_STATIC
      = dd("tc-invoke-static", true);

   public static final DataDesc TC_INVOKE_INTRF
      = bucket("tc-invoke-intrf");

   public static final DataDesc TC_GETFIELD
      = dd("tc-getfield", true);

   public static final DataDesc TC_PUTFIELD
      = dd("tc-putfield", true);


   public static final DataDesc TC_INST_F = bucket("tc-inst-f");
   public static final DataDesc TC_INST_F_F = bucket("tc-inst-f-f");
   
   public static final DataDesc STATIC_FINAL_FIELDS = dd("stat-f-f");
   public static final DataDesc STATIC_FIELDS = dd("stat-f");

   
   public static final DataDesc INST_FINAL_FIELDS = dd("inst-f-f");
   public static final DataDesc INST_FIELDS = dd("inst-f");

   public static final DataDesc PRIVATE_CTORS = dd("priv-ctor");
   public static final DataDesc NON_PRIVATE_CTORS = dd("non-priv-ctor");

   public static final DataDesc STATIC_METHODS = dd("stat-m");
   public static final DataDesc METHODS = dd("m");
   
   public static final DataDesc GETFIELD_INSTRUCTIONS = dd("getfield");
   public static final DataDesc PUTFIELD_INSTRUCTIONS = dd("putfield");
   
   public static final DataDesc PROT_ABS_METHODS = dd("prot-abs-methods");
   public static final DataDesc PUB_ABS_METHODS  = dd("pub-abs-methods");
   
   public static final DataDesc INVOKE_VIRTUAL_INSTRUCTIONS 
      = dd("invoke-virtual");

   public static final DataDesc SELF_INVOKEVIRTUAL_INSTRUCTIONS 
      = dd("self-invoke-virtual");

   
   public int get(DataDesc d)
   {
      if(d.is_bucket_)
         return get_bucket_size(d);
         
      JimaMisc.ensure(d.id_ < data_.length);
      return data_[d.id_];
   }

   public int get_bucket_size(DataDesc d)
   {
      JimaMisc.ensure(d.id_ < buckets_.length);
      return buckets_[d.id_].size();
   }
   
   private void set(DataDesc d, int value)
   {
      JimaMisc.ensure(d.id_ < data_.length);
      data_[d.id_] = value;
   }
   
   private void inc(DataDesc d, int n)
   {
      int temp = get(d);
      set(d, temp + n);
   }
   
   private void inc(DataDesc d)
   {
      inc(d, 1);
   }
   
   private void place(DataDesc d, ClassHandle o)
   {
      JimaMisc.ensure(d.is_bucket_);
      
      buckets_[d.id_].add(o);
   }


   
   private static void write_att(PrintWriter pw, DataDesc d)
   {
      pw.println("@attribute " + d.title_ + " {" + ZERO + ", " 
         + ONE + ", " + TWO + ", " + THREE + ", " + MANY + "}");
   }
   
   public void write(PrintWriter pw)
   {
      StringBuffer sb = new StringBuffer();
      
      for(Iterator i = DataDesc.all(); i.hasNext(); )
         make(sb, (DataDesc) i.next());
      
      pw.println(sb); 
   }
   
   private void make(StringBuffer dst, DataDesc d)
   {      
      int n = get(d);

      if(d.is_bucket_)
         n = get_bucket_size(d);
         
      String nom = to_nom(n);
      
      int len = dst.length();
      if(len == 0)
         dst.append(nom);
      else
         dst.append(", " + nom);
   }
   
   private static final String ZERO = "   0";
   private static final String ONE = "   1";
   private static final String TWO = "   2";
   private static final String THREE = "   3";
   private static final String MANY = "many";
   
   public static String to_nom(int n)
   {
      if(n == 0)
         return ZERO;
         
      if(n == 1)
         return ONE;
         
      if(n == 2)
         return TWO;         
 
      if(n == 3)
         return THREE;  
         
      // ...Else:
      return MANY;                  
   }
   
}
