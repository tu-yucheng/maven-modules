package cn.tuyucheng.taketoday.version.collision;

import com.google.common.util.concurrent.Futures;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VersionCollisionUnitTest {
  @Test
  public void whenVersionCollisionDoesNotExist_thenShouldCompile() {
    // assertThat(Futures.immediateVoidFuture(), notNullValue());
    assertNotNull(Futures.immediateCancelledFuture());
  }
}