package wx.toolkits.sysproc.concurrence.sync.lock;

/**
 * Created by apple on 16/5/24.
 */

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 分段锁，系统提供一定数量的原始锁，根据传入对象的哈希值获取对应的锁并加锁
 * 注意：要锁的对象的哈希值如果发生改变，有可能导致锁无法成功释放!!!
 */
public class SegmentLock<T> {
    private Integer segments = 16;//默认分段数量
    private final HashMap<Integer, ReentrantLock> lockMap = new HashMap<>();

    public SegmentLock() {
        init(null, false);
    }

    public SegmentLock(Integer counts, boolean fair) {
        init(counts, fair);
    }

    private void init(Integer counts, boolean fair) {
        if (counts != null) {
            segments = counts;
        }
        for (int i = 0; i < segments; i++) {
            lockMap.put(i, new ReentrantLock(fair));
        }
    }

    public void lock(T key) {


        ReentrantLock lock = lockMap.get((key.hashCode() >>> 1) % segments);
        lock.lock();
    }

    public void unlock(T key) {
        ReentrantLock lock = lockMap.get((key.hashCode() >>> 1) % segments);
        lock.unlock();
    }

    public static void main(String args[]) {

        SegmentLock<String> stringSegmentLock = new SegmentLock<>();

        stringSegmentLock.lock("key");

        stringSegmentLock.lock("key");

        System.out.println("Shut Down");

    }
}