> 需要特别提及的是，本文很多内容是笔者在借鉴 [Airbnb JavaScript Style](https://github.com/airbnb/javascript/blob/master/README.md#types) 的基础上，针对我司提取出需要的内容；具体的默认原则与考量参考前言部分。。本编程样式指南仅针对我司遇到的部分问题，代表笔者及我司同僚个人意见，不具备任何权威或者强制性。恳请而虚心接受码友的指教，可前往[这里]()提出意见。 **本文其他参考资料统一声明在[这里]()**

# 某熊的 Opinionated JavaScript 编程样式指南

* 默认使用 Prettier 进行代码格式化，因此能够由 Prettier 自动处理的格式问题本文不会提及；
* 默认使用 ES 6/7 的语法，优先使用 OOP 的原则，优先使用类而不是 Object 来定义实体；我们认为组件类默认遵循统一的类编码原则。
* 默认使用 Flow 进行静态类型检测；
* 默认使用 Jest 进行单元测试，对于任何稳定的类或函数必须添加单元测试，任何提交前必须保证通过单元测试；
* Clean Code 而不是 Hacky Code

## Principle: 基本原则

### 移除无用代码

* 移除无用代码

* 使用 Git 保存记录或者废弃代码，而不是建立 deprecated 文件夹。

### SRP: 遵循单一职责原则

除以之外，我们建议编码时可以多多参考 SOLID 原则；不过不建议滥用设计模式。

### DRY: 提取公共代码与避免过度抽象

```
// bad
if (...) {
  document.getElementById("second").className = "show";
} else {
  document.getElementById("second").className = "";
}

// good
var target = document.getElementById("second");
if (...) {
  target.className = "show";
} else {
  target.className = "";
}
```

### 数据独立于逻辑

# Code Style: 代码风格

## Name Conventions: 命名约定

### 目录与文件

* 目录统一以 camel_case 方式命名，子目录不应该包含父目录信息。譬如我们需要定义领域相关目录，应该使用 `user/auth` 而不是 `user/user_auth`；
* 包含类(组件类)的文件以 CamelCase 方式命名，并且遵循 Single Public Class in Single File 的原则；即每个类文件中只允许导出单个类，允许定义其他私有内部类；一旦某个内部类被其他文件引用(非 PublicClass.PrivateClass 引用)，即需要将该内部类提出为公共外部类；
* 仅包含函数或者配置性质的文件以 camel_case 方式命名，使用 .js 后缀；
* JavaScript 类文件使用 .js 后缀，React 组件类使用 .jsx 后缀，Vue 组件使用 .vue 后缀；

### 变量、函数与类

* 变量或者函数或者类等的命名必须具有自解释性，不要使用 `aa`、`bb` 等无意义命名。
* 不允许英文拼音混写，不建议强制使用英文命名

## Collection: 集合类型

* 优先使用 Set 存放有唯一性要求的集合；

```
// bad
var allChords = [];

chords.forEach(chord => {
  if(!allChords.includes(chord)){
    allChords.push(chord);
  }
});

// good
var allChords = new Set();

chords.forEach(chord => allChords.add(chord));
```

## Function: 函数

### Param & Invoke: 参数与调用

* 对于参数较少或者必须参数较多的情况下优先使用扁平化参数，使用 optional parameter 递默认值；

* 对于参数较多或者可选参数较多的情况下，或者需要避免传入空值的情况下优先使用 Named Options Objects；

```
// bad
const createEvent = (
  title = 'Untitled',
  timeStamp = Date.now(),
  description = ''
) => ({ title, description, timeStamp });

const birthdayParty = createEvent(
  'Birthday Party',
  undefined, // This was avoidable
  'Best party ever!'
);

// good
const createEvent = ({
  title = 'Untitled',
  timeStamp = Date.now(),
  description = ''
}) => ({ title, description, timeStamp });

const birthdayParty = createEvent({
  title: 'Birthday Party',
  description: 'Best party ever!'
});
```

### Async: 异步操作

* 优先使用 async / await 进行异步操作；

```
// bad
levelOne(function(){
  levelTwo(function(){
    levelThree(function(){
      levelFour(function(){
        // some code here
      });
    });
  });
});

// good
await levelOne();
await levelTwo();
await levelThree();
await levelFour();
```

* 合理排布多异步操作；

## Class: 类与对象

* 类私有属性或方法建议使用下划线前缀；

### Method ：成员方法

* 构造函数参数应优先指定自有属性；避免添加无意义构造器，谨慎传递全部构造函数参数给父类；

```
// bad
class Jedi {
  constructor() {}

  getName() {
    return this.name;
  }
}

// bad
class Rey extends Jedi {
  constructor(...args) {
    super(...args);
  }
}

// maybe bad 可能会传递无意义的参数给父类
class Rey extends Jedi {
  constructor(...args) {
    super(...args);
    this.name = 'Rey';
  }
}

// good
class Rey extends Jedi {
  constructor(ownProperty, ...args) {
    super(...args);
    this. ownProperty = ownProperty;
  }
}
```

* 优先使用纯函数，优先将纯函数定义为静态方法；

* 使用箭头函数或者 `bind` 绑定方法上下文；

```

```

### Order: 定义顺序

* 首先声明 flow、eslint 等配置信息；

```
// @flow
```

* 优先使用 `import` ，或者使用 `require` 引入外部依赖；注意，对于未实际使用的外部依赖请及时清除，或者使用编辑器自带的插件进行自动清除；

```

```

* 声明并导出公开外部类；

* 声明静态属性(可选)；

* 声明类属性；

* 声明复写的父类方法；

* 声明自定义类方法；

* 声明静态属性；

* 声明静态方法；

* 声明内部工具函数或者对象；

* 导出高阶函数封装类，优先使用装饰器声明；

# Code Format & Lint: 代码格式化与语法检测

## 使用 Prettier 格式化 JavaScript 代码

Prettier 是非常优秀的、具有一定特色的支持 ES2017、JSX 、 Flow 的 JavaScript 格式化工具。我们可以使用 yarn 安装 Prettier：

```
yarn global add prettier
```

然后直接在命令行中运行：

```
prettier --single-quote --trailing-comma es5 --write "{app,__{tests,mocks}__}/**/*.js"
```

### VSCode

### JetBrains

在 JetBrains 中我们可以使用 External Tools 来添加 Prettier 的界面插件。在 macOS 中，如果已经全局安装了 Prettier，那么直接使用全局的 prettier 命令行；否则使用本地安装的 ./node_modules/.bin/prettier。然后在 External Tools 进行如下配置：

![](https://d3nmt5vlzunoa1.cloudfront.net/webstorm/files/2016/08/prettier-external-tools.png)

我们也可以为 Prettier 添加快捷键，譬如将 JetBrains 默认的代码格式化工具 “ALT + COMMAND + L” 替换为 Prettier：

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/3/2/C09CB515-4F07-4B6C-8DE8-C6D3B3AA9CA9.png)

然后将原本的代码格式化快捷键替换为：“ALT + COMMAND + K” ，这样有助于我们对除了 JavaScript 之外的其他格式的文件进行处理：

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/3/2/D25811D1-CF2C-4C51-8EF1-E17FAB562168.png)

JetBrains 中还能够帮我们自动进行 Imports 优化，我们可以将其快捷键设置为 “ALT + COMMAND + I”：

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/3/2/WX20170509-233315.png)
