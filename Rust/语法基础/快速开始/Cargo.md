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

# 依赖管理

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

# 项目结构

```sh
.
├── Cargo.lock
├── Cargo.toml
├── src/
│   ├── lib.rs
│   ├── main.rs
│   └── bin/
│       ├── named-executable.rs
│       ├── another-executable.rs
│       └── multi-file-executable/
│           ├── main.rs
│           └── some_module.rs
├── benches/
│   ├── large-input.rs
│   └── multi-file-bench/
│       ├── main.rs
│       └── bench_module.rs
├── examples/
│   ├── simple.rs
│   └── multi-file-example/
│       ├── main.rs
│       └── ex_module.rs
└── tests/
    ├── some-integration-tests.rs
    └── multi-file-test/
        ├── main.rs
        └── test_module.rs
```
