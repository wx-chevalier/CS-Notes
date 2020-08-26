# Enums

enum 是 enumerations 的缩写。它们看起来与结构相似，但有所不同。区别在于：

- 当您想要一件事和另一件事时，请使用 struct。
- 当您需要一件事或另一件事时，请使用一个 enum。

因此，结构适用于许多事物，而枚举适用于许多选择。要声明一个枚举，请编写枚举并使用带有选项的代码块（以逗号分隔）。就像结构一样，最后一部分可以有逗号，也可以没有逗号。我们将创建一个名为 ThingsInTheSky 的枚举：

```rs
enum ThingsInTheSky {
    Sun,
    Stars,
}

fn main() { }
```

这是一个枚举，因为您可以看到太阳或星星：您必须选择一个。这些称为 variants。

```rs
// create the enum with two choices
enum ThingsInTheSky {
    Sun,
    Stars,
}

// With this function we can use an i32 to create ThingsInTheSky.
fn create_skystate(time: i32) -> ThingsInTheSky {
    match time {
        6..=18 => ThingsInTheSky::Sun, // Between 6 and 18 hours we can see the sun
        _ => ThingsInTheSky::Stars, // Otherwise, we can see stars
    }
}

// With this function we can match against the two choices in ThingsInTheSky.
fn check_skystate(state: &ThingsInTheSky) {
    match state {
        ThingsInTheSky::Sun => println!("I can see the sun!"),
        ThingsInTheSky::Stars => println!("I can see the stars!")
    }
}

fn main() {
    let time = 8; // it's 8 o'clock
    let skystate = create_skystate(time); // create_skystate returns a ThingsInTheSky
    check_skystate(&skystate); // Give it a reference so it can read the variable skystate
}
```

# 整数值

枚举的一部分也可以转换为整数。 Rust 为枚举的每个分支赋予一个以 0 开头的数字。如果枚举中没有任何其他数据，则可以使用该数字。

```rs
enum Season {
    Spring, // If this was Spring(String) or something it wouldn't work
    Summer,
    Autumn,
    Winter,
}

fn main() {
    use Season::*;
    let four_seasons = vec![Spring, Summer, Autumn, Winter];
    for season in four_seasons {
        println!("{}", season as u32);
    }
}

0
1
2
3
```

您可以根据需要给它一个不同的数字，可以以相同的方式使用它。只需在要添加数字的手臂上添加=和您的数字即可。您不必全力以赴。但是，如果您不这样做，Rust 只会从手臂添加 1 来给它一个数字。

```rs
enum Star {
    BrownDwarf = 10,
    RedDwarf = 50,
    YellowStar = 100,
    RedGiant = 1000,
    DeadStar,
}

fn main() {
    use Star::*;
    let starvec = vec![BrownDwarf, RedDwarf, YellowStar, RedGiant];
    for star in starvec {
        match star as u32 {
            size if size <= 80 => println!("Not the biggest star."),
            size if size >= 80 => println!("This is a good-sized star."),
            _ => println!("That star is pretty big!"),
        }
    }
    println!("What about DeadStar? It's the number {}.", DeadStar as u32);
}
```

# 自定义值

您也可以将数据添加到枚举。

```rs
enum ThingsInTheSky {
    Sun(String), // Now each variant has a string
    Stars(String),
}

fn create_skystate(time: i32) -> ThingsInTheSky {
    match time {
        6..=18 => ThingsInTheSky::Sun(String::from("I can see the sun!")), // Write the strings here
        _ => ThingsInTheSky::Stars(String::from("I can see the stars!")),
    }
}

fn check_skystate(state: &ThingsInTheSky) {
    match state {
        ThingsInTheSky::Sun(description) => println!("{}", description), // Give the string the name description so we can use it
        ThingsInTheSky::Stars(n) => println!("{}", n), // Or you can name it n. Or anything else - it doesn't matter
    }
}

fn main() {
    let time = 8; // it's 8 o'clock
    let skystate = create_skystate(time); // create_skystate returns a ThingsInTheSky
    check_skystate(&skystate); // Give it a reference so it can read the variable skystate
}
```
