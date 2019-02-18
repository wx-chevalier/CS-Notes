# Callable & Future

Executors 本身提供了一种对于多线程的封装，而 Executor 还支持另一种类型的任务——Callable。Callables 也是类似于 Runnable 的函数接口，不同之处在于，Callable 返回一个值 Future，用来描述一个异步计算的结果。Callable 接口本身是一个 Lambda 表达式(函数式接口)：

```java
Callable<Integer> task = () -> {
    try {
        TimeUnit.SECONDS.sleep(1);
        return 123;
    }
    catch (InterruptedException e)
        throw new IllegalStateException("task interrupted", e);
    }
};
```

Callbale 也可以像 Runnable 一样提交给 executor services。但是 submit()不会等待任务完成，executor service 不能直接返回 callable 的结果。不过，executor 可以返回一个 Future 类型的结果，它可以用来在稍后某个时间取出实际的结果。

```java
ExecutorService executor = Executors.newFixedThreadPool(1);
Future<Integer> future = executor.submit(task);

System.out.println("future done? " + future.isDone());

Integer result = future.get();

System.out.println("future done? " + future.isDone());
System.out.print("result: " + result);
```

Future 与底层的 executor service 紧密的结合在一起。记住，如果你关闭 executor，所有的未中止的 future 都会抛出异常。

```java
executor.shutdownNow();
future.get();
```

任何 future.get()调用都会阻塞，然后等待直到 callable 中止。在最糟糕的情况下，一个 callable 持续运行——因此使你的程序将没有响应。我们可以简单的传入一个时长来避免这种情况。

```java
ExecutorService executor = Executors.newFixedThreadPool(1);

Future<Integer> future = executor.submit(() -> {
    try {
        TimeUnit.SECONDS.sleep(2);
        return 123;
    }
    catch (InterruptedException e) {
        throw new IllegalStateException("task interrupted", e);
    }
});

future.get(1, TimeUnit.SECONDS);
```

运行上面的代码将会产生一个 TimeoutException：

```
Exception in thread "main" java.util.concurrent.TimeoutException
    at java.util.concurrent.FutureTask.get(FutureTask.java:205)
```
