package wx;

/**
 * Hello world!
 *
 */
public class App
{
    private String appName = "appName";

    public void set(){
        InnerClass innerClass = new InnerClass();
        innerClass.print();
    }

    public class InnerClass{

        public void print(){
            System.out.print(appName);
        }
    }

    public static void main( String[] args )
    {

        App app = new App();
        app.set();

    }
}
