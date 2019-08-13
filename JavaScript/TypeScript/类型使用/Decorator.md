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

# 注解与元数据操作

所谓注解的定义就是：为相应的类附加元数据支持。所谓元数据可以简单的解释，就是修饰数据的数据，比如一个人有 name，age 等数据属性，那么 name 和 age 这些字段就是为了修饰数据的数据，可以简单的称为元数据。通过注解添加元数据，然后在装饰器中获取这些元数据，完成对类、类的方法等等的修改，可以在装饰器中添加元数据的支持，比如可以可以在装饰器工厂函数以及装饰器函数中添加元数据支持等。

## 注解使用

Reflect Metadata 是 ES7 的一个提案，它主要用来在声明的时候添加和读取元数据。Reflect Metadata 的 API 可以用于类或者类的属性上，如：

```ts
function metadata(
  metadataKey: any,
  metadataValue: any
): {
  (target: Function): void;
  (target: Object, propertyKey: string | symbol): void;
};
```

Reflect.metadata 当作 Decorator 使用，当修饰类时，在类上添加元数据，当修饰类属性时，在类原型的属性上添加元数据，如：

```ts
@Reflect.metadata('inClass', 'A')
class Test {
  @Reflect.metadata('inMethod', 'B')
  public hello(): string {
    return 'hello world';
  }
}

console.log(Reflect.getMetadata('inClass', Test)); // 'A'
console.log(Reflect.getMetadata('inMethod', new Test(), 'hello')); // 'B'
```

譬如在 vue-property-decorator 6.1 及其以下版本中，通过使用 Reflect.getMetadata API，Prop Decorator 能获取属性类型传至 Vue，简要代码如下：

```ts
function Prop(): PropertyDecorator {
  return (target, key: string) => {
    const type = Reflect.getMetadata('design:type', target, key);
    console.log(`${key} type: ${type.name}`);
    // other...
  };
}

class SomeClass {
  @Prop()
  public Aprop!: string;
}
```

## 自定义 metadataKey

除能获取类型信息外，常用于自定义 metadataKey，并在合适的时机获取它的值，示例如下：

```ts
function classDecorator(): ClassDecorator {
  return target => {
    // 在类上定义元数据，key 为 `classMetaData`，value 为 `a`
    Reflect.defineMetadata('classMetaData', 'a', target);
  };
}

function methodDecorator(): MethodDecorator {
  return (target, key, descriptor) => {
    // 在类的原型属性 'someMethod' 上定义元数据，key 为 `methodMetaData`，value 为 `b`
    Reflect.defineMetadata('methodMetaData', 'b', target, key);
  };
}

@classDecorator()
class SomeClass {
  @methodDecorator()
  someMethod() {}
}

Reflect.getMetadata('classMetaData', SomeClass); // 'a'
Reflect.getMetadata('methodMetaData', new SomeClass(), 'someMethod'); // 'b'
```

## 案例：Format

可以通过 reflect-metadata 包来实现对于元数据的操作。首先我们来看 reflect-metadata 的使用，首先定义使用元数据的函数：

```ts
const formatMetadataKey = Symbol('format');

function format(formatString: string) {
  return Reflect.metadata(formatMetadataKey, formatString);
}

function getFormat(target: any, propertyKey: string) {
  return Reflect.getMetadata(formatMetadataKey, target, propertyKey);
}
```

这里的 format 可以作为装饰器函数的工厂函数，因为 format 函数返回的是一个装饰器函数，上述的方法定义了元数据 `Sysmbol("format")`,用 Sysmbol 的原因是为了防止元数据中的字段重复，而 format 定义了取元数据中相应字段的功能。接着我们来在类中使用相应的元数据：

```ts
class Greeter {
  @format('Hello, %s')
  name: string;

  constructor(name: string) {
    this.name = message;
  }
  sayHello() {
    let formatString = getFormat(this, 'name');
    return formatString.replace('%s', this.name);
  }
}

const g = new Greeter('Jony');
console.log(g.sayHello());
```

在上述中，我们在 name 属性的装饰器工厂函数，执行`@Format("Hello, %s")`，返回一个装饰器函数，且该装饰器函数修饰了 Greeter 类的 name 属性，将“name”属性的值写入为"Hello, %s"。然后再 sayHello 方法中，通过 getFormat(this,"name") 取到 formatString 为“Hello,%s”.

## 案例：Angular 2 DI

```ts
type Constructor<T = any> = new (...args: any[]) => T;

const Injectable = (): ClassDecorator => target => {};

class OtherService {
  a = 1;
}

@Injectable()
class TestService {
  constructor(public readonly otherService: OtherService) {}

  testMethod() {
    console.log(this.otherService.a);
  }
}

const Factory = <T>(target: Constructor<T>): T => {
  // 获取所有注入的服务
  const providers = Reflect.getMetadata('design:paramtypes', target); // [OtherService]
  const args = providers.map((provider: Constructor) => new provider());
  return new target(...args);
};

Factory(TestService).testMethod(); // 1
```
