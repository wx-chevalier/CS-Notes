# strings

strings 包实现了用于操作字符串的简单函数，包括 strings 导出函数和 Reader, Replacer 两个结构体。

# 常用导出函数

判断字符串与子串关系

- `func EqualFold(s, t string) bool` // 判断两个 utf-8 编码字符串，大小写不敏感
- `func HasPrefix(s, prefix string) bool` // 判断 s 是否有前缀字符串 prefix
- `func Contains(s, substr string) bool` // 判断字符串 s 是否包含子串 substr
- `func ContainsAny(s, chars string) bool` // 判断字符串 s 是否包含字符串 chars 中的任一字符
- `func Count(s, sep string) int` // 返回字符串 s 中有几个不重复的 sep 子串

获取字符串中子串位置

- `func Index(s, sep string) int` // 子串 sep 在字符串 s 中第一次出现的位置，不存在则返回-1
- `func IndexByte(s string, c byte) int` // 字符 c 在 s 中第一次出现的位置，不存在则返回-
- `func IndexAny(s, chars string) int` // 字符串 chars 中的任一 utf-8 码值在 s 中第一次出现的位置，如果不存在或者 chars 为空字符串则返回-1
- `func IndexFunc(s string, f func(rune) bool) int` // s 中第一个满足函数 f 的位置 i（该处的 utf-8 码值 r 满足 f(r)==true），不存在则返回-1
- `func LastIndex(s, sep string) int` // 子串 sep 在字符串 s 中最后一次出现的位置，不存在则返回-1

字符串中字符处理

- `func Title(s string) string` // 返回 s 中每个单词的首字母都改为标题格式的字符串拷贝
- `func ToLower(s string) string` // 返回将所有字母都转为对应的小写版本的拷贝
- `func ToUpper(s string) string` // 返回将所有字母都转为对应的大写版本的拷贝
- `func Repeat(s string, count int) string` // 返回 count 个 s 串联的字符串
- `func Replace(s, old, new string, n int) string` // 返回将 s 中前 n 个不重叠 old 子串都替换为 new 的新字符串，如果 n<0 会替换所有 old 子串
- `func Map(mapping func(rune) rune, s string) string` // 将 s 的每一个 unicode 码值 r 都替换为 mapping(r)，返回这些新码值组成的字符串拷贝。如果 mapping 返回一个负值，将会丢弃该码值而不会被替换

字符串前后端处理

- `func Trim(s string, cutset string) string` // 返回将 s 前后端所有 cutset 包含的 utf-8 码值都去掉的字符串
- `func TrimSpace(s string) string` // 返回将 s 前后端所有空白（unicode.IsSpace 指定）都去掉的字符串
- `func TrimFunc(s string, f func(rune) bool) string` // 返回将 s 前后端所有满足 f 的 unicode 码值都去掉的字符串

字符串分割与合并

- `func Fields(s string) []string` // 返回将字符串按照空白（通过 unicode.IsSpace 判断，可以是一到多个连续的空白字符）分割的多个字符串
- `func Split(s, sep string) []string` // 用去掉 s 中出现的 sep 的方式进行分割，会分割到结尾，并返回生成的所有片段组成的切片
- `func Join(a []string, sep string) string` // 将一系列字符串连接为一个字符串，之间用 sep 来分隔

```go
// go 标准库 strings
package main

import (
    "fmt"
    "strings"
)

func main() {
    // 判断两个utf-8编码字符串，大小写不敏感
    s, t := "hello go", "hello Go"
    is_equal := strings.EqualFold(s, t)
    fmt.Println("EqualFold: ", is_equal) // EqualFold:  true

    // 判断s是否有前缀字符串prefix
    prefix := "hello"
    has_prefix := strings.HasPrefix(s, prefix)
    fmt.Println(has_prefix) // true

    // 判断s是否有后缀字符串suffix
    suffix := "go"
    has_suffix := strings.HasSuffix(s, suffix)
    fmt.Println(has_suffix) // true

    // 判断字符串s是否包含子串substr
    substr := "lo"
    con := strings.Contains(s, substr)
    fmt.Println(con) // true

    // 判断字符串s是否包含utf-8码值r
    r := rune(101)
    ru := 'e'
    con_run := strings.ContainsRune(s, r)
    fmt.Println(con_run, r, ru) // true

    // 子串sep在字符串s中第一次出现的位置，不存在则返回-1
    sep := "o"
    sep_idnex := strings.Index(s, sep)
    fmt.Println(sep_idnex) // 4

    // 子串sep在字符串s中最后一次出现的位置，不存在则返回-1
    sep_lastindex := strings.LastIndex(s, sep)
    fmt.Println(sep_lastindex) // 7

    // 返回s中每个单词的首字母都改为标题格式的字符串拷贝
    title := strings.Title(s)
    fmt.Println(title) // Hello Go

    // 返回将所有字母都转为对应的标题版本的拷贝
    to_title := strings.ToTitle(s)
    fmt.Println(to_title) // HELLO GO

    // 返回将所有字母都转为对应的小写版本的拷贝
    s_lower := strings.ToLower(s)
    fmt.Println(s_lower) // hello go

    // 返回count个s串联的字符串
    s_repeat := strings.Repeat(s, 3)
    fmt.Println(s_repeat) // hello gohello gohello go

    // 返回将s中前n个不重叠old子串都替换为new的新字符串，如果n<0会替换所有old子串
    s_old, s_new := "go", "world"
    s_replace := strings.Replace(s, s_old, s_new, -1)
    fmt.Println(s_replace) // hello world

    // 返回将s前后端所有cutset包含的utf-8码值都去掉的字符串
    s, cutset := "#abc!!!", "#!"
    s_new = strings.Trim(s, cutset)
    fmt.Println(s, s_new) // #abc!!! abc

    // 返回将字符串按照空白（unicode.IsSpace确定，可以是一到多个连续的空白字符）分割的多个字符串
    s = "hello world! go language"
    s_fields := strings.Fields(s)
    for k, v := range s_fields {
        fmt.Println(k, v)
    }
    // 0 hello
    // 1 world!
    // 2 go
    // 3 language

    // 用去掉s中出现的sep的方式进行分割，会分割到结尾，并返回生成的所有片段组成的切片
    s_split := strings.Split(s, " ")
    fmt.Println(s_split) // [hello world! go language]

    // 将一系列字符串连接为一个字符串，之间用sep来分隔
    s_join := strings.Join([]string{"a", "b", "c"}, "/")
    fmt.Println(s_join) // a/b/c

    // 将s的每一个unicode码值r都替换为mapping(r)，返回这些新码值组成的字符串拷贝。如果mapping返回一个负值，将会丢弃该码值而不会被替换
    map_func := func(r rune) rune {
        switch {
        case r > 'A' && r < 'Z':
            return r + 32
        case r > 'a' && r < 'z':
            return r - 32
        }
        return r
    }
    s = "Hello World!"
    s_map := strings.Map(map_func, s)
    fmt.Println(s_map) // hELLO wORLD!
}
```

# Reader 结构体

`Reader` 类型从一个字符串读取数据，实现了`io.Reader`, `io.Seeker`等接口。

- `func NewReader(s string) *Reader` // 通过字符串 `s` 创建一个 `Reader`
- `func (r *Reader) Len() int` // 返回 `r` 还没有读取部分的长度
- `func (r *Reader) Read(b []byte) (n int, err error)` // 读取部分数据到 `b` 中，读取的长度取决于 `b` 的容量
- `func (r *Reader) ReadByte() (b byte, err error)` // 从 `r` 中读取一字节数据

```go
// go 标准库 strings.Reader
package main

import (
    "fmt"
    "strings"
)

func main() {
    s := "hello world"
    // 创建 Reader
    r := strings.NewReader(s)

    fmt.Println(r) // &{hello world 0 -1}
    fmt.Println(r.Size()) // 11 获取字符串长度
    fmt.Println(r.Len()) // 11 获取未读取长度

    // 读取前6个字符
    for r.Len() > 5 {
        b, err := r.ReadByte() // 读取1 byte
        fmt.Println(string(b), err, r.Len(), r.Size())
        // h <nil> 10 11
        // e <nil> 9 11
        // l <nil> 8 11
        // l <nil> 7 11
        // o <nil> 6 11
        //   <nil> 5 11
    }

    // 读取还未被读取字符串中5字符的数据
    b_s := make([]byte, 5)
    n, err := r.Read(b_s)
    fmt.Println(string(b_s), n ,err) // world 5 <nil>
    fmt.Println(r.Size()) // 11
    fmt.Println(r.Len()) // 0
}
```

# Replacer 结构体

`Replacer` 类型实现字符串替换的操作

- `func NewReplacer(oldnew ...string) *Replacer` // 使用提供的多组 old、new 字符串对创建一个\*Replacer
- `func (r *Replacer) Replace(s string) string` // 返回`s` 所有替换完后的拷贝
- `func (r *Replacer) WriteString(w io.Writer, s string) (n int, err error)` // 向 w 中写入 s 替换完后的拷贝

```go
// go 标准库 strings.Replacer
package main

import (
    "fmt"
    "strings"
    "os"
)

func main() {
    s := "<p>Go Language</p>"
    r := strings.NewReplacer("<", "&lt;", ">", "&gt;")
    fmt.Println(r.Replace(s))

    r.WriteString(os.Stdout, s)
}
```
