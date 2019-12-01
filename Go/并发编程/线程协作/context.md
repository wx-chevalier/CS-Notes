# context

在 Go1.7 发布时，标准库增加了一个 context 包，用来简化对于处理单个请求的多个 Goroutine 之间与请求域的数据、超时和退出等操作，官方有博文对此做了专门介绍。我们可以用 context 包来重新实现前面的线程安全退出或超时的控制:

```go
func worker(ctx context.Context, wg *sync.WaitGroup) error {
	defer wg.Done()

	for {
		select {
		default:
			fmt.Println("hello")
		case <-ctx.Done():
			return ctx.Err()
		}
	}
}

func main() {
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)

	var wg sync.WaitGroup
	for i := 0; i < 10; i++ {
		wg.Add(1)
		go worker(ctx, &wg)
	}

	time.Sleep(time.Second)
	cancel()

	wg.Wait()
}
```

当并发体超时或 main 主动停止工作者 Goroutine 时，每个工作者都可以安全退出。Go 语言是带内存自动回收特性的，因此内存一般不会泄漏。在前面素数筛的例子中，GenerateNatural 和 PrimeFilter 函数内部都启动了新的 Goroutine，当 main 函数不再使用管道时后台 Goroutine 有泄漏的风险。我们可以通过 context 包来避免这个问题，下面是改进的素数筛实现：

```go
// 返回生成自然数序列的管道: 2, 3, 4, ...
func GenerateNatural(ctx context.Context) chan int {
	ch := make(chan int)
	go func() {
		for i := 2; ; i++ {
			select {
			case <- ctx.Done():
				return
			case ch <- i:
			}
		}
	}()
	return ch
}

// 管道过滤器: 删除能被素数整除的数
func PrimeFilter(ctx context.Context, in <-chan int, prime int) chan int {
	out := make(chan int)
	go func() {
		for {
			if i := <-in; i%prime != 0 {
				select {
				case <- ctx.Done():
					return
				case out <- i:
				}
			}
		}
	}()
	return out
}

func main() {
	// 通过 Context 控制后台Goroutine状态
	ctx, cancel := context.WithCancel(context.Background())

	ch := GenerateNatural(ctx) // 自然数序列: 2, 3, 4, ...
	for i := 0; i < 100; i++ {
		prime := <-ch // 新出现的素数
		fmt.Printf("%v: %v\n", i+1, prime)
		ch = PrimeFilter(ctx, ch, prime) // 基于新素数构造的过滤器
	}

	cancel()
}
```

当 main 函数完成工作前，通过调用 cancel()来通知后台 Goroutine 退出，这样就避免了 Goroutine 的泄漏。
