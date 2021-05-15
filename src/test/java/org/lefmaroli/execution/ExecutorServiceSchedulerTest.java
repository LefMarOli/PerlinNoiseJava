package org.lefmaroli.execution;

import static org.awaitility.Awaitility.await;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExecutorServiceSchedulerTest {

  @Test
  void testExecutorServiceSchedulerWithTaskException() {
    Callable<Double> callableThatThrows =
        () -> {
          throw new Exception("Testing exception handling");
        };
    ExecutorServiceScheduler scheduler = new ExecutorServiceScheduler(10);
    CompletableFuture<Double> future = scheduler.schedule(callableThatThrows);
    await().until(future::isCompletedExceptionally);
    Assertions.assertTrue(future.isCompletedExceptionally());
  }
}
