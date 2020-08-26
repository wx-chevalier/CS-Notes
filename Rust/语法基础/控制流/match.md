# match

`if`，`else` 和 `else if` 太多可能难以阅读。您可以改用 match。但是您必须匹配所有可能的结果。例如，这将不起作用：

```rs
fn main() {
    let my_number: u8 = 5;
    match my_number {
        0 => println!("it's zero"),
        1 => println!("it's one"),
        2 => println!("it's two"),
        // ⚠️
    }
}

error[E0004]: non-exhaustive patterns: `3u8..=std::u8::MAX` not covered
 --> src\main.rs:3:11
  |
3 |     match my_number {
  |           ^^^^^^^^^ pattern `3u8..=std::u8::MAX` not covered

```

这意味着“您告诉我 0 到 2，但是 u8 最多可以达到 255。3 怎么样？4 怎么样？5 怎么样？”等等。因此，您可以添加 `_`，表示“其他”。

```rs
fn main() {
    let my_number: u8 = 5;
    match my_number {
        0 => println!("it's zero"),
        1 => println!("it's one"),
        2 => println!("it's two"),
        _ => println!("It's some other number"),
    }
}
```

## 元组匹配

您看到分号结尾了吗？这是因为，匹配结束后，我们实际上告诉编译器： `let second_number = 10;`。您也可以匹配更复杂的东西。您使用一个元组来做到这一点。

```rs
fn main() {
    let sky = "cloudy";
    let temperature = "warm";

    match (sky, temperature) {
        ("cloudy", "cold") => println!("It's dark and unpleasant today"),
        ("clear", "warm") => println!("It's a nice day"),
        ("cloudy", "warm") => println!("It's dark but not bad"),
        _ => println!("Not sure what the weather is."),
    }
}
```

您甚至可以在 match 中放入 if。

```rs
fn main() {
    let children = 5;
    let married = true;

    match (children, married) {
        (children, married) if married == false => println!("Not married with {} children", children),
        (children, married) if children == 0 && married == true => println!("Married but no children"),
        _ => println!("Married? {}. Number of children: {}.", married, children),
    }
}

```

您可以在比赛中任意多次使用 `_`。在这种颜色匹配中，我们有三个，但一次只能检查一个。

```rs
fn match_colours(rbg: (i32, i32, i32)) {
    match rbg {
        (r, _, _) if r < 10 => println!("Not much red"),
        (_, b, _) if b < 10 => println!("Not much blue"),
        (_, _, g) if g < 10 => println!("Not much green"),
        _ => println!("Each colour has at least 10"),
    }
}

fn main() {
    let first = (200, 0, 0);
    let second = (50, 50, 50);
    let third = (200, 50, 0);

    match_colours(first);
    match_colours(second);
    match_colours(third);

}

Not much blue
Each colour has at least 10
Not much green
```

这也显示了 match 语句的工作方式，因为在第一个示例中，它仅打印了不多的蓝色。但是首先也没有太多绿色。 match 语句在找到匹配项时总是停止，并且不检查其余部分。这是一个很好的代码示例，可以很好地编译，但不是您想要的代码。您可以制作一个很大的 match 语句来修复它，但使用 for 循环可能更好。我们将很快讨论循环。

您也可以在需要时使用 @ 来使用匹配表达式的值。在此示例中，我们在函数中匹配了 i32 输入。如果是 4 或 13，我们想在 println 中使用该数字！声明。否则，我们不需要使用它。

```rs
fn match_number(input: i32) {
    match input {
    number @ 4 => println!("{} is an unlucky number in China (sounds close to 死)!", number),
    number @ 13 => println!("{} is unlucky in North America, lucky in Italy! In bocca al lupo!", number),
    _ => println!("Looks like a normal number"),
    }
}

fn main() {
    match_number(50);
    match_number(13);
    match_number(4);
}
```

## 返回值

您可以声明一个具有匹配项的值：

```rs
fn main() {
    let my_number = 5;
    let second_number = match my_number {
        0 => 0,
        5 => 10,
        _ => 2,
    };
}
```

匹配项必须返回相同的类型。所以你不能这样做：

```rs
fn main() {
    let my_number = 10;
    let some_variable = match my_number {
        10 => 8,
        _ => "Not ten", // ⚠️
    };
}

error[E0308]: `match` arms have incompatible types
  --> src\main.rs:17:14
   |
15 |       let some_variable = match my_number {
   |  _________________________-
16 | |         10 => 8,
   | |               - this is found to be of type `{integer}`
17 | |         _ => "Not ten",
   | |              ^^^^^^^^^ expected integer, found `&str`
18 | |     };
   | |_____- `match` arms have incompatible types

fn main() {
    let some_variable = if my_number == 10 { 8 } else { "something else "}; // ⚠️
}
```

但这有效，因为您有一个不同的 let 语句。

```rs
fn main() {
    let my_number = 10;

    if my_number == 10 {
        let some_variable = 8;
    } else {
        let some_variable = "Something else";
    }
}
```
