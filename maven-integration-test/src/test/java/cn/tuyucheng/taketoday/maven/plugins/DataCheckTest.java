package cn.tuyucheng.taketoday.maven.plugins;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class DataCheckTest {
  @Test
  public void whenDataObjectIsCreated_thenItIsNotNull() {
    Data data = new Data();
    assertNotNull(data);
  }
}