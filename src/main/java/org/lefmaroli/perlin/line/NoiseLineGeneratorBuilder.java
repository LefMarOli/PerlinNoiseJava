package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.NoiseBuilder;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import java.util.List;

public class NoiseLineGeneratorBuilder
        extends NoiseBuilder<NoiseLineGenerator, NoiseLineGeneratorBuilder> {

    private final int lineLength;

    NoiseLineGeneratorBuilder(int lineLength) {
        super(2);
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
    protected NoiseLineGenerator buildSingleLayerNoise(List<Integer> interpolationPoints, double layerAmplitude,
                                                       long randomSeed) {
        return new LineGenerator(lineLength, interpolationPoints.get(0), interpolationPoints.get(1), layerAmplitude,
                randomSeed);
    }

    @Override
    protected NoiseLineGenerator buildMultipleLayerNoise(List<NoiseLineGenerator> layers) {
        return new MultiLayerLineGenerator(layers);
    }
}
