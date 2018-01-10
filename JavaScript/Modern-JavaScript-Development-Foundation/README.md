![封面](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/8/1/1-roedigbmFjRYkZobdZWuKg.jpeg)

# [现代 JavaScript 开发：语法基础与工程实践](https://parg.co/bxN)

`Copyright © 王下邀月熊`

历经二十载风云变幻，JavaScript 也终于成为了一流的语言，在前端开发、服务端开发、嵌入式开发乃至于机器学习与数据挖掘、操作系统开发等各个领域都有不俗的表现。而在这不断的变化之后，也有很多语法或者模式成了明日黄花；本系列文章即是希望为读者总结与呈现出最新的应该掌握的 JavaScript 语法基础与实践基础。

# 前言

JavaScript 的设计与语法一直为人所诟病，不过正如 Zeit 的 CEO Guillermo Rauch 所言：JavaScript 虽然生于泥沼，但是在这么多年不断地迭代中，它也慢慢被开发者与市场所认可，最终化茧成蝶，被广泛地应用在从客户端到服务端，从应用开发、系统构建到数据分析的各个领域。JavaScript 最薄弱的一点在于其是解释性的无类型的语言，这一点让其在大型项目或者系统开发中充满了很多的性能瓶颈或者不稳定性；譬如在 JavaScript 中某个函数可以接受任意数目、任意类型的参数，而 Java 则会在编译时即检测参数类型是否符合预期。早期的 JavaScript 仅被用于为网页添加简单的用户交互，譬如按钮响应事件或者发送 Ajax 请求；不过随着 Webpack 等现代构建工具的发展，开发者可以更加工程化地进行高效的前端项目开发，并且整个网页的加载性能也大大提高，譬如 PWA 等现代 Web 技术能够使 Web 应用拥有与原生应用相近的用户体验。

我喜爱这门语言，所以我希望能够以绵薄之力让更多的人无痛地使用它。

Next Milestone: 0.1

## 参考

* [Collection of helpful tips and tricks for VS Code.](https://github.com/Microsoft/vscode-tips-and-tricks)

## 版权

![](https://parg.co/bDY) ![](https://parg.co/bDm)

笔者所有文章遵循 [知识共享 署名-非商业性使用-禁止演绎 4.0 国际许可协议](https://creativecommons.org/licenses/by-nc-nd/4.0/deed.zh)，欢迎转载，尊重版权。如果觉得本系列对你有所帮助，欢迎给我家布丁买点狗粮（支付宝扫码）~

![](https://github.com/wxyyxc1992/OSS/blob/master/2017/8/1/Buding.jpg?raw=true)

# 目录

* [TypeScript](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/TypeScript/Index.md)
  * [环境配置与基本使用](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/TypeScript/%E7%8E%AF%E5%A2%83%E9%85%8D%E7%BD%AE%E4%B8%8E%E5%9F%BA%E6%9C%AC%E4%BD%BF%E7%94%A8.md): TypeScript 语法基础与工程实践
* [WebAssembly](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/WebAssembly/Index.md)
  * [WebAssembly 101: a developer's first steps](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/WebAssembly/WebAssembly%20101:%20a%20developer's%20first%20steps.md): 编译环境搭建
* [元编程](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E5%85%83%E7%BC%96%E7%A8%8B/Index.md)
  * [ES6 Proxy 原理与实践](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%85%83%E7%BC%96%E7%A8%8B/ES6%20Proxy%20%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B5.md): 深入浅出 ES6 Proxy 与 Reflect
  * [属性监听与数据绑定](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%85%83%E7%BC%96%E7%A8%8B/%E5%B1%9E%E6%80%A7%E7%9B%91%E5%90%AC%E4%B8%8E%E6%95%B0%E6%8D%AE%E7%BB%91%E5%AE%9A.md):
  * [深入浅出 ES6 Reflect](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%85%83%E7%BC%96%E7%A8%8B/%E6%B7%B1%E5%85%A5%E6%B5%85%E5%87%BA%20ES6%20Reflect.md):
* [内置数据结构](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E5%86%85%E7%BD%AE%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84/Index.md)
  * [基础类型](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%86%85%E7%BD%AE%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84/%E5%9F%BA%E7%A1%80%E7%B1%BB%E5%9E%8B.md): Symbol
  * [序列类型](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%86%85%E7%BD%AE%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84/%E5%BA%8F%E5%88%97%E7%B1%BB%E5%9E%8B.md): Array-Like
  * [数值类型与运算符](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%86%85%E7%BD%AE%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84/%E6%95%B0%E5%80%BC%E7%B1%BB%E5%9E%8B%E4%B8%8E%E8%BF%90%E7%AE%97%E7%AC%A6.md): 隐式类型转换
  * [映射类型](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%86%85%E7%BD%AE%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84/%E6%98%A0%E5%B0%84%E7%B1%BB%E5%9E%8B.md): Object
* [函数](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E5%87%BD%E6%95%B0/Index.md)
  * [函数声明与闭包](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%87%BD%E6%95%B0/%E5%87%BD%E6%95%B0%E5%A3%B0%E6%98%8E%E4%B8%8E%E9%97%AD%E5%8C%85.md):
  * [函数调用与 this 绑定](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%87%BD%E6%95%B0/%E5%87%BD%E6%95%B0%E8%B0%83%E7%94%A8%E4%B8%8E%20this%20%E7%BB%91%E5%AE%9A.md): 函数执行
  * [原型链与构造函数](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%87%BD%E6%95%B0/%E5%8E%9F%E5%9E%8B%E9%93%BE%E4%B8%8E%E6%9E%84%E9%80%A0%E5%87%BD%E6%95%B0.md):
  * [装饰器](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%87%BD%E6%95%B0/%E8%A3%85%E9%A5%B0%E5%99%A8.md):
  * [迭代器与生成器](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%87%BD%E6%95%B0/%E8%BF%AD%E4%BB%A3%E5%99%A8%E4%B8%8E%E7%94%9F%E6%88%90%E5%99%A8.md):
* [函数式编程](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E5%87%BD%E6%95%B0%E5%BC%8F%E7%BC%96%E7%A8%8B/Index.md)
  * [JavaScript 函数式编程综述](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%87%BD%E6%95%B0%E5%BC%8F%E7%BC%96%E7%A8%8B/JavaScript%20%E5%87%BD%E6%95%B0%E5%BC%8F%E7%BC%96%E7%A8%8B%E7%BB%BC%E8%BF%B0.md):
  * [纯函数与不可变对象](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%87%BD%E6%95%B0%E5%BC%8F%E7%BC%96%E7%A8%8B/%E7%BA%AF%E5%87%BD%E6%95%B0%E4%B8%8E%E4%B8%8D%E5%8F%AF%E5%8F%98%E5%AF%B9%E8%B1%A1.md):
* [剖析 V8 引擎](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E5%89%96%E6%9E%90%20V8%20%E5%BC%95%E6%93%8E/Index.md)
  * [Object 内存结构与属性访问](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%89%96%E6%9E%90%20V8%20%E5%BC%95%E6%93%8E/Object%20%E5%86%85%E5%AD%98%E7%BB%93%E6%9E%84%E4%B8%8E%E5%B1%9E%E6%80%A7%E8%AE%BF%E9%97%AE.md): V8 Object 内存结构与属性访问
  * [深入 JavaScript 运行机制](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%89%96%E6%9E%90%20V8%20%E5%BC%95%E6%93%8E/%E6%B7%B1%E5%85%A5%20JavaScript%20%E8%BF%90%E8%A1%8C%E6%9C%BA%E5%88%B6.md):
* [异步编程](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E5%BC%82%E6%AD%A5%E7%BC%96%E7%A8%8B/Index.md)
  * [JavaScript 异步编程综述](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%BC%82%E6%AD%A5%E7%BC%96%E7%A8%8B/JavaScript%20%E5%BC%82%E6%AD%A5%E7%BC%96%E7%A8%8B%E7%BB%BC%E8%BF%B0.md): JavaScript 异步编程
* [Promise](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/Promise/Index.md)
  * [A 标准实现与异步模式](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E5%BC%82%E6%AD%A5%E7%BC%96%E7%A8%8B/Promise/A%20%E6%A0%87%E5%87%86%E5%AE%9E%E7%8E%B0%E4%B8%8E%E5%BC%82%E6%AD%A5%E6%A8%A1%E5%BC%8F.md): Promise/A 标准实现与异步模式
* [概述](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E6%A6%82%E8%BF%B0/Index.md)
  * [ECMAScript 各版本特性概述](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E6%A6%82%E8%BF%B0/ECMAScript%20%E5%90%84%E7%89%88%E6%9C%AC%E7%89%B9%E6%80%A7%E6%A6%82%E8%BF%B0.md): ECMAScript 各版本特性概述
  * [JavaScript 编年史](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E6%A6%82%E8%BF%B0/JavaScript%20%E7%BC%96%E5%B9%B4%E5%8F%B2.md):
  * [现代 JavaScript 开发](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E6%A6%82%E8%BF%B0/%E7%8E%B0%E4%BB%A3%20JavaScript%20%E5%BC%80%E5%8F%91.md): 现代 JavaScript 开发
* [模块化与开发工具](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E6%A8%A1%E5%9D%97%E5%8C%96%E4%B8%8E%E5%BC%80%E5%8F%91%E5%B7%A5%E5%85%B7/Index.md)
  * [ES Modules](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E6%A8%A1%E5%9D%97%E5%8C%96%E4%B8%8E%E5%BC%80%E5%8F%91%E5%B7%A5%E5%85%B7/ES%20Modules.md): ES Modules
  * [JavaScript 模块演化简史](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E6%A8%A1%E5%9D%97%E5%8C%96%E4%B8%8E%E5%BC%80%E5%8F%91%E5%B7%A5%E5%85%B7/JavaScript%20%E6%A8%A1%E5%9D%97%E6%BC%94%E5%8C%96%E7%AE%80%E5%8F%B2.md): 命名空间模式
  * [开发环境与辅助工具](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E6%A8%A1%E5%9D%97%E5%8C%96%E4%B8%8E%E5%BC%80%E5%8F%91%E5%B7%A5%E5%85%B7/%E5%BC%80%E5%8F%91%E7%8E%AF%E5%A2%83%E4%B8%8E%E8%BE%85%E5%8A%A9%E5%B7%A5%E5%85%B7.md): Visual Studio Code
* [深入 JavaScript 运行机制](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E6%B7%B1%E5%85%A5%20JavaScript%20%E8%BF%90%E8%A1%8C%E6%9C%BA%E5%88%B6/Index.md)
  * [Event Loop 机制详解与实践应用](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E6%B7%B1%E5%85%A5%20JavaScript%20%E8%BF%90%E8%A1%8C%E6%9C%BA%E5%88%B6/Event%20Loop%20%E6%9C%BA%E5%88%B6%E8%AF%A6%E8%A7%A3%E4%B8%8E%E5%AE%9E%E8%B7%B5%E5%BA%94%E7%94%A8.md): 1. 事件循环机制详解与实践应用
* [类与对象](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E7%B1%BB%E4%B8%8E%E5%AF%B9%E8%B1%A1/Index.md)
  * [原型链与类的继承](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E7%B1%BB%E4%B8%8E%E5%AF%B9%E8%B1%A1/%E5%8E%9F%E5%9E%8B%E9%93%BE%E4%B8%8E%E7%B1%BB%E7%9A%84%E7%BB%A7%E6%89%BF.md):
  * [基于 decorator-x 的实体类增强](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E7%B1%BB%E4%B8%8E%E5%AF%B9%E8%B1%A1/%E5%9F%BA%E4%BA%8E%20decorator-x%20%E7%9A%84%E5%AE%9E%E4%BD%93%E7%B1%BB%E5%A2%9E%E5%BC%BA.md):
  * [类的声明与实例化](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E7%B1%BB%E4%B8%8E%E5%AF%B9%E8%B1%A1/%E7%B1%BB%E7%9A%84%E5%A3%B0%E6%98%8E%E4%B8%8E%E5%AE%9E%E4%BE%8B%E5%8C%96.md):
  * [类的继承与封装](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E7%B1%BB%E4%B8%8E%E5%AF%B9%E8%B1%A1/%E7%B1%BB%E7%9A%84%E7%BB%A7%E6%89%BF%E4%B8%8E%E5%B0%81%E8%A3%85.md):
* [类型系统](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E7%B1%BB%E5%9E%8B%E7%B3%BB%E7%BB%9F/Index.md)
  * [JavaScript 类型系统概述](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E7%B1%BB%E5%9E%8B%E7%B3%BB%E7%BB%9F/JavaScript%20%E7%B1%BB%E5%9E%8B%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md):
  * [基于 Flow 的静态类型检测](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E7%B1%BB%E5%9E%8B%E7%B3%BB%E7%BB%9F/%E5%9F%BA%E4%BA%8E%20Flow%20%E7%9A%84%E9%9D%99%E6%80%81%E7%B1%BB%E5%9E%8B%E6%A3%80%E6%B5%8B.md): 基于 Flow 的静态类型检测
* [表达式与控制流](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E8%A1%A8%E8%BE%BE%E5%BC%8F%E4%B8%8E%E6%8E%A7%E5%88%B6%E6%B5%81/Index.md)
  * [变量作用域与提升](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E8%A1%A8%E8%BE%BE%E5%BC%8F%E4%B8%8E%E6%8E%A7%E5%88%B6%E6%B5%81/%E5%8F%98%E9%87%8F%E4%BD%9C%E7%94%A8%E5%9F%9F%E4%B8%8E%E6%8F%90%E5%8D%87.md): 变量作用域与提升
  * [变量声明与赋值](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E8%A1%A8%E8%BE%BE%E5%BC%8F%E4%B8%8E%E6%8E%A7%E5%88%B6%E6%B5%81/%E5%8F%98%E9%87%8F%E5%A3%B0%E6%98%8E%E4%B8%8E%E8%B5%8B%E5%80%BC.md): 变量声明与赋值
  * [条件判断](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E8%A1%A8%E8%BE%BE%E5%BC%8F%E4%B8%8E%E6%8E%A7%E5%88%B6%E6%B5%81/%E6%9D%A1%E4%BB%B6%E5%88%A4%E6%96%AD.md):
* [语法分析](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E8%AF%AD%E6%B3%95%E5%88%86%E6%9E%90/Index.md)
  * [Babel](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E8%AF%AD%E6%B3%95%E5%88%86%E6%9E%90/Babel.md): 基于 Babel 的 JavaScript 语法树构造与代码转化
* [集合类型](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E9%9B%86%E5%90%88%E7%B1%BB%E5%9E%8B/Index.md)
  * [序列类型](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E9%9B%86%E5%90%88%E7%B1%BB%E5%9E%8B/%E5%BA%8F%E5%88%97%E7%B1%BB%E5%9E%8B.md):
  * [映射类型](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E9%9B%86%E5%90%88%E7%B1%BB%E5%9E%8B/%E6%98%A0%E5%B0%84%E7%B1%BB%E5%9E%8B.md):
* [面试](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/JavaScript/Modern-JavaScript-Development-Foundation/%E9%9D%A2%E8%AF%95/Index.md)
  * [Interview Algorithm Questions in Javascript](https://github.com/wxyyxc1992/Domain-of-ProgrammingLanguage/blob/master/JavaScript/Modern-JavaScript-Development-Foundation/%E9%9D%A2%E8%AF%95/Interview%20Algorithm%20Questions%20in%20Javascript.md): JavaScript Specification
