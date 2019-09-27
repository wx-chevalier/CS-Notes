# HashSet

HashSet 实现 Set 接口，由哈希表（实际上是 HashMap）支持，但不保证 Set 的迭代顺序，并允许使用 null 元素。HashSet 的时间复杂度跟 HashMap 一致，如果没有哈希冲突则时间复杂度为 O(1)，如果存在哈希冲突则时间复杂度不超过 O(n)。所以，在日常编码中，可以使用 HashSet 判断主键是否存在。

# 应用示例

## 重复性判断

```java
/** 查找第一个重复字符 */
public static Character findFirstRepeatedChar(String string) {
    // 检查空字符串
    if (Objects.isNull(string) || string.isEmpty()) {
        return null;
    }

    // 查找重复字符
    char[] charArray = string.toCharArray();
    Set charSet = new HashSet<>(charArray.length);
    for (char ch : charArray) {
        if (charSet.contains(ch)) {
            return ch;
        }
        charSet.add(ch);
    }

    // 默认返回为空
    return null;
}
```

其中，由于 Set 的 add 函数有个特性——如果添加的元素已经再集合中存在，则会返回 false。可以简化代码为：

```java
if (!charSet.add(ch)) {
    return ch;
}
```

# 源码分析
