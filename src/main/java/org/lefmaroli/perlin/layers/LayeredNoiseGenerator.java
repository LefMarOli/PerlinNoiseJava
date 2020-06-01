package org.lefmaroli.perlin.layers;

import org.lefmaroli.execution.TaskScheduler;
import org.lefmaroli.perlin.INoiseGenerator;
import org.lefmaroli.perlin.data.NoiseData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class LayeredNoiseGenerator<ReturnType extends NoiseData<?, ReturnType>,
        NoiseLayer extends INoiseGenerator<ReturnType>>
        implements INoiseGenerator<ReturnType> {

    private final double maxAmplitude;
    private final TaskScheduler scheduler;
    private final List<NoiseLayer> layers;

    protected LayeredNoiseGenerator(List<NoiseLayer> layers, TaskScheduler scheduler) {
        if (layers.size() < 1) {
            throw new IllegalArgumentException("Number of layers must at least be 1");
        }
        this.layers = layers;
        double sum = 0.0;
        for (NoiseLayer layer : layers) {
            sum += layer.getMaxAmplitude();
        }
        this.maxAmplitude = sum;
        this.scheduler = scheduler;
    }

    @Override
    public ReturnType getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        }
        return generateResults(count);
    }

    public double getMaxAmplitude() {
        return maxAmplitude;
    }

    public int getNumberOfLayers() {
        return layers.size();
    }

    protected List<NoiseLayer> getLayers() {
        return layers;
    }

    protected abstract ReturnType initializeResults(int count);

    private ReturnType generateResults(int count) {
        ReturnType results = initializeResults(count);
        List<CompletableFuture<ReturnType>> futures = new ArrayList<>(layers.size());
        for (NoiseLayer layer : layers) {
            LayerProcess<ReturnType, NoiseLayer> layerProcess = new LayerProcess<>(layer, count);
            futures.add(scheduler.schedule(layerProcess));
        }
        for (CompletableFuture<ReturnType> future : futures) {
            try {
                results.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Problem executing parallel code", e);
            }
        }
        results.normalizeBy(maxAmplitude);
        return results;
    }
}
