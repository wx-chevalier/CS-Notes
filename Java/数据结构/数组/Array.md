# Array

# List 与 Array 转换

List 接口提供了 toArray() 方法来返回包含列表元素的 Object 数组：

```java
List<String> list = Arrays.asList("C", "C++", "Java");
Object[] array = list.toArray();
System.out.println(Arrays.toString(array));
```

不过该函数并不了解元素的类型，可以通过 `toArray(T[] a)` 来返回指定类型的数组：

```java
List<String> list = Arrays.asList("C", "C++", "Java");
String[] array = list.toArray(new String[list.size()]);
System.out.println(Arrays.toString(array));

// 我们还可以传入空数组来让 JVM 自动分配内存
List<String> list = Arrays.asList("C", "C++", "Java");
String[] array = list.toArray(new String[0]);
System.out.println(Arrays.toString(array));
```

在 Java 8 中我们可以使用 Stream API 来将列表转化为数组：

```java
List<String> list = Arrays.asList("C", "C++", "Java");
String[] array = list.stream().toArray(String[]::new);
System.out.println(Arrays.toString(array));

List<String> list = Arrays.asList("C", "C++", "Java");
String[] array = list.stream().toArray(n -> new String[n]);
System.out.println(Arrays.toString(array));
```

可以使用 asList 等方法将数组转化为列表：

```java
ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(arrays));
List<String> list = Arrays.asList(arrays);

List<String> list2 = new ArrayList<String>(arrays.length);
Collections.addAll(list2, arrays);
```
