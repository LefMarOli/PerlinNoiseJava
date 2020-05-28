package org.lefmaroli.perlin.point;

import org.lefmaroli.perlin.NoiseBuilder;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import java.util.List;

public class NoisePointGeneratorBuilder
        extends NoiseBuilder<Double[], PointNoiseGenerator, NoisePointGeneratorBuilder> {

    public NoisePointGeneratorBuilder() {
        super(1);
    }

    @Override
    protected NoisePointGeneratorBuilder self() {
        return this;
    }

    @Override
    public PointNoiseGenerator build() throws NoiseBuilderException {
        return (PointNoiseGenerator) super.build();
    }

    @Override
    protected PointNoiseGenerator buildSingleNoiseLayer(List<Integer> interpolationPoints, double layerAmplitude,
                                                        long randomSeed) {
        return new PointGenerator(interpolationPoints.get(0), layerAmplitude, randomSeed);
    }

    @Override
    protected PointNoiseGenerator buildMultipleNoiseLayer(List<PointNoiseGenerator> layers) {
        return new MultiLayerPointGenerator(layers);
    }
}
