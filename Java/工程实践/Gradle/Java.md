# Java 应用构建

一个 Plugin 就是一个定义了一系列 Properties 与 Tasks 的集合。

# Java

使用 Java plugin，只需要在 build.gradle 中加入这句话：

```
apply plugin: 'java'
```

### Built-in Tasks

Gradle 和 Maven 一样，采用了“约定优于配置”的方式对 Java project 布局，并且布局方式是和 Maven 一样的，此外，Gradle 还可以方便的自定义布局。在 Gradle 中，一般把这些目录叫做 source set。看下官方的答案：
![gradle source set](http://tech.meituan.com/img/gradle/source_set.png)。
这里要注意，每个 plugin 的 source set 可能都不一样。同样的，Java plugin 还定义好了一堆 task，让我们可以直接使用，比如：clean、test、build 等等。这些 task 都是围绕着 Java plugin 的构建生命周期的：
![](http://tech.meituan.com/img/gradle/javaPluginTasks.png)
图中每一块都是一个 task，箭头表示 task 执行顺序/依赖，比如执行 task jar，那么必须先执行 task compileJava 和 task processResources。另外可以看到，Gradle 的 Java plugin 构建生命周期比较复杂，但是也表明了更加灵活，而且，在项目中，一般只使用其中常用的几个：clean test check build 等等。

gradle 构建过程中，所有的依赖都表现为配置，比如说系统运行时的依赖是 runtime，gradle 里有一个依赖配置叫 runtime，那么系统运行时会加载这个依赖配置以及它的相关依赖。这里说的有点绕，可以简单理解依赖和 maven 类似，只不过 gradle 用 configuration 实现，所以更灵活，有更多选择。下图是依赖配置关系图以及和 task 调用的关系图：
![javaPluginConfigurations](http://tech.meituan.com/img/gradle/javaPluginConfigurations.png)
可以看到，基本和 Maven 是一样的。其实 Gradle 里面这些依赖(scope)都是通过 configuration 来实现的，这里就不细说，有兴趣的可以研究一下官方资料。

### Project Structure

![](https://lippiouyang.gitbooks.io/gradle-in-action-cn/content/images/dag12.png)
现在你可以构建你的项目了，java 插件添加了一个 build 任务到你项目中，build 任务编译你的代码、运行测试然后打包成 jar 文件，所有都是按序执行的。运行 gradle build 之后你的输出应该是类似这样的：

```
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

![](https://lippiouyang.gitbooks.io/gradle-in-action-cn/content/images/dag13.png)![](https://lippiouyang.gitbooks.io/gradle-in-action-cn/content/images/dag14.png)

在项目的根目录你可以找到一个 build 目录，这里包含了所有的输出，包含 class 文件，测试报告，打包的 jar 文件，以及一些用来归档的临时文件。如果你之前使用过 maven,它的标准输出是 target，这两个结构应该很类似。jar 文件目录 build/libs 下可以直接运行，jar 文件的名称直接由项目名称得来的，这里是 todo-app。

### 自定义属性

Java 插件是一个非常固执的框架，对于项目很多的方面它都假定有默认值，比如项目布局，如果你看待世界的方法是不一样的，Gradle 给你提供了一个自定义约定的选项。想知道哪些东西是可以配置的？可以参考这个手册：http://www.gradle.org/docs/current/dsl/，之前提到过，运行命令行gradle properties 可以列出可配置的标准和插件属性以及他们的默认值。

#### 修改项目与插件属性

```
//Identifies project’sversion through a number scheme
version = 0.1

//Sets Java version compilation compatibility to 1.6
sourceCompatibility = 1.6

//Adds Main-Class header to JAR file’s manifest

jar {
    manifest {
        attributes 'Main-Class': 'com.manning.gia.todo.ToDoApp'
    }
}
```

打包成 JAR 之后，你会发现 JAR 文件的名称变成了 todo-app-0.1.jar，这个 jar 包含了 main-class 首部，你就可以通过命令 java -jar build/libs/todo-app-0.1.jar 运行了。

#### 项目布局

```
//Replaces conventional source code directory with list of different directories

sourceSets {
    main {
        java {
            srcDirs = ['src']
        }
    }
//Replaces conventional test source code directory with list of different directories

    test {
        java {
            srcDirs = ['test']
            }
        }
}

//Changes project output property to directory out

buildDir = 'out'
```

## Web Application

# publish(发布)

## Maven Central

Gradle 构建的项目，发布到仓库中，也非常容易：

```
apply plugin: 'maven'

uploadArchives {
    repositories {
        ivy {
            credentials {
                username "username"
                password "pw"
            }
            url "http://repo.mycompany.com"
        }
    }
}
```

# 多项目结构

每一个活跃的项目会随着时间慢慢增长的，一开始可能只是个很小的项目到后面可能包含很多包和类。为了提高可维护性和解藕的目的，你可能想把项目根据逻辑和功能来划分成一个个模块。模块通常按照等级来组织，相互之间可以定义依赖。Gradle 给项目模块化提供了强大的支持，在 Gradle 中每个模块都是一个项目，我们称之为多项目构建。

# Multiple Projects Structure

在 Gradle 中，使用文件 settings.gradle 定义当前项目的子项目，格式如下所示：

```
include 'sub-project1', 'sub-project2', 'sub-project3'，
```

它表示在当前的项目下建立三个子项目，分别为'sub-project1', 'sub-project2', 'sub-project3'。默认情况下，每个子项目的名称对应着当前操作系统目录下的一个子目录。

当 Gradle 运行时，会根据 settings.gradle 的配置情况，构建一个单根节点的项目树。其中的每个子节点代表一个项目(Project)，每个项目都有一个唯一的路径表示它在当前树中的位置，路径的定义方式类似:

```
Root:<Level1-子节点>:<Level2-子节点>:<Level3-子节点>
```

也可以简写成“:<Level1-子节点>:<Level2-子节点>:<Level3-子节点>”。借助这种路径的定义方式，我们可以在 build.gradle 去访问不同的子项目。另外，对于单项目，实际上是一种特殊的、只存在根节点，没有子节点的项目树。

例如，我们有个产品 A，包括以下几个组件 core，web，mobile。分别代表"核心逻辑"、"网站"、“手机客户端”。 因为每个组件是独立的部分，这个时候最好我们能定义多个子项目，让每个子项目分别管理自己的构建。于是我们可以这样定义 A/settings.gradle

```
include 'core', 'web', 'mobile'
```

按照之前描述的，core 组件对应 A/core 目录，web 组件对应 A/web 目录，mobile 组件对应 A/mobile 目录。接下来，我们就可以在每个组件内部，定义 build.gradle 负责管理当前组件的构建。

Gradle 提供了一个内建的 task 'gradle projects'，可以 帮助我们查看当前项目所包含的子项目，下面让我们看看 gradle projects 的输出结果：

```
$ gradle projects
:projects
------------------------------------------------------------
Root project
------------------------------------------------------------
Root project 'A'
+--- Project ':core'
+--- Project ':mobile'
\--- Project ':web
```

结果一目了然，首先是 Root 级别的项目 A，然后是 A 下面的子项目'core', 'mobile', 'mobile'

最终的文件以及目录结构如下所示：

```
A
   --settings.gradle
   --build.gradle
   --core
     --build.gradle
   --web
      --build.gradle
   --mobile
      --build.gradle
```

如果你不喜欢这种默认的结构，也可以按照如下方式定义子项目的名称和物理目录结构：
include(':core)
project(':core').projectDir = new File(settingsDir, 'core-xxx')

include(':web)
project(':web').projectDir = new File(settingsDir, 'web-xxx')

include(':mobile)
project(':mobile').projectDir = new File(settingsDir, 'mobile-xxx')

在这个例子中，子项目 core 实际上对应的物理目录为 A/core-xxx，web 实际上对应的是 A/web-xxx，mobile 也类似。

虽然我们更改了子项目的物理目录结构，不过由于我们在 build.gradle 中使用的是类似 “ :<SubProject>”的方式访问对应的子项目，所以目录结构的改变，对我们 Gradle 的构建脚本并不会产生影响。

接下来，考虑一个更复杂的情况，随着产品的发展，mobile 这个组件慢慢的划分成了 Android 和 IOS 两个部分，这时我们只需要在目录 A/mobile 下定义新的 settings.gradle，并加入如下部分：
include 'android', 'ios'

现在，mobile 组件下将存在两个新的子项目 "android"和"ios"

于是，这时候'gradle projects'的目录结构就变成
A
--settings.gradle
--core
--build.gradle
--web
--build.gradle
--mobile
--settings.gradle
--ios
--build.gradle
--android
--build.gradle

## Inherited properties and methods

任何定义在顶层项目中的方法或者属性自动地对于子项目是可见的，可以通过这种方式来定义一些公共的配置

## Intellij-Idea Support

笔者一直用 Intellij 作为 J2EE 的开发工具，在配置 Gradle 多项目结构的情况下，为了方便能够动态的导入，譬如在如下项目中：
![](http://i.stack.imgur.com/t4cWT.png)
如果希望在子项目里直接用`import`导入其他依赖项目的类包，可以先为每一个项目添加`idea`插件，譬如：

```
allprojects { apply plugin: "idea" }
```

然后再运行：

```
gradle (cleanIdea) idea
```

最后再打开生成好的 IDEA 的项目。
