package wx.toolkits.sysproc.concurrence.rxjava.operator.transforming;

import rx.Observable;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by apple on 16/5/1.
 */
public class ShiftObservable {

    public static String[] names = new String[]{"A", "B", "C", "D", "E", "F", "G", "E"};

    /**
     * @function 根据数目来进行合并
     */
    public static void bufferByCount() {

        Observable.from(names).buffer(3).subscribe(strings -> {
            System.out.println(Arrays.asList(strings));
        });

    }

    /**
     * @function 根据时间平移
     */
    public static void bufferByTime() {
        Observable
                .interval(100, TimeUnit.MILLISECONDS)
                .buffer(250, TimeUnit.MILLISECONDS)
                .subscribe(System.out::println);
    }


    /**
     * @function 根据时间以及数目平移
     */
    public static void bufferBuCountAndTime() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(10)
                .buffer(250, TimeUnit.MILLISECONDS, 2)
                .subscribe(System.out::println);
    }


    public static void main(String[] args) throws InterruptedException {

        System.out.println("演示根据数目平移");

        ShiftObservable.bufferByCount();

        Thread.sleep(1000l);

        System.out.println("演示根据时间平移");

        ShiftObservable.bufferByTime();

        Thread.sleep(2000l);

        System.out.println("演示根据数目与时间平移");

        ShiftObservable.bufferBuCountAndTime();

        Thread.sleep(2000l);

    }

}
