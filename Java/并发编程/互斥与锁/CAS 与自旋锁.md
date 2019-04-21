# 乐观锁 CAS

在 JDK1.5 中新增 `java.util.concurrent` (J.U.C)就是建立在 CAS 之上的。相对于对于 synchronized 这种阻塞算法，CAS 是非阻塞算法的一种常见实现。所以 J.U.C 在性能上有了很大的提升。我们以 `java.util.concurrent` 中的 `AtomicInteger` 为例，看一下在不使用锁的情况下是如何保证线程安全的。主要理解 `getAndIncrement` 方法，该方法的作用相当于 `++i` 操作。

```java
public class AtomicInteger extends Number implements java.io.Serializable {

    private volatile int value;

    public final int get() {
        return value;
    }

    public final int getAndIncrement() {
        for (;;) {
            int current = get();
            int next = current + 1;
            if (compareAndSet(current, next))
                return current;
        }
    }

    public final boolean compareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
    }
}
```

在没有锁的机制下需要字段 value 要借助 volatile 原语，保证线程间的数据是可见的。这样在获取变量的值的时候才能直接读取。然后来看看 `++i` 是怎么做到的。`getAndIncrement` 采用了 CAS 操作，每次从内存中读取数据然后将此数据和 `+1` 后的结果进行 CAS 操作，如果成功就返回结果，否则重试直到成功为止。而 `compareAndSet` 利用 JNI 来完成 CPU 指令的操作。

# 自旋锁

自旋锁是采用让当前线程不停地的在循环体内执行实现的，当循环的条件被其他线程改变时才能进入临界区。

```java
public class SpinLock {

  private AtomicReference<Thread> sign =new AtomicReference<>();

  public void lock(){
    Thread current = Thread.currentThread();
    while(!sign .compareAndSet(null, current)){
    }
  }

  public void unlock (){
    Thread current = Thread.currentThread();
    sign .compareAndSet(current, null);
  }
}
```

使用了 CAS 原子操作，lock 函数将 owner 设置为当前线程，并且预测原来的值为空。unlock 函数将 owner 设置为 null，并且预测值为当前线程。当有第二个线程调用 lock 操作时由于 owner 值不为空，导致循环一直被执行，直至第一个线程调用 unlock 函数将 owner 设置为 null，第二个线程才能进入临界区。由于自旋锁只是将当前线程不停地执行循环体，不进行线程状态的改变，所以响应速度更快。但当线程数不停增加时，性能下降明显，因为每个线程都需要执行，占用 CPU 时间。如果线程竞争不激烈，并且保持锁的时间段。适合使用自旋锁。
