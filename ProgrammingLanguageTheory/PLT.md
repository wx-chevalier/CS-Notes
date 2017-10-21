

# Introduction

## Reference
### Resources & Practices
[plt:A path to Programming Language Theory enlightenment](https://github.com/steshaw/plt) 
[怎样写一个解释器](http://www.jianshu.com/p/509505d3bd50) 


# Scope


已知IBM的PowerPC是big-endian字节序列而Intel的X86是little-endian字节序，如果在地址啊存储的整形值时0x04030201，那么地址为a+3的字节内存储的值在PowerPC和Intel X86结构下的值分别是:1 4
大端从大地址开始存储，小端相反，两者都是从数据低位开始存起；
 假设从上至下地址递增，则

  PowerPC（大）：                    Intel X86（小）：

  04                                            01                   
  低
 03                                           
  02                      |

  02                                            03                     
  |  
 01                                           
  04                     高
 a+3指向最大的地址，所以分别为1 4









# Endian
> 
- [Big Endian 和 Little Endian](http://blog.csdn.net/sunshine1314/article/details/2309655)























