# Go Modules

模块根目录和其子目录的所有包构成模块，在根目录下存在 go.mod 文件，子目录会向着父目录、爷目录一直找到 go.mod
文件。

模块路径指模块根目录的导入路径，也是其他子目录导入路径的前缀。 go.mod
文件第一行定义了模块路径，有了这一行才算作是一个模块。

go.mod 文件接下来的篇幅用来定义当前模块的依赖和依赖版本，也可以排除依赖和替换依赖。

```
module example.com/m

require (
    golang.org/x/text v0.3.0
    gopkg.in/yaml.v2 v2.1.0
)

replace (
    golang.org/x/text => github.com/golang/text v0.3.0
)
```

这个文件不用手写，可以用 `go mod -init -module example.com/m` 生成 go.mod 的第一行。
