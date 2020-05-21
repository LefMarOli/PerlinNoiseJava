package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.NoiseBuilder;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import java.util.List;

public class NoiseLineGeneratorBuilder
        extends NoiseBuilder<NoiseLineGenerator, LineGenerator, NoiseLineGeneratorBuilder> {

    private final int lineLength;

    NoiseLineGeneratorBuilder(int lineLength) {
        this.lineLength = lineLength;
    }

    @Override
    public NoiseLineGenerator build() throws NoiseBuilderException {
        return (NoiseLineGenerator) super.build();
    }

    @Override
    protected NoiseLineGeneratorBuilder self() {
        return this;
    }

    @Override
    protected LineGenerator buildSingleLayerNoise(int interpolationPoints, double layerAmplitude, long randomSeed) {
        return new LineGenerator(lineLength, interpolationPoints, layerAmplitude, randomSeed);
    }

    @Override
    protected NoiseLineGenerator buildMultipleLayerNoise(List<LineGenerator> layers) {
        return new MultiLayerLineGenerator(layers);
    }
}
