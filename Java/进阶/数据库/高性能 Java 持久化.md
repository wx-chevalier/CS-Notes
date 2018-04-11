# Introduction

一个高性能的数据访问层需要很多关于数据库的内部结构、JDBC、JPA、Hibernate 以及很多优化商业应用的技术建议。

# SQL Statement Logging:SQL 语句日志

如果你正在使用譬如 Hibernate 或者 MyBatis 这样的 ORM 框架，那么可以参考[验证执行语句的效率](https://vladmihalcea.com/2016/05/03/the-best-way-of-logging-jdbc-statements/)。另外推荐一个 [测试中断言机制](https://vladmihalcea.com/2014/02/01/taming-jpa-with-the-sql-statement-count-validator/) 可以帮你在提交代码之前就发现很多的查询问题。

# Connection management:连接管理

数据库连接一直是数据库中比较耗时的操作，因此建议是务必使用[数据库连接池](https://vladmihalcea.com/2014/04/17/the-anatomy-of-connection-pooling/) 机制。另外，数据库连接还受到数据库底层的限制，因此也需要合理有效地释放无用的数据库连接。在性能调优中，我们经常需要测试并且设置合理的连接池大小。这里推荐一个[FlexyPool](https://vladmihalcea.com/2014/04/30/professional-connection-pool-sizing/)工具可以帮助你选择生产环境下合适的连接池大小。

# JDBC Batching:批量 JDBC 操作

JDBC Batching 允许在单次数据库连接中发送多个 SQL 语句。[这篇博客里进行了对比可以看出 Batch 操作的性能提升非常巨大](https://leanpub.com/high-performance-java-persistence/read#jdbc-batch-updates) ，无论是在客户端还是数据库端。 `PreparedStatements` 是不错的用于 Batching 操作的选择，像 Oracle 也仅支持基于 PreparedStatements 的 Batching 操作。
JDBC 中已经基于[`PreparedStataement.addBatch`](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html#addBatch--) 与 [`PreparedStataement.executeBatch`](https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html#executeBatch--))提供了 Batching 操作的辅助，不过如果打算手动的构造 Batching 操作，那么在设计阶段就要考虑到是否需要引入 Batching。如果你用的是 Hibernate，那么[可以用简单的配置就开启 Batching](https://vladmihalcea.com/2015/03/18/how-to-batch-insert-and-update-statements-with-hibernate/)，Hibernate 5.2 提供了 [Session 级别的 Batching](https://hibernate.atlassian.net/browse/HHH-10431), 也是非常方便的。

# Statement Caching:语句缓存

Statement Caching 算是最不常用的几种优化手段之一了，你可以利用`PreparedStatements`同时在客户端(Driver)或者数据库端同时缓存语句。

# Hibernate Identifiers

如果你是使用 Hibernate 作为 ORM 工具，那么`IDENTITY`生成器可能会影响到你的性能，因为它会禁止掉 JDBC Batching。`Table`生成器也不是啥好选择，它会使用独立的事务上下文进行捕获操作，而导致底层的事务日志承受额外的压力，并且导致了每次连接池中的新的请求都需要一个新的 Identifier。因此笔者还是推荐`SEQUENCE`生成器，SQL Server 在 2012 版本之后也开始支持了该生成器。

# 选择合适的列类型

在数据库设计的时候，我们应该尽可能地选用合适的列类型，这样可以让你的数据库以最合适的方式去索引存储你的数据。譬如在 PostgreSQL 中你应该使用`inet`来存放 IPv4 的地址，特别是 Hibernate 还允许你[自定义数据类型](https://vladmihalcea.com/2016/06/20/how-to-map-json-objects-using-generic-hibernate-types/)，这样方面和数据库中的列类型一一对应。

# Relationships:映射关联

Hibernate 提供了很多的关系映射，不过并不是所有的映射都是性能优化的。
![](https://vladmihalcea.files.wordpress.com/2016/06/relationships.png?w=1326&h=398)
我们在开发的过程中需要注意避免单向的关系映射，以及`@ManyToMany`这种映射。对于集合查询而言，双向的`@OneToMany`关系才是值得推荐的。

# Inheritance:继承

继承是面向对象的语言中的不可或缺的一部分，但这也是关系型数据库与面向对象的语言之间的不协调最甚的地方。JPA 提供了譬如`SINGLE_TABLE`、`JOIN`以及`TABLE_PER_CLASS`来处理继承映射的问题，而这几个办法都是各有千秋。

* `SINGLE_TABLE`在 SQL 语句中的表现最好，不过不能使用`NOT NULL`约束，数据完整性的控制较差。
* `JOIN` 通过更复杂的语句控制来保证了数据的完整性，只要你不使用多态查询或者`@OneToMany`关系注解，那一切还好。
* 应该避免使用`TABLE_PER_CLASS`，它基本上无法生成高效的 SQL 语句。

# Persistence Context Size:持久化上下文的大小

在使用 JPA 或者 Hibernate 时候，应该随时注意持久化上下文的大小，避免同时管理过多的实体类。通过限制受管实体类的数量，我们可以更好地进行内存管理，而默认的[脏检测机制](https://vladmihalcea.com/2014/08/21/the-anatomy-of-hibernate-dirty-checking/)也会有更好的效果。

# 只获取必要的数据

获取过多的冗余数据可能是导致数据访问层性能下降的原因之一，即使是包含了投影等操作，对于实体的查询应该也是排外的，即不会引入冗余数据的。我们应该只获取那些业务逻辑需要到的数据，这里推荐使用 DTO Projections。[过早的数据获取](https://vladmihalcea.com/2014/12/15/eager-fetching-is-a-code-smell/)以及[Open Session In View](https://vladmihalcea.com/2016/05/30/the-open-session-in-view-anti-pattern/)这种反模式都是要被避免的。

# Caching:缓存

![](https://vladmihalcea.files.wordpress.com/2016/06/cachelayers.png)
关系型数据库使用了很多的内存缓冲结构体来避免大量的磁盘访问，但是我们往往忽略了[数据库缓存](https://vladmihalcea.com/2015/04/16/things-to-consider-before-jumping-to-enterprise-caching/)。我们可以通过调整数据库查询引擎，将更多的内容留于内存中以避免磁盘查询最终明显的减少响应耗时。应用层的缓存则利用高速副本的方式来保证低响应时间。而 Second-Level 缓存能够有效减少读写事务的响应时间，特别是在主从复制架构中。根据不同的应用取钱，Hibernate 提供了 [READ_ONLY](https://vladmihalcea.com/2015/04/27/how-does-hibernate-read_only-cacheconcurrencystrategy-work/), [NONSTRICT_READ_WRITE](https://vladmihalcea.com/2015/05/18/how-does-hibernate-nonstrict_read_write-cacheconcurrencystrategy-work/), [READ_WRITE](https://vladmihalcea.com/2015/05/25/how-does-hibernate-read_write-cacheconcurrencystrategy-work/), 以及 [TRANSACTIONAL](https://vladmihalcea.com/2015/06/01/how-does-hibernate-transactional-cacheconcurrencystrategy-work/)这几种方式。

# Concurrency Control:并发控制

在考虑性能和数据完整性的时候，[事务隔离层](https://vladmihalcea.com/2014/12/23/a-beginners-guide-to-transaction-isolation-levels-in-enterprise-java/) 就变得至关重要。对于并发较高的应用，需要避免[更新失败](https://vladmihalcea.com/2014/09/14/a-beginners-guide-to-database-locking-and-the-lost-update-phenomena/), 可以使用 [乐观锁或者扩展的持久化上下文](https://vladmihalcea.com/2014/09/22/preventing-lost-updates-in-long-conversations/).
而为了避免 `乐观锁中的` false positives, 可以使用 [无版本的乐观控制](https://vladmihalcea.com/2014/12/08/the-downside-of-version-less-optimistic-locking/)或者[基于写属性集的实体划分](https://vladmihalcea.com/2014/11/10/an-entity-modeling-strategy-for-scaling-optimistic-locking/).

# 提高数据库查询能力

虽然你是用了 JPA 或者 Hibernate，但是你可以用一些原生查询，建议是好好利用[Window Functions](https://vladmihalcea.com/2014/05/12/time-to-break-free-from-the-sql-92-mindset/), CTE (Common Table Expressions), `CONNECT BY`, `PIVOT`等等。这些工具能够避免你一次性传输过多的数据进入应用层，如果你可以把这个操作托付给数据库层进行，那么可以仅关心最终的结果，从而节约了磁盘 IO 与网络带宽。

# 集群扩展

关系型数据库能够方便地进行扩展，像 Facebook、Twitter、Pinterest 这些大公司都扩展了数据库系统：
![](https://vladmihalcea.files.wordpress.com/2016/06/databaseintegrationpoint.png?w=1326&h=656)
[数据副本与分片](http://highscalability.com/blog/2016/5/11/performance-and-scaling-in-enterprise-systems.html)是两种常用的增加吞吐量的扩展方式，你应该合理的组合应用这些方式从而提高你的商业应用的能力。
