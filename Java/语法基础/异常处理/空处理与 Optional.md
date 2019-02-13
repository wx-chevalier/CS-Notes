# 基本类型(Basic)

## 可选类型(Optional)

> [Java 8 Optional 类深度解析](http://www.importnew.com/6675.html) > [Optional Improvements in Java 9](http://iteratrlearning.com/java9/2016/09/05/java9-optional.html)

Optional 不是一个函数式接口，而是一个精巧的工具接口，用来防止 NullPointerEception 产生。这个概念在下一节会显得很重要，所以我们在这里快速地浏览一下 Optional 的工作原理。Optional 是一个简单的值容器，这个值可以是 null，也可以是 non-null。考虑到一个方法可能会返回一个 non-null 的值，也可能返回一个空值。为了不直接返回 null，我们在 Java 8 中就返回一个 Optional.

```java
Optional<String> optional = Optional.of("bam");

optional.isPresent();           // true
optional.get();                 // "bam"
optional.orElse("fallback");    // "bam"

optional.ifPresent((s) -> System.out.println(s.charAt(0)));     // "b"
```

# DS Utils

## 类型检查

### [Checker](http://types.cs.washington.edu/checker-framework/current/checker-framework-manual.html#nullness-checks):

他们返回类型的不同是最大的原因:

static int parseInt(String s)
将字符串参数作为有符号的十进制整数进行分析。

static Integer valueOf(int i)
返回一个表示指定的 int 值的 Integer 实例。
static Integer valueOf(String s)
返回保持指定的 String 的值的 Integer 对象。

从返回值可以看出他们的区别 parseInt()返回的是基本类型 int
而 valueOf()返回的是包装类 Integer Integer 是可以使用对象方法的 而 int 类型就不能和 Object 类型进行互相转换

Integer.parseInt(chuan)返回值是 int 型的.
Integer.valueOf(chuan)返回值是 Integer 型的.把 Integer 赋值给 int 型的话,JRE 会自己完成这些工作.

区别还是有的.如果你写一个方法的形参是 int 型的,比如:
void test(int a){
//todo:
};
当你调用这个方法的时候 test(Integer.parseInt(chuan))会翻译通过,但 test(Integer.valueOf(chuan))会翻译错误.
