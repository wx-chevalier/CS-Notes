# C++ 工程：总结 CMake 添加第三方库依赖方式 git submodule、 find_library、FetchContent、CPM 等

CMake 已经成为了 C++工程管理的主流方式，功能非常强大，现在大多数的 C++ 库都已经支持 CMake，下面以 [jsoncpp](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fopen-source-parsers%2Fjsoncpp) 为例，介绍几种引入第三方库的方式。

## 1. 代码依赖

这种方式是把第三方库的完整代码直接添加到我们的项目中，当做项目代码的一部分进行编译，这种方式会把第三方代码和我们的代码混在一起，并不推荐使用。首先我们需要到 [jsoncpp](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fopen-source-parsers%2Fjsoncpp) 下载需要的头文件和实现代码，放到项目当中。

##### 工程文件目录

```css
├── CMakeLists.txt
├── jsoncpp
│   ├── include
│   │   └── json
│   │       ├── autolink.h
│   │       ├── config.h
│   │       ├── features.h
│   │       ├── forwards.h
│   │       ├── json.h
│   │       ├── reader.h
│   │       ├── value.h
│   │       └── writer.h
│   ├── json_batchallocator.h
│   ├── json_internalarray.inl
│   ├── json_internalmap.inl
│   ├── json_reader.cpp
│   ├── json_value.cpp
│   ├── json_valueiterator.inl
│   └── json_writer.cpp
└── main.cpp
```

##### CMakeLists.txt

```bash
cmake_minimum_required(VERSION 3.17)
project(includes_full_code)
set(CMAKE_CXX_STANDARD 14)
# 包含头文件
include_directories(./jsoncpp/include)
set(jsoncpp jsoncpp/json_reader.cpp jsoncpp/json_writer.cpp jsoncpp/json_value.cpp)
# 添加可执行代码
add_executable(includes_full_code main.cpp ${jsoncpp})
```

##### main.cpp

后面的示例的 main.cpp 都是一样

```cpp
#include <iostream>
#include "json/json.h"
int main() {
    Json::Value json;
    json["name"] = "Wiki";
    json["age"] = 18;
    std::cout << json.toStyledString() << std::endl;
    return 0;
}
```

完整代码：[includes_full_code_exmaple](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Ftaoweiji%2Fcpp-cmake-example%2Ftree%2Fmaster%2Fincludes_full_code)

## 2. 内部工程依赖

这种方式和上面 `代码依赖` 的方式类似，不同的是内部工程依赖会把第三方库的管理职责交给第三方库工程 CMakeLists.txt 文件，这种方式的好处是职责分明，是最常用的依赖方式。

##### 工程文件目录

目录结果和上面的案例相似，不同的是 jsoncpp 文件夹多了一个 `CMakeLists.txt` 文件

```makefile
├── CMakeLists.txt
├── jsoncpp
│   ├── CMakeLists.txt
│   ├── include
│   │   └── json
│   │       ├── autolink.h
│   │       ├── config.h
│   │       ├── features.h
│   │       ├── forwards.h
│   │       ├── json.h
│   │       ├── reader.h
│   │       ├── value.h
│   │       └── writer.h
│   ├── json_batchallocator.h
│   ├── json_internalarray.inl
│   ├── json_internalmap.inl
│   ├── json_reader.cpp
│   ├── json_value.cpp
│   ├── json_valueiterator.inl
│   └── json_writer.cpp
└── main.cpp
```

##### jsoncpp/CMakeLists.txt

```bash
cmake_minimum_required(VERSION 3.17)
project(jsoncpp)
add_library(${PROJECT_NAME} json_reader.cpp json_value.cpp json_writer.cpp)
target_include_directories(${PROJECT_NAME} PUBLIC ${PROJECT_SOURCE_DIR}/include)
```

##### CMakeLists.txt

```makefile
cmake_minimum_required(VERSION 3.17)
project(multi_cmakelists)
# 添加子工程
add_subdirectory(jsoncpp)
add_executable(${PROJECT_NAME} main.cpp)
# 链接子工程
target_link_libraries(${PROJECT_NAME} jsoncpp)
```

完整代码：[multi_cmakelists_example](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Ftaoweiji%2Fcpp-cmake-example%2Ftree%2Fmaster%2Fmulti_cmakelists)

> 这种方式除了引入第三方依赖，通常我们也会用这种方式来管理项目中的各个子模块，每个模块都有独立的 CMakeLists.txt 文件，从而实现子工程的单独引用，源码请看 [subdirectory_example](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Ftaoweiji%2Fcpp-cmake-example%2Ftree%2Fmaster%2Fsubdirectory)。

## 3. find_library：编译库方式引入

这种方式是用来依赖已经打包好的二进制文件，这种方式也分为静态库（.a、.lib）和动态库（.so、.dll）方式引入，这种方式也可以查找本机已经安装好的库，比如 Android 的 log 库就是通过这种方式引入。

##### 生成.a 文件

运行上面的 `内部工程依赖` 案例后，我们我们可以从项目中找到编译好的 multi_cmakelists/cmake-build-debug/jsoncpp/libjsoncpp.a 文件。

##### 工程文件目录

和上面不同的是，这里只需要导入 jsoncpp 的头文件和.a 文件。

```css
├── CMakeLists.txt
├── jsoncpp
│   ├── include
│   │   └── json
│   │       ├── autolink.h
│   │       ├── config.h
│   │       ├── features.h
│   │       ├── forwards.h
│   │       ├── json.h
│   │       ├── reader.h
│   │       ├── value.h
│   │       └── writer.h
│   └── libjsoncpp.a
└── main.cpp
```

##### CMakeLists.txt

```bash
cmake_minimum_required(VERSION 3.17)
project(find_library_example)
include_directories(jsoncpp/include)
add_executable(${PROJECT_NAME} main.cpp)
find_library(jsoncpp_lib NAMES jsoncpp PATHS ./jsoncpp)
target_link_libraries(${PROJECT_NAME} ${jsoncpp_lib})
```

完整代码：[find_library_example](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Ftaoweiji%2Fcpp-cmake-example%2Ftree%2Fmaster%2Ffind_library)

> 这种方式在 Android 开发很常见，比如我们引入 xlog 实现日志打印就可以通过这种方式实现，代码参考 [xlog_example](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Ftaoweiji%2Fcpp-cmake-example%2Ftree%2Fmaster%2Fxlog)。

## 4. FetchContent

[FetchContent](https://links.jianshu.com/go?to=https%3A%2F%2Fcmake.org%2Fcmake%2Fhelp%2Flatest%2Fmodule%2FFetchContent.html) 是 cmake 3.11.0 版本开始提供的功能，可以非常方便用来添加第三方依赖。

##### 工程文件目录

```css
├── CMakeLists.txt
└── main.cpp
```

##### CMakeLists.txt

```bash
cmake_minimum_required(VERSION 3.17)
project(fetch_content_example)
include(FetchContent)
#FetchContent_Declare(jsoncpp
#        GIT_REPOSITORY https://github.com/open-source-parsers/jsoncpp.git
#        GIT_TAG 1.9.4)
# 建议使用压缩包的方式依赖，下载速度更快
FetchContent_Declare(jsoncpp
        URL https://github.com/open-source-parsers/jsoncpp/archive/1.9.4.tar.gz)
FetchContent_MakeAvailable(jsoncpp)
add_executable(${PROJECT_NAME} main.cpp)
target_link_libraries(${PROJECT_NAME} jsoncpp_lib)
```

建议通过压缩包的方式引入，因为直接引入 git 仓库可能会很慢。

完整代码：[fetch_content_example](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Ftaoweiji%2Fcpp-cmake-example%2Ftree%2Fmaster%2Ffetch_content)

> Android SDK 的 CMake 的默认版本是 3.10.2，并不支持 FetchContent，如果想在 Android 开发中使用需要安装 3.11.0 以上版本的 cmake，为了降低团队的协同成本，并不建议在 Android 工程使用，建议使用内部工程的方式引入。

## 5. CPM

[CPM.cmake](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2FTheLartians%2FCPM.cmake) 是在 FetchContent 的基础上封装而来，相比 FetchContent 更加简单易用，使用 CPM 需要到 [CPM.cmake](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2FTheLartians%2FCPM.cmake) 下载 cmake 目录的文件 CPM.cmake、get_cpm.cmake 和 testing.cmake，添加到项目当中。

##### 工程文件目录

```css
├── CMakeLists.txt
├── cmake
│   ├── CPM.cmake
│   ├── get_cpm.cmake
│   └── testing.cmake
└── main.cpp
```

##### CMakeLists.txt

```bash
cmake_minimum_required(VERSION 3.17)
project(cpm_example)
include(cmake/CPM.cmake)
#CPMAddPackage(
#        GIT_REPOSITORY https://github.com/open-source-parsers/jsoncpp.git
#        GIT_TAG 1.9.4)
# 建议使用压缩包的方式依赖，下载速度更快
CPMAddPackage(
        NAME jsoncpp
        URL https://github.com/open-source-parsers/jsoncpp/archive/1.9.4.tar.gz)

add_executable(${PROJECT_NAME} main.cpp)
target_link_libraries(${PROJECT_NAME} jsoncpp_lib)
```

这种方式的细节不需要我们自己处理，都交给了 CPM 解决，这种方式也同样不建议在 Android 工程使用。

完整代码：[cpm_example](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Ftaoweiji%2Fcpp-cmake-example%2Ftree%2Fmaster%2Fcpm)

## 6. find_package

[find_package](https://links.jianshu.com/go?to=https%3A%2F%2Fcmake.org%2Fcmake%2Fhelp%2Flatest%2Fcommand%2Ffind_package.html) 是 cmake 3.19.0 版本开始提供的功能，可以非常方便添加，这种方式主要是从本机上查找已经安装好的库，需要提前通过命令安装。

##### 安装 jsoncpp

我的 Mac OS，通过下面方法安装可以成功，其它系统可能会出错

```bash
# 拉取代码
git clone https://github.com/open-source-parsers/jsoncpp
cd jsoncpp
mkdir -p build/debug
cd build/debug
# 生成Makefile
cmake -DCMAKE_BUILD_TYPE=release -DBUILD_STATIC_LIBS=OFF -DBUILD_SHARED_LIBS=ON -DARCHIVE_INSTALL_DIR=. -DCMAKE_INSTALL_INCLUDEDIR=include -G "Unix Makefiles" ../..
# 安装
make && make install
```

> 如果提示没有安装 cmake，需要自行安装 cmake

##### 工程文件目录

```css
├── CMakeLists.txt
└── main.cpp
```

##### CMakeLists.txt

```bash
cmake_minimum_required(VERSION 3.17)
project(find_package_example)
find_package(jsoncpp REQUIRED)
add_executable(${PROJECT_NAME} main.cpp)
target_link_libraries(${PROJECT_NAME} jsoncpp_lib)
```

##### 完整代码：[find_package_example](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Ftaoweiji%2Fcpp-cmake-example%2Ftree%2Fmaster%2Ffind_package)

> 使用这种方式是需要有个大前提，电脑必须已经安装好了对应的库，否则无法正常工作，这种方式只有在特定的场景下使用，比如调用电脑的 opencv、openssl。

## 7. git submodule

这种方式是利用 git 的 submodule 实现，推荐 Android 使用，通过 git 添加另外一个仓库的依赖，可更新另外一个仓库的依赖，但是代码不会包含进来。

```csharp
# 在A仓库添加B仓库依赖，操作完后需要提交上去
git submodule add https://github.com/taoweiji/B.git
```

A 仓库拉取及 submodule 仓库的更新

```php
git clone https://github.com/taoweiji/A.git
git submodule init && git submodule update
```

## 8. Android 动态依赖

[C++工程：以 xlog 为例介绍 Android NDK 如何依赖第三方 C++动态库](https://www.jianshu.com/p/ad2d0e4958e4)

### 总结

C++添加依赖的方式有很多种，没有绝对的好与差，应该根据不同的场景使用不同的依赖方式，例如在 Android 工程中，我们应该尽量不要改变默认的 CMake 版本，避免增加环境的依赖。
