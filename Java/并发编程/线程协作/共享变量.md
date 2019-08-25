# 共享变量

Java 编程语言允许线程访问共享变量，为了确保共享变量能被准确和一致的更新，线程应该确保通过排他锁单独获得这个变量。

```java
public class MySharedObject {

    // static variable pointing to instance of MySharedObject
    public static final MySharedObject sharedInstance =
        new MySharedObject();

    // member variables pointing to two objects on the heap
    public Integer object2 = new Integer(22);
    public Integer object4 = new Integer(44);

    public long member1 = 12345;
    public long member1 = 67890;
}

public class MyRunnable implements Runnable() {

    public void run() {
        methodOne();
    }

    public void methodOne() {
        int localVariable1 = 45;

        MySharedObject localVariable2 =
            MySharedObject.sharedInstance;

        //... do more with local variables.

        methodTwo();
    }

    public void methodTwo() {
        Integer localVariable1 = new Integer(99);

        //... do more with local variable.
    }
}
```

# volatile

Java 中的 volatile 关键字主要即是保证了变量的可见性，而不是原子性。JVM 中每一个变量都有一个主内存，为了保证最佳性能，JVM 允许线程从主内存拷贝一份私有拷贝，然后在线程读取变量的时候从主内存里面读，退出的时候，将修改的值同步到主内存。形象而言，对于变量 t，A 线程对 t 变量修改的值，对 B 线程是可见的。但是 A 获取到 t 的值加 1 之后，突然挂起了，B 获取到的值还是最新的值，volatile 能保证 B 能获取到的 t 是最新的值，因为 A 的 t+1 并没有写到主内存里面去。

在实际的编程中，要注意，除非是在保证仅有一个线程处于写，而其他线程处于读的状态下的时候，才可以使用 volatile 来保证可见性，而不需要使用原子变量或者锁来保证原子性。

```java
// 原子操作
public static AtomicInteger count = new AtomicInteger();

// 线程协作处理
public static CountDownLatch latch= new CountDownLatch(1000);

// volatile 只能保证可见性，不能保证原子性
public static volatile int countNum = 0;

// 同步处理计算
public static int synNum = 0;

public static void inc() {
    Thread.sleep(1);

    countNum++;
    int c = count.addAndGet(1);
    add();
}

public static synchronized void add(){
    synNum++;
}

public static void main(String[] args) {
    // 同时启动1000个线程，去进行i++计算，看看实际结果
    for (int i = 0; i < 1000; i++) {
        new Thread(()=>{
            Counter.inc();
            latch.countDown();
        }), "thread" + i).start();
    }

    latch.await();
```

## volatile 实现原理

将带有 volatile 变量操作的 Java 代码转换成汇编代码后，可以看到多了个 lock 前缀指令；这个 lock 指令是关键，在多核处理器下实现两个重要操作:

- 将当前处理器缓存行的数据写回到系统内存。
- 这个写回内存的操作会使其他处理器里缓存该内存地址的数据失效

CPU 为了提高处理速度，不和内存直接进行交互，而是使用 Cache 高速缓存，通过缓存数据交互速度和内存不是一个数量级，而同时 Cache 的存储容量也很小。从内存将数据读到缓存后，CPU 进行一系列数据操作，而操作完成时间是不可知的。而 JVM 对带有 volatile 变量进行写操作时，会发送 Lock 前缀指令，将数据从缓存行写入到内存。写入内存还不够，因为其他线程的缓存行中数据还是旧的，Lock 指令可以让其他 CPU 通过监听在总线上的数据，检查自己的缓存数据是否过期，如果缓存行的地址和总线上的地址相同，则将缓存行失效，下次该线程对这个数据操作时，会重新从内存中读取，更新到缓存行。
