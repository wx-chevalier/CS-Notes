[![返回目录](https://parg.co/USw)](https://parg.co/bxN)

# 函数调用与 this 绑定

# 函数执行

我们已经知道 , 浏览器第一次加载脚本 , 它将默认进入 `全局执行上下文` 中。 如果 , 你在全局环境中调用了一个函数 , 你的程序序列流会进入被调用的函数的当中，创建一个新的 `执行上下文` 并且将这个上下文压入`执行栈`之中。

如果你在当前函数里面又调用了另外一个函数 , 也会发生同样的事情。代码的执行流进入内部函数，这将创建一个新的`执行上下文`，它被压入现有栈的顶部。浏览器永远会执行当前栈中顶部的`执行上下文` 一旦函数在当前`执行上下文`执行完毕，它会被从栈的顶部弹出，然后将控制权移交给当前栈的下一个上下文当中。 下面的代码展示了一个递归函数以及程序的 `执行上下文`:

```js
(function foo(i) {
  if (i === 3) {
    return;
  } else {
    foo(++i);
  }
})(0);
```

```js
console.log(1);
(_ => console.log(2))();
eval('console.log(3);');
console.log.call(null, 4);
console.log.apply(null, [5]);
new Function('console.log(6)')();
Reflect.apply(console.log, null, [7])
Reflect.construct(function(){console.log(8)}, []);
Function.prototype.apply.call(console.log, null, [9]);
Function.prototype.call.call(console.log, null, 10);
new (require('vm').Script)('console.log(11)‘).runInThisContext();
```

```js
function createNamedFunction(name, fn) {
  return new Function(
    'f',
    `return function ${name}(){ return f.apply(this,arguments)}`
  )(fn);
}

let func = createNamedFunction('namedFunction', () => {
  console.log('namedFunction');
});

console.log(func);

func();
```

[![img](http://p0.qhimg.com/t0151cc8a48b0da097c.gif)](http://p0.qhimg.com/t0151cc8a48b0da097c.gif)

这段代码调用自己自身 3 次 , 每次将 i 的值增加 1。 每次函数 `foo` 被调用的时候 , 就会创建一个新的执行上下文。 一旦上下文执行完毕之后 , 它就会从栈中弹出并且返回控制权到下一个上下文当中，直到`全局上下文` 又再次被访问。

**关于 执行上下文** 有五个要点是要记住的 :

- 单线程。

- 同步执行。

- 只有一个全局上下文。

- 可有无数个函数上下文。

- 每个函数调用都会创建一个新的 `执行上下文`，哪怕是递归调用。

## 执行上下文中的细节

现在我们已经知道了每个函数调用都会创建一个新的 `执行上下文` 。 然而，在 JavaScript 解释器内部，对每个执行上下文的调用会经历两个阶段：

1. **创建阶段** [当函数被调用 , 但内部的代码还没开始执行]:

2. 创建 [作用域链](http://davidshariff.com/blog/javascript-scope-chain-and-closures/).

3. 创建变量、函数以及参数

4. 决定 [`"this"`](http://davidshariff.com/blog/javascript-this-keyword/)的值

5. **激活 / 代码执行阶段**:

6. 赋值 , 寻找函数引用以及解释 / 执行代码

我们可以用一个具有三个属性的概念性对象来代表 `执行上下文`：

```
executionContextObj = {

    'scopeChain': { /* 变量对象 + 所有父级执行上下文中的变量对象 */ },

    'variableObject': { /*  函数参数 / 参数, 内部变量以及函数声明 */ },

    'this': {}

}
```

### 活动对象 / 变量对象 [AO/VO]

这个`executionContextObj` 对象在函数调用的时候创建 , 但是实在函数真正执行之前。这就是我们所说的第 1 阶段 `创建阶段`。 在这个阶段，解释器通过扫描传入函数的参数，局部函数声明和局部变量声明来创建 `executionContextObj` 对象。这个扫描的结果就变成了 `executionContextObj` 中的 `variableObject` 对象。

**这是解释器执行代码时的伪概述**:

1. 寻找调用函数的代码

2. 在执行 `函数` 代码之前 , 创建 `执行上下文`.

3. 进入创建阶段 :

4. 初始化 [`作用域链`](http://davidshariff.com/blog/javascript-scope-chain-and-closures/).

5. 创建`变量对象`：

6. 创建 `参数对象`, 检查参数的上下文 , 初始化其名称和值并创建一个引用拷贝。

7. 扫描上下文中的函数声明：

8. 对于每个被发现的函数 , 在 `变量对象` 中创建一个和函数名同名的属性，这是函数在内存中的引用。

9. 如果函数名已经存在 , 引用值将会被覆盖。

10. 扫描上下文中的变量声明：

11. 对于每个被发现的变量声明 , 在`变量对象`中创建一个同名属性并初始化值为 [undefined](http://davidshariff.com/blog/javascripts-undefined-explored/)。

12. 如果变量名在 `变量对象` 中已经存在 , 什么都不做，继续扫描。

13. 确定上下文中的 [`"this"`](http://davidshariff.com/blog/javascript-this-keyword/)

14. 激活 / 代码执行阶段：

15. 执行 / 在上下文中解释函数代码，并在代码逐行执行时给变量赋值。

让我们来看一个例子 :

```js
function foo(i) {
  var a = 'hello';

  var b = function privateB() {};

  function c() {}
}

foo(22);
```

在调用`foo(22)` 的时候 , `创建阶段` 看起来像是这样 :

```
fooExecutionContext = {

    scopeChain: { ... },

    variableObject: {

        arguments: {

            0: 22,

            length: 1

        },

        i: 22,

        c: pointer to function c()

        a: undefined,

        b: undefined

    },

    this: { ... }

}
```

你可以发现 , `创建阶段` 掌管着属性名的定义，而不是给它们赋值，不过参数除外。 一旦 `创建阶段`完成之后，执行流就会进入函数中。 在函数执行完之后，激活 / 代码 `执行阶段` 看起来像是这样：

```
fooExecutionContext = {

    scopeChain: { ... },

    variableObject: {

        arguments: {

            0: 22,

            length: 1

        },

        i: 22,

        c: pointer to function c()

        a: 'hello',

        b: pointer to function privateB()

    },

    this: { ... }

}
```

# bind

```
Function.prototype.bind = function() {

  var fn = this,

    args = Array.prototype.slice.call(arguments),

    object = args.shift();

  return function() {

    return fn.apply(object, args.concat(Array.prototype.slice.call(arguments)));

  };

};
```

# JavaScript this

在 Java 等面向对象的语言中，this 关键字的含义是明确且具体的，即指代当前对象。一般在编译期确定下来，或称为编译期绑定。而在 JavaScript 中，this 是动态绑定，或称为运行期绑定的，这就导致 JavaScript 中的 this 关键字有能力具备多重含义，变得有点随意。而在 ES6 中又引入了 Arrow Function 以及 Class，它们对于 this 指针也带来了一定的影响。

## Default this: 默认情况下 this 的指向

### Global Context( 全局上下文 )

在任何函数之外的全局环境中，不管在不在 strict 模式中，this 指针往往指向一个全局变量。

```javascript
console.log(this.document === document); // true

// In web browsers, the window object is also the global object:
console.log(this === window); // true

this.a = 37;
console.log(window.a); // 37
```

### Simple Function Context( 简单函数上下文 )

在某个函数中，this 的值取决于该函数的调用者。无论是用`hello("world”)`还是 call 这种方式，都取决于传入该函数的对象。不过，在 ES5 的严格或者不严格模式下，同样的调用方式会有不同的结果。

```javascript
function hello(thing) {
  console.log('Hello ' + thing);
}

// this:
hello('world');

// 编译为
hello.call(window, 'world');
```

而如果是 strict 模式下：

```javascript
// this:
hello('world');

// 编译为
hello.call(undefined, 'world');
```

### DOM Event handler(DOM 事件 )

当某个函数作为事件监听器时，它的 this 值往往被设置为它的调用者。

```javascript
// When called as a listener, turns the related element blue
function bluify(e){
  // Always true
  console.log(this === e.currentTarget);
  // true when currentTarget and target are the same object
  console.log(this === e.target);
  this.style.backgroundColor = '#A5D9F3';
}

// Get a list of every element in the document
var elements = document.getElementsByTagName('*');

// Add bluify as a click listener so when the
// element is clicked on, it turns blue
for(var i=0  i<elements.length  i++){
  elements[i].addEventListener('click', bluify, false);
}
```

如果是行内的事件监听者，this 指针会被设置为其所在的 DOM 元素：

```javascript
<button onclick="alert(this.tagName.toLowerCase());">Show this</button>
```

## Manual Setting: 手动指定 this

### Closures( 闭包 )

```javascript
var asyncFunction = (param, callback) => {
  window.setTimeout(() => {
    callback(param);
  }, 1);
};

// Define a reference to `this` outside of the callback,
// but within the callback's lexical scope
var o = {
  doSomething: function() {
    var self = this;
    // Here we pass `o` into the async function,
    // expecting it back as `param`
    asyncFunction(o, function(param) {
      console.log('param === this?', param === self);
    });
  }
};

o.doSomething(); // param === this? true
```

### 对象方法

如果将某个方法设置为 Object 的一个属性，并且作为对象方法进行调用时，那么方法中的 this 指针会默认指向该 Object。

```javascript
function hello(thing) {
  console.log(this + ' says hello ' + thing);
}

person = { name: 'Brendan Eich' };
person.hello = hello;

person.hello('world'); // still desugars to person.hello.call(person, "world") [object Object] says hello world

hello('world'); // "[object DOMWindow]world"
```

这种效果等效于使用 apply/call 进行调用。

### call/apply: 运行时指定

```js
var Cat = function(name) {
  this.name = name;
};
var Dog = function(name) {
  this.name = name;
};
Cat.prototype.sayHi = function() {
  console.log(`${this.name} meows loudly!`);
};
Dog.prototype.sayHi = function() {
  console.log(`${this.name} barks excitedly!`);
};
var whiskers = new Cat('whiskers');
var fluffybottom = new Dog('fluffy bottom');
whiskers.sayHi(); // => whiskers meows loudly!
fluffybottom.sayHi(); // => fluffy bottom barks excitedly!
Cat.prototype.sayHi.call(fluffybottom); // => fluffy bottom meows loudly!
whiskers.sayHi.call(fluffybottom); // => fluffy bottom meows loudly!
Dog.prototype.sayHi.call(whiskers); // => whiskers barks excitedly!
fluffybottom.sayHi.call(whiskers); // => whiskers barks excitedly!
```

### bind: 绑定

```js
                    +-------------------+-------------------+
                    |                  |                  |
                    |      time of      |      time of    |
                    |function execution |    this binding  |
                    |                  |                  |
+-------------------+-------------------+-------------------+
|                  |                  |                  |
|  function object  |      future      |      future      |
|        f        |                  |                  |
|                  |                  |                  |
+-------------------+-------------------+-------------------+
|                  |                  |                  |
|  function call  |      now        |        now        |
|        f()        |                  |                  |
|                  |                  |                  |
+-------------------+-------------------+-------------------+
|                  |                  |                  |
|    f.call()      |      now        |        now        |
|    f.apply()    |                  |                  |
|                  |                  |                  |
+-------------------+-------------------+-------------------+
|                  |                  |                  |
|    f.bind()      |      future      |        now        |
|                  |                  |                  |
+-------------------+-------------------+-------------------+
```

很多时候，需要为某个函数指定一个固定的 this 对象，最简单的方式即是使用闭包来获取一个不变的 this 对象。 bind 函数的官方解释为：

> The `bind()` method creates a new function that, when called, has its `this` keyword set to the provided value, with a given sequence of arguments preceding any provided when the new function is called.

其作用可以用下面一个例子进行说明：

```javascript
this.x = 9;
var module = {
  x: 81,
  getX: function() {
    return this.x;
  }
};

module.getX(); // 81

var getX = module.getX;
getX(); // 9, because in this case, "this" refers to the global object

// Create a new function with 'this' bound to module
var boundGetX = getX.bind(module);
boundGetX(); // 81
```

bind 方法在 React 中应用的比较广泛，因为 React 声明方程时往往要绑定到 this 指针上。然而在异步编程中，this 指针极有可能指向错误，譬如：

```javascript
var myObj = {
  specialFunction: function() {},

  anotherSpecialFunction: function() {},

  getAsyncData: function(cb) {
    cb();
  },

  render: function() {
    var that = this;
    this.getAsyncData(function() {
      that.specialFunction();
      that.anotherSpecialFunction();
    });
  }
};

myObj.render();
```

如果在 getAsyncData 这个异步方程中调用`that.specialFunction();`，是会得到如下的错误显示：

> Uncaught TypeError: Object [object global] has no method 'specialFunction'

可以将代码以如下方式重写：

```js
render: function () {

    this.getAsyncData(function () {

        this.specialFunction();

        this.anotherSpecialFunction();

    }.bind(this));

}
```

bind 方程的支持情况如下：

| Browser           | Version support |
| ----------------- | --------------- |
| Chrome            | 7               |
| Firefox (Gecko)   | 4.0 (2)         |
| Internet Explorer | 9               |
| Opera             | 11.60           |
| Safari            | 5.1.4           |

```javascript
var person = {
  name: 'Brendan Eich',
  hello: function(thing) {
    console.log(this.name + ' says hello ' + thing);
  }
};

var boundHello = function(thing) {
  return person.hello.call(person, thing);
};

boundHello('world');
```

不过，这种方式仍然存在着一定的问题，ES5 为 Function 对象引入了一个新的 bind 方法来解决这个问题。bind() 方法会创建一个新函数，当这个新函数被调用时，它的 this 值是传递给 bind() 的第一个参数 , 它的参数是 bind() 的其他参数和其原本的参数。

```
fun.bind(thisArg[, arg1[, arg2[, ...]]])
```

- thisArg 当绑定函数被调用时，该参数会作为原函数运行时的 this 指向。当使用 new 操作符调用绑定函数时，该参数无效。
- arg1, arg2, ... 当绑定函数被调用时，这些参数加上绑定函数本身的参数会按照顺序作为原函数运行时的参数。

```javascript
var boundHello = person.hello.bind(person);
boundHello('world'); // "Brendan Eich says hello world"
```

这种方式在设置回调函数中的 this 指针的时候会起到很大的作用，特别是在 React 中，为了保证指针的稳定性，往往需要为内置方法设置 bind。

```javascript
var person = {
  name: 'Alex Russell',
  hello: function() {
    console.log(this.name + ' says hello world');
  }
};

$('#some-div').click(person.hello.bind(person));

// when the div is clicked, "Alex Russell says hello world" is printed
```

```javascript
var asyncFunction = (param, callback) => {
  window.setTimeout(() => {
    callback(param);
  }, 1);
};

// Here we control the context of the callback using
// `bind` ensuring `this` is correct
var o = {
  doSomething: function() {
    // Here we pass `o` into the async function,
    // expecting it back as `param`
    asyncFunction(
      o,
      function(param) {
        console.log('param === this?', param === this);
      }.bind(this)
    );
  }
};

o.doSomething(); // param === this? true
```

还有一个类似的实例是 array.forEach，在这样一个回调函数中，回调函数的 this 指针是由调用者决定的，完整的 forEach 声明如下：**array.forEach(callback[, thisArg])**，这个传入的 thisArg 即是回调的调用者。

```javascript
var o = {
  v: 'hello',
  p: ['a1', 'a2'],
  f: function f() {
    this.p.forEach(function(item) {
      console.log(this.v + ' ' + item);
    });
  }
};

o.f();
//undefined a1
//undefined a2
```

### Arrow Function 绑定

在 ECMAScript 中使用 Arrow Function 时候，会在创建该 Function 的时候即在创建时就被绑定到了闭合的作用域内，不会收到 new、bind 、 call 以及 apply 这些方法的影响。

```javascript
var asyncFunction = (param, callback) => {
  window.setTimeout(() => {
    callback(param);
  }, 1);
};

var o = {
  doSomething: function() {
    // Here we pass `o` into the async function,
    // expecting it back as `param`.
    //
    // Because this arrow function is created within
    // the scope of `doSomething` it is bound to this
    // lexical scope.
    asyncFunction(o, param => {
      console.log('param === this?', param === this);
    });
  }
};

o.doSomething(); // param === this? true
```
