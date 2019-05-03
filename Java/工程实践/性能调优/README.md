```conf
-server       --启用能够执行优化的编译器，显著提高服务器的性能
-Xmx4000M     --堆最大值
-Xms4000M     --堆初始大小
-Xmn600M      --年轻代大小
-XX:PermSize=200M         --持久代初始大小
-XX:MaxPermSize=200M      --持久代最大值
-Xss256K                  --每个线程的栈大小
-XX:+DisableExplicitGC    --关闭System.gc()
-XX:SurvivorRatio=1       --年轻代中Eden区与两个Survivor区的比值
-XX:+UseConcMarkSweepGC   --使用CMS内存收集
-XX:+UseParNewGC          --设置年轻代为并行收集
-XX:+CMSParallelRemarkEnabled        --降低标记停顿
-XX:+UseCMSCompactAtFullCollection   --在FULL GC的时候，对年老代进行压缩，可能会影响性能，但是可以消除碎片
-XX:CMSFullGCsBeforeCompaction=0     --此值设置运行多少次GC以后对内存空间进行压缩、整理
-XX:+CMSClassUnloadingEnabled        --回收动态生成的代理类 SEE：http://stackoverflow.com/questions/3334911/what-does-jvm-flag-cmsclassunloadingenabled-actually-do
-XX:LargePageSizeInBytes=128M        --内存页的大小不可设置过大， 会影响Perm的大小
-XX:+UseFastAccessorMethods          --原始类型的快速优化
-XX:+UseCMSInitiatingOccupancyOnly   --使用手动定义初始化定义开始CMS收集，禁止hostspot自行触发CMS GC
-XX:CMSInitiatingOccupancyFraction=80  --使用cms作为垃圾回收，使用80％后开始CMS收集
-XX:SoftRefLRUPolicyMSPerMB=0          --每兆堆空闲空间中SoftReference的存活时间
-XX:+PrintGCDetails                    --输出GC日志详情信息
-XX:+PrintGCApplicationStoppedTime     --输出垃圾回收期间程序暂停的时间
-Xloggc:$WEB_APP_HOME/.tomcat/logs/gc.log  --把相关日志信息记录到文件以便分析.
-XX:+HeapDumpOnOutOfMemoryError            --发生内存溢出时生成heapdump文件
-XX:HeapDumpPath=$WEB_APP_HOME/.tomcat/logs/heapdump.hprof  --heapdump文件地址
```

## 内存溢出 OOM

就是你要求分配的 java 虚拟机内存超出了系统能给你的，系统不能满足需求，于是产生溢出。

### 避免 OOM

#### 利用引用类别避免 OOM

前面讲了关于软引用和弱引用相关的基础知识，那么到底如何利用它们来优化程序性能，从而避免 OOM 的问题呢？

下面举个例子，假如有一个应用需要读取大量的本地图片，如果每次读取图片都从硬盘读取，则会严重影响性能，但是如果全部加载到内存当中，又有可能造成内存溢出，此时使用软引用可以解决这个问题。

设计思路是：用一个 HashMap 来保存图片的路径 和 相应图片对象关联的软引用之间的映射关系，在内存不足时，JVM 会自动回收这些缓存图片对象所占用的空间，从而有效地避免了 OOM 的问题。在 Android 开发中对于大量图片下载会经常用到。

```java
// .....
private Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

public void addBitmapToCache(String path) {

        // 强引用的Bitmap对象

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        // 软引用的Bitmap对象

        SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(bitmap);

        // 添加该对象到Map中使其缓存

        imageCache.put(path, softBitmap);

    }

 public Bitmap getBitmapByPath(String path) {

        // 从缓存中取软引用的Bitmap对象

        SoftReference<Bitmap> softBitmap = imageCache.get(path);

        // 判断是否存在软引用

        if (softBitmap == null) {

            return null;

        }

        // 取出Bitmap对象，如果由于内存不足Bitmap被回收，将取得空

        Bitmap bitmap = softBitmap.get();

        return bitmap;

    }
```

## 内存泄漏

是指你向系统申请分配内存进行使用 (new)，可是使用完了以后却不归还 (delete)，结果你申请到的那块内存你自己也不能再访问 , 该块已分配出来的内存也无法再使用，随着服务器内存的不断消耗，而无法使用的内存越来越多，系统也不能再次将它分配给需要的程序，产生泄露。一直下去，程序也逐渐无内存使用，就会溢出。

# Todos
