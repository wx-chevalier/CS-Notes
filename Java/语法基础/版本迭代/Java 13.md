# Java 13 版本介绍

# Dynamic CDS Archives

这一特性是在 JEP310：Application Class-Data Sharing 基础上扩展而来的，Dynamic CDS Archives 中的 CDS 指的就是 Class-Data Sharing。我们知道在同一个物理机／虚拟机上启动多个 JVM 时，如果每个虚拟机都单独装载自己需要的所有类，启动成本和内存占用是比较高的。所以 Java 团队引入了 CDS 的概念，通过把一些核心类在每个 JVM 间共享，每个 JVM 只需要装载自己的应用类，启动时间减少了，另外核心类是共享的，所以 JVM 的内存占用也减少了。

CDS 只能作用于 Boot Class Loader 加载的类，不能作用于 App Class Loader 或者自定义的 Class Loader 加载的类。在 Java 10 中，则将 CDS 扩展为 AppCDS，顾名思义，AppCDS 不止能够作用于 Boot Class Loader 了，App Class Loader 和自定义的 Class Loader 也都能够起作用，大大加大了 CDS 的适用范围。也就说开发自定义的类也可以装载给多个 JVM 共享了。

Java 10 中包含的 JEP310 的通过跨不同 Java 进程共享公共类元数据来减少了内存占用和改进了启动时间。但是，JEP310 中，使用 AppCDS 的过程还是比较复杂的，需要有三个步骤：

1、决定要 Dump 哪些 Class
2、将类的内存 Dump 到归档文件中
3、使用 Dump 出来的归档文件加快应用启动速度

这一次的 JDK 13 中的 JEP 350 ，在 JEP310 的基础上，又做了一些扩展。允许在 Java 应用程序执行结束时动态归档类，归档类将包括默认的基础层 CDS（class data-sharing）存档中不存在的所有已加载的应用程序类和库类。也就是说，在 Java 13 中再使用 AppCDS 的时候，就不在需要这么复杂了。

# ZGC: Uncommit Unused Memory

JVM 的 GC 释放的内存会还给操作系统吗？GC 后的内存如何处置，其实是取决于不同的垃圾回收器的。因为把内存还给 OS，意味着要调整 JVM 的堆大小，这个过程是比较耗费资源的。

在 JDK 11 中，Java 引入了 ZGC，这是一款可伸缩的低延迟垃圾收集器，但是当时只是实验性的。并且，ZGC 释放的内存是不会还给操作系统的。而在 Java 13 中，JEP 351 再次对 ZGC 做了增强，本次 ZGC 可以将未使用的堆内存返回给操作系统。之所以引入这个特性，是因为如今有很多场景中内存是比较昂贵的资源，在以下情况中，将内存还给操作系统还是很有必要的：

1、那些需要根据使用量付费的容器
2、应用程序可能长时间处于空闲状态并与许多其他应用程序共享或竞争资源的环境。
3、应用程序在执行期间可能有非常不同的堆空间需求。例如，启动期间所需的堆可能大于稍后在稳定状态执行期间所需的堆。

# Reimplement the Legacy Socket API

使用易于维护和调试的更简单、更现代的实现替换 java.net.Socket 和 java.net.ServerSocket API。java.net.Socket 和 java.net.ServerSocket 的实现非常古老，这个 JEP 为它们引入了一个现代的实现。现代实现是 Java 13 中的默认实现，但是旧的实现还没有删除，可以通过设置系统属性 jdk.net.usePlainSocketImpl 来使用它们。

运行一个实例化 Socket 和 ServerSocket 的类将显示这个调试输出。这是默认的(新的):

```sh
java -XX:+TraceClassLoading JEP353  | grep Socket
[0.033s][info   ][class,load] java.net.Socket source: jrt:/java.base
[0.035s][info   ][class,load] java.net.SocketOptions source: jrt:/java.base
[0.035s][info   ][class,load] java.net.SocketImpl source: jrt:/java.base
[0.039s][info   ][class,load] java.net.SocketImpl$$Lambda$1/0x0000000800b50840 source: java.net.SocketImpl
[0.042s][info   ][class,load] sun.net.PlatformSocketImpl source: jrt:/java.base
[0.042s][info   ][class,load] sun.nio.ch.NioSocketImpl source: jrt:/java.base
[0.043s][info   ][class,load] sun.nio.ch.SocketDispatcher source: jrt:/java.base
[0.044s][info   ][class,load] java.net.DelegatingSocketImpl source: jrt:/java.base
[0.044s][info   ][class,load] java.net.SocksSocketImpl source: jrt:/java.base
[0.044s][info   ][class,load] java.net.ServerSocket source: jrt:/java.base
[0.045s][info   ][class,load] jdk.internal.access.JavaNetSocketAccess source: jrt:/java.base
[0.045s][info   ][class,load] java.net.ServerSocket$1 source: jrt:/java.base
```

上面输出的 sun.nio.ch.NioSocketImpl 就是新提供的实现。如果使用旧的实现也是可以的（指定参数 jdk.net.usePlainSocketImpl）：

```sh
$ java -Djdk.net.usePlainSocketImpl -XX:+TraceClassLoading JEP353  | grep Socket
[0.037s][info   ][class,load] java.net.Socket source: jrt:/java.base
[0.039s][info   ][class,load] java.net.SocketOptions source: jrt:/java.base
[0.039s][info   ][class,load] java.net.SocketImpl source: jrt:/java.base
[0.043s][info   ][class,load] java.net.SocketImpl$$Lambda$1/0x0000000800b50840 source: java.net.SocketImpl
[0.046s][info   ][class,load] sun.net.PlatformSocketImpl source: jrt:/java.base
[0.047s][info   ][class,load] java.net.AbstractPlainSocketImpl source: jrt:/java.base
[0.047s][info   ][class,load] java.net.PlainSocketImpl source: jrt:/java.base
[0.047s][info   ][class,load] java.net.AbstractPlainSocketImpl$1 source: jrt:/java.base
[0.047s][info   ][class,load] sun.net.ext.ExtendedSocketOptions source: jrt:/java.base
[0.047s][info   ][class,load] jdk.net.ExtendedSocketOptions source: jrt:/jdk.net
[0.047s][info   ][class,load] java.net.SocketOption source: jrt:/java.base
[0.047s][info   ][class,load] jdk.net.ExtendedSocketOptions$ExtSocketOption source: jrt:/jdk.net
[0.047s][info   ][class,load] jdk.net.SocketFlow source: jrt:/jdk.net
[0.047s][info   ][class,load] jdk.net.ExtendedSocketOptions$PlatformSocketOptions source: jrt:/jdk.net
[0.047s][info   ][class,load] jdk.net.ExtendedSocketOptions$PlatformSocketOptions$1 source: jrt:/jdk.net
[0.048s][info   ][class,load] jdk.net.LinuxSocketOptions source: jrt:/jdk.net
[0.048s][info   ][class,load] jdk.net.LinuxSocketOptions$$Lambda$2/0x0000000800b51040 source: jdk.net.LinuxSocketOptions
[0.049s][info   ][class,load] jdk.net.ExtendedSocketOptions$1 source: jrt:/jdk.net
[0.049s][info   ][class,load] java.net.StandardSocketOptions source: jrt:/java.base
[0.049s][info   ][class,load] java.net.StandardSocketOptions$StdSocketOption source: jrt:/java.base
[0.051s][info   ][class,load] sun.net.ext.ExtendedSocketOptions$$Lambda$3/0x0000000800b51440 source: sun.net.ext.ExtendedSocketOptions
[0.057s][info   ][class,load] java.net.DelegatingSocketImpl source: jrt:/java.base
[0.057s][info   ][class,load] java.net.SocksSocketImpl source: jrt:/java.base
[0.058s][info   ][class,load] java.net.ServerSocket source: jrt:/java.base
[0.058s][info   ][class,load] jdk.internal.access.JavaNetSocketAccess source: jrt:/java.base
[0.058s][info   ][class,load] java.net.ServerSocket$1 source: jrt:/java.base
```

上面的结果中，旧的实现 java.net.PlainSocketImpl 被用到了。

# Switch Expressions (Preview)

在 JDK 12 中引入了 Switch 表达式作为预览特性。JEP 354 修改了这个特性，它引入了 yield 语句，用于返回值。这意味着，switch 表达式(返回值)应该使用 yield, switch 语句(不返回值)应该使用 break。在以前，我们想要在 switch 中返回内容，还是比较麻烦的，一般语法如下：

```java
int i;
switch (x) {
    case "1":
        i=1;
        break;
    case "2":
        i=2;
        break;
    default:
        i = x.length();
        break;
}
```

在 JDK13 中使用以下语法：

```java
int i = switch (x) {
    case "1" -> 1;
    case "2" -> 2;
    default -> {
        int len = args[1].length();
        yield len;
    }
};

// 或者
int i = switch (x) {
    case "1": yield 1;
    case "2": yield 2;
    default: {
        int len = args[1].length();
        yield len;
    }
};
```

在这之后，switch 中就多了一个关键字用于跳出 switch 块了，那就是 yield，他用于返回一个值。和 return 的区别在于：return 会直接跳出当前循环或者方法，而 yield 只会跳出当前 switch 块。

# Text Blocks (Preview)

在 JDK 12 中引入了 Raw String Literals 特性，但在发布之前就放弃了。这个 JEP 在引入多行字符串文字（text block）在意义上是类似的。Text Blocks，文本块，是一个多行字符串文字，它避免了对大多数转义序列的需要，以可预测的方式自动格式化字符串，并在需要时让开发人员控制格式。

我们以前从外部 copy 一段文本串到 Java 中，会被自动转义，如有一段以下字符串：

```xml
<html>
  <body>
      <p>Hello, world</p>
  </body>
</html>
```

将其复制到 Java 的字符串中，会展示成以下内容：

```java
"<html>\n" +
"    <body>\n" +
"        <p>Hello, world</p>\n" +
"    </body>\n" +
"</html>\n";
```

即被自动进行了转义，这样的字符串看起来不是很直观，在 JDK 13 中，就可以使用以下语法了：

```java
"""
<html>
  <body>
      <p>Hello, world</p>
  </body>
</html>
""";
```

使用“”“作为文本块的开始符合结束符，在其中就可以放置多行的字符串，不需要进行任何转义。看起来就十分清爽了。如常见的 SQL 语句：

```java
String query = """
    SELECT `EMP_ID`, `LAST_NAME` FROM `EMPLOYEE_TB`
    WHERE `CITY` = 'INDIANAPOLIS'
    ORDER BY `EMP_ID`, `LAST_NAME`;
""";
```
