# CountDownLatch

一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待。用给定的计数 初始化 CountDownLatch。由于调用了 countDown() 方法，所以在当前计数到达零之前，await 方法会一直受阻塞。之后，会释放所有等待的线程，await 的所有后续调用都将立即返回。这种现象只出现一次——计数无法被重置。

一个线程(或者多个)， 等待另外 N 个线程完成某个事情之后才能执行。在一些应用场合中，需要等待某个条件达到要求后才能做后面的事情；同时当线程都完成后也会触发事件，以便进行后面的操作。 这个时候就可以使用 CountDownLatch。CountDownLatch 最重要的方法是 countDown()和 await()，前者主要是倒数一次，后者是等待倒数到 0，如果没有到达 0，就只有阻塞等待了。

```java
public void countDown()
```

递减锁存器的计数，如果计数到达零，则释放所有等待的线程。如果当前计数大于零，则将计数减少。如果新的计数为零，出于线程调度目的，将重新启用所有的等待线程。如果当前计数等于零，则不发生任何操作。

```java
public boolean await(long timeout,
                     TimeUnit unit)
              throws InterruptedException
```

使当前线程在锁存器倒计数至零之前一直等待，除非线程被中断或超出了指定的等待时间。如果当前计数为零，则此方法立刻返回 true 值。如果当前计数大于零，则出于线程调度目的，将禁用当前线程，且在发生以下三种情况之一前，该线程将一直处于休眠状态：由于调用 countDown() 方法，计数到达零；或者其他某个线程中断当前线程；或者已超出指定的等待时间。

如果计数到达零，则该方法返回 true 值。如果当前线程在进入此方法时已经设置了该线程的中断状态；或者在等待时被中断，则抛出 InterruptedException，并且清除当前线程的已中断状态。如果超出了指定的等待时间，则返回值为 false。如果该时间小于等于零，则此方法根本不会等待。

```java
// 开始的倒数锁
final CountDownLatch begin = new CountDownLatch(1);

// 结束的倒数锁
final CountDownLatch end = new CountDownLatch(10);

// 十名选手
final ExecutorService exec = Executors.newFixedThreadPool(10);

for (int index = 0; index < 10; index++) {
    final int NO = index + 1;
    Runnable run = new Runnable() {
        public void run() {
            try {
                // 如果当前计数为零，则此方法立即返回。
                // 等待
                begin.await();
                Thread.sleep((long) (Math.random() * 10000));
                System.out.println("No." + NO + " arrived");
            } catch (InterruptedException e) {
            } finally {
                // 每个选手到达终点时，end 就减一
                end.countDown();
            }
        }
    };
    exec.submit(run);
}

// begin 减一，开始游戏
begin.countDown();
// 等待 end 变为 0，即所有选手到达终点
end.await();
System.out.println("Game Over");
exec.shutdown();
```
