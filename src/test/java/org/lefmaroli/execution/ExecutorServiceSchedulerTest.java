package org.lefmaroli.execution;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import org.junit.Test;

public class ExecutorServiceSchedulerTest {

  @Test
  public void testExecutorServiceSchedulerWithTaskException() {
    Callable<Double> callableThatThrows =
        () -> {
          throw new Exception("Testing exception handling");
        };
    ExecutorServiceScheduler scheduler = new ExecutorServiceScheduler(10);
    CompletableFuture<Double> future = scheduler.schedule(callableThatThrows);
    await().until(future::isCompletedExceptionally);
    assertTrue(future.isCompletedExceptionally());
  }
}
