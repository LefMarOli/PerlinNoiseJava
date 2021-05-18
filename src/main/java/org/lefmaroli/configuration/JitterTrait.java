package org.lefmaroli.configuration;

public class JitterTrait implements JitterStrategy {
  private static final JitterStrategy DEFAULT = new ProductionJitterStrategy();
  private static JitterStrategy jitterStrategy = DEFAULT;

  public static void setJitterStrategy(JitterStrategy jitterStrategy) {
    JitterTrait.jitterStrategy = jitterStrategy;
  }

  public static void resetJitterStrategy() {
    JitterTrait.jitterStrategy = DEFAULT;
  }

  @Override
  public void jitter() {
    jitterStrategy.jitter();
  }
}
