package org.lefmaroli.perlin.configuration;

public class JitterTrait {
  private static final JitterStrategy DEFAULT_PRODUCTION_STRATEGY = new ProductionJitterStrategy();
  private static JitterStrategy jitterStrategy = DEFAULT_PRODUCTION_STRATEGY;

  private JitterTrait(){}

  public static void setJitterStrategy(JitterStrategy jitterStrategy) {
    JitterTrait.jitterStrategy = jitterStrategy;
  }

  public static void resetJitterStrategy() {
    JitterTrait.jitterStrategy = DEFAULT_PRODUCTION_STRATEGY;
  }

  public static boolean isJitterStrategyDefaultProduction() {
    return jitterStrategy == DEFAULT_PRODUCTION_STRATEGY;
  }

  public static long getTimeout(){
    return jitterStrategy.getTimeout();
  }

  public static void jitter() {
    jitterStrategy.jitter();
  }
}
