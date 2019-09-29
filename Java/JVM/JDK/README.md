# JDK

而 Android 中使用的 Dalvik 虚拟机与 JVM 的区别在于，Java 虚拟机基于栈，基于栈的机器必须使用指令来载入和操作栈上数据，所需指令更多更多。而 dalvik 虚拟机是基于寄存器的：Java 虚拟机运行的是 java 字节码，Java 类会被编译成一个或多个字节码.class 文件，打包到.jar 文件中，Java 虚拟机从相应的.class 文件和.jar 文件中获取相应的字节码。Dalvik 和 Java 之间的另外一大区别就是运行环境——Dalvik 经过优化，允许在有限的内存中同时运行多个虚拟机的实例。
