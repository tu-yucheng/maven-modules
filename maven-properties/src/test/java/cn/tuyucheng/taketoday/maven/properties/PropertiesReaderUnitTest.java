package cn.tuyucheng.taketoday.maven.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link PropertiesReader}.
 *
 * @author Donato Rimenti
 */
public class PropertiesReaderUnitTest {

  /**
   * Reads a property and checks that's the one expected.
   *
   * @throws IOException if anything goes wrong
   */
  @Test
  @DisplayName("givenPomProperties_whenPropertyRead_thenPropertyReturned")
  public void givenPomProperties_whenPropertyRead_thenPropertyReturned() throws IOException {
    PropertiesReader reader = new PropertiesReader("properties-from-pom.properties");
    String property = reader.getProperty("my.awesome.property");
    assertEquals("property-from-pom", property);
  }
}