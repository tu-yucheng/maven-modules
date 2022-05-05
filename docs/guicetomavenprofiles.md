## 1. 概述

Maven profiles可用于创建自定义的构建配置,比如针对某一级别的测试粒度或特定的部署环境.

在本文中,我们将介绍如何使用Maven profiles.

## 2. 基础例子

通常,当我们运行mvn package时,也会执行单元测试.但是,如果我们想快速打包工件并运行它,看看它是否有效呢?

首先,我们将创建一个no-tests profile来设置maven.test.skip为true:

```
<profile>
  <id>no-tests</id>
  <properties>
    <maven.test.skip>true</maven.test.skip>
  </properties>
</profile>
```

接下来,我们将通过运行mvn package -Pno-tests命令来执行profile.现在创建工件并跳过测试.在本例中,mvn package -Dmaven.test.skip命令会更容易.

然而,这只是对Maven profiles的介绍.让我们来看一些更复杂的设置.

## 3. 声明Profiles

在上一节中,我们了解了如何创建一个profile.**我们可以通过提供唯一的ID来配置任意多个profile**.

比如说,我们想创建一个只运行集成测试的profile,以及一组突变测试的profile.

我们将首先为pom.xml中的每一个指定一个id:

```
<profiles>
  <profile>
    <id>integration-tests</id>
  </profile>
  <profile>
    <id>mutation-tests</id>
  </profile>
</profiles>
```

在每个profile元素中,**我们可以配置许多元素,比如dependencies, plugins, resources, finalName**.

因此,对于上面的例子,我们可以分别为集成测试和变异测试添加插件及其依赖项.

将测试分离到profiles中可以使默认构建更快,比如只关注单元测试.

### 3.1 Profiles作用域

现在,我们只是把这些profiles放在我们的pom.xml文件中,只是在我们的项目声明它们.

但是,在Maven 3中,我们实际上可以将profile添加到三个位置中的任意一个:

1. 特定于项目的profiles在项目的pom.xml中配置.
2. 特定于用户的profiles在setting.xml中配置.
3. 全局profiles在setting.xml中配置.

请注意,Maven 2支持四个位置,但这在Maven 3中被删除.

**我们尽可能地尝试在pom.xml中配置profiles**.原因是我们希望在开发机器和构建机器上都使用profile.使用setting.xml更难,也更容易出错,因为我们必须自己在构建环境中也配置它.

## 4. 激活Profiles

创建一个或多个profile后,我们可以开始使用它们,或者换句话说,激活它们.

### 4.1 查看哪些Profiles处于激活状态

我们可以使用help:active-profiles goal查看哪些profiles在默认构建中处于激活状态:

```
mvn help:active-profiles
```

实际上,由于我们还没有激活任何东西,我们得到空:

```
The following profiles are active:
```

我们马上激活它们.但是很快,另一种查看激活内容的方法是在pom.xml中包含maven-help-plugin.并将active-profiles goal与compile phase联系起来:

```
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-help-plugin</artifactId>
      <version>3.2.0</version>
      <executions>
        <execution>
          <id>show-profiles</id>
          <phase>compile</phase>
          <goals>
            <goal>active-profiles</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

现在,让我们开始使用它们,我们来看看几个不同的方法.

### 4.2 使用-P

实际上,我们一开始已经看到了一种方法,**那就是我们可以用-P参数激活Profile**.

因此,让我们从启用integration-tests profile开始:

```
mvn package -P integration-tests
```

如果我们使用maven-help-plugin或mvn help:active-profiles -P integration-tests命令验证激活的Profiles,我们将得到以下结果:

```
The following profiles are active:

 - integration-tests (source: cn.tuyucheng.taketoday:maven-profiles:1.0.0)
```

如果我们想同时激活多个profile,我们使用逗号分隔的profiles列表:

```
mvn package -P integration-tests,mutation-tests
```

### 4.3 默认激活

如果我们总是想执行一个profile,我们可以在默认情况下激活一个profile:

```
<profile>
  <id>integration-tests</id>
  <activation>
    <activeByDefault>true</activeByDefault>
  </activation>
</profile>
```

然后,我们可以在不指定profile的情况下运行mvn package,并且可以验证integration-test profile是否处于激活状态.

**但是,如果我们运行Maven命令并启用另一个profile,那么默认激活的profile将被跳过**.因此,当我们运行mvn package -P mutation-tests,只有mutation-tests profile是激活的.

当我们以其他方式激活时,默认激活的profile也会被跳过,我们将在接下来的部分中看到.

### 4.4 基于属性

我们可以在命令行上激活profile.然而,如果自动激活,有时会更方便.**例如,我们可以基于一个-D的系统属性**:

```
<profile>
  <id>active-on-property-environment</id>
  <activation>
    <property>
      <name>environment</name>
    </property>
  </activation>
</profile>
```

现在,我们使用mvn package -Denvironment命令激活profile.

如果属性不存在,也可以激活profile:

```
<property>
  <name>!environment</name>
</property>
```

或者,如果属性具有特定值,我们可以激活profile:

```
<property>
  <name>environment</name>
  <value>test</value>
</property>
```

我们现在可以使用mvn package -Denvironment=test命令来运行profile.

最后,如果属性的值不是指定值,我们可以激活profile:

```
<property>
  <name>environment</name>
  <value>!test</value>
</property>
```

### 4.5 基于JDK版本

另一个选项是基于机器上运行的JDK版本启用profile.在这种情况下,如果JDK版本以11开头,我们希望启用profile:

```
<profile>
  <id>active-on-jdk-11</id>
  <activation>
    <jdk>11</jdk>
  </activation>
</profile>
```

### 4.6 基于操作系统

或者,我们可以根据一些操作系统信息激活profile.

如果我们不确定,我们可以首先使用mvn enforcer:display-info命令,在电脑上输出以下提示:

```
[INFO] Maven Version: 3.8.4
[INFO] JDK Version: 17.0.1 normalized as: 17.0.1
[INFO] Java Vendor: Oracle Corporation
[INFO] OS Info: Arch: amd64 Family: windows Name: windows 10 Version: 10.0
```

之后,我们可以配置仅在Windows 10上激活的profile:

```
<profile>
  <id>active-on-windows-10</id>
  <activation>
    <os>
      <name>windows 10</name>
      <family>Windows</family>
      <arch>amd64</arch>
      <version>10.0</version>
    </os>
  </activation>
</profile>
```

### 4.7 基于文件

另一个选项是在文件存在或不存在时运行profile.

所以,让我们创建一个test profile,它只在testreport.html不存在时执行:

```
<activation>
  <file>
    <missing>target/testreport.html</missing>
  </file>
</activation>
```

## 5. 停用Profile

我们已经看到了许多激活Profile的方法,但有时我们也需要禁用它们.

**要禁用Profile,我们可以使用"!"或者"-"**.

因此,为了禁用active-on-jdk-11 Profile,我们执行mvn compile -P -active-on-jdk-11命令.

## 6. 总结

在本文中,我们了解了如何使用Maven Profile,以便创建不同的构建配置.

当我们需要Profiles时,这些Profiles有助于执行构建特定的元素.这优化了我们的构建过程,有助于向开发人员提供更快的反馈.
