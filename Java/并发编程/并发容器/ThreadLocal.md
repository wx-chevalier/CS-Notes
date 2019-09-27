# ThreadLocal

ThreadLocal 提供了线程专有对象，可以在整个线程生命周期中随时取用，极大地方便了一些逻辑的实现。常见的 ThreadLocal 用法主要有两种：保存线程上下文对象，避免多层级参数传递；保存非线程安全对象，避免多线程并发调用。

Because if it were an instance level field, then it would actually be "Per Thread - Per Instance", not just a guaranteed "Per Thread." That isn't normally the semantic you're looking for.

Usually it's holding something like objects that are scoped to a User Conversation, Web Request, etc. You don't want them also sub-scoped to the instance of the class.
One web request => one Persistence session.
Not one web request => one persistence session per object.

# 应用示例

## 跨层级参数传递

以 PageHelper 插件的源代码中的分页参数设置与使用为例说明，如果要把分页参数通过函数参数逐级传给查询语句，除非修改 MyBatis 相关接口函数，否则是不可能实现的。首先是设置分页参数代码：

```java
/** 分页方法类 */
public abstract class PageMethod {
    /** 本地分页 */
    protected static final ThreadLocal<Page> LOCAL_PAGE = new ThreadLocal<Page>();

    /** 设置分页参数 */
    protected static void setLocalPage(Page page) {
        LOCAL_PAGE.set(page);
    }

    /** 获取分页参数 */
    public static <T> Page<T> getLocalPage() {
        return LOCAL_PAGE.get();
    }

    /** 开始分页 */
    public static <E> Page<E> startPage(int pageNum, int pageSize, boolean count, Boolean reasonable, Boolean pageSizeZero) {
        Page<E> page = new Page<E>(pageNum, pageSize, count);
        page.setReasonable(reasonable);
        page.setPageSizeZero(pageSizeZero);
        Page<E> oldPage = getLocalPage();
        if (oldPage != null && oldPage.isOrderByOnly()) {
            page.setOrderBy(oldPage.getOrderBy());
        }
        setLocalPage(page);
        return page;
    }
}
```

然后使用分页参数代码：

```java
/** 虚辅助方言类 */
public abstract class AbstractHelperDialect extends AbstractDialect implements Constant {
    /** 获取本地分页 */
    public <T> Page<T> getLocalPage() {
        return PageHelper.getLocalPage();
    }

    /** 获取分页SQL */
    @Override
    public String getPageSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey pageKey) {
        String sql = boundSql.getSql();
        Page page = getLocalPage();
        String orderBy = page.getOrderBy();
        if (StringUtil.isNotEmpty(orderBy)) {
            pageKey.update(orderBy);
            sql = OrderByParser.converToOrderBySql(sql, orderBy);
        }
        if (page.isOrderByOnly()) {
            return sql;
        }
        return getPageSql(sql, page, pageKey);
    }
    ...
}
```

最后使用分页插件代码：

```java
/** 查询用户函数 */
public PageInfo<UserDO> queryUser(UserQuery userQuery, int pageNum, int pageSize) {
    PageHelper.startPage(pageNum, pageSize);
    List<UserDO> userList = userDAO.queryUser(userQuery);
    PageInfo<UserDO> pageInfo = new PageInfo<>(userList);
    return pageInfo;
}
```

## 非线程安全对象存储

在写日期格式化工具函数时，首先想到的写法如下：

```java
/** 日期模式 */
private static final String DATE_PATTERN = "yyyy-MM-dd";

/** 格式化日期函数 */
public static String formatDate(Date date) {
    return new SimpleDateFormat(DATE_PATTERN).format(date);
}
```

其中，每次调用都要初始化 DateFormat 导致性能较低，把 DateFormat 定义成常量后的写法如下：

```java
/** 日期格式 */
private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

/** 格式化日期函数 */
public static String formatDate(Date date) {
    return DATE_FORMAT.format(date);
}
```

由于 SimpleDateFormat 是非线程安全的，当多线程同时调用 formatDate 函数时，会导致返回结果与预期不一致。如果采用 ThreadLocal 定义线程专有对象，优化后的代码如下：

```java
/** 本地日期格式 */
private static final ThreadLocal<DateFormat> LOCAL_DATE_FORMAT = new ThreadLocal<DateFormat>() {
    @Override
    protected DateFormat initialValue() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }
};

/** 格式化日期函数 */
public static String formatDate(Date date) {
    return LOCAL_DATE_FORMAT.get().format(date);
}
```

不过 ThreadLocal 有一定的内存泄露的风险，尽量在业务代码结束前调用 remove 函数进行数据清除。

# 链接

- https://mp.weixin.qq.com/s/6RlOtw78ALEN7UVUX8FoDg
