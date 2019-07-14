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











package il.ac.technion.micropatterns.jane.app;


public class App
{
//   private int class_count_ = 0;
//
//   public static PrintStream out_;
//   
//   static
//   {
//      out_ = System.out;
//      try
//      {
//         out_ = new PrintStream(new FileOutputStream("c:\\temp\\a.log"));
//      }
//      catch(FileNotFoundException e)
//      {
//         // Absorb
//      }
//   }
//   
//   private XModel model_;
//   private static Environment env_;
//   
//   
//   // False: 0 > 0 
//   // True:  1 > 0
//   private static final boolean BUILD_ALWAYS = 1 > 0;
//
//   
//   public App(String[] args, String cp) 
//      throws IOException, Environment.CommandLineError, ModelInitError, 
//         ClassNotFoundException
//   {
//      Environment.instance().init(args);
//      File f = env_.file();
//      
//      if(BUILD_ALWAYS || !f.exists())
//         model_ = new XModel(f, false);  
//      else         
//         model_ = new XModel(env_.file());
//      
//      if(model_.is_empty())
//      {
//         build(cp);
//         model_.save();
//      }
//   }
//   
//   public void build(String cp) throws ClassNotFoundException, IOException
//   {
//      StringTokenizer st = new StringTokenizer(cp,
//         System.getProperty("path.separator").toString());
//
//      while(st.hasMoreTokens())
//      {
//         String curr = st.nextToken();
//         File f = new File(curr);
//         if(f.isDirectory())
//            build_from_dir("", f);
//         else 
//         {
//            JarFile jf = new JarFile(f);
//            build_from_jar(jf);
//         }
//      }
//   }
//
//   public void build_from_dir(String path, File dir) throws ClassNotFoundException
//   {
//      File[] entries = dir.listFiles();
//      
//      for(int i = 0; i < entries.length; ++i)
//      {
//         File f = entries[i];
//
//         if(f.isDirectory())         
//         {
//            String temp = path + f.getName() + ".";
//            build_from_dir(temp, f);
//         }
//         else if(f.getName().endsWith(".class"))
//         {
//            String temp = path + JimaMisc.change_suffix(f.getName(), "");
//            System.out.println("temp=" + temp);
//            add_type(temp);
//         }
//      }
//   }
//
//   public void build_from_jar(JarFile jar) throws ClassNotFoundException
//   {
//      Enumeration iter = jar.entries();
//      while(iter.hasMoreElements())
//      {         
//         JarEntry curr = (JarEntry) iter.nextElement();
//         
//         String temp = translate_name(curr.getName());
//         if(temp == null)
//            continue;
//
//         add_type(temp);
//      }               
//   }
//   
//   private static String translate_name(String class_name)
//   {
//      if(!class_name.endsWith(CLASS_SUFFIX))
//         return null;
//         
//      StringBuffer result = new StringBuffer(class_name);
//      
//      for(int i = 0; i < class_name.length(); ++i)
//      {
//         char c = class_name.charAt(i);
//         if(c == '/')
//            c = '.';
//            
//         result.setCharAt(i, c);
//      } 
//      
//      return result.substring(0, class_name.length() - CLASS_SUFFIX.length());
//   }
//   
//   private static final String CLASS_SUFFIX = ".class";
//   
//   private void add_type(String name) throws ClassNotFoundException
//   {   
//      if(name == null)
//         return;
//    
//      class_count_ += 1;
//      if(class_count_ % 1000 == 0)
//         System.out.println("classes loaded = " + class_count_);        
//
//      ClassProxy cp = new ClassProxy(model_, name);
//   }
//   
//   private class Printer extends EmptyVisitor
//   {
//      private PrintStream out_ = App.out_;
//      
//      public void visit(ClassProxy cp)
//      {
//         ClassElement ce = (ClassElement) model_.get_element(cp);
//         print(out_, ce);
//      }      
//
//      public void visit(SubsetProxy sp) { }
//      
//      public void print(PrintStream out, ClassElement ce)
//      {
//         String s = ce.super_class_name();            
//
//         out.println("======================================");
//         out.println(ce.name() + " extends " + s + " {");
//         Field[] f = ce.jc().getFields();
//         for(int i = 0; i < f.length; ++i)
//         {                  
//            out.println("  " + f[i].getSignature() + " " 
//               + f[i].getName() + ";");
//         }                     
//         out.println("}");
//         out.println("======================================");
//         out.println();
//         out.println();         
//      }
//   }
//   
//
//   private static String class_to_bc_format(String class_name)
//   {
//      StringBuffer result = new StringBuffer("L" + class_name + ";");
//      for(int i = 0; i < result.length(); ++i)
//      {
//         char c = result.charAt(i);
//         if(c == '.')
//            result.setCharAt(i, '/');            
//      }            
//      
//      return result.toString();
//   }
//
//
//   private class DeadEndFinder extends EmptyVisitor
//   {
//      private PrintStream out_ = App.out_;
//      private int count_ = 0;
//      
//      private class MyVis extends de.fub.bytecode.generic.EmptyVisitor
//      {
//         public int invoke_count_ = 0;
//         
//         public void visitInvokeInstruction(InvokeInstruction obj)
//         {
//            invoke_count_ += 1;
//         }         
//      }
//      
//      public void visit(SubsetProxy sp) { }
//            
//      public void visit(ClassProxy cp)
//      {
//         ClassElement ce = (ClassElement) model_.get_element(cp);
//         if(ce.jc().isInterface())
//            return;
//         
//         MyVis iv = new MyVis();
//
//         Method[] m = ce.jc().getMethods();
//         for(int i = 0; i < m.length; ++i)
//         {
//            Method curr = m[i];
//            if(curr.getName().endsWith("init>"))
//               continue; // Ignore "invoke" instructions within constructors
//
//            InstructionParser ip = new InstructionParser(curr); 
//                        
//            try
//            {
//               ip.accept(iv);
////               out_.println(ce.name() + "." + curr.getName() + ", ic=" + iv.invoke_count_);
//            }
//            catch (RuntimeException e)
//            {
//               return;
//            }
//         }         
//         
//         if(iv.invoke_count_ > 0)
//            return;
//
//         // ...Else:
//         count_ += 1;
//         out_.println("[Dead end: " + count_ + "]");            
//         cp.accept(new Printer());            
//      }      
//   }
//
//   private class DecoratorFinder extends EmptyVisitor
//   {
//      private PrintStream out_ = App.out_;
//      private int count_ = 0;
//
//      public void visit(SubsetProxy sp) { }
//            
//      public void visit(ClassProxy cp)
//      {
//         ClassElement ce = (ClassElement) model_.get_element(cp);
//         
//         if(ce.is_java_object())
//            return;
//            
//         String scn = ce.super_class_name();            
//         String s = class_to_bc_format(scn);
//
//         int n = 0;
//         
//         Field[] f = ce.jc().getFields();
//         for(int i = 0; i < f.length; ++i)
//         {              
//            if(f[i].isStatic())
//               continue;
//                   
//            String sig = f[i].getSignature();
//            if(sig.equals(s))
//               n += 1;
//         }
//         
//         if(n != 1)               
//            return;
//
//         count_ += 1;
//         out_.println("[Decorator: " + count_ + "]");            
//         cp.accept(new Printer());
//      }      
//   }
//
//   private class TreeLikeFinder extends EmptyVisitor
//   {
//      private PrintStream out_ = App.out_;
//      private int count_ = 0;
//
//      public void visit(SubsetProxy sp) { }
//            
//      public void visit(ClassProxy cp)
//      {
//         ClassElement ce = (ClassElement) model_.get_element(cp);
//         
//         String s = class_to_bc_format(ce.name());
//
//         int n = 0;
//         
//         Field[] f = ce.jc().getFields();
//         for(int i = 0; i < f.length; ++i)
//         {              
//            if(f[i].isStatic())
//               continue;
//                   
//            String sig = f[i].getSignature();
//            if(sig.equals(s))
//               n += 1;
//         }
//         
//         if(n == 0)               
//            return;
//
//         count_ += 1;
//         out_.println("[TreeLike: " + count_ + "]");            
//         cp.accept(new Printer());
//      }      
//   }
//   
//   private class SingletonFinder extends EmptyVisitor
//   {
//      private PrintStream out_ = App.out_;
//      private int count_ = 0;
//            
//      public void visit(SubsetProxy sp) { }
//
//      public void visit(ClassProxy cp)
//      {
//         ClassElement ce = (ClassElement) model_.get_element(cp);
//         
//         String s = class_to_bc_format(ce.name());
//
//         int n = 0;
//         
//         Field[] f = ce.jc().getFields();
//         for(int i = 0; i < f.length; ++i)
//         {              
//            if(!f[i].isStatic())
//               continue;
//                   
//            String sig = f[i].getSignature();
//            if(sig.equals(s))
//               n += 1;
//         }
//         
//         if(n == 0)               
//            return;
//
//
//         // Make sure there is a static method which
//         // return an object of type 'this'         
//
////         Method[] m = ce.jc().getMethods();
////         for(int i = 0; i < m.length; ++i)
////         {           
////            String mn = m[i].getName();   
////            if(!mn.equals("<init>"))
////               continue;
////
////            if(m[i].isPublic())
////               return;
////         }
//
//         count_ += 1;
//         out_.println("[Singleton: " + count_ + "]");            
//         cp.accept(new Printer());
//      }    
//   }
//
//   
//   private class DepthCounter extends EmptyVisitor
//   {
//      public int result_ = 0;
//      public int n_ = 0;
//
//      public void visit(SubsetProxy sp) { }
//
//      public void visit(ClassProxy cp)
//      {
//         n_ += 1;
//         String s0 = cp.name();
//
//         int d = 0;
//         while(true)
//         {
//            d += 1;
//            
//            ClassElement ce = (ClassElement) model_.get_element(cp);
//            
//            String s = ce.super_class_name();            
//            if(n_ > 1000 && n_ < 1010)
//            {
//               System.out.println(cp.name() + " extends " + s + " {");
//               Field[] f = ce.jc().getFields();
//               for(int i = 0; i < f.length; ++i)
//               {                  
//                  System.out.println("  " + f[i].getSignature() + " " 
//                     + f[i].getName() + ";");
//               }                     
//               System.out.println("}");
//               System.out.println();
//            }
//            
//            ClassProxy temp = (ClassProxy) model_.get_proxy(s);
//            if(temp == cp)
//               break;
//
//            if(temp == null)
//               return;
//
//            cp = temp;
//         }   
//
//         result_ += d;      
//      }      
//   }
//   
//   public void inspect_all_types(IVisitor v)
//   {
//      for(Iterator i = this.model_.all_types(); i.hasNext(); )
//      {
//         IProxy p = model_.get_proxy(i.next());
//         p.accept(v);         
//      }           
//   }
//   
//   public void inspect()
//   {
//      long t0 = System.currentTimeMillis();
//
//      inspect_all_types(new DecoratorFinder());
//      inspect_all_types(new TreeLikeFinder());
//      inspect_all_types(new SingletonFinder());          
//      inspect_all_types(new DeadEndFinder());
//      
////      model_.get_proxy("il.ac.technion.micropatterns.samples.DeadEndA").accept(new DeadEndFinder());
////      model_.get_proxy("il.ac.technion.micropatterns.samples.SingletonA").accept(new DeadEndFinder());
//      
//      long t1 = System.currentTimeMillis();
//      
//      out_.println("Time: " + (t1 - t0) / 1000.0f + " [sec]");
//   }
//
//   public static void main(String[] args) throws Throwable
//   {
//      String cp = "c:\\var\\tools\\jdk1.4\\jre\\lib\\rt.jar;"
//         + "C:\\var\\Itay\\ibm-a\\JarScan\\bin";
//
//      Repository.class_path = new ClassPath(cp);
//      App a = new App(args, cp);
//      
//      a.inspect();
//
//      System.out.println("-The End-");
//   }
}
