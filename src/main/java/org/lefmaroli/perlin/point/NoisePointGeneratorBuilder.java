package org.lefmaroli.perlin.point;

import org.lefmaroli.perlin.NoiseBuilder;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import java.util.List;

public class NoisePointGeneratorBuilder
        extends NoiseBuilder<NoisePointGenerator, NoisePointGenerator, NoisePointGeneratorBuilder> {

    public NoisePointGeneratorBuilder() {
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
    protected NoisePointGenerator buildSingleLayerNoise(int interpolationPoints, double layerAmplitude, long randomSeed) {
        return new PointGenerator(interpolationPoints, layerAmplitude, randomSeed);
    }

    @Override
    protected NoisePointGenerator buildMultipleLayerNoise(List<NoisePointGenerator> layers) {
        return new MultiLayerPointGenerator(layers);
    }
}
