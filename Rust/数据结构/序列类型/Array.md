# Array

数组是方括号 [] 中的数据。数组：

- 不得更改其大小，
- 只能包含相同的类型。

数组的类型为：`[type; number]`。例如，`["One", "Two"]` 的类型为 `[＆str; 2]`。这意味着即使这两个数组也具有不同的类型：

```rs
fn main() {
    let array1 = ["One", "Two"];
    let array2 = ["One", "Two", "Five"];
}
```

一个很好的技巧：要知道变量的类型，可以通过给出错误的指令来“询问”编译器。例如：

```rs
fn main() {
    let seasons = ["Spring", "Summer", "Autumn", "Winter"];
    let seasons2 = ["Spring", "Summer", "Fall", "Autumn", "Winter"];
    let () = seasons; // ⚠️
    let () = seasons2; // ⚠️ as well
}

error[E0308]: mismatched types
 --> src\main.rs:4:9
  |
4 |     let () = seasons;
  |         ^^   ------- this expression has type `[&str; 4]`
  |         |
  |         expected array `[&str; 4]`, found `()`

error[E0308]: mismatched types
 --> src\main.rs:5:9
  |
5 |     let () = seasons2;
  |         ^^   -------- this expression has type `[&str; 5]`
  |         |
  |         expected array `[&str; 5]`, found `()`
```

如果要使用具有相同值的数组，则可以这样声明：

```rs
fn main() {
    let my_array = ["a"; 20];
    println!("{:?}", my_array);
}

["a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"]
```

此方法经常用于创建缓冲区。例如，让 `mut buffer = [0; 640]` 创建一个 640 个零的数组。然后我们可以将零更改为其他数字以添加数据。

# 数组访问

您可以使用 `[]` 索引（获取）数组中的条目。第一个条目是 [0]，第二个条目是 [1]，依此类推。

```rs
fn main() {
    let my_numbers = [0, 10, -20];
    println!("{}", my_numbers[1]); // prints 10
}
```

您可以获取数组的一部分（一片）。首先，您需要一个＆，因为编译器不知道大小。然后，您可以使用..显示范围。

```rs
fn main() {
    let array_of_ten = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

    let three_to_five = &array_of_ten[2..5];
    let start_at_two = &array_of_ten[1..];
    let end_at_five = &array_of_ten[..5];
    let everything = &array_of_ten[..];

    println!("Three to five: {:?},
start at two: {:?}
end at five: {:?}
everything: {:?}", three_to_five, start_at_two, end_at_five, everything);
}
```

因此，`[0..2]` 表示第一个索引和第二个索引（0 和 1）。或者，您可以将其称为“零和第一”索引。它没有第三项，即索引 2。您也可以有一个包含范围，这也包括最后一个数字。为此，请添加 = 以编写 `..=` 而不是 `..`。因此，如果需要第一，第二和第三项，可以写 `[0..=2]` 代替 `[0..2]`。
