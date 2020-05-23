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

public abstract class NoiseBuilder<NoiseType extends NoiseGenerator,
        NoiseBuilderType extends NoiseBuilder> {
    private static final Logger LOGGER = LogManager.getLogger(NoiseBuilder.class);
    private static final int INTERPOLATION_POINTS_UPPER_LIMIT = 50000;
    private static final NumberGenerator<Integer>
            DEFAULT_DISTANCE_GENERATOR = new IntegerGenerator(64, 0.5);
    private final NoiseBuilderType thisObj;
    private final int dimensions;
    protected int numberOfLayers = 5;
    protected long randomSeed = System.currentTimeMillis();
    private final List<NumberGenerator<Integer>> distanceGenerators;
    private NumberGenerator<Double> amplitudeGenerator = new DoubleGenerator(1.0, 0.5);

    public NoiseBuilder(int dimensions) {
        thisObj = self();
        this.dimensions = dimensions;
        this.distanceGenerators = new ArrayList<>(dimensions);
        for (int i = 0; i < dimensions; i++) {
            this.distanceGenerators.add(DEFAULT_DISTANCE_GENERATOR.getCopy());
        }
    }

    public NoiseBuilderType withNoiseDistanceGenerator(NumberGenerator<Integer> distanceGenerator) {
        setDistanceGeneratorForDimension(1, distanceGenerator);
        return thisObj;
    }

    public NoiseBuilderType withAmplitudeGenerator(NumberGenerator<Double> amplitudeGenerator) {
        this.amplitudeGenerator = amplitudeGenerator;
        return thisObj;
    }

    public NoiseBuilderType withRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
        return thisObj;
    }

    public NoiseBuilderType withNumberOfLayers(int numberOfLayers) {
        if (numberOfLayers < 1) {
            throw new IllegalArgumentException("Number of layers must be at least 1");
        }
        this.numberOfLayers = numberOfLayers;
        return thisObj;
    }

    public NoiseGenerator build() throws NoiseBuilderException {
        resetNumberGenerators();
        if (numberOfLayers == 1) {
            try {
                return buildSingleLayerNoise(getNextInterpolationPointCount(), amplitudeGenerator.getNext(),
                        randomSeed);
            } catch (InterpolationPointException e) {
                throw new NoiseBuilderException(e);
            }
        } else {
            return buildMultipleLayerNoise(generateNoiseLayers());
        }
    }

    protected void setDistanceGeneratorForDimension(int dimension, NumberGenerator<Integer> distanceGenerator) {
        if (dimension > this.dimensions) {
            String dimensionRange = this.dimensions == 1 ? "1" : "1-" + this.dimensions;
            throw new IllegalArgumentException(
                    "Dimension " + dimension + " is higher than the supported [" + dimensionRange +
                            "] dimensions for this builder.");
        } else {
            this.distanceGenerators.set(dimension - 1, distanceGenerator);
        }
    }

    protected abstract NoiseBuilderType self();

    protected abstract NoiseType buildSingleLayerNoise(List<Integer> interpolationPoints, double layerAmplitude,
                                                       long randomSeed)
            throws NoiseBuilderException;

    protected abstract NoiseType buildMultipleLayerNoise(List<NoiseType> layers)
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
        for (NumberGenerator<Integer> distanceGenerator : distanceGenerators) {
            distanceGenerator.reset();
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
        return buildSingleLayerNoise(interpolationPoints, amplitudeGenerator.getNext(), randomSeed);
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
                distanceGenerators.stream().map(NumberGenerator::getNext).collect(Collectors.toList());
        assertInterpolationPointsCountForAll(interpolationPoints);
        return interpolationPoints;
    }
}
