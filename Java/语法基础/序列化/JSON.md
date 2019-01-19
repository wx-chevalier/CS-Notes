# Java 中 JSON 处理

# Jackson

- [jackson-databind文档](https://github.com/FasterXML/jackson-databind)

Jackson可以轻松的将Java对象转换成json对象和xml文档，同样也可以将json、xml转换成Java对象。在项目中如果要引入Jackson，可以直接利用Maven或者Gradle引入：
```
<properties>
  ...
  <!-- Use the latest version whenever possible. -->
  <jackson.version>2.7.0</jackson.version>
  ...
</properties>

<dependencies>
  ...
  <dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>${jackson.version}</version>
  </dependency>
  ...
</dependencies>
```
注意，databind项目已经自动依赖了jackson-core与jackson-annotation，不需要额外重复引入。
## Convert Java to JSON
首先声明有一个简单的POJO:
```
// Note: can use getters/setters as well; here we just use public fields directly:
public class MyValue {
  public String name;
  public int age;
  // NOTE: if using getters/setters, can keep fields `protected` or `private`
}
```
然后创建一个ObjectMapper实例用于进行转化：
```
ObjectMapper mapper = new ObjectMapper(); // create once, reuse
```
```
MyValue value = mapper.readValue(new File("data.json"), MyValue.class);
// or:
value = mapper.readValue(new URL("http://some.com/api/entry.json"), MyValue.class);
// or:
value = mapper.readValue("{\"name\":\"Bob\", \"age\":13}", MyValue.class);
```
我们可以参考一个实例，将某个Staff的信息转化为JSON然后写入到文件中，首先来定义实体类：
```
package com.mkyong.json;

import java.math.BigDecimal;
import java.util.List;

public class Staff {

	private String name;
	private int age;
	private String position;
	private BigDecimal salary;
	private List<String> skills;

	//getters and setters
```
然后具体的将Java实体类转化为JSON的语句为：
```
package com.mkyong.json;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Jackson2Example {

	public static void main(String[] args) {
		Jackson2Example obj = new Jackson2Example();
		obj.run();
	}

	private void run() {
		ObjectMapper mapper = new ObjectMapper();

		Staff staff = createDummyObject();

		try {
			// Convert object to JSON string and save into a file directly
			mapper.writeValue(new File("D:\\staff.json"), staff);

			// Convert object to JSON string
			String jsonInString = mapper.writeValueAsString(staff);
			System.out.println(jsonInString);

			// Convert object to JSON string and pretty print
			jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(staff);
			System.out.println(jsonInString);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Staff createDummyObject() {

		Staff staff = new Staff();

		staff.setName("mkyong");
		staff.setAge(33);
		staff.setPosition("Developer");
		staff.setSalary(new BigDecimal("7500"));

		List<String> skills = new ArrayList<>();
		skills.add("java");
		skills.add("python");

		staff.setSkills(skills);

		return staff;

	}

}
```
最终的输出为：
```
//new json file is created in D:\\staff.json"

{"name":"mkyong","age":33,"position":"Developer","salary":7500,"skills":["java","python"]}

{
  "name" : "mkyong",
  "age" : 33,
  "position" : "Developer",
  "salary" : 7500,
  "skills" : [ "java", "python" ]
}
```

### Properties:属性处理
#### Rename:属性重命名
```
public class Name {
  @JsonProperty("firstName")
  public String _first_name;
}
```
在将Name实体类转化为JSON的时候，就会变成：
```
{ "firstName" : "Bob" }
```
#### Ignore:属性忽略
```
public class Value {
  public int value;
  @JsonIgnore public int internalValue;
}
```
最终生成的JSON是如下格式：
```
{ "value" : 42 }
```
也可以在类的头部统一声明:
```
@JsonIgnoreProperties({ "extra", "uselessValue" })
public class Value {
  public int value;
}
```
那么如下的JSON字符串也是可以被转化为该实体类的:
```
{ "value" : 42, "extra" : "fluffy", "uselessValue" : -13 }
```
对于意外地未知属性，也可以统一忽略：
```
@JsonIgnoreProperties(ignoreUnknown=true)
public class PojoWithAny {
  public int value;
}
```
### @JsonView:动态控制展示的成员变量
首先定义一个简单的View控制类：
```
package com.mkyong.json;

public class Views {

	public static class Normal{};
	
	public static class Manager extends Normal{};

}
```
在下面的代码实现中，如果是选择了Normal View，那么salary属性将会被隐藏，而在Manager View状态下，任何属性都会被展示。
```
package com.mkyong.json;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonView;

public class Staff {

	@JsonView(Views.Normal.class)
	private String name;

	@JsonView(Views.Normal.class)
	private int age;

	@JsonView(Views.Normal.class)
	private String position;

	@JsonView(Views.Manager.class)
	private BigDecimal salary;

	@JsonView(Views.Normal.class)
	private List<String> skills;
```
在进行Object转化为JSON的过程中，进行视图控制：
```
package com.mkyong.json;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Jackson2Example {

	public static void main(String[] args) {
		Jackson2Example obj = new Jackson2Example();
		obj.run();
	}

	private void run() {
		ObjectMapper mapper = new ObjectMapper();

		Staff staff = createDummyObject();

		try {

			// Salary will be hidden
			System.out.println("Normal View");
			String normalView = mapper.writerWithView(Views.Normal.class).writeValueAsString(staff);
			System.out.println(normalView);

			String jsonInString = "{\"name\":\"mkyong\",\"age\":33,\"position\":\"Developer\",\"salary\":7500,\"skills\":[\"java\",\"python\"]}";
			Staff normalStaff = mapper.readerWithView(Views.Normal.class).forType(Staff.class).readValue(jsonInString);
			System.out.println(normalStaff);

			// Display everything
			System.out.println("\nManager View");
			String managerView = mapper.writerWithView(Views.Manager.class).writeValueAsString(staff);
			System.out.println(managerView);

			Staff managerStaff = mapper.readerWithView(Views.Manager.class).forType(Staff.class).readValue(jsonInString);
			System.out.println(managerStaff);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Staff createDummyObject() {

		Staff staff = new Staff();

		staff.setName("mkyong");
		staff.setAge(33);
		staff.setPosition("Developer");
		staff.setSalary(new BigDecimal("7500"));

		List<String> skills = new ArrayList<>();
		skills.add("java");
		skills.add("python");

		staff.setSkills(skills);

		return staff;

	}

}
```
最终输出的结果为:
```
Normal View
{"name":"mkyong","age":33,"position":"Developer","skills":["java","python"]}
Staff [name=mkyong, age=33, position=Developer, salary=null, skills=[java, python]]

Manager View
{"name":"mkyong","age":33,"position":"Developer","salary":7500,"skills":["java","python"]}
Staff [name=mkyong, age=33, position=Developer, salary=7500, skills=[java, python]]
```
## Convert JSON to Java
将JSON转化为Java的实体类同样需要用到ObjectMapper对象：
```
mapper.writeValue(new File("result.json"), myResultObject);
// or:
byte[] jsonBytes = mapper.writeValueAsBytes(myResultObject);
// or:
String jsonString = mapper.writeValueAsString(myResultObject);
```
而如果我们要将JSON转化为Java中的List或者Map的话，可以采用如下方式：
```
//将某个JSON转化为 List
String json = "[{\"name\":\"mkyong\"}, {\"name\":\"laplap\"}]";
List<Staff> list = mapper.readValue(json, new TypeReference<List<Staff>>(){});
//将某个JSON转化为Map
String json = "{\"name\":\"mkyong\", \"age\":33}";
Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String,Object>>(){});
```
还是来看一个实例，：
```
package com.mkyong.json;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Jackson2Example {

	public static void main(String[] args) {
		Jackson2Example obj = new Jackson2Example();
		obj.run();
	}

	private void run() {
		ObjectMapper mapper = new ObjectMapper();

		try {

			// Convert JSON string from file to Object
			Staff staff = mapper.readValue(new File("D:\\staff.json"), Staff.class);
			System.out.println(staff);

			// Convert JSON string to Object
			String jsonInString = "{\"name\":\"mkyong\",\"salary\":7500,\"skills\":[\"java\",\"python\"]}";
			Staff staff1 = mapper.readValue(jsonInString, Staff.class);
			System.out.println(staff1);

			//Pretty print
			String prettyStaff1 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(staff1);
			System.out.println(prettyStaff1);
			
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
```
最终的输出为：
```
Staff [name=mkyong, age=33, position=Developer, salary=7500, skills=[java, python]]

Staff [name=mkyong, age=0, position=null, salary=7500, skills=[java, python]]

{
  "name" : "mkyong",
  "age" : 0,
  "position" : null,
  "salary" : 7500,
  "skills" : [ "java", "python" ]
}
```
### Tree Model:抽象的JSON数据类型，类似于FastJSON中的JSONObject
```
// can be read as generic JsonNode, if it can be Object or Array; or,
// if known to be Object, as ObjectNode, if array, ArrayNode etc:
ObjectNode root = mapper.readTree("stuff.json");
String name = root.get("name").asText();
int age = root.get("age").asInt();

// can modify as well: this adds child Object as property 'other', set property 'type'
root.with("other").put("type", "student");
String json = mapper.writeValueAsString(root);

// with above, we end up with something like as 'json' String:
// {
//   "name" : "Bob", "age" : 13,
//   "other" : {
//      "type" : "student"
//   }
// }
```
### Constructor:自定义构造器
默认情况下，Jackson使用默认的构造器创建新的对象，不过你也可以使用`@JsonCreator`与`@JsonProperty`注解来自定义对象创建函数与值的绑定。
```
public class CtorPOJO {
   private final int _x, _y;

   @JsonCreator
   public CtorPOJO(@JsonProperty("x") int x, @JsonProperty("y") int y) {
      _x = x;
      _y = y;
   }
}
```
```
public class DelegatingPOJO {
   private final int _x, _y;

   @JsonCreator
   public DelegatingPOJO(Map<String,Object> delegate) {
      _x = (Integer) delegate.get("x");
      _y = (Integer) delegate.get("y");
   }
}
```

# FastJson

FastJson上由阿里的一位工程师开发并开源的。

### Encode

``` 
import com.alibaba.fastjson.JSON;

Group group = new Group();
group.setId(0L);
group.setName("admin");

User guestUser = new User();
guestUser.setId(2L);
guestUser.setName("guest");

User rootUser = new User();
rootUser.setId(3L);
rootUser.setName("root");

group.add(guestUser);
group.add(rootUser);

String jsonString = JSON.toJSONString(group);

System.out.println(jsonString);
```

### Output

``` 
{"id":0,"name":"admin","users":[{"id":2,"name":"guest"},{"id":3,"name":"root"}]}

```

### Decode

``` 
String jsonString = ...;
Group group = JSON.parseObject(jsonString, Group.class);

```

- Group.java

``` 
public class Group {

    private Long       id;
    private String     name;
    private List<User> users = new ArrayList<User>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

        public void addUser(User user) {
            users.add(user);
        }
}

```

- User.java

``` 
public class User {

    private Long   id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```