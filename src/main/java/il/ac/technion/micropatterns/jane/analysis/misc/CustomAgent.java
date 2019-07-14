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











package il.ac.technion.micropatterns.jane.analysis.misc;

import il.ac.technion.jima.JimaMisc;
import il.ac.technion.micropatterns.jane.elements.ClassHandle;
import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.ProvidedMethods;

import java.io.PrintStream;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;


public class CustomAgent  
   extends InheritanceDescendingAgent
{
   private Result designator_;
   private Result one_method_;
   
   public static PrintStream out_ = System.out;
   private static boolean first_time_ = true;

   public static String NAME = "Custom";
   public CustomAgent()
   {
      super(NAME, null); // DescCP.CUSTOM);
      JimaMisc.wrong_way();
      
      designator_ = this.main_result_;
//      one_method_ = Result.new_result(this, DescCP.ONEMETH);
      
   }

   protected boolean inspect(ClassHandle h)
   {      
      ClassSpec cs = h.typed_value(xmodel_);
      ProvidedMethods pm = new ProvidedMethods(xmodel_, h, false);
      check(h, cs, pm);
      
      return true;
   }

   private static String b2s(boolean b)
   {
      return b ? "Yes" : "-";
      
   }
   
   public static void dump(ClassSpec cs, AvailableFeatures af)
   {
      JavaClass jc = cs.jc();
      if(first_time_)
      {         
         out_.println("Name,Package,isFinal,isAbstract,Conc.Methods,Abs.Methods,"
               +"Static.Methods,Fields,StatFinalFields,StatFields,InstFields");
      }
      
      first_time_ = false;
      
      int statfinal = 0;
      int statf = 0;
      int instf = 0;
      
      VectorOfFields vof = af.get_all().get_fields();
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
      
      out_.println(jc.getClassName() 
         + "," + jc.getPackageName()
         + "," + b2s(jc.isFinal())
         + "," + b2s(jc.isAbstract())
         + "," + af.concrete_methods().size()
         + "," + af.abstract_methods().size()
         + "," + af.static_methods().size()
         + "," + af.get_all().get_fields().size()
         + "," + statfinal
         + "," + statf
         + "," + instf);
   }
   
   
   private void check(ClassHandle ch, ClassSpec cs, ProvidedMethods pm)
   {
      if(cs.jc().isAbstract())
         return;
      
      JimaMisc.ensure(af_.abstract_methods().size() == 0);
      int n = af_.concrete_methods().size() + af_.static_methods().size();
      if(n != 1)
         return;
      
//      System.out.println(ch.get_name() + ", n=" + n + ", " + af_.static_methods().size());
      this.one_method_.set_decision(ch, true);
      dump(cs, af_);      
   }
   
   
   private void check_xxx(ClassHandle ch, ClassSpec cs, ProvidedMethods pm)
   {
//      if(!cs.jc().isAbstract())
//         return;
//    
//      if(af_.concrete_methods().size() == 0 && af_.abstract_methods().size() == 0
//         && af_.static_methods().size() == 0)
//      {
//         if(af_.get_effective().get_fields().size() == 0)
//         {
//            designator_.set_decision(ch, true);
////            System.out.println(ch.get_name());
//         }         
//      }
//      
//
////      {
////      }
////      for(int i = 0; i < af_.get_effective().get_methods(); ++i)
////      {
////         Method m = af_.get_effective().get_methods().at
////      }
////      
//
////      if(ch.get_name().startsWith("java.text"))
////      {
////         JimaMisc.log().println();
////         JimaMisc.log().println();
////         JimaMisc.log().println("Class: " + ch.get_name());
////         JimaMisc.log().println("Abs.Methods=" + af_.num_abstract_methods());
////         JimaMisc.log().println("Conc.Methods=" + af_.num_concrete_methods());
////
////         JimaMisc.log().println("Fields: " + af_.get_effective().get_fields().size());
////         for(int i = 0; i <  af_.get_effective().get_fields().size(); ++i)
////            JimaMisc.log().println("  " + af_.get_effective().get_fields().at(i));
////
////         dump("Static. Methods:", af_.static_methods());
////         dump("Abs. Methods:", af_.abstract_methods());
////         dump("Conc. Methods:", af_.concrete_methods());         
////      }
   }
   
   private static void dump(String s, VectorOfMethods vom)
   {
         JimaMisc.log().println(s + vom.size());
         for(int i = 0; i <  vom.size(); ++i)
            JimaMisc.log().println("  " + vom.at(i));
      
   }
}
