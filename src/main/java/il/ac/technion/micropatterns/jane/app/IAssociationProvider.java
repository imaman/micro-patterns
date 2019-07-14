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
 * Created on 22/11/2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package il.ac.technion.micropatterns.jane.app;

import il.ac.technion.micropatterns.jane.elements.ClassSpec;
import il.ac.technion.micropatterns.jane.lib.JavaSpec;
import il.ac.technion.micropatterns.jane.typedmodel.TypedModel;

import java.util.Iterator;

public interface IAssociationProvider
{
   public String name();

   public Iterator associations_of(ClassSpec cs, TypedModel m);

   public static final IAssociationProvider COMPOSITION
      = new IAssociationProvider()
      {
         public String name()
         {
            return "composition";
         }

         public Iterator associations_of(ClassSpec cs, TypedModel m)
         {
            return JavaSpec.aggregated_classes(cs.jc(), false);
         }
      };

   public static final IAssociationProvider INHERITANCE 
      = new IAssociationProvider()
      {
         public String name()
         {
            return "inheritance";
         }
   
         public Iterator associations_of(ClassSpec cs, TypedModel m)
         {
            return JavaSpec.direct_sub_class(cs.jc());
         }
      };

   public static final IAssociationProvider METHOD_CALLS 
      = new IAssociationProvider()
      {
         public String name()
         {
            return "method invocation";
         }
   
         public Iterator associations_of(ClassSpec cs, TypedModel m)
         {
            return JavaSpec.method_invocations(cs, m, false);
         }
      };
      
   public static final IAssociationProvider METHOD_CALLS_FROM_CTORS 
      = new IAssociationProvider()
      {
         public String name()
         {
            return "method invoked by ctors";
         }
   
         public Iterator associations_of(ClassSpec cs, TypedModel m)
         {
            return JavaSpec.method_invocations(cs, m, true);
         }
      };
}
