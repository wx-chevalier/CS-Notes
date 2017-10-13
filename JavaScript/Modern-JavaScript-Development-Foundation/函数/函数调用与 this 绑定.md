[![章节头]("https://parg.co/UG3")](﻿https://parg.co/bxN) 
 - [函数执行](#%E5%87%BD%E6%95%B0%E6%89%A7%E8%A1%8C)
  * [执行上下文中的细节](#%E6%89%A7%E8%A1%8C%E4%B8%8A%E4%B8%8B%E6%96%87%E4%B8%AD%E7%9A%84%E7%BB%86%E8%8A%82)
    + [活动对象 / 变量对象 [AO/VO]](#%E6%B4%BB%E5%8A%A8%E5%AF%B9%E8%B1%A1--%E5%8F%98%E9%87%8F%E5%AF%B9%E8%B1%A1-aovo)
- [bind](#bind) 


# 函数执行
我们已经知道, 浏览器第一次加载脚本, 它将默认进入 `全局执行上下文` 中。 如果,你在全局环境中调用了一个函数, 你的程序序列流会进入被调用的函数的当中，创建一个新的 `执行上下文` 并且将这个上下文压入`执行栈`之中。



如果你在当前函数里面又调用了另外一个函数, 也会发生同样的事情。代码的执行流进入内部函数，这将创建一个新的`执行上下文`，它被压入现有栈的顶部。浏览器永远会执行当前栈中顶部的`执行上下文` 一旦函数在当前`执行上下文`执行完毕，它会被从栈的顶部弹出，然后将控制权移交给当前栈的下一个上下文当中。 下面的代码展示了一个递归函数以及程序的 `执行上下文`:



```

(function foo(i) {

    if (i === 3) {

        return;

    }

    else {

        foo(++i);

    }

}(0));

```



[![img](http://p0.qhimg.com/t0151cc8a48b0da097c.gif)](http://p0.qhimg.com/t0151cc8a48b0da097c.gif)



这段代码调用自己自身3次, 每次将 i 的值增加 1。 每次函数 `foo` 被调用的时候, 就会创建一个新的执行上下文。 一旦上下文执行完毕之后, 它就会从栈中弹出并且返回控制权到下一个上下文当中，直到`全局上下文` 又再次被访问。



**关于 执行上下文** 有五个要点是要记住的:



- 单线程。

- 同步执行。

- 只有一个全局上下文。

- 可有无数个函数上下文。

- 每个函数调用都会创建一个新的 `执行上下文`，哪怕是递归调用。



## 执行上下文中的细节



现在我们已经知道了每个函数调用都会创建一个新的 `执行上下文` 。 然而，在 JavaScript 解释器内部，对每个执行上下文的调用会经历两个阶段：



1. **创建阶段** [当函数被调用, 但内部的代码还没开始执行]:

2. 创建 [作用域链](http://davidshariff.com/blog/javascript-scope-chain-and-closures/).

3. 创建变量、函数以及参数

4. 决定 [`"this"`](http://davidshariff.com/blog/javascript-this-keyword/)的值

5. **激活 / 代码执行阶段**:

6. 赋值, 寻找函数引用以及解释 /执行代码



我们可以用一个具有三个属性的概念性对象来代表 `执行上下文`：



```

executionContextObj = {

    'scopeChain': { /* 变量对象 + 所有父级执行上下文中的变量对象 */ },

    'variableObject': { /*  函数参数 / 参数, 内部变量以及函数声明 */ },

    'this': {}

}

```



### 活动对象 / 变量对象 [AO/VO]



这个`executionContextObj` 对象在函数调用的时候创建,但是实在函数真正执行之前。这就是我们所说的第 1 阶段 `创建阶段`。 在这个阶段，解释器通过扫描传入函数的参数，局部函数声明和局部变量声明来创建 `executionContextObj` 对象。这个扫描的结果就变成了 `executionContextObj` 中的 `variableObject` 对象。



**这是解释器执行代码时的伪概述**:



1. 寻找调用函数的代码

2. 在执行 `函数` 代码之前, 创建 `执行上下文`.

3. 进入创建阶段:

4. 初始化 [`作用域链`](http://davidshariff.com/blog/javascript-scope-chain-and-closures/).

5. 创建`变量对象`：

6. 创建 `参数对象`, 检查参数的上下文, 初始化其名称和值并创建一个引用拷贝。

7. 扫描上下文中的函数声明：

8. 对于每个被发现的函数, 在 `变量对象` 中创建一个和函数名同名的属性，这是函数在内存中的引用。

9. 如果函数名已经存在, 引用值将会被覆盖。

10. 扫描上下文中的变量声明：

11. 对于每个被发现的变量声明,在`变量对象`中创建一个同名属性并初始化值为 [undefined](http://davidshariff.com/blog/javascripts-undefined-explored/)。

12. 如果变量名在 `变量对象` 中已经存在, 什么都不做，继续扫描。

13. 确定上下文中的 [`"this"`](http://davidshariff.com/blog/javascript-this-keyword/)

14. 激活 / 代码执行阶段：

15. 执行 / 在上下文中解释函数代码，并在代码逐行执行时给变量赋值。



让我们来看一个例子:



```

function foo(i) {

    var a = 'hello';

    var b = function privateB() {



    };

    function c() {



    }

}



foo(22);

```



在调用`foo(22)` 的时候, `创建阶段` 看起来像是这样:



```

fooExecutionContext = {

    scopeChain: { ... },

    variableObject: {

        arguments: {

            0: 22,

            length: 1

        },

        i: 22,

        c: pointer to function c()

        a: undefined,

        b: undefined

    },

    this: { ... }

}

```



你可以发现, `创建阶段` 掌管着属性名的定义，而不是给它们赋值，不过参数除外。 一旦 `创建阶段`完成之后，执行流就会进入函数中。 在函数执行完之后，激活 / 代码 `执行阶段` 看起来像是这样：



```

fooExecutionContext = {

    scopeChain: { ... },

    variableObject: {

        arguments: {

            0: 22,

            length: 1

        },

        i: 22,

        c: pointer to function c()

        a: 'hello',

        b: pointer to function privateB()

    },

    this: { ... }

}

```



# bind
```
Function.prototype.bind = function() {

  var fn = this,

    args = Array.prototype.slice.call(arguments),

    object = args.shift();

  return function() {

    return fn.apply(object, args.concat(Array.prototype.slice.call(arguments)));

  };

};
```
