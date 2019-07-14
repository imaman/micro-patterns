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











package il.ac.technion.micropatterns.jane.elements;

import il.ac.technion.micropatterns.jane.model.IVisitor;

/**
 * A do-nothing visitor. Allow Jane Model visitor classes to inherit all 
 * required methods with a NOP implementation.
 *
 * @author Itay
 * @since 06/07/2004
 */
public class EmptyVisitor implements IVisitor
{
   
   /**
    * @see il.ac.technion.micropatterns.jane.model.IVisitor#visit(il.ac.technion.micropatterns.jane.app.elements.ClassProxy)
    */
   public void visit(ClassHandle h) { }

   /**
    * @see il.ac.technion.micropatterns.jane.model.IVisitor#visit(il.ac.technion.micropatterns.jane.app.elements.GenericProxy)
    */
   public void visit(VectorHandle h) { }
   
   /**
    * @see il.ac.technion.micropatterns.jane.model.IVisitor#visit(il.ac.technion.micropatterns.jane.app.elements.SubsetProxy)
    */
   public void visit(SubsetHandle h) { }
   
   
   /**
    * @see il.ac.technion.micropatterns.jane.model.IVisitor#visit(il.ac.technion.micropatterns.jane.elements.LazyVectorProxy)
    */
//   public void visit(LazyVectorProxy gp) { }
   /**
    * @see il.ac.technion.micropatterns.jane.model.IVisitor#visit(il.ac.technion.micropatterns.jane.elements.MethodHandle)
    */
   public void visit(MethodHandle h) { }

}
