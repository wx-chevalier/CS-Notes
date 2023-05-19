# C++ 11

C++ 11 包含下面的新语言特性

- [C++ 11](#c-11)
  - [特性弃用](#特性弃用)
  - [C++11 Language Features](#c11-language-features)
    - [Move semantics](#move-semantics)
    - [Rvalue references](#rvalue-references)
    - [Forwarding references](#forwarding-references)
    - [Variadic templates](#variadic-templates)
    - [Initializer lists](#initializer-lists)
    - [Static assertions](#static-assertions)
    - [auto](#auto)
    - [Lambda expressions](#lambda-expressions)
    - [decltype](#decltype)
    - [Type aliases](#type-aliases)
    - [nullptr](#nullptr)
    - [Strongly-typed enums](#strongly-typed-enums)
    - [Attributes](#attributes)
    - [constexpr](#constexpr)
    - [Delegating constructors](#delegating-constructors)
    - [User-defined literals](#user-defined-literals)
    - [Explicit virtual overrides](#explicit-virtual-overrides)
    - [Final specifier](#final-specifier)
    - [Default functions](#default-functions)
    - [Deleted functions](#deleted-functions)
    - [Range-based for loops](#range-based-for-loops)
    - [Special member functions for move semantics](#special-member-functions-for-move-semantics)
    - [Converting constructors](#converting-constructors)
    - [Explicit conversion functions](#explicit-conversion-functions)
    - [Inline namespaces](#inline-namespaces)
    - [Non-static data member initializers](#non-static-data-member-initializers)
    - [Right angle brackets](#right-angle-brackets)
    - [Ref-qualified member functions](#ref-qualified-member-functions)
    - [Trailing return types](#trailing-return-types)
    - [Noexcept specifier](#noexcept-specifier)
  - [C++11 Library Features](#c11-library-features)
    - [std::move](#stdmove)
    - [std::forward](#stdforward)
    - [std::thread](#stdthread)
    - [std::to_string](#stdto_string)
    - [Type traits](#type-traits)
    - [Smart pointers](#smart-pointers)
    - [std::chrono](#stdchrono)
    - [Tuples](#tuples)
    - [std::tie](#stdtie)
    - [std::array](#stdarray)
    - [Unordered containers](#unordered-containers)
    - [std::make_shared](#stdmake_shared)
    - [std::ref](#stdref)
    - [Memory model](#memory-model)
    - [std::async](#stdasync)
    - [std::begin/end](#stdbeginend)
  - [Acknowledgements](#acknowledgements)

C++11 包含以下新库特性：

- [std::move](#stdmove)
- [std::forward](#stdforward)
- [std::thread](#stdthread)
- [std::to_string](#stdto_string)
- [type traits](#type-traits)
- [smart pointers](#smart-pointers)
- [std::chrono](#stdchrono)
- [tuples](#tuples)
- [std::tie](#stdtie)
- [std::array](#stdarray)
- [unordered containers](#unordered-containers)
- [std::make_shared](#stdmake_shared)
- [std::ref](#stdref)
- [memory model](#memory-model)
- [std::async](#stdasync)
- [std::begin/end](#stdbeginend)

## 特性弃用

在学习 C++ 1x 之前，我们先了解一下从 C++ 11 开始，被弃用的主要特性：

> **注意**：弃用不等于废弃，只是用于暗示程序员这些特性将从未来的标准中消失，应该尽量避免使用。但是，已弃用的特性依然是标准库的一部分，并且出于兼容性的考虑，这些特性其实会永久保留。

- **如果一个类有析构函数，为其生成拷贝构造函数和拷贝赋值运算符的特性被弃用了。**
- **不再允许字符串字面值常量赋值给一个 char \*。如果需要用字符串字面值常量赋值和初始化一个 char \*，应该使用 const char \* 或者 auto。**

```cpp
char *str = "hello world!"; // 将出现弃用警告
```

- **C++98 异常说明、unexcepted_handler、set_unexpected() 等相关特性被弃用，应该使用 noexcept。**
- **auto_ptr 被弃用，应使用 unique_ptr。**
- **register 关键字被弃用。**
- **bool 类型的 ++ 操作被弃用。**
- **C 语言风格的类型转换被弃用，应该使用 static_cast、reinterpret_cast、const_cast 来进行类型转换。**

还有一些其他诸如参数绑定（C++11 提供了 `std::bind` 和 `std::function`）、`export` 等特性也均被弃用。前面提到的这些特性**如果你从未使用或者听说过，也请不要尝试去了解他们，应该向新标准靠拢**，直接学习新特性。毕竟，技术是向前发展的。

## C++11 Language Features

### Move semantics

**移动语义**

移动一个对象意味着转移某些资源的所有权给另一个对象去管理。

移动语义的首个收益就是性能优化。

当一个对象的生命到达终点时，或临时对象，或显式调用`std::move`，“移动”是一次更轻量的资源转移方式。举个例子，“移动”一个`std::vector`仅仅拷贝一些指针和内部状态到新向量，“拷贝”需要拷贝`vector`中的每一个元素，如果这个被拷贝的`vector`马上被销毁，那么“拷贝”的代价是很高昂的且不是必要的。

移动也允许非复制类型如`std::unique_ptr` ([smart pointers](#smart-pointers)) 在语言层面保证一个实例只被一个资源所管理，然而可以在各个作用域之间传递实例。

查看相关部分: [rvalue references](#rvalue-references), [special member functions for move semantics](#special-member-functions-for-move-semantics), [`std::move`](#stdmove), [`std::forward`](#stdforward), [`forwarding references`](#forwarding-references).

### Rvalue references

**右值引用**

C++11 引入了一种新的引用被称为右值引用。使用`T&&`来创建一个非 template 类型(如`int`，或用户自定义类型)`T`的右值引用。右值引用只会绑定右值。

左值和右值的描述：

```c++
int x = 0; // `x` is an lvalue of type `int`

int& xl = x; // `xl` is an lvalue of type `int&`

int&& xr = x; // compiler error -- `x` is an lvalue

int&& xr2 = 0; // `xr2` is an lvalue of type `int&&` -- binds to the rvalue temporary, `0`
```

**注意**：这里要区分好右值和绑定右值的左值(如上述，0 是右值，但是 xr2 是左值。)

查看: [`std::move`](#stdmove), [`std::forward`](#stdforward), [`forwarding references`](#forwarding-references).

### Forwarding references

**转发引用**

也被称为万能引用。转发引用的创建方式有两种：当`T`是 template 类型参数时，使用`T&&`语法被创建；或者使用`auto&&`。这使得**完美转发**成为可能：传递参数同时维护其值类别的能力 (例如 左值还是左值, 临时变量转发为右值)。

转发引用允许一个引用既可以绑定左值类型又可以绑定右值类型。转发引用遵循**引用坍缩**规则：

- `T& &` 坍缩成 `T&`
- `T& &&` 坍缩成 `T&`
- `T&& &` 坍缩成 `T&`
- `T&& &&` 坍缩成 `T&&`

`auto` 类型推导左值和右值:

```c++
int x = 0; // `x` is an lvalue of type `int`
auto&& al = x; // `al` is an lvalue of type `int&` -- binds to the lvalue, `x`
auto&& ar = 0; // `ar` is an lvalue of type `int&&` -- binds to the rvalue temporary, `0`
```

template 类型推导左值和右值:

```c++
// Since C++14 or later:
void f(auto&& t) {
  // ...
}

// Since C++11 or later:
template <typename T>
void f(T&& t) {
  // ...
}

int x = 0;
f(0); // deduces as f(int&&)
f(x); // deduces as f(int&)

int& y = x;
f(y); // deduces as f(int& &&) => f(int&)

int&& z = 0; // NOTE: `z` is an lvalue with type `int&&`.
f(z); // deduces as f(int&& &) => f(int&)
f(std::move(z)); // deduces as f(int&& &&) => f(int&&)
```

查看: [`std::move`](#stdmove), [`std::forward`](#stdforward), [`rvalue references`](#rvalue-references).

### Variadic templates

**变参模板**

`...`语法用来创建或展开一个参数包。模板参数包是一个接受 0 个或更多模板参数。 具有至少一个参数包的模板称为可变参数模板。

```c++
template <typename... T>
struct arity {
  constexpr static int value = sizeof...(T);
};
static_assert(arity<>::value == 0);
static_assert(arity<char, short, int>::value == 3);
```

一个有趣的用途是从一个参数包创建一个初始化器列表，以便遍历可变参数。

```c++
template <typename First, typename... Args>
auto sum(const First first, const Args... args) -> decltype(first) {
  const auto values = {first, args...};
  return std::accumulate(values.begin(), values.end(), First{0});
}

sum(1, 2, 3, 4, 5); // 15
sum(1, 2, 3);       // 6
sum(1.5, 2.0, 3.7); // 7.2
```

**注意：**变参模板的用法不只这些，如`tuple`就是用变参模板实现的，其能存储不同类型的值，这里只是简单介绍它不定长的特点。

### Initializer lists

**初始化列表**

使用"初始化列表"语法创建的轻量级类似数组的元素容器。例如`{1, 2, 3}`创建了一个整数的序列，类型是`std::initializer_list<int>`。用`std::initializer_list<int>`来替换传递给函数的`vector`(或其他支持初始化列表的容器)的对象。

```c++
int sum(const std::initializer_list<int>& list) {
  int total = 0;
  for (auto& e : list) {
    total += e;
  }

  return total;
}

auto list = {1, 2, 3};
sum(list); // == 6
sum({1, 2, 3}); // == 6
sum({}); // == 0
```

### Static assertions

**静态断言**

静态断言在编译器就会被评估。

```c++
constexpr int x = 0;
constexpr int y = 1;
static_assert(x == y, "x != y");
```

### auto

`auto`-类型的变量是由编译器根据它们初始化器的类型推导出来的。

```c++
auto a = 3.14; // double
auto b = 1; // int
auto& c = b; // int&
auto d = { 0 }; // std::initializer_list<int>
auto&& e = 1; // int&&
auto&& f = b; // int&
auto g = new auto(123); // int*
const auto h = 1; // const int
auto i = 1, j = 2, k = 3; // int, int, int
auto l = 1, m = true, n = 1.61; // error -- `l` deduced to be int, `m` is bool
auto o; // error -- `o` requires initializer
```

`auto`对可读性及其有益，尤其是特别复杂的类型。

```c++
std::vector<int> v = ...;
std::vector<int>::const_iterator cit = v.cbegin();
// vs.
auto cit = v.cbegin();
```

函数也可以使用`auto`来进行返回类型的推导。在 C++11 中，返回类型必须显示指定，或者使用`decltype`。

```c++
template <typename X, typename Y>
auto add(X x, Y y) -> decltype(x + y) {
  return x + y;
}
add(1, 2); // == 3
add(1, 2.0); // == 3.0
add(1.5, 1.5); // == 3.0
```

上面例子中的后置返回类型是表达式`x + y`所声明的类型。举个例子，如果`x`是个整形，`y`是 double 类型，`decltype(x + y)`是 double。因此，上面函数将依据表达式`x + y`的类型来推导返回类型。

注意：后置返回类型只能访问函数参数，也可调用参数的`this`相关功能

### Lambda expressions

**lambda 表达式**

`lambda` 是一个可以在当前作用域中捕捉变量的无名函数对象。

它由五部分组成：捕捉列表，参数列表，函数属性，后置返回类型，函数体。其中 2，3，4 部分可以省略

一个完整的例子如下：

```cpp
auto func = []()mutable -> int { return 1; };
// [] 为捕捉列表
// ()为参数列表，当空可省略
// mutable,表示按值传递捕捉列表，可以修改捕捉列表中的变量，但不会影响外部变量,当空可省略。当其存在时，参数列表必须也存在。
// -> int 后置返回类型，如果是 void 可省略。
// { return 1; }; 函数体
```

捕捉列表如下:

- `[]` - 什么也不捕捉，表示函数完全用不到当前作用域的其他变量
- `[=]` - 捕捉当前作用域内函数定义之前的所有局部变量(如果当前所处在一个函数中，那么参数也算)，但是以值传递来使用。
- `[&]` - 作用同上，但是捕捉变量以引用形式进行传递。
- `[this]` - 捕获当前 lambda 所在的对象的 this 指针。
- `[a]` - 将 a 按照值进行传递。
- `[&a]` - 将 a 按照引用传递。
- `[a, &b]` - 将 a 按值传递，b 按引用传递
- `[=，&a，&b]` - 除 a 和 b 按引用进行传递外，其他参数都按值进行传递。
- `[&，a，b]` - 除 a 和 b 按值进行传递外，其他参数都按引用进行传递。

注意：捕捉的范围是当前作用域内 lambda 表达式定义之前所声明的变量。不包括常量和全局变量(这两项不捕捉也可以访问)

```c++
int x = 1;

auto getX = [=] { return x; };
getX(); // == 1

auto addX = [=](int y) { return x + y; };
addX(1); // == 2

auto getXRef = [&]() -> int& { return x; };
getXRef(); // int& to `x`
```

默认情况下，按值捕捉的变量在 lambda 中不可以被修改，因为编译器在生成时将捕捉变量标记成了`const`，`mutable`关键字允许修改捕捉变量。关键字被置在参数列表后面(即使参数列表是空也要为`mutable`而存在)。

```c++
int x = 1;

auto f1 = [&x] { x = 2; }; // OK: x is a reference and modifies the original

auto f2 = [x] { x = 2; }; // ERROR: the lambda can only perform const-operations on the captured value
// vs.
auto f3 = [x]() mutable { x = 2; }; // OK: the lambda can perform any operations on the captured value
```

### decltype

`decltype`是一个用来返回表达式声明类型的操作，其也会维护表达式中的**cv 限定符**和**引用**。

`decltype`例子:

```c++
int a = 1; // `a` is declared as type `int`
decltype(a) b = a; // `decltype(a)` is `int`
const int& c = a; // `c` is declared as type `const int&`
decltype(c) d = a; // `decltype(c)` is `const int&`
decltype(123) e = 123; // `decltype(123)` is `int`
int&& f = 1; // `f` is declared as type `int&&`
decltype(f) g = 1; // `decltype(f) is `int&&`
decltype((a)) h = g; // `decltype((a))` is int&
```

```c++
template <typename X, typename Y>
auto add(X x, Y y) -> decltype(x + y) {
  return x + y;
}
add(1, 2.0); // `decltype(x + y)` => `decltype(3.0)` => `double`
```

**注意**：decltype 的细节比较多，如上述例子中的`decltype(a)`和`decltype((a))`，很容易定义错类型，建议**只在模板函数**的返回值中使用。

相关查看: `decltype(auto)` (C++14).

### Type aliases

**类型别名**

在语义上和`typedef`相近，但是`using`更为简洁，且兼容模板。

```c++
template <typename T>
using Vec = std::vector<T>;
Vec<int> v; // std::vector<int>

using String = std::string;
String s {"foo"};
```

### nullptr

**空指针**

C++11 引入了一种全新的空指针类型用来替代 C 语言中的`NUll`宏。`nullptr`的类型是`std::nullptr_t`,可以隐式的转成指针类型、`bool`类型。但是`nullptr`不可以隐式的转成整形，这是`nullptr`和`NULL`的最大不同，解决了`NULL`在特定情况下的调用歧义问题。

```c++
void foo(int);
void foo(char*);
foo(NULL); // error -- ambiguous
foo(nullptr); // calls foo(char*)
```

### Strongly-typed enums

**强类型枚举**

类型安全枚举解决了 C 格式枚举中的一系列问题：隐式转换，不能指定基础类型，作用域污染。

```c++
// Specifying underlying type as `unsigned int`
enum class Color : unsigned int { Red = 0xff0000, Green = 0xff00, Blue = 0xff };
// `Red`/`Green` in `Alert` don't conflict with `Color`
enum class Alert : bool { Red, Green };
Color c = Color::Red;
```

### Attributes

Attributes 为 `__attribute__(...)`, `__declspec`等属性提供了通用语法。

```c++
// `noreturn` attribute indicates `f` doesn't return.
[[ noreturn ]] void f() {
  throw "error";
}
```

**注意：**个人没咋用过，感觉即使能优化也有限，尽量少用。

### constexpr

**常量表达式**

常量表达式是编译器在编译时计算的表达式。在常量表达式中，只有简单的计算才可以被执行。使用`constexpr`来将一个变量，函数等指定为常量表达式。

```c++
constexpr int square(int x) {
  return x * x;
}

int square2(int x) {
  return x * x;
}

int a = square(2);  // mov DWORD PTR [rbp-4], 4

int b = square2(2); // mov edi, 2
                    // call square2(int)
                    // mov DWORD PTR [rbp-8], eax
```

**注意：**当函数被标为 square，但是参数传进来的不是常量表达式(在编译期不能得到值)，那么函数还是会像正常函数一样被初始化，然后调用函数等操作。

`constexpr` 变量要在编译期就被计算出来:

```c++
const int x = 123;
constexpr const int& y = x; // error -- constexpr variable `y` must be initialized by a constant expression
```

类使用常量表达式:

```c++
struct Complex {
  constexpr Complex(double r, double i) : re{r}, im{i} { }
  constexpr double real() { return re; }
  constexpr double imag() { return im; }

private:
  double re;
  double im;
};

constexpr Complex I(0, 1);
```

**注意：**个人在开发中目前还没见过类对象使用常量表达式，在编译期就能确定的类对象比较少，在一个为了一点优化舍弃可读性，感觉不值当。

### Delegating constructors

**委托构造**

构造函数可以使用初始化列表来调用当前类中其他的构造函数。

```c++
struct Foo {
  int foo;
  Foo(int foo) : foo{foo} {}
  Foo() : Foo(0) {}
};

Foo foo;
foo.foo; // == 0
```

### User-defined literals

**用户自定义字面值**

用户自定义字面值允许你扩展语言，添加你自己的语法。定义一个`T operator "" X(...) { ... }`，返回值是`T`类型，名字是`X`的函数。名字`X`就是你要使用的字面值。`X`要以下划线起始，否则不会被调用。函数的参数类型有以下几个选项：`unsigned long long`，`long double`，`char`，`wchar_t`，`char16_t`，`char32_t`，`const char *`

例子 1:

```c++
// `unsigned long long` parameter required for integer literal.
long long operator "" _celsius(unsigned long long tempCelsius) {
  return std::llround(tempCelsius * 1.8 + 32);
}
24_celsius; // == 75
```

例子 2:

```c++
// `const char*` and `std::size_t` required as parameters.
int operator "" _int(const char* str, std::size_t) {
  return std::stoi(str);
}

"123"_int; // == 123, with type `int`

// 作用同上
int operator "" _int(const char* str) {
  return std::stoi(str);
}

123_int; // == 123, with type `int`
```

### Explicit virtual overrides

**显式虚函数重写**

指定一个虚函数重写于另一个虚函数。使用`override`但是虚函数并没有重写基类虚函数的话，会提示编译错误。

```c++
struct A {
  virtual void foo();
  void bar();
};

struct B : A {
  void foo() override; // correct -- B::foo overrides A::foo
  void bar() override; // error -- A::bar is not virtual
  void baz() override; // error -- B::baz does not override A::baz
};
```

### Final specifier

**Final 指示符**

指定当前虚函数不可以在派生类中被重写，或指定当前类不可被继承。

虚函数不可被重写：

```c++
struct A {
  virtual void foo();
};

struct B : A {
  virtual void foo() final;
};

struct C : B {
  virtual void foo(); // error -- declaration of 'foo' overrides a 'final' function
};
```

类不可被继承：

```c++
struct A final {};
struct B : A {}; // error -- base 'A' is marked 'final'
```

### Default functions

**默认函数**

提供一种更优雅，更有效的函数的默认实现方式，比如构造函数：

```c++
struct A {
  A() = default;
  A(int x) : x{x} {}
  int x {1};
};
A a; // a.x == 1
A a2 {123}; // a.x == 123
```

继承:

```c++
struct B {
  B() : x{1} {}
  int x;
};

struct C : B {
  // Calls B::B
  C() = default;
};

C c; // c.x == 1
```

### Deleted functions

**删除函数**

提供一种更优雅，更有效的函数的删除实现方式。如阻止对象的拷贝：

```c++
class A {
  int x;

public:
  A(int x) : x{x} {};
  A(const A&) = delete;
  A& operator=(const A&) = delete;
};

A x {123};
A y = x; // error -- call to deleted copy constructor
y = x; // error -- operator= deleted
```

### Range-based for loops

可迭代容器的循环语法糖。

```c++
std::array<int, 5> a {1, 2, 3, 4, 5};
for (int& x : a) x *= 2;
// a == { 2, 4, 6, 8, 10 }
```

注意`int`和`int&`的区别。

```c++
std::array<int, 5> a {1, 2, 3, 4, 5};
for (int x : a) x *= 2;
// a == { 1, 2, 3, 4, 5 }
```

### Special member functions for move semantics

**移动构造函数**

当复制操作发生时，拷贝构造函数和拷贝赋值运算符会被调用。C++11 引入了右值语义，所以支持了移动构造和移动赋值运算符。

```c++
struct A {
  std::string s;
  A() : s{"test"} {}
  A(const A& o) : s{o.s} {}
  A(A&& o) : s{std::move(o.s)} {}
  A& operator=(A&& o) {
   s = std::move(o.s);
   return *this;
  }
};

A f(A a) {
  return a;
}

A a1 = f(A{}); // move-constructed from rvalue temporary
A a2 = std::move(a1); // move-constructed using std::move
A a3 = A{};
a2 = std::move(a3); // move-assignment using std::move
a1 = f(A{}); // move-assignment from rvalue temporary
```

**注意：**只要注意到确实调用了移动函数就可以，不要细纠结调用顺序，层次等，编译器添加了许多开发者不知的编译优化如 RVO，NRVO，掺在一起，迷糊的很。

### Converting constructors

**转换构造**(我觉得叫统一构造比较好)

C++11 统一了初始化方式，可以用`{}`来初始化所有对象。

编译器会构造一个类似`tuple`的容器，然后看当前类是否带有初始化列表的构造函数，如果有且数据类型能对应上，那么直接调用:

```cpp
struct A {
    A(int, int) { cout << "ii" << endl; }
    A(initializer_list<int> il) { cout << "il" << endl; }
};
A a {0, 0}; // calls A::A(initializer_list<int> il)
A b (0, 0); // calls A::A(int, int)
```

如果对应不上的类型都是由精度缺失引起的(如传入的是`double`但初始化列表类型是`int`)，那么程序会报错；

```c++
struct A {
    A(int, double) { cout << "id" << endl; }
    A(initializer_list<int> il) { cout << "il" << endl; }
};
A a {0, 0.5}; // error! 'double' cannot be narrowed to 'int'
A b (0, 0.5); // calls A::A(int, double)
```

如果对应不上的类型完全不一致(如初始化列表是`int`但传入的`string`) 或 当前类没有初始化列表构造函数，再调用相应的构造函数。

```c++
struct A {
    A(int, double, string) { cout << "ids" << endl; }
    A(initializer_list<int>) { cout << "il" << endl; }
};

A a {0, 0.5, "abc"}; // calls A::A(int, double, string)
A b {1, 2, 3}; // calls A::A(initializer_list<int>)
```

**注意：**任何时刻大括号中的参数传入时都不允许精度缺失。

```c++
struct A {
  A(int) {}
};
A a(1.1); // OK
A b {1.1}; // Error narrowing conversion from double to int
```

### Explicit conversion functions

**显式转换函数**

转换函数可以通过`explicit`标识符来置位显式声明，禁止隐式转换。

```c++
struct A {
  operator bool() const { return true; }
};

struct B {
  explicit operator bool() const { return true; }
};

A a;
if (a); // OK calls A::operator bool()
bool ba = a; // OK copy-initialization selects A::operator bool()

B b;
if (b); // OK calls B::operator bool()
bool bb = b; // error copy-initialization does not consider B::operator bool()
bool bbb = bool(b);	// OK
```

### Inline namespaces

**内联命名空间**

将内联名称空间的所有成员视为其父名称空间的一部分，从而允许函数的专门化并简化版本控制过程。 这是一个传递属性，如果 a 包含 b，而 b 又包含 c，而 b 和 c 都是内联名称空间，那么 c 的成员就可以像在 a 上一样使用。

将内联命名空间的所有成员视为它们是在其父亲的命名空间下，从而使函数专有化(只属于某个作用域)并简化版本控制过程(将当前版本函数的命名空间设置成 inline)。这个属性是可传递的，如果 A 包含 B，B 又包含 C，而 B 和 C 都被声明成了内联，那么 A 可以直接使用 C 中的成员。

```c++
namespace Program {
  namespace Version1 {
    int getVersion() { return 1; }
    bool isFirstVersion() { return true; }
  }
  inline namespace Version2 {
    int getVersion() { return 2; }
  }
}

int version {Program::getVersion()};              // Uses getVersion() from Version2
int oldVersion {Program::Version1::getVersion()}; // Uses getVersion() from Version1
bool firstVersion {Program::isFirstVersion()};    // Does not compile when Version2 is added
```

### Non-static data member initializers

允许在声明非静态数据成员的地方初始化它们，但构造函数中重复赋值会覆盖他们。

```c++
// C++11 之前
class Human {
    Human() : age{0} {}
  private:
    unsigned age;
};
// C++11 及之后
class Human {
  private:
    unsigned age {0};
};
```

### Right angle brackets

**右角括号**

从 C++11 开始，允许多个右角括号连在一起，不需要添加空白格。

```c++
typedef std::map<int, std::map <int, std::map <int, int> > > cpp98LongTypedef;
typedef std::map<int, std::map <int, std::map <int, int>>>   cpp11LongTypedef;
```

### Ref-qualified member functions

**引用限定成员函数**

相同的成员函数函数，现在可以根据`*this`是左值还是右值引用来调用不同的重载。

```c++
struct Bar {
  // ...
};

struct Foo {
  Bar getBar() & { return bar; }
  Bar getBar() const& { return bar; }
  Bar getBar() && { return std::move(bar); }
private:
  Bar bar;
};

Foo foo{};
Bar bar = foo.getBar(); // calls `Bar getBar() &`

const Foo foo2{};
Bar bar2 = foo2.getBar(); // calls `Bar Foo::getBar() const&`

Foo{}.getBar(); // calls `Bar Foo::getBar() &&`
std::move(foo).getBar(); // calls `Bar Foo::getBar() &&`
std::move(foo2).getBar(); // calls `Bar Foo::getBar() const&&`
```

### Trailing return types

**后置返回类型**

C++11 中函数或 lambda 表达式定义返回类型的新语法格式。

```c++
int f() {
  return 123;
}
// vs.
auto f() -> int {
  return 123;
}
```

```c++
auto g = []() -> int {
  return 123;
};
```

当返回类型不能被立即确定时，这个特点会非常有用。

```c++
// NOTE: This does not compile!
template <typename T, typename U>
decltype(a + b) add(T a, U b) {
    return a + b;
}

// Trailing return types allows this:
template <typename T, typename U>
auto add(T a, U b) -> decltype(a + b) {
    return a + b;
}
```

在 C++14 中，可以使用`decltype(auto)`来替代。

### Noexcept specifier

**无异常说明符**

`noexcept` 说明符指定一个函数是否会返回异常. 它是 `throw()`的进阶版。

```c++
void func1() noexcept;        // does not throw，一般用这个就够了

void func2() noexcept(true);  // does not throw
void func3() throw();         // does not throw

void func4() noexcept(false); // may throw
```

Non-throwing 函数被允许去调用存在异常的函数。当一个异常被抛出，寻找处理异常的 handler 时，当递归到一个 Non-throwing 函数时，函数直接回调用`std::terminate`来结束程序。

```c++
extern void f();  // 可能会有异常
void g() noexcept {
    f();          // 合法，即使f可能有异常
    throw 42;     // 合法, 调用std::terminate
}
```

## C++11 Library Features

### std::move

`std::move`代表对象在传入的时候有可能会有资源传输。当使用调用过`std::move`的对象的时候应该小心，因为他的某些值可能已经处于一个未知的状态。

`std::move`定义：

```c++
template <typename T>
typename remove_reference<T>::type&&
move(T&& arg) {
  // remove_reference 作用是去除T类型的引用
  return static_cast<typename remove_reference<T>::type&&>(arg);
}
```

`std::unique_ptr`中的使用：

```c++
std::unique_ptr<int> p1 {new int{0}};  // in practice, use std::make_unique
std::unique_ptr<int> p2 = p1; // error -- cannot copy unique pointers
std::unique_ptr<int> p3 = std::move(p1); // move `p1` into `p3`
                                         // now unsafe to dereference object held by `p1`
```

### std::forward

返回的结果的值和传递给它的参数的值相同，同时维护了参数的值种类(左右值)和`cv-限定符`。对万能模板引用代码很有用。一般与 [`forwarding references`](#forwarding-references)连用。

`std::forward`的定义:

```c++
template <typename T> // 当传入是左值时，调用此函数，但传入的左值可能是具名右值，
											// 所以返回结果要看传入的T的具体类型而定。
T&& forward(typename remove_reference<T>::type& arg) noexcept {
  return static_cast<T&&>(arg);
}

template <typename T> // 当传入的是纯右值时，调用此函数
T&& forward(typename remove_reference<T>::type&& arg) noexcept {
    static_assert(!is_lvalue_reference<T>::value,	// 如果T的类型是左值引用，报错
                  "can not forward an rvalue as an lvalue");
    return static_cast<T&&>(arg);
}
```

例子： `wrapper`函数仅用传来的 A 对象通过拷贝或移动的方式生成另一个 A 对象

```c++
struct A {
  A() = default;
  A(const A& o) { std::cout << "copied" << std::endl; }
  A(A&& o) { std::cout << "moved" << std::endl; }
};

template <typename T>
A wrapper(T&& arg) {
  return A{std::forward<T>(arg)};
}

wrapper(A{}); // moved
A a;
wrapper(a); // copied
wrapper(std::move(a)); // moved
```

**注意：**`std::forward`作用主要有两个，一是将具名右值在传递时继续按照右值传递下去，二是在万能模板引用中自动推导左右值。

See also: [`forwarding references`](#forwarding-references), [`rvalue references`](#rvalue-references).

### std::thread

`std::thread`库提供了一种标准方式去控制线程，如生成线程或杀死线程。下面的例子，生成多个线程去做不同的计算然后程序等待所有线程结束。

```c++
void foo(bool clause) { /* do something... */ }

std::vector<std::thread> threadsVector;
threadsVector.emplace_back([]() {
  // Lambda function that will be invoked
});
threadsVector.emplace_back(foo, true);  // thread will run foo(true)
for (auto& thread : threadsVector) {
  thread.join(); // Wait for threads to finish
}
```

### std::to_string

转换一个数学类型到字符串

```c++
std::to_string(1.2); // == "1.2"
std::to_string(123); // == "123"
```

### Type traits

Type traits 定义了一个基于编译时模板的接口，用于查询或修改类型的属性。如类型是否相同，类型是否是左值，右值等。

```c++
static_assert(std::is_integral<int>::value);
static_assert(std::is_same<int, int>::value);
static_assert(std::is_same<std::conditional<true, int, double>::type, int>::value);
static_assert(is_lvalue_reference<_Tp>::value);
static_assert(is_rvalue_reference<_Tp>::value);
```

### Smart pointers

C++11 引入了新的智能指针：`std::unique_ptr`, `std::shared_ptr`, `std::weak_ptr`。 `std::auto_ptr` 在 C++17 中已经被移除。

`std::unique_ptr`在管理其指向的堆内存对象时，要求对象不可复制，仅可移动。

**注意：**相对于使用对象的构造函数，我们更倾向于用`std::make_X`来构造智能指针。

查看 [`std::make_unique`](#stdmake_unique) and [`std::make_shared`](#stdmake_shared).

```c++
std::unique_ptr<Foo> p1 { new Foo{} };  // `p1` owns `Foo`
if (p1) {
  p1->bar();
}

{
  std::unique_ptr<Foo> p2 {std::move(p1)};  // Now `p2` owns `Foo`
  f(*p2);

  p1 = std::move(p2);  // Ownership returns to `p1` -- `p2` gets destroyed
}

if (p1) {
  p1->bar();
}
// `Foo` instance is destroyed when `p1` goes out of scope
```

`std::shared_ptr`是一个智能指针，它管理跨多个所有者共享的资源。共享指针持有一个控制块，该控制块具有一些组件，如托管对象和引用计数器。 所有控制块访问都是线程安全的，但是操作托管对象本身是非线程安全的。

```c++
// p1在复制给参数时是线程安全的，保证计数正确，但是在操作对象时不是线程安全的
void foo(std::shared_ptr<T> t) {
  // Do something with `t`...
}

void bar(std::shared_ptr<T> t) {
  // Do something with `t`...
}

void baz(std::shared_ptr<T> t) {
  // Do something with `t`...
}

std::shared_ptr<T> p1 {new T{}};
// Perhaps these take place in another threads?
foo(p1);
bar(p1);
baz(p1);
```

### std::chrono

`chrono`库包含一组实用函数和类型，用于处理持续时间、时钟和时间点。下面用例是程序运行时间测试代码:

```c++
std::chrono::time_point<std::chrono::steady_clock> start, end;
start = std::chrono::steady_clock::now();
// Some computations...
end = std::chrono::steady_clock::now();

std::chrono::duration<double> elapsed_seconds = end - start;
double t = elapsed_seconds.count(); // t number of seconds, represented as a `double`
```

### Tuples

元组是用来存储不同类型值得集合。解析元组用`std::tie`或`std::get`

```c++
// `playerProfile` has type `std::tuple<int, const char*, const char*>`.
auto playerProfile = std::make_tuple(51, "Frans Nielsen", "NYI");
std::get<0>(playerProfile); // 51
std::get<1>(playerProfile); // "Frans Nielsen"
std::get<2>(playerProfile); // "NYI"
```

**个人理解：**tuple 是根据 Variadic templates 生成的，但是这块看的有点蒙，元组用的也比较少，先搁置吧..

### std::tie

返回一个左值引用的 tuple，对解析`std::pair`和`std::tuple`很有用，用`std::ignore`占位符忽略某个值。

```c++
// With tuples...
std::string playerName;
std::tie(std::ignore, playerName, std::ignore) = std::make_tuple(91, "John Tavares", "NYI");

// With pairs...
std::string yes, no;
std::tie(yes, no) = std::make_pair("yes", "no");
```

### std::array

`std::array`是一个基于 C 样式数组 构建的容器。支持常见的容器操作，如排序。

```c++
std::array<int, 3> a = {2, 1, 3};
std::sort(a.begin(), a.end()); // a == { 1, 2, 3 }
for (int& x : a) x *= 2; // a == { 2, 4, 6 }
```

### Unordered containers

这些容器操作如插入，删除，查找的平均时间复杂度是常数时间级别。容器为了使操作能达到常数级别的时间复杂度，将元素的存储在桶中，而不是以往的红黑树，所以牺牲了排序的功能。有四种不排序容器：

- `unordered_set`
- `unordered_multiset`
- `unordered_map`
- `unordered_multimap`

### std::make_shared

推荐使用`std::make_shared`来构建`std::shared_ptr`的实例的原因如下：

- 避免使用`new`运算符
- Prevents code repetition when specifying the underlying type the pointer shall hold.(译者没懂要表达什么意思，防止代码重复，但我没感觉到有这个意义)
- It provides exception-safety. Suppose we were calling a function `foo` like so:

```c++
foo(std::shared_ptr<T>{new T{}}, function_that_throws(), std::shared_ptr<T>{new T{}});
```

The compiler is free to call `new T{}`, then `function_that_throws()`, and so on... Since we have allocated data on the heap in the first construction of a `T`, we have introduced a leak here. With `std::make_shared`, we are given exception-safety:

```c++
foo(std::make_shared<T>(), function_that_throws(), std::make_shared<T>());
```

**上述是原文，个人认为描述的跟我理解的有出入，以下是个人理解。**

保证异常安全。假设我们现在在调用`foo`函数：

```c++
foo(std::shared_ptr<T>{new T{}}, function_that_throws());
```

编译器在调用函数前会先将参数处理完成。需要处理的参数 1.`new T{}`，2.`std::share_ptr<T>`构造，3.调用`function_that_throws`函数。但是编译器的处理顺序不是确定的，当执行顺序为 1，3，2 时，3 函数调用时发生了异常，那么 1 中 new 的内存将泄漏。改成如下：

```c++
foo(std::make_shared<T>(), function_that_throws());
```

- `std::shared_ptr{ new T{} }`再调用时，除了给`T`分配内存，还要再给`shared_ptr`中的控制块分配内存，一共分配了两次，而使用`std::make_shared`来构造时，会一次性分配好两者的内存。只会进行一次内存动态分配。

### std::ref

`std::ref(val)` 通过生成一个`std::reference_wrapper`来保证参数传递时按照引用传递。通常用来显式指定传递参数为引用传递，或指定容器中保存的类型是某种类型的引用。

```c++
// create a container to store reference of objects.
auto val = 99;
auto _ref = std::ref(val);
_ref++;
auto _cref = std::cref(val);
//_cref++; does not compile
std::vector<std::reference_wrapper<int>>vec; // vector<int&>vec does not compile
vec.push_back(_ref); // vec.push_back(&i) does not compile
cout << val << endl; // prints 100
cout << vec[0] << endl; // prints 100
cout << _cref; // prints 100
```

### Memory model

C++11 引入了一种内存模型，这意味着库支持线程和原子操作。其中一些操作包括(但不限于)`atomic loads/stores`、`compare-and-swap`、`atomic flags`、`promises`、`futures`、`locks`和`condition variables`。

查看相关部分: [std::thread](#stdthread)

### std::async

创建一个后台异步线程来执行任务，返回一个`std::future`对象来存储函数调用返回的结果。

可以手动的选择异步线程的执行方式，将下面执行方式置为`std::async`的第一个参数即可：

1. `std::launch::async` 在后台线程中执行可调用对象。当返回的 future 失效前会强制执行完成可调用对象，即不调用 future.get 也会保证任务的执行。
1. `std::launch::deferred` 仅当调用 future.get 时才会执行任务。
1. `std::launch::async | std::launch::deferred` 如果创建 async 时不指定 launch policy，他会默认在二者中选一种方式进行执行。

```c++
int foo() {
  /* Do something here, then return the result. */
  return 1000;
}

auto handle = std::async(std::launch::async, foo);  // create an async task
auto result = handle.get();  // wait for the result
```

### std::begin/end

一般而言，`std::begin` and `std::end` 用来返回容器的首迭代器和尾迭代器，且同样作用于 C 格式数组。

```c++
template <typename T>
int CountTwos(const T& container) {
  return std::count_if(std::begin(container), std::end(container), [](int item) {
    return item == 2;
  });
}

std::vector<int> vec = {2, 2, 43, 435, 4543, 534};
int arr[8] = {2, 43, 45, 435, 32, 32, 32, 32};
auto a = CountTwos(vec); // 2
auto b = CountTwos(arr);  // 1
```

## Acknowledgements

- [cppreference](http://en.cppreference.com/w/cpp) - 其中有很好的例子，特性和解释。
- [C++ Rvalue References Explained](http://thbecker.net/articles/rvalue_references/section_01.html) - 一系列非常好的右值引用，完美转发， 移动语义文章。
- [clang](http://clang.llvm.org/cxx_status.html) 和 [gcc](https://gcc.gnu.org/projects/cxx-status.html) 的标准支持标准页面。
- [Compiler explorer](https://godbolt.org/) 一个由源码转到汇编代码的网页，(译者：**无敌好用**)。
- [Scott Meyers' Effective Modern C++](https://www.amazon.com/Effective-Modern-Specific-Ways-Improve/dp/1491903996) - 特别推荐的书籍
- [Jason Turner's C++ Weekly](https://www.youtube.com/channel/UCxHAlbZQNFU2LgEtiqd2Maw) - C++相关视频
- [What can I do with a moved-from object?](http://stackoverflow.com/questions/7027523/what-can-i-do-with-a-moved-from-object) 被以右值引用调用过的对象还能用来做什么
- [What are some uses of decltype(auto)?](http://stackoverflow.com/questions/24109737/what-are-some-uses-of-decltypeauto) decltype(auto)的用途都有啥
- 还有很多帖子，但是我忘记了...
