# CMakeLists.txt

```h
//声明要求的 cmake 最低版本,终端输入 cmake -version 可查看cmake的版本
cmake_minimum_required(VERSION 2.8 )

//声明 cmake 工程名字
project(slam)

//设置使用g++编译器,这是添加变量的用法set(KEY VALUE)接收两个参数，用来声明变量。在camke语法中使用${KEY}这种写法来取到VALUE
set(CMAKE_CXX_COMPILER "g++")

//设置cmake编译模式有debug和release两种PROJECT_SOURCE_DIR项目根目录也就是是CmakeLists.txt的绝对路径
set(CMAKE_BUILD_TYPE "Release" )

//设定生成的可执行二进制文件存放的存放目录
set(EXECUTABLE_OUTPUT_PATH ${PROJECT_SOURCE_DIR}/bin)

//设定生成的库文件的存放目录
set(LIBRARY_OUTPUT_PATH ${PROJECT_SOURCE_DIR}/lib)

//参数CMAKE_CXX_FLAGS含义是： set compiler for c++ language
//添加c++11标准支持，*.CPP文件编译选项,-march=native指定目标程序的cpu架构来进行程序优化
//native就是相当于自检测cpu，-march是gcc优化选项,后面的-O3是用来调节编译时的优化程度的，最高为-O3,最低为-O0即不做优化
//-Ox这个参数只有在CMake -DCMAKE_BUILD_TYPE=release时有效
//因为debug版的项目生成的可执行文件需要有调试信息并且不需要进行优化,而release版的不需要调试信息但需要优化
set(CMAKE_CXX_FLAGS “-std=c++11 -march=native -O3”)

//调试手段message打印信息，类似于echo/printf，主要用于查cmake文件的语法错误
set(use_test ${SOURCES_DIRECTORY}/user_accounts.cpp)
message("use_test： ${use_test}")

//在CMakeLists.txt中指定安装位置, 在编译终端指定安装位置:cmake -DCMAKE_INSTALL_PREFIX=/usr ..
set(CMAKE_INSTALL_PREFIX < install_path >)

//增加子文件夹，也就是进入源代码文件夹继续构建
add_subdirectory(${PROJECT_SOURCE_DIR}/src)

//添加依赖，去寻找该库的头文件位置、库文件位置以及库文件名称，并将其设为变量，返回提供给CMakeLists.txt其他部分使用。
//cmake_modules.cmake文件是把CMakeLists.txt里用来寻找特定库的内容分离出来,如果提示没有找到第三方依赖库可以尝试安装或者暴力指定路径
// 寻找OpenCV库
find_package(OpenCV REQUIRED )

//在CMakeLists.txt中使用第三方库的三部曲:find_package、include_directories、target_link_libraries
include_directories(${OpenCV_INCLUDE_DIRS})// 去哪里找头文件
link_directories()// 去哪里找库文件(.so/.lib/.ddl等)
target_link_libraries(${OpenCV_LIBRARIES})// 需要链接的库文件
message("OpenCV_INCLUDE_DIRS: \n" ${OpenCV_INCLUDE_DIRS})
message("OpenCV_LIBS: \n" ${OpenCV_LIBS})

// find_package(Eigen3 REQUIRED), 假如找不到Eigen3库，我们就设置变量来指定Eigen3的头文件位置
set(Eigen3_DIR /usr/lib/cmake/eigen3/Eigen3Config.cmake)
include_directories(/usr/local/include/eigen3)
```

# 常用命令说明

- add_executable

`add_executable`命令用于将多个源文件编译成可执行文件。举个例子，假设我们有两个源文件`main.cpp`和`helper.cpp`，它们需要被编译成一个可执行文件`myapp`，我们可以使用下面的代码：

```
add_executable(myapp main.cpp helper.cpp)
```

其中，`myapp`表示生成的可执行文件的名称，`main.cpp`和`helper.cpp`表示源代码文件的名称。如果有多个源代码文件，可以将它们作为参数逐一列出。

- add_library

`add_library`命令用于将多个源文件编译成静态库或动态库。举个例子，假设我们有两个源文件`foo.cpp`和`bar.cpp`，它们需要被编译成一个静态库`libfoobar.a`，我们可以使用下面的代码：

```
add_library(foobar STATIC foo.cpp bar.cpp)
```

其中，`foobar`表示生成的库的名称，`foo.cpp`和`bar.cpp`表示源代码文件的名称。`STATIC`表示生成静态库，`SHARED`表示生成动态库，`MODULE`表示生成插件库。如果不指定库类型，则默认生成静态库。

- target_link_libraries

`target_link_libraries`命令用于将一个或多个库链接到可执行文件或其他库中。举个例子，假设我们需要将`libfoo.a`和`libbar.a`链接到可执行文件`myapp`中，我们可以使用下面的代码：

```
target_link_libraries(myapp foo bar)
```

其中，`myapp`表示可执行文件或其他库的名称，`foo`和`bar`表示需要链接的库的名称。如果有多个库，可以将它们作为参数逐一列出。

- include_directories

`include_directories`命令用于将头文件路径添加到编译器的搜索路径中。举个例子，假设我们需要将`/path/to/include`添加到编译器的头文件搜索路径中，我们可以使用下面的代码：

```
include_directories(/path/to/include)
```

如果有多个路径，可以将它们作为参数逐一列出。另外，`AFTER`和`BEFORE`表示添加的路径在搜索路径中的位置，`SYSTEM`表示添加的路径是系统头文件路径。

- link_directories

`link_directories`命令用于将库文件路径添加到链接器的搜索路径中。举个例子，假设我们需要将`/path/to/lib`添加到链接器的库文件搜索路径中，我们可以使用下面的代码：

```
link_directories(/path/to/lib)
```

如果有多个路径，可以将它们作为参数逐一列出。

- set

`set`命令用于设置变量的值。举个例子，假设我们需要将变量`MY_VARIABLE`的值设置为`hello world`，我们可以使用下面的代码：

```
set(MY_VARIABLE "hello world")
```

其中，`MY_VARIABLE`表示变量的名称，`hello world`表示变量的值。如果变量的值是一个字符串，需要用引号将其括起来。

- if

`if`命令用于判断条件是否成立。举个例子，假设我们需要判断变量`MY_VARIABLE`是否等于`hello world`，如果成立，则执行一些操作，我们可以使用下面的代码：

```
if(MY_VARIABLE STREQUAL "hello world")    # do somethingendif()
```

其中，`MY_VARIABLE`表示判断的条件，`STREQUAL`表示字符串相等。如果条件成立，则执行`do something`部分的代码。

- endif

`endif`命令用于结束`if`语句块。其实，在 CMake 中，所有的控制流语句都需要以`endif`命令结束。举个例子，假设我们需要判断变量`MY_VARIABLE`是否等于`hello world`，如果成立，则打印一条消息，否则打印另一条消息，我们可以使用下面的代码：

```
if(MY_VARIABLE STREQUAL "hello world")    message("MY_VARIABLE is hello world")else()    message("MY_VARIABLE is not hello world")endif()
```

其中，`message`命令用于打印消息。

- foreach

`foreach`命令用于遍历一个列表，并对其中的每个元素执行相同的操作。举个例子，假设我们有一个列表`mylist`，其中包含三个元素`foo`、`bar`和`baz`，我们需要将它们依次打印出来，我们可以使用下面的代码：

```
set(mylist foo bar baz)
foreach(item IN LISTS mylist)    message(${item})endforeach()
```

其中，`item`表示列表中的元素，`mylist`表示需要遍历的列表。`LISTS`表示`mylist`是一个列表。
