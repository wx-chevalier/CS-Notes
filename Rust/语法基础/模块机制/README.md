# Rust 模块机制

模块是几乎所有语言的基础设施，尽管叫法各有不同。每次在 Rust 中编写代码时，都是在一个 Crate 中编写代码。Crate 是一起存放代码的文件。在您编写的文件内，您还可以制作一个 mod；mod 是函数、结构等的空间。其使用有以下几个原因：

- 构建代码：它可以帮助您考虑代码的一般结构。随着代码越来越大，这可能很重要。
- 阅读您的代码：人们可以更轻松地理解您的代码。例如，当您看到 `std::collections::HashMap` 时，您知道它在模块集合内的 std 中。这给您一个提示，即您可以尝试在集合中包含更多集合类型。
- 隐私权：一切开始都是私人的。这样就可以防止用户直接使用功能。

Rust 中与模块相关的概念定义如下：

- 包 Packages: Cargo 提供的让我们创建, 测试和分享 Crates 的工具.
- 箱 Crates: 提供类库或可执行文件的模块树
- 模块 Modules and use: 管理和组织路径, 及其作用域和访问权限
- 路径 Paths: 如结构体(structs), 函数(function), 或模块(module)等事物的命名方式

# 包与箱

包(Package)通过 Cargo 创建. 每一个包(Package)都有一个 Cargo.toml 文件. 包(Package)包含箱(Crates)的规则如下:

- 只能包含 0 或 1 个类库箱(library crates)
- 可以包含任意多个二进制箱(binary crates)
- 至少有一个箱(Crate), 可以是类库箱(library crates), 也可以是二进制箱(binary crates)

创建二进制包(binary package)

```text
❯ cargo new my-project
     Created binary (application) `my-project` package
❯ tree my-project
my-project
├── Cargo.toml
└── src
    └── main.rs
```

创建类库包(library package)

```text
❯ cargo new --lib my-lib
     Created library `my-lib` package
❯ tree my-lib
my-lib
├── Cargo.toml
└── src
    └── lib.rs
```

默认, 一个箱(crate):

- src/main.rs 是二进制箱(binary crate)的根文件, 该箱(crate)与包(package)同名
- src/lib.rs 是类库箱(library crate)的根文件, 该箱(crate)与包(package)同名
- 多个二进制箱(binary crates): 在 src/bin 目录下创建 .rs 文件, 每个文件对应一个二进制箱(binary crate).
