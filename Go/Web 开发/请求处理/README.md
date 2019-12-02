# 请求处理

任何实现了 Handler 的对象都能够处理 HTTP 请求。

```go
type Handler interface {
	ServeHTTP(ResponseWriter, *Request)
}
```

例如，

```go
// Simple counter server.
type Counter struct {
    n int
}

func (ctr *Counter) ServeHTTP(w http.ResponseWriter, req *http.Request) {
    ctr.n++
    fmt.Fprintf(w, "counter = %d\n", ctr.n)
}

ctr := new(Counter)
http.Handle("/counter", ctr)
```

其实我们还可以把一个函数作为 http handler，在 http 包中有如下的代码

```go
// The HandlerFunc type is an adapter to allow the use of
// ordinary functions as HTTP handlers.  If f is a function
// with the appropriate signature, HandlerFunc(f) is a
// Handler object that calls f.
type HandlerFunc func(ResponseWriter, *Request)

// ServeHTTP calls f(w, req).
func (f HandlerFunc) ServeHTTP(w ResponseWriter, req *Request) {
    f(w, req)
}
```

因此我们可以写出如下的代码：

```go
http.Handle("/hello", http.HandlerFunc(func(w http.ResponseWriter, req *http.Request) {
    // do somthing
}))
```
