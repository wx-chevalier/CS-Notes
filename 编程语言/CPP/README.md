[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![license: CC BY-NC-SA 4.0](https://img.shields.io/badge/license-CC%20BY--NC--SA%204.0-lightgrey.svg)][license-url]

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/wx-chevalier/Java-Notes">
    <img src="https://assets.ng-tech.icu/item/header.svg" alt="Logo" style="width: 100vw;height: 400px" />
  </a>

  <p align="center">
    <a href="https://ng-tech.icu/books/Java-Notes"><strong>在线阅读 >> </strong></a>
    <br />
    <br />
    <a href="https://github.com/wx-chevalier/cpp-examples">代码案例（cpp-examples）</a>
    ·
    <a href="https://github.com/wx-chevalier/Awesome-Lists">参考资料</a>

  </p>
</p>

<!-- ABOUT THE PROJECT -->

# 现代 C 与 C++ 开发基础

C++ 是一种令人兴奋的语言，它在 C 语言的基础 上添加了对面向对象编程和泛型编程的支持，在 20 世纪 90 年代便是最重要的编程语言之一，并在 21 世纪仍保持强劲势头。C++继承了 C 语言高 效、简洁、快速和可移植性的传统。C++ 1x (C++11/14, 甚至 C++17) 为传统 C++ 注入的大量特性使得整个 C++ 变得更加像一门现代化的语言。C++1x 不仅仅增强了 C++ 语言自身的可用性，auto 关键字语义的修改使得我们更加有信心来操控极度复杂的模板类型。同时还对语言运行期进行了大量的强化，Lambda 表达式的出现让 C++ 具有了几乎在现代的编程语言中已经司空见惯的匿名函数的闭包特性，右值引用的出现解决了 C++ 长期以来被人诟病的临时对象效率问题等等。C++ 1x 为自身的标准库增加了非常多的工具和方法，诸如在语言层面上提供了 std::thread 支持了并发编程，在不同平台上不再依赖于系统底层的 API，实现了语言层面的跨平台支持；std::regex 提供了完整的正则表达式支持等等。C++98 已经被实践证明了是一种非常成功的范型，而 C++ 1x 的出现，则进一步推动这种范型，让 C++ 成为系统程序设计和库开发更好的语言。

![C++ 版本变迁](https://assets.ng-tech.icu/item/20221231163035.png)

- C++ 编程语言的历史可以追溯到 **1979 年**，当时 Bjarne Stroustrup 为博士学位论文进行了一些开发。在 Stroustrup 可以使用的所有语言中，有一种被称为 Simula 的语言，顾名思义，它可能是一种主要为仿真而设计的语言。Simula 67 语言是 Stroustrup 使用的变体，被认为是支持面向对象编程范例的主要语言。Stroustrup 发现这种范例对包装开发很有帮助。但是，Simula 语言对于实践和实际使用而言太慢了。随后不久，Bjarne Stroustrup 希望通过支持面向对象范例来增强 C。他深入研究了 Smalltalk 的 OO 实现，以获取有关实现的想法。但是他不愿意为此放弃性能，因此他开始从事 “C with Classes (带有类的 C）” 的工作，希望 C++ 代码运行时应具有与 C 代码相似（或更好）的性能。

- **1983 年**，语言的名称从 “带有类的 C” 更改为 C++。C 语言中的 ++ 运算符是用于递增变量的运算符，它使您可以深入了解 Stroustrup 如何看待该语言。在此期间添加了许多新功能，其中最引人注目的是虚函数，函数重载，带有＆符号的引用，const 关键字和使用两个正斜杠的单行注释。

- **1985 年**，Stroustrup 出版了名为*“C++ 编程语言” 的书籍*。同年，C++ 被实现为商业产品。该语言尚未正式标准化，因此使该书成为非常重要的参考。该语言在 1989 年再次进行了更新，以包括受保护的成员和静态成员，以及从多个类的继承。

- **1990 年**，发行了*《带注释的 C++ 参考手册*》。同年，Borland 的 Turbo C++ 编译器将作为商业产品发布。Turbo C++ 添加了许多其他库，这些库会对 C++ 的开发产生相当大的影响。尽管 Turbo C++ 的最后一个稳定版本是 2006 年，但该编译器仍被广泛使用。

- **1998 年**，C++ 标准委员会发布了第一个 C++ ISO / IEC 14882：1998 国际标准，其非正式名称为 C++ 98。据说*《带注释的 C++ 参考手册》*对标准的制定产生了很大的影响。还包括标准模板库，该模板库于 1979 年开始概念开发。2003 年，该委员会对 1998 年标准所报告的多个问题做出了回应，并对其进行了相应的修订。更改的语言称为 C++ 03。

- **2005 年**，C++ 标准委员会发布了一份技术报告（称为 TR1），详细介绍了他们计划添加到最新 C++ 标准中的各种功能。新标准被非正式地称为 C++ 0x，因为它有望在第一个十年结束之前的某个时间发布。具有讽刺意味的是，新标准要到 2011 年年中才会发布。直到那时为止，已经发布了几份技术报告，并且一些编译器开始为新功能添加实验性支持。

- **2011 年中**，新的 C++ 标准（称为 C++ 11）完成。Boost 库项目对新标准产生了重大影响，其中一些新模块直接来自相应的 Boost 库。一些新功能包括正则表达式支持，全面的随机化库，新的 C++ 时间库，原子支持，标准线程库 ，一种新的 for 循环语法，提供的功能类似于某些其他语言中的 foreach 循环，auto 关键字，新的容器类，对联合和数组初始化列表以及可变参数模板的更好支持。

- **2014 年**，C++ 14（也称为 C++ 1y）作为 C++11 的一个小扩展发布，主要功能是错误修复和小的改进，国际标准投票程序草案于 2014 年 8 月中完成，加强 lambda 函数，constexpr 和类型推导特性。

- **2017 年**，发布 C++ 17 标准，C++ 17 提供了很多东西。增强了核心语言和库。

- **2020 年**，发布 C++20 标准，推出了很多重量级功能，其中比较重要的有：
  - Concepts：概念改变了我们思考和编程模板的方式。它们是模板参数的语义类别。它们使您可以直接在类型系统中表达您的意图。如果出了什么问题，您会收到清晰的错误消息。
  - Ranges library：新的 ranges 库使它可以直接在容器上执行算法，用管道符号组成算法，并将其应用于无限数据流。
  - Coroutines：由于协程，C++ 中的异步编程成为主流。协程是协作任务，事件循环，无限数据流或管道的基础。
  - Modules：模块克服了头文件的限制。头文件和源文件的分离变得和预处理器一样过时了。最后，我们有更快的构建时间和更轻松的构建软件包的方法。
  - Concurrency：Atomic Smart Pointers, Joining & Cancellable Threads,The C20 Synchronization Library，增强了 C++ 并发编程能力；

# About

## Links

- https://www.cnblogs.com/QG-whz/category/685776.html?page=2
- http://c.biancheng.net/view/vip_2242.html

## Copyright & More | 延伸阅读

笔者所有文章遵循[知识共享 署名 - 非商业性使用 - 禁止演绎 4.0 国际许可协议](https://creativecommons.org/licenses/by-nc-nd/4.0/deed.zh)，欢迎转载，尊重版权。您还可以前往 [NGTE Books](https://ng-tech.icu/books-gallery/) 主页浏览包含知识体系、编程语言、软件工程、模式与架构、Web 与大前端、服务端开发实践与工程架构、分布式基础架构、人工智能与深度学习、产品运营与创业等多类目的书籍列表：

[![NGTE Books](https://s2.ax1x.com/2020/01/18/19uXtI.png)](https://ng-tech.icu/books-gallery/)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]: https://img.shields.io/github/contributors/wx-chevalier/Java-Notes.svg?style=flat-square
[contributors-url]: https://github.com/wx-chevalier/Java-Notes/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/wx-chevalier/Java-Notes.svg?style=flat-square
[forks-url]: https://github.com/wx-chevalier/Java-Notes/network/members
[stars-shield]: https://img.shields.io/github/stars/wx-chevalier/Java-Notes.svg?style=flat-square
[stars-url]: https://github.com/wx-chevalier/Java-Notes/stargazers
[issues-shield]: https://img.shields.io/github/issues/wx-chevalier/Java-Notes.svg?style=flat-square
[issues-url]: https://github.com/wx-chevalier/Java-Notes/issues
[license-shield]: https://img.shields.io/github/license/wx-chevalier/Java-Notes.svg?style=flat-square
[license-url]: https://github.com/wx-chevalier/Java-Notes/blob/master/LICENSE.txt
