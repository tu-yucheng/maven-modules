## 1. 概述

Maven提供了各种各样的插件来帮助我们构建项目.然而,我们可能会发现这些插件还不够,我们必须开发自己的插件.

幸运的是,Maven提供了一些有用的工具来帮助我们完成这个过程.

在本文中,我们将逐步展示如何从头创建Maven插件.

我们还将展示如何在项目中使用它,以及如何为它创建文档.

## 2. 创建插件

在本文中,我们将开发一个名为counter-maven-plugin的插件,该插件将计算项目包含的依赖项的数量.当我们为插件选择名称时,遵循Maven推荐的插件命名约定非常重要.

接下来我们需要做的就是创建一个Maven项目.在pom.xml中我们将定义插件的groupId,artifactId和version:

```xml

<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

  <modelVersion>4.0.0</modelVersion>
  <groupId>cn.tuyucheng.taketoday</groupId>
  <artifactId>counter-maven-plugin</artifactId>
  <packaging>maven-plugin</packaging>
  <version>1.0.0</version>

  <name>counter-maven-plugin Maven Mojo</name>
  <url>http://maven.apache.org</url>

  <properties>
    <maven.compiler.target>17</maven.compiler.target>
    <maven.compiler.source>17</maven.compiler.source>
  </properties>
</project>
```

**注意,我们将packaging设置为maven-plugin**.

在本例中,我们手动创建了该项目,但也可以使用maven-archetype-mojo:

```
mvn archetype:generate 
  -DgroupId=cn.tuyucheng.taketoday
  -DartifactId=counter-maven-plugin 
  -Dversion=1.0.0
  -DarchetypeGroupId=org.apache.maven.archetypes 
  -DarchetypeArtifactId=maven-archetype-mojo
```

进行此操作时,我们应该更新依赖的默认版本,以使用最新版本.

## 3. 创建Mojo

现在需要创造我们的第一个mojo了.**Mojo是一个Java类,代表我们的插件将执行的goal.一个插件包含一个或多个mojo**.

我们的mojo将负责计算一个项目的依赖数量.

### 3.1 添加依赖

在创建mojo之前,我们需要向pom.xml添加一些依赖:

```
<dependencies>
  <dependency>
    <groupId>org.apache.maven</groupId>
    <artifactId>maven-plugin-api</artifactId>
    <version>3.8.4</version>
  </dependency>
  <dependency>
    <groupId>org.apache.maven.plugin-tools</groupId>
    <artifactId>maven-plugin-annotations</artifactId>
    <version>3.6.2</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>org.apache.maven</groupId>
    <artifactId>maven-project</artifactId>
    <version>2.2.1</version>
  </dependency>
</dependencies>
```

**maven-plugin-api依赖是必需的,它包含创建mojo所需的类和接口**.maven-plugin-annotations依赖可以方便地在我们的类中使用注解.maven-project依赖允许我们访问包含插件的项目的相关信息.

### 3.2 创建Mojo类

mojo必须实现Mojo接口.在我们的例子中,我们将继承AbstractMojo,因此我们只需要实现execute()方法:

```java

@Mojo(name = "dependency-counter", defaultPhase = LifecyclePhase.COMPILE)
public class DependencyCounterMojo extends AbstractMojo {
  // ...
}
```

正如我们所见,dependency-counter是goal的名称.另一方面,默认情况下,我们将其附加到compile阶段,因此在使用此goal时,不必指定阶段.

要访问项目信息,我们必须添加一个MavenProject作为参数:

```
@Parameter(defaultValue = "${project}", required = true, readonly = true)
MavenProject project;
```

创建上下文时,Maven将注入此对象.

此时,我们能够实现execute()方法并计算项目的依赖数量:

```
public void execute() throws MojoExecutionException, MojoFailureException {
  List<Dependency> dependencies = project.getDependencies();
  long numDependencies = dependencies.stream().count();          
  getLog().info("Number of dependencies: " + numDependencies);
}
```

getLog()方法提供对Maven日志的访问.AbstractMojo已经处理了它的生命周期.

### 3.3 添加参数

我们之前添加的参数是只读的,用户无法配置.而且,它是由Maven注入的,所以我们可以说它有点特别.

在本节中,我们将添加一个参数,用户可以在其中指定要计算的依赖的范围.

因此,让我们在mojo中创建一个scope参数:

```
@Parameter(property = "scope")
String scope;
```

我们只设置了property属性.它允许我们通过命令行或pom属性设置此属性.对于其他属性,我们可以使用默认值.

现在,我们将修改execute()方法以使用此参数,并在计数时过滤依赖:

```
public void execute() throws MojoExecutionException, MojoFailureException {
  List<Dependency> dependencies = project.getDependencies();
  long numDependencies = dependencies.stream()
    .filter(d -> (scope == null || scope.isEmpty()) || scope.equals(d.getScope()))
    .count();          
  getLog().info("Number of dependencies: " + numDependencies);
}
```

## 4. 测试插件

我们已经完成了插件的开发.让我们测试一下它是否有效.

首先,我们必须在local repository中安装插件:

```
mvn clean install
```

在接下来的部分中,我们将首先看到如何从命令行运行插件.然后,我们还将介绍如何在Maven项目中使用它.

### 4.1 执行我们的插件

我们可以通过指定插件的完全限定名,在命令行中运行插件的goal:

```
mvn groupId:artifactId:version:goal
```

在我们的例子中,我们的命令如下:

```
mvn cn.tuyucheng.taketoday:counter-maven-plugin:1.0.0:dependency-counter
```

但是,如果我们遵循了本教程开头提到的插件命名约定,Maven将解析插件的前缀,我们可以缩短命令:

```
mvn counter:dependency-counter
```

注意,这个命令使用的是最新版本的插件.此外,请记住,我们必须将groupId添加到setting.xml(在maven安装目录的conf文件夹下)中的pluginGroups标签中,maven也会搜索该group:

```
<pluginGroups>
  <pluginGroup>cn.tuyucheng.taketoday</pluginGroup>
</pluginGroups>
```

如果我们检查输出,我们可以看到插件计算了pom.xml中的依赖项数量.

<img src="../asserts/customplugin.png">

我们还可以通过命令行属性设置scope参数:

```
mvn counter:dependency-counter -Dscope=test
```

请注意,scope名称是我们在mojo中参数的property属性中定义的名称.

### 4.2 在项目中使用我们的插件

现在让我们在一个项目中使用我们的插件来测试它.

我们将创建一个非常简单的Maven项目,其中包含一些依赖,我们的插件将计算这些依赖:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>cn.tuyucheng.taketoday</groupId>
  <artifactId>usage-example</artifactId>
  <version>1.0.0</version>
  <packaging>pom</packaging>

  <dependencies>
    <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-lang3</artifactId>
      <version>3.12.0</version>
    </dependency>
    <dependency>
      <groupId>org.junit.vintage</groupId>
      <artifactId>junit-vintage-engine</artifactId>
      <version>${junit-jupiter.version}</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>cn.tuyucheng.taketoday</groupId>
        <artifactId>counter-maven-plugin</artifactId>
        <version>1.0.0</version>
        <executions>
          <execution>
            <goals>
              <goal>dependency-counter</goal>
            </goals>
          </execution>
        </executions>
        <configuration>
          <scope>test</scope>
        </configuration>
      </plugin>
    </plugins>
  </build>

  <properties>
    <junit-jupiter.version>5.8.1</junit-jupiter.version>
  </properties>
</project>
```

我们需要做的是将我们的插件添加到build标签中.我们必须明确设置要运行dependency-counter goal,如上所示.

请注意,我们已经在configuration标签中指定了scope参数.此外,我们还没有指定任何阶段,因为我们的mojo默认附加到编译阶段.

现在,我们只需要执行编译阶段来执行我们的插件:

```
mvn clean compile
```

我们的插件将打印测试范围依赖的数量:

<img src="../asserts/customplugin2.png">

在本文中,我们不讨论如何为插件编写单元测试或集成测试,但Maven提供了一些机制.

## 5. 添加文档

**当我们创建Maven插件时,生成文档以便于其他人使用是很重要的**.

我们将简要介绍如何使用maven-plugin-plugin生成此文档.

maven-plugin-plugin已经包含在项目中,但我们将更新它以使用最新版本.

此外,我们也将对maven-site-plugin进行更新:

```
<build>
  <pluginManagement>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-plugin-plugin</artifactId>
        <version>3.6.2</version>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-site-plugin</artifactId>
        <version>3.8.2</version>
      </plugin>
    </plugins>
  </pluginManagement>
</build>
```

**然后,我们必须确保已经将javadoc添加到Mojo类中,并在pom.xml中添加一些元数据**:

```
<organization>
  <name>Baeldung</name>
  <url>https://www.baeldung.com/</url>
</organization>
```

之后,我们需要在pom.xml中添加一个reporting标签:

```
<reporting>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-plugin-plugin</artifactId>
      <reportSets>
        <reportSet>
          <reports>
            <report>report</report>
          </reports>
        </reportSet>
      </reportSets>
    </plugin>
  </plugins>
</reporting>
```

最后,我们将使用maven site命令生成文档:

```
mvn site
```

在target文件夹中,我们可以找到包含生成的所有HTML文件的site目录.plugin-info.html包含该插件的文档:

<img src="../asserts/customplugin3.png">

打开该html页面,我们可以看到插件的详细信息:

<img src="../asserts/customplugin4.png">

## 6. 总结

在本文中,我们展示了如何创建Maven插件.我们首先实现了一个简单的插件,我们看到了一个典型的Maven插件项目结构.然后,我们介绍了Maven提供的帮助我们开发插件的一些工具.
