# Queue

在 java5 中新增加了 java.util.Queue 接口，用以支持队列的常见操作。该接口扩展了 java.util.Collection 接口。

```
    public interface Queue<E>
        extends Collection<E>
```

除了基本的 Collection 操作外，队列还提供其他的插入、提取和检查操作。每个方法都存在两种形式：一种抛出异常(操作失败时)，另一种返回一个特殊值(null 或 false，具体取决于操作)。
![](http://dl2.iteye.com/upload/attachment/0099/4684/0a3af09c-84c1-3192-a396-788778793770.jpg)
队列通常(但并非一定)以 FIFO(先进先出)的方式排序各个元素。不过优先级队列和 LIFO 队列(或堆栈)例外，前者根据提供的比较器或元素的自然顺序对元素进行排序，后者按 LIFO(后进先出)的方式对元素进行排序。
在 FIFO 队列中，所有的新元素都插入队列的末尾，移除元素从队列头部移除。

Queue 使用时要尽量避免 Collection 的 add()和 remove()方法，而是要使用 offer()来加入元素，使用 poll()来获取并移出元素。它们的优点是通过返回值可以判断成功与否，add()和 remove()方法在失败的时候会抛出异常。 如果要使用前端而不移出该元素，使用 element()或者 peek()方法。
![](http://dl2.iteye.com/upload/attachment/0099/4686/6c5cd3f1-9cb8-3af5-8be6-558e880fb143.jpg)
offer 方法可插入一个元素，否则返回 false。这与 Collection.add 方法不同，该方法只能通过抛出未经检查的异常使添加元素失败。
remove() 和 poll() 方法可移除和返回队列的头。到底从队列中移除哪个元素是队列排序策略的功能，而该策略在各种实现中是不同的。remove() 和 poll() 方法仅在队列为空时其行为有所不同：remove() 方法抛出一个异常，而 poll() 方法则返回 null。
element() 和 peek() 返回，但不移除，队列的头。

Queue 实现通常不允许插入 null 元素，尽管某些实现(如 LinkedList)并不禁止插入 null。即使在允许 null 的实现中，也不应该将 null 插入到 Queue 中，因为 null 也用作 poll 方法的一个特殊返回值，表明队列不包含元素。

值得注意的是 LinkedList 类实现了 Queue 接口，因此我们可以把 LinkedList 当成 Queue 来用。

```
import java.util.Queue;
import java.util.LinkedList;

public class TestQueue {
    public static void main(String[] args) {
        Queue<String> queue = new LinkedList<String>();
        queue.offer("Hello");
        queue.offer("World!");
        queue.offer("你好！");
        System.out.println(queue.size());
        String str;
        while((str=queue.poll())!=null){
            System.out.print(str);
        }
        System.out.println();
        System.out.println(queue.size());
    }
}
```

# Deque:双端队列

![](http://img.my.csdn.net/uploads/201212/07/1354809676_3123.png)
Deque 在 Queue 的基础上增加了更多的操作方法。
![](http://img.my.csdn.net/uploads/201212/07/1354809693_2460.png)
从上图可以看到，Deque 不仅具有 FIFO 的 Queue 实现，也有 FILO 的实现，也就是不仅可以实现队列，也可以实现一个堆栈。

同时在 Deque 的体系结构图中可以看到，实现一个 Deque 可以使用数组(ArrayDeque)，同时也可以使用链表(LinkedList)，还可以同实现一个支持阻塞的线程安全版本队列 LinkedBlockingDeque。

```
public interface Deque<E>
	extends Queue<E>
```

一个线性 collection，支持在两端插入和移除元素。
名称 deque 是“double ended queue(双端队列)”的缩写，通常读为“deck”。
大多数 Deque 实现对于它们能够包含的元素数没有固定限制，但此接口既支持有容量限制的双端队列，也支持没有固定大小限制的双端队列。
![](http://dl2.iteye.com/upload/attachment/0099/4690/b1956624-3f28-312c-808a-301e67059ce8.jpg)
此接口定义在双端队列两端访问元素的方法。提供插入、移除和检查元素的方法。因为此接口**继承了队列接口 Queue**，所以其每种方法也存在两种形式：一种形式在操作失败时抛出异常，另一种形式返回一个特殊值(null 或 false，具体取决于操作)。

**a、\*\***在将双端队列用作队列时，将得到 FIFO(先进先出)行为\*\*。将元素添加到双端队列的末尾，从双端队列的开头移除元素。从 Queue 接口继承的方法完全等效于 Deque 方法，如下表所示：

![img](http://dl2.iteye.com/upload/attachment/0099/4692/f769ea83-1a83-3bec-84df-52342cbe00b8.jpg)

**b、\*\***用作 LIFO(后进先出)堆栈。应优先使用此接口而不是遗留 Stack 类\*\*。在将双端队列用作堆栈时，元素被推入双端队列的开头并从双端队列开头弹出。堆栈方法完全等效于 Deque 方法，如下表所示：

![img](http://dl2.iteye.com/upload/attachment/0099/4694/610121eb-2298-36a9-918a-73cdaa085df9.jpg)

## ArrayDeque

![](http://img.my.csdn.net/uploads/201212/07/1354809706_2576.png)
对于数组实现的 Deque 来说，数据结构上比较简单，只需要一个存储数据的数组以及头尾两个索引即可。由于数组是固定长度的，所以很容易就得到数组的头和尾，那么对于数组的操作只需要移动头和尾的索引即可。

特别说明的是 ArrayDeque 并不是一个固定大小的队列，每次队列满了以后就将队列容量扩大一倍(doubleCapacity())，因此加入一个元素总是能成功，而且也不会抛出一个异常。也就是说 ArrayDeque 是一个没有容量限制的队列。

同样继续性能的考虑，使用 System.arraycopy 复制一个数组比循环设置要高效得多。
