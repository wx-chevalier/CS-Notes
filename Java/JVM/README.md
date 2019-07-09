# JVM 架构机制与性能调优

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2016/8/2/b41e6ae786440fa12cb33ea3a5bc70ce-1024x607.png)

# JDK

Oracle JDK 与 Open JDK 在程序上是非常接近的，两者共用了大量相同的代码，所以我们编译的 OpenJDK，基本上可以认为性能、功能和执行逻辑上都和官方的 Oracle JDK 是一致的。

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2016/8/2/OpenOracleJDK.png)

OpenJDK 是 Sun 在 2006 年末把 Java 开源而形成的项目，这里的“开源”是通常意义上的源码开放形式，即源码是可被复用的，例如 IcedTea、UltraViolet 都是从 OpenJDK 源码衍生出的发行版。但如果仅从“开源”字面意义(开放可阅读的源码)上看，其实 Sun 自 JDK 1.5 之 后就开始以 Java Research License(JRL)的形式公布过 Java 源码，主要用于研究人员阅读(JRL 许可证的开放源码至 JDK 1.6 Update 23 为止)。把这些 JRL 许可证形式的 Sun/OracleJDK 源码和对应版本的 OpenJDK 源码进行比较，发现除了文件头的版权注释之外，其余代码 基本上都是相同的，只有字体渲染部分存在一点差异，Oracle JDK 采用了商业实现，而 OpenJDK 使用的是开源的 FreeType。当然，“相同”是建立在两者共有的组件基础上的，Oracle JDK 中还会存在一些 Open JDK 没有的、商用闭源的功能，例如从 JRockit 移植改造而来的 Java Flight Recorder。预计以后 JRockit 的 MissionControl 移植到 HotSpot 之后，也会以 Oracle JDK 专有、闭源的形式提供。

而 Android 中使用的 Dalvik 虚拟机与 JVM 的区别在于，Java 虚拟机基于栈，基于栈的机器必须使用指令来载入和操作栈上数据，所需指令更多更多。而 dalvik 虚拟机是基于寄存器的：Java 虚拟机运行的是 java 字节码，Java 类会被编译成一个或多个字节码.class 文件，打包到.jar 文件中，Java 虚拟机从相应的.class 文件和.jar 文件中获取相应的字节码。Dalvik 和 Java 之间的另外一大区别就是运行环境——Dalvik 经过优化，允许在有限的内存中同时运行多个虚拟机的实例。

## HotSpot

SUN 的 JDK 版本从 1.3.1 开始运用 HotSpot 虚拟机， 2006 年底开源，主要使用 C++实现，JNI 接口部分用 C 实现。HotSpot 是较新的 Java 虚拟机，用来代替 JIT(Just in Time)，可以大大提高 Java 运行的性能。 Java 原先是把源代码编译为字节码在虚拟机执行，这样执行速度较慢。而 HotSpot 将常用的部分代码编译为本地(原生，native)代码，这样显着提高了性能。 HotSpot JVM 参数可以分为规则参数(standard options)和非规则参数(non-standard options)。规则参数相对稳定，在 JDK 未来的版本里不会有太大的改动，非规则参数则有因升级 JDK 而改动的可能。

HotSpot 包括一个解释器和两个编译器(client 和 server，二选一的)，解释与编译混合执行模式，默认启动解释执行。HotSpot 采取的是动态编译(Compile During Runtime/Dynamic Compilation)机制，即对 bytecode 的编译不是在程序运行前编译的，而是在程序运行过程中编译的。HotSpot 里运行着一个监视器(Profile Monitor)，用来监视程序的运行状况。Java 字节码(class 文件)是以解释的方式被加载到虚拟机中(默认启动时解释执行)。 程序运行过程中，那一部分运用频率大，那些对程序的性 能影响重要。对程序运行效率影响大的代码，称为热点(hotspot)，HotSpot 会把这些热点动态地编译成机器码(native code)，同时对机器码进行优化，从而提高运行效率。对那些较少运行的代码，HotSpot 就不会把他们编译。HotSpot 对字节码有三层处理：不编译(字节码加载到虚拟机中时的状态。也就是当虚拟机执行的时候再编译)，编译(把字节码编译成本地代码。虚拟机执行的时候已经编译好了，不要再编译了)，编译并优化(不但把字节码编译成本地代码，而且还进行了优化)。

动态编译器也在许多方面比静态编译器优越。静态编译器通常很难准确预知程序运行过程中究竟什么部分最需要优化。函数调用都是很浪费系统时间的，因为有许多进栈出栈操作。因此有一种优化办法，就是把原来的函数调用，通过编译器的编译，改成非函数调用，把函数代码直接嵌到调用出，变成顺序执行。面向对象的语言支持多态，静态编译无效确定程序调用哪个方法，因为多态是在程序运行中确定调用哪个方法。
