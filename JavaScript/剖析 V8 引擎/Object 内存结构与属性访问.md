[![返回目录](https://parg.co/USw)](https://parg.co/bxN)

# V8 Object 内存结构与属性访问

上世纪九十年代，随着网景浏览器的发行，JavaScript 首次进入人们的视线。之后随着 AJAX 的大规模应用与富客户端、单页应用时代的到来，JavaScript 在 Web 开发中占据了越来越重要的地位。在早期的 JavaScript 引擎中，性能越发成为了开发网页应用的瓶颈。而 V8 引擎设计的目标就是为了保证大型 JavaScript 应用的执行效率，在[很多测试](https://developers.google.com/v8/benchmarks)中可以明显发现 V8 的性能优于 JScript (Internet Explorer), SpiderMonkey (Firefox), 以及 JavaScriptCore(Safari). 根据 V8 的官方文档介绍，其主要是从属性访问、动态机器码生成以及高效的垃圾回收这三个方面着手性能优化。Obejct 当属 JavaScript 最重要的数据类型之一，本文我们对其内部结构进行详细阐述。其继承关系图如下所示：

![](https://v8docs.nodesource.com/node-7.2/dc/dc3/classv8_1_1_object__inherit__graph.png)

在 V8 中新分配的 JavaScript 对象结构如下所示：

```
[ class / map ] -> ... ; 指向内部类
[ properties  ] -> [empty array]
[ elements    ] -> [empty array] ; 数值类型名称的属性
[ reserved #1 ] -\
[ reserved #2 ]  |
[ reserved #3 ]  }- in object properties,即预分配的内存空间
...............  |
[ reserved #N ] -/
```

在创建新的对象时，V8 会创建某个预分配的内存区域来存放所谓的 in-object 属性，预分配区域的大小由构造函数中的参数数目决定(`this.field = expr`)。当你打算向对象中添加某个新属性时，V8 首先会尝试放入所谓的 in-order 槽位中，当 in-object 槽位过载之后，V8 会尝试将新的属性添加到 out-of-object 属性列表。而属性名与属性下标的映射关系即存放在所谓隐藏类中，譬如`{ a: 1, b: 2, c: 3, d: 4}`对象的存储方式可能如下：

```
[ class       ] -> [a: in obj #1, b: in obj #2, c: out obj #1, d: out obj #2]
[ properties  ] -> [  3  ][  4  ] ; this is linear array
[ elements    ]
[ 1           ]
[ 2           ]
```

随着属性数目的增加，V8 会转回到传统的字典模式 / 哈希表模式：

```
[ class       ] -> [ OBJECT IS IN DICTIONARY MODE ]
[ properties  ] -> [a: 1, b: 2, c: 3, d: 4, e: 5] ; this is classical hash table
[ elements    ]
```

# Property Name: 属性名

作为动态语言，JavaScript 允许我们以非常灵活的方式来定义对象，譬如：

```
obj.prop
obj["prop"]
```

参照 JavaScript 定义规范中的描述，属性名恒为字符串，即使你使用了某个非字符串的名字，也会隐式地转化为字符串类型。譬如你创建的是个数组，以数值下标进行访问，然而 V8 还是将其转化为了字符串再进行索引，因此以下的方式就会获得相同的效果：

```
obj[1];    //
obj["1"];  // names for the same property
obj[1.0];  //


var o = { toString: function () { return "-1.5"; } };


obj[-1.5];  // also equivalent
obj[o];     // since o is converted to string
```

而 JavaScript 中的 Array 只是包含了额外的`length`属性的对象而已，`length`会返回当前最大下标加一的结果(此时字符串下标会被转化为数值类型计算):

```
var a = new Array();
a[100] = "foo";
a.length;  //101
a[undefined] = 'a';
a.length; //0
```

`Function`本质上也是对象，只不过`length`属性会返回参数的长度而已：

```
> a = ()=>{}
[Function: a]
> a.length
0
> a = (b)=>{}
[Function: a]
> a.length
1
```

# In-Object Properties & Fast Property Access: 对象内属性与访问优化

作为动态类型语言，JavaScript 中的对象属性可以在运行时动态地增删，意味着整个对象的结构会频繁地改变。大部分 JavaScript 引擎倾向于使用字典类型的数据结构来存放对象属性( Object Properties )，每次进行属性访问的时候引擎都需要在内层中先动态定位属性对应的下标地址然后读取值。这种方式实现上比较容易，但是会导致较差的性能表现。其他的类似于 Java 与 Smalltalk 这样的静态语言中，成员变量在编译阶段即确定了其在内存中的固定偏移地址，进行属性访问的时候只需要单指令从内存中加载即可。而 V8 则利用动态创建隐藏内部类的方式动态地将属性的内存地址记录在对象内，从而提升整体的属性访问速度。总结而言，每当为某个对象添加新的属性时，V8 会自动修正其隐藏内部类。我们先通过某个[实验]()来感受下隐藏类的存在：

```
var PROPERTIES = 10000000;
var o = {};


var start = +new Date;


for (var i = 0; i < PROPERTIES; i++) {
  o[i] = i;
}


console.log(+new Date - start);


function O(size) {
  for (var i = 0; i < size; i++) {
    this[i] = null;
  }
}


var o = new O(PROPERTIES);


var start = +new Date;


for (var i = 0; i < PROPERTIES; i++) {
  o[i] = i;
}


console.log(+new Date - start);


class OClass {


    constructor(size){
        for (var i = 0; i < size; i++) {
            this[i] = null;
        }
    }


}


var o = new OClass(PROPERTIES);


var start = +new Date;


for (var i = 0; i < PROPERTIES; i++) {
  o[i] = i;
}


console.log(+new Date - start);
```

该程序的执行结果如下：

```
// Babel 下结果
385
37
49
// Chrome 下结果
416
32
31
```

第一种实现中，每次为对象`o`设置新的属性时，V8 都会创建新的隐藏内部类(内部称为 Map)来存储新的内存地址以优化属性查找速度。而第二种实现时，我们在创建新的对象时即初始化了内部类，这样在赋值属性时 V8 以及能够高性能地定位这些属性。第三种实现则是用的 ES6 Class，在纯正的 V8 下性能最好。接下来我们具体阐述下隐藏类的工作原理，假设我们定义了描述点的函数：

```
function Point(x, y) {
  this.x = x;
  this.y = y;
}
```

当我们执行`new Point(x,y)`语句时，V8 会创建某个新的`Point`对象。创建的过程中，V8 首先会创建某个所谓`C0`的隐藏内部类，因为尚未为对象添加任何属性，此时隐藏类还是空的: ![](https://github.com/v8/v8/wiki/images/map_trans_a.png) 接下来调用首个赋值语句`this.x = x;`为当前`Point`对象创建了新的属性`x`，此时 V8 会基于`C0`创建另一个隐藏类`C1`来替换`C0`，然后在`C1`中存放对象属性`x`的内存位置信息: ![](https://github.com/v8/v8/wiki/images/map_trans_b.png)

这里从`C0`到`C1`的变化称为转换(Transitions )，当我们为同一个类型的对象添加新的属性时，并不是每次都会创建新的隐藏类，而是多个对象会共用某个符合转换条件的隐藏类。接下来继续执行`this.y = y` 这一条语句，会为`Point`对象创建新的属性。此时 V8 会进行以下步骤：

* 基于`C1`创建另一个隐藏类`C1`，并且将关于属性`y`的位置信息写入到`C2`中。
* 更新`C1`为其添加转换信息，即当为`Point`对象添加属性 `y` 时，应该转换到隐藏类 `C2`。

![](https://github.com/v8/v8/wiki/images/map_trans_c.png) 整个过程的伪代码描述如下：

```
<Point object is allocated>


  Class C0
    "x": TRANSITION to C1 at offset 0


this.x = x;


  Class C1
    "x": FIELD at offset 0
    "y": TRANSITION to C2 at offset 1


this.y = y;


  Map C2
    "x": FIELD at offset 0
    "y": FIELD at offset 1
```

## Reused Hidden Class: 重复使用的隐藏类

我们在上文中提及，如果每次添加新的属性时都创建新的隐藏类无疑是极大的性能浪费，实际上当我们再次创建新的`Point`对象时，V8 并不会创建新的隐藏类而是使用已有的，过程描述如下：

* 初始化新的`Point`对象，并将隐藏类指向`C0`。
* 添加`x`属性时，遵循隐藏类的转换原则指向到`C1` , 并且根据`C1`指定的偏移地址写入`x`。
* 添加`y`属性时，遵循隐藏类的转换原则指向到`C2`，并且根据`C2`指定的偏移地址写入`y`。

另外我们在上文以链表的方式描述转换，实际上真实场景中 V8 会以树的结构来描述转换及其之间的关系，这样就能够用于类似于下面的属性一致而赋值顺序颠倒的场景：

```
function Point(x, y, reverse) {
  if (reverse) {
    this.x = x;
    this.y = y;
  } else {
    this.y = x;
    this.x = y;
  }
}
```

## Methods & Prototypes: 方法与原型

JavaScript 中并没有类的概念(语法糖除外)，因此对于方法的调用处理会难于 C++ 或者 Java。下面这个例子中，`distance`方法可以被看做`Point`的普通属性之一，不过其并非原始类型的数据，而是指向了另一个函数：

```
function Point(x, y) {
  this.x = x;
  this.y = y;
  this.distance = PointDistance;
}


function PointDistance(p) {
  var dx = this.x - p.x;
  var dy = this.y - p.y;
  return Math.sqrt(dx*dx + dy*dy);
}
```

如果我们像上文介绍的普通的 in-object 域一样来处理`distance`属性，那么无疑会带来较大的内存浪费，毕竟每个对象都要存放一段外部函数引用(Reference 的内存占用往往大于原始类型)。 C++ 中则是以指向多个虚函数的虚函数表(V-Tables )解决这个问题。每个包含虚函数的类的实例都会指向这个虚函数表，当调用某个虚函数时，程序会自动从虚函数表中加载该函数的地址信息然后转向到该地址调用。V8 中我们已经使用了隐藏类这一共享数据结构，因此可以很方便地改造下就可以。我们引入了所谓 Constant Functions 的概念，某个 Constant Function 即代表了对象中仅包含某个名字，而具体的属性值存放在描述符本身的概念：

```
<Point object is allocated>


  Class C0
    "x": TRANSITION to C1 at offset 0


this.x = x;


  Class C1
    "x": FIELD at offset 0
    "y": TRANSITION to C2 at offset 1


this.y = y;


  Class C2
    "x": FIELD at offset 0
    "y": FIELD at offset 1
    "distance": TRANSITION to C3 <PointDistance>


this.distance = PointDistance;


  Class C3
    "x": FIELD at offset 0
    "y": FIELD at offset 1
    "distance": CONSTANT_FUNCTION <PointDistance>
```

注意，在这里如果我们将`PointDistance`重定义指向了其他函数，那么这个转换也会自动失效，V8 会创建新的隐藏类。另一种解决这个问题的方法就是使用原型，每个构造函数都会有所谓的`Prototype`属性，该属性会自动成为对象的原型链上的一环，上面的例子可以改写为以下方式：

```
function Point(x, y) {
  this.x = x;
  this.y = y;
}


Point.prototype.distance = function(p) {
  var dx = this.x - p.x;
  var dy = this.y - p.y;
  return Math.sqrt(dx*dx + dy*dy);
}


...
var u = new Point(1, 2);
var v = new Point(3, 4);

var d = u.distance(v);
```

V8 同样会把原型链上的方法在隐藏类中映射为 Constant Function 描述符，而调用原型方法往往会比调用自身方法慢一点，毕竟引擎不仅要去扫描自身的隐藏类，还要去扫描原型链上对象的隐藏类才能得知真正的函数调用地址。不过这个不会对于代码的性能造成明显的影响，因此写代码的时候也不必小心翼翼的避免这个。

# Dictionary Mode

对于复杂属性的对象，V8 会使用所谓的字典模式(Dictionary Mode )来存储对象，也就是使用哈希表来存放键值信息，这种方式存储开销会小于上文提到的包含了隐藏类的方式，不过查询速度会远小于前者。初始状态下，哈希表中的所有的键与值都被设置为了`undefined`，当插入新的数据时，计算得出的键名的哈希值的低位会被当做初始的存储索引地址。如果此地址已经被占用了，V8 会尝试向下一个地址进行插入，直到插入成功，伪代码表述如下：

```
// 插入
insert(table, key, value):
  table = ensureCapacity(table, length(table) + 1)
  code = hash(key)
  n = capacity(table)
  index = code (mod n)
  while getKey(table, index) is not undefined:
    index += 1 (mod n)
  set(table, index, key, value)


//查找
lookup(table, key):
  code = hash(key)
  n = capacity(table)
  index = code (mod n)
  k = getKey(table, index)
  while k is not null or undefined
        and k != key:
    index += 1 (mod n)
    k = getKey(table, index)
  if k == key:
    return getValue(table, index)
  else:
    return undefined
```

尽管计算键名哈希值与比较的速度会比较快，但是每次读写属性的时候都进行这么多步骤无疑会大大拉低速度，因此 V8 尽可能地会避免使用这种存储方式。

# Fast Elements: 数值下标的属性

V8 中将属性名为非负整数(0 、 1、2…… )的属性称为 Element，每个对象都有一个指向 Element 数组的指针，其存放和其他属性是分开的。注意，隐藏类中并不包含 Element 的描述符，但可能包含其它有着不同 Element 类型的同一种隐藏类的转换描述符。大多数情况下，对象都会有 Fast Element，也就是说这些 Element 以连续数组的形式存放。有三种不同的 Fast Element：

* Fast small integers
* Fast doubles
* Fast values

根据标准，JavaScript 中的所有数字都理应以 64 位浮点数形式出现。因此 V8 尽可能以 31 位带符号整数来表达数字(最低位总是 0，这有助于垃圾回收器区分数字和指针)。因此含有 Fast small integers 类型的对象，其 Element 类型只会包含这样的数字。如果需要存储小数、大整数或其他特殊值，如 -0，则需要将数组提升为 Fast doubles。于是这引入了潜在的昂贵的复制 - 转换操作，但通常不会频繁发生。Fast doubles 仍然是很快的，因为所有的数字都是无封箱存储的。但如果我们要存储的是其他类型，比如字符串或者对象，则必须将其提升为普通的 Fast Element 数组。

JavaScript 不提供任何确定存储元素多少的办法。你可能会说像这样的办法，`new Array(100)`，但实际上这仅仅针对`Array`构造函数有用。如果你将值存在一个不存在的下标上，V8 会重新开辟更大的内存，将原有元素复制到新内存。V8 可以处理带空洞的数组，也就是只有某些下标是存有元素，而期间的下标都是空的。其内部会安插特殊的哨兵值，因此试图访问未赋值的下标，会得到`undefined`。当然，Fast Element 也有其限制。如果你在远远超过当前数组大小的下标赋值，V8 会将数组转换为字典模式，将值以哈希表的形式存储。这对于稀疏数组来说很有用，但性能上肯定打了折扣，无论是从转换这一过程来说，还是从之后的访问来说。如果你需要复制整个数组，不要逆向复制(索引从高到低)，因为这几乎必然触发字典模式。

```
	// 这会大大降低大数组的性能
	function copy(a) {
		var b = new Array();
		for (var i = a.length - 1; i >= 0; i--)
			b[i] = a[i];
		return b;
	}
```

由于普通的属性和数字式属性分开存放，即使数组退化为字典模式，也不会影响到其他属性的访问速度(反之亦然)。

# Object 代码声明

```
// https://v8docs.nodesource.com/node-7.2/d4/da0/v8_8h_source.html#l02660
class V8_EXPORT Object : public Value {
  public:
   V8_DEPRECATE_SOON("Use maybe version",
                     bool Set(Local<Value> key, Local<Value> value));
   V8_WARN_UNUSED_RESULT Maybe<bool> Set(Local<Context> context,
                                         Local<Value> key, Local<Value> value);

   V8_DEPRECATE_SOON("Use maybe version",
                     bool Set(uint32_t index, Local<Value> value));
   V8_WARN_UNUSED_RESULT Maybe<bool> Set(Local<Context> context, uint32_t index,
                                         Local<Value> value);

   // Implements CreateDataProperty (ECMA-262, 7.3.4).
   //
   // Defines a configurable, writable, enumerable property with the given value
   // on the object unless the property already exists and is not configurable
   // or the object is not extensible.
   //
   // Returns true on success.
   V8_WARN_UNUSED_RESULT Maybe<bool> CreateDataProperty(Local<Context> context,
                                                        Local<Name> key,
                                                        Local<Value> value);
   V8_WARN_UNUSED_RESULT Maybe<bool> CreateDataProperty(Local<Context> context,
                                                        uint32_t index,
                                                        Local<Value> value);

   // Implements DefineOwnProperty.
   //
   // In general, CreateDataProperty will be faster, however, does not allow
   // for specifying attributes.
   //
   // Returns true on success.
   V8_WARN_UNUSED_RESULT Maybe<bool> DefineOwnProperty(
       Local<Context> context, Local<Name> key, Local<Value> value,
       PropertyAttribute attributes = None);

   // Sets an own property on this object bypassing interceptors and
   // overriding accessors or read-only properties.
   //
   // Note that if the object has an interceptor the property will be set
   // locally, but since the interceptor takes precedence the local property
   // will only be returned if the interceptor doesn't return a value.
   //
   // Note also that this only works for named properties.
   V8_DEPRECATED("Use CreateDataProperty / DefineOwnProperty",
                 bool ForceSet(Local<Value> key, Local<Value> value,
                               PropertyAttribute attribs = None));
   V8_DEPRECATE_SOON("Use CreateDataProperty / DefineOwnProperty",
                     Maybe<bool> ForceSet(Local<Context> context,
                                          Local<Value> key, Local<Value> value,
                                          PropertyAttribute attribs = None));

   V8_DEPRECATE_SOON("Use maybe version", Local<Value> Get(Local<Value> key));
   V8_WARN_UNUSED_RESULT MaybeLocal<Value> Get(Local<Context> context,
                                               Local<Value> key);

   V8_DEPRECATE_SOON("Use maybe version", Local<Value> Get(uint32_t index));
   V8_WARN_UNUSED_RESULT MaybeLocal<Value> Get(Local<Context> context,
                                               uint32_t index);

   V8_DEPRECATED("Use maybe version",
                 PropertyAttribute GetPropertyAttributes(Local<Value> key));
   V8_WARN_UNUSED_RESULT Maybe<PropertyAttribute> GetPropertyAttributes(
       Local<Context> context, Local<Value> key);

   V8_DEPRECATED("Use maybe version",
                 Local<Value> GetOwnPropertyDescriptor(Local<String> key));
   V8_WARN_UNUSED_RESULT MaybeLocal<Value> GetOwnPropertyDescriptor(
       Local<Context> context, Local<String> key);

   V8_DEPRECATE_SOON("Use maybe version", bool Has(Local<Value> key));
   V8_WARN_UNUSED_RESULT Maybe<bool> Has(Local<Context> context,
                                         Local<Value> key);

   V8_DEPRECATE_SOON("Use maybe version", bool Delete(Local<Value> key));
   // TODO(dcarney): mark V8_WARN_UNUSED_RESULT
   Maybe<bool> Delete(Local<Context> context, Local<Value> key);

   V8_DEPRECATED("Use maybe version", bool Has(uint32_t index));
   V8_WARN_UNUSED_RESULT Maybe<bool> Has(Local<Context> context, uint32_t index);

   V8_DEPRECATED("Use maybe version", bool Delete(uint32_t index));
   // TODO(dcarney): mark V8_WARN_UNUSED_RESULT
   Maybe<bool> Delete(Local<Context> context, uint32_t index);

   V8_DEPRECATED("Use maybe version",
                 bool SetAccessor(Local<String> name,
                                  AccessorGetterCallback getter,
                                  AccessorSetterCallback setter = 0,
                                  Local<Value> data = Local<Value>(),
                                  AccessControl settings = DEFAULT,
                                  PropertyAttribute attribute = None));
   V8_DEPRECATED("Use maybe version",
                 bool SetAccessor(Local<Name> name,
                                  AccessorNameGetterCallback getter,
                                  AccessorNameSetterCallback setter = 0,
                                  Local<Value> data = Local<Value>(),
                                  AccessControl settings = DEFAULT,
                                  PropertyAttribute attribute = None));
   // TODO(dcarney): mark V8_WARN_UNUSED_RESULT
   Maybe<bool> SetAccessor(Local<Context> context, Local<Name> name,
                           AccessorNameGetterCallback getter,
                           AccessorNameSetterCallback setter = 0,
                           MaybeLocal<Value> data = MaybeLocal<Value>(),
                           AccessControl settings = DEFAULT,
                           PropertyAttribute attribute = None);

   void SetAccessorProperty(Local<Name> name, Local<Function> getter,
                            Local<Function> setter = Local<Function>(),
                            PropertyAttribute attribute = None,
                            AccessControl settings = DEFAULT);

   Maybe<bool> HasPrivate(Local<Context> context, Local<Private> key);
   Maybe<bool> SetPrivate(Local<Context> context, Local<Private> key,
                          Local<Value> value);
   Maybe<bool> DeletePrivate(Local<Context> context, Local<Private> key);
   MaybeLocal<Value> GetPrivate(Local<Context> context, Local<Private> key);

   V8_DEPRECATE_SOON("Use maybe version", Local<Array> GetPropertyNames());
   V8_WARN_UNUSED_RESULT MaybeLocal<Array> GetPropertyNames(
       Local<Context> context);
   V8_WARN_UNUSED_RESULT MaybeLocal<Array> GetPropertyNames(
       Local<Context> context, KeyCollectionMode mode,
       PropertyFilter property_filter, IndexFilter index_filter);

   V8_DEPRECATE_SOON("Use maybe version", Local<Array> GetOwnPropertyNames());
   V8_WARN_UNUSED_RESULT MaybeLocal<Array> GetOwnPropertyNames(
       Local<Context> context);

   V8_WARN_UNUSED_RESULT MaybeLocal<Array> GetOwnPropertyNames(
       Local<Context> context, PropertyFilter filter);

   Local<Value> GetPrototype();

   V8_DEPRECATED("Use maybe version", bool SetPrototype(Local<Value> prototype));
   V8_WARN_UNUSED_RESULT Maybe<bool> SetPrototype(Local<Context> context,
                                                  Local<Value> prototype);

   Local<Object> FindInstanceInPrototypeChain(Local<FunctionTemplate> tmpl);

   V8_DEPRECATED("Use maybe version", Local<String> ObjectProtoToString());
   V8_WARN_UNUSED_RESULT MaybeLocal<String> ObjectProtoToString(
       Local<Context> context);

   Local<String> GetConstructorName();

   Maybe<bool> SetIntegrityLevel(Local<Context> context, IntegrityLevel level);

   int InternalFieldCount();

   V8_INLINE static int InternalFieldCount(
       const PersistentBase<Object>& object) {
     return object.val_->InternalFieldCount();
   }

   V8_INLINE Local<Value> GetInternalField(int index);

   void SetInternalField(int index, Local<Value> value);

   V8_INLINE void* GetAlignedPointerFromInternalField(int index);

   V8_INLINE static void* GetAlignedPointerFromInternalField(
       const PersistentBase<Object>& object, int index) {
     return object.val_->GetAlignedPointerFromInternalField(index);
   }

   void SetAlignedPointerInInternalField(int index, void* value);
   void SetAlignedPointerInInternalFields(int argc, int indices[],
                                          void* values[]);

   // Testers for local properties.
   V8_DEPRECATED("Use maybe version", bool HasOwnProperty(Local<String> key));
   V8_WARN_UNUSED_RESULT Maybe<bool> HasOwnProperty(Local<Context> context,
                                                    Local<Name> key);
   V8_WARN_UNUSED_RESULT Maybe<bool> HasOwnProperty(Local<Context> context,
                                                    uint32_t index);
   V8_DEPRECATE_SOON("Use maybe version",
                     bool HasRealNamedProperty(Local<String> key));
   V8_WARN_UNUSED_RESULT Maybe<bool> HasRealNamedProperty(Local<Context> context,
                                                          Local<Name> key);
   V8_DEPRECATE_SOON("Use maybe version",
                     bool HasRealIndexedProperty(uint32_t index));
   V8_WARN_UNUSED_RESULT Maybe<bool> HasRealIndexedProperty(
       Local<Context> context, uint32_t index);
   V8_DEPRECATE_SOON("Use maybe version",
                     bool HasRealNamedCallbackProperty(Local<String> key));
   V8_WARN_UNUSED_RESULT Maybe<bool> HasRealNamedCallbackProperty(
       Local<Context> context, Local<Name> key);

   V8_DEPRECATED(
       "Use maybe version",
       Local<Value> GetRealNamedPropertyInPrototypeChain(Local<String> key));
   V8_WARN_UNUSED_RESULT MaybeLocal<Value> GetRealNamedPropertyInPrototypeChain(
       Local<Context> context, Local<Name> key);

   V8_DEPRECATED(
       "Use maybe version",
       Maybe<PropertyAttribute> GetRealNamedPropertyAttributesInPrototypeChain(
           Local<String> key));
   V8_WARN_UNUSED_RESULT Maybe<PropertyAttribute>
   GetRealNamedPropertyAttributesInPrototypeChain(Local<Context> context,
                                                  Local<Name> key);

   V8_DEPRECATED("Use maybe version",
                 Local<Value> GetRealNamedProperty(Local<String> key));
   V8_WARN_UNUSED_RESULT MaybeLocal<Value> GetRealNamedProperty(
       Local<Context> context, Local<Name> key);

   V8_DEPRECATED("Use maybe version",
                 Maybe<PropertyAttribute> GetRealNamedPropertyAttributes(
                     Local<String> key));
   V8_WARN_UNUSED_RESULT Maybe<PropertyAttribute> GetRealNamedPropertyAttributes(
       Local<Context> context, Local<Name> key);

   bool HasNamedLookupInterceptor();

   bool HasIndexedLookupInterceptor();

   int GetIdentityHash();

   // TODO(dcarney): take an isolate and optionally bail out?
   Local<Object> Clone();

   Local<Context> CreationContext();

   bool IsCallable();

   bool IsConstructor();

   V8_DEPRECATED("Use maybe version",
                 Local<Value> CallAsFunction(Local<Value> recv, int argc,
                                             Local<Value> argv[]));
   V8_WARN_UNUSED_RESULT MaybeLocal<Value> CallAsFunction(Local<Context> context,
                                                          Local<Value> recv,
                                                          int argc,
                                                          Local<Value> argv[]);

   V8_DEPRECATED("Use maybe version",
                 Local<Value> CallAsConstructor(int argc, Local<Value> argv[]));
   V8_WARN_UNUSED_RESULT MaybeLocal<Value> CallAsConstructor(
       Local<Context> context, int argc, Local<Value> argv[]);

   V8_DEPRECATE_SOON("Keep track of isolate correctly", Isolate* GetIsolate());

   static Local<Object> New(Isolate* isolate);

   V8_INLINE static Object* Cast(Value* obj);

  private:
   Object();
   static void CheckCast(Value* obj);
   Local<Value> SlowGetInternalField(int index);
   void* SlowGetAlignedPointerFromInternalField(int index);
 };
```
