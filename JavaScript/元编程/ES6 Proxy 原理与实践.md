[![返回目录](https://i.postimg.cc/KvQbty96/image.png)](https://url.wx-coder.cn/lrKga)

# 深入浅出 ES6 Proxy 与 Reflect

```
var target = {
  get foo() {
  return this.bar;
  },
  bar: 3
};
var handler = {
  get(target, propertyKey, receiver) {
  if (propertyKey === 'bar') return 2;


  console.log(Reflect.get(target, propertyKey, receiver)); // this in foo getter references Proxy instance; logs 2
  console.log(target[propertyKey]); // this in foo getter references "target" - logs 3
  }
};
var obj = new Proxy(target, handler);
```
