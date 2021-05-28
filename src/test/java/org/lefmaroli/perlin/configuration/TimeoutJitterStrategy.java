package org.lefmaroli.perlin.configuration;

public class TimeoutJitterStrategy extends AbstractTestJitterStrategy {

  @Override
  public void jitter() {
    waitFor(50000);
  }

  @Override
  public long getTimeout() {
    return 1;
  }
}
