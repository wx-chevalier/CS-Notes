# Selector

多路复用器的核心就是通过 Selector 来轮询其上的 Channel，当发现某个或者多个 Channel 处于就绪状态后，从阻塞状态返回就绪的 Channel 的选择键集合，进行 I/O 操作。由于多路复用器是 NIO 实现非阻塞的关键，它又是主要通过 Selector 实现的。
