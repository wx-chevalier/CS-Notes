# Ray

Ray 占据了一个独特的中间地带。它并没有引入新的概念，而是采用了函数和类的概念，并将它们转换为分布式的任务和 actor。Ray 可以在不做出重大修改的情况下对串行应用程序进行并行化。

# 并行任务

要将 Python 函数 f 转换为一个“远程函数”（可以远程和异步执行的函数），可以使用 @ray.remote 装饰器来声明这个函数。然后函数调用 f.remote() 将立即返回一个 future（future 是对最终输出的引用），实际的函数执行将在后台进行（我们将这个函数执行称为任务）。

```py
import ray
import time

# Start Ray.
ray.init()

@ray.remote
def f(x):
   time.sleep(1)
   return x

# Start 4 tasks in parallel.
result_ids = []
for i in range(4):
   result_ids.append(f.remote(i))

# Wait for the tasks to complete and retrieve the results.
# With at least 4 cores, this will take 1 second.
results = ray.get(result_ids)  # [0, 1, 2, 3]
```

因为对 f.remote(i) 的调用会立即返回，所以运行这行代码四次就可以并行执行 f 的四个副本。

## 任务依赖

一个任务还可以依赖于其他任务。在下面的代码中，multiply_matrices 任务依赖两个 create_matrix 任务的输出，因此在执行前两个任务之前它不会先执行。前两个任务的输出将自动作为参数传给第三个任务，future 将被替换为相应的值。通过这种方式，任务可以按照任意的 DAG 依赖关系组合在一起。

```py
import numpy as np

@ray.remote
def create_matrix(size):
   return np.random.normal(size=size)

@ray.remote
def multiply_matrices(x, y):
   return np.dot(x, y)

x_id = create_matrix.remote([1000, 1000])
y_id = create_matrix.remote([1000, 1000])
z_id = multiply_matrices.remote(x_id, y_id)

# Get the results.
z = ray.get(z_id)
```

## 值聚合

就像经典的 WordCount 程序，我们常常需要将值聚合起来。但在很多应用程序中，跨多台计算机聚合大型向量可能会造成性能瓶颈。在这个时候，只要修改一行代码就可以将聚合的运行时间从线性降为对数级别，即聚合值的数量。

```py
import time

@ray.remote
def add(x, y):
   time.sleep(1)
   return x + y

# Aggregate the values slowly. This approach takes O(n) where n is the
# number of values being aggregated. In this case, 7 seconds.
id1 = add.remote(1, 2)
id2 = add.remote(id1, 3)
id3 = add.remote(id2, 4)
id4 = add.remote(id3, 5)
id5 = add.remote(id4, 6)
id6 = add.remote(id5, 7)
id7 = add.remote(id6, 8)
result = ray.get(id7)

# Aggregate the values in a tree-structured pattern. This approach
# takes O(log(n)). In this case, 3 seconds.
id1 = add.remote(1, 2)
id2 = add.remote(3, 4)
id3 = add.remote(5, 6)
id4 = add.remote(7, 8)
id5 = add.remote(id1, id2)
id6 = add.remote(id3, id4)
id7 = add.remote(id5, id6)
result = ray.get(id7)
```

![](https://ww1.sinaimg.cn/large/007rAy9hgy1g0egpe5ntnj30q20akdgs.jpg)

代码还可以进一步简化：

```py
# Slow approach.
values = [1, 2, 3, 4, 5, 6, 7, 8]
while len(values) > 1:
   values = [add.remote(values[0], values[1])] + values[2:]
result = ray.get(values[0])

# Fast approach.
values = [1, 2, 3, 4, 5, 6, 7, 8]
while len(values) > 1:
   values = values[2:] + [add.remote(values[0], values[1])]
result = ray.get(values[0])
```

# Actor

可以使用 @ray.remote 装饰器声明一个 Python 类。在实例化类时，Ray 会创建一个新的“actor”，这是一个运行在集群中并持有类对象副本的进程。对这个 actor 的方法调用转变为在 actor 进程上运行的任务，并且可以访问和改变 actor 的状态。通过这种方式，可以在多个任务之间共享可变状态，这是远程函数无法做到的。

各个 actor 按顺序执行方法（每个方法都是原子方法），因此不存在竞态条件。可以通过创建多个 actor 来实现并行性。

```py
@ray.remote
class Counter(object):
 def __init__(self):
 self.x = 0

 def inc(self):
 self.x += 1

 def get_value(self):
 return self.x

# Create an actor process.
c = Counter.remote()

# Check the actor's counter value.
print(ray.get(c.get_value.remote()))# 0

# Increment the counter twice and check the value again.
c.inc.remote()
c.inc.remote()
print(ray.get(c.get_value.remote()))# 2
```

上面的例子是 actor 最简单的用法。Counter.remote() 创建一个新的 actor 进程，它持有一个 Counter 对象副本。对 c.get_value.remote() 和 c.inc.remote() 的调用会在远程 actor 进程上执行任务并改变 actor 的状态。

## 句柄

actor 的一个最强大的地方在于我们可以将句柄传给它，让其他 actor 或其他任务都调用同一 actor 的方法。以下示例创建了一个可以保存消息的 actor。几个 worker 任务反复将消息推送给 actor，主 Python 脚本定期读取消息。

```py
import time

@ray.remote
class MessageActor(object):
 def __init__(self):
 self.messages = []

 def add_message(self, message):
 self.messages.append(message)

 def get_and_clear_messages(self):
 messages = self.messages
 self.messages = []
 return messages

# Define a remote function which loops around and pushes
# messages to the actor.
@ray.remote
def worker(message_actor, j):
 for i in range(100):
 time.sleep(1)
 message_actor.add_message.remote(
 "Message {} from actor {}.".format(i, j))

# Create a message actor.
message_actor = MessageActor.remote()

# Start 3 tasks that push messages to the actor.
[worker.remote(message_actor, j) for j in range(3)]

# Periodically get the messages and print them.
for _ in range(100):
 new_messages = ray.get(message_actor.get_and_clear_messages.remote())
 print("New messages:", new_messages)
 time.sleep(1)

# This script prints something like the following:
# New messages: []
# New messages: ['Message 0 from actor 1.', 'Message 0 from actor 0.']
# New messages: ['Message 0 from actor 2.', 'Message 1 from actor 1.', 'Message 1 from actor 0.', 'Message 1 from actor 2.']
# New messages: ['Message 2 from actor 1.', 'Message 2 from actor 0.', 'Message 2 from actor 2.']
# New messages: ['Message 3 from actor 2.', 'Message 3 from actor 1.', 'Message 3 from actor 0.']
# New messages: ['Message 4 from actor 2.', 'Message 4 from actor 0.', 'Message 4 from actor 1.']
# New messages: ['Message 5 from actor 2.', 'Message 5 from actor 0.', 'Message 5 from actor 1.']
```
