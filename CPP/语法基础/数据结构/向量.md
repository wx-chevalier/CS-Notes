vector 类向量容器使用动态数组存储、管理对象。因为数组是一个随机访问数据结构，所以可以随机访问向量中的元素。在数组中间或是开始处插入一个元素是费时的，特别是在数组非常大的时候更是如此。然而在数组末端插入元素却很快。实现向量容器的类名是 vector(容器是类模板)。包含 vector 类的头文件名是 vector。所以，如果要在程序里使用向量容器，就要在程序中包含下面语句：
#include <vector>
此外，在定义向量类型对象时，必须指定该对象的类型，因为 vector 类是一个类模板。例如，语句：
vector<int> intList;  
将 intList 声明为一个元素类型为 int 的向量容器对象。类似地，语句：
vector<string> stringList;将 stringList 声明为一个元素类型为 string 的向量容器对象。

声明向量对象
vector 类包含了多个构造函数，其中包括默认构造函数。因此，可以通过多种方式来声明和初始化向量容器。表一描述了怎样声明和初始化指定类型的向量容器。

                          表一     各种声明和初始向量容器的方法

语句
作用
vector<elementType> vecList;
创建一个没有任何元素的空向量 vecList(使用默认构造函数)
vector<elementType> vecList(otherVecList) 创建一个向量 vecList，并使用向量 otherVecList 中的元素初始化该向量。向量 vecList 与向量 otherVecList 的类型相同
vector<elementType> vecLIst(size);

    创建一个大小为size的向量vecList，并使用默认构造函数初始化该向量

vector<elementType> vecList(n,elem);
创建一个大小为 n 的向量 vecList，该向量中所有的 n 个元素都初始化为 elem
vector<elementType> vecList(begin,end);  
 创建一个向量 vecList，并初始化该向量(begin,end)中的元素。即，从 begin 到 end-1 之间的所有元素

在介绍了如何声明向量顺序容器之后，让我们开始讨论如何操作向量容器中的数据。首先，必须要知道下面几种基本操作：
元素插入
元素删除
遍历向量容器中的元素假设 vecList 是一个向量类型容器。表二给出了在 vecList 中插入元素和删除元素的操作，这些操作是 vector 类的成员函数。表二还说明了如何使用这些操作。

                      表二     向量容器上的各种操作

语句 作用
vecList.clear() 从容器中删除所有元素
vecList.erase(position)
删除由 position 指定的位置上的元素
vecList.erase(beg,end)  
 删除从 beg 到 end-1 之间的所有元素
vecList.insert(position, elem)  
 将 elem 的一个拷贝插入到由 position 指定的位置上，并返回新元素的位置
vecList.inser(position, n, elem) 将 elem 的 n 个拷贝插入到由 position 指定的位置上
vecList.insert(position, beg, end) 将从 beg 到 end-1 之间的所有元素的拷贝插入到 vecList 中由 position 指定的位置上
vecList.push_back(elem)
将 elem 的一个拷贝插入致 List 的末尾
vecList.pop_back()  
 删除最后元素
vecList.resize(num)
将元素个数改为 num。如果 size()增加，默认的构造函数负责创建这些新元素
vecList.resize(num, elem) 将元素个数改为 num。如果 size()增加，默认的构造函数将这些新元素初始化为 elem

在向量容器中声明迭代器
vector 类包含了一个 typedef iterator，这是一个 public 成员。通过 iterator，可以声明向量容器中的迭代器。例如，语句：
vector<int>::iterator intVeciter; 将 intVecIter 声明为 int 类型的向量容器迭代器。因为 iterator 是一个定义在 vector 类中的 typedef，所以必须使用容器名(vector)、容器元素类型和作用域符来使用 iterator。表达式：
++intVecIter
将迭代器 intVecIter 加１，使其指向容器中的下一个元素。表达式：*intVecIter
返回当前迭代器位置上的元素。注意，迭代器上的这些操作和指针上的相应操作是相同的。运算符*作为单目运算符使用时，称为递引用运算符。下面将讨论如何使用迭代器来操作向量容器中的数据。假设有下面语句：
vector<int> intList;
vector<int>::iterator intVecIter;
第一行中的语句将 intList 声明为元素为 int 类型的向量容器。第二行中的语句将 intVecIter 声明为元素为 int 类型的向量容器的迭代器。

容器与函数 begin 和 end
所有容器都包含成员函数 begin 和 end。函数 begin 返回容器中第一个元素的位置；函数 end 返回容器中最后一个元素的位置。这两个函数都没有参数。在执行下面语句：
intVecIter = intList.begin();
迭代器 intVecIter 指向容器 intList 中第一个元素。下面的 for 循环将 intList 中所有元素输出互标准输出设备上：
for (intVecIter = intList.begin(); intVecIter != intList.end();
cout<<\*intVecList<<" ";
可以通过表三中给出的操作直接访问向量容器中的元素。

表三　访问向量容器中元素的操作

表达式 作用
vecList.at(index) 返回由 index 指定的位置上的元素
vecList[index]
返回由 index 指定的位置上的元素
vecList.front()  
 返回第一个元素　(不检查容器是否为空)
vecList.back()  
 返回最后一个元素(不检查容器是否为空)

表三说明：可以按照数组的方式来处理向量中的元素(注意，在 C++中，数组下标从 0 始。，向量容器中第一个元素的位置也是 0)。徽号类中还包含：返回容器中当前元素个数的成员函数，返回可以插入到容器中的元素的最大个数的成员函数等。表四描述其中　一些操作(假设 vecCont 是向量容器)。

表四　计算向量容器大小的操作

表达式 作用
vecCont.capacity() 返回不重新分配空间可以插入到容器 vecCont 中的元素的最大个数
vecCont.empty()
容器 vecCont 为空，返回 true；否则，返回 false
vecCont.size()
返回容器 vecCont 中当前的个数
vecCont.max_size() 返回可以插入到容器 vecCont 中的元素的最大个数

下面给出一个样本程序供进一步认识这些函数的用法：

#include <iostream>

#include <vector>

using namespace std;

int main()

{

vector<int> intList;

int i;

intList.push_back(13);

intList.push_back(75);

intList.push_back(28);

intList.push_back(35);

cout<<"Line １: List Elements: ";

for(i=0;i<4;i++)cout<<intList[i]<<" ";

for(i=0;i<4;i++)intList[i] \*=2;

cout<<"Line ２: List Elements: ";

for(i=0;i<4;i++)cout<<intList[i]<<" ";

cout<<endl;

vector<int>::iterator listIt;

cout<<"Line ３ 0:list Elements: ";

for(listIt=intList.begin();listIt != intList.end();++listIt)cout<<\*lintIt<<" ";

cout<<endl;

listIt=intList.begin();

++listIt;

++listIt;

intList.insert(listIt,88);

cout<<"Line ４:List Elements: ";

for(listIt = intList.begin();listIt != intList.end();++listIt)cout<<\*listIt<<" ";

cout<<endl;

return 0;

}

运行程序输出：

Line １: List Elements: 13 75 28 35

Line ２: List Elements: 26 150 56 70

Line ３: List Elements: 26 150 56 70

Line ４: List Elements: 26 150 88 56 70

注意：当对象为常量时，只能调用类中用 const 修饰符声明的成员函数。成员函数 back()和下标运算符[]各有一个独立的实现，声明为 const。因此，这些函数可以被常量对象调用。例如：

template<typename T>

void writeVectorEnds(const vector<T>&v){

if(v.size()>0) cout<<v[0]<<" "<<v.back()<<endl;

}
