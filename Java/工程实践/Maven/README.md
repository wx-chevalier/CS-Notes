# Maven

Maven 是功能强大的构建工具能够帮我们自动化构建过程，从清理、编译、测试到生成报告，再到打包和部署。我们只需要输入简单的命令(如 `mvn clean install`)，Maven 就会帮我们处理繁琐的任务；它最大化的消除了构建的重复，抽象了构建生命周期，并且为绝大部分的构建任务提供了已实现的插件。比如说测试，我们只需要遵循 Maven 的约定编写好测试用例，当我们运行构建的时候，这些测试便会自动运行。除此之外，Maven 能帮助我们标准化构建过程。在 Maven 之前，十个项目可能有十种构建方式，但通过 Maven，所有项目的构建命令都是简单一致的。有利于促进项目团队的标准化。

# 构建工具对比

在 Linux C 开发中我们常常使用 Make 进行构建，不过 Make 将自己和操作系统绑定在一起了；也就是说，使用 Make，就不能实现(至少很难)跨平台的构建，这对于 Java 来说是非常不友好的。此外，Makefile 的语法也成问题，很多人抱怨 Make 构建失败的原因往往是一个难以发现的空格或 Tab 使用错误。而在 Java 发展过程中常见的自动化构建工具以 Ant、Maven、Gradle 为代表，整个自动化流程往往包含以下步骤：编译源代码、运行单元测试和集成测试、执行静态代码分析、生成分析报告、创建发布版本、部署到目标环境、部署传递过程以及执行冒烟测试和自动功能测试。

和 Make 一样，Ant 也都是过程式的，开发者显式地指定每一个目标，以及完成该目标所需要执行的任务。针对每一个项目，开发者都需要重新编写这一过程，这里其实隐含着很大的重复。Maven 是声明式的，项目构建过程和过程各个阶段所需的工作都由插件实现，并且大部分插件都是现成的，开发者只需要声明项目的基本元素，Maven 就执行内置的、完整的构建过程。这在很大程度上消除了重复。

此外，Ant 是没有依赖管理的，所以很长一段时间 Ant 用户都不得不手工管理依赖，这是一个令人头疼的问题。幸运的是，Ant 用户现在可以借助 Ivy 管理依赖。而对于 Maven 用户来说，依赖管理是理所当然的，Maven 不仅内置了依赖管理，更有一个可能拥有全世界最多 Java 开源软件包的中央仓库，Maven 用户无须进行任何配置就可以直接享用。

而 Gradle 抛弃了 Maven 的基于 XML 的繁琐配置；众所周知 XML 的阅读体验比较差，对于机器来说虽然容易识别，但毕竟是由人去维护的。取而代之的是 Gradle 采用了领域特定语言 Groovy 的配置，大大简化了构建代码的行数。Maven 的设计核心 Convention Over Configuration 被 Gradle 更加发扬光大，而 Gradle 的配置即代码又超越了 Maven。在 Gradle 中任何配置都可以作为代码被执行的，我们也可以随时使用已有的 Ant 脚本(Ant task 是 Gradle 中的一等公民)、Java 类库、Groovy 类库来辅助完成构建任务的编写。在[现代 Java 开发基础](https://parg.co/bgk)系列文章中也有专门的章节讲解 Gradle，笔者在 Android 与 Spring 项目构建中也会优先选择 Gradle。

# 安装与配置

Maven 的安装也非常方便，可从 Apache 官方下载最新的 Maven 压缩包然后解压，也可以使用 [SDK Man](http://sdkman.io/) 执行安装；如果是手动配置的话我们还需要配置设置下系统的环境变量：

- M2HOME: 指向 Maven 安装目录
- Path: 追加 Maven 安装目录下的 bin 目录

在用户目录下，我们可以发现 .m2 文件夹。默认情况下，该文件夹下放置了 Maven 本地仓库 .m2/repository。所有的 Maven 构件(artifact)都被存储到该仓库中，以方便重用。默认情况下，~/.m2 目录下除了 repository 仓库之外就没有其他目录和文件了，不过大多数 Maven 用户需要复制 M2HOME/conf/settings.xml 文件到 ~/.m2/settings.xml。

部分常用的 Maven 命令如下：

```sh
mvn -v # 查看maven版本

mvn compile # 编译

mvn test # 测试

mvn package # 打包

mvn clean # 删除 target

mvn install # 安装jar包到本地仓库中

mvn archetype:generate -DgroupId=co.hoteam -DartifactId=Zigbee -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false # 创建一个新工程
```

## 网络代理

众所周知的原因，国内有时候并不能够很顺畅的访问 Maven 的中央仓库，往往我们需要访问国内的镜像地址：

```
<mirror>
<id>CN</id>
<name>OSChina Central</name>
<url>http://maven.oschina.net/content/groups/public/</url>
<mirrorOf>central</mirrorOf>
</mirror>
```

或者编辑 ~/.m2/settings.xml 文件(如果没有该文件，则复制 \$M2HOME/conf/settings.xml)，添加代理配置如下：

```xml
<settings>
  ...
<pqroxies>
  <proxy>
    <id>my-proxy</id>
    <active>true</active>
    <protocol>http</protocol>
    <host>代理服务器主机名</host>
    <port>端口号</port>
    <!--
        <username>***</username>
        <password>***</password>
        <nonProxyHosts>repository.mycom.com|*.google.com</nonProxyHosts>
-->
  </proxy>
  </proxies>
  ...
</settings>
```

如果不行试试重启机器或者 eclipse 等 ide 还不行试试下面这种方式：windows-->preferences-->maven-->installations add

![maven config](http://outofmemory.cn/ugc/upload/00/20/20130620/maven-config.png)

这样配置后将使用指定目录下的 maven，而非 eclipse 的 maven 内置插件。
