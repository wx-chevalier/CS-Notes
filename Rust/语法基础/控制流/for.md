# for

一个 for 循环使您可以告诉 Rust 每次都要做什么。但是在 for 循环中，循环会在一定次数后停止。 for 循环经常使用范围。您使用 `..` 和 `.. =` 来创建范围。

- `..` 创建一个不包含范围： `0..3` 创建 `0, 1, 2`.
- `..=` 创建一个包含范围： `0..=3` = `0, 1, 2, 3`

```rs
fn main() {
    for number in 0..3 {
        println!("The number is: {}", number);
    }

    for number in 0..=3 {
        println!("The next number is: {}", number);
    }
}

The number is: 0
The number is: 1
The number is: 2
The next number is: 0
The next number is: 1
The next number is: 2
The next number is: 3
```

如果不需要变量名，请使用 `_`。

```rs
fn main() {
    for _ in 0..3 {
        println!("Printing the same thing three times");
    }
}
```

因为我们没有给它每次打印任何数字。实际上，如果您提供一个变量名而不使用它，Rust 会告诉您：
