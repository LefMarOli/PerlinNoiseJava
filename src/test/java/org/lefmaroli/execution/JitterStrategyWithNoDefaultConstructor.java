package org.lefmaroli.execution;

public class JitterStrategyWithNoDefaultConstructor implements JitterStrategy {

  JitterStrategyWithNoDefaultConstructor(int variable) {}

  @Override
  public void jitter() {}
}
