package org.lefmaroli.perlin.line;

import org.lefmaroli.factorgenerator.NumberGenerator;
import org.lefmaroli.perlin.dimensional.MultiDimensionalNoiseBuilder;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import java.util.List;

public class LineNoiseGeneratorBuilder
        extends MultiDimensionalNoiseBuilder<LineNoiseDataContainer, LineNoiseGenerator,
        LineNoiseGeneratorBuilder> {

    private final int lineLength;

    LineNoiseGeneratorBuilder(int lineLength) {
        super(2);
        this.lineLength = lineLength;
    }

    @Override
    public LineNoiseGenerator build() throws NoiseBuilderException {
        return (LineNoiseGenerator) super.build();
    }

    LineNoiseGeneratorBuilder withLineInterpolationPointCountGenerator(NumberGenerator<Integer> numberGenerator) {
        setInterpolationPointCountGeneratorForDimension(2, numberGenerator);
        return this;
    }

    @Override
    protected LineNoiseGeneratorBuilder self() {
        return this;
    }

    @Override
    protected LineNoiseGenerator buildSingleNoiseLayer(List<Integer> interpolationPoints, double layerAmplitude,
                                                       long randomSeed) {
        return new LineGenerator(lineLength, interpolationPoints.get(0), interpolationPoints.get(1), layerAmplitude,
                randomSeed, isCircular());
    }

    @Override
    protected LineNoiseGenerator buildMultipleNoiseLayer(List<LineNoiseGenerator> layers) {
        return new LayeredLineGenerator(layers);
    }
}
