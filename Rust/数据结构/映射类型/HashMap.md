# HashMap

HashMap 是由键和值组成的集合。您使用键来查找与键匹配的值。您可以仅使用 `HashMap::new()` 创建一个新的 HashMap，并使用 `.insert(key，value)`插入项目。

```rs
use std::collections::HashMap; // This is so we can just write HashMap instead of std::collections::HashMap every time

struct City {
    name: String,
    population: HashMap<u32, u32>, // This will have the year and the population for the year
}

fn main() {

    let mut tallinn = City {
        name: "Tallinn".to_string(),
        population: HashMap::new(), // So far the HashMap is empty
    };

    tallinn.population.insert(1372, 3_250); // insert three dates
    tallinn.population.insert(1851, 24_000);
    tallinn.population.insert(2020, 437_619);


    for (year, population) in tallinn.population { // The HashMap is HashMap<u32, u32> so it returns a two items each time
        println!("In the year {} the city of {} had a population of {}.", year, tallinn.name, population);
    }
}

In the year 1372 the city of Tallinn had a population of 3250.
In the year 2020 the city of Tallinn had a population of 437619.
In the year 1851 the city of Tallinn had a population of 24000.
```

HashMap 要求一个可哈希（实现 Hash trait）的 Key 类型，和一个编译时知道大小的 Value 类型。
同时，Rust 还要求你的 Key 类型必须是可比较的，在 Rust 中，你可以为你的类型轻易的加上编译器属性：

```rust
#[derive(PartialEq, Eq, Hash)]
```

这样，即可将你的类型转换成一个可以作为 Hash 的 Key 的类型。但是，如果你想要自己实现`Hash`这个 trait 的话，你需要谨记两点：

- 1. 如果 Key1==Key2 ,那么一定有 Hash(Key1) == Hash(Key2)
- 2. 你的 Hash 函数本身不能改变你的 Key 值，否则将会引发一个逻辑错误（很难排查，遇到就完的那种）

# 增删改查

```rs
use std::collections::HashMap;

// 声明
let mut come_from = HashMap::new();
// 插入
come_from.insert("WaySLOG", "HeBei");
come_from.insert("Marisa", "U.S.");
come_from.insert("Mike", "HuoGuo");

// 查找key
if !come_from.contains_key("elton") {
    println!("Oh, 我们查到了{}个人，但是可怜的Elton猫还是无家可归", come_from.len());
}

// 根据key删除元素
come_from.remove("Mike");
println!("Mike猫的家乡不是火锅！不是火锅！不是火锅！虽然好吃！");

// 利用get的返回判断元素是否存在
let who = ["MoGu", "Marisa"];
for person in &who {
    match come_from.get(person) {
        Some(location) => println!("{} 来自: {}", person, location),
        None => println!("{} 也无家可归啊.", person),
    }
}

// 遍历输出
println!("那么，所有人呢？");
for (name, location) in &come_from {
    println!("{}来自: {}", name, location);
}
```

## Entry

Rust 为我们提供了一个名叫 `entry` 的 api，它很有意思，和 Python 相比，我们不需要在一次迭代的时候二次访问原 map，只需要借用 entry 出来的 Entry 类型（这个类型持有原有 HashMap 的引用）即可对原数据进行修改。就语法来说，毫无疑问 Rust 在这个方面更加直观和具体。

```rust
use std::collections::HashMap;

let mut letters = HashMap::new();

for ch in "a short treatise on fungi".chars() {
    let counter = letters.entry(ch).or_insert(0);
    *counter += 1;
}

assert_eq!(letters[&'s'], 2);
assert_eq!(letters[&'t'], 3);
assert_eq!(letters[&'u'], 1);
assert_eq!(letters.get(&'y'), None);
```

# BTreeMap

如果您希望可以对 HashMap 进行排序，则可以使用 BTreeMap。实际上它们彼此非常相似，因此我们可以快速将 HashMap 更改为 BTreeMap 进行查看。您可以看到它几乎是相同的代码。

```rs
use std::collections::BTreeMap; // Just change HashMap to BTreeMap

struct City {
    name: String,
    population: BTreeMap<u32, u32>, // Just change HashMap to BTreeMap
}

fn main() {

    let mut tallinn = City {
        name: "Tallinn".to_string(),
        population: BTreeMap::new(), // Just change HashMap to BTreeMap
    };

    tallinn.population.insert(1372, 3_250);
    tallinn.population.insert(1851, 24_000);
    tallinn.population.insert(2020, 437_619);

    for (year, population) in tallinn.population {
        println!("In the year {} the city of {} had a population of {}.", year, tallinn.name, population);
    }
}

In the year 1372 the city of Tallinn had a population of 3250.
In the year 1851 the city of Tallinn had a population of 24000.
In the year 2020 the city of Tallinn had a population of 437619.
```
