# ES6 特性

ECMAScript 6 (以下简称 ES6)是 JavaScript 语言的下一代标准，已经在 2015 年 6 月正式发布了。Mozilla 公司将在这个标准的基础上，推出 JavaScript 2.0。ES6 的目标，是使得 JavaScript 语言可以用来编写大型的复杂的应用程序，成为企业级开发语言。标准的制定者有计划，以后每年发布一次标准，使用年份作为标准的版本。因为当前版本的 ES6 是在 2015 年发布的，所以又称 ECMAScript 2015。

# **Module & Module Loader**

ES2015 中加入的原生模块机制支持可谓是意义最重大的 feature 了，且不说目前市面上五花八门的 module/loader 库，各种不同实现机制

互不兼容也就罢了 ( 其实这也是非常大的问题 )，关键是那些模块定义 / 装载语法都丑到爆炸，但是这也是无奈之举，在没有语言级别的支持下，js 只能做到这一

步，正所谓巧妇难为无米之炊。ES2016 中的 Module 机制借鉴自

CommonJS，同时又提供了更优雅的关键字及语法 ( 虽然也存在一些问题 )。遗憾的是同样有重大价值的 Module

Loader 在 2014 年底从 ES2015 草案中移除了，我猜测可能是对于浏览器而言 Module

Loader 的支持遭遇了一些技术上的难点，从而暂时性的舍弃了这一 feature。但是一个原生支持的模块加载器是非常有意义的，相信它不久后还是会回

归到 ES 规范中 ( 目前由 WHATWG 组织在单独维护 )。

2.  **Class**

    准确来说 class 关键字只是一个 js 里构造函数的语法糖而已，跟直接 function 写法无本质区别。只不过有了 Class 的原生支持后，js 的面向对象机制有了更多的可能性，比如衍生的 extends 关键字 ( 虽然也只是语法糖 )。

3.  **Promise & Reflect API**

    Promise 的诞生其实已经有几十年了，它被纳入 ES 规范最大意义在于，它将市面上各种异步实现库的最佳实践都标准化了。至于 Reflect API，它让 js 历史上第一次具备了元编程能力，这一特性足以让开发者们脑洞大开。

除此之外，ES2016 的相关草案也已经确定了一大部分其他 new features。这里提两个我比较感兴趣的 new feature：

1.  async/await ：协程。ES2016 中 async/await 实际是对 Generator&Promise 的上层封装，几乎同步的写法写异步比 Promise 更优雅更简单，非常值得期待。
2.  decorator ：装饰器，其实等同于 Java 里面的注解。注解机制对于大型应用的开发的作用想必不用我过多赘述了。用过的同学都说好。

而关于纯 ES6 语法在各大浏览器上的支持情况，可以查看[这里](http://kangax.github.io/compat-table/es6/)。另外推荐一个可以将 ES5 代码转化为可读的 ES6 代码的转化器：[lebab](https://github.com/mohebifar/lebab)
