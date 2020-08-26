# 结构体

使用结构，您可以创建自己的类型。使用关键字 struct 创建结构。结构的名称应为 UpperCamelCase（每个单词的大写字母，不能有空格）。

# 结构体分类

有三种类型的结构。一个是单元结构（Unit Struct）。单位的意思是“什么都没有”。

```rs
struct FileDirectory;
fn main() { }
```

下一个是元组结构（Tuple Struct）或未命名的结构。之所以称为“未命名”，是因为您只需要编写类型，而不是变量名。当您需要简单的结构并且不需要记住名称时，元组结构会很好。

```rs
struct Colour(u8, u8, u8);

fn main() {
    let my_colour = Colour(50, 0, 50); // Make a colour out of RGB (red, green, blue)
    println!("The second part of the colour is: {}", my_colour.1);
}
```

第三种类型是命名结构（Named Struct）。这可能是最常见的结构。在此结构中，您可以在 {} 代码块内声明变量名称和类型。

```rs
struct Colour(u8, u8, u8); // Declare the same Colour tuple struct

struct SizeAndColour {
    size: u32,
    colour: Colour, // And we put it in our new named struct
}

fn main() {
    let my_colour = Colour(50, 0, 50);

    let size_and_colour = SizeAndColour {
        size: 150,
        colour: my_colour
    };
}
```

在命名结构中，用逗号分隔变量。对于最后一个变量，您可以添加或不添加逗号。 SizeAndColour 在 colour 后有一个逗号：

```rs
struct Colour(u8, u8, u8); // Declare the same Colour tuple struct

struct SizeAndColour {
    size: u32,
    colour: Colour, // And we put it in our new named struct
}

fn main() { }
```

但您不需要它。但是最好始终使用逗号，因为有时您会更改变量的顺序。
