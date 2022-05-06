## 1. 概述

Maven多模块项目可能有复杂的依赖关系图.这些可能会产生不寻常的结果,模块之间的相互导入越多.

在本文中,我们将演示如何解决Maven中依赖的版本冲突.

我们将从一个多模块项目开始,在这个项目中,我们故意使用同一依赖的不同版本.然后,我们将看到如何通过排除或依赖管理来防止获得错误版本的依赖.

最后,我们将尝试使用maven-enforcer-plugin,通过禁止使用可传递依赖项,使事情更易于控制.

## 2. 依赖的版本冲突

我们在项目中包含的每个依赖都可能依赖到其他jar包.Maven可以自动引入这些依赖.也称为可传递依赖项.当多个依赖项链接到同一依赖中,但使用不同的版本时,就会发生版本冲突.

因此,我们的应用程序在编译阶段和运行时都可能出现错误.

### 2.1 项目结构

让我们定义一个用于演示的多模块项目.我们的项目由一个maven-version-collision父模块和三个子模块组成:

```
maven-version-collision
    project-a
    project-b
    project-collision
```

project-a和project-b的pom.xml几乎相同.唯一的区别是com.google.guava依赖的版本不同.project-a使用版本22.0:

```
<dependencies>
  <dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>22.0</version>
  </dependency>
</dependencies>
```

但是,project-b使用的是版本29.0-jre:

```
<dependencies>
  <dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>29.0-jre</version>
  </dependency>
</dependencies>
```

第三个模块"project-collision"依赖于以上两个模块:

```
<dependencies>
  <dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>project-a</artifactId>
    <version>1.0.0</version>
  </dependency>
  <dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>project-b</artifactId>
    <version>1.0.0</version>
  </dependency>
</dependencies>
```

那么,哪个版本的guave可以用于project-collision呢

### 2.2 使用特定依赖版本的功能

我们可以通过创建一个简单的测试,使用来自guava的Futures.immediateVoidFuture()方法,找出project-collision使用的guava依赖版本.

```
@Test
public void whenVersionCollisionDoesNotExist_thenShouldCompile() {
  assertThat(Futures.immediateVoidFuture(), notNullValue());
}
```

此方法仅适用于29.0-jre版本.我们从其他模块中继承了该版本的guava,但只有在从project-b获得可传递依赖项时,我们才能编译代码.

### 2.3 版本冲突导致编译错误

根据project-collision模块中依赖项的定义顺序,Maven会返回编译错误:

```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.1:testCompile (default-testCompile) on project project-collision: Compilation failure
[ERROR] /tutorials/maven-all/version-collision/project-collision/src/test/java/cn/tuyucheng/taketoday/version/collision/VersionCollisionUnitTest.java:[12,27] cannot find symbol
[ERROR]   symbol:   method immediateVoidFuture()
[ERROR]   location: class com.google.common.util.concurrent.Futures
```

这是com.google.guava工件版本冲突的结果.默认情况下,对于依赖关系树中同一级别的依赖关系,Maven会选择它找到的第一个库.在我们的例子中,两者com.google.guava依赖在同一高度,选择较旧的版本.

### 2.4 使用maven-dependency-plugin

maven-dependency-plugin是一个非常有用的工具,可以显示所有依赖项及其版本:

```
mvn dependency:tree -Dverbose

[INFO] --- maven-dependency-plugin:2.8:tree (default-cli) @ project-collision ---
[INFO] cn.tuyucheng.taketoday:project-collision:jar:0.0.1-SNAPSHOT
[INFO] +- cn.tuyucheng.taketoday:project-a:jar:1.0.0:compile
[INFO] |  \- com.google.guava:guava:jar:22.0:compile
[INFO] \- cn.tuyucheng.taketoday:project-b:jar:1.0.0:compile
[INFO]    \- (com.google.guava:guava:jar:29.0-jre:compile - omitted for conflict with 22.0)
```

-Dverbose标志显示冲突的依赖.事实上,com.google.guava依赖有两个版本:22.0和29.0-jre.后者是我们希望在project-collsion模块中使用的.

## 3. 从工件中排除可传递依赖项

解决版本冲突的一种方法是从特定工件中移除冲突的可传递依赖项.在我们的案例中,我们不希望使用project-a项目中可传递的依赖项22.0版本的com.google.guava.

因此,我们在可以project-collision的pom中排除它:

```
<dependencies>
  <dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>project-a</artifactId>
    <version>1.0.0</version>
    <exclusions>
      <exclusion>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
      </exclusion>
    </exclusions>
  </dependency>
  <dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>project-b</artifactId>
    <version>1.0.0</version>
  </dependency>
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

现在,当我们运行dependency:tree命令时,我们可以看到它不再存在了:

```
mvn dependency:tree -Dverbose

[INFO] --- maven-dependency-plugin:2.8:tree (default-cli) @ project-collision ---
[INFO] cn.tuyucheng.taketoday:project-collision:jar:1.0.0
[INFO] \- cn.tuyucheng.taketoday:project-b:jar:1.0.0:compile
[INFO]    \- com.google.guava:guava:jar:29.0-jre:compile
```

因此,编译阶段结束时不会出现错误,我们可以使用29.0-jre版本中的类和方法.

## 4. 使用dependencyManagement

Maven的dependencyManagement标签是一种集中依赖管理的机制.它最有用的特性之一是控制用作可传递依赖项的依赖的版本.

考虑到这一点,让我们在父pom中创建dependencyManagement配置:

```
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.google.guava</groupId>
      <artifactId>guava</artifactId>
      <version>29.0-jre</version>
    </dependency>
  </dependencies>
</dependencyManagement>
```

因此,Maven将确保使用29.0-jre版本的guava:

```
mvn dependency:tree -Dverbose

[INFO] --- maven-dependency-plugin:2.8:tree (default-cli) @ project-collision ---
[INFO] cn.tuyucheng.taketoday:project-collision:jar:1.0.0
[INFO] +- cn.tuyucheng.taketoday:project-a:jar:1.0.0:compile
[INFO] |  \- com.google.guava:guava:jar:29.0-jre:compile (version managed from 22.0)
[INFO] \- cn.tuyucheng.taketoday:project-b:jar:1.0.0:compile
[INFO]    \- (com.google.guava:guava:jar:29.0-jre:compile - version managed from 22.0; omitted for duplicate)
```

## 5. 防止意外传递依赖

maven-enforcer-plugin提供了许多内置规则,可以简化多模块项目的管理.其中一个是禁止使用传递依赖的类和方法.

显式依赖声明消除了依赖版本冲突的可能性.让我们将带有该规则的maven-enforcer-plugin添加到父pom中:

```
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-enforcer-plugin</artifactId>
      <version>3.0.0-M3</version>
      <executions>
        <execution>
          <id>enforce-banned-dependencies</id>
          <goals>
            <goal>enforce</goal>
          </goals>
          <configuration>
            <rules>
              <banTransitiveDependencies/>
            </rules>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

因此,如果我们想在project-collision模块中使用我们自己的guava版本,我们必须指定要使用的版本,或者在父pom中设置dependencyManagement.这使我们的项目更加防错,但要求我们在pom.xml中更加明确.

## 6. 总结

在本文中,我们看到了如何解决Maven中依赖的版本冲突.

首先,我们研究了一个多模块项目中的版本冲突案例.

然后,我们展示了如何在pom.xml中排除可传递依赖项.我们研究了如何使用父pom中的dependencyManagement部分控制依赖项版本.

最后,我们尝试maven-enforcer-plugin来禁止使用可传递依赖项,以迫使每个模块控制自己的依赖版本.
