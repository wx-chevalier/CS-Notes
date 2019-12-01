# WaitGroup

# N 线程同步等待

我们无法确定两个原子操作之间的顺序。解决问题的办法就是通过同步原语来给两个事件明确排序：

```go
func main() {
	done := make(chan int)

	go func(){
		println("你好, 世界")
		done <- 1
	}()

	<-done
}
```

当`<-done`执行时，必然要求`done <- 1`也已经执行。根据同一个 Gorouine 依然满足顺序一致性规则，我们可以判断当`done <- 1`执行时，`println("你好, 世界")`语句必然已经执行完成了。因此，现在的程序确保可以正常打印结果。当然，通过`sync.Mutex`互斥量也是可以实现同步的：

```go
func main() {
	var mu sync.Mutex

	mu.Lock()
	go func(){
		println("你好, 世界")
		mu.Unlock()
	}()

	mu.Lock()
}
```

可以确定后台线程的 `mu.Unlock()` 必然在 `println("你好, 世界")` 完成后发生（同一个线程满足顺序一致性），`main` 函数的第二个 `mu.Lock()` 必然在后台线程的 `mu.Unlock()` 之后发生（`sync.Mutex`保证），此时后台线程的打印工作已经顺利完成了。

基于带缓存的管道，我们可以很容易将打印线程扩展到 N 个。下面的例子是开启 10 个后台线程分别打印：

```go
func main() {
	done := make(chan int, 10) // 带 10 个缓存

	// 开N个后台打印线程
	for i := 0; i < cap(done); i++ {
		go func(){
			fmt.Println("你好, 世界")
			done <- 1
		}()
	}

	// 等待N个后台线程完成
	for i := 0; i < cap(done); i++ {
		<-done
	}
}
```

# sync.WaitGroup

对于这种要等待 N 个线程完成后再进行下一步的同步操作有一个简单的做法，就是使用 sync.WaitGroup 来等待一组事件：

```go
func main() {
	var wg sync.WaitGroup

	// 开N个后台打印线程
	for i := 0; i < 10; i++ {
		wg.Add(1)

		go func() {
			fmt.Println("你好, 世界")
			wg.Done()
		}()
	}

	// 等待N个后台线程完成
	wg.Wait()
}
```

其中 `wg.Add(1)` 用于增加等待事件的个数，必须确保在后台线程启动之前执行（如果放到后台线程之中执行则不能保证被正常执行到）。当后台线程完成打印工作之后，调用 `wg.Done()` 表示完成一个事件。`main` 函数的 `wg.Wait()` 是等待全部的事件完成。
