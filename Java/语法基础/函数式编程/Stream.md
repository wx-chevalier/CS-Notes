# Java Stream

# 操作符

Java Stream 中最核心的就是操作符，往往某个 Stream 由多个中间操作符（Intermediate Operations）以及最终操作符（Terminal Operation）构成。中间操作符会依次获取并且处理元素，所有的最终操作符都是懒操作的，仅在流启动之后才会工作。

## 流变换

peek 操作符往往被用于调试，或者在变换之中进行某些元素操作：

```java
// 用于打印中间状态
Stream.of("one", "two", "three", "four")
  .filter(e -> e.length() > 3)
  .peek(e -> System.out.println("Filtered value: " + e))
  .map(String::toUpperCase)
  .peek(e -> System.out.println("Mapped value: " + e))
  .collect(Collectors.toList());

// 用于修改初始状态
Stream<User> userStream = Stream.of(new User("Alice"), new User("Bob"), new User("Chuck"));
userStream.peek(u -> u.setName(u.getName().toLowerCase()))
.forEach(System.out::println);
```

# Parallel Stream

Looking at the stream’s parallel method, you may wonder where the threads used by the parallel stream come from, how many there are, and how you can customize the process. Parallel streams internally use the default `ForkJoinPool`, which by default has as many threads as you have processors, as returned by `Runtime.getRuntime().availableProcessors()`. But you can change the size of this pool using the system property `java.util.concurrent.ForkJoinPool.common.parallelism`.

The infrastructure used behind the scenes by parallel streams to execute operations in parallel is the fork/join framework introduced in Java 7. It’s vital to have a good understanding of the parallel stream internals in order to use them correctly. The fork/join framework was designed to recursively split a parallelizable task into smaller tasks and then combine the results of each subtask to produce the overall result. It’s an implementation of the `ExecutorService` interface, which distributes those subtasks to worker threads in a thread pool, called `ForkJoinPool`.

The `Spliterator` stands for “splitable iterator.” Like Iterators, Spliterators are used to traverse the elements of a source, but they’re also designed to do this in parallel. Although you may not have to develop your own Spliterator in practice, understanding how to do so will give you a wider understanding about how parallel streams work.

The algorithm that splits a Stream into multiple parts is a recursive process. In the first step, a method called `trySplit` is invoked on the first Spliterator and generates a second one. Then in step 2 it’s called again on these two Spliterators, which results in a total of four. The framework keeps invoking the method trySplit on a Spliterator until it returns null to signal that the data structure that it’s processing is no longer divisible. Finally, this recursive splitting process terminates when all Spliterators have returned null to a trySplit invocation.

The last abstract method declared by the Spliterator interface is characteristics, which returns an int encoding the set of characteristics of the Spliterator itself. The Spliterator clients can use these characteristics to better control and optimize its usage. They are: `ORDERED`, `DISTINCT`, `SORTED`, `SIZED`, `NONNULL`, `IMMUTABLE`, `CONCURRENT`, and `SUBSIZED`. Depending on the specific characteristics of a stream, it may in fact not run in parallel at all.

The book that explains all this in detail is: [Java 8 in Action: Lambdas, streams, and functional-style programming (Raoul-Gabriel Urma, Mario Fusco, and Alan Mycroft)](http://rads.stackoverflow.com/amzn/click/1617291994), from Manning. See Chapter 7:**Parallel data processing and performance**.

# 异常处理

可以在 lambda 中做一个简单的 try-catch 并将异常包装成一个 RuntimeException:

```java

```

# Todos

- https://mp.weixin.qq.com/s/Acd2PKQK3fkXHcjJduNPNQ
