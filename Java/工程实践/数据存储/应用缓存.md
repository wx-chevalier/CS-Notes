# 应用缓存

# On-Heap Cache

on-heap 存储主要指那些存储在 Java 堆中并且会被 GC 的对象，另一方面，off-heap 存储指代那些序列化的被类似 EHCache 这样的缓存框架管理的对象，它们存放在堆之外，因此不会被自动垃圾回收。off-heap 存储的对象同样是被存放在内存中，它的存取速度会比 on-heap 存储要低，但是仍然高于磁盘存储。

## [Caffeine](https://github.com/ben-manes/caffeine)

Caffeine is a [high performance](https://github.com/ben-manes/caffeine/wiki/Benchmarks), [near optimal](https://github.com/ben-manes/caffeine/wiki/Efficiency) caching library based onJava 8. For more details, see our [user's guide](https://github.com/ben-manes/caffeine/wiki) and browse the [API docs](http://www.javadoc.io/doc/com.github.ben-manes.caffeine/caffeine) forthe latest release.

### Cache

Caffeine provides an in-memory cache using a Google Guava inspired API. The improvements draw on ourexperience designing [Guava's cache](https://github.com/google/guava/wiki/CachesExplained) and [ConcurrentLinkedHashMap](https://code.google.com/p/concurrentlinkedhashmap).

```java
LoadingCache<Key, Graph> graphs = Caffeine.newBuilder()
    .maximumSize(10_000)
    .expireAfterAccess(5, TimeUnit.MINUTES)
    .refreshAfterWrite(1, TimeUnit.MINUTES)
    .build(key -> createExpensiveGraph(key));
```

# Off-Heap Cache

Usually all non-temporary objects you allocate are managed by java's garbage collector. Although the VM does a decent job doing garbage collection, at a certain point the VM has to do a so called 'Full GC'. A full GC involves scanning the complete allocated Heap, which means GC pauses/slowdowns are proportional to an applications heap size. So don't trust any person telling you 'Memory is Cheap'. In java memory consumtion hurts performance. Additionally you may get notable pauses using heap sizes > 1 Gb. This can be nasty if you have any near-real-time stuff going on, in a cluster or grid a java process might get unresponsive and get dropped from the cluster.

However todays server applications (frequently built on top of bloaty frameworks -) ) easily require heaps far beyond 4Gb.

One solution to these memory requirements, is to 'offload' parts of the objects to the non-java heap (directly allocated from the OS). Fortunately java.nio provides classes to directly allocate/read and write 'unmanaged' chunks of memory (even memory mapped files).

So one can allocate large amounts of 'unmanaged' memory and use this to save objects there. In order to save arbitrary objects into unmanaged memory, the most viable solution is the use of Serialization. This means the application serializes objects into the offheap memory, later on the object can be read using deserialization.

The heap size managed by the java VM can be kept small, so GC pauses are in the millis, everybody is happy, job done.

It is clear, that the performance of such an off heap buffer depends mostly on the performance of the serialization implementation. Good news: for some reason FST-serialization is pretty fast :-).

Sample usage scenarios:

- Session cache in a server application. Use a memory mapped file to store gigabytes of (inactive) user sessions. Once the user logs into your application, you can quickly access user-related data without having to deal with a database.
- Caching of computational results (queries, html pages, ..) (only applicable if computation is slower than deserializing the result object ofc).
- very simple and fast persistance using memory mapped files

Edit: For some scenarios one might choose more sophisticated Garbage Collection algorithms such as ConcurrentMarkAndSweep or G1 to support larger heaps (but this also has its limits beyond 16GB hepas). There is also a commercial java virtual machine with improved 'pasueless' GC (Azul) available.

## Ehcache

- [Ehcache 详细解读](http://www.blogjava.net/libin2722/articles/406569.html)
- [ehcache-documentation](http://www.ehcache.org/documentation/3.0/getting-started.html)

```java
CacheManager cacheManager
    = CacheManagerBuilder.newCacheManagerBuilder()
    .withCache("preConfigured",
        CacheConfigurationBuilder.newCacheConfigurationBuilder()
            .buildConfig(Long.class, String.class))
    .build(false);
cacheManager.init();

Cache<Long, String> preConfigured =
    cacheManager.getCache("preConfigured", Long.class, String.class);

Cache<Long, String> myCache = cacheManager.createCache("myCache",
    CacheConfigurationBuilder.newCacheConfigurationBuilder().buildConfig(Long.class, String.class));

myCache.put(1L, "da one!");
String value = myCache.get(1L);

cacheManager.removeCache("preConfigured");

cacheManager.close();
```

## [MapDB](http://www.mapdb.org/index.html)
