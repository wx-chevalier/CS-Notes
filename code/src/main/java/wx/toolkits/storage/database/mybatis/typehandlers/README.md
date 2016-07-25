用于对Java 8中新增的时间与日期类型进行处理:
```
java.time.Instant (via java.sql.Timestamp)
java.time.LocalDate (via java.sql.Date)
java.time.LocalDateTime (via java.sql.Timestamp)
java.time.LocalTime (via java.sql.Time)
java.time.OffsetDateTime (via java.sql.Timestamp)
java.time.ZonedDateTime (via java.sql.Timestamp)
```

### Mybatis config

```
<!-- mybatis-config.xml -->
<typeHandlers>
  <package name="wx.toolkits.storage.database.mybatis.typehandlers"/>
</typeHandlers>

```

Or you can specify each type handler class one by one. In a case if you are need particular handlers only.

```
<!-- mybatis-config.xml -->
<typeHandlers>
  <typeHandler handler="wx.toolkits.storage.database.mybatis.typehandlers.InstantTypeHandler"/>
  <typeHandler handler="wx.toolkits.storage.database.mybatis.typehandlers.LocalDateTypeHandler"/>
  <typeHandler handler="wx.toolkits.storage.database.mybatis.typehandlers.LocalDateTimeTypeHandler"/>
  <typeHandler handler="wx.toolkits.storage.database.mybatis.typehandlers.LocalTimeTypeHandler"/>
  <typeHandler handler="wx.toolkits.storage.database.mybatis.typehandlers.OffsetDateTimeTypeHandler"/>
  <typeHandler handler="wx.toolkits.storage.database.mybatis.typehandlers.ZonedDateTimeTypeHandler"/>
</typeHandlers>

```

### [](https://github.com/javaplugs/mybatis-types/blob/master/README.md#mybatis-via-spring)Mybatis via Spring

```
<bean id="SomeId" class="org.mybatis.spring.SqlSessionFactoryBean">
    <!-- your configuration -->
    <property name="typeHandlersPackage" value="wx.toolkits.storage.database.mybatis.typehandlers" />
</bean>
```

