# Tuples

Rust 中的元组使用 ()。我们已经看到了许多空元组。`fn do_something() {}` 有一个空的元组。另外，当您在函数中不返回任何内容时，实际上会返回一个空的元组。

```rs
fn main() {

}

fn just_prints() {
    println!("I am printing"); // Adding ; means we return an empty tuple
}
```

但是元组可以容纳很多东西，也可以容纳不同的类型。要访问元组中的项目，请不要使用 `[]`，而应使用 `.`。

```rs
fn main() {
    let mut new_vec = Vec::new();
    new_vec.push('a');
    let random_tuple = ("Here is a name", 8, new_vec, 'b', [8, 9, 10], 7.7);
    println!(
        "Inside the tuple is: First item: {:?}
Second item: {:?}
Third item: {:?}
Fourth item: {:?}
Fifth item: {:?}
Sixth item: {:?}",
        random_tuple.0,
        random_tuple.1,
        random_tuple.2,
        random_tuple.3,
        random_tuple.4,
        random_tuple.5,
    )
}
```

您可以使用元组创建多个变量。

```rs
fn main() {
    let str_vec = vec!["one", "two", "three"];

    let (a, b, c) = (str_vec[0], str_vec[1], str_vec[2]);
    println!("{:?}", b);
}

```
