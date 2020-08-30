# 函数

要声明一个函数，需要使用关键字`fn`，后面跟上函数名，比如

```rust
fn add_one(x: i32) -> i32 {
    x + 1
}
```

其中函数参数的类型不能省略，可以有多个参数，但是最多只能返回一个值， 提前返回使用`return`关键字。Rust 编译器会对未使用的函数提出警告， 可以使用属性`#[allow(dead_code)]`禁用无效代码检查。

Rust 有一个特殊特性适用于发散函数 (diverging function)，它不返回：

```rust
fn diverges() -> ! {
    panic!("This function never returns!");
}
```

其中`panic!`是一个宏，使当前执行线程崩溃并打印给定信息。返回类型`!`可用作任何类型：

```rust
let x: i32 = diverges();
let y: String = diverges();
```
