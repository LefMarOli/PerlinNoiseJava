package org.lefmaroli.perlin.configuration;

import java.util.concurrent.TimeUnit;

public class DelayJitterStrategy extends AbstractTestJitterStrategy {

  @Override
  public void jitter() {
    waitFor(5, TimeUnit.MILLISECONDS);
  }

  @Override
  public long getTimeout() {
    return 100000;
  }
}
