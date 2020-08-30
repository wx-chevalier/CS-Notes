# Result

Result 类似于 Option，但是区别在于：Option 为 Some 或 None（值或无值），Result 为 Ok 或 Err（好的结果或错误结果）。因此，Option 的语义是：也许会有东西，也许不会。”但是 Result 的语义是：也许它将失败。”

```rs
enum Option<T> {
    None,
    Some(T),
}

enum Result<T, E> {
    Ok(T),
    Err(E),
}

fn main() { }
```

因此 Result 的值在 Ok 内，而值在 Err 内。这是因为错误通常（并且应该具有）内部具有信息。`Result<T, E>` 意味着您需要考虑要为 Ok 返回的内容以及要为 Err 返回的内容。实际上，您可以决定任何事情。即使这样也可以：

```rs
fn check_error() -> Result<(), ()> {
    Ok(())
}

fn main() {
    check_error();
}
```

check_error 表示“如果确定可以返回 ()，如果得到错误则返回 ()”。然后我们用 () 返回 Ok。编译器给我们一个有趣的警告：

```rs
warning: unused `std::result::Result` that must be used
 --> src\main.rs:6:5
  |
6 |     check_error();
  |     ^^^^^^^^^^^^^^
  |
  = note: `#[warn(unused_must_use)]` on by default
  = note: this `Result` may be an `Err` variant, which should be handled
```

的确如此：我们只返回了 Result，但可能是 Err。因此，即使我们仍然没有真正做任何事情，让我们稍微处理一下错误。

```rs
fn give_result(input: i32) -> Result<(), ()> {
    if input % 2 == 0 {
        return Ok(())
    } else {
        return Err(())
    }
}

fn main() {
    if give_result(5).is_ok() {
        println!("It's okay, guys")
    } else {
        println!("It's an error, guys")
    }
}

// It's an error, guys
```

请记住，四种易于检查的方法是.is_some()，is_none()，is_ok()和 is_err()。有时带有 Result 的函数将使用 String 作为 Err 值。这不是最好的方法，但是比我们到目前为止做的要好。

```rs
fn check_if_five(number: i32) -> Result<i32, String> {
    match number {
        5 => Ok(number),
        _ => Err("Sorry, the number wasn't five.".to_string()), // This is our error message
    }
}

fn main() {
    let mut result_vec = Vec::new(); // Create a new vec for the results

    for number in 2..7 {
        result_vec.push(check_if_five(number)); // push each result into the vec
    }

    println!("{:?}", result_vec);
}

[Err("Sorry, the number wasn\'t five."), Err("Sorry, the number wasn\'t five."), Err("Sorry, the number wasn\'t five."), Ok(5),
Err("Sorry, the number wasn\'t five.")]
```
