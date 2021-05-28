package org.lefmaroli.perlin.configuration;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractTestJitterStrategy implements JitterStrategy {

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final Logger logger = LogManager.getLogger(this.getClass());

  protected void waitFor(long delay, TimeUnit timeUnit){
    try {
      ScheduledFuture<?> future = scheduler.schedule(() -> {}, delay, timeUnit);
      future.get();
    } catch (InterruptedException e) {
      logger.error("Interrupted while causing jitter wait", e);
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      logger.error("Exception while waiting for jitter", e);
    }
  }

  public void shutdown() {
    scheduler.shutdown();
  }
}
