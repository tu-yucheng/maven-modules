## 1. 概述

在本文中,我们将了解如何使用从Java应用程序中访问Maven的pom.xml中定义的变量.

## 2. 插件配置

在本例中,我们将使用Maven Properties Plugin.

这个插件将绑定到generate-resources阶段,并在编译过程中创建一个包含我们定义在pom.xml中的属性的文件.然后,我们可以在运行时读取该文件以获取值.

让我们首先在我们的项目中加入插件:

```
<plugins>
  <plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>properties-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
      <execution>
        <phase>generate-resources</phase>
        <goals>
          <goal>write-project-properties</goal>
        </goals>
        <configuration>
          <outputFile>${project.build.outputDirectory}/properties-from-pom.properties</outputFile>
        </configuration>
      </execution>
    </executions>
  </plugin>
```

接下来,我们将为变量提供一个值.此外,因为我们在pom.xml中定义了它们,我们也可以使用Maven占位符:

```
<properties> 
  <name>${project.name}</name> 
  <my.awesome.property>property-from-pom</my.awesome.property> 
</properties>
```

## 3. 读取属性

现在是时候从配置中访问我们的属性了.让我们创建一个简单的类,从类路径上的文件中读取属性:

```java
public class PropertiesReader {

  private Properties properties;

  public PropertiesReader(String propertyFileName) {
    InputStream is = getClass().getClassLoader().getResourceAsStream(propertyFileName);
    this.properties = new Properties();
    this.properties.load(is);
  }

  public String getProperty(String propertyName) {
    return this.properties.getProperty(propertyName);
  }
}
```

接下来,我们只需编写一个测试用例来读取我们的值:

```java
public class PropertiesReaderUnitTest {
  
  @Test
  @DisplayName("givenPomProperties_whenPropertyRead_thenPropertyReturned")
  public void givenPomProperties_whenPropertyRead_thenPropertyReturned() throws IOException {
    PropertiesReader reader = new PropertiesReader("properties-from-pom.properties");
    String property = reader.getProperty("my.awesome.property");
    assertEquals("property-from-pom", property);
  }
}
```