package org.lefmaroli.execution;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public interface TaskScheduler<ReturnType> {
    CompletableFuture<ReturnType> schedule(Callable<ReturnType> task);
}
