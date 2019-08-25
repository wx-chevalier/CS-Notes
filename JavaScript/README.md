![封面](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/8/1/1-roedigbmFjRYkZobdZWuKg.jpeg)

# [现代 JavaScript 开发：语法基础与工程实践](https://url.wx-coder.cn/lrKga)

历经二十载风云变幻，JavaScript 也终于成为了一流的语言，在前端开发、服务端开发、嵌入式开发乃至于机器学习与数据挖掘、操作系统开发等各个领域都有不俗的表现。而在这不断的变化之后，也有很多语法或者模式成了明日黄花；本系列文章即是希望为读者总结与呈现出最新的应该掌握的 JavaScript 语法基础与实践基础。

JavaScript 的设计与语法一直为人所诟病，不过正如 Zeit 的 CEO Guillermo Rauch 所言：JavaScript 虽然生于泥沼，但是在这么多年不断地迭代中，它也慢慢被开发者与市场所认可，最终化茧成蝶，被广泛地应用在从客户端到服务端，从应用开发、系统构建到数据分析的各个领域。JavaScript 最薄弱的一点在于其是解释性的无类型的语言，这一点让其在大型项目或者系统开发中充满了很多的性能瓶颈或者不稳定性；譬如在 JavaScript 中某个函数可以接受任意数目、任意类型的参数，而 Java 则会在编译时即检测参数类型是否符合预期。早期的 JavaScript 仅被用于为网页添加简单的用户交互，譬如按钮响应事件或者发送 Ajax 请求；不过随着 Webpack 等现代构建工具的发展，开发者可以更加工程化地进行高效的前端项目开发，并且整个网页的加载性能也大大提高，譬如 PWA 等现代 Web 技术能够使 Web 应用拥有与原生应用相近的用户体验。

我喜爱这门语言，所以我希望能够以绵薄之力让更多的人无痛地使用它，在返回主页后您可以看到更多有关 JavaScript 的代码实践。本篇的所有参考资料声明在 [Awesome JavaScript Lists](https://ngte-al.gitbook.io/i/?q=JavaScript)。

![](https://i.postimg.cc/L8hGQznm/image.png)

# ECMAScript

ECMAScript 是 JavaScript 语言的国际标准，JavaScript 是 ECMAScript 的实现。要讲清楚这个问题，需要回顾历史。1996 年 11 月，JavaScript 的创造者 Netscape 公司，决定将 JavaScript 提交给国际标准化组织 ECMA，希望这种语言能够成为国际标准。次年，ECMA 发布 262 号标准文件(ECMA-262 )的第一版，规定了浏览器脚本语言的标准，并将这种语言称为 ECMAScript。这个版本就是 ECMAScript 1.0 版。

之所以不叫 JavaScript，有两个原因。一是商标，Java 是 Sun 公司的商标，根据授权协议，只有 Netscape 公司可以合法地使用 JavaScript 这个名字，且 JavaScript 本身也已经被 Netscape 公司注册为商标。二是想体现这门语言的制定者是 ECMA，不是 Netscape，这样有利于保证这门语言的开放性和中立性。因此，ECMAScript 和 JavaScript 的关系是，前者是后者的规格，后者是前者的一种实现。在日常场合，这两个词是可以互换的。

ECMAScript 定义了：

- [语言语法](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Lexical_grammar) – 语法解析规则、关键字、语句、声明、运算符等。
- [类型](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Data_structures) – 布尔型、数字、字符串、对象等。
- [原型和继承](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Inheritance_and_the_prototype_chain)
- 内建对象和函数的[标准库](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects) – [JSON](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/JSON)、[Math](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math)、[数组方法](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array)、[对象自省方法](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object)等。

ECMAScript 标准不定义 HTML 或 CSS 的相关功能，也不定义类似 DOM(文档对象模型)的[Web API](https://developer.mozilla.org/en-US/docs/Web/API)，这些都在独立的标准中进行定义。ECMAScript 涵盖了各种环境中 JS 的使用场景，无论是浏览器环境还是类似[node.js](http://nodejs.org/)的非浏览器环境。

## 特性阶段

- Stage 0 “strawman” — The starting point for all proposals. These can change significantly before advancing to the next stage. There is no acceptance criteria and anyone can make a new proposal for this stage. There doesn’t need to be any implementation and the spec isn’t held to any standard. This stage is intended to start a discussion about the feature. There are currently over twenty stage 0 proposals.

- Stage 1 “proposal” — An actual formal proposal. These require a “champion”(i.e. a member of TC39 committee). At this stage the API should be well thought out and any potential implementation challenges should be outlined. At this stage, a polyfill is developed and demos produced. Major changes might happen after this stage, so use with caution. Proposals at this stage include the long-awaited Observables type and the Promise.try function.

- Stage 2 “draft” — At this stage the syntax is precisely described using the formal TC39 spec language. Minor editorial changes might still happen after this stage, but the specification should be complete enough not to need major revisions. If a proposal makes it this far, it’s a good bet that the committee expects the feature to be included eventually.

- Stage 3 “candidate” — The proposal has approved and further changes will only occur at the request of implementation authors. Here is where you can expect implementation to begin in JavaScript engines. Polyfills for proposals at this stage are safe to use without worry.

- Stage 4 “finished” — Indicates that the proposal has been accepted and the specification has been merged with the main JavaScript spec. No further changes are expected. JavaScript engines are expected to ship their implementations. As of October 2017 there are nine finished proposals, most notably async functions.

# About

## Home & More | 延伸阅读

![](https://i.postimg.cc/59QVkFPq/image.png)

您可以通过以下导航来在 Gitbook 中阅读笔者的系列文章，涵盖了技术资料归纳、编程语言与理论、Web 与大前端、服务端开发与基础架构、云计算与大数据、数据科学与人工智能、产品设计等多个领域：

- 知识体系：《[Awesome Lists | CS 资料集锦](https://ngte-al.gitbook.io/i/)》、《[Awesome CheatSheets | 速学速查手册](https://ngte-ac.gitbook.io/i/)》、《[Awesome Interviews | 求职面试必备](https://github.com/wx-chevalier/Awesome-Interviews)》、《[Awesome RoadMaps | 程序员进阶指南](https://github.com/wx-chevalier/Awesome-RoadMaps)》、《[Awesome MindMaps | 知识脉络思维脑图](https://github.com/wx-chevalier/Awesome-MindMaps)》、《[Awesome-CS-Books | 开源书籍（.pdf）汇总](https://github.com/wx-chevalier/Awesome-CS-Books)》

- 编程语言：《[编程语言理论](https://ngte-pl.gitbook.io/i/)》、《[Java 实战](https://ngte-pl.gitbook.io/i/java/java)》、《[JavaScript 实战](https://ngte-pl.gitbook.io/i/javascript/javascript)》、《[Go 实战](https://ngte-pl.gitbook.io/i/go/go)》、《[Python 实战](https://ngte-pl.gitbook.io/i/python/python)》、《[Rust 实战](https://ngte-pl.gitbook.io/i/rust/rust)》

- 软件工程、模式与架构：《[编程范式与设计模式](https://ngte-se.gitbook.io/i/)》、《[数据结构与算法](https://ngte-se.gitbook.io/i/)》、《[软件架构设计](https://ngte-se.gitbook.io/i/)》、《[整洁与重构](https://ngte-se.gitbook.io/i/)》、《[研发方式与工具](https://ngte-se.gitbook.io/i/)》

* Web 与大前端：《[现代 Web 开发基础与工程实践](https://ngte-web.gitbook.io/i/)》、《[数据可视化](https://ngte-fe.gitbook.io/i/)》、《[iOS](https://ngte-fe.gitbook.io/i/)》、《[Android](https://ngte-fe.gitbook.io/i/)》、《[混合开发与跨端应用](https://ngte-fe.gitbook.io/i/)》

* 服务端开发实践与工程架构：《[服务端基础](https://ngte-be.gitbook.io/i/)》、《[微服务与云原生](https://ngte-be.gitbook.io/i/)》、《[测试与高可用保障](https://ngte-be.gitbook.io/i/)》、《[DevOps](https://ngte-be.gitbook.io/i/)》、《[Node](https://ngte-be.gitbook.io/i/)》、《[Spring](https://ngte-be.gitbook.io/i/)》、《[信息安全与渗透测试](https://ngte-be.gitbook.io/i/)》

* 分布式基础架构：《[分布式系统](https://ngte-infras.gitbook.io/i/)》、《[分布式计算](https://ngte-infras.gitbook.io/i/)》、《[数据库](https://ngte-infras.gitbook.io/i/)》、《[网络](https://ngte-infras.gitbook.io/i/)》、《[虚拟化与编排](https://ngte-infras.gitbook.io/i/)》、《[云计算与大数据](https://ngte-infras.gitbook.io/i/)》、《[Linux 与操作系统](https://ngte-infras.gitbook.io/i/)》

* 数据科学，人工智能与深度学习：《[数理统计](https://ngte-aidl.gitbook.io/i/)》、《[数据分析](https://ngte-aidl.gitbook.io/i/)》、《[机器学习](https://ngte-aidl.gitbook.io/i/)》、《[深度学习](https://ngte-aidl.gitbook.io/i/)》、《[自然语言处理](https://ngte-aidl.gitbook.io/i/)》、《[工具与工程化](https://ngte-aidl.gitbook.io/i/)》、《[行业应用](https://ngte-aidl.gitbook.io/i/)》

* 产品设计与用户体验：《[产品设计](https://ngte-pd.gitbook.io/i/)》、《[交互体验](https://ngte-pd.gitbook.io/i/)》、《[项目管理](https://ngte-pd.gitbook.io/i/)》

* 行业应用：《[行业迷思](https://github.com/wx-chevalier/Business-Series)》、《[功能域](https://github.com/wx-chevalier/Business-Series)》、《[电子商务](https://github.com/wx-chevalier/Business-Series)》、《[智能制造](https://github.com/wx-chevalier/Business-Series)》

此外，你还可前往 [xCompass](https://wx-chevalier.github.io/home/#/search) 交互式地检索、查找需要的文章/链接/书籍/课程；或者在 [MATRIX 文章与代码索引矩阵](https://github.com/wx-chevalier/Developer-Zero-To-Mastery)中查看文章与项目源代码等更详细的目录导航信息。最后，你也可以关注微信公众号：『**某熊的技术之路**』以获取最新资讯。
