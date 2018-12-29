
# Run

## Main

如果需要在Maven中直接运行某个类中的Main方法：

``` 
mvn exec:java -Dexec.mainClass="com.example.Main"
```

如果是经常使用的话，可以在pom文件中添加如下的配置：

``` xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>1.2.1</version>
  <executions>
    <execution>
      <goals>
        <goal>java</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <mainClass>com.example.Main</mainClass>
    <arguments>
      <argument>foo</argument>
      <argument>bar</argument>
    </arguments>
  </configuration>
</plugin>
```



### Jetty-Plugin

#### Scan(文件扫描)

``` xml
<project>
... 
  <plugins>
... 
    <plugin>
      <groupId>org.eclipse.jetty</groupId>
      <artifactId>jetty-maven-plugin</artifactId>
      <version>9.3.1-SNAPSHOT</version>
      <configuration>
        <webAppSourceDirectory>${project.basedir}/src/staticfiles</webAppSourceDirectory>
        <webApp>
          <contextPath>/</contextPath>
          <descriptor>${project.basedir}/src/over/here/web.xml</descriptor>
          <jettyEnvXml>${project.basedir}/src/over/here/jetty-env.xml</jettyEnvXml>
        </webApp>
        <classesDirectory>${project.basedir}/somewhere/else</classesDirectory>
        <scanClassesPattern>
          <excludes>
             <exclude>**/Foo.class</exclude>
          </excludes>
        </scanClassesPattern>
        <scanTargets>
          <scanTarget>src/mydir</scanTarget>
          <scanTarget>src/myfile.txt</scanTarget>
        </scanTargets>
        <scanTargetPatterns>
          <scanTargetPattern>
            <directory>src/other-resources</directory>
            <includes>
              <include>**/*.xml</include>
              <include>**/*.properties</include>
            </includes>
            <excludes>
              <exclude>**/myspecial.xml</exclude>
              <exclude>**/myspecial.properties</exclude>
            </excludes>
          </scanTargetPattern>
        </scanTargetPatterns>
      </configuration>
    </plugin>
  </plugins>
</project>
```

#### Port(监听端口)

在运行Jetty时往往需要改变其监听的端口，主要就是修正HttpConnector的参数来建立一些ServerConnector的配置，主要有如下的三种方式：

- Change the port when just at runtime:
  
  ``` 
  mvn jetty:run -Djetty.http.port=9999
  
  ```
  
- Set the property inside your *pom.xml* file:
  
  ``` 
  <properties>
    <jetty.http.port>9999</jetty.http.port>
  </properties>
  ```
  
  Then just run:
  
  ``` 
  mvn jetty:run
  
  ```
  
- Set the port in your plugin declaration inside the *pom.xml* file:
  
  ``` 
  <build>
    <plugins>
      <plugin>
        <groupId>org.eclipse.jetty</groupId>
        <artifactId>jetty-maven-plugin</artifactId>
        <version>9.2.1.v20140609</version>
        <configuration>
          <httpConnector>
            <!--host>localhost</host-->
            <port>9999</port>
          </httpConnector>
        </configuration>
      </plugin>
    </plugins>
  </build>
  ```



# Package(打包)

## Profile:构建不同环境的部署包
**重要提示，Spring Boot中的关键字变成了 @@ ，看[这里](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-1.3-Release-Notes#maven-resources-filtering)**
> [using-maven-profiles-and-resource-filtering](http://portofino.manydesigns.com/en/docs/portofino3/tutorials/using-maven-profiles-and-resource-filtering/#TOC-Definition-and-purpose)

> [introduction-to-profiles](http://maven.apache.org/guides/introduction/introduction-to-profiles.html)

项目开发好以后，通常要在多个环境部署，象我们公司多达5种环境：本机环境(**local**)、(开发小组内自测的)开发环境(**dev**)、(提供给测试团队的)测试环境(**test**)、预发布环境(**pre**)、正式生产环境(**prod**)，每种环境都有各自的配置参数，比如：数据库连接、远程调用的ws地址等等。如果每个环境build前手动修改这些参数，显然会非常的麻烦。而Maven本身就可以允许我们通过定义Profile的方式来在编译是动态注入配置：

``` xml
<profiles>
        <profile>
            <!-- 本地环境 -->
            <id>local</id>
            <properties>                
                <db-url>jdbc:oracle:thin:@localhost:1521:XE</db-url>
                <db-username>***</db-username>
                <db-password>***</db-password>
            </properties>
        </profile>
        <profile>
            <!-- 开发环境 -->
            <id>dev</id>
            <properties>                
                <db-url>jdbc:oracle:thin:@172.21.129.51:1521:orcl</db-url>
                <db-username>***</db-username>
                <db-password>***</db-password>
            </properties>
            <!-- 默认激活本环境 -->
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>
        ...
</profiles>
```

profiles节点中，定义了二种环境：local、dev(默认激活dev环境)，可以在各自的环境中添加需要的property值，接下来修改build节点，参考下面的示例：

<build>

``` xml
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
        </resource>
    </resources>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>2.5.1</version>
            <configuration>
                <source>1.6</source>
                <target>1.6</target>
                <encoding>utf-8</encoding>
            </configuration>
        </plugin>
    </plugins>
</build>
```

resource节点是关键，它表明了哪个目录下的配置文件(不管是xml配置文件，还是properties属性文件)，需要根据profile环境来替换属性值。通常配置文件放在resources目录下，build时该目录下的文件都自动会copy到class目录下:

![](http://images.cnitblog.com/blog/27612/201408/281044295329658.jpg)

以上图为例，其中spring-database.xml的内容为：

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans 
    http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="dataSource"
        class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="oracle.jdbc.driver.OracleDriver" />
        <property name="url" value="${db-url}" />
        <property name="username" value="${db-username}" />
        <property name="password" value="${db-password}" />        
    </bean>
</beans>
```

各属性节点的值，用占位符"${属性名}"占位，maven在package时，会根据profile的环境自动替换这些占位符为实际属性值。

默认情况下: 

`maven package`

将采用默认激活的profile环境来打包，也可以手动指定环境，比如：

`maven package -P dev`

将自动打包成dev环境的部署包(注：参数P为大写)

**1、开发环境与生产环境数据源采用不同方式的问题**

本机开发时为了方便，很多开发人员喜欢直接用JDBC直接连接数据库，这样修改起来方便；

``` 
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource"
        destroy-method="close">
        <property name="driverClassName" value="oracle.jdbc.driver.OracleDriver" />
        <property name="url" value="${db-url}" />
        <property name="username" value="${db-username}" />
        <property name="password" value="${db-password}" />
        <property name="defaultAutoCommit" value="false" />
        <property name="initialSize" value="2" />
        <property name="maxActive" value="10" />
        <property name="maxWait" value="60000" />
    </bean>
```

而生产环境，通常是在webserver(比如weblogic上)配置一个JNDI数据源，

``` xml
<bean id="dataSource" class="org.springframework.jndi.JndiObjectFactoryBean">
         <property name="jndiName" value="appDS" />        
</bean>
```

如果每次发布生产前，都要手动修改，未免太原始，可以通过maven的profile来解决。先把配置文件改成 ：

``` xml
<bean id="${db-source-jdbc}" class="org.apache.commons.dbcp.BasicDataSource"
        destroy-method="close">
        <property name="driverClassName" value="oracle.jdbc.driver.OracleDriver" />
        <property name="url" value="${db-url}" />
        <property name="username" value="${db-username}" />
        <property name="password" value="${db-password}" />
        <property name="defaultAutoCommit" value="false" />
        <property name="initialSize" value="2" />
        <property name="maxActive" value="10" />
        <property name="maxWait" value="60000" />
    </bean>
    
    <bean id="${db-source-jndi}" class="org.springframework.jndi.JndiObjectFactoryBean">
        <property name="jndiName" value="appDS" />        
</bean>
```

即用占位符来代替bean的id，然后在pom.xml里类似下面设置

``` xml
<profile>
            <!-- 本机环境 -->
            <id>local</id>
            <properties>
                ...
                <db-source-jdbc>dataSource</db-source-jdbc>
                <db-source-jndi>NONE</db-source-jndi>
                <db-url>jdbc:oracle:thin:@172.21.129.51:1521:orcl</db-url>                
                <db-username>mu_fsu</db-username>
                <db-password>mu_fsu</db-password>
                ...
            </properties>
            <!-- 默认激活本环境 -->
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>        
        <profile>
            <!-- 生产环境 -->
            <id>pro</id>
            <properties>
                ...
                <db-source-jdbc>NONE</db-source-jdbc>
                <db-source-jndi>dataSource</db-source-jndi>
                ...
            </properties>
        </profile>
    </profiles>
```

这样，mvn clean package -P local打包本地开发环境时，将生成

``` xml
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource"
        destroy-method="close">
        <property name="driverClassName" value="oracle.jdbc.driver.OracleDriver" />
        <property name="url" value="jdbc:oracle:thin:@172.21.129.***:1521:orcl" />
        <property name="username" value="***" />
        <property name="password" value="***" />
        <property name="defaultAutoCommit" value="false" />
        <property name="initialSize" value="2" />
        <property name="maxActive" value="10" />
        <property name="maxWait" value="60000" />
    </bean>
    
    <bean id="NONE" class="org.springframework.jndi.JndiObjectFactoryBean">
        <property name="jndiName" value="appDS" />        
    </bean>
```

而打包生产环境 mvn clean package -P pro时，生成

``` xml
<bean id="NONE" class="org.apache.commons.dbcp.BasicDataSource"
        destroy-method="close">
        <property name="driverClassName" value="oracle.jdbc.driver.OracleDriver" />
        <property name="url" value="${db-url}" />
        <property name="username" value="${db-username}" />
        <property name="password" value="${db-password}" />
        <property name="defaultAutoCommit" value="false" />
        <property name="initialSize" value="2" />
        <property name="maxActive" value="10" />
        <property name="maxWait" value="60000" />
    </bean>
    
    <bean id="dataSource" class="org.springframework.jndi.JndiObjectFactoryBean">
        <property name="jndiName" value="appDS" />        
    </bean>
```



**2、不同webserver环境，依赖jar包，是否打包的问题**

weblogic上，允许多个app，把共用的jar包按约定打包成一个war文件，以library的方式部署，然后各应用在WEB-INF/weblogic.xml中，用类似下面的形式

``` xml
<?xml version="1.0" encoding="utf-8"?>
<weblogic-web-app xmlns="http://www.bea.com/ns/weblogic/90">
    ...
    <library-ref>
        <library-name>my-share-lib</library-name>
    </library-ref>
</weblogic-web-app>
```

指定共享library 的名称即可。这样的好处是，即节省了服务器开销，而且各app打包时，就不必再重复打包这些jar文件，打包后的体积大大减少，上传起来会快很多。

而其它webserver上却未必有这个机制，一般为了方便，我们开发时，往往采用一些轻量级的webserver，比如:tomcat,jetty,jboss 之类，正式部署时才发布到weblogic下，这样带来的问题就是，本机打包时，要求这些依赖jar包，全打包到app的WEB-INF/lib下；而生产环境下，各应用的WEB-INF/lib下并不需要这些jar文件，同样还是用profile来搞定，先处理pom.xml，把依赖项改成类似下面的形式：

``` 
<dependency>
            <groupId>dom4j</groupId>
            <artifactId>dom4j</artifactId>
            <version>1.6.1</version>
            <scope>${jar.scope}</scope>
        </dependency>
```

即scope这里，用一个占位符来代替，然后profile这样配置

<profile>

``` xml
        <!-- 本机环境 -->
        <id>local</id>
        <properties>
            <jar.scope>compile</jar.scope>
            ...
        </properties>
        <!-- 默认激活本环境 -->
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
    <profile>
        <!-- 生产环境 -->
        <id>pro</id>
        <properties>
            <jar.scope>provided</jar.scope>
            ...
        </properties>
    </profile>
```

在maven里，如果一个依赖项的scope是provided，表示由容器提供，打包时将不会打包进最终的package里，所以这样配置后，生产环境打包时，依赖项的scope全变成了provided，即不打包进war文件，而本机环境下，因为scope是compile，所以会打包到war里。

# Distribution
## DistributionManagement
- [maven-deploy-plugin](https://maven.apache.org/plugins/maven-deploy-plugin/)

- [deploy:deploy](https://maven.apache.org/plugins/maven-deploy-plugin/deploy-mojo.html) is used to automatically install the artifact, its pom and the attached artifacts produced by a particular project. Most if not all of the information related to the deployment is stored in the project's pom.
- [deploy:deploy-file](https://maven.apache.org/plugins/maven-deploy-plugin/deploy-file-mojo.html) is used to install a single artifact along with its pom. In that case the artifact information can be taken from an optionally specified pomFile, but can be completed/overriden using the command line.
## Public Repository
### Maven Central
### JitPack
### Github Based 
目前很多开源项目都会基于Github搭建Maven，本部分也是介绍搭建方法。简单来说，共有三步：
1. deploy到本地目录
2. 把本地目录提交到gtihub上
3. 配置github地址为仓库地址

#### 配置Local File Maven仓库
maven可以通过http, ftp, ssh等deploy到远程服务器，也可以deploy到本地文件系统里。例如把项目deploy到/home/hengyunabc/code/maven-repo/repository/目录下：
```
<distributionManagement>
    <repository>
      <id>hengyunabc-mvn-repo</id>
      <url>file:/home/hengyunabc/code/maven-repo/repository/</url>
    </repository>
  </distributionManagement>
```
通过命令行则是：
```
mvn deploy -DaltDeploymentRepository=hengyunabc-mvn-repo::default::file:/home/hengyunabc/code/maven-repo/repository/
```
#### 提交到Github上
上面把项目deploy到本地目录home/hengyunabc/code/maven-repo/repository里，下面把这个目录提交到github上。在Github上新建一个项目，然后把home/hengyunabc/code/maven-repo下的文件都提交到gtihub上。
```
cd /home/hengyunabc/code/maven-repo/
git init
git add repository/*
git commit -m 'deploy xxx'
git remote add origin git@github.com:hengyunabc/maven-repo.git
git push origin master
```
#### 使用Github上Maven Repo
因为github使用了raw.githubusercontent.com这个域名用于raw文件下载。所以使用这个maven仓库，只要在pom.xml里增加：
```
<repositories>
        <repository>
            <id>hengyunabc-maven-repo</id>
            <url>https://raw.githubusercontent.com/hengyunabc/maven-repo/master/repository</url>
        </repository>
    </repositories>

```







## Private Repository


# Test(测试)

> 参考资料
> 
- [Maven单元测试][1]

Maven本身并不是一个单元测试框架，它只是在构建执行到特定生命周期阶段的时候，通过插件来执行JUnit或者TestNG的测试用例。这个插件就是maven-surefire-plugin，也可以称为测试运行器(Test Runner)，它能兼容JUnit 3、JUnit 4以及TestNG。在默认情况下，maven-surefire-plugin的test目标会自动执行测试源码路径(默认为src/test/java/)下所有符合一组命名模式的测试类。这组模式为：

- **/Test*.java：任何子目录下所有命名以Test开关的Java类。
- **/*Test.java：任何子目录下所有命名以Test结尾的Java类。
- **/*TestCase.java：任何子目录下所有命名以TestCase结尾的Java类。

## JUnit

在Java世界中，由Kent Beck和Erich Gamma建立的JUnit是事实上的单元测试标准。要使用JUnit，我们首先需要为Hello World项目添加一个JUnit依赖，修改项目的POM如代码清单。

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.juvenxu.mvnbook</groupId>
  <artifactId>hello-world</artifactId>
  <version>1.0-SNAPSHOT</version>
  <name>Maven Hello World Project</name>
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.7</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
</project>
```

代码中添加了dependencies元素，该元素下可以包含多个dependency元素以声明项目的依赖，这里我们添加了一个依赖——groupId是junit，artifactId是junit，version是4.7。前面我们提到groupId、artifactId和version是任何一个Maven项目最基本的坐标，JUnit也不例外，有了这段声明，Maven就能够自动下载junit-4.7.jar。也许你会问，Maven从哪里下载这个jar呢？在Maven之前，我们可以去JUnit的官网下载分发包。而现在有了Maven，它会自动访问中央仓库([http://repo1.maven.org/maven2/](http://repo1.maven.org/maven2/)) ,下载需要的文件。读者也可以自己访问该仓库，打开路径junit/junit/4.7/，就能看到junit-4.7.pom和junit-4.7.jar。

上述POM代码中还有一个值为test的元素scope，scope为依赖范围，若依赖范围为test则表示该依赖只对测试有效，换句话说，测试代码中的import JUnit代码是没有问题的，但是如果我们在主代码中用import JUnit代码，就会造成编译错误。如果不声明依赖范围，那么默认值就是compile，表示该依赖对主代码和测试代码都有效。

配置了测试依赖，接着就可以编写测试类，回顾一下前面的HelloWorld类，现在我们要测试该类的sayHello()方法，检查其返回值是否为“Hello Maven”。在src/test/java目录下创建文件，其内容如代码清单如下：

``` 
package com.juvenxu.mvnbook.helloworld;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class HelloWorldTest
{
    @Test
    public void testSayHello()
    {
        HelloWorld helloWorld = new HelloWorld();

        String result = helloWorld.sayHello();

        assertEquals( "Hello Maven", result );
    }
}

```

测试用例编写完毕之后就可以调用Maven执行测试，运行 mvn clean test。

构建在执行compiler:testCompile任务的时候失败了，Maven输出提示我们需要使用-source 5或更高版本以启动注释，也就是代码中JUnit 4的@Test注解。这是Maven初学者常常会遇到的一个问题。由于历史原因，Maven的核心插件之一compiler插件默认只支持编译Java 1.3，因此我们需要配置该插件使其支持Java 5，见代码清单：

``` xml
<project>
…
<build>
<plugins>
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
       <source>1.5</source>
       <target>1.5</target>
     </configuration>
   </plugin>
</plugins>
</build>
…
</project>
```

该POM省略了除插件配置以外的其他部分。现在再执行mvn clean test,结果正常。

## 测试命令

Maven中使用package、install等命令时会自动调用Test组件，```mvn package -DskipTests```命令可以跳过测试。也可以在插件配置的时候设置跳过：

``` 
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.5</version>
    <configuration>
        <skipTests>true</skipTests>
    </configuration>
</plugin>
```

### 指定测试用例

maven-surefire-plugin提供了一个test参数让Maven用户能够在命令行指定要运行的测试用例。如：

``` 
mvn test -Dtest=RandomGeneratorTest  
```

也可以使用通配符：

``` 
mvn test -Dtest=Random*Test  
```

或者也可以使用“，”号指定多个测试类：

``` 
mvn test -Dtest=Random*Test,AccountCaptchaServiceTest  
```

如果由于历史原因，测试类不符合默认的三种命名模式，可以通过pom.xml设置maven-surefire-plugin插件添加命名模式或排除一些命名模式。

``` 
    <plugin>  
        <groupId>org.apache.maven.plugins</groupId>  
        <artifactId>maven-surefire-plugin</artifactId>  
        <version>2.5</version>  
        <configuration>  
            <includes>  
                <include>**/*Tests.java</include>  
            </includes>  
            <excludes>  
                <exclude>**/*ServiceTest.java</exclude>  
                <exclude>**/TempDaoTest.java</exclude>  
            </excludes>  
        </configuration>  
    </plugin>  
```



## Coverage(测试覆盖率)

### Cobertura