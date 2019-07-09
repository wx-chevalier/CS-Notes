# JVM 垃圾回收算法

与标记对象的传统算法相比，ZGC 在指针上做标记，在访问指针时加入 Load Barrier（读屏障），比如当对象正被 GC 移动，指针上的颜色就会不对，这个屏障就会先把指针更新为有效地址再返回，也就是，永远只有单个对象读取时有概率被减速，而不存在为了保持应用与 GC 一致而粗暴整体的 Stop The World。

# 链接

- https://mp.weixin.qq.com/s/tfyHwbsNCTjvMGTrfQ0qwQ
- https://toutiao.io/k/vdlyqj
- https://mp.weixin.qq.com/s/l42_A6odHwht_UyEbILDzQ?from=groupmessage&isappinstalled=0
- https://mp.weixin.qq.com/s/m02f-omq5TovMxy1gzETuA
