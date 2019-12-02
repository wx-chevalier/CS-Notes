# select

通道是将 Goroutine 的粘合剂，select 语句是通道的粘合剂。后者让我们能够在项目中组合通道以形成更大的抽象来解决实际中遇到的问题。我们可以在单个函数或类型定义中找到将本地通道绑定在一起的 select 语句，也可以在全局范围找到连接系统级别两个或多个组件的使用范例。除了连接组件外，在程序中的关键部分，select 语句还可以帮助你安全地将通道与业务层面的概念（如取消，超时，等待和默认值）结合在一起。

```go
var c1, c2 <-chan interface{}
var c3 chan<- interface{}
select {
case <-c1:
	// Do something
case <-c2:
	// Do something
case c3 <- struct{}{}:
	// Do something
}
```

跟 switch 相同的是，select 代码块也包含一系列 case 分支。跟 switch 不同的是，case 分支不会被顺序测试，如果没有任何分支的条件可供满足，select 会一直等待直到某个 case 语句完成。所有通道的读取和写入都被同时考虑，以查看它们中的任何一个是否准备好： 如果没有任何通道准备就绪，则整个 select 语句将会阻塞。当一个通道准备好时，该操作将继续，并执行相应的语句。 我们来看一个简单的例子：

```go
start := time.Now()
c := make(chan interface{})
go func() {
	time.Sleep(5 * time.Second)
	close(c) // 5 秒后关闭通道
}()

fmt.Println("Blocking on read...")
select {
case <-c: // 尝试读取通道。注意，尽管我们可以不使用select语句而直接使用<-c，但我们的目的是为了展示select语句。
	fmt.Printf("Unblocked %v later.\n", time.Since(start))
}

// Blocking on read...
// Unblocked 5s later.
```
