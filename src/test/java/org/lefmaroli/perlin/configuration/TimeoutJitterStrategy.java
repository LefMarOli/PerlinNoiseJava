package org.lefmaroli.perlin.configuration;

import java.util.concurrent.TimeUnit;

public class TimeoutJitterStrategy extends AbstractTestJitterStrategy {

  @Override
  public void jitter() {
    waitFor(50000, TimeUnit.MILLISECONDS);
  }

  @Override
  public long getTimeout() {
    return 1;
  }

}
