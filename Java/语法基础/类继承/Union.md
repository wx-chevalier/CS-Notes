# Union

在 C/C++语言中，联合体（Union），又称共用体，类似结构体（Struct）的一种数据结构。联合体（Union）和结构体（Struct）一样，可以包含很多种数据类型和变量，两者区别如下：

- 结构体（Struct）中所有变量是“共存”的，同时所有变量都生效，各个变量占据不同的内存空间；
- 联合体（Union）中是各变量是“互斥”的，同时只有一个变量生效，所有变量占据同一块内存空间。

当多个数据需要共享内存或者多个数据每次只取其一时，可以采用联合体（Union）。在 Java 语言中，没有联合体（Union）和结构体（Struct）概念，只有类（class）的概念。众所众知，结构体（Struct）可以用类（class）来实现。其实，联合体（Union）也可以用类（class）来实现。但是，这个类不具备“多个数据需要共享内存”的功能，只具备“多个数据每次只取其一”的功能。

# 使用函数方式实现 Union

```java
/** 客户消息类 */
@ToString
public class CustomerMessage {

    /** 属性相关 */
    /** 消息类型 */
    private String msgType;
    /** 目标用户 */
    private String toUser;

    /** 共用体相关 */
    /** 新闻内容 */
    private News news;
    ...

    /** 常量相关 */
    /** 新闻消息 */
    public static final String MSG_TYPE_NEWS = "news";
    ...

    /** 构造函数 */
    public CustomerMessage() {}

    /** 构造函数 */
    public CustomerMessage(String toUser) {
        this.toUser = toUser;
    }

    /** 构造函数 */
    public CustomerMessage(String toUser, News news) {
        this.toUser = toUser;
        this.msgType = MSG_TYPE_NEWS;
        this.news = news;
    }

    /** 清除消息内容 */
    private void removeMsgContent() {
        // 检查消息类型
        if (Objects.isNull(msgType)) {
            return;
        }

        // 清除消息内容
        if (MSG_TYPE_NEWS.equals(msgType)) {
            news = null;
        } else if (...) {
            ...
        }
        msgType = null;
    }

    /** 检查消息类型 */
    private void checkMsgType(String msgType) {
        // 检查消息类型
        if (Objects.isNull(msgType)) {
            throw new IllegalArgumentException("消息类型为空");
        }

        // 比较消息类型
        if (!Objects.equals(msgType, this.msgType)) {
            throw new IllegalArgumentException("消息类型不匹配");
        }
    }

    /** 设置消息类型函数 */
    public void setMsgType(String msgType) {
        // 清除消息内容
        removeMsgContent();

        // 检查消息类型
        if (Objects.isNull(msgType)) {
            throw new IllegalArgumentException("消息类型为空");
        }

        // 赋值消息内容
        this.msgType = msgType;
        if (MSG_TYPE_NEWS.equals(msgType)) {
            news = new News();
        } else if (...) {
            ...
        } else {
            throw new IllegalArgumentException("消息类型不支持");
        }
    }

    /** 获取消息类型 */
    public String getMsgType() {
        // 检查消息类型
        if (Objects.isNull(msgType)) {
            throw new IllegalArgumentException("消息类型无效");
        }

        // 返回消息类型
        return this.msgType;
    }

    /** 设置新闻 */
    public void setNews(News news) {
        // 清除消息内容
        removeMsgContent();

        // 赋值消息内容
        this.msgType = MSG_TYPE_NEWS;
        this.news = news;
    }

    /** 获取新闻 */
    public News getNews() {
        // 检查消息类型
        checkMsgType(MSG_TYPE_NEWS);

        // 返回消息内容
        return this.news;
    }

    ...
}
```

Union 类使用如下：

```java
String accessToken = ...;
String toUser = ...;
List<Article> articleList = ...;
News news = new News(articleList);
CustomerMessage customerMessage = new CustomerMessage(toUser, news);
wechatApi.sendCustomerMessage(accessToken, customerMessage);
```

# 使用继承方式实现 Union

```java
/** 客户消息类 */
@Getter
@Setter
@ToString
public abstract class CustomerMessage {
    /** 属性相关 */
    /** 消息类型 */
    private String msgType;
    /** 目标用户 */
    private String toUser;

    /** 常量相关 */
    /** 新闻消息 */
    public static final String MSG_TYPE_NEWS = "news";
    ...

    /** 构造函数 */
    public CustomerMessage(String msgType) {
        this.msgType = msgType;
    }

    /** 构造函数 */
    public CustomerMessage(String msgType, String toUser) {
        this.msgType = msgType;
        this.toUser = toUser;
    }
}

/** 新闻客户消息类 */
@Getter
@Setter
@ToString(callSuper = true)
public class NewsCustomerMessage extends CustomerMessage {

    /** 属性相关 */
    /** 新闻内容 */
    private News news;

    /** 构造函数 */
    public NewsCustomerMessage() {
        super(MSG_TYPE_NEWS);
    }

    /** 构造函数 */
    public NewsCustomerMessage(String toUser, News news) {
        super(MSG_TYPE_NEWS, toUser);
        this.news = news;
    }
}
```

Union 类使用如下：

```java
String accessToken = ...;
String toUser = ...;
List<Article> articleList = ...;
News news = new News(articleList);
CustomerMessage customerMessage = new NewsCustomerMessage(toUser, news);
wechatApi.sendCustomerMessage(accessToken, customerMessage);
```

在 C/C++语言中，联合体并不包括联合体当前的数据类型。但在上面实现的 Java 联合体中，已经包含了联合体对应的数据类型。所以，从严格意义上说，Java 联合体并不是真正的联合体，只是一个具备“多个数据每次只取其一”功能的类。
