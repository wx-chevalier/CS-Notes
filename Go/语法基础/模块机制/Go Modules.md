# Go Modules

Go Modules 将包名与路径分离，可以存放于文件系统上的任何为止，而不用管 GOPATH 路径到底是什么，我们可以创建任意的项目目录:

```sh
$ mkdir -p /tmp/scratchpad/hello
$ cd /tmp/scratchpad/hello
```

然后可以用 `go mod init example.com/m` 生成 go.mod 模板。模块根目录和其子目录的所有包构成模块，在根目录下存在 go.mod 文件，子目录会向着父目录、爷目录一直找到 go.mod 文件。模块路径指模块根目录的导入路径，也是其他子目录导入路径的前缀。go.mod 文件第一行定义了模块路径，有了这一行才算作是一个模块。go.mod 文件接下来的篇幅用来定义当前模块的依赖和依赖版本，也可以排除依赖和替换依赖。

```sh
module example.com/m

require (
    golang.org/x/text v0.3.0
    gopkg.in/yaml.v2 v2.1.0
    rsc.io/quote v1.5.2
)

replace (
    golang.org/x/text => github.com/golang/text v0.3.0
)
```

然后照常编写 Go 模块代码:

```go
// hello.go
package main

import (
    "fmt"
    "rsc.io/quote"
)

func main() {
    fmt.Println(quote.Hello())
}
```

在执行 `go build` 命令之后，即可以在 go.mod 文件中查看模块定义与显式的声明，它会自动地将未声明的依赖添加到 go.mod 文件中。

# 模块结构

模块是包含了 Go 源文件的目录树，并在根目录中添加了名为 go.mod 的文件，go.mod 包含模块导入名称，声明了要求的依赖项，排除的依赖项和替换的依赖项。

```sh
module my/thing

require (
        one/thing v1.3.2
        other/thing v2.5.0 // indirect
        ...
)

exclude (
        bad/thing v0.7.3
)

replace (
        src/thing 1.0.2 => dst/thing v1.1.0
)
```

需要注意的是，该文件中声明的依赖，并不会在模块的源代码中使用 import 自动导入，还是需要我们人工添加 import 语句来导入的。模块可以包含其他模块，在这种情况下，它们的内容将从父模块中排除。除了 go.mod 文件外，跟目录下还可以存在一个名为 go.sum 的文件，用于保存所有的依赖项的哈希摘要校验之，用于验证缓存的依赖项是否满足模块要求。

## 目录结构

一般来说，我们在 go.mod 中指定的名称是项目名，每个 package 中的名称需要和目录名保持一致。

```go
// go.mod
module myprojectname
// or
module github.com/myname/myproject
```

然后用如下方式导入其他模块：

```go
import myprojectname/stuff
import github.com/myname/myproject/stuff
```

# 外部依赖

模块依赖项会被下载并存储到 `GOPATH/src/mod` 目录中，直接后果就是废除了模块的组织名称。假设我们正在开发的项目依赖于 github.com/me/lib 且版本号 1.0.0 的模块，对于这种情况，我们会发现在 GOPATH/src/mod 中文件结构如下：

![Go Modules 缓存路径](https://s1.ax1x.com/2019/11/19/M2IIhD.png)

Go 的模块版本号必须以 v 开头，在发布版本时可以通过 Tag 方式来指定不同的版本。我们可以使用 `go list -m all` 来查看全部的依赖，使用 `go mod tidy` 来移除未被使用的依赖，使用 `go mod vendor` 可以生成独立的 vendor 目录。
