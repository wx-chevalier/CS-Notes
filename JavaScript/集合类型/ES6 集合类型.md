[![返回目录](https://parg.co/USw)](https://parg.co/bxN)

# Set: 集合

集合也是一种常见的数据结构，在 ES6 中添加了对于集合的支持，其基本用法如下：

```js
// Sets
var s = new Set();
s
  .add("hello")
  .add("goodbye")
  .add("hello");
s.size === 2;
s.has("hello") === true;
```

### WeakSet

WeakSet 提供了一种自回收的存储方式：

```javascript
// Weak Sets
var ws = new WeakSet();
ws.add({ data: 42 });
// Because the added object has no other references, it will not be held in the set
```

>[Maps,Sets And Iterators in JavaScript](http://bjorn.tipling.com/maps-sets-and-iterators-in-javascript)

# Map

## 创建增删

```javascript
var map = new Map();
map.set("foo", "bar");
console.log(map.get("foo")); //logs "bar"
var animalSounds = new Map();

animalSounds.set("dog", "woof");
animalSounds.set("cat", "meow");
animalSounds.set("frog", "ribbit");

console.log(animalSounds.size); //logs 3
console.log(animalSounds.has("dog")); //logs true

animalSounds.delete("dog");

console.log(animalSounds.size); //logs 2
console.log(animalSounds.has("dog")); //logs false

animalSounds.clear();
console.log(animalSounds.size); //logs 0
```

## 索引遍历

```javascript
usersMap = new Map();
usersMap.set(1, "sally");
usersMap.set(2, "bob");
usersMap.set(3, "jane");

console.log(usersMap.get(1)); //logs "sally"
usersMap.forEach(function(username, userId) {
  console.log(userId, typeof userId); //logs 1..3, "number"
  if (userId === 1) {
    console.log("We found sally.");
  }
});

//如果用for...of方式遍历，每次返回的是一个Array
for (data of usersMap) {
  console.log(data); //Array [1,"sally"]
}
```

Map 的键的类型可以是 object、NaN 等等。

```javascript
var obj, map;
map = new Map();
obj = { foo: "bar" };
map.set(obj, "foobar");
obj.newProp = "stuff";
console.log(map.has(obj)); //logs true
console.log(map.get(obj)); //logs "foobar"
```
