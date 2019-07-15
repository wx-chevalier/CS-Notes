> [Java 9 特性概述]()从属于笔者的[]()，主要是对于 Java 9 的特性与简要用法进行介绍。[Java 9 Released — 9 Biggest Features & DownloadListAre Here](https://fossbytes.com/java-9-features-released-download-course/) 以及 [Java 9 Features with Examples](https://www.journaldev.com/13121/java-9-features-with-examples)

# Java 9 特性概述

Java 9 为我们提供了用于创建不可变列表、集合与映射的工厂方法，用于完善 Java 8 中提供的 Collections.unmodifiableXXX 系列方法；工厂方法允许创建包含不超过十个不等元素的不可变对象，这些对象在被创建后即不能再被添加、更新或者删除：

```
List<String> list=List.of("apple","bat");
list.add("cat");
```

强制操作的话会抛出 `unsupportedOperationException`，并且这种创建方式不允许传入空参数，否则会抛出空指针异常：

```
List<String> list=List.of("apple",null);
```

```
List<String> list1=List.of("apple","bat");
List<String> list2= List.of();


System.out.println("** List with values **");
list1.forEach(value-> System.out.println(value));
System.out.println("** List empty **");
list2.forEach(value-> System.out.println(value));


** List with values **
apple
bat
** List empty **
```

```
Set<String> set1= Set.of("apple","bat");
Set<String> set2= Set.of();


System.out.println("** Set with values **");
set1.forEach(value-> System.out.println(value));
System.out.println("** List empty **");
set2.forEach(value-> System.out.println(value));
```

```
Map<Integer,String> emptyMap = Map.of();
Map<Integer,String> map = Map.of(1, "Apple", 2, "Bat", 3, "Cat");


System.out.println("** Empty  Map **");
emptyMap.forEach((k,v) -> System.out.println( k +"-"+ v));
System.out.println("** Map with values **");
map.forEach((k,v) -> System.out.println( k +"-"+ v));


** Empty  Map **
** Map with values **
1-Apple
3-Cat
2-Bat
```

```
System.out.println("** Empty  Map Entry **");
Map<Integer,String> emptyEntry = Map.ofEntries();
emptyEntry.forEach((k,v) -> System.out.println(k+"-"+v));


System.out.println("**   Map Entry with value **");
Map.Entry<Integer,String> mapEntry1 = Map.entry(1,"Apple");
Map.Entry<Integer,String> mapEntry2 = Map.entry(2,"Bat");
Map.Entry<Integer,String> mapEntry3 = Map.entry(3,"Cat");
Map<Integer,String> mapEntry = Map.ofEntries(mapEntry1,mapEntry2,mapEntry3);


mapEntry.forEach((k,v) -> System.out.println(k+"-"+v));


** Empty  Map Entry **
**   Map Entry with value **
1-Apple
3-Cat
2-Bat
```
