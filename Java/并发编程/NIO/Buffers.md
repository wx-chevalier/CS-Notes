# Buffers

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

# IntBuffer

```java
// 分配新的 int 缓冲区，参数为缓冲区容量
// 新缓冲区的当前位置将为零，其界限(限制位置)将为其容量。它将具有一个底层实现数组，其数组偏移量将为零。
IntBuffer buffer = IntBuffer.allocate(8);

for (int i = 0; i < buffer.capacity(); ++i) {
    int j = 2 * (i + 1);
    // 将给定整数写入此缓冲区的当前位置，当前位置递增
    buffer.put(j);
}

// 重设此缓冲区，将限制设置为当前位置，然后将当前位置设置为 0
buffer.flip();

// 查看在当前位置和限制位置之间是否有元素
while (buffer.hasRemaining()) {
    // 读取此缓冲区当前位置的整数，然后当前位置递增
    int j = buffer.get();
    System.out.print(j + "  ");
}
```
