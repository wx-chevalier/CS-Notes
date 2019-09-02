# 线程池

线程多起来的话就需要管理，不然就会乱成一锅。我们知道线程在物理上对应的就是栈里面的一段内存，存放着局部变量的空间和待执行指令集。如果每次执行都要从头初始化这段内存，然后再交给 CPU 执行，效率就有点低了。假如我们知道该段栈内存会被经常用到，那我们就不要回收，创建完就让它在栈里面呆着，要用的时候取出来，用完换回去，是不是就省了初始化线程空间的时间，这样是我们搞出线程池的初衷。

其实线程池很简单，就是搞了个池子放了一堆线程。既然我们搞线程池是为了提高效率，那就要考虑线程池放多少个线程比较合适，太多了或者太少了有什么问题，怎么拒绝多余的请求，除了异常怎么处理。首先我们来看跟线程池有关的一张类图。

在 Java 中并不鼓励直接创建线程，线程资源必须通过线程池提供，不允许在应用中自行显式创建线程。使用线程池的好处是减少在创建和销毁线程上所花的时间以及系统资源的开销，解决资源不足的问题。如果不使用线程池，有可能造成系统创建大量同类线程而导致消耗完内存或者“过度切换”的问题。

```java
ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
.setNameFormat("demo-pool-%d").build();

ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

singleThreadPool.execute(()-> System.out.println(Thread.currentThread().getName()));
singleThreadPool.shutdown();
```

![](https://s2.ax1x.com/2019/09/02/nPC2c9.png)

![](https://s2.ax1x.com/2019/09/02/nPCRXR.png)
