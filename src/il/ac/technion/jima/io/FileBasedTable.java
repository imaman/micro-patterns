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



package il.ac.technion.jima.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class FileBasedTable
{

   private File file_;
   private RandomAccessFile raf_;
   
   /**
    * 
    */
   public FileBasedTable(File f) throws IOException
   {
      file_ = f;
      raf_ = new RandomAccessFile(file_, "rw");
   }
   
   public static class EntryId
   {
      private long offset_;
      
      public EntryId(long offset)
      {
         offset_ = offset;
      }
   }
   
   public EntryId write(Serializable data) throws IOException
   {            
      ByteArrayOutputStream bao = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bao);
      oos.writeObject(data);
      oos.close();
      
      byte[] bytes = bao.toByteArray();
      
            
      long pos = raf_.length();
      raf_.seek(pos);

      raf_.write(bytes.length);
      raf_.write(bytes);
      
      EntryId result = new EntryId(pos);
      return result;
   }
   

   public Serializable write(EntryId eid) 
      throws IOException, ClassNotFoundException
   {            
      long pos = eid.offset_;
      raf_.seek(pos);
      
      
      int count = raf_.read();
      byte[] bytes = new byte[count];      
      int read = raf_.read(bytes);
      
      if(read != count)
         throw new IOException("File structure is damaged");
      
      ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(bai);
      
      Serializable result = (Serializable) ois.readObject();
      ois.close();
      
      return result;
   }


}
