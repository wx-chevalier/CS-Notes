# Log-Introduction

日志记录一直是每个 Java 应用程序，特别是服务端应用程序的标配。譬如 Log4j2/Log4j, Logback 等。

# java.util.logging.Logger

java.util.logging.Logger 不是什么新鲜东西了，1.4 就有了，可是因为 log4j 的存在，这个 logger 一直沉默着，其实在一些测试性的代码中，jdk 自带的 logger 比 log4j 更方便。
