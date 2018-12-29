> []() 从属于笔者的 [Rust in Action]() 系列文章，介绍了 Rust 的背景、安装与包管理、基础语法、构建简单的 Web 服务器以及如何编译为 WebAssembly。

# Rust 初窥 : 设计理念、包管理与 WebAssembly 集成


当我们需要编写接近实时高性能，稳健，并有足够开发效率的大程序时，譬如数据库、交易系统、大型桌面应用等，往往会首先选择 C 或者 C++。这种情况下我们往往有以下考量：

* 不需要自动垃圾回收(GC )，避免因为垃圾回收导致的程序中断，以及可能引发的错误或者麻烦。

* 有成熟完善的基础组件库，以保证开发效率。这一点正是 C 的不足，其虽然能保证高性能，但却需要重复造轮子；而 C++ 虽然内置了标准库，但是其却缺乏统一、完善的包管理器。

- 零开销抽象(Zero Cost Abstraction )，我们希望有合适的抽象来保证开发效率、可读与可维护性，但是这种抽象必须是没有运行时开销的。因此我们需要静态强类型多范式语言，编译器能够尽早地发现问题，并且在编译阶段即能自动地进行性能优化；譬如 C++ 中，编译器如果发现虚类 (Virtual Class) 没有真正被用到甚至会优化掉虚表 (Virtual Table)。

借鉴 [How Stylo Brought Rust and Servo to Firefox](http://bholley.net/blog/2017/stylo.html) 一文中的阐述，Mozilla, Google, Apple, 以及 Microsoft 等优秀公司开发的大型的 C/C++ 应用中，错误与漏洞从未停止，我们需要的是一门安全、高效、可扩展的语言。作为新语言，Rust 没有太多历史的包袱；但是 Rust 也并非一蹴而就，而是近 30 年的编程语言理论研究和实际软件工程的经验的集大成者：

* They borrowed Apple’s [C++ compiler backend](https://llvm.org/), which lets Rust match C++ in speed without reimplementing decades of platform-specific code generation optimizations.

* They leaned on the existing corpus of research languages, which contained droves of well-vetted ideas that nonetheless hadn’t been or couldn’t be integrated into C++.

* They included the _unsafe_ keyword - an escape hatch which, for an explicit section of code, allows programmers to override the safety checks and do anything they might do in C++. This allowed people to start building real things in Rust without waiting for the language to grow idiomatic support for each and every use case.

* They built a convenient [package ecosystem](https://crates.io/), allowing the out-of-the-box capabilities of Rust to grow while the core language and standard library remained small.

Rust 是为工业应用而生，并不拘泥于遵循某个范式( Paradigm )，笔者认为其最核心的特性为 Ownership 与 Lifetime；能够在没有 GC 与 Runtime 的情况下，防止近乎所有的段错误，并且保证线程安全(prevents nearly all segfaults, and guarantees thread safety )。Rust 为每个引用与指针设置了 Lifetime，对象则不允许在同一时间有两个和两个以上的可变引用，并且在编译阶段即进行了内存分配(栈或者堆)； Rust 还提供了 Closure 等函数式编程语言的特性、编译时多态(Compile-time Polymorphism )、衍生的错误处理机制、灵活的模块系统等。从应用层面来看，Mozilla 本身就是 Web 领域的执牛耳者，无论是使用 Rust 开发 Node.js 插件，还是 [Rust 默认支持 WebAssembly](https://parg.co/UPo)，都能很好地弥补目前笔者在进行 Web 前端 / Electron 客户端 / Node.js 计算模块的一些性能缺失。

## 背景

任何一门新技术的兴起，都是为了解决一个问题。自操作系统诞生以来，系统级主流编程语言，从汇编语言到C++，已经发展了近50 个年头，但依然存在两个难题：

很难编写内存安全的代码。

很难编写线程安全的代码。

这两个难题存在的本质原因是C/C++属于类型不安全的语言，它们薄弱的内存管理机制导致了很多常见的漏洞。其实20 世纪80 年代也出现过非常优秀的语言，比如Ada 语言。Ada拥有诸多优秀的特性：可以在编译期进行类型检查、无GC 式确定性内存管理、内置安全并发模型、无数据竞争、系统级硬实时编程等。但它的性能和同时期的C/C++相比确实是有差距的。那个时代计算资源匮乏，大家追求的是性能。所以，大家都宁愿牺牲安全性来换取性能。这也是C/C++得以普及的原因。

“Rust”这个名字包含了GH 对这门语言的预期。在自然界有一种叫作锈菌（Rust Fungi）的真菌，这种真菌寄生于植物中，引发病害，而且号称“本世纪最可怕的生态病害”之一。这种真菌的生命力非常顽强，其在生命周期内可以产生多达5 种孢子类型，这5 种生命形态还可以相互转化，如果用软件术语来描述这种特性，那就是“鲁棒性超强”。可以回想一下Rust的Logo形状，像不像一个细菌？Logo上面有5个圆圈，也和锈菌这5种生命形态相对应，暗示Rust语言的鲁棒性也超强。Rust也有铁锈的意思，暗合裸金属之意，代表其系统级编程语言属性，有直接操作底层硬件的能力。此外Rust在字形组合上也糅合了Trust和Robust，暗示了信任与鲁棒性。


# 快速开始

[Rust 官方的教程](https://parg.co/UPm)也是非常优秀的 Rust 学习资料，也可以在 [Rust  Learning & Practices Links](https://parg.co/UZ6)中查询更多的参考资料。下面我们开始在本地安装 Rust 开发环境，可以使用如下脚本，或者在 Windows 上下载 [rustup-init.exe](https://static.rust-lang.org/rustup/dist/i686-pc-windows-gnu/rustup-init.exe)；离线安装的话则可以下载[离线安装包](https://www.rust-lang.org/en-US/other-installers.html)。

```s
$ curl https://sh.rustup.rs -sSf | sh -s -- --help
```

在 Rust 开发环境中，所有工具都安装到 %USERPROFILE%\.cargo\bin 目录， 并且您能够在这里找到 Rust 工具链，包括 rustc、cargo 及 rustup。rustup 往往用于版本与工具链管理，rustc 用于编译与运行，cargo 则是负责包管理以及项目的全生命周期管理。Rust 中的 Hello World 如下所示：

```rs
fn main() {
    println!("Hello, world!");
}
```

我们可以通过 rustc 将其编译为可执行程序：

```s
$ rustc main.rs
$ ./main
Hello, world!
```

官方入门教程还提供了稍为复杂的[猜数字游戏](https://parg.co/UZE)，也能让我们一窥 Rust 的语法特性 :

```rs
// 声明使用的第三方库
extern crate rand;

// 引入标准库或者第三方库中的模块
use std::io;
use std::cmp::Ordering;
use rand::Rng;

// 声明主函数
fn main() {
    // 打印函数，println! 是 Macro
    println!("Guess the number!");

    // let 进行变量绑定(Variable Bindings)
    let secret_number = rand::thread_rng().gen_range(1, 101);

    loop {
        println!("Please input your guess.");

        // 创建可变的字符串，UTF-8 格式
        // :: 操作符表示某个特定类型的关联函数，类似于静态方法
        let mut guess = String::new();

        // read_line 是某个类型实例的方法
        // 读取用户输入，这里的 &mut 表示将 guess 的可变引用借用给 read_line 函数
        io::stdin().read_line(&mut guess)
            // 添加异常处理，否则程序无法编译通过
            .expect("Failed to read line");

        let guess: u32 = match guess.trim().parse() {
            Ok(num) => num,
            Err(_) => continue,
        };

        // {} 表示占位符，用于进行字符串格式化
        println!("You guessed: {}", guess);

        // 进行值比较，Ordering 是枚举类型，包含了三个可能的值
        match guess.cmp(&secret_number) {
            Ordering::Less    => println!("Too small!"),
            Ordering::Greater => println!("Too big!"),
            Ordering::Equal   => {
                println!("You win!");
                break;
            }
        }
    }
}
```

# Cargo

[Cargo](http://doc.crates.io/) 是 Rust 内置的代码组织管理工具，它借鉴了 Maven, Gradle, Go, Npm 等依赖管理、项目构建工具的优点，提供了一系列的工具，从项目的建立、构建到测试、运行直至部署，为 Rust 项目的管理提供尽可能完整的手段。Cargo 目前包含在了官方的分发包中，Rust 开发环境安装完毕后，我们可以直接在命令行中使用 `cargo` 命令来创建新的项目：

```sh
# --bin 表示该项目将生成可执行文件，否则表示生成库文件
$ cargo new hello-rust --bin
```

`cargo new` 指令会在当前目录下新建了基于 cargo 项目管理的 Rust 项目，我们自动生成了基本运行所必须的所有代码：

```rs
fn main() {
    println!("Hello, world!");
}
```

然后我们可以使用 `cargo build` 命令进行项目编译，编译完毕后 Cargo 会自动生成如下的文件目录 :

```sh
├─src
└─target
    └─debug
        ├─.fingerprint
        │  └─hello-rust-af2f81aa8cdfcf12
        ├─build
        ├─deps
        ├─examples
        ├─incremental
        └─native
```

最后使用 `cargo run` 即自动运行生成的代码：

```sh
Finished dev [unoptimized + debuginfo] target(s) in 0.0 secs
    Running `target\debug\hello-rust.exe`
Hello, world!
```

如果我们希望生成发布包，则可以使用 `cargo build --release` 来进行带优化操作的编译：

```sh
$ cargo build --release
   Compiling hello-rust v0.1.0 (file:///path/to/project/hello-rust)
```

对于现有的 Cargo 项目，我们同样可以直接利用 build 指令进行编译：

```sh
$ git clone https://github.com/rust-lang-nursery/rand.git
$ cd rand

$ cargo build
   Compiling rand v0.1.0 (file:///path/to/project/rand)
```

[Cargo Guide](http://doc.crates.io/guide.html)

## 依赖管理

Cargo 的依赖管理主要依托于 Cargo.toml 与 Cargo.lock 这两个文件，Cargo.toml 文件存储了项目的所有信息；Cargo.lock 则是根据同一项目的 toml 文件生成的项目依赖详细清单文件，类似于 yarn.lock，其可以避免泛版本号带来的依赖版本混乱问题。Cargo 使用了集中式地包管理 [crates.io](https://crates.io/)，我们可以在上面搜索需要的第三方依赖，并将其添加到 Cargo.toml 中 :

```sh
[dependencies]
time = "0.1.12"
regex = "0.1.41"
```

Rust 包管理使用 crate 格式的压缩包存储和发布库，托管在 AWS S3 上；国内中科大则是提供了[资源镜像](https://lug.ustc.edu.cn/wiki/mirrors/help/rust-crates)，我们可以在项目根目录的 `.cargo/config` 或者全局的 `$HOME/.cargo/config` 文件中添加如下 Registry 配置 :

```toml
[source.crates-io]
registry = "https://github.com/rust-lang/crates.io-index"
replace-with = 'ustc'

[source.ustc]
registry = "git://mirrors.ustc.edu.cn/crates.io-index"
```

更详细的 Cargo Configuration 查看[这里](http://doc.crates.io/config.html)。

## 项目结构

# 扩展应用

## 简单 Web 服务器

[nickel.rs](https://github.com/nickel-org/nickel.rs)

[Creating a basic webservice in Rust](https://parg.co/UPW)

## WebAssembly

目前，[Rust 能够直接编译为 WebAssembly](https://www.hellorust.com) 格式，本部分以简单的加法为例，首先编写 Rust 加法函数如下：

```rs
#[no_mangle]
pub fn add_one(x: i32) -> i32 {
    x + 1
}
```

然后更新 Rust 版本并且添加 wasm32 编译源：

```rs
rustup update
rustup target add wasm32-unknown-unknown --toolchain nightly
rustc +nightly --target wasm32-unknown-unknown -O --crate-type=cdylib add.rs -o add.big.wasm
```

然后利用 wasm-gc 来减少文件体积：

```rs
cargo install --git https://github.com/alexcrichton/wasm-gc
wasm-gc add.big.wasm add.wasm
```

对应的 JavaScript 部分代码，首先会抓取 add.wasm 模块，然后初始化为 WebAssembly 对象并且执行：

```rs
fetch('add.wasm')
.then(response => response.arrayBuffer())
.then(bytes => WebAssembly.instantiate(bytes, {}))
.then(results => {
    alert(results.instance.exports.add_one(41));
});
```

这里 add.wasm 的可读化格式为：

```rs
(module
  (type (;0;) (func (param i32) (result i32)))
  (type (;1;) (func))
  (func (;0;) (type 0) (param i32) (result i32)
    get_local 0
    i32.const 1
    i32.add)
  (func (;1;) (type 1))
  (table (;0;) 0 anyfunc)
  (memory (;0;) 17)
  (export "memory" (memory 0))
  (export "add_one" (func 0))
  (export "rust_eh_personality" (func 1))
  (data (i32.const 4) "\10\00\10\00"))
```
