![](https://i.postimg.cc/632XHpJV/adding-programming-language-enterprise-blog-hero-1200x630.jpg)

# 编程语言语法基础与工程实践

参考[某熊的技术之路指北 ☯](https://github.com/wx-chevalier/Developer-Zero-To-Mastery)中对于知识结构的划分，本仓库存放了笔者在编程语言方面学习与实践总结而来的笔记。

# Nav | 导航

您可以通过以下任一方式阅读笔者的系列文章，涵盖了技术资料归纳、编程语言与理论、Web 与大前端、服务端开发与基础架构、云计算与大数据、数据科学与人工智能、产品设计等多个领域：

- 在 Gitbook 中在线浏览，每个系列对应各自的 Gitbook 仓库。

| [Awesome Lists](https://ngte-al.gitbook.io/i/) | [Awesome CheatSheets](https://ngte-ac.gitbook.io/i/) | [Awesome Interviews](https://github.com/wx-chevalier/Developer-Zero-To-Mastery/tree/master/Interview) | [Awesome RoadMaps](https://github.com/wx-chevalier/Developer-Zero-To-Mastery/tree/master/RoadMap) | [Awesome-CS-Books-Warehouse](https://github.com/wx-chevalier/Awesome-CS-Books-Warehouse) |
| ---------------------------------------------- | ---------------------------------------------------- | ----------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------- |


| [编程语言理论](https://ngte-pl.gitbook.io/i/) | [Java 实战](https://ngte-pl.gitbook.io/i/go/go) | [JavaScript 实战](https://ngte-pl.gitbook.io/i/javascript/javascript) | [Go 实战](https://ngte-pl.gitbook.io/i/go/go) | [Python 实战](https://ngte-pl.gitbook.io/i/python/python) | [Rust 实战](https://ngte-pl.gitbook.io/i/rust/rust) |
| --------------------------------------------- | ----------------------------------------------- | --------------------------------------------------------------------- | --------------------------------------------- | --------------------------------------------------------- | --------------------------------------------------- |


| [软件工程、数据结构与算法、设计模式、软件架构](https://ngte-se.gitbook.io/i/) | [现代 Web 开发基础与工程实践](https://ngte-web.gitbook.io/i/) | [大前端混合开发与数据可视化](https://ngte-fe.gitbook.io/i/) | [服务端开发实践与工程架构](https://ngte-be.gitbook.io/i/) | [分布式基础架构](https://ngte-infras.gitbook.io/i/) | [数据科学，人工智能与深度学习](https://ngte-aidl.gitbook.io/i/) | [产品设计与用户体验](https://ngte-pd.gitbook.io/i/) |
| ----------------------------------------------------------------------------- | ------------------------------------------------------------- | ----------------------------------------------------------- | --------------------------------------------------------- | --------------------------------------------------- | --------------------------------------------------------------- | --------------------------------------------------- |


- 前往 [xCompass https://wx-chevalier.github.io](https://wx-chevalier.github.io/home/#/search) 交互式地检索、查找需要的文章/链接/书籍/课程，或者关注微信公众号：某熊的技术之路。

![](https://i.postimg.cc/3RVYtbsv/image.png)

- 在下文的 [MATRIX 文章与代码矩阵 https://github.com/wx-chevalier/Developer-Zero-To-Mastery](https://github.com/wx-chevalier/Developer-Zero-To-Mastery) 中查看文章与项目的源代码。

| [ProgrammingLanguage Theory Primer/编程语言理论指南](./编程语言理论) | [JavaScript 篇](./JavaScript) | [Java 篇](./Java) | [Python 篇](./Python) | [Go 篇](./Go) | [Rust 篇](./Rust) |
| -------------------------------------------------------------------- | ----------------------------- | ----------------- | --------------------- | ------------- | ----------------- |


# 前言 | Preface

> Programmers who program “in” a language limit their thoughts to constructs that the language directly supports. If the language tools are primitive, the programmer’s thoughts will also be primitive.
>
> Programmers who program “into” a language first decide what thoughts they want to express, and then they determine how to express those thoughts using the tools provided by their specific language.
>
> -- Steve McConnell’s Code Complete

程序指一组指示计算机或其他具有消息处理能力装置每一步动作的指令；代码指一套转换信息的规则系统编码。编程的本质是把我们人类想做的事情通过代码命令计算机来执行。譬如设定好了 10 个步骤，也就是 10 条指令，然后计算机通过程序计数器（时钟周期振荡器）按步骤从存放指令的内存区域一条一条取出来，扔到 CPU 执行。每条指令都有一个唯一的地址，当遇到需要选择的时候，当前指令执行完就会把下一条要执行的指令的地址返回给程序计数器。

计算机底层程序一直都是已 0 和 1 为基础执行不同的指令，而代码却一直在进化。最原始的代码其实就是计算机执行的程序，但是由于程序是指导计算机运行的，硬件处通过高低电平来处理信号再计算信息。对于机器很快就可以执行，但是对于人类来讲确异常痛苦。刚开始我记住不同组合的指令代表的意义，慢慢就发展成助记符，来表示程序，从而有了代码的概念。有了助记符很方便人类的记忆，毕竟记住一个单词比记住一串数字要好记得多。我们从最开始用一连串 0 和 1 来写指令，到后面采用了助记符，才发展成汇编语言，而随着硬件的性能提升，旧的代码语言已经无法满足人们的需求，高级语言也应运而生。

纵观计算机发展历史，软件研发效率提升就是弥合现实世界和计算机二进制世界的鸿沟。从二进制编码到汇编，到 C 语言，到面向对象的 C++、JAVA。编程语言随着硬件的发展变得越来越接近人类语言，语法也越来越简练；本质上不同语言最终都是被翻译成计算机自己认识的机器程序来执行。从古老的 Fortran 到现在的 JAVA 或者 PHP，还有其他庞大的计算机语言家族，把这些语言抽象出共性来，那么基础的语言规则都是相同的。

编程语言的两大要素分别是数据类型（字符，字符串，整形，浮点型，集合等）与操作类型（顺序，判断，选择，循环，分支等）。

# 版权

![License: CC BY-NC-SA 4.0](https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg)
![](https://parg.co/bDm)

笔者所有文章遵循 [知识共享 署名-非商业性使用-禁止演绎 4.0 国际许可协议](https://creativecommons.org/licenses/by-nc-nd/4.0/deed.zh)，欢迎转载，尊重版权。如果觉得本系列对你有所帮助，欢迎给我家布丁买点狗粮(支付宝扫码)~

![default](https://i.postimg.cc/y1QXgJ6f/image.png)
