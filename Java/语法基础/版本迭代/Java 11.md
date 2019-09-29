# Java 11

# 本地变量类型推断

```java
var javastack = "javastack";
System.out.println(javastack);
```

# 字符串加强

Java 11 增加了一系列的字符串处理方法，如以下所示。

```java

// 判断字符串是否为空白
" ".isBlank(); // true
// 去除首尾空格
" Javastack ".strip(); // "Javastack"
// 去除尾部空格
" Javastack ".stripTrailing(); // " Javastack"
// 去除首部空格
" Javastack ".stripLeading(); // "Javastack "
// 复制字符串
"Java".repeat(3);// "JavaJavaJava"
// 行数统计
"A\nB\nC".lines().count(); // 3
```

# 集合加强

自 Java 9 开始，Jdk 里面为集合（List/ Set/ Map）都添加了 of 和 copyOf 方法，它们两个都用来创建不可变的集合。

```java
var list = List.of("Java", "Python", "C");
var copy = List.copyOf(list);
System.out.println(list == copy); // true

var list = new ArrayList<String>();
var copy = List.copyOf(list);
System.out.println(list == copy); // false
```

# Stream 加强

Stream 是 Java 8 中的新特性，Java 9 开始对 Stream 增加了以下 4 个新方法。

- 增加单个参数构造方法，可为 null

```java
Stream.ofNullable(null).count(); // 0
```

- 增加 takeWhile 和 dropWhile 方法

```java
Stream.of(1, 2, 3, 2, 1)
.takeWhile(n -> n < 3)
.collect(Collectors.toList()); // [1, 2]
```

从开始计算，当 n < 3 时就截止。

```java
Stream.of(1, 2, 3, 2, 1)
.dropWhile(n -> n < 3)
.collect(Collectors.toList()); // [3, 2, 1]
```

- iterate 重载

这个 iterate 方法的新重载方法，可以让你提供一个 Predicate (判断条件)来指定什么时候结束迭代。

# Optional 加强

Opthonal 也增加了几个非常酷的方法，现在可以很方便的将一个 Optional 转换成一个 Stream, 或者当一个空 Optional 时给它一个替代的。

```java
Optional.of("javastack").orElseThrow(); // javastack
Optional.of("javastack").stream().count(); // 1
Optional.ofNullable(null)
.or(() -> Optional.of("javastack"))
.get(); // javastack
```

# HTTP Client API

这是 Java 9 开始引入的一个处理 HTTP 请求的的孵化 HTTP Client API，该 API 支持同步和异步，而在 Java 11 中已经为正式可用状态，你可以在 java.net 包中找到这个 API。

```java
var request = HttpRequest.newBuilder()
.uri(URI.create("https://javastack.cn"))
.GET()
.build();
var client = HttpClient.newHttpClient();
// 同步
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
// 异步
client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
.thenApply(HttpResponse::body)
.thenAccept(System.out::println);
```
