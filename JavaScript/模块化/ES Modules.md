[![返回目录](https://parg.co/USw)](https://parg.co/bxN)

# ES Modules

# 浏览器

目前主流浏览器中默认支持 ES2015 Modules 只有 Safari，而 Firefox 在 54 版本之后允许用户手动启用该特性。以 Firefox 为例，如果我们在浏览器中使用 ES2015 Modules，我们需要声明入口模块：

```js
<script type="module" scr="PATH/file.js" />
```

这里的 `module` 关键字就告诉浏览器该脚本中包含了对于其他脚本的导入语句，需要进行预先处理；不过问题来了，那么 JavaScript 解释器又该如何判断某个文件是否为模块。社区也经过很多轮的讨论，我们可以来看下简单的例子：

```
<!--index.html-->
<!DOCTYPE html>
<html>
  <head>
    <script type="module" src="main.js"></script>
  </head>
  <body>
  </body>
</html>
```

main.js 的代码实现如下：

```
// main.js
import utils from "./utils.js";


utils.alert(`
  JavaScript modules work in this browser:
  https://blog.whatwg.org/js-modules
`);
```

待导入的模块如下：

```
// utils.js
export default {
    alert: (msg)=>{
        alert(msg);
    }
};
```

我们可以发现，在 `import` 语句中我们提供了 `.js` 扩展名，这也是区别于打包工具的重要特性之一，往往打包工具中并不需要我们提供扩展名。此外，在浏览器中进行模块的动态加载，也要求待加载文件具有正确的 MIME 类型。我们常用的正确的模块地址譬如：

```
https://example.com/apples.js
http:example.com\pears.mjs (becomes http://example.com/pears.mjs as step 1 parses with no base URL)
//example.com/bananas
./strawberries.js.cgi
../lychees
/limes.jsx
data:text/javascript,export default ‘grapes’;
blob:https://whatwg.org/d0360e2f-caee-469f-9a2f-87d5b0456f6f
```

不过笔者觉得有个不错的特性在于浏览器中支持 CORS 协议，跨域加载其他域中的脚本。在浏览器中加载进来的模块与直接加载的脚本的作用域也是不一致的，并且不需要 `use strict` 声明其也默认处于严格模式下：

```
var x = 1;


alert(x === window.x);//false
alert(this === undefined);// true
```

浏览器对于模块的加载默认是异步延迟进行的，即模块脚本的加载并不会阻塞浏览器的解析行为，而是并发加载并在页面加载完毕后进行解析，也就是所有的模块脚本具有 `defer` 属性。我们也可以为脚本添加 `async` 属性，即指明该脚本会在加载完毕后立刻执行。这一点与传统的非模块脚本相比很大不同，传统的脚本会阻塞浏览器解析直到抓取完毕，在抓取之后也会立刻进行执行操作。整个加载流程如下所示：
![](https://hospodarets.com/img/blog/1482858323861214000.png)

# Node.js

> [利用 std/esm 在 Node.js 开发中使用 ES Modules](https://zhuanlan.zhihu.com/p/28478464) 整理自[ES Modules in Node Today!](https://parg.co/bjg)，从属于笔者的[现代 JavaScript 开发：语法基础与实践技巧](https://parg.co/bWW)系列中的模块化与构建章节。本文主要介绍了如何利用  std/esm 第三方库在 Node.js 应用中顺滑地使用 ES Modules 语法。

# 利用 std/esm 在 Node.js 开发中使用 ES Modules

随着主流浏览器逐步开始支持 ES Modules 标准，越来越多的目光投注于 Node.js 对于 ESM 的支持实现上；目前 Node.js 使用 CommonJS 作为官方的模块解决方案，虽然内置的模块方案促进了 Node.js 的流行，但是也为引入新的 ES Modules 造成了一定的阻碍。CommonJS 与 ES Modules 模块标准的对比如下：

```
// CJS
const a = require("./a")
module.exports = { a, b: 2 }


// ESM
import a from "./a"
export default { a, b: 2 }
```

鉴于 CommonJS 并不兼容于 ES Modules，Node.js 打算引入 `.mjs`(Modular JavaScript)文件扩展来指明模块解析规则；这个有点类似于目前对于 JSON 文件的解析，如果我们指明了载入 `.json` 格式文件，Node.js 会自动调用 `JSON.parse` 方法。Node.js  拟计划在 2020 年发布的 9.x 版本中引入内置的 ESM 支持，详细的 Node.js 中 ESM 实现规范查看 Node.js 官方文档 [ES Module Interoperability](https://parg.co/bjW)；而目前主流的办法即是采用 Rollup、Webpack 这样的构建工具或者 Babel 这样的转化工具来进行代码转化。

而近日正式发布的 [@std/esm](https://www.npmjs.com/package/@std/esm)  为我们提供了高性能的 Node.js 中 CommonJS 与 ES Modules 模块间调用，其能够作用于 Node.js 4.x 以上版本；它能够顺滑地集成到现有的 Webpack、Babel 环境中，并且支持不同模块使用不同的依赖版本。不同于目前的解决方案需要是发布编译之后的 CommonJS 格式的文件，[@std/esm] 能够以最小的代价的、按需转化的、动态缓存的方式来进行源代码转化，其基本命令行中的使用方式如下所示：

```
> require('@std/esm')
@std/esm enabled
> import path from 'path';
undefined
> path.join("Hello","World");
'Hello/World'
```

[@std/esm]  除了会自动识别 `.mjs` 扩展的文件之外，它还支持任何包含 `import/export`、Dynamic import、file URI scheme 等语句的文件，典型的用例如下：

```
// 首先安装依赖
npm i --save @std/esm




// index.js

import hello from "./main.js";

hello();



// main.js


import thing from "./constants.js";

export default function hello() {
  console.log(thing);
}



// constants.js

export default "Hello World!";


// 运行文件
node -r @std/esm index.js
// Hello World!
```

笔者在自己尝试的时候发现 @std/esm 还存在些 Bug，对于缓存代码的处理也并不完善，目前并不建议直接用于生产环境，但是有所了解还是不错的。@std/esm 官方给出的与 [Node.js 9](https://github.com/nodejs/node/pull/14369)  以及 CommonJS 模块的加载时间对比如下，可以发现还是很接近于内建的解决方案性能的：

* Loading CJS equivs was ~0.28 milliseconds per module
* Loading built-in ESM was ~0.51 milliseconds per module
* First `@std/esm` no cache run\* \*was ~1.56 milliseconds per module
* Secondary `@std/esm` cached runs were ~0.54 milliseconds per module
