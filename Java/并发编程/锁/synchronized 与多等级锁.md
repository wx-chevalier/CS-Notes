# synchronized

synchronized 也是经常用到的，它给人的印象一般是重量级锁。在 JDK1.6 后，对 synchronized 进行了一系列优化，引入了偏向锁和轻量级锁，对锁的存储结构和升级过程，有效减少获得锁和释放锁带来的性能消耗。

synchronized 关键字，同时解决了原子性、可见性、有序性问题:

- 可见性：按照 Java 内存模型（Java Memory Model ,JMM）的规范，对一个变量解锁之前，必须先把此变量同步回主存中，这样解锁后，后续线程就可以访问到被修改后的值。所以被 synchronized 锁住的对象，其值具有可见性。
- 原子性：通过监视器锁，可以保证 synchronized 修饰的代码在同一时间，只能被一个线程访问，在锁未释放之前其它线程无法进入该方法或代码块，保证了操作的原子性。
- 有序性：synchronized 关键字并不禁止指令重排，但是由于程序是以单线程的方式执行的，所以执行的结果是确定的，不会受指令重排的干扰，有序性不再是个问题。

需要注意的是，当我们使用 synchronized 关键字，管理某个状态时，必须对访问这个对象的所有操作，都加上 synchronized 关键字， 否则仍然会有并发安全性问题。

## 同步使用

对于，普通同步方法，锁是当前实例对象。`public synchronized void test(){...}`

对于静态同步方法，锁是当前类的 Class 对象。`public static synchronized void test(...){}`

对于对于同步方法块，锁是 synchronized 括号中里配置的对象。`synchronized(instance){...}`

## 底层实现

```java
public class SynchronizedDemo {
     //同步方法
    public synchronized void syncMethod(){
        System.out.println("Hello World");
    }

    //同步代码块
    public void syncBlock(){
        synchronized (this){
            System.out.println("Hello World");
        }
    }
}
```

以上的示例代码，编译后的字节码为:

```java
public synchronized void syncMethod();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_SYNCHRONIZED
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #3                  // String Hello World
         5: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return

  public void syncBlock();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=2, locals=3, args_size=1
         0: ldc           #5                  // class com/hollis/SynchronizedTest
         2: dup
         3: astore_1
         4: monitorenter
         5: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         8: ldc           #3                  // String Hello World
        10: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        13: aload_1
        14: monitorexit
        15: goto          23
        18: astore_2
        19: aload_1
        20: monitorexit
        21: aload_2
        22: athrow
        23: return
```

对于同步方法，JVM 使用 ACC_SYNCHRONIZED 标记符，对于同步代码块，JVM 采用 monitorenter、monitorexit 指令。这两种方式殊途同归，都是通过获取和对象关联的监视器锁(monitor), 来实现同步的。

# 多等级锁

synchronized 用到的锁存在 Java 对象头里，若对象非数组类型，用 32bit 存储(2 个字宽，32 虚拟机一个字宽为 4 字节，一个字节 8bit)。其中 MarkWord 存储和锁相关的信息:

![](https://i.postimg.cc/KvKDzFxG/image.png)

锁有四个等级: 无锁->偏向锁->轻量级锁->重量级锁。如果存在竞争，就会不断升级，但不会降级。

|          | 优点                                                             | 缺点                                           | 适用场景                         |
| -------- | ---------------------------------------------------------------- | ---------------------------------------------- | -------------------------------- |
| 偏向锁   | 加锁和解锁不需要额外的消耗，和执行非同步方法比仅存在纳秒级的差距 | 如果线程间存在锁竞争，会带来额外的锁撤销的消耗 | 适用于只有一个线程访问同步块场景 |
| 轻量级锁 | 竞争的线程不会阻塞，提高了程序的响应速度                         | 如果始终得不到锁竞争的线程使用自旋会消耗 CPU   | 追求响应时间,锁占用时间很短      |
| 重量级锁 | 线程竞争不使用自旋，不会消耗 CPU                                 | 线程阻塞，响应时间缓慢                         | 追求吞吐量,锁占用时间较长        |

## 偏向锁

多数情况下，锁不会存在竞争，而是同一个线程多次获得。当某个线程访问同步块代码时，会将锁对象和栈帧中的锁记里存储锁偏向的线程 ID，以后线程在进入和退出同步块时不需要进行 CAS 操作来加锁和解锁，只需简单比对一下对象头中的 MarkWord 里的线程 ID，如果一致则表示线程获得锁。若不一致，再继续测试偏向锁的标识是否为 1：如果没有设置(无锁状态)，用 CAS(Compare and Swap)竞争锁；如果设置了，尝试使用 CAS 将对象头的偏向锁指向当前线程。

当有另一个线程尝试竞争锁时，持有偏向锁的线程才会释放锁。需要等待全局安全点（在这个时间点上没有字节码正在执行），它会首先暂停拥有偏向锁的线程，然后检查持有偏向锁的线程是否活着，如果线程不处于活动状态，则将对象头设置成无锁状态，如果线程仍然活着，拥有偏向锁的栈会被执行，遍历偏向对象的锁记录，栈中的锁记录和对象头的 Mark Word，要么重新偏向于其他线程，要么恢复到无锁或者标记对象不适合作为偏向锁，最后唤醒暂停的线程。

Java 6，7 默认开启偏向锁，可以通过 JVM 的参数 `-XX:-UsebiasedLocking=false` 关闭。

## 轻量级锁

加锁的流程中，锁记录存储在栈桢，会将对象头的 MarkWord 复制到锁记录。线程在执行同步块时，会尝试用 CAS 将对象头的 MarkWord 替换为指向锁记录的指针，若成功，获得锁；失败表示其他线程竞争锁，当前线程尝试使用自旋获取锁。解锁的流程中，类似于加锁反向操作，会将锁记录复制会对象头的 MarkWord。若成功，表示操作过程中没有竞争发生；若失败，存在竞争，锁会膨胀成重量级锁。

![](https://i.postimg.cc/PqFmhF9p/image.png)

当膨胀到重量级锁时，不会再通过自选获得锁(自旋时线程处于活动状态，会消耗 CPU)，而是将线程阻塞，获得锁的线程执行完后会释放重量级锁，此时唤醒因为锁阻塞的线程，进行新一轮的竞争。

## 重量级锁

# 链接

- https://blog.csdn.net/significantfrank/article/details/80399179 Synchronized 和 Lock 该如何选择
