# select

# 并发的退出机制

有时候我们需要通知 goroutine 停止它正在干的事情，特别是当它工作在错误的方向上的时候。Go 语言并没有提供在一个直接终止 Goroutine 的方法，由于这样会导致 goroutine 之间的共享变量处在未定义的状态上。但是如果我们想要退出两个或者任意多个 Goroutine 怎么办呢？

Go 语言中不同 Goroutine 之间主要依靠管道进行通信和同步。要同时处理多个管道的发送或接收操作，我们需要使用 select 关键字（这个关键字和网络编程中的 select 函数的行为类似）。当 select 有多个分支时，会随机选择一个可用的管道分支，如果没有可用的管道分支则选择 default 分支，否则会一直保存阻塞状态。

基于 select 实现的管道的超时判断：

```go
select {
    case v := <-in:
        fmt.Println(v)
    case <-time.After(time.Second):
        return // 超时
}
```

通过 select 的 default 分支实现非阻塞的管道发送或接收操作：

```go
select {
    case v := <-in:
        fmt.Println(v)
    default:
        // 没有数据
}
```

通过 select 来阻止 main 函数退出：

```go
func main() {
	// do some thins
	select{}
}
```

当有多个管道均可操作时，select 会随机选择一个管道。基于该特性我们可以用 select 实现一个生成随机数序列的程序：
