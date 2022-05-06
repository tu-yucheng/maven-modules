## 1. 概述

通常,我们在Maven构建期间使用Maven surefire plugin执行测试.

本文将介绍如何使用该插件运行单个测试类或测试方法.

## 2. 问题简介

Maven surefire插件易于使用.它只有一个goal,就是test.

因此,在默认配置下,我们可以通过命令mvn test执行项目中的所有测试.

有时,我们可能想要执行一个测试类,甚至一个测试方法.

在本文中,我们将以JUnit 5作为测试工具,介绍如何实现它.

## 3. 项目案例

为了以更直观的方式显示测试结果,让我们创建两个简单的测试类:

```java
class TheFirstUnitTest {
  private static final Logger logger = LoggerFactory.getLogger(TheFirstUnitTest.class);

  @Test
  @DisplayName("whenTestCase_thenPass")
  void whenTestCase_thenPass() {
    logger.info("Running a dummyTest");
  }
}

class TheSecondUnitTest {
  private static final Logger logger = LoggerFactory.getLogger(TheSecondUnitTest.class);

  @Test
  @DisplayName("whenTestCase1_thenPrintTest1_1")
  void whenTestCase1_thenPrintTest1_1() {
    logger.info("Running When Case1: test1_1");
  }

  @Test
  @DisplayName("whenTestCase1_thenPrintTest1_2")
  void whenTestCase1_thenPrintTest1_2() {
    logger.info("Running When Case1: test1_2");
  }

  @Test
  @DisplayName("whenTestCase1_thenPrintTest1_3")
  void whenTestCase1_thenPrintTest1_3() {
    logger.info("Running When Case1: test1_3");
  }

  @Test
  @DisplayName("whenTestCase2_thenPrintTest2_1")
  void whenTestCase2_thenPrintTest2_1() {
    logger.info("Running When Case2: test2_1");
  }
}
```

在第一个类中,我们只有一个测试方法.然而,第二个单元测试包含四个测试方法.我们所有的方法名称都遵循"when...then..."模式.

为了简单起见,我们让每个测试方法输出一行代码,指示正在调用该方法.

现在,如果我们运行mvn test,将执行所有测试.

接下来,我们告诉Maven只执行指定的测试.

## 4. 执行单个测试类

Maven surefire插件提供了一个"test"参数,我们可以使用它来指定要执行的测试类或方法.

**如果我们想要执行单个测试类,我们可以执行以下命令:mvn test -DTest="测试类名"**.

例如,我们可以将-Dtest="TheFirstUnitTest"传递给mvn命令,以仅执行TheFirstUnitTest:

```
$ mvn test -Dtest="TheFirstUnitTest"
...
[INFO] Scanning for projects...
...
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running cn.tuyucheng.taketoday.runasingletest.TheFirstUnitTest
17:10:35.351 [main] INFO cn.tuyucheng.taketoday.runasingletest.TheFirstUnitTest - Running a dummyTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.053 s - in cn.tuyucheng.taketoday.runasingletest.TheFirstUnitTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```

如输出所示,只执行我们传递给"test"参数的测试类.

## 5. 执行单一测试方法

此外，我们可以通过向mvn命令传递-Dtest="TestClassName#TestMethodName",让Maven surefire插件执行一个测试方法.

现在,我们执行TheSecondUnitTest类中的whenTestCase2_thenPrintTest2_1()方法:

```
$ mvn test -Dtest="TheSecondUnitTest#whenTestCase2_thenPrintTest2_1"    
...
[INFO] Scanning for projects...
...
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running cn.tuyucheng.taketoday.runasingletest.TheSecondUnitTest
17:22:07.063 [main] INFO ...TheSecondUnitTest - Running When Case2: test2_1
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.057 s - in cn.tuyucheng.taketoday.runasingletest.TheSecondUnitTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
...
```

正如我们所见,这一次,我们只执行了指定的测试方法.

## 6. 更多关于test参数的信息

到目前为止,我们已经展示了如何通过相应地提供test参数值来执行单个测试类或测试方法.

实际上,Maven surefire插件允许我们以不同的格式设置test参数的值,以便灵活地执行测试.

接下来,我们列出一些常用的格式:

+ 按名称执行多个测试类: -Dtest="TestClassName1, TestClassName2, TestClassName3..."
+ 按名称模式执行多个测试类: -Dtest="\*ServiceUnitTest"或者-Dtest="The\*UnitTest, Controller\*Test"
+ 按名称指定多个测试方法: -Dtest="ClassName#method1+method2"
+ 按名称模式指定多个方法名称: Dtest="ClassName#whenSomethingHappens_\*"

最后,让我们再看一个例子.

假设我们只想执行TheSecondUnitTest类中的所有"WhentTestCase1..."方法.

因此,按照我们上面讲解的,我们可以使用-Dtest="TheSecondUnitTest#whenTestCase1\*"实现这个目的:

```
$ mvn test -Dtest="TheSecondUnitTest#whenTestCase1*"
...
[INFO] Scanning for projects...
...
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running cn.tuyucheng.taketoday.runasingletest.TheSecondUnitTest
17:51:04.973 [main] INFO ...TheSecondUnitTest - Running When Case1: test1_1
17:51:04.979 [main] INFO ...TheSecondUnitTest - Running When Case1: test1_2
17:51:04.980 [main] INFO ...TheSecondUnitTest - Running When Case1: test1_3
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.055 s - in cn.tuyucheng.taketoday.runasingletest.TheSecondUnitTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```

正如我们所期望的,只有三个与指定的名称模式匹配的测试方法被执行.

## 7. 总结

Maven surefire插件提供了一个test参数.它允许我们在选择要执行的测试时有很大的灵活性.

在本文中,我们讨论了一些常用的test参数值格式.

此外,我们还通过案例说明了如何使用Maven仅运行指定的测试类或测试方法.
