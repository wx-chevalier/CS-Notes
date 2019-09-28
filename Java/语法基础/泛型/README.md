# Java 泛型

泛型即是把类型明确的工作推迟到创建对象或调用方法的时候才去明确的特殊的类型，Java 泛型设计原则是只要在编译时期没有出现警告，那么运行时期就不会出现 ClassCastException 异常。泛型常见的相关术语有：

- `ArrayList<E>`中的**E 称为类型参数变量**
- `ArrayList<Integer>`中的**Integer 称为实际类型参数**
- **整个称为 ArrayList<E>泛型类型**
- **整个 ArrayList<Integer>称为参数化的类型 ParameterizedType**

早期 Java 是使用 Object 来代表任意类型的，但是向下转型有强转的问题，这样程序就不太安全。在没有泛型的时候，Collection、Map 集合对元素的类型是没有任何限制的。本来我的 Collection 集合装载的是全部的 Dog 对象，但是外边把 Cat 对象存储到集合中，是没有任何语法错误的。把对象扔进集合中，集合是不知道元素的类型是什么的，仅仅知道是 Object。因此在 `get()` 的时候，返回的是 Object；外边获取该对象，还需要强制转换。

在引入了泛型之后，代码更加简洁，程序更加健壮，可读性和稳定性也得到了极大的提升。

# 泛型类

泛型类就是把泛型定义在类上，用户使用该类的时候，才把类型明确下来。这样的话，用户明确了什么类型，该类就代表着什么类型。用户在使用的时候就不用担心强转的问题，运行时转换异常的问题了。

```java
/* 把泛型定义在类上，类型变量定义在类上,方法中也可以使用 */
public class ObjectTool<T> {
    private T obj;

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}

public static void main(String[] args) {
    // 创建对象并指定元素类型
    ObjectTool<String> tool = new ObjectTool<>();

    tool.setObj(new String("钟福成"));
    String s = tool.getObj();
    System.out.println(s);


    //创建对象并指定元素类型
    ObjectTool<Integer> objectTool = new ObjectTool<>();
    /**
      * 如果我在这个对象里传入的是String类型的,它在编译时期就通过不了了.
      */
    objectTool.setObj(10);
    int i = objectTool.getObj();
    System.out.println(i);
}
```

# 泛型方法

仅仅在某一个方法上需要使用泛型，外界仅仅是关心该方法，不关心类其他的属性。

```java
// 定义泛型方法..
public <T> void show(T t) {
    System.out.println(t);
}

public static void main(String[] args) {
    //创建对象
    ObjectTool tool = new ObjectTool();

    //调用方法,传入的参数是什么类型,返回值就是什么类型
    tool.show("hello");
    tool.show(12);
    tool.show(12.5);
}
```

定义泛型的浅拷贝函数：

```java
/** 浅拷贝函数 */
public static <T> T shallowCopy(Object source, Class<T> clazz) throws BeansException {
    // 判断源对象
    if (Objects.isNull(source)) {
        return null;
    }

    // 新建目标对象
    T target;
    try {
        target = clazz.newInstance();
    } catch (Exception e) {
        throw new BeansException("新建类实例异常", e);
    }

    // 拷贝对象属性
    BeanUtils.copyProperties(source, target);

    // 返回目标对象
    return target;
}
```

# 泛型类派生出的子类

泛型类是拥有泛型这个特性的类，它本质上还是一个 Java 类，那么它就可以被继承。泛型类的继承分为两种情况：

- 子类明确泛型类的类型参数变量
- 子类不明确泛型类的类型参数变量

## 子类明确泛型类的类型参数变量

```java
/*
    把泛型定义在接口上
 */
public interface Inter<T> {
    public abstract void show(T t);
}

/**
 * 子类明确泛型类的类型参数变量:
 */
public class InterImpl implements Inter<String> {
    @Override
    public void show(String s) {
        System.out.println(s);

    }
}
```

## 子类不明确泛型类的类型参数变量

当子类不明确泛型类的类型参数变量时，外界使用子类的时候，也需要传递类型参数变量进来，在实现类上需要定义出类型参数变量。

```java
/**
 * 子类不明确泛型类的类型参数变量: 实现类也要定义出<T>类型的
 */
public class InterImpl<T> implements Inter<T> {
    @Override
    public void show(T t) {
        System.out.println(t);
    }
}

public static void main(String[] args) {
    //测试第一种情况
    //Inter<String> i = new InterImpl();
    //i.show("hello");

    //第二种情况测试
    Inter<String> ii = new InterImpl<>();
    ii.show("100");
}
```

值得注意的是，类上声明的泛型只对非静态成员有效，并且实现类的要是重写父类的方法，返回值的类型是要和父类一样的。

# 泛型擦除

泛型是提供给 javac 编译器使用的，它用于限定集合的输入类型，让编译器在源代码级别上，即挡住向集合中插入非法数据。但编译器编译完带有泛形的 Java 程序后，生成的 class 文件中将不再带有泛形信息，以此使程序运行效率不受到影响，这个过程称之为“擦除”。

# 典型案例

## DAO

在抽象 DAO 中，是不可能知道哪一个 DAO 会继承它自己，所以是不知道其具体的类型的。而泛型就是在创建的时候才指定其具体的类型。

```java
public abstract class BaseDao<T> {

    //模拟hibernate....
    private Session session;
    private Class clazz;

    //哪个子类调的这个方法，得到的class就是子类处理的类型（非常重要）
    public BaseDao(){
        Class clazz = this.getClass();  //拿到的是子类
        ParameterizedType pt = (ParameterizedType) clazz.getGenericSuperclass();  //BaseDao<Category>
        clazz = (Class) pt.getActualTypeArguments()[0];
        System.out.println(clazz);
    }

    public void add(T t){
        session.save(t);
    }

    public T find(String id){
        return (T) session.get(clazz, id);
    }

    public void update(T t){
        session.update(t);
    }

    public void delete(String id){
        T t = (T) session.get(clazz, id);
        session.delete(t);
    }
}
```

继承抽象 DAO，该实现类就有对应的增删改查的方法了。

```java
public class CategoryDao extends BaseDao<Category> {}

public class BookDao extends BaseDao<Book> {}
```
