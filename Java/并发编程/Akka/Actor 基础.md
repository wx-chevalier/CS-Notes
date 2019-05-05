# Actor 基础

The Actor Model provides a higher level of abstraction for writing concurrent and distributed systems. It alleviates the developer from having to deal with explicit locking and thread management, making it easier to write correct concurrent and parallel systems. Actors were defined in the 1973 paper by Carl Hewitt but have been popularized by the Erlang language, and used for example at Ericsson with great success to build highly concurrent and reliable telecom systems.

Before starting with recipes, let's take a look at the following the actor properties:

State: An actor has internal state, which is mutated sequentially as messages are processed one by one.
Behavior: An Actor reacts to messages which are sent to it by applying behavior on it.
Communication: An actor communicates with other actors by sending and receiving messages to/from them.
Mailbox: A mailbox is the queue of messages from which an actor picks up the message and processes it.

Actors are message-driven, that is, they are passive and do nothing unless and until you send messages to them. Once you send them a message, they pick a thread from the thread pool which is also known as a dispatcher, process the message, and release the thread back to the thread pool.

Actors are also asynchronous by nature; they never block your current thread of execution, and continue to work on another thread.

## ActorSystem

An actor in Akka always belongs to a parent. Typically, you create an actor by calling getContext().actorOf(). Rather than creating a “freestanding” actor, this injects the new actor as a child into an already existing tree: the creator actor becomes the parent of the newly created child actor. You might ask then, who is the parent of the first actor you create?

As illustrated below, all actors have a common parent, the user guardian. New actor instances can be created under this actor using system.actorOf().

![image](https://user-images.githubusercontent.com/5803001/47984204-d12e5100-e110-11e8-892d-44bef8d6ba04.png)

- / the so-called root guardian. This is the parent of all actors in the system, and the last one to stop when the system itself is terminated.

- /user the guardian. This is the parent actor for all user created actors. Don’t let the name user confuse you, it has nothing to do with end users, nor with user handling. Every actor you create using the Akka library will have the constant path /user/ prepended to it.

- /system the system guardian.

## Actor 声明与实例化

最简单的 Actor 声明即是继承自 AbstractActor，并且实现了 createReceive 方法，该方法会自定义消息的处理方式。

```java
class PrintMyActorRefActor extends AbstractActor {
  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .matchEquals("printit", p -> {
          // 创建新的子 Actor 实例
          ActorRef secondRef = getContext().actorOf(Props.empty(), "second-actor");
          System.out.println("Second: " + secondRef);
        })
        .build();
  }
}
```

```java
// 精确匹配某个值
.matchEquals
// 匹配某个类对象
.match(String.class, s -> {
log.info("Received String message: {}", s);
})
// 匹配任意对象
.matchAny(o -> log.info("received unknown message"))
```

然后在 Actor System 中注册该 Actor 的实例对象：

```java
ActorSystem system = ActorSystem.create("testSystem");

// 创建某个 Actor
ActorRef firstRef = system.actorOf(Props.create(PrintMyActorRefActor.class), "first-actor");
System.out.println("First: " + firstRef);

// 发送消息
firstRef.tell("printit", ActorRef.noSender());

// 终止该系统
system.terminate();

// First: Actor[akka://testSystem/user/first-actor#1053618476]
// Second: Actor[akka://testSystem/user/first-actor/second-actor#-1544706041]
```

Props is a configuration class to specify options for the creation of actors, think of it as an immutable and thus freely shareable recipe for creating an actor including associated deployment information.

```java
Props props1 = Props.create(MyActor.class);
// 该方法不建议使用
Props props2 = Props.create(ActorWithArgs.class,
  () -> new ActorWithArgs("arg"));
Props props3 = Props.create(ActorWithArgs.class, "arg");
```

## 消息传递

### 消息定义

在真实场景下，我们往往会使用 POJO 或者 Enum 做消息的结构化传递：

```java
// GreeterActor
public static enum Msg {
    GREET, DONE;
}

@Override
public Receive createReceive() {
    return receiveBuilder()
        .matchEquals(Msg.GREET, m -> {
        System.out.println("Hello World!");
        sender().tell(Msg.DONE, self());
    })
    .build();
}
```

### Future 异步调用

类似于 Java 中的 Future，可以将一个 actor 的返回结果重定向到另一个 actor 中进行处理，主 actor 或者进程无需等待 actor 的返回结果。

```java
ActorSystem system = ActorSystem.create("strategy", ConfigFactory.load("akka.config"));
ActorRef printActor = system.actorOf(Props.create(PrintActor.class), "PrintActor");
ActorRef workerActor = system.actorOf(Props.create(WorkerActor.class), "WorkerActor");

//等等future返回
Future<Object> future = Patterns.ask(workerActor, 5, 1000);
int result = (int) Await.result(future, Duration.create(3, TimeUnit.SECONDS));
System.out.println("result:" + result);

//不等待返回值，直接重定向到其他actor，有返回值来的时候将会重定向到printActor
Future<Object> future1 = Patterns.ask(workerActor, 8, 1000);
Patterns.pipe(future1, system.dispatcher()).to(printActor);


workerActor.tell(PoisonPill.getInstance(), ActorRef.noSender());]
```

## LifeCycle | 生命周期

![image](https://user-images.githubusercontent.com/5803001/47840701-1fcaab00-ddf2-11e8-93a9-a0cd34601dd0.png)

```java
class StartStopActor1 extends AbstractActor {
  @Override
  public void preStart() {
    ...
    getContext().actorOf(Props.create(StartStopActor2.class), "second");
  }

  @Override
  public void postStop() {
    ...
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .matchEquals("stop", s -> {
          getContext().stop(getSelf());
        })
        .build();
  }
}
```

## 各类型 Actor

### Typed Actor & AbstractActor

早期 Akka 库中常见的是 TypedActor 与 UntypedActor，TypedActor 的优势在于其内置了静态协议，而不需要用户自定义消息类型；并且 UntypedActor 已经被弃用，而应该使用 AbstractActor。

### AbstractLoggingActor

```java
public static class Terminator extends AbstractLoggingActor {

    private final ActorRef ref;

    public Terminator(ActorRef ref) {
        this.ref = ref;
        getContext().watch(ref);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
        .match(Terminated.class, t -> {
            log().info("{} has terminated, shutting down system", ref.path());
            getContext().system().terminate();
        })
        .build();
    }
}

// 可以用来监控其他的 Actor
ActorRef actorRef = system.actorOf(Props.create(HelloWorld.class), "helloWorld");
system.actorOf(Props.create(Terminator.class, actorRef), "terminator");
```
