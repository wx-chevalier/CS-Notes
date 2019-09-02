# Condition

Condition 从拥有监控方法（wait,notify,notifyAll）的 Object 对象中抽离出来成为独特的对象，高效的让每个对象拥有更多的等待线程。和锁对比起来，如果说用 Lock 代替 synchronized，那么 Condition 就是用来代替 Object 本身的监控方法。

Condition 实例跟 Object 本身的监控相似，同样提供 wait()方法让调用的线程暂时挂起让出资源，知道其他线程通知该对象转态变化，才可能继续执行。Condition 实例来源于 Lock 实例，通过 Lock 调用 newCondition()即可。Condition 较 Object 原生监控方法，可以保证通知顺序。
