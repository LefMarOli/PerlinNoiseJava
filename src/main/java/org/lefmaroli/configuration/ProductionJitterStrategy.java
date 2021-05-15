package org.lefmaroli.configuration;

public class ProductionJitterStrategy implements JitterStrategy {
  @Override
  public void jitter() {
    // Empty implementation to not affect production code
  }
}
