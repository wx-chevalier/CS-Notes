# Go 并发编程

Go 语言正是在多核和网络化的时代背景下诞生的原生支持并发的编程语言。Go 语言并发体系的理论是 C.A.R Hoare 在 1978 年提出的 CSP（Communicating Sequential Process，通讯顺序进程）。CSP 有着精确的数学模型，并实际应用在了 Hoare 参与设计的 T9000 通用计算机上。从 NewSqueak、Alef、Limbo 到现在的 Go 语言，对于对 CSP 有着 20 多年实战经验的 Rob Pike 来说，他更关注的是将 CSP 应用在通用编程语言上产生的潜力。

作为 Go 并发编程核心的 CSP 理论的核心概念只有一个：同步通信。在并发编程中，对共享资源的正确访问需要精确的控制，在目前的绝大多数语言中，都是通过加锁等线程同步方案来解决这一困难问题，而 Go 语言却另辟蹊径，它将共享的值通过 Channel 传递(实际上多个独立执行的线程很少主动共享资源)。在任意给定的时刻，最好只有一个 Goroutine 能够拥有该资源。数据竞争从设计层面上就被杜绝了。为了提倡这种思考方式，Go 语言将其并发编程哲学化为一句口号：

> Do not communicate by sharing memory; instead, share memory by communicating.

Go 不推荐使用共享内存的方式传递数据，而推荐使用 Channel（或称“通道”）在多个 goroutine 之间传递数据，同时保证整个过程的并发安全性。不过，作为可选方法，Go 依然提供了一些传统的同步方法（比如互斥量、条件变量等）。

# 链接

- https://www.kancloud.cn/mutouzhang/go/598654
