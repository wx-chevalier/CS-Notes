package wx.toolkits.sysproc.concurrence.sync.utils;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by apple on 16/5/27.
 */
public class BlockingQueueShowCase {

    public static class Buffer {

        BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(3);

        Random random = new Random();

        /**
         * @throws InterruptedException
         * @function 生产者函数
         */
        public void produce() throws InterruptedException {
            buffer.put(random.nextInt());
        }

        /**
         * 消费者函数
         *
         * @return
         * @throws InterruptedException
         */
        public Integer consume() throws InterruptedException {
            return buffer.take();
        }
    }

    public static void main(String args[]) {

        Buffer bufferInstance = new Buffer();

        Runnable producer = () -> {
            while (true) {


                try {
                    System.out.println("生产一个数据");
                    bufferInstance.produce();
                    Thread.sleep(1000l);
                    System.out.println("生产完毕");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        Runnable consumer = () -> {

            while (true) {
                try {
                    System.out.println("消费一个数据");
                    bufferInstance.consume();
                    Thread.sleep(300l);
                    System.out.println("消费完毕");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(producer);

        executorService.submit(consumer);


    }
}
