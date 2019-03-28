# dict 默认值

众所周知，在 Python 中如果访问字典中不存在的键，会引发 KeyError 异常(JavaScript 中如果对象中不存在某个属性，则返回 undefined)。但是有时候，字典中的每个键都存在默认值是非常方便的。例如下面的例子：

```py
strings = ('puppy', 'kitten', 'puppy', 'puppy',
           'weasel', 'puppy', 'kitten', 'puppy')
counts = {}

for kw in strings:
    counts[kw] += 1
```

该例子统计 strings 中某个单词出现的次数，并在 counts 字典中作记录。单词每出现一次，在 counts 相对应的键所存的值数字加 1。但是事实上，运行这段代码会抛出 KeyError 异常，出现的时机是每个单词第一次统计的时候，因为 Python 的 dict 中不存在默认值的说法：

```py
strings = ('puppy', 'kitten', 'puppy', 'puppy',
           'weasel', 'puppy', 'kitten', 'puppy')
counts = {}

for kw in strings:
    if kw not in counts:
        counts[kw] = 1
    else:
        counts[kw] += 1

# counts:
# {'puppy': 5, 'weasel': 1, 'kitten': 2}
```

## dict.setdefault()

也可以通过 dict.setdefault()方法来设置默认值，dict.setdefault()方法接收两个参数，第一个参数是健的名称，第二个参数是默认值。假如字典中不存在给定的键，则返回参数中提供的默认值；反之，则返回字典中保存的值。利用 dict.setdefault()方法的返回值可以重写 for 循环中的代码，使其更加简洁：

```py
strings = ('puppy', 'kitten', 'puppy', 'puppy',
           'weasel', 'puppy', 'kitten', 'puppy')
counts = {}

for kw in strings:
    counts[kw] = counts.setdefault(kw, 0) + 1
```

## defaultdict

使用 `collections.defaultdict` 类，defaultdict 类就好像是一个 dict，但是它是使用一个类型来初始化的：

```py
>>> from collections import defaultdict
>>> dd = defaultdict(list)
>>> dd
defaultdict(<type 'list'>, {})
```

defaultdict 类的初始化函数接受一个类型作为参数，当所访问的键不存在的时候，可以实例化一个值作为默认值：

```py
>>> dd['foo']
[]
>>> dd
defaultdict(<type 'list'>, {'foo': []})
>>> dd['bar'].append('quux')
>>> dd
defaultdict(<type 'list'>, {'foo': [], 'bar': ['quux']})
```

需要注意的是，这种形式的默认值只有在通过 dict[key]或者 dict.**getitem**(key)访问的时候才有效。

```
>>> from collections import defaultdict
>>> dd = defaultdict(list)
>>> 'something' in dd
False
>>> dd.pop('something')
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
KeyError: 'pop(): dictionary is empty'
>>> dd.get('something')
>>> dd['something']
[]
```

defaultdict 类除了接受类型名称作为初始化函数的参数之外，还可以使用任何不带参数的可调用函数，到时该函数的返回结果作为默认值，这样使得默认值的取值更加灵活。下面用一个例子来说明，如何用自定义的不带参数的函数 zero()作为 defaultdict 类的初始化函数的参数：

```py
>>> from collections import defaultdict
>>> def zero():
...     return 0
...
>>> dd = defaultdict(zero)
>>> dd
defaultdict(<function zero at 0xb7ed2684>, {})
>>> dd['foo']
0
>>> dd
defaultdict(<function zero at 0xb7ed2684>, {'foo': 0})
```

利用 collections.defaultdict 来解决最初的单词统计问题，代码如下：

```py
from collections import defaultdict

strings = ('puppy', 'kitten', 'puppy', 'puppy',
           'weasel', 'puppy', 'kitten', 'puppy')
counts = defaultdict(lambda: 0)  # 使用lambda来定义简单的函数

for s in strings:
    counts[s] += 1
```

## defaultdict 的内部实现

在 defaultdict 类中又是如何来实现默认值的功能，这其中的关键是使用了看 `missing()` 这个方法：

```py
>>> from collections import defaultdict
>>> print defaultdict.__missing__.__doc__
__missing__(key) # Called by __getitem__ for missing key; pseudo-code:
  if self.default_factory is None: raise KeyError(key)
  self[key] = value = self.default_factory()
  return value
```

通过查看 `missing()` 方法的 docstring，可以看出当使用 `getitem()` 方法访问一个不存在的键时(dict[key]这种形式实际上是 `getitem()` 方法的简化形式)，会调用 `missing()` 方法获取默认值，并将该键添加到字典中去。文档中介绍，从 2.5 版本开始，如果派生自 dict 的子类定义了 `missing()` 方法，当访问不存在的键时，dict[key]会调用 `missing()` 方法取得默认值。
通过以下方式完成兼容新旧 Python 版本的代码：

```py
try:
    from collections import defaultdict
except ImportError:
    class defaultdict(dict):
      def __init__(self, default_factory=None, *a, **kw):
          dict.__init__(self, *a, **kw)
          self.default_factory = default_factory


      def __getitem__(self, key):
          try:
              return dict.__getitem__(self, key)
          except KeyError:
              return self.__missing__(key)


      def __missing__(self, key):
          self[key] = value = self.default_factory()
          return value
```
