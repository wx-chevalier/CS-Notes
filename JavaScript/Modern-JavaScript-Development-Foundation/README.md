![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/8/1/1-roedigbmFjRYkZobdZWuKg.jpeg)

# 现代 JavaScript 开发：语法基础与实践技巧
Copyright © 2017 王下邀月熊

![](https://camo.githubusercontent.com/322fefce6b2264d9ff2ad35ea5dcd4622e437b04/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4c6963656e73652d434325323042592d2d4e432d2d5341253230342e302d626c75652e737667)
![](https://camo.githubusercontent.com/d4e0f63e9613ee474a7dfdc23c240b9795712c96/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f5052732d77656c636f6d652d627269676874677265656e2e737667)

历经二十载风云变幻，JavaScript 也终于成为了一流的语言，在前端开发、服务端开发、嵌入式开发乃至于机器学习与数据挖掘、操作系统开发等各个领域都有不俗的表现。而在这不断的变化之后，也有很多语法或者模式成了明日黄花；本系列文章即是希望为读者总结与呈现出最新的应该掌握的 JavaScript 语法基础与实践基础。


# 前言

JavaScript 的设计与语法一直为人所诟病，不过正如 Zeit 的 CEO Guillermo Rauch 所言：JavaScript 虽然生于泥沼，但是在这么多年不断地迭代中，它也慢慢被开发者与市场所认可，最终化茧成蝶，被广泛地应用在从客户端到服务端，从应用开发、系统构建到数据分析的各个领域。JavaScript 最薄弱的一点在于其是解释性的无类型的语言，这一点让其在大型项目或者系统开发中充满了很多的性能瓶颈或者不稳定性；譬如在 JavaScript 中某个函数可以接受任意数目、任意类型的参数，而 Java 则会在编译时即检测参数类型是否符合预期。早期的 JavaScript 仅被用于为网页添加简单的用户交互，譬如按钮响应事件或者发送 Ajax 请求；不过随着 Webpack 等现代构建工具的发展，开发者可以更加工程化地进行高效的前端项目开发，并且整个网页的加载性能也大大提高，譬如 PWA 等现代 Web 技术能够使 Web 应用拥有与原生应用相近的用户体验。

我喜爱这门语言，所以我希望能够以绵薄之力让更多的人无痛地使用它。

# 目录
> [返回目录](https://parg.co/bjK)；本系列文章仍处于筹备写作阶段，可以直接在本文件夹下查看相关章节；目前主要会分为以下几个章节：

- [概念与语法转换]()
    - [ECMAScript 的过去、现在与未来]()
    - [ECMAScript 各版本特性概述]()
    - [JavaScript 语法树与代码转化]()

- [模块化与构建工具]()
    - [JavaScript 模块演化简史]()
    - [ES Modules]()
    - [构建工具对比与解析]()
    - [开发环境与辅助工具]()

- [表达式与控制流]()
    - [变量声明与赋值]()
    - [变量作用域与提升]()

- [内置数据结构]()
    - [字符串]()
    - [正则表达式]()
    - [时间与日期]()
    - [序列类型]()
    - [映射类型]()

- [函数]()
    - [函数声明与闭包]()
    - [函数调用与 this 绑定]()
    - [原型链与构造函数]()
    - [迭代器与生成器]()
    - [装饰器]()    

- [函数式编程]()
    - [JavaScript 函数式编程综述]()
    - [纯函数与不可变数据]()

- [异步编程]()
    - [JavaScript 异步编程综述：回调、Promise、Generator、async/await]()
    - [Event Loop 详解与应用]()：函数调用栈，事件循环进程模型与应用，Node.js Event Loop
    - [Promise/A 标准与实现]()

- [面向对象编程]()
    - [类的声明与实例化]()
    - [类的封装与继承]()
    - [基于 decorator-x 的实体类增强]()
    
- [元编程]()
    - [ES6 Proxy 原理与实践]()
    - [深入浅出 ES6 Reflect]()
    - [属性监听与数据绑定]()

- [类型系统]()
    - [JavaScript 类型系统概述]()
    - [基于 Flow 的静态类型检测]()
    - [TypeScript]()

- [工程实践]()
    - [性能优化]()
    - [样式指南]()

- [设计模式]()
    - [Observable 观察者模式]()

- [WebAssembly]()

- [剖析 V8 引擎]()
    - [Object 内存结构与属性访问]()

- [面试]()

# 狗粮

如果觉得本系列对你有所帮助，欢迎给我家布丁买点狗粮~

![](https://github.com/wxyyxc1992/OSS/blob/master/2017/8/1/Buding.jpg?raw=true)


# 参考

- [Collection of helpful tips and tricks for VS Code.](https://github.com/Microsoft/vscode-tips-and-tricks)
