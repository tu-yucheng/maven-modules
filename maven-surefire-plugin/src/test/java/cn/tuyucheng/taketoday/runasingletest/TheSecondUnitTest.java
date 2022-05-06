package cn.tuyucheng.taketoday.runasingletest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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