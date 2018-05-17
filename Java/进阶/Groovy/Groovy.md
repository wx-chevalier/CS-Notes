# Groovy

要读懂 Gradle，我们首先需要了解 Groovy 语言中的两个概念，一个 Groovy 中的 Bean 概念，一个是 Groovy 闭包的 delegate 机制。

## Bean

Groovy 中的 Bean 和 Java 中的 Bean 有一个很大的不同，即 Groovy 为每一个字段都会自动生成 getter 和 setter，并且我们可以通过像访问字段本身一样调用 getter 和 setter，比如：

```
class GroovyBeanExample {
   private String name
}

def bean = new GroovyBeanExample()
bean.name = 'this is name'
println bean.name











7









1class GroovyBeanExample {


2   private String name


3}


4


5def bean = new GroovyBeanExample()


6bean.name = 'this is name'


7println bean.name
```

我们看到，GroovyBeanExample 只定义了一个私有的 name 属性，并没有 getter 和 setter。但是在使用时，我们可以直接对 name 进行访问，无论时读还是写。事实上，我们并不是在直接访问 name 属性，当我们执行"bean.name = 'this is name'"时，我们实际调用的是"bean.setName('this is name')"，而在调用"println bean.name"时，我们实际调用的是"println bean.getName()"。这里的原因在于，Groovy 动态地为 name 创建了 getter 和 setter，采用像直接访问的方式的目的是为了增加代码的可读性，使它更加自然，而在内部，Groovy 依然是在调用 setter 和 getter 方法。这样，我们便可以理解上面对 showDescription2 的 description 设置原理。

## Clousre

Gradle 大量地使用了 Groovy 闭包的 delegate 机制。简单来说，delegate 机制可以使我们将一个闭包中的执行代码的作用对象设置成任意其他对象。比如：

```
class Child {
   private String name
}

class Parent {
   Child child = new Child();

   void configChild(Closure c) {
      c.delegate = child
      c.setResolveStrategy Closure.DELEGATE_FIRST
      c()
   }
}

def parent = new Parent()
parent.configChild {
name = "child name"
}

println parent.child.name











20









1class Child {


2   private String name


3}


4


5class Parent {


6   Child child = new Child();


7


8   void configChild(Closure c) {


9      c.delegate = child


10      c.setResolveStrategy Closure.DELEGATE_FIRST


11      c()


12   }


13}


14


15def parent = new Parent()


16parent.configChild {


17name = "child name"


18}


19


20println parent.child.name
```

在上面的例子中，当我们调用 configChild()方法时，我们并没有指出 name 属性是属于 Child 的，但是它的确是在设置 Child 的 name 属性。事实上光从该方法的调用中，我们根本不知道 name 是属于
    哪个对象的，你可能会认为它是属于 Parent 的。真实情况是，在默认情况下，name 的确被认为是属于 Parent 的，但是我们在 configChild()方法的定义中做了手脚，使其不再访问 Parent 中的 name(Parent 也没有 name 属性)，而是 Child 的 name。在 configChild()方法中，我们将该方法接受的闭包的 delegate 设置成了 child，然后将该闭包的 ResolveStrategy 设置成了 DELEGATE_FIRST。这样，在调用 configChild()时，所跟闭包中代码被代理到了 child 上，即这些代码实际上是在 child 上执行的。此外，闭包的 ResolveStrategy 在默认情况下是 OWNER_FIRST，即它会先查找闭包的 owner(这里即 parent)，如果 owner 存在，则在 owner 上执行闭包中的代码。这里我们将其设置成了 DELEGATE_FIRST，即该闭 包会首先查找 delegate(本例中即 child)，如果找到，该闭包便会在 delegate 上执行。对于上面的 showDescription3，便 是这种情况。当然，实际情况会稍微复杂一点，比如 showDescription3()方法会在内部调用 showDescription3 的 configure()方法，再在 configure()方法中执行闭包中的代码。

你可能会发现，在使用 Gradle 时，我们并没有像上面的 parent.configChild()一样指明方法调用的对象，而是在 build.gradle 文件中直接调用 task()，apply()和 configuration()方法等，这是
    因为在没有说明调用对象的情况下，Gradle 会自动将调用对象设置成当前 Project。比如调用 apply()方法和调用 project.apply()方法的效果是一样的。查查 Gradle 的 Project 文档，你会发现这些方法都是 Project 类的方法。

另外举个例子，对于 configurations()方法(它的作用我们将在后面的文章中讲到)，该方法实际上会将所跟闭包的 delegate 设置成 ConfigurationContainer，然后在该 ConfigurationContainer 上执行闭包中的代码。再比 如，dependencies()方法，该方法会将所跟闭包的 delegate 设置成 DependencyHandler。
