
> [ES6 函数默认参数、TDZ以及Firefox 50.x 版本的实现](https://zhuanlan.zhihu.com/p/24979292) 从属于 [Web 前端入门与工程实践](https://github.com/wxyyxc1992/Web-Frontend-Introduction-And-Engineering-Practices)


昨天看到阮老师发的一个微博：


![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/1/3/WechatIMG1.jpeg)


笔者自己也尝试了下，在 Chrome 中：


![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/1/3/QQ20170121-02x.png) 


在 Firefox 与 Babel 中的效果：


![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/1/3/QQ20170121-0232.png) 


理论上来说，ES6 中引入了 Temporal Dead Zone 的概念，即参数或者变量不可在初始化前被访问，上述代码中的`= x`赋值语句是在参数域中被执行，因此会覆盖掉全局定义中的`x`，自然就无法被初始化为自身。而我们使用额外的参数指向参数作用域中的`x`时就可以执行：
```
function foo(x, y = x) { // OK
  ...
}
```
这里的`y`的默认值会被赋予为`undefined`而不是抛出异常，因为在从左向右初始化参数的时候已经给`x`赋予了`undefined`。而在 Babel 中，因为其将默认参数解析为了具体的值判断，因此不存在 TDZ 问题：
```
// ES6
function foo(x, y = function() { x = 2; }) {
  var x = 3;
  y(); // is `x` shared?
  console.log(x); // no, still 3, not 2
}
 
// Compiled to ES5
function foo(x, y) {
  // Setup defaults.
  if (typeof y == 'undefined') {
    y = function() { x = 2; }; // now clearly see that it updates `x` from params
  }
 
  return function() {
    var x = 3; // now clearly see that this `x` is from inner scope
    y();
    console.log(x);
  }.apply(this, arguments);
}
```
不过号称在 FF15 版本中就实现了默认参数的 Firefox 为何会与 Babel 有一样的表现呢？笔者截止目前还没有确定答案，有可能是 FF50 中还没有实现 TDZ 吧：
![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/1/3/9073440C-E61F-4ADB-9613-70E92DDAA277.png) 


除此之外还有个小疑问：
```function rest(b = a, ...a) {
console.log(b, a);
}

rest(undefined, 2);
```
上述代码在 Chrome 中输出为`[2][2]`，Firefox 中输出`undefined`，直接运行 Babel 抛出异常，官方 Babel 的编译结果为：
```
"use strict";


function rest() {
  var b = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : a;


  for (var _len = arguments.length, a = Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
    a[_key - 1] = arguments[_key];
  }


  console.log(b, a);
}


rest(undefined, 2);
```
执行结果为`undefined [2]`



