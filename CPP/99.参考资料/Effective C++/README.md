# Effective C++中文版(第三版) 学习笔记

### part1 习惯C++
- item1 视C++为一个语言集合
- item2 用const,enum,inline代替#define
- item3 多用const
- item4 确保对象在使用前已被初始化

### part2 构造/析构/赋值运算
- item5 了解C++类中自动生成和调用的函数
- item6 若不想使用编译器自动生成的函数，要显式拒绝
- item7 为多态基类声明virtual析构函数
- item8 不要让析构函数抛出异常
- item9 不要在构造和析构函数中调用virtual函数
- item10 自定义赋值操作符(operator=) 要返回*this的引用
- item11 注意operator= 的“自我赋值”
- item12 对象进行复制时需要完整拷贝

### part3 资源管理
- item13 使用对象来管理资源
- item14 注意资源管理类中的拷贝行为
- item15 在资源管理类中提供对原始资源的访问git
- item16 使用new/delete形式要对应
- item17 用单独的语句来创建智能指针

### part4 设计与声明
- item18 让接口不容易被误用
- item19 把类当作类型来设计
- item20 用常量引用传递代替值传递
- item21 不要在需要返回对象时返回引用
- item22 类的数据成员声明为private
- item23 用非成员且非友元函数来替换成员函数
- item24 如果参数要进行类型转换，该函数不能作为成员函数
- item25 考虑写一个高效的swap函数

### part5 实现
- item26 尽可能推迟变量定义
- item27 减少类型转换的使用
- item28 避免返回指向对象内部成员的句柄
- item29 保证代码的异常安全性
- item30 透彻了解inline函数
- item31 最小化文件之间的编译依赖关系

### part6
- item32 让public继承塑造出is-a关系
- item33 避免继承中发生的名称覆盖
- item34 区分接口继承和实现继承
- item35 考虑virtual函数的替代方法
- item36 不要重写继承来的非虚函数
- item37 不要重定义通过继承得到的默认参数值
- item38 通过组合塑造has-a或use-a关系
- item39 慎用private继承
- item40 慎用多继承

### part7

### part8

### part9

### 参考文献 & 资源链接
- [Effective C++ 读书笔记](https://zhuanlan.zhihu.com/c_1104392405461315584)