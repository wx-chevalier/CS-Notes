package wx.toolkits.jvm.clasz.loading;

/**
 * Created by apple on 16/8/1.
 */
class ComplexInitialization implements Cloneable {
    public static int k = 0;
    public static ComplexInitialization t1 = new ComplexInitialization("t1");
    public static ComplexInitialization t2 = new ComplexInitialization("t2");
    public static int i = print("i");
    public static int n = 99;

    public int j = print("j");

    {
        print("构造块");
    }

    static {
        print("静态块");
    }

    public ComplexInitialization(String str) {
        System.out.println((++k) + ":" + str + "    i=" + i + "  n=" + n);
        ++n;
        ++i;
    }

    public static int print(String str) {
        System.out.println((++k) + ":" + str + "   i=" + i + "   n=" + n);
        ++n;
        return ++i;
    }

    public static void main(String[] args) {
        ComplexInitialization t = new ComplexInitialization("init");
    }
}