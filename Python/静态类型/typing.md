# typing

动态语言的灵活性使其在做一些工具，脚本时非常方便，但是同时也给大型项目的开发带来了一些麻烦。自 python3.5 开始，PEP484 为 python 引入了类型注解(type hints),虽然在 pep3107 定义了函数注释(function annotation)的语法,但仍然故意留下了一些未定义的行为.现在已经拥有许多对于静态类型的分析的第三方工具,而 pep484 引入了一个模块来提供这些工具，同时还规定一些不能使用注释(annoation)的情况：

```py
# 一个典型的函数注释例子，为参数加上了类型
def greeting(name: str) -> str:
    return 'Hello ' + name
```

# Typing Basic | 类型机制

## Type Alias | 类型别名

```py
from typing import List
Vector = List[float]

def scale(scalar: float, vector: Vector)->Vector:
    return [scalar*num for num in vector]

new_vector = scale(2.0, [1.0, -4.2, 5.4])
```

类型别名有助于简化一些复杂的类型声明：

```py
from typing import Dict, Tuple, List

ConnectionOptions = Dict[str, str]
Address = Tuple[str, int]
Server = Tuple[Address, ConnectionOptions]

def broadcast_message(message: str, servers: List[Server]) -> None:
    # ...

# The static type checker will treat the previous type signature as
# being exactly equivalent to this one.
def broadcast_message(
        message: str,
        servers: List[Tuple[Tuple[str, int], Dict[str, str]]]) -> None:
    pass
```

## New Type | 新类型

使用 NewType 来辅助函数创造不同的类型：

```py
from typing import NewType

UserId = NewType("UserId", int)
some_id = UserId(524313)
```

静态类型检查器将将新类型视为原始类型的子类。这对于帮助捕获逻辑错误非常有用：

```py
def get_user_name(user_id: UserId) -> str:
    pass

# typechecks
user_a = get_user_name(UserId(42351))

# does not typecheck; an int is not a UserId
user_b = get_user_name(-1)
```

# Primitives | 基础类型

## Any

Any 是一种特殊的类型，静态类型检查器将将每个类型视为与任何类型和任何类型兼容，与每个类型兼容。

```py
from typing import Any

a = None    # type: Any
a = []      # OK
a = 2       # OK

s = ''      # type: str
s = a       # OK

def foo(item: Any) -> int:
    # Typechecks; 'item' could be any type,
    # and that type might have a 'bar' method
    item.bar()
    ...
```

# Function | 函数

## Callable | 回调

回调函数可以使用类似 `Callable[[Arg1Type, Arg2Type],ReturnType]` 的类型注释：

```py
from typing import Callable

def feeder(get_next_item: Callable[[], str]) -> None:
    # Body

def async_query(on_success: Callable[[int], None],
                on_error: Callable[[int, Exception], None]) -> None:
    # Body
```

# Generic | 泛型

因为容器中的元素的类型信息由于泛型不同通过一般方式静态推断，因此抽象类被用来拓展表示容器中的元素：

```py
from typing import Mapping, Sequence

def notify_by_email(employees: Sequence[Employee],
                    overrides: Mapping[str, str]) -> None: ...
```

可以通过 typing 中的 TypeVar 将泛型参数化：

```py
from typing import Sequence, TypeVar

T = TypeVar('T')      # 申明类型变量

def first(l: Sequence[T]) -> T:   # Generic function
    return l[0]
```

定义了 `Generic[T]` 作为 LoggedVar 的基类，同时 T 也作为了方法中的参数。

```py
from typing import TypeVar, Generic
from logging import Logger

T = TypeVar('T')

class LoggedVar(Generic[T]):
    def __init__(self, value: T, name: str, logger: Logger) -> None:
        self.name = name
        self.logger = logger
        self.value = value

    def set(self, new: T) -> None:
        self.log('Set ' + repr(self.value))
        self.value = new

    def get(self) -> T:
        self.log('Get ' + repr(self.value))
        return self.value

    def log(self, message: str) -> None:
        self.logger.info('%s: %s', self.name, message)
```
