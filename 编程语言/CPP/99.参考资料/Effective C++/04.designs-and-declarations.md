本文对应原书的第四部分，主要介绍为了设计和声明良好的 C++接口需要注意的事项。

<!--more-->

## item18 让接口不容易被误用

所谓的接口就是提供给用户使用代码的途径。C++有大量关于接口的概念，如函数接口，类接口，模板接口等。理想情况下，如果用户用错了接口，这个代码不应该通过编译。如果代码通过了编译，就应该得到想要的结果。
让接口不易被误用的办法包括：建立新类型、限制类型上的操作，保证接口设计的一致性，消除用户的资源管理责任。

1. **建立新的数据类型、限制类型上的操作**
   接口要设计的不易被误用，就要充分考虑用户可能犯的错误。假如设计一个表示日期的类：

```
    class Date
    {
        public:
            Date(int month, int day, int year); // 美式日期标准
    }

    Date d1(30,3,1995); // 错误，输入的是英式日期标准
    Date d2(3,40,1995); // 数字输入错误
```

像上述这样的错误，可以通过引入新的数据类型来进行预防。通过使用简单的包装类(wrapper class)，让编译器来检测错误：

```
    struct Day{
        explicit Day(int d):val(d){}
        int val;
    };
    struct Month{
        explicit Month(int m):val(m){}
        int val;
    };
    struct Year{
        explicit Year(int y):val(y){}
        int val;
    };
    class Date
    {
        public:
            Date(const Month& m, const Day& d, const Year& y); // 美式日期标准
    }

    Date d1(3,30,1995); // 数据类型错误
    Date d2(Day(30), Month(3), Year(1995)); // 格式错误
    Date d3(Month(3), Day(30), Year(1995)); //正确
```

将 Day，Month，Year 设计成成熟的类比使用上述的结构体要好。上述代码保证了数据类型的准确性，限制其值的范围也是必要的，例如一年只有 12 个月，Month 应该反映出这个事实。可以通过 enum 满足功能上的要求，但是 enum 不是类型安全的(type safe)，item2 中展示过 enum 可以被用来当作 int 类型使用。比较安全的做法是预先定义所有有效的 Month。下面的代码虽然繁琐，但是保证了数据的准确性。

```
class Month{
  public:
    static Month Jan(){return Month(1);}  //用函数替换对象，避免初始化出现问题(item4)
    static Month Feb(){return Month(2);}
    ...
    static Month Dec(){return Month(12);}
  private:
    explicit Month(int m);  //explicit禁止参数隐式转换，private禁止用户生成自定义的月份
    ...
};

Date d(Month::Mar(), Day(30), Year(1995)); //正确
```

预防错误的另一个办法是，限制类型内什么操作允许，什么操作不允许，常见的限制是加上`const`。item3 中展示了用`const`修饰 operator\*返回值来避免无意义的赋值：

```
    if(a * b = c)... // 应该是a * b == c
```

2. **保证接口设计的一致性**
   STL 容器的接口设计保证了高度的一致性，例如每个 STL 容器都是通过`size()`成员函数返回容器中对象的个数。而在 Java 中，数组使用`length`属性，List 使用`size()`成员函数。这样的不一致性会给开发人员带来不便。

3. **避免要求用户必须进行某些操作**
   任何要求用户记得做某些事情的接口都容易造成误用，因为用户很可能忘记完成。例如动态分配了一个资源，要求用户以特定的方式释放资源。

```
    Investment* createInvestment();
```

对于上述接口，要求用户在资源使用完后进行释放，但是用户可能产生两种错误：忘记释放资源；多次释放资源。解决方法是用智能指针来进行管理资源，为了避免用户忘记把函数的返回值封装到智能指针内，我们最好让这个函数直接返回一个智能指针对象：

```
    std::shared_ptr<Investment> createInvestment();
```

实际上，返回一个智能指针还解决了一些列用户资源泄漏的问题，如 item14 中讲到的，`shared_ptr`允许在建立智能指针时为它指定一个资源释放函数(即所谓的删除器，deleter)。
假如在我们设计的接口中，通过`createInvestment()`得到一个 Investment\*对象，这个对象必须通过`getRidOfInvestment()`来进行资源释放。我们必须把`getRidOfInvestment()`绑定到 shared_ptr 的删除器，这样 shared_ptr 在使用完成后会自动调用指定的资源释放函数，避免使用错误的释放机制。
shared_ptr 还有一个好处是，它会自动使用它的每个指针专属的删除器，从而能够避免所谓的 DLL 交叉问题(cross-DLL problem)。这个问题发生于当对象在一个 DLL 中被创建，在另外一个 DLL 中被释放时，在许多平台上会导致运行时问题，因为不同的 DLL 可能会被链接到不同的代码。shared_ptr 会在构造时就确定当引用计数为零时调用哪个 DLL 的删除器，因此不必担心 DLL 交叉问题。

**Note：总结**

- 一个良好的接口应该保证其不容易被误用。我们在设计接口时要努力实现这个目标。
- 保证正确使用的方法包括保证接口的一致性，以及自定义类型的行为与内置类型的行为保持一致。
- 避免无用的方法包括定义新的包装类型、限制类型的操作、限制取值范围、避免让用户负责管理资源等。
- shared_ptr 支持绑定自定义的删除器，实现想要的析构机制，可以有效防范 DLL 交叉问题。

## item19 把类当作类型来设计

在面向对象编程的语言中，当定义一个新 class 的时候，也就是定义了一个新的 type。这就意味着重载函数和操作符、控制内存的分配和释放、定义对象的初始化和析构等等，全部都要加以考虑。因此，应该带着像语言设计者设计原始类型一样谨慎。
设计一个良好的 class 是一项艰巨的工作，好的 type 有自然的语法、直观的语义以及高效的实现。在 C++中，设计一个良好的类应该时刻考虑到以下的规范：

1. **新类型的对象如何被创建和销毁？**

   - 这影响了类的构造函数和析构函数，以及内存分配函数和释放函数(operator new, operator new[], operator delete, operator delete[])的设计。

2. **对象的初始化和对象的赋值应该有什么区别**

   - 这决定了构造函数和赋值操作符的行为，以及其间的差异。初始化用于未创建的对象，赋值适用于已创建的对象。

3. **新类型的对象如果作为值进行传递有什么意义**

   - 拷贝构造函数决定了一个 type 的值传递如何实现的。

4. **新类型的合法值有什么限制**

   - 通常情况下，并不是所有的成员变量是有效的。为了避免函数抛出异常，我们要在成员函数中堆变量进行错误检查工作，尤其是构造函数、赋值操作符和所谓的 setter 函数。

5. **新的类型是否存在继承关系**

   - 如果新的类型继承自已有的类型，类型的设计就会受到被继承类的约束，比如说函数是否为虚函数。

6. **新类型允许进行什么样的转换**

   - 新类型的对象可能被隐式地转换成其他类型，需要决定是否允许类型的转换。如果希望把 T1 隐式转换成 T2，可以在 class T1 中定义一个类型转换函数(operator T2)，或者在 class T2 内写一个可被单一实参调用(non-explicit-one-argument)的构造函数。如果进行显式转换，需要定义个显式转换的函数(item15)。

7. **哪些运算符和函数对于新类型是合理的**

   - 这决定了新类型中需要声明哪些函数，包括成员函数，非成员函数，友元函数等。

8. **哪些标准函数是需要被禁止的**

   - 将不希望编译器自动生成的标准函数声明为 private(item6)。

9. **谁可以访问新类型中的成员**

   - 这决定了成员函数的访问级别是 public，protected 或 private。

10. **新类型中的“隐藏接口”是什么**

    - 新类型对于性能、异常安全性、资源管理有什么保障，需要在代码中加上相应的约束条件。

11. **新类型的通用性如何**

    - 如果需要新类型适用于多种类型，应该定义一个类模板(class template)，而不是单个 class。

12. **是否真的需要一个新类型**
    - 如果只是定义新的派生类以便为既有类增加功能，定义一些非成员函数或者函数模板更加划算。

**Note：总结**

- 设计 class 就是设计 type，在定义一个新的 type 前，要充分考虑到上述问题。

## item20 用常量引用传递代替值传递

默认情况下，C++以值传递的方式传递对象给函数的。编译器会调用对象的拷贝构造函数创建实参的副本，再把副本传递到函数中，而拷贝是一个耗时的操作。

```
    class Person
    {
        public:
            Person();
            virtual ~Person();
            ...
        private:
            std::string name;
            std::string address;
    }
    class Student:public Person
    {
        public:
            Student();
            ~Student();
            ...
        private:
            std::string schoolName;
            std::string schoolAddress;
    }

    bool validateStudent(Student s);
    // 以值传递的方式调用函数
    Student stu;
    bool isOK=validateStudent(stu); // 调用函数，以值传递的方式传递参数
```

当上述函数被调用时，发生了以下过程：

- Student 类的拷贝函数被调用，用来初始化参数 s
- 当 validateStudent 函数返回时，s 被销毁

因此，当函数 validateStudent 被调用时，参数的传递成本是**调用了一次 Student 的拷贝构造函数，调用了一次 Student 的析构函数**。Student 中还有两个 string 对象，所以每次构造 Student 对象也会构造两个 string 对象。Student 又继承自 Person 类，Person 类中也包含两个 string 对象。这就意味着，Student 对象的值传递会调用一次 Student 拷贝构造函数、一次 Person 拷贝构造函数、四次 string 拷贝构造函数，析构过程也有同样的过程。

```
    bool validateStudent(const Student& s);
```

传递常量引用的效率要高得多，没有任何构造函数或析构函数被调用，因为没有任何对象被调用。参数声明中`const`是十分重要的，这样可以避免传入的参数在函数内被修改。

---

以传引用的方式传递参数可以避免对象切割(slicing)问题。当函数的参数类型是基类，通过值传递传入派生类对象时，调用的是基类的拷贝构造函数，派生类中派生的特性就会被切割，留下的是基类对象。

```
    class Window
    {
    public:
        ...
        std::string name() const;
        virtual void display() const;
    }

    class WindowWithScrollBars:public Window
    {
    public:
        ...
        virtual void display() const;
    }

    void printNameAndDisplay(Window w) // 值传递，会产生对象切割
    {
        std::cout<<w.name();
        w.display();
    }
```

`display()`是虚函数，在两个类中有不同的实现。如果使用值传递就会导致对象被切割，函数内调用的永远是基类 Window 中的版本。解决对象切个问题的方法就是传递常量引用：

```
    void printNameAndDisplay(const Window& w)
    {
        std::cout<<w.name();
        w.display();
    }
```

---

在 C++中，引用往往是通过指针来实现的，引用传递本质上就是指针传递。因此，对于内置的类型，值传递比引用传递的效率要高。此外，STL 迭代器和函数对象都被设计为值传递。

**Note：总结**

- 尽量用常量引用传递替换值传递，前者通常比较高效，并可以避免对象切割问题。
- 对于内置类型，STL 迭代器和函数对象，值传递更加高效。

## item21 不要在需要返回对象时返回引用

在 item20 中我们见证到了引用传递的高效性，但这并不意味着引用传递是没有风险的。引用是已经存在的变量的别称，引用存在的风险就是**指向一个并不存在的对象**。这个错误很容易出现在函数的返回值是一个引用的情况下。

```
    class Rational
    {
    public:
        Rational(int numerator=0,int denominator=1);
        ...
    private:
        int n,d;
        friend const Rational operator*(const Rational& lhs,const Rational& rhs);
    }
```

上述代码中，operator*返回的是值，该值是在函数调用时生成的拷贝副本。如果返回的是一个引用，，那它必然指向一个 Rational 对象，该对象包含两个 Rational 的乘积。operator*返回的 Rational 对象必须通过函数自己创建，函数创建新对象的途径有两个：**在栈上创建**以及**在堆上创建**。

```
    // 在栈上创建
    const Rational& operator*(const Rational& lhs,const Rational& rhs)
    {
        Rational result(lhs.n*rhs.n,lhs.d*rhs.d); // 在栈上创建对象
        return result; // result的生命周期结束，被析构
    }
```

在上述代码中，operator\*返回的是指向 result 的引用，但是 result 是一个局部变量，当函数返回时自动销毁，那么引用指向的就是一个错误的对象。

```
    const Rational& operator*(const Rational& lhs,const Rational& rhs)
    {
        Rational* result = new Rational(lhs.n*rhs.n,lhs.d*rhs.d); // 在堆上创建对象
        return *result;
    }
```

上述代码在堆上创建 Rational 对象，对象资源在函数内没有被释放，可以被访问。但是这就要求用户要进行资源释放，否则就有资源泄漏的风向，这违背了 item18 中设计良好接口的原则。
无论时在堆上还是在栈上创建对象，每次访问这个函数都会调用一次构造函数，我们的目的时节省这次调用。下面代码通过引入静态变量来解决这个问题，但是静态对象可能会带来多线程安全问题。

```
    const Rational& operator*(const Rational& lhs,const Rational& rhs)
    {
        static Rational result; // 创建静态对象
        ...
        return result;
    }

    // 调用
    Rational a,b,c,d;
    if((a*b)==(c*d)) // 等价于operator==(operator*(a,b),operator*(c,d))
    {
        ... // 分支语句1
    }
    else
    {
        ... // 分支语句2
    }
```

在上述代码中，无论 a,b,c,d 取什么值，if 的条件语句每次结果都为 true。当 operator==被执行，会产生两次 operator\*的调用，他们返回的是指向同一个对象的引用，因此一定会相等。

上面所举的反例，是为了说明当一个函数必须要返回一个新的对象时，为了代码安全我们应该接受值传递带来的构造和析构成本。

```
    const Rational operator*(const Rational& lhs,const Rational& rhs)
    {
        return Rational(lhs.n*rhs.n,lhs.d*rhs.d);
    }
```

**Note：总结**

- 不要让函数返回一个指向局部变量的指针或引用，否则会造成资源泄漏和程序崩溃。
- 不要让函数返回一个指向局部静态对象(程序中可能需要多个这样的对象)的指针或引用。
- 在面临返回引用还是返回值的选择时，优先保证程序能正常运行。

## item22 类的数据成员声明为 private

本 item 主要通过分析为什么数据成员不能是 public，其观点同样适用于 protected，最后得出一个结论：成员变量应该是 private。

1. **保证语法的一致性**
   在 item18 中强调良好的接口应该保证其设计的一致性。如果成员变量不是 public，那么能够访问成员变量的唯一方法就是通过成员函数。如果 public 接口内的都是函数，用户就不需要在访问成员时在意是否该使用小括号。

2. **精确控制对成员变量的处理**
   如果成员变量是 public，任何人都可以进行读写。如果将其私有化，通过函数获取或者设定其值，可以实现“不准访问”、“只读”、“只写”以及“读写”。

```
    class AccessLevels
    {
    public:
        ...
        int getReadOnly() const      {return readOnly;}   //读取只读的成员
        void setReadWrite(int value) {readWrite = value;} //写入可读写的成员
        int getReadWrite() const     {return readWrite;}  //读取可读写的成员
        void setWriteOnly(int value) {writeOnly = value;} //写入只写的成员
    private:
        int noAccess:  //这个成员既不能读也不能写
        int readOnly;  //这个成员只读
        int readWrite; //这个成员可读可写
        int writeOnly; //这个成员只写
    }
```

3. **保证类的封装**
   如果通过函数来访问成员变量，便于更改某个计算过程来替换成员变量，而用户不用关注其内部实现是否发生变化。例如，对于一个自动测速程序，当汽车通过时，其速度便被计算并被填入速度收集器。

```
    class SpeedDataCollection
    {
    public:
        void addValue(int speed);    // 把当前测得的速度放进数据集
        double averageSoFar() const; // 利用数据计算平均速度
    }
```

对于函数`averageSoFar()`，我们有两种思路进行实现：第一，在类中定义一个成员变量，记录所有速度的平均值，当`averageSoFar()`被调用时，只需要返回该变量就可；第二，每次调用`averageSoFar()`函数时都重新计算平均值，此函数可以访问速度数据集中每一个值。两种方法可以适用于不同的环境，方法一`averageSoFar()`高效，但是要为累积总量、数据点数以及平均值分配存储空间，比较消耗内存；方法二中调用`averageSoFar()`时才进行计算，执行较慢。方法一适用于对反应速度有要求的情况，方法二适用于内存比较紧张的机器上(如嵌入式设备)。
将成员变量隐藏在函数接口的背后，可以为所有可能的实现方式提供弹性支持。对用户隐藏成员变量，可以确保 class 的约束条件总是会获得维护，因为只有成员函数可以影响它们。
假设我们有一个 public 成员变量，如果对其进行了修改或删除，所有使用它的代码都有可能被破坏，对于 protected 成员变量来说，使用它的派生类也会被破坏，protected 成员变量和 public 成员变量一样缺乏封装性。

**Note：总结**

- 成员变量要声明为 private，这样可以保证接口的一致性、可以进行精确的访问权限控制，并提供 class 多种功能实现的弹性。
- protected 并不比 public 更具有封装性。

## item23 用非成员且非友元函数来替换成员函数

这个 item 更直白的意思是“不要随意把函数定义为类的成员函数”，否则会破坏类的封装性。这个结论看着很矛盾，直觉上我们把函数定义为成员函数貌似更具有封装的特征。事实上并不是这样的！
假设我们定义了一个浏览器类，它用到三个函数来实现清除下载缓存、清除浏览记录、清除 cookies：

```
    class WebBrowser
    {
    public:
        void clearCache();
        void clearHistory();
        void removeCookies();
        void cleanEverything(); // 调用上面三个函数
        ...
    }
```

在上面代码中，声明了一个成员函数`cleanEverything()`实现一键清除。这个功能也可以通过一个非成员函数进行实现：

```
    void clearBrowser(WebBrowser& wb)
    {
        wb.clearCache();
        wb.clearHistory();
        wb.removeCookies();
    }
```

上面两种实现方法，哪种更好？在面向对象编程中，数据以及执行数据操作的函数应该被捆绑在一起，这意味着成员函数的方案更好。事实上，这是不正确的，这是对面向对象真实意义的误解。面向对象要求数据应该尽可能的被封装，与直观判断相反，成员函数`cleanEverything()`的封装性要比非成员函数`clearBrowser(WebBrowser&)`低。此外，提供非成员函数可以为 WebBrowser 类相关的功能提供更大的包装弹性，减少编译依赖，增加 WebBrowser 的可延伸性。究其原因，我们需要从封装进行讨论。
对于对象的成员变量，越少的代码可以访问到数据，意味着成员变量的封装性越好。我们通过计算能够访问数据的函数数量来衡量其封装性，能访问成员变量的函数越多，其封装性越低。item22 中提到，成员变量应该是 private，否则就毫无封装性。能够访问 private 成员变量的函数只有成员函数和友元函数。如果要在一个成员函数和一个非成员且非友元的函数之间做选择，且两者功能相同，那么非成员函数并不会增加能够访问成员变量的函数，具有更好的封装性。
**需要注意的是：**

- 上面的讨论只适用于**非成员且非友元**函数，而不是非成员函数。因为友元函数和成员函数对封装性的影响是相同的。
- 上面所谓的“非成员函数”并不意味着其不能是其他类的成员函数。例如我们可以让`clearBrowser(WebBrowser&)`成为某个工具类的静态成员函数，只要它不是 WebBrowser 的成员或友元函数，就不破坏 WebBrowser 的封装性。

`clearBrowser(WebBrowser&)`函数既不是成员函数也不是友元函数，没有对 WebBrowser 的特殊访问权限，即使这个函数不存在，用户也可以自己调用三个函数实现同样的功能，我们将它称为便利函数。虽然不能作为成员函数，但是为了管理方便，我们可以将其和 WebBrowser 放在同一个命名空间下。如：

```
    namespace WebBrowserStuff
    {
        class WebBrowser{...};
        void clearBrowser(WebBrowser&);
    }
```

将两者放在同一个命名空间下还有另外一个作用。与 class 不同的是,namespace 可以跨越多个源文件，这样就可以把不同功能的代码放在不同的头文件中，降低编译依赖性。假如 WebBrowser 有多个不同功能的便利函数，某些与书签有关，某些与打印有关，某些与 Cookies 有关……当只需要书签相关的便利函数时，我们并不希望与管理 Cookies 的便利函数发生编译依赖关系。分离它们的最直接做法就是将书签相关的便利函数声明在一个头文件中，Cookies 管理的函数声明在另外一个头文件中。如：

```
    // 头文件webbrowser.h，WebBrowset的核心功能以及所有用户都使用的便利函数
    namespace WebBrowserStuff
    {
        class WebBrowser{...};
        ...
    }

    // 头文件webbrowserbookmarks.h
    namespace WebBrowserStuff
    {
        ...   // 书签相关的便利函数
    }

    // 头文件webbrowsercookies.h
    namespace WebBrowserStuff
    {
        ...   // Cookies管理相关的便利函数
    }
```

其实，这正是 C++标准库的组织方式。C++标准库有数十个头文件，每个头文件都声明了 std 的某些机能。如果用户只想使用`vector`的功能，就不需要`#include <memory>`，这允许用户只对他们使用那一小部分系统形成编译依赖。
将所有便利函数放在隶属于同一命名空间的多个头文件，意味着用户可以轻松扩展这一组便利函数。用户所需要做的就是添加更多非成员且非友元函数到命名空间内，如用户为 WebBrowser 添加下载功能，只需要在 WebBrowserStuff 命名空间中建立一个头文件，内含便利函数的声明即可，新函数就像原来的便利函数那样可用并且整合为一体。这是 class 无法实现的，虽然可以派生出子类来扩展功能，但是子类无法访问父类的 private 成员而且并非所有的类都被设计用来作为基类。

**Note：总结**

- 尽可能用非成员且非友元的函数替换成员函数，这样可以增加类的封装性、包装弹性和功能扩展性。
- 命名空间可以分布在不同的编译单元中，减小编译依赖性。

## item24 如果参数要进行类型转换，该函数不能作为成员函数

在导论中提到，**class 支持隐式类型转换会给程序带来隐患**，因为如果出现了类型错误，编译器是不会报错的。但是我们在建立数值类型时，比如设计一个类来表现有理数，允许整数隐式转换为有理数是完全合理的。此外，C++也支持 int 到 double 的隐式转换。我们设计一个有理数类：

```
    class Rational
    {
    public:
        Rational(int numerator = 0, int denominator = 1);
        //构造函数专门不声明为explicit来允许从int到Rational的隐式转换
        int numerator() const;
        int denominator() const;

        const Rational operator*(const Rational& rhs) const;
    ...
    };
```

作为有理数，我们可以定义各种算术运算符，例如加减乘除。上述代码中，我们采用常规的将 operator\*声明为成员函数，并进行代码调用：

```
    // 可以对任意两个有理数对象进行相乘
    Rational oneEighth(1,8);
    Rational oneHalf(1,2);
    Rational result = oneEighth * oneHalf; //编译通过
    result = result * oneEighth; //编译通过

    // 将一个对象和int进行相乘
    result = oneHalf * 2; //编译通过，等价于result = oneHalf.operator*(2)，int进行了隐式类型转换
    result = 2 * oneHalf; //编译错误，等价于result = 2.operator*(oneHalf)
```

在上述代码中，一个对象和 int 数据进行相乘时，我们往往会想当然的觉得两种写法都是正确的，因为乘法满足交换律。事实上，`oneHalf`是一个包含`operator*`函数的对象，编译器可以调用该函数，然而整数 2 并没有`operator*`成员函数。虽然编译器会在命名空间内或 global 作用域内尝试寻找可以调用的非成员`operator*`函数：

```
    result = operator*(2, oneHalf);
```

但是本例中并没有声明这样一个函数，所以编译器会报错。
对于运行正确的代码，其发生了隐式类型转换，编译器知道传递的是 int 类型的值，而函数需要的是 Rational。编译器用传入的值隐式调用了 Rational 的构造函数，生成了一个 Rational 对象，其真正的过程如下：

```
    const Rational tmp(2);
    result = onHalf*tmp;
```

上述情况是因为构造函数是 non-explicit，如果构造函数是 explicit 的，编译器就不会这么操作，那么以下语句都不会通过编译。

```
    result = oneHalf * 2; //编译错误，构造函数声明为explicit，int便不能转换为Rational
    result = 2 * oneHalf; //编译错误，等价于result = 2.operator*(oneHalf)
```

总结下来，**只有当参数位于参数表里时才可以进行隐式转换**。我们这里的`operator*`作为成员函数，只能对乘号右边的函数参数进行隐式转换，而不在参数表里的参数，即调用成员函数的对象不能进行隐式转换。

然而，我们还是希望两个语句都可以编译通过，这也满足我们对乘法交换律的认识。解决方案就是将`operator*`声明为非成员函数，即允许编译器在每个实参上进行隐式类型转换。

```
    class Rational
    {
    public:
        Rational(int numerator = 0, int denominator = 1);
        //构造函数专门不声明为explicit来允许从int到Rational的隐式转换
        int numerator() const;
        int denominator() const;
        ...  // 不声明operator*
    }

    const Rational operator*(const Rational& lhs,const Rational& rhs) // 非成员函数
    {
        return Rational(lhs.numerator()*rhs.numerator(),
                        lhs.denominator()*rhs.denominator());
    }

    Rational oneFourth(1,4);
    Rational result;
    result = oneFourth * 2; //可以编译
    result = 2 * oneFourth; //可以编译
```

很多 C++程序员有一个**误区**：如果某个函数跟某个类有关并且不能作为成员函数，那么就应该将其作为友元函数。这个想法是完全不必要的，本例表明了`operator*`完全可以由 Rational 的 public 接口完成任务，不不要额外的特殊接口。而且，能够避免使用友元函数就应该极力避免。

本条款讨论的内容只是限于面向对象编程这一条件下，当我们在 Template C++条件下时，让 Rational 作为一个 class template 更为妥当，涉及的内容在以后讨论。

**Note：总结**

- 如果某个函数所有的参数都可能需要隐式转换，这个函数必须作为非成员函数。

## item25 考虑写一个高效的 swap 函数

swap 是一个非常有用的函数，是保证代码异常安全性重要方法(Item29)，也可以用来避免自我赋值(Item11)。在标准库里面，swap 的实现方式就是经典的方法：

```
    namespace std
    {
        template<typename T>
        void swap(T& a,T& b)
        {
            T temp(a);
            a=b;
            b=temp;
        }
    }
```

只要类型 T 支持拷贝(拷贝构造函数和赋值操作符)，swap 函数就会帮助实现交换功能。但是在上述过程中进行了三次拷贝：a 拷贝给 temp，b 拷贝给 a，temp 拷贝给 b。如果实现的是比较大的对象的交换，其效率是非常低的。
为了解决上述问题，常用的方法叫做**pimpl**(pointer to implementation，item31)，其基本的思想就是把数据和功能放到不同的类中，并通过一个指针来访问数据。通过这种方法设计 Widget 类：

```
    class WidgetImpl
    {
    public:
        ...
    private:
        int a,b,c; // 可能有很多数据
        std::vector<double> vec; // 意味着复制需要很长时间
    }
    class Widget
    {
    public:
        Widget(const Widget& rhs);
        Widget& operator=(const Widget& rhs)
        {
            ...
            *pImpl = *(rhs.pImpl); // 复制Widget时，让其复制WindgetImpl对象
            ...
        }
    private:
        WidgetImpl* pImpl; // 指针所指向的对象内包含Widget数据
    }
```

这样一来，当需要交换两个对象时，直接交换指针即可，不需要交换成千上万的数据。但是 std::swap 并不会这么操作，它不止复制三个 Widget，还会复制三个 WidgetImpl 对象。我们需要明确的告诉 std::swap，当传入的对象是 Widget 对象时，需要进行**特殊化**对待。

```
    namespace std
    {
        template<> // 表示这是一个std::swap函数完全特殊化的实现
        void swap<Widget>(Widget& a,Widget& b) // 当T是Widget时，会调用这个版本的函数
        {
            swap(a.pImpl,b,pImpl); // 只需要置换Widget对象的pImpl指针即可
        }
    }
```

通常情况下，我们不能改变 std 命名空间中的任何东西，但是我们可以为 template 设计一个特殊化的版本，让其适用于我们自己的类。但是上面的代码是无法通过编译的，因为其访问了 Widget 对象的私有成员。我们可以把这个函数声明为类的友元函数，但是更常见的做法是在 Widget 类中声明一个 public 的 swap 成员函数，然后将 std::swap 特殊化并调用该成员函数。

```
    class Widget
    {
    public:
        void swap(Widget& other)
        {
            using std::swap;
            swap(pImpl,other.pImpl);
        }
    }

    namespace std
    {
        template<>
        void swap<Widget>(Widget& a,Widget& b)
        {
            a.swap(b);
        }
    }
```

这样就可以通过编译，而且这也是 STL 标准容器实现 swap 的实现方法，STL 也是通过提供 public swap 成员函数和 std::swap 特殊化版本来实现交换功能的。

---

上面讨论的对象是类，如果 WidgetImpl 和 Widget 是类模板的话，我们可以将数据类型加以参数化：

```
    template<typename T>
    class WidgetImpl{...}
    template<template T>
    class Widget{...}
```

但是特殊化 std::swap 是会出现问题，即**部分特殊化**：

```
    namespace std
    {
        template<typename T>
        void swap<Widget<T>>(Widget<T>& a,Widget<T>& b)
        {
            a.swap(b);
        }
    }
```

但是 C++只允许对类模板进行部分特殊化，在函数模板上进行部分特殊化是行不通的，这段代码不应该通过编译。如果要部分特殊化一个函数模板，常见做法是为它添加一个重载版本，即：

```
    namespace std
    {
        template<typename T>
        void swap(Widget<T>& a,Widget<T>& b)
        {
            a.swap(b);
        }
    }
```

重载函数模板是没有问题的，但是 std 命名空间规则比较特殊，用户只能对其中的功能进行特殊化，不允许用户自行扩展。解决办法就是不将重载版本的函数放入 std 命名空间：

```
    namespace WidgetStuff
    {
        template<typename T>
        class Widget<...>
        ...
        template<typename T> // 非成员的swap函数，在用户自定义命名空间内
        void swap(Widget<T>& a,Widget<T>& b)
        {
            a.swap(b);
        }
    }
```

上述代码的好处是能把我们自定义的所有类相关的功能整合在一起，在逻辑上和代码上都更加的简洁。这也符合 C++的函数搜索法则，即实参依赖查找(argument dependent lookup)。

重载函数模板的方法既适用于类也适用于类模板，但是对 std 进行特殊化依然是必要的。如果我们想让 swap 函数适用于多种情况，除了在我们自己的命名空间写一个非成员版本的 swap 函数，在 std 里面依然要特殊化一个 swap。

---

上面讨论的是针对 swap 代码编写者的建议，对于用户来说也有一些需要注意的事项。假设一个函数模板内部需要交换两个对象的值：

```
    template<typename T>
    void doSomething(T& obj1,T& obj2)
    {
        ...
        swap(obj1,obj2);
        ...
    }
```

上述代码中应该调用的 swap 函数是哪一个版本呢？是默认存在的一般化的 std::swap，还是可能存在的特殊化的 std::swap，抑或是可能存在于某个自定义命名空间专属于 T 的 swap 重载函数？最理想的情况是调用专属于类型 T 的自定义 swap，如果没有则使用一般化版本的 std::swap。

```
    template<typename T>
    void doSomething(T& obj1,T& obj2)
    {
        using std::swap; // 令std::swap在此函数可用
        ...
        swap(obj1,obj2); // 为T类型对象调用最佳swap版本
        ...
    }
```

当编译器看到调用 swap 的时候，实参依赖查找会让编译器在全局作用域和实参的命名空间里搜索。例如，如果 T 是 Widget 类型，那么编译器就会查找 Widget 的命名空间的 swap。using 声明式让 std::swap 在函数内曝光，如果编译器在 Widget 命名空间内没有找到 swap，就会调用 std::swap。但是编译器会优先查找特殊化的 std::swap，为了提高效率，需要对其进行特殊化。在调用 swap 函数时应该注意下面的调用方式。编译器只会调用 std 命名空间中的 swap 函数(因此，对 std::swap 进行特殊化是必要的)，而忽略其他命名空间中的 T 专属版本。

```
    std::swap(obj1,obj2);
```

---

到现在为止，我们讨论了默认的 std::swap、std::swap 特殊化版本、成员 swap 函数、非成员 swap 函数、以及对 swap 的调用。我们进行一个总结：

1. 如果默认的 std::swap 对效率没有太大影响，可以直接使用一般化版本。
2. 如果默认的 std::swap 效率不足(类或模板实现了 pimpl 方法)，应该尝试进行以下操作：
   - 提供一个 public swap 成员函数，让它高效的交换自定义类型的两个对象的值，而且这个函数禁止抛出异常(item29)
   - 在类或模板所在的命名空间提供一个非成员 swap 函数，并令它调用上述 swap 成员函数
   - 如果所写的是一个类而非类模板，可以为该类特殊化一个 std::swap，并让其调用 swap 成员函数
3. 如果调用 swap，确定要包含 using 声明式，以便让编译器可以访问 std::swap。另外 swap 的调用不要加 namespace 修饰符。

成员 swap 函数不允许抛出异常，这是因为 swap 的一个重要用途就是帮助类和类模板提供异常安全性。但是非成员的 swap 函数允许抛出异常，因为它是以拷贝构造函数和赋值操作符为基础的，一般情况下 i 昂着都允许抛出异常。当我们需要自定义一个高效的 swap 函数，还注意要保证其异常安全性，但是两者往往是相辅相成的。

**Note：总结**

- 当默认的 std::swap 函数效率不高时，可以在类中提供一个 swap 成员函数，但是要保证其不抛出异常。
- 如果提供了一个成员 swap 函数，相应的要提供一个非成员的 swap 函数来调用这个成员函数。对于类(非模板)来说，还要特殊化 std::swap。
- 在调用 swap 函数时，要针对 std::swap 使用 using 声明式，swap 的调用函数前不加任何命名空间修饰符。
- 为自定义的类型完全特殊化 std 模板是好的，但是不要尝试在 std 命名空间中添加任何东西。
