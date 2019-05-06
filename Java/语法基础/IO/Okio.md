# Okio

Okio 是 java.io 和 java.nio 的补充，使用它更容易访问、存储和处理数据。

![](http://s3.51cto.com/wyfs02/M01/5B/90/wKiom1ULwkCi7uJWAAPh6stvER4577.jpg)

Okio 中有几个常用的概念：

- ByteString：不可变的字节序列，类似 String 类，使用它更容易处理二进制数据。
- Buffer：可变的字节序列，类似 ArrayList。
- Source：类似 InputStream(输入流)
- Sink：类似 OutputStream(输出流)

# Source

Source 相当于输入流(InputStream)。把硬盘中的数据输入到内存中。

![](http://s3.51cto.com/wyfs02/M01/5B/CF/wKiom1UTcZTSZNnCAABUplp4Pnk401.jpg)

```java
try {
      File file = new File("test.txt");

      BufferedSource source = Okio.buffer(Okio.source(file));

      //byte[] data = source.readByteArray();
      //System.out.println(new String(data, Charset.forName("UTF-8")));

      BufferedSink sink = Okio.buffer(Okio.sink(new File("test2.txt")));
      source.readAll(sink);
      sink.close();

      source.close();
 } catch (IOException e) {
      e.printStackTrace();
 }
```

# Sink

![](http://s3.51cto.com/wyfs02/M02/5B/94/wKiom1UL7ifA8Gq9AABmy7lI3Yo667.jpg)

```java
try {
    File file = new File("test.txt"); //如果文件不存在，则自动创建
    BufferedSink sink = Okio.buffer(Okio.sink(file));
    sink.writeUtf8("Hello, World");
    sink.writeString("测试信息", Charset.forName("UTF-8"));
    sink.close();
 } catch (IOException e) {
    e.printStackTrace();
}
```
