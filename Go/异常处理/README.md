# Go 中的异常处理

Go 语言中并不存在 try-catch 等异常处理的关键字，对于那些可能返回异常的函数，只需要在函数返回值中添加额外的 Error 类型的返回值：

```go
type error interface {
    Error() string
}
```

错误信息不要以大写字母开头或是以句点结尾。因为他们通常会在一个上下文中被打印出来。某个可能返回异常的函数调用方式如下：

```go
import (
    "fmt"
    "errors"
)

func main() {
    result, err:= Divide(2,0)

    if err != nil {
            fmt.Println(err)
    }else {
            fmt.Println(result)
    }
}

func Divide(value1 int,value2 int)(int, error) {
    if(value2 == 0){
        return 0, errors.New("value2 mustn't be zero")
    }
    return value1/value2  , nil
}
```

# 典型错误

```go
// PathError 记录一个错误以及产生该错误的路径和操作。
type PathError struct {
	Op string    // "open"、"unlink" 等等。
	Path string  // 相关联的文件。
	Err error    // 由系统调用返回。
}

func (e *PathError) Error() string {
	return e.Op + " " + e.Path + ": " + e.Err.Error()
}
```

PathError 的 Error 会生成如下错误信息：

```go
open /etc/passwx: no such file or directory
```

这种错误包含了出错的文件名、操作和触发的操作系统错误，即便在产生该错误的调用 和输出的错误信息相距甚远时，它也会非常有用，这比苍白的“不存在该文件或目录”更具说明性。

错误字符串应尽可能地指明它们的来源，例如产生该错误的包名前缀。例如在 image 包中，由于未知格式导致解码错误的字符串为“image: unknown format”。

若调用者关心错误的完整细节，可使用类型选择或者类型断言来查看特定错误，并抽取其细节。对于 PathErrors，它应该还包含检查内部的 Err 字段以进行可能的错误恢复。

```go
for try := 0; try < 2; try++ {
	file, err = os.Create(filename)
	if err == nil {
		return
	}
	if e, ok := err.(*os.PathError); ok && e.Err == syscall.ENOSPC {
		deleteTempFiles()  // 恢复一些空间。
		continue
	}
	return
}
```

这里的第二条 if 是另一种类型断言。若它失败， ok 将为 false，而 e 则为 nil. 若它成功，ok 将为 true，这意味着该错误属于 \*os.PathError 类型，而 e 能够检测关于该错误的更多信息。

预先定义好的错误，例如 os 包中预先定义好一系列错误，并且设置为导出变量，使用放可以通过导出变量来判读到底发生了何种类型的错误。

```go
// Portable analogs of some common system call errors.
var (
	ErrInvalid    = errors.New("invalid argument") // methods on File will return this error when the receiver is nil
	ErrPermission = errors.New("permission denied")
	ErrExist      = errors.New("file already exists")
	ErrNotExist   = errors.New("file does not exist")
	ErrClosed     = errors.New("file already closed")
	ErrNoDeadline = poll.ErrNoDeadline
)
```

# 链接

