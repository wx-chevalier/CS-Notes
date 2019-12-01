# Goroutine

Goroutine 是 Go 语言特有的并发体，是一种轻量级的线程，由 go 关键字启动。在真实的 Go 语言的实现中，goroutine 和系统线程也不是等价的。尽管两者的区别实际上只是一个量的区别，但正是这个量变引发了 Go 语言并发编程质的飞跃。协程的设计隐藏了线程创建和管理的诸多复杂性，在多线程操作系统上可实现多路复用，因此若一个线程阻塞，比如说等待 IO，那么其它的线程就会运行。不过即便协程相对轻量，但是也并不意味着可以无限制地开协程。一个很典型的例子，比如在 CPU 密集型的计算中，开出超过 CPU 核心（线程）数的协程并不能加快计算速度，可能反而会适得其反。

go 语句会在当前 Goroutine 对应函数返回前创建新的 Goroutine. 例如：

```go
var a string

func f() {
	print(a)
}

func hello() {
	a = "hello, world"
	go f()
}
```

执行 go f()语句创建 Goroutine 和 hello 函数是在同一个 Goroutine 中执行, 根据语句的书写顺序可以确定 Goroutine 的创建发生在 hello 函数返回之前, 但是新创建 Goroutine 对应的 f()的执行事件和 hello 函数返回的事件则是不可排序的，也就是并发的。调用 hello 可能会在将来的某一时刻打印"hello, world"，也很可能是在 hello 函数执行完成后才打印。
