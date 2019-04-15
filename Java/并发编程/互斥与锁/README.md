# 互斥与锁

# synchronized

synchronized 也是经常用到的，它给人的印象一般是"重量级锁"。在 JDK1.6 后，对 synchronized 进行了一系列优化，引入了偏向锁和轻量级锁，对锁的存储结构和升级过程，有效减少获得锁和释放锁带来的性能消耗。

- 普通同步方法，锁是当前实例对象。`public synchronized void test(){...}`

- 静态同步方法，锁是当前类的 Class 对象。`public static synchronized void test(...){}`

- 对于同步方法块，锁是 Synchronized 括号中里配置的对象。`synchronized(instance){...}`

用 javap 反编译 class 文件，可以看到 synchronized 用的是 monitorenter 和 monitorexit 实现加锁。一个 monitorenter 必须要有 monitorexit 与之对应，所以同步方法会在异常处和方法返回处加入 monitorexit 指令。

```s
3: monitorenter  //注意此处，进入同步方法
4: aload_0
5: dup
6: getfield      #2             // Field i:I
9: iconst_1
10: iadd
11: putfield      #2            // Field i:I
14: aload_1
15: monitorexit   //注意此处，退出同步方法
```
