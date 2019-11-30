# HelloWorld

```go
package main
import "fmt"

func main() {
 fmt.Printf("Hello, world or 你好，世界 or καλημ ́ρα κóσμ or こんにちはせかい\n")
}
```

首先我们要了解一个概念，协程序是通过 package 来组织的。package 这一行告诉我们当前文件属于哪个包，而包名 main 则告诉我们它是一个可独立运行的包，它在编译后会产生可执行文件。除了 main 包之外，其它的包最后都会生成\*.a 文件(也就是包文件)并放置在$GOPATH/pkg/$GOOS\_$GOARCH中(以 Mac 为例就是$GOPATH/pkg/darwin_amd64)。

每一个可独立运行的 协程序，必定包含一个 package main，在这个 main 包中必定包含一个入口函数 main，而这个函数既没有参数，也没有返回值。为了打印 Hello, world...，我们调用了一个函数 Printf，这个函数来自于 fmt 包，所以我们在第三行中导入了系统级别的 fmt 包：import "fmt"。包的概念和 Python 中的 package 类似，它们都有一些特别的好处：模块化(能够把你的程序分成多个模块)和可重用性(每个模块都能被其它应用程序反复使用)。

最后大家可以看到我们输出的内容里面包含了很多非 ASCII 码字符。实际上，Go 是天生支持 UTF-8 的，任何字符都可以直接输出，你甚至可以用 UTF-8 中的任何字符作为标识符。
