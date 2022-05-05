## 1. 概述

在本文中,我们将介绍使用Maven的super, simplest,和effective pom之间的区别.

## 2. 什么是pom

pom代表项目对象模型(Project Object Model),它是Maven中项目配置的核心.它是一个名为pom.xml的单一配置文件.包含构建项目所需的大部分信息.

**pom文件的作用是描述项目,管理依赖项,并声明有助于Maven构建项目的配置细节**.

## 3. Super POM

为了更容易地理解Super pom,我们可以与Java中的Object类进行类比:Java中的每个类都默认继承了Object类.类似地,在pom的情况下,每个pom继承Super pom.

**Super pom文件定义了所有默认配置.因此,即使是最简单的pom文件形式也会继承Super pom文件中定义的所有配置**.

根据我们使用的Maven版本,Super pom可能略有不同.例如,如果我们安装了Maven,我们可以打开${M2_HOME}/lib/maven-model-builder-版本
.jar文件.如果我们打开这个jar文件,就可以在org/apache/maven/model下找到pom-4.0.0.xml.

在接下来的部分中,我们将介绍3.8.4版本的Super pom配置元素.

### 3.1 Repositories

Maven使用在repositories标签部分下定义的repositories,在Maven构建期间下载所有相关依赖.

让我们看一个例子:

```
<repositories>
  <repository>
    <id>central</id>
    <name>Central Repository</name>
    <url>https://repo.maven.apache.org/maven2</url>
    <layout>default</layout>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
  </repository>
</repositories>
```

### 3.2 Plugin Repositories

默认的插件repository是Maven中央仓库.让我们看看pluginRepository标签是如何定义它的:

```
<pluginRepositories>
  <pluginRepository>
    <id>central</id>
    <name>Central Repository</name>
    <url>https://repo.maven.apache.org/maven2</url>
    <layout>default</layout>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
    <releases>
      <updatePolicy>never</updatePolicy>
    </releases>
  </pluginRepository>
</pluginRepositories>
```

如上所述,snapshots被禁用,updatePolicy被设置为"never".因此,在这种配置下,如果发布新版本,Maven将永远不会自动更新插件.

### 3.3 Build

build配置部分包括构建项目所需的所有信息.

让我们看一个默认build标签的示例:

```
<build>
  <directory>${project.basedir}/target</directory>
  <outputDirectory>${project.build.directory}/classes</outputDirec
  <finalName>${project.artifactId}-${project.version}</finalName>
  <testOutputDirectory>${project.build.directory}/test-classes
  </testOutputDirectory>
  <sourceDirectory>${project.basedir}/src/main/java</sourceDirecto
  <scriptSourceDirectory>${project.basedir}/src/main/scripts
  </scriptSourceDirectory>
  <testSourceDirectory>${project.basedir}/src/test/java
  </testSourceDirectory>
  <resources>
    <resource>
      <directory>${project.basedir}/src/main/resources</directory>
    </resource>
  </resources>
  <testResources>
    <testResource>
      <directory>${project.basedir}/src/test/resources</directory>
    </testResource>
  </testResources>
  <pluginManagement>
    <!-- NOTE: These plugins will be removed from future versions 
        POM -->
    <!-- They are kept for the moment as they are very unlikely to
        with lifecycle mappings (MNG-4453) -->
    <plugins>
      <plugin>
        <artifactId>maven-antrun-plugin</artifactId>
        <version>1.3</version>
      </plugin>
      <plugin>
        <artifactId>maven-assembly-plugin</artifactId>
        <version>2.2-beta-5</version>
      </plugin>
      <plugin>
        <artifactId>maven-dependency-plugin</artifactId>
        <version>2.8</version>
      </plugin>
      <plugin>
        <artifactId>maven-release-plugin</artifactId>
        <version>2.5.3</version>
      </plugin>
    </plugins>
  </pluginManagement>
</build>
```

### 3.4 Reporting

对于reporting,Super pom仅为outputDirectory提供默认值:

```
<reporting>
  <outputDirectory>${project.build.directory}/site</outputDirectory>
</reporting>
```

### 3.5 Profiles

如果我们没有在应用程序级别定义profile,那么将执行默认的build profile.

默认profiles标签如下所示:

```
<profiles>
  <!-- NOTE: The release profile will be removed from future versions of the super POM -->
  <profile>
    <id>release-profile</id>

    <activation>
      <property>
        <name>performRelease</name>
        <value>true</value>
      </property>
    </activation>

    <build>
      <plugins>
        <plugin>
          <inherited>true</inherited>
          <artifactId>maven-source-plugin</artifactId>
          <executions>
            <execution>
              <id>attach-sources</id>
              <goals>
                <goal>jar-no-fork</goal>
              </goals>
            </execution>
          </executions>
        </plugin>
        <plugin>
          <inherited>true</inherited>
          <artifactId>maven-javadoc-plugin</artifactId>
          <executions>
            <execution>
              <id>attach-javadocs</id>
              <goals>
                <goal>jar</goal>
              </goals>
            </execution>
          </executions>
        </plugin>
        <plugin>
          <inherited>true</inherited>
          <artifactId>maven-deploy-plugin</artifactId>
          <configuration>
            <updateReleaseInfo>true</updateReleaseInfo>
          </configuration>
        </plugin>
      </plugins>
    </build>
  </profile>
</profiles>
```

## 4. Simplest POM

**Simplest pom是在Maven项目中声明的pom**.为了声明pom,你需要至少指定以下四个标签:modelVersion,groupId,artifactId和version.**Simplest pom将继承Super
pom的所有配置**.

让我们看看Maven项目所需的必备元素:

```
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>cn.tuyucheng.taketoday</groupId>
  <artifactId>maven-pom-types</artifactId>
  <version>1.0.0</version>
</project>
```

**Maven中pom层次结构的一个主要优点是,我们可以扩展和覆盖从顶层继承的配置**.因此,为了覆盖pom层次结构中给定标签或工件的配置,Maven应该能够唯一地标识相应工件.

## 5. Effective POM

**Effective pom结合了Super pom文件中的所有默认设置和应用程序pom中定义的配置**.当配置元素在应用程序pom.xml中未被重写时,Maven会使用默认值.因此,如果我们从Simplest pom部分获取相同的pom文件示例,我们将看到Effective pom文件将是Simplest和Super pom之间的合并.我们可以从命令行看到:

```
mvn help:effective-pom
```

这也是查看Maven使用的默认值的最佳方式.