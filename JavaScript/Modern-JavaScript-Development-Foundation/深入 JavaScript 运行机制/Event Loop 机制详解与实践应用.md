[toc]

> [JavaScript/Node.js 中 Event Loop 机制详解与实践应用](https://zhuanlan.zhihu.com/p/28508795)归纳于笔者的[现代 JavaScript 开发：语法基础与实践技巧](https://parg.co/bjK)系列文章。本文依次介绍了函数调用栈、MacroTask 与 MicroTask 执行顺序、浅析 Vue.js 中 nextTick 实现等内容；本文中引用的参考资料统一声明在 [JavaScript 学习与实践资料索引](https://parg.co/b2O)。

# 事件循环机制详解与实践应用

JavaScript 是典型的单线程单并发语言，即表示在同一时间片内其只能执行单个任务或者部分代码片。换言之，我们可以认为 JavaScript 主线程拥有一个函数调用栈以及一个任务队列；主线程会依次执行代码，当遇到函数时，会先将函数入栈，函数运行完毕后再将该函数出栈，直到所有代码执行完毕。当函数调用栈为空时，运行时即会根据事件循环（Event Loop）机制来从任务队列中提取出待执行的回调并执行，执行的过程同样会进行函数帧的入栈出栈操作。Event Loop（事件循环）并不是 JavaScript 中独有的，其广泛应用于各个领域的异步编程实现中；所谓的 Event Loop 即是一系列回调函数的集合，在执行某个异步函数时，会将其回调压入队列中，JavaScript 引擎会在异步代码执行完毕后开始处理其关联的回调。

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/8/2/event-loop.png)

在 Web 开发中，我们常常会需要处理网络请求等相对较慢的操作，如果将这些操作全部以同步阻塞方式运行无疑会大大降低用户界面的体验。另一方面，我们点击某些按钮之后的响应事件可能会导致界面重渲染，如果因为响应事件的执行而阻塞了界面的渲染，同样会影响整体性能。实际开发中我们会采用异步回调来处理这些操作，这种调用者与响应之间的解耦保证了 JavaScript 能够在等待异步操作完成之前仍然能够执行其他的代码。Event Loop 正是负责执行队列中的回调并且将其压入到函数调用栈中，其基本的代码逻辑如下所示：

```
while (queue.waitForMessage()) {
  queue.processNextMessage();
}
```

完整的浏览器中 JavaScript 事件循环机制图解如下：

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/8/3/1--MMBHKy_ZxCrouecRqvsBg.png)

在 Web 浏览器中，任何时刻都有可能会有事件被触发，而仅有那些设置了回调的事件会将其相关的任务压入到任务队列中。回调函数被调用时即会在函数调用栈中创建初始帧，而直到整个函数调用栈清空之前任何产生的任务都会被压入到任务队列中延后执行；顺序的同步函数调用则会创建新的栈帧。



# 函数调用栈与任务队列


在[变量作用域与提升](https://parg.co/bT4)一节中我们介绍过所谓执行上下文（Execution Context）的概念，在 JavaScript 代码执行过程中，我们可能会拥有一个全局上下文，多个函数上下文或者块上下文；每个函数调用都会创造新的上下文与局部作用域。而这些执行上下文堆叠就形成了所谓的执行上下文栈（Execution Context STack），便如上文介绍的 JavaScript 是单线程事件循环机制，同时刻仅会执行单个事件，而其他事件都在所谓的执行栈中排队等待：

![](http://p0.qhimg.com/t01e858c269438d695a.jpg)

而从 JavaScript 内存模型的角度，我们可以将内存划分为调用栈（Call Stack）、堆（Heap）以及队列（Queue）等几个部分：

![](https://github.com/wxyyxc1992/OSS/blob/master/2017/8/1/1-ZSFHnq9iMHIApVLcgwczPQ.png?raw=true)

其中的调用栈会记录所有的函数调用信息，当我们调用某个函数时，会将其参数与局部变量等压入栈中；在执行完毕后，会弹出栈首的元素。而堆则存放了大量的非结构化数据，譬如程序分配的变量与对象。队列则包含了一系列待处理的信息与相关联的回调函数，每个 JavaScript 运行时都必须包含一个任务队列。当调用栈为空时，运行时会从队列中取出某个消息并且执行其关联的函数（也就是创建栈帧的过程）；运行时会递归调用函数并创建调用栈，直到函数调用栈全部清空再从任务队列中取出消息。换言之，譬如按钮点击或者 HTTP 请求响应都会作为消息存放在任务队列中；需要注意的是，仅当这些事件的回调函数存在时才会被放入任务队列，否则会被直接忽略。

这里我们着重函数调用栈的执行机制，以[]()整理的代码调用图为例：


![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/8/2/11111111.jpg)

```

(function test() {
    setTimeout(function() {console.log(4)}, 0);
    new Promise(function executor(resolve) {
        console.log(1);
        for( var i=0 ; i<10000 ; i++ ) {
            i == 9999 && resolve();
        }
        console.log(2);
    }).then(function() {
        console.log(5);
    });
    console.log(3);
})()


```

Promise.then是异步执行的，而创建Promise实例（executor）是同步执行的。
要想找到原因，最自然的做法就是去看规范。我们首先去看看Promise的规范。

摘录promise.then相关的部分如下：

promise.then(onFulfilled, onRejected)

2.2.4 onFulfilled or onRejected must not be called until the execution context stack contains only platform code. [3.1].

Here “platform code” means engine, environment, and promise implementation code. In practice, this requirement ensures that onFulfilled and onRejected execute asynchronously, after the event loop turn in which then is called, and with a fresh stack. This can be implemented with either a “macro-task” mechanism such as setTimeout or setImmediate, or with a “micro-task” mechanism such as MutationObserver or process.nextTick. Since the promise implementation is considered platform code, it may itself contain a task-scheduling queue or “trampoline” in which the handlers are called.
规范要求，onFulfilled必须在 执行上下文栈（execution context stack） 只包含 平台代码（platform code） 后才能执行。平台代码指 引擎，环境，Promise实现代码。实践上来说，这个要求保证了onFulfilled的异步执行（以全新的栈），在then被调用的这个事件循环之后。


# MacroTask（Task） 与 MicroTask（Job）


```
// 测试代码
console.log('main1');

// 该函数仅在 Node.js 环境下可以使用
process.nextTick(function() {
    console.log('process.nextTick1');
});

setTimeout(function() {
    console.log('setTimeout');
    process.nextTick(function() {
        console.log('process.nextTick2');
    });
}, 0);

new Promise(function(resolve, reject) {
    console.log('promise');
    resolve();
}).then(function() {
    console.log('promise then');
});

console.log('main2');

// 执行结果
main1
promise
main2
process.nextTick1
promise then
setTimeout
process.nextTick2
```


那么遇到 WebAPI（例如：setTimeout, AJAX）这些函数时，这些函数会立即返回一个值，从而让主线程不会在此处阻塞。而真正的异步操作会由浏览器执行，浏览器会在这些任务完成后，将事先定义的回调函数推入主线程的 任务队列 中。

而主线程则会在 清空当前执行栈后，按照先入先出的顺序读取任务队列里面的任务。
以上就是浏览器的异步任务的执行机制，核心点为：

- 异步任务是由浏览器执行的，不管是`AJAX`请求，还是`setTimeout`等 API，浏览器内核会在其它线程中执行这些操作，当操作完成后，将操作结果以及事先定义的回调函数放入 JavaScript 主线程的任务队列中
- JavaScript 主线程会在执行栈清空后，读取任务队列，读取到任务队列中的函数后，将该函数入栈，一直运行直到执行栈清空，再次去读取任务队列，不断循环
- 当主线程阻塞时，任务队列仍然是能够被推入任务的。这也就是为什么当页面的 JavaScript 进程阻塞时，我们触发的点击等事件，会在进程恢复后依次执行。

macrotasks: setTimeout, setInterval, setImmediate, requestAnimationFrame, I/O, UI rendering
microtasks: process.nextTick, Promises, Object.observe, MutationObserver





![](http://mmbiz.qpic.cn/mmbiz_png/meG6Vo0Mevia3qqAdZXbGMvOQWvD3AxX5RExFksDUS067icPUUmVweUqmuaR2vHlkOqia7x0XydvVfstK6Lf5l7GQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

首先，micro-task在ES2015规范中称为Job。 其次，macro-task代指task。

whatwg规范：[https://html.spec.whatwg.org/multipage/webappapis.html#task-queue](https://html.spec.whatwg.org/multipage/webappapis.html#task-queue)


- 一个事件循环(event loop)会有一个或多个任务队列(task queue) task queue 就是 macrotask queue
- 每一个 event loop 都有一个 microtask queue
- task queue == macrotask queue != microtask queue
- 一个任务 task 可以放入 macrotask queue 也可以放入 microtask queue 中
- 当一个 task 被放入队列 queue(macro或micro) 那这个 task 就可以被立即执行了

再来回顾下事件循环如何执行一个任务的流程

当执行栈(call stack)为空的时候，开始依次执行：

1. 把最早的任务(task A)放入任务队列
2. 如果 task A 为null (那任务队列就是空)，直接跳到第6步
3. 将 currently running task 设置为 task A
4. 执行 task A (也就是执行回调函数)
5. 将 currently running task 设置为 null 并移出 task A
6. 执行 microtask 队列
   - a: 在 microtask 中选出最早的任务 task X
   - b: 如果 task X 为null (那 microtask 队列就是空)，直接跳到 g
   - c: 将 currently running task 设置为 task X
   - d: 执行 task X
   - e: 将 currently running task 设置为 null 并移出 task X
   - f: 在 microtask 中选出最早的任务 , 跳到 b
   - g: 结束 microtask 队列
7. 跳到第一步


每个线程有自己的事件循环，所以每个web worker有自己的，所以它才可以独立执行。然而，所有同属一个origin的windows共享一个事件循环，所以它们可以同步交流。
事件循环不间断在跑，执行任何进入队列的task。
一个事件循环可以有多个task source，每个task source保证自己的任务列表的执行顺序，但由浏览器在（事件循环的）每轮中挑选某个task source的task。
tasks are scheduled，所以浏览器可以从内部到JS/DOM，保证动作按序发生。在tasks之间，浏览器可能会render updates。从鼠标点击到事件回调需要schedule task，解析html，setTimeout这些都需要。
microtasks are scheduled，经常是为需要直接在当前脚本执行完后立即发生的事，比如async某些动作但不必承担新开task的弊端。microtask queue在回调之后执行，只要没有其它JS在执行中，并且在每个task的结尾。microtask中添加的microtask也被添加到microtask queue的末尾并处理。microtask包括mutation observer callbacks和promise callbacks。

上面就算是一个简单的 event-loop 执行模型

再简单点可以总结为：


1. 在 macrotask 队列中执行最早的那个 task ，然后移出
2. 执行 microtask 队列中所有可用的任务，然后移出
3. 下一个循环，执行下一个 macrotask 中的任务 (再跳到第2步)


# 浅析 Vue.js 中 nextTick 的实现

在 Vue.js 中，其会异步执行 DOM 更新；当观察到数据变化时，Vue 将开启一个队列，并缓冲在同一事件循环中发生的所有数据改变。如果同一个 watcher 被多次触发，只会一次推入到队列中。这种在缓冲时去除重复数据对于避免不必要的计算和 DOM 操作上非常重要。然后，在下一个的事件循环“tick”中，Vue 刷新队列并执行实际（已去重的）工作。Vue 在内部尝试对异步队列使用原生的 `Promise.then` 和 `MutationObserver`，如果执行环境不支持，会采用 `setTimeout(fn, 0)` 代替。

为啥要用 microtask？根据 HTML Standard，在每个 task 运行完以后，UI 都会重渲染，那么在 microtask 中就完成数据更新，当前 task 结束就可以得到最新的 UI 了。反之如果新建一个 task 来做数据更新，那么渲染就会进行两次。根据我们上面提到的事件循环进程模型，每一次执行 task 后，然后执行 microtasks queue，最后进行页面更新。如果我们使用 task 来设置 DOM 更新，那么效率会更低。而 microtask 则会在页面更新之前完成数据更新，会得到更高的效率。

而当我们希望在数据更新之后执行某些 DOM 操作，就需要使用 `nextTick` 函数来添加回调：
```
// HTML
<div id="example">{{message}}</div>

// JS
var vm = new Vue({
  el: '#example',
  data: {
    message: '123'
  }
})
vm.message = 'new message' // 更改数据
vm.$el.textContent === 'new message' // false
Vue.nextTick(function () {
  vm.$el.textContent === 'new message' // true
})
```
在组件内使用 vm.$nextTick() 实例方法特别方便，因为它不需要全局 Vue ，并且回调函数中的 this 将自动绑定到当前的 Vue 实例上：
```
Vue.component('example', {
  template: '<span>{{ message }}</span>',
  data: function () {
    return {
      message: '没有更新'
    }
  },
  methods: {
    updateMessage: function () {
      this.message = '更新完成'
      console.log(this.$el.textContent) // => '没有更新'
      this.$nextTick(function () {
        console.log(this.$el.textContent) // => '更新完成'
      })
    }
  }
})
```
src/core/util/env
```

/**
 * 使用 MicroTask 来异步执行批次任务
 */
export const nextTick = (function() {
  // 需要执行的回调列表
  const callbacks = [];

  // 是否处于挂起状态
  let pending = false;

  // 时间函数句柄
  let timerFunc;

  // 执行并且清空所有的回调列表
  function nextTickHandler() {
    pending = false;
    const copies = callbacks.slice(0);
    callbacks.length = 0;
    for (let i = 0; i < copies.length; i++) {
      copies[i]();
    }
  }

  // nextTick 的回调会被加入到 MicroTask 队列中，这里我们主要通过原生的 Promise 与 MutationObserver 实现
  /* istanbul ignore if */
  if (typeof Promise !== 'undefined' && isNative(Promise)) {
    let p = Promise.resolve();
    let logError = err => {
      console.error(err);
    };
    timerFunc = () => {
      p.then(nextTickHandler).catch(logError);

      // 在部分 iOS 系统下的 UIWebViews 中，Promise.then 可能并不会被清空，因此我们需要添加额外操作以触发
      if (isIOS) setTimeout(noop);
    };
  } else if (
    typeof MutationObserver !== 'undefined' &&
    (isNative(MutationObserver) ||
      // PhantomJS and iOS 7.x
      MutationObserver.toString() === '[object MutationObserverConstructor]')
  ) {
    // 当 Promise 不可用时候使用 MutationObserver
    // e.g. PhantomJS IE11, iOS7, Android 4.4
    let counter = 1;
    let observer = new MutationObserver(nextTickHandler);
    let textNode = document.createTextNode(String(counter));
    observer.observe(textNode, {
      characterData: true
    });
    timerFunc = () => {
      counter = (counter + 1) % 2;
      textNode.data = String(counter);
    };
  } else {
    // 如果都不存在，则回退使用 setTimeout
    /* istanbul ignore next */
    timerFunc = () => {
      setTimeout(nextTickHandler, 0);
    };
  }

  return function queueNextTick(cb?: Function, ctx?: Object) {
    let _resolve;
    callbacks.push(() => {
      if (cb) {
        try {
          cb.call(ctx);
        } catch (e) {
          handleError(e, ctx, 'nextTick');
        }
      } else if (_resolve) {
        _resolve(ctx);
      }
    });
    if (!pending) {
      pending = true;
      timerFunc();
    }

    // 如果没有传入回调，则表示以异步方式调用
    if (!cb && typeof Promise !== 'undefined') {
      return new Promise((resolve, reject) => {
        _resolve = resolve;
      });
    }
  };
})();
```


# 延伸阅读

- [深入浅出 Node.js 全栈架构 - Node.js 事件循环机制详解与实践](https://parg.co/b2s)
