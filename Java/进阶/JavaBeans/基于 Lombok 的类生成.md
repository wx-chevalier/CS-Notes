# 基于 Lombok 的类生成

# [Lombok](https://projectlombok.org/features/index.html)

Lombok 主要依赖编译时代码生成技术，帮你自动生成基于模板的常用的 Java 代码，譬如最常见的 Getter 与 Setter。之前动态的插入 Getter 与 Setter 主要有两种，一个是像 Intellij 与 Eclipse 这样在开发时动态插入，缺点是这样虽然不用你手动写，但是还是会让你的代码异常的冗长。另一种是通过类似于 Spring 这样基于注解的在运行时利用反射动态添加，不过这样的缺陷是会影响性能，并且有一定局限性。

# Data Model | 数据模型

## Getter&Setter

源代码：

```java

public class GetterSetterExample {
	@Getter @Setter private int age = 10;

	@Setter(AccessLevel.PROTECTED) private String name;

	@Override public String toString() {
		return String.format("%s (age: %d)", name, age);
	}
}
```

编译之后的代码：

```java
public class GetterSetterExample {
	...
	public int getAge() {
		return age;
	}

	...
	public void setAge(int age) {
		this.age = age;
	}

	...
	protected void setName(String name) {
		this.name = name;
	}
}
```

### Accessors

```java
@Accessors(fluent = true)
public class AccessorsExample {
  @Getter @Setter
  private int age = 10;
}

class PrefixExample {
  @Accessors(prefix = "f") @Getter
  private String fName = "Hello, World!";
}
```

如果设置了 chain 为 true，则仅在 Setter 方法中返回对象，仍然保留标准的 Getter/Setter 风格。

```java
public class AccessorsExample {
  private int age = 10;

  public int age() {
    return this.age;
  }

  public AccessorsExample age(final int age) {
    this.age = age;
    return this;
  }
}

class PrefixExample {
  private String fName = "Hello, World!";

  public String getName() {
    return this.fName;
  }
}
```

### Lazy Getter

源代码：

```java
public class GetterLazyExample {
	@Getter(lazy=true) private final double[] cached = expensive();

	private double[] expensive() {
		...
	}
}
```

编译之后：

```java
public class GetterLazyExample {
  private final java.util.concurrent.AtomicReference<java.lang.Object> cached = new java.util.concurrent.AtomicReference<java.lang.Object>();

  public double[] getCached() {
    java.lang.Object value = this.cached.get();
    if (value == null) {
      synchronized(this.cached) {
        value = this.cached.get();
        if (value == null) {
          final double[] actualValue = expensive();
          value = actualValue == null ? this.cached : actualValue;
          this.cached.set(value);
        }
      }
    }
    return (double[])(value == this.cached ? null : value);
  }

  private double[] expensive() {
    double[] result = new double[1000000];
    for (int i = 0; i < result.length; i++) {
      result[i] = Math.asin(i);
    }
    return result;
  }
}
}
```

### Data

源代码:

```
import lombok.AccessLevel;
import lombok.Setter;
import lombok.Data;
import lombok.ToString;

@Data public class DataExample {
	private final String name;
	@Setter(AccessLevel.PACKAGE) private int age;
	private double score;
	private String[] tags;

	@ToString(includeFieldNames=true)
	@Data(staticConstructor="of")
	public static class Exercise<T> {
		private final String name;
		private final T value;
	}
}
```

编译后：

```
import java.util.Arrays;

public class DataExample {
	private final String name;
	private int age;
	private double score;
	private String[] tags;

	public DataExample(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	void setAge(int age) {
		this.age = age;
	}

	public int getAge() {
		return this.age;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getScore() {
		return this.score;
	}

	public String[] getTags() {
		return this.tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	@Override public String toString() {
		return "DataExample(" + this.getName() + ", " + this.getAge() + ", " + this.getScore() + ", " + Arrays.deepToString(this.getTags()) + ")";
	}

	protected boolean canEqual(Object other) {
		return other instanceof DataExample;
	}

	@Override public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof DataExample)) return false;
		DataExample other = (DataExample) o;
		if (!other.canEqual((Object)this)) return false;
		if (this.getName() == null ? other.getName() != null : !this.getName().equals(other.getName())) return false;
		if (this.getAge() != other.getAge()) return false;
		if (Double.compare(this.getScore(), other.getScore()) != 0) return false;
		if (!Arrays.deepEquals(this.getTags(), other.getTags())) return false;
		return true;
	}

	@Override public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final long temp1 = Double.doubleToLongBits(this.getScore());
		result = (result*PRIME) + (this.getName() == null ? 43 : this.getName().hashCode());
		result = (result*PRIME) + this.getAge();
		result = (result*PRIME) + (int)(temp1 ^ (temp1 >>> 32));
		result = (result*PRIME) + Arrays.deepHashCode(this.getTags());
		return result;
	}

	public static class Exercise<T> {
		private final String name;
		private final T value;

		private Exercise(String name, T value) {
			this.name = name;
			this.value = value;
		}

		public static <T> Exercise<T> of(String name, T value) {
			return new Exercise<T>(name, value);
		}

		public String getName() {
			return this.name;
		}

		public T getValue() {
			return this.value;
		}

		@Override public String toString() {
			return "Exercise(name=" + this.getName() + ", value=" + this.getValue() + ")";
		}

		protected boolean canEqual(Object other) {
			return other instanceof Exercise;
		}

		@Override public boolean equals(Object o) {
			if (o == this) return true;
			if (!(o instanceof Exercise)) return false;
			Exercise<?> other = (Exercise<?>) o;
			if (!other.canEqual((Object)this)) return false;
			if (this.getName() == null ? other.getValue() != null : !this.getName().equals(other.getName())) return false;
			if (this.getValue() == null ? other.getValue() != null : !this.getValue().equals(other.getValue())) return false;
			return true;
		}

		@Override public int hashCode() {
			final int PRIME = 59;
			int result = 1;
			result = (result*PRIME) + (this.getName() == null ? 43 : this.getName().hashCode());
			result = (result*PRIME) + (this.getValue() == null ? 43 : this.getValue().hashCode());
			return result;
		}
	}
}
```

## Object

### Constructor

源代码

```
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ConstructorExample<T> {
	private int x, y;
	@NonNull private T description;

	@NoArgsConstructor
	public static class NoArgsExample {
		@NonNull private String field;
	}
}
```

编译之后：

```
public class ConstructorExample<T> {
	private int x, y;
	@NonNull private T description;

	private ConstructorExample(T description) {
		if (description == null) throw new NullPointerException("description");
		this.description = description;
	}

	public static <T> ConstructorExample<T> of(T description) {
		return new ConstructorExample<T>(description);
	}

	@java.beans.ConstructorProperties({"x", "y", "description"})
	protected ConstructorExample(int x, int y, T description) {
		if (description == null) throw new NullPointerException("description");
		this.x = x;
		this.y = y;
		this.description = description;
	}

	public static class NoArgsExample {
		@NonNull private String field;

		public NoArgsExample() {
		}
	}
}
```

### Builder

源代码：

```
package wx.toolkits.basic.class_object.utils.lombok.object;

import lombok.experimental.Builder;

import java.util.Set;

@Builder
public class BuilderExample {
    private String name;
    private int age;
    private Set<String> occupations;

    public static void main(String args[]) {

        BuilderExample builderExample = BuilderExample.builder().build();

    }

}
```

编译之后的源代码：

```
import java.util.Set;

public class BuilderExample {
	private String name;
	private int age;
	private Set<String> occupations;

	BuilderExample(String name, int age, Set<String> occupations) {
		this.name = name;
		this.age = age;
		this.occupations = occupations;
}

	public static BuilderExampleBuilder builder() {
		return new BuilderExampleBuilder();
	}

	public static class BuilderExampleBuilder {
		private String name;
		private int age;
		private java.util.ArrayList<String> occupations;

		BuilderExampleBuilder() {
		}

		public BuilderExampleBuilder name(String name) {
			this.name = name;
			return this;
		}

		public BuilderExampleBuilder age(int age) {
			this.age = age;
			return this;
		}

		public BuilderExampleBuilder occupation(String occupation) {
			if (this.occupations == null) {
				this.occupations = new java.util.ArrayList<String>();
			}

			this.occupations.add(occupation);
			return this;
		}

		public BuilderExampleBuilder occupations(Collection<? extends String> occupations) {
			if (this.occupations == null) {
				this.occupations = new java.util.ArrayList<String>();
			}

			this.occupations.addAll(occupations);
			return this;
		}

		public BuilderExampleBuilder clearOccupations() {
			if (this.occupations != null) {
				this.occupations.clear();
			}

			return this;
		}

		public BuilderExample build() {
			// complicated switch statement to produce a compact properly sized immutable set omitted.
			// go to https://projectlombok.org/features/Singular-snippet.html to see it.
			Set<String> occupations = ...;
			return new BuilderExample(name, age, occupations);
		}

		@java.lang.Override
		public String toString() {
			return "BuilderExample.BuilderExampleBuilder(name = " + this.name + ", age = " + this.age + ", occupations = " + this.occupations + ")";
		}
	}
}
```

#### 设置建造者模式的必要参数

```
import lombok.Builder;
import lombok.ToString;

@Builder(builderMethodName = "hiddenBuilder")
@ToString
public class Person {

    private String name;
    private String surname;

    public static PersonBuilder builder(String name) {
        return hiddenBuilder().name(name);
    }
}
```

用法如下：

```

```

## Exception:异常处理

### NonNull

源代码：

```
import lombok.NonNull;

public class NonNullExample extends Something {
	private String name;

	public NonNullExample(@NonNull Person person) {
		super("Hello");
		this.name = person.getName();
	}
}
```

编译之后：

```
import lombok.NonNull;

public class NonNullExample extends Something {
	private String name;

	public NonNullExample(@NonNull Person person) {
		super("Hello");
		if (person == null) {
			throw new NullPointerException("person");
		}
		this.name = person.getName();
	}
}
```

### SneakyThrows

源代码：

```
import lombok.SneakyThrows;

public class SneakyThrowsExample implements Runnable {
	@SneakyThrows(UnsupportedEncodingException.class)
	public String utf8ToString(byte[] bytes) {
		return new String(bytes, "UTF-8");
	}

	@SneakyThrows
	public void run() {
		throw new Throwable();
	}
}
```

编译之后：

```
import lombok.Lombok;

public class SneakyThrowsExample implements Runnable {
	public String utf8ToString(byte[] bytes) {
		try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw Lombok.sneakyThrow(e);
		}
	}

	public void run() {
		try {
			throw new Throwable();
		} catch (Throwable t) {
			throw Lombok.sneakyThrow(t);
		}
	}
}
```

## Thread:线程

### Synchronized

源代码：

```
import lombok.Synchronized;

public class SynchronizedExample {
	private final Object readLock = new Object();

	@Synchronized
	public static void hello() {
		System.out.println("world");
	}

	@Synchronized
	public int answerToLife() {
		return 42;
	}

	@Synchronized("readLock")
	public void foo() {
		System.out.println("bar");
	}
}
```

编译之后：

```
public class SynchronizedExample {
	private static final Object $LOCK = new Object[0];
	private final Object $lock = new Object[0];
	private final Object readLock = new Object();

	public static void hello() {
		synchronized($LOCK) {
			System.out.println("world");
		}
	}

	public int answerToLife() {
		synchronized($lock) {
			return 42;
		}
	}

	public void foo() {
		synchronized(readLock) {
			System.out.println("bar");
		}
	}
}
```

## Utils

### Cleanup

源代码：

```
import lombok.Cleanup;
import java.io.*;

public class CleanupExample {
	public static void main(String[] args) throws IOException {
		@Cleanup InputStream in = new FileInputStream(args[0]);
		@Cleanup OutputStream out = new FileOutputStream(args[1]);
		byte[] b = new byte[10000];
		while (true) {
			int r = in.read(b);
			if (r == -1) break;
			out.write(b, 0, r);
		}
	}
}
```

编译之后：

```
import java.io.*;

public class CleanupExample {
	public static void main(String[] args) throws IOException {
		InputStream in = new FileInputStream(args[0]);
		try {
			OutputStream out = new FileOutputStream(args[1]);
			try {
				byte[] b = new byte[10000];
				while (true) {
					int r = in.read(b);
					if (r == -1) break;
					out.write(b, 0, r);
				}
			} finally {
				if (out != null) {
					out.close();
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
}
```

### Log:日志

使用@Log 或者类似注解可以为类自动创建一个 log 对象，其效果如下所示：

```
@CommonsLog
Creates private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(LogExample.class);
@Log
Creates private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(LogExample.class.getName());
@Log4j
Creates private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LogExample.class);
@Log4j2
Creates private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(LogExample.class);
@Slf4j
Creates private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogExample.class);
@XSlf4j
Creates private static final org.slf4j.ext.XLogger log = org.slf4j.ext.XLoggerFactory.getXLogger(LogExample.class);
```

使用了 Lombok 之后的代码如下：

```
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Log
public class LogExample {

	public static void main(String... args) {
		log.error("Something's wrong here");
	}
}

@Slf4j
public class LogExampleOther {

	public static void main(String... args) {
		log.error("Something else is wrong here");
	}
}

@CommonsLog(topic="CounterLog")
public class LogExampleCategory {

	public static void main(String... args) {
		log.error("Calling the 'CounterLog' with a message");
	}
}
```

编译之后的代码如下：

```
public class LogExample {
	private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(LogExample.class.getName());

	public static void main(String... args) {
		log.error("Something's wrong here");
	}
}

public class LogExampleOther {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogExampleOther.class);

	public static void main(String... args) {
		log.error("Something else is wrong here");
	}
}

public class LogExampleCategory {
	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog("CounterLog");

	public static void main(String... args) {
		log.error("Calling the 'CounterLog' with a message");
	}
}
```

其他可配置的参数为：

- lombok.log.fieldName = an identifier (default: log)
  The generated logger fieldname is by default 'log', but you can change it to a different name with this setting.
- lombok.log.fieldIsStatic = [true | false] (default: true)
  Normally the generated logger is a static field. By setting this key to false, the generated field will be an instance field instead.
- lombok.log.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of any of the various log annotations as a warning or error if configured.
- lombok.log.apacheCommons.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of @lombok.extern.apachecommons.CommonsLog as a warning or error if configured.
- lombok.log.javaUtilLogging.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of @lombok.extern.java.Log as a warning or error if configured.
- lombok.log.log4j.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of @lombok.extern.log4j.Log4j as a warning or error if configured.
- lombok.log.log4j2.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of @lombok.extern.log4j.Log4j2 as a warning or error if configured.
- lombok.log.slf4j.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of @lombok.extern.slf4j.Slf4j as a warning or error if configured.
- lombok.log.xslf4j.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of @lombok.extern.slf4j.XSlf4j as a warning or error if configured.
