package org.lefmaroli.execution;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.configuration.JitterStrategy;

public class TestJitterStrategy implements JitterStrategy {

  private final Random random = new Random();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final Logger logger = LogManager.getLogger(TestJitterStrategy.class);

  @Override
  public void jitter() {
    switch (random.nextInt(3)) {
      case 0:
        return;
      case 1:
        try {
          int delay = random.nextInt(5);
          ScheduledFuture<?> future =
              scheduler.schedule(
                  () -> {}, delay, TimeUnit.MICROSECONDS);
          future.get();
        } catch (InterruptedException e) {
          logger.error("Interrupted while causing jitter wait", e);
          Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
          logger.error("Exception while waiting for jitter", e);
        }
        return;
      case 2:
        Thread.yield();
    }
  }
}
