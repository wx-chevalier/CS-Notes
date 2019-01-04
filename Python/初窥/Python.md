# Python

Python 是一个脚本解释器, 可以从命令行运行脚本, 也可以在脚本上双击, 象运行其他应用程序一样。它还是一个交互 shell, 可以执行任意的语句和表达式。Python 的交互 shell 可以计算任意的 Python 表达式, 包括任何基本的数学表达式。交互 shell 可以执行任意的 Python 语句, 包括 print 语句。也可以给变量赋值, 并且变量值在 shell 打开时一直有效(一旦关毕交互
Sheel , 变量值将丢失)。
![](http://7xkt0f.com1.z0.glb.clouddn.com/%E6%97%A0%E6%A0%87%E9%A2%981.png)

表格 编程语言数据类型的比较

| 静态类型定义语言 | 一种在编译期间数据类型固定的语言。大多数静态类型定义语言是通过要求在使用所有变量之前声明它们的数据类型来保证这一点的。Java 和 C 是静态类型定义语言。                                                    |
| ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 动态类型定义语言 | 一种在运行期间才去确定数据类型的语言, 与静态类型定义相反。VBScript 和 Python 是动态类型定义的, 因为它们确定一个变量的类型是在您第一次给它赋值的时候。                                                   |
| 强类型定义语言   | 一种总是强制类型定义的语言。 Java 和 Python 是强制类型定义的。您有一个整数, 如果不明确地进行转换 , 不能将把它当成一个字符串。                                                                           |
| 弱类型定义语言   | 一种类型可以被忽略的语言, 与强类型定义相反。 VBScript 是弱类型定义的。在 VBScript 中, 您可以将字符串 '12' 和整数 3 进行连接得到字符串'123', 然后可以把它看成整数 123 , 所有这些都不需要任何的显示转换。 |

## Reference

- [听说你会 Python ？ ](http://manjusaka.itscoder.com/2016/11/18/Someone-tell-me-that-you-think-Python-is-simple/)

### Books

- [零基础学](http://python.xiaoleilu.com/index.html)

- [Python 进阶](https://github.com/eastlakeside/interpy-zh)

### Practices & Resources

- [awesome-python-cn](https://github.com/jobbole/awesome-python-cn)

# Quick Start

## Installation

## Hello Word

```
import os
import os.path
import time
  
  
#class used to handle one application instance mechanism
class ApplicationInstance:
  
    #specify the file used to save the application instance pid
    def __init__( self, pid_file ):
        self.pid_file = pid_file
        self.check()
        self.startApplication()
  
    #check if the current application is already running
    def check( self ):
        #check if the pidfile exists
        if not os.path.isfile( self.pid_file ):
            return
  
        #read the pid from the file
        pid = 0
        try:
            file = open( self.pid_file, 'rt' )
            data = file.read()
            file.close()
            pid = int( data )
        except:
            pass
  
        #check if the process with specified by pid exists
        if 0 == pid:
            return
  
        try:
            os.kill( pid, 0 )    #this will raise an exception if the pid is not valid
        except:
            return
  
        #exit the application
        print "The application is already running !"
        exit(0) #exit raise an exception so don't put it in a try/except block
  
    #called when the single instance starts to save it's pid
    def startApplication( self ):
        file = open( self.pid_file, 'wt' )
        file.write( str( os.getpid() ) )
        file.close()
  
    #called when the single instance exit ( remove pid file )
    def exitApplication( self ):
        try:
            os.remove( self.pid_file )
        except:
            pass
  
  
if __name__ == '__main__':
    #create application instance
    appInstance = ApplicationInstance( '/tmp/myapp.pid' )
  
    #do something here
    print "Start MyApp"
    time.sleep(5)    #sleep 5 seconds
    print "End MyApp"
  
    #remove pid file
appInstance.exitApplication()
```

### Mutex

```
from win32event import CreateMutex
from win32api import CloseHandle, GetLastError
from winerror import ERROR_ALREADY_EXISTS
class singleinstance:
    """ Limits application to single instance """
    def __init__(self):
        self.mutexname = "testmutex_{D0E858DF-985E-4907-B7FB-8D732C3FC3B9}"
        self.mutex = CreateMutex(None, False, self.mutexname)
        self.lasterror = GetLastError()
    
    def aleradyrunning(self):
        return (self.lasterror == ERROR_ALREADY_EXISTS)
        
    def __del__(self):
        if self.mutex:
            CloseHandle(self.mutex)
#---------------------------------------------#
# sample usage:
#
from singleinstance import singleinstance
from sys import exit
# do this at beginnig of your application
myapp = singleinstance()
# check is another instance of same program running
if myapp.aleradyrunning():
    print "Another instance of this program is already running"
    exit(0)
# not running, safe to continue...
print "No another instance is running, can continue here"
```

## VirtualEnv:多环境配置的沙盒

在安装 virtualenv 之前，我们需要安装至少有一个版本的[Python](http://lib.csdn.net/base/11)；因为 virtualenv 是 python 的一个第三方模块，必须基于 python 环境才能安装；

如果你的 python 环境有 pip，那么直接使用命令：pip install virtualenv 安装即可；否则需要下载源码，然后使用命令：python install setup.py 来安装

To install globally with pip (if you have pip 1.3 or greater installed globally):

```
$ [sudo] pip install virtualenv


```

Or to get the latest unreleased dev version:

```
$ [sudo] pip install https://github.com/pypa/virtualenv/tarball/develop


```

To install version X.X globally from source:

```
$ curl -O https://pypi.python.org/packages/source/v/virtualenv/virtualenv-X.X.tar.gz
$ tar xvfz virtualenv-X.X.tar.gz
$ cd virtualenv-X.X
$ [sudo] python setup.py install
```

To _use_ locally from source:

```
$ curl -O https://pypi.python.org/packages/source/v/virtualenv/virtualenv-X.X.tar.gz
$ tar xvfz virtualenv-X.X.tar.gz
$ cd virtualenv-X.X
$ python virtualenv.py myVE


```

安装之后需要新建 virtualenv 的独立环境，具体可以查看其帮助命令：virtualenv -h

![img](http://img.blog.csdn.net/20150603090151676?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvZml2ZTM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

常用的参数有：

-p：指定一个版本 python 环境；通常当你的系统中安装了多个 python 版本时会用到；默认情况下 virtualenv 会优先选取它的宿主 python 环境，即它安装在那个 python 版本下就会默认选择哪个版本作为默认 python 隔离环境。

--no-site-packages：不使用系统环境的 python 安装包，即隔离包中不能使用真实 python 环境的安装包；当前版本这个选项是默认的。

--system-site-packages：与上面相反，使隔离环境能访问系统环境的 python 安装包

--distribute：copy 一个 python 环境的分支，默认会安装 setup、pip、wheel 等基础模块

### 激活与切换

# Modules

## 命名空间

命名空间是从命名到对象的映射。以下有一些命名空间的例子：内置命名(像 abs() 这样的函数，以及内置异常名)集，模块中的全局命名，函数调用中的局部命名。某种意义上讲对象的属性集也是一个命名空间。关于命名空间需要了解的一件很重要的事就是不同命名空间中的命名没有任何联系，例如两个不同的模块可能都会定义一个名为“maximize”的函数而不会发生混淆－－用户必须以模块名为前缀来引用它们。
Python 中任何一个“.”之后的命名为属性－－例如，表达式 z.real 中的 real 是对象 z 的一个属性。严格来讲，从模块中引用命名是引用属性：表达式 modname.funcname 中，modname 是一个模块对象， funcname 是它的一个属性。因此，模块的属性和模块中的全局命名有直接的映射关系：它们共享同一命名空间。
不同的命名空间在不同的时刻创建，有不同的生存期。包含内置命名的命名空间在 Python 解释器启动时创建，会一直保留，不被删除。模块的全局命名空间在模块定义被读入时创建，通常，模块命名空间也会一直保存到解释器退出。由解释器在最高层调用执行的语句，不管它是从脚本文件中读入还是来自交互式输入，都是**main**模块的一部分，所以它们也拥有自己的命名空间。(内置命名也同样被包含在一个模块中，它被称作**builtin**。)
当函数被调用时创建一个局部命名空间，函数反正返回过抛出一个未在函数内处理的异常时删除。(实际上，说是遗忘更为贴切)。当然，每一个递归调用拥有自己的命名空间。

## [json-sempai](https://github.com/kragniz/json-sempai):Use JSON files as Modules
