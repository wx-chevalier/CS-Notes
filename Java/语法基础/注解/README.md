## Annotation(注解)

![](http://images.cnitblog.com/blog/34483/201304/25200814-475cf2f3a8d24e0bb3b4c442a4b44734.jpg)

### 元注解

元注解的作用就是负责注解其他注解。Java5.0 定义了 4 个标准的 meta-annotation 类型，它们被用来提供对其它 annotation 类型作说明。Java5.0 定义的元注解：

- @Target
- @Retention
- @Documented
- @Inherited

这些类型和它们所支持的类在 java.lang.annotation 包中可以找到。下面我们看一下每个元注解的作用和相应分参数的使用说明。

#### @Target

@Target 说明了 Annotation 所修饰的对象范围：Annotation 可被用于 packages、types(类、接口、枚举、Annotation 类型)、类型成员(方法、构造方法、成员变量、枚举值)、方法参数和本地变量(如循环变量、catch 参数)。在 Annotation 类型的声明中使用了 target 可更加明晰其修饰的目标。
取值(ElementType)有：

- CONSTRUCTOR:用于描述构造器
- FIELD:用于描述域
- LOCAL_VARIABLE:用于描述局部变量
- METHOD:用于描述方法
- PACKAGE:用于描述包
- PARAMETER:用于描述参数
- TYPE:用于描述类、接口(包括注解类型) 或 enum 声明

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

#### @Retention

@Retention 定义了该 Annotation 被保留的时间长短：某些 Annotation 仅出现在源代码中，而被编译器丢弃；而另一些却被编译在 class 文件中；编译在 class 文件中的 Annotation 可能会被虚拟机忽略，而另一些在 class 被装载时将被读取(请注意并不影响 class 的执行，因为 Annotation 与 class 在使用上是被分离的)。使用这个 meta-Annotation 可以对 Annotation 的“生命周期”限制。取值(RetentionPoicy)有：

- SOURCE:在源文件中有效(即源文件保留)
- CLASS:在 class 文件中有效(即 class 保留)
- RUNTIME:在运行时有效(即运行时保留)

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

#### @Documented

@Documented 用于描述其它类型的 annotation 应该被作为被标注的程序成员的公共 API，因此可以被例如 javadoc 此类的工具文档化。Documented 是一个标记注解，没有成员。

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

#### @Inherited

@Inherited 元注解是一个标记注解，@Inherited 阐述了某个被标注的类型是被继承的。如果一个使用了@Inherited 修饰的 annotation 类型被用于一个 class，则这个 annotation 将被用于该 class 的子类。

> 注意：@Inherited annotation 类型是被标注过的 class 的子类所继承。类并不从它所实现的接口继承 annotation，方法并不从它所重载的方法继承 annotation。

当@Inherited annotation 类型标注的 annotation 的 Retention 是 RetentionPolicy.RUNTIME，则反射 API 增强了这种继承性。如果我们使用 java.lang.reflect 去查询一个@Inherited annotation 类型的 annotation 时，反射代码检查将展开工作：检查 class 和其父类，直到发现指定的 annotation 类型被发现，或者到达类继承结构的顶层。

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

### 自定义注解

使用@interface 自定义注解时，自动继承了 java.lang.annotation.Annotation 接口，由编译程序自动完成其他细节。在定义注解时，不能继承其他的注解或接口。@interface 用来声明一个注解，其中的每一个方法实际上是声明了一个配置参数。方法的名称就是参数的名称，返回值类型就是参数的类型(返回值类型只能是基本类型、Class、String、enum)。可以通过 default 来声明参数的默认值。
(1)定义注解格式

```
　　public @interface 注解名 {定义体}
```

(2)注解参数的可支持数据类型：

- 所有基本数据类型(int,float,boolean,byte,double,char,long,short)
- String 类型
- Class 类型
- enum 类型
- Annotation 类型
- 以上所有类型的数组

(3)参数设置类型

- 只能用 public 或默认(default)这两个访问权修饰.例如,String value();这里把方法设为 defaul 默认类型；
- 参数成员只能用基本类型 byte,short,char,int,long,float,double,boolean 八种基本数据类型和 String ,Enum ,Class , annotations 等数据类型,以及这一些类型的数组.例如,String value();这里的参数成员就为 String;
- 如果只有一个参数成员,最好把参数名称设为"value",后加小括号.例:下面的例子 FruitName 注解就只有一个参数成员。

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

### 注解处理器

Java 使用 Annotation 接口来代表程序元素前面的注解，该接口是所有 Annotation 类型的父接口。除此之外，Java 在 java.lang.reflect 包下新增了 AnnotatedElement 接口，该接口代表程序中可以接受注解的程序元素，该接口主要有如下几个实现类：

- Class：类定义
- Constructor：构造器定义
- Field：累的成员变量定义
- Method：类的方法定义
- Package：类的包定义

java.lang.reflect 包下主要包含一些实现反射功能的工具类，实际上，java.lang.reflect 包所有提供的反射 API 扩充了读取运行时 Annotation 信息的能力。当一个 Annotation 类型被定义为运行时的 Annotation 后，该注解才能是运行时可见，当 class 文件被装载时被保存在 class 文件中的 Annotation 才会被虚拟机读取。AnnotatedElement 接口是所有程序元素(Class、Method 和 Constructor)的父接口，所以程序通过反射获取了某个类的 AnnotatedElement 对象之后，程序就可以调用该对象的如下四个个方法来访问 Annotation 信息：

- 方法 1：<T extends Annotation> T getAnnotation(Class<T> annotationClass): 返回改程序元素上存在的、指定类型的注解，如果该类型注解不存在，则返回 null。
- 方法 2：Annotation[] getAnnotations():返回该程序元素上存在的所有注解。
- 方法 3：boolean is AnnotationPresent(Class<?extends Annotation> annotationClass):判断该程序元素上是否包含指定类型的注解，存在则返回 true，否则返回 false.
- 方法 4：Annotation[] getDeclaredAnnotations()：返回直接存在于此元素上的所有注释。与此接口中的其他方法不同，该方法将忽略继承的注释。(如果没有注释直接存在于此元素上，则返回长度为零的一个数组。)该方法的调用者可以随意修改返回的数组；这不会对其他调用者返回的数组产生任何影响。

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

# 链接

- https://parg.co/kYB
- https://mp.weixin.qq.com/s?__biz=MzI4Njg5MDA5NA==&mid=2247484113&idx=1&sn=f5fd35b2f7dc45a256fee170cad0cdc9&chksm=ebd743d0dca0cac6a8fed8caf09f0d561b0358dece9b8e746e1e6726bca093287cf515638349&scene=21#wechat_redirect