package wx.toolkits.jvm.clasz.loading;

/**
 * Created by apple on 16/8/1.
 */
public class Child extends Parent {

    static Child child = new Child();

    static {
        System.out.println("子类静态初始化");
    }

    {
        System.out.println("子类实例初始化");
    }

    Child() {
        System.out.println("子类构造函数");
    }

    public static void main(String args[]) {

        Child child = new Child();

    }
}
