依赖管理是 maven 的一大特征，对于一个简单的项目，对依赖的管理并不是什么困难的事，但是如果这个项目依赖的库文件达到几十个甚至于上百个的时候就不是一个简单的问题了。在这个时候 maven 对于依赖管理的作用就显露出来了。下面主要讨论几个方面的内容：传递性依赖，依赖范围，依赖管理，系统依赖，可选依赖

# Dependence Usage(依赖使用)

## 本地依赖

### 安装到本地的 Maven Repo 中

### 使用 system 依赖范围

在下文中会有对于依赖的 scope 的详细解释，这里只需要知道如果将 scope 设置为了 system 即是自动在本地路径中寻找依赖的 Jar 包即可。

```
        <dependency>
			<groupId>dnsns</groupId>
			<artifactId>dnsns</artifactId>
			<version>1.0</version>
			<scope>system</scope>
			<systemPath>${project.basedir}/src/lib/dnsns.jar</systemPath>
		</dependency>
		<dependency>
			<groupId>localedata</groupId>
			<artifactId>localedata</artifactId>
			<version>1.0</version>
			<scope>system</scope>
			<systemPath>${project.basedir}/src/lib/localedata.jar</systemPath>
		</dependency>
		<dependency>
			<groupId>sunjce_provider</groupId>
			<artifactId>sunjce_provider</artifactId>
			<version>1.0</version>
			<scope>system</scope>
			<systemPath>${project.basedir}/src/lib/sunjce_provider.jar</systemPath>
		</dependency>
		<dependency>
			<groupId>sunpkcs11</groupId>
			<artifactId>sunpkcs11</artifactId>
			<version>1.0</version>
			<scope>system</scope>
			<systemPath>${project.basedir}/src/lib/sunpkcs11.jar</systemPath>
		</dependency>


```

# Dependency Mediation(依赖调停)

![](http://www.yiibai.com/uploads/allimg/131228/212042K00-0.jpg)

传递性依赖是在 maven2 中添加的新特征，这个特征的作用就是你不需要考虑你依赖的库文件所需要依赖的库文件，能够将依赖模块的依赖自动的引入。例如我们依赖于 spring 的库文件，但是 spring 本身也有依赖，如果没有传递性依赖那就需要我们了解 spring 项目依赖，然后也要添加到我们的项目中。
由于没有限制依赖的数量，如果出现循环依赖的时候会出现问题，这个时候有两种方式处理，一种是通过 build-helper-maven-plugin 插件来规避，另一种就是重构两个相互依赖的项目。
通过传递性依赖，项目的依赖结构能够很快生成。但是因为这个新的特性会有一些其他的特性被添加进来来限制由于传递性依赖所引入的包。
依赖调节：如果在一个项目里面出现不同的模块，依赖了一个项目的不同版本的时候判断依赖的版本。maven2.0 的时候仅仅支持最近原则也就是在依赖树中的最靠近项目的版本作为依赖版本。到了 maven2.0.9 的时候又提出了一个最先声明原则，也就是在项目中被最早声明的被判断为依赖的版本。

调停什么？谁会有冲突？是版本！同一个 jar 包，又会有不同的版本，因为总是在升级嘛。当你所依赖的 jar 有不同的版本的时候，该选择那一个呢？那位说了，就选最新的呗。好主意，毕竟大部门软件都向下兼容，但这也不一定哦。看看 maven 怎么做。maven 选择最近的一个版本。什么意思？
比如你的 A 项目:
A -> B -> C -> D 2.0 and A -> E -> D 1.0，什么意思？A 依赖 B，。。。一直到依传递依赖到 D2.0。同时 A 还依赖了 E 到一直到 D1.0。这就出现了冲突，你当然也可以再 A 里面配置指定依赖 D2.0。如果你没在 A 的 pom 里面配置指定用哪一个。maven 会替你找一个近的。哪个近？当然是 D1.0，那 build A 的时候就会选择 D1.0。什么原因？这样就好吗？得好好想想？你有什么看法？
有什么好想的？别这么纠结，就指定呗。说的容易，你想想很多时候你是不知道 D 的存在的，它很可能是个很底层的库，你只熟悉和你最近的项目 B 和 E。

# Excluded & Optional Dependencies(排除依赖于可选依赖)

可选依赖使用的情况是对于某个依赖来说系统只有在某个特定的情况下使用到它。例如数据库驱动，有 mysql 的，oracle 的。只有在我们使用到 mysql 的时候才会被使用。# Scope(依赖作用域)

```
    <project>
      ...
      <dependencies>
        <!-- declare the dependency to be set as optional -->
        <dependency>
          <groupId>sample.ProjectA</groupId>
          <artifactId>Project-A</artifactId>
          <version>1.0</version>
          <scope>compile</scope>
         <optional>true</optional> <!-- value will be true or false only -->
       </dependency>
     </dependencies>
   </project>
```

```
  <project>
     ...
      <dependencies>
        <dependency>
          <groupId>sample.ProjectA</groupId>
          <artifactId>Project-A</artifactId>
          <version>1.0</version>
          <scope>compile</scope>
          <exclusions>
           <exclusion>  <!-- declare the exclusion here -->
             <groupId>sample.ProjectB</groupId>
             <artifactId>Project-B</artifactId>
           </exclusion>
         </exclusions>
       </dependency>
     </dependencies>
   </project>
```

# Dependency Scope

maven 有三套 classpath(编译 classpath，运行 classpath，测试 classpath)分别对应构建的三个阶段。依赖范围就是控制依赖与这三套 classpath 的关系。依赖范围有六种：
在 POM 4 中，<dependency>中还引入了<scope>，它主要管理依赖的部署。目前<scope>可以使用 5 个值:  
 _ compile，缺省值，适用于所有阶段，会随着项目一起发布。 compile 是默认的范围；如果没有提供一个范围，那该依赖的范围就是编译范围。编译范围依赖在所有的 classpath 中可用，同时它们也会被打包。  
 _ provided，provided 依赖只有在当 JDK 或者一个容器已提供该依赖之后才使用。例如，如果你开发了一个 web 应用，你可能在编译 classpath 中需要可用的 ServletAPI 来编译一个 servlet，但是你不会想要在打包好的 WAR 中包含这个 ServletAPI；这个 Servlet API JAR 由你的应用服务器或者 servlet 容器提供。已提供范围的依赖在编译 classpath(不是运行时)可用。它们不是传递性的，也不会被打包。 _ runtime， runtime 依赖在运行和测试系统的时候需要，但在编译的时候不需要。比如，你可能在编译的时候只需要 JDBC API JAR，而只有在运行的时候才需要 JDBC 驱动实现。
_ test，只在测试时使用，用于编译和运行测试代码。不会随项目发布。  
 \* system，system 范围依赖与 provided 类似，但是你必须显式的提供一个对于本地系统中 JAR 文件的路径。这么做是为了允许基于本地对象编译，而这些对象是系统类库的一部分。这样的构件应该是一直可用的，Maven 也不会在仓库中去寻找它。如果你将一个依赖范围设置成系统范围，你必须同时提供一个 systemPath 元素。注意该范围是不推荐使用的(你应该一直尽量去从公共或定制的 Maven 仓库中引用依赖)。

```
    <project>
      ...
      <dependencies>
        <dependency>
          <groupId>javax.sql</groupId>
          <artifactId>jdbc-stdext</artifactId>
          <version>2.0</version>
          <scope>system</scope>
          <systemPath>${java.home}/lib/rt.jar</systemPath>
       </dependency>
     </dependencies>
     ...
   </project>
```

# Dependency Management

Maven 使用 dependencyManagement 元素来提供了一种管理依赖版本号的方式。通常会在一个组织或者项目的最顶层的父 POM 中看到 dependencyManagement 元素。使用 pom.xml 中的 dependencyManagement 元素能让所有在子项目中引用一个依赖而不用显式的列出版本号。Maven 会沿着父子层次向上走，直到找到一个拥有 dependencyManagement 元素的项目，然后它就会使用在这个 dependencyManagement 元素中指定的版本号。

例如在父项目里：

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>5.1.2</version>
    </dependency>
    ...
  <dependencies>
</dependencyManagement>
```

然后在子项目里就可以添加 mysql-connector 时可以不指定版本号，例如：

```xml
<dependencies>
  <dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
  </dependency>
</dependencies>
```

同时在 dependenceManagement 种，也可以从外部导入 POM 文件中的依赖项：

```xml
<dependencyManagement>
     <dependencies>
        <dependency>
            <!-- Import dependency management from Spring Boot -->
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>1.3.0.RC1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

# Resources(资源管理)

## Resource Directories：资源文件夹

它对应的配置方式为：

```xml
<project>
 ...
 <build>
   ...
   <resources>
     <resource>
       <directory>[your folder here]</directory>
     </resource>
   </resources>
   ...
 </build>
 ...
</project>
```

对于如下的结构：

```yaml
Project
|-- pom.xml
`-- src
`-- my-resources
```

我们需要在 pom 文件中进行如下配置：

```xml
...
   <resources>
     <resource>
       <directory>src/my-resources</directory>
     </resource>
   </resources>
...
```

譬如如果我们要用 Maven 构建一个 Web 项目，会在 src/main 目录下构建一个

## 选择包含或者忽视文件或者目录

```xml
<project>
  ...
  <name>My Resources Plugin Practice Project</name>
  ...
  <build>
    ...
    <resources>
      <resource>
        <directory>src/my-resources</directory>
        <excludes>
          <exclude>**/*.bmp</exclude>
          <exclude>**/*.jpg</exclude>
          <exclude>**/*.jpeg</exclude>
          <exclude>**/*.gif</exclude>
        </excludes>
      </resource>
      <resource>
        <directory>src/my-resources2</directory>
        <includes>
          <include>**/*.txt</include>
        </includes>
        <excludes>
          <exclude>**/*test*.*</exclude>
        </excludes>
      </resource>
      ...
    </resources>
    ...
  </build>
  ...
</project>
```

## Filter：过滤与内容替换
