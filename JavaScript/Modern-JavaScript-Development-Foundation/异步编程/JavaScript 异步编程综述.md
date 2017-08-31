[toc]
# JavaScript 异步编程

异步函数语法在其他语言中存在已久，就像 C# 中的 async/await、Kotlin 中的 coroutines、Go 中的 goroutines；而随着 Node.js 8 的发布，async/await 语法也得到了原生支持而不再需要依赖于 Babel 等转化工具。

# 回调

# Promise

```
function executeAsyncTask () {  
  return functionA()
    .then((valueA) => {
      return functionB(valueA)
        .then((valueB) => {          
          return functionC(valueA, valueB)
        })
    })
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
        let v2 = yield asyncfuc(v1);  //上一个异步调用的结果作为下一个异步调用的入参
        return v2;
    }();
    
    g.next();
```
```
//这个方法用来模拟一个异步调用
    function delay(time, callback) {
      setTimeout(function () {
        callback(`slept for ${time}`);
      }, time);
    }
    
    function run(...functions) {
        //构造一个生成器循环执行传入的方法
        var generator = function* sync(resume, functions) {
            let result;
            for (var func of functions) {
                result = yield func(result, resume); //前一个方法执行的结果作为下一个方法的入参
            }
            return result;
        }(resume, functions);
        
        //提供一个方法用于推进生成器执行。
        function resume(callbackValue) {
            generator.next(callbackValue);
        }
        generator.next(); //触发生成器立即执行第一个方法
    }
    
    //模拟异步方法调用, 斐波那契数列
    function d(result, resume) {
        delay(1000, (msg) => {
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

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/6/1/async.png)

```
function asyncTask () {  
  return functionA()
    .then((valueA) => functionB(valueA))
    .then((valueB) => functionC(valueB))
    .then((valueC) => functionD(valueC))
    .catch((err) => logger.error(err))
}
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

# 异步设计模式

在日常的项目开发中我们经常会需要处理异步调用，本部分我们即讨论如何以回调、Promise 以及 async/await 来实现常见的异步模式。

## 失败重试

```
function requestWithRetry (url, retryCount) {  
  if (retryCount) {
    return new Promise((resolve, reject) => {
      const timeout = Math.pow(2, retryCount)

      setTimeout(() => {
        console.log('Waiting', timeout, 'ms')
        _requestWithRetry(url, retryCount)
          .then(resolve)
          .catch(reject)
      }, timeout)
    })
  } else {
    return _requestWithRetry(url, 0)
  }
}

function _requestWithRetry (url, retryCount) {  
  return request(url, retryCount)
    .catch((err) => {
      if (err.statusCode && err.statusCode >= 500) {
        console.log('Retrying', err.message, retryCount)
        return requestWithRetry(url, ++retryCount)
      }
      throw err
    })
}

requestWithRetry('http://localhost:3000')  
  .then((res) => {
    console.log(res)
  })
  .catch(err => {
    console.error(err)
  })
```

```
function wait (timeout) {  
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve()
    }, timeout)
  })
}

async function requestWithRetry (url) {  
  const MAX_RETRIES = 10
  for (let i = 0; i <= MAX_RETRIES; i++) {
    try {
      return await request(url)
    } catch (err) {
      const timeout = Math.pow(2, i)
      console.log('Waiting', timeout, 'ms')
      await wait(timeout)
      console.log('Retrying', err.message, i)
    }
  }
}
```

## 数组转换

```
function asyncThing (value) {  
  return new Promise((resolve, reject) => {
    setTimeout(() => resolve(value), 100)
  })
}

async function mapArray() {  
  return [1,2,3,4].map(async (value) => {
    const v = await asyncThing(value)
    return v * 2
  })
}

async function filterArray() {  
  return [1,2,3,4].filter(async (value) => {
    const v = await asyncThing(value)
    return v % 2 === 0
  })
}

async function reduceArray() {  
  return [1,2,3,4].reduce(async (acc, value) => {
    return await acc + await asyncThing(value)
  }, Promise.resolve(0))
}
```
```javascript
// [ Promise { <pending> }, Promise { <pending> }, Promise { <pending> }, Promise { <pending> } ]
```

```javascript
filterArray().then((v)=>{console.log(v)})
// [ 1, 2, 3, 4 ]
```

