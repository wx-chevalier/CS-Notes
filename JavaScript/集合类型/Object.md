# Object

Object 在 JavaScript 中可谓一个神奇的东西，除了 Function，其他的元素在 typeof 关键字之后都会被解释成 object。在 JavaScript 中，Object 是面向对象概念中的对象与字典类型中的混合。

`typeof Object === "function"typeof {} === "object"`

```js
const obj = Object.create(null);

console.log(obj + '');
console.log(String(obj));
console.log(Number(obj));
console.log(obj.__proto__ === Object.prototype);

const obj = {
  a: 1,
  b: 2
};
Object.setPrototypeOf(obj, {
  c: 3
});

console.log(Object.keys(obj));
console.log(JSON.stringify(obj));

const keys1 = [];
for (let key in obj) keys1.push(key);
console.log(keys1);

const keys2 = [];
for (let key in Object.assign({}, obj)) keys2.push(key);
console.log(keys2);
```

# Object 键

Object 中的 Key 类别 JavaScript 中 Object 是一个混合了类似于 Dictionary 与 Class 的用法，基本上来说也是一种键值类型。其中键的类型主要包含四种：

```js
const a = 'a';
const object = {
  a, // a:"a" // `abc` is a valid identifier; no quotes are needed
  abc: 1, // `123` is a numeric literal; no quotes are needed
  123: 2, // `012` is an octal literal with value `10` and thus isn’t allowed in strict mode; but if you insist on using it, quotes aren’t needed
  012: 3, // `π` is a valid identifier; no quotes are needed
  π: Math.PI, // `const` is a valid identifier name (although it’s a reserved word); no quotes are needed
  const: 4, // `foo bar` is not a valid identifier name; quotes are required
  'foo bar': 5, // `foo-bar` is not a valid identifier name; quotes are required
  'foo-bar': 6, // the empty string is not a valid identifier name; quotes are required
  '': 7
};
```

- Identifier: 包含任何[有效地](https://mathiasbynens.be/notes/javascript-identifiers-es6)标识符，包括了 ES 的保留关键字。

- 字符串 :single (`'`) or double (`"`) quotes. `'foo'`, `"bar"`,`'qu\'ux'`, `""` (the empty string), and `'Ich \u2665 B\xFCcher'` are all valid string literals.

- 数字 :decimal literal (e.g. `0`, `123`, `123.`, `.123`, `1.23`, `1e23`, `1E-23`, `1e+23`, `12`, but not `01`, `+123` or `-123`) or a hex integer literal (`0[xX][0-9a-fA-F]+` in regex, e.g. `0xFFFF`, `0X123`,`0xaBcD`).

- Object Literals: 在 ES6 中，Object 的字面值调用也得到了增强，譬如用于构建时候的原型设置、`foo:foo`形式的简写、方法定义、父类调用以及计算属性值等等。

注意，与 object 不同的是，[JSON](http://json.org/) 只允许用双引号 (`"`)  包裹的字符串作为键名。而如果要根据键名进行索引的话，可以使用方括号，这种方式对于三种键值皆有效：

```js
object['abc']; // 1
```

有时候也可以使用点操作符，不过这种方式只可以被用于键为有效地 Identifier 情况：

```js
object.abc; // 1
```

如果需要获取所有的键名的话，可以使用 Object.keys 方法：> 注意，所有的 Object 的方法只能用 Object.methodName 方式调用。

## Object.create | 指定原型创建

ECMAScript 5 中引入了一个新方法：[Object.create](https://developer.mozilla.org/zh-cn/JavaScript/Reference/Global_Objects/Object/create)。可以调用这个方法来创建一个新对象。新对象的原型就是调用 `create` 方法时传入的第一个参数：

````js
const a = {a: 1};
// a ---> Object.prototype ---> null

const b = Object.create(a);
// b ---> a ---> Object.prototype ---> null
console.log(b.a); // 1 (继承而来)

const c = Object.create(b);
// c ---> b ---> a ---> Object.prototype ---> null

const d = Object.create(null);
// d ---> null
console.log(d.hasOwnProperty); // undefined, 因为d没有继承Object.prototype

//如下是实现了简单的基于函数的原型链的继承
const Scope = function(){};
Scope.prototype.$clone = function(){
    return Object.create(this);
}
```

其基本语法为：

```
Object.create(proto, { propertiesObject })
```

这里需要注意的是，propertiesObject 不是一个简单的键值类型，而是有固定格式的 object。
````
