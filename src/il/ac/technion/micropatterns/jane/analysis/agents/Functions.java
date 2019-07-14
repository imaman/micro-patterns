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










package il.ac.technion.micropatterns.jane.analysis.agents;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.analysis.misc.Features;
import il.ac.technion.micropatterns.jane.analysis.misc.InheritanceDescendingAgent;
import il.ac.technion.micropatterns.jane.analysis.misc.Result;
import il.ac.technion.micropatterns.jane.analysis.misc.VectorOfFields;
import il.ac.technion.micropatterns.jane.analysis.misc.VectorOfMethods;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.InstructionParser;
import il.ac.technion.micropatterns.jane.lib.JaneMisc;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.lib.MethodSelector;
import il.ac.technion.micropatterns.janeutils.Foreign;
import il.ac.technion.micropatterns.janeutils.impl.DescCP;

import java.util.Iterator;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.ReturnInstruction;

public class Functions extends InheritanceDescendingAgent
{
   private Result funp_;
   private Result funo_;
   private Result joiners_;
   private Result records_;
   private Result taxonomy_;
   private Result abs_types_;
   private Result designator_;
   private Result extenders_;
   private Result moulds_;
   private Result data_managers_;   
   private Result algorithms_;
   private Result steday_;

   
   public Functions()
   {
      super("Functions", DescCP.FUNP);
      
      funp_ = main_result_;
      funo_ = Result.new_result(this, DescCP.FUNO);
      joiners_ = Result.new_result(this, DescCP.JOINER);
      records_ = Result.new_result(this, DescCP.RECORD);
      taxonomy_ = Result.new_result(this, DescCP.TAXONOMY);
      abs_types_ = Result.new_result(this, DescCP.ABSTYPE);
      designator_ = Result.new_result(this, DescCP.DSGN);

      extenders_ = Result.new_result(this, DescCP.EXTENDER);
      moulds_ = Result.new_result(this, DescCP.MOULD);      
      data_managers_ = Result.new_result(this, DescCP.DATAMANAGER);      
      algorithms_ = Result.new_result(this, DescCP.ALGORITHM);

      
      steday_ = Result.new_result(this, DescCP.STEADY_SERVICES);
      
   }

   
   /**
    * @see il.ac.technion.micropatterns.jane.analysis.misc.InheritanceDescendingAgent#inspect(il.ac.technion.micropatterns.jane.elements.ClassHandle)
    */
   protected boolean inspect(ClassHandle h)
   {
      ClassSpec cs = h.typed_value(xmodel_);
      JavaClass jc = cs.jc();
      
      Features features = new Features(cs, xmodel_);
      
      
      ClassHandle spr_ch = cs.get_first_super();
      ClassSpec spr = null;
      if(spr_ch != null)
         spr = spr_ch.typed_value(xmodel_);
      
      int statfinal = 0;
      int statf = 0;
      int instf = 0;

      VectorOfFields vof = af_.get_all().get_fields();
      for(int i = 0; i < vof.size(); ++i)
      {
         Field f = vof.at(i);
         if(f.isStatic() && f.isFinal())
            statfinal += 1;
         else if(f.isStatic() && !f.isFinal())
            statf += 1;
         else if(!f.isStatic())
            instf += 1;
      }
      
      int absm = af_.abstract_methods().size();
      int concm = af_.concrete_methods().size();
      int statm = af_.static_methods().size();
      
      
      
      //
      // Designator
      //      
      if(jc.isAbstract() 
         && concm == 0 
         && absm == 0 
         && statm == 0
         && af_.get_effective().get_interface_methods().size() == 0
         && af_.get_all().get_fields().size() == 0)            
      {
         this.designator_.set_decision(h, true);
      }
      
      //
      // Abs Type
      //
      if(jc.isAbstract())
      {
         if(absm >= 1 && concm == 0 
            && af_.get_all().get_fields().size()== 0)
         {
            this.abs_types_.set_decision(h, true);
         }
      }
      
      //
      // Mould
      //
      if(jc.isAbstract())
      {
      
         boolean ok = false;
         for(int i = 0; i < features.get_methods().size(); ++i)
         {
            Method curr = features.get_methods().at(i);
            if(curr.isAbstract())
            {
               ok = true;
               break;
            }                        
         }

         boolean has_inst_fields = false;
         for(int i = 0; i < features.get_fields().size(); ++i)
         {
            Field f = features.get_fields().at(i);
            if(f.isStatic())
               continue;
            
            has_inst_fields = true;
            break;            
         }
         
         if(ok && concm >= 1 && !has_inst_fields)
            this.moulds_.set_decision(h, true);
      }
      

      // 
      // Taxonomy
      //
      boolean b = is_taxonomy(h, cs, jc);
      if(b)
         this.taxonomy_.set_decision(h, true);
      
      
      //
      // Joiner
      //
      b = is_joiner(h, cs, jc, instf);
      if(b)
         this.joiners_.set_decision(h, true);
      

      
      
      //
      // Record
      //
      if(!cs.jc().isAbstract())
      {
         int count = 0;
         boolean ok = true;

         for(int i = 0; ok && (i < af_.get_all().get_fields().size()); 
            ++i)
         {
            Field f = af_.get_all().get_fields().at(i);
            if(!f.isStatic())
            {
               if(f.isPublic())
                  count += 1;
               else
                  ok = false;
            }
         }
         
         if(ok && (count >= 1) && (absm + concm + statm == 0))
            this.records_.set_decision(h, true);
      }
      
      
      //
      // FUNO, FUNP
      //
      if(!cs.jc().isAbstract())
      {
         if(absm + concm + statm == 1)
         {
            if(instf == 0 && statf == 0)
               funp_.set_decision(h, true);
            else
               funo_.set_decision(h, true);
         }
      }
      
      //
      // Extender
      //
      b = is_extender(h, cs, jc, spr);
      if(b)
         extenders_.set_decision(h, true);
      

      //
      // Data Manager
      //
      b = is_data_manager(h, cs, jc, features, instf);
      if(b)
         data_managers_.set_decision(h, true);
      
      //
      // Algorithm
      //
      b = is_algorithm(h, cs, jc, absm, concm, statm, instf, statf, statfinal);
      if(b)
         algorithms_.set_decision(h, true);
      
      //
      // Steady Services
      //
      b = is_steady(h, cs, jc, features);
      if(b)
         steday_.set_decision(h, true);
            
      return true;
   }
   
   private void my_insepector(ClassHandle h, ClassSpec cs, JavaClass jc)
   {
      Method m[] = jc.getMethods();
      
      boolean has_stat_m = false;
      boolean has_inst_m = false;
      boolean has_stat_f = false;
      boolean has_inst_f = false;
      
      for(int i = 0; i < m.length; ++i)
      {
         if(JavaSpec.is_constructor(m[i]))
            continue;
         
         if(m[i].isStatic())
            has_stat_m = true;
         
         if(!m[i].isStatic())
            has_inst_m = true;
      }
      
      
      Field f[] = jc.getFields();
      for(int i = 0; i < f.length; ++i)
      {
         if(f[i].isStatic())
            has_stat_f = true;
         
         if(!f[i].isStatic())
            has_inst_f = true;
      }
      
      boolean has_stat = has_stat_m || has_stat_f;
      boolean has_inst = has_inst_m || has_inst_f;
      
      if(has_stat && !has_inst)
      {
         only_stat_ += 1;
         JimaMisc.log().println(";;**;;  STAT=" + h.get_name());
      }
      
      if(!has_stat && has_inst)
      {
         only_inst_ += 1;
         JimaMisc.log().println(";;**;;  INST=" + h.get_name());
      }
      
      if(has_stat && has_inst)
      {
         both_stat_inst_ += 1;
         JimaMisc.log().println(";;**;;  BOTH=" + h.get_name());
      }
      
      boolean spr_is_obj = jc.getSuperclassName().equals(JavaSpec.JAVA_LANG_OBJECT);
      if(!spr_is_obj)
      {
         inherits_not_from_object_ += 1;
         JimaMisc.log().println(";;**;;  NOT EXTENDS OBJECT=" + h.get_name());
         if(jc.isInterface())
            System.out.println(";;**;; ^^^^^ INTERFACE");
      }
      
//      if(!jc.isInterface())
//      {
//         if(spr_is_obj)
//            this.ext_obj_.set_decision(h, true);
//         else
//            this.ext_not_obj_.set_decision(h, true);
//      }
      
      
      
   }
   
   
   
   /**
    * @see il.ac.technion.micropatterns.jane.analysis.misc.AbstractAgent#agent_teardown()
    */
   public void agent_teardown() throws Throwable
   {
      
      JimaMisc.log().println("Only stat=" + only_stat_);
      JimaMisc.log().println("Only inst=" + only_inst_);
      JimaMisc.log().println("Both=" + both_stat_inst_);
      JimaMisc.log().println("inherits_not_from_object_=" + inherits_not_from_object_);
      
      super.agent_teardown();
   }
   
   private int only_stat_ = 0;
   private int only_inst_ = 0;
   private int both_stat_inst_ = 0;
   private int inherits_not_from_object_ = 0;
   
   private boolean is_steady(ClassHandle ch, ClassSpec cs, JavaClass jc, 
      Features features)
   {
      if(!jc.isInterface())
         return false;
      
      int n = 0;
      
      VectorOfMethods vom = features.get_methods();
      for(int i = 0; i < vom.size(); ++i)
      {
         Method m = vom.at(i);
         if(m.isStatic())
            continue;
         
         if(JavaSpec.is_constructor(m))
            continue;
         
         if(m.isPrivate())
            continue;
         

         String method_sig = m.getSignature();
         if(JavaSpec.num_of_args(method_sig) != 0)
            return false;
         
//            continue;
//         
//         if(sig == null)     
//         {            
//            sig = method_sig;
//            continue;
//         }
//         
//         if(!sig.equals(method_sig))
//            return false;
//         
//         System.out.println("Sig=" + sig);
            
         n += 1;
      }
   
      if(n >= 1)
         return true;
      
      // ...Else:
      return false;      
   }
   
   private boolean is_algorithm(ClassHandle ch, ClassSpec cs, JavaClass jc, 
      int absm, int concm, int statm, int instf, int statf, int statfinal)
   {
      if(instf != 0)
         return false;
      
      if(absm != 0)
         return false;
      
      if(concm != 0)
         return false;
      
      if(statm != 1)
         return false;
      
      
      return true;
      
   }
   
   //
   // Data Manager
   //
   public boolean is_data_manager(ClassHandle ch, ClassSpec cs, JavaClass jc, 
      Features features, int instf)
   {
      
      if(instf == 0)
         return false;
      
      
      if(jc.isInterface())
         return false;
      
      String class_name = cs.name();
      
      ConstantPoolGen cpg = cs.constant_pool();
      
      int valid_methods = 0;

      int num_setters = 0;
      int num_getters = 0;
      
      for(int i = 0; i < features.get_methods().size(); ++i)
      {
         Method curr = features.get_methods().at(i);

         if(JavaSpec.is_constructor(curr))
            continue; // Ignore constructors
         
         if(curr.isPrivate())
            continue;
         
         valid_methods += 1;
         boolean isg = is_getter(curr, class_name, cpg);
         if(isg)
         {
            num_getters += 1;
            continue;
         }
         
         boolean iss = is_setter(curr, class_name, cpg);
         if(iss)
            num_setters += 1;
      }        
      
      if(valid_methods == 0)
         return false;
      

      if(num_getters == 0)
         return false;
      
      return num_setters + num_getters == valid_methods;      
   }
   
   private static boolean is_getter(Method m,  String class_name, 
      ConstantPoolGen cpg)
   {
      InstructionParser ip = new InstructionParser(m); 

      ALOAD ins_a = null;
      GETFIELD ins_b = null;
      ReturnInstruction ins_c = null;
   
      Iterator j = ip.iterator(); 
      try
      {
         if(j.hasNext())
            ins_a = (ALOAD) j.next();

         if(j.hasNext())
            ins_b = (GETFIELD) j.next();
         
         if(j.hasNext())
            ins_c = (ReturnInstruction) j.next();
         
         if(ins_a == null || ins_b == null || ins_c == null)
            return false;
         
         if(ins_a.getIndex() != 0)
            return false;
         
         if(!ins_b.getClassName(cpg).equals(class_name))
            return false;
         
         return true;            
      }
      catch(ClassCastException e)
      {
         return false;
      }      
   }

   
   private static boolean is_setter(Method m,  String class_name, 
      ConstantPoolGen cpg)
   {
      if(JavaSpec.num_of_args(m) != 1)
         return false;
      
      Iterator i = JavaSpec.get_signatures_of_args(m.getSignature());

      JimaMisc.ensure(i.hasNext());
      String sig_of_arg = (String) i.next();
      JimaMisc.ensure(!i.hasNext());
      
      InstructionParser ip = new InstructionParser(m); 

      if(!JavaSpec.is_returning_void(m))
         return false; // Method m has a non-void return type => Not a setter
      
      for(Iterator j = ip.iterator(); j.hasNext(); )
      {
         Instruction curr = (Instruction) j.next();
         if(!(curr instanceof PUTFIELD))
            continue;
         
         PUTFIELD pf = (PUTFIELD) curr;
         if(pf.getClassName(cpg).equals(class_name))
            return true; // Success!
         
         String field_type = pf.getFieldType(cpg).getSignature();
         field_type = JaneMisc.signature_to_text(field_type);
         
         if(field_type.equals(sig_of_arg))
            return true;
      }
      
      return false; // Failure
   }
   
   //
   // Join
   //
   public boolean is_joiner(ClassHandle ch, ClassSpec cs, JavaClass jc, 
      int instf)
   {
      VectorOfFields vof = new VectorOfFields(jc.getFields());
      for(int i = 0; i < vof.size(); ++i)
      {
         Field f = vof.at(i);
         if(f.isStatic())
            continue;
         
         // ...Else:
         return false;         
      }
      
      int num_interfaces = Foreign.getInterfaces(jc).length;
      if(num_interfaces == 0)
         return false;
      
      // We have least one super interface

      for(int i = 0;  i < jc.getMethods().length; ++i)
      {
         Method m = jc.getMethods()[i];
         if(JavaSpec.is_constructor(m))
            continue;
        
         // ...Else:
         return false;
      }
      
      // No methods are defined      
      if(jc.isInterface() && num_interfaces == 1)
         return false; // This is actually a Taxonomy, not a Join
      
      if(jc.isInterface() && num_interfaces >= 2)
         return true;
      
      // ...Else: jc is actually a class
      if(num_interfaces >= 1)
         return true;
      
      // ...Else:
      return false;
   }
   
   
   //
   // Taxonomy
   //
   private boolean is_taxonomy(ClassHandle ch, ClassSpec cs, JavaClass jc)
   {
      if(jc.getFields().length > 0)
         return false;
      
      
      int num_intrf = Foreign.getInterfaces(jc).length;
      
      if(num_intrf > 1)
         return false; 
      
      // We have 0 or 1 super interfaces

      for(int i = 0;  i < jc.getMethods().length; ++i)
      {
         Method m = jc.getMethods()[i];
         if(JavaSpec.is_constructor(m))
            continue;
        
         // ...Else:
         return false;
      }
      
      if(jc.isInterface())
      {
         if(num_intrf == 1)
            return true;
         else
            return false; // This is a Designator
      }
      
      // ...Else: This is a class
      ClassHandle spr = cs.get_first_super();

      // java.lang.Object defines methods so spr must be non null
      JimaMisc.ensure(spr != null, "jc=" + jc);  
      
      if(JavaSpec.is_java_lang_object(spr.get_name()))
         return false;  // jc's super is java.lang.Object => This a designator
      
      // ... JC is:
      // 1) a class
      // 2) It defines no methods
      // 3) Its super class is not java.lang.Object
      if(num_intrf == 0)
         return true; // jc joins spr and an interface => Success
      
      // ...Else: this is a joiner
      return true;
   }
   
   private boolean is_extender(ClassHandle ch, ClassSpec cs, JavaClass jc, 
      ClassSpec spr)
   {      
      Features inherited = af_.get_inherited();
      if(!inherited.has_instance_methods())
         return false;
      
      
      int n = 0;
      
      VectorOfMethods vom = new VectorOfMethods(jc.getMethods());
      for(int i = 0; i < vom.size(); ++i)
      {
         Method curr = vom.at(i);
         if(curr.isStatic())
            continue;
         
         if(JavaSpec.is_constructor(curr))
            continue;
         
         if(curr.isPrivate())
            continue;
         
         MethodSelector ms = new MethodSelector(curr);
         if(inherited.is_defined(ms))
            return false;
         
         n += 1;
      }
      
      if(n > 0)
         return true;
      
      // ...Else:
      return false;
   }
}
