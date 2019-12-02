# WaitGroup

如果你不关心并发操作的结果，或者有其他方式收集结果，那么 WaitGroup 是等待一组并发操作完成的好方法。如果这两个条件都不成立，我建议你改用 channel 和 select 语句。

```go
var wg sync.WaitGroup

wg.Add(1) //1
go func() {
	defer wg.Done() //2
	fmt.Println("1st goroutine sleeping...")
	time.Sleep(1)
}()

wg.Add(1) //1
go func() {
	defer wg.Done() //2
	fmt.Println("2nd goroutine sleeping...")
	time.Sleep(2)
}()

wg.Wait() //3
fmt.Println("All goroutines complete.")
```
