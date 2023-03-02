# LLVM

LLVM 项目是可重用(reusable)、模块化(modular)的编译器以及工具链技术(toolchain technologies)的集合。有人将其理解为“底层虚拟机(Low Level Virtual Machine)”的简称，但是官方原话为：

```
“The name “LLVM” itself is not an acronym; it is the full name of the project.”
```

意思是：LLVM 不是首字母缩写，而是这整个项目的全名。LLVM 项目的发展起源于 2000 年伊利诺伊大学厄巴纳-香槟分校维克拉姆·艾夫（Vikram Adve）与克里斯·拉特纳（Chris Lattner）的研究，他们想要为所有静态及动态语言创造出动态的编译技术。2005 年，苹果计算机雇用了克里斯·拉特纳及他的团队为苹果计算机开发应用程序系统，LLVM 为现今 Mac OS X 及 iOS 开发工具的一部分。

## 编译架构

LLVM 是基于静态单一分配的表示形式，可提供类型安全性、底层操作、灵活性，并且适配几乎所有高级语言，具有通用的代码表示。现在 LLVM 已经成为多个编译器和代码生成相关子项目的母项目。

> The LLVM code representation is designed to be used in three different forms: as an in-memory compiler IR, as an on-disk bitcode representation (suitable for fast loading by a Just-In-Time compiler), and as a human readable assembly language representation.

其中，LLVM 提供了完整编译系统的中间层，并将中间语言（Intermediate Repressentation, IR）从编译器取出并进行最优化，最优化后的 IR 接着被转换及链接到目标平台的汇编语言。我们知道，传统的编译器主要结构为：

![传统编译器架构](https://assets.ng-tech.icu/item/20221227170240.png)

- Frontend：前端，词法分析、语法分析、语义分析、生成中间代码
- Optimizer：优化器，进行中间代码优化
- Backend：后端，生成机器码

LLVM 主要结构为：

![LLVM 结构](https://assets.ng-tech.icu/item/20221227170355.png)

也就是说，对于 LLVM 来说，不同的前后端使用统一的中间代码 LLVM IR。如果需要支持一种新的编程语言/硬件设备，那么只需要实现一个新的前端/后端就可以了，而优化截断是一个通用的阶段，针对统一的 LLVM IR，都不需要对于优化阶段修改。对比 GCC，其前端和后端基本耦合在一起，所以 GCC 支持一门新的语言或者目标平台会变得很困难。
