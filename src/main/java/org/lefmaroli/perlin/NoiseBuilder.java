package org.lefmaroli.perlin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.factorgenerator.IntegerGenerator;
import org.lefmaroli.factorgenerator.NumberGenerator;
import org.lefmaroli.perlin.exceptions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class NoiseBuilder<RawDataType,
        ReturnType extends NoiseData<RawDataType, ReturnType>,
        NoiseType extends INoiseGenerator<RawDataType, ReturnType>,
        NoiseBuilderType extends NoiseBuilder<RawDataType, ReturnType, NoiseType, NoiseBuilderType>> {
    private static final Logger LOGGER = LogManager.getLogger(NoiseBuilder.class);
    private static final int INTERPOLATION_POINTS_UPPER_LIMIT = 50000;
    private static final NumberGenerator<Integer>
            DEFAULT_INTERPOLATION_POINT_COUNT_GENERATOR = new IntegerGenerator(64, 0.5);
    private final int dimensions;
    protected int numberOfLayers = 5;
    protected long randomSeed = System.currentTimeMillis();
    private final List<NumberGenerator<Integer>> interpolationPointCountGenerators;
    private NumberGenerator<Double> amplitudeGenerator = new DoubleGenerator(1.0, 0.5);

    public NoiseBuilder(int dimensions) {
        this.dimensions = dimensions;
        this.interpolationPointCountGenerators = new ArrayList<>(dimensions);
        for (int i = 0; i < dimensions; i++) {
            this.interpolationPointCountGenerators.add(DEFAULT_INTERPOLATION_POINT_COUNT_GENERATOR.getCopy());
        }
    }

    public NoiseBuilderType withNoiseInterpolationPointCountGenerator(
            NumberGenerator<Integer> interpolationPointCountGenerator) {
        setInterpolationPointCountGeneratorForDimension(1, interpolationPointCountGenerator);
        return self();
    }

    public NoiseBuilderType withAmplitudeGenerator(NumberGenerator<Double> amplitudeGenerator) {
        this.amplitudeGenerator = amplitudeGenerator;
        return self();
    }

    public NoiseBuilderType withRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
        return self();
    }

    public NoiseBuilderType withNumberOfLayers(int numberOfLayers) {
        if (numberOfLayers < 1) {
            throw new IllegalArgumentException("Number of layers must be at least 1");
        }
        this.numberOfLayers = numberOfLayers;
        return self();
    }

    public INoiseGenerator<RawDataType, ReturnType> build() throws NoiseBuilderException {
        resetNumberGenerators();
        if (numberOfLayers == 1) {
            try {
                return buildSingleNoiseLayer(getNextInterpolationPointCount(), amplitudeGenerator.getNext(),
                        randomSeed);
            } catch (InterpolationPointException e) {
                throw new NoiseBuilderException(e);
            }
        } else {
            return buildMultipleNoiseLayer(generateNoiseLayers());
        }
    }

    protected void setInterpolationPointCountGeneratorForDimension(int dimension,
                                                                   NumberGenerator<Integer> interpolationPointCountGenerator) {
        if (dimension > this.dimensions) {
            String dimensionRange = this.dimensions == 1 ? "1" : "1-" + this.dimensions;
            throw new IllegalArgumentException(
                    "Dimension " + dimension + " is higher than the supported [" + dimensionRange +
                            "] dimensions for this builder.");
        } else {
            this.interpolationPointCountGenerators.set(dimension - 1, interpolationPointCountGenerator);
        }
    }

    protected abstract NoiseBuilderType self();

    protected abstract NoiseType buildSingleNoiseLayer(List<Integer> interpolationPoints, double layerAmplitude,
                                                       long randomSeed)
            throws NoiseBuilderException;

    protected abstract NoiseType buildMultipleNoiseLayer(List<NoiseType> layers)
            throws NoiseBuilderException;

    private static void assertInterpolationPointsCountForAll(List<Integer> interpolationPoints)
            throws InterpolationPointException {
        for (Integer interpolationPoint : interpolationPoints) {
            assertInterpolationPointsCount(interpolationPoint);
        }
    }

    private static void assertInterpolationPointsCount(int interpolationPoints) throws InterpolationPointException {
        if (interpolationPoints < 1) {
            throw new NoInterpolationPointException();
        } else if (interpolationPoints > INTERPOLATION_POINTS_UPPER_LIMIT) {
            throw new TooManyInterpolationPointsException(interpolationPoints, INTERPOLATION_POINTS_UPPER_LIMIT);
        }
    }

    private void resetNumberGenerators() {
        amplitudeGenerator.reset();
        for (NumberGenerator<Integer> interpolationPointCountGenerator : interpolationPointCountGenerators) {
            interpolationPointCountGenerator.reset();
        }
    }

    private List<NoiseType> generateNoiseLayers() throws NoiseBuilderException {
        Random randomGenerator = new Random(randomSeed);
        List<NoiseType> layers = new ArrayList<>(numberOfLayers);
        for (int i = 0; i < numberOfLayers; i++) {
            long randomSeed = randomGenerator.nextLong();
            try {
                layers.add(generateNoiseLayer(i, randomSeed));
            } catch (NoiseLayerException e) {
                throw new NoiseBuilderException(e);
            }
        }
        return layers;
    }

    private NoiseType generateNoiseLayer(int layerNumber, long randomSeed)
            throws NoiseLayerException, NoiseBuilderException {
        List<Integer> interpolationPoints = getInterpolationPointsForLayer(layerNumber);
        return buildSingleNoiseLayer(interpolationPoints, amplitudeGenerator.getNext(), randomSeed);
    }

    private List<Integer> getInterpolationPointsForLayer(int layerNumber) throws NoiseLayerException {
        try {
            return getNextInterpolationPointCount();
        } catch (InterpolationPointException e) {
            throw new NoiseLayerException.Builder(numberOfLayers, layerNumber).setCause(e).build();
        }
    }

    private List<Integer> getNextInterpolationPointCount() throws InterpolationPointException {
        List<Integer> interpolationPoints =
                interpolationPointCountGenerators.stream().map(NumberGenerator::getNext).collect(Collectors.toList());
        assertInterpolationPointsCountForAll(interpolationPoints);
        return interpolationPoints;
    }
}
