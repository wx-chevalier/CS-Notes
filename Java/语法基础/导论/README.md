# Introduction

## Version Iteration

### Java8

> - [Java 8 简明教程](http://www.importnew.com/10360.html)

## Reference

### Blog & News

- [java-weekly](http://www.thoughts-on-java.org/java-weekly/)
- [Java, JVM and beyond](http://blog.sanaulla.info/)
- [ImportNew](http://www.importnew.com/)
- [javacodegeeks](http://www.javacodegeeks.com/category/java/core-java/)
- [Oracle-Java Magazine](http://www.oracle.com/technetwork/java/javamagazine/index.html)

# DS(数据类型与结构)

## 基本类型(Basic)

### 可选类型(Optional)

> [Java 8 Optional 类深度解析](http://www.importnew.com/6675.html)

Optional 不是一个函数式接口，而是一个精巧的工具接口，用来防止 NullPointerEception 产生。这个概念在下一节会显得很重要，所以我们在这里快速地浏览一下 Optional 的工作原理。Optional 是一个简单的值容器，这个值可以是 null，也可以是 non-null。考虑到一个方法可能会返回一个 non-null 的值，也可能返回一个空值。为了不直接返回 null，我们在 Java 8 中就返回一个 Optional.

```java
Optional<String> optional = Optional.of("bam");

optional.isPresent();           // true
optional.get();                 // "bam"
optional.orElse("fallback");    // "bam"

optional.ifPresent((s) -> System.out.println(s.charAt(0)));     // "b"
```

## DateTime：时间与日期

> - [Java 8 时间与日期处理](http://www.liaoxuefeng.com/article/00141939241051502ada88137694b62bfe844cd79e12c32000)
> - [Java8 时间/日期](http://www.importnew.com/14140.html)
> - [20 个关于 Java8 时间与日期的例子](http://javarevisited.blogspot.com/2015/03/20-examples-of-date-and-time-api-from-Java8.html)

在 Java8 之前，最常见的就是 Date(包括一系列的 Format)以及 Calendar。Java 8 新增了 LocalDate 和 LocalTime 接口，为什么要搞一套全新的处理日期和时间的 API？因为旧的 java.util.Date 实在是太难用了。java.util.Date 月份从 0 开始，一月是 0，十二月是 11，变态吧！java.time.LocalDate 月份和星期都改成了 enum，就不可能再用错了。

java.util.Date 和 SimpleDateFormatter 都不是线程安全的，而 LocalDate 和 LocalTime 和最基本的 String 一样，是不变类型，不但线程安全，而且不能修改。java.util.Date 是一个“万能接口”，它包含日期、时间，还有毫秒数，如果你只想用 java.util.Date 存储日期，或者只存储时间，那么，只有你知道哪些部分的数据是有用的，哪些部分的数据是不能用的。在新的 Java 8 中，日期和时间被明确划分为 LocalDate 和 LocalTime，LocalDate 无法包含时间，LocalTime 无法包含日期。当然，LocalDateTime 才能同时包含日期和时间。新接口更好用的原因是考虑到了日期时间的操作，经常发生往前推或往后推几天的情况。用 java.util.Date 配合 Calendar 要写好多代码，而且一般的开发人员还不一定能写对。

最新 JDBC 映射将把数据库的日期类型和 Java 8 的新类型关联起来：

```
SQL -> Java
--------------------------
date -> LocalDate
time -> LocalTime
timestamp -> LocalDateTime
```

### Clock & Instant

Clock 方便我们去读取当前的日期与时间。Clocks 可以根据不同的时区来进行创建，并且可以作为`System.currentTimeMillis()`的替代。这种指向时间轴的对象即是`Instant`类。Instants 可以被用于创建`java.util.Date`对象。

```java
Clock clock = Clock.systemDefaultZone();
long millis = clock.millis();

Instant instant = clock.instant();
Date legacyDate = Date.from(instant);   // legacy java.util.Date
```

### TimeZones

Timezones 以`ZoneId`来区分。可以通过静态构造方法很容易的创建，Timezones 定义了 Instants 与 Local Dates 之间的转化关系：

```java
System.out.println(ZoneId.getAvailableZoneIds());
// prints all available timezone ids

ZoneId zone1 = ZoneId.of("Europe/Berlin");
ZoneId zone2 = ZoneId.of("Brazil/East");
System.out.println(zone1.getRules());
System.out.println(zone2.getRules());

// ZoneRules[currentStandardOffset=+01:00]
// ZoneRules[currentStandardOffset=-03:00]
```

### LocalTime

LocalTime 代表了一个与时区无关的本地时间，譬如 10pm 或者 17:30:15。

#### Create

`LocalTime`只包含时间，以前用`java.util.Date`怎么才能只表示时间呢？答案是，假装忽略日期。

`LocalTime`包含毫秒：

```java
LocalTime now = LocalTime.now(); // 11:09:09.240
```

你可能想清除毫秒数：

```java
LocalTime now = LocalTime.now().withNano(0)); // 11:09:09
```

LocalTime 也提供了非常好用的工厂方法：

```java
LocalTime late = LocalTime.of(23, 59, 59);
System.out.println(late);       // 23:59:59
```

#### Parse Strings

从格式化字符串中解析得到时间对象：

```java
DateTimeFormatter germanFormatter =
    DateTimeFormatter
        .ofLocalizedTime(FormatStyle.SHORT)
        .withLocale(Locale.GERMAN);

LocalTime leetTime = LocalTime.parse("13:37", germanFormatter);
System.out.println(leetTime);   // 13:37
```

时间也是按照 ISO 格式识别，但可以识别以下 3 种格式：

- 12:00
- 12:01:02
- 12:01:02.345

### LocalDate

```java
// 取当前日期：
LocalDate today = LocalDate.now(); // -> 2014-12-24
// 根据年月日取日期，12月就是12：
LocalDate crischristmas = LocalDate.of(2014, 12, 25); // -> 2014-12-25
// 根据字符串取：
LocalDate endOfFeb = LocalDate.parse("2014-02-28"); // 严格按照ISO yyyy-MM-dd验证，02写成2都不行，当然也有一个重载方法允许自己定义格式
LocalDate.parse("2014-02-29"); // 无效日期无法通过：DateTimeParseException: Invalid date
```

日期转换：

```java
// 取本月第1天：
LocalDate firstDayOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth()); // 2014-12-01
// 取本月第2天：
LocalDate secondDayOfThisMonth = today.withDayOfMonth(2); // 2014-12-02
// 取本月最后一天，再也不用计算是28，29，30还是31：
LocalDate lastDayOfThisMonth = today.with(TemporalAdjusters.lastDayOfMonth()); // 2014-12-31
// 取下一天：
LocalDate firstDayOf2015 = lastDayOfThisMonth.plusDays(1); // 变成了2015-01-01
// 取2015年1月第一个周一，这个计算用Calendar要死掉很多脑细胞：
LocalDate firstMondayOf2015 = LocalDate.parse("2015-01-01").with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)); // 2015-01-05
```

LocalDate 代表了一个独立的时间类型，譬如 2014-03-11。它是一个不可变的对象并且很类似于 LocalTime。下列代码展示了如何通过增减时间年月来计算日期：

```
LocalDate today = LocalDate.now();
LocalDate tomorrow = today.plus(1, ChronoUnit.DAYS);
LocalDate yesterday = tomorrow.minusDays(2);

LocalDate independenceDay = LocalDate.of(2014, Month.JULY, 4);
DayOfWeek dayOfWeek = independenceDay.getDayOfWeek();
System.out.println(dayOfWeek);    // FRIDAY
```

从字符串解析得到 LocalDate 对象也像 LocalTime 一样简单：

```
DateTimeFormatter germanFormatter =
    DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.GERMAN);

LocalDate xmas = LocalDate.parse("24.12.2014", germanFormatter);
System.out.println(xmas);   // 2014-12-24
```

### LocalDateTime

LocalDateTime 代表了时间日期类型，它组合了上文提到的 Date 类型以及 Time 类型。LocalDateTime 同样也是一种不可变类型，很类似于 LocalTime 以及 LocalDate。

```java
LocalDateTime sylvester = LocalDateTime.of(2014, Month.DECEMBER, 31, 23, 59, 59);

DayOfWeek dayOfWeek = sylvester.getDayOfWeek();
System.out.println(dayOfWeek);      // WEDNESDAY

Month month = sylvester.getMonth();
System.out.println(month);          // DECEMBER

long minuteOfDay = sylvester.getLong(ChronoField.MINUTE_OF_DAY);
System.out.println(minuteOfDay);    // 1439
```

上文中提及的 Instant 也可以用来将时间根据时区转化：

```
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(1450073569l), TimeZone.getDefault().toZoneId());
```

从格式化字符串中解析获取到数据对象，也是非常简单：

```
DateTimeFormatter formatter =
    DateTimeFormatter
        .ofPattern("MMM dd, yyyy - HH:mm");

LocalDateTime parsed = LocalDateTime.parse("Nov 03, 2014 - 07:13", formatter);
String string = formatter.format(parsed);
System.out.println(string);     // Nov 03, 2014 - 07:13
```

```
LocalDateTime ldt = ...
ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
Date output = Date.from(zdt.toInstant());
```

## String(字符串类型)

### 创建增删

#### UUID

[java-uuid-generator](https://github.com/cowtowncoder/java-uuid-generator)

# DS-Collection：集合

## List

### Array

Java 中也内置了许多对于 Array 类型的操作，不过大多是放置在了`Arrays`这个包中。

- 创建增删

**方式 1\*\***(推荐，更能表明数组类型)\*\*

type[] 变量名 = new type[数组中元素的个数];

比如：

int[] a = new int[10];

数组名，也即引用 a，指向数组元素的首地址。

**方式 2\*\***(同 C 语言)\*\*

type 变量名[] = new type[数组中元素的个数];

如：

int a[] = new int[10];

**方式 3 \*\***定义时直接初始化\*\*

type[] 变量名 = new type[]{逗号分隔的初始化值};

其中红色部分可省略，所以又有两种：

int[] a = {1,2,3,4};

int[] a = new int[]{1,2,3,4};

其中 int[] a = new int[]{1,2,3,4};的第二个方括号中不能加上数组长度，因为元素个数是由后面花括号的内容决定的。

**数组长度**

Java 中的每个数组都有一个名为 length 的属性，表示数组的长度。

length 属性是 public final int 的，即 length 是只读的。数组长度一旦确定，就不能改变大小。

**equals()**

数组内容的比较可以使用 equals()方法吗？

如下程序：

[![复制代](http://common.cnblogs.com/images/copycode.gif)](<javascript:void(0);>)

```
public class ArrayTest
{
      public static void main(String[] args)
      {
             int[] a = {1, 2, 3};
             int[] b = {1, 2, 3};

             System.out.println(a.equals(b));
      }
}
```

[![复制代](http://common.cnblogs.com/images/copycode.gif)](<javascript:void(0);>)

输出结果是 false。

所以证明**不能直接用 equals()方法比较数组内容，因为没有 override Object 中的实现，所以仍采用其实现，即采用==实现 equals()方法，比较是否为同一个对象。**

怎么比较呢？一种解决方案是自己写代码，另一种方法是利用**java.util.Arrays**。

java.util.Arrays 中的方法全是 static 的。其中包括了 equals()方法的各种重载版本。

代码如下：

![](http://images.cnblogs.com/OutliningIndicators/ContractedBlock.gif)ArrayEqualsTest.java

**数组元素不为基本数据类型时**

数组元素不为基本原生数据类型时，存放的是引用类型，而不是对象本身。当生成对象之后，引用才指向对象，否则引用为 null。

如下列程序：

![](http://images.cnblogs.com/OutliningIndicators/ContractedBlock.gif)ArrayTest2.java

输出：

null

10

20

30

**也可以在初始化列表里面直接写：**

Person[] p = new Person[]{new Person(10), new Person(20), new Person(30)};

#### 二维数组

二维数组是数组的数组。

**二维数组基础**

基本的定义方式有两种形式，如：

type[][] i = new type[2][3];(推荐)

type i[][] = new type[2][3];

如下程序：

[![复制代](http://common.cnblogs.com/images/copycode.gif)](<javascript:void(0);>)

```
public class ArrayTest3
{
      public static void main(String[] args)
      {

             int[][] i = new int[2][3];

             System.out.println("Is i an Object? "
                           + (i instanceof Object));

             System.out.println("Is i[0] an int[]? "
                           + (i[0] instanceof int[]));

      }
}
```

[![复制代](http://common.cnblogs.com/images/copycode.gif)](<javascript:void(0);>)

输出结果是两个 true。

**变长的二维数组**

二维数组的每个元素都是一个一维数组，这些数组不一定都是等长的。

声明二维数组的时候可以只指定第一维大小，空缺出第二维大小，之后再指定不同长度的数组。但是注意，第一维大小不能空缺(不能只指定列数不指定行数)。

如下程序：

[![复制代](http://common.cnblogs.com/images/copycode.gif)](<javascript:void(0);>)

```
public class ArrayTest4
{
    public static void main(String[] args)
    {
        //二维变长数组
        int[][] a = new int[3][];
        a[0] = new int[2];
        a[1] = new int[3];
        a[2] = new int[1];

        //Error: 不能空缺第一维大小
        //int[][] b = new int[][3];
    }
}
```

[![复制代](http://common.cnblogs.com/images/copycode.gif)](<javascript:void(0);>)

二维数组也可以在定义的时候初始化，使用花括号的嵌套完成，这时候不指定两个维数的大小，并且根据初始化值的个数不同，可以生成不同长度的数组元素。

如下程序：

[![复制代](http://common.cnblogs.com/images/copycode.gif)](<javascript:void(0);>)

```
public class ArrayTest5
{
    public static void main(String[] args)
    {

        int[][] c = new int[][]{{1, 2, 3},{4},{5, 6, 7, 8}};

        for(int i = 0; i < c.length; ++i)
        {
            for(int j = 0; j < c[i].length; ++j)
            {
                System.out.print(c[i][j]+" ");
            }

            System.out.println();
        }

    }
}
```

[![复制代](http://common.cnblogs.com/images/copycode.gif)](<javascript:void(0);>)

输出：

1 2 3

4

5 6 7 8

## Set

## Map

## Comparison(对比)

### Performance(性能)

> [Java 集合类性能分析](http://www.cnblogs.com/supersugar/archive/2012/03/20/2408216.html)

# 流程控制

## 条件选择

## 循环

### while

### for

#### for-in

#### forEach

## 迭代器

## 异常处理

# Method(方法)

Java 是一个完全的面向对象的语言，这也是它为人诟病的一点。

## Lambda

> - [Java8 lambda 表达式 10 个示例](http://www.importnew.com/16436.html)

Lambda 表达式本身是构造了一个继承自某个函数式接口的子类，所以可以用父类指针指向它：

```
Runnable task = () -> {
    // do something
};

Comparator<String> cmp = (s1, s2) -> {
    return Integer.compare(s1.length(), s2.length());
};
```

#### 方法引用

​ 有时候 Lambda 表达式的代码就只是一个简单的方法调用而已，遇到这种情况，Lambda 表达式还可以进一步简化为 [方法引用(Method References)](http://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html) 。一共有四种形式的方法引用，第一种引用 **静态方法** ，例如：

```
List<Integer> ints = Arrays.asList(1, 2, 3);
ints.sort(Integer::compare);
```

第二种引用

某个特定对象的实例方法

，例如前面那个遍历并打印每一个 word 的例子可以写成这样：

```
words.forEach(System.out::println);
```

第三种引用

某个类的实例方法

，例如：

```
words.stream().map(word -> word.length()); // lambda
words.stream().map(String::length); // method reference
```

​ 第四种引用类的 **构造函数** ，例如：

```
// lambda
words.stream().map(word -> {
    return new StringBuilder(word);
});
// constructor reference
words.stream().map(StringBuilder::new);
```

### Variable Scope(变量作用域)

#### Lambda 表达式的作用域

lambda 表达式的方法体与嵌套代码块有着相同的作用域。因此它也适用同样的命名冲突和屏蔽规则。在 lambda 表达式中不允许声明一个与局部变量同名的参数或者局部变量。

```
Path first = Paths.get("/usr/bin");
Comparator<String> comp = (first,second) ->
    Integer.compare(first.length(),second.length());
//错误，变量first已经定义了
```

在一个方法里，你不能有两个同名的局部变量，因此，你也不能在 lambda 表达式中引入这样的变量。

当你在 lambda 表达式中使用 this 关键字，你会引用创建该 lambda 表达式的方法的 this 参数，以下面的代码为例：

```
public class Application{
    public void doWork(){
        Runnable runner = () -> {....;System.out.println(this.toString());......};
    }
}
```

表达式 this.toString()会调用 Application 对象的 toString()方法，而不是 Runnable 实例的 toString()方法。在 lambda 表达式中使用 this，与在其他地方使用 this 没有什么不同。lambda 表达式的作用域被嵌套在 doWork()方法中，并且无论 this 位于方法的何处，其意义都是一样的。

#### 闭合作用域的变量访问

- 本地变量(Local Variable)可以访问但是不可以修改。
- 类成员变量与静态变量可以被读写。
- 函数式接口的默认方法不可以在 Lambda 表达式中被访问。

通常，我们希望能够在 lambda 表达式的闭合方法或类中访问其他的变量，例如：

```java
package java8test;

public class T1 {
    public static void main(String[] args) {
        repeatMessage("Hello", 20);
    }
    public static void repeatMessage(String text,int count){
        Runnable r = () -> {
            for(int i = 0; i < count; i++){
                System.out.println(text);
                Thread.yield();
            }
        };
        new Thread(r).start();
    }
}
```

注意看 lambda 表达式中的变量 count 和 text，它们并没有在 lambda 表达式中被定义，而是方法 repeatMessage 的参数变量。如果你思考一下，就会发现这里有一些隐含的东西。lambda 表达式可能会在 repeatMessage 返回之后才运行，此时参数变量已经消失了。如果保留 text 和 count 变量会怎样呢？

为了理解这一点，我们需要对 lambda 表达式有更深入的理解。一个 lambda 表达式包括三个部分：

- 一段代码
- 参数
- 自由变量的值，这里的“自由”指的是那些不是参数并且没有在代码中定义的变量。

在我们的示例中，lambda 表达式有两个自由变量，text 和 count。数据结构表示 lambda 表达式必须存储这两个变量的值，即“Hello”和 20。我们可以说，这些值已经被 lambda 表达式捕获了(这是一个技术实现的细节。例如，你可以将一个 lambda 表达式转换为一个只含一个方法的对象，这样自由变量的值就会被复制到该对象的实例变量中)。

**注意**：含有自由变量的代码块才被称之为“闭包(closure)”。在 Java 中，lambda 表达式就是闭包。事实上，内部类一直都是闭包。Java8 中为闭包赋予了更吸引人的语法。

如你所见，lambda 表达式可以捕获闭合作用域中的变量值。在 java 中，为了确保被捕获的值是被良好定义的，需要遵守一个重要的约束。在 lambda 表达式中，被引用的变量的值不可以被更改。例如，下面这个表达式是不合法的：

```java
public static void repeatMessage(String text,int count){
    Runnable r = () -> {
        while(count > 0){
            count--;        //错误，不能更改已捕获变量的值
            System.out.println(text);
            Thread.yield();
         }
     };
     new Thread(r).start();
}
```

做出这个约束是有原因的。更改 lambda 表达式中的变量不是线程安全的。假设有一系列并发的任务，每个线程都会更新一个共享的计数器。

```java
int matches = 0;
for(Path p : files)
    new Thread(() -> {if(p中包含某些属性) matches++;}).start();    //非法更改matches的值
```

如果这段代码是合法的，那么会引起十分糟糕的结果。自增操作 matches++不是原子操作，如果多个线程并发执行该自增操作，天晓得会发生什么。

不要指望编译器会捕获所有并发访问错误。不可变的约束只作用在局部变量上，如果 matches 是一个实例变量或者闭合类的静态变量，那么不会有任何错误被报告出来即使结果同样未定义。同样，改变一个共享对象也是完全合法的，即使这样并不恰当。例如：

```java
List<Path> matches = new ArrayList<>();
for(Path p: files)
//你可以改变matches的值，但是在多线程下是不安全的
    new Thread(() -> {if(p中包含某些属性) matches.add(p);}).start();
```

注意 matches 是“有效 final”的(一个有效的 final 变量被初始化后，就永远不会再被赋一个新值的变量)。在我们的示例中，matches 总是引用同一个 ArrayList 对象，但是，这个对象是可变的，因此是线程不安全的 。如果多个线程同时调用 add 方法，结果将无法预测。

## Streams

> - [Java8 中的 Stream API 详解](http://www.ibm.com/developerworks/cn/java/j-lo-java8streamapi/)
>
> * [side-effects-and-java-8-streams:Java 8 Stream 的部分副作用](https://www.voxxed.com/blog/2015/10/side-effects-and-java-8-streams/#comments)
> * [Follow-up: How fast are the Java 8 Streams:Java 8 流接口的性能评测](https://jaxenter.com/follow-up-how-fast-are-the-java-8-streams-122522.html)

java.util.Stream 表示了某一种元素的序列，在这些元素上可以进行各种操作。Stream 操作可以是中间操作，也可以是完结操作。完结操作会返回一个某种类型的值，而中间操作会返回流对象本身，并且你可以通过多次调用同一个流操作方法来将操作结果串起来(就像 StringBuffer 的 append 方法一样————译者注)。Stream 是在一个源的基础上创建出来的，例如 java.util.Collection 中的 list 或者 set(map 不能作为 Stream 的源)。Stream 操作往往可以通过顺序或者并行两种方式来执行。

首先以 String 类型的 List 的形式创建流：

```java
List<String> stringCollection = new ArrayList<>();
stringCollection.add("ddd2");
stringCollection.add("aaa2");
stringCollection.add("bbb1");
stringCollection.add("aaa1");
stringCollection.add("bbb3");
stringCollection.add("ccc");
stringCollection.add("bbb2");
stringCollection.add("ddd1");

//直接从数组创建流
int m = Arrays.stream(ints)
              .reduce(Integer.MIN_VALUE, Math::max);
```

- Intermediate：

map (mapToInt, flatMap 等)、 filter、 distinct、 sorted、 peek、 limit、 skip、 parallel、 sequential、 unordered

- Terminal：

forEach、 forEachOrdered、 toArray、 reduce、 collect、 min、 max、 count、 anyMatch、 allMatch、 noneMatch、 findFirst、 findAny、 iterator

- Short-circuiting：

anyMatch、 allMatch、 noneMatch、 findFirst、 findAny、 limit

### Intermediate

#### Filter

Filter 接受一个 predicate 接口类型的变量，并将所有流对象中的元素进行过滤。该操作是一个中间操作，因此它允许我们在返回结果的基础上再进行其他的流操作(forEach)。ForEach 接受一个 function 接口类型的变量，用来执行对每一个元素的操作。ForEach 是一个中止操作。它不返回流，所以我们不能再调用其他的流操作。

```java
stringCollection
    .stream()
    .filter((s) -> s.startsWith("a"))
    .forEach(System.out::println);
// "aaa2", "aaa1"
```

#### Sorted

```
Sorted是一个中间操作，能够返回一个排过序的流对象的视图。流对象中的元素会默认按照自然顺序进行排序，除非你自己指定一个Comparator接口来改变排序规则。
```

```java
stringCollection
    .stream()
    .sorted()
    .filter((s) -> s.startsWith("a"))
    .forEach(System.out::println);
// "aaa1", "aaa2"
```

```
一定要记住，sorted只是创建一个流对象排序的视图，而不会改变原来集合中元素的顺序。原来string集合中的元素顺序是没有改变的。
```

```java
System.out.println(stringCollection);
// ddd2, aaa2, bbb1, aaa1, bbb3, ccc, bbb2, ddd1
```

#### Map

```
map是一个对于流对象的中间操作，通过给定的方法，它能够把流对象中的每一个元素对应到另外一个对象上。下面的例子就演示了如何把每个string都转换成大写的string. 不但如此，你还可以把每一种对象映射成为其他类型。对于带泛型结果的流对象，具体的类型还要由传递给map的泛型方法来决定。
```

```java
stringCollection
    .stream()
    .map(String::toUpperCase)
    .sorted((a, b) -> b.compareTo(a))
    .forEach(System.out::println);
```

#### Match

```
匹配操作有多种不同的类型，都是用来判断某一种规则是否与流对象相互吻合的。所有的匹配操作都是终结操作，只返回一个boolean类型的结果。
```

```java
boolean anyStartsWithA =
    stringCollection
        .stream()
        .anyMatch((s) -> s.startsWith("a"));

System.out.println(anyStartsWithA);      // true

boolean allStartsWithA =
    stringCollection
        .stream()
        .allMatch((s) -> s.startsWith("a"));

System.out.println(allStartsWithA);      // false

boolean noneStartsWithZ =
    stringCollection
        .stream()
        .noneMatch((s) -> s.startsWith("z"));

System.out.println(noneStartsWithZ);      // true
```

### Terminal

#### Count

```
Count是一个终结操作，它的作用是返回一个数值，用来标识当前流对象中包含的元素数量。
```

```java
long startsWithB =
    stringCollection
        .stream()
        .filter((s) -> s.startsWith("b"))
        .count();

System.out.println(startsWithB);    // 3
```

#### Reduce

```
该操作是一个终结操作，它能够通过某一个方法，对元素进行削减操作。该操作的结果会放在一个Optional变量里返回。
```

```java
Optional<String> reduced =
    stringCollection
        .stream()
        .sorted()
        .reduce((s1, s2) -> s1 + "#" + s2);

reduced.ifPresent(System.out::println);
// "aaa1#aaa2#bbb1#bbb2#bbb3#ccc#ddd1#ddd2"
```

#### Collect

```
java.util.stream.Collectors 类的主要作用就是辅助进行各类有用的 reduction 操作，例如转变输出为 Collection，把 Stream  元素进行归组。
```

```java
     // Accumulate names into a  List
     List<String> list = people.stream().map(Person::getName).collect(Collectors.toList());

     // Accumulate names into a TreeSet
     Set<String> set = people.stream().map(Person::getName).collect(Collectors.toCollection(TreeSet::new));

     // Convert elements to strings and concatenate them, separated by commas
     String joined = things.stream()
                           .map(Object::toString)
                           .collect(Collectors.joining(", "));

     // Compute sum of salaries of employee
     int total = employees.stream()
                          .collect(Collectors.summingInt(Employee::getSalary)));

     // Group employees by department
     Map<Department, List<Employee>> byDept
         = employees.stream()
                    .collect(Collectors.groupingBy(Employee::getDepartment));

     // Compute sum of salaries by department
     Map<Department, Integer> totalByDept
         = employees.stream()
                    .collect(Collectors.groupingBy(Employee::getDepartment,
                                                   Collectors.summingInt(Employee::getSalary)));

     // Partition students into passing and failing
     Map<Boolean, List<Student>> passingFailing =
         students.stream()
                 .collect(Collectors.partitioningBy(s -> s.getGrade() >= PASS_THRESHOLD));
```

##### groupingBy/partitioningBy

- 按照年龄归组

```java
Map<Integer, List<Person>> personGroups = Stream.generate(new PersonSupplier()).
 limit(100).
 collect(Collectors.groupingBy(Person::getAge));
Iterator it = personGroups.entrySet().iterator();
while (it.hasNext()) {
 Map.Entry<Integer, List<Person>> persons = (Map.Entry) it.next();
 System.out.println("Age " + persons.getKey() + " = " + persons.getValue().size());
}
```

上面的 code，首先生成 100 人的信息，然后按照年龄归组，相同年龄的人放到同一个 list 中，可以看到如下的输出：

```
Age 0 = 2
Age 1 = 2
Age 5 = 2
Age 8 = 1
Age 9 = 1
Age 11 = 2
……
```

- ##### 按照未成年人和成年人归组

```java
Map<Boolean, List<Person>> children = Stream.generate(new PersonSupplier()).
 limit(100).
 collect(Collectors.partitioningBy(p -> p.getAge() < 18));
System.out.println("Children number: " + children.get(true).size());
System.out.println("Adult number: " + children.get(false).size());
```

输出结果：

```
     Children number: 23
     Adult number: 77
```

```
在使用条件“年龄小于 18”进行分组后可以看到，不到 18 岁的未成年人是一组，成年人是另外一组。partitioningBy 其实是一种特殊的    groupingBy，它依照条件测试的是否两种结果来构造返回的数据结构，get(true) 和 get(false) 能即为全部的元素对象。
```

### Parallel Streams

# Class：类与对象

## Reflect & ClassLoader

> [Java 类加载与初始化](http://www.cnblogs.com/zhguang/p/3154584.html)

```
常说的反射，即是能够根据类名加载对应的类。在Java中，反射往往是利用ClassLoader进行，而一般来说Class装载的三个阶段为：
```

- 载入 (Load)

```
从Class文件或别的什么地方载入一段二进制流字节流，把它解释成永久代里的运行时数据结构，生成一个Class对象。
```

- 链接 (Resolve)

```
将之前载入的数据结构里的符号引用表，解析成直接引用。中间如果遇到引用的类还没被加载，就会触发该类的加载。可能JDK会很懒惰的在运行某个函数实际使用到该引用时才发生链接，也可能在类加载时就解析全部引用。
```

- 初始化 (Initniazle)

```
初始化静态变量，并执行静态初始化语句。

ClassLoader.loadClass(String name, boolean resolve)，其中resolve默认为false，即只执行类装载的第一个阶段。Class.forName(String name, boolean initialize, ClassLoader loader)， 其中initialize默认为true，即执行到类装载的第三个阶段。
```

关于类与类装载的详细分析可以查看 JVM 部分的对应章节。

### Jar 包预加载

```java
URL jarUrl = ClassA.getProtectionDomain().getCodeSource().getLocation();
JarFile jarfile = new JarFile(jarUrl.getPath());
Enumeration entries = jarfile.entries();
```

## Interface & Abstract Class

类实现接口，接口继承接口。

### Functional Interface(函数式接口)

Java 原本作为纯粹的面向对象的语言，需要对 Lambda 表达式特性进行支持，其实是基于了一种特殊的函数式接口。换言之，()->{}这样的语法本质上还是继承并且实现了一个接口。FI 的定义其实很简单：任何接口，如果只包含 **唯一** 一个抽象方法，那么它就是一个 FI。为了让编译器帮助我们确保一个接口满足 FI 的要求(也就是说有且仅有一个抽象方法)，Java8 提供了@FunctionalInterface 注解。举个简单的例子，Runnable 接口就是一个 FI，下面是它的源代码：

```java
@FunctionalInterface
public interface Runnable {
    public abstract void run();
}
```

### Built-in Functional Interface(内置函数式接口)

#### Predicate

Predicate 是一个布尔类型的函数，该函数只有一个输入参数。Predicate 接口包含了多种默认方法，用于处理复杂的逻辑动词(and, or，negate)

```java
Predicate<String> predicate = (s) -> s.length() > 0;

predicate.test("foo");              // true
predicate.negate().test("foo");     // false

Predicate<Boolean> nonNull = Objects::nonNull;
Predicate<Boolean> isNull = Objects::isNull;

Predicate<String> isEmpty = String::isEmpty;
Predicate<String> isNotEmpty = isEmpty.negate();
```

#### Comparators

Comparator 接口在早期的 Java 版本中非常著名。Java 8 为这个接口添加了不同的默认方法。

```java
Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);

Person p1 = new Person("John", "Doe");
Person p2 = new Person("Alice", "Wonderland");

comparator.compare(p1, p2);             // > 0
comparator.reversed().compare(p1, p2);  // < 0
```

### Annotation(注解)

![](http://images.cnitblog.com/blog/34483/201304/25200814-475cf2f3a8d24e0bb3b4c442a4b44734.jpg)

元注解的作用就是负责注解其他注解。Java5.0 定义了 4 个标准的 meta-annotation 类型，它们被用来提供对其它 annotation 类型作说明。Java5.0 定义的元注解：

1.@Target,

2.@Retention,

3.@Documented,

4.@Inherited

这些类型和它们所支持的类在 java.lang.annotation 包中可以找到。下面我们看一下每个元注解的作用和相应分参数的使用说明。

**@Target：**

@Target 说明了 Annotation 所修饰的对象范围：Annotation 可被用于 packages、types(类、接口、枚举、Annotation 类型)、类型成员(方法、构造方法、成员变量、枚举值)、方法参数和本地变量(如循环变量、catch 参数)。在 Annotation 类型的声明中使用了 target 可更加明晰其修饰的目标。

\*\*　\*\*　**作用：用于描述注解的使用范围(即：被描述的注解可以用在什么地方)**

**　取值(ElementType)有：**

1.CONSTRUCTOR:用于描述构造器

2.FIELD:用于描述域

3.LOCAL_VARIABLE:用于描述局部变量

4.METHOD:用于描述方法

5.PACKAGE:用于描述包

6.PARAMETER:用于描述参数

7.TYPE:用于描述类、接口(包括注解类型) 或 enum 声明

```java
@Target(ElementType.TYPE)
public @interface Table {
    /**
     * 数据表名称注解，默认值为类名称
     * @return
     */
    public String tableName() default "className";
}

@Target(ElementType.FIELD)
public @interface NoDBColumn {

}
```

注解 Table 可以用于注解类、接口(包括注解类型) 或 enum 声明,而注解 NoDBColumn 仅可用于注解类的成员变量。

**@Retention：**

**　@Retention**定义了该 Annotation 被保留的时间长短：某些 Annotation 仅出现在源代码中，而被编译器丢弃；而另一些却被编译在 class 文件中；编译在 class 文件中的 Annotation 可能会被虚拟机忽略，而另一些在 class 被装载时将被读取(请注意并不影响 class 的执行，因为 Annotation 与 class 在使用上是被分离的)。使用这个 meta-Annotation 可以对 Annotation 的“生命周期”限制。

**作用：表示需要在什么级别保存该注释信息，用于描述注解的生命周期(即：被描述的注解在什么范围内有效)**

**　取值(RetentionPoicy)有：**

1.SOURCE:在源文件中有效(即源文件保留)

2.CLASS:在 class 文件中有效(即 class 保留)

3.RUNTIME:在运行时有效(即运行时保留)

Retention meta-annotation 类型有唯一的 value 作为成员，它的取值来自 java.lang.annotation.RetentionPolicy 的枚举类型值。具体实例如下：

```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    public String name() default "fieldName";
    public String setFuncName() default "setField";
    public String getFuncName() default "getField";
    public boolean defaultDBValue() default false;
}
```

Column 注解的的 RetentionPolicy 的属性值是 RUTIME,这样注解处理器可以通过反射，获取到该注解的属性值，从而去做一些运行时的逻辑处理

**@Documented:**

**　　@**Documented 用于描述其它类型的 annotation 应该被作为被标注的程序成员的公共 API，因此可以被例如 javadoc 此类的工具文档化。Documented 是一个标记注解，没有成员。

```
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    public String name() default "fieldName";
    public String setFuncName() default "setField";
    public String getFuncName() default "getField";
    public boolean defaultDBValue() default false;
}
```

**@Inherited：**

\*\*　　\*\*@Inherited 元注解是一个标记注解，@Inherited 阐述了某个被标注的类型是被继承的。如果一个使用了@Inherited 修饰的 annotation 类型被用于一个 class，则这个 annotation 将被用于该 class 的子类。

注意：@Inherited annotation 类型是被标注过的 class 的子类所继承。类并不从它所实现的接口继承 annotation，方法并不从它所重载的方法继承 annotation。

当@Inherited annotation 类型标注的 annotation 的 Retention 是 RetentionPolicy.RUNTIME，则反射 API 增强了这种继承性。如果我们使用 java.lang.reflect 去查询一个@Inherited annotation 类型的 annotation 时，反射代码检查将展开工作：检查 class 和其父类，直到发现指定的 annotation 类型被发现，或者到达类继承结构的顶层。

实例代码：

```
/**
 *
 * @author peida
 *
 */
@Inherited
public @interface Greeting {
    public enum FontColor{ BULE,RED,GREEN};
    String name();
    FontColor fontColor() default FontColor.GREEN;
}
```

**自定义注解：**

使用@interface 自定义注解时，自动继承了 java.lang.annotation.Annotation 接口，由编译程序自动完成其他细节。在定义注解时，不能继承其他的注解或接口。@interface 用来声明一个注解，其中的每一个方法实际上是声明了一个配置参数。方法的名称就是参数的名称，返回值类型就是参数的类型(返回值类型只能是基本类型、Class、String、enum)。可以通过 default 来声明参数的默认值。

**定义注解格式：**

public @interface 注解名 {定义体}

\*　　**\*注解参数的可支持数据类型：**

1.所有基本数据类型(int,float,boolean,byte,double,char,long,short)

2.String 类型

3.Class 类型

4.enum 类型

5.Annotation 类型

6.以上所有类型的数组

Annotation 类型里面的参数该怎么设定:

第一,只能用 public 或默认(default)这两个访问权修饰.例如,String value();这里把方法设为 defaul 默认类型；

第二,参数成员只能用基本类型 byte,short,char,int,long,float,double,boolean 八种基本数据类型和

String,Enum,Class,annotations 等数据类型,以及这一些类型的数组.例如,String

value();这里的参数成员就为 String;

第三,如果只有一个参数成员,最好把参数名称设为"value",后加小括号.例:下面的例子 FruitName 注解就只有一个参数成员。

简单的自定义注解和使用注解实例：

```java
package annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 水果名称注解
 * @author peida
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FruitName {
    String value() default "";
}
```

```java
package annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 水果颜色注解
 * @author peida
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FruitColor {
    /**
     * 颜色枚举
     * @author peida
     *
     */
    public enum Color{ BULE,RED,GREEN};

    /**
     * 颜色属性
     * @return
     */
    Color fruitColor() default Color.GREEN;

}
```

```
package annotation;

import annotation.FruitColor.Color;

public class Apple {

    @FruitName("Apple")
    private String appleName;

    @FruitColor(fruitColor=Color.RED)
    private String appleColor;




    public void setAppleColor(String appleColor) {
        this.appleColor = appleColor;
    }
    public String getAppleColor() {
        return appleColor;
    }


    public void setAppleName(String appleName) {
        this.appleName = appleName;
    }
    public String getAppleName() {
        return appleName;
    }

    public void displayName(){
        System.out.println("水果的名字是：苹果");
    }
}
```

# Common APIs

## IO

### Console

````
​```

> 参数处理函数

​``` java
    /**
     * a function that assembles all the arguments into a single string so that
     * we can call a method like search
     *
     * @param commandTokens
     *            - a string array that corresponds to what the user entered
     * @return a string that can be passed on to functions like search, serve
     *         etc
     */
    public static String makeArguments(String[] commandTokens) {
        String completeStringArgument = "";
        for (int i = 1; i < commandTokens.length - 1; i++) {
            completeStringArgument += commandTokens[i] + " ";
        }
        return completeStringArgument + commandTokens[commandTokens.length - 1];
    }
````

#### 注解处理器

Java 使用 Annotation 接口来代表程序元素前面的注解，该接口是所有 Annotation 类型的父接口。除此之外，Java 在 java.lang.reflect 包下新增了 AnnotatedElement 接口，该接口代表程序中可以接受注解的程序元素，该接口主要有如下几个实现类：

Class：类定义

Constructor：构造器定义

Field：累的成员变量定义

Method：类的方法定义

Package：类的包定义

java.lang.reflect 包下主要包含一些实现反射功能的工具类，实际上，java.lang.reflect

包所有提供的反射 API 扩充了读取运行时 Annotation 信息的能力。当一个 Annotation 类型被定义为运行时的 Annotation 后，该注

解才能是运行时可见，当 class 文件被装载时被保存在 class 文件中的 Annotation 才会被虚拟机读取。

AnnotatedElement

接口是所有程序元素(Class、Method 和 Constructor)的父接口，所以程序通过反射获取了某个类的 AnnotatedElement 对

象之后，程序就可以调用该对象的如下四个个方法来访问 Annotation 信息：

方法 1：<T extends Annotation> T getAnnotation(Class<T> annotationClass): 返回改程序元素上存在的、指定类型的注解，如果该类型注解不存在，则返回 null。

方法 2：Annotation[] getAnnotations():返回该程序元素上存在的所有注解。

方法 3：boolean is AnnotationPresent(Class<?extends Annotation> annotationClass):判断该程序元素上是否包含指定类型的注解，存在则返回 true，否则返回 false.

方法 4：Annotation[]

getDeclaredAnnotations()：返回直接存在于此元素上的所有注释。与此接口中的其他方法不同，该方法将忽略继承的注释。(如果没有

注释直接存在于此元素上，则返回长度为零的一个数组。)该方法的调用者可以随意修改返回的数组；这不会对其他调用者返回的数组产生任何影响。

一个简单的注解处理器：

```java
/***********注解声明***************/

/**
 * 水果名称注解
 * @author peida
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FruitName {
    String value() default "";
}

/**
 * 水果颜色注解
 * @author peida
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FruitColor {
    /**
     * 颜色枚举
     * @author peida
     *
     */
    public enum Color{ BULE,RED,GREEN};

    /**
     * 颜色属性
     * @return
     */
    Color fruitColor() default Color.GREEN;

}

/**
 * 水果供应者注解
 * @author peida
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FruitProvider {
    /**
     * 供应商编号
     * @return
     */
    public int id() default -1;

    /**
     * 供应商名称
     * @return
     */
    public String name() default "";

    /**
     * 供应商地址
     * @return
     */
    public String address() default "";
}

/***********注解使用***************/

public class Apple {

    @FruitName("Apple")
    private String appleName;

    @FruitColor(fruitColor=Color.RED)
    private String appleColor;

    @FruitProvider(id=1,name="陕西红富士集团",address="陕西省西安市延安路89号红富士大厦")
    private String appleProvider;

    public void setAppleColor(String appleColor) {
        this.appleColor = appleColor;
    }
    public String getAppleColor() {
        return appleColor;
    }

    public void setAppleName(String appleName) {
        this.appleName = appleName;
    }
    public String getAppleName() {
        return appleName;
    }

    public void setAppleProvider(String appleProvider) {
        this.appleProvider = appleProvider;
    }
    public String getAppleProvider() {
        return appleProvider;
    }

    public void displayName(){
        System.out.println("水果的名字是：苹果");
    }
}

/***********注解处理器***************/

public class FruitInfoUtil {
    public static void getFruitInfo(Class<?> clazz){

        String strFruitName=" 水果名称：";
        String strFruitColor=" 水果颜色：";
        String strFruitProvicer="供应商信息：";

        Field[] fields = clazz.getDeclaredFields();

        for(Field field :fields){
            if(field.isAnnotationPresent(FruitName.class)){
                FruitName fruitName = (FruitName) field.getAnnotation(FruitName.class);
                strFruitName=strFruitName+fruitName.value();
                System.out.println(strFruitName);
            }
            else if(field.isAnnotationPresent(FruitColor.class)){
                FruitColor fruitColor= (FruitColor) field.getAnnotation(FruitColor.class);
                strFruitColor=strFruitColor+fruitColor.fruitColor().toString();
                System.out.println(strFruitColor);
            }
            else if(field.isAnnotationPresent(FruitProvider.class)){
                FruitProvider fruitProvider= (FruitProvider) field.getAnnotation(FruitProvider.class);
                strFruitProvicer=" 供应商编号："+fruitProvider.id()+" 供应商名称："+fruitProvider.name()+" 供应商地址："+fruitProvider.address();
                System.out.println(strFruitProvicer);
            }
        }
    }
}

/***********输出结果***************/
public class FruitRun {

    /**
     * @param args
     */
    public static void main(String[] args) {

        FruitInfoUtil.getFruitInfo(Apple.class);

    }

}

====================================
 水果名称：Apple
 水果颜色：RED
 供应商编号：1 供应商名称：陕西红富士集团 供应商地址：陕西省西安市延安路89号红富士大厦
```

# Reference

## News

> - [ImportNews:专注 Java&Android](http://www.importnew.com/)​

## Interview

- [Java 高级开发工程师面试考点](http://www.sanesee.com/article/java-engineer-interview-of-content-tree)
