# clang

clang 是一个以 LLVM 为后端的编译前端。编译前端主要负责 parse 源码、检查错误，并生成抽象语法树 Abstract Syntax Tree (AST)。相较于其他编译器生成的 AST，clang 生成的 AST 更加接近 C++ 源码，所以我们可以更加准确地在源码中进行查找和定位。并且，clang 还提供了丰富的库和 API，让我们能在 AST 上很方便地做遍历，搜索和修改等操作。

我们在 vscode 上用的代码自动补全工具 clangd（或 vim 的 YouCompleteMe）就是用 clang 来实现的。

![LLVM](https://assets.ng-tech.icu/item/20221227165326.png)

一般什么时候会用到 Clang：

- 需要基于编译器的 AST 对源码做精确的编辑：自动纠正不符合 coding style 的源码
- 需要引入自定义的编译错误和警告：禁止用裸指针创建共享指针，声明了变量但是没有使用
- 基于 C/C++ 源码的代码生成(code generation)：自动生成数据结构的序列化方法，反射方法

# Clang AST

Clang AST 的节点是由几种没有共同基类的类来组成（建模）的，其中比较常用的四种 class 是 Type, Decl, DeclContext, Stmt
