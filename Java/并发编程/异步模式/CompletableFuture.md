# CompleteableFuture

CompletableFuture 类实现了 CompletionStage 和 Future 接口，JDK 吸收了 guava 的设计思想，加入了 Future 的诸多扩展功能形成了 CompletableFuture，CompletableFuture 的基础用法就是当做对于未来对象的包裹使用：

```java
// 无参构造函数简单的创建 CompletableFuture
CompletableFuture<String> completableFuture = new CompletableFuture<String>();

// 使用 CompletableFuture.get() 方法获取结果，该方法会一直阻塞直到 Future 完成
String result = completableFuture.get()

// 设置 Future 完成
completableFuture.complete("Future's Result")
```

我们也可以手动地监听完成函数：

```java
CompletableFuture completableFuture = new CompletableFuture();

completableFuture.whenComplete(new BiConsumer() {
    @Override
    public void accept(Object o, Object o2) {
        //handle complete
    }
}); // complete the task

completableFuture.complete(new Object())
```

### 链式调用与转换

CompletableFuture 还提供了 runAsync/supplyAsync 等静态方法，让我们创建便捷地异步执行流程：

```java
// Variations of runAsync() and supplyAsync() methods
static CompletableFuture<Void> runAsync(Runnable runnable)
static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)
static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)
static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)

String result = CompletableFuture.supplyAsync(() -> "hello").thenApply(s -> s + " world").join();

// 创建一个线程池，并传递给其中一个方法
Executor executor = Executors.newFixedThreadPool(10);
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    ...
    TimeUnit.SECONDS.sleep(1);
    return "Result of the asynchronous computation";
}, executor);
```

```java
// CompletionStage 是一个接口，从命名上看得知是一个完成的阶段，它里面的方法也标明是在某个运行阶段得到了结果之后要做的事情。
public <U> CompletionStage<U> thenApply(Function<? super T,? extends U> fn);
public <U> CompletionStage<U> thenApplyAsync(Function<? super T,? extends U> fn);
public <U> CompletionStage<U> thenApplyAsync(Function<? super T,? extends U> fn,Executor executor);

CompletableFuture<String> welcomeText = CompletableFuture.supplyAsync(() -> {
    try {
        TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
       throw new IllegalStateException(e);
    }
    return "Rajeev";
}).thenApply(name -> {
    return "Hello " + name;
}).thenApply(greeting -> {
    return greeting + ", Welcome to the CalliCoder Blog";
});

System.out.println(welcomeText.get());
```

CompletableFuture 的 `then*` 方法会生成 CompletionStage 对象，该对象的 `then*` 方法能够接收 Consumer:

```java
// 获取上一个单元的执行结果，处理和改变 CompletableFuture 的结果
public CompletionStage<Void> thenAccept(Consumer<? super T> action);
public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action);
public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action,Executor executor);

// thenAccept() example
CompletableFuture.supplyAsync(() -> {
	return ProductService.getProductDetail(productId);
}).thenAccept(product -> {
	System.out.println("Got product detail from remote service " + product.getName())
});
```

thenAccept()可以访问 CompletableFuture 的结果，但 thenRun()不能访 Future 的结果，它持有一个 Runnable 返回 CompletableFuture:

```java
// 对上一步的计算结果不关心，执行下一个操作
public CompletionStage<Void> thenRun(Runnable action);
public CompletionStage<Void> thenRunAsync(Runnable action);
public CompletionStage<Void> thenRunAsync(Runnable action,Executor executor);

// thenRun() example
CompletableFuture.supplyAsync(() -> {
    // Run some computation
}).thenRun(() -> {
    // Computation Finished.
});
```

### 组合

使用 thenCompose()组合两个独立的 future, thenCombine() 当两个独立的 Future 都完成的时候，用来做一些事情。

```java
// thenCompose, 独立等待
CompletableFuture<Double> result = getUserDetail(userId)
.thenCompose(user -> getCreditRating(user));

// thenCombine, 等待全部执行完成
System.out.println("Retrieving weight.");
CompletableFuture<Double> weightInKgFuture = CompletableFuture.supplyAsync(() -> {
    ...
    TimeUnit.SECONDS.sleep(1);
    return 65.0;
});

System.out.println("Retrieving height.");
CompletableFuture<Double> heightInCmFuture = CompletableFuture.supplyAsync(() -> {
    ...
    TimeUnit.SECONDS.sleep(1);
    return 177.8;
});

System.out.println("Calculating BMI.");
CompletableFuture<Double> combinedFuture = weightInKgFuture
        .thenCombine(heightInCmFuture, (weightInKg, heightInCm) -> {
    Double heightInMeter = heightInCm/100;
    return weightInKg/(heightInMeter*heightInMeter);
});

System.out.println("Your BMI is - " + combinedFuture.get());
```

CompletableFuture.allOf 的使用场景是当你一个列表的独立 future，并且你想在它们都完成后并行的做一些事情。

```java
List<String> webPageLinks = Arrays.asList(...)	// A list of 100 web page links

// Download contents of all the web pages asynchronously
List<CompletableFuture<String>> pageContentFutures = webPageLinks.stream()
        .map(webPageLink -> downloadWebPage(webPageLink))
        .collect(Collectors.toList());


// Create a combined Future using allOf()
CompletableFuture<Void> allFutures = CompletableFuture.allOf(
        pageContentFutures.toArray(new CompletableFuture[pageContentFutures.size()])
);
```

CompletableFuture.anyOf()和其名字介绍的一样，当任何一个 CompletableFuture 完成的时候，返回一个新的 CompletableFuture。

```java
CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(future1, future2, future3);

System.out.println(anyOfFuture.get()); // Result of Future 2
```

### 异常处理

如果在原始的 supplyAsync()任务中发生一个错误，这时候没有任何 thenApply 会被调用并且 future 将以一个异常结束。如果在第一个 thenApply 发生错误，这时候第二个和第三个将不会被调用，同样的，future 将以异常结束。

```java
CompletableFuture.supplyAsync(() -> {
	// Code which might throw an exception
	return "Some result";
}).thenApply(result -> {
	return "processed result";
}).thenApply(result -> {
	return "result after further processing";
}).thenAccept(result -> {
	// do something with the final result
});
```

使用 exceptionally() 回调处理异常 exceptionally()回调给你一个从原始 Future 中生成的错误恢复的机会。你可以在这里记录这个异常并返回一个默认值。

```java
CompletableFuture<String> maturityFuture = CompletableFuture.supplyAsync(() -> {
    ...
    throw new IllegalArgumentException("Age can not be negative");
    ...
}).exceptionally(ex -> {
    System.out.println("Oops! We have an exception - " + ex.getMessage());
    return "Unknown!";
});。
```

使用 handle() 方法处理异常 API 提供了一个更通用的方法，handle()从异常恢复，无论一个异常是否发生它都会被调用。

```java
CompletableFuture<String> maturityFuture = CompletableFuture.supplyAsync(() -> {
    ...
}).handle((res, ex) -> {
    if(ex != null) {
        System.out.println("Oops! We have an exception - " + ex.getMessage());
        return "Unknown!";
    }
    return res;
});
```

如果异常发生，res 参数将是 null，否则，ex 将是 null。

### ListenableFuture

```java
ListenableFuture listenable = service.submit(...);

Futures.addCallback(listenable, new FutureCallback<Object>() {
    @Override
    public void onSuccess(Object o) {}

    @Override
    public void onFailure(Throwable throwable) {}
})
```
