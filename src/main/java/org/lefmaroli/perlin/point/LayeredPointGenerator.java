package org.lefmaroli.perlin.point;

import org.lefmaroli.execution.ExecutorServiceScheduler;
import org.lefmaroli.perlin.layers.LayeredNoiseGenerator;

import java.util.List;

public class LayeredPointGenerator extends LayeredNoiseGenerator<PointNoiseDataContainer, PointNoiseGenerator>
        implements PointNoiseGenerator {

    LayeredPointGenerator(List<PointNoiseGenerator> layers) {
        super(layers, new ExecutorServiceScheduler(10));
    }

    @Override
    public String toString() {
        return "LayeredPointGenerator{" +
                "layers=" + getLayers() +
                ", maxAmplitude=" + getMaxAmplitude() +
                '}';
    }

    @Override
    protected PointNoiseDataContainer initializeResults(int count) {
        return new PointNoiseDataContainer(count);
    }
}
