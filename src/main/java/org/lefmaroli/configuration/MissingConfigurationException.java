package org.lefmaroli.configuration;

public class MissingConfigurationException extends ConfigurationException {

    public MissingConfigurationException(String configuration) {
        super("Missing configuration " + configuration);
    }
}
