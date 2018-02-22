[![返回目录](https://parg.co/USw)](https://parg.co/bxN) 
 
 
 




> [基于 Flow 的静态类型检测：努力实现无痛地类型校验]()


![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/8/2/1-wyxuq21keffc5b0d_lMkUw.jpeg)


# 基于 Flow 的静态类型检测 


Flow 是针对 JavaScript 代码的静态类型检查工具，它能够提升项目的鲁棒性与代码的健壮性，并且使得代码以更高性能、可扩展地运行。Flow 通过为代码添加静态类型注解以进行类型推导与检查，基础的用法如下：


```
// @flow
function square(n: number): number {
  return n * n;
}


square("2"); // Error!
```
实际上 Flow 可以通过自动类型推断来帮我们推导出变量类型，因此我们只需要为代码添加最小的类型声明，Flow 即会为我们推导出完整的依赖图：
```
// @flow
function square(n) {
  return n * n; // Error!
}


square("2");
```


目前 React、Vue.js 等著名的前端项目都在使用 Flow 作为静态类型检查工具。
Flow 与 TypeScript 都可以用作静态代码检查，在使用体验与完善度上相对而言 TypeScript 更为优秀；不过如果是为现有的项目添加静态类型检查的支持，那么 Flow 的整体升级流程会更为平滑，迁移成本也较低。我们可以逐步地为项目文件添加语法检查支持，并且可以使用 Transform Flow Strip Types 在不需要静态类型检查时方便地移除项目中的类型注释。同时 Flow 本身和 Rollup、Babel、ESLint 等工具也结合良好，可以完全沿用已有的构建配置。最后从笔者个人的态度而言，是坚定的 JavaScript First 支持者，虽然绝大部分的语法和使用二者都非常接近，但是笔者更倾向于使用 JavaScript 扩展而不是另一门语言。


# 安装配置


## 开发环境


我们可以使用 `npx` 更新本机安装的 flow 版本
```
npx flow-upgrade

```


## 项目配置


类似于 TypeScript 的 [DefinitelyTyped](https://github.com/DefinitelyTyped/DefinitelyTyped)，flow-typed 可以为我们统一管理第三方库的声明，我们可以按照如下方式为项目引入 flow-typed：
```
$ npm install -g flow-typed


$ cd /path/to/my/project
$ npm install
$ flow-typed install rxjs@5.0.x
'rxjs_v5.0.x.js' installed at /path/to/my/project/flow-typed/npm/rxjs_v5.0.xjs
```


每个 Flow 项目都会包含一个 `.flowconfig` 文件，我们可以使用 `flow init` 命令快速创建空白的 `.flowconfig` 文件；该文件是标准的 INI 文件格式，其主要包含以下五个配置：
```


- [include]：指明需要包含在内的非项目根目录文件

- [ignore]：指明需要忽略的文件

- [libs]：指明类型声明文件，默认会包含 flow-typed 文件夹

- [options]：配置类型检查的原则

- [version]：指定需要使用的 Flow 版本
```
一般来说，Flow 会将 `.flowconfig` 所在的目录当做项目的根目录；默认情况下 Flow 会检查根目录下的所有源代码，而 `<PROJECT_ROOT>` 宏也就指向了项目根目录。如果我们的项目结构如下所示：
```

otherdir
└── src
    ├── othercode.js
mydir
├── .flowconfig
├── build
│   ├── first.js
│   └── shim.js
├── lib
│   └── flow
├── node_modules
│   └── es6-shim
└── src
    ├── first.js
    └── shim.js
```
那么对应的 `.flowconfig` 文件应该配置如下：
```

[include]
../otherdir/src


[ignore]
.*/build/.*


[libs]
./lib
```
这样 Flow 就会自动地将外层目录包含在内，并且忽略 `build` 目录下文件，使用 `./lib` 目录中声明的类型。`[include]` 还支持如下的模糊匹配：
```

[include]
../externalFile.js
../externalDir/
../otherProject/*.js
../otherProject/**/coolStuff/
```
而 `[ignore]` 忽略的规则支持简单的 OCaml 正则匹配，我们可以使用 `flow ls` 查看 Flow 可见的文件列表：
```

[ignore]
<PROJECT_ROOT>/__tests__/.*

.*/__tests__/.*
.*/src/\(foo\|bar\)/.*


; 忽略指定格式文件
.*\.ignore\.js
.*/**/*.json



; 常用的项目目录下忽略文件

.*/node_modules/.*
.*/dev-config/.*
.*/dist/.*
.*/public/.*
```
# 基础数据类型


# 复合类型


```

// @flow


type mm = {
  [string]:string | mm;
}


let m:mm = {
  '1':{
    '1':'1'
  }
};


let subKey: string = '1';


if( typeof m['1'] === 'string'){
  m['1'];
}else{
  m['1'][subKey];
}
```
# 函数


## 读写限定



```

// @flow
function setNullableStringContainer(container: {-value: ?string}) {
  setStringContainer(container);
}
function setStringContainer(container: {-value: string}) {
  container.value = 'foo';
}
const container = {value: null};
setNullableStringContainer(container);
```


# 类与对象
```

type PromiseLike<R> = {
	then<U>(
		onFulfill?: (value: R) => Promise<U> | U,
		onReject?: (error: any) => Promise<U> | U
	): Promise<U>;
}


type ObservableLike = {
	subscribe(observer: (value: {}) => void): void;
};


type SpecialReturnTypes =
	| PromiseLike<any>
	| Iterator<any>
	| ObservableLike;


type Constructor = Class<{
	constructor(...args: Array<any>): any
}>;
```




## 接口



```
interface ISet {
  has(key: string | number): boolean;
  add(key: string | number): mixed;
  clear(): void;
}


...


export type { ISet }
```






# Immutability


# 类型扩展
## Typeof
## 强制类型转换
## 模块化
```
imports/helpers.js:3
  3: import invariant from 'invariant';
                           ^^^^^^^^^^^ invariant. Required module not found
```


```
// [PROJECT]/flow-typed/invariant.js:


declare module 'invariant' {
  declare module.exports: any;
}
```


```
src/source/spider/web/HTMLSpider.js:4
  4: const $ = require('isomorphic-parser');
                       ^^^^^^^^^^^^^^^^^^^ isomorphic-parser. Required module not found
```