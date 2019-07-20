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

# 数据读取

在前面我们说过，任何时候读取数据，都不是直接从通道读取，而是从通道读取到缓冲区。所以使用 NIO 读取数据可以分为下面三个步骤:

- 从 FileInputStream 获取 Channel
- 创建 Buffer
- 将数据从 Channel 读取到 Buffer 中

下面是一个简单的使用 NIO 从文件中读取数据的例子：

```java
FileInputStream fin = new FileInputStream("test");
// 获取通道
FileChannel fc = fin.getChannel();
// 创建缓冲区
ByteBuffer buffer = ByteBuffer.allocate(1024);
// 读取数据到缓冲区
fc.read(buffer);
buffer.flip();
while (buffer.remaining()>0) {
    byte b = buffer.get();
    System.out.print(((char)b));
}

fin.close();
```

我们也可以使用 RandomAccessFile 来随机读取文件内容：

```java
RandomAccessFile aFile = new RandomAccessFile("data/nio-data.txt", "rw");
FileChannel inChannel = aFile.getChannel();
ByteBuffer buf = ByteBuffer.allocate(48);
int bytesRead = inChannel.read(buf);
while (bytesRead != -1) {
  System.out.println("Read " + bytesRead);
  buf.flip();
  while(buf.hasRemaining()){
      System.out.print((char) buf.get());
  }
  buf.clear();
  bytesRead = inChannel.read(buf);
}
aFile.close();
```

# 数据写入

使用 NIO 写入数据与读取数据的过程类似，同样数据不是直接写入通道，而是写入缓冲区，可以分为下面三个步骤：

- 从 FileInputStream 获取 Channel
- 创建 Buffer
- 将数据从 Channel 写入到 Buffer 中

下面是一个简单的使用 NIO 向文件中写入数据的例子：

```java
static private final byte message[] = { 83, 111, 109, 101, 32, 98, 121, 116, 101, 115, 46 };

static public void main( String args[] ) throws Exception {
    FileOutputStream fout = new FileOutputStream("/test");
    FileChannel fc = fout.getChannel();
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    for (int i=0; i<message.length; ++i) {
        buffer.put( message[i] );
    }
    buffer.flip();
    fc.write(buffer);

    fout.close();
}
```
