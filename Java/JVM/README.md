
# JVM 内部原理与性能调优

`Copyright © 2017 王下邀月熊`

![](https://camo.githubusercontent.com/322fefce6b2264d9ff2ad35ea5dcd4622e437b04/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4c6963656e73652d434325323042592d2d4e432d2d5341253230342e302d626c75652e737667)
![](https://camo.githubusercontent.com/d4e0f63e9613ee474a7dfdc23c240b9795712c96/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f5052732d77656c636f6d652d627269676874677265656e2e737667)


本系列文章包括[现代 Java 开发：语法基础与工程实践](https://parg.co/bgk)、[Java 设计模式与应用架构](https://parg.co/bgJ)、[深入浅出 Java 并发编程](https://parg.co/b7l)、[JVM 内部原理与性能调优](https://parg.co/bgL)、[Spring Boot 5 与 Spring Cloud 微服务实践](https://parg.co/b7Y)。

# 前言

## 版权

![](https://parg.co/bDY) ![](https://parg.co/bDm)

笔者所有文章遵循 [知识共享 署名-非商业性使用-禁止演绎 4.0 国际许可协议](https://creativecommons.org/licenses/by-nc-nd/4.0/deed.zh)，欢迎转载，尊重版权。如果觉得本系列对你有所帮助，欢迎给我家布丁买点狗粮(支付宝扫码)~

![](https://github.com/wxyyxc1992/OSS/blob/master/2017/8/1/Buding.jpg?raw=true)


# 目录

- 垃圾回收
 
- [垃圾回收算法与 JVM 垃圾收集器综述]()


- 内存模型


- 类文件


- JVM 应用调试与分析


- 性能调优
