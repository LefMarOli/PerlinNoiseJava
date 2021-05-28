package org.lefmaroli.perlin.configuration;

public class DelayJitterStrategy extends AbstractTestJitterStrategy {

  @Override
  public void jitter() {
    waitFor(50);
  }

  @Override
  public long getTimeout() {
    return 100000;
  }
}
