# Log4j2

Log4j 2 包含了基于 LMAX 分离库的下一代的异步日志系统，在多线程环境下，异步日志系统比 Log4j 1.x 和 Logback 提高了 10 倍性能提升(吞吐量和延迟率)，各种框架的对比可以看到如下所示：

![](http://cdn3.infoqstatic.com/statics_s2_20160105-0313u5/resource/news/2014/08/apache-log4j2/zh/resources/0805000.png)

Log4j 2 是 Log4j 的升级版本，该版本比起其前任来说有着显著的改进，包含很多在 Logback 中的改进以及 Logback 架构中存在的问题。这是 Log4j 2 的首次发行的版本，值得关注的改进包括：

- API 分离 – Log4j 的 API 和其实现进行分类(注：我讨厌这样，本来一个 jar 包搞定的，要变成好几个，跟 slf4j 似的的)

- 为日志审计而设计，与 Log4j 1.x 和 Logback 不同的是 Log4j 2 将不会在重新配置期间丢失事件，支持消息可方便进行审计

- 性能方面的提升，在关键领域比 Log4j 1.x 的性能提升不少，大部分情况下性能跟 Logback 差不多

- 支持多 APIs，支持 SLF4J 和 Commons Logging API

- 自动配置重载，支持 XML 和 JSON 格式的配置

- 插件体系架构，所有可配置的组件都是通过 Log4j 插件进行定义，包括 Appender, Layout, Pattern Converter, 等等

- 配置属性支持

![](http://logging.apache.org/log4j/2.x/images/Log4jClasses.jpg)

# 配置文件

如果需要使用 Log4j2，首先需要在他的依赖文件里引入如下依赖：

```xml
    <dependencies>
      <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
        <version>2.4.1</version>
      </dependency>
      <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>2.4.1</version>
      </dependency>
    </dependencies>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
        <version>2.0-beta9</version>
    </dependency>
```

而后在代码文件中，可以使用 Slf4j，也可以直接使用 Log4j2，如下：

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger log = LoggerFactory.getLogger(Test.class);
```

如果是直接使用的 log4j2，则只要用 LogManager 的 getLogger 函数获取一个 logger，就可以使用 logger 记录日志，代码如下：

```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HelloLog4j {
    private static Logger logger = LogManager.getLogger("HelloLog4j");
    public static void main(String[] args) {
    MyApplication myApplication =  new MyApplication();

    logger.entry();
    logger.info("Hello, World!");
    myApplication.doIt();
            logger.error("Hello, World!");
            logger.exit();
    }
}
```

需要注意的是，log4j 2.0 与以往的 1.x 有一个明显的不同，其配置文件只能采用.xml, .json 或者 .jsn。在默认情况下，系统选择 configuration 文件的优先级如下：

- classpath 下名为 log4j-test.json 或者 log4j-test.jsn 文件
- classpath 下名为 log4j2-test.xml
- classpath 下名为 log4j.json 或者 log4j.jsn 文件
- classpath 下名为 log4j2.xml

# Logger 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
    <!-- status=debug 可以查看log4j的装配过程 -->
    <configuration status="off" monitorInterval="1800">
    <properties>
        <property name="LOG_HOME">/log/fish</property>
        <!-- 日志备份目录 -->
        <property name="BACKUP_HOME">{LOG_HOME}/backup</property>
        <property name="STAT_NAME">stat</property>
        <property name="SERVER_NAME">global</property>
    </properties>
    <appenders>
        <!-- 定义控制台输出 -->
        <Console name="Console" target="SYSTEM_OUT" follow="true">
        <PatternLayout pattern="%date{yyyy-MM-dd HH:mm:ss.SSS} %level [%thread][%file:%line] - %msg%n" />
        </Console>
        <!-- 程序员调试日志 -->
        <RollingRandomAccessFile name="DevLog" fileName="${LOG_HOME}/${SERVER_NAME}"
        filePattern="${LOG_HOME}/${SERVER_NAME}.%d{yyyy-MM-dd-HH}.log">
        <PatternLayout pattern="%date{yyyy-MM-dd HH:mm:ss.SSS} %level [%thread][%file:%line] - %msg%n" />
        <Policies>
        <TimeBasedTriggeringPolicy interval="1" modulate="true" />
        </Policies>
        </RollingRandomAccessFile>
        <!-- 游戏产品数据分析日志 -->
        <RollingRandomAccessFile name="ProductLog"
        fileName="${LOG_HOME}/${SERVER_NAME}_${STAT_NAME}"
        filePattern="${LOG_HOME}/${SERVER_NAME}_${STAT_NAME}.%d{yyyy-MM-dd-HH}.log">
        <PatternLayout
        pattern="%date{yyyy-MM-dd HH:mm:ss.SSS} %level [%thread][%file:%line] - %msg%n" />
        <Policies>
        <TimeBasedTriggeringPolicy interval="1"
        modulate="true" />
        </Policies>
        </RollingRandomAccessFile>
    </appenders>
    <loggers>
        <!-- 3rdparty Loggers -->
        <logger name="org.springframework.core" level="info">
        </logger>
        <logger name="org.springframework.beans" level="info">
        </logger>
        <logger name="org.springframework.context" level="info">
        </logger>
        <logger name="org.springframework.web" level="info">
        </logger>
        <logger name="org.jboss.netty" level="warn">
        </logger>
        <logger name="org.apache.http" level="warn">
        </logger>
        <logger name="com.mchange.v2" level="warn">
        </logger>
        <!-- Game Stat logger -->
        <logger name="com.u9.global.service.log" level="info"
        additivity="false">
            <appender-ref ref="ProductLog" />
        </logger>
        <!-- Root Logger -->
        <root level="DEBUG" includeLocation="true">
            <appender-ref ref="DevLog" />
            <appender-ref ref="Console" />
        </root>
    </loggers>
</configuration>
```

loggers 标签，用于定义 logger 的 lever 和所采用的 appender，其中 appender-ref 必须为先前定义的 appenders 的名称，例如，此处为 Console。那么 log 就会以 appender 所定义的输出格式来输出 log。root 标签为 log 的默认输出形式，如果一个类的 log 没有在 loggers 中明确指定其输出 lever 与格式，那么就会采用 root 中定义的格式。例如以下定义：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration status="OFF">
  <appenders>
    <Console name="Console" target="SYSTEM_OUT">
      <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
    </Console>
  </appenders>
  <loggers>
    <logger name="com.relin.HelloLog4j" level="error" additivity="false">
      <appender-ref ref="Console"/>
    </logger>
    <root level="trace">
      <appender-ref ref="Console"/>
    </root>
  </loggers>
</configuration>
```

此时，HelloLog4j 则会在 error 级别上输出 log，而其他类则会在 trace 级别上输出 log。需要注意的是 additivity 选项，如果设置为 true(默认值)则 HelloLog4j 的 log 会被打印两次，第二次打印是由于 HelloLog4j 同时也满足 root 里面定义的 trace。在 log4j2 中可以配置不同的 Logger 输出到不同的文件中，如果有时候需要按照不同的级别输出到不同的文件中，则直接在 logger 的 AppenderRef 中定义不同的 level 指向。

```xml
<loggers>
    <logger name="com.mvc.login" level="info" additivity="false">
        <appenderRef ref="LoginController" level="error"/>
        <appenderRef ref="InfoController" level="info"/>
    </logger>
</loggers>
```
