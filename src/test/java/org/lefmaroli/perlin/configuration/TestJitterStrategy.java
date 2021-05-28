package org.lefmaroli.perlin.configuration;

import java.util.Random;

public class TestJitterStrategy extends AbstractTestJitterStrategy {

  private final Random random = new Random();
  private static final long TIMEOUT = 10000;

  @Override
  public long getTimeout() {
    return TIMEOUT;
  }

  @Override
  public void jitter() {
    switch (random.nextInt(3)) {
      case 0:
        return;
      case 1:
        int delay = random.nextInt(5);
        waitFor(delay);
        return;
      case 2:
        Thread.yield();
    }
  }
}
