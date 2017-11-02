
依赖管理是maven的一大特征，对于一个简单的项目，对依赖的管理并不是什么困难的事，但是如果这个项目依赖的库文件达到几十个甚至于上百个的时候就不是一个简单的问题了。在这个时候maven对于依赖管理的作用就显露出来了。下面主要讨论几个方面的内容：传递性依赖，依赖范围，依赖管理，系统依赖，可选依赖
# Dependence Usage(依赖使用)
## 本地依赖
### 安装到本地的Maven Repo中
### 使用system依赖范围 
在下文中会有对于依赖的scope的详细解释，这里只需要知道如果将scope设置为了system即是自动在本地路径中寻找依赖的Jar包即可。
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

传递性依赖是在maven2中添加的新特征，这个特征的作用就是你不需要考虑你依赖的库文件所需要依赖的库文件，能够将依赖模块的依赖自动的引入。例如我们依赖于spring的库文件，但是spring本身也有依赖，如果没有传递性依赖那就需要我们了解spring项目依赖，然后也要添加到我们的项目中。
  由于没有限制依赖的数量，如果出现循环依赖的时候会出现问题，这个时候有两种方式处理，一种是通过build-helper-maven-plugin插件来规避，另一种就是重构两个相互依赖的项目。
  通过传递性依赖，项目的依赖结构能够很快生成。但是因为这个新的特性会有一些其他的特性被添加进来来限制由于传递性依赖所引入的包。
  依赖调节：如果在一个项目里面出现不同的模块，依赖了一个项目的不同版本的时候判断依赖的版本。maven2.0的时候仅仅支持最近原则也就是在依赖树中的最靠近项目的版本作为依赖版本。到了maven2.0.9的时候又提出了一个最先声明原则，也就是在项目中被最早声明的被判断为依赖的版本。


调停什么？谁会有冲突？是版本！同一个jar包，又会有不同的版本，因为总是在升级嘛。当你所依赖的jar有不同的版本的时候，该选择那一个呢？那位说了，就选最新的呗。好主意，毕竟大部门软件都向下兼容，但这也不一定哦。看看maven怎么做。maven选择最近的一个版本。什么意思？ 
比如你的A项目： 
A -> B -> C -> D 2.0 and A -> E -> D 1.0，什么意思？A依赖B，。。。一直到依传递依赖到D2.0。同时A还依赖了E到一直到D1.0。这就出现了冲突，你当然也可以再A里面配置指定依赖D2.0。如果你没在A的pom里面配置指定用哪一个。maven会替你找一个近的。哪个近？当然是D1.0，那build A的时候就会选择D1.0。什么原因？这样就好吗？得好好想想？你有什么看法？ 
有什么好想的？别这么纠结，就指定呗。说的容易，你想想很多时候你是不知道D的存在的，它很可能是个很底层的库，你只熟悉和你最近的项目B和E。
# Excluded & Optional Dependencies(排除依赖于可选依赖)
可选依赖使用的情况是对于某个依赖来说系统只有在某个特定的情况下使用到它。例如数据库驱动，有mysql的，oracle的。只有在我们使用到mysql的时候才会被使用。# Scope(依赖作用域)
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
maven有三套classpath（编译classpath，运行classpath，测试classpath）分别对应构建的三个阶段。依赖范围就是控制依赖与这三套classpath的关系。依赖范围有六种：
在POM 4中，<dependency>中还引入了<scope>，它主要管理依赖的部署。目前<scope>可以使用5个值：   
    * compile，缺省值，适用于所有阶段，会随着项目一起发布。  compile 是默认的范围；如果没有提供一个范围，那该依赖的范围就是编译范围。编译范围依赖在所有的classpath 中可用，同时它们也会被打包。  
    * provided，provided 依赖只有在当JDK 或者一个容器已提供该依赖之后才使用。例如，如果你开发了一个web 应用，你可能在编译classpath 中需要可用的ServletAPI 来编译一个servlet，但是你不会想要在打包好的WAR中包含这个ServletAPI；这个Servlet API JAR 由你的应用服务器或者servlet 容器提供。已提供范围的依赖在编译classpath（不是运行时）可用。它们不是传递性的，也不会被打包。    * runtime， runtime 依赖在运行和测试系统的时候需要，但在编译的时候不需要。比如，你可能在编译的时候只需要JDBC API JAR，而只有在运行的时候才需要JDBC驱动实现。
    * test，只在测试时使用，用于编译和运行测试代码。不会随项目发布。    
    * system，system 范围依赖与provided 类似，但是你必须显式的提供一个对于本地系统中JAR 文件的路径。这么做是为了允许基于本地对象编译，而这些对象是系统类库的一部分。这样的构件应该是一直可用的，Maven 也不会在仓库中去寻找它。如果你将一个依赖范围设置成系统范围，你必须同时提供一个systemPath元素。注意该范围是不推荐使用的（你应该一直尽量去从公共或定制的Maven仓库中引用依赖）。
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

Maven 使用dependencyManagement 元素来提供了一种管理依赖版本号的方式。通常会在一个组织或者项目的最顶层的父POM 中看到dependencyManagement 元素。使用pom.xml 中的dependencyManagement 元素能让所有在子项目中引用一个依赖而不用显式的列出版本号。Maven 会沿着父子层次向上走，直到找到一个拥有dependencyManagement 元素的项目，然后它就会使用在这个dependencyManagement 元素中指定的版本号。

例如在父项目里：

``` xml
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

然后在子项目里就可以添加mysql-connector时可以不指定版本号，例如：

``` xml
<dependencies>  
  <dependency>  
    <groupId>mysql</groupId>  
    <artifactId>mysql-connector-java</artifactId>  
  </dependency>  
</dependencies>  
```

同时在dependenceManagement种，也可以从外部导入POM文件中的依赖项：

``` xml
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
[toc]

# Resources(资源管理)

## Resource Directories：资源文件夹

它对应的配置方式为：

``` xml
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

``` yaml
Project
|-- pom.xml
`-- src
    `-- my-resources
```

我们需要在pom文件中进行如下配置：

``` xml
...
   <resources>
     <resource>
       <directory>src/my-resources</directory>
     </resource>
   </resources>
...
```



譬如如果我们要用Maven构建一个Web项目，会在src/main目录下构建一个

## 选择包含或者忽视文件或者目录

``` xml
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