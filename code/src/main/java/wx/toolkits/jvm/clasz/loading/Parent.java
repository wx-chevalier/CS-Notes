package wx.toolkits.jvm.clasz.loading;

/**
 * Created by apple on 16/8/1.
 */
public class Parent extends GrandPa {

    static String language = "Chinese";

    static {
        System.out.println("父类静态初始化");
    }

    {
        System.out.println("父类实例初始化");
    }

    Parent() {
        System.out.println("父类构造函数");
    }
}
