> [JavaScript For 循环的函数式改造](https://zhuanlan.zhihu.com/p/24882997)翻译自[Rethinking JavaScript: Death of the For Loop](https://hackernoon.com/rethinking-javascript-death-of-the-for-loop-c431564c84a8#.vli8lstqx)。前两天笔者整理了一篇[JavaScript 函数式编程导论](https://zhuanlan.zhihu.com/p/24819380)，笔者个人不是很喜欢彻底的函数式编程化，在复杂逻辑处理与性能上可能都存在部分问题。不过借鉴函数式编程的思想去改造部分代码片以提高其可读性与可测试性还是蛮有好处的，此篇文章以   For-Loop 入手，笔者觉得讲得蛮好的。

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/1/2/1-wiBSyN1Kd2smpkR_EbcgpQ.jpeg)

For 循环的设计思想深受可变状态与副作用的影响，不过函数式编程中认为可变状态与副作用是导致潜在错误与不可预测性的罪魁祸首，是应该尽力避免的模式。众所周知，使用全局状态会污染局部代码，而同样的局部状态同样会导致与全局状态一样的问题，只不过因为局部状态的影响被限制在较小的影响范围内，因此不像全局状态那样的突兀。允许可变的状态也就意味着变量可能因为未知的原因被改变，开发者可能要花费数小时的时间去定位到底是哪一段代码修改了这个变量值。在过去的开发岁月里，我就是因为这样变秃了，却似乎没有变强。另一方面，任何修改除了作用域内变量值的函数都被称为有副作用的函数。典型的譬如修改全局变量、读入键盘输入、调用远程 API 、写入磁盘等等。副作用的功效强大，我们应该尽可能地将其封装与控制在一定范围内。大道理就回顾到这里，下面我们直接看下代码：

```
const cats = [
  { name: 'Mojo',    months: 84 },
  { name: 'Mao-Mao', months: 34 },
  { name: 'Waffles', months: 4 },
  { name: 'Pickles', months: 6 }
]
var kittens = []
//典型的 for 循环用法
for (var i = 0; i < cats.length; i++) {
  if (cats[i].months < 7) {
    kittens.push(cats[i].name)
  }
}
console.log(kittens)
```

我们改造的第一步是将`if`语句中的逻辑判断提取出来，老实说提取之后笔者觉得正好符合[Clean JavaScript:写出整洁的 JavaScript 代码](https://zhuanlan.zhihu.com/p/24761475)里面提及的让变量名而不是注释表述其含义的效果：

```
const isKitten = cat => cat.months < 7
var kittens = []
for (var i = 0; i < cats.length; i++) {
  if (isKitten(cats[i])) {
    kittens.push(cats[i].name)
  }
}
```

这种方式一方面增加了代码的可用性，另一方面也保证了我们测试条件的可测试性，特别是当我们的逻辑判断很复杂的时候。下面我们是将属性提取也抽象出来：

```js
const isKitten = cat => cat.months < 7;
const getName = cat => cat.name;
var kittens = [];
for (var i = 0; i < cats.length; i++) {
  if (isKitten(cats[i])) {
    kittens.push(getName(cats[i]));
  }
}
```

下面我们可以用函数式中的过滤与转换函数来描述这个过程：

```js
const isKitten = cat => cat.months < 7;
const getName = cat => cat.name;
const kittens = cats.filter(isKitten).map(getName);
```

到这里我们摒弃了`push`函数，即移除了可变状态的介入。最后的重构即是将我们的过滤与转换过程再进行一层封装，使其成为可复用的过程：

```
const isKitten = cat => cat.months < 7
const getName = cat => cat.name
const getKittenNames = cats =>
  cats.filter(isKitten)
      .map(getName)
const cats = [
  { name: 'Mojo',    months: 84 },
  { name: 'Mao-Mao', months: 34 },
  { name: 'Waffles', months: 4 },
  { name: 'Pickles', months: 6 }
]
const kittens = getKittenNames(cats)
console.log(kittens)
```
