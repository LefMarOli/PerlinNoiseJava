package org.lefmaroli.configuration;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.execution.JitterStrategy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ConfigurationLoader {

  private static final Properties appProperties = new Properties();
  private static final Logger LOGGER = LogManager.getLogger(ConfigurationLoader.class);

  private ConfigurationLoader() {}

  public static String getEnv() {
    retrieveEnvironmentTarget();
    return appProperties.getProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY);
  }

  public static void clear() {
    appProperties.clear();
  }

  public static JitterStrategy getJitterStrategy() {
    retrieveEnvironmentTarget();
    if (!appProperties.containsKey(ConfigurationProperties.JITTER_STRATEGY_PROPERTY)) {
      String jitterStrategyType =
          appProperties.getProperty(ConfigurationProperties.JITTER_STRATEGY_TYPE_PROPERTY);
      if (jitterStrategyType == null) {
        throw new MissingConfigurationException(
            ConfigurationProperties.JITTER_STRATEGY_TYPE_PROPERTY);
      } else {
        Class<?> jitterStrategyClass;
        try {
          jitterStrategyClass = Class.forName(jitterStrategyType);
        } catch (ClassNotFoundException e) {
          throw new ConfigurationException(
              "Jitter strategy class "
                  + jitterStrategyType
                  + " does not exists. Please check configurations.",
              e);
        }
        Constructor<?> declaredConstructor;
        try {
          declaredConstructor = jitterStrategyClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
          throw new ConfigurationException(
              "Jitter strategy class "
                  + jitterStrategyType
                  + " does not have a default constructor",
              e);
        }
        Object instance;
        try {
          instance = declaredConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
          throw new ConfigurationException(
              "Unable to create new instance of " + jitterStrategyType, e);
        }
        appProperties.put(ConfigurationProperties.JITTER_STRATEGY_PROPERTY, instance);
      }
    }
    return (JitterStrategy) appProperties.get(ConfigurationProperties.JITTER_STRATEGY_PROPERTY);
  }

  private static void retrieveEnvironmentTarget() {
    if (!isEnvironmentTargetSet()) {
      String environmentTarget = System.getenv(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY);
      if (environmentTarget == null) {
        environmentTarget = System.getProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY);
        if (environmentTarget == null) {
          environmentTarget = ConfigurationProperties.DEFAULT_ENVIRONMENT_TARGET;
        }
      }
      appProperties.clear();
      appProperties.put(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY, environmentTarget);
      loadConfigurations();
    }
  }

  private static boolean isEnvironmentTargetSet() {
    return appProperties.containsKey(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY);
  }

  private static void loadConfigurations() {
    String location =
        "classpath:"
            + appProperties.getProperty(ConfigurationProperties.ENVIRONMENT_TARGET_PROPERTY)
            + ".properties";
    Resource[] resources;
    try {
      resources = new PathMatchingResourcePatternResolver().getResources(location);
    } catch (IOException e) {
      throw new ConfigurationException(
          "Trouble reading properties file matching pattern " + location, e);
    }
    if (resources.length == 0) {
      throw new ConfigurationException(
          "Unable to find properties file matching pattern " + location);
    }
    for (Resource resource : resources) {
      LOGGER.debug("Loading properties from resource: {}", resource.getFilename());
      try {
        appProperties.load(resource.getInputStream());
      } catch (IOException e) {
        throw new ConfigurationException(
            "Could not load properties from resource " + resource.getFilename(), e);
      }
    }
  }
}
