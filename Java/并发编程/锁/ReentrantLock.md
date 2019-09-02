# ReentrantLock

可重入互斥锁具有与使用 synchronized 的隐式监视器锁具有相同的行为和语义，但具有更好扩展功能。ReentrantLock 由最后成功锁定的线程拥有，而且还未解锁。当锁未被其他线程占有时，线程调用 lock()将返回并且成功获取锁。如果当前线程已拥有锁，则该方法将立即返回。这可以使用方法 isHeldByCurrentThread()和 getHoldCount()来检查。

构造函数接受可选的 fairness 参数。当设置为 true 时，在竞争条件下，锁定有利于赋予等待时间最长线程的访问权限。否则，锁将不保证特定的访问顺序。在多线程访问的情况，使用公平锁比默认设置，有着更低的吞吐量，但是获得锁的时间比较小而且可以避免等待锁导致的饥饿。但是，锁的公平性并不能保证线程调度的公平性。因此，使用公平锁的许多线程中的一个可以连续多次获得它，而其他活动线程没有进展并且当前没有持有锁。不定时的 tryLock（）方法不遵循公平性设置。即使其他线程正在等待，如果锁可用，它也会成功。

- 任意指定锁的起始位置

- 中断响应

- 锁申请等待限时 tryLock()

- 公平锁

```java
public class Test implements Runnable{
    public synchronized void get(){
     System.out.println(Thread.currentThread().getId());
     //在子方法里又进入了锁
     set();
    }
    public synchronized void set(){
     System.out.println(Thread.currentThread().getId());
    }
    @Override
    public void run() {
     get();
    }
    public static void main(String[] args) {
     Test ss=new Test();
     new Thread(ss).start();
     new Thread(ss).start();
     new Thread(ss).start();
    }
}
```

# 链接

- [Java 中的 ReentrantLock 和 synchronized 两种锁定机制的对比](http://my.eoe.cn/niunaixiaoshu/archive/5227.html)
