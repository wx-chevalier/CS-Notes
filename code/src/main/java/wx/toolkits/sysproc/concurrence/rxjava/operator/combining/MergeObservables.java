package wx.toolkits.sysproc.concurrence.rxjava.operator.combining;

/**
 * Created by apple on 16/4/20.
 */

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @function 本例演示如何合并Observables
 */
public class MergeObservables {

    /**
     * @function 演示阻塞型的Observable
     */
    public static void mergeBlockObservables() {

        Observable<Integer> observable_1 = Observable.from(new Integer[]{1, 2});

        Observable<Integer> observable_2 = Observable.from(new Integer[]{2, 3});

        Observable<Integer> observable_combined = Observable.merge(observable_1, observable_2);

        observable_combined.subscribe(
                (value) -> {

                    System.out.println(Thread.currentThread().getName() + " Emited!");
                    System.out.println(value);
                }
        );
    }

    /**
     * @function 合并异步Observables
     */
    public static void mergeObervables() {

        Observable<Object> observable_1 = Observable.create(subscriber -> {
            try {
                Thread.sleep(1000l);

                subscriber.onNext(1);

                Thread.sleep(3000l);

                subscriber.onNext(2);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).subscribeOn(Schedulers.newThread());

        Observable<Object> observable_2 = Observable.create(subscriber -> {
            try {

                Thread.sleep(2000l);

                subscriber.onNext(3);

                Thread.sleep(4000l);

                subscriber.onNext(4);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })
                .subscribeOn(Schedulers.newThread());

        //observable_3是运行在主线程中的一个同步的被观察者,可以看出它会自主运行而不受子线程影响
        Observable<Object> observable_3 = Observable.create(subscriber -> {

            subscriber.onNext(5);

            subscriber.onNext(6);

        });


        Observable<Object> observable_combined = Observable.merge(observable_1, observable_2, observable_3);

        observable_combined.subscribe(
                (value) -> {

                    System.out.println(Thread.currentThread().getName() + " Emited!");

                    System.out.println(value);
                }
        );

    }

    public static void main(String args[]) throws InterruptedException {

        System.out.println("演示阻塞型Observables");

        MergeObservables.mergeBlockObservables();

        System.out.println("演示异步Observables");

        MergeObservables.mergeObervables();

        Thread.sleep(50000l);

    }

}
