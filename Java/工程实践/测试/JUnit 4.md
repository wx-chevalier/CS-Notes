# JUnit 4

JUnit ——是一个开源的 Java 测试框架，主要用于编写白盒测试，回归测试。无论白盒测试还是回归测试，都是运行可重复的测试。所谓”回归测试“——就是，软件或环境的修复或更正后的“再测试”，自动测试工具对这类测试尤其有用；而所谓”单元测试“——就是，最小粒度的测试，以测试某个功能或代码块。一般由程序员来做，因为它需要知道内部程序设计和编码的细节。对于持续发展的产品，单元测试在后期的维护，回归有重要等方面有重要作用。

```java
@Test
public void testMultiply() {
    MyClass tester = new MyClass();
    assertEquals( "10 x 5 must be 50 ", 50, tester.multiply( 10, 5 ));
}
```

# JUnit

- @Test(expected = Exception.class) 表示预期会抛出 Exception.class 的异常
- @Ignore 含义是“某些方法尚未完成，暂不参与此次测试”。这样的话测试结果就会提示你有几个测试被忽略，而不是失败。一旦你完成了相应函数，只需要把@Ignore 注解删去，就可以进行正常的测试。
- @Test(timeout=100) 表示预期方法执行不会超过 100 毫秒，控制死循环
- @Before 表示该方法在每一个测试方法之前运行，可以使用该方法进行初始化之类的操作
- @After 表示该方法在每一个测试方法之后运行，可以使用该方法进行释放资源，回收内存之类的操
- @BeforeClass 表示该方法只执行一次，并且在所有方法之前执行。一般可以使用该方法进行数据库连接操作，注意该注解运用在静态方法。
- @AfterClass 表示该方法只执行一次，并且在所有方法之后执行。一般可以使用该方法进行数据库连接关闭操作，注意该注解运用在静态方法。

# TestSuite

如果你须有多个测试单元，可以合并成一个测试套件进行测试，况且在一个项目中，只写一个测试类是不可能的，我们会写出很多很多个测试类。可是这些测试类必须一个一个的执行，也是比较麻烦的事情。鉴于此， JUnit 为我们提供了打包测试的功能，将所有需要运行的测试类集中起来，一次性的运行完毕，大大的方便了我们的测试工作。并且可以按照指定的顺序执行所有的测试类。下面的代码示例创建了一个测试套件来执行两个测试单元。如果你要添加其他的测试单元可以使用语句 @Suite.SuiteClasses 进行注解。

```java
@RunWith(Suite.class)
@SuiteClasses({ JUnit1Test.class, StringUtilTest.class })
public class JSuit {

}
```

TestSuite 测试包类——多个测试的组合 TestSuite 类负责组装多个 Test Cases。待测得类中可能包括了对被测类的多个测试，而 TestSuit 负责收集这些测试，使我们可以在一个测试中，完成全部的对被测类的多个测试。 TestSuite 类实现了 Test 接口，且可以包含其它的 TestSuites。它可以处理加入 Test 时的所有抛出的异常。

TestResult 结果类集合了任意测试累加结果，通过 TestResult 实例传递个每个测试的 Run() 方法。TestResult 在执行 TestCase 是如果失败会异常抛出 TestListener 接口是个事件监听规约，可供 TestRunner 类使用。它通知 listener 的对象相关事件，方法包括测试开始 startTest(Test test)，测试结束 endTest(Test test),错误，增加异常 addError(Test test, Throwable t) 和增加失败 addFailure(Test test, AssertionFailedError t) 。TestFailure 失败类是个“失败”状况的收集类，解释每次测试执行过程中出现的异常情况，其 toString() 方法返回“失败”状况的简要描述。

# Assert

JUnit 中的 assert 方法全部放在 Assert 类中，总结一下 JUnit 类中 assert 方法的分类：

- `assertTrue/False([String message,]boolean condition)`: 判断一个条件是 true 还是 false。感觉这个最好用了，不用记下来那么多的方法名。

- `fail([String message,])`: 失败，可以有消息，也可以没有消息。

- `assertEquals([String message,]Object expected,Object actual)`: 判断是否相等，可以指定输出错误信息。第一个参数是期望值，第二个参数是实际的值。这个方法对各个变量有多种实现。在 JDK1.5 中基本一样。但是需要主意的是 float 和 double 最后面多一个 delta 的值。

- `assertNotNull/Null([String message,]Object obj)`: 判读一个对象是否非空(非空)。

- `assertSame/NotSame([String message,]Object expected,Object actual)`: 通过内存地址，判断两个对象是否指向同一个对象。

- `failNotSame/failNotEquals(String message, Object expected, Object actual)`: 当不指向同一个内存地址或者不相等的时候，输出错误信息。注意信息是必须的，而且这个输出是格式化过的。
