[![java](https://user-images.githubusercontent.com/5803001/43033009-f03a83a4-8cf4-11e8-9822-2059a0e3a5a8.jpg)](https://github.com/wxyyxc1992/ProgrammingLanguage-Series)

# Java IO 基础

Java.io 包几乎包含了所有操作输入、输出需要的类。所有这些流类代表了输入源和输出目标；Java.io 包中的流支持很多种格式，比如：基本类型、对象、本地化字符集等等。一个流可以理解为一个数据的序列。输入流表示从一个源读取数据，输出流表示向一个目标写数据。Java 为 I/O 提供了强大的而灵活的支持，使其更广泛地应用到文件传输和网络编程中。

Java 是面向对象的编程语言，对象是对现实实体的抽象表述。所以 Java API 中流(Stream)是对一连串数据的抽象，同时定义了一些操作，write 和 read 等。 所以现实实体，只要包含数据和对数据的读写操作都可以表示为流。OutputStream 类和 InputStream 类，是 2 个抽象类，分别对应输出、输入流，所有其它流对象，都是其子类。　　比如文件，文件本质是保存在存储设备中的一连串数据，在 Java API 中抽象为 FileOutputStream 类和 FileInputStream 类，文件的读写可以通过对相应流的读写实现的。

![](http://www.runoob.com/wp-content/uploads/2013/12/iostream2xx.png)

# 控制台

## 输入处理

```java
Scanner sc = new Scanner (new BufferedInputStream(System.in));
Scanner sc = new Scanner (System.in);

// 读一个整数
int n = sc.nextInt();
// 读一个字符串
String s = sc.next();
// 读一个浮点数
double t = sc.nextDouble();
// 读一整行
String s = sc.nextLine();

// 判断是否还存在输入
sc.hasNext()
```

很多时候我们还需要去判断输入，并且根据不同的指令执行不同的操作：

```java
    Scanner scanner = new Scanner(System.in);
    System.out.println("Command me please!");
    String inputString = "";
    while (!inputString.equals("quit")) {
     inputString = scanner.nextLine();
     String[] commandTokens = inputString.split(" ");

     if (commandTokens.length == 0) {
         scanner.close();
         throw new InputMismatchException("please enter a command");
     } else {
         String command = commandTokens[0];
         if (command.equals("search")) {
          System.out.println("this is where we can call search with "
               + makeArguments(commandTokens));
         }
     }
    }
    scanner.close();
```

或者将输入的参数连接：

```java
public static String makeArguments(String[] commandTokens) {
 String completeStringArgument = "";
 for (int i = 1; i < commandTokens.length - 1; i++) {
     completeStringArgument += commandTokens[i] + " ";
 }
 return completeStringArgument + commandTokens[commandTokens.length - 1];
}
```

##  输出处理

在此前已经介绍过，控制台的输出由  `print()` 和 `println()` 完成，这些方法都由类 PrintStream 定义，System.out 是该类对象的一个引用；PrintStream 继承了 OutputStream 类，并且实现了方法 write()，也可以用来往控制台写操作。

下面的例子用 write()把字符"A"和紧跟着的换行符输出到屏幕：

```java
import java.io.*;

// 演示 System.out.write().
public class WriteDemo {
   public static void main(String args[]) {
      int b;
      b = 'A';
      System.out.write(b);
      System.out.write('\n');
   }
}
```

# 文件读写

## 文件对象

```java
// 要找 resource 目录下的某个文件
BufferedReader bufferedReaderB = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/B/B1.txt")));
String url = this.getClass().getResource("/userFile.properties").getFile();

// 找某个类
File f = new File(MyClass.class.getProtectionDomain().getCodeSource().getLocation().getPath());
String[] classpathEntries = classpath.split(File.pathSeparator);
```

###  按行读取

``` java
public class ReadSelectedLine{
    // 读取文件指定行。
    static void readAppointedLineNumber(File sourceFile, int lineNumber)
         throws IOException {
     FileReader in = new FileReader(sourceFile);
     LineNumberReader reader = new LineNumberReader(in);
     String s = "";
     if (lineNumber <= 0 || lineNumber > getTotalLines(sourceFile)) {
         System.out.println("不在文件的行数范围(1至总行数)之内。");
         System.exit(0);
     }
     int lines = 0;
     while (s != null) {
         lines++;
         s = reader.readLine();
         if((lines - lineNumber) == 0) {
          System.out.println(s);
          System.exit(0);
         }
     }
     reader.close();
     in.close();
    }
    // 文件内容的总行数。
    static int getTotalLines(File file) throws IOException {
     FileReader in = new FileReader(file);
     LineNumberReader reader = new LineNumberReader(in);
     String s = reader.readLine();
     int lines = 0;
     while (s != null) {
         lines++;
         s = reader.readLine();
     }
     reader.close();
     in.close();
     return lines;
    }

    /**
     * 读取文件指定行。
     */
    public static void main(String[] args) throws IOException {
     // 指定读取的行号
     int lineNumber = 2;
     // 读取文件
     File sourceFile = new File("D:/java/test.txt");
     // 读取指定的行
     readAppointedLineNumber(sourceFile, lineNumber);
     // 获取文件的内容的总行数
     System.out.println(getTotalLines(sourceFile));
    }
}
```

# Okio

Okio 是 java.io 和 java.nio 的补充，使用它更容易访问、存储和处理数据。

![](http://s3.51cto.com/wyfs02/M01/5B/90/wKiom1ULwkCi7uJWAAPh6stvER4577.jpg)

Okio 中有几个常用的概念：

- ByteString：不可变的字节序列，类似 String 类，使用它更容易处理二进制数据。
- Buffer：可变的字节序列，类似 ArrayList。
- Source：类似 InputStream(输入流)
- Sink：类似 OutputStream(输出流)

## Source

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

## Sink

![](http://s3.51cto.com/wyfs02/M02/5B/94/wKiom1UL7ifA8Gq9AABmy7lI3Yo667.jpg)

``` java
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
