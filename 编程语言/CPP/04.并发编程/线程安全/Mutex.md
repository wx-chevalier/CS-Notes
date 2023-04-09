# Mutex

互斥锁是通过锁的机制来实现线程间的同步问题。互斥锁的基本流程为：

- 初始化一个互斥锁：pthread_mutex_init() 函数

- 加锁：pthread_mutex_lock() 函数或者 pthread_mutex_trylock() 函数

- 解锁：pthread_mutex_unlock() 函数

- 注销互斥锁：pthread_mutex_destory() 函数

其中，在加锁过程中，pthread_mutex_lock() 函数和 pthread_mutex_trylock()函数的过程略有不同：

- 当使用 pthread_mutex_lock() 函数进行加锁时，若此时已经被锁，则尝试加锁的线程会被阻塞，直到互斥锁被其他线程释放，当 pthread_mutex_lock()函数有返回值时，说明加锁成功；

- 而使用 pthread_mutex_trylock() 函数进行加锁时，若此时已经被锁，则会返回 EBUSY 的错误码。

同时，解锁的过程中，也需要满足两个条件：解锁前，互斥锁必须处于锁定状态；必须由加锁的线程进行解锁。当互斥锁使用完成后，必须进行清除。

```cpp
#include <stdio.h>
#include <pthread.h>
#include <malloc.h>

pthread_mutex_t mutex;

const char filename[] = "hello";

void *thread(void *id)
{

    int num = *(int *)id;
    // 加锁

    if (pthread_mutex_lock(&mutex) != 0)
    {
        fprintf(stdout, "lock error!\n");
    }
    // 写文件的操作
    FILE *fp = fopen(filename, "a+");
    int start = *((int *)id);
    int end = start + 1;
    setbuf(fp, NULL); // 设置缓冲区的大小
    fprintf(stdout, "%d\n", start);
    for (int i = (start * 10); i < (end * 10); i++)
    {
        fprintf(fp, "%d\t", i);
    }
    fprintf(fp, "\n");
    fclose(fp);

    // 解锁
    pthread_mutex_unlock(&mutex);

    return NULL;
}

int main()
{
    int num_thread = 5;
    pthread_t *pt = (pthread_t *)malloc(sizeof(pthread_t) * num_thread);
    int *id = (int *)malloc(sizeof(int) * num_thread);

    // 初始化互斥锁
    if (pthread_mutex_init(&mutex, NULL) != 0)
    {
        // 互斥锁初始化失败
        free(pt);
        free(id);
        return 1;
    }

    for (int i = 0; i < num_thread; i++)
    {
        id[i] = i;
        if (pthread_create(&pt[i], NULL, thread, &id[i]) != 0)
        {
            printf("thread create failed!\n");
            return 1;
        }
    }
    for (int i = 0; i < num_thread; i++)
    {
        pthread_join(pt[i], NULL);
    }
    pthread_mutex_destroy(&mutex);

    // 释放资源
    free(pt);
    free(id);
    return 0;
}
```

# 互斥锁类型

## 普通锁（PTHREAD_MUTEX_NORMAL）

互斥锁默认类型。当一个线程对一个普通锁加锁以后，其余请求该锁的线程将形成一个等待队列，并在该锁解锁后按照优先级获得它，这种锁类型保证了资源分配的公平性。一个线程如果对一个已经加锁的普通锁再次加锁，将引发死锁。

对一个已经被其他线程加锁的普通锁解锁，或者对一个已经解锁的普通锁再次解锁，将导致不可预期的后果。

## 检错锁（PTHREAD_MUTEX_ERRORCHECK）

一个线程如果对一个已经加锁的检错锁再次加锁，则加锁操作返回 EDEADLK；对一个已经被其他线程加锁的检错锁解锁或者对一个已经解锁的检错锁再次解锁，则解锁操作返回 EPERM；

## 嵌套锁（PTHREAD_MUTEX_RECURSIVE）

该锁允许一个线程在释放锁之前多次对它加锁而不发生死锁；其他线程要获得这个锁，则当前锁的拥有者必须执行多次解锁操作。

对一个已经被其他线程加锁的嵌套锁解锁，或者对一个已经解锁的嵌套锁再次解锁，则解锁操作返回 EPERM。

## 默认锁（PTHREAD_MUTEX_DEFAULT）

一个线程如果对一个已经加锁的默认锁再次加锁，或者虽一个已经被其他线程加锁的默认锁解锁，或者对一个解锁的默认锁解锁，将导致不可预期的后果。

这种锁实现的时候可能被映射成上述三种锁之一。
