# Reflect

```js
const target = {
  get foo() {
    return this.bar;
  },
  bar: 3
};

const handler = {
  get(target, propertyKey, receiver) {
    if (propertyKey === 'bar') return 2;

    console.log(Reflect.get(target, propertyKey, receiver)); // this in foo getter references Proxy instance; logs 2
    console.log(target[propertyKey]); // this in foo getter references "target" - logs 3
  }
};

const obj = new Proxy(target, handler);
console.log(obj.bar); // 2
```

# Reflect Metadata

所谓注解的定义就是：为相应的类附加元数据支持。所谓元数据可以简单的解释，就是修饰数据的数据，比如一个人有 name，age 等数据属性，那么 name 和 age 这些字段就是为了修饰数据的数据，可以简单的称为元数据。通过注解添加元数据，然后在装饰器中获取这些元数据，完成对类、类的方法等等的修改，可以在装饰器中添加元数据的支持，比如可以可以在装饰器工厂函数以及装饰器函数中添加元数据支持等。

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

## 内置 Key

reflect-metadata 内置了三种 Key，在代码运行时可以利用内置的 Key 去获取到响应的元数据。

### Type Metadata `design:type`

```ts
function logType(target: any, key: string) {
  var t = Reflect.getMetadata('design:type', target, key);
  console.log(`${key} type: ${t.name}`);
}

class Demo {
  @logType // apply property decorator
  public attr1: string;
}

// attr1 type: String
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

### Parameter type metadata `design:paramtypes`

```ts
function logParamTypes(target: any, key: string) {
  var types = Reflect.getMetadata('design:paramtypes', target, key);
  var s = types.map(a => a.name).join();
  console.log(`${key} param types: ${s}`);
}

class Foo {}
interface IFoo {}

class Demo {
  @logParameters // apply parameter decorator
  doSomething(
    param1: string,
    param2: number,
    param3: Foo,
    param4: { test: string },
    param5: IFoo,
    param6: Function,
    param7: (a: number) => void
  ): number {
    return 1;
  }
}

// doSomething param types: String, Number, Foo, Object, Object, Function, Function
```

### Return type metadata `design:returntype`

```ts
Reflect.getMetadata('design:returntype', target, key);
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
