
# Linux NIO
select，poll，epoll都是IO多路复用的机制。IO多路复用就通过一种机制，可以监视多个描述符，一旦某个描述符就绪（一般是读就绪或者写就绪），能够通知程序进行相应的读写操作。但select，poll，epoll本质上都是同步IO，因为他们都需要在读写事件就绪后自己负责进行读写，也就是说这个读写过程是阻塞的，而异步IO则无需自己负责进行读写，异步IO的实现会负责把数据从内核拷贝到用户空间。
select 和 epoll效率差异的原因：select采用轮询方式处理连接，epoll是触发式处理连接。
Select:
1.Socket数量限制：该限制可操作的Socket数由FD_SETSIZE决定，内核默认32*32=1024.
2.操作限制：通过遍历FD_SETSIZE(1024)个Socket来完成调度，不管哪个Socket是活跃的，都遍历一遍。

Epoll
1.Socket数量无限制：该模式下的Socket对应的fd列表由一个数组来保存，大小不限制（默认4k）。
2.操作无限制：基于内核提供的反射模式，有活跃Socket时，内核访问该Socket的callback，不需要遍历轮询。
但当所有的Socket都活跃的时候，所有的callback都被唤醒，会导致资源的竞争。既然都是要处理所有的Socket，
那么遍历是最简单最有效的实现方式。 

select

    select能监控的描述符个数由内核中的FD_SETSIZE限制，仅为1024，这也是select最大的缺点，因为现在的服务器并发量远远不止1024。即使能重新编译内核改变FD_SETSIZE的值，但这并不能提高select的性能。
    每次调用select都会线性扫描所有描述符的状态，在select结束后，用户也要线性扫描fd_set数组才知道哪些描述符准备就绪，等于说每次调用复杂度都是O（n）的，在并发量大的情况下，每次扫描都是相当耗时的，很有可能有未处理的连接等待超时。
    每次调用select都要在用户空间和内核空间里进行内存复制fd描述符等信息。 

poll

    poll使用pollfd结构来存储fd，突破了select中描述符数目的限制。
    与select的后两点类似，poll仍然需要将pollfd数组拷贝到内核空间，之后依次扫描fd的状态，整体复杂度依然是O（n）的，在并发量大的情况下服务器性能会快速下降。 

epoll

    epoll维护的描述符数目不受到限制，而且性能不会随着描述符数目的增加而下降。
    服务器的特点是经常维护着大量连接，但其中某一时刻读写的操作符数量却不多。epoll先通过epoll_ctl注册一个描述符到内核中，并一直维护着而不像poll每次操作都将所有要监控的描述符传递给内核；在描述符读写就绪时，通过回掉函数将自己加入就绪队列中，之后epoll_wait返回该就绪队列。也就是说，epoll基本不做无用的操作，时间复杂度仅与活跃的客户端数有关，而不会随着描述符数目的增加而下降。
    epoll在传递内核与用户空间的消息时使用了内存共享，而不是内存拷贝，这也使得epoll的效率比poll和select更高。 

## select/poll
![](http://images.cnitblog.com/blog/305504/201308/17201205-8ac47f1f1fcd4773bd4edd947c0bb1f4.png)




### 函数分析

```
select(int nfds, fd_set *r, fd_set *w, fd_set *e, struct timeval *timeout)
```

- `maxfdp1`表示该进程中描述符的总数。

- `fd_set`则是配合`select`模型的重点数据结构，用来存放描述符的集合。

- `timeout`表示`select`返回需要等待的时间。

对于select()，我们需要传3个集合，r，w和e。其中，r表示我们对哪些fd的可读事件感兴趣，w表示我们对哪些fd的可写事件感兴趣。每个集合其实是一个bitmap，通过0/1表示我们感兴趣的fd。例如，我们对于fd为6的可读事件感兴趣，那么r集合的第6个bit需要被 设置为1。这个系统调用会阻塞，直到我们感兴趣的事件（至少一个）发生。调用返回时，内核同样使用这3个集合来存放fd实际发生的事件信息。也就是说，调 用前这3个集合表示我们感兴趣的事件，调用后这3个集合表示实际发生的事件。

select为最早期的UNIX系统调用，它存在4个问题：1）这3个bitmap有大小限制（FD_SETSIZE，通常为1024）；2）由于 这3个集合在返回时会被内核修改，因此我们每次调用时都需要重新设置；3）我们在调用完成后需要扫描这3个集合才能知道哪些fd的读/写事件发生了，一般情况下全量集合比较大而实际发生读/写事件的fd比较少，效率比较低下；4）内核在每次调用都需要扫描这3个fd集合，然后查看哪些fd的事件实际发生， 在读/写比较稀疏的情况下同样存在效率问题。

由于存在这些问题，于是人们对select进行了改进，从而有了poll。
```
poll(struct pollfd *fds, int nfds, int timeout)
struct pollfd {
    int fd;     
    short events;
    short revents;
    }
```
poll调用需要传递的是一个pollfd结构的数组，调用返回时结果信息也存放在这个数组里面。 pollfd的结构中存放着fd、我们对该fd感兴趣的事件(events)以及该fd实际发生的事件(revents)。poll传递的不是固定大小的 bitmap，因此select的问题1解决了；poll将感兴趣事件和实际发生事件分开了，因此select的问题2也解决了。但select的问题3和问题4仍然没有解决。
### 处理逻辑
总的来说，Select模型的内核的处理逻辑为：

（1）使用copy_from_user从用户空间拷贝fd_set到内核空间
（2）注册回调函数__pollwait
（3）遍历所有fd，调用其对应的poll方法（对于socket，这个poll方法是sock_poll，sock_poll根据情况会调用到tcp_poll,udp_poll或者datagram_poll）
（4）以tcp_poll为例，其核心实现就是__pollwait，也就是上面注册的回调函数。
（5）__pollwait的主要工作就是把current（当前进程）挂到设备的等待队列中，不同的设备有不同的等待队列，对于tcp_poll 来说，其等待队列是sk->sk_sleep（注意把进程挂到等待队列中并不代表进程已经睡眠了）。在设备收到一条消息（网络设备）或填写完文件数 据（磁盘设备）后，会唤醒设备等待队列上睡眠的进程，这时current便被唤醒了。
（6）poll方法返回时会返回一个描述读写操作是否就绪的mask掩码，根据这个mask掩码给fd_set赋值。
（7）如果遍历完所有的fd，还没有返回一个可读写的mask掩码，则会调用schedule_timeout是调用select的进程（也就是 current）进入睡眠。当设备驱动发生自身资源可读写后，会唤醒其等待队列上睡眠的进程。如果超过一定的超时时间（schedule_timeout 指定），还是没人唤醒，则调用select的进程会重新被唤醒获得CPU，进而重新遍历fd，判断有没有就绪的fd。
（8）把fd_set从内核空间拷贝到用户空间。
多客户端请求服务端，服务端与各客户端保持长连接并且能接收到各客户端数据大体思路如下：

（1）初始化`readset`，并且将服务端监听的描述符添加到`readset`中去。

（2）然后`select`阻塞等待`readset`集合中是否有描述符可读。

（3）如果是服务端描述符可读，那么表示有新客户端连接上。通过`accept`接收客户端的数据，并且将客户端描述符添加到一个数组`client`中，以便二次遍历的时候使用。

（4）执行第二次循环，此时通过`for`循环把`client`中的有效的描述符都添加到`readset`中去。

（5）`select`再次阻塞等待`readset`集合中是否有描述符可读。

（6）如果此时已经连接上的某个客户端描述符有数据可读，则进行数据读取。


## epoll/kqueue
### select不足与epoll中的改进
> [select、poll、epoll之间的区别总结[整理]](http://www.cnblogs.com/Anker/p/3265058.html)

select与poll问题的关键在于无状态。对于每一次系统调用，内核不会记录下任何信息，所以每次调用都需要重复传递相同信息。总结而言，select/poll模型存在的问题即是每次调用select，都需要把fd集合从用户态拷贝到内核态，这个开销在fd很多时会很大并且每次都需要在内核遍历传递进来的所有的fd，这个开销在fd很多时候也很大。讨论epoll对于select/poll改进的时候，epoll和select和poll的调用接口上的不同，select和poll都只提供了一个函数——select或者poll函数。而epoll提供了三个函数，epoll_create,epoll_ctl和epoll_wait，epoll_create是创建一个epoll句柄；epoll_ctl是注册要监听的事件类型；epoll_wait则是等待事件的产生。对于上面所说的select/poll的缺点，主要是在epoll_ctl中解决的，每次注册新的事件到epoll句柄中时（在epoll_ctl中指定EPOLL_CTL_ADD），会把所有的fd拷贝进内核，而不是在epoll_wait的时候重复拷贝。epoll保证了每个fd在整个过程中只会拷贝一次。epoll的解决方案不像select或poll一样每次都把current轮流加入fd对应的设备等待队列中，而只在epoll_ctl时把 current挂一遍（这一遍必不可少）并为每个fd指定一个回调函数，当设备就绪，唤醒等待队列上的等待者时，就会调用这个回调函数，而这个回调函数会 把就绪的fd加入一个就绪链表）。epoll_wait的工作实际上就是在这个就绪链表中查看有没有就绪的fd（利用 schedule_timeout()实现睡一会，判断一会的效果，和select实现中的第7步是类似的）。

（1）select，poll实现需要自己不断轮询所有fd集合，直到设备就绪，期间可能要睡眠和唤醒多次交替。而epoll其实也需要调用epoll_wait不断轮询就绪链表，期间也可能多次睡眠和唤醒交替，但是它是设备就绪时，调用回调函数，把就绪fd放入就绪链表中，并唤醒在epoll_wait中进入睡眠的进程。虽然都要睡眠和交替，但是select和poll在“醒着”的时候要遍历整个fd集合，而epoll在“醒着”的时候只要判断一下就绪链表是否为空就行了，这节省了大量的CPU时间。这就是回调机制带来的性能提升。
（2）select，poll每次调用都要把fd集合从用户态往内核态拷贝一次，并且要把current往设备等待队列中挂一次，而epoll只要一次拷贝，而且把current往等待队列上挂也只挂一次（在epoll_wait的开始，注意这里的等待队列并不是设备等待队列，只是一个epoll内部定义的等待队列）。这也能节省不少的开销。


### 函数分析
#### int epoll_create(int size);

创建一个epoll的句柄，size用来告诉内核这个监听的数目一共有多大。这个 参数不同于select()中的第一个参数，给出最大监听的fd+1的值。需要注意的是，当创建好epoll句柄后，它就是会占用一个fd值，在 linux下如果查看/proc/进程id/fd/，是能够看到这个fd的，所以在使用完epoll后，必须调用close()关闭，否则可能导致fd被 耗尽。


#### int epoll_ctl(int epfd, int op, int fd, struct epoll_event *event);

epoll的事件注册函数，它不同与select()是在监听事件时告诉内核要监听什么类型的事件，而是在这里先注册要监听的事件类型。第一个参数是epoll_create()的返回值，第二个参数表示动作，用三个宏来表示：
- EPOLL_CTL_ADD：注册新的fd到epfd中；
- EPOLL_CTL_MOD：修改已经注册的fd的监听事件；
- EPOLL_CTL_DEL：从epfd中删除一个fd；

第三个参数是需要监听的fd，第四个参数是告诉内核需要监听什么事，struct epoll_event结构如下：
```
typedef union epoll_data {
    void *ptr;
    int fd;
    __uint32_t u32;
    __uint64_t u64;
} epoll_data_t;

struct epoll_event {
    __uint32_t events; /* Epoll events */
    epoll_data_t data; /* User data variable */
};
```
events可以是以下几个宏的集合：
- EPOLLIN ：表示对应的文件描述符可以读（包括对端SOCKET正常关闭）；
- EPOLLOUT：表示对应的文件描述符可以写；
- EPOLLPRI：表示对应的文件描述符有紧急的数据可读（这里应该表示有带外数据到来）；
- EPOLLERR：表示对应的文件描述符发生错误；
- EPOLLHUP：表示对应的文件描述符被挂断；
- EPOLLET： 将EPOLL设为边缘触发(Edge Triggered)模式，这是相对于水平触发(Level Triggered)来说的。
- EPOLLONESHOT：只监听一次事件，当监听完这次事件之后，如果还需要继续监听这个socket的话，需要再次把这个socket加入到EPOLL队列里

#### int epoll_wait(int epfd, struct epoll_event * events, int maxevents, int timeout);

等 待事件的产生，类似于select()调用。参数events用来从内核得到事件的集合，maxevents告之内核这个events有多大，这个 maxevents的值不能大于创建epoll_create()时的size，参数timeout是超时时间（毫秒，0会立即返回，-1将不确定，也有 说法说是永久阻塞）。该函数返回需要处理的事件数目，如返回0表示已超时。
### 处理逻辑
使用epoll 来实现服务端同时接受多客户端长连接数据时，的大体步骤如下：
（1）使用epoll_create创建一个 epoll 的句柄，下例中我们命名为epollfd。
（2）使用epoll_ctl把服务端监听的描述符添加到epollfd指定的 epoll 内核事件表中，监听服务器端监听的描述符是否可读。
（3）使用epoll_wait阻塞等待注册的服务端监听的描述符可读事件的发生。
（4）当有新的客户端连接上服务端时，服务端监听的描述符可读，则epoll_wait返回，然后通过accept获取客户端描述符。
（5）使用epoll_ctl把客户端描述符添加到epollfd指定的 epoll 内核事件表中，监听服务器端监听的描述符是否可读。
（6）当客户端描述符有数据可读时，则触发epoll_wait返回，然后执行读取。

几乎所有的epoll模型编码都是基于以下模板：
```
    for( ; ; )
    {
        nfds = epoll_wait(epfd,events,20,500);
        for(i=0;i<nfds;++i)
        {
            if(events[i].data.fd==listenfd) //有新的连接
            {
                connfd = accept(listenfd,(sockaddr *)&clientaddr, &clilen); //accept这个连接
                ev.data.fd=connfd;
                ev.events=EPOLLIN|EPOLLET;
                epoll_ctl(epfd,EPOLL_CTL_ADD,connfd,&ev); //将新的fd添加到epoll的监听队列中
            }
            else if( events[i].events&EPOLLIN ) //接收到数据，读socket
            {
                n = read(sockfd, line, MAXLINE)) < 0    //读
                ev.data.ptr = md;     //md为自定义类型，添加数据
                ev.events=EPOLLOUT|EPOLLET;
                epoll_ctl(epfd,EPOLL_CTL_MOD,sockfd,&ev);//修改标识符，等待下一个循环时发送数据，异步处理的精髓
            }
            else if(events[i].events&EPOLLOUT) //有数据待发送，写socket
            {
                struct myepoll_data* md = (myepoll_data*)events[i].data.ptr;    //取数据
                sockfd = md->fd;
                send( sockfd, md->ptr, strlen((char*)md->ptr), 0 );        //发送数据
                ev.data.fd=sockfd;
                ev.events=EPOLLIN|EPOLLET;
                epoll_ctl(epfd,EPOLL_CTL_MOD,sockfd,&ev); //修改标识符，等待下一个循环时接收数据
            }
            else
            {
                //其他的处理
            }
        }
    }
```

## Demo
> 本部分代码实现参考[可能是最接地气的 IO 多路复用小结](https://mengkang.net/726.html?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io)

### 阻塞式网络编程接口
```
    #include <stdio.h>
    #include <unistd.h>
    #include <sys/types.h>
    #include <sys/socket.h>
    #include <arpa/inet.h>
    #include <netinet/in.h>
    #include <string.h>
       
    #define SERV_PORT 8031
    #define BUFSIZE 1024
      
    int main(void)
    {
        int lfd, cfd;
        struct sockaddr_in serv_addr,clin_addr;
        socklen_t clin_len;
        char recvbuf[BUFSIZE];
        int len;
       
        lfd = socket(AF_INET,SOCK_STREAM,0);
           
        serv_addr.sin_family = AF_INET;
        serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
        serv_addr.sin_port = htons(SERV_PORT);
          
        bind(lfd, (struct sockaddr *)&serv_addr, sizeof(serv_addr));
           
        listen(lfd, 128);
      
        while(1){
            clin_len = sizeof(clin_addr);
            cfd = accept(lfd, (struct sockaddr *)&clin_addr, &clin_len);
            while(len = read(cfd,recvbuf,BUFSIZE)){
                write(STDOUT_FILENO,recvbuf,len);//把客户端输入的内容输出在终端
                // 只有当客户端输入 stop 就停止当前客户端的连接
                if (strncasecmp(recvbuf,"stop",4) == 0){
                    close(cfd);
                    break;
                }
            }
        }      
        close(lfd); 
        return 0;
    }
```
编译运行之后，开启两个终端使用命令`nc 10.211.55.4 8031`（假如服务器的 ip 为 10.211.55.4）。如果首先连上的客户端一直不输入`stop`加回车，那么第二个客户端输入任何内容都不会被客户端接收。如下图所示

![](https://mengkang.net/upload/image/2016/0405/1459863994163365.png)

输入`abc`的是先连接上的，在其输入`stop`之前，后面连接上的客户端输入`123`并不会被服务端收到。也就是说一直阻塞在第一个客户端那里。当第一个客户端输入`stop`之后，服务端才收到第二个客户端的发送过来的数据。

![](https://mengkang.net/upload/image/2016/0405/1459864077691859.png)
### select
```
    #include <stdio.h>
    #include <stdlib.h>
    #include <unistd.h>
    #include <errno.h>
    #include <sys/types.h>
    #include <sys/socket.h>
    #include <arpa/inet.h>
    #include <netinet/in.h>
    #include <fcntl.h>
    #include <sys/select.h>
    #include <sys/time.h>
    #include <string.h>
     
    #define SERV_PORT     8031
    #define BUFSIZE       1024
    #define FD_SET_SIZE   128
     
    int main(void) {
        int lfd, cfd, maxfd, scokfd, retval;
        struct sockaddr_in serv_addr, clin_addr;
     
        socklen_t clin_len; // 地址信息结构体大小
     
        char recvbuf[BUFSIZE];
        int len;
     
        fd_set read_set, read_set_init;
     
        int client[FD_SET_SIZE];
        int i;
        int maxi = -1;
     
     
        if ((lfd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
            perror("套接字描述符创建失败");
            exit(1);
        }
     
        int opt = 1;
        setsockopt(lfd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));
     
        memset(&serv_addr, 0, sizeof(serv_addr));
        serv_addr.sin_family = AF_INET;
        serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
        serv_addr.sin_port = htons(SERV_PORT);
     
        if (bind(lfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) == -1) {
            perror("绑定失败");
            exit(1);
        }
     
        if (listen(lfd, FD_SET_SIZE) == -1) {
            perror("监听失败");
            exit(1);
        }
     
        maxfd = lfd;
     
        for (i = 0; i < FD_SET_SIZE; ++i) {
            client[i] = -1;
        }
     
        FD_ZERO(&read_set_init);
        FD_SET(lfd, &read_set_init);
     
        while (1) {
            // 每次循环开始时，都初始化 read_set
            read_set = read_set_init;
             
            // 因为上一步 read_set 已经重置，所以需要已连接上的客户端 fd （由上次循环后产生）重新添加进 read_set
            for (i = 0; i < FD_SET_SIZE; ++i) {
                if (client[i] > 0) {
                    FD_SET(client[i], &read_set);
                }
            }
     
            printf("select 等待\n");
            // 这里会阻塞，直到 read_set 中某一个 fd 有数据可读才返回，注意 read_set 中除了客户端 fd 还有服务端监听的 fd
            retval = select(maxfd + 1, &read_set, NULL, NULL, NULL);
            if (retval == -1) {
                perror("select 错误\n");
            } else if (retval == 0) {
                printf("超时\n");
                continue;
            }
            printf("select 返回\n");
     
            //------------------------------------------------------------------------------------------------
            // 用 FD_ISSET 来判断 lfd (服务端监听的fd)是否可读。只有当新的客户端连接时，lfd 才可读 
            if (FD_ISSET(lfd, &read_set)) {
                clin_len = sizeof(clin_addr);
                if ((cfd = accept(lfd, (struct sockaddr *) &clin_addr, &clin_len)) == -1) {
                    perror("接收错误\n");
                    continue;
                }
     
                for (i = 0; i < FD_SET_SIZE; ++i) {
                    if (client[i] < 0) {
                        // 把客户端 fd 放入 client 数组
                        client[i] = cfd;
                        printf("接收client[%d]一个请求来自于: %s:%d\n", i, inet_ntoa(clin_addr.sin_addr), ntohs(clin_addr.sin_port));
                        break;
                    }
                }
                 
                // 最大的描述符值也要重新计算
                maxfd = (cfd > maxfd) ? cfd : maxfd;
                // maxi 用于下面遍历所有有效客户端 fd 使用，以免遍历整个 client 数组
                maxi = (i >= maxi) ? ++i : maxi;
            }
            //------------------------------------------------------------------------------------------------
     
            for (i = 0; i < maxi; ++i) {
                if (client[i] < 0) {
                    continue;
                }
                 
                // 如果客户端 fd 中有数据可读，则进行读取
                if (FD_ISSET(client[i], &read_set)) {
                    // 注意：这里没有使用 while 循环读取，如果使用 while 循环读取，则有阻塞在一个客户端了。
                    // 可能你会想到如果一次读取不完怎么办？
                    // 读取不完时，在循环到 select 时 由于未读完的 fd 还有数据可读，那么立即返回，然后到这里继续读取，原来的 while 循环读取直接提到最外层的 while(1) + select 来判断是否有数据继续可读
                    len = read(client[i], recvbuf, BUFSIZE);
                    if (len > 0) {
                        write(STDOUT_FILENO, recvbuf, len);
                    }else if (len == 0){
                        // 如果在客户端 ctrl+z
                        close(client[i]);
                        printf("clinet[%d] 连接关闭\n", i);
                        FD_CLR(client[i], &read_set);
                        client[i] = -1;
                        break;
                    }
                }
            }
     
        }
     
        close(lfd);
     
        return 0;
    }
```
![](https://mengkang.net/upload/image/2016/0407/1459997845662935.png)

### epoll
```
    #include <stdio.h>
    #include <stdlib.h>
    #include <unistd.h>
    #include <errno.h>
    #include <sys/types.h>
    #include <sys/socket.h>
    #include <arpa/inet.h>
    #include <netinet/in.h>
    #include <fcntl.h>
    #include <sys/epoll.h>
    #include <sys/time.h>
    #include <string.h>
     
    #define SERV_PORT           8031
    #define MAX_EVENT_NUMBER    1024
    #define BUFFER_SIZE         10
     
     
    /* 将文件描述符 fd 上的 EPOLLIN 注册到 epollfd 指示的 epoll 内核事件表中 */
    void addfd(int epollfd, int fd) {
        struct epoll_event event;
        event.data.fd = fd;
        event.events = EPOLLIN | EPOLLET;
        epoll_ctl(epollfd, EPOLL_CTL_ADD, fd, &event);
        int old_option = fcntl(fd, F_GETFL);
        int new_option = old_option | O_NONBLOCK;
        fcntl(fd, F_SETFL, new_option);
    }
     
    void et(struct epoll_event *events, int number, int epollfd, int listenfd) {
        char buf[BUFFER_SIZE];
        for (int i = 0; i < number; ++i) {
            int sockfd = events[i].data.fd;
            if (sockfd == listenfd) {
                struct sockaddr_in client_address;
                socklen_t length = sizeof(client_address);
                int connfd = accept(listenfd, (struct sockaddr *) &client_address, &length);
                printf("接收一个请求来自于: %s:%d\n", inet_ntoa(client_address.sin_addr), ntohs(client_address.sin_port));
     
                addfd(epollfd, connfd);
            } else if (events[i].events & EPOLLIN) {
                /* 这段代码不会被重复触发，所以我们循环读取数据，以确保把 socket 缓存中的所有数据读取*/
                while (1) {
                    memset(buf, '\0', BUFFER_SIZE);
                    int ret = recv(sockfd, buf, BUFFER_SIZE - 1, 0);
                    if (ret < 0) {
                        /* 对非阻塞 IO ，下面的条件成立表示数据已经全部读取完毕。此后 epoll 就能再次触发 sockfd 上的 EPOLLIN 事件，以驱动下一次读操作 */
                        if ((errno == EAGAIN) || (errno == EWOULDBLOCK)) {
                            printf("read later\n");
                            break;
                        }
                        close(sockfd);
                        break;
                    } else if (ret == 0) {
                        printf("断开一个连接\n");
                        close(sockfd);
                    } else {
                        printf("get %d bytes of content: %s\n", ret, buf);
                    }
                }
            }
        }
    }
     
     
    int main(void) {
        int lfd, epollfd,ret;
        struct sockaddr_in serv_addr;
     
        if ((lfd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
            perror("套接字描述符创建失败");
            exit(1);
        }
     
        int opt = 1;
        setsockopt(lfd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));
     
        memset(&serv_addr, 0, sizeof(serv_addr));
        serv_addr.sin_family = AF_INET;
        serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
        serv_addr.sin_port = htons(SERV_PORT);
     
        if (bind(lfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) == -1) {
            perror("绑定失败");
            exit(1);
        }
     
        if (listen(lfd, 5) == -1) {
            perror("监听失败");
            exit(1);
        }
     
        struct epoll_event events[MAX_EVENT_NUMBER];
        if ((epollfd = epoll_create(5)) == -1) {
            perror("创建失败");
            exit(1);
        }
     
        // 把服务器端 lfd 添加到 epollfd 指定的 epoll 内核事件表中，添加一个 lfd 可读的事件
        addfd(epollfd, lfd);
        while (1) {
            // 阻塞等待新客户端的连接或者客户端的数据写入，返回需要处理的事件数目
            if ((ret = epoll_wait(epollfd, events, MAX_EVENT_NUMBER, -1)) < 0) {
                perror("epoll_wait失败");
                exit(1);
            }
     
            et(events, ret, epollfd, lfd);
        }
     
        close(lfd);
        return 0;
    }
```