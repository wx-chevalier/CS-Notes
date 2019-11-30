# Go 模块化

```go
import "fmt"
```

`fmt` is the name of a package that includes a variety of functions related to formatting and output to the screen. Bundling code in this way serves 3 purposes:

- It reduces the chance of having overlapping names. This keeps our function names short and succinct
- It organizes code so that its easier to find code you want to reuse.
- It speeds up the compiler by only requiring recompilation of smaller chunks of a program. Although we use the package `fmt`, we don't have to recompile it every time we change our program.

imports 应该按照字母顺序排序，并且按照，标准库，三方，二方，一方的顺序分节。特殊情况下，如有引用顺序的需求，通过空行实现。
