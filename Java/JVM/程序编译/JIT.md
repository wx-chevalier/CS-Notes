[![java](https://user-images.githubusercontent.com/5803001/43033009-f03a83a4-8cf4-11e8-9822-2059a0e3a5a8.jpg)](https://github.com/wx-chevalier/ProgrammingLanguage-Series)

# JIT

JIT 是 Just-In-Time Compiliation 的缩写，中文为即时编译。就是 JAVA 在运行过程中，如果有些动态极度频繁的被执行或者不被执行，就会被自动编译成机器码，跳过其中的部分环节。Java 源码通过编译器转为平台无关的字节码（Bytecode）或 Java class 文件。在启动 Java 应用程序后，JVM 会在运行时加载编译后的类并通过 Java 解释器执行适当的语义计算。当开启 JIT 时，JVM 会分析 Java 应用程序的函数调用并且(达到内部一些阀值后)编译字节码为本地更高效的机器码，编译出的原生机器码被存储在非堆内存的代码缓存中。

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2016/7/4/JVM_JIT_interraction.png)

JIT 流程通常为最繁忙的函数调用提供更高的优先级。一旦函数调用被转为机器码，JVM 会直接执行而不是解释执行，上述过程会在程序运行过程中不断提高性能。通过这种方法，Hotspot 虚拟机将权衡下面两种时间消耗：将字节码编译成本地代码需要的额外时间和解释执行字节码消耗更多的时间。
