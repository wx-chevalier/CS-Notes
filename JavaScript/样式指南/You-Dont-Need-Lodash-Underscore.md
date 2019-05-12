## You don't (may not) need Lodash/Underscore 

Lodash 和 Underscore 是非常优秀的当代JavaScript的工具集合框架，它们被前端开发者广泛地使用。但是，当我们现在是针对现代化浏览器进行开发时，很多时候我们利用的Underscore中的方法已经被ES5与ES6所支持了，如果我们希望我们的项目尽可能地减少依赖的话，我们可以根据目标浏览器来选择不用Lodash或者Underscore。

## 开发者的声音

>[Make use of native JavaScript object and array utilities before going big.](https://twitter.com/codylindley/status/692356631007342593)  
>Cody lindley, Author of [jQuery Cookbook](http://shop.oreilly.com/product/9780596159788.do),[JavaScript Enlightenment](http://shop.oreilly.com/product/0636920027713.do)

>[You probably don't need Lodash. Nice List of JavaScript methods which you can use natively.](https://twitter.com/daniellmb/status/692200768556916740)  
>Daniel Lamb, Computer Scientist, Technical Reviewer of [Secrets of the JavaScript Ninja](https://www.manning.com/books/secrets-of-the-javascript-ninja-second-edition), [Functional Programming in JavaScript](https://www.manning.com/books/functional-programming-in-javascript) 

>[I guess not, but I want it.](https://twitter.com/teropa/status/692280179666898944)  
>Tero Parviainen, Author of [build-your-own-angular](http://teropa.info/build-your-own-angular)

>[I'll admit, I've been guilty of overusing #lodash. Excellent resource.](https://twitter.com/therebelrobot/status/692907269512642561)  
>therebelrobot, Maker of web things, Facilitator for Node.js/io.js


## Quick links

1. [_.each](#_each)
1. [_.map](#_map)
1. [_.every](#_every)
1. [_.some](#_every)
1. [_.reduce](#_reduce)
1. [_.reduceRight](#_reduceright)
1. [_.filter](#_filter)
1. [_.find](#_find)
1. [_.findIndex](#_find)
1. [_.indexOf](#_indexof)
1. [_.lastIndexOf](#_lastindexof)
1. [_.includes](#_includes)
1. [_.keys](#_keys)
1. [_.size](#_size)
1. [_.isNaN](#_isnan)
1. [_.reverse](#_reverse)
1. [_.join](#_join)
1. [_.toUpper](#_toupper)
1. [_.toLower](#_tolower)
1. [_.trim](#_trim)
1. [_.repeat](#_repeat)
1. [_.after](#_after)

## _.each

遍历一系列的元素，并且调用回调处理方程。
Iterates over a list of elements, yielding each in turn to an iteratee function.

  ```js
  // Underscore/Lodash
  _.each([1, 2, 3], function(value, index) {
    console.log(value);
  });
  // output: 1 2 3

  // Native
  [1, 2, 3].forEach(function(value, index) {
    console.log(value);
  });
  // output: 1 2 3
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  ✔  | 1.5 ✔ |  9 ✔  |  ✔  |  ✔  |   

**[⬆ back to top](#quick-links)**

## _.map

将某个列表中的元素映射到新的列表中。

  ```js
  // Underscore/Lodash
  var array1 = [1, 2, 3];
  var array2 = _.map(array1, function(value, index) {
    return value*2;
  });
  console.log(array2);
  // output: [2, 4, 6]

  // Native
  var array1 = [1, 2, 3];
  var array2 = array1.map(function(value, index) {
    return value*2;
  });
  console.log(array2);
  // output: [2, 4, 6]
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  ✔  | 1.5 ✔ |  9 ✔  |  ✔  |  ✔  |  

**[⬆ back to top](#quick-links)**

## _.every

测试数组的所有元素是否都通过了指定函数的测试。
  ```js
  // Underscore/Lodash
  function isLargerThanTen(element, index, array) {
    return element >=10;
  }
  var array = [10, 20, 30];
  var result = _.every(array, isLargerThanTen);
  console.log(result);
  // output: true

  // Native
  function isLargerThanTen(element, index, array) {
    return element >=10;
  }

  var array = [10, 20, 30];
  var result = array.every(isLargerThanTen);
  console.log(result);
  // output: true
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  ✔  | 1.5 ✔ |  9 ✔  |  ✔  |  ✔  |  

**[⬆ back to top](#quick-links)**

## _.some

判断序列中是否存在元素满足给定方程的条件。
 
  ```js
  // Underscore/Lodash
  function isLargerThanTen(element, index, array) {
    return element >=10;
  }
  var array = [10, 9, 8];
  var result = _.some(array, isLargerThanTen);
  console.log(result);
  // output: true

  // Native
  function isLargerThanTen(element, index, array) {
    return element >=10;
  }

  var array = [10, 9, 8];
  var result = array.some(isLargerThanTen);
  console.log(result);
  // output: true
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  ✔  | 1.5 ✔ |  9 ✔  |  ✔  |  ✔  |  

**[⬆ back to top](#quick-links)**

## _.reduce

接收一个函数作为累加器(accumulator)，数组中的每个值(从左到右)开始缩减，最终为一个值。

  ```js
  // Underscore/Lodash
  var array = [0, 1, 2, 3, 4];
  var result = _.reduce(array, function (previousValue, currentValue, currentIndex, array) {
    return previousValue + currentValue;
  });
  console.log(result);
  // output: 10

  // Native
  var array = [0, 1, 2, 3, 4];
  var result = array.reduce(function (previousValue, currentValue, currentIndex, array) {
    return previousValue + currentValue;
  });
  console.log(result);
  // output: 10
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  ✔  | 3.0 ✔ |  9 ✔  |  10.5  |  4.0  |  

**[⬆ back to top](#quick-links)**

## _.reduceRight

接受一个函数作为累加器(accumulator)，让每个值(从右到左，亦即从尾到头)缩减为一个值。(与 reduce() 的执行方向相反)

  ```js
  // Underscore/Lodash
  var array = [0, 1, 2, 3, 4];
  var result = _.reduceRight(array, function (previousValue, currentValue, currentIndex, array) {
    return previousValue - currentValue;
  });
  console.log(result);
  // output: -2

  // Native
  var array = [0, 1, 2, 3, 4];
  var result = array.reduceRight(function (previousValue, currentValue, currentIndex, array) {
    return previousValue - currentValue;
  });
  console.log(result);
  // output: -2
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  ✔  | 3.0 ✔ |  9 ✔  |  10.5  |  4.0  |  

**[⬆ back to top](#quick-links)**

## _.filter

使用指定的函数测试所有元素，并创建一个包含所有通过测试的元素的新数组。

  ```js
  // Underscore/Lodash
  function isBigEnough(value) {
    return value >= 10;
  } 
  var array = [12, 5, 8, 130, 44];
  var filtered = _.filter(array, isBigEnough);
  console.log(filtered);
  // output: [12, 130, 44]

  // Native
  function isBigEnough(value) {
    return value >= 10;
  } 
  var array = [12, 5, 8, 130, 44];
  var filtered = array.filter(isBigEnough);
  console.log(filtered);
  // output: [12, 130, 44]
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  ✔  | 1.5 ✔ |  9 ✔  |  ✔  |  ✔  |  

**[⬆ back to top](#quick-links)**

## _.find

返回数组中满足测试条件的一个元素，如果没有满足条件的元素，则返回 undefined。

  ```js
  // Underscore/Lodash
  var users = [
    { 'user': 'barney',  'age': 36, 'active': true },
    { 'user': 'fred',    'age': 40, 'active': false },
    { 'user': 'pebbles', 'age': 1,  'active': true }
  ];

  _.find(users, function(o) { return o.age < 40; });
  // output: object for 'barney'

  // Native
  var users = [
    { 'user': 'barney',  'age': 36, 'active': true },
    { 'user': 'fred',    'age': 40, 'active': false },
    { 'user': 'pebbles', 'age': 1,  'active': true }
  ];

  users.find(function(o) { return o.age < 40; });
  // output: object for 'barney'
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  45.0  | 25.0 ✔ |  Not supported  |  Not supported |  7.1  |  

**[⬆ back to top](#quick-links)**

## _.findIndex

用来查找数组中某指定元素的索引, 如果找不到指定的元素, 则返回 -1.

  ```js
  // Underscore/Lodash
  var users = [
    { 'user': 'barney',  'age': 36, 'active': true },
    { 'user': 'fred',    'age': 40, 'active': false },
    { 'user': 'pebbles', 'age': 1,  'active': true }
  ];

  var index =  _.findIndex(users, function(o) { return o.age >= 40; });
  console.log(index);
  // output: 1

  // Native
  var users = [
    { 'user': 'barney',  'age': 36, 'active': true },
    { 'user': 'fred',    'age': 40, 'active': false },
    { 'user': 'pebbles', 'age': 1,  'active': true }
  ];

  var index =  users.findIndex(function(o) { return o.age >= 40; });
  console.log(index);
  // output: 1
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  45.0  | 25.0 ✔ |  Not supported  |  Not supported |  7.1  |  

**[⬆ back to top](#quick-links)**

## _.indexOf

返回指定值在字符串对象中首次出现的位置。从 fromIndex 位置开始查找，如果不存在，则返回 -1。

  ```js
  // Underscore/Lodash
  var array = [2, 9, 9];
  var result = _.indexOf(array, 2);    
  console.log(result); 
  // output: 0

  // Native
  var array = [2, 9, 9];
  var result = array.indexOf(2);    
  console.log(result); 
  // output: 0
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  ✔  | 1.5 ✔ |  9 ✔  |  ✔  |  ✔  |  

**[⬆ back to top](#quick-links)**

## _.lastIndexOf

返回指定元素(也即有效的 JavaScript 值或变量)在数组中的最后一个的索引，如果不存在则返回 -1。从数组的后面向前查找，从 fromIndex 处开始。

  ```js
  // Underscore/Lodash
  var array = [2, 9, 9, 4, 3, 6];
  var result = _.lastIndexOf(array, 9);    
  console.log(result); 
  // output: 2

  // Native
  var array = [2, 9, 9, 4, 3, 6];
  var result = array.lastIndexOf(9);    
  console.log(result); 
  // output: 2
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  ✔  |  ✔ |  9 ✔  |  ✔  |  ✔  |  

**[⬆ back to top](#quick-links)**

## _.includes

判断元素是否在列表中 

  ```js
  var array = [1, 2, 3];
  // Underscore/Lodash - also called with _.contains
  _.includes(array, 1);
  // → true

  // Native
  var array = [1, 2, 3];
  array.includes(1);
  // → true
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  47✔  | 43 ✔ |  Not supported  |  34 |  9  |  

**[⬆ back to top](#quick-links)**

## _.keys

返回某个对象所有可枚举的键名。

  ```js
  // Underscore/Lodash 
  var result = _.keys({one: 1, two: 2, three: 3});
  console.log(result);
  // output: ["one", "two", "three"]

  // Native
  var result2 = Object.keys({one: 1, two: 2, three: 3});
  console.log(result2); 
  // output: ["one", "two", "three"]
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  5✔  | 4.0 ✔ |  9  |  12 |  5  |  

**[⬆ back to top](#quick-links)**

## _.size

返回集合大小。

  ```js
  // Underscore/Lodash
  var result = _.size({one: 1, two: 2, three: 3});
  console.log(result);
  // output: 3

  // Native
  var result2 = Object.keys({one: 1, two: 2, three: 3}).length;
  console.log(result2); 
  // output: 3
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  5✔  | 4.0 ✔ |  9  |  12 |  5  |  

**[⬆ back to top](#quick-links)**

## _.isNaN

判断是否为NaN

  ```js
  // Underscore/Lodash
  console.log(_.isNaN(NaN));
  // output: true

  // Native
  console.log(isNaN(NaN));
  // output: true

  // ES6
  console.log(Number.isNaN(NaN));
  // output: true
  ```
MDN:
>In comparison to the global `isNaN()` function, `Number.isNaN()` doesn't suffer the problem of forcefully converting the parameter to a number. This means it is now safe to pass values that would normally convert to `NaN`, but aren't actually the same value as `NaN`. This also means that only values of the type number, that are also `NaN`, return true. [Number.isNaN()](https://developer.mozilla.org/en/docs/Web/JavaScript/Reference/Global_Objects/Number/isNaN)

Voice from the Lodash author:

>Lodash's `_.isNaN` is equiv to ES6 `Number.isNaN` which is different than the global `isNaN`.  
>--
- [jdalton](https://github.com/cht8687/You-Dont-Need-Lodash-Underscore/commit/b8559a603dccaaa2449b5a68a2d8325cf1fb29cd#)

### Browser Support for `isNaN`

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  ✔  |  ✔ |  ✔ |  ✔ |  ✔  |  

### Browser Support for `Number.isNaN`

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  25  | 15 |  Not supported |  ✔ |  9  |  

**[⬆ back to top](#quick-links)**

## _.reverse
:heavy_exclamation_mark:`Lodash only`
将一个序列反向。

  ```js
  // Lodash
  var array = [1, 2, 3];
  console.log(_.reverse(array));
  // output: [3, 2, 1]

  // Native
  var array = [1, 2, 3];
  console.log(array.reverse());
  // output: [3, 2, 1]
  ```

Voice from the Lodash author:

>Lodash's `_.reverse` just calls `Array#reverse` and enables composition like `_.map(arrays, _.reverse).`
It's exposed on _ because previously, like `Underscore`, it was only exposed in the chaining syntax.
>--
- [jdalton](https://github.com/cht8687/You-Dont-Need-Lodash-Underscore/commit/22c4bcf2be48dd415d2b073759805562e520b615#)  

### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  1.0✔  |  1.0✔ |  5.5✔ |  ✔ |  ✔  |  

**[⬆ back to top](#quick-links)**

## _.join
:heavy_exclamation_mark:`Lodash only`
将一个序列变成一个字符串。
  ```js
  // Lodash
  var result = _.join(['one', 'two', 'three'], '--');
  console.log(result);
  // output: 'one--two--three'

  // Native
  var result = ['one', 'two', 'three'].join('--');
  console.log(result)
  // output: 'one--two--three'
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  1.0✔  |  1.0✔ |  5.5✔  |  ✔ |  ✔  |  

**[⬆ back to top](#quick-links)**

## _.toUpper
:heavy_exclamation_mark:`Lodash only`
将字符串大写。

  ```js
  // Lodash
  var result = _.toUpper('foobar');
  console.log(result);
  // output: 'FOOBAR'

  // Native
  var result = 'foobar'.toUpperCase();
  console.log(result);
  // output: 'FOOBAR'
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  ✔  |  ✔ |  ✔  |  ✔ |  ✔  |  

**[⬆ back to top](#quick-links)**

## _.toLower
:heavy_exclamation_mark:`Lodash only`
将字符串变为小写。

  ```js
  // Lodash
  var result = _.toLower('FOOBAR');
  console.log(result);
  // output: 'foobar'

  // Native
  var result = 'FOOBAR'.toLowerCase();
  console.log(result);
  // output: 'foobar'
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  ✔  |  ✔ |  ✔  |  ✔ |  ✔  |  

**[⬆ back to top](#quick-links)**

## _.trim
:heavy_exclamation_mark:`Lodash only`
消去字符串起始的空白。

  ```js
  // Lodash
  var result = _.trim(' abc ');
  console.log(result);
  // output: 'abc'

  // Native
  var result = ' abc '.trim();
  console.log(result);
  // output: 'abc'
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  5.0✔  |  3.5✔ |  9.0✔  |  10.5✔ |  5.0✔  |  

**[⬆ back to top](#quick-links)**

## _.repeat
:heavy_exclamation_mark:`Lodash only`
重复创建字符串。

  ```js
  // Lodash
  var result = _.repeat('abc', 2);
  // output: 'abcabc'

  // Native
  var result = 'abc'.repeat(2);
  console.log(result);
  // output: 'abcabc'
  ```
### Browser Support

![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
--- | --- | --- | --- | --- |
  41✔  |  24✔ |  Not supported  |  Not supported |  9  |  

**[⬆ back to top](#quick-links)**

## _.after
:heavy_exclamation_mark:`Note this is an alternative implementation`
创建一个在经过了指定计数器之后才会被调用的方程。
 
 ```js
var notes = ['profile', 'settings'];
 // Underscore/Lodash
var renderNotes = _.after(notes.length, render);
    notes.forEach(function(note) {
    console.log(note);
    renderNotes();
});
 
  // Native
notes.forEach(function(note, index) {
  console.log(note);
  if (notes.length === (index + 1)) {
    render();
  }
});
```
### Browser Support
 
 ![Chrome](https://raw.github.com/alrra/browser-logos/master/chrome/chrome_48x48.png) | ![Firefox](https://raw.github.com/alrra/browser-logos/master/firefox/firefox_48x48.png) | ![IE](https://raw.github.com/alrra/browser-logos/master/internet-explorer/internet-explorer_48x48.png) | ![Opera](https://raw.github.com/alrra/browser-logos/master/opera/opera_48x48.png) | ![Safari](https://raw.github.com/alrra/browser-logos/master/safari/safari_48x48.png)
 --- | --- | --- | --- | --- |
   ✔  |  ✔ |  ✔ |  ✔ |  ✔  |  
 
 **[⬆ back to top](#quick-links)**

## Reference

* [Mozilla Developer Network](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference)
* [Underscore.js](http://underscorejs.org)
* [Lodash.js](https://lodash.com/docs)

## Inspired by:

* [You-Dont-Need-jQuery](https://github.com/oneuijs/You-Dont-Need-jQuery)
* [Rui's blog](http://ktei.github.io/2016/01/07/some-general-js-tips-1.html)

