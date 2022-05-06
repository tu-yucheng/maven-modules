## 1. 概述

本文介绍failsafe插件,它是Maven构建工具的核心插件之一.

## 2. Goals

failsafe插件用于项目的集成测试.它有两个目标:

+ integration-test : 运行集成测试;默认情况下,此goal绑定到integration-test阶段
+ verify - 验证集成测试是否通过;默认情况下,此goal绑定到verify阶段

## 3. 执行Goal

这个插件在测试类中运行方法,就像surefire插件一样.我们可以用相似的方式配置这两个插件.然而,它们之间有一些关键的区别.

首先,与Super pom中包含的surefile不同,failsure插件必须在pom中明确指定及其goal.成为构建生命周期的一部分:

```
<build>
  <plugins>
    <plugin>
      <artifactId>maven-failsafe-plugin</artifactId>
      <version>2.22.0</version>
      <configuration>
        <includes>
          <include>**/*</include>
        </includes>
        <groups>Integration</groups>
      </configuration>
      <executions>
        <execution>
          <goals>
            <goal>integration-test</goal>
            <goal>verify</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

其次,failsafe插件使用不同的goal运行和验证测试.integration-test的测试失败不会直接导致构建失败,从而允许执行post-integration-test阶段,在该阶段执行清理操作.

失败的测试(如果有的话)仅在集成测试环境被正确清除后的verify阶段报告.