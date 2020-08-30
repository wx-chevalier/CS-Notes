# for

一个 for 循环使您可以告诉 Rust 每次都要做什么。for 语句用于遍历一个迭代器。

```rs
for var in iterator {
    code
}
```

# 范围遍历

在 for 循环中，循环会在一定次数后停止。for 循环经常使用范围。您使用 `..` 和 `.. =` 来创建范围。

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

因为我们没有给它每次打印任何数字。实际上，如果您提供一个变量名而不使用它，Rust 会给您提出警告：

```rs
fn main() {
    for number in 0..3 {
        println!("Printing the same thing three times");
    }
}

warning: unused variable: `number`
 --> src\main.rs:2:9
  |
2 |     for number in 0..3 {
  |         ^^^^^^ help: if this is intentional, prefix it with an underscore: `_number`
```

# 数组遍历

现在我们知道了如何使用循环，这是一种更好的解决方案，可以解决来自迷惑颜色的匹配问题。这是一个更好的解决方案，因为我们想比较所有内容，并且 for 循环查看每个项目。

```rs
fn match_colours(rbg: (i32, i32, i32)) {
    println!("Comparing a colour with {} red, {} blue, and {} green:", rbg.0, rbg.1, rbg.2);
    let new_vec = vec![(rbg.0, "red"), (rbg.1, "blue"), (rbg.2, "green")]; // Put the colours in a vec. Inside are tuples with the colour names
    let mut all_have_at_least_10 = true; // Start with true. We will set it to false if one colour is less than 10
    for item in new_vec {
        if item.0 < 10 {
            all_have_at_least_10 = false; // Now it's false
            println!("Not much {}.", item.1) // And we print the colour name.
        }
    }
    if all_have_at_least_10 { // Check if it's still true, and print if true
        println!("Each colour has at least 10.")
    }
    println!(); // Add one more line
}

fn main() {
    let first = (200, 0, 0);
    let second = (50, 50, 50);
    let third = (200, 50, 0);

    match_colours(first);
    match_colours(second);
    match_colours(third);
}

Comparing a colour with 200 red, 0 blue, and 0 green:
Not much blue.
Not much green.

Comparing a colour with 50 red, 50 blue, and 50 green:
Each colour has at least 10.

Comparing a colour with 200 red, 50 blue, and 0 green:
Not much green.
```
