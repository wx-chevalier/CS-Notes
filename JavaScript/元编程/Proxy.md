[![返回目录](https://i.postimg.cc/KvQbty96/image.png)](https://ngte-pl.gitbook.io/i/javascript)

# Proxy

```js
/*
const proxy = new Proxy({}, {
  get: (obj, prop) => { ... },
  set: (obj, prop, value) => { ... },
  // more props here
});
*/

// This basic proxy returns null instead of undefined if the
// property doesn't exist
// 如果属性不存在那么返回的是 null 而不是 undefined
const proxy = new Proxy(
  {},
  {
    get: (obj, prop) => {
      return prop in obj ? obj[prop] : null;
    }
  }
);

// proxy.whatever => null
```

# Proxy 案例

## 数据存储

```js
// storage 是 Storage API 的类型，可以是 localStorage 或是 sessionStorage
// prefix 则属于 namespace
function getStorage(storage, prefix) {
  // 这里返回一个 Proxy 实例，调用这个实例的 set 或 get 方法来存取数据
  return new Proxy(
    {},
    {
      set: (obj, prop, value) => {
        obj[prop] = value;
        storage.setItem(`${prefix}.${prop}`, value);
      },
      get: (obj, prop) => {
        // return obj[prop];
        return storage.getItem(`${prefix}.${prop}`);
      }
    }
  );
}

// Create an instance of the storage proxy
// 使用的时候首先通过 namespace 创建 Storage Proxy 实例
const userObject = getStorage(localStorage, 'user');

// Set a value in localStorage
// 然后通过直接访问属性的方法来操作数据
userObject.name = 'David';

// Get the value from localStorage
// 可以方便的使用解构获取数据
const { name } = userObject;
```
