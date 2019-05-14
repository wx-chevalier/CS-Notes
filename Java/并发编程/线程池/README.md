# 线程池

在 Java 中并不鼓励直接创建线程，线程资源必须通过线程池提供，不允许在应用中自行显式创建线程。使用线程池的好处是减少在创建和销毁线程上所花的时间以及系统资源的开销，解决资源不足的问题。如果不使用线程池，有可能造成系统创建大量同类线程而导致消耗完内存或者“过度切换”的问题。

```java
ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
.setNameFormat("demo-pool-%d").build();

ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

singleThreadPool.execute(()-> System.out.println(Thread.currentThread().getName()));
singleThreadPool.shutdown();
```

