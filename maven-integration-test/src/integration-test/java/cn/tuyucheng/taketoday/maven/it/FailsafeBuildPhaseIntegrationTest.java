package cn.tuyucheng.taketoday.maven.it;


import org.junit.Test;

import static org.junit.Assert.assertTrue;

class FailsafeBuildPhaseIntegrationTest {

  @Test
  public void whenTestExecutes_thenPreAndPostIntegrationBuildPhasesAreExecuted() {
    assertTrue(true);
  }
}
