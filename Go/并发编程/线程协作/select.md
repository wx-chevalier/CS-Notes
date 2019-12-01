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

```go
func main() {
	ch := make(chan int)
	go func() {
		for {
			select {
			case ch <- 0:
			case ch <- 1:
			}
		}
	}()

	for v := range ch {
		fmt.Println(v)
	}
}
```

我们通过 select 和 default 分支可以很容易实现一个 Goroutine 的退出控制:

```go
func worker(cannel chan bool) {
	for {
		select {
		default:
			fmt.Println("hello")
			// 正常工作
		case <-cannel:
			// 退出
		}
	}
}

func main() {
	cannel := make(chan bool)
	go worker(cannel)

	time.Sleep(time.Second)
	cannel <- true
}
```

但是管道的发送操作和接收操作是一一对应的，如果要停止多个 Goroutine 那么可能需要创建同样数量的管道，这个代价太大了。其实我们可以通过 close 关闭一个管道来实现广播的效果，所有从关闭管道接收的操作均会收到一个零值和一个可选的失败标志。

```go
func worker(cannel chan bool) {
	for {
		select {
		default:
			fmt.Println("hello")
			// 正常工作
		case <-cannel:
			// 退出
		}
	}
}

func main() {
	cancel := make(chan bool)

	for i := 0; i < 10; i++ {
		go worker(cancel)
	}

	time.Sleep(time.Second)
	close(cancel)
}
```

我们通过 close 来关闭 cancel 管道向多个 Goroutine 广播退出的指令。不过这个程序依然不够稳健：当每个 Goroutine 收到退出指令退出时一般会进行一定的清理工作，但是退出的清理工作并不能保证被完成，因为 main 线程并没有等待各个工作 Goroutine 退出工作完成的机制。我们可以结合 sync.WaitGroup 来改进:

```go
func worker(wg *sync.WaitGroup, cannel chan bool) {
	defer wg.Done()

	for {
		select {
		default:
			fmt.Println("hello")
		case <-cannel:
			return
		}
	}
}

func main() {
	cancel := make(chan bool)

	var wg sync.WaitGroup
	for i := 0; i < 10; i++ {
		wg.Add(1)
		go worker(&wg, cancel)
	}

	time.Sleep(time.Second)
	close(cancel)
	wg.Wait()
}
```

现在每个工作者并发体的创建、运行、暂停和退出都是在 main 函数的安全控制之下了。
