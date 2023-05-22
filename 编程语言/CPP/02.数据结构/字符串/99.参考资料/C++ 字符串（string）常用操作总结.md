# C++ 字符串（string）常用操作总结

由**单引号**括起来的**一个字符**被称作 **char 型字面值**，**双引号**括起来的**零个或多个字符**则构成**字符串型字面值**。字符串字面值的类型实际上就是由常量字符构成的**数组**，，编译器在每一个字符串后面添加一个空字符（'\0'），因此字符串的实际长度要比他的内容多 1。

如字面值 **'A'** 表示的就是单独字符 A ，而字符串 **"A"** 代表了一个包含两个字符的字符数组，分别是字母 A 和空字符。

## 0、常用功能汇总

| s.**insert**(pos, args)           | 在 pos 之前插入 args 指定的字符                                                                     |
| --------------------------------- | --------------------------------------------------------------------------------------------------- |
| s.**erase**(pos, len)             | 删除从 pos 开始的 len 个字符。如果 len 省略，则删除 pos 开始的后面所有字符。返回一个指向 s 的引用。 |
| s.**assign**(args)                | 将 s 中的字符替换为 args 指定的字符。返回一个指向 s 的引用。                                        |
| s.**append**(args)                | 将 args 追加到 s 。返回一个指向 s 的引用。args 必须是双引号字符串                                   |
| s.**replace**(range, args)        | 将 s 中范围为 range 内的字符替换为 args 指定的字符                                                  |
| s.**find**(args)                  | 查找 s 中 args 第一次出现的位置                                                                     |
| s.**rfind**(args)                 | 查找 s 中 args 最后一次出现的位置                                                                   |
| **to_string**(val)                | 将数值 val 转换为 string 并返回。val 可以是任何算术类型（int、浮点型等）                            |
| **stoi**(s) / **atoi**(c)         | 字符串/字符 转换为整数并返回                                                                        |
| **stof**(s) / **atof**(s)         | 字符串/字符 转换为浮点数并返回                                                                      |
| s.**substr**(pos, n)              | 从索引 pos 开始，提取连续的 n 个字符，包括 pos 位置的字符                                           |
| **reverse**(s2.begin(), s2.end()) | 反转 string 定义的字符串 s2 （加头文件 **<algorithm>** ）                                           |

## 1、定义一个字符串

使用标准库类型 string 声明并初始化一个字符串，需要包含头文件 **string**。可以初始化的方式如下：

```cpp
    string s1;    // 初始化一个空字符串
    string s2 = s1;   // 初始化s2，并用s1初始化
    string s3(s2);    // 作用同上
    string s4 = "hello world";   // 用 "hello world" 初始化 s4，除了最后的空字符外其他都拷贝到s4中
    string s5("hello world");    // 作用同上
    string s6(6,'a');  // 初始化s6为：aaaaaa
    string s7(s6, 3);  // s7 是从 s6 的下标 3 开始的字符拷贝
    string s8(s6, pos, len);  // s7 是从 s6 的下标 pos 开始的 len 个字符的拷贝
```

使用 = 的是拷贝初始化，使用 () 的是直接初始化。当初始值只有一个时，两者都可。当初始值有多个时一般来说要使用直接初始化，如上述最后一个的形式。

## 2、读写 string 操作

输入时遇到空格或回车键将停止。但需要注意的是只有按下回车键时才会结束输入执行，当按下空格后还能继续输入，但最终存到字符串中的只是第一个空格之前输入的字符串（开头的空白除外，程序会自动忽略开头的空白的），空格操作可以用来同时对多个字符串进行初始化，如下例

```cpp
#include <iostream>
#include <string>
using namespace std;
int main(void)
{
    string s1, s2, s3;    // 初始化一个空字符串
    // 单字符串输入，读入字符串，遇到空格或回车停止
    cin >> s1;
    // 多字符串的输入，遇到空格代表当前字符串赋值完成，转到下个字符串赋值，回车停止
    cin >> s2 >> s3;
    // 输出字符串
    cout << s1 << endl;
    cout << s2 << endl;
    cout << s3 << endl;
    return 0;
}
// 运行结果 //
  abc def hig
abc
def
hig
```

如果希望在最终读入的字符串中保留空格，可以使用 **getline** 函数，例子如下：

```cpp
#include <iostream>
#include <string>

using namespace std;

int main(void)
{
    string s1 ;    // 初始化一个空字符串
    getline(cin , s1);
    cout << s1 << endl;  // 输出
    return 0;
}
// 结果输出 //
abc def hi
abc def hi
```

## 3、查询字符串信息、索引

可以用 empty size/length 查询字符串状态及长度，可以用下标操作提取字符串中的字符。

```cpp
#include <iostream>
#include <string>
using namespace std;
int main(void)
{
    string s1 = "abc";    // 初始化一个字符串
    cout << s1.empty() << endl;  // s 为空返回 true，否则返回 false
    cout << s1.size() << endl;   // 返回 s 中字符个数，不包含空字符
    cout << s1.length() << endl;   // 作用同上
    cout << s1[1] << endl;  // 字符串本质是字符数组
    cout << s1[3] << endl;  // 空字符还是存在的
    return 0;
}
// 运行结果 //
0
3
3
b
```

## 4、拼接、比较等操作

```cpp
s1+s2          // 返回 s1 和 s2 拼接后的结果。加号两边至少有一个 string 对象，不能都是字面值
s1 == s2       // 如果 s1 和 s2 中的元素完全相等则它们相等，区分大小写
s1 != s2
<, <=, >, >=   // 利用字符的字典序进行比较，区分大小写
```

## 5、cctype 头文件(判断字符类型：大/小写字母、标点、数字等)

cctype 头文件中含有对 string 中字符操作的库函数，如下：

```cpp
isalnum(c)  // 当是字母或数字时为真
isalpha(c)  // 当是字母时为真
isdigit(c)  // 当是数字是为真
islower(c)  // 当是小写字母时为真
isupper(c)  // 当是大写字母时为真
isspace(c)  // 当是空白（空格、回车、换行、制表符等）时为真
isxdigit(c) // 当是16进制数字是为真
ispunct(c)  // 当是标点符号时为真（即c不是 控制字符、数字、字母、可打印空白 中的一种）
isprint(c)  // 当时可打印字符时为真（即c是空格或具有可见形式）
isgraph(c)  // 当不是空格但可打印时为真
iscntrl(c)  // 当是控制字符时为真
tolower(c)  // 若c是大写字母，转换为小写输出，否则原样输出
toupper(c)  // 类似上面的
```

## 6、for 循环遍历

可以使用 c++11 标准的 **for(declaration: expression)** 形式循环遍历，例子如下：

（**如果想要改变 string 对象中的值，必须把循环变量定义为引用类型**）

```cpp
#include <iostream>
#include <string>
#include <cctype>
using namespace std;
int main(void)
{
    string s1 = "nice to meet you~";    // 初始化一个空字符串
    // 如果想要改变 string 对象中的值，必须把循环变量定义为引用类型。引用只是个别名，相当于对原始数据进行操作
    for(auto &c : s1)
        c = toupper(c);
    cout << s1 << endl; // 输出
    return 0;
}
// 运行结果 //
NICE TO MEET YOU~
```

## 7、修改 string 的操作

在 pos 之前插入 args 指定的字符。pos 是一个下标或者迭代器。接受下标的版本返回一个指向 s 的引用；接受迭代器的版本返回一个指向第一个插入字符的迭代器

```cpp
s.insert(pos, args)
// 在 s 的位置 0 之前插入 s2 的拷贝
s.insert(0, s2)
```

删除从 pos 开始的 len 个字符。如果 len 省略，则删除 pos 开始的后面所有字符。返回一个指向 s 的引用。

```cpp
s.erase(pos, len)
```

将 s 中的[字符替换](https://so.csdn.net/so/search?q=字符替换&spm=1001.2101.3001.7020)为 args 指定的字符。返回一个指向 s 的引用。

```cpp
s.assign(args)
```

将 args 追加到 s 。返回一个指向 s 的引用。args 不能是单引号字符，若是单个字符则必须用双引号表示。如，可以是 s.append(**"**A**"**) 但不能是 s.append(**'**A**'**)

```cpp
s.append(args)
```

将 s 中范围为 range 内的字符替换为 args 指定的字符。range 或者是一个下标或长度，或者是一对指向 s 的迭代器。返回一个指向 s 的引用。

```cpp
s.replace(range, args)
// 从位置 3 开始，删除 6 个字符，并插入 "aaa".删除插入的字符数量不必相等
s.replace(3, 6, "aaa")
```

## 8、string 搜索操作

搜索操作返回指定字符出现的下标，如果未找到返回 npos

```cpp
s.find(args)  // 查找 s 中 args 第一次出现的位置
s.rfind(args)  // 查找 s 中 args 最后一次出现的位置
```

在 s 中查找 args 中任何一个字符 最早/最晚 出现的位置

```cpp
s.find_first_of(args)  // 在 s 中查找 args 中任何一个字符最早出现的位置
s.find_last_of(args)  // 在 s 中查找 args 中任何一个字符最晚出现的位置
例如：
string s1 = "nice to meet you~";
cout << s1.find_first_of("mey") << endl; // 输出结果为 3，'e' 出现的最早

```

在 s 中查找 第一个/最后一个 不在 args 中的字符的位置

```cpp
s.find_first_not_of(args)  // 查找 s 中 第一个不在 args 中的字符的位置
s.find_last_not_of(args)  // 查找 s 中 最后一个不在 args 中的字符的位置
例如：
string s1 = "nice to meet you~";
cout << s1.find_first_not_of("nop") << endl; // 输出结果为 1 ，'i' 不在 "nop" 里
```

## 9、string、char 型与数值的转换

**1、**将数值 val 转换为 string 。val 可以是任何算术类型（int、浮点型等）。

```cpp
string s = to_string(val)
```

**2、转换为整数并返回**。返回类型分别是 int、long、unsigned long、long long、unsigned long long。**b** 表示转换所用的进制数，默认为 10，即将字符串当作几进制的数转换，最终结果仍然是十进制的表示形式 。**p** 是 size_t 指针，用来保存 s 中第一个非数值字符的下标，默认为 0，即函数不保存下标，该参数也可以是空指针，在这种情况下不使用。

```cpp
stoi(s)
// 函数原型 int stoi (const string&  str, size_t* idx = 0, int base = 10);
stoi(s, p, b)
stol(s, p, b)
stoul(s, p, b)
stoll(s, p, b)
stoull(s, p, b)
// 例如
string s1 = "11";    // 初始化一个空字符串
int a1 = stoi(s1);
cout << a1 << endl; // 输出 11
int a2 = stoi(s1, nullptr, 8);
cout << a2 << endl; // 输出 9
int a3 = stoi(s1, nullptr, 2);
cout << a3 << endl; // 输出 3
```

**3、转换为浮点数并返回。**返回类型分别是 float、double、long double 。**p** 是 size_t 指针，用来保存 s 中第一个非数值字符的下标，默认为 0，即函数不保存下标，该参数也可以是空指针，在这种情况下不使用。

```cpp
stof(s)
stof(s, p)
stod(s, p)
stold(s, p)
```

**4、char 型转数值。**注意传入的参数是指针类型，即要对字符取地址

```cpp
atoi(c)
// 函数原型 int atoi(const char *_Str)
atol(c)
atoll(c)
atof(c)
```

## 10、[字符串反转](https://so.csdn.net/so/search?q=字符串反转&spm=1001.2101.3001.7020)

使用 <algorithm> 头文件中的 reverse() 方法：

```cpp
string s2 = "12345";    // 初始化一个字符串
reverse(s2.begin(), s2.end()); // 反转 string 定义的字符串 s2
cout << s2 << endl; // 输出 54321
```

## 11、提取字串

使用 **string ss = s.substr(pos, n)** 。从索引 pos 开始，提取连续的 n 个字符，包括 pos 位置的字符。函数原型：

```cpp
inline std::__cxx11::string std::__cxx11::string::substr(std::size_t __pos, std::size_t __n) const
```
