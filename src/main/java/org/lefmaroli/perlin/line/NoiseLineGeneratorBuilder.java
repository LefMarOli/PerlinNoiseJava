package org.lefmaroli.perlin.line;

import org.lefmaroli.factorgenerator.NumberGenerator;
import org.lefmaroli.perlin.NoiseBuilder;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import java.util.List;

public class NoiseLineGeneratorBuilder
        extends NoiseBuilder<Double[][], LineNoiseGenerator, NoiseLineGeneratorBuilder> {

    private final int lineLength;

    NoiseLineGeneratorBuilder(int lineLength) {
        super(2);
        this.lineLength = lineLength;
    }

    NoiseLineGeneratorBuilder withLineInterpolationPointCountGenerator(NumberGenerator<Integer> numberGenerator){
        setInterpolationPointCountGeneratorForDimension(2, numberGenerator);
        return this;
    }

    @Override
    public LineNoiseGenerator build() throws NoiseBuilderException {
        return (LineNoiseGenerator) super.build();
    }

    @Override
    protected NoiseLineGeneratorBuilder self() {
        return this;
    }

    @Override
    protected LineNoiseGenerator buildSingleNoiseLayer(List<Integer> interpolationPoints, double layerAmplitude,
                                                       long randomSeed) {
        return new LineGenerator(lineLength, interpolationPoints.get(0), interpolationPoints.get(1), layerAmplitude,
                randomSeed);
    }

    @Override
    protected LineNoiseGenerator buildMultipleNoiseLayer(List<LineNoiseGenerator> layers) {
        return new MultiLayerLineGenerator(layers);
    }
}
