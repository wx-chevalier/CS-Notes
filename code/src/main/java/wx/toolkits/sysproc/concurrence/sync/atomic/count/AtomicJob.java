package wx.toolkits.sysproc.concurrence.sync.atomic.count;

import java.util.concurrent.CountDownLatch;

/**
 * Created by apple on 16/5/18.
 */
public class AtomicJob implements Runnable {

    private CountDownLatch countDown;

    private Count count;

    public AtomicJob(Count count, CountDownLatch countDown) {

        this.count = count;

        this.countDown = countDown;

    }

    @Override
    public void run() {

        boolean isSuccess = false;

        while (!isSuccess) {

            int countValue = count.atomicCount.get();

            isSuccess = count.atomicCount.

                    compareAndSet(countValue, countValue + 1);

        }

        countDown.countDown();
    }
}
