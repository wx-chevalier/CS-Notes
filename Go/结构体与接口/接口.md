# 接口

Go 的接口设计理念不同于其他语言，虽然同样是为指定对象的行为提供了一种方法，但并非某类型实现了某接口，所以需要包含某些方法；而是某类包含了某些方法，所以可以认为它是实现自某接口。

```go
// 接口声明
type Awesomizer interface {
    Awesomize() string
}

// 结构体并不需要显式实现接口
type Foo struct {}

// 而是通过实现所有接口规定的方法的方式，来实现接口
func (foo Foo) Awesomize() string {
    return "Awesome!"
}
```

# 多重实现

每种类型都能实现多个接口。例如一个实现了 sort.Interface 接口的集合就可通过 sort 包中的例程进行排序。该接口包括 Len()、Less(i, j int) bool 以及 Swap(i, j int)，另外，该集合仍然可以有一个自定义的格式化器。以下特意构建的例子 Sequence 就同时满足这两种情况。

```go
type Sequence []int

// Methods required by sort.Interface.
// sort.Interface 所需的方法。
func (s Sequence) Len() int {
    return len(s)
}
func (s Sequence) Less(i, j int) bool {
    return s[i] < s[j]
}
func (s Sequence) Swap(i, j int) {
    s[i], s[j] = s[j], s[i]
}

// Method for printing - sorts the elements before printing.
// 用于打印的方法 - 在打印前对元素进行排序。
func (s Sequence) String() string {
    sort.Sort(s)
    str := "["
    for i, elem := range s {
        if i > 0 {
            str += " "
        }
        str += fmt.Sprint(elem)
    }
    return str + "]"
}
```

# 多态性

Go 允许我们通过定义接口的方式来实现多态性：

```go
type Shape interface {
   area() float64
}

func getArea(shape Shape) float64 {
   return shape.area()
}

type Circle struct {
   x,y,radius float64
}

type Rectangle struct {
   width, height float64
}

func(circle Circle) area() float64 {
   return math.Pi * circle.radius * circle.radius
}

func(rect Rectangle) area() float64 {
   return rect.width * rect.height
}

func main() {
   circle := Circle{x:0,y:0,radius:5}
   rectangle := Rectangle {width:10, height:5}

   fmt.Printf("Circle area: %f\n",getArea(circle))
   fmt.Printf("Rectangle area: %f\n",getArea(rectangle))
}

//Circle area: 78.539816
//Rectangle area: 50.000000
```

惯用的思路是先定义接口，再定义实现，最后定义使用的方法：

```go
package animals

type Animal interface {
	Speaks() string
}

// implementation of Animal
type Dog struct{}
func (a Dog) Speaks() string { return "woof" }

/** 在需要的地方直接引用 **/
package circus

import "animals"

func Perform(a animal.Animal) { return a.Speaks() }
```

Go 也为我们提供了另一种接口的实现方案，我们可以不在具体的实现处定义接口，而是在需要用到该接口的地方，该模式为：

```go
func funcName(a INTERFACETYPE) CONCRETETYPE
```

定义接口：

```go
package animals

type Dog struct{}
func (a Dog) Speaks() string { return "woof" }

/** 在需要使用实现的地方定义接口 **/
package circus

type Speaker interface {
	Speaks() string
}

func Perform(a Speaker) { return a.Speaks() }
```
