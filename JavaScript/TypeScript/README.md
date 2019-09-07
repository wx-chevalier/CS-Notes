[![返回目录](https://i.postimg.cc/KvQbty96/image.png)](https://ngte-pl.gitbook.io/i/javascript)

# TypeScript 语法基础与工程实践

TypeScript 是由 MicroSoft 出品的 JavaScript 超集，它在兼容 JavaScript 的所有特性的基础上，附带了静态类型的支持；TypeScript 还允许我们使用尚未正式发布的 ECMAScript 的语言特性，在编译时进行类似于 Babel 这样的降级转化。JavaScript 本身乃动态类型的语言，即是在运行时才进行类型校验；该特性赋予了其快速原型化的能力，却在构建大型 JavaScript 应用时力有不逮，其无法在编译时帮助规避可能的类型错误，也无法利用自动补全、自动重构等工具特性。TypeScript 的静态类型特性则帮助我们在编译时尽可能规避类型错误，并且 TypeScript 会尽可能地从上下文信息中进行类型推导，以避免像 Java 等静态类型语言中过于冗余的麻烦。

![](https://coding.net/u/hoteam/p/Cache/git/raw/master/2017/8/3/1-brsHVxCkP9_Fi3yxPZ5fYA.png)

目前， Angular 已经使用 TypeScript 重构了代码，另一大前端框架 Vue 的新版本也将使用 TypeScript 进行重构。在可预见的未来，TypeScript 将成为前端开发者必须掌握的开发语言之一。

- 类型检查。TypeScript 会在编译代码时进行严格的静态类型检查，这意味着你可以在编码阶段发现可能存在的隐患，而不必把它们带到线上。

- 语言扩展。TypeScript 会包括来自 ES 6 和未来提案中的特性，比如异步操作和装饰器；也会从其他语言借鉴某些特性，比如接口和抽象类。

- 工具属性。TypeScript 能够编译成标准的 JavaScript，可以在任何浏览器、操作系统上运行，无需任何运行时的额外开销。从这个角度上讲，TypeScript 更像是一个工具，而不是一门独立的语言。

- TypeScript 还可以帮助团队重塑“类型思维”，接口提供方将被迫去思考 API 的边界，他们将从代码的编写者蜕变为代码的设计者。

![mindmap](https://i.postimg.cc/15NwgxZW/Type-Script.png)

# 环境配置

可以参考 [fe-boilerplates](https://github.com/wx-chevalier/fe-boilerplates) 或者 [Backend-Boilerplates](https://github.com/wx-chevalier/Backend-Boilerplates)，如果想了解 TypeScript 在前后端开发中的应用。我们们可以通过 npm 安装 TypeScript 的依赖包：

```sh
# 全局安装
$ npm install -g typescript

# 检测是否安装成功
$ tsc -v
Version 2.8.3
```

TypeScript 源文件一般使用 `.ts` 或者 `.tsx` 为后缀，其并不能直接运行在浏览器中而需要进行编译转化，TypeScript 的官方提供了 `tsc` 命令来进行文件编译：

```sh
$ tsc main.ts

# 同时编译多个文件
$ tsc main.ts worker.ts

# 编译当前目录下的全部 ts 文件，并不会递归编译
$ tsc *.ts

# 启动后台常驻编译程序
$ tsc main.ts --watch
```

在实际的项目中，我们也往往会在项目根目录配置 tsconfig.json 文件，来个性化配置 TypeScript 的编译参数：

```json
{
  "compilerOptions": {
    "outDir": "./dist/es",
    "declarationDir": "./dist/types",
    "target": "es5",
    "module": "commonjs",
    "jsx": "react",
    "downlevelIteration": true,
    "moduleResolution": "node",
    "allowUnreachableCode": true,
    "declaration": true,
    "experimentalDecorators": true,
    "noImplicitAny": true,
    "noImplicitReturns": true,
    "noImplicitThis": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "pretty": true,
    "skipLibCheck": true,
    "sourceMap": true,
    "strictNullChecks": true,
    "suppressImplicitAnyIndexErrors": true,
    "lib": ["dom", "es2015"],
    "baseUrl": "src"
  },
  "include": ["src/**/*", "typings/**/*"]
}
```

也可以使用 [ts-node](https://github.com/TypeStrong/ts-node) 快速地直接运行 TypeScript 文件：

```sh
# Execute a script as you would normally with `node`.
ts-node script.ts

# Starts the TypeScript REPL.
ts-node

# Execute code with TypeScript.
ts-node -e 'console.log("Hello, world!")'

# Execute, and print, code with TypeScript.
ts-node -p '"Hello, world!"'

# Pipe scripts to execute with TypeScript.
echo "console.log('Hello, world!')" | ts-node
```

# 编译配置

在 tsconfig.json 中，我们可以自定义很多的编译配置项，本节我们即讨论某些典型场景下的配置案例。

# 链接

- https://mp.weixin.qq.com/s/yK0ll0MygWrChYNrthTwxw
