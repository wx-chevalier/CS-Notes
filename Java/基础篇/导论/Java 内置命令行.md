# javap

The javap command is called the Java “disassembler”* *because it takes apart class files and tells you what’s inside them. You won’t use this command often, but using it to find out how a particular Java statement works is fun, sometimes. You can also use it to find out what methods are available for a class if you don’t have the source code that was used to create the class.

Here is the general format:

```
javap filename [options]
```

The following is typical of the information you get when you run the javap command:

```
C:javasamples>javap HelloApp
Compiled from "HelloApp.java"
public class HelloApp extends java.lang.Object{
    public HelloApp();
    public static void main(java.lang.String[]);
}
```

As you can see, the javap command indicates that the HelloApp class was compiled from the HelloApp.java file and that it consists of a HelloApp public class and a main public method.

You may want to use two options with the javap command. If you use the -c option, the javap command displays the actual Java bytecodes created by the compiler for the class. (*Java bytecode* is the executable program compiled from your Java source file.)

And if you use the -verbose option, the bytecodes — plus a ton of other fascinating information about the innards of the class — are displayed. Here’s the -c output for a class named HelloApp:

```
C:javasamples>javap HelloApp -c
Compiled from "HelloApp.java"
public class HelloApp extends java.lang.Object{
public HelloApp();
  Code:
   0:   aload_0
   1:   invokespecial   #1; //Method
   java/lang/Object."<init>":()V
   4:   return
public static void main(java.lang.String[]);
  Code:
   0:   getstatic       #2; //Field 
   java/lang/System.out:Ljava/io/PrintStream;
   3:   ldc     #3; //String Hello, World!
   5:   invokevirtual   #4; //Method 
   java/io/PrintStream.println:(Ljava/lang/String;)V
   8:   return
}
```