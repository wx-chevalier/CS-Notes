
# Promise/A 标准实现与异步模式


```js
try{
    let arrayLike ={
        0:Promise.resolve('233'),
        length:1,
    };
    Promise.all(arrayLike);
}catch(e){
    console.log('error');
};




var promises =[
    new Promise(function(resolve){
        setTimeout(function(){
            resolve(1)
        },90);
    }),
    new Promise(function(resolve){
        setTimeout(function(){
            resolve(2)
        },10);
    }),
    new Promise(function(resolve){
        setTimeout(function(){
            resolve(3)
        },50);
    }),
];


Promise.all(promises).then(console.log)
```




# 异步设计模式


在日常的项目开发中我们经常会需要处理异步调用，本部分我们即讨论如何以回调、Promise 以及 async/await 来实现常见的异步模式。


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

// [ 1, 2, 3, 4 ]
```


