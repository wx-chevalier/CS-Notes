# Array

数组是方括号 [] 中的数据。数组：

- 不得更改其大小，
- 只能包含相同的类型。

数组的类型为：`[type; number]`。例如，`["One", "Two"]` 的类型为 `[＆str; 2]`。这意味着即使这两个数组也具有不同的类型：

```rs
fn main() {
    let array1 = ["One", "Two"];
    let array2 = ["One", "Two", "Five"];

    let a = [8, 9, 10];
    let b: [u8;3] = [8, 6, 5];
    print!("{}", a[0]);
}
```

一个很好的技巧：要知道变量的类型，可以通过给出错误的指令来“询问”编译器。例如：

```rs
fn main() {
    let seasons = ["Spring", "Summer", "Autumn", "Winter"];
    let seasons2 = ["Spring", "Summer", "Fall", "Autumn", "Winter"];
    let () = seasons; // ⚠️
    let () = seasons2; // ⚠️ as well
}

error[E0308]: mismatched types
 --> src\main.rs:4:9
  |
4 |     let () = seasons;
  |         ^^   ------- this expression has type `[&str; 4]`
  |         |
  |         expected array `[&str; 4]`, found `()`

error[E0308]: mismatched types
 --> src\main.rs:5:9
  |
5 |     let () = seasons2;
  |         ^^   -------- this expression has type `[&str; 5]`
  |         |
  |         expected array `[&str; 5]`, found `()`
```

如果要使用具有相同值的数组，则可以这样声明：

```rs
fn main() {
    let my_array = ["a"; 20];
    println!("{:?}", my_array);
}

["a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"]
```

此方法经常用于创建缓冲区。例如，让 `mut buffer = [0; 640]` 创建一个 640 个零的数组。然后我们可以将零更改为其他数字以添加数据。

# 数组访问

您可以使用 `[]` 索引（获取）数组中的条目。第一个条目是 [0]，第二个条目是 [1]，依此类推。

```rs
fn main() {
    let my_numbers = [0, 10, -20];
    println!("{}", my_numbers[1]); // prints 10
}
```

# 数组切片

Slice 从直观上讲，是对一个 Array 的切片，通过 Slice，你能获取到一个 Array 的部分或者全部的访问权限。和 Array 不同，Slice 是可以动态的，但是呢，其范围是不能超过 Array 的大小，这点和 Golang 是不一样的。一个 Slice 的表达式可以为如下: &[T] 或者 &mut [T]。

```rs
fn main() {
    let array_of_ten = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

    let three_to_five = &array_of_ten[2..5];
    let start_at_two = &array_of_ten[1..];
    let end_at_five = &array_of_ten[..5];
    let everything = &array_of_ten[..];

    println!("Three to five: {:?},
start at two: {:?}
end at five: {:?}
everything: {:?}", three_to_five, start_at_two, end_at_five, everything);
}
```

因此，`[0..2]` 表示第一个索引和第二个索引（0 和 1）。或者，您可以将其称为“零和第一”索引。它没有第三项，即索引 2。您也可以有一个包含范围，这也包括最后一个数字。为此，请添加 = 以编写 `..=` 而不是 `..`。因此，如果需要第一，第二和第三项，可以写 `[0..=2]` 代替 `[0..2]`。

# 数组变换

# 消费者与适配器

说完了`for`循环，我们大致弄清楚了 `Iterator` 和 `IntoIterator` 之间的关系。下面我们来说一说消费者和适配器。消费者是迭代器上一种特殊的操作，其主要作用就是将迭代器转换成其他类型的值，而非另一个迭代器。而适配器，则是对迭代器进行遍历，并且其生成的结果是另一个迭代器，可以被链式调用直接调用下去。由上面的推论我们可以得出: _迭代器其实也是一种适配器！_

## collect

就像所有人都熟知的生产者消费者模型，迭代器负责生产，而消费者则负责将生产出来的东西最终做一个转化。一个典型的消费者就是`collect`。前面我们写过`collect`的相关操作，它负责将迭代器里面的所有数据取出，例如下面的操作：

```rust
let v = (1..20).collect(); //编译通不过的！
```

尝试运行上面的代码，却发现编译器并不让你通过。因为你没指定类型！指定什么类型呢？原来 collect 只知道将迭代器收集到一个实现了 `FromIterator` 的类型中去，但是，事实上实现这个 trait 的类型有很多（Vec, HashMap 等），因此，collect 没有一个上下文来判断应该将 v 按照什么样的方式收集！！

要解决这个问题，我们有两种解决办法：

1. 显式地标明`v`的类型:

   ```rust
   let v: Vec<_> = (1..20).collect();
   ```

2. 显式地指定`collect`调用时的类型：

   ```rust
   let v = (1..20).collect::<Vec<_>>();
   ```

当然，一个迭代器中还存在其他的消费者，比如取第几个值所用的 `.nth()`函数，还有用来查找值的 `.find()` 函数，调用下一个值的`next()`函数等等，这里限于篇幅我们不能一一介绍。所以，下面我们只介绍另一个比较常用的消费者—— `fold` 。当然了，提起 Rust 里的名字你可能没啥感觉，其实，`fold`函数，正是大名鼎鼎的 MapReduce 中的 Reduce 函数(稍微有点区别就是这个 Reduce 是带初始值的)。

`fold`函数的形式如下：

```rust
fold(base, |accumulator, element| .. )
```

我们可以写成如下例子：

```rust
let m = (1..20).fold(1u64, |mul, x| mul*x);
```

需要注意的是，`fold`的输出结果的类型，最终是和`base`的类型是一致的（如果`base`的类型没指定，那么可以根据前面`m`的类型进行反推，除非`m`的类型也未指定），也就是说，一旦我们将上面代码中的`base`从 `1u64` 改成 `1`，那么这行代码最终将会因为数据溢出而崩溃！

## map

我们所熟知的生产消费的模型里，生产者所生产的东西不一定都会被消费者买账，因此，需要对原有的产品进行再组装。这个再组装的过程，就是适配器。因为适配器返回的是一个新的迭代器，所以可以直接用链式请求一直写下去。
前面提到了 Reduce 函数，那么自然不得不提一下另一个配套函数 —— `map` :

熟悉 Python 语言的同学肯定知道，Python 里内置了一个`map`函数，可以将一个迭代器的值进行变换，成为另一种。Rust 中的`map`函数实际上也是起的同样的作用，甚至连调用方法也惊人的相似！

```rust
(1..20).map(|x| x+1);
```

上面的代码展示了一个“迭代器所有元素的自加一”操作，但是，如果你尝试编译这段代码，编译器会给你提示：

```
warning: unused result which must be used: iterator adaptors are lazy and
         do nothing unless consumed, #[warn(unused_must_use)] on by default
(1..20).map(|x| x + 1);
 ^~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
```

呀，这是啥？

因为，所有的适配器，都是惰性求值的！

**也就是说，除非你调用一个消费者，不然，你的操作，永远也不会被调用到！**

现在，我们知道了`map`，那么熟悉 Python 的人又说了，是不是还有`filter`！？答，有……用法类似，`filter`接受一个闭包函数，返回一个布尔值，返回`true`的时候表示保留，`false`丢弃。

```rust
let v: Vec<_> = (1..20).filter(|x| x%2 == 0).collect();
```

以上代码表示筛选出所有的偶数。

## skip 和 take

`take(n)`的作用是取前`n`个元素，而`skip(n)`正好相反，跳过前`n`个元素。

```rust
let v = vec![1, 2, 3, 4, 5, 6];
let v_take = v.iter()
    .cloned()
    .take(2)
    .collect::<Vec<_>>();
assert_eq!(v_take, vec![1, 2]);

let v_skip: Vec<_> = v.iter()
    .cloned()
    .skip(2)
    .collect();
assert_eq!(v_skip, vec![3, 4, 5, 6]);
```

### zip 和 enumerate 的恩怨情仇

`zip`是一个适配器，他的作用就是将两个迭代器的内容压缩到一起，形成 `Iterator<Item=(ValueFromA, ValueFromB)>` 这样的新的迭代器；

```rust
let names = vec!["WaySLOG", "Mike", "Elton"];
let scores = vec![60, 80, 100];
let score_map: HashMap<_, _> = names.iter()
    .zip(scores.iter())
    .collect();
println!("{:?}", score_map);
```

而`enumerate`, 熟悉的 Python 的同学又叫了：Python 里也有！对的，作用也是一样的，就是把迭代器的下标显示出来，即：

```rust
let v = vec![1u64, 2, 3, 4, 5, 6];
let val = v.iter()
    .enumerate()
    // 迭代生成标，并且每两个元素剔除一个
    .filter(|&(idx, _)| idx % 2 == 0)
    // 将下标去除,如果调用unzip获得最后结果的话，可以调用下面这句，终止链式调用
    // .unzip::<_,_, vec<_>, vec<_>>().1
    .map(|(idx, val)| val)
    // 累加 1+3+5 = 9
    .fold(0u64, |sum, acm| sum + acm);

println!("{}", val);
```

## 一系列查找函数

Rust 的迭代器有一系列的查找函数，比如：

- `find()`: 传入一个闭包函数，从开头到结尾依次查找能令这个闭包返回`true`的第一个元素，返回`Option<Item>`
- `position()`: 类似`find`函数，不过这次输出的是`Option<usize>`，第几个元素。
- `all()`: 传入一个函数，如果对于任意一个元素，调用这个函数返回`false`,则整个表达式返回`false`，否则返回`true`
- `any()`: 类似`all()`，不过这次是任何一个返回`true`，则整个表达式返回`true`，否则`false`
- `max()`和`min()`: 查找整个迭代器里所有元素，返回最大或最小值的元素。注意：因为第七章讲过的`PartialOrder`的原因，`max`和`min`作用在浮点数上会有不符合预期的结果。

以上，为常用的一些迭代器和适配器及其用法，仅作科普，对于这一章。我希望大家能够多练习去理解，而不是死记硬背。
