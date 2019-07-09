# 类加载

JVM 提供了 3 种类加载器：BootstrapClassLoader, ExtClassLoader, AppClassLoader 分别加载 Java 核心类库、扩展类库以及应用的类路径( CLASSPATH)下的类库。JVM 通过双亲委派模型进行类的加载，我们也可以通过继承 java.lang.classloader 实现自己的类加载器。何为双亲委派模型？当一个类加载器收到类加载任务时，会先交给自己的父加载器去完成，因此最终加载任务都会传递到最顶层的 BootstrapClassLoader，只有当父加载器无法完成加载任务时，才会尝试自己来加载。

采用双亲委派模型的一个好处是保证使用不同类加载器最终得到的都是同一个对象，这样就可以保证 Java 核心库的类型安全，比如，加载位于 rt.jar 包中的 java.lang.Object 类，不管是哪个加载器加载这个类，最终都是委托给顶层的 BootstrapClassLoader 来加载的，这样就可以保证任何的类加载器最终得到的都是同样一个 Object 对象。

```java
protected Class<?> loadClass(String name, boolean resolve) {
    synchronized (getClassLoadingLock(name)) {
        // 首先，检查该类是否已经被加载，如果从JVM缓存中找到该类，则直接返回
        Class<?> c = findLoadedClass(name);

        if (c == null) {
        try {
            // 遵循双亲委派的模型，首先会通过递归从父加载器开始找，
            // 直到父类加载器是BootstrapClassLoader为止
            if (parent != null) {
            c = parent.loadClass(name, false);
            } else {
                c = findBootstrapClassOrNull(name);
            }
        } catch (ClassNotFoundException e) {}
            if (c == null) {
            // 如果还找不到，尝试通过findClass方法去寻找
            // findClass是留给开发者自己实现的，也就是说
            // 自定义类加载器时，重写此方法即可
            c = findClass(name);
            }
        }
        if (resolve) {
            resolveClass(c);
        }
    return c;
    }
}
```

但双亲委派模型并不能解决所有的类加载器问题，比如，Java 提供了很多服务提供者接口( ServiceProviderInterface，SPI)，允许第三方为这些接口提供实现。常见的 SPI 有 JDBC、JNDI、JAXP 等，这些 SPI 的接口由核心类库提供，却由第三方实现，这样就存在一个问题：SPI 的接口是 Java 核心库的一部分，是由 BootstrapClassLoader 加载的；SPI 实现的 Java 类一般是由 AppClassLoader 来加载的。BootstrapClassLoader 是无法找到 SPI 的实现类的，因为它只加载 Java 的核心库。它也不能代理给 AppClassLoader，因为它是最顶层的类加载器。也就是说，双亲委派模型并不能解决这个问题。

线程上下文类加载器( ContextClassLoader)正好解决了这个问题。从名称上看，可能会误解为它是一种新的类加载器，实际上，它仅仅是 Thread 类的一个变量而已，可以通过 setContextClassLoader(ClassLoadercl)和 getContextClassLoader()来设置和获取该对象。如果不做任何的设置，Java 应用的线程的上下文类加载器默认就是 AppClassLoader。在核心类库使用 SPI 接口时，传递的类加载器使用线程上下文类加载器，就可以成功的加载到 SPI 实现的类。线程上下文类加载器在很多 SPI 的实现中都会用到。

# 链接

- https://time.geekbang.org/column/article/11523
