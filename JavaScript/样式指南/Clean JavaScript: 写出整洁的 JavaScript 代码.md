> [Clean JavaScript: 写出整洁的 JavaScript 代码](https://zhuanlan.zhihu.com/p/24761475)翻译自[clean-code-javascript](https://github.com/ryanmcdermott/clean-code-javascript)。本文从属于笔者的[Web 前端入门与工程实践](https://github.com/wxyyxc1992/Web-Frontend-Introduction-And-Engineering-Practices)。

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/1/1/wtfm.jpg)

# Introduction: 简介

很多开发者都会推崇 Robert C. Martin 的[_Clean Code_](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)一书中提及的软件工程准则，本文就是对于这些准则在 JavaScript 开发领域中的实践应用总结。本文并不仅仅是样式指南，而是对于如何编写出基于 JavaScript 实现的高可读性、高可用性以及可重构的软件系统。虽然本文对比的讲了很多好坏的实践，但并不是说本文就建议大家强制遵循所有的指南。实际上对于 Clean Code 的概念不同的团队、不同的开发者都会有不同的见解与看法，本文的很多观点也是充满争议。软件工程已经走过了五十多个年头，而我们也一直在前行，很难说有什么原则是永恒正确的。作者更希望这些指南与考量起到试金石的作用，成为评判团队 JavaScript 代码质量的考量标准之一。

最后还需要强调的，好的代码、好的架构都是慢慢衍化而来，切不可操之过急。千里之行，始于足下，在前行的道路上势必会走很多的弯路、错误，但是只要不断调整方向总会回到正确的道路上。我们不能畏惧改变，也不能把他人的话完全奉为圭臬，无论多少年的老程序员也是会犯错的、

# **Variables**: 变量

## 使用有意义的可发音的变量名

**Bad:**

```javascript
var yyyymmdstr = moment().format("YYYY/MM/DD");
```

**Good**:

```javascript
var yearMonthDay = moment().format("YYYY/MM/DD");
```

## 对相同类型的变量使用相同的关键字

**Bad:**

```javascript
getUserInfo();
getClientData();
getCustomerRecord();
```

**Good**:

```javascript
getUser();
```

## 使用可搜索的命名

在开发过程中，我们阅读代码的时间会远远超过编写代码的时间，因此保证代码的可读性与可搜索会非常重要。切记，没事不要坑自己。

**Bad:**

```javascript
//525600到底啥意思？
for (var i = 0; i < 525600; i++) {
  runCronJob();
}
```

**Good**:

```javascript
// 声明为全局变量
var MINUTES_IN_A_YEAR = 525600;
for (var i = 0; i < MINUTES_IN_A_YEAR; i++) {
  runCronJob();
}
```

## 使用说明性质的临时变量

**Bad:**

```javascript
let cityStateRegex = /^(.+)[,\\s]+(.+?)\s*(\d{5})?$/;
saveCityState(
  cityStateRegex.match(cityStateRegex)[1],
  cityStateRegex.match(cityStateRegex)[2]
);
```

**Good**:

```javascript
let cityStateRegex = /^(.+)[,\\s]+(.+?)\s*(\d{5})?$/;
let match = cityStateRegex.match(cityStateRegex);
let city = match[1];
let state = match[2];
saveCityState(city, state);
```

## 避免摸不着头脑的临时变量

在遍历或者 mapping 过程中，需要避免短小无意义的变量命名。

**Bad:**

```javascript
var locations = ['Austin', 'New York', 'San Francisco'];
locations.forEach((l) => {
  doStuff();
  doSomeOtherStuff();
  ...
  ...
  ...
  // Wait, what is `l` for again?
  dispatch(l);
});
```

**Good**:

```javascript
var locations = ['Austin', 'New York', 'San Francisco'];
locations.forEach((location) => {
  doStuff();
  doSomeOtherStuff();
  ...
  ...
  ...
  dispatch(location);
});
```

## 避免添加不需要的内容

如果你的类名 / 实例名已经能够表述某些信息，那么在类 / 实例的属性中就不需要重复命名。

**Bad:**

```javascript
var Car = {
  carMake: "Honda",
  carModel: "Accord",
  carColor: "Blue"
};

function paintCar(car) {
  car.carColor = "Red";
}
```

**Good**:

```javascript
var Car = {
  make: "Honda",
  model: "Accord",
  color: "Blue"
};

function paintCar(car) {
  car.color = "Red";
}
```

## Short-circuiting 优于条件选择

**Bad:**

```javascript
function createMicrobrewery(name) {
  var breweryName;
  if (name) {
    breweryName = name;
  } else {
    breweryName = "Hipster Brew Co.";
  }
}
```

**Good**:

```javascript
function createMicrobrewery(name) {
  var breweryName = name || "Hipster Brew Co.";
}
```

# 函数

## 函数参数最好不超过两个

限制函数的参数数目还是比较重要的，它能够方便对于函数的测试，避免需要进行不同的 Case 测试时把代码变得一团糟。我们应该尽可能地控制参数数目小于或等于两个，如果你的参数数目多于两个，那么建议使用高阶对象进行适当封装。

**Bad:**

```javascript
function createMenu(title, body, buttonText, cancellable) {
  ...
}
```

**Good**:

```javascript
var menuConfig = {
  title: 'Foo',
  body: 'Bar',
  buttonText: 'Baz',
  cancellable: true
}

function createMenu(menuConfig) {
  ...
}
```

## 函数应当遵循单一职责原则

这一条算是迄今为止软件工程中最重要的原则之一了。如果我们给单一函数赋予了过多的职责，那么其很难被用于组合、测试等。而如果你保证函数的单一职责性质，那么相对其重构难度、代码可读性也会更好。

**Bad:**

```javascript
function emailClients(clients) {
  clients.forEach(client => {
    let clientRecord = database.lookup(client);
    if (clientRecord.isActive()) {
      email(client);
    }
  });
}
```

**Good**:

```javascript
function emailClients(clients) {
  clients.forEach(client => {
    emailClientIfNeeded(client);
  });
}

function emailClientIfNeeded(client) {
  if (isClientActive(client)) {
    email(client);
  }
}

function isClientActive(client) {
  let clientRecord = database.lookup(client);
  return clientRecord.isActive();
}
```

## 函数命名应该反映其功能

**Bad:**

```javascript
function dateAdd(date, month) {
  // ...
}

let date = new Date();

// 很难从函数名中获知该函数到底是谁加上谁
dateAdd(date, 1);
```

**Good**:

```javascript
function dateAddMonth(date, month) {
  // ...
}

let date = new Date();
dateAddMonth(date, 1);
```

## 函数应当只是一层抽象

这一条类似于单一职责原则，不过更倾向于关注函数的抽象程度，如果我们在单一函数中添加了过多的抽象层，同样会降低的函数可读性、增加重构难度。

**Bad:**

```javascript
function parseBetterJSAlternative(code) {
  let REGEXES = [
    // ...
  ];

  let statements = code.split(" ");
  let tokens;
  REGEXES.forEach(REGEX => {
    statements.forEach(statement => {
      // ...
    });
  });

  let ast;
  tokens.forEach(token => {
    // lex...
  });

  ast.forEach(node => {
    // parse...
  });
}
```

**Good**:

```javascript
function tokenize(code) {
  let REGEXES = [
    // ...
  ];

  let statements = code.split(" ");
  let tokens;
  REGEXES.forEach(REGEX => {
    statements.forEach(statement => {
      // ...
    });
  });

  return tokens;
}

function lexer(tokens) {
  let ast;
  tokens.forEach(token => {
    // lex...
  });

  return ast;
}

function parseBetterJSAlternative(code) {
  let tokens = tokenize(code);
  let ast = lexer(tokens);
  ast.forEach(node => {
    // parse...
  });
}
```

## 移除重复代码

在任何情况下都不要去容许重复代码的存在。重复代码指那些修改单一逻辑时需要修改多个代码片的代码交集，JavaScript 本身是弱类型语言，相对而言编写泛型函数会更加容易。

**Bad:**

```javascript
function showDeveloperList(developers) {
  developers.forEach(developers => {
    var expectedSalary = developer.calculateExpectedSalary();
    var experience = developer.getExperience();
    var githubLink = developer.getGithubLink();
    var data = {
      expectedSalary: expectedSalary,
      experience: experience,
      githubLink: githubLink
    };

    render(data);
  });
}

function showManagerList(managers) {
  managers.forEach(manager => {
    var expectedSalary = manager.calculateExpectedSalary();
    var experience = manager.getExperience();
    var portfolio = manager.getMBAProjects();
    var data = {
      expectedSalary: expectedSalary,
      experience: experience,
      portfolio: portfolio
    };

    render(data);
  });
}
```

**Good**:

```javascript
function showList(employees) {
  employees.forEach(employee => {
    var expectedSalary = employee.calculateExpectedSalary();
    var experience = employee.getExperience();
    var portfolio;

    if (employee.type === "manager") {
      portfolio = employee.getMBAProjects();
    } else {
      portfolio = employee.getGithubLink();
    }

    var data = {
      expectedSalary: expectedSalary,
      experience: experience,
      portfolio: portfolio
    };

    render(data);
  });
}
```

## 使用默认参数代替或运算

**Bad:**

```javascript
function writeForumComment(subject, body) {
  subject = subject || "No Subject";
  body = body || "No text";
}
```

**Good**:

```javascript
function writeForumComment(subject = 'No subject', body = 'No text') {
  ...
}
```

## 使用 Object.assign 设置默认值

**Bad:**

```javascript
var menuConfig = {
  title: null,
  body: "Bar",
  buttonText: null,
  cancellable: true
};

function createMenu(config) {
  config.title = config.title || "Foo";
  config.body = config.body || "Bar";
  config.buttonText = config.buttonText || "Baz";
  config.cancellable =
    config.cancellable === undefined ? config.cancellable : true;
}

createMenu(menuConfig);
```

**Good**:

```javascript
var menuConfig = {
  title: "Order",
  // User did not include 'body' key
  buttonText: "Send",
  cancellable: true
};

function createMenu(config) {
  config = Object.assign(
    {
      title: "Foo",
      body: "Bar",
      buttonText: "Baz",
      cancellable: true
    },
    config
  );

  // config now equals: {title: "Foo", body: "Bar", buttonText: "Baz", cancellable: true}
  // ...
}

createMenu(menuConfig);
```

## 避免在参数中使用 Flags

有的开发者会使用 Flags 来控制函数执行不同的逻辑流，不过就如我们在上文中提及的单一职责原则，我们应当将函数拆分为不同的部分，然后在外层调用上根据 Flags 调用不同的函数。

**Bad:**

```javascript
function createFile(name, temp) {
  if (temp) {
    fs.create("./temp/" + name);
  } else {
    fs.create(name);
  }
}
```

**Good**:

```javascript
function createTempFile(name) {
  fs.create("./temp/" + name);
}

function createFile(name) {
  fs.create(name);
}
```

## 避免冗余副作用

如果某个函数除了接收输入值与返回值之外还做了其他事，那么就称其具有副作用。典型的副作用譬如写文件、修改某些全局变量、修改内存参数等等。在编程中我们不可避免的需要产生副作用，譬如上面例子中我们需要写入到某个外部文件。而你应当做的就是将所有的写文件操作由某个服务统一处理，而不应该将写文件的操作分散到数个类或者函数中。这一点最大的优势在于避免了不同对象之间共享状态，共享的可变状态可是万恶之源啊。

**Bad:**

```javascript
// 定义全局变量
// 如果我们有其他的函数引用了该变量，那么我们就无法预测该变量类型
var name = "Ryan McDermott";

function splitIntoFirstAndLastName() {
  name = name.split(" ");
}

splitIntoFirstAndLastName();

console.log(name); // ['Ryan', 'McDermott'];
```

**Good**:

```javascript
function splitIntoFirstAndLastName(name) {
  return name.split(" ");
}

var name = "Ryan McDermott";
var newName = splitIntoFirstAndLastName(name);

console.log(name); // 'Ryan McDermott';
console.log(newName); // ['Ryan', 'McDermott'];
```

## 避免污染全局函数

JavaScript 中有个不太好的实践就是修改某个全局函数，将其指向其他的库或者自定义函数，不过这个会对某个懵懂的用户造成困恼。如果你想给 JavaScript 原生的 Array 添加一个 diff 函数支持，来展示两个数组的差异。你可以选择将函数挂载到`Array.prototype`，不过很有可能跟其他打算占用这个位置的库起冲突。我们更建议使用 ES6 的 classes，并且使用继承方式去添加新的功能函数。

**Bad:**

```javascript
Array.prototype.diff = function(comparisonArray) {
  var values = [];
  var hash = {};

  for (var i of comparisonArray) {
    hash[i] = true;
  }

  for (var i of this) {
    if (!hash[i]) {
      values.push(i);
    }
  }

  return values;
};
```

**Good:**

```javascript
class SuperArray extends Array {
  constructor(...args) {
    super(...args);
  }

  diff(comparisonArray) {
    var values = [];
    var hash = {};

    for (var i of comparisonArray) {
      hash[i] = true;
    }

    for (var i of this) {
      if (!hash[i]) {
        values.push(i);
      }
    }

    return values;
  }
}
```

## 优先选择函数式编程而不是命令式编程

JavaScript 并不像 Haskell 这样纯粹的函数式编程语言，不过其对于实践函数式编程的理念还是很推崇的。函数式编程可读性更好，也更易于测试。

**Bad:**

```javascript
const programmerOutput = [
  {
    name: "Uncle Bobby",
    linesOfCode: 500
  },
  {
    name: "Suzie Q",
    linesOfCode: 1500
  },
  {
    name: "Jimmy Gosling",
    linesOfCode: 150
  },
  {
    name: "Gracie Hopper",
    linesOfCode: 1000
  }
];

var totalOutput = 0;

for (var i = 0; i < programmerOutput.length; i++) {
  totalOutput += programmerOutput[i].linesOfCode;
}
```

**Good**:

```javascript
const programmerOutput = [
  {
    name: "Uncle Bobby",
    linesOfCode: 500
  },
  {
    name: "Suzie Q",
    linesOfCode: 1500
  },
  {
    name: "Jimmy Gosling",
    linesOfCode: 150
  },
  {
    name: "Gracie Hopper",
    linesOfCode: 1000
  }
];

var totalOutput = programmerOutput
  .map(programmer => programmer.linesOfCode)
  .reduce((acc, linesOfCode) => acc + linesOfCode, 0);
```

## 封装条件选择

**Bad:**

```javascript
if (fsm.state === "fetching" && isEmpty(listNode)) {
  /// ...
}
```

**Good**:

```javascript
function shouldShowSpinner(fsm, listNode) {
  return fsm.state === "fetching" && isEmpty(listNode);
}

if (shouldShowSpinner(fsmInstance, listNodeInstance)) {
  // ...
}
```

## 避免负类条件

**Bad:**

```javascript
function isDOMNodeNotPresent(node) {
  // ...
}

if (!isDOMNodeNotPresent(node)) {
  // ...
}
```

**Good**:

```javascript
function isDOMNodePresent(node) {
  // ...
}

if (isDOMNodePresent(node)) {
  // ...
}
```

## 避免使用条件选择

很多人第一次听到这个概念都会觉得不可思议，没有`if`条件选择语句的话又该如何编程呢？在这里我们推荐使用多态性来达成这一目标，因为如果在函数或类中嵌入过多的`if`语句，会导致该函数或者类破坏单一职责原则。

**Bad:**

```javascript
class Airplane {
  //...
  getCruisingAltitude() {
    switch (this.type) {
      case "777":
        return getMaxAltitude() - getPassengerCount();
      case "Air Force One":
        return getMaxAltitude();
      case "Cesna":
        return getMaxAltitude() - getFuelExpenditure();
    }
  }
}
```

**Good**:

```javascript
class Airplane {
  //...
}

class Boeing777 extends Airplane {
  //...
  getCruisingAltitude() {
    return getMaxAltitude() - getPassengerCount();
  }
}

class AirForceOne extends Airplane {
  //...
  getCruisingAltitude() {
    return getMaxAltitude();
  }
}

class Cesna extends Airplane {
  //...
  getCruisingAltitude() {
    return getMaxAltitude() - getFuelExpenditure();
  }
}
```

## 避免依赖于类型检测

很多时候我们会依赖于 JavaScript 输入的参数类型来进入不同的控制流，不过鉴于 JavaScript 本身是弱类型语言，我们还是应该避免这种实践。第一个方法就是使用较为一致性的接口。

**Bad:**

```javascript
function travelToTexas(vehicle) {
  if (vehicle instanceof Bicycle) {
    vehicle.peddle(this.currentLocation, new Location("texas"));
  } else if (vehicle instanceof Car) {
    vehicle.drive(this.currentLocation, new Location("texas"));
  }
}
```

**Good**:

```javascript
function travelToTexas(vehicle) {
  vehicle.move(this.currentLocation, new Location("texas"));
}
```

## 避免依赖于类型检测

如果你需要操作像字符串、数值、列表这样的基础数据类型，你就无法依赖于多态性来实现类型检测。那么建议是使用 TypeScript，它为普通的 JavaScript 添加了静态类型支持。

**Bad:**

```javascript
function combine(val1, val2) {
  if (
    (typeof val1 == "number" && typeof val2 == "number") ||
    (typeof val1 == "string" && typeof val2 == "string")
  ) {
    return val1 + val2;
  } else {
    throw new Error("Must be of type String or Number");
  }
}
```

**Good**:

```javascript
function combine(val1, val2) {
  return val1 + val2;
}
```

## 避免过度优化

现代浏览器已经在运行时做了很多的优化，因此很多时候如果我们要遵循那些流传已久的优化策略不过是浪费时间。可以参考[这个](https://github.com/petkaantonov/bluebird/wiki/Optimization-killers)来获取建议的优化要点。

**Bad:**

```javascript
// On old browsers, each iteration would be costly because `len` would be
// recomputed. In modern browsers, this is optimized.
for (var i = 0, len = list.length; i < len; i++) {
  // ...
}
```

**Good**:

```javascript
for (var i = 0; i < list.length; i++) {
  // ...
}
```

## 移除弃用的代码

弃用的代码就和重复的代码一样，我们没有任何理由保留他们。不过为防万一建议不要彻底从 Git 的历史记录中删除它们。

**Bad:**

```javascript
function oldRequestModule(url) {
  // ...
}

function newRequestModule(url) {
  // ...
}

var req = newRequestModule;
inventoryTracker("apples", req, "www.inventory-awesome.io");
```

**Good**:

```javascript
function newRequestModule(url) {
  // ...
}

var req = newRequestModule;
inventoryTracker("apples", req, "www.inventory-awesome.io");
```

# 对象与数据结构

## 使用 getters 与 setters

在 JavaScript 的对象属性读写中，建议使用 getter 或者 setter，而不是直接读取或者赋值。不过 JavaScript 并没有类似于 public 或者 private 这样的关键字，因此很难通过接口方式进行强限制。不过鉴于以下优势我们还是强烈建议使用 getter 或者 setter: 1. 如果你打算不仅仅是直接获取原始值，使用 getter 能够避免去修改每个取值的地方。 2. 使用`set`能够方便地添加校验。

3. 封装内部表述。
4. 便于添加日志与错误处理。
5. 通过继承能够复写默认功能。
6. 支持属性懒加载。

**Bad:**

```javascript
class BankAccount {
  constructor() {
    this.balance = 1000;
  }
}

let bankAccount = new BankAccount();

// Buy shoes...
bankAccount.balance = bankAccount.balance - 100;
```

**Good**:

```javascript
class BankAccount {
  constructor() {
    this.balance = 1000;
  }

  // It doesn't have to be prefixed with `get` or `set` to be a getter/setter
  withdraw(amount) {
    if (verifyAmountCanBeDeducted(amount)) {
      this.balance -= amount;
    }
  }
}

let bankAccount = new BankAccount();

// Buy shoes...
bankAccount.withdraw(100);
```

## 为对象添加私有属性

可以通过闭包方式添加私有属性：

**Bad:**

```javascript
var Employee = function(name) {
  this.name = name;
};

Employee.prototype.getName = function() {
  return this.name;
};

var employee = new Employee("John Doe");
console.log("Employee name: " + employee.getName()); // Employee name: John Doe
delete employee.name;
console.log("Employee name: " + employee.getName()); // Employee name: undefined
```

**Good**:

```javascript
var Employee = (function() {
  function Employee(name) {
    this.getName = function() {
      return name;
    };
  }

  return Employee;
})();

var employee = new Employee("John Doe");
console.log("Employee name: " + employee.getName()); // Employee name: John Doe
delete employee.name;
console.log("Employee name: " + employee.getName()); // Employee name: John Doe
```

# 类

## 单一职责原则

便如 Clean Code 中所述，不应该为了多个理由去更改某个类的代码，这样会把某个类塞入过多的功能。最小化你需要去改变某个类的次数对于保证代码的稳定性至关重要，过多的改变代码会影响代码库中依赖于该类的其他模块。

**Bad:**

```javascript
class UserSettings {
  constructor(user) {
    this.user = user;
  }

  changeSettings(settings) {
    if (this.verifyCredentials(user)) {
      // ...
    }
  }

  verifyCredentials(user) {
    // ...
  }
}
```

**Good**:

```javascript
class UserAuth {
  constructor(user) {
    this.user = user;
  }

  verifyCredentials() {
    // ...
  }
}

class UserSettings {
  constructor(user) {
    this.user = user;
    this.auth = new UserAuth(user);
  }

  changeSettings(settings) {
    if (this.auth.verifyCredentials()) {
      // ...
    }
  }
}
```

## 开放封闭原则

正如 Bertrand Meyer 所述，譬如类、模块、函数这样的实体应该面向扩展开放，而拒绝修改。换言之，我们推荐去继承扩展某个函数或模块，而不是每次都去修改源代码。

**Bad:**

```javascript
class AjaxRequester {
  constructor() {
    // What if we wanted another HTTP Method, like DELETE? We would have to
    // open this file up and modify this and put it in manually.
    this.HTTP_METHODS = ["POST", "PUT", "GET"];
  }

  get(url) {
    // ...
  }
}
```

**Good**:

```javascript
class AjaxRequester {
  constructor() {
    this.HTTP_METHODS = ["POST", "PUT", "GET"];
  }

  get(url) {
    // ...
  }

  addHTTPMethod(method) {
    this.HTTP_METHODS.push(method);
  }
}
```

## 里氏替换原则

这个原则听起来有点拗口，不过概念却很好理解。其形式化描述为如果 S 为 T 的子类型，那么类型 T 的实例可以被类型 S 的实例替换而不需要修改任何的代码。形象而言，我们创建的父类与其子类应当可交换地使用而不会引起异常，譬如下文的 Square-Rectangle 这个例子。Square 也是 Rectangle：

**Bad:**

```javascript
class Rectangle {
  constructor() {
    this.width = 0;
    this.height = 0;
  }

  setColor(color) {
    // ...
  }

  render(area) {
    // ...
  }

  setWidth(width) {
    this.width = width;
  }

  setHeight(height) {
    this.height = height;
  }

  getArea() {
    return this.width * this.height;
  }
}

class Square extends Rectangle {
  constructor() {
    super();
  }

  setWidth(width) {
    this.width = width;
    this.height = width;
  }

  setHeight(height) {
    this.width = height;
    this.height = height;
  }
}

function renderLargeRectangles(rectangles) {
  rectangles.forEach(rectangle => {
    rectangle.setWidth(4);
    rectangle.setHeight(5);
    let area = rectangle.getArea(); // BAD: Will return 25 for Square. Should be 20.
    rectangle.render(area);
  });
}

let rectangles = [new Rectangle(), new Rectangle(), new Square()];
renderLargeRectangles(rectangles);
```

**Good**:

```javascript
class Shape {
  constructor() {}

  setColor(color) {
    // ...
  }

  render(area) {
    // ...
  }
}

class Rectangle extends Shape {
  constructor() {
    super();
    this.width = 0;
    this.height = 0;
  }

  setWidth(width) {
    this.width = width;
  }

  setHeight(height) {
    this.height = height;
  }

  getArea() {
    return this.width * this.height;
  }
}

class Square extends Shape {
  constructor() {
    super();
    this.length = 0;
  }

  setLength(length) {
    this.length = length;
  }

  getArea() {
    return this.length * this.length;
  }
}

function renderLargeShapes(shapes) {
  shapes.forEach(shape => {
    switch (shape.constructor.name) {
      case "Square":
        shape.setLength(5);
      case "Rectangle":
        shape.setWidth(4);
        shape.setHeight(5);
    }

    let area = shape.getArea();
    shape.render(area);
  });
}

let shapes = [new Rectangle(), new Rectangle(), new Square()];
renderLargeShapes(shapes);
```

## 接口隔离原则

JavaScript 本身并不包含对于接口语法的支持，因此也无法像其他语言那样达到严格限制的程度。不过鉴于 JavaScript 本身类型系统的缺失，遵循接口隔离原则还是蛮重要的。ISP 的表述为不应该强制客户端去依赖于他们不需要的接口，这一点在 JavaScript 中较为典型的例子就是那些需要大量配置信息的对象。其实使用者并不需要去关心每一个配置项，允许他们动态的设置能够节省大量的时间，代码的可读性也会更好。

**Bad:**

```javascript
class DOMTraverser {
  constructor(settings) {
    this.settings = settings;
    this.setup();
  }

  setup() {
    this.rootNode = this.settings.rootNode;
    this.animationModule.setup();
  }

  traverse() {
    // ...
  }
}

let $ = new DOMTraverser({
  rootNode: document.getElementsByTagName("body"),
  animationModule: function() {} // Most of the time, we won't need to animate when traversing.
  // ...
});
```

**Good**:

```javascript
class DOMTraverser {
  constructor(settings) {
    this.settings = settings;
    this.options = settings.options;
    this.setup();
  }

  setup() {
    this.rootNode = this.settings.rootNode;
    this.setupOptions();
  }

  setupOptions() {
    if (this.options.animationModule) {
      // ...
    }
  }

  traverse() {
    // ...
  }
}

let $ = new DOMTraverser({
  rootNode: document.getElementsByTagName("body"),
  options: {
    animationModule: function() {}
  }
});
```

## 依赖反转原则

This principle states two essential things:

1. High-level modules should not depend on low-level modules. Both should depend on abstractions.
2. Abstractions should not depend upon details. Details should depend on abstractions.

This can be hard to understand at first, but if you've worked with Angular.js, you've seen an implementation of this principle in the form of Dependency Injection (DI). While they are not identical concepts, DIP keeps high-level modules from knowing the details of its low-level modules and setting them up. It can accomplish this through DI. A huge benefit of this is that it reduces the coupling between modules. Coupling is a very bad development pattern because it makes your code hard to refactor.

As stated previously, JavaScript doesn't have interfaces so the abstractions that are depended upon are implicit contracts. That is to say, the methods and properties that an object/class exposes to another object/class. In the example below, the implicit contract is that any Request module for an `InventoryTracker` will have a `requestItems` method.

**Bad:**

```javascript
class InventoryTracker {
  constructor(items) {
    this.items = items;

    // BAD: We have created a dependency on a specific request implementation.
    // We should just have requestItems depend on a request method: `request`
    this.requester = new InventoryRequester();
  }

  requestItems() {
    this.items.forEach(item => {
      this.requester.requestItem(item);
    });
  }
}

class InventoryRequester {
  constructor() {
    this.REQ_METHODS = ["HTTP"];
  }

  requestItem(item) {
    // ...
  }
}

let inventoryTracker = new InventoryTracker(["apples", "bananas"]);
inventoryTracker.requestItems();
```

**Good**:

```javascript
class InventoryTracker {
  constructor(items, requester) {
    this.items = items;
    this.requester = requester;
  }

  requestItems() {
    this.items.forEach(item => {
      this.requester.requestItem(item);
    });
  }
}

class InventoryRequesterV1 {
  constructor() {
    this.REQ_METHODS = ["HTTP"];
  }

  requestItem(item) {
    // ...
  }
}

class InventoryRequesterV2 {
  constructor() {
    this.REQ_METHODS = ["WS"];
  }

  requestItem(item) {
    // ...
  }
}

// By constructing our dependencies externally and injecting them, we can easily
// substitute our request module for a fancy new one that uses WebSockets.
let inventoryTracker = new InventoryTracker(
  ["apples", "bananas"],
  new InventoryRequesterV2()
);
inventoryTracker.requestItems();
```

## 优先选择 ES6 类而不是 ES5 的基本函数定义

传统 ES5 的类实现语法对于类的继承、构建以及方法定义的可读性都不是很好。如果你考虑在类中实现继承，那么建议优先考虑 ES6 的类语法糖。如果你只是需要构建简单的对象，那么可以考虑使用 ES5 的基本函数定义来构造类对象。

**Bad:**

```javascript
var Animal = function(age) {
  if (!(this instanceof Animal)) {
    throw new Error("Instantiate Animal with `new`");
  }

  this.age = age;
};

Animal.prototype.move = function() {};

var Mammal = function(age, furColor) {
  if (!(this instanceof Mammal)) {
    throw new Error("Instantiate Mammal with `new`");
  }

  Animal.call(this, age);
  this.furColor = furColor;
};

Mammal.prototype = Object.create(Animal.prototype);
Mammal.prototype.constructor = Mammal;
Mammal.prototype.liveBirth = function() {};

var Human = function(age, furColor, languageSpoken) {
  if (!(this instanceof Human)) {
    throw new Error("Instantiate Human with `new`");
  }

  Mammal.call(this, age, furColor);
  this.languageSpoken = languageSpoken;
};

Human.prototype = Object.create(Mammal.prototype);
Human.prototype.constructor = Human;
Human.prototype.speak = function() {};
```

**Good:**

```javascript
class Animal {
  constructor(age) {
    this.age = age;
  }

  move() {}
}

class Mammal extends Animal {
  constructor(age, furColor) {
    super(age);
    this.furColor = furColor;
  }

  liveBirth() {}
}

class Human extends Mammal {
  constructor(age, furColor, languageSpoken) {
    super(age, furColor);
    this.languageSpoken = languageSpoken;
  }

  speak() {}
}
```

## Use method chaining

Against the advice of Clean Code, this is one place where we will have to differ. It has been argued that method chaining is unclean and violates the [Law of Demeter](https://en.wikipedia.org/wiki/Law_of_Demeter). Maybe it's true, but this pattern is very useful in JavaScript and you see it in many libraries such as jQuery and Lodash. It allows your code to be expressive, and less verbose. For that reason, I say, use method chaining and take a look at how clean your code will be. In your class functions, simply return `this` at the end of every function, and you can chain further class methods onto it.

**Bad:**

```javascript
class Car {
  constructor() {
    this.make = "Honda";
    this.model = "Accord";
    this.color = "white";
  }

  setMake(make) {
    this.name = name;
  }

  setModel(model) {
    this.model = model;
  }

  setColor(color) {
    this.color = color;
  }

  save() {
    console.log(this.make, this.model, this.color);
  }
}

let car = new Car();
car.setColor("pink");
car.setMake("Ford");
car.setModel("F-150");
car.save();
```

**Good**:

```javascript
class Car {
  constructor() {
    this.make = "Honda";
    this.model = "Accord";
    this.color = "white";
  }

  setMake(make) {
    this.name = name;
    // NOTE: Returning this for chaining
    return this;
  }

  setModel(model) {
    this.model = model;
    // NOTE: Returning this for chaining
    return this;
  }

  setColor(color) {
    this.color = color;
    // NOTE: Returning this for chaining
    return this;
  }

  save() {
    console.log(this.make, this.model, this.color);
  }
}

let car = new Car()
  .setColor("pink")
  .setMake("Ford")
  .setModel("F-150")
  .save();
```

## Prefer composition over inheritance

As stated famously in the [Gang of Four](https://en.wikipedia.org/wiki/Design_Patterns), you should prefer composition over inheritance where you can. There are lots of good reasons to use inheritance and lots of good reasons to use composition. The main point for this maxim is that if your mind instinctively goes for inheritance, try to think if composition could model your problem better. In some cases it can.

You might be wondering then, "when should I use inheritance?" It depends on your problem at hand, but this is a decent list of when inheritance makes more sense than composition:

1. Your inheritance represents an "is-a" relationship and not a "has-a" relationship (Animal->Human vs. User->UserDetails).
2. You can reuse code from the base classes (Humans can move like all animals).
3. You want to make global changes to derived classes by changing a base class. (Change the caloric expenditure of all animals when they move).

**Bad:**

```javascript
class Employee {
  constructor(name, email) {
    this.name = name;
    this.email = email;
  }

  // ...
}

// Bad because Employees "have" tax data. EmployeeTaxData is not a type of Employee
class EmployeeTaxData extends Employee {
  constructor(ssn, salary) {
    super();
    this.ssn = ssn;
    this.salary = salary;
  }

  // ...
}
```

**Good**:

```javascript
class Employee {
  constructor(name, email) {
    this.name = name;
    this.email = email;
  }

  setTaxData(ssn, salary) {
    this.taxData = new EmployeeTaxData(ssn, salary);
  }
  // ...
}

class EmployeeTaxData {
  constructor(ssn, salary) {
    this.ssn = ssn;
    this.salary = salary;
  }

  // ...
}
```

# 测试

测试是代码部署前不可避免的重要步骤，如果你没有添加任何的测试，那么你在每次部署之前你压根不敢确定是否会产生什么意外情况。不同的团队对于测试覆盖率的需求不太一致，不过保持 100% 的覆盖率能够让你的团队对于代码保持较好的掌控与信赖。我们可以使用很多优秀的[测试工具](http://jstherightway.org/#testing-tools)与[测试覆盖率检测工具](http://gotwarlost.github.io/istanbul/)，建议是对于每个新的特征或者模块都添加测试用例。如果更倾向于使用测试驱动开发，一定要注意在你打算添加新的特性或者重构当前代码之前保证测试覆盖率已经达到了预期。

## 每个测试用例单一目标

**Bad:**

```javascript
const assert = require("assert");

describe("MakeMomentJSGreatAgain", function() {
  it("handles date boundaries", function() {
    let date;

    date = new MakeMomentJSGreatAgain("1/1/2015");
    date.addDays(30);
    date.shouldEqual("1/31/2015");

    date = new MakeMomentJSGreatAgain("2/1/2016");
    date.addDays(28);
    assert.equal("02/29/2016", date);

    date = new MakeMomentJSGreatAgain("2/1/2015");
    date.addDays(28);
    assert.equal("03/01/2015", date);
  });
});
```

**Good**:

```javascript
const assert = require("assert");

describe("MakeMomentJSGreatAgain", function() {
  it("handles 30-day months", function() {
    let date = new MakeMomentJSGreatAgain("1/1/2015");
    date.addDays(30);
    date.shouldEqual("1/31/2015");
  });

  it("handles leap year", function() {
    let date = new MakeMomentJSGreatAgain("2/1/2016");
    date.addDays(28);
    assert.equal("02/29/2016", date);
  });

  it("handles non-leap year", function() {
    let date = new MakeMomentJSGreatAgain("2/1/2015");
    date.addDays(28);
    assert.equal("03/01/2015", date);
  });
});
```

# 并发

## 使用 Promise 替代回调

回调含义不清晰，还会导致过深的代码嵌套，就是所谓的回调地狱。在 ES6 中，Promises 已经是内置的全局类型。

**Bad:**

```javascript
require("request").get(
  "https://en.wikipedia.org/wiki/Robert_Cecil_Martin",
  function(err, response) {
    if (err) {
      console.error(err);
    } else {
      require("fs").writeFile("article.html", response.body, function(err) {
        if (err) {
          console.error(err);
        } else {
          console.log("File written");
        }
      });
    }
  }
);
```

**Good**:

```javascript
require("request-promise")
  .get("https://en.wikipedia.org/wiki/Robert_Cecil_Martin")
  .then(function(response) {
    return require("fs-promise").writeFile("article.html", response);
  })
  .then(function() {
    console.log("File written");
  })
  .catch(function(err) {
    console.log(err);
  });
```

## Async/Await 更为清晰

Promises 本身已经是对于回调的不错的替代，而 ES7 中的 async 与 await 则是更为清晰的解决方案，可以避免你编写大量的`then`调用链。

**Bad:**

```javascript
require("request-promise")
  .get("https://en.wikipedia.org/wiki/Robert_Cecil_Martin")
  .then(function(response) {
    return require("fs-promise").writeFile("article.html", response);
  })
  .then(function() {
    console.log("File written");
  })
  .catch(function(err) {
    console.log(err);
  });
```

**Good**:

```javascript
async function getCleanCodeArticle() {
  try {
    var request = await require("request-promise");
    var response = await request.get(
      "https://en.wikipedia.org/wiki/Robert_Cecil_Martin"
    );
    var fileHandle = await require("fs-promise");

    await fileHandle.writeFile("article.html", response);
    console.log("File written");
  } catch (err) {
    console.log(err);
  }
}
```

# 格式化

就像本文的很多建议一样，格式化本身是非常主观的原则。建议是使用[工具](http://standardjs.com/rules.html) 来自动完成格式化操作，而不是争论具体的格式化的细节。

## 相似含义变量的大写一致性

JavaScript 本身是无类型的，因此变量名大写也能传递很多有用的信息。这个规则算是比较主观的，建议团队可以根据自己的内部规范将相同含义变量的大小写保持一致性。

**Bad:**

```javascript
var DAYS_IN_WEEK = 7;
var daysInMonth = 30;

var songs = ["Back In Black", "Stairway to Heaven", "Hey Jude"];
var Artists = ["ACDC", "Led Zeppelin", "The Beatles"];

function eraseDatabase() {}
function restore_database() {}

class animal {}
class Alpaca {}
```

**Good**:

```javascript
var DAYS_IN_WEEK = 7;
var DAYS_IN_MONTH = 30;

var songs = ["Back In Black", "Stairway to Heaven", "Hey Jude"];
var artists = ["ACDC", "Led Zeppelin", "The Beatles"];

function eraseDatabase() {}
function restoreDatabase() {}

class Animal {}
class Alpaca {}
```

## 函数的定义与调用位置尽量靠近

尽量将两个有相互调用关系的函数在源文件的竖直上较为接近的位置，并且将调用者放置于被调用者上方。我们习惯从上至下的阅读代码，这样的布局会提高整个代码的可读性。

**Bad:**

```javascript
class PerformanceReview {
  constructor(employee) {
    this.employee = employee;
  }

  lookupPeers() {
    return db.lookup(this.employee, "peers");
  }

  lookupMananger() {
    return db.lookup(this.employee, "manager");
  }

  getPeerReviews() {
    let peers = this.lookupPeers();
    // ...
  }

  perfReview() {
    getPeerReviews();
    getManagerReview();
    getSelfReview();
  }

  getManagerReview() {
    let manager = this.lookupManager();
  }

  getSelfReview() {
    // ...
  }
}

let review = new PerformanceReview(user);
review.perfReview();
```

**Good**:

```javascript
class PerformanceReview {
  constructor(employee) {
    this.employee = employee;
  }

  perfReview() {
    getPeerReviews();
    getManagerReview();
    getSelfReview();
  }

  getPeerReviews() {
    let peers = this.lookupPeers();
    // ...
  }

  lookupPeers() {
    return db.lookup(this.employee, "peers");
  }

  getManagerReview() {
    let manager = this.lookupManager();
  }

  lookupMananger() {
    return db.lookup(this.employee, "manager");
  }

  getSelfReview() {
    // ...
  }
}

let review = new PerformanceReview(employee);
review.perfReview();
```

# 注释

## 仅仅对业务逻辑进行注释

好的代码应该是见名知义，注释更多的是对于业务逻辑的描述说明。

**Bad:**

```javascript
function hashIt(data) {
  // The hash
  var hash = 0;

  // Length of string
  var length = data.length;

  // Loop through every character in data
  for (var i = 0; i < length; i++) {
    // Get character code.
    var char = data.charCodeAt(i);
    // Make the hash
    hash = (hash << 5) - hash + char;
    // Convert to 32-bit integer
    hash = hash & hash;
  }
}
```

**Good**:

```javascript
function hashIt(data) {
  var hash = 0;
  var length = data.length;

  for (var i = 0; i < length; i++) {
    var char = data.charCodeAt(i);
    hash = (hash << 5) - hash + char;

    // Convert to 32-bit integer
    hash = hash & hash;
  }
}
```

## 避免保留被注释的代码

**Bad:**

```javascript
doStuff();
// doOtherStuff();
// doSomeMoreStuff();
// doSoMuchStuff();
```

**Good**:

```javascript
doStuff();
```

## 不要使用日记形式的注释

千万记住，要使用版本控制工具，而不是在你的代码前面添加日记形式的注释，使用`git log`查看历史记录。

**Bad:**

```javascript
/**
 * 2016-12-20: Removed monads, didn't understand them (RM)
 * 2016-10-01: Improved using special monads (JP)
 * 2016-02-03: Removed type-checking (LI)
 * 2015-03-14: Added combine with type-checking (JR)
 */
function combine(a, b) {
  return a + b;
}
```

**Good**:

```javascript
function combine(a, b) {
  return a + b;
}
```

## 避免额外的代码标记注释

建议是让函数与变量名来表述其功能，避免添加过多额外的注释。

**Bad:**

```javascript
////////////////////////////////////////////////////////////////////////////////
// Scope Model Instantiation
////////////////////////////////////////////////////////////////////////////////
let $scope.model = {
  menu: 'foo',
  nav: 'bar'
};

////////////////////////////////////////////////////////////////////////////////
// Action setup
////////////////////////////////////////////////////////////////////////////////
let actions = function() {
  // ...
}
```

**Good**:

```javascript
let $scope.model = {
  menu: 'foo',
  nav: 'bar'
};

let actions = function() {
  // ...
}
```

## 避免在源文件中添加法律声明

**Bad:**

```javascript
/*
The MIT License (MIT)

Copyright (c) 2016 Ryan McDermott

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE
*/

function calculateBill() {
  // ...
}
```

**Good**:

```javascript
function calculateBill() {
  // ...
}
```

# 错误处理

在 JavaScript 中抛出错误是个不错的实践，不仅可以帮助开发者即时感知程序中出现的错误，还能立刻终止程序执行并且打印出其调用栈。

## 不要忽略被捕获的错误

如果我们只是简单地捕获错误而没有将其反馈给相对应的开发人员或者将该错误记录下来，那么我们进行错误处理就没有什么意义。

**Bad:**

```
try {
  functionThatMightThrow();
} catch (error) {
  console.log(error);
}
```

**Good:**

```
try {
  functionThatMightThrow();
} catch (error) {
  // One option (more noisy than console.log):
  console.error(error);
  // Another option:
  notifyUserOfError(error);
  // Another option:
  reportErrorToService(error);
  // OR do all three!
}
```

## 不要忽略被拒绝的 Promises

**Bad:**

```js
getdata()
  .then(data => {
    functionThatMightThrow(data);
  })
  .catch(error => {
    console.log(error);
  });
```

**Good:**

```js
getdata()
  .then(data => {
    functionThatMightThrow(data);
  })
  .catch(error => {
    // One option (more noisy than console.log):
    console.error(error);
    // Another option:
    notifyUserOfError(error);
    // Another option:
    reportErrorToService(error);
    // OR do all three!
  });
```

> 延伸阅读
>
>[知乎专栏：某熊的全栈之路](https://zhuanlan.zhihu.com/wxyyxc1992)
>[知乎专栏：前端当自强](https://zhuanlan.zhihu.com/c_67532981)
>[知乎专栏：lotuc 的编程之路](https://zhuanlan.zhihu.com/lotuc)
>[2016- 我的技术之路 : 编程知识体系结构](https://zhuanlan.zhihu.com/p/24476917?refer=wxyyxc1992)
>[2016- 我的前端之路 : 工具化与工程化](https://zhuanlan.zhihu.com/p/24575395?refer=wxyyxc1992)
>[某熊周刊系列 : 一周推荐外文技术资料 (12.1)](https://zhuanlan.zhihu.com/p/24516669?refer=wxyyxc1992)
