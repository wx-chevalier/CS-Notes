[![返回目录](https://i.postimg.cc/JzFTMvjF/image.png)](https://github.com/wx-chevalier/Awesome-CheatSheets)

# JavaScript 函数式编程导论

近年来，函数式编程(Functional Programming )已经成为了 JavaScript 社区中炙手可热的主题之一，无论你是否欣赏这种编程理念，相信你都已经对它有所了解。即使是前几年函数式编程尚未流行的时候，我已经在很多的大型应用代码库中发现了不少对于函数式编程理念的深度实践。函数式编程即是在软件开发的工程中避免使用共享状态(Shared State )、可变状态(Mutable Data)以及副作用(Side Effects )。函数式编程中整个应用由数据驱动，应用的状态在不同纯函数之间流动。与偏向命令式编程的面向对象编程而言，函数式编程其更偏向于声明式编程，代码更加简洁明了、更可预测，并且可测试性也更好。。函数式编程本质上也是一种编程范式(Programming Paradigm )，其代表了一系列用于构建软件系统的基本定义准则。其他编程范式还包括面向对象编程(Object Oriented Programming )与过程程序设计(Procedural Programming )。

# 纯函数

顾名思义，纯函数往往指那些仅根据输入参数决定输出并且不会产生任何副作用的函数。纯函数最优秀的特性之一在于其结果的可预测性：

```js
var z = 10;
function add(x, y) {
  return x + y;
}
console.log(add(1, 2)); // prints 3
console.log(add(1, 2)); // still prints 3
console.log(add(1, 2)); // WILL ALWAYS print 3
```

在`add`函数中并没有操作`z`变量，即没有读取`z`的数值也没有修改`z`的值。它仅仅根据参数输入的`x`与`y`变量然后返回二者相加的和。这个`add`函数就是典型的纯函数，而如果在`add`函数中涉及到了读取或者修改`z`变量，那么它就失去了纯洁性。我们再来看另一个函数 :

```js
function justTen() {
  return 10;
}
```

对于这样并没有任何输入参数的函数，如果它要保持为纯函数，那么该函数的返回值就必须为常量。不过像这种固定返回为常量的函数还不如定义为某个常量呢，就没必要大材小用用函数了，因此我们可以认为绝大部分的有用的纯函数至少允许一个输入参数。再看看下面这个函数 :

```
function addNoReturn(x, y) {
    var z = x + y
}
```

注意这个函数并没有返回任何值，它确实拥有两个输入参数`x`与`y`，然后将这两个变量相加赋值给`z`，因此这样的函数也可以认为是无意义的。这里我们可以说，绝大部分有用的纯函数必须要有返回值。总结而言，纯函数应该具有以下几个特效 :

- 绝大部分纯函数应该拥有一或多个参数值。
- 纯函数必须要有返回值。
- 相同输入的纯函数的返回值必须一致。
- 纯函数不能够产生任何的副作用。

# 共享状态与副作用

在软件开发中有个很有趣的观点：共享的状态时万恶之源。共享状态(Shared State )可以是存在于共享作用域(全局作用域与闭包作用域)或者作为传递到不同作用域的对象属性的任何变量、对象或者内存空间。在面向对象编程中，我们常常是通过添加属性到其他对象的方式共享某个对象。共享状态问题在于，如果开发者想要理解某个函数的作用，必须去详细了解该函数可能对于每个共享变量造成的影响。譬如我们现在需要将客户端生成的用户对象保存到服务端，可以利用`saveUser()`函数向服务端发起请求，将用户信息编码传递过去并且等待服务端响应。而就在你发起请求的同时，用户修改了个人头像，触发了另一个函数`updateAvatar()`以及另一次`saveUser()`请求。正常来说，服务端会先响应第一个请求，并且根据第二个请求中用户参数的变更对于存储在内存或者数据库中的用户信息作相应的修改。不过某些意外情况下，可能第二个请求会比第一个请求先到达服务端，这样用户选定的新的头像反而会被第一个请求中的旧头像覆写。这里存放在服务端的用户信息就是所谓的共享状态，而因为多个并发请求导致的数据一致性错乱也就是所谓的竞态条件(Race Condition )，也是共享状态导致的典型问题之一。另一个共享状态的常见问题在于不同的调用顺序可能会触发未知的错误，这是因为对于共享状态的操作往往是时序依赖的。

```js
const x = {
  val: 2
};

const x1 = () => (x.val += 1);

const x2 = () => (x.val *= 2);

x1();
x2();

console.log(x.val); // 6

const y = {
  val: 2
};

const y1 = () => (y.val += 1);

const y2 = () => (y.val *= 2);

// 交换了函数调用顺序
y2();
y1();

// 最后的结果也受到了影响
console.log(y.val); // 5
```

副作用指那些在函数调用过程中没有通过返回值表现的任何可观测的应用状态变化，常见的副作用包括但不限于：

- 修改任何外部变量或者外部对象属性
- 在控制台中输出日志
- 写入文件
- 发起网络通信
- 触发任何外部进程事件
- 调用任何其他具有副作用的函数

在函数式编程中我们会尽可能地规避副作用，保证程序更易于理解与测试。Haskell 或者其他函数式编程语言通常会使用[Monads](https://en.wikipedia.org/wiki/Monad_%28functional_programming%29)来隔离与封装副作用。在绝大部分真实的应用场景进行编程开始时，我们不可能保证系统中的全部函数都是纯函数，但是我们应该尽可能地增加纯函数的数目并且将有副作用的部分与纯函数剥离开来，特别是将业务逻辑抽象为纯函数，来保证软件更易于扩展、重构、调试、测试与维护。这也是很多前端框架鼓励开发者将用户的状态管理与组件渲染相隔离，构建松耦合模块的原因。

# 不变性

不可变对象(Immutable Object )指那些创建之后无法再被修改的对象，与之相对的可变对象(Mutable Object )指那些创建之后仍然可以被修改的对象。不可变性(Immutability )是函数式编程的核心思想之一，保证了程序运行中数据流的无损性。如果我们忽略或者抛弃了状态变化的历史，那么我们很难去捕获或者复现一些奇怪的小概率问题。使用不可变对象的优势在于你在程序的任何地方访问任何的变量，你都只有只读权限，也就意味着我们不用再担心意外的非法修改的情况。另一方面，特别是在多线程编程中，每个线程访问的变量都是常量，因此能从根本上保证线程的安全性。总结而言，不可变对象能够帮助我们构建简单而更加安全的代码。在 JavaScript 中，我们需要搞清楚`const`与不可变性之间的区别。`const`声明的变量名会绑定到某个内存空间而不可以被二次分配，其并没有创建真正的不可变对象。你可以不修改变量的指向，但是可以修改该对象的某个属性值，因此`const`创建的还是可变对象。JavaScript 中最方便的创建不可变对象的方法就是调用`Object.freeze()`函数，其可以创建一层不可变对象：

```js
const a = Object.freeze({
  foo: 'Hello',
  bar: 'world',
  baz: '!'
});

a.foo = 'Goodbye';
// Error: Cannot assign to read only property 'foo' of object Object
```

不过这种对象并不是彻底的不可变数据，譬如如下的对象就是可变的：

```js
const a = Object.freeze({
  foo: { greeting: 'Hello' },
  bar: 'world',
  baz: '!'
});

a.foo.greeting = 'Goodbye';

console.log(`${a.foo.greeting}, ${a.bar}${a.baz}`);
```

如上所见，顶层的基础类型属性是不可以改变的，不过如果对象类型的属性，譬如数组等，仍然是可以变化的。在很多函数式编程语言中，会提供特殊的不可变数据结构 Trie Data Structures 来实现真正的不可变数据结构，任何层次的属性都不可以被改变。Tries 还可以利用结构共享(Structural Sharing )的方式来在新旧对象之间共享未改变的对象属性值，从而减少内存占用并且显著提升某些操作的性能。JavaScript 中虽然语言本身并没有提供给我们这个特性，但是可以通过[Immutable.js](https://github.com/facebook/immutable-js)与[Mori](https://github.com/swannodette/mori)这些辅助库来利用 Tries 的特性。我个人两个库都使用过，不过在大型项目中会更倾向于使用 Immutable.js。估计到这边，很多习惯了命令式编程的同学都会大吼一句：在没有变量的世界里我又该如何编程呢？不要担心，现在我们考虑下我们何时需要去修改变量值：譬如修改某个对象的属性值，或者在循环中修改某个循环计数器的值。而函数式编程中与直接修改原变量值相对应的就是创建原值的一个副本并且将其修改之后赋予给变量。而对于另一个常见的循环场景，譬如我们所熟知的`for`,`while`,`do`,`repeat`这些关键字，我们在函数式编程中可以使用递归来实现原本的循环需求 :

```js
// 简单的循环构造
var acc = 0;
for (var i = 1; i <= 10; ++i) acc += i;
console.log(acc); // prints 55
// 递归方式实现
function sumRange(start, end, acc) {
  if (start > end) return acc;
  return sumRange(start + 1, end, acc + start);
}
console.log(sumRange(1, 10, 0)); // prints 55
```

注意在递归中，与变量 i 相对应的即是 start 变量，每次将该值加 1，并且将 acc+start 作为当前和值传递给下一轮递归操作。在递归中，并没有修改任何的旧的变量值，而是根据旧值计算出新值并且进行返回。不过如果真的让你把所有的迭代全部转变成递归写法，估计得疯掉，这个不可避免地会受到 JavaScript 语言本身的混乱性所影响，并且迭代式的思维也不是那么容易理解的。而在 Elm 这种专门面向函数式编程的语言中，语法会简化很多 :

```js
sumRange start end acc =
    if start > end then
        acc
    else
        sumRange (start + 1) end (acc + start)
```

其每一次的迭代记录如下 :

```
sumRange 1 10 0 =      -- sumRange (1 + 1)  10 (0 + 1)
sumRange 2 10 1 =      -- sumRange (2 + 1)  10 (1 + 2)
sumRange 3 10 3 =      -- sumRange (3 + 1)  10 (3 + 3)
sumRange 4 10 6 =      -- sumRange (4 + 1)  10 (6 + 4)
sumRange 5 10 10 =     -- sumRange (5 + 1)  10 (10 + 5)
sumRange 6 10 15 =     -- sumRange (6 + 1)  10 (15 + 6)
sumRange 7 10 21 =     -- sumRange (7 + 1)  10 (21 + 7)
sumRange 8 10 28 =     -- sumRange (8 + 1)  10 (28 + 8)
sumRange 9 10 36 =     -- sumRange (9 + 1)  10 (36 + 9)
sumRange 10 10 45 =    -- sumRange (10 + 1) 10 (45 + 10)
sumRange 11 10 55 =    -- 11 > 10 => 55
55
```

在实际编程中，多个不可变对象之间可能会共享部分对象：

![image](https://user-images.githubusercontent.com/5803001/49514081-f9ef6500-f8cd-11e8-9500-7f1f389e5caa.png)

# 高阶函数

函数式编程倾向于重用一系列公共的纯函数来处理数据，而面向对象编程则是将方法与数据封装到对象内。这些被封装起来的方法复用性不强，只能作用于某些类型的数据，往往只能处理所属对象的实例这种数据类型。而函数式编程中，任何类型的数据则是被一视同仁，譬如`map()`函数允许开发者传入函数参数，保证其能够作用于对象、字符串、数字，以及任何其他类型。JavaScript 中函数同样是一等公民，即我们可以像其他类型一样处理函数，将其赋予变量、传递给其他函数或者作为函数返回值。而高阶函数(Higher Order Function )则是能够接受函数作为参数，能够返回某个函数作为返回值的函数。高阶函数经常用在如下场景：

- 利用回调函数、Promise 或者 Monad 来抽象或者隔离动作、作用以及任何的异步控制流
- 构建能够作用于泛数据类型的工具函数
- 函数重用或者创建柯里函数
- 将输入的多个函数并且返回这些函数复合而来的复合函数

典型的高阶函数的应用就是复合函数，作为开发者，我们天性不希望一遍一遍地重复构建、测试与部分相同的代码，我们一直在寻找合适的只需要写一遍代码的方法以及如何将其重用于其他模块。代码重用听上去非常诱人，不过其在很多情况下是难以实现的。如果你编写过于偏向具体业务的代码，那么就会难以重用。而如果你把每一段代码都编写的过于泛化，那么你就很难将这些代码应用于具体的有业务场景，而需要编写额外的连接代码。而我们真正追寻的就是在具体与泛化之间寻求一个平衡点，能够方便地编写短小精悍而可复用的代码片，并且能够将这些小的代码片快速组合而解决复杂的功能需求。在函数式编程中，函数就是我们能够面向的最基础代码块，而在函数式编程中，对于基础块的组合就是所谓的函数复合(Function Composition )。我们以如下两个简单的 JavaScript 函数为例 :

```
var add10 = function(value) {
    return value + 10;
};
var mult5 = function(value) {
    return value * 5;
};
```

如果你习惯了使用 ES6，那么可以用 Arrow Function 重构上述代码 :

```js
var add10 = value => value + 10;
var mult5 = value => value * 5;
```

现在看上去清爽多了吧，下面我们考虑面对一个新的函数需求，我们需要构建一个函数，首先将输入参数加 10 然后乘以 5，我们可以创建一个新函数如下 :

```js
var mult5AfterAdd10 = value => 5 * (value + 10);
```

尽管上面这个函数也很简单，我们还是要避免任何函数都从零开始写，这样也会让我们做很多重复性的工作。我们可以基于上文的 add10 与 mult5 这两个函数来构建新的函数 :

```
var mult5AfterAdd10 = value => mult5(add10(value));
```

在 mult5AfterAdd10 函数中，我们已经站在了 add10 与 mult5 这两个函数的基础上，不过我们可以用更优雅的方式来实现这个需求。在数学中，我们认为`f ∘ g`是所谓的 Function Composition，因此``f ∘ g`可以认为等价于`f(g(x))`，我们同样可以基于这种思想重构上面的 mult5AfterAdd10。不过 JavaScript 中并没有原生的 Function Composition 支持，在 Elm 中我们可以用如下写法 :

```
add10 value =
    value + 10
mult5 value =
    value * 5
mult5AfterAdd10 value =
    (mult5 << add10) value
```

这里的`<<`操作符也就指明了在 Elm 中是如何组合函数的，同时也较为直观的展示出了数据的流向。首先 value 会被赋予给 add10，然后 add10 的结果会流向 mult5。另一个需要注意的是，`(mult5 << add10)`中的中括号是为了保证函数组合会在函数调用之前。你也可以组合更多的函数 :

```
f x =
   (g << h << s << r << t) x
```

如果在 JavaScript 中，你可能需要以如下的递归调用来实现该功能 :

```
g(h(s(r(t(x)))))
```

# 函数操作库

## Immer

参考 Weststrate 的[这篇文章](https://hackernoon.com/introducing-immer-immutability-the-easy-way-9d73d8f71cb3)

![](https://cdn-images-1.medium.com/max/1600/1*bZ2J4iIpsm2lMG4ZoXcj3A.png)

## [Immutable.js](https://facebook.github.io/immutable-js/docs/#/fromJS)

[Immutable](http://en.wikipedia.org/wiki/Immutable_object) 对象一旦被创建之后即不可再更改，这样可以使得应用开发工作变得简化，不再需要大量的保护性拷贝，使用简单的逻辑控制即可以保证内存控制与变化检测。Immutable.js 虽然和 React 同期出现且跟 React 配合很爽，但它可不是 React 工具集里的(它的光芒被掩盖了)，它是一个完全独立的库，无论基于什么框架都可以用它。意义在于它弥补了 Javascript 没有不可变数据结构的问题。不可变数据结构是函数式编程中必备的。前端工程师被 OOP 洗脑太久了，组件根本上就是函数用法，FP 的特点更适用于前端开发。

Javascript 中对象都是参考类型，也就是`a={a:1}; b=a; b.a=10;`你发现`a.a`也变成 10 了。可变的好处是节省内存或是利用可变性做一些事情，但是，在复杂的开发中它的副作用远比好处大的多。于是才有了浅 copy 和深 copy，就是为了解决这个问题。举个常见例子：

```js
var defaultConfig = {
  /* 默认值 */
};

var config = $.extend({}, defaultConfig, initConfig); // jQuery用法。initConfig是自定义值

var config = $.extend(true, {}, defaultConfig, initConfig); // 如果对象是多层的，就用到deep-copy了
```

而

```javascript
var stateV1 = Immutable.fromJS({
  users: [{ name: 'Foo' }, { name: 'Bar' }]
});
var stateV2 = stateV1.updateIn(['users', 1], function() {
  return Immutable.fromJS({
    name: 'Barbar'
  });
});
stateV1 === stateV2; // false
stateV1.getIn(['users', 0]) === stateV2.getIn(['users', 0]); // true
stateV1.getIn(['users', 1]) === stateV2.getIn(['users', 1]); // false
```

如上，我们可以使用 === 来通过引用来比较对象，这意味着我们能够方便快速的进行对象比较，并且它能够和 React 中的 PureRenderMixin 兼容。基于此，我们可以在整个应用构建中使用 Immutable.js。也就是说，我们的 Flux Store 应该是一个具有不变性的对象，并且我们通过 将具有不变性的数据作为属性传递给我们的应用程序。

# 不可变对象操作

## Immer

Immer 是 MobX 作者开源的 JavaScript 中不可变对象操作库，不同于 ImmutableJS，其基于 Proxy 提供了更为直观易用的操作方式。

```js
import produce from 'immer';

const baseState = [
  {
    todo: 'Learn typescript',
    done: true
  },
  {
    todo: 'Try immer',
    done: false
  }
];

const nextState = produce(baseState, draftState => {
  draftState.push({ todo: 'Tweet about it' });
  draftState[1].done = true;
});
```

immer 同样可以简化 Reducer 的写法：

```js
import produce from 'immer';

const byId = produce(
  (draft, action) => {
    switch (action.type) {
      case RECEIVE_PRODUCTS:
        action.products.forEach(product => {
          draft[product.id] = product;
        });
        return;
    }
  },
  // 传入默认初始状态
  {
    1: { id: 1, name: 'product-1' }
  }
);
```

或者直接在 React setState 中使用：

```js
onBirthDayClick2 = () => {
  this.setState(
    produce(draft => {
      draft.user.age += 1;
    })
  );
};
```
