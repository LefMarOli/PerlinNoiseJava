package org.lefmaroli.execution;

import org.lefmaroli.configuration.JitterStrategy;

public class JitterStrategyWithNoDefaultConstructor implements JitterStrategy {

  JitterStrategyWithNoDefaultConstructor(int variable) {}

  @Override
  public void jitter() {}
}
