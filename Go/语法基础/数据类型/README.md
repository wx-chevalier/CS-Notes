# 数据结构

值得一提的是，`interface{}` 可以表示为任意的接口，譬如表示任意类型的多重数组：

```go
simpleCase := [][]interface{}{
   []interface{}{1, 2},
   []interface{}{3, 4},
}
```

# 类型别名

```go
type (
	subscriber chan interface{}         // 订阅者为一个管道
	topicFunc  func(v interface{}) bool // 主题为一个过滤器
)
```

# 类型断言

类型断言接受一个接口值，并从中提取指定的明确类型的值。

```go
value.(typeName)
```

而其结果则是拥有静态类型 typeName 的新值。该类型必须为该接口所拥有的具体类型，或者该值可转换成的第二种接口类型。要提取我们知道在该值中的字符串，可以这样：

```go
str := value.(string)
```

如果断言失败，会 panic。

```go
str, ok := value.(string)
```

如果断言失败，ok 为 false，str 为 string 的空值。

# 类型转换

```go
func (s Sequence) String() string {
	sort.Sort(s)
	return fmt.Sprint([]int(s))
}
```

该方法是通过类型转换技术，在 String 方法中安全调用 Sprintf 的另个一例子。若我们忽略类型名的话，这两种类型（Sequence 和 []int）其实是相同的，因此在二者之间进行转换是合法的。转换过程并不会创建新值，它只是值暂让现有的时看起来有个新类型而已。（还有些合法转换则会创建新值，如从整数转换为浮点数等。）
