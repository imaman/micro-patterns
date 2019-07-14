# Micro Patterns

*Micro patterns* are well-defined code structures that can be automatically detected by a tool. They were first described in an [OOPSLA 2005](http://www.oopsla.org/2005/ShowPage.do?id=Home) paper titled "[Micro Patterns in Java Code](http://www.cs.technion.ac.il/~imaman/stuff/ip-oopsla05-c.pdf)" (by Yossi Gil and Itay Maman). The code here is the original micro-patterns detector used for the research work described in that paper. 


## Prerequisite
Your local machine needs to have the following installed:

- Java
- [Maven](https://maven.apache.org/install.html)


(yes, the former is implied by the latter...)


## Usage
Run the mp.sh script to invoke the micro-patterns detector on a JAR of your choice. Example:


```
$ mp.sh "../../../tools/apache-maven-3.6.1/lib/jansi-1.17.1.jar"
```

This will result in an output such as:
```
Found 20906 classes (program + library) in 1.71 seconds [12226 classes/sec]
Checked 54 program classes in 0.09 seconds [600 classes/sec]

            Useless   0%
                Box   7%
       Compound Box  13%
            Sampler   0%
             Canopy   4%
          Immutable  22%
        Implementor   2%
       Pseudo Class   0%
               Pool  11%
Restricted Creation  17%
          Overrider  17%
               Sink  19%
          Stateless  13%
       Common State   6%
            Outline   0%
   Function Pointer   2%
    Function Object   9%
             Joiner   0%
         Designator   0%
             Record   0%
           Taxonomy   2%
           PureType  11%
     Augmented Type   0%
           Extender   4%
       Data Manager   0%
              Trait   0%
         Cobol Like   2%
      State Machine   7%
          Recursive   0%
       Limited Self  28%
                     ---
           Coverage  83%
```





