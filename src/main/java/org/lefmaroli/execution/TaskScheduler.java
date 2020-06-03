package org.lefmaroli.execution;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public interface TaskScheduler {
  <R> CompletableFuture<R> schedule(Callable<R> task);
}
