package org.lefmaroli.utils;

import static org.awaitility.Awaitility.waitAtMost;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;

public class ScheduledUpdater {

  private static final Logger logger = LogManager.getLogger(ScheduledUpdater.class);

  public static CompletableFuture<Void> updateAtRateForDuration(
      Runnable r, long rate, TimeUnit rateUnit, long duration, TimeUnit durationUnit) {
    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    AtomicBoolean isDone = new AtomicBoolean(false);
    AtomicBoolean hasErrors = new AtomicBoolean(false);
    AtomicReference<Throwable> error = new AtomicReference<>();
    ses.scheduleAtFixedRate(
        () -> {
          try {
            r.run();
          } catch (Throwable t) {
            hasErrors.set(true);
            error.set(t);
            throw t;
          }
        },
        0,
        rate,
        rateUnit);
    ses.schedule(() -> isDone.set(true), duration, durationUnit);
    try {
      Awaitility.setDefaultPollInterval(Duration.ofMillis(10));
      waitAtMost(duration + 1, durationUnit).until(() -> (isDone.get() || hasErrors.get()));
      Assertions.assertFalse(
          hasErrors.get(), () -> "Test did not complete without errors:" + error.get());
    } finally {
      ses.shutdownNow();
      try {
        boolean areTasksDone = ses.awaitTermination(10, TimeUnit.SECONDS);
        if (!areTasksDone) {
          logger.error("Tasks were not completed within the delay");
        } else logger.debug("ExecutorService shutdown complete");
      } catch (InterruptedException e) {
        logger.error("Awaiting task termination was interrupted");
        Thread.currentThread().interrupt();
      }
    }
    return CompletableFuture.completedFuture(null);
  }
}
