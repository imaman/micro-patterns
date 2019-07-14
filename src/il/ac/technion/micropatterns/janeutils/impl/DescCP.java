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









package il.ac.technion.micropatterns.janeutils.impl;

import java.util.Iterator;
import java.util.Vector;

public class DescCP implements Comparable
{
   private String in_name_;
   private String out_name_;
   private int id_;
   private String initials_;
   private String full_name_ = "";
   
   public String get_full_name()
   {
      return full_name_;
   }
   
   public String get_initials()
   {
      return initials_;
   }

   private static Vector all_ = new Vector();
   
   public static Iterator all()
   {
      return all_.iterator();
   }
   
   public static int size()
   {
      return all_.size();
   }
   
   public String out_name()
   {
      return out_name_;
   }
   
   public static DescCP get(int id)
   {
      if(id == -1)
         return DescCP.NONE;
      return (DescCP) all_.get(id);
   }
            
   public boolean match_name(String name)
   {
      boolean b = in_name_.equals(name) || out_name_.equals(name) 
         || initials_.equals(name);
      
      return b;
   }
   
   public int get_id()
   {
      return id_;
   }

   
   private DescCP(String in_name, String initials, String full)
   {
      full_name_ = full;
      out_name_ = "\\" + initials;
      in_name_ = in_name;
      initials_ = initials;
      id_ = all_.size();
      
      all_.add(this);
   }

   private DescCP()
   {
      out_name_ = in_name_ = "NONE";
      id_ = -1;
   }
   public static final DescCP USLS = makeFull("USLS", "Useless");
   
   public static final DescCP BOX = makeFull("BOX", "Box");
   public static final DescCP BX2 = makeFull("CBOX", "Compound Box");
   public static final DescCP FLY = makeFull("FLYW", "Sampler");
   public static final DescCP IMB = makeFull("IMBX", "Canopy");
   public static final DescCP IMM = makeFull("IMMU", "Immutable");
   public static final DescCP IMP = makeFull("IMPL", "Implementor");
   public static final DescCP INTR = makeFull("INTR", "Interface");
   public static final DescCP PCLS = makeFull("PCLS", "Pseudo Class");
   public static final DescCP POO = makeFull("POOL", "Pool");
   public static final DescCP RC = makeFull("RSCR", "Restricted Creation");
   public static final DescCP RIM = makeFull("REIM", "Overrider");
   public static final DescCP SNK = makeFull("SINK", "Sink");
   public static final DescCP ST0 = makeFull("STLS", "Stateless");
   public static final DescCP ST1 = makeFull("MONO", "Common State");
   public static final DescCP TM = makeFull("TMPL", "Outline");
   public static final DescCP FUNP= makeFull("FPTR", "Function Pointer");
   public static final DescCP FUNO = makeFull("FOBJ", "Function Object");
   public static final DescCP JOINER = makeFull("JOIN", "Joiner");
   public static final DescCP DSGN = makeFull("DSGN", "Designator");
   public static final DescCP RECORD = makeFull("RCRD", "Record");
   public static final DescCP TAXONOMY = makeFull("TXNM", "Taxonomy");
   public static final DescCP ABSTYPE= makeFull("TYPE", "PureType");
   public static final DescCP ENUMTYPE= makeFull("ENUM", "Augmented Type");
   public static final DescCP EXTENDER = makeFull("EXTN", "Extender");
   public static final DescCP DATAMANAGER = makeFull("DATA", "Data Manager");
   public static final DescCP MOULD = makeFull("MOLD", "Trait");
   public static final DescCP ALGORITHM = makeFull("ALGO", "Cobol Like");
   public static final DescCP STEADY_SERVICES = makeFull("STDY", 
      "State Machine");
   public static final DescCP RECURSIVE = makeFull("RCUR", "Recursive");
   public static final DescCP LIMITED_SELF = makeFull("LSLF", "Limited Self");
   
//   public static final DescCP METHODS_IS_PRIME = make("MPR");
//   public static final DescCP FIELDS_IS_PRIME = make("FPR");
//   public static final DescCP CLASS_NAME_DIV_4 = make("CND4");
//   public static final DescCP CLASS_NAME_DIV_5 = make("CND5");
//   public static final DescCP CLASS_NAME_IS_PRIME = make("CNP");
//   

   
//   public static final DescCP EXTENDS_OBJECT = make("SUBOB");
//   public static final DescCP EXTENDS_NOT_OBJECT = make("!SUBO");
   

   
   public static final DescCP NONE = new DescCP();
   
   private static DescCP makeFull(String s, String full)
   {
      return new DescCP(s, s, full);
   }
   
   private static DescCP make(String s, String out)
   {
      return new DescCP(s, out, "");
   }

   private static DescCP make(String s)
   {
      return make(s,s);
   }
   
   public String short_text()
   {
      return id_ + ", " + out_name_;
   }
   
   public String get_name()
   {
      return in_name_;
   }
   
   
   public String toString()
   {
      return out_name_;
   }
   
   public int hashCode()
   {
      return id_;
   }
   
   public boolean equals(Object other)
   {
      if(other == null)
         return false;
      
      if(other.getClass() != this.getClass())
         return false;
      
      DescCP rhs = (DescCP) other;
      return this.id_ == rhs.id_;
   }
   
   
   
   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(T)
    */
   public int compareTo(Object o)
   {
      DescCP that = (DescCP) o;
      return this.out_name().compareTo(that.out_name());
   }
}
