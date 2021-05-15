package org.lefmaroli.configuration;

import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ConfigurationLoaderTest {

  private static String ENV;

  @BeforeAll
  public static void init() {
    ENV = ConfigurationLoader.getEnv();
    ConfigurationLoader.clear();
  }

  @AfterAll
  public static void cleanUp() {
    System.setProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY, ENV);
  }

  @ParameterizedTest(name = "{index} {2}")
  @MethodSource("provideParameters")
  @SuppressWarnings("unused")
  void testWrongConfigs(String envVar, Class<? extends Exception> expectedException, String title) {
    System.setProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY, envVar);
    Assertions.assertThrows(expectedException, ConfigurationLoader::getJitterStrategy);
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> provideParameters() {
    String fileSeparator = java.io.File.separator;
    return Stream.of(
        Arguments.of("nonsense", ConfigurationException.class, "Missing config file"),
        Arguments.of(
            fileSeparator + "config" + fileSeparator + "missingProperty",
            MissingConfigurationException.class,
            "Missing property in config file"),
        Arguments.of(
            fileSeparator + "config" + fileSeparator + "classNotFound",
            ConfigurationException.class,
            "Class not found"),
        Arguments.of(
            fileSeparator + "config" + fileSeparator + "noDefaultConstructor",
            ConfigurationException.class,
            "No default constructor"),
        Arguments.of(
            fileSeparator + "config" + fileSeparator + "instantiationException",
            ConfigurationException.class,
            "Class instantiation exception"));
  }

  @BeforeEach
  public void setup() {
    System.clearProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY);
    ConfigurationLoader.clear();
  }

  @AfterEach
  public void tearDown() {
    System.clearProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY);
    ConfigurationLoader.clear();
  }
}
