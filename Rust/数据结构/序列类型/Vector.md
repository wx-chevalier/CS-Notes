# Vector

与我们拥有 ＆str 和 String 一样，我们拥有数组和向量。数组越快，功能越少，向量越慢，功能越多。类型写为 Vec。

![Rust Vector](https://s1.ax1x.com/2020/08/02/aYugeI.png)

# Vector 声明

声明向量有两种主要方法。一种类似于使用 new 的 String：

```rs
fn main() {
    let name1 = String::from("Windy");
    let name2 = String::from("Gomesy");

    let mut my_vec = Vec::new();
    // If we run the program now, the compiler will give an error.
    // It doesn't know the type of vec.

    my_vec.push(name1); // Now it knows: it's Vec<String>
    my_vec.push(name2);
}

```

或者，您可以只声明类型。

```rs
fn main() {
    let mut my_vec: Vec<String> = Vec::new(); // The compiler knows the type
                                              // so there is no error.
}
```

您可以看到向量中的项目必须具有相同的类型。创建向量的另一种简单方法是使用 vec！宏。它看起来像数组声明，但是具有 vec！在它前面。

```rs
fn main() {
    let mut my_vec = vec![8, 10, 10];
}
```

类型是 `Vec<i32>`。您称它为 i32 的 vec。`Vec<String>`是字符串 vec，`Vec<Vec<String>>`是字符串 vec 的 vec。

因为 vec 比数组要慢，所以我们可以使用一些方法使其更快。vec 具有容量，这意味着赋予向量的空间。如果添加的向量多于其容量，它将使容量增加一倍，并将项目复制到新空间中。这称为重新分配。

```rs
fn main() {
    let mut num_vec = Vec::new();
    num_vec.push('a'); // add one character
    println!("{}", num_vec.capacity()); // prints 1
    num_vec.push('a'); // add one more
    println!("{}", num_vec.capacity()); // prints 2
    num_vec.push('a'); // add one more
    println!("{}", num_vec.capacity()); // prints 4. It has three elements, but capacity is 4
    num_vec.push('a'); // add one more
    num_vec.push('a'); // add one more // Now we have 5 elements
    println!("{}", num_vec.capacity()); // Now capacity is 8
}
```

因此，此向量具有三个重新分配：1 到 2，2 到 4 和 4 到 8。我们可以使其更快：

```rs
fn main() {
    let mut num_vec = Vec::with_capacity(8); // Give it capacity 8
    num_vec.push('a'); // add one character
    println!("{}", num_vec.capacity()); // prints 8
    num_vec.push('a'); // add one more
    println!("{}", num_vec.capacity()); // prints 8
    num_vec.push('a'); // add one more
    println!("{}", num_vec.capacity()); // prints 8.
    num_vec.push('a'); // add one more
    num_vec.push('a'); // add one more // Now we have 5 elements
    println!("{}", num_vec.capacity()); // Still 8
}
```

此向量具有 0 重新分配，更好。因此，如果您认为自己知道需要多少元素，可以使用 `Vec::with_capacity()` 使其更快。您还记得可以使用 .into() 将 ＆str 转换为字符串。您也可以使用它来将数组制成 Vec。您必须告诉 .into() 您想要一个 Vec，但不必选择 Vec 的类型。如果您不想选择，则可以编写 `Vec <_>`。

```rs
fn main() {
    let my_vec: Vec<u8> = [1, 2, 3].into();
    let my_vec2: Vec<_> = [9, 0, 10].into(); // Vec<_> means "choose the Vec type for me"
                                             // Rust will choose Vec<i32>
}
```

# Vector 访问

## 切片

```rs
fn main() {
    let vec_of_ten = vec![1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
    // Everything is the same except we added vec!
    let three_to_five = &vec_of_ten[2..5];
    let start_at_two = &vec_of_ten[1..];
    let end_at_five = &vec_of_ten[..5];
    let everything = &vec_of_ten[..];

    println!("Three to five: {:?},
start at two: {:?}
end at five: {:?}
everything: {:?}", three_to_five, start_at_two, end_at_five, everything);
}
```
