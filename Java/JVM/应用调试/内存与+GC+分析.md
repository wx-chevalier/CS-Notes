尽管JVM提供了自动内存管理的机制，试图降低程序员的开发门槛，确实也实现了这一目标，在日常开发中，我们一般都不需要关心对象的内存释放。JVM大部分都是使用trace算法来判断一个对象是否该被回收，那么JVM只能回收那些从gc roots不可达的对象。 

如果我们在使用某些大的对象、集合对象或者一些三方包里的资源，忘记及时释放资源的话，还是会造成JVM的内存泄漏或内存浪费的问题。

通过jmap命令生成dump文件 
命令格式：jmap -dump:live,format=b,file=heap.bin <pid> 
注意：如果要保留heapdump中的不可达对象，则需要把”:live“去掉，即使用命令”jmap -dump,format=b,file=heap.bin <pid>“ 
通过设置JVM参数自动生成 使用-XX:+HeapDumpOnOutOfMemoryError这个JVM参数，在Java进程运行过程中发生OOM的时候就会生成一个heapdump文件，并写入到指定目录，一般用-XX:HeapDumpPath=${HOME}/logs/test来设置。 



# MAT

MAT的官网在：https://www.eclipse.org/mat/，可以看下它的介绍——MAT是一款高性能、具备丰富功能的Java堆内存分析工具，可以用来排查内存泄漏和内存浪费的问题。 




