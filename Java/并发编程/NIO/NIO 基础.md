
# Java NonBlocking IO

Java NIO，被称为新 IO(New IO)，是 Java 1.4 引入的，用来替代 IO API的，它的基础是操作系统提供的I/O多路复用技术。。在JDK1.4推出Java NIO之前，基于Java的所有Socket通信都采用了同步阻塞模式(BIO)，这种一请求一应答的通信模型简化了上层的应用开发，但是在性能和可靠性方面却存在着巨大的瓶颈。因此，在很长的一段时间里，大型的应用服务器都采用C或者C++，因为它们可以直接使用操作系统提供的异步I/O能力。 一般来说BIO提供的都是面向流的I/O系统，系统一次一个字节地处理数据，一个输入流产生一个字节的数据，一个输出流消费一个字节的数据，面向流的I/O速度非常慢，而NIO是一个面向块的I/O系统，系统以块的方式处理处理，每一个操作在一步中产生或者消费一个数据库，按块处理要比按字节处理数据快的多。

# Basic Concepts
标准的 Java IO API ，你操作的对象是字节流(byte stream)或者字符流(character stream)，而 NIO，你操作的对象是 channels 和 buffers。数据总是从一个 channel 读到一个 buffer 上，或者从一个 buffer 写到 channel 上。Non-blocking 是非阻塞的意思。Java NIO 让你可以做非阻塞的 IO 操作。比如一个线程里，可以从一个 channel 读取数据到一个 buffer 上，在 channel 读取数据到 buffer 的时候，线程可以做其他的事情。当数据读取到 buffer 上后，线程可以继续处理它。
Java NIO 有三个核心组件(core components)：
- Channels
- Buffers
- Selectors

## Channels
Channel是一个通道，可以通过它读取和写入数据，它就像自来水管一样，网络数据通过Channel读取和写入。通道与流的不同之处在于通道是双向的，流只是在一个方向上移动(InputStream或OutputStream的子类)，而且通道可以用于读、写或者同时用于读写。
- 一个 Channel 可以读和写，而一个流一般只能读或者写
- Channel 可以异步(asynchronously)的读和写
- Channel 总是需要一个 Buffer，不管是读到 Buffer 还是从 Buffer 写到 Channel


因为Channel是全双工的，所以它可以比流更好地映射底层操作系统的API。在UNIX网络编程模型中，底层操作系统的通道都是全双工的，同时支持读写操作。NIO 中包含了几个常见的 Channel ，这几个 channles 包含了咱们开发中使用率较高的 文件 IO，UDP+TCP 网络 IO。

- FileChannel 读取数据或者写入数据到文件中
- DatagramChannel 读写数据通过 UDP 协议
- SocketChannel 读写数据通过 TCP 协议
- ServerSocketChannel 提供 TCP 连接的监听，每个进入的连接都会创建一个 SocketChannel。

![](http://hi.csdn.net/attachment/201107/17/0_1310888420STkI.gif)

### 从Channel中读取数据

在前面我们说过，任何时候读取数据，都不是直接从通道读取，而是从通道读取到缓冲区。所以使用NIO读取数据可以分为下面三个步骤： 
(1)从FileInputStream获取Channel 
(2)创建Buffer 
(3)将数据从Channel读取到Buffer中
下面是一个简单的使用NIO从文件中读取数据的例子：
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
### 向Channel中写入数据
使用NIO写入数据与读取数据的过程类似，同样数据不是直接写入通道，而是写入缓冲区，可以分为下面三个步骤：
(1)从FileInputStream获取Channel
(2)创建Buffer
(3)将数据从Channel写入到Buffer中

下面是一个简单的使用NIO向文件中写入数据的例子：
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
在NIO库中，所有数据都是通过缓冲处理的，在读取数据时，它是直接读到缓冲区中的；在写入数据时，写入到缓冲区中。任何时候访问NIO中的数据，都是通过缓冲区进行的。缓冲区实际上是一个容器对象，更直接的说，其实就是一个数组，在NIO库中，所有数据都是用缓冲区处理的。在读取数据时，它是直接读到缓冲区中的； 在写入数据时，它也是写入到缓冲区中的；任何时候访问 NIO 中的数据，都是将它放到缓冲区中。而在面向流I/O系统中，所有数据都是直接写入或者直接将数据读取到Stream对象中。
在NIO中，所有的缓冲区类型都继承于抽象类Buffer，最常用的就是ByteBuffer，对于Java中的基本类型，基本都有一个具体Buffer类型与之相对应，它们之间的继承关系如下图所示：
![](http://hi.csdn.net/attachment/201107/17/0_131088834611J5.gif)
- ByteBuffer
- CharBuffer
- DoubleBuffer
- FloatBuffer
- IntBuffer
- LongBuffer
- ShortBuffer
- MappedByteBuffer

一个简单的使用IntBuffer的例子：
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
> 有这么一种检查员，她工作在养鸡场，每天的工作就是不停的查看特定的鸡舍，如果有鸡生蛋了，或者需要喂食，或者有鸡生病了，就把相应信息记录下来，这样一来，鸡舍负责人想知道鸡舍的情况，只需要到检查员那里查询即可，当然，鸡舍负责人得事先告知检查员去查询哪些鸡舍。

多路复用器Selector是Java NIO编程的基础。Selector会不断地轮询注册在其上的Channel，如果某个Channel上面有新的TCP连接接入、读和写事件，这个Channel就处于就绪状态，会被Selector轮询出来，然后通过SelectionKey可以获取就绪Channel的集合，进行后续的I/O操作。
Java的NIO为reactor模式提供了实现的基础机制，它的Selector当发现某个channel有数据时，会通过SelectorKey来告知我们，在此我们实现事件和handler的绑定。 1.Reactor 负责响应IO事件，一旦发生，广播发送给相应的Handler去处理,这类似于AWT的thread 2.Handler 是负责非堵塞行为，类似于AWT ActionListeners；同时负责将handlers与event事件绑定，类似于AWT addActionListener
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

# Implementation
