
# Definition
## Constructor&Destructor:构造函数与析构函数
 C++提供了构造函数(constructor)来处理对象的初始化。在建立对象时自动执行。构造函数的名字必须与类名同名，它不具有任何类型，不返回任何值。构造函数的功能是由用户定义的，用户根据初始化的要求设计函数体和函数参数。在类对象进入其作用域时调用构造函数。构造函数没有返回值，因此也不需要在定义构造函数时声明类型，这是它和一般函数的一个重要的不同之点。
      如果构造函数不带参数，在函数体中对数据成员赋初值，使该类的每一个对象都得到同一组初值。采用带参数的构造函数，在调用不同对象的构造函数时，从外面将不同的数据传递给构造函数，以实现不同的初始化。构造函数首部的一般格式为  构造函数名(类型 1 形参1，类型2 形参2，…)
实参是在定义对象时给出的。定义对象的一般格式为 
类名 对象名(实参1，实参2，…);
#include <iostream>
using namespace std;
class Box
{
public:
 Box(int,int,int);
 int volume();
private:
 int height;
 int width;
 int length;
};
Box::Box(int h,int w,int len)
{
height=h;
width=w;
length=len;
}
int Box::volume()
{
return(height*width*length);
}
int main( )
{
Box box1(12,25,30);
cout<<"the volume of box1 is"<<box1.volume()<<endl;
Box box2(15,30,21);
cout<<"the volume of box2 is"<<box2.volume()<<endl;
return 0;
}


### 参数初始化列表
C++还提供另一种初始化数据成员的方法——参数初始化表来实现对数据成员的初始化。这种方法不在函数体内对数据成员初始化，而是在函数首部实现。
Box∷Box(int h,int w,int len):height(h)，width(w)，length(len){ }
构
造函数具有相同的名字，而参数的个数或参数的类型不相同。这称为构造函数的重载。调用构造函数时不必给出实参的构造函数，称为默认构造函数
(default constructor)。参的构造函数属于默认构造函数。一个类只能有一个默认构造函数，尽管在一个类中可以包含多个构造函数，但是
对于每一个对象来说，建立对象时只执行其中一个构造函数。
```
Box( );                   //声明一个无参的构造函数


Box(int h,int w,int len):height(h),width(w),length(len){ }


//声明一个有参的构造函数，用参数的初始化表对数据成员初始化


Box(int h=10,int w=10,int len=10); //在声明构造函数时指定默认参数


Box::Box(int h,int w,int len)


{


height=h;


width=w;


length=len;


}
```
应该在声明构造函数时指定默认值，而不能只在定义构造函数时指定默认值。如果构造函数的全部参数都指定了默认值，则在定义对象时可以给一个或几个实参，也可以不给出实参。在一个类中定义了全部是默认参数的构造函数后，不能再定义重载构造函数。

```
#include <iostream>
using namespace std;
class Box
{public:
Box(int h=10,int w=10,int len=10);        //在声明构造函数时指定默认参数
int volume( );
 private:
int height;
int width;
int length;
};
Box::Box(int h,int w,int len)        //在定义函数时可以不指定默认参数
{
height=h;
width=w;
length=len;
}
int Box::volume( )
{
 return(height*width*length);
}
int main( )
{
Box box1;                   //没有给实参 
cout<<"The volume of box1 is"<<box1.volume( )<<endl;
Box box2(15);               //只给定一个实参
cout<<"The volume of box2 is"<<box2.volume( )<<endl;
Box box3(15,30);            //只给定2个实参
cout<<"The volume of box3 is"<<box3.volume( )<<endl;
Box box4(15,30,20);            //给定3个实参
cout<<"The volume of box4 is"<<box4.volume( )<<endl;
return 0;
}
```
## 析构函数
析构函数(destructor)也是一个特殊的成员函数，它的作用与构造函数相反，它的名字是类名的前面加一个“～”符号，析构函数是与构造函数作用相反的函数。当
对象的生命期结束时，会自动执行析构函数：①如果在一个函数中定义了一个对象(它是自动局部对象)，当这个函数被调用结束时，对象应该释放，在对象释放前
自动执行析构函数。②static局部对象在函数调用结束时对象并不释放，因此也不调用析构函数，只在main函数结束或调用exit函数结束程序时，才
调用static局部对象的析构函数。③如果定义了一个全局对象，则在程序的流程离开其作用域时(如main函数结束或调用exit函数) 时，调用该全
局对象的析构函数。④如果用new运算符动态地建立了一个对象，当用delete运算符释放该对象时，先调用该对象的析构函数。
析构函数的作用并不是删除对象，而是在撤销对象占用的内存之前完成一些清理工作，使这部分内存可以被程序分配给新对象使用。析构函数不返回任何值，没有函数类型，也没有函数参数。因此它不能被重载。一个类可以有多个构造函数，但只能有一个析构函数。当然，析构函数也可被用来执行“用户希望在最后一次使用对象之后所执行的任何操作”，例如输出有关的信息。如果用户没有定义析构函数，C++编译系统会自动生成一个析构函数，实际上什么操作都不进行。
在一般情况下，调用析构函数的次序正好与调用构造函数的次序相反：最先被调用的构造函数，其对应的(同一对象中的)析构函数最后被调用，而最后被调用的构造函数，其对应的析构函数最先被调用。
![](http://img115.ph.126.net/mZ3NodKZGKUq6h5f0bjsmw==/684828618338727175.jpg)