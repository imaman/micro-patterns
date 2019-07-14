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



package il.ac.technion.jima.util;

import java.util.Iterator;

/**
 * An Iterator over a collection of Integer elements.
 *
 * @author Itay
 * @since 09/07/2004
 */
public class IntIterator implements Iterator
{
   private Iterator inner_;
   
   private IntIterator(Iterator inner)
   {
      inner_ = inner;
   }
   
   /**
    * @see java.util.Iterator#hasNext()
    */
   public boolean hasNext()
   {
      return inner_.hasNext();
   }

   /**
    * @see java.util.Iterator#next()
    */
   public Object next()
   {
      return inner_.next();
   }

   /**
    * @see java.util.Iterator#remove()
    */
   public void remove()
   {
      inner_.remove();
   }
   
    /**
     * Returns the next Integer in the iteration.  Calling this method
     * repeatedly until the {@link @hasNext()} method returns false will
     * return each element in the underlying collection exactly once.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */   
   public Integer nextInt()
   {
      Integer result = (Integer) next();
      return result;
   }
}
