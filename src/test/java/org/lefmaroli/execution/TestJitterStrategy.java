package org.lefmaroli.execution;

import java.util.Random;

public class TestJitterStrategy implements JitterStrategy {

  private final Random random = new Random();

  @Override
  public void jitter() {
    switch (random.nextInt(4)) {
      case 0:
        return;
      case 1:
        try {
          Thread.sleep(random.nextInt(25));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return;
      case 2:
        Thread.yield();
        return;
      case 3:
        Thread.onSpinWait();
    }
  }
}
