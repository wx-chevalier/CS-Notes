# 基于 Logback 的 Java 日志处理

Logback 是一个日志框架，它与Log4j可以说是同出一源，都出自Ceki Gülcü之手。Slf4j 的全称是Simple Logging Facade for Java ，即简单日志门面；实现了日志框架一些通用的api，结合日志框架一起使用，最终日志的格式、记录级别、输出方式等都是通过绑定具体的日志框架实现的。log4j2不是log4j的升级版，而是apache开发的, log4j2在设计时的性能是优于logback的，但是其使用时相对于 Logback 更为复杂，并且 Spring Boot 的内置日志框架也是 Logback，因此我们还是首选 Logback。

Logback 主要由 logback-core, logback-classic, logback-access 三个模块组成，logback-core 是其它模块的基础设施，提供了一些关键的通用机制。logback-classic 的地位和作用等同于 Log4J，它也被认为是 Log4J 的一个改进版，并且它实现了简单日志门面 SLF4J；而 logback-access 主要作为一个与 Servlet 容器交互的模块，比如说tomcat或者 jetty，提供一些与 HTTP 访问相关的功能。

## 基础配置

我们可以通过 logback-spring.xml 来配置 Logback，基础配置如下：

```xml
<configuration scan="true" scanPeriod="60 seconds" debug="false">  
    <!-- 引入 Spring Boot 基础配置 -->
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    
    <!-- 定义变量 -->
    <property name="APP_NAME" value="test"/>
    <property name="LOG_PATH" value="${user.home}/${APP_NAME}/logs"/>
    <property name="LOG_FILE" value="${LOG_PATH}/application.log"/>
    
    <!-- 日志输出级别变量 @see application-xxx.properties-->
    <springProperty scope="context" name="logLevel" source="log.level"/>
    
    <appender>
        // xxxx
    </appender>   
    
    <logger>
        // xxxx
    </logger>
    
    <root level="${logLevel}">
        // xxxx
    </root>
</configuration>  
```

property 用来定义变量值的标签，property标签有两个属性，name和value；其中name的值是变量的名称，value的值时变量定义的值。通过property定义的值会被插入到logger上下文中。定义变量后，可以使 `${name}` 来使用变量。

```xml
<included>
	<conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter" />
	<conversionRule conversionWord="wex" converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter" />
	<conversionRule conversionWord="wEx" converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter" />
	<property name="CONSOLE_LOG_PATTERN" value="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>
	<property name="FILE_LOG_PATTERN" value="${FILE_LOG_PATTERN:-%d{yyyy-MM-dd HH:mm:ss.SSS} ${LOG_LEVEL_PATTERN:-%5p} ${PID:- } --- [%t] %-40.40logger{39} : %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>

	<appender name="DEBUG_LEVEL_REMAPPER" class="org.springframework.boot.logging.logback.LevelRemappingAppender">
		<destinationLogger>org.springframework.boot</destinationLogger>
	</appender>

	<logger name="org.apache.catalina.startup.DigesterFactory" level="ERROR"/>
	...
	<logger name="org.springframework.boot.actuate.endpoint.jmx" additivity="false">
		<appender-ref ref="DEBUG_LEVEL_REMAPPER"/>
	</logger>
	<logger name="org.thymeleaf" additivity="false">
		<appender-ref ref="DEBUG_LEVEL_REMAPPER"/>
	</logger>
</included>
```

最常用的日志输出就是输出到 Console 中，我们可以定义 Console 专用的 Appender:

```xml
<appender name="console" class="ch.qos.logback.core.ConsoleAppender">
  <encoder>
    <pattern>logback: %d {HH:mm:ss.SSS} %logger{36} - %M - %msg%n</pattern>
    <!--<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS}|%level|%class|%thread|%method|%line|%msg%n</pattern>-->
    <!-- 使用 Spring Boot 预定义的 Pattern -->
    <!--<pattern>${CONSOLE_LOG_PATTERN}</pattern>-->
  </encoder>
</appender>

<root level="${logLevel}">
    <appender-ref ref="console"/>
</root>
```

然后在 Java 代码中可以通过 Slf4j 来获取到日志记录器对象：

```java
// 通过 Slf4j LoggerFactory 获取 logger 对象
private static final Logger logger = LoggerFactory.getLogger(DemoTest.class);

// 通过 Slf4j 注解注入
@Slf4j

logger.info("info")
```

## Appender

appender是一个日志打印的组件，这里组件里面定义了打印过滤的条件、打印输出方式、滚动策略、编码方式、打印格式等等。但是它仅仅是一个打印组件，如果我们不使用一个logger或者root的appender-ref指定某个具体的appender时，它就没有什么意义。上文定义的 root 本质上是根logger,只不过root中不能有name和additivity属性，是有一个level。

Appender 主要包含以下三类：

- ConsoleAppender：把日志添加到控制台
- FileAppender：把日志添加到文件
- RollingFileAppender：滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件。它是FileAppender的子类

```xml
<appender name="APPLICATION"
          class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_FILE}</file>
    <encoder>
        <pattern>${FILE_LOG_PATTERN}</pattern>
    </encoder>
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        <maxHistory>7</maxHistory>
        <maxFileSize>50MB</maxFileSize>
        <totalSizeCap>20GB</totalSizeCap>
    </rollingPolicy>
</appender>
```

rollingPolicy 子标签
这个子标签用来描述滚动策略的。这个只有appender的class是RollingFileAppender时才需要配置。TimeBasedRollingPolicy 最常用的滚动策略，它根据时间来制定滚动策略，既负责滚动也负责出发滚动。FixedWindowRollingPolicy 根据固定窗口算法重命名文件的滚动策略。

在真实场景下，我们可能会需要将不同级别的日志输出到不同的文件中，在不引入新的 Logger 的情况下，我们可以通过 Filter 来进行过滤：

filter其实是appender里面的子元素。它作为过滤器存在，执行一个过滤器会有返回DENY，NEUTRAL，ACCEPT三个枚举值中的一个。

- DENY：日志将立即被抛弃不再经过其他过滤器
- NEUTRAL：有序列表里的下个过滤器过接着处理日志
- ACCEPT：日志会被立即处理，不再经过剩余过滤器

```xml
<appender name="fileInfoLog" class="ch.qos.logback.core.rolling.RollingFileAppender">
<filter class="ch.qos.logback.classic.filter.LevelFilter">
            <!--要拦截的日志级别-->
            <level>ERROR</level>
            <!--如果匹配，则禁止-->
            <onMatch>DENY</onMatch>
            <!--如果不匹配，则允许记录-->
            <onMismatch>ACCEPT</onMismatch>
        </filter>
    <encoder>
        <pattern>%d -- %msg%n</pattern>
    </encoder>
    <!--滚动策略-->
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <!--路径-->
        <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
    </rollingPolicy>
</appender>

<appender name="fileErrorLog" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <!--添加 范围 过滤-->
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>ERROR</level>
    </filter>
    <encoder>
        <pattern>%d -- %msg%n</pattern>
    </encoder>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
    </rollingPolicy>
</appender>

<root level="info">
    <appender-ref ref="fileInfoLog"/>
    <appender-ref ref="fileErrorLog"/>
</root>
```
- ThresholdFilter: 临界值过滤器，过滤掉低于指定临界值的日志。当日志级别等于或高于临界值时，过滤器返回NEUTRAL；当日志级别低于临界值时，日志会被拒绝。

- LevelFilter: 级别过滤器，根据日志级别进行过滤。如果日志级别等于配置级别，过滤器会根据onMath(用于配置符合过滤条件的操作) 和 onMismatch(用于配置不符合过滤条件的操作)接收或拒绝日志。

## Logger

我们也可以通过自定义 Logger，来关联不同的包或者 Appender:

```xml
<logger name="wx.spring.boot.controller"
        level="${logging.level}" additivity="false">
    <appender-ref ref="console" />
</logger>
```
上面的这个配置文件描述的是：wx.spring.boot.controller 这个包下的${logging.level}级别的日志将会使用 console 来打印。logger有三个属性和一个子标签：

name:用来指定受此logger约束的某一个包或者具体的某一个类。
level:用来设置打印级别（TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF），还有一个值INHERITED或者同义词NULL，代表强制执行上级的级别。如果没有设置此属性，那么当前logger将会继承上级的级别。
addtivity:用来描述是否向上级logger传递打印信息。默认是true。

```xml
<!-- fileControllerLog-->
<appender name="fileControllerLog" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${APP_LOG_FILE}</file>
     <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>${logging.level}</level>
    </filter>
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} ${LOG_LEVEL_PATTERN:-%5p} %t %logger{0} %m%n</pattern>
        <charset>utf8</charset>
    </encoder>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>${APP_LOG_FILE}.log.%d{yyyy-MM-dd}</fileNamePattern>
        <maxHistory>10</maxHistory>
    </rollingPolicy>
</appender>

<!--此logger约束将.controller 包下的日志输出到 fileControllerLog-->
<logger name="wx.spring.boot.controller" level="${logging.level}" additivity="false">
    <appender-ref ref="fileControllerLog" />
    <appender-ref ref="fileErrorLog" />
</logger>

<!--此logger约束将.service 包下的日志输出到 fileServiceLog-->
<logger name="wx.spring.boot.service" level="${logging.level}" additivity="false">
    <appender-ref ref="fileServiceLog" />
    <appender-ref ref="fileErrorLog" />
</logger>
```

我们也可以将日志维度固定到某个具体的类：

```xml
<!--这里指定到了具体的某一个类-->
<logger name="wx.spring.boot.task.TestLogTask" level="${logging.level}" additivity="true">
        <appender-ref ref="SCHEDULERTASKLOCK-APPENDER" />
        <appender-ref ref="fileErrorLog" />
    </logger>
```

或者自定义 logger 名称：

```xml
<logger name="dependency" level="info" additivity="false">
    <appender-ref ref="dependencyAppender" />
</logger>
```

然后在代码中显式获取：

```java
private static Logger logger = LoggerFactory.getLogger("dependency");
```

如果我们希望打印出 Mybatis 的 SQL 语句，则在 logback-spring.xml 中添加如下配置：

```xml
 <!-- 将sql语句输出到具体的日志文件中 -->
<logger name="com.alipay.sofa.cloudplatform.common.dao" level="${logging.sql.level}" additivity="false">
    <appender-ref ref="SQL-APPENDER"/>
</logger>
```

