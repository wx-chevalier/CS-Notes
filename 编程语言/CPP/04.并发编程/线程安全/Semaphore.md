# Semaphore

简单来说，就是信号量太容易出错了（too error prone），通过组合互斥锁（mutex）和条件变量（condition variable）可以达到相同的效果，且更加安全。实现如下：

```cpp
class Semaphore {
    public:
    explicit Semaphore(int count = 0) : count_(count) {
    }

    void Signal() {
        std::unique_lock<std::mutex> lock(mutex_);
        ++count_;
        cv_.notify_one();
    }

    void Wait() {
        std::unique_lock<std::mutex> lock(mutex_);
        cv_.wait(lock, [=] { return count_ > 0; });
        --count_;
    }

    private:
    std::mutex mutex_;
    std::condition_variable cv_;
    int count_;
};
```

下面创建三个工作线程（Worker），来测试这个信号量。

```cpp
int main() {
  const std::size_t SIZE = 3;

  std::vector<std::thread> v;
  v.reserve(SIZE);

  for (std::size_t i = 0; i < SIZE; ++i) {
    v.emplace_back(&Worker);
  }

  for (std::thread& t : v) {
    t.join();
  }

  return 0;
}
```

每个工作线程先等待信号量，然后输出线程 ID 和当前时间，输出操作以互斥锁同步以防止错位，睡眠一秒是为了模拟线程处理数据的耗时。

```cpp
std::mutex g_io_mutex;

void Worker() {
  g_semaphore.Wait();

  std::thread::id thread_id = std::this_thread::get_id();

  std::string now = FormatTimeNow("%H:%M:%S");
  {
    std::lock_guard<std::mutex> lock(g_io_mutex);
    std::cout << "Thread " << thread_id << ": wait succeeded" << " (" << now << ")" << std::endl;
  }

  // Sleep 1 second to simulate data processing.
  std::this_thread::sleep_for(std::chrono::seconds(1));

  g_semaphore.Signal();
}
```

信号量本身是一个全局对象，`count` 为 `1`，一次只允许一个线程访问：

```
Semaphore g_semaphore(1);
```

输出为：

```
Thread 1d38: wait succeeded (13:10:10)
Thread 20f4: wait succeeded (13:10:11)
Thread 2348: wait succeeded (13:10:12)
```

可见每个线程相隔一秒，即一次只允许一个线程访问。如果把 `count` 改为 `3`：

```
Semaphore g_semaphore(3);
```

那么三个线程输出的时间应该一样：

```
Thread 19f8: wait succeeded (13:10:57)
Thread 2030: wait succeeded (13:10:57)
Thread 199c: wait succeeded (13:10:57)
```

最后附上 `FormatTimeNow` 函数的实现：

```cpp
std::string FormatTimeNow(const char* format) {
  auto now = std::chrono::system_clock::now();
  std::time_t now_c = std::chrono::system_clock::to_time_t(now);
  std::tm* now_tm = std::localtime(&now_c);

  char buf[20];
  std::strftime(buf, sizeof(buf), format, now_tm);
  return std::string(buf);
}
```
