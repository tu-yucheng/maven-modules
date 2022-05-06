## 1. 概述

Maven是Java领域最流行的构建工具,而集成测试是开发过程中必不可少的一部分.因此,使用Maven配置和执行集成测试是一种自然选择.

在本文中,我们将讨论使用Maven进行集成测试以及将集成测试与单元测试分离的多种不同方法.

## 2. 准备

为了使演示代码更接近真实项目,我们构建一个JAX-RS应用程序.该应用程序在执行集成测试之前部署到服务器上,之后被拆除.

### 2.1 Maven配置

我们将围绕Jersey(JAX-RS的参考实现)构建REST应用程序.该实现需要几个依赖:

```
<dependency>
  <groupId>org.glassfish.jersey.containers</groupId>
  <artifactId>jersey-container-servlet-core</artifactId>
  <version>2.27</version>
</dependency>
<dependency>
  <groupId>org.glassfish.jersey.inject</groupId>
  <artifactId>jersey-hk2</artifactId>
  <version>2.27</version>
</dependency>
```

我们将使用Jetty Maven插件来设置测试环境.该插件在Maven构建生命周期的pre-integration-test阶段启动Jetty服务器,然后在post-integration-test phase阶段停止.

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

当Jetty服务器启动时,它将监听端口8999.stopKey和stopPort配置仅由插件的stop goal使用,从我们的角度来看,它们并不重要.

另一件需要注意的事情是,我们必须在pom.xml中设置packaging为war,否则Jetty插件无法启动服务器:

```
<packaging>war</packaging>
```

### 2.2 创建REST应用程序

应用程序端点非常简单-当GET请求到达上下文根目录时返回欢迎消息:

```java

@Path("/")
public class RestEndpoint {
  @GET
  public String hello() {
    return "Welcome to Baeldung!";
  }
}
```

下面是我们向Jersey注册endpoint类的方式:

```java
public class EndpointConfig extends ResourceConfig {
  public EndpointConfig() {
    register(RestEndpoint.class);
  }
}
```

为了让Jetty服务器知道我们的REST应用程序,我们可以使用web.xml部署描述符:

```xml

<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
  <servlet>
    <servlet-name>rest-servlet</servlet-name>
    <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
    <init-param>
      <param-name>javax.ws.rs.Application</param-name>
      <param-value>cn.tuyucheng.taketoday.maven.it.EndpointConfig</param-value>
    </init-param>
  </servlet>
  <servlet-mapping>
    <servlet-name>rest-servlet</servlet-name>
    <url-pattern>/*</url-pattern>
  </servlet-mapping>
</web-app>
```

web.xml必须放在目录/src/main/webapp/WEB-INF中,才能被服务器识别.

### 2.3 客户端测试代码

以下部分中的所有测试类都包含一个方法:

```
@Test
public void whenSendingGet_thenMessageIsReturned() throws IOException {
  String url = "http://localhost:8999";
  URLConnection connection = new URL(url).openConnection();
  try (InputStream response = connection.getInputStream();
    Scanner scanner = new Scanner(response)) {
      String responseBody = scanner.nextLine();
      assertEquals("Welcome to Baeldung!", responseBody);
  }
}
```

正如我们所看到的,这个方法除了向我们之前设置的web应用程序发送GET请求并验证响应之外什么都不做.

## 3. 集成测试

关于集成测试,需要注意的一点是,测试方法通常需要相当长的时间才能运行.

因此,我们应该将集成测试从默认的构建生命周期中排除,防止它们在每次构建项目时延长整个过程.

**分离集成测试的一种方便方法是使用profiles.这种配置使我们能够仅在必要时执行集成测试-通过指定合适的profil**e.

在接下来的部分中,我们将使用profile配置所有集成测试.

## 4. 使用Failsafe创建测试

运行集成测试的最简单方法是使用Maven failsafe插件.

默认情况下,Maven surefire插件在test阶段执行单元测试,而failsafe插件在integration-test阶段运行集成测试.

我们可以用不同的命名模式来命名测试类,以便这些插件测试相应的测试类.

**surefire和failsafe强制执行的默认命名约定是不同的,因此我们只需要遵循这些约定来隔离单元测试和集成测试**.

surefire插件的执行包括名称以Test开头或以Test,Tests或TestCase结尾的所有类.相比之下,failsafe插件在名称以IT开头或IT或ITCase结尾的类中执行测试方法.

让我们使用默认配置将failsafe插件添加到pom:

```
<profile>
  <id>failsafe</id>
  <build>
    <plugins>
      <plugin>
        <artifactId>maven-failsafe-plugin</artifact
        <version>2.22.0</version
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
</profile>
```

通过上述配置,将在integration-test阶段执行以下测试方法:

```
public class RestIT {
  // test method shown in subsection 2.3
}
```

由于Jetty服务器在pre-integration-test阶段启动,在post-integration-test阶段关闭,执行以下命令我们可以看到测试通过:

```
mvn verify -Pfailsafe
```


