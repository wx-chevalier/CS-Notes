本文对应原书第三部分，主要记录 C++中资源管理需要注意的事项，包括用对象管理资源、对象的复制行为、资源的释放以及智能指针等。

<!--more-->

## item13 使用对象来管理资源

我们使用系统资源必须遵循`申请资源`->`使用资源`->`释放资源`这样一个完成的步骤，如果资源使用后没有释放就会造成资源泄漏。在编程中我们通过手动管理资源往往会存在着因为异常或者逻辑不合理跳过了资源释放，或者忘记释放资源等问题。为了避免以上情况出现，我们可以利用 C++的对象析构函数自动调用机制，把资源封装在对象里面，当对象的生命周期结束，资源会保证被释放，也就是 RAII 规范。其基本思想就是**在构造函数中申请资源，在析构函数中释放资源**。

**Note：总结**

- 为防止资源泄漏，使用 RAII 机制来进行资源管理，通过构造函数获取资源，利用析构函数确保资源被释放。
- C++中常用的 RAII 实例就是智能指针。`shared_ptr`使用引用计数来管理资源。

## item14 注意资源管理类的拷贝行为

现在假设我们用 RAII 规范来管理一个互斥锁，保证该互斥锁最后能被解除。

```
    class Lock
    {
        public:
            explicit Lock(Mutex* pm):mutexPtr(pm)
            {lock(mutexPtr);}
            ~Lock(){unlock(mutexPtr);}
        private:
            Mutex* mutexPtr;
    }

    /* 接下来我们使用这个类*/
    Mutex m;
    Lock lock1(&m);    // 锁定m
    Lock lock2(lock1); // 将lock1复制到lock2
```

在上述代码中，发生了资源的复制。在用 RAII 规范来管理资源类的时候，特别要注意这个类的拷贝行为。对于资源管理类的拷贝，通常可以采取以下几种措施：

1. **禁止拷贝**
   对于有些资源来说，比如上面提到的互斥锁，它的拷贝是没有意义，我们可以通过将其拷贝构造函数和赋值运算符显式声明为 private 来禁止外部使用其拷贝功能。

```
    class Lock
    {
        public:
            explicit Lock(Mutex* pm):mutexPtr(pm)
            {lock(mutexPtr);}
            ~Lock(){unlock(mutexPtr);}
        private:
            /* 私有化其拷贝函数 */
            Lock(const Lock& lock);
            Lock& operator=(const Lock& lock);
            Mutex* mutexPtr;
    }

```

2. **使用引用计数**
   有时我们希望保有资源，直到最后一个使用者被销毁。这种情况下复制 RAII 对象时，应该将该资源的引用计数递增。`shared_ptr`就是使用的这种机制，`shared_ptr`提供了一个特殊的可定义函数——删除器(deleter)，即在其引用计数为零时调用。

```
    class Lock
    {
        public:
            explicit Lock(Mutex* pm):mutexPtr(pm，unlock) // 将unlock函数绑定到删除器
            {lock(mutexPtr.get());}   // 锁定原始指针
            // 这里不再需要定义析构函数来释放资源 ~Lock(){unlock(mutexPtr);}
        private:
            std::shared_ptr<Mutex> mutexPtr; // 使用shared_ptr，不再使用原始指针
    }
```

3. **深度拷贝**
   当我们需要的不仅仅是资源的所有权，而是要获取资源的副本的时候，我们就需要拷贝底层资源，即不仅仅是指向资源的指针，在内存上的资源也要进行复制，也就是“深拷贝”。

4. **所有权转移**
   当我们只想要一个 RAII 对象来持有这个资源，在进行拷贝的时候就要进行所有权的转移，即释放原对象对资源的权限，将所有权转移到新的对象上，这也是`auto_ptr`的工作原理。

**Note：总结**

- RAII 对象的拷贝要根据其管理的资源具体考虑，资源的拷贝行为决定 RAII 对象的拷贝行为。
- 常用的 RAII 对象的拷贝行为有：禁止拷贝、使用引用计数、深度拷贝、所有权转移。

## item15 在资源管理类中提供对原始资源的访问

资源管理类通过对原始资源的封装可以有效避免资源泄漏，但是在很多情况下外部的 API 需要直接访问原始资源，因此我们需要在资源管理类中提供对原始资源的访问。提供访问的方式有两种：_显式访问_ 和*隐式访问*。其中，显示访问比较安全，隐式访问便于使用。

- 显式访问：在 RAII 类中声明一个显式的转换函数，返回原始资源类型的对象
- 隐式访问：利用 operator 操作符声明一个隐式转换函数

1. **智能指针获取原始指针**
   智能指针都有一个成员函数`get()`，来执行显式转换，返回智能指针对象包含的原始指针：

```
    mutexPtr.get(); // 获取指向互斥锁的原始指针
```

智能指针重载了指针解引操作符(operator->和 operator\*)，它们允许隐式的将智能指针转换成底部的原始指针。

```
    class Investment
    {
        public:
            bool isTaxFree() const;
    }

    Investment* pInv;
    std::shared_ptr<Investment> ptr1(pInv); // 使用shared_ptr管理资源
    bool tax1 = ptr1->isTaxFree(); // 使用operator->访问原始资源
    std::auto_ptr<Investment> ptr2(pInv); // 使用auto_ptr管理资源
    bool tax1 = (*ptr2).isTaxFree(); // 使用operator*访问原始资源
```

2. **自定义 RAII 类获取原始资源**
   同智能指针类似，在自定义的 RAII 类中可以通过定义一个`get()`函数来显式的返回原始资源，也可以通过 operator 操作符声明一个隐式转换函数。

**Note：总结**

- API 往往需要访问原始资源，因此 RAII 类应该提供一个访问原始资源的方法。
- 对原始资源的访问有显式和隐式两种。一般而言，显式比较安全，隐式便于使用。

## item16 使用 new/delete 形式要对应

为了保证内存的释放，我们经常会特别注意 new/delete 成对的出现。对于下面的代码，使用了 new 也有对应的 delete，但还是有错误。

```
    std::string* strArray = new std::string[100];
    ...
    delete strArray;
```

当使用 new 来创建一个对象时，发生了两件事：第一，通过 operator new 操作符分配内存；第二，针对此内存，有一个或多个构造函数被调用。同理，使用 delete 时也有两件事发生：针对此内存有一个或多个析构函数被调用，然后通过 operator delete 释放内存。delete 最大的问题在于**即将被删除的内存中到底有多少个对象**？这个答案决定了有多少个析构函数被调用。
确定 delete 要删除多少个对象，就需要明确告诉 delete 被删除的指针指向的是单一对象还是一个数组。这是因为单一对象的内存布局不同于数组的内存布局。数组对象中包含了一个表明数组大小的记录，以便 delete 知道需要调用多少次析构函数。单一对象的内存中没有这个记录。
让 delete 知道内存中是否存在一个数组大小记录的方法，就是显式的声明，如果使用 delete 时加上`[]`，delete 便认定指向的是一个数组，否则便认定指向的是一个单一对象。

```
    std::string* strPtr1 = new std::string;
    std::string* strPtr2 = new std::string[100];
    ...
    delete strPtr1;  // 使用delete删除单个对象
    delete[] strPtr2;// 使用delete[]删除数组
```

**Note：总结**

- 主要针对单个对象，应该使用 new 对应 delete，对于对象数组，new[] 要对应 delete[]。

## item17 用单独的语句来创建智能指针

假设有如下函数：

```
    int priority(); // 返回程序的优先级
    void processWidget(std::shared_ptr<Widget> pw, int priority);

    /* 通过以下语句来调用processWidget */
    processWidget(std::shared_ptr<Widget>(new Widget),priority());
```

上述调用 processWidget 的代码中使用了智能指针来管理资源，但是仍然存在资源泄漏的可能。
在调用 processWidget 函数之前，编译器需要完成三件事：调用`priority()`函数；执行`new Widget`；调用`shared_ptr构造函数`。其中，`new Widget`的执行在调用`shared_ptr构造函数`之前，但是调用`priority()`函数的次序是不一定的。这是因为在 C++中，为了保证代码效率，不是以固定的顺序来解析函数参数的。假设编译器生成的代码执行顺序是：

- 1、执行`new Widget`；
- 2、调用`priority()`函数；
- 3、调用`shared_ptr构造函数`。
  如果调用`priority()`函数的过程中抛出异常，`new Widget`返回的指针就会遗失，因为它还没有被放入智能指针中，从而造成资源泄漏。
  为了避免上述问题，需要用单独的语句来创建智能指针，从而保证两个动作不会被隔离，即将 new 的对象立即放入智能指针。

```
    std::shared_ptr<Widget> pw(new Widget);
    processWidget(pw,priority());
```

**Note：总结**

- 用单独的语句创建智能指针，否则可能导致难以察觉的资源泄漏问题。
