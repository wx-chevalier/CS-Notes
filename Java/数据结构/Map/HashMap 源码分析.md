# HashMap 源码分析

我们主要结合源码，从存储结构、常用方法分析、扩容以及安全性等方面深入讲解 HashMap 的工作原理。

Java 8 开始 HashMap 的实现是综合利用了数组、链表与红黑树。

下面这段代码是 java.util.HashMap 的中 put 方法的具体实现：

```java
public V put(K key, V value) {
    if (key == null)
        return putForNullKey(value);

    // 计算 key 的 hashCode 值
    int hash = hash(key.hashCode());
    // 提取出该值在 HashTable 中的下标
    int i = indexFor(hash, table.length);

    // 这里是使用了链表方式来存储元素，依次提取出元素
    for (Entry<K,V> e = table[i]; e != null; e = e.next) {
        Object k;

        // 如果存在相似的则进行替换
        if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
            V oldValue = e.value;
            e.value = value;
            e.recordAccess(this);
            return oldValue;
        }
    }

    // 否则添加新的入口
    modCount++;
    addEntry(hash, key, value, i);
    return null;
}
```

# get

下面是 HashMap 的 get 方法的具体实现：

```java
public V get(Object key) {
        if (key == null)
            return getForNullKey();
        int hash = hash(key.hashCode());
        for (Entry<K,V> e = table[indexFor(hash, table.length)];
             e != null;
             e = e.next) {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k)))
                return e.value;
        }
        return null;
    }
```

所以在 hashmap 进行 get 操作时，因为得到的 hashcdoe 值不同(注意，上述代码也许在某些情况下会得到相同的 hashcode 值，不过 这种概率比较小，因为虽然两个对象的存储地址不同也有可能得到相同的 hashcode 值)，所以导致在 get 方法中 for 循环不会执行，直接返回 null。

# 链接

- [hashmap-changes-in-java-8/](https://examples.javacodegeeks.com/core-java/util/hashmap/hashmap-changes-in-java-8/)

- [Java8 系列之重新认识 HashMap](http://www.importnew.com/20386.html)

- [LinkedHashMap 原理解析](http://uule.iteye.com/blog/1522291)

- [Java HashMap 原理解析](https://github.com/HelloListen/Secret/blob/master/content/post/2016/05/java-hashmap-hashcode-hash.md)

- https://www.cnblogs.com/xawei/p/6747660.html

- https://www.cnblogs.com/shileibrave/p/9836731.html

- http://www.importnew.com/28263.html

- https://mp.weixin.qq.com/s/XMQ27dyGokAegjOnba7FJA
