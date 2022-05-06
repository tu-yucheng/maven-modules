## 1. 概述

在典型的测试驱动开发中,我们的目标是编写大量低级别的单元测试,这些测试可以快速地独立运行和设置.此外,依赖于外部系统的高级集成测试也很少,例如,设置服务器或数据库.不出所料,这些通常既耗费资源又耗费时间.

**因此,这些测试主要需要一些pre-integration设置和post-integration清理,以实现优雅的终止.因此,需要区分这两种类型的测试,并能够在构建过程中分别运行它们**.

在本文中,我们将比较在典型Maven构建中运行各种类型测试最常用的Surefire和Failsafe插件.

## 2. Surefire插件

Surefire插件属于Maven核心插件,运行应用程序的单元测试.

项目pom默认包含此插件,但我们也可以显式配置它:

```
<build>
  <pluginManagement>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.0.0-M5</version>
        ....
      </plugin>
    </plugins>
  </pluginManagement>
</build>
```

插件绑定到默认生命周期的test阶段.因此,我们用命令执行它:

```
mvn clean test
```

这将运行我们项目中的所有单元测试,由于Surefire插件与test阶段绑定,**因此在任何测试失败的情况下,构建都会失败,并且在构建过程中不会执行其他阶段**.

或者,我们可以修改插件配置来运行集成测试和单元测试.然而,对于集成测试来说,这可能不是理想的行为,因为集成测试可能需要在测试执行之前进行一些环境设置,以及在测试执行之后进行一些清理.

Maven提供了另一个插件,正是为了这个目的.

## 3. Failsafe插件

Failsafe插件旨在运行项目中的集成测试.

### 3.1 配置

首先,让我们在项目pom中配置:

```
<plugin>
  <artifactId>maven-failsafe-plugin</artifactId>
  <version>3.0.0-M5</version>
  <executions>
    <execution>
      <goals>
        <goal>integration-test</goal>
        <goal>verify</goal>
      </goals>
      ....
    </execution>
  </executions>
</plugin>
```

在这里,插件的goal绑定到构建周期的integration-test和verify阶段,以便执行集成测试.

现在,让我们从命令行执行verify阶段:

```
mvn clean verify
```

**这将运行所有集成测试,但如果在integration-test阶段有任何测试失败,插件不会立即使构建失败**.

相反mMaven仍然执行post-integration-test阶段.因此,作为post-integration-test阶段的一部分,我们仍然可以执行任何环境的清理.构建过程的后续verify阶段会报告任何测试失败.

## 3.2 例子

在我们的示例中,我们将配置Jetty服务器,使其在运行集成测试之前启动,在测试执行之后停止.

首先,让我们将Jetty插件添加到pom中:

```
<plugin>
  <groupId>org.eclipse.jetty</groupId>
  <artifactId>jetty-maven-plugin</artifactId>
  <version>${jetty.version}</version>
  <configuration>
    <httpConnector>
      <port>8999</port>
    </httpConnector>
    <stopKey>quit</stopKey>
    <stopPort>9000</stopPort>
  </configuration>
  <executions>
    <execution>
      <id>start-jetty</id>
      <phase>pre-integration-test</phase>
      <goals>
        <goal>start</goal>
      </goals>
    </execution>
    <execution>
      <id>stop-jetty</id>
      <phase>post-integration-test</phase>
      <goals>
        <goal>stop</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

在这里,我们添加了在pre-integration-test和post-integration-test阶段分别启动和停止Jetty服务器的配置.

现在,让我们再次执行集成测试.

Jetty服务器在集成测试执行前启动.为了演示,我们进行了一次失败的集成测试,但这不会立即导致构建失败.post-integration-test阶段在测试执行之后执行,服务器在构建失败之前停止.

**相比之下,如果我们使用Surefire插件来运行这些集成测试,构建将在integration-test阶段停止,而不执行任何所需的清理**.

为不同类型的测试使用不同插件的另一个好处是不同配置之间的分离.这提高了项目构建的可维护性.
