# Java 应用构建

Java 中所谓的 Plugin 就是一个定义了一系列 Properties 与 Tasks 的集合。如果希望使用 Java plugin，只需要在 build.gradle 中加入这句话：

```groovy
apply plugin: 'java'
```

Gradle 和 Maven 一样，采用了“约定优于配置”的方式对 Java project 布局，并且布局方式是和 Maven 一样的，此外，Gradle 还可以方便的自定义布局。在 Gradle 中，一般把这些目录叫做 source set。看下官方的答案：

![gradle source set](http://tech.meituan.com/img/gradle/source_set.png)。

这里要注意，每个 plugin 的 source set 可能都不一样。同样的，Java plugin 还定义好了一堆 task，让我们可以直接使用，比如：clean、test、build 等等。这些 task 都是围绕着 Java plugin 的构建生命周期的：

![](http://tech.meituan.com/img/gradle/javaPluginTasks.png)

图中每一块都是一个 task，箭头表示 task 执行顺序/依赖，比如执行 task jar，那么必须先执行 task compileJava 和 task processResources。另外可以看到，Gradle 的 Java plugin 构建生命周期比较复杂，但是也表明了更加灵活，而且，在项目中，一般只使用其中常用的几个：clean test check build 等等。

gradle 构建过程中，所有的依赖都表现为配置，比如说系统运行时的依赖是 runtime，gradle 里有一个依赖配置叫 runtime，那么系统运行时会加载这个依赖配置以及它的相关依赖。这里说的有点绕，可以简单理解依赖和 maven 类似，只不过 gradle 用 configuration 实现，所以更灵活，有更多选择。下图是依赖配置关系图以及和 task 调用的关系图：

![javaPluginConfigurations](http://tech.meituan.com/img/gradle/javaPluginConfigurations.png)

可以看到，基本和 Maven 是一样的。其实 Gradle 里面这些依赖(scope)都是通过 configuration 来实现的。

# 自定义配置

![](https://lippiouyang.gitbooks.io/gradle-in-action-cn/content/images/dag12.png)

现在你可以构建你的项目了，java 插件添加了一个 build 任务到你项目中，build 任务编译你的代码、运行测试然后打包成 jar 文件，所有都是按序执行的。运行 gradle build 之后你的输出应该是类似这样的：

```sh
$ gradle build
:compileJava
:processResources UP-TO-DATE
:classes
:jar
:assemble
:compileTestJava UP-TO-DATE
:processTestResources UP-TO-DATE
:testClasses UP-TO-DATE
:test
:check
:build
```

输出的每一行都表示一个可执行的任务，你可能注意到有一些任务标记为 UP_TO-DATE,这意味着这些任务被跳过了，gradle 能够自动检查哪些部分没有发生改变，就把这部分标记下来，省的重复执行。在大型的企业项目中可以节省不少时间。执行完 gradle build 之后项目结构应该是类似这样的：

![](https://lippiouyang.gitbooks.io/gradle-in-action-cn/content/images/dag13.png)

![](https://lippiouyang.gitbooks.io/gradle-in-action-cn/content/images/dag14.png)

在项目的根目录你可以找到一个 build 目录，这里包含了所有的输出，包含 class 文件，测试报告，打包的 jar 文件，以及一些用来归档的临时文件。如果你之前使用过 maven，它的标准输出是 target，这两个结构应该很类似。jar 文件目录 build/libs 下可以直接运行，jar 文件的名称直接由项目名称得来的。

## 自定义属性

Java 插件是一个非常固执的框架，对于项目很多的方面它都假定有默认值，比如项目布局，如果你看待世界的方法是不一样的，Gradle 给你提供了一个自定义约定的选项。想知道哪些东西是可以配置的？可以参考这个手册：http://www.gradle.org/docs/current/dsl/，之前提到过，运行命令行 gradle properties 可以列出可配置的标准和插件属性以及他们的默认值。

```groovy
// Identifies project’sversion through a number scheme
version = 0.1

// Sets Java version compilation compatibility to 1.6
sourceCompatibility = 1.6

// Adds Main-Class header to JAR file’s manifest
jar {
    manifest {
        attributes 'Main-Class': 'com.manning.gia.todo.ToDoApp'
    }
}
```

打包成 JAR 之后，你会发现 JAR 文件的名称变成了 todo-app-0.1.jar，这个 jar 包含了 main-class 首部，你就可以通过命令 java -jar build/libs/todo-app-0.1.jar 运行了。

## 代码目录

```groovy
// Replaces conventional source code directory with list of different directories
sourceSets {
    main {
        java {
            srcDirs = ['src']
        }
    }
    // Replaces conventional test source code directory with list of different directories
    test {
        java {
            srcDirs = ['test']
        }
    }
}

// Changes project output property to directory out
buildDir = 'out'
```

# 链接

- https://tech.meituan.com/2014/08/18/gradle-practice.html
