# 快速开始

[Rust 官方的教程](https://parg.co/UPm)也是非常优秀的 Rust 学习资料，也可以在 [Rust Learning & Practices Links](https://parg.co/UZ6)中查询更多的参考资料。下面我们开始在本地安装 Rust 开发环境，可以使用如下脚本，或者在 Windows 上下载 [rustup-init.exe](https://static.rust-lang.org/rustup/dist/i686-pc-windows-gnu/rustup-init.exe)；离线安装的话则可以下载[离线安装包](https://www.rust-lang.org/en-US/other-installers.html)。

```s
$ curl https://sh.rustup.rs -sSf | sh -s -- --help
```

在 Rust 开发环境中，所有工具都安装到 `%USERPROFILE%\.cargo\bin` 目录，并且您能够在这里找到 Rust 工具链，包括 rustc、cargo 及 rustup。rustup 往往用于版本与工具链管理，rustc 用于编译与运行，cargo 则是负责包管理以及项目的全生命周期管理。Rust 中的 Hello World 如下所示：

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

# 猜数字游戏

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
        //::操作符表示某个特定类型的关联函数，类似于静态方法
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

# 简单 Web 服务器

如果想对 Web Server 开发有更深入的了解，可以参考 [nickel.rs](https://github.com/nickel-org/nickel.rs) 去实现自己简单的 Web 服务器。

> - [Creating a basic webservice in Rust](https://parg.co/UPW)

# WebAssembly

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
