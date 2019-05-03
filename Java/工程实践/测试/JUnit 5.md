# JUnit 5

JUnit 是 Java 中使用最广泛的测试框架，JUnit5 主要在希望能够适应 Java8 风格的编码以及相关工，这就是为什么建议在 Java 8 之后的项目中使用 JUnit5 来创建和执行测试。JUnit5 的第一个可用性版本是在 2017 年 9 月 10 日发布的。

![](https://i.postimg.cc/4x81wh5F/image.png)

`JUnit 5 = JUnit Platform + JUnit Jupiter + JUnit Vintage`：

- JUnit Platform: 启动 Junit 测试、IDE、构建工具或插件都需要包含和扩展 Platform API，它定义了 TestEngine 在平台运行的新测试框架的 API。它还提供了一个控制台启动器，可以从命令行启动 Platform，为 Gradle 和 Maven 插件提供支持。

- JUnit Jupiter: 它用于编写测试代码的新的编程和扩展模型。它具有所有新的 Junit 注释和 TestEngine 实现来运行这些注释编写的测试。

- JUnit Vintage: 它主要的目的是支持在 JUnit5 的测试代码中运行 JUnit3 和 4 方式写的测试，它能够向前兼容之前的测试代码。

使用 JUnit 5 的时候需要在 Maven 或者 Gradle 中添加对应的依赖：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>${junit.jupiter.version}</version>
</dependency>
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-runner</artifactId>
    <version>${junit.platform.version}</version>
    <scope>test</scope>
</dependency>
```

```groovy
testRuntime("org.junit.jupiter:junit-jupiter-engine:5.0.0-M4")
testRuntime("org.junit.platform:junit-platform-runner:1.0.0-M4")
```

# 测试用例

JUnit4 和 JUnit5 在测试编码风格上没有太大变化，这是其生命周期方法的样本测试。

```java
public class AppTest {
    @BeforeAll
    static void setup(){
        System.out.println("@BeforeAll executed");
    }
    @BeforeEach
    void setupThis(){
        System.out.println("@BeforeEach executed");
    }
    @Tag("DEV")
    @Test
    void testCalcOne()
    {
        System.out.println("======TEST ONE EXECUTED=======");
        Assertions.assertEquals( 4 , Calculator.add(2, 2));
    }
    @Tag("PROD")
    @Disabled
    @Test
    void testCalcTwo()
    {
        System.out.println("======TEST TWO EXECUTED=======");
        Assertions.assertEquals( 6 , Calculator.add(2, 4));
    }
    @AfterEach
    void tearThis(){
        System.out.println("@AfterEach executed");
    }
    @AfterAll
    static void tear(){
        System.out.println("@AfterAll executed");
    }
}
```

## 测试套件

使用 JUnit5 的测试套件，可以将测试扩展到多个测试类和不同的软件包。JUnit5 提供了两个注解： @SelectPackages 和 @SelectClasses 来创建测试套件。要执行测试套件，可以是使用 `@RunWith(JUnitPlatform.class)`

```java
@RunWith(JUnitPlatform.class)
@SelectPackages("com.github.tonydeng.junit5.examples")
public class JUnit5TestSuiteExample
{
}
```

另外，你也可以使用以下注解来过滤测试包、类甚至测试方法。

- @IncludePackages 和 `@ExcludePackages` 来过滤包
- @IncludeClassNamePatterns 和 `@ExcludeClassNamePatterns` 过滤测试类
- @IncludeTags 和 `@ExcludeTags` 过滤测试方法

```java

@RunWith(JUnitPlatform.class)
@SelectPackages("com.github.tonydeng.junit5.examples")
@IncludePackages("com.github.tonydeng.junit5.examples.packageC")
@ExcludeTags("PROD")
public class JUnit5TestSuiteExample
{
}
```

## 断言

断言有助于使用测试用例的实际输出验证预期输出。为了保持简单，所有 JUnit Jupiter 断言是 org.junit.jupiter.Assertions 类中的静态方法，例如 assertEquals()，assertNotEquals()。

```java
void testCase()
{
    //Test will pass
    Assertions.assertNotEquals(3, Calculator.add(2, 2));
    //Test will fail
    Assertions.assertNotEquals(4, Calculator.add(2, 2), "Calculator.add(2, 2) test failed");
    //Test will fail
    Supplier<String> messageSupplier  = ()-> "Calculator.add(2, 2) test failed";
    Assertions.assertNotEquals(4, Calculator.add(2, 2), messageSupplier);
}
```

## 假设

Assumptions 类提供了静态方法来支持基于假设的条件测试执行。失败的假设导致测试被中止。无论何时继续执行给定的测试方法没有意义，通常使用假设。在测试报告中，这些测试将被标记为已通过。JUnit 的 Jupiter 假设类有两个这样的方法：assumeFalse()，assumeTrue()。

```java
public class AppTest {
    @Test
    void testOnDev()
    {
        System.setProperty("ENV", "DEV");
        Assumptions.assumeTrue("DEV".equals(System.getProperty("ENV")), AppTest::message);
    }
    @Test
    void testOnProd()
    {
        System.setProperty("ENV", "PROD");
        Assumptions.assumeFalse("DEV".equals(System.getProperty("ENV")));
    }
    private static String message () {
        return "TEST Execution Failed :: ";
    }
}
```
