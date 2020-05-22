package org.lefmaroli.perlin.point;

import org.lefmaroli.perlin.NoiseBuilder;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import java.util.List;

public class NoisePointGeneratorBuilder
        extends NoiseBuilder<NoisePointGenerator, NoisePointGeneratorBuilder> {

    public NoisePointGeneratorBuilder() {
        super(1);
    }

    @Override
    protected NoisePointGeneratorBuilder self() {
        return this;
    }

    @Override
    public NoisePointGenerator build() throws NoiseBuilderException {
        return (NoisePointGenerator) super.build();
    }

    @Override
    protected NoisePointGenerator buildSingleLayerNoise(List<Integer> interpolationPoints, double layerAmplitude,
                                                        long randomSeed) {
        return new PointGenerator(interpolationPoints.get(0), layerAmplitude, randomSeed);
    }

    @Override
    protected NoisePointGenerator buildMultipleLayerNoise(List<NoisePointGenerator> layers) {
        return new MultiLayerPointGenerator(layers);
    }
}
