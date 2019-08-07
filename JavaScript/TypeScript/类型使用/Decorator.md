# Decorator | 装饰器

TypeScript 内建支持装饰器语法，需要我们在编译配置中开启装饰器参数：

```json
{
  "compilerOptions": {
    // ...
    "experimentalDecorators": true
  }
  // ...
}
```

装饰器提供了声明式的语法来修改类的结构或者属性声明，以简单的日志装饰器为例：

```ts
export function Log() {
  return function(
    target: Object,
    propertyKey: string,
    descriptor: TypedPropertyDescriptor<any>
  ) {
    // 保留原函数引用
    let originalFunction = descriptor.value || descriptor.get;

    // 定义包裹函数
    function wrapper() {
      let startedAt = +new Date();
      let returnValue = originalFunction.apply(this);
      let endedAt = +new Date();
      console.log(
        `${propertyKey} executed in ${endedAt - startedAt} milliseconds`
      );
      return returnValue;
    }

    // 将描述对象中的函数引用指向包裹函数
    if (descriptor.value) descriptor.value = wrapper;
    else if (descriptor.get) descriptor.get = wrapper;
  };
}
```

其使用方式如下：

```js
import { Log } from './log';

export class Entity {
  // ...

  @Log()
  get title(): string {
    Entity.wait(1572);
    return this._title;
  }

  // ...
}
```
