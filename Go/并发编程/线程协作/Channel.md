# 信道

Channel 通信是在 Goroutine 之间进行同步的主要方法；信道与映射一样，也需要通过 make 来分配内存。其结果值充当了对底层数据结构的引用。若提供了一个可选的整数形参，它就会为该信道设置缓冲区大小。默认值是零，表示不带缓冲的或同步的信道。

```go
i := make(chan int)            // 整数类型的无缓冲信道
cj := make(chan int, 0)         // 整数类型的无缓冲信道
cs := make(chan *os.File, 100)  // 指向文件指针的带缓冲信道
```

无缓冲信道在通信时会同步交换数据，它能确保（两个协程的）计算处于确定状态。接收者在收到数据前会一直阻塞。若信道是不带缓冲的，那么在接收者收到值前，发送者会一直阻塞；若信道是带缓冲的，则发送者仅在值被复制到缓冲区前阻塞；若缓冲区已满，发送者会一直等待直到某个接收者取出一个值为止。若在关闭 Channel 后继续从中接收数据，接收者就会收到该 Channel 返回的零值。

# 无缓冲信道

在无缓存的 Channel 上的每一次发送操作都有与其对应的接收操作相配对，发送和接收操作通常发生在不同的 Goroutine 上（在同一个 Goroutine 上执行 2 个操作很容易导致死锁）。无缓存的 Channel 上的发送操作总在对应的接收操作完成前发生。

```go
var done = make(chan bool)
var msg string

func aGoroutine() {
	msg = "你好, 世界"
	done <- true
}

func main() {
	go aGoroutine()
	<-done
	println(msg)
}
```

若在关闭 Channel 后继续从中接收数据，接收者就会收到该 Channel 返回的零值。因此在这个例子中，用 close(c) 关闭管道代替 `done <- false` 依然能保证该程序产生相同的行为。

```go
var done = make(chan bool)
var msg string

func aGoroutine() {
	msg = "你好, 世界"
	close(done)
}

func main() {
	go aGoroutine()
	<-done
	println(msg)
}
```

对于从无缓冲 Channel 进行的接收，发生在对该 Channel 进行的发送完成之前。

```go
var done = make(chan bool)
var msg string

func aGoroutine() {
	msg = "hello, world"
	<-done
}
func main() {
	go aGoroutine()
	done <- true
	println(msg)
}
```

# 缓冲信道

带缓冲的信道可被用作信号量，例如限制吞吐量。在此例中，进入的请求会被传递给 handle，它从信道中接收值，处理请求后将值发回该信道中，以便让该 “信号量”准备迎接下一次请求。信道缓冲区的容量决定了同时调用 process 的数量上限，因此我们在初始化时首先要填充至它的容量上限。

```go
var sem = make(chan int, MaxOutstanding)

func handle(r *Request) {
	sem <- 1 // 等待活动队列清空。
	process(r)  // 可能需要很长时间。
	<-sem    // 完成；使下一个请求可以运行。
}

func Serve(queue chan *Request) {
	for {
		req := <-queue
		go handle(req)  // 无需等待 handle 结束。
	}
}
```

对于带缓冲的 Channel，对于 Channel 的第 K 个接收完成操作发生在第 K+C 个发送操作完成之前，其中 C 是 Channel 的缓存大小。 如果将 C 设置为 0 自然就对应无缓存的 Channel，也即使第 K 个接收完成在第 K 个发送完成之前。因为无缓存的 Channel 只能同步发 1 个，也就简化为前面无缓存 Channel 的规则：对于从无缓冲 Channel 进行的接收，发生在对该 Channel 进行的发送完成之前。我们可以根据控制 Channel 的缓存大小来控制并发执行的 Goroutine 的最大数目, 例如：

```go
var limit = make(chan int, 3)

func main() {
	for _, w := range work {
		go func() {
			limit <- 1
			w()
			<-limit
		}()
	}
	select{}
}
```

最后一句 `select{}` 是一个空的管道选择语句，该语句会导致`main`线程阻塞，从而避免程序过早退出。还有`for{}`、`<-make(chan int)`等诸多方法可以达到类似的效果。因为`main`线程被阻塞了，如果需要程序正常退出的话可以通过调用`os.Exit(0)`实现。

# 内建信道

```go
// Ctrl+C 退出
sig := make(chan os.Signal, 1)
signal.Notify(sig, syscall.SIGINT, syscall.SIGTERM)
fmt.Printf("quit (%v)\n", <-sig)
```

# 链接

- 完成 Go Web Server 并发控制一文 https://github.com/eliben/code-for-blog/blob/master/2019/gohttpconcurrency/channel-manager-server.go
