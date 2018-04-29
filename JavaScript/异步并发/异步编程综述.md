[![返回目录](https://parg.co/USw)](https://parg.co/bxN)

# JavaScript 异步编程

异步函数语法在其他语言中存在已久，就像 C# 中的 async/await、Kotlin 中的 coroutines、Go 中的 goroutines；而随着 Node.js 8 的发布，async/await 语法也得到了原生支持而不再需要依赖于 Babel 等转化工具。 浏览器或者 Node.js 这样的 JavaScript Runtime

```js
var start = new Date();
setTimeout(function() {
  var end = new Date();
  console.log('Time elapsed:', end - start, 'ms');
}, 500);
while (new Date() - start < 1000) {}
```

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/6/1/async.png)

[Error Handling In Node/Javascript Sucks. Unless You Know this. 2018](https://parg.co/Uiy)

# Callback: 回调

回调常用于事件监听中，

```js
function processFile(inputFile) {
  var fs = require('fs'),
    readline = require('readline'),
    instream = fs.createReadStream(inputFile),
    outstream = new (require('stream'))(),
    rl = readline.createInterface(instream, outstream);

  rl.on('line', function(line) {
    console.log(line);
  });

  rl.on('close', function(line) {
    console.log(line);

    console.log('done reading file.');
  });
}

processFile('/path/to/a/input/file.txt');
```

## Callback Hell

## Zalgo

# Promise

一个 Promise 应该会处于以下的几个状态中：

* pending / Unfulfilled ：初始状态，没有完成或者拒绝
* fulfilled / resolved：意味着该 Promise 已经完成
* rejected：意味着该操作失败

## Promise Orchestration: 编排多个 Promise

```js
/*
 * promiseSerial resolves Promises sequentially.
 * @example
 * const urls = ['/url1', '/url2', '/url3']
 * const funcs = urls.map(url => () => $.ajax(url))
 *
 * promiseSerial(funcs)
 *   .then(console.log)
 *   .catch(console.error)
 */
const promiseSerial = funcs =>
  funcs.reduce(
    (promise, func) =>
      promise.then(result => func().then(Array.prototype.concat.bind(result))),
    Promise.resolve([])
  );

// some url's to resolve
const urls = ['/url1', '/url2', '/url3'];

// convert each url to a function that returns a promise
const funcs = urls.map(url => () => $.ajax(url));

// execute Promises in serial
promiseSerial(funcs)
  .then(console.log)
  .catch(console.error);
```

### Promise.race: 返回第一个确定状态

race 函数返回一个 Promise，这个 Promise 根据传入的 Promise 中的第一个确定状态 -- 不管是接受还是拒绝 -- 的状态而确定状态。

```js
var p1 = new Promise(function(resolve, reject) {
  setTimeout(resolve, 500, '一');
});
var p2 = new Promise(function(resolve, reject) {
  setTimeout(resolve, 100, '二');
});

Promise.race([p1, p2]).then(function(value) {
  console.log(value); // "二"
  // 两个都解决，但p2更快
});

var p3 = new Promise(function(resolve, reject) {
  setTimeout(resolve, 100, '三');
});
var p4 = new Promise(function(resolve, reject) {
  setTimeout(reject, 500, '四');
});

Promise.race([p3, p4]).then(
  function(value) {
    console.log(value); // "三"
    // p3更快，所以被解决（resolve）了
  },
  function(reason) {
    // 未被执行
  }
);

var p5 = new Promise(function(resolve, reject) {
  setTimeout(resolve, 500, '五');
});
var p6 = new Promise(function(resolve, reject) {
  setTimeout(reject, 100, '六');
});

Promise.race([p5, p6]).then(
  function(value) {
    // 未被执行
  },
  function(reason) {
    console.log(reason); // "六"
    // p6更快，所以被拒绝（reject了）
  }
);
```

```js
function executeAsyncTask() {
  return functionA().then(valueA => {
    return functionB(valueA).then(valueB => {
      return functionC(valueA, valueB);
    });
  });
}
```

```
const converge = (...promises) => (...args) => {
  let [head, ...tail] = promises
  if (tail.length) {
    return head(...args)
      .then((value) => converge(...tail)(...args.concat([value])))
  } else {
    return head(...args)
  }
}


functionA(2)
  .then((valueA) => converge(functionB, functionC)(valueA))
```

```
let promise = new Promise((resolve,reject)=>{
	setTimeout(()=>{
		console.log('2 in setTimeout');
		resolve(2);
	},0);
	resolve(1);
});


promise.then((value)=>{
	console.log(value);
})


// 1
// 2 in setTimeout
```

```js
const prom = new Promise((resolve, reject) => {
  setTimeout(() => {
    resolve(5);
  }, 1000);
});

prom.then(value => {
  console.log(value);
});

setTimeout(() => {
  prom.then(value => {
    console.log(value);
  });
}, 5000);
```

## 链式调用

# Generator

异步编程的核心目标是希望下一个异步操作能够等待上一个异步执行得到结果之后再运行，即实现函数的顺序执行。而生成器中其会在 `yield` 处停止执行直到触发下一个 `next` 调用，我们可以在 `yield` 后进行异步操作，然后在异步操作中执行生成器的 `next` 函数：

```
   function asyncfuc(v) {
        setTimeout(function() {
            let r = v + 20;
            console.log(r);
            g.next(r); //把异步函数执行得到的结果传出并触发下一个yield
        }, 500);
    }


    let g = function* gen() {
        let v1 = yield asyncfuc(0);
        let v2 = yield asyncfuc(v1);  //上一个异步调用的结果作为下一个异步调用的入参
        return v2;
    }();

    g.next();
```

```js
//这个方法用来模拟一个异步调用
function delay(time, callback) {
  setTimeout(function() {
    callback(`slept for ${time}`);
  }, time);
}

function run(...functions) {
  //构造一个生成器循环执行传入的方法
  var generator = (function* sync(resume, functions) {
    let result;
    for (var func of functions) {
      result = yield func(result, resume); //前一个方法执行的结果作为下一个方法的入参
    }
    return result;
  })(resume, functions); //提供一个方法用于推进生成器执行。

  function resume(callbackValue) {
    generator.next(callbackValue);
  }
  generator.next(); //触发生成器立即执行第一个方法
} //模拟异步方法调用, 斐波那契数列

function d(result, resume) {
  delay(1000, msg => {
    let value = result;
    if (value) {
      [value.a, value.b] = [value.b, value.a + value.b];
    } else {
      value = { a: 0, b: 1 };
    }
    console.log(value.a);
    resume(value);
  });
  return result;
}

run(d, d, d); //顺序执行异步方法
```

# async/await

```js
function asyncTask() {
  return functionA()
    .then(valueA => functionB(valueA))
    .then(valueB => functionC(valueB))
    .then(valueC => functionD(valueC))
    .catch(err => logger.error(err));
}
```

```js
var wait = ms => new Promise((r, j)=>setTimeout(r, ms))

// Promise syntax
var prom = wait(2000)  // prom, is a promise
var showdone = ()=>console.warn('done')
prom.then(showdone)
// same thing, using await syntax
await wait(2000)
console.warn('done')
```

## 多函数调用

async/await 默认情况下是顺序执行的，

```
async function executeAsyncTask () {
  const valueA = await functionA()
  const valueB = await functionB(valueA)
  return function3(valueA, valueB)
}
```

```
async function executeParallelAsyncTasks () {
  const [ valueA, valueB, valueC ] = await Promise.all([ functionA(), functionB(), functionC() ])
  doSomethingWith(valueA)
  doSomethingElseWith(valueB)
  doAnotherThingWith(valueC)
}
```

## 异常处理

引入[此文](https://blog.lavrton.com/javascript-loops-how-to-handle-async-await-6252dd3c795)中对于 async/await 的循环的写法

```js
async function waitAndMaybeReject() {
  // Wait one second
  await new Promise(r => setTimeout(r, 1000));
  // Toss a coin
  const isHeads = Boolean(Math.round(Math.random()));

  if (isHeads) return 'yay';
  throw Error('Boo!');
}

async function foo() {
  try {
    // Wait for the result of waitAndMaybeReject() to settle,
    // and assign the fulfilled value to fulfilledValue:
    const fulfilledValue = await waitAndMaybeReject();
    // If the result of waitAndMaybeReject() rejects, our code
    // throws, and we jump to the catch block.
    // Otherwise, this block continues to run:
    return fulfilledValue;
  } catch (e) {
    return 'caught';
  }
}
```

Promises 是为了让异步代码也能保持这些同步代码的属性：扁平缩进和单异常管道。在 ES6 之前，存在着很多的 Promise 的支持库，譬如著名的 q 以及 jQuery 中都有着对于 Promise 模式的内在的实现。在 ES6 之后，笔者是推荐仅使用 ES6 提供的 Promise 对象。

### ES6 Promise

ES6 中内置了 Promise 对象，不过目前 Babel 并没有对它进行转化 (ES5 中并没有相对应的方法 )，所以如果需要在老浏览中使用的话请参考[es6-promise](https://github.com/jakearchibald/es6-promise)这个 Promise。如果对于 Promise 的执行顺序感到疑惑的可以参考下这个[可视化的 Playground](https://github.com/bevacqua/promisees)。

![Promise visualization playground for the adventurous](https://camo.githubusercontent.com/8f60cd1c340b27a2490325da47e6b079ba3007f0/687474703a2f2f692e696d6775722e636f6d2f4f753551304e622e676966)

安装 :

```
npm install es6-promise
```

使用 :

```
var Promise = require('es6-promise').Promise;
```

如果需要自动的全局替换的话，利用：

```
require('es6-promise').polyfill();
```

即可。

不过对于 IE < 9 的情况，因为 catch 为保留的关键字，因此需要用如下方式：

```javascript
promise['catch'](function(err) {
  // ...
});
```

或者直接使用`.then`来作为代替：

```javascript
promise.then(undefined, function(err) {
  // ...
});
```

### Basic Usage

基本的 Promise 对象的声明为：

```
new Promise(executor);
new Promise(function(resolve, reject) { ... });
```

其中 executor 是一个包含了两个参数的方程。其中 resolve 表示履行或者实现了该 Promise，而第二个参数意味着拒绝了该 Promise。

```javascript
var p1 = new Promise(function(resolve, reject) {
  resolve('Success!');
  // or
  // reject ("Error!");
});

p1.then(
  function(value) {
    console.log(value); // Success!
  },
  function(reason) {
    console.log(reason); // Error!
  }
);
```

#### 异常捕获

最容易犯的错误，没有使用 `catch` 去捕获 `then` 里抛出的报错：

```
// snippet1
somePromise().then(function () {
  throw new Error('oh noes');
}).catch(function (err) {
  // I caught your error! :)
});

// snippet2
somePromise().then(function resolve() {
  throw new Error('oh noes');
}, function reject(err) {
  // I didn't catch your error! :(
});
```

这里的问题在于 snippet2 在 function resolve 中出错时无法捕获。而 catch 则可以。

下面的两个示例返回结果是不一样的：

```
// example1
Promise.resolve('foo').then(Promise.resolve('bar')).then(function (result) {
  console.log(result);  // foo
});
// example2
Promise.resolve('foo').then(function () {
  return Promise.resolve('bar')
}).then(function (result) {
  console.log(result); // bar
});
```

example2 改变了返回值，因而 result 发生了变化。

### 嵌套

一个完整的 Promise 的串联为：

![](https://mdn.mozillademos.org/files/8633/promises.png)

最基本的串联方式是可以基于回调函数式的串联，即在一个 Promise 对象的 Resolve 或者 Reject 方法内调用另一个 Promise：

```
loadSomething().then(function(something) {
  loadAnothering().then(function(another) {
    DoSomethingOnThem(something, another);
  });
});
```

鉴于 Promise 的 then 与 catch 方法本身也是返回一个 Promise 对象，因此它们可以很方便地即进行串联：

```javascript
var p2 = new Promise(function(resolve, reject) {
  resolve(1);
});

p2
  .then(function(value) {
    console.log(value); // 1
    return value + 1;
  })
  .then(function(value) {
    console.log(value); // 2
  });

//注意，这里演示了如果对一个原始的Promise对象设置多个then方法，那么在不同的代码块里的then是不会自动串联的
p2.then(function(value) {
  console.log(value); // 1
});
```

#### Promise.all: 等待多个 Promise 的结果

如果只是想对两个 promise 的结果做处理，可以使用 Promise.all 方法：

```
Promise.all([loadSomething, loadAnothering]).then(function(something, another) {
  DoSomethingOnThem(something, another);
});
```

Promise.all 也可以用于对数组中的元素执行异步操作，例如需要对一个集合中的每个元素执行异步操作：

```
function workMyCollection(arr) {
  var resultArr = [];
  function _recursive(idx) {
    if (idx >= resultArr.length) return resultArr;
    return doSomethingAsync(arr[idx]).then(function(res) {
      resultArr.push(res);
      return _recursive(idx + 1);
    });
  }
  return _recursive(0);
}
```

而如果引入了 Promise.all 的话，那就会清晰很多：

```
function workMyCollection(arr) {
  return Promise.all(
    arr.map(function(item) {
      return doSomethingAsync(item);
    })
  );
}
```

#### 断链

例如：

```
function anAsyncCall() {
  var promise = doSomethingAsync();
  promise.then(function() {
    somethingComplicated();
  });
  return promise;
}
```

这里的问题在于加入 `somethingComplicated()` 出错的话不会被捕获。promise 应该链式调用。也就是说所有的 `then` 方法都应该返回一个新的 `promise`。所以上面代码的正确写法为：

```
function anAsyncCall() {
  var promise = doSomethingAsync();
  return promise.then(function() {
    somethingComplicated();
  });
}
```

[stage 4](https://github.com/tc39/proposal-promise-finally),

```js
let isLoading = true;

fetch(myRequest)
  .then(function(response) {
    var contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      return response.json();
    }
    throw new TypeError("Oops, we haven't got JSON!");
  })
  .then(function(json) {
    /* process your JSON further */
  })
  .catch(function(error) {
    console.log(error);
  })
  .finally(function() {
    isLoading = false;
  });
```

```js
// 不使用 finally
showLoadingSpinner();
fetch('data.json')
  .then(data => {
    renderContent(data);
    hideLoadingSpinner();
  })
  .catch(error => {
    displayError(error);
    hideLoadingSpinner();
  });

// 使用 finally
showLoadingSpinner();
fetch('data.json')
  .then(data => {
    renderContent(data);
  })
  .catch(error => {
    displayError(error);
  })
  .finally(() => {
    hideLoadingSpinner();
  });
```
