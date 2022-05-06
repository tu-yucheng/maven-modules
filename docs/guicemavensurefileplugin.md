## 1. 概述

本文演示surefire插件,它是Maven构建工具的核心插件之一.

## 2. 插件Goal

我们可以使用surefire插件运行项目测试.默认情况下,该插件在target/surefire-reports目录下生成XML报告.

这个插件只有一个test的goal.这个goal绑定到默认构建生命周期的test阶段,命令mvn test将执行它.

## 3. 配置

surefire插件可以与测试框架JUnit和TestNG一起使用.无论我们使用哪种框架,surefire的行为都是一样的.

默认情况下,surefire自动包含名称以Test开头或以Test,Tests或TestCase结尾的所有测试类.

我们可以使用excludes和includes标签更改此配置,但是:

```
<plugin>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>2.22.2</version>
  <configuration>
    <excludes>
      <exclude>DataTest.java</exclude>
    </excludes>
    <includes>
      <include>DataCheck.java</include>
    </includes>
  </configuration>
</plugin>
```

通过这种配置,DataCheck类中的测试用例会被执行,而DataTest中的测试用例不会被执行: