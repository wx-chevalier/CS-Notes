# RMI

RMI:远程方法调用(Remote Method Invocation)。能够让在某个 java 虚拟机上的对象像调用本地对象一样调用另一个 java 虚拟机中的对象上的方法。
![](http://img.blog.csdn.net/20130813104359359?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYTE5ODgxMDI5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

RMI 远程调用步骤：

1，客户对象调用客户端辅助对象上的方法

2，客户端辅助对象打包调用信息(变量，方法名)，通过网络发送给服务端辅助对象

3，服务端辅助对象将客户端辅助对象发送来的信息解包，找出真正被调用的方法以及该方法所在对象

4，调用真正服务对象上的真正方法，并将结果返回给服务端辅助对象

5，服务端辅助对象将结果打包，发送给客户端辅助对象

6，客户端辅助对象将返回值解包，返回给客户对象

7，客户对象获得返回值

对于客户对象来说，步骤 2-6 是完全透明的

搭建一个 RMI 服务的过程分为以下 7 步;

1，创建远程方法接口，该接口必须继承自 Remote 接口

Remote 接口是一个标识接口，用于标识所包含的方法可以从非本地虚拟机上调用的接口，Remote 接口本身不包含任何方法

搭建一个 RMI 服务的过程分为以下 7 步;
1，创建远程方法接口，该接口必须继承自 Remote 接口
Remote 接口是一个标识接口，用于标识所包含的方法可以从非本地虚拟机上调用的接口，Remote 接口本身不包含任何方法

```
    package server;

    import java.rmi.Remote;
    import java.rmi.RemoteException;

    public interface Hello extends Remote {
        public String sayHello(String name) throws RemoteException;
    }
```

由于远程方法调用的本质依然是网络通信，只不过隐藏了底层实现，网络通信是经常会出现异常的，所以接口的所有方法都必须抛出 RemoteException 以说明该方法是有风险的
2，创建远程方法接口实现类：
UnicastRemoteObject 类的构造函数抛出了 RemoteException，故其继承类不能使用默认构造函数，继承类的构造函数必须也抛出 RemoteException
由于方法参数与返回值最终都将在网络上传输，故必须是可序列化的

```
    package server;

    import java.rmi.RemoteException;
    import java.rmi.server.UnicastRemoteObject;

    public class HelloImpl extends UnicastRemoteObject implements Hello {
        private static final long serialVersionUID = -271947229644133464L;

        public HelloImpl() throws RemoteException{
            super();
        }

        public String sayHello(String name) throws RemoteException {
            return "Hello,"+name;
        }
    }
```

3，利用 java 自带 rmic 工具生成 sutb 存根类(jdk1.5.0_15/bin/rmic)
jdk1.2 以后的 RMI 可以通过反射 API 可以直接将请求发送给真实类，所以不需要 skeleton 类了
sutb 存根为远程方法类在本地的代理，是在服务端代码的基础上生成的，需要 HelloImpl.class 文件，由于 HelloImpl 继承了 Hello 接口，故 Hello.class 文件也是不可少的
Test

- - server
- - - - Hello.class
- - - - HelloImpl.class
        方式一：

```
[name@name Test]$ cd /home/name/Test/
[name@name Test]$ rmic server.HelloImpl
```

方式二：

```
[name@name Test]$ rmic -classpath /home/name/Test server.HelloImpl
```

运行成功后将会生成 HelloImpl_Stub.class 文件
4，启动 RMI 注册服务(jdk1.5.0_15/bin/rmiregistry)
方式一：后台启动 rmiregistry 服务

```
    [name@name jdk]$ jdk1.5.0_15/bin/rmiregistry 12312 &
    [1] 22720
    [name@name jdk]$ ps -ef|grep rmiregistry
    name    22720 13763  0 16:43 pts/3    00:00:00 jdk1.5.0_15/bin/rmiregistry 12312
    name    22737 13763  0 16:43 pts/3    00:00:00 grep rmiregistry
```

如果不带具体端口号，则默认为 1099
方式二：人工创建 rmiregistry 服务，需要在代码中添加：

```
    LocateRegistry.createRegistry(12312);
```

5，编写服务端代码

```
    package server;

    import java.rmi.Naming;
    import java.rmi.registry.LocateRegistry;

    public class HelloServer {
        public static void main(String[] args) {
            try{
                Hello h = new HelloImpl();

                //创建并导出接受指定port请求的本地主机上的Registry实例。
                //LocateRegistry.createRegistry(12312);

                /** Naming 类提供在对象注册表中存储和获得远程对远程对象引用的方法
                 *  Naming 类的每个方法都可将某个名称作为其一个参数，
                 *  该名称是使用以下形式的 URL 格式(没有 scheme 组件)的 java.lang.String:
                 *  //host:port/name
                 *  host：注册表所在的主机(远程或本地)，省略则默认为本地主机
                 *  port：是注册表接受调用的端口号，省略则默认为1099，RMI注册表registry使用的著名端口
                 *  name：是未经注册表解释的简单字符串
                 */
                //Naming.bind("//host:port/name", h);
                Naming.bind("rmi://192.168.58.164:12312/Hello", h);
                System.out.println("HelloServer启动成功");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
```

先创建注册表，然后才能在注册表中存储远程对象信息
6，运行服务端(58.164)：
Test

- - server
- - - - Hello.class
- - - - HelloImpl.class
- - - - HelloServer.class

```
    [name@name ~]$ java server.HelloServer
    HelloServer启动成功
```

当然/home/name/Test 一定要在系统 CLASSPATH 中，否则会报找不到相应的.class 文件
7，编写客户端代码

```
    package client;

    import java.net.MalformedURLException;
    import java.rmi.Naming;
    import java.rmi.NotBoundException;
    import java.rmi.RemoteException;

    import server.Hello;

    public class HelloClient {
        public static void main(String[] args) {
            try {
                Hello h = (Hello)Naming.lookup("rmi://192.168.58.164:12312/Hello");
                System.out.println(h.sayHello("zx"));
            } catch (MalformedURLException e) {
                System.out.println("url格式异常");
            } catch (RemoteException e) {
                System.out.println("创建对象异常");
                e.printStackTrace();
            } catch (NotBoundException e) {
                System.out.println("对象未绑定");
            }
        }
    }
```

8，运行客户端(58.163)：
Test

- - client
- - - - HelloClient.class
- - server
- - - - Hello.class
- - - - HelloImpl_Stub.class//服务端生成的存根文件

```
    [name@name client]$ java client.HelloClient
    Hello,zx
```

![](http://img.blog.csdn.net/20130816094554390?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYTE5ODgxMDI5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)
