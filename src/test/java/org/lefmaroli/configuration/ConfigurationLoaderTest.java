package org.lefmaroli.configuration;

import org.junit.*;

public class ConfigurationLoaderTest {

    private static String ENV;

    @BeforeClass
    public static void init(){
        ENV = ConfigurationLoader.getEnv();
        ConfigurationLoader.clear();
    }

    @AfterClass
    public static void cleanUp(){
        System.setProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY, ENV);
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

    @Test(expected = ConfigurationException.class)
    public void testWrongEnvironmentTarget() {
        System.setProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY, "nonsense");
        ConfigurationLoader.getJitterStrategy();
    }

    @Test(expected = MissingConfigurationException.class)
    public void testMissingJitterConfigurationProperty() {
        System.setProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY, "/config/missingProperty");
        ConfigurationLoader.getJitterStrategy();
    }

    @Test(expected = ConfigurationException.class)
    public void testClassNotFound() {
        System.setProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY, "/config/classNotFound");
        ConfigurationLoader.getJitterStrategy();
    }

    @Test(expected = ConfigurationException.class)
    public void testNoDefaultConstructor() {
        System.setProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY,
                "/config/noDefaultConstructor");
        ConfigurationLoader.getJitterStrategy();
    }

    @Test(expected = ConfigurationException.class)
    public void testInstantiationException() {
        System.setProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY,
                "/config/instantiationException");
        ConfigurationLoader.getJitterStrategy();
    }

}