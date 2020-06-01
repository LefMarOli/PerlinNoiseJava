package org.lefmaroli.perlin.layers;

import org.lefmaroli.execution.ExecutorServiceScheduler;
import org.lefmaroli.perlin.INoiseGenerator;
import org.lefmaroli.perlin.data.NoiseData;
import org.lefmaroli.perlin.dimensional.MultiDimensionalNoiseGenerator;

import java.util.List;

public abstract class MultiDimensionalLayeredNoiseGenerator<ReturnType extends NoiseData<?, ReturnType>,
        NoiseLayer extends INoiseGenerator<ReturnType> & MultiDimensionalNoiseGenerator>
        extends LayeredNoiseGenerator<ReturnType, NoiseLayer> implements MultiDimensionalNoiseGenerator {

    private final boolean isCircular;

    protected MultiDimensionalLayeredNoiseGenerator(List<NoiseLayer> layers) {
        super(layers, new ExecutorServiceScheduler(10));
        isCircular = checkCircularity(layers);
    }

    @Override
    public boolean isCircular() {
        return isCircular;
    }

    private boolean checkCircularity(List<NoiseLayer> layers) {
        boolean isCircular = layers.get(0).isCircular();
        for (MultiDimensionalNoiseGenerator layer : layers) {
            if (layer.isCircular() != isCircular) {
                //Cannot guarantee circularity, force to false;
                isCircular = false;
                break;
            }
        }
        return isCircular;
    }
}
