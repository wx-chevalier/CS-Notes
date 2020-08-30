# Option

我们现在了解枚举和泛型，因此我们可以了解选项和结果。Rust 使用这两个枚举类型来使代码更安全。当您拥有一个可能存在或不存在的值时，请使用 Option。当值存在时为 `Some(value)`，当值不存在时仅为 None，这是一个错误代码示例，可以使用 Option 进行改进。

```rs
    // ⚠️
fn take_fifth(value: Vec<i32>) -> i32 {
    value[4]
}

fn main() {
    let new_vec = vec![1, 2];
    let index = take_fifth(new_vec);
}

// thread 'main' panicked at 'index out of bounds: the len is 2 but the index is 4', src\main.rs:34:5
```

Panic 表示程序在问题发生之前已停止。 Rust 看到该函数想要一些不可能的事情，然后停止。它“展开堆栈”（从堆栈中取出值），并告诉您“对不起，我不能这样做”。所以现在我们将返回类型从 i32 更改为`Option<i32>`。这意味着“如果有，请给我 Some（i32），如果没有，请给我任何内容”。我们说 i32 是“包装”在 Option 中的，这意味着它在 Option 内部。您必须做一些事情才能获得价值。

```rs
fn take_fifth(value: Vec<i32>) -> Option<i32> {
    if value.len() < 5 { // .len() gives the length of the vec.
                         // It must be at least 5.
        None
    } else {
        Some(value[4])
    }
}

fn main() {
    let new_vec = vec![1, 2];
    let bigger_vec = vec![1, 2, 3, 4, 5];
    println!("{:?}, {:?}", take_fifth(new_vec), take_fifth(bigger_vec));
}

None, Some(5)
```

# unwrap

我们可以使用 `.unwrap()` 获取选项中的值，但使用 `.unwrap()` 时要小心。就像打开礼物一样：也许里面好东西，或者里面有一条愤怒的蛇。如果您确定的话，只想 `.unwrap()` 即可。如果解开的值是 None，则程序将崩溃。

```rs
    // ⚠️
fn take_fifth(value: Vec<i32>) -> Option<i32> {
    if value.len() < 4 {
        None
    } else {
        Some(value[4])
    }
}

fn main() {
    let new_vec = vec![1, 2];
    let bigger_vec = vec![1, 2, 3, 4, 5];
    println!("{:?}, {:?}",
        take_fifth(new_vec).unwrap(), // this one is None. .unwrap() will panic!
        take_fifth(bigger_vec).unwrap()
    );
}
```

不过我们并不需要 unwrap，可以使用 match 来进行值匹配：

```rs
fn take_fifth(value: Vec<i32>) -> Option<i32> {
    if value.len() < 4 {
        None
    } else {
        Some(value[4])
    }
}

fn handle_option(my_option: Vec<Option<i32>>) {
  for item in my_option {
    match item {
      Some(number) => println!("Found a {}!", number),
      None => println!("Found a None!"),
    }
  }
}

fn main() {
    let new_vec = vec![1, 2];
    let bigger_vec = vec![1, 2, 3, 4, 5];
    let mut option_vec = Vec::new(); // Make a new vec to hold our options
                                     // The vec is type: Vec<Option<i32>>. That means a vec of Option<i32>.

    option_vec.push(take_fifth(new_vec)); // This pushes "None" into the vec
    option_vec.push(take_fifth(bigger_vec)); // This pushes "Some(5)" into the vec

    handle_option(option_vec); // handle_option looks at every option in the vec.
                               // It prints the value if it is Some. It doesn't touch it if it is None.
}

Found a None!
Found a 5!
```

当然，我们也可以直接去判断 Option 的属性，譬如它提供了 .is_some() 方法来判断是否为 Some 类型，包括 .is_none() 来判断是否为 None 类型：

```rs
fn take_fifth(value: Vec<i32>) -> Option<i32> {
    if value.len() < 4 {
        None
    } else {
        Some(value[4])
    }
}

fn main() {
    let new_vec = vec![1, 2];
    let bigger_vec = vec![1, 2, 3, 4, 5];
    let vec_of_vecs = vec![new_vec, bigger_vec];
    for vec in vec_of_vecs {
        let inside_number = take_fifth(vec);
        if inside_number.is_some() {
            // .is_some() returns true if we get Some, false if we get None
            println!("We got: {}", inside_number.unwrap()); // now it is safe to use .unwrap() because we already checked
        } else {
            println!("We got nothing.");
        }
    }
}

We got nothing.
We got: 5
```

# Option 与 Some

因为我们知道泛型，所以我们能够阅读 Option 的代码。看起来像这样：

```rs
enum Option<T> {
    None,
    Some(T),
}

fn main() {}
```

要记住的重要点：使用 Some，您将拥有 T 类型的值（任何类型）。还要注意，在 `<T>` 周围的枚举名称后面的方括号告诉编译器它是通用的。它没有显示（Display）之类的特征或任何限制它的东西，因此可以是任何东西。但是使用 None，您将一无所有。
