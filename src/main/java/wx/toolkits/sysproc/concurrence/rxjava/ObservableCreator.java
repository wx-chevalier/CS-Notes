package wx.toolkits.sysproc.concurrence.rxjava;

import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ObservableCreator {

    public static void fromArrayList() {

        List<Integer> arrayList = Arrays.asList(new Integer[]{1, 2, 3, 4, 5});

        /*
         * Example using single-value lambdas (Func1)
         */
        Observable.from(arrayList)
                .filter((v) -> {
                    return v < 4;
                })
                .subscribe((value) -> {
                    System.out.println("Value: " + value);
                });
        /*
         * Example with 'reduce' that takes a lambda with 2 arguments (Func2)
         */
        Observable.from(arrayList)
                .reduce((seed, value) -> {
                    // sum all values from the sequence
                    return seed + value;
                })
                .map((v) -> {
                    return "DecoratedValue: " + v;
                })
                .subscribe((value) -> {
                    System.out.println(value);
                });

    }

    public static void fromFuture() {

        Callable<List<Integer>> callable = () -> {
            List<Integer> arrayList = Arrays.asList(new Integer[]{1, 2, 3, 4, 5});
            return arrayList;
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future future = executorService.submit(callable);

        Observable.from(future)
                .subscribe((value) -> {
                    System.out.println(value);
                });

    }


    public static void main(String args[]) {

        //演示从ArrayList创建
        ObservableCreator.fromArrayList();

        //演示從Callable創建
        ObservableCreator.fromFuture();


    }
}
