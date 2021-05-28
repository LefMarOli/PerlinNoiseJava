package org.lefmaroli.perlin.configuration;

public class ProductionJitterStrategy implements JitterStrategy {
  @Override
  public void jitter() {
    // Empty implementation to not affect production code
  }

  @Override
  public long getTimeout() {
    return 5;
  }
}
