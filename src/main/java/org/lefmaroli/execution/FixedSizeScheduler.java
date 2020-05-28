package org.lefmaroli.execution;

import java.util.concurrent.*;

public class FixedSizeScheduler<ReturnType> implements TaskScheduler<ReturnType> {

    private final ExecutorService executorService;

    public FixedSizeScheduler(int size){
        this.executorService = Executors.newFixedThreadPool(size);
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
