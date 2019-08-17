# 线程安全

# 多线程问题引入

多线程的最大的特点是资源的共享，但是，当多个线程同时去操作（同时去改变）一个临界资源时，会破坏临界资源。如利用多线程同时写一个文件：

```cpp
#include <stdio.h>
#include <pthread.h>
#include <malloc.h>

const char filename[] = "hello";

void* thread(void *id){

        int num = *(int *)id;

        // 写文件的操作
        FILE *fp = fopen(filename, "a+");
        int start = *((int *)id);
        int end = start + 1;
        setbuf(fp, NULL);// 设置缓冲区的大小
        fprintf(stdout, "%d\n", start);
        for (int i = (start * 10); i < (end * 10); i ++){
                fprintf(fp, "%d\t", i);
        }
        fprintf(fp, "\n");
        fclose(fp);

        return NULL;
}

int main(){
        int num_thread = 5;
        pthread_t *pt = (pthread_t *)malloc(sizeof(pthread_t) * num_thread);
        int * id = (int *)malloc(sizeof(int) * num_thread);

        for (int i = 0; i < num_thread; i++){
                id[i] = i;
                if (pthread_create(&pt[i], NULL, thread, &id[i]) != 0){
                        printf("thread create failed!\n");
                        return 1;
                }
        }
        for (int i = 0; i < num_thread; i++){
                pthread_join(pt[i], NULL);
        }

        // 释放资源
        free(pt);
        free(id);
        return 0;
}
```

执行以上的代码，我们会发现，得到的结果是混乱的，出现上述的最主要的原因是，我们在编写多线程代码的过程中，每一个线程都尝试去写同一个文件，这样便出现了上述的问题，这便是共享资源的同步问题，在 Linux 编程中，线程同步的处理方法包括：信号量，互斥锁和条件变量。
