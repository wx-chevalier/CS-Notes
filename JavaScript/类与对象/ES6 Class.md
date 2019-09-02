# ES6

## Babel

```
class Parent {

  constructor() {
    this.a = 1;
  }

}

class Child extends Parent {

}
```

```js
'use strict';

const _getPrototypeOf = require('babel-runtime/core-js/object/get-prototype-of');

const _getPrototypeOf2 = _interopRequireDefault(_getPrototypeOf);

const _possibleConstructorReturn2 = require('babel-runtime/helpers/possibleConstructorReturn');

const _possibleConstructorReturn3 = _interopRequireDefault(
  _possibleConstructorReturn2
);

const _inherits2 = require('babel-runtime/helpers/inherits');

const _inherits3 = _interopRequireDefault(_inherits2);

const _classCallCheck2 = require('babel-runtime/helpers/classCallCheck');

const _classCallCheck3 = _interopRequireDefault(_classCallCheck2);

function _interopRequireDefault(obj) {
  return obj && obj.__esModule ? obj : { default: obj };
}

const Parent = function Parent() {
  (0, _classCallCheck3.default)(this, Parent);

  this.a = 1;
};

const Child = (function(_Parent) {
  (0, _inherits3.default)(Child, _Parent);

  function Child() {
    (0, _classCallCheck3.default)(this, Child);
    return (0, _possibleConstructorReturn3.default)(
      this,
      (Child.__proto__ || (0, _getPrototypeOf2.default)(Child)).apply(
        this,
        arguments
      )
    );
  }

  return Child;
})(Parent);
```

# JavaScript 中类的声明与实例化

JavaScript is a powerful object-oriented programming (OOP) language, however, unlike many traditional programming languages, it uses a prototype-based OOP model which makes its syntax foreign to most developers. In addition, JavaScript also treats functions as first-class objects which may cause further confusion amongst developers who are not familiar with these concepts.

[14.5.14 Runtime Semantics: ClassDefinitionEvaluation](http://www.ecma-international.org/ecma-262/6.0/#sec-runtime-semantics-classdefinitionevaluation)

```js
class C {
  constructor() {
    // Use inner name C to refer to class
    console.log(`constructor: ${C.prop}`);
  }
  logProp() {
    // Use inner name C to refer to class
    console.log(`logProp: ${C.prop}`);
  }
}
C.prop = 'Hi!';

const D = C;
C = null;

// C is not a class, anymore:
new C().logProp(); // TypeError: C is not a function
// But inside the class, the identifier C
// still works
new D().logProp(); // constructor: Hi! // logProp: Hi!
```

```js
class A {
  say() {
    console.log(A);
  }
}

const B = A;
A = null;

const a = new B();
a.say();

let C = function() {};

C.prototype.say = function() {
  console.log(C);
};

const D = C;
C = null;

const c = new D();
c.say();

// [Function: A]
// null
```

## 静态方法

```
class Obj {

  static myMethod(msg) {

  console.log('static', msg);

  }

  myMethod(msg) {

  console.log('instance', msg);

  }

}

Obj.myMethod(1); // static 1

const instance = new Obj();

instance.myMethod(2); // instance 2
```

Access Self Method

```js
class A {
  constructor(input) {
    this.tight = A.getResult(input); //this.tight = this.constructor.getResult( input )
  }

  static getResult(input) {
    return input * 2;
  }
}

let instanceA = new A(4);

console.log('A.tight', instanceA.tight); //A.tight 8
```

Remark

```
this.tight = this.constructor.getResult( input )
```

is as same as

```
this.tight = A.getResult( input )
```

# Class

## Reference

# 实例化

## 构造函数

## 单例模式

单例模式的根本目的在于不需要重复初始化一个类，并且在某些情况下能够使用某个对象来进行全局的状态管理，在 ES6 中要实现一个单例类也比较方便：

```
/*
  * Setting up block level variable to store class state
  * , set's to null by default.
*/
let instance = null;

class Cache{
    constructor() {
        if(!instance){
              instance = this;
        }

        // to test whether we have singleton or not
        this.time = new Date()

        return instance;
      }
}
```

要进行测试的话可以用如下方式：

```
 let cache = new Cache()
 console.log(cache.time);
 setTimeout(function(){
   let cache = new Cache();
   console.log(cache.time);
 },4000);
```

# Definition

从 ECMAScript 6 开始，JavaScript 中有了类(class )这个概念。但需要注意的是，这并不是说：JavaScript 从此变得像其它基于类的面向对象语言一样，有了一种全新的继承模型。JavaScript 中的类只是 JavaScript 现有的、基于原型的继承模型的一种语法包装(语法糖)，它能让我们用更简洁明了的语法实现继承。ES6 的类是一个基本的基于 Prototype 的 OO 模式，它依旧支持原型继承，并且支持父类调用、实例化、静态方法以及构造器，首先看下基本的类表达式定义：

```
// 匿名类表达式
const Polygon = class {
  constructor(height, width) {
    this.height = height;
    this.width = width;
  }
};

// 命名类表达式
const Polygon = class Polygon {
  constructor(height, width) {
    this.height = height;
    this.width = width;
  }
};
```

下面看一个更全面一点的类的定义：

```js
class SkinnedMesh extends THREE.Mesh {
  constructor(geometry, materials) {
    super(geometry, materials);

    this.idMatrix = SkinnedMesh.defaultMatrix();
    this.bones = [];
    this.boneMatrices = [];
    //...
  }
  update(camera) {
    //...
    super.update();
  }
  get boneCount() {
    return this.bones.length;
  }
  set matrixType(matrixType) {
    this.idMatrix = SkinnedMesh[matrixType]();
  }
  static defaultMatrix() {
    return new THREE.Matrix4();
  }
}
```

### 变量提升

类声明和函数声明不同的一点是，函数声明存在[变量提升](https://developer.mozilla.org/en-US/docs/Glossary/Hoisting)现象，而类声明不会。也就是说，你必须先声明类，然后才能使用它，否则代码会抛出 [`ReferenceError`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/ReferenceError) 异常，像下面这样：

```
const p = new Polygon(); // ReferenceError
class Polygon {}
```

## 可见域

### 私有数据

> [如何在 ES6 中管理类的私有数据](http://efe.baidu.com/blog/managing-the-private-data-of-es6-classes/)

## 属性

## 方法

### 构造器

`constructor` 方法是一个特殊的类方法，它既不是静态方法也不是实例方法，它仅在实例化一个类的时候被调用。一个类只能拥有一个名为 `constructor` 的方法，否则会抛出 [`SyntaxError`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/SyntaxError) 异常。在子类的构造器中可以使用 `super` 关键字调用父类的构造器。

### 原型方法

```
class Polygon {
  constructor(height, width) {
    this.height = height;
    this.width = width;
  }

  get area() {
    return this.calcArea();
  }

  calcArea() {
    return this.height * this.width;
  }
}

const square = new Polygon(10, 10);

console.log(square.area);
```

#### 将 JSON 对象映射到 Object

```
constructor(data) {Object.assign(this, data);}const data = JSON.parse(req.responseText);new User(data);
```

### 静态方法

`static` 关键字用来定义类的静态方法。 静态方法是指那些不需要对类进行实例化，使用类名就可以直接访问的方法。静态方法经常用来作为工具函数。

```
class Point {
    constructor(x, y) {
        this.x = x;
        this.y = y;
    }

    static distance(a, b) {
        const dx = a.x - b.x;
        const dy = a.y - b.y;

        return Math.sqrt(dx*dx + dy*dy);
    }
}

const p1 = new Point(5, 5);
const p2 = new Point(10, 10);

console.log(Point.distance(p1, p2));
```

# 继承

```js
/**
 * Classes and Inheritance
 * Code Example from http://www.es6fiddle.net/
 */
class Polygon {
  constructor(height, width) {
    //class constructor
    this.name = 'Polygon';
    this.height = height;
    this.width = width;
  }

  sayName() {
    //class method
    console.log('Hi, I am a', this.name + '.');
  }
}

class Square extends Polygon {
  constructor(length = 10) {
    // ES6 features Default Parameters
    super(length, length); //call the parent method with super
    this.name = 'Square';
  }

  get area() {
    //calculated attribute getter
    return this.height * this.width;
  }
}

let s = new Square(5);

s.sayName(); // => Hi, I am a Square.
console.log(s.area); // => 25

console.log(new Square().area); // => 100
```

## 使用 super 关键字引用父类

```js
class Cat {
  constructor(name) {
    this.name = name;
  }

  speak() {
    console.log(this.name + ' makes a noise.');
  }
}

class Lion extends Cat {
  speak() {
    super.speak();
    console.log(this.name + ' roars.');
  }
}
```

## 通过子类工厂实现简单的合成器

当 ES6 类继承另一个类，被继承的类可以是通过任意表达式创建的动态类：

```
// Function id() simply returns its parameter
const id = x => x;

class Foo extends id(Object) {}
```

这个特性可以允许你实现一种合成器模式，用一个函数来将一个类 `C` 映射到一个新的继承了`C`的类。例如，下面的两个函数 `Storage` 和 `Validation` 是合成器：

```
const Storage = Sup => class extends Sup {
    save(database) { ··· }
};
const Validation = Sup => class extends Sup {
    validate(schema) { ··· }
};
```

你可以使用它们去组合生成一个如下的 `Employee` 类：

```
class Person { ··· }
class Employee extends Storage(Validation(Person)) { ··· }
```
