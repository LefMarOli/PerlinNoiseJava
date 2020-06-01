package org.lefmaroli.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.execution.JitterStrategy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class ConfigurationLoader {

    public static final String ENVIRONMENT_TARGET_ENVIRONMENT_PROPERTY = "envTarget";
    public static final String DEFAULT_ENVIRONMENT_TARGET = "dev";
    public static final String JITTER_STRATEGY_TYPE_PROPERTY = "jitterStrategyType";
    public static final String JITTER_STRATEGY_PROPERTY = "jitterStrategy";

    private static final Properties appProperties = new Properties();
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationLoader.class);

    static {
        String environmentTarget = System.getenv(ENVIRONMENT_TARGET_ENVIRONMENT_PROPERTY);
        if (environmentTarget == null) {
            environmentTarget = DEFAULT_ENVIRONMENT_TARGET;
        }
        appProperties.put(ENVIRONMENT_TARGET_ENVIRONMENT_PROPERTY, environmentTarget);
        String location = "classpath:" + environmentTarget + ".properties";
        Resource resource = new PathMatchingResourcePatternResolver().getResource(location);
        try {
            appProperties.load(resource.getInputStream());
        } catch (IOException e) {
            LOGGER.error("Could not load properties from resource " + resource.getFilename());
        }
    }

    public static JitterStrategy getJitterStrategy() {
        if (!appProperties.containsKey(JITTER_STRATEGY_PROPERTY)) {
            String jitterStrategyType = appProperties.getProperty(JITTER_STRATEGY_TYPE_PROPERTY);
            if (jitterStrategyType == null) {
                throw new ConfigurationException("Missing property " + JITTER_STRATEGY_TYPE_PROPERTY);
            } else {
                Class<?> jitterStrategyClass;
                try {
                    jitterStrategyClass = Class.forName(jitterStrategyType);
                } catch (ClassNotFoundException e) {
                    throw new ConfigurationException("Jitter strategy class " + jitterStrategyType +
                            " does not exists. Please check configurations.", e);
                }
                Constructor<?> declaredConstructor;
                try {
                    declaredConstructor = jitterStrategyClass.getDeclaredConstructor();
                } catch (NoSuchMethodException e) {
                    throw new ConfigurationException(
                            "Jitter strategy class " + jitterStrategyType + " does not have a default constructor", e);
                }
                Object instance;
                try {
                    instance = declaredConstructor.newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new ConfigurationException("Unable to create new instance of " + jitterStrategyType, e);
                }
                appProperties.put(JITTER_STRATEGY_PROPERTY, instance);
            }
        }
        return (JitterStrategy) appProperties.get(JITTER_STRATEGY_PROPERTY);
    }

}
