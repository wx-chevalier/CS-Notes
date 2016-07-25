package wx.toolkits.sysproc.concurrence.sync.atomic.count;

import java.util.concurrent.CountDownLatch;

/**
 * Created by apple on 16/5/18.
 */

/**
 * @function 演示计数错误的工作
 */
public class BrokenJob implements Runnable {

    private CountDownLatch countDown;

    private Count count;

    public BrokenJob(Count count, CountDownLatch countDown) {

        this.count = count;

        this.countDown = countDown;

    }

    @Override

    public void run() {

        //使用计数器的Count进行加一操作
        count.count++;

        //使用CountDownLatch进行精确控制
        countDown.countDown();

    }
}
