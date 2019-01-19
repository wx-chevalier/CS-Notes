 

# Redis

## Jedis

``` java
package com.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class TestRedis {
    private Jedis jedis; 

    @Before
    public void setup() {
        //连接redis服务器，192.168.0.100:6379
        jedis = new Jedis("192.168.0.100", 6379);
        //权限认证
        jedis.auth("admin");  
    }

    /**
     * redis存储字符串
     */
    @Test
    public void testString() {
        //-----添加数据----------  
        jedis.set("name","xinxin");//向key-->name中放入了value-->xinxin  
        System.out.println(jedis.get("name"));//执行结果：xinxin  

        jedis.append("name", " is my lover"); //拼接
        System.out.println(jedis.get("name")); 

        jedis.del("name");  //删除某个键
        System.out.println(jedis.get("name"));
        //设置多个键值对
        jedis.mset("name","liuling","age","23","qq","476777XXX");
        jedis.incr("age"); //进行加1操作
        System.out.println(jedis.get("name") + "-" + jedis.get("age") + "-" + jedis.get("qq"));

    }

    /**
     * redis操作Map
     */
    @Test
    public void testMap() {
        //-----添加数据----------  
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "xinxin");
        map.put("age", "22");
        map.put("qq", "123456");
        jedis.hmset("user",map);
        //取出user中的name，执行结果:[minxr]-->注意结果是一个泛型的List  
        //第一个参数是存入redis中map对象的key，后面跟的是放入map中的对象的key，后面的key可以跟多个，是可变参数  
        List<String> rsmap = jedis.hmget("user", "name", "age", "qq");
        System.out.println(rsmap);  

        //删除map中的某个键值  
        jedis.hdel("user","age");
        System.out.println(jedis.hmget("user", "age")); //因为删除了，所以返回的是null  
        System.out.println(jedis.hlen("user")); //返回key为user的键中存放的值的个数2 
        System.out.println(jedis.exists("user"));//是否存在key为user的记录 返回true  
        System.out.println(jedis.hkeys("user"));//返回map对象中的所有key  
        System.out.println(jedis.hvals("user"));//返回map对象中的所有value 

        Iterator<String> iter=jedis.hkeys("user").iterator();  
        while (iter.hasNext()){  
            String key = iter.next();  
            System.out.println(key+":"+jedis.hmget("user",key));  
        }  
    }

    /** 
     * jedis操作List 
     */  
    @Test  
    public void testList(){  
        //开始前，先移除所有的内容  
        jedis.del("java framework");  
        System.out.println(jedis.lrange("java framework",0,-1));  
        //先向key java framework中存放三条数据  
        jedis.lpush("java framework","spring");  
        jedis.lpush("java framework","struts");  
        jedis.lpush("java framework","hibernate");  
        //再取出所有数据jedis.lrange是按范围取出，  
        // 第一个是key，第二个是起始位置，第三个是结束位置，jedis.llen获取长度 -1表示取得所有  
        System.out.println(jedis.lrange("java framework",0,-1));  

        jedis.del("java framework");
        jedis.rpush("java framework","spring");  
        jedis.rpush("java framework","struts");  
        jedis.rpush("java framework","hibernate"); 
        System.out.println(jedis.lrange("java framework",0,-1));
    }  

    /** 
     * jedis操作Set 
     */  
    @Test  
    public void testSet(){  
        //添加  
        jedis.sadd("user","liuling");  
        jedis.sadd("user","xinxin");  
        jedis.sadd("user","ling");  
        jedis.sadd("user","zhangxinxin");
        jedis.sadd("user","who");  
        //移除noname  
        jedis.srem("user","who");  
        System.out.println(jedis.smembers("user"));//获取所有加入的value  
        System.out.println(jedis.sismember("user", "who"));//判断 who 是否是user集合的元素  
        System.out.println(jedis.srandmember("user"));  
        System.out.println(jedis.scard("user"));//返回集合的元素个数  
    }  

    @Test  
    public void test() throws InterruptedException {  
        //jedis 排序  
        //注意，此处的rpush和lpush是List的操作。是一个双向链表(但从表现来看的)  
        jedis.del("a");//先清除数据，再加入数据进行测试  
        jedis.rpush("a", "1");  
        jedis.lpush("a","6");  
        jedis.lpush("a","3");  
        jedis.lpush("a","9");  
        System.out.println(jedis.lrange("a",0,-1));// [9, 3, 6, 1]  
        System.out.println(jedis.sort("a")); //[1, 3, 6, 9]  //输入排序后结果  
        System.out.println(jedis.lrange("a",0,-1));  
    }  

    @Test
    public void testRedisPool() {
        RedisUtil.getJedis().set("newname", "中文测试");
        System.out.println(RedisUtil.getJedis().get("newname"));
    }
}
```

## [Redisson](https://github.com/mrniko/redisson)

基于Redis进行的一种顶层的封装，提供了一系列的分布式与可扩展的Java的数据结构。Redisson底层使用了Netty进行封装，同时将Java本身大量的类似于CountDownLatch这样的同步辅助类利用Redis映射到了分布式环境下。在redisson中，各个部分均采用了最新的一些技术栈，包括java 5线程语义，Promise编程语义，在技术的学习上有很高的学习意义。相比jedis，其支持的特性并不是很高，但对于日常的使用还是没有问题的。其对集合的封装，编解码的处理，都达到了一个开箱即用的目的。相比jedis，仅完成了一个基本的redis网络实现，可以理解为redisson是一个完整的框架，而jedis即完成了语言层的适配。其次，redisson在设计模式，以及编码上，都有完整的测试示例，代码可读性也非常好，很值得进行源码级学习。如果在项目中已经使用了netty，那么如果需要集成redis,那么使用redisson是最好的选择了，都不需要另外增加依赖信息。

- Maven

``` 
<dependency>
   <groupId>org.redisson</groupId>
   <artifactId>redisson</artifactId>
   <version>2.2.3</version>
</dependency>
```

- Gradle

``` 
compile 'org.redisson:redisson:2.2.3'
```



### Config

>  [Redisson调用流程](http://www.iflym.com/index.php/code/201503290001.html#more-778)

- Single Server

``` 
// connects to default Redis server 127.0.0.1:6379
Redisson redisson = Redisson.create();
// connects to single Redis server via Config
Config config = new Config();
config.useSingleServer().setAddress("127.0.0.1:6379");

//or with database select num = 1
config.useSingleServer().setAddress("127.0.0.1:6379").setDatabase(1);

Redisson redisson = Redisson.create(config);
```

- Master/Slave Servers Connection

``` 
Config config = new Config();
config.useMasterSlaveServers()
    .setMasterAddress("127.0.0.1:6379")
    .setLoadBalancer(new RandomLoadBalancer()) // RoundRobinLoadBalancer used by default
    .addSlaveAddress("127.0.0.1:6389", "127.0.0.1:6332", "127.0.0.1:6419")
    .addSlaveAddress("127.0.0.1:6399");

Redisson redisson = Redisson.create(config);
```

- Sentinel Servers Connection

``` 
Config config = new Config();
config.useSentinelServers()
    .setMasterName("mymaster")
    .addSentinelAddress("127.0.0.1:26389", "127.0.0.1:26379")
    .addSentinelAddress("127.0.0.1:26319");

Redisson redisson = Redisson.create(config);
```

- Cluster Nodes Connections

``` 
Config config = new Config();
config.useClusterServers()
    .setScanInterval(2000) // sets cluster state scan interval
    .addNodeAddress("127.0.0.1:7000", "127.0.0.1:7001")
    .addNodeAddress("127.0.0.1:7002");

Redisson redisson = Redisson.create(config);
```

### Basic Data Type(基本数据类型)

### Extension Data Type(扩展数据类型)

#### Object storage

Implements [RBucket](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RBucket.java) and [RBucketAsync](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RBucketAsync.java) interfaces

``` 
Redisson redisson = Redisson.create();

RBucket<AnyObject> bucket = redisson.getBucket("anyObject");
bucket.set(new AnyObject());
bucket.setAsync(new AnyObject());
AnyObject obj = bucket.get();

redisson.shutdown();
```

#### 

#### Map

Implements [RMap](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RMap.java), [RMapAsync](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RMapAsync.java) and [ConcurrentMap](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ConcurrentMap.html) interfaces

``` 
Redisson redisson = Redisson.create();

RMap<String, SomeObject> map = redisson.getMap("anyMap");
SomeObject prevObject = map.put("123", new SomeObject());
SomeObject currentObject = map.putIfAbsent("323", new SomeObject());
SomeObject obj = map.remove("123");

map.fastPut("321", new SomeObject());
map.fastRemove("321");

Future<SomeObject> putAsyncFuture = map.putAsync("321");
Future<Void> fastPutAsyncFuture = map.fastPutAsync("321");

map.fastPutAsync("321", new SomeObject());
map.fastRemoveAsync("321");

redisson.shutdown();
```

#### 

#### SortedSet

Set collection uses comparator to sort elements and keep uniqueness.

``` 
RSortedSet<Integer> set = redisson.getSortedSet("anySet");
set.trySetComparator(new MyComparator()); // set object comparator
set.add(3);
set.add(1);
set.add(2);

set.removeAsync(0);
set.addAsync(5);
```

#### 

#### ScoredSortedSet

Set collection sorts elements by score defined during element insertion. Keeps elements uniqueness via element state comparison.

``` 
RScoredSortedSet<SomeObject> set = redisson.getScoredSortedSet("simple");

set.add(0.13, new SomeObject(a, b));
set.addAsync(0.251, new SomeObject(c, d));
set.add(0.302, new SomeObject(g, d));

set.pollFirst();
set.pollLast();

int index = set.rank(new SomeObject(g, d)); // get element index
Double score = set.getScore(new SomeObject(g, d)); // get element score

```

#### 

#### Set

Implements [RSet](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RSet.java), [RSetAsync](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RSetAsync.java) and [Set](http://docs.oracle.com/javase/7/docs/api/java/util/Set.html) interfaces

``` 
Redisson redisson = Redisson.create();

RSet<SomeObject> set = redisson.getSet("anySet");
set.add(new SomeObject());
set.remove(new SomeObject());

set.addAsync(new SomeObject());

redisson.shutdown();
```

#### 

####  List

Implements [RList](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RList.java), [RListAsync](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RListAsync.java) and [List](http://docs.oracle.com/javase/7/docs/api/java/util/List.html) interfaces

``` 
Redisson redisson = Redisson.create();

RList<SomeObject> list = redisson.getList("anyList");
list.add(new SomeObject());
list.get(0);
list.remove(new SomeObject());

redisson.shutdown();
```

#### 

#### Queue

Implements [RQueue](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RQueue.java), [RQueueAsync](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RQueueAsync.java) and [Queue](http://docs.oracle.com/javase/7/docs/api/java/util/Queue.html) interfaces

``` 
Redisson redisson = Redisson.create();

RQueue<SomeObject> queue = redisson.getQueue("anyQueue");
queue.add(new SomeObject());
SomeObject obj = queue.peek();
SomeObject someObj = queue.poll();

redisson.shutdown();
```

#### 

#### Deque

Implements [RDeque](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RDeque.java), [RDequeAsync](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RDequeAsync.java) and [Deque](http://docs.oracle.com/javase/7/docs/api/java/util/Deque.html) interfaces

``` 
Redisson redisson = Redisson.create();

RDeque<SomeObject> queue = redisson.getDeque("anyDeque");
queue.addFirst(new SomeObject());
queue.addLast(new SomeObject());
SomeObject obj = queue.removeFirst();
SomeObject someObj = queue.removeLast();

redisson.shutdown();
```

#### 

#### Blocking Queue

``` 
Redisson redisson = Redisson.create();

RBlockingQueue<SomeObject> queue = redisson.getBlockingQueue("anyQueue");
queue.offer(new SomeObject(), 12, TimeUnit.SECONDS);
SomeObject obj = queue.peek();
SomeObject someObj = queue.poll();

redisson.shutdown();
```

#### 

#### Blocking Deque

``` 
Redisson redisson = Redisson.create();

RBlockingDeque<Integer> deque = redisson.getBlockingDeque("anyDeque");
deque.putFirst(1);
deque.putLast(2);
Integer firstValue = queue.takeFirst();
Integer lastValue = queue.takeLast();

redisson.shutdown();
```

#### 

#### Lock

> [基于redis的分布式锁 RedissonLock实现分析](http://www.iflym.com/index.php/code/201507050001.html)

Implements [RLock](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RLock.java) and [Lock](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/locks/Lock.html) interfaces

``` 
Redisson redisson = Redisson.create();

RLock lock = redisson.getLock("anyLock");
lock.lock();
...
lock.unlock();

// Lock time-to-live support
// releases lock automatically after 10 seconds
// if unlock method not invoked
lock.lock(10, TimeUnit.SECONDS);
...
lock.unlock();

redisson.shutdown();
```

#### 

#### AtomicLong

Implements [RAtomicLong](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RAtomicLong.java) and [RAtomicLongAsync](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RAtomicLongAsync.java) interfaces

``` 
Redisson redisson = Redisson.create();

RAtomicLong atomicLong = redisson.getAtomicLong("anyAtomicLong");
atomicLong.set(3);
atomicLong.incrementAndGet();
atomicLong.get();

redisson.shutdown();
```

#### 

#### CountDownLatch

Implements [RCountDownLatch](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RCountDownLatch.java) interface

``` 
Redisson redisson = Redisson.create();

RCountDownLatch latch = redisson.getCountDownLatch("anyCountDownLatch");
latch.trySetCount(1);
latch.await();

// in other thread or other JVM
RCountDownLatch latch = redisson.getCountDownLatch("anyCountDownLatch");
latch.countDown();

redisson.shutdown();
```

#### 

#### Publish subscribe

Implements [RTopic](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RTopic.java) and [RTopicAsync](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RTopicAsync.java) interfaces

``` 
Redisson redisson = Redisson.create();

RTopic<SomeObject> topic = redisson.getTopic("anyTopic");
topic.addListener(new MessageListener<SomeObject>() {

    public void onMessage(String channel, SomeObject message) {
        ...
    }
});

// in other thread or other JVM
RTopic<SomeObject> topic = redisson.getTopic("anyTopic");
long clientsReceivedMessage = topic.publish(new SomeObject());

redisson.shutdown();
```

#### 

#### Publish subscribe by pattern

Implements [RPatternTopic](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RPatternTopic.java) and [RPatternTopicAsync](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RPatternTopicAsync.java) interfaces

``` 
// subscribe to all topics by `topic1.*` pattern
RPatternTopicAsync<Message> topic1 = redisson.getTopicPattern("topic1.*");
int listenerId = topic1.addListener(new PatternMessageListener<Message>() {
    @Override
    public void onMessage(String pattern, String channel, Message msg) {
         Assert.fail();
    }
});
```

#### 

#### Multiple commands batch (commands pipelining)

Send multiple commands to the server without waiting for the replies at all, and finally read the replies in a single step. Implements [RBatch](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RBatch.java) interface

``` 
RBatch batch = redisson.createBatch();
batch.getMap("test").fastPutAsync("1", "2");
batch.getMap("test").fastPutAsync("2", "3");
batch.getMap("test").putAsync("2", "5");
batch.getAtomicLongAsync("counter").incrementAndGetAsync();
batch.getAtomicLongAsync("counter").incrementAndGetAsync();

List<?> res = batch.execute();
```

#### 

#### Scripting

Lua scripts could be executed. More [details](http://redis.io/commands/eval).

Implements [RScript](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RScript.java) and [RScriptAsync](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/core/RScriptAsync.java) interfaces

``` 
redisson.getBucket("foo").set("bar");
String r = redisson.getScript().eval(Mode.READ_ONLY, 
   "return redis.call('get', 'foo')", RScript.ReturnType.VALUE);

// do the same using cache
RScript s = redisson.getScript();
// load script into cache to all redis master instances
String res = s.scriptLoad("return redis.call('get', 'foo')");
// res == 282297a0228f48cd3fc6a55de6316f31422f5d17

// call script by sha digest
Future<Object> r1 = redisson.getScript().evalShaAsync(Mode.READ_ONLY, 
   "282297a0228f48cd3fc6a55de6316f31422f5d17", 
   RScript.ReturnType.VALUE, Collections.emptyList());
```

#### 

#### Low level Redis client

Redisson uses high-perfomance async and lock-free Redis client. You may use it if you want to do something not yet implemented by Redisson. It support both async and sync modes. Here is [all commands](https://github.com/mrniko/redisson/blob/master/src/main/java/org/redisson/client/protocol/RedisCommands.java) for client. But you may create any other command with `RedisCommand` object.

``` 
RedisClient client = new RedisClient("localhost", 6379);
RedisConnection conn = client.connect();
//or 
Future<RedisConnection> connFuture = client.connectAsync();

conn.sync(StringCodec.INSTANCE, RedisCommands.SET, "test", 0);
conn.async(StringCodec.INSTANCE, RedisCommands.GET, "test");

conn.sync(RedisCommands.PING);

conn.close()
// or
conn.closeAsync()

client.shutdown();
// or
client.shutdownAsync();
```

#### 

#### Misc operations

``` 
long deletedObjects = redisson.delete("obj1", "obj2", "obj3");

long deletedObjectsByPattern = redisson.deleteByPattern("test?");

Queue<String> foundKeys = redisson.getKeys().findKeysByPattern("name*object");

redisson.flushdb();

redisson.flushall();
```
