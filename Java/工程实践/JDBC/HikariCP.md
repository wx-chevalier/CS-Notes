# HikariCP

HikariCP 是一个高性能的 JDBC 连接池，基于 BoneCP 做了不少的改进和优化。JDBC 连接池的实现并不复杂，主要是对 JDBC 中几个核心对象 Connection、Statement、PreparedStatement、CallableStatement 以及 ResultSet 的封装与动态代理。

目前主流的两个数据库连接池就是 Druid 与 HikariCP，HikariCP 在性能方面是要优于 Druid，但是 Druid 在监控以及可扩展性上是较为完善的。

# Usage | 配置与使用

# 性能之道

## 优化并精简字节码

HikariCP 利用了一个第三方的 Java 字节码修改类库 Javassist 来生成委托实现动态代理。动态代理的实现在 ProxyFactory 类，源码如下：

```java
static ProxyConnection getProxyConnection(final PoolEntry poolEntry, final Connection connection, final FastList<Statement> openStatements, final ProxyLeakTask leakTask, final long now, final boolean isReadOnly, final boolean isAutoCommit)
{
    // Body is replaced (injected) by JavassistProxyFactory
    throw new IllegalStateException("You need to run the CLI build and you need target/classes in your classpath to run.");
}
```

发现这些代理方法中只有一行直接抛异常的代码，注释写着“Body is replaced (injected) by JavassistProxyFactory”，其实方法 body 中的代码是在编译时调用 JavassistProxyFactory 才生成的，主要代码如下：

```java
public static void main(String... args) throws Exception {
    classPool = new ClassPool();
    classPool.importPackage("java.sql");
    classPool.appendClassPath(new LoaderClassPath(JavassistProxyFactory.class.getClassLoader()));

    if (args.length > 0) {
        genDirectory = args[0];
    }

    // Cast is not needed for these
    String methodBody = "{ try { return delegate.method($$); } catch (SQLException e) { throw checkException(e); } }";
    generateProxyClass(Connection.class, ProxyConnection.class.getName(), methodBody);
    generateProxyClass(Statement.class, ProxyStatement.class.getName(), methodBody);
    generateProxyClass(ResultSet.class, ProxyResultSet.class.getName(), methodBody);

    // For these we have to cast the delegate
    methodBody = "{ try { return ((cast) delegate).method($$); } catch (SQLException e) { throw checkException(e); } }";
    generateProxyClass(PreparedStatement.class, ProxyPreparedStatement.class.getName(), methodBody);
    generateProxyClass(CallableStatement.class, ProxyCallableStatement.class.getName(), methodBody);

    modifyProxyFactory();
}

private static void modifyProxyFactory() throws NotFoundException, CannotCompileException, IOException {
    System.out.println("Generating method bodies for com.zaxxer.hikari.proxy.ProxyFactory");

    String packageName = ProxyConnection.class.getPackage().getName();
    CtClass proxyCt = classPool.getCtClass("com.zaxxer.hikari.pool.ProxyFactory");
    for (CtMethod method : proxyCt.getMethods()) {
        switch (method.getName()) {
        case "getProxyConnection":
        method.setBody("{return new " + packageName + ".HikariProxyConnection($$);}");
        break;
        case "getProxyStatement":
        method.setBody("{return new " + packageName + ".HikariProxyStatement($$);}");
        break;
        case "getProxyPreparedStatement":
        method.setBody("{return new " + packageName + ".HikariProxyPreparedStatement($$);}");
        break;
        case "getProxyCallableStatement":
        method.setBody("{return new " + packageName + ".HikariProxyCallableStatement($$);}");
        break;
        case "getProxyResultSet":
        method.setBody("{return new " + packageName + ".HikariProxyResultSet($$);}");
        break;
        default:
        // unhandled method
        break;
        }
    }

    proxyCt.writeFile(genDirectory + "target/classes");
}
```

之所以使用 Javassist 生成动态代理，是因为其速度更快，相比于 JDK Proxy 生成的字节码更少，精简了很多不必要的字节码。

## ConcurrentBag

ConcurrentBag 的实现借鉴于 C#中的同名类，是一个专门为连接池设计的 lock-less 集合，实现了比 LinkedBlockingQueue、LinkedTransferQueue 更好的并发性能。ConcurrentBag 内部同时使用了 ThreadLocal 和 CopyOnWriteArrayList 来存储元素，其中 CopyOnWriteArrayList 是线程共享的。

ConcurrentBag 采用了 queue-stealing 的机制获取元素：首先尝试从 ThreadLocal 中获取属于当前线程的元素来避免锁竞争，如果没有可用元素则再次从共享的 CopyOnWriteArrayList 中获取，进而减少伪共享的发生。

## FastList

FastList 是一个 List 接口的精简实现，只实现了接口中必要的几个方法。JDK ArrayList 每次调用 get()方法时都会进行 rangeCheck 检查索引是否越界，FastList 的实现中去除了这一检查，只要保证索引合法那么 rangeCheck 就成为了不必要的计算开销。

此外，HikariCP 使用 List 来保存打开的 Statement，当 Statement 关闭或 Connection 关闭时需要将对应的 Statement 从 List 中移除。通常情况下，同一个 Connection 创建了多个 Statement 时，后打开的 Statement 会先关闭。ArrayList 的 remove(Object)方法是从头开始遍历数组，而 FastList 是从数组的尾部开始遍历，因此更为高效。
