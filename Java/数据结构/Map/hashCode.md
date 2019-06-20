# hashCode

对于包含容器类型的程序设计语言来说，基本上都会涉及到 hashCode。在 Java 中也一样，hashCode 方法的主要作用是为了配合基于散列的集合一起正常运行，这样的散列集合包括 HashSet、HashMap 以及 HashTable。

当向集合中插入对象时，如何判别在集合中是否已经存在该对象，也许大多数人都会想到调用 equals 方法来逐个进行比较，这个方法确实可行。但是如果集合中已经存在一万条数据或者更多的数据，如果采用 equals 方法去逐一比较，效率必然是一个问题。此时 hashCode 方法的作用就体现出来了，当集合要添加新的对象时，先调用这个对象的 hashCode 方法，得到对应的 hashcode 值，实际上在 HashMap 的具体实现中会用一个 table 保存已经存进去的对象的 hashcode 值，如果 table 中没有该 hashcode 值，它就可以直接存进去，不用再进行任何比较了；如果存在该 hashcode 值，就调用它的 equals 方法与新元素进行比较，相同的话就不存了，不相同就散列其它的地址，所以这里存在一个冲突解决的问题，这样一来实际调用 equals 方法的次数就大大降低了

通俗来说，Java 中的 hashCode 方法就是根据一定的规则将与对象相关的信息，比如对象的存储地址，对象的字段等，映射成一个数值，这个数值称作为哈希值。

# 字符串中的 hashCode

hashCode 就是根据对象存储在内存的地址计算出的一个值。这个值可以标识这个对象的位置。也可以对比两个引用变量是否指向同一个对象。String 重写了 hashCode 方法——改为根据字符序列计算 hashCode 值，所以 String 通过 `String new("String")` 方式创建的两个相同字符串内容的对象他们的 hashcode 相同。要想获取正确的 hashcode，需要使用 System.identityHashCode() 方法。

```java
public int hashCode() {
    int h = hash;
    if (h == 0) {
    int off = offset;
    char val[] = value;
    int len = count;
        for (int i = 0; i < len; i++) {
            h = 31*h + val[off++];
        }
        hash = h;
    }
    return h;
}
```

```java
public class IdentityHashCodeTest
{
    public static void main(String[] args)
    {
        //下面程序中s1和s2是两个不同对象
        String s1 = new String("Hello");
        String s2 = new String("Hello");
        //String重写了hashCode方法——改为根据字符序列计算hashCode值，
        //因为s1和s2的字符序列相同，所以它们的hashCode方法返回值相同
        System.out.println(s1.hashCode()
            + "----" + s2.hashCode());
        //s1和s2是不同的字符串对象，所以它们的identityHashCode值不同
        System.out.println(System.identityHashCode(s1)
            + "----" + System.identityHashCode(s2));
        String s3 = "Java";
        String s4 = "Java";
        //s3和s4是相同的字符串对象，所以它们的identityHashCode值相同
        System.out.println(System.identityHashCode(s3)
            + "----" + System.identityHashCode(s4));
    }
}
/*
69609650----69609650
13078969----3154093
28399250----28399250
*/
```

在有些情况下，程序设计者在设计一个类的时候为需要重写 equals 方法，比如 String 类，但是千万要注意，在重写 equals 方法的同时，必须重写 hashCode 方法。

```java
class People{
    private String name;
    private int age;

    public People(String name,int age) {
        this.name = name;
        this.age = age;
    }

    public void setAge(int age){
        this.age = age;
    }

    /* 先注释掉对于hashCode的复写
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return name.hashCode()*37+age;
    }
    */
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return this.name.equals(((People)obj).name) && this.age== ((People)obj).age;
    }
}

public class Main {

    public static void main(String[] args) {

        People p1 = new People("Jack", 12);
        System.out.println(p1.hashCode());

        HashMap<People, Integer> hashMap = new HashMap<People, Integer>();
        hashMap.put(p1, 1);

        System.out.println(hashMap.get(new People("Jack", 12)));
    }
}
```

在这里我只重写了 equals 方法，也就说如果两个 People 对象，如果它的姓名和年龄相等，则认为是同一个人。这段代码本来的意愿是想这段代码输出结果为“1”，但是事实上它输出的是“null”。为什么呢？原因就在于重写 equals 方法的同时忘记重写 hashCode 方法。

虽然通过重写 equals 方法使得逻辑上姓名和年龄相同的两个对象被判定为相等的对象(跟 String 类类似)，但是要知道默认情况 下，hashCode 方法是将对象的存储地址进行映射。那么上述代码的输出结果为“null”就不足为奇了。原因很简单，p1 指向的对象和 `System.out.println(hashMap.get(new People("Jack", 12)));` 这句中的 `new People("Jack", 12)` 生成的是两个对象，它们的存储地址肯定不同。

# hashCode 设计

一个好的 hash 函数应该是这样的：**为不相同的对象产生不相等的 hashCode。**

在理想情况下，hash 函数应该把集合中不相等的实例均匀分布到所有可能的 hashCode 上，要想达到这种理想情形是非常困难的，至少 java 没有达到。因为我们可以看到，hashCode 是非随机生成的，它有一定的规律，就是上面的数学等式，我们可以构造一些具有相同 hashCode 但 value 值不一样的，比如说：Aa 和 BB 的 hashCode 是一样的。

说到这里，你可能会想，原来构造 hash 冲突那么简单啊，那我是不是可以对 HashMap 函数构造很多<key,value>不都一样，但具有相同的 hashCode，这样的话可以把 HashMap 函数变成一条单向链表，运行时间由线性变为平方级呢？虽然 HashMap 重写的 hashCode 方法比 String 类的要复杂些，但理论上说是可以这么做的。这也是最近比较热门的 Hash Collision DoS 事件。

```
       public final int hashCode() {
           return (key==null   ? 0 : key.hashCode()) ^
                   (value==null ? 0 : value.hashCode());
        }
```

下面这段话摘自 Effective Java 一书：

- 在程序执行期间，只要 equals 方法的比较操作用到的信息没有被修改，那么对这同一个对象调用多次，hashCode 方法必须始终如一地返回同一个整数。
- 如果两个对象根据 equals 方法比较是相等的，那么调用两个对象的 hashCode 方法必须返回相同的整数结果。
- 如果两个对象根据 equals 方法比较是不等的，则 hashCode 方法不一定得返回不同的整数。

对于第二条和第三条很好理解，但是第一条，很多时候就会忽略。在《Java 编程思想》一书中的 P495 页也有同第一条类似的一段话：

“设计 hashCode()时最重要的因素就是：无论何时，对同一个对象调用 hashCode()都应该产生同样的值。如果在讲一个对象用 put()添加进 HashMap 时产生一个 hashCdoe 值，而用 get()取出时却产生了另一个 hashCode 值，那么就无法获取该对象了。所以如果你的 hashCode 方法依赖于对象中易变的数据，用户就要当心了，因为此数据发生变化时，hashCode()方法就会生成一个不同的散列码”。

下面举个例子：

```java
class People{
    private String name;
    private int age;

    public People(String name,int age) {
        this.name = name;
        this.age = age;
    }

    public void setAge(int age){
        this.age = age;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return name.hashCode()*37+age;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return this.name.equals(((People)obj).name) && this.age== ((People)obj).age;
    }
}

public class Main {

    public static void main(String[] args) {

        People p1 = new People("Jack", 12);
        System.out.println(p1.hashCode());

        HashMap<People, Integer> hashMap = new HashMap<People, Integer>();
        hashMap.put(p1, 1);

        p1.setAge(13);

        System.out.println(hashMap.get(p1));
    }
}
```

这段代码输出的结果为“null”，想必其中的原因大家应该都清楚了。因此，在设计 hashCode 方法和 equals 方法的时候，如果对象中的数据易变，则最好在 equals 方法和 hashCode 方法中不要依赖于该字段。
