[![返回目录](https://parg.co/USw)](https://parg.co/bxN)

# JavaScript 模块演化简史

当年 Brendan Eich 草创 JavaScript 之际，他应该无法想象 JavaScript 在未来二十年内发挥的巨大作用；同样作为广为诟病的过于随意的语言，缺乏强有力的模块化解决方案一直是 JavaScript 的缺陷之一。早期的 JavaScript 往往作为嵌入到 HTML 页面中的用于控制动画与简单的用户交互的脚本语言，我们习惯于将其直接嵌入到 `script` 标签中：

```
<!--html-->
<script type="application/javascript">
    // module1 code
    // module2 code
</script>
```

不过随着单页应用与富客户端的流行，不断增长的代码库也急需合理的代码分割与依赖管理的解决方案，这也就是我们在软件工程领域所熟悉的模块化(Modularity)。所谓模块化主要是解决代码分割、作用域隔离、模块之间的依赖管理以及发布到生产环境时的自动化打包与处理等多个方面。二十年间流行过的 JavaScript 模块化解决方案包括但不限于直接声明依赖(Directly Defined Dependences)、命名空间(Namespace Pattern)、模块模式(Module Pattern)、依赖分离定义(Detached Dependency Definitions)、沙盒(Sandbox)、依赖注入(Dependency Injection)、CommonJS、AMD、UMD、标签化模块(Labeled Modules)、YModules、ES 2015 Modules。在早期的 Web 开发中，所有的嵌入到网页内的 JavaScript 对象都会使用全局的 `window` 对象来存放未使用 `var` 定义的变量。大概在上世纪末，JavaScript 多用于解决简单的任务，这也就意味着我们只需编写少量的 JavaScript 代码；不过随着代码库的线性增长，我们首先会碰到的就是所谓命名冲突(Name Collisions)困境：

```// file greeting.js
var helloInLang = {
    en: 'Hello world!',
    es: '¡Hola mundo!',
    ru: 'Привет мир!'
};

function writeHello(lang) {
    document.write(helloInLang[lang]);
}

// file hello.js
function writeHello() {
    document.write('The script is broken');
}
```

当我们在页面内同时引入这两个 JavaScript 脚本文件时，显而易见两个文件中定义的 `writeHello` 函数起了冲突，最后调用的函数取决于我们引入的先后顺序。此外在大型应用中，我们不可能将所有的代码写入到单个 JavaScript 文件中；我们也不可能手动地在 HTML 文件中引入全部的脚本文件，特别是此时还存在着模块间依赖的问题，相信很多开发者都会遇到 `jQuery` 尚未定义这样的问题。不过物极必反，过度碎片化的模块同样会带来性能的损耗与包体尺寸的增大，这包括了模块加载、模块解析、因为 Webpack 等打包工具包裹模块时封装的过多 IIFE 函数导致的 JavaScript 引擎优化失败等。譬如我们的源码如下：

```
// index.js
var total = 0
total += require('./module_0')
total += require('./module_1')
total += require('./module_2')
// etc.
console.log(total)


// module_0.js
module.exports = 0


// module_1.js
module.exports = 1
```

经过 Browserify 打包之后的代码变成了如下式样：

```
(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
module.exports = 0
},{}],2:[function(require,module,exports){
module.exports = 1
},{}],3:[function(require,module,exports){
module.exports = 10
},{}],4:[function(require,module,exports){
module.exports = 100
// etc.
```

我们分别测试 100、1000 与 5000 模块，可以发现随着模块数目的增长最后的包体大小并非线性增长：
![](https://nolanwlawson.files.wordpress.com/2016/08/min.png)

# 命名空间模式

命名空间模式始于 2002 年，顾名思义我们可以使用特殊的约定命名。譬如我们可以为某个模块内的变量统一添加 `myApp_` 前缀，譬如 `myApp_address`，`myApp_validateUser()` 等等。同样，我们也可以将函数赋值给模块内的变量或者对象的属性，从而可以使得可以像 `document.write()` 这样在子命名空间下定义函数而避免冲突。首个采样该设计模式的界面库当属 Bindows，其是 Erik Arvidsson 创建于 2002 年。他没有简单地为自定义函数或者对象添加命名前缀，而是将所有的 Bindows 当中的数据与逻辑代码封装于某个全局对象内，从而避免所谓的全局作用域污染。命名空间模式的设计思想如下所示：

```
// file app.js
var app = {};


// file greeting.js
app.helloInLang = {
    en: 'Hello world!',
    es: '¡Hola mundo!',
    ru: 'Привет мир!'
};


// file hello.js
app.writeHello = function (lang) {
    document.write(app.helloInLang[lang]);
};
```

我们可以发现自定义代码中的所有数据对象与函数都归属于全局对象 `app`，不过显而易见这种方式对于大型多人协同项目的可维护性还是较差，并且没有解决模块间依赖管理的问题。另外有时候我们需要处理一些自动执行的 Pollyfill 性质的代码，就需要将模块包裹在自调用的函数中，譬如在某个大型应用中，我们的代码可能会切分为如下几个模块：

```js
// polyfill-vendor.js
(function() {
  // polyfills-vendor code
})();

// module1.js
function module1(params) {
  // module1 code
  return module1;
}

// module3.js
function module3(params) {
  this.a = params.a;
}

module3.prototype.getA = function() {
  return this.a;
};

// app.js
var APP = {};

if (isModule1Needed) {
  APP.module1 = module1({ param1: 1 });
}

APP.module3 = new module3({ a: 42 });
```

那么在引入的时候我们需要手动地按照模块间依赖顺序引入进来：

```
<!--html-->
<script type="application/javascript" src="PATH/polyfill-vendor.js" ></script>
<script type="application/javascript" src="PATH/module1.js" ></script>
<script type="application/javascript" src="PATH/module2.js" ></script>
<script type="application/javascript" src="PATH/app.js" ></script>
```

不过这种方式对于模块间通信也是个麻烦。命名空间模式算是如今 JavaScript 领域最为著名的模式之一，而在 Bindows 之后 Dojo(2005)，YUI(2005) 这些优秀的界面框架也是承袭了这种思想。

# 依赖注入

Martin Fowler 于 2004 年提出了依赖注入([Dependency Injection](https://martinfowler.com/articles/injection.html))的概念，其主要用于  Java 中的组件内通信；以 Spring 为代表的一系列支持依赖注入与控制反转的框架将这种设计模式发扬光大，并且成为了 Java 服务端开发的标准模式之一。依赖注入的核心思想在于某个模块不需要手动地初始化某个依赖对象，而只需要声明该依赖并由外部框架自动实例化该对象实现并且传递到模块内。而五年之后的 2009 年 [Misko Hevery](https://github.com/mhevery)  开始设计新的 JavaScript 框架，并且使用了依赖注入作为其组件间通信的核心机制。这个框架就是引领一时风骚，甚至于说是现代 Web 开发先驱之一的 Angular。Angular 允许我们定义模块，并且在显式地声明其依赖模块而由框架完成自动注入。其核心思想如下所示：

```
// file greeting.js
angular.module('greeter', [])
    .value('greeting', {
        helloInLang: {
            en: 'Hello world!',
            es: '¡Hola mundo!',
            ru: 'Привет мир!'
        },


        sayHello: function(lang) {
            return this.helloInLang[lang];
        }
    });


// file app.js
angular.module('app', ['greeter'])
    .controller('GreetingController', ['$scope', 'greeting', function($scope, greeting) {
        $scope.phrase = greeting.sayHello('en');
    }]);
```

之后在 [Angular 2](https://github.com/angular/angular) 与 [Slot](https://github.com/2gis/slot)  之中依赖注入仍是核心机制之一，这也是 Angular 一系的更多的被视为大而全的框架而不是小而美的库的原因之一。

# CommonJS

在 Node.js 横空出世之前，就已经有很多将运行于客户端浏览器中的 JavaScript 迁移运行到服务端的[框架](https://en.wikipedia.org/wiki/Comparison_of_server-side_JavaScript_solutions)；不过由于缺乏合适的规范，也没有提供统一的与操作系统及运行环境交互的接口，这些框架并未流行开来。2009  年时 Mozilla 的雇员 [Kevin Dangoor](https://github.com/dangoor)  发表了[博客](http://www.blueskyonmars.com/2009/01/29/what-server-side-javascript-needs/)讨论服务端  JavaScript 代码面临的困境，号召所有有志于规范服务端 JavaScript 接口的志同道合的开发者协同讨论，群策群力，最终形成了 ServerJS 规范；一年之后 ServerJS 重命名为 CommonJS。后来 CommonJS 内的模块规范成为了 Node.js 的标准实现规范，其基本语法为 `var commonjs = require("./commonjs");`，核心设计模式如下所示：

```js
// file greeting.js
var helloInLang = {
  en: 'Hello world!',
  es: '¡Hola mundo!',
  ru: 'Привет мир!'
};

var sayHello = function(lang) {
  return helloInLang[lang];
};

module.exports.sayHello = sayHello;

// file hello.js
var sayHello = require('./lib/greeting').sayHello;
var phrase = sayHello('en');

console.log(phrase);
```

该模块实现方案主要包含 `require` 与 `module` 这两个关键字，其允许某个模块对外暴露部分接口并且由其他模块导入使用。在 Node.js 中我们通过内建辅助函数来使用 CommonJS 的导入导出功能，而在其他 JavaScript 引擎中我们可以将其包裹为如下形式：

```js
(function(exports, require, module, __filename, __dirname) {
  // ...
  // Your code injects here!
  // ...
});
```

CommonJS 本质上导入值是对导出值的拷贝，在加载之后是以单例化运行，并且遵循值传递原则：

```js
//------ lib.js ------
var counter = 3;
function incCounter() {
  counter++;
}
module.exports = {
  counter: counter, // (A)
  incCounter: incCounter
};

//------ main1.js ------
var counter = require('./lib').counter; // (B)
var incCounter = require('./lib').incCounter;

// The imported value is a (disconnected) copy of a copy
console.log(counter); // 3
incCounter();
console.log(counter); // 3

// The imported value can be changed
counter++;
console.log(counter); // 4
```

CommonJS 规范本身只是定义了不同环境下支持模块交互性的最小化原则，其具备极大的可扩展性。Node.js 中就对 `require` 函数添加了 `main` 属性，该属性在执行模块所属文件时指向 `module` 对象。Babel 在实现 ES2015 Modules 的转义时也扩展了 `require` 关键字：

```js
'use strict';

Object.defineProperty(exports, '__esModule', {
  value: true
});
exports.foo = foo;
function foo() {}
exports.default = 123;

// 在将 ES6 模块编译为 CJS 模块后，可以使用默认导出；并且兼容 ES6 的默认导入
module.exports = require('./dist/index.js').default;
module.exports.default = module.exports;
```

Webpack 打包工具也使用了很多扩展，譬如 `require.ensure`、`require.cache`、`require.context` 等等。CommonJS 算是目前最流行的模块格式，我们不仅可以在 Node.js 中使用，还可以通过 [Browserify](http://browserify.org/)  与 [Webpack](https://webpack.js.org/) 这样的打包工具将代码打包到客户端运行。

# AMD

就在 CommonJS 规范火热讨论的同时，很多开发者也关注于如何实现模块的异步加载。Web 应用的性能优化一直是前端工程实践中不可避免的问题，而模块的异步加载以及预加载等机制能有效地优化 Web 应用的加载速度。Mozilla 的另一位雇员 [James Burke](https://github.com/jrburke)  是[讨论组](https://groups.google.com/forum/#!msg/commonjs/nbpX739RQ5o/SdpVQDtx88AJ)的活跃成员，他在  Dojo 1.7 版本中引入了异步模块机制，并且在 2009 年开发了 require.js 框架。James 的核心思想在于不应该以同步方式加载模块，而应该充分利用浏览器的并发加载能力；James 按照其设计理念开发出的模块工具就是 AMD(Asynchronous Module Definition)，其基本形式如下：

``` javascript
define(["amd-module", "../file"], function(amdModule, file) {
    require(["big-module/big/file"], function(big) {
        var stuff = require("../my/stuff");
    });
});
```

而将我们上述使用的例子改写为 AMD 模式应当如下所示：

```js
// file lib/greeting.js
define(function() {
  var helloInLang = {
    en: 'Hello world!',
    es: '¡Hola mundo!',
    ru: 'Привет мир!'
  };

  return {
    sayHello: function(lang) {
      return helloInLang[lang];
    }
  };
});

// file hello.js
define(['./lib/greeting'], function(greeting) {
  var phrase = greeting.sayHello('en');
  document.write(phrase);
});
```

hello.js 作为整个应用的入口模块，我们使用 `define` 关键字声明了该模块以及外部依赖；当我们执行该模块代码时，也就是执行 `define` 函数的第二个参数中定义的函数功能，其会在框架将所有的其他依赖模块加载完毕后被执行。这种延迟代码执行的技术也就保证了依赖的并发加载。从我个人而言，AMD 及其相关技术对于前端开发的工程化进步有着非常积极的意义，不过随着以 `npm` 为主导的依赖管理机制的统一，越来越多的开发者放弃了使用 AMD 模式。

# UMD

AMD 与 CommonJS 虽然师出同源，但还是分道扬镳，关注于代码异步加载与最小化入口模块的开发者将目光投注于 AMD；而随着 Node.js 以及 Browserify 的流行，越来越多的开发者也接受了 CommonJS 规范。令人扼腕叹息的是，符合 AMD 规范的模块并不能直接运行于实践了 CommonJS 模块规范的环境中，符合 CommonJS 规范的模块也不能由 AMD 进行异步加载，整个 JavaScript 生态圈貌似分崩离析。2011 年中，UMD，也就是 Universal Module Definition 规范正是为了弥合这种不一致性应运而出，其允许在环境中同时使用 AMD 与 CommonJS 规范。[Q](https://github.com/kriskowal/q)  算是 UMD 的首个规范实现，其能同时运行于浏览器环境(以脚本标签形式嵌入)与服务端的 Node.js 或者 Narwhal(CommonJS 模块)环境中；稍后，James 也为 Q 添加了对于 AMD 的支持。我们将上述例子中的 greeting.js 改写为同时支持 CommonJS 与 AMD 规范的模块：

```
(function(define) {
    define(function () {
        var helloInLang = {
            en: 'Hello world!',
            es: '¡Hola mundo!',
            ru: 'Привет мир!'
        };


        return {
            sayHello: function (lang) {
                return helloInLang[lang];
            }
        };
    });
}(
    typeof module === 'object' && module.exports && typeof define !== 'function' ?
    function (factory) { module.exports = factory(); } :
    define
));
```

该模式的核心思想在于所谓的 IIFE(Immediately Invoked Function Expression)，该函数会根据环境来判断需要的参数类别，譬如在 CommonJS 环境下上述代码会以如下方式执行：

```
function (factory) {
    module.exports = factory();
}
```

而如果是在 AMD 模块规范下，函数的参数就变成了 `define`。正是因为这种运行时的灵活性是我们能够将同一份代码运行于不同的环境中。

# ES2015 Modules

JavaScript 模块规范领域群雄逐鹿，各领风骚，作为 ECMAScript 标准的起草者 TC39 委员会自然也不能置身事外。ES2015 Modules 规范始于 2010 年，主要由 [Dave Herman](https://github.com/dherman)  主导；随后的五年中 David 还参与了 asm.js，emscription，servo，等多个重大的开源项目，也使得  ES2015 Modules 的设计能够从多方面进行考虑与权衡。而最后的模块化规范定义于 2015 年正式发布，也就是被命名为 ES2015 Modules。我们上述的例子改写为 ES2015 Modules 规范如下所示：

```js
// file lib/greeting.js
const helloInLang = {
  en: 'Hello world!',
  es: '¡Hola mundo!',
  ru: 'Привет мир!'
};

export const greeting = {
  sayHello: function(lang) {
    return helloInLang[lang];
  }
};

// file hello.js
import { greeting } from './lib/greeting';
const phrase = greeting.sayHello('en');

document.write(phrase);
```

ES2015 Modules 中主要的关键字就是 `import` 与 `export`，前者负责导入模块而后者负责导出模块。完整的导出语法如下所示：

```js
// default exports
export default 42;
export default {};
export default [];
export default foo;
export default function () {}
export default class {}
export default function foo () {}
export default class foo {}


// variables exports
export var foo = 1;
export var foo = function () {};
export var bar; // lazy initialization
export let foo = 2;
export let bar; // lazy initialization
export const foo = 3;
export function foo () {}
export class foo {}


// named exports
export {foo};
export {foo, bar};
export {foo as bar};
export {foo as default};
export {foo as default, bar};


// exports from
export * from "foo";
export {foo} from "foo";
export {foo, bar} from "foo";
export {foo as bar} from "foo";
export {foo as default} from "foo";
export {foo as default, bar} from "foo";
export {default} from "foo";
export {default as foo} from "foo";
```

相对应的完整的支持的导入方式如下所示：

```js
// default imports
import foo from "foo";
import {default as foo} from "foo";

// named imports
import {bar} from "foo";
import {bar, baz} from "foo";
import {bar as baz} from "foo";
import {bar as baz, xyz} from "foo";

// glob imports
import * as foo from "foo";

// mixing imports
import foo, {baz as xyz} from "foo";
import * as bar, {baz as xyz} from "foo";
import foo, * as bar, {baz as xyz} from "foo";
```

ES2015 Modules 作为 JavaScript 官方标准，日渐成为了开发者的主流选择。虽然我们目前还不能直接保证在所有环境(特别是旧版本浏览器)中使用该规范，但是通过 Babel 等转化工具能帮我们自动处理向下兼容。此外 ES2015 Modules 还是有些许被诟病的地方，譬如导入语句只能作为模块顶层的语句出现，不能出现在 `function` 里面或是 `if` 里面：

```
if(Math.random()>0.5){
  import './module1.js'; // SyntaxError: Unexpected keyword 'import'
}
const import2 = (import './main2.js'); // SyntaxError
try{
  import './module3.js'; // SyntaxError: Unexpected keyword 'import'
}catch(err){
  console.error(err);
}
const moduleNumber = 4;



import module4 from `module${moduleNumber}`; // SyntaxError: Unexpected token
```

并且 import 语句会被提升到文件顶部执行，也就是说在模块初始化的时候所有的 `import` 都必须已经导入完成：

```
import './module1.js';


alert('code1');


import module2 from './module2.js';


alert('code2');



import module3 from './module3.js';


// 执行结果
module1
module2
module3
code1
code2
```

并且  `import` 的模块名只能是字符串常量，导入的值也是不可变对象；比如说你不能 `import { a } from './a'` 然后给 a 赋值个其他什么东西。这些设计虽然使得灵活性不如 CommonJS 的 require，但却保证了 ES6 Modules 的依赖关系是确定(Deterministic)的，和运行时的状态无关，从而也就保证了 ES6 Modules 是可以进行可靠的静态分析的。对于主要在服务端运行的 Node 来说，所有的代码都在本地，按需动态 require 即可，但对于要下发到客户端的 Web 代码而言，要做到高效的按需使用，不能等到代码执行了才知道模块的依赖，必须要从模块的静态分析入手。这是 ES6 Modules 在设计时的一个重要考量，也是为什么没有直接采用 CommonJS。此外我们还需要关注下的是 ES2015 Modules 在浏览器内的原生支持情况，尽管我们可以通过 Webpack 等打包工具将应用打包为单个包文件。
