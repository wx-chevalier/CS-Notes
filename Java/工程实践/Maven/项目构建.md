
> [Maven 项目构建基础](https://parg.co/bFi)从属于笔者的[现代 Java 开发基础](https://parg.co/bgk)系列文章，介绍了 Maven 的历史背景与多种构建工具对比，以及 Maven 的基本配置安装与使用；本文涉及的参考资料声明在 [Java  Learning & Practices Links](https://parg.co/bgv)以及 [Maven 学习与资料索引](https://parg.co/bFL)。本文整理时间也较早，最近因为整理 Java 相关的资料所以重新归纳了下。

# Maven 

Maven 是功能强大的构建工具能够帮我们自动化构建过程，从清理、编译、测试到生成报告，再到打包和部署。我们只需要输入简单的命令(如 `mvn clean install`)，Maven 就会帮我们处理繁琐的任务；它最大化的消除了构建的重复，抽象了构建生命周期，并且为绝大部分的构建任务提供了已实现的插件。比如说测试，我们只需要遵循 Maven 的约定编写好测试用例，当我们运行构建的时候，这些测试便会自动运行。除此之外，Maven 能帮助我们标准化构建过程。在 Maven 之前，十个项目可能有十种构建方式，但通过 Maven，所有项目的构建命令都是简单一致的。有利于促进项目团队的标准化。

# 构建工具对比

Maven 是笔者接触的第一个脱离于 IDE 的命令行构建工具，笔者之前一直是基于 Visual Studio 下进行 Windows 驱动开发，并不是很能明白 Builder 与 IDE 之间的区别。依赖大量的手工操作。编译、测试、代码生成等工作都是相互独立的，很难一键完成所有工作。手工劳动往往意味着低效，意味着容易出错。很难在项目中统一所有的 IDE 配置，每个人都有自己的喜好。也正是由于这个原因，一个在机器 A 上可以成功运行的任务，到了机器 B 的 IDE 中可能就会失败。

在 Linux C 开发中我们常常使用 Make 进行构建，不过 Make 将自己和操作系统绑定在一起了；也就是说，使用Make，就不能实现(至少很难)跨平台的构建，这对于Java来说是非常不友好的。此外，Makefile 的语法也成问题，很多人抱怨 Make 构建失败的原因往往是一个难以发现的空格或 Tab 使用错误。而在 Java 发展过程中常见的自动化构建工具以 Ant、Maven、Gradle 为代表，整个自动化流程往往包含以下步骤：编译源代码、运行单元测试和集成测试、执行静态代码分析、生成分析报告、创建发布版本、部署到目标环境、部署传递过程以及执行冒烟测试和自动功能测试。

和 Make 一样，Ant 也都是过程式的，开发者显式地指定每一个目标，以及完成该目标所需要执行的任务。针对每一个项目，开发者都需要重新编写这一过程，这里其实隐含着很大的重复。Maven 是声明式的，项目构建过程和过程各个阶段所需的工作都由插件实现，并且大部分插件都是现成的，开发者只需要声明项目的基本元素，Maven 就执行内置的、完整的构建过程。这在很大程度上消除了重复。

此外，Ant 是没有依赖管理的，所以很长一段时间 Ant 用户都不得不手工管理依赖，这是一个令人头疼的问题。幸运的是，Ant 用户现在可以借助 Ivy 管理依赖。而对于 Maven 用户来说，依赖管理是理所当然的，Maven 不仅内置了依赖管理，更有一个可能拥有全世界最多 Java 开源软件包的中央仓库，Maven 用户无须进行任何配置就可以直接享用。

而 Gradle 抛弃了 Maven 的基于 XML 的繁琐配置；众所周知 XML 的阅读体验比较差，对于机器来说虽然容易识别，但毕竟是由人去维护的。取而代之的是 Gradle 采用了领域特定语言 Groovy 的配置，大大简化了构建代码的行数。Maven 的设计核心 Convention Over Configuration 被 Gradle 更加发扬光大，而 Gradle 的配置即代码又超越了Maven。在 Gradle 中任何配置都可以作为代码被执行的，我们也可以随时使用已有的 Ant 脚本(Ant task 是 Gradle 中的一等公民)、Java 类库、Groovy 类库来辅助完成构建任务的编写。在[现代 Java 开发基础](https://parg.co/bgk)系列文章中也有专门的章节讲解 Gradle，笔者在 Android 与 Spring 项目构建中也会优先选择 Gradle。


# 环境配置

Maven 的安装也非常方便，可从 Apache 官方下载最新的 Maven 压缩包然后解压，也可以使用 [SDK Man](http://sdkman.io/) 执行安装；如果是手动配置的话我们还需要配置设置下系统的环境变量：

- M2HOME: 指向Maven安装目录
- Path: 追加 Maven 安装目录下的 bin 目录

在用户目录下，我们可以发现 .m2 文件夹。默认情况下，该文件夹下放置了 Maven 本地仓库 .m2/repository。所有的 Maven 构件(artifact)都被存储到该仓库中，以方便重用。默认情况下，~/.m2 目录下除了 repository 仓库之外就没有其他目录和文件了，不过大多数 Maven 用户需要复制 M2HOME/conf/settings.xml 文件到 ~/.m2/settings.xml。

部分常用的Maven命令如下：

```
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
或者编辑 ~/.m2/settings.xml 文件(如果没有该文件，则复制 $M2HOME/conf/settings.xml)，添加代理配置如下：

``` xml
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

如果不行试试重启机器或者eclipse等ide还不行试试下面这种方式：windows-->preferences-->maven-->installations  add

![maven config](http://outofmemory.cn/ugc/upload/00/20/20130620/maven-config.png)

这样配置后将使用指定目录下的maven，而非eclipse的maven内置插件。


## 其他错误处理

(1)有时候执行```mvn compile```时候会爆出无法找到junit的错误，可能的解决方法有：

- 在Eclipse的Projects选项中使用Projects Clean
  
- 在pom.xml中引入junit依赖项，并且保证其scope为compile:
  
  ``` 
  <dependency>
  	<groupId>junit</groupId>
  	<artifactId>junit</artifactId>
  	<version>4.11</version>
  	<scope>test</scope>
  </dependency>
  ```

(2)有时候在Eclipse下执行`mvn compile`或者相关命令时，会报某某文件出现不识别字符或者非UTF-8编码，此时可以做几步检查：

- 检查对应的Java文件是否有Bom头
- 检查对应的Java文件的编码
- 如果都没有问题，在Eclipse中先将文件编码设置为GBK，再改回UTF-8试试。


# 项目配置

就像 Make 的 Makefile，Ant 的 build.xml 一样，Maven 项目的核心是 pom.xml。首先创建一个名为 hello-world 的文件夹，打开该文件夹，新建一个名为 pom.xml 的文件，输入其内容如下：

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
http://maven.apache.org/maven-v4_0_0.xsd">
<modelVersion>4.0.0</modelVersion>
<groupId>com.wx.mvn</groupId>
<artifactId>hello-world</artifactId>
<version>1.0-SNAPSHOT</version>
<name>Maven Hello World Project</name>
</project>
```

## 文件结构

- 代码的第一行是 XML 头，指定了该 xml 文档的版本和编码方式。紧接着是 project 元素，project 是所有 pom.xml 的根元素，它还声明了一些 POM 相关的命名空间及 xsd 元素，虽然这些属性不是必须的，但使用这些属性能够让第三方工具(如 IDE 中的 XML 编辑器)帮助我们快速编辑 POM。


- 根元素下的第一个子元素 modelVersion 指定了当前 POM 模型的版本，对于 Maven 2 及 Maven 3 来说，它只能是4.0.0。这段代码中最重要的是 groupId，artifactId 和 version 三行。这三个元素定义了一个项目基本的坐标，在Maven 的世界，任何的 jar、pom 或者 war 都是以基于这些基本的坐标进行区分的。


- groupId 定义了项目属于哪个组，这个组往往和项目所在的组织或公司存在关联，譬如你在 googlecode 上建立了一个名为 myapp 的项目，那么 groupId 就应该是 com.googlecode.myapp，如果你的公司是 mycom，有一个项目为 myapp，那么 groupId 就应该是 com.mycom.myapp。


- artifactId 定义了当前 Maven 项目在组中唯一的 ID，我们为这个 Hello World 项目定义 artifactId 为 hello-world，本书其他章节代码会被分配其他的 artifactId。而在前面的 groupId 为 com.googlecode.myapp 的例子中，你可能会为不同的子项目(模块)分配 artifactId，如：myapp-util、myapp-domain、myapp-web 等等。


- version 指定了 Hello World 项目当前的版本——1.0-SNAPSHOT。SNAPSHOT 意为快照，说明该项目还处于开发中，是不稳定的版本。随着项目的发展，version 会不断更新，如升级为 1.0、1.1-SNAPSHOT、1.1、2.0 等等。

- 最后一个 name 元素声明了一个对于用户更为友好的项目名称，虽然这不是必须的，但我还是推荐为每个 POM 声明 name，以方便信息交流。 没有任何实际的 Java 代码，我们就能够定义一个 Maven 项目的 POM，这体现了 Maven 的一大优点，它能让项目对象模型最大程度地与实际代码相独立，我们可以称之为解耦，或者正交性，这在很大程度上避免了 Java 代码和 POM 代码的相互影响。比如当项目需要升级版本时，只需要修改 POM，而不需要更改 Java 代码；而在 POM 稳定之后，日常的 Java 代码开发工作基本不涉及 POM 的修改。


## 变量替换

在 pom.xml 定义 properties 标签
```
<properties>
 <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
 <spring.version>1.2.6</spring.version>
 <developer.organization><![CDATA[xy公司]]></developer.organization>
</properties>
```
以上内容就改成了
```
<dependency>
 <groupId>org.springframework</groupId>
 <artifactId>spring-core</artifactId>
 <version>${spring.version}</version>
</dependency>
<dependency>
 <groupId>org.springframework</groupId>
 <artifactId>spring-aop</artifactId>
 <version>${spring.version}</version>
</dependency>
```
也可以使用 [maven-properties](http://www.mojohaus.org/properties-maven-plugin/plugin-info.html)  插件来支持外部变量

## 目录结构

项目主代码和测试代码不同，项目的主代码会被打包到最终的构件中(比如 jar)，而测试代码只在运行测试时用到，不会被打包。默认情况下，Maven 假设项目主代码位于 `src/main/java` 目录，我们遵循 Maven 的约定，创建该目录，然后在该目录下创建文件 `com/wx/mvn/helloworld/HelloWorld.java`，其内容如下:

``` java
package com.wx.mvn.helloworld;

public class HelloWorld
{
    public String sayHello()
    {
        return "Hello Maven";
    }

    public static void main(String[] args)
    {
        System.out.print( new HelloWorld().sayHello() );
    }
}
```

关于该 Java 代码有两点需要注意。首先，大部分情况下我们应该把项目主代码放到 src/main/java/ 目录下(遵循Maven的约定)，而无须额外的配置，Maven 会自动搜寻该目录找到项目主代码。其次，该 Java 类的包名是 com.wx.mvn.helloworld，这与我们之前在 POM 中定义的 groupId 和 artifactId 相吻合。一般来说，项目中 Java 类的包都应该基于项目的 groupId 和 artifactId，这样更加清晰，更加符合逻辑，也方便搜索构件或者 Java 类。 代码编写完毕后，我们使用 Maven 进行编译，在项目根目录下运行命令 `mvn clean compile` 即可。Maven 首先执行了clean:clean 任务，删除 target/ 目录，默认情况下 Maven 构建的所有输出都在 target/ 目录中；接着执行 resources:resources 任务(未定义项目资源，暂且略过)；最后执行 compiler:compile 任务，将项目主代码编译至 target/classes 目录(编译好的类为 com/wx/mvn/helloworld/HelloWorld.Class)。

## 仓库配置

![](http://hengyunabc.github.io/img/maven-repositories.png)

下面介绍一些 Maven 仓库工作的原理。典型的一个 Maven依赖下会有这三个文件：
```
maven-metadata.xml
maven-metadata.xml.md5
maven-metadata.xml.sha1
```
maven-metadata.xml里面记录了最后deploy的版本和时间。
```
<?xml version="1.0" encoding="UTF-8"?>
<metadata modelVersion="1.1.0">
  <groupId>io.github.hengyunabc</groupId>
  <artifactId>mybatis-ehcache-spring</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <versioning>
    <snapshot>
      <timestamp>20150804.095005</timestamp>
      <buildNumber>1</buildNumber>
    </snapshot>
    <lastUpdated>20150804095005</lastUpdated>
  </versioning>
</metadata>
```
其中 md5, sha1 校验文件是用来保证这个 meta 文件的完整性。Maven 在编绎项目时，会先尝试请求 maven-metadata.xml，如果没有找到，则会直接尝试请求到jar文件，在下载 jar 文件时也会尝试下载 jar 的 md5, sha1 文件。Maven 的 repository 并没有优先级的配置，也不能单独为某些依赖配置 repository。所以如果项目配置了多个repository，在首次编绎时，会依次尝试下载依赖。如果没有找到，尝试下一个，整个流程会很长。所以尽量多个依赖放同一个仓库，不要每个项目都有一个自己的仓库。如果想要使用本地file仓库里，在项目的pom.xml里配置，如：
```
<repositories>
       <repository>
           <id>hengyunabc-maven-repo</id>
           <url>file:/home/hengyunabc/code/maven-repo/repository/</url>
       </repository>
</repositories>
```
