---
title: Effective C++——实现
date: 
tags: [C/C++, Effective C++]
categories: 编程语言
comments: true
---
本文对应原书的第五部分，主要介绍在接口实现过程中需要注意的事项，包括类型转换、异常安全和内联函数的使用等。
<!--more-->

## item26 尽可能推迟变量定义
只要我们定义了一个变量而其带有一个构造函数或析构函数，那么当程序运行到此处时，就会产生构造成本，当变量的生命周期结束时，会产生析构成本。即使变量没有被使用，这些成本依然存在。我们主观上都会避免定义不使用的变量，然而我们的代码中很有可能产生没有被使用的变量。我们定义一个密码加密的函数：
```
    std::string encryptPassword(const std::string& password)
    {
        using namespace std;
        string encrypted; // 变量定义太早了
        encrypted = password;

        if(password.length() < minPasswordLength)
            throw logic_error("Password too short");
        ....//加密
        return encrypted;
    }
```
在上述代码中，对象`encrypted`并不是完全没有被使用，但是如果函数抛出了异常，它就是一个没有被使用的变量，它所消耗的资源也就浪费了。解决办法就是推迟变量`encrypted`的定义，直到确实需要它时：
```
    std::string encryptPassword(const std::string& password)
    {
        using namespace std;

        if(password.length() < minPasswordLength)
            throw logic_error("Password too short");
       
        string encrypted; // 推迟变量定义
        encrypted = password;
        ....//加密
        return encrypted;
    }
```
上述代码仍然不是最优的，因为`encrypted`虽然定义了但是没有任何实参作为初始值，这意味着调用的是其默认构造函数，然后对其赋值，其效率比直接在构造时指定初始值的效率差(item4)。
```
    std::string encryptPassword(const std::string& password)
    {
        using namespace std;

        if(password.length() < minPasswordLength)
            throw logic_error("Password too short");
       
        string encrypted(password); // 通过拷贝构造函数定义并初始化
        ....//加密
        return encrypted;
    }
```

“尽可能推迟”的真正意义，不仅仅在于推迟定义变量到要使用它的时刻，甚至应该推迟到能够给它初始值为止。这样可以避免构造和析构一些不必要的中间变量，还可以避免无意义的默认构造行为。

对于循环问题，如果变量只是在循环体内使用，那么是把变量定义在循环外比较好，还是应该把它定义在循环内？
```
    // 方法A，在外面定义
    Widget w;
    for(int i=0; i<n, i++)
    {
        w = ...
        ...
    }

    // 方法B，在里面定义
    for(int i=0; i<n; i++)
    {
        Widget w(...);
        ...
    }
```
在`Widget`函数内部，以上两种方法的成本如下：
- 方法A：1次构造+1次析构+n次赋值
- 方法B：n次构造+n次析构

如果对象的一次赋值成本低于一组构造+析构成本，方法A比较高效，尤其是n比较大的时候，否则方法B比较好。但是方法A的作用域比方法B大，程序的可读性和可维护性较差。因此除非(1)明确赋值成本比构造+析构成本低，(2)代码更注重效率，我们默认使用方法B。


**Note：总结**
- 尽可能推迟变量定义，直到它要被用到，且有初始值可用时。这样做可以增加程序的可读性并改善程序效率。



## item27 减少类型转换的使用
C++的语法被设计成类型安全的。因此，如果程序顺利通过了编译并且没有发出任何警告，这表示理论上代码不会包含类型不安全的操作。但是，类型转换会破坏类型系统，可能会带来意想不到的风险。C++中的类型转换有3种方式，C风格、函数风格和C++风格：
```
    (T)expression  // C风格
    T(expression)  // 函数风格
```
两种形式并没有明显区别，纯粹只是小括号的位置不同。我们称这两种形式为旧式转换。C++风格的类型转换有四种方式：
```
    static_cast<T>(expression)
    dynamic_cast<T>(expression)
    const_cast<T>(expression)
    reinterpret<T>(expression)
```
- `static_cast`：C++中的任何隐式转换都是使用static_cast来实现的(C++ Primer)，任何编写程序时能够明确的类型都可以使用static_cast，基本数据类型的转换如int转为double、void指针转为typed指针、基类指针转为派生类指针、non-const对象转为const对象，但是不能将const对象转为non-const对象等。static_cast发生在编译阶段，不提供运行时检查(所以叫static_cast)，所以这种强制转换存在安全隐患。
- `dynamic_cast`：dynamic_cast在运行时进行转换(所以是dynamic_cast)，会进行运行时检查，有一定的安全性，因此会额外消耗一些性能。dynamic_cast用于继承体系中，而且只支持**指向多态类型的指针或引用**，即继承中的派生类对象或有虚函数的基类。可以将派生类指针向上转型为基类指针，也可以将基类指针向下转型为派生类指针，但返回的是空指针。因此dynamic_cast的一个用途就是检测当前指针指向的是基类还是派生类，如果返回空指针，说明其指向基类，如果返回有效指针，指向的就是派生类。
- `const_cast`：const_cast常被用来*移除对象的常量性*，这也是唯一具有此功能的C++类型转换符。去除对象的常量性主要用在函数的值传递中，如果需要将一个const实参传递给non-const形参，就需要移除对象的常量性。此外，const_cast还可以去掉volatile限定符。
- `reinterpret_cast`：用来把某种类型的指针转为其他类型的指针，是一种非常激进的指针类型转换，在编译器完成，可以转换为任何类型的指针，所以极不安全。

相对于旧式转换，建议使用C++风格类型转换。原因是：第一，它们很容易在代码中进行辨识，可以快速定位类型转换的问题；第二，将类型的转换细化，因此编译器能更好的进行诊断。C++风格的类型转换完全能够取代C风格的类型转换，如显式构造函数的类型转换：
```
    class Widget
    {
    public:
        explicit Widget(int size);
        ...
    };

    void doSomething(const Widget& w);
    doSomething(Widget(15));  // 利用函数风格类型转换，
    doSomething(static_cast<Widget>(15)); //C++风格类型转换
```
上面两种方式没有功能上的差异，都会把int转换为临时的Widget对象传递给函数。如果要突出对象的创建，用函数风格，如果要突出类型转换，用C++风格。

---

许多程序员错误地认为，类型转换只是告诉编译器把某种类型视为另一种类型。事实上，无论显式转换还是隐式转换都会产生更多的机器代码。如将int转换成double类型肯定会产生一些代码，因为在大部分计算机体系结构中，int的底层表述不同于double的底层表述。
```
    class Base{...}
    class Derived:public Base{...}

    Derived d;
    Base* pb = &d; // Derived*隐式转换为Base*
```
在上述代码中，我们只是建立一个基类指针指向一个派生类对象，但有时候两个指针的值是不同的。这种情况下，会有一个**偏移量(offset)**在运行期间施加在`Derived*`上，用以获得正确的`Base*` 值。上面例子表明，单一对象(如Derived对象)可能拥有一个以上的地址(如以`Base*`指向它时的指针和以`Derived*`指向它时的指针)。一旦使用多继承，这种情况经常发生，即使在单一继承中也可能发生。
对象的布局方式和它们的地址计算方式随着编译器的不同而不同，因此上面提到的偏移量往往是不固定，在一个平台上的偏移量是这么多，并不代表另一个平台上的编译器生成的偏移量也是这么多。

---

类型转换的另一个有趣现象是，很容易写出看起来正确事实上错误的代码。例如，许多应用框架要求派生类的virtual函数代码的第一个动作是调用基类的对应函数。我们有一个Window基类和一个SpecialWindow派生类，两者都定义了virtual函数onResize，进一步假设SpecialWindow的onResize函数被要求首先调用Window的onResize。下面的实现方式看起来正确，实际上是错的：
```
	class Window
    {
    public:
        virtual void onResize(){...}
        ...
    }
    class SpecialWindow:public Window
    {
    public:
        virtual void onResize()
        {
            static_cast<Window>(*this).onResize(); // 将 *this 转型为Window，然后调用其onResize，这是不可行的。
            ...
        }
    }
```
代码中强调类型转换动作，无论是C++风格类型转换还是旧式类型转换都符合下面的描述。正如预期的那样，上述代码中`*this`转型为Window，也调用了`Window::onResize()`。问题在于，上述类型转换语句中会生成一个副本，这个副本的内容是“`*this`对象中的基类部分”，上述代码并不是在当前对象中调用`Window::onResize()`而是在副本上调用的，然后执行当前对象的专属动作。如果`Window::onResize()`修改了对象的内容，，当前对象事实上并没有被修改，被修改的是副本对象。这样的话调用`Window::onResize()`就失去了其意义。
解决之道就是去除类型转换动作，直接告诉编译器调用基类的的`onResize()`函数：
```
    class SpecialWindow:public Window
    {
    public:
        virtual void onResize()
        {
            Window::onResize(); // 调用Window::onResize()作用于 *this。
            ...
        }
    }
```

---

上面的例子说明，如果要进行类型转换的时候，就要注意可能会出现错误，使用`dynamic_cast`更是如此。首先，dynamic_cast的效率比较慢，因为类型的检测是通过字符串的比较实现的，在深层次继承或者多继承中，调用`strcmp`的次数会很多，如果程序很看重性能，就尤其要注意dynamic_cast。
之所以需要dynamic_cast，通常是因为我们想对派生类进行操作，但是我们持有的是一个基类指针，需要通过dynamic_cast判断其指向的是基类对象还是派生类对象。通常有两种方法来避免dynamic_cast的使用。

**第一种方法**，使用容器存储指向派生类对象的指针(通常是智能指针，item13)，这样就避免了需要通过基类指针来操作派生类。如我们在SpecialWindow中增加闪烁功能：
```
    class Window{...}
    class SpecialWindow:public Window
    {
    public:
        void blink();
        ...
    }

    typedef std::vector<std::shared_ptr<Window>> VPW;
    VPW winPtrs;
    ...
    for(VPW::iterator iter = winPtrs.begin();
        iter != winPtrs.end();
        ++iter)
    {
        if(SpecialWindow* psw = dynamic_cast<SpecialWindow*>(iter->get()))
        {
            psw->blink();
        }
    }
```
上面的代码中使用了dynamic_cast来判断基类指针是否指向派生类对象，我们希望避免使用dynamic_cast。可以这样做：
```
    typedef std::vector<std::shared_ptr<SpecialWindow>> VPSW;
    VPSW winPtrs;
    
    for(VPSW::iterator iter = winPtrs.begin();
        iter != winPtrs.end();
        ++iter)
    {
        (*iter)->blink();
    }
```
这种方法的缺陷是容器中只能存储一种类型的派生类对象，如果像保存多种类型的派生类，就需要多个这样的容器。

**第二种方法**，就是在基类中提供一个virtual函数，通过基类对象调用这个函数的方式实现派生类的功能(其实就是多态)。例如，虽然只有SpecialWindow可以闪烁，但是我们可以在基类中声明一个不予实现的`blink()`函数。
```
    class Window
    {
        virtual void blink(){} // 执行代码缺省
        ...
    }
    class SpecialWindow:public Window
    {
    public:
        void blink(){...};
        ...
    }

    typedef std::vector<std::shared_ptr<Window>> VPW;
    VPW winPtrs;  // 容器，内含指针，指向所有可能的Window类型
    ...
    for(VPW::iterator iter = winPtrs.begin();
        iter != winPtrs.end();
        ++iter)
    {
        (*iter)->blink();
    }
```

上面两种方法，即使用包含派生类指针的容器和使用包含多态基类指针的容器，可能并不百分百适用于所有场合，但是它们确实能替换dynamic_cast。

---

还有一种需要避免的糟糕代码，就是dynamic_cast串联出现：
```
    class Window{...}
    class SpecialWindow1:public Window
    {
    public:
        void blink();
        ...
    }
    ... // 其他类型的派生类
    typedef std::vector<std::shared_ptr<Window>> VPW;
    VPW winPtrs;
    ...
    for(VPW::iterator iter = winPtrs.begin();
        iter != winPtrs.end();
        ++iter)
    {
        if(SpecialWindow* psw = dynamic_cast<SpecialWindow1*>(iter->get()))
        {...}
        else if(SpecialWindow* psw = dynamic_cast<SpecialWindow2*>(iter->get()))
        {...}
        else if(SpecialWindow* psw = dynamic_cast<SpecialWindow3*>(iter->get()))
        {...}
    }
```
上面的代码不仅生成大量低效率的机器代码，而且极其脆弱，每次Window类的继承体系有变化，所有的这类代码都必须重新检查是否需要修改。例如，如果有新的派生类，就需要添加分支语句。

良好的C++代码很少使用类型转换，但是类型转换又是无法完全避免的，如int转为double。我们能做的就是尽可能隔离转型操作，通常是将它隐藏在函数内，函数接口可以保护使用者不受函数内部任何不良操作的影响。


**Note：总结**
- 如果可以就要尽量避免类型转换，尤其是在注重效率的代码中要避免dynamic_cast。如果需要进行类型转换，尽量用其他方案替代。
- 如果类型转换是必要的，就把它封装在函数内，这样用户只能调用接口而自己的代码中就不会出现类型转换。
- 尽量使用C++风格的类型转换替换C风格类型转换，有助于在程序中进行识别和定位。


## item28 避免返回指向对象内部成员的句柄
假设我们要写一个表示矩形的类，每个矩形由其左上角和右下角表示。为了让Rectangle对象尽可能小，我们选择把定义矩形的点放在一个结构体内，再让Rectangle指向它。
```
    class Point
    {
    public:
        Point(int x,int y);
        void setX(int x);
        void setY(int y);
    }

    struct RectData
    {
        Point ulhc;
        Point lrhc;
    };
    class Rectangle
    {
        ...
    private:
        std::shared_ptr<RectData> pData;
    }
```
Rectangle的用户想要获得矩形的点，所以提供了两个返回点的函数。Point是一个自定义类型，根据item20的建议，返回一个引用(引用方式传递用户自定义类型往往比值传递高效)：
```
    class Rectangle
    {
    public:
        Point& upperLeft() const{return pData->ulhc;}
        Point& lowerRight() const(return pData->lrhc;)
    }
```
上述代码可以通过编译器的编译，但是却是错误的。一方面`upperLeft()`和`lowerRight()`两个函数被声明为const成员函数，因为它们只是返回一个值，而不需要用户对值进行修改。另一方面，两个函数却都返回了一个指向内部private成员变量的引用，调用者可以通过引用修改内部数据，如：
```
    Point coord1(0,0);
    Point coord2(100,100);
    const Rectangle rec(coord1,coord2);

    rec.upperLeft().setX(50); // coord1的坐标变为(50,0)
```
从上面我们可以得到两点教训：
1. 成员变量的封装性取决于破坏封装的函数，本例中的ulhc和lrhc虽然声明为private，但事实上却是public的，因为`upperLeft()`和`lowerRight()`两个public函数传递出了它们的引用。
2. 如果一个函数返回了指向储存在对象外部的数据成员的引用，即使这个函数声明为了const，调用这个函数的人也能修改这个成员。这正是item3中数据常量性的局限性。

除了引用，成员函数返回指针或迭代器也会因为相同的原因产生相同的结果。引用、指针和迭代器都是我们这里所讲的**句柄**，即接触对象的某种方式。直接返回句柄总会带来破坏封装性的风险，这也导致声明为const的函数并不是真正的const。
内部成员除了内部数据外还包括内部函数，即声明为private或protected的函数，因此也不要返回它们的句柄，否则用户也可以通过返回的函数指针来调用它们，这样私有的成员也相当于变成了公有。
如果要解决返回引用会导致成员变量被改变的问题，只需要给函数的返回类型加上const，这样用户就只能对其进行读操作而不能进行写操作了。
```
    class Rectangle
    {
    public:
        const Point& upperLeft() const{return pData->ulhc;}
        const Point& lowerRight() const(return pData->lrhc;)
    }
```

---

虽然解决了封装性的问题，但是返回句柄还会导致**野句柄**的出现，即这个句柄所指向的对象不存在了。野句柄最常见的来源就是函数的返回值。假如我们给某个GUI对象写一个返回它边界框的函数，返回类型是Rectangle：
```
    class GUIObject{...} // GUI对象
    const Rectangle boundingBox(const GUIObject& obj);

    // 用户可能使用下面的代码
    GUIObject* pgo;
    ...
    const Point* pUpperLeft = &(boundingBox(*pgo).upperLeft());
```
在上面的代码中，取值运算符括号里面的boundingBox函数会返回一个临时的Rectangle对象，我们称它为temp。随后取得其左上角的Point对象，pUpperLeft获得了这个对象的地址。可是temp是一个临时对象，这行代码运行完之后，temp就会被销毁，最后pUpperLeft存了指向一个并不存在的对象的指针。

这就是为什么返回指向内部成员的句柄是危险的，不论这个句柄是指针或迭代器或引用，也不论这个handle是否为const，也不论返回handle的函数是否为const。但这并不意味着绝对不能让成员函数返回handle。有时候是必须的，如operator[] (索引操作符)，用来获取容器中某个对象，他返回的就是指向容器里对象的引用，让用户能够完成写操作。但在我们的程序中，应该要尽力避免。


**Note：总结**
- 要避免返回指向内部成员的句柄，包括指针、引用、迭代器，这样可以增加封装性，并减少野句柄的。


## item29 保证代码的异常安全性
假设有一个可进行背景替换的菜单GUI类，我们希望这个类能用于多线程环境：
```
    class PrettyMenu
    {
    public:
        void changeBackground(std::istream& imgSrc); // 改变背景图像
        ...
    private:
        Mutex mutex;  // 互斥器
        Image* bgImage; // 目前的背景图像
        int imageChange; // 背景图被改变的次数
    }
```
下面是`changeBackground`函数的一个可能实现：
```
    void PrettyMenu::changeBackground(std::istream& imgSrc)
    {
        lock(&mutex); // 锁定互斥器
        delete bgImage; // 释放旧背景图
        ++imgChange;  // 更新计数器
        bgImage = new Image(imgSrc); // 设置新的背景图
        unlock(&mutex); //解除锁定
    }
```
所谓的**异常安全性**是指当异常发生时，1)不会发生资源泄漏，2)不会破坏数据。上述代码就没有实现这两点，当`new Image(imgSrc)`发生异常，unlock不会被调用，mutex就会一直被锁住；同时，imageChanges被改变，bgImage指向一个被删除的对象。
解决资源泄漏问题很容易，可以使用item13中提到的用对象管理资源，item14中也介绍了Mutex的安全管理类Lock。
```
    void PrettyMenu::changeBackground(std::istream& imgSrc)
    {
        Lock lock(&mutex); // RAII资源管理类
    }
```
解决了资源泄漏的问题，接下来我们关注数据破坏的问题。函数的异常安全性有三种级别：
1. **基本保证**：如果异常发生，程序内的任何对象都保持在有效的状态，没有任何数据和对象被破坏，所有对象也处于前后一致的状态。
2. **强烈保证**：如果异常发生，程序状态不变。这就意味这对强烈保证函数的调用是原子性的：如果函数成功，则完全成功；如果函数失败，程序会恢复到函数调用前的状态。
3. **不抛出异常保证**：函数绝不抛出异常，因为它们总能完成它们的功能。例如所有对基本类型(int，指针等)的操作都提供不抛出异常保证，它是异常安全代码的基础。

假设我们给函数规定一个空的异常规范，那这个函数貌似提供了最安全的不抛出异常保证。
```
    void doSomething() throw(); // 空的异常规范
```
事实上，`doSomething()`函数并不是不会抛出异常，而是如果它抛出了异常就是严重错误，然后出发`unexpected()`函数，而`unexpected()`函数会触发`terminate()`。所以，`doSomething()`没有提供任何异常安全保证。函数的声明无法提供任何安全性保证，而是由函数的实现决定的。

---

异常安全的代码必须满足上述三种保证之一，如果不这样做，代码就不具备异常安全性。我们需要考虑的是选择为函数提供哪种保证。一般而言会想到提供最强烈的保证——不抛出异常，然而这是很难做到的，任何使用动态内存的东西(如STL容器)都会在内存不足时抛出bad_alloc异常(item49)。如果可以的话为函数提供不抛出异常保证，但是对大部分函数而言，我们通常在基本保证和强烈保证中做出选择。
对于`changeBackground()`提供强烈保证不困难。首先，改变PrettyMenu的bgImage成员变量的类型，使用智能指针来管理资源。第二，重新排列changeBackground内语句的次序，在更换图像后再更新imageChange的值。
```
    class PrettyMenu
    {
        std::shared_ptr<Image> bgImage;
    }

    void PrettyMenu::changeBackground(std::istream& imgSrc)
    {
        Lock lock(&mutex); // RAII资源管理类
        bgImage.reset(new Image(imgSrc)); // 设定bgImage内部指针
        ++imageChange;
    }
```
通过智能指针来管理资源，不再需要手动delete图像。删除图像的操作发生在新图像被成功创建后，delete只在reset函数内部被调用。这两个操作几乎可以保证`changeBackground()`提供强烈的异常安全保证。美中不足的是imgSrc参数，如果istream&类型的数据不可读，Image构造函数可能会抛出异常，所以最坏的情况下`changeBackground()`只提供基本保证。

有一个一般化的策略可以提供异常安全性的强烈保证，即**拷贝和交换**(copy and swap)。其基本原理是：给想要改变的对象创建一个副本，然后对副本进行操作，当成功执行完所有操作，将副本和原对象交换。如果其中任何一步抛出异常，原对象不会改变。
实现方法通常是将对象的数据放到另一个对象内部，即item25中提到的pimpl方法。对PrettyMenu而言，典型的写法如下：
```
    struct PMImpl
    {
        std::shared_ptr<Image> bgImage;
        int imageChange;
    }
    class PrettyMenu
    {
    private:
        Mutex mutex;
        std::shared_ptr<PMImpl> pImpl;
    }
    void PrettyMenu::changeBackground(std::istream& imgSrc)
    {
        using std::swap;
        Lock lock(&mutex); 
        std::shared_ptr<PMImpl> pNew(new PMImpl(*pImpl)); // 获得副本数据
        pNew->bgImage.reset(new Image(imgSrc)); // 修改副本
        ++(pNew->imageChange); // 交换数据

        swap(pImpl,pNew);
    }
```
上述代码中使用struct来封装数据，它的封装性可以被PrettyMenu的私有接口保证，要接触到其中的数据要先通过PrettyMenu的私有接口。使用类也可以，但可能不方便。

**拷贝和交换**策略可以保证对象的原子性，但是它并不能保证整个函数有强烈的异常安全性。为了了解原因，我们可以看下述代码：
```
    void someFunc()
    {
        ... // 对local对象进行拷贝
        f1();
        f2();
        ... // 将修改后的对象进行交换
    }
```
如果`f1()`和`f2()`的异常安全性比 *强烈保证* 低，那么很难为`someFunc()`提供强烈保证。例如，`f1()`只提供 *基本保证* ，为了让`someFunc()`提供强烈保证，我们需要获取`f1()`之前整个程序的状态、捕获`f1()`所有可能的异常、然后恢复状态。即使`f1()`和`f2()`都提供强烈保证，依然存在风险，如果`f1()`正常运行，已经对对象进行了改变，`f2()`运行过程中抛出异常，程序状态和以前已经不同了。
问题在于连带影响，如果函数只操作局部状态，提供强烈保证相对容易。但是当函数对非局部数据有影响时，提供强烈保证就困难的多。另一个重要的问题就在于效率，**拷贝和交换**要对修改的对象进行拷贝，这会消耗额外的资源。因此，当提供强烈保证比较困难时，我们必须提供*基本保证*。


**Note：总结**
- 异常安全的函数在抛出异常时也不会泄漏资源和破坏数据。异常安全性有三种级别：基本保证、强烈保证和不抛出异常保证。
- 强烈保证可以通过**拷贝和交换**来实现，但是强烈保证并非对所有函数都可实现或具有实际意义。
- 函数提供的异常安全保证通常最高只等于其调用的各个函数中最弱级别的异常安全性。


## item31 透彻了解inline函数
内联函数有众多优点，相较于宏来说它更像函数(item2)，又不会像调用普通函数那样产生额外的开销。因为编译器最优化机制可以优化那些没有进行函数调用的代码，所以当我们在代码中使用inline函数时，意味着编译器可能对它进行语境相关的最优化。
内联函数的基本原理是在函数调用的地方替换为函数本体，这就意味着可能会增加目标代码的大小。而且inline函数只是对编译器的一个申请，不是强制命令，是否将其作为inline函数使用由编译器进行判断。

---

声明inline函数的做法是在其定义式(非声明式)前面加上关键字inline。例如标准的max模板函数实现方式为：
```
    template<typename T>
    inline const T& std::max(const T& a,const T& b)
    {
        return a < b?b:a;
    }
```
inline函数通常定义在头文件中，这是因为inlining行为在大多数C++程序中是编译器行为，为了将函数调用替换为函数本体，编译器必须要了解函数的实现。template也通常被放在头文件中，以为它一旦被调用，编译器为了将它具体化，也需要了解函数的具体实现。
template的具体化和inlining无关。如果我们要将一个模板函数具体化出来的所有函数都进行inlining，就可以将此template声明为inline。如果模板函数不要求具体化的所有函数inlining，就应该避免将该template声明为inline。

---

一个声明为inline的函数是否进行inlining，取决于编译器。
- 大部分编译器拒绝将太过复杂的函数(如带有递归或循环)进行内联；
- 编译器也会拒绝将虚函数内联，这是因为虚函数意味着在运行时才确定调用的函数，而内联发生在编译期；
- 如果程序要获取inline函数的地址，编译器通常会为此函数生成一个非内联的函数体；
- 编译器不会对通过函数指针调用的函数进行inlining。

```
    inline void f(){...}
    void (*pf) = f;
    f(); // 函数会被inlining
    pf(); // 通过函数指针调用，不会被inlining
```

---

对于下面的代码：
```
    class Base
    {
    public:
        ...
    private:
        std::string str1;
    }

    class Derived:public Base
    {
    public:
        Derived(){}; // 构造函数看着是空的
    private:
        std::string str1,str2;
    }
```
上面代码中，派生类的构造函数中没有任何代码，看起来是inline函数的绝佳候选。为了分析问题，我们需要了解对象被创建和销毁时发生了什么。当创建一个对象时，其每一个基类和每一个成员变量都会被自动构造，当销毁一个对象时，反向程序的析构行为也会自动发生。编译器会在编译期产生这些功能代码并将其插入到程序中，有时候就放在构造和析构函数中。编译器为上面看起来空的Derived构造函数产生的代码相当于下面：
```
    Derived::Derived()
    {
        Base::Base();
        try{ str1.std::string::string(); }
        catch(...)
        {
            Base::Base();
            throw;
        }
        try{ str2.std::string::string(); }
        catch(...)
        {
            data1.std::string::~string();
            Base::~Base();
            throw;
        }
        ...
    }
```
上述代码并不代表编译器生成的真正代码，但已经反映出编译后的Derived空白构造函数。事实上构造函数和析构函数在函数中被大量的调用，如果全部inlining的话，这些调用都会被扩展为函数体，势必造成目标代码膨胀。
    

**Note：总结**
- inline函数主要用在功能简单、被频繁调用的函数上。这既可以使代码膨胀最小化，也能最大化提升程序的速度。
- 不要因为template也出现在头文件中，就将其声明为inline.


## item31 最小化文件之间的编译依赖关系
在C++中，class不仅详细描述了其外部接口，也给出了内部的实现：
```
    class Person
    {
    public:
        Person(const std::string& name,const Date& birthday,const Address& addr);
        std::string name() const;
        std::string birthday() const;
        std::string address() const;
        ...
    privare:
        std::string theName;
        Date theBirthday;
        Address theAddress;
    }
```
上述代码要通过编译器的编译还需要引入`std::string`，`Data`和`Address`的定义式，这样的定义式通常式由`#include`提示符来实现的。因此上述代码最开始的地方可能存在代码：
```
    #include <string>
    #include "date.h"
    #include "address.h"
```
但是这样一来，`Person`的定义文件和其包含文件之间就形成了编译依赖关系。如果依赖的头文件有任何修改，或者这些头文件依赖的其他文件被改变，那么Person的文件就得重新编译，任何使用Person的文件也必须重新编译。这样的一连串的编译依赖关系对于项目来说就是巨大的灾难。

---

为了解决过度依赖问题，我们可以尝试使用前置声明来替代#include。通过前置声明，可以让Person在其自身接口被修改时才重新编译。
```
    namespace std
    {
        class string; // 这是错误的前置声明
    }
    class Date;
    class Address;
    class Person
    {
    public:
        Person(const std::string& name,const Date& birthday,const Address& addr);
        std::string name() const;
        std::string birthday() const;
        std::string address() const;
        ...
    privare:
        std::string theName;
        Date theBirthday;
        Address theAddress;
    }
```
上述代码存在两个问题。第一，string不是一个类，它是一个typedef(定义为basic_string<char>)，因此上述关于string的前置声明不正确，而且我们也不应该手动声明标准程序库。另外，标准头文件不太可能成为编译瓶颈。第二，编译在编译期间必须要知道对象的大小，获取这项信息的唯一办法就是访问class的定义式。前置声明可以合法的不列出实现的细节，那编译器就无法确定该分配多少空间。
解决上述问题的办法就是：1)标准程序库文件不适用前置声明；2)定义一个指针，让其指向实现细节的对象。
```
    #include <string>  // 标准库头文件不使用前置声明
    #include <memory>

    class PersonImpl;
    class Date;
    class Address;
    class Person
    {
    public:
        Person(const std::string& name,const Date& birthday,const Address& addr);
        std::string birthday() const;
        std::string address() const;
        ...
    privare:
        std::shared_ptr<PersonImpl> pImpl; // 指向实现细节的指针
    }
```
上述代码使用了pimpl方法来实现类，Person类中只有一个指针成员，编译器只需要给指针分配空间即可。这样的设计之下，Person的接口就完全和Date，Address和Person的实现细节进行了分离。这些class的任何修改都不需要Person重新编译，这就是**接口与实现分离**。

---

接口与实现分离的关键在于**以声明依赖替换定义依赖**，这也是编译依赖最小化的本质，即**为每个类单独提供声明文件和定义文件，如果无法实现，就让其依赖其他文件的声明式而非定义式**。最小化编译依赖的设计策略：
1. **尽可能使用对象引用或者对象指针来替换直接使用对象**。通过一个类型声明式就可以定义出指向该类型的引用和指针，如果定义该类型的对象，就需要访问该类型的定义式。
2. **用class声明式代替class定义式**。当我们声明一个函数而它又用到某个class时，其并不会用到该class的定义式，即使函数是以值传递的方式传递该类型参数或返回值也是一样的。
```
    class Date; // class声明式
    Date day(); // 函数返回Date类型对象
    void clearAppointments(Date d); // 
```
上述代码中，声明today函数和clearAppointments函数不需要Date的定义。这是因为如果有人调用上面两个函数，那么Date的定义在函数调用前已经且必须被访问到。
3. **为声明式和定义式提供不同的头文件**。即我们需要提供两个头文件，一个用于声明式，一个用于定义式，这些文件必须保持一致。声明式头文件中包含了各个类型的声明，对应的定义分布在不同的定义式头文件中。
```
    #include "datefwd.h" // 头文件内声明了class Date
    Date day(); 
    void clearAppointments(Date d); 
```
我们应该在程序中`#include`声明式头文件，而不是在文件中重复前置声明多个函数。`datefwd.h`的命名方式取自C++标准库头文件的`<iosfwd>`，`<iosfwd>`内包含了iostream各个组件的声明式，其对应的定义分布在不同的头文件内，包括`<sstream>`，`<streambuf>`，`<fstream>`和`<iostream>`。
`<iosfwd>`另外一个启发意义在于，上述建议template和non-template同样适用，虽然许多编译环境中template的定义式通常放在头文件中，但是C++提供关键字`export`语序将template声明式和定义式放在不同的文件内。

像Person这样使用pimpl方法法的类常被称为**句柄类(Handle Classe)**。当PersonImpl的内部实现发生改变时，Person的代码不需要重新编译。让Person变成一个句柄类不会改变它的功能，只是改变了其完成功能的方式。
```
    #include "Person.h" // 包含class Person的定义式
    #include "PersonImpl.h"  // 包含PersonImpl的定义式，否则无法调用其成员函数

    Person::Person(const std::string& name,const Date& birthday,const Address& addr)
    :pImpl(new PersonImpl(name,birthday,addr))  // 初始化智能指针指向的对象
    {}
    std::string Person::name() const
    {
        return pImpl->name();
    }
```

---

另一种设计句柄类的方法是，让Person成为一种特殊的抽象基类(abstract base class)，称为**接口类(Interface Class)**。这个类的作用是为派生类提供接口，因此接口类中通常没有成员变量，没有构造函数，只有一个virtual析构函数和一组描述接口的纯虚函数。针对Person的接口类可能如下所示：
```
    class Person
    {
    public:
        virtual ~Person();
        virtual std::string name() const =0;
        virtual std::string birthday() const =0;
        virtual std::string address() const =0;
        ...
    }
```
和Handle类一样，除非Interface类的接口发生变换，否则不需要重新编译。用户必须适用Person的指针或引用来写程序，因为包含纯虚函数的类无法实例化。因此，接口类的用户必须有办法创建新的对象。常用的方法是调用一个特殊的函数，这个函数能够像构造函数一样执行对派生类的实例化。这样的函数一般称为工厂(factory)函数或virtual构造函数，该函数返回一个指针指向动态分配的对象。这样的函数在接口类中一般声明为static：
```
    class Person
    {
    public:
        static std::shared_ptr<Person> create(const std::string& name,
                                            const Date& birthday,const Address& addr);
    }
    // 适用工厂函数初始化对象
    std::string name;
    Date dateOfBirth;
    Address address;
    ...
    // 创建一个对象
    std::shared_ptr<Person> pp(Person::create(name,dateOfBirth,address));
    ...
    std::cout << pp->name() // 通过Person接口使用这个对象
              << " was born on "
              << pp->birthDate()
              << " and lives at "
              << pp->address();
```
当然，必须定义支持支持接口类的具体类而且真正的构造函数必须被调用。假设Person接口类有一个派生类RealPerson且能够被实例化，RealPerson提供继承来的virtual函数的实现：
```
    class RealPerson:public Person
    {
    public:
        RealPerson(const std::string& name,const Date& birthday,const Address& addr)
        :theName(name),theBirthDate(birthday),theAddress(addr)
        {}
        virtual ~RealPerson(){}
        std::string name() const;
        std::string birthDate() const;
        std::string address() const;
    private:
        std::string theName;
        Date theBirthDate;
        Address theAddress;
    }

    std::shared_ptr<Person> Person::create(const std::string& name,
                                           const Date& birthday,
                                           const Address& addr)
    {
        return std::shared_ptr<Person> (new RealPerson(name,birthday,addr));
    }
```
Person::create能够创建不同派生类型的对象。RealPerson示范了实现接口类的两个最常见机制之一，即从接口类继承它的接口规格，然后实现接口中的函数。另一个机制设计多继承，即item40的主题。

Handle类和Interface类解除了接口和实现之间的耦合关系，从而降低文件之间的编译依赖性。在Handle类中，成员函数通过实现指针取得对象的数据，而且必须在存储每一个对象所需的内存中增加存储指针的内存。最后，实现指针必须通过Handle类的构造函数进行初始化，让其指向一个动态分配得到的对象。Interface类的每个函数是virtual，每次调用函数都需要通过虚指针去进行访问，而且Interface类的派生类都包含一个虚函数表，这会增加对象的大小。


**Note：总结**
- 最小化编译依赖性的一般原则是：用声明依赖替代定义依赖。基于此原则的两个方法是Handle类和Interface类。
- 程序库头文件应该以完整并且只有声明的形式存在，无论是否涉及template都适用。
