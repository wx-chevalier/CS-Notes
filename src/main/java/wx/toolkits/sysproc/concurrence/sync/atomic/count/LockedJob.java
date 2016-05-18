package wx.toolkits.sysproc.concurrence.sync.atomic.count;

import java.util.concurrent.CountDownLatch;

/**
 * Created by apple on 16/5/18.
 */
public class LockedJob implements Runnable {

    private CountDownLatch countDown;

    private Count count;

    public LockedJob(Count count, CountDownLatch countDown) {

        this.count = count;

        this.countDown = countDown;

    }

    @Override
    public void run() {

        synchronized (this.count) {
            //使用计数器的Count进行加一操作
            count.count++;

            //使用CountDownLatch进行精确控制
            countDown.countDown();
        }

    }
}
