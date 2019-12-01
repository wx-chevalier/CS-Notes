# panic

Go 还为我们提供了 panic 函数，所谓 panic，即是未获得预期结果，常用于抛出异常结果。譬如当我们获得了某个函数返回的异常，却不知道如何处理或者不需要处理时，可以直接通过 panic 函数中断当前运行，打印出错误信息、Goroutine 追踪信息，并且返回非零的状态码：

```go
_, err := os.Create("/tmp/file")
if err != nil {
	panic(err)
}
```

该函数接受一个任意类型的实参（一般为字符串），并在程序终止时打印。它还能表明发生了意料之外的事情，比如从无限循环中退出了。

```go
// 用牛顿法计算立方根的一个玩具实现。
func CubeRoot(x float64) float64 {
	z := x/3   // 任意初始值
	for i := 0; i < 1e6; i++ {
		prevz := z
		z -= (z*z*z-x) / (3*z*z)
		if veryClose(z, prevz) {
			return z
		}
	}
	// 一百万次迭代并未收敛，事情出错了。
	panic(fmt.Sprintf("CubeRoot(%g) did not converge", x))
}
```

实际的库函数应避免 panic。若问题可以被屏蔽或解决，最好就是让程序继续运行而不是终止整个程序。一个可能的反例就是初始化 若某个库真的不能让自己工作，且有足够理由产生 Panic，那就由它去吧。

```go
var user = os.Getenv("USER")

func init() {
	if user == "" {
		panic("no value for $USER")
	}
}
```

# Recover | 恢复

当函数 F 调用 panic 时，其执行流程会被终止，而所有的 deferred 函数会被正常的依次执行，然后 F 会返回到调用者。F 此时的行为逻辑与直接调用 panic 函数并无差异，进程会在函数所在 Goroutine 的所有函数执行完毕之后，恢复异常的调用栈。Recover 函数则可以手动地恢复 Panic Goroutine 的执行，正常的执行中 recover 函数会返回 nil；而如果当前 Goroutine 被 panic，recover 函数会捕获传递给 panic 的值，并且恢复正常的执行流。

```go
func main(){
    defer func(){
        if r := recover();r != nil{
            fmt.Println(r)
        }
	}()

    panic([]int{12312})
}
```

当 panic 被调用后（包括不明确的运行时错误，例如切片检索越界或类型断言失败）， 程序将立刻终止当前函数的执行，并开始回溯 协程的栈，运行任何被推迟的函数。若回溯到达 协程栈的顶端，程序就会终止。不过我们可以用内建的 recover 函数来重新或来取回 协程的控制权限并使其恢复正常执行。

调用 recover 将停止回溯过程，并返回传入 panic 的实参。由于在回溯时只有被推迟函数中的代码在运行，因此 recover 只能在被推迟的函数中才有效。

## 局部容错

recover 的一个应用就是在服务器中终止失败的协程而无需杀死其它正在执行的协程。

```go
func server(workChan <-chan *Work) {
	for work := range workChan {
		go safelyDo(work)
	}
}

func safelyDo(work *Work) {
	defer func() {
		if err := recover(); err != nil {
			log.Println("work failed:", err)
		}
	}()
	do(work)
}
```

在此例中，若 do(work) 触发了 Panic，其结果就会被记录， 而该协程会被干净利落地结束，不会干扰到其它协程。我们无需在推迟的闭包中做任何事情， recover 会处理好这一切。由于直接从被推迟函数中调用 recover 时不会返回 nil， 因此被推迟的代码能够调用本身使用了 panic 和 recover 的库函数而不会失败。例如在 safelyDo 中，被推迟的函数可能在调用 recover 前先调用记录函数，而该记录函数应当不受 Panic 状态的代码的影响。

## 避免在非 defer 语句中调用 recover

在非`defer`语句中执行`recover`调用是初学者常犯的错误:

```
func main() {
	if r := recover(); r != nil {
		log.Fatal(r)
	}

	panic(123)

	if r := recover(); r != nil {
		log.Fatal(r)
	}
}
```

上面程序中两个`recover`调用都不能捕获任何异常。在第一个`recover`调用执行时，函数必然是在正常的非异常执行流程中，这时候`recover`调用将返回`nil`。发生异常时，第二个`recover`调用将没有机会被执行到，因为`panic`调用会导致函数马上执行已经注册`defer`的函数后返回。

其实`recover`函数调用有着更严格的要求：我们必须在`defer`函数中直接调用`recover`。如果`defer`中调用的是`recover`函数的包装函数的话，异常的捕获工作将失败！比如，有时候我们可能希望包装自己的`MyRecover`函数，在内部增加必要的日志信息然后再调用`recover`，这是错误的做法：

```go
func main() {
	defer func() {
		// 无法捕获异常
		if r := MyRecover(); r != nil {
			fmt.Println(r)
		}
	}()
	panic(1)
}

func MyRecover() interface{} {
	log.Println("trace...")
	return recover()
}
```

同样，如果是在嵌套的`defer`函数中调用`recover`也将导致无法捕获异常：

```go
func main() {
	defer func() {
		defer func() {
			// 无法捕获异常
			if r := recover(); r != nil {
				fmt.Println(r)
			}
		}()
	}()
	panic(1)
}
```

2 层嵌套的`defer`函数中直接调用`recover`和 1 层`defer`函数中调用包装的`MyRecover`函数一样，都是经过了 2 个函数帧才到达真正的`recover`函数，这个时候 Goroutine 的对应上一级栈帧中已经没有异常信息。

如果我们直接在`defer`语句中调用`MyRecover`函数又可以正常工作了：

```go
func MyRecover() interface{} {
	return recover()
}

func main() {
	// 可以正常捕获异常
	defer MyRecover()
	panic(1)
}
```

但是，如果`defer`语句直接调用`recover`函数，依然不能正常捕获异常：

```go
func main() {
	// 无法捕获异常
	defer recover()
	panic(1)
}
```

必须要和有异常的栈帧只隔一个栈帧，`recover`函数才能正常捕获异常。换言之，`recover`函数捕获的是祖父一级调用函数栈帧的异常（刚好可以跨越一层`defer`函数）！

当然，为了避免`recover`调用者不能识别捕获到的异常, 应该避免用`nil`为参数抛出异常:

```go
func main() {
	defer func() {
		if r := recover(); r != nil { ... }
		// 虽然总是返回nil, 但是可以恢复异常状态
	}()

	// 警告: 用`nil`为参数抛出异常
	panic(nil)
}
```

当希望将捕获到的异常转为错误时，如果希望忠实返回原始的信息，需要针对不同的类型分别处理：

```go
func foo() (err error) {
	defer func() {
		if r := recover(); r != nil {
			switch x := r.(type) {
			case string:
				err = errors.New(x)
			case error:
				err = x
			default:
				err = fmt.Errorf("Unknown panic: %v", r)
			}
		}
	}()

	panic("TODO")
}
```

基于这个代码模板，我们甚至可以模拟出不同类型的异常。通过为定义不同类型的保护接口，我们就可以区分异常的类型了：

```go
func main {
	defer func() {
		if r := recover(); r != nil {
			switch x := r.(type) {
			case runtime.Error:
				// 这是运行时错误类型异常
			case error:
				// 普通错误类型异常
			default:
				// 其他类型异常
			}
		}
	}()

	// ...
}
```

不过这样做和 Go 语言简单直接的编程哲学背道而驰了。

## 适当的异常处理

通过恰当地使用恢复模式，do 函数（及其调用的任何代码）可通过调用 panic 来避免更坏的结果。我们可以利用这种思想来简化复杂软件中的错误处理。让我们看看 regexp 包的理想化版本，它会以局部的错误类型调用 panic 来报告解析错误。以下是一个 error 类型的 Error 方法和一个 Compile 函数的定义：

```go
// Error 是解析错误的类型，它满足 error 接口。
type Error string
func (e Error) Error() string {
	return string(e)
}

// error 是 *Regexp 的方法，它通过用一个 Error 触发Panic来报告解析错误。
func (regexp *Regexp) error(err string) {
	panic(Error(err))
}

// Compile 返回该正则表达式解析后的表示。
func Compile(str string) (regexp *Regexp, err error) {
	regexp = new(Regexp)
	// doParse will panic if there is a parse error.
	defer func() {
		if e := recover(); e != nil {
			regexp = nil    // 清理返回值。
			err = e.(Error) // 若它不是解析错误，将重新触发Panic。
		}
	}()
	return regexp.doParse(str), nil
}
```

若 doParse 触发了 Panic，恢复块会将返回值设为 nil，被推迟的函数能够修改已命名的返回值。在 err 的赋值过程中，我们将通过断言它是否拥有局部类型 Error 来检查它。若它没有，类型断言将会失败，此时会产生运行时错误，并继续栈的回溯，仿佛一切从未中断过一样。该检查意味着若发生了一些像索引越界之类的意外，那么即便我们使用了 panic 和 recover 来处理解析错误，代码仍然会失败。

通过适当的错误处理，error 方法（由于它是个绑定到具体类型的方法， 因此即便它与内建的 error 类型名字相同也没有关系） 能让报告解析错误变得更容易，而无需手动处理回溯的解析栈：

```go
if pos == 0 {
	re.error("'*' illegal at start of expression")
}
```

尽管这种模式很有用，但它应当仅在包内使用。Parse 会将其内部的 panic 调用转为 error 值，它并不会向调用者暴露出 panic。这是个值得遵守的良好规则。

顺便一提，这种重新触发 Panic 的惯用法会在产生实际错误时改变 Panic 的值。然而，不管是原始的还是新的错误都会在崩溃报告中显示，因此问题的根源仍然是可见的。这种简单的重新触发 Panic 的模型已经够用了，毕竟他只是一次崩溃。但若你只想显示原始的值，也可以多写一点代码来过滤掉不需要的问题，然后用原始值再次触发 Panic。
