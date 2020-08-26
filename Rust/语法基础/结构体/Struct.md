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
