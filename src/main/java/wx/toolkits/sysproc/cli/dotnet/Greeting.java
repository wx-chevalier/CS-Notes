import java.io.File;
import net.sf.jni4net.Bridge;
import java.io.IOException;
import java.lang.String;
import testlib.Test;

public class Greeting {
	public static void main(String[] args) throws IOException {
		try {
			Bridge.setVerbose(true);
			Bridge.init();


			Bridge.LoadAndRegisterAssemblyFrom(new File("testlib.j4n.dll"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Test test = new Test();
		// test.Hello();
	}
}