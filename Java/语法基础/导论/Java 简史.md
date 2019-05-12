# Java 的过去、现在与未来

# 版本

2018 年 3 月 21 日，Oracle 官方宣布 Java 10 正式发布。这是 Java 大版本周期变化后的第一个正式发布版本。需要注意的是 Java 9 和 Java 10 都不是 LTS 版本。和过去的 Java 大版本升级不同，这两个只有半年左右的开发和维护期。而未来的 Java 11，也就是 18.9 LTS，才是 Java 8 之后第一个 LTS 版本。

```sh
/jdk-10/bin$ ./java -version

openjdk version "10" 2018-03-20

OpenJDK Runtime Environment 18.3 (build 10+46)

OpenJDK 64-Bit Server VM 18.3 (build 10+46, mixed mode)
```

这种发布模式已经得到了广泛应用，一个成功的例子就是 Ubuntu Linux 操作系统，在偶数年 4 月的发行版本为 LTS，会有很长时间的支持。如 2014 年 4 月份发布的 14.04 LTS，Canonical 公司和社区支持到 2019 年。类似的，Node.js，Linux kernel，Firefox 也采用类似的发布方式。

Java 未来的发布周期，将每半年发布一个大版本，每个季度发布一个中间特性版本。这样可以把一些关键特性尽早合并入 JDK 之中，快速得到开发者反馈，可以在一定程度上避免 Java 9 两次被迫推迟发布日期的尴尬。
