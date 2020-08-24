# 语法基础

```rs
fn main() {
    print!("Hello, world!");
}
```

print 是 Rust 标准库中定义的宏的名称。! 指定前面的名称表示宏。如果没有这样的符号，则打印将指示功能。Rust 标准库中没有此类函数，因此您会得到编译错误。宏与功能类似，它是一些与名称相关联的 Rust 代码。通过使用此名称，您要求此时插入此类代码。要了解 `;`，我们将创建另一个函数。首先，主要是打印数字 8：

```rs
fn main() {
    println!("Hello, world number {}!", 8);
}
```

println! 中的 {} 表示“将变量放入此处”。这会打印 `Hello, world number 8!`。我们可以放更多：

```rs
fn main() {
    println!("Hello, worlds number {} and {}!", 8, 9);
}
```

下面我们可以创建新的函数：

```rs
fn main() {
    println!("Hello, world number {}!", number());
}

fn number() -> i32 {
    8
}
```

函数内部只有 8，没有 `;`，所以它是返回的值。如果带有`;`，则不会返回任何内容。如果有 `;`，Rust 不会编译它，因为返回值为 i32 和 ; 返回 `()`，而不是 i32：

```rs
fn main() {
    println!("Hello, world number {}", number());
}

fn number() -> i32 {
    8;  // ⚠️
}

5 | fn number() -> i32 {
  |    ------      ^^^ expected `i32`, found `()`
  |    |
  |    implicitly returns `()` as its body has no tail or `return` expression
6 |     8;
  |      - help: consider removing this semicolon
```

这意味着“您告诉我 number() 返回一个 i32，但您添加了;，因此它不返回任何内容”。因此，编译器建议删除分号。您也可以写 `return 8;` 但是在 Rust 中，仅仅删除 `;` 回来。要为函数提供变量时，请将其放在 () 中。您必须给他们起一个名字并写下类型。

```rs
fn main() {
    multiply(8, 9); // We can give the numbers directly
    let some_number = 10; // Or we can declare two variables
    let some_other_number = 2;
    multiply(some_number, some_other_number); // and put them in the function
}

fn multiply(number_one: i32, number_two: i32) { // Two i32s will enter the function. We will call them number_one and number_two.
    let result = number_one * number_two;
    println!("{} times {} is {}", number_one, number_two, result);
}
```

当然，我们也可以返回一个 i32：

```rs
fn main() {
    let multiply_result = multiply(8, 9); // We used multiply() to print and to give the result to multiply_result
}

fn multiply(number_one: i32, number_two: i32) -> i32 {
    let result = number_one * number_two;
    println!("{} times {} is {}", number_one, number_two, result);
    result // this is the i32 that we return
}
```
