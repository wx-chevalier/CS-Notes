# Metaspace

![](http://karunsubramanian.com/wp-content/uploads/2014/07/Java8-heap.jpg)

# 方法区

方法区是所有线程共享的内存区域，用于存储已经被 JVM 加载的类信息、常量、静态变量等数据，一般来说，方法区属于持久代(关于持久代，会在 GC 部分详细介绍，除了持久代，还有新生代和旧生代)，也难怪 Java 规范将方法区描述为堆的一个逻辑部分，但是它不是堆。方法区的垃圾回收比较棘手，就算是 Sun 的 HotSpot VM 在这方面也没有做得多么完美。此处引入方法区中一个重要的概念：运行时常量池。主要用于存放在编译过程中产生的字面量(字面量简单理解就是常量)和引用。一般情况，常量的内存分配在编译期间就能确定，但不一定全是，有一些可能就是运行时也可将常量放入常量池中，如 String 类中有个 Native 方法 intern()。

方法区(method area)只是 JVM 规范中定义的一个概念，用于存储类信息、常量池、静态变量、JIT 编译后的代码等数据，具体放在哪里，不同的实现可以放在不同的地方。而永久代是 Hotspot 虚拟机特有的概念，是方法区的一种实现，别的 JVM 都没有这个东西。

在 Java 6 中，方法区中包含的数据，除了 JIT 编译生成的代码存放在 native memory 的 CodeCache 区域，其他都存放在永久代；
在 Java 7 中，Symbol 的存储从 PermGen 移动到了 native memory，并且把静态变量从 instanceKlass 末尾(位于 PermGen 内)移动到了 java.lang.Class 对象的末尾(位于普通 Java heap 内)；
在 Java 8 中，永久代被彻底移除，取而代之的是另一块与堆不相连的本地内存——元空间(Metaspace),‑XX:MaxPermSize 参数失去了意义，取而代之的是-XX:MaxMetaspaceSize。

方法区是一个线程共享的区域，它用于存储已被虚拟机加载的类信息、常量、静态变量。方法区是堆的逻辑组成部分，Hotspot 用永久代实现了方法区。方法 区还包含运行时常量池，用于存放编译时生成的各种字面量和符号引用，但是不要求常量一定是在编译时期产生的，运行期间也可以将新的常量放入池中，比如 String 的 intern()方法便是利用了这一特性。
方法区是所有线程共享的内存区域，用于存储已经被 JVM 加载的类信息、常量、静态变量等数据，一般来说，方法区属于持久代(关于持久代，会在 GC 部分详细介绍，除了持久代，还有新生代和旧生代)，也难怪 Java 规范将方法区描述为堆的一个逻辑部分，但是它不是堆。方法区的垃圾回收比较棘手，就算是 Sun 的 HotSpot VM 在这方面也没有做得多么完美。此处引入方法区中一个重要的概念：运行时常量池。主要用于存放在编译过程中产生的字面量(字面量简单理解就是常量)和引用。一般情况，常量的内存分配在编译期间就能确定，但不一定全是，有一些可能就是运行时也可将常量放入常量池中，如 String 类中有个 Native 方法 intern()<关于intern()的详细说明，请看另一篇文章：[http://blog.csdn.net/zhangerqing/article/details/8093919](http://blog.csdn.net/zhangerqing/article/details/8093919)>

此处补充一个在 JVM 内存管理之外的一个内存区：直接内存。在 JDK1.4 中新加入类 NIO 类，引入了一种基于通道与缓冲区的 IO 方式，它可以使用 Native 函数库直接分配堆外内存，即我们所说的直接内存，这样在某些场景中会提高程序的性能。
所有线程共享同一个方法区，因此访问方法区数据的和动态链接的进程必须线程安全。如果两个线程试图访问一个还未加载的类的字段或方法，必须只加载一次，而且两个线程必须等它加载完毕才能继续执行。

    Classloader 引用
    运行时常量池
        数值型常量
        字段引用
        方法引用
        属性
    字段数据
        针对每个字段的信息
            字段名
            类型
            修饰符
            属性(Attribute)
    方法数据
        每个方法
            方法名
            返回值类型
            参数类型(按顺序)
            修饰符
            属性
    方法代码
        每个方法
            字节码
            操作数栈大小
            局部变量大小
            局部变量表
            异常表
            每个异常处理器
            开始点
            结束点
            异常处理代码的程序计数器(PC)偏移量
            被捕获的异常类对应的常量池下标

# 运行时常量池

JVM 维护了一个按类型区分的常量池，一个类似于符号表的运行时数据结构。尽管它包含更多数据。Java 字节码需要数据。这个数据经常因为太大不能直接存储在字节码中，取而代之的是存储在常量池中，字节码包含这个常量池的引用。运行时常量池被用来上面介绍过的动态链接。

常量池中可以存储多种类型的数据：

- 数字型
- 字符串型
- 类引用型
- 域引用型
- 方法引用

示例代码如下：

```
Object foo = new Object();
```

写成字节码将是下面这样：

```
0:     new #2             // Class java/lang/Object
1:    dup
2:    invokespecial #3    // Method java/ lang/Object "&lt;init&gt;"( ) V
```

new 操作码的后面紧跟着操作数 #2 。这个操作数是常量池的一个索引，表示它指向常量池的第二个实体。第二个实体是一个类的引用，这个实体反过来引用了另一个在常量池中包含 UTF8 编码的字符串类名的实体(// Class java/lang/Object)。然后，这个符号引用被用来寻找 java.lang.Object 类。new 操作码创建一个类实例并初始化变量。新类实例的引用则被添加到操作数栈。dup 操作码创建一个操作数栈顶元素引用的额外拷贝。最后用 invokespecial 来调用第 2 行的实例初始化方法。操作码也包含一个指向常量池的引用。初始化方法把操作数栈出栈的顶部引用当做此方法的一个参数。最后这个新对象只有一个引用，这个对 象已经完成了创建及初始化。
如果你编译下面的类：

```
package org.jvminternals;
public class SimpleClass {

    public void sayHello() {
        System.out.println("Hello");
    }

}
```

生成的类文件常量池将是这个样子：

```
Constant pool:
   #1 = Methodref          #6.#17         //  java/lang/Object."&lt;init&gt;":()V
   #2 = Fieldref           #18.#19        //  java/lang/System.out:Ljava/io/PrintStream;
   #3 = String             #20            //  "Hello"
   #4 = Methodref          #21.#22        //  java/io/PrintStream.println:(Ljava/lang/String;)V
   #5 = Class              #23            //  org/jvminternals/SimpleClass
   #6 = Class              #24            //  java/lang/Object
   #7 = Utf8               &lt;init&gt;
   #8 = Utf8               ()V
   #9 = Utf8               Code
  #10 = Utf8               LineNumberTable
  #11 = Utf8               LocalVariableTable
  #12 = Utf8               this
  #13 = Utf8               Lorg/jvminternals/SimpleClass;
  #14 = Utf8               sayHello
  #15 = Utf8               SourceFile
  #16 = Utf8               SimpleClass.java
  #17 = NameAndType        #7:#8          //  "&lt;init&gt;":()V
  #18 = Class              #25            //  java/lang/System
  #19 = NameAndType        #26:#27        //  out:Ljava/io/PrintStream;
  #20 = Utf8               Hello
  #21 = Class              #28            //  java/io/PrintStream
  #22 = NameAndType        #29:#30        //  println:(Ljava/lang/String;)V
  #23 = Utf8               org/jvminternals/SimpleClass
  #24 = Utf8               java/lang/Object
  #25 = Utf8               java/lang/System
  #26 = Utf8               out
  #27 = Utf8               Ljava/io/PrintStream;
  #28 = Utf8               java/io/PrintStream
  #29 = Utf8               println
  #30 = Utf8               (Ljava/lang/String;)V
```

这个常量池包含了下面的类型：

## 方法信息

### 异常表

异常表像这样存储每个异常处理信息：

- 起始点(Start point)
- 结束点(End point)
- 异常处理代码的 PC 偏移量
- 被捕获异常的常量池索引

如果一个方法有定义 try-catch 或者 try-finally 异常处理器，那么就会创建一个异常表。它为每个异常处理器和 finally 代码块存储必要的信息，包括处理器覆盖的代码块区域和处理异常的类型。

当方法抛出异常时，JVM 会寻找匹配的异常处理器。如果没有找到，那么方法会立即结束并弹出当前栈帧，这个异常会被重新抛到调用这个方法的方法中(在新的栈帧中)。如果所有的栈帧都被弹出还没有找到匹配的异常处理器，那么这个线程就会终止。如果这个异常在最后一个非守护进程抛出(比如这个线程是主线程)，那么也有会导致 JVM 进程终止。

Finally 异常处理器匹配所有的异常类型，且不管什么异常抛出 finally 代码块都会执行。在这种情况下，当没有异常抛出时，finally 代码块还是会在方法最后执行。这种靠在代码 return 之前跳转到 finally 代码块来实现。

### 符号表

除了按类型来分的运行时常量池，Hotspot JVM 在永久代还包含一个符号表。这个符号表是一个哈希表，保存了符号指针到符号的映射关系(也就是 Hashtable<Symbol\*, Symbol>)，它拥有指向所有符号(包括在每个类运行时常量池中的符号)的指针。
引用计数被用来控制一个符号从符号表从移除的过程。比如当一个类被卸载时，它拥有的在常量池中所有符号的引用计数将减少。当符号表中的符号引用计数 为 0 时，符号表会认为这个符号不再被引用，将从符号表中卸载。符号表和后面介绍的字符串表都被保存在一个规范化的结构中，以便提高效率并保证每个实例只出现一 次。

## 字符串表

Java 语言规范要求相同的(即包含相同序列的 Unicode 指针序列)字符串字面量必须指向相同的 String 实例。除此之外，在一个字符串实例上调用 String.intern() 方法的返回引用必须与字符串是字面量时的一样。因此，下面的代码返回 true：

```
("j" + "v" + "m").intern() == "jvm"
```

Hotspot JVM 中 interned 字符串保存在字符串表中。字符串表是一个哈希表，保存着对象指针到符号的映射关系(也就是`Hashtable<oop, Symbol>`)，它被保存到永久代中。符号表和字符串表的实体都以规范的格式保存，保证每个实体都只出现一次。
当类加载时，字符串字面量被编译器自动 intern 并加入到符号表。除此之外，String 类的实例可以调用 String.intern() 显式地 intern。当调用 String.intern() 方法时，如果符号表已经包含了这个字符串，那么就会返回符号表里的这个引用，如果不是，那么这个字符串就被加入到字符串表中同时返回这个引用。

# 链接

- [Java 8: From PermGen to Metaspace](https://dzone.com/articles/java-8-permgen-metaspace)
