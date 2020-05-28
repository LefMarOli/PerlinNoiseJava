package org.lefmaroli.perlin;

import java.util.List;

public abstract class MultiDimensionalLayeredNoiseGenerator<ReturnType,
        NoiseLayer extends INoiseGenerator<ReturnType> & MultiDimensionalNoiseGenerator>
        extends LayeredNoiseGenerator<ReturnType, NoiseLayer> implements MultiDimensionalNoiseGenerator {

    private final boolean isCircular;

    protected MultiDimensionalLayeredNoiseGenerator(List<NoiseLayer> layers) {
        super(layers);
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
