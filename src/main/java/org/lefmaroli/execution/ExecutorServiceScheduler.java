package org.lefmaroli.execution;

import java.util.concurrent.*;

public class ExecutorServiceScheduler<ReturnType> implements TaskScheduler<ReturnType> {

    private final ExecutorService executorService;

    public ExecutorServiceScheduler(int size){
        this.executorService = Executors.newFixedThreadPool(size);
    }
    public ExecutorServiceScheduler(ExecutorService executorService){
        this.executorService = executorService;
    }

    @Override
    public CompletableFuture<ReturnType> schedule(Callable<ReturnType> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executorService);
    }
}
