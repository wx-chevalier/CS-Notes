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

# 链接

- https://github.com/daumann/ECMAScript-new-features-list
