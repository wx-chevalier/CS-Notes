# Channel

Channel 是一个通道，可以通过它读取和写入数据，它就像自来水管一样，网络数据通过 Channel 读取和写入。通道与流的不同之处在于通道是双向的，流只是在一个方向上移动(InputStream 或 OutputStream 的子类)，而且通道可以用于读、写或者同时用于读写。

- 一个 Channel 可以读和写，而一个流一般只能读或者写
- Channel 可以异步(asynchronously)的读和写
- Channel 总是需要一个 Buffer，不管是读到 Buffer 还是从 Buffer 写到 Channel

因为 Channel 是全双工的，所以它可以比流更好地映射底层操作系统的 API。在 UNIX 网络编程模型中，底层操作系统的通道都是全双工的，同时支持读写操作。NIO 中包含了几个常见的 Channel ，这几个 channles 包含了咱们开发中使用率较高的 文件 IO，UDP+TCP 网络 IO。

- FileChannel 读取数据或者写入数据到文件中
- DatagramChannel 读写数据通过 UDP 协议
- SocketChannel 读写数据通过 TCP 协议
- ServerSocketChannel 提供 TCP 连接的监听，每个进入的连接都会创建一个 SocketChannel。

![](http://hi.csdn.net/attachment/201107/17/0_1310888420STkI.gif)
