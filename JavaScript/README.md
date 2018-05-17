![封面](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/8/1/1-roedigbmFjRYkZobdZWuKg.jpeg)

# [现代 JavaScript 开发：语法基础与工程实践](https://parg.co/bxN)

`Copyright © 王下邀月熊`

历经二十载风云变幻，JavaScript 也终于成为了一流的语言，在前端开发、服务端开发、嵌入式开发乃至于机器学习与数据挖掘、操作系统开发等各个领域都有不俗的表现。而在这不断的变化之后，也有很多语法或者模式成了明日黄花；本系列文章即是希望为读者总结与呈现出最新的应该掌握的 JavaScript 语法基础与实践基础。

# 前言

JavaScript 的设计与语法一直为人所诟病，不过正如 Zeit 的 CEO Guillermo Rauch 所言：JavaScript 虽然生于泥沼，但是在这么多年不断地迭代中，它也慢慢被开发者与市场所认可，最终化茧成蝶，被广泛地应用在从客户端到服务端，从应用开发、系统构建到数据分析的各个领域。JavaScript 最薄弱的一点在于其是解释性的无类型的语言，这一点让其在大型项目或者系统开发中充满了很多的性能瓶颈或者不稳定性；譬如在 JavaScript 中某个函数可以接受任意数目、任意类型的参数，而 Java 则会在编译时即检测参数类型是否符合预期。早期的 JavaScript 仅被用于为网页添加简单的用户交互，譬如按钮响应事件或者发送 Ajax 请求；不过随着 Webpack 等现代构建工具的发展，开发者可以更加工程化地进行高效的前端项目开发，并且整个网页的加载性能也大大提高，譬如 PWA 等现代 Web 技术能够使 Web 应用拥有与原生应用相近的用户体验。

我喜爱这门语言，所以我希望能够以绵薄之力让更多的人无痛地使用它。本系列中提及的很多代码实现与工具库可以参阅笔者的 [coding-snippets/js](https://github.com/wxyyxc1992/coding-snippets) 以及 [js-swissgear](https://github.com/wxyyxc1992/coding-snippets/tree/master/js)。

## 参考

* [Collection of helpful tips and tricks for VS Code.](https://github.com/Microsoft/vscode-tips-and-tricks)

## 版权

![](https://parg.co/bDY) ![](https://parg.co/bDm)

笔者所有文章遵循 [知识共享 署名-非商业性使用-禁止演绎 4.0 国际许可协议](https://creativecommons.org/licenses/by-nc-nd/4.0/deed.zh)，欢迎转载，尊重版权。如果觉得本系列对你有所帮助，欢迎给我家布丁买点狗粮(支付宝扫码)~

![](https://github.com/wxyyxc1992/OSS/blob/master/2017/8/1/Buding.jpg?raw=true)

# 目录

* [导论](./导论/Index.md)

  * [概述](./导论/概述.md): JavaScript 概述
  * [现代 JavaScript 开发](./导论/现代%20JavaScript%20开发.md): 现代 JavaScript 开发
  * [JavaScript 编年史](./导论/编年史.md):
  * [环境配置与工具链](./导论/环境配置与工具链.md): Visual Studio Code

* [表达式与变量](./表达式与变量/Index.md)

  * [变量作用域与提升](./表达式与变量/变量作用域与提升.md): 变量作用域与提升
  * [变量声明与赋值](./表达式与变量/变量声明与赋值.md): 变量声明与赋值

* [控制流](./控制流/Index.md)

  * [ErrorHandling-en](./控制流/ErrorHandling-en.md):
  * [异常处理](./控制流/异常处理.md): JavaScript 中的异常处理
  * [条件判断](./控制流/条件判断.md):
  * [流程控制](./控制流/流程控制.md): JavaScript 流程控制资料索引

* [数据结构](./数据结构/Index.md)

  * [基本类型](./数据结构/基本类型.md): JavaScript 中基本数据类型
  * [字符串与编码](./数据结构/字符串与编码.md): JavaScript 字符串
  * [时间与日期](./数据结构/时间与日期.md): JavaScript 时间与日期类型
  * [类型判断与转换](./数据结构/类型判断与转换.md): JavaScript 中类型判断与转换
  * [正则表达式](./数据结构/正则表达式.md): JavaScript 正则表达式详解与实战

* [函数](./函数/Index.md)

  * [函数声明与闭包](./函数/函数声明与闭包.md): 函数声明与闭包
  * [函数调用与 this 绑定](./函数/函数调用与%20this%20绑定.md): 函数调用与 this 绑定
  * [装饰器](./函数/装饰器.md): JavaScript 装饰器学习与实战
  * [迭代器与生成器](./函数/迭代器与生成器.md): 迭代器

* [函数式编程](./函数式编程/Index.md)

  * [JavaScript 函数式编程综述](./函数式编程/JavaScript%20函数式编程综述.md): JavaScript 函数式编程
  * [纯函数与不可变对象](./函数式编程/纯函数与不可变对象.md): 纯函数与不可变对象

* [类与对象](./类与对象/Index.md)

  * [原型链与类的继承](./类与对象/原型链与类的继承.md): 原型链与类的继承
  * [基于 decorator-x 的实体类增强](./类与对象/基于%20decorator-x%20的实体类增强.md): 基于 decorator-x 的实体类增强
  * [面向对象编程](./类与对象/面向对象编程.md): JavaScript Object Oriented Programming

* [模块化](./模块化/Index.md)

  * [ES Modules](./模块化/ES%20Modules.md): ES Modules
  * [JavaScript 模块演化简史](./模块化/JavaScript%20模块演化简史.md): 命名空间模式

* [异步并发](./异步并发/Index.md)

  * [Event Loop 机制详解与实践应用](./异步并发/Event%20Loop%20.md): 1. 事件循环机制详解与实践应用
  * [异步编程综述](./异步并发/异步编程综述.md): JavaScript 异步编程
  * [Promise/A 内部原理与常见接口实现](./异步并发/Promise.md): Promise/A 标准实现与异步模式
  * [Web Worker](./异步并发/Web%20Worker.md): Web Worker

* [元编程](./元编程/Index.md)

  * [ES6 Proxy 原理与实践](./元编程/ES6%20Proxy%20原理与实践.md): 深入浅出 ES6 Proxy 与 Reflect
  * [属性监听与数据绑定](./元编程/属性监听与数据绑定.md):
  * [深入浅出 ES6 Reflect](./元编程/深入浅出%20ES6%20Reflect.md):

* [类型系统](./类型系统/Index.md)

  * [JavaScript 类型系统概述](./类型系统/JavaScript%20类型系统概述.md):
  * [基于 Flow 的静态类型检测](./类型系统/基于%20Flow%20的静态类型检测.md): 基于 Flow 的静态类型检测

* [TypeScript](./TypeScript/Index.md)

  * [介绍配置与语法基础](./TypeScript/介绍配置与语法基础.md): TypeScript 语法基础与工程实践

* [单元测试](./单元测试/Index.md)

* [样式指南](./样式指南/Index.md)

  * [2016-编写高性能的 JavaScript](./样式指南/2016-编写高性能的%20JavaScript.md): Monomorphism: 单态性
  * [Clean JavaScript: 写出整洁的 JavaScript 代码](./样式指南/Clean%20JavaScript:%20写出整洁的%20JavaScript%20代码.md): Introduction: 简介
  * [某熊的 Opinionated JavaScript 编程样式指南](./样式指南/某熊的%20Opinionated%20JavaScript%20编程样式指南.md): 某熊的 Opinionated JavaScript 编程样式指南

* [语法分析](./语法分析/Index.md)

  * [Babel](./语法分析/Babel.md): 基于 Babel 的 JavaScript 语法树构造与代码转化

* [剖析 V8 引擎](./剖析%20V8%20引擎/Index.md)

  * [JIT](./剖析%20V8%20引擎/JIT.md): JIT
  * [Object 内存结构与属性访问](./剖析%20V8%20引擎/Object%20内存结构与属性访问.md): V8 Object 内存结构与属性访问
  * [V8 架构与优化](./剖析%20V8%20引擎/V8%20架构与优化.md): V8 架构与优化
