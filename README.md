[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![license: CC BY-NC-SA 4.0](https://img.shields.io/badge/license-CC%20BY--NC--SA%204.0-lightgrey.svg)][license-url]

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/wx-chevalier/ProgrammingLanguage-Notes">
    <img src="https://ngte-superbed.oss-cn-beijing.aliyuncs.com/item/header.svg" alt="Logo" style="width: 100vw;height: 400px" />
  </a>

  <p align="center">
    <a href="https://ng-tech.icu/books/ProgrammingLanguage-Notes"><strong>在线阅读 >> </strong></a>
    <br />
    <br />
    
    <a href="https://github.com/wx-chevalier">代码实践</a>
    ·
    <a href="https://github.com/wx-chevalier/Awesome-Lists">参考资料</a>

  </p>
</p>

<!-- ABOUT THE PROJECT -->

# Introduction | 前言

![](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/item/20230417213337.png)

参考[某熊的技术之路指北 ☯](https://github.com/wx-chevalier/Developer-Zero-To-Mastery)中对于知识结构的划分，本仓库存放了笔者在编程语言方面学习与实践总结而来的笔记。

> Programmers who program “in” a language limit their thoughts to constructs that the language directly supports. If the language tools are primitive, the programmer’s thoughts will also be primitive.
>
> Programmers who program “into” a language first decide what thoughts they want to express, and then they determine how to express those thoughts using the tools provided by their specific language.
>
> -- Steve McConnell’s Code Complete

程序指一组指示计算机或其他具有消息处理能力装置每一步动作的指令；代码指一套转换信息的规则系统编码。编程的本质是把我们人类想做的事情通过代码命令计算机来执行。譬如设定好了 10 个步骤，也就是 10 条指令，然后计算机通过程序计数器（时钟周期振荡器）按步骤从存放指令的内存区域一条一条取出来，扔到 CPU 执行。每条指令都有一个唯一的地址，当遇到需要选择的时候，当前指令执行完就会把下一条要执行的指令的地址返回给程序计数器。

计算机底层程序一直都是已 0 和 1 为基础执行不同的指令，而代码却一直在进化。最原始的代码其实就是计算机执行的程序，但是由于程序是指导计算机运行的，硬件处通过高低电平来处理信号再计算信息。对于机器很快就可以执行，但是对于人类来讲确异常痛苦。刚开始我记住不同组合的指令代表的意义，慢慢就发展成助记符，来表示程序，从而有了代码的概念。有了助记符很方便人类的记忆，毕竟记住一个单词比记住一串数字要好记得多。我们从最开始用一连串 0 和 1 来写指令，到后面采用了助记符，才发展成汇编语言，而随着硬件的性能提升，旧的代码语言已经无法满足人们的需求，高级语言也应运而生。

纵观计算机发展历史，软件研发效率提升就是弥合现实世界和计算机二进制世界的鸿沟。从二进制编码到汇编，到 C 语言，到面向对象的 C++、Java。编程语言随着硬件的发展变得越来越接近人类语言，语法也越来越简练；本质上不同语言最终都是被翻译成计算机自己认识的机器程序来执行。从古老的 Fortran 到现在的 Java 或者 PHP，还有其他庞大的计算机语言家族，把这些语言抽象出共性来，那么基础的语言规则都是相同的。

![编程语言发展史](https://z3.ax1x.com/2021/05/06/gQx1Mj.png)

在这样一个优秀编程语言百花齐放的时代，掌握多门编程语言无疑是一件必须要做的事情。学习一门语言的基本语法并无意义，但是这是你真正了解这门语言魅力的基石。语言无论其语法之间具备多大的差异性，从 Java、C 这样严谨的语言到 Ruby ,Python, JavaScript 这样想怎么写就怎么写的脚本语言，在学习一门语言时需要知道的基本知识却是大同小异。编程语言的两大要素分别是数据类型（字符，字符串，整形，浮点型，集合等）与操作类型（顺序，判断，选择，循环，分支等）。在下面的知识脑图中，我们会了解编程语言学习中通用的知识点。

![mindmap](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/item/20230417213359.png)

# About

## Copyright & More | 延伸阅读

笔者所有文章遵循 [知识共享 署名-非商业性使用-禁止演绎 4.0 国际许可协议](https://creativecommons.org/licenses/by-nc-nd/4.0/deed.zh)，欢迎转载，尊重版权。您还可以前往 [NGTE Books](https://ng-tech.icu/books-gallery/) 主页浏览包含知识体系、编程语言、软件工程、模式与架构、Web 与大前端、服务端开发实践与工程架构、分布式基础架构、人工智能与深度学习、产品运营与创业等多类目的书籍列表：

[![NGTE Books](https://s2.ax1x.com/2020/01/18/19uXtI.png)](https://ng-tech.icu/books-gallery/)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]: https://img.shields.io/github/contributors/wx-chevalier/ProgrammingLanguage-Notes.svg?style=flat-square
[contributors-url]: https://github.com/wx-chevalier/ProgrammingLanguage-Notes/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/wx-chevalier/ProgrammingLanguage-Notes.svg?style=flat-square
[forks-url]: https://github.com/wx-chevalier/ProgrammingLanguage-Notes/network/members
[stars-shield]: https://img.shields.io/github/stars/wx-chevalier/ProgrammingLanguage-Notes.svg?style=flat-square
[stars-url]: https://github.com/wx-chevalier/ProgrammingLanguage-Notes/stargazers
[issues-shield]: https://img.shields.io/github/issues/wx-chevalier/ProgrammingLanguage-Notes.svg?style=flat-square
[issues-url]: https://github.com/wx-chevalier/ProgrammingLanguage-Notes/issues
[license-shield]: https://img.shields.io/github/license/wx-chevalier/ProgrammingLanguage-Notes.svg?style=flat-square
[license-url]: https://github.com/wx-chevalier/ProgrammingLanguage-Notes/blob/master/LICENSE.txt
