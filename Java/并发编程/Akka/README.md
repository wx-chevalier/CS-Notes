# Akka

In today's world, computer hardware is becoming cheaper and more powerful, as we have multiple cores on a single CPU chip. As cores keep on increasing, with the increasing power of hardware, we need a state of the art software framework which can use these cores efficiently.

Akka is such a framework, or you can say, a toolkit, which utilizes the hardware cores efficiently and lets you write performant applications.

Thus, Akka provides a basic unit of abstraction of transparent distribution called actors, which form the basis for writing resilient, elastic, event-driven, and responsive systems.

Let's see what is meant by these properties:

Resilient: Applications that can heal themselves, which means they can recover from failure, and will always be responsive, even in case of failure like if we get errors or exceptions

Elastic: A system which is responsive under varying amount of workload, that is, the system always remains responsive, irrespective of increasing or decreasing traffic, by increasing or decreasing the resources allocated to service this workload

Message Driven: A system whose components are loosely coupled with each other and communicate using asynchronous message passing, and which reacts to those messages by taking an action

Responsive: A system that satisfies the preceding three properties is called responsive

Akka 是一个建立在 Actors 概念和可组合 Futures 之上的并发框架, Akka 设计灵感来源于 Erlang；Erlang 是基于 Actor 模型构建的。它通常被用来取代阻塞锁如同步、读写锁及类似的更高级别的异步抽象。Netty 是一个异步网络库，使 JAVA NIO 的功能更好用。Akka 针对 IO 操作有一个抽象，这和 netty 是一样的。使用 Akka 可以用来创建计算集群,Actor 在不同的机器之间传递消息。从这个角度来看,Akka 相对于 Netty 来说，是一个更高层次的抽象

Akka 是一种高度可扩展的软件，这不仅仅表现在性能方面，也表现在它所适用的应用的大小。Akka 的核心，Akka-actor 是非常小的，可以非常方便地放进你的应用中，提供你需要的异步无锁并行功能，不会有任何困扰。你可以任意选择 Akka 的某些部分集成到你的应用中，也可以使用完整的包——Akka 微内核，它是一个独立的容器，可以直接部署你的 Akka 应用。随着 CPU 核数越来越多，即使你只使用一台电脑，Akka 也可作为一种提供卓越性能的选择。 Akka 还同时提供多种并发范型，允许用户选择正确的工具来完成工作。

# Todos

- https://doc.akka.io/docs/akka/current/actors.html#become-unbecome
