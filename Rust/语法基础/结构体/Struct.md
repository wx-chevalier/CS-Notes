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

在命名结构中，用逗号分隔变量。对于最后一个变量，您可以添加或不添加逗号。SizeAndColour 在 colour 后有一个逗号：

```rs
struct Colour(u8, u8, u8); // Declare the same Colour tuple struct

struct SizeAndColour {
    size: u32,
    colour: Colour, // And we put it in our new named struct
}

fn main() { }
```

但您不需要它。但是最好始终使用逗号，因为有时您会更改变量的顺序。

# 结构体初始化

让我们创建一个 Country 结构来举一个例子。国家结构具有字段 population，capital 和 leader_name：

```rs
struct Country {
    population: u32,
    capital: String,
    leader_name: String
}

fn main() {
    let population = 500_000;
    let capital = String::from("Elist");
    let leader_name = String::from("Batu Khasikov");

    let kalmykia = Country {
        population: population,
        capital: capital,
        leader_name: leader_name,
    };
}
```

您是否注意到我们写过两次相同的东西？实际上，您不需要这样做。如果字段名和变量名相同，则不必写两次。

```rs
struct Country {
    population: u32,
    capital: String,
    leader_name: String
}

fn main() {
    let population = 500_000;
    let capital = String::from("Elist");
    let leader_name = String::from("Batu Khasikov");

    let kalmykia = Country {
        population,
        capital,
        leader_name,
    };
}
```

# 调试

对于新的 struct 或 enum，如果要使用 `{:?}` 进行打印，则需要给它提供 Debug，因此我们将这样做。如果在 struct 或 enum 上方写上 `#[derive(Debug)]`，则可以使用 `{:?}` 打印它。这些带有 `#[]` 的消息称为属性。有时，您可以使用它们来告诉编译器使结构具有 Debug 之类的功能。有很多属性，我们将在后面学习。但是派生可能是最常见的，您会在结构和枚举之上看到很多。

```rs
#[derive(Debug)]
struct Animal {
    age: u8,
    animal_type: AnimalType,
}

#[derive(Debug)]
enum AnimalType {
    Cat,
    Dog,
}

impl Animal {
    fn new() -> Self {
        // Self means Animal.
        //You can also write Animal instead of Self

        Self {
            // When we write Animal::new(), we always get a cat that is 10 years old
            age: 10,
            animal_type: AnimalType::Cat,
        }
    }

    fn change_to_dog(&mut self) { // because we are inside Animal, &mut self means &mut Animal
                                  // use .change_to_dog() to change the cat to a dog
                                  // with &mut self we can change it
        println!("Changing animal to dog!");
        self.animal_type = AnimalType::Dog;
    }

    fn change_to_cat(&mut self) {
        // use .change_to_cat() to change the dog to a cat
        // with &mut self we can change it
        println!("Changing animal to cat!");
        self.animal_type = AnimalType::Cat;
    }

    fn check_type(&self) {
        // we want to read self
        match self.animal_type {
            AnimalType::Dog => println!("The animal is a dog"),
            AnimalType::Cat => println!("The animal is a cat"),
        }
    }
}

fn main() {
    let mut new_animal = Animal::new(); // Associated method to create a new animal
                                        // It is a cat, 10 years old
    new_animal.check_type();
    new_animal.change_to_dog();
    new_animal.check_type();
    new_animal.change_to_cat();
    new_animal.check_type();
}

The animal is a cat
Changing animal to dog!
The animal is a dog
Changing animal to cat!
The animal is a cat
```

请记住，Self（Self 类型）和 self（变量 self）是缩写。`fn change_to_dog(&mut self)`即等价于 `fn change_to_dog(&mut Animal)`。impl 同样可以用于 Enum 类型：

```rs
enum Mood {
    Good,
    Bad,
    Sleepy,
}

impl Mood {
    fn check(&self) {
        match self {
            Mood::Good => println!("Feeling good!"),
            Mood::Bad => println!("Eh, not feeling so good"),
            Mood::Sleepy => println!("Need sleep NOW"),
        }
    }
}

fn main() {
    let my_mood = Mood::Sleepy;
    my_mood.check();
}
```
