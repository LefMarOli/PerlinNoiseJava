package org.lefmaroli.perlin.layers;

import org.lefmaroli.execution.TaskScheduler;
import org.lefmaroli.perlin.data.NoiseData;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class LayerProcessScheduler<ResultType extends NoiseData<?, ResultType>>
        implements TaskScheduler<ResultType> {

    private final TaskScheduler<ResultType> concreteScheduler;

    public LayerProcessScheduler(TaskScheduler<ResultType> scheduler) {
        this.concreteScheduler = scheduler;
    }

    @Override
    public CompletableFuture<ResultType> schedule(Callable<ResultType> task) {
        return concreteScheduler.schedule(task);
    }
}
