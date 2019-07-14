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



package il.ac.technion.jima;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Represents an application's command line, typically referred to as argv.
 * class CommandLine is built around the notion of options each optionally 
 * having several parameters. 
 * <p>
 * To understand this terminology let's consider the following command line:
 *    "prg.exe -v -log f1.log;f2.log -out o.txt m1.txt m2.txt m3.txt"
 * 
 * <p>The "Options" in this line are -v, -log and -out. 
 * <p>The "Parameter" of -log is "f1.log;f2.log". The "Parameter" of -out 
 * is o.txt. -v has no parameters.
 * <p>"Sub Parameters" are parts of a parameter. Thus, "f1.log" and "f2.log" 
 * are the sub-parameters of the -log option.
 * <p>
 * The rest of the arguments in the command line (m1.txt, m2.txt, m3.txt) 
 * are considered to be parameters of the "null" option.
 * 
 * @author Itay
 * @since 01/07/2004
 */
public class CommandLine
{
   private Vector entries_ = new Vector();
   private ArgsDescriptions ad_;
   private String[] argv_;
   

   /**
    * Base class for all exceptions thrown by CommandLineParser.
    */
   public static class ParserError extends Exception
   {
      public ParserError(String msg)
      {
         super(msg);
      }
   }
   
      
   private static class ArgsDescriptions
   {
      private HashMap arg2param_count_ = new HashMap();
      
      public ArgsDescriptions(String comma_separated_options0)
      {
         this(comma_separated_options0, null, null);
      }
      
      public ArgsDescriptions(String comma_separated_options0, 
         String comma_separated_options1)
      {
         this(comma_separated_options0, comma_separated_options1, null);
      }

      public ArgsDescriptions(String comma_separated_options0, 
         String comma_separated_options1, String comma_separated_options2)
      {         
         init(comma_separated_options0, 0);
         init(comma_separated_options1, 1);
         init(comma_separated_options2, 2);
      }

      
      private void init(String comma_separater_options, int param_count)
      {
         if(comma_separater_options == null)
            return;
            
         StringTokenizer st = new StringTokenizer(comma_separater_options, ",");
         while(st.hasMoreTokens())
         {
            String curr = st.nextToken();
            set_param_count(curr, param_count);
         }            
      }
      
      public void set_param_count(String option, int param_count)
      {
         arg2param_count_.put(option, new Integer(param_count));
      }
      
      public int param_count_of(String option)
      {
         if(option == null)
            return 1;
            
         Integer result = (Integer) arg2param_count_.get(option);
         if(result == null)
            return -1; // Failure
            
         return result.intValue(); // Success            
      }      
   }
   
   /**
    * Represents an instance of an option along with its parameters.
    */
   public static class Entry
   {
      private String option_;
      private Vector params_ = new Vector();

      private Entry(String option, String value)
      {
         option_ = option;
         params_.add(value);
      }
      
      private Entry(String option)
      {
         option_ = option;
      }
      
      private boolean matches(String option)
      {
         if(option_ == null)
            return option == null;
            
         // ...Else:
         return option_.equals(option);            
      }
      
      private void add_param(String param)
      {
         params_.add(param);
      }      
      
      /**
       * Iterate over all parameters of this Entry.
       * @return An iterator to a collection of Strings.
       */
      public Iterator iterator()
      {
         return params_.iterator();
      }

      /**
       * Iterate over all sub-parameters of this Entry.
       * The sub-parameters are formed by splitting the parameter(s)
       * into tokens which are delimited by the characters specified 
       * by @param separators.
       * 
       * @param separator A String used specifing the sub-parameter delimiting 
       * characters.
       * @return An iterator to a collection of Strings.
       */
      public Iterator iterator(String separators)
      {
         Vector result = new Vector();
         for(int i = 0; i < param_count(); ++i)
         {
            String curr = (String) params_.elementAt(i);
            StringTokenizer st = new StringTokenizer(curr, separators);
            while(st.hasMoreTokens())
            {
               String token = st.nextToken();
               result.add(token);
            }               
         }
         
         return result.iterator();
      }
      
      /** 
       * @return Number of parameters found.
       */
      public int param_count()
      {
         return params_.size();
      }
      
      /**
       * Returns the parameter at the specified index.
       * 
       * @param index an index into this Entry's list of parameters.
       * @return the parameter at the specified index.
       * @exception ArrayIndexOutOfBoundsException
       * if <tt>index</tt> is negative or >= <tt>this.param_count()</tt>
       */
      public String param_at(int index)
      {
         return (String) params_.elementAt(index);
      }
      
      /**
       * Obtain All parameters as a String array
       */
      public String[] to_array()
      {
         String[] result = new String[params_.size()];
         for(int i = 0; i < result.length; ++i)
            result[i] = (String) params_.elementAt(i);

         return result;
      }
   }
   

   /** 
    * Initialize a new CommandLineParaser object.
    * @param argv A String array specifying the command line. Typically this 
    * value is passed directly from the args parameter of the program's main() 
    * method.
    * @param options0 Comma separated strings specifying "Options" with 0 parameters.
    * @param options1 Comma separated strings specifying "Options" with 1 parameter.
    * @param options2 Comma separated strings specifying "Options" with 2 parameter.
    */
   public CommandLine(String[] argv, String options0, String options1, 
      String options2) throws ParserError
   {
      this(argv, new ArgsDescriptions(options0, options1, options2));
   }      


   /** 
    * Initialize a new CommandLineParaser object.
    * @param argv A String array specifying the command line. Typically this 
    * value is passed directly from the args parameter of the program's main() 
    * method.
    * @param options0 Comma separated strings specifying "Options" with 0 parameters.
    * @param options1 Comma separated strings specifying "Options" with 1 parameter.
    */
   public CommandLine(String[] argv, String options0, String options1) 
      throws ParserError
   {
      this(argv, options0, options1, null);
   }      

   /** 
    * Initialize a new CommandLineParaser object.
    * @param argv A String array specifying the command line. Typically this 
    * value is passed directly from the args parameter of the program's main() 
    * method.
    * @param options0 Comma separated strings specifying "Options" with 0 parameters.
    */
   public CommandLine(String[] argv, String options0) 
      throws ParserError
   {
      this(argv, options0, null);
   }      
   
   private CommandLine(String[] argv, ArgsDescriptions ad) 
      throws ParserError
   {
      ad_ = ad;
      argv_ = argv;
      
      parse(0);
   }
   
   private String arg_at(int index)
   {
      JimaMisc.ensure(index < argv_.length);
      return argv_[index];
   }

   private String param_at(String option, int index) throws ParserError
   {
      if(index >= argv_.length)
         throw new ParserError("Missing parameter for opttion " + option);
         
      return arg_at(index);
   }
   
   private void parse(int index) throws ParserError
   {
      Vector entries = new Vector();
      Entry default_entry = new Entry(null);
      
      while(true)
      {
         if(index >= argv_.length)
            break;
   
         String option = arg_at(index);
         Entry e = new Entry(option);
         index += 1;         
   
         int pc = ad_.param_count_of(option);
         if(pc < 0)
         {
            default_entry.add_param(option);
            continue;
         }
                     
         for(int i = 0; i < pc; ++i)
         {
            String param = param_at(option, index);
            index += 1;
            
            e.add_param(param);
         }         
         
         entries.add(e);
      }
      
      if(default_entry.param_count() > 0)
         entries.add(default_entry);
         
      entries_ = entries;               
   }
   
   /** 
    * Iterate over all entris extracted by the parser.
    * @return An Iterator to a collection of Entry objects.
    * @see Entry
    */
   public Iterator entries()
   {
      return entries_.iterator();
   }
   
   private Entry find_entry(String option)
   {
      JimaMisc.ensure(this.ad_.param_count_of(option) >= 0);
      
      for(int i = 0; i < entries_.size(); ++i)
      {
         Entry curr = (Entry) entries_.elementAt(i);
         if(curr.matches(option))
            return curr;
      }         
      
      return null;      
   }
   
   /**
    * Find an entry by its option.
    * @param option Specifies the requested entry's option. 
    * If null, the "null" entry is returned.
    * @param default_value Default param value
    * @return Corresponding Entry object. If the option is not on 
    * the command line, return an entry initialized with the specified 
    * default value.
    * @see Entry
    */
   public Entry get_optional(String option, String default_value)
   {
      Entry result = find_entry(option);
      if(result != null)
         return result;
      
      // ...Else:
      result = new Entry(option, default_value);
      return result;
   }
   
   /**
    * Check if an option was specified on the command line
    * @param option Specifies option 
    * @return true - the option was specified on the command line. 
    * Otherwise - false.
    */
   public boolean has(String option)
   {
      boolean result = (find_entry(option) != null);
      return result;
   }

   /**
    * Find an entry by its option.
    * @throws ParserError if the option was not specified at the command line.
    * @param option Specifies the requested entry's option. 
    * If null, the "null" entry is returned.
    * @return Corresponding Entry object.
    * @see Entry
    */
   public Entry get_required(String option) throws ParserError
   {
      Entry result = find_entry(option);
      if(result != null)
         return result;
         
      throw new ParserError("Required option " + option + " not specified");
   }   
}
