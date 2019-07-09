# 流程控制

# 运算符

## 一元运算符

### ++ / --

```
public class Test{

    public static void main(String args[]){
        int i=0;
        i=i++;
        i=i++;
        System.out.println(i);

    }
}
// 输出结果为 0
```

整个过程实际上如下所示:

```
int oldValue = i;
i = i + 1;
i = oldValue;
```

# 循环

## while

## for

### for-in

### forEach

# 迭代器
