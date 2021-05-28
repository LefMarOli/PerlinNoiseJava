package org.lefmaroli.perlin.configuration;

public class ProductionJitterStrategy implements JitterStrategy {

  private static final ProductionJitterStrategy INSTANCE = new ProductionJitterStrategy();

  public static ProductionJitterStrategy getInstance() {
    return INSTANCE;
  }

  @Override
  public void jitter() {
    // Empty implementation to not affect production code
  }

  @Override
  public long getTimeout() {
    return 5;
  }
}
