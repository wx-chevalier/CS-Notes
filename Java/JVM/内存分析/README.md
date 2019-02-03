# JVM 内存分配与回收分析

# 基础概念

尽管 JVM 提供了自动内存管理的机制，试图降低程序员的开发门槛，确实也实现了这一目标，在日常开发中，我们一般都不需要关心对象的内存释放。JVM 大部分都是使用 trace 算法来判断一个对象是否该被回收，那么 JVM 只能回收那些从 gc roots 不可达的对象。

如果我们在使用某些大的对象、集合对象或者一些三方包里的资源，忘记及时释放资源的话，还是会造成 JVM 的内存泄漏或内存浪费的问题。

## Heap Dump

Heap Dump 是 Java 进程在某个时刻的内存快照，主要包含当生成快照时堆中的 Java 对象和类的信息；不同 JVM 的实现的 Heap Dump 的文件格式可能不同，进而存储的数据也可能不同，主要分为如下几类：

- 对象信息：类名、属性、基础类型和引用类型

- 类信息：类加载器、类名称、超类、静态属性

- GC Roots: JVM 中的一个定义，进行垃圾收集时，要遍历可达对象的起点节点的集合

- 线程栈和局部变量：快照生成时候的线程调用栈，和每个栈上的局部变量

Heap Dump 中没有包含对象的分配信息，因此它不能用来分析这种问题：一个对象什么时候被创建、一个对象时被谁创建的。

## Garbage Collection Roots

gc roots 中的对象，是指那些可以从堆外访问到的对象的集合。如果一个对象符合下面这些场景中的一个，就可以被认为是 gc roots 中的节点：

- System Class: 由 Bootstrap Classloader 加载的类，例如 rt.jar，里面的类的包名都是 `java.util.*` 开头的。

* Thread Block/Thread: 正在存活的线程，被当前活跃的线程锁引用的对象。

* Busy Monitor: 调用了 wait()、notify()或 synchronized 关键字修饰的代码——例如 synchronized(object)或 synchronized 方法。

- Java Local：局部变量。例如函数的输入参数、正在运行的线程栈里创建的对象。

- JNI Local/Global：native 代码中的局部变量/全局变量，例如用户编写的 JNI 代码或 JVM 内部代码。

- Native Stack: native 代码的输入或输出参数，例如用户定义的 JNI 代码或 JVM 的内部代码。在文件/网络 IO 方法或反射方法的参数。

- Finalizable/Unfinalized: 在 finalize 队列中等待它的 finalizer 对象运行的对象；重载了 finalize 方法，但是还没有进入 finalize 队列中的对象。

- Java Stack Frame：Java 栈帧，用于存放局部变量。只在 dump 文件被解析的时候会将 java stack frame 视为对象。

- Unknown: 没有 Root 类型的对象。有些 dump 文件（例如 IBM 的 Portable Heap Dump）没有 Root 信息。

## Shallow 与 Retained Heap

Shallow heap 是一个对象本身占用的堆内存大小。一个对象中，每个引用占用 8 或 64 位，Integer 占用 4 字节，Long 占用 8 字节等等。Retained set，对于某个对象 X 来说，它的 Retained set 指的是——如果 X 被垃圾收集器回收了，那么这个集合中的对象都会被回收，同理，如果 X 没有被垃圾收集器回收，那么这个集合中的对象都不会被回收。
Retained Heap，对象 X 的 Retained Heap 指的时候它的 Retained set 中的所有对象的 Shallow Size 的和，换句话说，Retained Heap 指的是对象 X 的保留内存大小，即由于它的存活导致多大的内存也没有被回收。而某个 Retained set 对应的对象可能有多个，这些对象就构成了所谓的 Leading Set。

A 和 B 是 gc roots 中的节点（方法参数、局部变量，或者调用了 wait()、notify()或 synchronized()的对象）等等。可以看出，E 的存在，会导致 G 无法被回收，因此 E 的 Retained set 是 E 和 G；C 的存在，会导致 E、D、F、G、H 都无法被回收，因此 C 的 Retined set 是 C、E、D、F、G、H；A 和 B 的存在，会导致 C、E、D、F、G、H 都无法被回收，因此 A 和 B 的 Retained set 是 A、B、C、E、D、F、G、H。

![](https://mmbiz.qpic.cn/mmbiz_png/4AG6tic68AGaP8ElL7YbHfYIXWkQotTX1KwPnRCacrYXPFtdA5HEMn8WeYWHGljgT5de60ctsVK6cPuuaasTZaA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

## Dominator Tree

MAT 根据堆上的对象引用关系构建了支配树（Dominator Tree），通过支配树可以很方便得识别出哪些对象占用了大量的内存，并可以看到它们之间的依赖关系。如果在对象图中，从 gc root 或者 x 上游的一个节点开始遍历，x 是 y 的必经节点，那么就可以说 x 支配了 y（dominate）。如果在对象图中，x 支配的所有对象中，y 的距离最近，那么就可以说 x 直接支配（immediate dominate）y。

支配树是基于对象的引用关系图建立的，在支配树中每个节点都是它的子节点的直接支配节点。基于支配树可以很清楚得看到对象之间的依赖关系。下图中，x 节点的子树就是所有被 x 支配的节点集合，也正式 x 的 retained set；如果 x 是 y 的直接支配节点，那么 x 的支配节点也可以支配 y；支配树中的边跟对象引用图中的引用关系并不是一一对应的。

![](https://mmbiz.qpic.cn/mmbiz_png/4AG6tic68AGaP8ElL7YbHfYIXWkQotTX1aibXxeugr62WkInfljcOB1BuzXzYXcWc0q7HBicicib7wn1bKagkBib8TQw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)
