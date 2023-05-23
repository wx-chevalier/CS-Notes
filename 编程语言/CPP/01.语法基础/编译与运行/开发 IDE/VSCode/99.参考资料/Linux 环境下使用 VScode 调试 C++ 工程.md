# Linux 环境下使用 VScode 调试 CMake 工程

# Linux 环境使用 VSCode 调试简单 C++ 代码

本文将通过演示一个简单 C++代码的编译调试过程，介绍在 VSCode 中如何使用 Linux 环境下的 GCC C++编译器(g++)和 GDB 调试器(gdb)。看懂这篇文章的内容，只需要知道 g++用来编译 C++代码，gdb 用来调试 C++代码即可。

示例代码内容如下：

```cpp
// hello.cpp
#include <iostream>
using namespace std;
int main(){
	cout << "Hello, VSCode!" << endl;
    return 0;
}
1234567
```

## 1. 终端命令行方式编译、调试简单 C++代码

如果不考虑 VSCode 编辑器，在 Linux 环境中编译调试一个简单的 C++代码可以只通过命令行实现，具体过程分为两步：

1. **第一步**：将`*.cpp`源代码文件通过`g++`编译器生成一个可调试的可执行二进制文件：

```bash
g++ -g hello.cpp -o hello
1
```

指令解析：

- 为了能够使用 gdb 调试，需要在编译时加上`-g`
- `hello.cpp`：待编译的源文件名
- `-o hello`：指定生成的可执行文件名为 `hello`

```bash
# 运行 hello 的结果：
./hello
Hello, VSCode!
```

1. **第二步**：调用**gdb 调试器**对可执行文件进行调试：

```bash
gdb hello
```

调试的过程如下：

![gdb调试过程](https://img-blog.csdnimg.cn/3d6e8e6339f24d5f8fdbd9d9482e6d90.png)

- **如果希望在 VSCode 中编译调试 C++代码，所要做的就是配置完成上面的两步。**

## 2. 通过 VSCode 对 C++代码进行编译、调试

主要参考：

- [VSCode 官方教程: Using C++ on Linux in VS Code](https://code.visualstudio.com/docs/cpp/config-linux#_debug-helloworldcpp)
- [详解 Linux 下使用 vscode 编译运行和调试 C/C++](https://zhuanlan.zhihu.com/p/394595507)

### 2.1 前提条件

1. VSCode 中的"C/C++插件"已安装（直接在 VSCode 扩展商店搜索"C++"即可）

![C/C++插件](https://img-blog.csdnimg.cn/396696ec65b540349c6a03843011ca46.png)

1. **g++编译器**已安装。可以在终端查看 g++是否已安装

```bash
g++ -v
```

如果能输出版本信息，则已安装。如果没有，通过以下命令安装：

```bash
sudo apt-get update
sudo apt-get install build-essential gdb
```

### 2.2 配置 tasks.json

在 VSCode 中打开示例代码文件夹，

![folder](https://img-blog.csdnimg.cn/b6049357bb5e45269720cb25b406f44f.png)

1. 在 VSCode 的主菜单中，选择 **Terminal>Configure Default Build Task**
2. 出现一个下拉菜单，显示 C++ 编译器的各种预定义编译任务。选择 **C/C++: g++ build active file**（如果配置了中文，会显示 **“C/C++: g++ 生成活动文件”**）
3. 选择后，vscode 会自动生成一个`.vscode`文件夹和 `tasks.json`文件，此时的代码文件夹结构如下：
   ![tasks.json](https://img-blog.csdnimg.cn/e8d9c5aa1f9643afa37d9f2a282407a5.png)
   `tasks.json`的内容如下:

```json
{
  "version": "2.0.0",
  "tasks": [
    {
      "type": "cppbuild",
      "label": "C/C++: g++ 生成活动文件",
      "command": "/usr/bin/g++",
      "args": [
        "-g",
        "${file}",
        "-o",
        "${fileDirname}/${fileBasenameNoExtension}"
      ],
      "options": {
        "cwd": "${fileDirname}"
      },
      "problemMatcher": ["$gcc"],
      "group": {
        "kind": "build",
        "isDefault": true
      },
      "detail": "编译器: /usr/bin/g++"
    }
  ]
}
```

> `tasks.json`的作用是告诉 VSCode 如何编译程序

**在本文中是希望调用 g++编译器从 cpp 源代码创建一个可执行文件，这样就完成了第 1 节中所说的编译调试第一步。**

从 tasks.json 的`"command"`和`"args"`可以看出，其实就是执行了以下命令：

```bash
/usr/bin/g++ -g ${file} -o ${fileDirname}/${fileBasenameNoExtension}
```

其中，

- `${file}`：当前活动文件(就是 vscode 当前查看的文件)，这里是`hello.cpp`
- `${fileDirname}/${fileBasenameNoExtension}`：在这里就是当前目录下的`hello`
- 有关 VSCode 中的变量名，可以参考：[VSCode Variables Reference](https://code.visualstudio.com/docs/editor/variables-reference)

### 2.3 执行编译

在 2.2 节配置完成 tasks.json 文件后，VSCode 就知道应该用 g++编译器对 cpp 文件进行编译，下面执行编译即可：

1. 回到活动文件 hello.cpp（很重要，不然 ${file} 和 ${fileDirname}这些变量都会错）
2. 快捷键`ctrl+shift+B`或从菜单中选择运行：**Terminal -> Run Build Task**，即可执行 tasks.json 中指定的编译过程
3. 编译任务完成后，会出现终端提示，对于成功的 g++编译，输出如下：
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/fc9013195e9745c68c053a846501bd38.png)
   这一步完成后，在代码目录下就出现了一个可执行文件`hello`。
4. (可选) 个性化修改 tasks.json
   可以通过修改 tasks.json 满足一些特定需求，比如将`"${file}"`替换`“${workspaceFolder}/*.cpp”`来构建多个 C++ 文件; 将`“${fileDirname} /${fileBasenameNoExtension}”` 替换为硬编码文件名（如`“hello.out”`）来修改输出文件名

### 2.4 调试 hello.cpp

完成上述的编译配置后，就可以对`hello.cpp`进行调试了：

1. 回到 hello.cpp，确保其是活动文件(active file)
2. 设置一个断点
3. 从右上角的按钮中，选择 **Debug C/C++ file**
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/9c82b9de64844967a07856b372107e2f.png)
4. 然后就开启调试过程了，可以单步运行、添加监视等等。

### 2.5 个性化配置 launch.json

按照 2.4 节的过程，已经可以简单调试一个`.cpp`代码，但是在某些情况下，可能希望自定义调试配置，比如**指定要在运行时传递给程序的命令参数**。这种情况下我们可以在`launch.json`中定义自定义调试配置。

下面是配置**调试过程**的步骤：

1. 从主菜单中，选择 **Run > Add Configuration…**，将会生成一个`launch.json`文件
2. `launch.json`文件右下角点击 **“添加配置”**，选择 **“(gbd)启动”**，文件内容参考如下：

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "(gdb) 启动",
            "type": "cppdbg",
            "request": "launch",
            "program": "${fileDirname}/${fileBasenameNoExtension}",
            "args": [],
            "stopAtEntry": false,
            "cwd": "${fileDirname}",
            "environment": [],
            "externalConsole": false,
            "MIMode": "gdb",
            "setupCommands": [
                {
                    "description": "为 gdb 启用整齐打印",
                    "text": "-enable-pretty-printing",
                    "ignoreFailures": true
                },
                {
                    "description": "将反汇编风格设置为 Intel",
                    "text": "-gdb-set disassembly-flavor intel",
                    "ignoreFailures": true
                }
            ],
            "miDebuggerPath": "/usr/bin/gdb",
            "preLaunchTask": "C/C++: g++ 生成活动文件"
        }
    ]
}
12345678910111213141516171819202122232425262728293031
```

> 这里`launch.json`的作用就是告诉 VSCode 如何调用[gdb 调试](https://so.csdn.net/so/search?q=gdb调试&spm=1001.2101.3001.7020)器。

如果想要在调试/运行程序时添加参数，只需要把参数添加在`"args"`选项中即可。

### 2.6 总结

在 VSCode 中编译、调试一个简单的.cpp 文件，所需要做的就是:

1. 在 tasks.json 中调用 g++ 生成一个可执行二进制文件
2. 在 launch.json 中调用 gdb 对生成的可执行文件进行调试

### 2.7 复用 C++配置

上面的过程已经完成了在 VSCode 中调试 Linux 环境下的 C++代码的配置，但只适用于当前工作空间。如果想要在其他的工程文件夹下复用这种配置，只需要把`tasks.json`和`launch.json`文件复制到新文件夹下的`.vscode`目录下，然后根据需要改变对应的源文件和可执行文件的名称即可。

# Linux 环境下使用 VScode 调试 CMake 工程

为什么要用 CMake？高翔博士的《[视觉 SLAM 十四讲](https://so.csdn.net/so/search?q=视觉SLAM十四讲&spm=1001.2101.3001.7020)》给出了很好的解释：

> 理论上，任意一个 C++程序都可以用 g++ 来编译。但当程序规模越来越大时，一个工程可能有许多个文件夹和源文件，这时输入的编译命令将越来越长。通常，一个小型 C++ 项目可能含有十几个类，各类间还存在着复杂的依赖关系。其中一部分要编译成可执行文件，另一部分编译成库文件。如果仅靠 g++命令，则需要输人大量的编译指令，整个编译过程会变得异常烦琐因此，对于 C++项目，使用一些工程管理工具会更加高效。
> CMake 就是一个方便的 C++工程管理工具。

### 1. 演示文件目录

本文中用以演示的 CMake 工程目录如下：

```
|—— build
|—— helloCMake.cpp
|—— CMakeLists.txt
```

`build`文件夹用于存放 cmake 生成的中间文件。另外两个文件的内容如下：

```cpp
// helloCMake.cpp
#include <iostream>
using namespace std;
int main(int argc, char **argv){
	cout << "Hello, VSCode and CMake!" << endl;

    if ( argc >=2 ) {
        cout << "args[1]: " << string(argv[1]) << endl;
        cout << "args[2]: " << string(argv[2]) << endl;
    }

    return 0;
}

# CMakeLists.txt
cmake_minimum_required(VERSION 2.8)
project(vscode_cmake)

add_executable(helloCMake helloCMake.cpp)
```

### 2. CMake 编译

如果不使用 VSCode，而是使用终端命令行方式进行 cmake 编译的话，标准做法是：

```bash
cd build
cmake ..
make
```

这样在`build`目录下就会生成 cmake 中间文件，以及一个最终的可执行文件`helloCMake`，工程目录将会如下所示：

```
├── build
│   ├── CMakeCache.txt
│   ├── CMakeFiles
│   ├── Makefile
│   ├── cmake_install.cmake
│   └── helloCMake
├── CMakeLists.txt
└── helloCMake.cpp
```

执行`helloCMake`(执行时附带参数 `para1` 和 `para2`)，结果为：

```bash
./helloCMake para1 para2
Hello, VSCode and CMake!
args[1]: para1
args[2]: para2
1234
```

### 3. VSCode 中实现 CMake 编译

那么，如何**把上面的过程配置在 VSCode 中**？具体步骤如下：

1. 在 VSCode 的主菜单中，选择 **Terminal>Configure Default Build Task**，
2. 选择 **“CMake: build”**
3. 将生成一个 `tasks.json`文件，将其中的内容替换为以下内容即可：

```json
{
  "version": "2.0.0",
  "tasks": [
    {
      "label": "cmake",
      "type": "shell",
      "command": "cmake",
      "args": ["../"],
      "options": {
        "cwd": "${fileDirname}/build"
      }
    },
    {
      "label": "make",
      "type": "shell",
      "command": "make",
      "args": [],
      "options": {
        "cwd": "${fileDirname}/build"
      }
    },
    {
      "label": "build",
      "dependsOn": ["cmake", "make"]
    }
  ]
}
```

可以看出，上面的 `tasks.json` 文件主要包含三个命令：

- label 为`cmake`的任务：执行 shell 类型的 cmake 命令，其参数为 `../`，执行时所在的目录为${fileDirname}/build。**这个命令等价于在`build`目录下执行`cmake ../`**
- label 为`make`的任务：执行 shell 类型的 make 命令，没有参数，执行时所在的目录为${fileDirname}/build。**这个命令等价于在`build`目录下执行`make`**
- label 为`build`的任务：该任务由 cmake 和 make 任务组成，也就是将上面两条命令执行的过程组合成一个 build 任务。

所以执行`build`任务，相当于在 build 目录下执行了 `cmake ../` 和 `make` 两条命令，完成了 CMake 的编译过程。

- 在 VSCode 的主菜单中，选择 **Terminal>Run Task…**，然后选择 **build** ，再选择 **“continue without scanning the task output”**，可以在编辑器下方的终端显示界面中看到，VSCode 执行完成了 cmake 和 make 两个任务：

  ![cmake任务](https://img-blog.csdnimg.cn/956be2c0a4c141a6a0cd514dcb546637.png)

  ![make任务](https://img-blog.csdnimg.cn/fcf6a0b534144479bf1bf73a8c10f1af.png)

  此时的工程文件将与第 2 节使用命令行完成 CMake 编译一样，因为两种方式的本质是完全一样的。

### 4. VSCode 中调试 CMake 工程代码

如上所述，完成 CMake 编译过程后，将会在 build 目录下生成一个可执行文件`helloCMake`，下面将介绍如何在 VSCode 中对其进行调试：

1. 在 VSCode 的上方菜单中，选择 Run -> Add Configuration，会生成一个空白的`launch.json`文件：

```json
{
  "version": "0.2.0",
  "configurations": []
}
```

1. 我们要做的就是在该文件中告诉 VSCode：用 gdb 调试前面生成的可执行文件，在`launch.json`文件中添加如下内容：

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "name": "g++ - Build and debug active file",
      "type": "cppdbg",
      "request": "launch",
      "program": "${fileDirname}/build/${fileBasenameNoExtension}",
      "args": ["para1", "para2"],
      "stopAtEntry": false,
      "cwd": "${fileDirname}",
      "environment": [],
      "externalConsole": false,
      "MIMode": "gdb",
      "setupCommands": [
        {
          "description": "Enable pretty-printing for gdb",
          "text": "-enable-pretty-printing",
          "ignoreFailures": true
        }
      ],
      "preLaunchTask": "build",
      "miDebuggerPath": "/usr/bin/gdb"
    }
  ]
}
```

其中，

- `"program"`：用于指定要调试的可执行文件，这里用变量名指代，其值就是`helloCMake`
- `"args"`：执行代码时，需要添加的命令行参数
- `prelaunchTask`：在执行 gdb 调试前，预先需要执行的任务，这里设置为`"build"`，就是指定第 3 节中配置完成的 build 任务，即在 gdb 调试前，先执行 cmake 和 make

1. 回到 helloCMake.cpp 文件，打上断点，然后按 F5，即可实现代码调试：

![Debug](https://img-blog.csdnimg.cn/ea3217171c8b491782bfe8875805dbc0.png)
