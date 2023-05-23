# Make

Make 这个词，英语的意思是"制作"。Make 命令直接用了这个意思，就是要做出某个文件。比如，要做出文件 a.txt，就可以执行下面的命令。

```
$ make a.txt
```

但是，如果你真的输入这条命令，它并不会起作用。因为 Make 命令本身并不知道，如何做出 a.txt，需要有人告诉它，如何调用其他命令完成这个目标。比如，假设文件 a.txt 依赖于 b.txt 和 c.txt，是后面两个文件连接（cat 命令）的产物。那么，make 需要知道下面的规则。

```
 a.txt: b.txt c.txt
     cat b.txt c.txt > a.txt
```

也就是说，make a.txt 这条命令的背后，实际上分成两步：第一步，确认 b.txt 和 c.txt 必须已经存在，第二步使用 cat 命令 将这个两个文件合并，输出为新文件。像这样的规则，都写在一个叫做 Makefile 的文件中，Make 命令依赖这个文件进行构建。Makefile 文件也可以写为 makefile，或者用命令行参数指定为其他文件名。

```
$ make -f rules.txt
# 或者
$ make --file=rules.txt
```

上面代码指定 make 命令依据 rules.txt 文件中的规则，进行构建。总之，make 只是一个根据指定的 Shell 命令进行构建的工具。它的规则很简单，你规定要构建哪个文件、它依赖哪些源文件，当那些文件有变动时，如何重新构建它。

# Make 文件格式

Makefile 文件由一系列规则（rules）构成。每条规则的形式如下。

```
<target> : <prerequisites>
[tab]  <commands>
```

上面第一行冒号前面的部分，叫做"目标"（target），冒号后面的部分叫做"前置条件"（prerequisites）；第二行必须由一个 tab 键起首，后面跟着"命令"（commands）。"目标"是必需的，不可省略；"前置条件"和"命令"都是可选的，但是两者之中必须至少存在一个。每条规则就明确两件事：构建目标的前置条件是什么，以及如何构建。下面就详细讲解，每条规则的这三个组成部分。

## 目标（target）

一个目标（target）就构成一条规则。目标通常是文件名，指明 Make 命令所要构建的对象，比如上文的 a.txt 。目标可以是一个文件名，也可以是多个文件名，之间用空格分隔。

除了文件名，目标还可以是某个操作的名字，这称为"伪目标"（phony target）。

```s
clean:
      rm *.o
```

上面代码的目标是 clean，它不是文件名，而是一个操作的名字，属于"伪目标 "，作用是删除对象文件。

```
$ make  clean
```

但是，如果当前目录中，正好有一个文件叫做 clean，那么这个命令不会执行。因为 Make 发现 clean 文件已经存在，就认为没有必要重新构建了，就不会执行指定的 rm 命令。为了避免这种情况，可以明确声明 clean 是"伪目标"，写法如下。

```
.PHONY: clean
clean:
        rm *.o temp
```

声明 clean 是"伪目标"之后，make 就不会去检查是否存在一个叫做 clean 的文件，而是每次运行都执行对应的命令。像.PHONY 这样的内置目标名还有不少，可以查看[手册](http://www.gnu.org/software/make/manual/html_node/Special-Targets.html#Special-Targets)。如果 Make 命令运行时没有指定目标，默认会执行 Makefile 文件的第一个目标。

```
$ make
```

上面代码执行 Makefile 文件的第一个目标。

## 前置条件（prerequisites）

前置条件通常是一组文件名，之间用空格分隔。它指定了"目标"是否重新构建的判断标准：只要有一个前置文件不存在，或者有过更新（前置文件的 last-modification 时间戳比目标的时间戳新），"目标"就需要重新构建。

```
result.txt: source.txt
    cp source.txt result.txt
```

上面代码中，构建 result.txt 的前置条件是 source.txt 。如果当前目录中，source.txt 已经存在，那么 `make result.txt` 可以正常运行，否则必须再写一条规则，来生成 source.txt 。

```
source.txt:
    echo "this is the source" > source.txt
```

上面代码中，source.txt 后面没有前置条件，就意味着它跟其他文件都无关，只要这个文件还不存在，每次调用`make source.txt`，它都会生成。

```
$ make result.txt
$ make result.txt
```

上面命令连续执行两次 `make result.txt`。第一次执行会先新建 source.txt，然后再新建 result.txt。第二次执行，Make 发现 source.txt 没有变动（时间戳晚于 result.txt），就不会执行任何操作，result.txt 也不会重新生成。如果需要生成多个文件，往往采用下面的写法。

```
source: file1 file2 file3
```

上面代码中，source 是一个伪目标，只有三个前置文件，没有任何对应的命令。

```
$ make source
```

执行 `make source` 命令后，就会一次性生成 file1，file2，file3 三个文件。这比下面的写法要方便很多。

```
$ make file1
$ make file2
$ make file3
```

## 命令（commands）

命令（commands）表示如何更新目标文件，由一行或多行的 Shell 命令组成。它是构建"目标"的具体指令，它的运行结果通常就是生成目标文件。每行命令之前必须有一个 tab 键。如果想用其他键，可以用内置变量.RECIPEPREFIX 声明。

```s
.RECIPEPREFIX = >
all:
> echo Hello, world
```

上面代码用.RECIPEPREFIX 指定，大于号（>）替代 tab 键。所以，每一行命令的起首变成了大于号，而不是 tab 键。

需要注意的是，每行命令在一个单独的 shell 中执行。这些 Shell 之间没有继承关系。

```
var-lost:
    export foo=bar
    echo "foo=[$$foo]"
```

上面代码执行后（`make var-lost`），取不到 foo 的值。因为两行命令在两个不同的进程执行。一个解决办法是将两行命令写在一行，中间用分号分隔。

```
var-kept:
    export foo=bar; echo "foo=[$$foo]"
```

另一个解决办法是在换行符前加反斜杠转义。

```
var-kept:
    export foo=bar; \
    echo "foo=[$$foo]"
```

最后一个方法是加上`.ONESHELL:`命令。

```
.ONESHELL:
var-kept:
    export foo=bar;
    echo "foo=[$$foo]"
```

# 文件格式

## 注释

井号（#）在 Makefile 中表示注释。

```
# 这是注释
result.txt: source.txt
    # 这是注释
    cp source.txt result.txt # 这也是注释
```

## 回声（echoing）

正常情况下，make 会打印每条命令，然后再执行，这就叫做回声（echoing）。

```
test:
    # 这是测试
```

执行上面的规则，会得到下面的结果。

```
$ make test
# 这是测试
```

在命令的前面加上@，就可以关闭回声。

```
test:
    @# 这是测试
```

现在再执行 `make test`，就不会有任何输出。由于在构建过程中，需要了解当前在执行哪条命令，所以通常只在注释和纯显示的 echo 命令前面加上@。

```
test:
    @# 这是测试
    @echo TODO
```

## 通配符

通配符（wildcard）用来指定一组符合条件的文件名。Makefile 的通配符与 Bash 一致，主要有星号（_）、问号（？）和 [...] 。比如，_.o 表示所有后缀名为 o 的文件。

```
clean:
        rm -f *.o
```

## 模式匹配

Make 命令允许对文件名，进行类似正则运算的匹配，主要用到的匹配符是%。比如，假定当前目录下有 f1.c 和 f2.c 两个源码文件，需要将它们编译为对应的对象文件。

```
%.o: %.c
```

等同于下面的写法。

```
f1.o: f1.c
f2.o: f2.c
```

使用匹配符%，可以将大量同类型的文件，只用一条规则就完成构建。

## 变量和赋值符

Makefile 允许使用等号自定义变量。

```
txt = Hello World
test:
    @echo $(txt)
```

上面代码中，变量 txt 等于 Hello World。调用时，变量需要放在 \$( ) 之中。

调用 Shell 变量，需要在美元符号前，再加一个美元符号，这是因为 Make 命令会对美元符号转义。

```
test:
    @echo $$HOME
```

有时，变量的值可能指向另一个变量。

```
v1 = $(v2)
```

上面代码中，变量 v1 的值是另一个变量 v2。这时会产生一个问题，v1 的值到底在定义时扩展（静态扩展），还是在运行时扩展（动态扩展）？如果 v2 的值是动态的，这两种扩展方式的结果可能会差异很大。为了解决类似问题，Makefile 一共提供了四个赋值运算符（=、:=、？=、+=），它们的区别请看 [StackOverflow](http://stackoverflow.com/questions/448910/makefile-variable-assignment)。

```s
# 在执行时扩展，允许递归扩展。
VARIABLE = value

# 在定义时扩展。
VARIABLE := value

# 只有在该变量为空时才设置值。
VARIABLE ?= value

# 将值追加到变量的尾端。
VARIABLE += value
```

## 内置变量（Implicit Variables）

Make 命令提供一系列内置变量，比如，$(CC) 指向当前使用的编译器，$(MAKE) 指向当前使用的 Make 工具。这主要是为了跨平台的兼容性，详细的内置变量清单见[手册](https://www.gnu.org/software/make/manual/html_node/Implicit-Variables.html)。

```
output:
    $(CC) -o output input.c
```

## 自动变量（Automatic Variables）

Make 命令还提供一些自动变量，它们的值与当前规则有关。主要有以下几个。

**（1）\$@**

$@指代当前目标，就是 Make 命令当前构建的那个目标。比如，`make foo`的 $@ 就指代 foo。

```
a.txt b.txt:
    touch $@
```

等同于下面的写法。

```
a.txt:
    touch a.txt
b.txt:
    touch b.txt
```

**（2）\$<**

$< 指代第一个前置条件。比如，规则为 t: p1 p2，那么$< 就指代 p1。

```
a.txt: b.txt c.txt
    cp $< $@
```

等同于下面的写法。

```
a.txt: b.txt c.txt
    cp b.txt a.txt
```

**（3）\$?**

$? 指代比目标更新的所有前置条件，之间以空格分隔。比如，规则为 t: p1 p2，其中 p2 的时间戳比 t 新，$?就指代 p2。

**（4）\$^**

$^ 指代所有前置条件，之间以空格分隔。比如，规则为 t: p1 p2，那么 $^ 就指代 p1 p2 。

**（5）\$\***

$* 指代匹配符 % 匹配的部分，比如% 匹配 f1.txt 中的f1，$\* 就表示 f1。

**（6）$(@D) 和 $(@F)**

$(@D) 和 $(@F) 分别指向 $@ 的目录名和文件名。比如，$@是 src/input.c，那么$(@D) 的值为 src，$(@F) 的值为 input.c。

**`（7）\$(**

`$( 分别指向 $< 的目录名和文件名。

所有的自动变量清单，请看[手册](https://www.gnu.org/software/make/manual/html_node/Automatic-Variables.html)。下面是自动变量的一个例子。

```
dest/%.txt: src/%.txt
    @[ -d dest ] || mkdir dest
    cp $< $@
```

上面代码将 src 目录下的 txt 文件，拷贝到 dest 目录下。首先判断 dest 目录是否存在，如果不存在就新建，然后，$< 指代前置文件（src/%.txt），$@ 指代目标文件（dest/%.txt）。

## 判断和循环

Makefile 使用 Bash 语法，完成判断和循环。

```
ifeq ($(CC),gcc)
  libs=$(libs_for_gcc)
else
  libs=$(normal_libs)
endif
```

上面代码判断当前编译器是否 gcc，然后指定不同的库文件。

```
LIST = one two three
all:
    for i in $(LIST); do \
        echo $$i; \
     done

# 等同于

all:
    for i in one two three; do \
        echo $i; \
    done
```

上面代码的运行结果。

```
one
two
three
```

## 函数

Makefile 还可以使用函数，格式如下。

```
$(function arguments)
# 或者
${function arguments}
```

Makefile 提供了许多[内置函数](http://www.gnu.org/software/make/manual/html_node/Functions.html)，可供调用。下面是几个常用的内置函数。

**（1）shell 函数**

shell 函数用来执行 shell 命令

```
srcfiles := $(shell echo src/{00..99}.txt)
```

**（2）wildcard 函数**

wildcard 函数用来在 Makefile 中，替换 Bash 的通配符。

```
srcfiles := $(wildcard src/*.txt)
```

**（3）subst 函数**

subst 函数用来文本替换，格式如下。

```
$(subst from,to,text)
```

下面的例子将字符串"feet on the street"替换成"fEEt on the strEEt"。

```
$(subst ee,EE,feet on the street)
```

下面是一个稍微复杂的例子。

```
comma:= ,
empty:=
# space变量用两个空变量作为标识符，当中是一个空格
space:= $(empty) $(empty)
foo:= a b c
bar:= $(subst $(space),$(comma),$(foo))
# bar is now `a,b,c'.
```

**（4）patsubst 函数**

patsubst 函数用于模式匹配的替换，格式如下。

```
$(patsubst pattern,replacement,text)
```

下面的例子将文件名"x.c.c bar.c"，替换成"x.c.o bar.o"。

```
$(patsubst %.c,%.o,x.c.c bar.c)
```

**（5）替换后缀名**

替换后缀名函数的写法是：变量名 + 冒号 + 后缀名替换规则。它实际上 patsubst 函数的一种简写形式。

```
min: $(OUTPUT:.js=.min.js)
```

上面代码的意思是，将变量 OUTPUT 中的后缀名 .js 全部替换成 .min.js 。

# 案例

## 执行多个目标

```
.PHONY: cleanall cleanobj cleandiff

cleanall : cleanobj cleandiff
        rm program

cleanobj
        rm *.o

cleandiff
        rm *.diff
```

上面代码可以调用不同目标，删除不同后缀名的文件，也可以调用一个目标（cleanall），删除所有指定类型的文件。

## 编译 C 语言项目

```
edit : main.o kbd.o command.o display.o
    cc -o edit main.o kbd.o command.o display.o

main.o : main.c defs.h
    cc -c main.c
kbd.o : kbd.c defs.h command.h
    cc -c kbd.c
command.o : command.c defs.h command.h
    cc -c command.c
display.o : display.c defs.h
    cc -c display.c

clean
     rm edit main.o kbd.o command.o display.o

.PHONY: edit clean
```

更完整的案例参考如下：

```s
# Makefile for the C code in concurrent-servers.
#
# This code is in the public domain.
CC = gcc
CCFLAGS = -std=gnu99 -Wall -O3 -g -DNDEBUG -pthread
LDFLAGS = -lpthread -pthread

# It's possible to compile uv-server after installing libuv. The full
# instructions for installation I used (including `make install`) are from:
# https://github.com/libuv/libuv/blob/master/README.md.
# libuv compiles into a shared library which is placed alongside the .a in the
# installation directory.
LDLIBUV = -luv -Wl,-rpath=/usr/local/lib

EXECUTABLES = \
	sequential-server \
	select-server \
	epoll-server \
	uv-server \
	uv-timer-sleep-demo \
	uv-timer-work-demo \
	uv-isprime-server \
	threadspammer \
	blocking-listener \
	nonblocking-listener \
	threaded-server

all: $(EXECUTABLES)

sequential-server: utils.c sequential-server.c
	$(CC) $(CCFLAGS) $^ -o $@ $(LDFLAGS)

select-server: utils.c select-server.c
	$(CC) $(CCFLAGS) $^ -o $@ $(LDFLAGS)

threaded-server: utils.c threaded-server.c
	$(CC) $(CCFLAGS) $^ -o $@ $(LDFLAGS)

epoll-server: utils.c epoll-server.c
	$(CC) $(CCFLAGS) $^ -o $@ $(LDFLAGS)

uv-server: utils.c uv-server.c
	$(CC) $(CCFLAGS) $^ -o $@ $(LDFLAGS) $(LDLIBUV)

uv-timer-sleep-demo: utils.c uv-timer-sleep-demo.c
	$(CC) $(CCFLAGS) $^ -o $@ $(LDFLAGS) $(LDLIBUV)

uv-timer-work-demo: utils.c uv-timer-work-demo.c
	$(CC) $(CCFLAGS) $^ -o $@ $(LDFLAGS) $(LDLIBUV)

uv-isprime-server: utils.c uv-isprime-server.c
	$(CC) $(CCFLAGS) $^ -o $@ $(LDFLAGS) $(LDLIBUV)

threadspammer: threadspammer.c
	$(CC) $(CCFLAGS) $^ -o $@ $(LDFLAGS)

blocking-listener: utils.c blocking-listener.c
	$(CC) $(CCFLAGS) $^ -o $@ $(LDFLAGS)

nonblocking-listener: utils.c nonblocking-listener.c
	$(CC) $(CCFLAGS) $^ -o $@ $(LDFLAGS)

.PHONY: clean format

clean:
	rm -f $(EXECUTABLES) *.o

format:
	clang-format -style=file -i *.c *.h

```
