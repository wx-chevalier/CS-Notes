# V8 中的内存管理

内存分为堆（heap）和栈（stack）。栈内存储简单数据类型，方便快速写入和读取数据。堆内存则可以存储复杂的数据类型。在访问数据时，先从栈内寻找相应数据的存储地址，再根据获得的地址，找到堆内该变量真正存储的内容读取出来。

在前端中，被存储在栈内的数据包括小数值型，string ，boolean 和复杂类型的地址索引。所谓小数值数据(small number), 即长度短于 32 位存储空间的 number 型数据。一些复杂的数据类型，诸如 Array，Object 等，是被存在堆中的。如果我们要获取一个已存储的对象 A，会先从栈中找到这个变量存储的地址，再根据该地址找到堆中相应的数据。

![](https://i.postimg.cc/J0GnS4dB/image.png)

简单的数据类型由于存储在栈中，读取写入速度相对复杂类型（存在堆中）会更快些。下面的 Demo 对比了存在堆中和栈中的写入性能：

```js
function inStack() {
  let number = 1e5;
  var a;

  while (number--) {
    a = 1;
  }
}

var obj = {};
function inHeap() {
  let number = 1e5;

  while (number--) {
    obj.key = 1;
  }
}
```
