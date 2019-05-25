# Lambda

闭包一般指存在自由变量的代码块，它与对象类似，都是用来描述一段代码与其环境的关系。在 Java 中，Lambda 表达式就是闭包。事实上，内部类一直都是闭包，而 Java8 中为闭包赋予了更吸引人的语法。一个 Lambda 表达式包括三个部分：一段代码、参数、自由变量的值，这里的“自由”指的是那些不是参数并且没有在代码中定义的变量。

Lambda 表达式本身是构造了一个继承自某个函数式接口的子类，所以可以用父类指针指向它。Java 中本质上闭包中是采用的值捕获，即不可以在闭包中使用可变对象。但是它实际上是允许捕获事实上不变量，譬如不可变的 ArrayList，只是指针指向不可变罢了。虽然实现用的是值捕获，但效果看起来跟引用捕获一样；就算以后的 Java 扩展到允许通用的(对可变变量的)引用捕获，也不会跟已有的代码发生不兼容。

Java 中最常见的闭包的使用如下所示：

```java
Runnable task = () -> {
    // do something
};

Comparator<String> cmp = (s1, s2) -> {
    return Integer.compare(s1.length(), s2.length());
};
```

# 方法引用

有时候 Lambda 表达式的代码就只是一个简单的方法调用而已，遇到这种情况，Lambda 表达式还可以进一步简化为方法引用(Method References)。一共有四种形式的方法引用：

- 静态方法引用

```java
List<Integer> ints = Arrays.asList(1, 2, 3);
ints.sort(Integer::compare);
```

- 某个特定对象的实例方法

例如前面那个遍历并打印每一个 word 的例子可以写成这样：

```java
words.forEach(System.out::println);
```

- 某个类的实例方法

```java
words.stream().map(word -> word.length()); // lambda
words.stream().map(String::length); // method reference
```

- 构造函数引用

```java
// lambda
words.stream().map(word -> {
    return new StringBuilder(word);
});

// constructor reference
words.stream().map(StringBuilder::new);
```

# Variable Scope | 变量作用域

在 Lambda 中，变量的作用域与访问操作主要遵循以下规则：

- 本地变量(Local Variable)可以访问但是不可以修改。
- 类成员变量与静态变量可以被读写，即闭包中的 this 实际指向的是创建该 Lambda 表达式的方法的 this 参数。
- 函数式接口的默认方法不可以在 Lambda 表达式中被访问。

## 局部变量

lambda 表达式的方法体与嵌套代码块有着相同的作用域。因此它也适用同样的命名冲突和屏蔽规则。在 lambda 表达式中不允许声明一个与局部变量同名的参数或者局部变量。

```java
Path first = Paths.get("/usr/bin");
Comparator<String> comp = (first,second) ->
    Integer.compare(first.length(),second.length());
//错误，变量first已经定义了
```

在一个方法里，你不能有两个同名的局部变量，因此，你也不能在 lambda 表达式中引入这样的变量。在下一个示例中，lambda 表达式有两个自由变量，text 和 count。数据结构表示 lambda 表达式必须存储这两个变量的值，即“Hello”和 20。我们可以说，这些值已经被 lambda 表达式捕获了(这是一个技术实现的细节。例如，你可以将一个 lambda 表达式转换为一个只含一个方法的对象，这样自由变量的值就会被复制到该对象的实例变量中)。

```java
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

## this

当你在 lambda 表达式中使用 this 关键字，你会引用创建该 lambda 表达式的方法的 this 参数，以下面的代码为例：

```java
public class Application{
    public void doWork(){
        Runnable runner = () -> { System.out.println(this.toString()); };
    }
}
```

表达式 this.toString()会调用 Application 对象的 toString()方法，而不是 Runnable 实例的 toString()方法。在 lambda 表达式中使用 this，与在其他地方使用 this 没有什么不同。lambda 表达式的作用域被嵌套在 doWork()方法中，并且无论 this 位于方法的何处，其意义都是一样的。

## 引用的变量不可更改

Lambda 表达式可以捕获闭合作用域中的变量值。在 Java 中，为了确保被捕获的值是被良好定义的，需要遵守一个重要的约束。在 lambda 表达式中，被引用的变量的值不可以被更改。例如，下面这个表达式是不合法的：

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

如果这段代码是合法的，那么会引起十分糟糕的结果。自增操作 matches++不是原子操作，如果多个线程并发执行该自增操作，天晓得会发生什么。不要指望编译器会捕获所有并发访问错误。不可变的约束只作用在局部变量上，如果 matches 是一个实例变量或者闭合类的静态变量，那么不会有任何错误被报告出来即使结果同样未定义。同样，改变一个共享对象也是完全合法的，即使这样并不恰当。例如：

```java
List<Path> matches = new ArrayList<>();
for(Path p: files)
    // 你可以改变matches的值，但是在多线程下是不安全的
    new Thread(() -> {if(p中包含某些属性) matches.add(p);}).start();
```

注意 matches 是“有效 final”的(一个有效的 final 变量被初始化后，就永远不会再被赋一个新值的变量)。在我们的示例中，matches 总是引用同一个 ArrayList 对象，但是，这个对象是可变的，因此是线程不安全的 。如果多个线程同时调用 add 方法，结果将无法预测。
