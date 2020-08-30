# if

If 是分支 (branch) 的一种特殊形式，也可以使用 else 和 else if。 与 C 语言不同的是，逻辑条件不需要用小括号括起来，但是条件后面必须跟一个代码块。 Rust 中的 if 是一个表达式 (expression)，可以赋给一个变量：

```rs
let x = 5;

let y = if x == 5 { 10 } else { 15 };

fn main() {
    let my_number = 5;
    if my_number == 7 {
        println!("It's seven");
    }
}
```

请注意，我们写的是 `my_number == 7` 而不是 `if(my_number == 7)`，在 Rust 中不需要使用 ()。

```rs
fn main() {
    let my_number = 5;
    if my_number == 7 {
        println!("It's seven");
    } else if my_number == 6 {
        println!("It's six")
    } else {
        println!("It's a different number")
    }
}
```

您可以使用 &&（和）和 ||（或）添加更多条件，此外，Rust 还引入了 if let 和 while let 进行模式匹配：

```rs
let number = Some(7);
let mut optional = Some(0);

// If `let` destructures `number` into `Some(i)`, evaluate the block.
if let Some(i) = number {
    println!("Matched {:?}!", i);
} else {
    println!("Didn't match a number!");
}

// While `let` destructures `optional` into `Some(i)`, evaluate the block.
while let Some(i) = optional {
    if i > 9 {
        println!("Greater than 9, quit!");
        optional = None;
    } else {
        println!("`i` is `{:?}`. Try again.", i);
        optional = Some(i + 1);
    }
}

```
