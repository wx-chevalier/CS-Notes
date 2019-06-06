# Executors 详解

并发 API 引入了 ExecutorService 作为一个在程序中直接使用 Thread 的高层次的替换方案。Executos 支持运行异步任务，通常管理一个线程池，这样一来我们就不需要手动去创建新的线程。在不断地处理任务的过程中，线程池内部线程将会得到复用，因此，在我们可以使用一个 Executor Service 来运行和我们想在我们整个程序中执行的一样多的并发任务。 下面是使用 Executors 的第一个代码示例：

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
executor.submit(() -> {
	String threadName = Thread.currentThread().getName();
	System.out.println("Hello " + threadName);
});
// => Hello pool-1-thread-1
```

# 创建线程池

Java 通过 Executors 提供四种线程池，分别为：

- newCachedThreadPool 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。

- newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。

- newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。

- newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。

Executors 类提供了便利的工厂方法来创建不同类型的 executor services。在这个示例中我们使用了一个单线程线程池的 executor。 代码运行的结果类似于上一个示例，但是当运行代码时，你会注意到一个很大的差别：Java 进程从没有停止！Executors 必须显式的停止-否则它们将持续监听新的任务。 ExecutorService 提供了两个方法来达到这个目的——shutdwon()会等待正在执行的任务执行完而 shutdownNow()会终止所有正在执行的任务并立即关闭 execuotr。

```java
try {
    System.out.println("attempt to shutdown executor");
    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);
    }
catch (InterruptedException e) {
    System.err.println("tasks interrupted");
}
finally {
    if (!executor.isTerminated()) {
        System.err.println("cancel non-finished tasks");
    }
    executor.shutdownNow();
    System.out.println("shutdown finished");
}
```

executor 通过等待指定的时间让当前执行的任务终止来“温柔的”关闭 executor。在等待最长 5 分钟的时间后，execuote 最终会通过中断所有的正在执行的任务关闭。

## Scheduled Executors

为了持续的多次执行常见的任务，我们可以利用调度线程池 ScheduledExecutorService 支持任务调度，持续执行或者延迟一段时间后执行：

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());
// 设置延迟 3 秒执行
ScheduledFuture<?> future = executor.schedule(task, 3, TimeUnit.SECONDS);

TimeUnit.MILLISECONDS.sleep(1337);

long remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
System.out.printf("Remaining Delay: %sms", remainingDelay);
```

调度一个任务将会产生一个专门的 ScheduleFuture 类型，它除了提供了 Future 的所有方法之外，他还提供了 getDelay()方法来获得剩余的延迟。在延迟消逝后，任务将会并发执行。为了调度任务持续的执行，executors 提供了两个方法 s`cheduleAtFixedRate()` 和 `scheduleWithFixedDelay()`；第一个方法用来以固定频率来执行一个任务，比如，下面这个示例中，每分钟一次：

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());

int initialDelay = 0;
int period = 1;
executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
```

另外，这个方法还接收一个初始化延迟，用来指定这个任务首次被执行等待的时长。需要注意的是，`scheduleAtFixedRate()` 并不考虑任务的实际用时。所以，如果你指定了一个 period 为 1 分钟而任务需要执行 2 分钟，那么线程池为了性能会更快的执行。在这种情况下，你应该考虑使用 scheduleWithFixedDelay()。这个方法的工作方式与上我们上面描述的类似。不同之处在于等待时间 period 的应用是在一次任务的结束和下一个任务的开始之间。

# 任务提交

提交给线程池的 task 最好是同一种类的 task，因为不同种类的 task 执行的时间可能会有差异，这样的话如果线程池的大小是固定的，那么就会出现执行时间不均匀的情况。

task 中的 threadlocal 最好不要跨任务使用，因为线程池中的 worker 线程可能会随着任务的数量或者任务执行的异常增加或减少，这样跨任务的 threadlocal 传递就会出现问题。

## invokeAll | 调用所有的 Callable

Executors 支持通过 invokeAll()一次批量提交多个 callable。这个方法结果一个 callable 的集合，然后返回一个 future 的列表。

```java
ExecutorService executor = Executors.newWorkStealingPool();

List<Callable<String>> callables = Arrays.asList(
        () -> "task1",
        () -> "task2",
        () -> "task3");

executor.invokeAll(callables)
    .stream()
    .map(future -> {
        try {
            return future.get();
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    })
    .forEach(System.out::println);
```

## invokeAny

批量提交 callable 的另一种方式就是 invokeAny()，它的工作方式与 invokeAll()稍有不同。在等待 future 对象的过程中，这个方法将会阻塞直到第一个 callable 中止然后返回这一个 callable 的结果。 为了测试这种行为，我们利用这个帮助方法来模拟不同执行时间的 callable。这个方法返回一个 callable，这个 callable 休眠指定 的时间直到返回给定的结果。

```java
//这个callable方法是用来构造不同的Callable对象
Callable<String> callable(String result, long sleepSeconds) {
    return () -> {
        TimeUnit.SECONDS.sleep(sleepSeconds);
        return result;
    };
}
```

我们利用这个方法创建一组 callable，这些 callable 拥有不同的执行时间，从 1 分钟到 3 分钟。通过 invokeAny()将这些 callable 提交给一个 executor，返回最快的 callable 的字符串结果-在这个例子中为任务 2：

```java
ExecutorService executor = Executors.newWorkStealingPool();

List<Callable<String>> callables = Arrays.asList(
callable("task1", 2),
callable("task2", 1),
callable("task3", 3));

String result = executor.invokeAny(callables);
System.out.println(result);

// => task2
```

# 线程池

# 自定义线程池

```java
 public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads,
                                  0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>());
}

ExecutorService executor = Executors.newFixedThreadPool(20);

return new ThreadPoolExecutor(20, 20,
                                  0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>());
```

在需要大量自定义配置的情况下，ThreadPoolExecutor 为更为灵活。

```java
ThreadPoolExecutor(int corePoolSize,
               int maximumPoolSize,
               long keepAliveTime,
               TimeUnit unit,
               BlockingQueue<Runnable> workQueue,
               ThreadFactory threadFactory,
               RejectedExecutionHandler handler)
```

```java
public class BoundedExecutor {
    private final Executor exec;
    private final Semaphore semaphore;

    public BoundedExecutor(Executor exec, int bound) {
        this.exec = exec;
        this.semaphore = new Semaphore(bound);
    }

    public void submitTask(final Runnable command)
            throws InterruptedException, RejectedExecutionException {
        semaphore.acquire();
        try {
            exec.execute(new Runnable() {
                public void run() {
                    try {
                        command.run();
                    } finally {
                        semaphore.release();
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            semaphore.release();
            throw e;
        }
    }
}
```

```java
RejectedExecutionHandler block = new RejectedExecutionHandler() {
  rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
     executor.getQueue().put( r );
  }
};

ThreadPoolExecutor pool = new ...
pool.setRejectedExecutionHandler(block);
```

# ExecutorService

并发 API 引入了 ExecutorService 作为一个在程序中直接使用 Thread 的高层次的替换方案。Executors 支持运行异步任务，通常管理一个线程池，这样一来我们就不需要手动去创建新的线程。在不断地处理任务的过程中，线程池内部线程将会得到复用，因此，在我们可以使用一个 executor service 来运行和我们想在我们整个程序中执行的一样多的并发任务。 下面是使用 executors 的第一个代码示例:

```java
ExecutorService executor = Executors.newSingleThreadExecutor();

executor.submit(() -> {
    String threadName = Thread.currentThread().getName();
    System.out.println("Hello " + threadName);
});
// => Hello pool-1-thread-1
```

Executors 类提供了便利的工厂方法来创建不同类型的 executor services。在这个示例中我们使用了一个单线程线程池的 executor。 代码运行的结果类似于上一个示例，但是当运行代码时，你会注意到一个很大的差别：Java 进程从没有停止！Executors 必须显式的停止-否则它们将持续监听新的任务。 ExecutorService 提供了两个方法来达到这个目的——shutdwon()会等待正在执行的任务执行完而 shutdownNow()会终止所有正在执行的任务并立即关闭 execuotr。

```java
try {
    System.out.println("attempt to shutdown executor");
    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);
    }
catch (InterruptedException e) {
    System.err.println("tasks interrupted");
}
finally {
    if (!executor.isTerminated()) {
        System.err.println("cancel non-finished tasks");
    }
    executor.shutdownNow();
    System.out.println("shutdown finished");
}
```

executor 通过等待指定的时间让当前执行的任务终止来“温柔的”关闭 executor。在等待最长 5 分钟的时间后，execuote 最终会通过中断所有的正在执行的任务关闭。一般来说 ExecutorService 与 Callable 经常协同使用，关于 Callable 的讲解可看下文的 Async 模块。

## 结合 Callable 与 Future 进行异步编程

Executors 支持通过 invokeAll()一次批量提交多个 callable。这个方法结果一个 callable 的集合，然后返回一个 future 的列表。

```java
ExecutorService executor = Executors.newWorkStealingPool();

List<Callable<String>> callables = Arrays.asList(
        () -> "task1",
        () -> "task2",
        () -> "task3");

executor.invokeAll(callables)
    .stream()
    .map(future -> {
        try {
            return future.get();
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    })
    .forEach(System.out::println);
```

在这个例子中，我们利用 Java8 中的函数流(stream)来处理 invokeAll()调用返回的所有 future。我们首先将每一个 future 映射到它的返回值，然后将每个值打印到控制台。如果你还不属性 stream，可以阅读我的[Java8 Stream 教程](http://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/)。

批量提交 callable 的另一种方式就是 invokeAny()，它的工作方式与 invokeAll()稍有不同。在等待 future 对象的过程中，这个方法将会阻塞直到第一个 callable 中止然后返回这一个 callable 的结果。 为了测试这种行为，我们利用这个帮助方法来模拟不同执行时间的 callable。这个方法返回一个 callable，这个 callable 休眠指定 的时间直到返回给定的结果。

```java
//这个callable方法是用来构造不同的Callable对象
Callable<String> callable(String result, long sleepSeconds) {
    return () -> {
        TimeUnit.SECONDS.sleep(sleepSeconds);
        return result;
    };
}
```

我们利用这个方法创建一组 callable，这些 callable 拥有不同的执行时间，从 1 分钟到 3 分钟。通过 invokeAny()将这些 callable 提交给一个 executor，返回最快的 callable 的字符串结果-在这个例子中为任务 2:

```java
ExecutorService executor = Executors.newWorkStealingPool();

List<Callable<String>> callables = Arrays.asList(
callable("task1", 2),
callable("task2", 1),
callable("task3", 3));

String result = executor.invokeAny(callables);
System.out.println(result);

// => task2
```

## 生命周期

ExecutorService 接口继承了 Executor 接口，定义了一些生命周期的方法

```
public interface

ExecutorService extends Executor {
void shutdown();
List<Runnable> shutdownNow();
boolean isShutdown();
boolean isTerminated();
boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException;
}
```

1、shutdown 方法：这个方法会平滑地关闭 ExecutorService，当我们调用这个方法时， ExecutorService 停止接受任何新的任务且等待已经提交的任务执行完成(已经提交的任务会分两类：一类是已 经在执行的，另一类是还没有开始执行的)，当所有已经提交的任务执行完毕后将会关闭 ExecutorService。
2、awaitTermination 方法：这个方法有两个参数，一个是 timeout 即超 时时间，另一个是 unit 即时间单位。这个方法会使线程等待 timeout 时长，当超过 timeout 时间后，会监测 ExecutorService 是否已经关闭，若关闭则返回 true，否则返回 false。一般情况下会和 shutdown 方法组合使用 。

# ScheduledExecutorService: 定期执行

为了持续的多次执行常见的任务，我们可以利用调度线程池 ScheduledExecutorService 支持任务调度，持续执行或者延迟一段时间后执行。 下面的实例，调度一个任务在延迟 3 分钟后执行:

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());
ScheduledFuture<?> future = executor.schedule(task, 3, TimeUnit.SECONDS);

TimeUnit.MILLISECONDS.sleep(1337);

long remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
System.out.printf("Remaining Delay: %sms", remainingDelay);
```

调度一个任务将会产生一个专门的 future 类型——ScheduleFuture，它除了提供了 Future 的所有方法之外，他还提供了 getDelay()方法来获得剩余的延迟。在延迟消逝后，任务将会并发执行。 为了调度任务持续的执行，executors 提供了两个方法 scheduleAtFixedRate()和 scheduleWithFixedDelay()。第一个方法用来以固定频率来执行一个任务，比如，下面这个示例中，每分钟一次:

```java
ScheduledExecutorService executor =     Executors.newScheduledThreadPool(1);

Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());

int initialDelay = 0;
int period = 1;
executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
```

另外，这个方法还接收一个初始化延迟，用来指定这个任务首次被执行等待的时长。 请记住：scheduleAtFixedRate()并不考虑任务的实际用时。所以，如果你指定了一个 period 为 1 分钟而任务需要执行 2 分钟，那么线程池为了性能会更快的执行。在这种情况下，你应该考虑使用 scheduleWithFixedDelay()。这个方法的工作方式与上我们上面描述的类似。不同之处在于等待时间 period 的应用是在一次任务的结束和下一个任务的开始之间。例如:

## ExecutorService 分类

```
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Ch09_Executor {
   private static void run(ExecutorService threadPool) {
for(int i = 1; i < 5; i++) {
            final int taskID = i;
            threadPool.execute(new Runnable() {
                @Override
public void run() {
                    for(int i = 1; i < 5; i++) {
                        try {
                            Thread.sleep(20);// 为了测试出效果，让每次任务执行都需要一定时间
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("第" + taskID + "次任务的第" + i + "次执行");
                    }
                }
            });
        }
        threadPool.shutdown();// 任务执行完毕，关闭线程池
   }
    public static void main(String[] args) {
   // 创建可以容纳3个线程的线程池
   ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
   // 线程池的大小会根据执行的任务数动态分配
   ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
            // 创建单个线程的线程池，如果当前线程在执行任务时突然中断，则会创建一个新的线程替代它继续执行任务
   ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
   // 效果类似于Timer定时器
   ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(3);

   run(fixedThreadPool);
//	   run(cachedThreadPool);
//	   run(singleThreadPool);
//	   run(scheduledThreadPool);
    }

}


```

### CachedThreadPool

CachedThreadPool 会创建一个缓存区，将初始化的线程缓存起来。会终止并且从缓存中移除已有 60 秒未被使用的线程。如果线程有可用的，就使用之前创建好的线程，如果线程没有可用的，就新创建线程。

- 重用：缓存型池子，先查看池中有没有以前建立的线程，如果有，就 reuse；如果没有，就建一个新的线程加入池中
- 使用场景：缓存型池子通常用于执行一些生存期很短的异步型任务，因此在一些面向连接的 daemon 型 SERVER 中用得不多。
- 超时：能 reuse 的线程，必须是 timeout IDLE 内的池中线程，缺省 timeout 是 60s，超过这个 IDLE 时长，线程实例将被终止及移出池。
- 结束：注意，放入 CachedThreadPool 的线程不必担心其结束，超过 TIMEOUT 不活动，其会自动被终止。

```
    // 线程池的大小会根据执行的任务数动态分配
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0,                 //core pool size
                                      Integer.MAX_VALUE, //maximum pool size
                                      60L,               //keep alive time
                                      TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }
```

执行结果，可以看出 4 个任务交替执行

```
第1次任务的第1次执行
第4次任务的第1次执行
第3次任务的第1次执行
第2次任务的第1次执行
第3次任务的第2次执行
第4次任务的第2次执行
第2次任务的第2次执行
第1次任务的第2次执行
第2次任务的第3次执行
第4次任务的第3次执行
第3次任务的第3次执行
第1次任务的第3次执行
第2次任务的第4次执行
第1次任务的第4次执行
第3次任务的第4次执行
第4次任务的第4次执行
```

### FixedThreadPool

在 FixedThreadPool 中，有一个固定大小的池。
如果当前需要执行的任务超过池大小，那么多出的任务处于等待状态，直到有空闲下来的线程执行任务，
如果当前需要执行的任务小于池大小，空闲的线程也不会去销毁。

- 重用：fixedThreadPool 与 cacheThreadPool 差不多，也是能 reuse 就用，但不能随时建新的线程
- 固定数目：其独特之处在于，任意时间点，最多只能有固定数目的活动线程存在，此时如果有新的线程要建立，只能放在另外的队列中等待，直到当前的线程中某个线程终止直接被移出池子
- 超时：和 cacheThreadPool 不同，FixedThreadPool 没有 IDLE 机制(可能也有，但既然文档没提，肯定非常长，类似依赖上层的 TCP 或 UDP IDLE 机制之类的)，
- 使用场景：所以 FixedThreadPool 多数针对一些很稳定很固定的正规并发线程，多用于服务器
- 源码分析：从方法的源代码看，cache 池和 fixed 池调用的是同一个底层池，只不过参数不同：
  fixed 池线程数固定，并且是 0 秒 IDLE(无 IDLE)
  cache 池线程数支持 0-Integer.MAX_VALUE(显然完全没考虑主机的资源承受能力)，60 秒 IDLE

```
// 创建可以容纳3个线程的线程池
ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, //core pool size
                                      nThreads, //maximum pool size
                                      0L,       //keep alive time
                                      TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
}


```

执行结果：创建了一个固定大小的线程池，容量为 3，然后循环执行了 4 个任务。由输出结果可以看到，前 3 个任务首先执行完，然后空闲下来的线程去执行第 4 个任务。

```
第1次任务的第1次执行
第3次任务的第1次执行
第2次任务的第1次执行
第3次任务的第2次执行
第2次任务的第2次执行
第1次任务的第2次执行
第3次任务的第3次执行
第1次任务的第3次执行
第2次任务的第3次执行
第3次任务的第4次执行
第1次任务的第4次执行
第2次任务的第4次执行
第4次任务的第1次执行
第4次任务的第2次执行
第4次任务的第3次执行
第4次任务的第4次执行
```

### SingleThreadExecutor

SingleThreadExecutor 得到的是一个单个的线程，这个线程会保证你的任务执行完成。如果当前线程意外终止，会创建一个新线程继续执行任务，这和我们直接创建线程不同，也和 newFixedThreadPool(1)不同。

```
// 创建单个线程的线程池，如果当前线程在执行任务时突然中断，则会创建一个新的线程替代它继续执行任务
ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1,  //core pool size
                                    1,  //maximum pool size
                                    0L, //keep alive time
                                    TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
}
```

执行结果：四个任务顺序执行

```
第1次任务的第1次执行
第1次任务的第2次执行
第1次任务的第3次执行
第1次任务的第4次执行
第2次任务的第1次执行
第2次任务的第2次执行
第2次任务的第3次执行
第2次任务的第4次执行
第3次任务的第1次执行
第3次任务的第2次执行
第3次任务的第3次执行
第3次任务的第4次执行
第4次任务的第1次执行
第4次任务的第2次执行
第4次任务的第3次执行
第4次任务的第4次执行
```

### ScheduledThreadPool

ScheduledThreadPool 是一个固定大小的线程池，与 FixedThreadPool 类似，执行的任务是定时执行。

```
// 效果类似于Timer定时器
ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(3);

public ScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize,      //core pool size
              Integer.MAX_VALUE, //maximum pool size
              0,                 //keep alive time
              TimeUnit.NANOSECONDS,
              new DelayedWorkQueue());
}
```

执行结果：它是定时执行的

```
第1次任务的第1次执行
第2次任务的第1次执行
第3次任务的第1次执行
第2次任务的第2次执行
第1次任务的第2次执行
第3次任务的第2次执行
第2次任务的第3次执行
第1次任务的第3次执行
第3次任务的第3次执行
第2次任务的第4次执行
第1次任务的第4次执行
第3次任务的第4次执行
第4次任务的第1次执行
第4次任务的第2次执行
第4次任务的第3次执行
第4次任务的第4次执行
```

## Concurrence Test

- [concurrency-torture-testing-your-code-within-the-java-memory-model](http://zeroturnaround.com/rebellabs/concurrency-torture-testing-your-code-within-the-java-memory-model/)
