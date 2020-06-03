package org.lefmaroli.execution;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceScheduler implements TaskScheduler {

  private final ExecutorService executorService;

  public ExecutorServiceScheduler(int size) {
    this.executorService = Executors.newFixedThreadPool(size);
  }

  public ExecutorServiceScheduler(ExecutorService executorService) {
    this.executorService = executorService;
  }

  @Override
  public <R> CompletableFuture<R> schedule(Callable<R> task) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            return task.call();
          } catch (Exception e) {
            throw new CompletionException(e);
          }
        },
        executorService);
  }
}
