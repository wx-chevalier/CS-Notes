
# JavaScript Event Loop



JavaScript 主线程拥有一个 执行栈 以及一个 任务队列，主线程会依次执行代码，当遇到函数时，会先将函数 入栈，函数运行完毕后再将该函数 出栈，直到所有代码执行完毕。


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



# 函数调用栈

# 事件循环进程模型与应用

## MacroTask 与 MicroTask 的执行顺序

![](http://mmbiz.qpic.cn/mmbiz_png/meG6Vo0Mevia3qqAdZXbGMvOQWvD3AxX5RExFksDUS067icPUUmVweUqmuaR2vHlkOqia7x0XydvVfstK6Lf5l7GQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

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

上面就算是一个简单的 event-loop 执行模型

再简单点可以总结为：


1. 在 macrotask 队列中执行最早的那个 task ，然后移出
2. 执行 microtask 队列中所有可用的任务，然后移出
3. 下一个循环，执行下一个 macrotask 中的任务 (再跳到第2步)


## 浅析 Vue.js 中 nextTick 的实现

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


# Node.js Event Loop

![](https://blog-assets.risingstack.com/2016/10/the-Node-js-event-loop.png)

```
   ┌───────────────────────┐
┌─>│        timers         │
│  └──────────┬────────────┘
│  ┌──────────┴────────────┐
│  │     I/O callbacks     │
│  └──────────┬────────────┘
│  ┌──────────┴────────────┐
│  │     idle, prepare     │
│  └──────────┬────────────┘      ┌───────────────┐
│  ┌──────────┴────────────┐      │   incoming:   │
│  │         poll          │<─────┤  connections, │
│  └──────────┬────────────┘      │   data, etc.  │
│  ┌──────────┴────────────┐      └───────────────┘
│  │        check          │
│  └──────────┬────────────┘
│  ┌──────────┴────────────┐
└──┤    close callbacks    │
   └───────────────────────┘
```


## nextTick 与 setImmediate

我们通过比较以下两个用例来了解 setImmediate 与 nextTick 的区别：
- setImmediate

```
setImmediate(function A() {
  setImmediate(function B() {
    log(1);
    setImmediate(function D() { log(2); });
    setImmediate(function E() { log(3); });
  });
  setImmediate(function C() {
    log(4);
    setImmediate(function F() { log(5); });
    setImmediate(function G() { log(6); });
  });
});

setTimeout(function timeout() {
  console.log('TIMEOUT FIRED');
}, 0)

// 'TIMEOUT FIRED' 1 4 2 3 5 6
// OR
// 1 'TIMEOUT FIRED' 4 2 3 5 6
```
- nextTick
```
process.nextTick(function A() {
  process.nextTick(function B() {
    log(1);
    process.nextTick(function D() { log(2); });
    process.nextTick(function E() { log(3); });
  });
  process.nextTick(function C() {
    log(4);
    process.nextTick(function F() { log(5); });
    process.nextTick(function G() { log(6); });
  });
});

setTimeout(function timeout() {
  console.log('TIMEOUT FIRED');
}, 0)

// 1 4 2 3 5 6 'TIMEOUT FIRED'
```
如上文所述，通过 setImmediate 设置的回调会以 MacroTask 加入到 Event Loop 中，每个循环中会提取出某个回调执行；setImmediate 能够避免 Event Loop 被阻塞，从而允许其他完成的 I/O 操作或者定时器回调顺利执行。而通过 nextTick 加入的回调会在当前代码执行完毕（即函数调用栈执行完毕）后立刻执行，即会在返回 Event Loop 之前立刻执行。譬如上面的例子中，setTimeout 的回调会在 Event Loop 中调用，因此 TIMEOUT FIRED 的输出会在所有的 nextTick 回调执行完毕后打印出来。

## 浏览器中实现 setImmediate 
当我们使用 Webpack 打包应用时，其默认会添加 setImmediate 的垫片
