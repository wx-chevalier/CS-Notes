# Decorator | 装饰器

Typescript 中的装饰器与类相关，分别可以修饰类的实例函数和静态函数、类本身、类的属性、类中函数的参数以及类的 set/get 存取器。TypeScript 内建支持装饰器语法，需要我们在编译配置中开启装饰器参数：

```json
{
  "compilerOptions": {
    // ...
    "experimentalDecorators": true
  }
  // ...
}
```

# 类方法的装饰器

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

对于类的函数的装饰器函数，依次接受的参数为：

- target：如果修饰的是类的实例函数，那么 target 就是类的原型。如果修饰的是类的静态函数，那么 target 就是类本身。
- key： 该函数的函数名。
- descriptor：该函数的描述属性，比如 configurable、value、enumerable 等。

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

  private static wait(ms) {
    let start = Date.now();
    let now = start;
    while (now - start < ms) {
      now = Date.now();
    }
  }
}
```

# 类的装饰器

当装饰函数直接修饰类的时候，装饰函数接受唯一的参数，这个参数就是该被修饰类本身。

```ts
let temple;
function foo(target) {
  console.log(target);
  temple = target;
}
@foo
class P {
  constructor() {}
}

const p = new P();
temple === P; //true
```

此外，在修饰类的时候，如果装饰函数有返回值，该返回值会重新定义这个类，也就是说当装饰函数有返回值时，其实是生成了一个新类，该新类通过返回值来定义。

```ts
function foo(target) {
  return class extends target {
    name = 'Jony';
    sayHello() {
      console.log('Hello ' + this.name);
    }
  };
}

@foo
class P {
  constructor() {}
}

const p = new P();
p.sayHello(); // 会输出Hello Jony
```

上面的例子可以看到，当装饰函数 foo 有返回值时，实际上 P 类已经被返回值所代表的新类所代替，因此 P 的实例 p 拥有 sayHello 方法。

# 属性与参数装饰器

装饰函数修饰类的属性时，在类实例化的时候调用属性的装饰函数，举例来说：

```ts
function foo(target, name) {
  console.log('target is', target);
  console.log('name is', name);
}
class P {
  @foo
  name = 'Jony';
}
const p = new P();
//会依次输出 target is f P()  name is Jony
```

这里对于类的属性的装饰器函数接受两个参数，对于静态属性而言，第一个参数是类本身，对于实例属性而言，第一个参数是类的原型，第二个参数是指属性的名字。类函数的参数装饰器可以修饰类的构建函数中的参数，以及类中其他普通函数中的参数。该装饰器在类的方法被调用的时候执行，下面来看实例：

```ts
function foo(target, key, index) {
  console.log('target is', target);
  console.log('key is', key);
  console.log('index is', index);
}
class P {
  test(@foo a) {}
}
const p = new P();
p.test('Hello Jony');
// 依次输出 f P() , test , 0
```

类函数参数的装饰器函数接受三个参数，依次为类本身，类中该被修饰的函数本身，以及被修饰的参数在参数列表中的索引值。上述的例子中，会依次输出 f P() 、test 和 0。再次明确一下修饰函数参数的装饰器函数中的参数含义：

- target： 类本身
- key：该参数所在的函数的函数名
- index： 该参数在函数参数列表中的索引值
