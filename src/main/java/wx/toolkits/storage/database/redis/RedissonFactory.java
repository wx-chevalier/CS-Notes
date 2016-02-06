package wx.storage.database.redis;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.redisson.core.RBucket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by apple on 16/1/5.
 */
public class RedissonFactory {

    final String ipAddress = "115.28.12.137";

    final String port = "6379";

    final String password = "HOTeamConfig1314";

    final Config config;

    //将客户端改为单例模式
    private RedissonClient redissonClient = null;

    public RedissonFactory() throws IOException {

        // connects to single Redis server via Config
        config = new Config();

        //设置连接与连接池大小
        config.useSingleServer().setAddress(ipAddress + ":" + port).setPassword(password).setConnectionPoolSize(50).setRetryAttempts(60);
    }

    public synchronized RedissonClient getRedissonClientInstance() {

        if (this.redissonClient == null) {
            this.redissonClient = Redisson.create(config);
        }
        return redissonClient;

    }


    public static void main(String args[]) throws IOException {

        RedissonFactory redissonFactory = new RedissonFactory();

        final RedissonClient redissonClient = redissonFactory.getRedissonClientInstance();

        final AtomicInteger atomicInteger = new AtomicInteger(0);

        ArrayList<Thread> threads = new ArrayList<Thread>();

        //使用1000个并发线程去连接并且获取数据
        for (int i = 0; i < 1000; i++) {

            final int i_inner = i;

            Thread thread = new Thread(new Runnable() {
                public void run() {

                    RBucket<String> rBucket = null;
                    try {
                        rBucket = redissonClient.getBucket("aaa");

                        rBucket.set("1", 2, TimeUnit.SECONDS);

                        System.out.println(rBucket.get() + ":" + i_inner);


                    } catch (Exception e) {

                        e.printStackTrace();

                        atomicInteger.addAndGet(1);

                        System.out.println("Failed:" + i_inner);

                    }


                }
            });

            threads.add(thread);

            //开启线程
            thread.start();



        }

        //等待全部线程终结
        threads.stream().forEach(thread->{
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Final:" + atomicInteger.get());

    }
}
