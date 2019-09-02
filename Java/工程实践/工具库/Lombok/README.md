# 基于 Lombok 的类生成

Lombok 主要依赖编译时代码生成技术，帮你自动生成基于模板的常用的 Java 代码，譬如最常见的 Getter 与 Setter。之前动态的插入 Getter 与 Setter 主要有两种，一个是像 Intellij 与 Eclipse 这样在开发时动态插入，缺点是这样虽然不用你手动写，但是还是会让你的代码异常的冗长。另一种是通过类似于 Spring 这样基于注解的在运行时利用反射动态添加，不过这样的缺陷是会影响性能，并且有一定局限性。

# 环境配置

## Gradle

我们可以简单地在 build.gradle 文件中添加依赖以引入 Gradle:

```groovy
repositories {
	mavenCentral()
}

dependencies {
	compileOnly 'org.projectlombok:lombok:1.18.8'
	annotationProcessor 'org.projectlombok:lombok:1.18.8'
}
```

对于复杂配置的场景，也可以使用官方的 Gradle 插件：

```groovy
plugins {
  id "io.freefair.lombok" version "4.0.1"
}

buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "io.freefair.gradle:lombok-plugin:4.0.1"
  }
}

apply plugin: "io.freefair.lombok"
```
