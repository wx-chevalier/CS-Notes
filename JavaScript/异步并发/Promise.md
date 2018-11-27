[![返回目录](https://parg.co/USw)](https://parg.co/bxN)

# Promise/A 内部原理与常见接口实现

```js
try {
  let arrayLike = {
    0: Promise.resolve('233'),
    length: 1
  };
  Promise.all(arrayLike);
} catch (e) {
  console.log('error');
}

var promises = [
  new Promise(function(resolve) {
    setTimeout(function() {
      resolve(1);
    }, 90);
  }),
  new Promise(function(resolve) {
    setTimeout(function() {
      resolve(2);
    }, 10);
  }),
  new Promise(function(resolve) {
    setTimeout(function() {
      resolve(3);
    }, 50);
  })
];

Promise.all(promises).then(console.log);
```

# 异步设计模式

在日常的项目开发中我们经常会需要处理异步调用，本部分我们即讨论如何以回调、Promise 以及  async/await 来实现常见的异步模式。

## 失败重试

```js
function requestWithRetry(url, retryCount) {
  if (retryCount) {
    return new Promise((resolve, reject) => {
      const timeout = Math.pow(2, retryCount);

      setTimeout(() => {
        console.log('Waiting', timeout, 'ms');
        _requestWithRetry(url, retryCount)
          .then(resolve)
          .catch(reject);
      }, timeout);
    });
  } else {
    return _requestWithRetry(url, 0);
  }
}

function _requestWithRetry(url, retryCount) {
  return request(url, retryCount).catch(err => {
    if (err.statusCode && err.statusCode >= 500) {
      console.log('Retrying', err.message, retryCount);
      return requestWithRetry(url, ++retryCount);
    }
    throw err;
  });
}

requestWithRetry('http://localhost:3000')
  .then(res => {
    console.log(res);
  })
  .catch(err => {
    console.error(err);
  });
```

```js
function wait(timeout) {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve();
    }, timeout);
  });
}

async function requestWithRetry(url) {
  const MAX_RETRIES = 10;
  for (let i = 0; i <= MAX_RETRIES; i++) {
    try {
      return await request(url);
    } catch (err) {
      const timeout = Math.pow(2, i);
      console.log('Waiting', timeout, 'ms');
      await wait(timeout);
      console.log('Retrying', err.message, i);
    }
  }
}
```

## 数组转换

```js
function asyncThing(value) {
  return new Promise((resolve, reject) => {
    setTimeout(() => resolve(value), 100);
  });
}

async function mapArray() {
  return [1, 2, 3, 4].map(async value => {
    const v = await asyncThing(value);
    return v * 2;
  });
}

async function filterArray() {
  return [1, 2, 3, 4].filter(async value => {
    const v = await asyncThing(value);
    return v % 2 === 0;
  });
}

async function reduceArray() {
  return [1, 2, 3, 4].reduce(async (acc, value) => {
    return (await acc) + (await asyncThing(value));
  }, Promise.resolve(0));
}

// [ Promise { <pending> }, Promise { <pending> }, Promise { <pending> }, Promise { <pending> } ]
```

```javascript
filterArray().then(v => {
  console.log(v);
});

// [ 1, 2, 3, 4 ]
```

```js
typeof new Promise((resolve, reject) => {}) === 'object'; // true
```

Promise 本质上只是普通的 JavaScript 对象，包含了允许你执行某些异步代码的方法。

```js
const fetch = function(url) {
  return new Promise((resolve, reject) => {
    request((error, apiResponse) => {
      if (error) {
        reject(error);
      }

      resolve(apiResponse);
    });
  });
};
```

```js
class SimplePromise {
  constructor(executionFunction) {
    this.promiseChain = [];
    this.handleError = () => {};

    this.onResolve = this.onResolve.bind(this);
    this.onReject = this.onReject.bind(this);

    // 这里就可以发现传入的带回调的函数时立即执行的
    executionFunction(this.onResolve, this.onReject);
  }

  then(onResolve) {
    this.promiseChain.push(onResolve);

    return this;
  }

  catch(handleError) {
    this.handleError = handleError;

    return this;
  }

  onResolve(value) {
    let storedValue = value;

    try {
      this.promiseChain.forEach(nextFunction => {
        storedValue = nextFunction(storedValue);
      });
    } catch (error) {
      this.promiseChain = [];

      this.onReject(error);
    }
  }

  onReject(error) {
    this.handleError(error);
  }
}
```

当我们使用 `new Promise((resolve, reject) => {/* ... */})` 这样的形式去创建 Promise 对象时，传入的 executionFunction 函数的两个参数 resolve 与 reject， 实际上映射到了 SimplePromise 类的 onResolve 与 onReject 方法。而构造器同样会创建内置的 promiseChain 数组，该数组用于记录通过 then 设置的异步传入值；而 handleError 则用于响应 onReject 回调。在原生的 Promise 实现中，then 与 catch 都返回的是新的 Promise 对象，在 SimplePromise 的实现中我们则忽略了这一步。另外，原生的 Promise 实现中允许添加多个 catch 回调，并且不需要跟随在 then 后面。

Promise.all

Promise.finally

connect

combineReducer

```js
function connect(mapStateToProps, mapDispatchToProps) {
  // 构造好的封装组件
  class HOCComponent extends Component {
    // 利用 Context 进行状态传递, Todo
    getChildContext() {}

    handleProps() {
      let propsFromState;

      if (typeof mapStateToProps === 'function') {
        // 计算映射之后的 Props 值
        propsFromState = mapStateToProps(state);
      } else {
        propsFromState = mapStateToProps;
      }

      return { ...propsFromState, ...mapDispatchToProps };
    }

    render() {
      // 将新的 Props 映射入组件
      return React.createElement(
        HOCComponent.WrappedComponent,
        this.handleProps(mapStateToProps, mapDispatchToProps)
      );
    }
  }

  // 高阶函数
  function wrap(WrappedComponent) {
    HOCComponent.WrappedComponent = WrappedComponent;

    // 返回创建好的高阶函数
    return HOCComponent;
  }

  // 返回需要封装的高阶函数
  return wrap;
}

function combineReducers(reducers) {
  // 获取所有函数键
  const reducerKeys = Object.keys(reducers);

  // 返回封装之后的函数
  return function finalReducer(state = {}, action) {
    // 最终的状态
    const nextState = {};

    // 依次对于 Reducer 进行处理
    for (const key of reducerKeys) {
      // 获取 reducer
      const reducer = reducers[key];

      // 获取状态树中的子对象
      const stateByKey = state[key];

      // 执行 reduce 转换操作
      const nextStateByKey = reducer(stateByKey, action);

      // Redux 需要避免状态空，进行异常检测
      if (typeof nextStateByKey === 'undefined') {
        throw new Error('Invalid Reducer');
      }

      // 将新的状态对象挂载
      nextState[key] = nextStateByKey;
    }

    return nextState;
  };
}

function clientCacheMiddleware({ maxAge = 3600 * 24 * 365 }) {
  return function(req, res, next) {
    res.setHeader('Cache-Control', `max-age=${maxAge}`);

    next();
  };
}
```

## Promise 编排

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
    // p3更快，所以被解决(resolve)了
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
    // p6更快，所以被拒绝(reject了)
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

```js
const converge = (...promises) => (...args) => {
  let [head, ...tail] = promises;
  if (tail.length) {
    return head(...args).then(value =>
      converge(...tail)(...args.concat([value]))
    );
  } else {
    return head(...args);
  }
};

functionA(2).then(valueA => converge(functionB, functionC)(valueA));
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

## Promise.all: 并发执行

async/await 默认情况下是顺序执行的，

```js
async function executeAsyncTask() {
  const valueA = await functionA();
  const valueB = await functionB(valueA);
  return function3(valueA, valueB);
}
```

```js
async function executeParallelAsyncTasks() {
  const [valueA, valueB, valueC] = await Promise.all([
    functionA(),
    functionB(),
    functionC()
  ]);
  doSomethingWith(valueA);
  doSomethingElseWith(valueB);
  doAnotherThingWith(valueC);
}
```
