## 1. 概述

本文中将介绍Maven的optional标签,以及我们如何使用它来减少Maven项目工件的大小和范围,例如WAR,EAR或JAR.

## 2. 什么是optional

有时我们会创建一个Maven项目,作为其他Maven项目的依赖项.在处理此类项目时,可能需要包含一个或多个仅对该项目功能子集有用的依赖项.

如果最终用户不使用该依赖项子集,项目仍然会通过传递方式引入这些依赖项.这会不必要地扩大用户的项目规模,甚至可能引入与其他项目依赖项冲突的依赖版本.

理想情况下,我们应该将项目的功能子集拆分为自己的模块,因此不会污染项目的其余部分.然而,这并不总是可行的.

为了从主项目中排除这些特殊依赖项,我们可以对它们应用Maven的optional标签.这会迫使任何想要使用这些依赖项的用户显式声明它们.然而,它不会将这些依赖项强制添加到不需要它们的项目中.

## 3. 怎么使用optional

正如我们将要看到的,我们可以将optional元素的值设为true,以使任何Maven依赖项都是可选的.

假设我们有以下pom项目:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>ccn.tuyucheng.taketoday</groupId>
  <artifactId>project-with-optionals</artifactId>
  <version>1.0.0</version>
  <packaging>pom</packaging>

  <dependencies>
    <dependency>
      <groupId>cn.tuyucheng.taketoday</groupId>
      <artifactId>optional-project</artifactId>
      <version>1.0.0</version>
      <optional>true</optional>
    </dependency>
  </dependencies>
</project>
```

在本例中,虽然optional-project被标记为optional,但它仍然是带有optional-project的依赖,就好像optional标签没有作用一样.

为了看见optional标签的效果,我们需要创建一个新项目,该项目依赖于project-with-optionals:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>cn.tuyucheng.taketoday</groupId>
  <artifactId>main-project</artifactId>
  <version>1.0.0</version>
  <packaging>pom</packaging>

  <dependencies>
    <dependency>
      <groupId>cn.tuyucheng.taketoday</groupId>
      <artifactId>projects-with-optionals</artifactId>
      <version>1.0.0</version>
    </dependency>
  </dependencies>
</project>
```

现在,如果我们试图从main-project中引用optional-project,我们会看到optional-project不存在.这是因为optional标签阻止它被传递性地包含.

如果我们在main-project中需要optional-project,我们只需要声明依赖即可.
