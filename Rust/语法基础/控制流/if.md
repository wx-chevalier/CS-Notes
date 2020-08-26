# if

最简单的控制流程是 if。

```rs
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

您可以使用 &&（和）和||（或）添加更多条件，
