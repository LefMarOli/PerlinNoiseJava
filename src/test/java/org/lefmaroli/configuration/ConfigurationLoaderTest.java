package org.lefmaroli.configuration;

import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ConfigurationLoaderTest {

  private static String ENV;
  @Parameter() public String envVar;

  @Parameter(1)
  public Class<? extends Exception> expectedException;

  @Parameter(2)
  public String testTitle;

  @BeforeClass
  public static void init() {
    ENV = ConfigurationLoader.getEnv();
    ConfigurationLoader.clear();
  }

  @AfterClass
  public static void cleanUp() {
    System.setProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY, ENV);
  }

  @Parameters(name = "{index}: {2}")
  public static Iterable<Object[]> data() {
    String fileSeparator = java.io.File.separator;
    return Arrays.asList(
        new Object[][] {
          {"nonsense", ConfigurationException.class, "Missing config file"},
          {
            fileSeparator + "config" + fileSeparator + "missingProperty",
            MissingConfigurationException.class,
            "Missing property in config file"
          },
          {
            fileSeparator + "config" + fileSeparator + "classNotFound",
            ConfigurationException.class,
            "Class not found"
          },
          {
            fileSeparator + "config" + fileSeparator + "noDefaultConstructor",
            ConfigurationException.class,
            "No default constructor"
          },
          {
            fileSeparator + "config" + fileSeparator + "instantiationException",
            ConfigurationException.class,
            "Class instantiation exception"
          },
        });
  }

  @Before
  public void setup() {
    System.clearProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY);
    ConfigurationLoader.clear();
  }

  @After
  public void tearDown() {
    System.clearProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY);
    ConfigurationLoader.clear();
  }

  @Test
  public void testWrongConfigs() {
    System.setProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY, envVar);
    assertThrows(expectedException, ConfigurationLoader::getJitterStrategy);
  }
}
