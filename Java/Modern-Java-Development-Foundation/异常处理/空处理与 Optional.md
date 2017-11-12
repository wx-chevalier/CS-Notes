# 基本类型(Basic)

## 可选类型(Optional)
> [Java 8 Optional类深度解析](http://www.importnew.com/6675.html)
> [Optional Improvements in Java 9](http://iteratrlearning.com/java9/2016/09/05/java9-optional.html)

Optional不是一个函数式接口，而是一个精巧的工具接口，用来防止NullPointerEception产生。这个概念在下一节会显得很重要，所以我们在这里快速地浏览一下Optional的工作原理。Optional是一个简单的值容器，这个值可以是null，也可以是non-null。考虑到一个方法可能会返回一个non-null的值，也可能返回一个空值。为了不直接返回null，我们在Java 8中就返回一个Optional.

``` java
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

从返回值可以看出他们的区别   parseInt()返回的是基本类型int 
而valueOf()返回的是包装类Integer  Integer是可以使用对象方法的  而int类型就不能和Object类型进行互相转换 

Integer.parseInt(chuan)返回值是int型的. 
Integer.valueOf(chuan)返回值是Integer型的.把Integer赋值给int型的话,JRE会自己完成这些工作. 

区别还是有的.如果你写一个方法的形参是int型的,比如: 
void test(int a){ 
//todo: 
}; 
当你调用这个方法的时候test(Integer.parseInt(chuan))会翻译通过,但test(Integer.valueOf(chuan))会翻译错误.

