package org.lefmaroli.execution;

import org.lefmaroli.configuration.JitterStrategy;

public class JitterStrategyWithInstantiationException implements JitterStrategy {

  JitterStrategyWithInstantiationException() {
    throw new RuntimeException("For testing");
  }

  @Override
  public void jitter() {}
}
