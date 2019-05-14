# Exchanger

```
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ExchangeTest {
public static void main(String[] args) {
ExecutorService service =Executors.newCachedThreadPool();
final Exchanger exchanger = new Exchanger();
service.execute(new Runnable() {
@Override
public void run() {
try{
String data1 = "零食";
System.out.println("线程"+Thread.currentThread().getName()+
"正在把数据 "+data1+" 换出去");
Thread.sleep((long)Math.random()*10000);
String data2 = (String)exchanger.exchange(data1);
System.out.println("线程 "+Thread.currentThread().getName()+
"换回的数据为 "+data2);
}catch(Exception e){
e.printStackTrace();
}
}
});
service.execute(new Runnable() {
@Override
public void run() {
try{
String data1 = "钱";
System.out.println("线程"+Thread.currentThread().getName()+
"正在把数据 "+data1+" 交换出去");
Thread.sleep((long)(Math.random()*10000));
String data2 =(String)exchanger.exchange(data1);
System.out.println("线程 "+Thread.currentThread().getName()+
"交换回来的数据是: "+data2);
}catch(Exception e){
e.printStackTrace();
}
}
});
}
}
```

最后结果为：

```
线程pool-1-thread-1正在把数据 零食 换出去
线程pool-1-thread-2正在把数据 钱 交换出去
线程 pool-1-thread-1换回的数据为 钱
线程 pool-1-thread-2交换回来的数据是: 零食
```

# Asynchronous(异步)

### Timeouts

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

运行上面的代码将会产生一个 TimeoutException:

```java
Exception in thread "main" java.util.concurrent.TimeoutException
    at java.util.concurrent.FutureTask.get(FutureTask.java:205)
```