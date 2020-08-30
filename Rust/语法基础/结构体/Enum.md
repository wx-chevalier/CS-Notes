# Enum

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

枚举的一部分也可以转换为整数。Rust 为枚举的每个分支赋予一个以 0 开头的数字。如果枚举中没有任何其他数据，则可以使用该数字。

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

Not the biggest star.
Not the biggest star.
This is a good-sized star.
This is a good-sized star.
What about DeadStar? It's the number 1001.
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

# 多类型使用

您知道，Vec，数组等中的数据结构都需要相同的类型（只有元组不同）。但是您实际上可以使用一个枚举来放入不同的类型。想象一下，我们想要一个带有 u32 或 i32 的 Vec。当然，您可以制作一个 `Vec<(u32, i32)>`（一个带有（u32，i32）元组的 vec），但是我们只想要一个。因此，您可以在此处使用枚举。这是一个简单的示例：

```rs
enum Number {
    U32(u32),
    I32(i32),
}

fn main() { }
```

因此，有两种变体：内置 u32 的 U32 变体和内置 i32 的 I32 变体。U32 和 I32 只是我们做的名字。他们可能是 UThirtyTwo 或 IThirtyTwo 或其他任何东西。现在，如果将它们放入 Vec 中，我们只有一个 `Vec<Number>`，并且编译器很高兴。因为它是一个枚举，所以您必须选择一个。我们将使用 `.is_positive()` 方法进行选择。如果为 true，则选择 U32；如果为 false，则选择 I32。

```rs
enum Number {
    U32(u32),
    I32(i32),
}

impl Number {
    fn new(number: i32) -> Number { // input number is i32
        match number.is_positive() {
            true => Number::U32(number as u32), // change it to u32 if it's positive
            false => Number::I32(number), // otherwise just give the number because it's already i32
        }
    }
}

fn main() {
    let my_vec = vec![Number::new(-800), Number::new(8)];

    for item in my_vec {
        match item {
            Number::U32(number) => println!("It's a u32 with the value {}", number),
            Number::I32(number) => println!("It's a i32 with the value {}", number),
        }
    }
}

It's a i32 with the value -800
It's a u32 with the value 8
```

基于多类型值的特性，我们有时候也可以在 Enum 中使用出类似泛型的功效：

```rs
enum Choice {
    Foo(String),
    Bar(i32)
}

fn main() {
    // this is so we don't need to put "Choice::" before everything
    // this is similar to how Option works, only difference is that
    // with Option, this is automatically done.
    use self::Choice::*;

    let foo: Choice = Foo("Hello World".to_string());
    let bar: Choice = Bar(10);

    let a: Choice;

    // magic random bool that you can change as much as you want
    let random_bool = false;

    if random_bool {
        a = foo; // Here `a` will store a String, which is inside Choice
    } else {
        a = bar; // Here `a` will store an i32, which is inside Choice
    }

    // here we don't know if `a` stores a String or an i32 until we check

    // here we check if we had a Foo variant or a Bar variant
    // in doing so, we figure out if we stored a String or an i32
    match a {
        Foo(s) => println!("We had the String: {}", s),
        Bar(i) => println!("We had the i32: {}", i)
    }
}

```
