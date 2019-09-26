# Java 集合类型基础

- [Java 性能调优指南之 Java 集合概览](http://www.tuicool.com/articles/6Jb2Qf)

![](http://pic002.cnblogs.com/images/2012/80896/2012053020261738.gif)
![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2016/8/1/F8C8A707-3953-4452-A090-25A41D9A4D60.png)

上述类图中，实线边框的是实现类，比如 ArrayList，LinkedList，HashMap 等，折线边框的是抽象类，比如 AbstractCollection，AbstractList，AbstractMap 等，而点线边框的是接口，比如 Collection，Iterator，List 等。发现一个特点，上述所有的集合类，都实现了 Iterator 接口，这是一个用于遍历集合中元素的接口，主要包含 hashNext(),next(),remove()三种方法。它的一个子接口 LinkedIterator 在它的基础上又添加了三种方法，分别是 add(),previous(),hasPrevious()。也就是说如果是先 Iterator 接口，那么在遍历集合中元素的时候，只能往后遍历，被遍历后的元素不会在遍历到，通常无序集合实现的都是这个接口，比如 HashSet，HashMap；而那些元素有序的集合，实现的一般都是 LinkedIterator 接口，实现这个接口的集合可以双向遍历，既可以通过 next()访问下一个元素，又可以通过 previous()访问前一个元素，比如 ArrayList。还有一个特点就是抽象类的使用。如果要自己实现一个集合类，去实现那些抽象的接口会非常麻烦，工作量很大。这个时候就可以使用抽象类，这些抽象类中给我们提供了许多现成的实现，我们只需要根据自己的需求重写一些方法或者添加一些方法就可以实现自己需要的集合类，工作流昂大大降低。

## Comparison

![](http://7xkt0f.com1.z0.glb.clouddn.com/E7BA8E64-D915-4475-9C99-4A0AC29FE4A3.png)

### Performance(性能)

> [Java 集合类性能分析](http://www.cnblogs.com/supersugar/archive/2012/03/20/2408216.html)

# Comparator

- [writing-comparators-the-java8-way](https://praveer09.github.io/technology/2016/06/21/writing-comparators-the-java8-way/)
- [Java8：Lambda 表达式增强版 Comparator 和排序](http://www.importnew.com/15259.html)

# Streams API(流接口)

- [Java8 中的 Stream API 详解](http://www.ibm.com/developerworks/cn/java/j-lo-java8streamapi/)
  >
- [side-effects-and-java-8-streams:Java 8 Stream 的部分副作用](https://www.voxxed.com/blog/2015/10/side-effects-and-java-8-streams/#comments)
- [Follow-up: How fast are the Java 8 Streams:Java 8 流接口的性能评测](https://jaxenter.com/follow-up-how-fast-are-the-java-8-streams-122522.html)

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

#### groupingBy/partitioningBy

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

- 按照未成年人和成年人归组

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

# Array

Java 中也内置了许多对于 Array 类型的操作，不过大多是放置在了`Arrays`这个包中。

## One-Dimensional Array

### 创建增删

(1)声明式

```
　　type[] 变量名 = new type[数组中元素的个数];
```

比如：

```
　　int[] a = new int[10];
```

数组名，也即引用 a，指向数组元素的首地址。(2)C 语言式

```
type变量名[] = new type[数组中元素的个数];
```

如：

```
　　int a[] = new int[10];
```

(3)定义时直接初始化

```
　　type[] 变量名 = new type[]{逗号分隔的初始化值};
　　int[] a = {1,2,3,4};
　　int[] a = new int[]{1,2,3,4};
```

其中 int[] a = new int[]{1,2,3,4};的第二个方括号中不能加上数组长度，因为元素个数是由后面花括号的内容决定的。

### 索引遍历

## Two-Dimensional Array

二维数组是数组的数组。基本的定义方式有两种形式，如：

```
　　type[][] i = new type[2][3];(推荐)

　　type i[][] = new type[2][3];
```

如下程序：

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

## Arrays

# List

# Set

## HashSet

### 创建增删

### 索引遍历

```
        Set<String> set=new HashSet<String>();

        set.add("a");
        set.add("b");
        set.add("c");
        set.add("c");
        set.add("d");

        //使用Iterator输出集合
        Iterator<String> iter=set.iterator();
        while(iter.hasNext())
        {
            System.out.print(iter.next()+" ");
        }
        System.out.println();
        //使用For Each输出结合for(String e:set)
        {
            System.out.print(e+" ");
        }
        System.out.println();

        //使用toString输出集合
        System.out.println(set);
```

# Map

# Java 集合类型概述

Java 中的 Collections 类型为我们提供了存储与操作序列对象的接口，常见的譬如搜索、排序、插入、修改、删除等操作都由 Collections 提供。

![](https://www.javatpoint.com/images/collection-hierarchy.png)

Collection 接口的方法如下：

| No. | Method                                   | Description                                                                                |
| --- | ---------------------------------------- | ------------------------------------------------------------------------------------------ |
| 1   | public boolean add(Object element)       | is used to insert an element in this collection.                                           |
| 2   | public boolean addAll(Collection c)      | is used to insert the specified collection elements in the invoking collection.            |
| 3   | public boolean remove(Object element)    | is used to delete an element from this collection.                                         |
| 4   | public boolean removeAll(Collection c)   | is used to delete all the elements of specified collection from the invoking collection.   |
| 5   | public boolean retainAll(Collection c)   | is used to delete all the elements of invoking collection except the specified collection. |
| 6   | public int size()                        | return the total number of elements in the collection.                                     |
| 7   | public void clear()                      | removes the total no of element from the collection.                                       |
| 8   | public boolean contains(Object element)  | is used to search an element.                                                              |
| 9   | public boolean containsAll(Collection c) | is used to search the specified collection in this collection.                             |
| 10  | public Iterator iterator()               | returns an iterator.                                                                       |
| 11  | public Object[] toArray()                | converts collection into array.                                                            |
| 12  | public boolean isEmpty()                 | checks if collection is empty.                                                             |
| 13  | public boolean equals(Object element)    | matches two collection.                                                                    |
| 14  | public int hashCode()                    | returns the hashcode number for collection.                                                |

```java
ArrayList aList = new ArrayList();
//Add elements to ArrayList object
aList.add("1");
aList.add("2");
aList.add("3");
aList.add("4");
aList.add("5");
Collections.reverse(aList);
System.out.println("After Reverse Order, ArrayList Contains : " + aList);
```

##　 Comparator

```java
Comparator<Developer> byName = new Comparator<Developer>() {
	@Override
	public int compare(Developer o1, Developer o2) {
		return o1.getName().compareTo(o2.getName());
	}
};

Comparator<Developer> byName =
	(Developer o1, Developer o2)->o1.getName().compareTo(o2.getName());


List<Developer> listDevs = getDevelopers();

//sort by age
Collections.sort(listDevs, new Comparator<Developer>() {
    @Override
    public int compare(Developer o1, Developer o2) {
        return o1.getAge() - o2.getAge();
    }
});
```

```java
public class NewClass2 implements Comparator<Point> {
    public int compare(Point p1, Point p2) {
        if (p1.getY() < p2.getY()) return -1;
        if (p1.getY() > p2.getY()) return 1;
        return 0;
    }
}
```
