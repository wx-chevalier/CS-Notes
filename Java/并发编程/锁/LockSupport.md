# LockSupport

LockSupport 是用来创建锁和其他同步类的基本线程阻塞原语。 LockSupport 中的 park() 和 unpark() 的作用分别是阻塞线程和解除阻塞线程，而且 park()和 unpark()不会遇到“Thread.suspend 和 Thread.resume 所可能引发的死锁”问题。因为 park() 和 unpark()有许可的存在；调用 park() 的线程和另一个试图将其 unpark() 的线程之间的竞争将保持活性。
