package org.lefmaroli.configuration;

public class JitterTrait implements JitterStrategy {
  private static final JitterStrategy DEFAULT_PRODUCTION_STRATEGY = new ProductionJitterStrategy();
  private static JitterStrategy jitterStrategy = DEFAULT_PRODUCTION_STRATEGY;

  public static void setJitterStrategy(JitterStrategy jitterStrategy) {
    JitterTrait.jitterStrategy = jitterStrategy;
  }

  public static void resetJitterStrategy() {
    JitterTrait.jitterStrategy = DEFAULT_PRODUCTION_STRATEGY;
  }

  public static boolean isJitterStrategyDefaultProduction(){
    return jitterStrategy == DEFAULT_PRODUCTION_STRATEGY;
  }

  @Override
  public void jitter() {
    jitterStrategy.jitter();
  }
}
