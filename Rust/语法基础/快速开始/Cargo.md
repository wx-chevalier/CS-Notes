# Cargo

> [Cargo Guide](http://doc.crates.io/guide.html)

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

完整的 Cargo 命令列表如下：

| Command                        | Description                                                                |
| ------------------------------ | -------------------------------------------------------------------------- |
| `cargo init`                   | Create a new project for the latest edition.                               |
| `cargo build`                  | Build the project in debug mode (`--release` for all optimization).        |
| `cargo check`                  | Check if project would compile (much faster).                              |
| `cargo test`                   | Run tests for the project.                                                 |
| `cargo run`                    | Run your project, if a binary is produced (main.rs).                       |
| `cargo run --bin b`            | Run binary `b`. Unifies features with other dependents (can be confusing). |
| `cargo run -p w`               | Run main of sub-workspace `w`. Treats features more as you would expect.   |
| `cargo doc --open`             | Locally generate documentation for your code and dependencies.             |
| `cargo rustc -- -Zunpretty=X`  | Show more desugared Rust code, in particular with X being:                 |
| `expanded`                     | Show with expanded macros, ...                                             |
| `cargo +{nightly, stable} ...` | Runs command with given toolchain, e.g., for 'nightly only' tools.         |
| `rustup doc`                   | Open offline Rust documentation (incl. the books), good on a plane!        |

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

## cargo.toml 与 cargo.lock

cargo.toml 和 cargo.lock 文件总是位于项目根目录下。

- 源代码位于 src 目录下。
- 默认的库入口文件是 src/lib.rs。
- 默认的可执行程序入口文件是 src/main.rs。
- 其他可选的可执行文件位于 `src/bin/*.rs`(这里每一个 rs 文件均对应一个可执行文件)。
- 外部测试源代码文件位于 tests 目录下。
- 示例程序源代码文件位于 examples。
- 基准测试源代码文件位于 benches 目录下。

cargo.toml 和 cargo.lock 是 cargo 项目代码管理的核心两个文件，cargo 工具的所有活动均基于这两个文件。

cargo.toml 是 cargo 特有的项目数据描述文件，cargo.toml 文件存储了项目的所有信息，如果想让自己的 rust 项目能够按照期望的方式进行构建、测试和运行，那么，必须按照合理的方式构建'cargo.toml'。而 cargo.lock 文件则不直接面向开发中，也不需要直接去修改这个文件。lock 文件是 cargo 工具根据同一项目的 toml 文件生成的项目依赖详细清单文件，所以我们一般不用不管他，只需要对着 cargo.toml 文件撸就行了。

toml 文件是由诸如[package]或[dependencies]这样的段落组成，每一个段落又由多个字段组成，这些段落和字段就描述了项目组织的基本信息，例如上述 toml 文件中的[package]段落描述了 hello_world 项目本身的一些信息，包括项目名称（对应于 name 字段）、项目版本（对应于 version 字段）、作者列表（对应于 authors 字段）等；[dependencies]段落描述了 hello_world 项目的依赖项目有哪些。

## 语义化版本管理

我们在使用 toml 描述文件对项目进行配置时，经常会遇到项目版本声明及管理的问题，比如：

```toml
[package]
name = "libevent_sys"
version = "0.1.0"

[dependencies]
libc = "0.2"
```

这里 package 段落中的 version 字段的值，以及 dependencies 段落中的 libc 字段的值，这些值的写法，都涉及到语义化版本控制的问题。语义化版本控制是用一组简单的规则及条件来约束版本号的配置和增长。这些规则是根据（但不局限于）已经被各种封闭、开放源码软件所广泛使用的惯例所设计。简单来说，语义化版本控制遵循下面这些规则：

- 版本格式：主版本号.次版本号.修订号，版本号递增规则如下：
- 主版本号：当你做了不兼容的 API 修改，
- 次版本号：当你做了向下兼容的功能性新增，
- 修订号：当你做了向下兼容的问题修正。
- 先行版本号及版本编译信息可以加到“主版本号.次版本号.修订号”的后面，作为延伸。

## [package]段落

```toml
[package]
 # 软件包名称，如果需要在别的地方引用此软件包，请用此名称。
name = "hello_world"

# 当前版本号，这里遵循semver标准，也就是语义化版本控制标准。
version = "0.1.0"    # the current version, obeying semver

# 软件所有作者列表
authors = ["you@example.com"]

# 非常有用的一个字段，如果要自定义自己的构建工作流，
# 尤其是要调用外部工具来构建其他本地语言（C、C++、D等）开发的软件包时。
# 这时，自定义的构建流程可以使用rust语言，写在"build.rs"文件中。
build = "build.rs"

# 显式声明软件包文件夹内哪些文件被排除在项目的构建流程之外，
# 哪些文件包含在项目的构建流程中
exclude = ["build/**/*.o", "doc/**/*.html"]
include = ["src/**/*", "Cargo.toml"]

# 当软件包在向公共仓库发布时出现错误时，使能此字段可以阻止此错误。
publish = false

# 关于软件包的一个简短介绍。
description = "..."

# 下面这些字段标明了软件包仓库的更多信息
documentation = "..."
homepage = "..."
repository = "..."

# 顾名思义，此字段指向的文件就是传说中的ReadMe，
# 并且，此文件的内容最终会保存在注册表数据库中。
readme = "..."

# 用于分类和检索的关键词。
keywords = ["...", "..."]

# 软件包的许可证，必须是cargo仓库已列出的已知的标准许可证。
license = "..."

# 软件包的非标许可证书对应的文件路径。
license-file = "..."
```

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

与平台相关的依赖定义格式不变，不同的是需要定义在[target]字段下。例如：

```toml
# 注意，此处的cfg可以使用not、any、all等操作符任意组合键值对。
# 并且此用法仅支持cargo 0.9.0（rust 1.8.0）以上版本。
# 如果是windows平台，则需要此依赖。
[target.'cfg(windows)'.dependencies]
winhttp = "0.4.0"

[target.'cfg(unix)'.dependencies]
openssl = "1.0.1"

#如果是32位平台，则需要此依赖。
[target.'cfg(target_pointer_width = "32")'.dependencies]
native = { path = "native/i686" }

[target.'cfg(target_pointer_width = "64")'.dependencies]
native = { path = "native/i686" }

# 另一种写法就是列出平台的全称描述
[target.x86_64-pc-windows-gnu.dependencies]
winhttp = "0.4.0"
[target.i686-unknown-linux-gnu.dependencies]
openssl = "1.0.1"

# 如果使用自定义平台，请将自定义平台文件的完整路径用双引号包含
[target."x86_64/windows.json".dependencies]
winhttp = "0.4.0"
[target."i686/linux.json".dependencies]
openssl = "1.0.1"
native = { path = "native/i686" }
openssl = "1.0.1"
native = { path = "native/x86_64" }

# [dev-dependencies]段落的格式等同于[dependencies]段落，
# 不同之处在于，[dependencies]段落声明的依赖用于构建软件包，
# 而[dev-dependencies]段落声明的依赖仅用于构建测试和性能评估。
# 此外，[dev-dependencies]段落声明的依赖不会传递给其他依赖本软件包的项目
[dev-dependencies]
iron = "0.2"
```

# 编译目标

Cargo 内置五种编译器调用模板，分别为 dev、release、test、bench、doc，分别用于定义不同类型生成目标时的编译器参数，如果我们自己想改变这些编译模板，可以自己定义相应字段的值，例如（注意：下述例子中列出的值均为此模板字段对应的系统默认值）：

```toml
# 开发模板, 对应`cargo build`命令
[profile.dev]
opt-level = 0  # 控制编译器的 --opt-level 参数，也就是优化参数
debug = true   # 控制编译器是否开启 `-g` 参数
rpath = false  # 控制编译器的 `-C rpath` 参数
lto = false    # 控制`-C lto` 参数，此参数影响可执行文件和静态库的生成，
debug-assertions = true  # 控制调试断言是否开启
codegen-units = 1 # 控制编译器的 `-C codegen-units` 参数。注意，当`lto = true`时，此字段值被忽略

# 发布模板, 对应`cargo build --release`命令
[profile.release]
opt-level = 3
debug = false
rpath = false
lto = false
debug-assertions = false
codegen-units = 1

# 测试模板，对应`cargo test`命令
[profile.test]
opt-level = 0
debug = true
rpath = false
lto = false
debug-assertions = true
codegen-units = 1

# 性能评估模板，对应`cargo bench`命令
[profile.bench]
opt-level = 3
debug = false
rpath = false
lto = false
debug-assertions = false
codegen-units = 1

# 文档模板，对应`cargo doc`命令
[profile.doc]
opt-level = 0
debug = true
rpath = false
lto = false
debug-assertions = true
codegen-units = 1
```

需要注意的是，当调用编译器时，只有位于调用最顶层的软件包的模板文件有效，其他的子软件包或者依赖软件包的模板定义将被顶层软件包的模板覆盖。

## [features]段落

[features]段落中的字段被用于条件编译选项或者是可选依赖。例如：

```toml
[package]
name = "awesome"

[features]
# 此字段设置了可选依赖的默认选择列表，
# 注意这里的"session"并非一个软件包名称，
# 而是另一个featrue字段session
default = ["jquery", "uglifier", "session"]

# 类似这样的值为空的feature一般用于条件编译，
# 类似于`#[cfg(feature = "go-faster")]`。
go-faster = []

# 此feature依赖于bcrypt软件包，
# 这样封装的好处是未来可以对secure-password此feature增加可选项目。
secure-password = ["bcrypt"]

# 此处的session字段导入了cookie软件包中的feature段落中的session字段
session = ["cookie/session"]

[dependencies]
# 必要的依赖
cookie = "1.2.0"
oauth = "1.1.0"
route-recognizer = "=2.1.0"

# 可选依赖
jquery = { version = "1.0.2", optional = true }
uglifier = { version = "1.5.3", optional = true }
bcrypt = { version = "*", optional = true }
civet = { version = "*", optional = true }
```

如果其他软件包要依赖使用上述 awesome 软件包，可以在其描述文件中这样写：

```toml
[dependencies.awesome]
version = "1.3.5"
default-features = false # 禁用awesome 的默认features
features = ["secure-password", "civet"] # 使用此处列举的各项features
```

使用 features 时需要遵循以下规则：

- feature 名称在本描述文件中不能与出现的软件包名称冲突
- 除了 default feature，其他所有的 features 均是可选的
- features 不能相互循环包含
- 开发依赖包不能包含在内
- features 组只能依赖于可选软件包

features 的一个重要用途就是，当开发者需要对软件包进行最终的发布时，在进行构建时可以声明暴露给终端用户的 features，这可以通过下述命令实现：

```
$ cargo build --release --features "shumway pdf"
```

## 配置构建目标

所有的诸如[[bin]], [lib], [[bench]], [[test]]以及 [[example]]等字段，均提供了类似的配置，以说明构建目标应该怎样被构建。例如（下述例子中[lib]段落中各字段值均为默认值）：

```toml
[lib]
# 库名称，默认与项目名称相同
name = "foo"

# 此选项仅用于[lib]段落，其决定构建目标的构建方式，
# 可以取dylib, rlib, staticlib 三种值之一，表示生成动态库、r库或者静态库。
crate-type = ["dylib"]

# path字段声明了此构建目标相对于cargo.toml文件的相对路径
path = "src/lib.rs"

# 单元测试开关选项
test = true

# 文档测试开关选项
doctest = true

# 性能评估开关选项
bench = true

# 文档生成开关选项
doc = true

# 是否构建为编译器插件的开关选项
plugin = false

# 如果设置为false，`cargo test`将会忽略传递给rustc的--test参数。
harness = true
```

# 集成测试用例

cargo 另一个重要的功能，即将软件开发过程中必要且非常重要的测试环节进行集成，并通过代码属性声明或者 toml 文件描述来对测试进行管理。其中，单元测试主要通过在项目代码的测试代码部分前用 `#[test]` 属性来描述，而集成测试，则一般都会通过 toml 文件中的 `[[test]]` 段落进行描述。例如，假设集成测试文件均位于 tests 文件夹下，则 toml 可以这样来写：

```rs
[[test]]
name = "testinit"
path = "tests/testinit.rs"

[[test]]
name = "testtime"
path = "tests/testtime.rs"
```

上述例子中，name 字段定义了集成测试的名称，path 字段定义了集成测试文件相对于本 toml 文件的路径。看看，定义集成测试就是如此简单。需要注意的是:

- 如果没有在 Cargo.toml 里定义集成测试的入口，那么 tests 目录(不包括子目录)下的每个 rs 文件被当作集成测试入口.
- 如果在 Cargo.toml 里定义了集成测试入口，那么定义的那些 rs 就是入口，不再默认指定任何集成测试入口.

# 项目示例和可执行程序

上面我们介绍了 cargo 项目管理中常用的三个功能，还有两个经常使用的功能：example 用例的描述以及 bin 用例的描述。其描述方法和 test 用例描述方法类似。不过，这时候段落名称'[[test]]'分别替换为：'[[example]]'或者'[[bin]]'。例如：

```rs
[[example]]
name = "timeout"
path = "examples/timeout.rs"

[[bin]]
name = "bin1"
path = "bin/bin1.rs"
```

对于'[[example]]'和'[[bin]]'段落中声明的 examples 和 bins，需要通过'cargo run --example NAME'或者'cargo run --bin NAME'来运行，其中 NAME 对应于你在 name 字段中定义的名称。
