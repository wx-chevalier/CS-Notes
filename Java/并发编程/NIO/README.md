# NIO

Java NIO，被称为新 IO(New IO)，是 Java 1.4 引入的，用来替代 IO API 的，它的基础是操作系统提供的 IO 多路复用技术。在早期的 JDK1.4 和 1.5update10 版本之前，JDK 的 Selector 基于 select/poll 模型实现，它是基于 I/O 复用技术的非阻塞 I/O，不是异步 I/O。在 JDK1.5 update10 和 Linux2 .6 以上版本，Sun 优化了 Selector 的实现，它在底层使用了 epoll 替换了 select/poll，但是上层的 API 并没有变化，所以 NIO 还是一种同步非阻塞 I/O。在 JDK1.7 提供的 AIO 新增了异步的套接字通道，它是真正的异步 I/O,在异步 I/O 操作的时候可以传递信号变量，当操作完成之后会回调相关的方法，异步 I/O 也称为 AIO。

对于网络 IO 相关的基础知识可以参考 [Linux 网络 IO](https://ngte-infras.gitbook.io/i/?q=Linux网络IO) 以及[并发 IO](https://ngte-infras.gitbook.io/i/?q=并发IO) 的相关章节。

# 基础概念
