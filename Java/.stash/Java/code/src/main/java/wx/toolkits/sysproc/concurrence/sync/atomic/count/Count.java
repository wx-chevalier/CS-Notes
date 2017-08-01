package wx.toolkits.sysproc.concurrence.sync.atomic.count;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by apple on 16/5/18.
 */
public class Count {

    public int count = 0;

    public AtomicInteger atomicCount = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        //计数值
        final int countNumber = 9999999;

        //Java内建的计数控制器
        CountDownLatch countDown = new CountDownLatch(countNumber);

        Count count = new Count();

        //线程池
        ExecutorService ex = Executors.newFixedThreadPool(5);

        Long startTime = System.currentTimeMillis();

        //使用BrokenJob进行计数
        for (int i = 0; i < countNumber; i++) {
            ex.execute(new BrokenJob(count, countDown));
        }

        //等待精确地执行完毕
        countDown.await();

        System.out.println("BrokenJob 最终计数值:" + count.count + " 运行时间:" + (System.currentTimeMillis() - startTime));

        //重置
        count.count = 0;

        countDown = new CountDownLatch(countNumber);

        startTime = System.currentTimeMillis();

        for (int i = 0; i < countNumber; i++) {
            ex.execute(new LockedJob(count, countDown));
        }

        countDown.await();

        System.out.println("LockedJob 最终计数值:" + count.count + " 运行时间:" + (System.currentTimeMillis() - startTime));

        //重置
        count.count = 0;

        countDown = new CountDownLatch(countNumber);

        startTime = System.currentTimeMillis();

        for (int i = 0; i < countNumber; i++) {
            ex.execute(new AtomicJob(count, countDown));
        }

        countDown.await();

        System.out.println("AtomicJob 最终计数值:" + count.atomicCount.get() + " 运行时间:" + (System.currentTimeMillis() - startTime));


    }

}
