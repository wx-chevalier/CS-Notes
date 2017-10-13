[![章节头]("https://parg.co/UG3")](﻿https://parg.co/bxN) 
 - [JavaScript 异步编程](#javascript-%E5%BC%82%E6%AD%A5%E7%BC%96%E7%A8%8B)
- [回调](#%E5%9B%9E%E8%B0%83)
  * [事件监听](#%E4%BA%8B%E4%BB%B6%E7%9B%91%E5%90%AC)
- [Promise](#promise)
  * [链式调用](#%E9%93%BE%E5%BC%8F%E8%B0%83%E7%94%A8)
- [Generator](#generator)
- [async/await](#asyncawait)
  * [多函数调用](#%E5%A4%9A%E5%87%BD%E6%95%B0%E8%B0%83%E7%94%A8)
  * [异常处理](#%E5%BC%82%E5%B8%B8%E5%A4%84%E7%90%86) 






[![](https://parg.co/UYU)](https://parg.co/bxN)



# JavaScript 异步编程


异步函数语法在其他语言中存在已久，就像 C# 中的 async/await、Kotlin 中的 coroutines、Go 中的 goroutines；而随着 Node.js 8 的发布，async/await 语法也得到了原生支持而不再需要依赖于 Babel 等转化工具。


# 回调


## 事件监听


```js
function processFile(inputFile) {

    var fs = require('fs'),

        readline = require('readline'),

        instream = fs.createReadStream(inputFile),

        outstream = new (require('stream'))(),

        rl = readline.createInterface(instream, outstream);

     

    rl.on('line', function (line) {

        console.log(line);

    });

    

    rl.on('close', function (line) {

        console.log(line);

        console.log('done reading file.');

    });

}

processFile('/path/to/a/input/file.txt');

```


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



