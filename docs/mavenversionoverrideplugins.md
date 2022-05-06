## 1. 概述

在Maven多模块项目中,**effective pom是合并模块及其父模块中定义的所有配置的结果**.

为了避免模块之间的冗余和重复,我们通常在共享父模块中保留公共配置.然而,如果我们需要在不影响其所有同级模块的情况下为子模块定制配置,则可能会遇到挑战.

在本文中,我们将学习如何覆盖父模块插件配置.

## 2. 默认配置继承

插件配置允许我们跨项目重用通用的构建逻辑.如果父模块有插件,子模块将自动拥有插件,无需额外配置.

为了实现这一点,Maven在元素级别合并xml文件.如果子元素定义了具有不同值的元素,它将替换父元素中的值.让我们看看它的实际效果.

### 2.1 项目结构

首先,让我们定义一个多模块Maven项目进行实验:

```
+ parent
     + child-a
     + child-b
```

假设我们想要配置maven-compiler-plugin,以便在模块之间使用不同的Java版本.让我们将我们的项目配置为使用Java11,但让child-a模块使用Java8.

```
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <source>11</source>
    <target>11</target>
    <maxmem>512m</maxmem>
  </configuration>
</plugin>
```

这里我们指定了另外一个属性maxmem,我们也希望使用它.但是,我们希望child-a有自己的编译插件配置.

因此,让我们将child-a配置为使用Java 8:

```
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <source>1.8</source>
    <target>1.8</target>
  </configuration>
</plugin>
```

现在我们的已经准备完成,接下来看看effective pom.

### 2.2 理解Effective POM

effective pom受到各种因素的影响,如继承,配置文件,外部设置等.为了查看effective pom,让我们从child-a模块下运行mvn help:effective pom:

```
<plugin>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.8.1</version>
  <configuration>
    <source>1.8</source>
    <target>1.8</target>
    <maxmem>512m</maxmem>
  </configuration>
</plugin>
```

如我们配置的,child-a有自己的source和target值.然而,另一个值得注意的是,它还拥有来自其父级的maxmem属性.

**这意味着,如果子pom定义了任何属性,它将使用子pom定义的,否则将使用父pom定义的**.

## 3. 高级配置继承

当我们想要微调合并策略时,我们可以使用属性.这些属性放在我们想要控制的xml标签上.此外,他们将被继承,只影响一级子pom.

### 3.1 使用列表

在上一个例子中,我们看到了如果子pom有不同的值会发生什么.现在,我们将看到当子元素有不同的元素列表时的情况.作为一个例子,让我们看看使用maven-resources-plugin包含多个资源目录.

我们将父pom配置为包含来自parent-resources目录的资源:

```
<plugin>
  <artifactId>maven-resources-plugin</artifactId>
  <configuration>
    <resources>
      <resource>
        <directory>parent-resources</directory>
      </resource>
    </resources>
  </configuration>
</plugin>
```

此时,child-a将从其父级继承此插件配置.但是,假设我们想为child-a定义一个另外的资源目录:

```
<plugin>
  <artifactId>maven-resources-plugin</artifactId>
  <configuration>
    <resources>
      <resource>
        <directory>child-a-resources</directory>
      </resource>
    </resources>
  </configuration>
</plugin>
```

现在,让我们看一下effective pom:

```
<plugin>
  <artifactId>maven-resources-plugin</artifactId>
  <version>3.2.0</version>
  <configuration>
    <resources combine.self="override">
      <resource>
        <directory>child-a-resources</directory>
      </resource>
    </resources>
  </configuration>
</plugin>
```

在这种情况下,整个列表被子配置覆盖.

### 3.2 追加父配置

也许我们希望一些子级使用公共资源目录,并定义其他目录.为此,我们可以在父pom的resources标签中使用combine.children="append"追加父级配置.

```
<resources combine.children="append">
  <resource>
    <directory>parent-resources</directory>
  </resource>
</resources>
```

因此,effective pom将包含以下两种:

```
<plugin>
  <artifactId>maven-resources-plugin</artifactId>
  <version>3.2.0</version>
  <configuration>
    <resources combine.children="append">
      <resource>
        <directory>parent-resources</directory>
      </resource>
      <resource>
        <directory>child-a-resources</directory>
      </resource>
    </resources>
  </configuration>
</plugin>
```

combine属性不能传播到任何嵌套元素.因此,如果resources部分是一个复杂的结构,那么嵌套的元素将使用默认策略进行合并.

### 3.3 覆盖子配置

在前一个例子中,由于parent的combine策略,child不能完全控制最终的pom.child可以通过在resources标签上添加combine.self="override"来覆盖父级配置.

```
<resources combine.self="override">
  <resource>
    <directory>child-a-resources</directory>
  </resource>
</resources>
```

在这种情况下,子pom仍然只使用自己的配置:

```
<plugin>
  <artifactId>maven-resources-plugin</artifactId>
  <version>3.2.0</version>
  <configuration>
    <resources combine.self="override">
      <resource>
        <directory>child-a-resources</directory>
      </resource>
    </resources>
  </configuration>
</plugin>
```

## 4. 不可继承的插件

前面的属性适合稍微调整,但当子级完全不使用父级配置时,它们就不合适了.

为了避免插件被继承,我们可以在父级添加inherited标签:

```
<plugin>
  <inherited>false</inherited>
  <groupId>org.apache.maven.plugins</groupId>
  ...
</plugin>
```

这意味着插件将只应用于父级,而不会被子pom继承.
