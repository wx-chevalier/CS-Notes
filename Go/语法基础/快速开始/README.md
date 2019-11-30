# HelloWorld

所有的 Go 程序，都是由最基本的函数和变量构成，函数和变量被组织到一个个单独的 Go 源文件中，这些源文件再按照作者的意图组织成合适的 package，最终这些 package 再有机地组成一个完整的 Go 语言程序。

```go
package main

import (
	"fmt"
	"log"
	"net/http"
	"time"
)

func main() {
	fmt.Println("Please visit http://127.0.0.1:12345/")
	http.HandleFunc("/", func(w http.ResponseWriter, req *http.Request) {
		s := fmt.Sprintf("你好, 世界! -- Time: %s", time.Now().String())
		fmt.Fprintf(w, "%v\n", s)
		log.Printf("%v\n", s)
	})
	if err := http.ListenAndServe(":12345", nil); err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}
```

package 这一行告诉我们当前文件属于哪个包，而包名 main 则告诉我们它是一个可独立运行的包，它在编译后会产生可执行文件。除了 main 包之外，其它的包最后都会生成 `*.a` 文件(也就是包文件)。每一个可独立运行的程序，必定包含一个 package main，在这个 main 包中必定包含一个入口函数 main，而这个函数既没有参数，也没有返回值。

其中，函数用于包含一系列的语句(指明要执行的操作序列)，以及执行操作时存放数据的变量。我们这个程序中函数的名字是 main。虽然 Go 语言中，函数的名字没有太多的限制，但是 main 包中的 main 函数默认是每一个可执行程序的入口。而 package 则用于包装和组织相关的函数、变量和常量。在使用一个 package 之前，我们需要使用 import 语句导入包。例如，我们这个程序中导入了 fmt 包（fmt 是 format 单词的缩写，表示格式化相关的包），然后我们才可以使用 fmt 包中的 Println 函数。

而双引号包含的“你好, 世界!”则是 Go 语言的字符串面值常量。和 C 语言中的字符串不同，Go 语言中的字符串内容是不可变更的。在以字符串作为参数传递给 fmt.Println 函数时，字符串的内容并没有被复制，传递的仅仅是字符串的地址和长度（字符串的结构在 reflect.StringHeader 中定义）。在 Go 语言中，函数参数都是以复制的方式(不支持以引用的方式)传递（比较特殊的是，Go 语言闭包函数对外部变量是以引用的方式使用）。

最后大家可以看到我们输出的内容里面包含了很多非 ASCII 码字符。实际上，Go 是天生支持 UTF-8 的，任何字符都可以直接输出，你甚至可以用 UTF-8 中的任何字符作为标识符。
