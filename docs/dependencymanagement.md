## 1. 概述

在本文中,我们将回顾两个重要的Maven标签--dependencyManagement和dependencies.

**这些标签对于多模块项目尤其有用**.

我们将回顾这两个标签的相似性和差异,我们还将研究开发人员在使用它们时可能会导致混淆的一些常见错误.

## 2. 使用

一般来说,在dependencies标签中定义依赖时,我们使用dependencyManagement标签来避免重复version和scope标签.通过这种方式,所需的依赖关系在一个中心pom文件中声明.

### 2.1 dependencyManagement

此标签由一个dependencies标签组成,dependencies标签本身可能包含多个dependency标签.每个依赖项应该至少有三个主要标签:groupId,artifactId和version.我们来看一个例子:

```
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-lang3</artifactId>
      <version>3.12.0</version>
    </dependency>
  </dependencies>
</dependencyManagement>
```

上面的代码只是声明了新的依赖commons-lang3,但并没有真正将其添加到项目依赖列表中.

### 2.2 dependencies

此标签包含一系列dependency标签.每个dependency应该至少有两个主标签,即groupId和artifactId.

让我们来看一个简单的例子:

```
<dependencies>
  <dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
  </dependency>
</dependencies>
```

如果我们之前在pom.xml文件中使用过dependencyManagement标签,则version和scope标签可以隐式继承;

```
<dependencies>
  <dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
  </dependency>
</dependencies>
```

## 3. 相似性

这两个标签都旨在声明某些第三方或子模块依赖关系.它们相辅相成.

事实上,我们通常在dependencies标签之前定义一次dependencyManager标签.这用于在pom文件中声明依赖项.**这个只是一个声明,并没有真正为项目添加依赖**.

我们来看一个添加JUnit依赖的例子:

```
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.13.2</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

如我们在上面的代码中看到的,有一个dependencyManagement标签,它本身包含另一个dependencies标签.

现在我们看看以下代码,它为项目添加了实际的依赖:

```
<dependencies>
  <dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
  </dependency>
</dependencies>
```

因此,当前标签与前一个标签非常相似.它们都将定义一个依赖列表.当然,还有一些小的差异.

两个代码段中都重复了相同的groupId和artifactId标签,它们之间存在着有意义的关联:它们都引用相同的artifact.

正如我们所看到的,我们后面的dependency标签中没有任何version标签.令人惊讶的是,它的语法是有效的,可以毫无问题地解析和编译它.原因很容易猜测:它将使用dependencyManager标签声明的版本.

## 4. 差异

### 4.1 结构不同

**如前所述,这两个标签之间的主要结构差异是继承逻辑**.我们在dependencyManagement标签中定义版本,然后我们可以使用提到的版本,而无需在下一个dependency标签中指定它.

### 4.2 行为不同

**dependencyManagement仅仅是一个声明,没有实际添加任何依赖**
.需要使用的依赖以后必须由dependencies标签提供.正是dependencies标签添加了真正的依赖.在上面的示例中,dependencyManagement标签不会将junit库添加到任何作用域中.它只是对未来dependencies标签的声明.

## 5. 真实案例

几乎所有基于Maven的开源项目都使用这种机制.

让我们看看Maven项目本身的一个例子.我们看到了hamcrest-core依赖,它存在于Maven项目中.它首先在dependencyManagement标签中声明,然后由主要的dependencies标签导入:

```
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.hamcrest</groupId>
      <artifactId>hamcrest-core</artifactId>
      <version>2.2</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-core</artifactId>
    <scope>test</scope>
  </dependency>
</dependencies>
```

## 6. 常见用例

此功能的一个非常常见的使用场景是多模块项目.

假设我们有一个由不同模块组成的大项目.每个模块都有自己的依赖,每个开发人员可能会对所使用的依赖使用不同的版本.然后,它可能会导致不同依赖版本的冲突.

**这个问题的简单解决方案肯定是在根pom文件(通常称为"父pom"文件)中使用dependencyManagement标签,然后在子pom文件(子模块)甚至父模块本身(如果适用)中使用dependencies**.

如果我们只有一个模块.虽然这在多模块环境中非常有用,但作为最佳实践,即使在单模块项目中也可以遵循这一原则.这有助于项目的可读性,并使其可以扩展到多模块项目.

## 7. 常见错误

一个常见的错误是只在dependencyManagement部分定义依赖项,而不将其包含在dependencies标签中.在这种情况下,我们将遇到编译或运行时错误,具体取决于所提到的scope.

我们来看一个例子:

```
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-lang3</artifactId>
      <version>3.12.0</version>
    </dependency>
  </dependencies>
</dependencyManagement>
```

上面的pom代码片段.假设我们将在子模块源文件中使用这个库:

```java
public class Main {

  public static void main(String[] args) {
    StringUtils.isBlank(" ");
  }
}
```

由于缺少依赖,此代码将无法编译.

为避免此错误,只需在子模块pom文件中添加以下dependencies标签即可:

```
<dependencies>
  <dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
  </dependency>
</dependencies>
```

## 8. 总结

在本文中,我们比较了Maven的dependencyManagement和dependencies标签.然后,我们回顾了它们的相同点和不同点,并了解它们是如何协同工作的.
