# 泛型

泛型程序设计是程序设计语言的一种风格或范式。允许程序员在强类型程序设计语言中编写代码时使用一些以后才指定的类型，在实例化时（instantiate）作为参数指明这些类型（在 Rust 中，有的时候类型还可以被编译器推导出来）。各种程序设计语言和其编译器、运行环境对泛型的支持均不一样。Ada, Delphi, Eiffel, Java, C#, F#, Swift, and Visual Basic .NET 称之为泛型（generics）；ML, Scala and Haskell 称之为参数多态（parametric polymorphism）；C++与 D 称之为模板。具有广泛影响的 1994 年版的《Design Patterns》一书称之为参数化类型（parameterized type）。

在编程的时候，我们经常利用多态。通俗的讲，多态就是好比坦克的炮管，既可以发射普通弹药，也可以发射制导炮弹（导弹），也可以发射贫铀穿甲弹，甚至发射子母弹，大家都不想为每一种炮弹都在坦克上分别安装一个专用炮管，即使生产商愿意，炮手也不愿意，累死人啊。所以在编程开发中，我们也需要这样“通用的炮管”，这个“通用的炮管”就是多态。需要知道的是，泛型就是一种多态。

泛型主要目的是为程序员提供了编程的便利，减少代码的臃肿,同时极大丰富了语言本身的表达能力, 为程序员提供了一个合适的炮管。想想，一个函数，代替了几十个，甚至数百个函数，是一件多么让人兴奋的事情。 泛型，可以理解为具有某些功能共性的集合类型，如 i8、i16、u8、f32 等都可以支持 add，甚至两个 struct Point 类型也可以 add 形成一个新的 Point。

# 泛型实例

在函数中，您写什么类型作为输入：

```rs
fn return_number(number: i32) -> i32 {
    println!("Here is your number.");
    number
}

fn main() {
    let number = return_number(5);
}
```

但是，如果您要使用的不仅仅是 i32，该怎么办？您可以为此使用泛型。泛型的意思是“也许是一种类型，也许是另一种类型”。对于泛型，可以使用内部类型的尖括号，如下所示：`<T>` 表示“放入函数中的任何类型”。通常，泛型使用带有一个大写字母（T，U，V 等）的类型，尽管您不必只使用一个字母。

```rs
fn return_number<T>(number: T) -> T {
    println!("Here is your number.");
    number
}

fn main() {
    let number = return_number(5);
}
```

重要的部分是函数名称后面的 `<T>`。没有这个，Rust 会认为 T 是一个具体的（具体=非泛型）类型，例如 String 或 i8。如果我们写出一个类型名，这更容易理解。看看将 T 更改为 MyType 时会发生什么：

```rs
fn return_number(number: MyType) -> MyType { // ⚠️
    println!("Here is your number.");
    number
}
```

如您所见，MyType 是具体的，而不是通用的。因此，我们需要编写此代码，这样它现在可以工作了：

```rs
fn return_number<MyType>(number: MyType) -> MyType {
    println!("Here is your number.");
    number
}

fn main() {
    let number = return_number(5);
}
```

因此，单个字母 T 用于人类的眼睛，但函数名后面的部分用于编译器的“眼睛”。没有它，它不是通用的。
