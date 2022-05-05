## 1. 概述

在本文中,我们将看到如何使用Maven从任何Java类运行任意主方法.

## 2. exec-maven-plugin

假设我们有以下类:

```java
public class Exec {
  private static final Logger LOGGER = LoggerFactory.getLogger(Exec.class);

  public static void main(String[] args) {
    LOGGER.info("Running the main method");
    if (args.length > 0) {
      LOGGER.info("List of arguments: {}", Arrays.toString(args));
    }
  }
}
```

我们希望通过Maven从命令行执行它的main()方法.

**为了做到这一点,我们可以使用exec-maven-plugin插件.更具体地说,这个插件的exec:java goal执行提供的java类,并将项目的依赖项作为classpath**.

要执行Exec类的main()方法,我们必须将类的完全限定类名传递给插件:

```
$ mvn compile exec:java -Dexec.mainClass="cn.tuyucheng.taketoday.main.Exec"
02:26:45.112 INFO cn.tuyucheng.taketoday.main.Exec - Running the main method
```

如上所示,我们使用exec.mainClass系统属性来传递完全限定类名.

此外,在运行main方法之前,我们必须确保classpath已经准备好.这就是为什么我们要在执行main方法之前编译源代码.

我们可以用普通java和javac实现同样的功能.然而,当我们使用相当大的classpath时,这可能会很麻烦.相反,**当使用这个插件时,Maven会自动填充classpath**.

## 3. 传递参数

还可以将参数从命令行传递给main方法.为了做到这一点,我们可以使用exec.args系统属性:

```
$ mvn compile exec:java -Dexec.mainClass="cn.tuyucheng.taketoday.main.Exec" \
  -Dexec.args="First Second"
02:31:08.235 INFO cn.tuyucheng.taketoday.main.Exec - Running the main method
02:31:08.236 INFO cn.tuyucheng.taketoday.main.Exec - List of arguments: [First, Second]
```

如上所示,我们传递一个以空格分隔的参数列表.此外,我们可以通过exec.arguments系统参数使用逗号分隔的参数列表:

```
$ mvn compile exec:java -Dexec.mainClass="cn.tuyucheng.taketoday.main.Exec" \ 
  -Dexec.arguments="Hello World,Bye"
02:32:25.616 INFO cn.tuyucheng.taketoday.main.Exec - Running the main method
02:32:25.618 INFO cn.tuyucheng.taketoday.main.Exec - List of arguments: [Hello World, Bye]
```

当我们想在参数本身中使用分隔符(空格或逗号)时,这两个选项可能很有用.

## 4. 自定义配置

我们还可以在pom.xml中明确声明插件依赖关系.这样,我们可以使用自定义和默认配置.

例如,我们可以在插件的configuration标签中指定默认的主类:

```
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>3.0.0</version>
  <configuration>
    <mainClass>cn.tuyucheng.taketoday.main.Exec</mainClass>
  </configuration>
</plugin>
```

现在,如果我们不指定该类的完全限定名.将会使用cn.tuyucheng.taketoday.main.Exec.

```
$ mvn compile exec:java
02:33:14.197 INFO cn.tuyucheng.taketoday.main.Exec - Running the main method
```

但是,仍然可以通过显式设置exec.mainClass覆盖此默认配置.

此外,我们还可以在configuration标签中指定默认程序参数:

```
<configuration>
  <mainClass>cn.tuyucheng.taketoday.main.Exec</mainClass>
  <arguments>
    <argument>First</argument>
    <argument>Second</argument>
    <argument>Third</argument>
  </arguments>
</configuration>
```

这样我们就不需要在命令行上传递这些参数:

```
[INFO] --- exec-maven-plugin:3.0.0:java (default-cli) @ maven-exec-plugin ---
 INFO cn.tuyucheng.taketoday.main.Exec - Running the main method
 INFO cn.tuyucheng.taketoday.main.Exec - List of arguments: [First, Second, Third]
```
