> [2016: 编写高性能的 JavaScript](https://segmentfault.com/a/1190000007604645)翻译自 Felix Maier 的文章[Writing-Efficient-JavaScript](https://medium.com/@xilefmai/efficient-javascript-14a11651d563#.i6494k3bl)。本文从属于笔者的[Web 前端入门与最佳实践](https://github.com/wxyyxc1992/Web-Frontend-Introduction-And-Best-Practices)中[JavaScript 入门与最佳实践](https://github.com/wxyyxc1992/web-frontend-practice-handbook/blob/master/Syntax/JavaScript)系列文章。

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2016/11/3/4/canvas.png)

本文的初衷是想介绍如何利用些简单的代码小技巧就能促进 JavaScript 编译器的优化进程从而提升代码运行效率。特别是在游戏这种对于垃圾回收速度要求较高，你性能稍微差点用户就能见到白屏的地方。

# Monomorphism: 单态性

JavaScript 中允许函数调用时候传入动态参数，不过就以简单的 2 参数函数为例，当你的参数类型、参数数目与返回类型动态调用时才能决定，编译器需要更多的时间来解析。编译器自然地希望能够处理那些单态可预测的数据结构、参数统计等。

```
function example(a, b) {
  // we expect a, b to be numeric
  console.log(++a * ++b);
};

example(); // bad
example(1); // still bad
example("1", 2); // dammit meg

example(1, 2); // good
```

# Constants: 常量

使用常量能够让编译器在编译时即完成变量的值替换 :

```
const a = 42; // we can easily unfold this
const b = 1337 * 2; // we can resolve this expression
const c = a + b; // still can be resolved
const d = Math.random() * c; // we can only unfold 'c'

// before unfolding
a;
b;
c;
d;

// after unfolding
// we can do this at compile time!
42;
2674;
2716;
Math.random() * 2716;
```

# Inlining: 内联

JIT 编译器能够找出你的代码中被执行次数最多的部分，将你的代码分割成多个小的代码块能够有助于编译器在编译时将这些代码块转化为内联格式然后增加执行速度。

# Data Types: 数据类型

尽可能地多用 Numbers 与 Booleans 类型，因为他们与其他类似于字符串等原始类型相比性能表现更好。使用字符串类型可能会带来额外的垃圾回收消耗。

```
const ROBOT = 0;
const HUMAN = 1;
const SPIDER = 2;

let E_TYPE = {
  Robot: ROBOT,
  Human: HUMAN,
  Spider: SPIDER
};

// bad
// avoid uncached strings in heavy tasks (or better in general)
if (entity.type === "Robot") {

}

// good
// the compiler can resolve member expressions
// without much deepness pretty fast
if (entity.type === E_TYPE.Robot) {

}

// perfect
// right side of binary expression can even get unfold
if (entity.type === ROBOT) {

}
```

# Strict & Abstract Operators

尽可能使用`===`这个严格比较操作符而不是`==`操作符。使用严格比较操作符能够避免编译器进行类型推导与转换，从而提高一定的性能。

# Strict Conditions

JavaScript 中的 if 语句也非常灵活，你可以直接在`if(a) then bla`这个类型的条件选择语句中传入随意类似的 a 值。不过这种情况下，就像上文提及的严格比较操作符与宽松比较操作符一样，编译器需要将其转化为多个数据类型进行比较，而不能立刻得出结果。当然，这并不是一味的反对使用简写方式，而是在非常强调性能的场景，还是建议做好每一个细节的优化 :

```
let a = 2;

// bad
// abstracts to check in the worst case:
// - is value equal to true
// - is value greater than zero
// - is value not null
// - is value not NaN
// ..
if (a) {
 // if a is true, do something
}

// good
if (a === 2) {
  // do sth
}

// same goes for functions
function b() {
  return (!false);
};

if (b()) {
  // get in here slow
}

if (b() === true) {
  // get in here fast
  // the compiler knows a specific value to compare with
}
```

# Arguments

尽可能避免使用 arguments[index]方式进行参数获取，并且尽量避免修改传入的参数变量 :

```
function mul(a, b) {
  return (arguments[0]*arguments[1]); // bad, very slow
  return (a*b); // good
};

function test(a, b) {
  a = 5; // bad, dont modify argument identifiers
  let tmp = a; // good
  tmp *= 2; // we can now modify our fake 'a'
};
```

# Toxicity: 这些关键字有毒

# Toxicity

如下列举的几个语法特性会影响优化进程 :

* eval
* with
* try/catch

同时尽量避免在函数内声明函数或者闭包，可能在大量的运算中导致过多的垃圾回收操作。

# Objecs

Object 实例通常会共享隐类，因此当我们访问或者设置某个实例的未预定义变量值的时候会创建一个隐类。

```
// our hidden class 'hc_0'
class Vector {
  constructor(x, y) {
    // compiler finds and expects member declarations here
    this.x = x;
    this.y = y;
  }
};

// both vector objects share hidden class 'hc_0'
let vec1 = new Vector(0, 0);
let vec2 = new Vector(2, 2);

// bad, vec2 got hidden class 'hc_1' now
vec2.z = 0;

// good, compiler knows this member
vec2.x = 1;
```

# Loops

尽可能的缓存数组长度的计算值，并且尽可能在同一个数组中存放单个类型。避免使用`for-in`语法来遍历某个数组，因为它真的很慢。另外，continue 与 break 语句在循环中的性能也是不错的，这一点使用的时候不用担心。另外，尽可能将短小的逻辑部分拆分到独立的函数中，这样更有利于编译器进行优化。另外，使用前缀自增表达式，也能带来小小的性能提升。( ++i 代替 i++)

```
let badarray = [1, true, 0]; // bad, dont mix types
let array = [1, 0, 1]; // happy compiler

// bad choice
for (let key in array) {

};

// better
// but always try to cache the array size
let i = 0;
for (; i < array.length; ++i) {
  key = array[i];
};

// good
let i = 0;
let key = null;
let length = array.length;
for (; i < length; ++i) {
  key = array[i];
};
```

# drawImage

draeImage 函数算是最快的 2D Canvas API 之一了，不过我们需要注意的是如果为了图方便省略了全参数传入，也会增加性能损耗：

```
// bad
ctx.drawImage(
  img,
  x, y
);

// good
ctx.drawImage(
  img,
  // clipping
  sx, sy,
  sw, sh,
  // actual stuff
  x, y,
  w, h
);

// much hax
// no subpixel rendering by passing integers
ctx.drawImage(
  img,
  sx|0, sy|0,
  sw|0, sh|0,
  x|0, y|0,
  w|0, h|0
);
```
