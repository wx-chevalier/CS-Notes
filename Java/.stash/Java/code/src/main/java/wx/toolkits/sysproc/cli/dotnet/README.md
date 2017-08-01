[TOC]



# JNI4NET示例
注意，本包只能够在Windows Server上运行，运行条件请参阅jni4net官方Repo

`Greeting.java`

```
import java.io.File;
import net.sf.jni4net.Bridge;
import java.io.IOException;
import java.lang.String;
import testlib.Test;      // 引用C#命名空间testlib中的类Test

public Greeting {
    public static void main(String[] args) throws IOException {
        Bridge.setVerbose(true);
        Bridge.init();

        // 加载dll库
        Bridge.LoadAndRegisterAssemblyFrom(new File("testlib.j4n.dll"));

        Test test = new Test();
        test.Hello();
    }
}
```

`testlib.cs`

```
namespace testlib {
    public class Test {
        public void Hello() {
            System.Console.WriteLine("Hello from .NET");
        }
    }
}
```

编译生成*C#* dll库（`testlib.dll`）：

```
csc.exe /target:library /out:testlib.dll testlib.cs
```

下载`jni4net-<version>-bin.zip`解压之后文件夹内容如下（如版本为0.8.8.0）：

```
jni4net-0.8.8.0-bin
├── ReadMe.md
├── samples/...
├── bin
│   ├── proxygen-GPL.3.0.txt
│   ├── proxygen.exe
│   └── proxygen.exe.config
├── changes.txt
└── lib
    ├── jni4net-MIT.txt
    ├── jni4net.j-0.8.8.0.jar
    ├── jni4net.n-0.8.8.0.dll
    ├── jni4net.n.w32.v20-0.8.8.0.dll
    ├── jni4net.n.w32.v40-0.8.8.0.dll
    ├── jni4net.n.w64.v20-0.8.8.0.dll
    └── jni4net.n.w64.v40-0.8.8.0.dll
```

生成代理对象包：

```
proxygen.exe testlib.dll -wd .\
call build.cmd
```

到此为止该目录下有我们需要的三个文件：`testlib.dll`，`testlib.j4n.dll`，`testlib.j4n.jar`

下面编译`Greeting.java`，我们再work目录下编译，首先把我们生成的C#的库相关文件和`Greeting.java`文件拷贝到该目录下：

```
work
├── Greeting.java
├── testlib.dll
├── testlib.j4n.dll
├── testlib.j4n.jar
```

由于[https://stackoverflow.com/questions/24448597/jni4net-failed-to-load-dlls-in-java-app](https://stackoverflow.com/questions/24448597/jni4net-failed-to-load-dlls-in-java-app)里面相同的问题，我们需要讲`jni4net`包中`lib`目录下的所有文件拷贝到我们的编译目录下：

```
work
├── Greeting.java
├── testlib.dll
├── testlib.j4n.dll
├── testlib.j4n.jar
├
├── jni4net.j-0.8.8.0.jar
├── jni4net.n-0.8.8.0.dll
├── jni4net.n.w32.v20-0.8.8.0.dll
├── jni4net.n.w32.v40-0.8.8.0.dll
├── jni4net.n.w64.v20-0.8.8.0.dll
└── jni4net.n.w64.v40-0.8.8.0.dll
```

执行下面命令编译：

```
javac -cp jni4net.j-0.8.8.0.jar;testlib.j4n.jar Greeting.java
```

运行：

```
java -cp jni4net.j-0.8.8.0.jar;testlib.j4n.jar;. Greeting
```