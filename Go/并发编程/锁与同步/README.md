# 锁与同步

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
