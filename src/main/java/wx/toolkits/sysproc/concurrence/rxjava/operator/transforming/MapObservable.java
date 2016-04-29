package wx.toolkits.sysproc.concurrence.rxjava.operator.transforming;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by apple on 16/4/21.
 */
public class MapObservable {

    public static String[] names = new String[]{"A", "B", "C", "D"};

    /**
     * @function 演示将姓名映射到ID, 即一一映射
     */
    public static void mapNameToIdWithMap() {

        Observable.from(names).map(s -> {
            return s.hashCode();
        }).subscribe(integer -> {
            System.out.println("Person Id is :" + integer);
        })
        ;

    }

    /**
     * @function 演示将姓名映射到邮箱, 即一对多映射
     */
    public static void mapNameToEmailsWithFlatMap() {

        Observable.from(names).flatMap(s -> {
            return Observable.from(new String[]{s + "@b.com", s + "@a.com"});
        }).subscribe(s -> {
            System.out.println("Person Id is :" + s);
        });

    }

    /**
     * @function 演示多层映射中出现BUG的情况
     */
    public static void flatMapWithException() {
        Observable.from(names).
                flatMap(s -> {
                    return Observable.<String>create(subscriber -> {
                        subscriber.onError(new Exception("Custom Exceptions"));
                    });
                })
                .flatMap(s -> {
                    System.out.println("In FlatMap 3");
                    return Observable.from(new String[]{s + "@b.com", s + "@a.com"});
                })
                .subscribe(s -> {
                            System.out.println("Person Id is :" + s);
                        },
                        throwable -> {
                            System.out.println(throwable.getMessage());
                        });
    }

    /**
     * @function 演示在不同线程中的flatMap
     */
    public static void flatMapWithMultipleThread() {
        Observable.from(new String[]{"name"}).
                flatMap(s -> {
                    return Observable.<String>create(subscriber -> {
                        System.out.println("FlatMap 1:" + Thread.currentThread().getName());
                        subscriber.onNext(s);
                    });
                })
                .flatMap(s -> {
                    return Observable.<String>create(subscriber -> {
                        try {
                            System.out.println("FlatMap 2:" + Thread.currentThread().getName());
                            Thread.sleep(1000l);
                            subscriber.onNext(s);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                })
                .subscribe(s -> {
                            System.out.println("Subscriber:" + Thread.currentThread().getName());
                        },
                        throwable -> {
                            System.out.println(throwable.getMessage());
                        });
    }

    public static void main(String args[]) throws InterruptedException {

        MapObservable.mapNameToIdWithMap();

        MapObservable.mapNameToEmailsWithFlatMap();

        MapObservable.flatMapWithException();

        MapObservable.flatMapWithMultipleThread();

        System.out.println("Before Stop");

        //睡眠一段时间以等待所有的输出
        Thread.sleep(5000l);

    }

}
