## JIT

作为解释型语言，JavaScript 的性能一直是其瓶颈之一。Google 在 2009 年在 V8 中引入了 JIT 技术 (Just in time compiling ) , JavaScript 瞬间提升了 20 － 40 倍的速度。JIT 基于运行期分析编译，而 Javascript 是一个没有类型的语言，于是， 大部分时间，JIT 编译器其实是在猜测 Javascript 中的类型，举个例子：

```
function add (a, b) { return a+b}
var c = add (1 + 2);

// 编译为
function add (int a, int b) { return a + b;} // a, b 被确定为 int 类型

// 有的开发者会做
var d = add ("hello", "world");
```

这种情况下，JIT 编译器只能推倒重来。JIT 带来的性能提升，有时候还没有这个重编的开销大， [Optimization killers · petkaantonov/bluebird Wiki · GitHub](https://github.com/petkaantonov/bluebird/wiki/Optimization-killers)。事实上，大部分时间 JIT 都不会生成优化代码，有字节码的，直接字节码，没有字节码的，粗粗编译下就结了，因为 JIT 自己也需要时间，除非是一个函数被使用过很多遍，否则不会被编译成机器码，因为编译花的时间可能比直接跑字节码还多。

## 静态类型检测

Typescript, Dart, JSX 为代表的，基本思想是， 我搞个其他的语言，这个语言是强类型的，所以程序猿们需要指定类型，然后我把它编译成 Javacript 不就行了嘛。
