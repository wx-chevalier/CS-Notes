# Java NonBlocking IO

# Basic Concepts

## Channels

### 从 Channel 中读取数据

在前面我们说过，任何时候读取数据，都不是直接从通道读取，而是从通道读取到缓冲区。所以使用 NIO 读取数据可以分为下面三个步骤:
(1)从 FileInputStream 获取 Channel
(2)创建 Buffer
(3)将数据从 Channel 读取到 Buffer 中
下面是一个简单的使用 NIO 从文件中读取数据的例子：

```java
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class Program {
    static public void main( String args[] ) throws Exception {
        FileInputStream fin = new FileInputStream("c:\\test.txt");

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
    }
}
```

### 向 Channel 中写入数据

使用 NIO 写入数据与读取数据的过程类似，同样数据不是直接写入通道，而是写入缓冲区，可以分为下面三个步骤：
(1)从 FileInputStream 获取 Channel
(2)创建 Buffer
(3)将数据从 Channel 写入到 Buffer 中

下面是一个简单的使用 NIO 向文件中写入数据的例子：

```
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class Program {
    static private final byte message[] = { 83, 111, 109, 101, 32,
        98, 121, 116, 101, 115, 46 };

    static public void main( String args[] ) throws Exception {
        FileOutputStream fout = new FileOutputStream( "c:\\test.txt" );

        FileChannel fc = fout.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate( 1024 );

        for (int i=0; i<message.length; ++i) {
            buffer.put( message[i] );
        }

        buffer.flip();

        fc.write( buffer );

        fout.close();
    }
}
```

## Buffers

在 NIO 库中，所有数据都是通过缓冲处理的，在读取数据时，它是直接读到缓冲区中的；在写入数据时，写入到缓冲区中。任何时候访问 NIO 中的数据，都是通过缓冲区进行的。缓冲区实际上是一个容器对象，更直接的说，其实就是一个数组，在 NIO 库中，所有数据都是用缓冲区处理的。在读取数据时，它是直接读到缓冲区中的； 在写入数据时，它也是写入到缓冲区中的；任何时候访问 NIO 中的数据，都是将它放到缓冲区中。而在面向流 IO 系统中，所有数据都是直接写入或者直接将数据读取到 Stream 对象中。
在 NIO 中，所有的缓冲区类型都继承于抽象类 Buffer，最常用的就是 ByteBuffer，对于 Java 中的基本类型，基本都有一个具体 Buffer 类型与之相对应，它们之间的继承关系如下图所示：
![](http://hi.csdn.net/attachment/201107/17/0_131088834611J5.gif)

- ByteBuffer
- CharBuffer
- DoubleBuffer
- FloatBuffer
- IntBuffer
- LongBuffer
- ShortBuffer
- MappedByteBuffer

一个简单的使用 IntBuffer 的例子：

```
    import java.nio.IntBuffer;

    public class TestIntBuffer {
        public static void main(String[] args) {
            // 分配新的int缓冲区，参数为缓冲区容量
            // 新缓冲区的当前位置将为零，其界限(限制位置)将为其容量。它将具有一个底层实现数组，其数组偏移量将为零。
            IntBuffer buffer = IntBuffer.allocate(8);

            for (int i = 0; i < buffer.capacity(); ++i) {
                int j = 2 * (i + 1);
                // 将给定整数写入此缓冲区的当前位置，当前位置递增
                buffer.put(j);
            }

            // 重设此缓冲区，将限制设置为当前位置，然后将当前位置设置为0
            buffer.flip();

            // 查看在当前位置和限制位置之间是否有元素
            while (buffer.hasRemaining()) {
                // 读取此缓冲区当前位置的整数，然后当前位置递增
                int j = buffer.get();
                System.out.print(j + "  ");
            }

        }

    }
```

## Selectors

多路复用器 Selector 是 Java NIO 编程的基础。Selector 会不断地轮询注册在其上的 Channel，如果某个 Channel 上面有新的 TCP 连接接入、读和写事件，这个 Channel 就处于就绪状态，会被 Selector 轮询出来，然后通过 SelectionKey 可以获取就绪 Channel 的集合，进行后续的 IO 操作。

Java 的 NIO 为 reactor 模式提供了实现的基础机制，它的 Selector 当发现某个 channel 有数据时，会通过 SelectorKey 来告知我们，在此我们实现事件和 handler 的绑定。 1.Reactor 负责响应 IO 事件，一旦发生，广播发送给相应的 Handler 去处理,这类似于 AWT 的 thread 2.Handler 是负责非堵塞行为，类似于 AWT ActionListeners；同时负责将 handlers 与 event 事件绑定，类似于 AWT addActionListener。

![](https://lukangping.gitbooks.io/java-nio/content/resources/nio.png)

# Built-in Channels

打开一个文件的连接，然后获取到一个 Channel，开辟一个 Buffer，从 channel 读取数据到 buffer，然后再从 Buffer 中获取到读取的数据。

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
