[![返回目录](https://parg.co/USw)](https://parg.co/bxN)

# 现代 JavaScript 开发

# ECMAScript 的过去、现在与未来

WTF is ECMAScript?

Here’s what happened long, long ago:

JavaScript was originally named JavaScript in hopes of capitalizing on the success of Java.

Netscape then submitted JavaScript to ECMA International for Standardization. (ECMA is an organization that standardizes information)

This results in a new language standard, known as ECMAScript.

Put simply, ECMAScript is a standard. While JavaScript is the most popular implementation of that standard. JavaScript implements ECMAScript and builds on top of it.

Okay, so ‘ES’…?

ES is simply short for ECMAScript. Every time you see ES followed by a number, it is referencing an edition of ECMAScript. In fact, there are eight editions of ECMAScript published. Lets dive into them:

ES1, ES2, ES3, ES4

ES1: June 1997 — ES2: June 1998 — ES3: Dec. 1999 — ES4: Abandoned

I’ve grouped all of these together. These were the first 4 editions of ECMAScript, and to save time, we wont go too in-depth. Just know that the first three editions were annual, and the fourth was abandoned due to political differences.

ES5

December 2009: Nearly 10 years later, ES5 was released in 2009. It would then take almost six years for the next version of ECMAScript to be released.

ES6 / ES2015

June 2015: Perhaps the cause for all of your confusion begins here. You see, ES6 and ES2015 are the same thing.

ES6 was the popularized name prior to release. However, the committee that oversees ECMAScript specifications made the decision to move to annual updates. With this change, the edition was renamed to ES 2015 to reflect the year of release. Subsequent releases will therefor also be named according to the year they are released.

ES2016 (ES7)

June 2016: Seventh edition of ECMAScript.

ES2017 (ES8)

June 2017: Eighth edition of ECMAScript.

ES.Next

You may have also seen ES.Next used online. This term is dynamic and references the next version of ECMAScript coming out.

Why?

Each release brings updates and new features to the language.

Key Takeaways:

An update to ECMAscript can be expected annually.

Initial Editions of ECMAScript are named numerically, increasing by 1: ES1, ES2, ES3, ES4, ES5

New editions (starting with 2015) will be named ES followed by the year of release: ES2015, ES2016, ES2017

ECMAScript is a standard. JavaScript is the most popular implementation of that standard. Other implementations include: SpiderMonkey, V8, and ActionScript.

混淆 JavaScript 标准语法与 DOM API

[Node.js]() React Native、Weex

Stage 0 “strawman” — The starting point for all proposals. These can change significantly before advancing to the next stage. There is no acceptance criteria and anyone can make a new proposal for this stage. There doesn’t need to be any implementation and the spec isn’t held to any standard. This stage is intended to start a discussion about the feature. There are currently over twenty stage 0 proposals.

Stage 1 “proposal” — An actual formal proposal. These require a “champion”(i.e. a member of TC39 committee). At this stage the API should be well thought out and any potential implementation challenges should be outlined. At this stage, a polyfill is developed and demos produced. Major changes might happen after this stage, so use with caution. Proposals at this stage include the long-awaited Observables type and the Promise.try function.

Stage 2 “draft” — At this stage the syntax is precisely described using the formal TC39 spec language. Minor editorial changes might still happen after this stage, but the specification should be complete enough not to need major revisions. If a proposal makes it this far, it’s a good bet that the committee expects the feature to be included eventually.

Stage 3 “candidate” — The proposal has approved and further changes will only occur at the request of implementation authors. Here is where you can expect implementation to begin in JavaScript engines. Polyfills for proposals at this stage are safe to use without worry.

Stage 4 “finished” — Indicates that the proposal has been accepted and the specification has been merged with the main JavaScript spec. No further changes are expected. JavaScript engines are expected to ship their implementations. As of October 2017 there are nine finished proposals, most notably async functions.

# 语法纵览

模块化无疑是 ES6 中最令人激动的特性，保证了大型应用程序的健壮性、可扩展性与可重构性

Arrow Function

# 引擎与运行时：代码解析与执行

JavaScript 是典型的解释型语言，其需要运行在某个容器内：该容器首先将 JavaScript 代码转化为可执行的命令(语法解析以及 JIT)，然后注入某些全局变量以允许 JavaScript 与外部进行交互。所谓的 JavaScript 引擎(Engine )就是负责解析与编译，将 JavaScript 脚本转化为机器理解的命令；而 JavaScript 运行时(Runtime )这是提供了程序运行期间能够访问的内建库。譬如在浏览器中运行的代码，能够访问 Window 对象或者 DOM API，其就是由浏览器的 JavaScript 运行时提供的。而 Node.js 提供的运行时则包含了不同的库，譬如 Cluster 或者文件系统接口等等。所有的运行时都会包含基础的数据类型以及常见的 Console 对象这样的工具库。我们可以认为 Chrome 与 Node.js 共用相同的 Google V8 引擎，而拥有不同的运行时。

[这篇文章](https://parg.co/Uuv)对于 V8 的工作原理有非常不错的讲解

![](https://cdn-images-1.medium.com/max/2000/0*bN9YVBLw_tT1Xvte.)

![](https://s3.amazonaws.com/images.ponyfoo.com/uploads/addy-ad3b2ea8f9be48a18c4bdad5041a3237.png)

```sh
$ out/Debug/d8 --print-ast add.js
…
--- AST ---
FUNC at 12
. KIND 0
. SUSPEND COUNT 0
. NAME "add"
. PARAMS
. . VAR (0x7fbd5e818210) (mode = VAR) "x"
. . VAR (0x7fbd5e818240) (mode = VAR) "y"
. RETURN at 23
. . ADD at 32
. . . VAR PROXY parameter[0] (0x7fbd5e818210) (mode = VAR) "x"
. . . VAR PROXY parameter[1] (0x7fbd5e818240) (mode = VAR) "y"
```

![](https://s3.amazonaws.com/images.ponyfoo.com/uploads/ast-602ed6f747124b0888c0a032eba50bb2.png)

```sh
$ out/Debug/d8 --print-bytecode add.js
…
[generated bytecode for function: add]
Parameter count 3
Frame size 0
   12 E> 0x37738712a02a @    0 : 94                StackCheck
   23 S> 0x37738712a02b @    1 : 1d 02             Ldar a1
   32 E> 0x37738712a02d @    3 : 29 03 00          Add a0, [0]
   36 S> 0x37738712a030 @    6 : 98                Return
Constant pool (size = 0)
Handler Table (size = 16)
```

![](https://parg.co/UOA)

## JIT

## 优化器

作为解释型语言，JavaScript 的性能一直是其瓶颈之一。Google 在 2009 年在 V8 中引入了 JIT 技术 (Just in time compiling ) , JavaScript 瞬间提升了 20 － 40 倍的速度。JIT 基于运行期分析编译，而 Javascript 是一个没有类型的语言，于是， 大部分时间，JIT 编译器其实是在猜测 Javascript 中的类型，举个例子：

```js
function add (a, b) { return a+b}
var c = add (1 + 2);

// 编译为
function add (int a, int b) { return a + b;} // a, b 被确定为 int 类型

// 有的开发者会做
var d = add ("hello", "world");
```

这种情况下，JIT 编译器只能推倒重来。JIT 带来的性能提升，有时候还没有这个重编的开销大， [Optimization killers · petkaantonov/bluebird Wiki · GitHub](https://github.com/petkaantonov/bluebird/wiki/Optimization-killers)。事实上，大部分时间 JIT 都不会生成优化代码，有字节码的，直接字节码，没有字节码的，粗粗编译下就结了，因为 JIT 自己也需要时间，除非是一个函数被使用过很多遍，否则不会被编译成机器码，因为编译花的时间可能比直接跑字节码还多。

# 语法增强

## 类型转换

Webpack、Fuse 、 Rollup、Browserify

## 静态类型
