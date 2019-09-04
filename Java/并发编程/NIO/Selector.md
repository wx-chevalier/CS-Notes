# Selector

多路复用器的核心就是通过 Selector 来轮询其上的 Channel，当发现某个或者多个 Channel 处于就绪状态后，从阻塞状态返回就绪的 Channel 的选择键集合，进行 IO 操作。

Java 的 NIO 为 Reactor 模式提供了实现的基础机制，它的 Selector 当发现某个 Channel 有数据时，会通过 SelectorKey 来告知我们，在此我们实现事件和 Handler 的绑定。

- Reactor 负责响应 IO 事件，一旦发生，广播发送给相应的 Handler 去处理,这类似于 AWT 的 thread
- Handler 是负责非堵塞行为，类似于 AWT ActionListeners；同时负责将 handlers 与 event 事件绑定，类似于 AWT addActionListener

![](https://i.postimg.cc/1tBYv4zR/image.png)

# 链接

- https://www.baeldung.com/java-nio-selector
