package org.lefmaroli.perlin;

import org.lefmaroli.factorgenerator.FactorGenerator;
import org.lefmaroli.factorgenerator.MultiplierFactorGenerator;
import org.lefmaroli.perlin.exceptions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class NoiseBuilder<NoiseType extends NoiseGenerator, LayerType extends NoiseType,
        NoiseBuilderType extends NoiseBuilder> {
    private static final int INTERPOLATION_POINTS_UPPER_LIMIT = 50000;
    private final NoiseBuilderType thisObj;
    protected int numberOfLayers = 5;
    protected long randomSeed = System.currentTimeMillis();
    private FactorGenerator distanceFactorGenerator = new MultiplierFactorGenerator(64, 0.5);
    private FactorGenerator amplitudeFactorGenerator = new MultiplierFactorGenerator(1.0, 0.5);

    public NoiseBuilder() {
        thisObj = self();
    }

    public NoiseBuilderType withDistanceFactorGenerator(FactorGenerator distanceFactorGenerator) {
        this.distanceFactorGenerator = distanceFactorGenerator;
        return thisObj;
    }

    public NoiseBuilderType withAmplitudeFactorGenerator(FactorGenerator amplitudeFactorGenerator) {
        this.amplitudeFactorGenerator = amplitudeFactorGenerator;
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
        amplitudeFactorGenerator.reset();
        distanceFactorGenerator.reset();
        if (numberOfLayers == 1) {
            try {
                return buildSingleLayerNoise(getNextInterpolationPointCount(), amplitudeFactorGenerator.getNextFactor(), randomSeed);
            } catch (InterpolationPointException e) {
                throw new NoiseBuilderException(e);
            }
        } else {
            return buildMultipleLayerNoise(generateNoiseLayers());
        }
    }

    protected abstract NoiseBuilderType self();

    protected abstract LayerType buildSingleLayerNoise(int interpolationPoints, double layerAmplitude, long randomSeed)
            throws NoiseBuilderException;

    protected abstract NoiseType buildMultipleLayerNoise(List<LayerType> layers)
            throws NoiseBuilderException;

    private List<LayerType> generateNoiseLayers() throws NoiseBuilderException {
        Random randomGenerator = new Random(randomSeed);
        List<LayerType> layers = new ArrayList<>(numberOfLayers);
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

    private LayerType generateNoiseLayer(int layerNumber, long randomSeed)
            throws NoiseLayerException, NoiseBuilderException {
        int interpolationPoints = getInterpolationPointsForLayer(layerNumber);
        return buildSingleLayerNoise(interpolationPoints, amplitudeFactorGenerator.getNextFactor(), randomSeed);
    }

    private int getInterpolationPointsForLayer(int layerNumber) throws NoiseLayerException {
        try {
            return getNextInterpolationPointCount();
        } catch (InterpolationPointException e) {
            throw new NoiseLayerException.Builder(numberOfLayers, layerNumber).setCause(e).build();
        }
    }

    private int getNextInterpolationPointCount() throws InterpolationPointException {
        int interpolationPoints = (int) distanceFactorGenerator.getNextFactor();
        assertInterpolationPointsCount(interpolationPoints);
        return interpolationPoints;
    }

    private static void assertInterpolationPointsCount(int interpolationPoints) throws InterpolationPointException {
        if (interpolationPoints < 1) {
            throw new NoInterpolationPointException();
        } else if (interpolationPoints > INTERPOLATION_POINTS_UPPER_LIMIT) {
            throw new TooManyInterpolationPointsException(interpolationPoints, INTERPOLATION_POINTS_UPPER_LIMIT);
        }
    }
}
