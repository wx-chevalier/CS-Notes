# Rust 基本数据类型

Rust 具有称为原始类型的简单类型。我们将从整数和 char（字符）开始。

# Copy types

Rust 中的某些类型非常简单。它们称为复制类型。这些简单类型都在堆栈上，并且编译器知道它们的大小。这意味着它们非常容易复制，因此编译器在将其发送到函数时始终会进行复制。因此，您无需担心所有权。这些简单类型包括：integers, floats, booleans (true and false), 以及 char.

```rs
fn main() {
    let my_number = 8;
    prints_number(my_number); // Prints 8. prints_number gets a copy of my_number
    prints_number(my_number); // Prints 8 again.
                              // No problem, because my_number is copy type!
}

fn prints_number(number: i32) { // No return with ->
                             // If number was not copy type, it would take it
                             // and we couldn't use it again
    println!("{}", number);
}
```
