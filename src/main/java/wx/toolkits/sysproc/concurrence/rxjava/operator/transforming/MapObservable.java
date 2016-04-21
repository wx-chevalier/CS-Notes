package wx.toolkits.sysproc.concurrence.rxjava.operator.transforming;

import rx.Observable;

/**
 * Created by apple on 16/4/21.
 */
public class MapObservable {

    public static String[] names = new String[]{"A", "B", "C", "D"};

    public static void mapNameToId() {

        Observable.from(names).map(s -> {
            return s.hashCode();
        }).subscribe(integer -> {
            System.out.println("Person Id is :" + integer);
        })
        ;

    }

    public static void mapNameToEmails() {

        Observable.from(names).flatMap(s -> {
            return Observable.from(new String[]{ s + "@b.com" , s + "@a.com"});
        }).subscribe(s -> {
            System.out.println("Person Id is :" + s);
        });

    }

    public static void main(String args[]) {

        MapObservable.mapNameToId();

        MapObservable.mapNameToEmails();

    }

}
