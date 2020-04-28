package org.lefmaroli.perlin1d;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.rounding.RoundUtils;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Perlin1D {

    private static final Logger LOGGER = LogManager.getLogger(Perlin1D.class);
    private static final int MIN_COUNT = 1000;
    private final int distance;
    private final int layers;
    private final int distanceFactor;
    private final double amplitudeFactor;
    private final Random random;
    private final Queue<Double> computed = new LinkedBlockingQueue<>();
    //Used for current computation but added to result in the following batch requested
    private Double currentLastRandom;
    private Double previousLastRandom;

    private Perlin1D(int distance, int layers, int distanceFactor, double amplitudeFactor, long seed) {
        this.distance = distance;
        this.layers = layers;
        this.distanceFactor = distanceFactor;
        this.amplitudeFactor = amplitudeFactor;
        this.random = new Random(seed);
        this.currentLastRandom = random.nextDouble();
        this.previousLastRandom = random.nextDouble();
    }

    public List<Double> getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        }
        if (computed.size() < count) {
            //Perform computation
            computed.addAll(computeAtLeast(count));
        }
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(computed.remove());
        }
        return result;
    }

    public Double getNext() {
        if (computed.isEmpty()) {
            computed.addAll(computeAtLeast(1));
        }
        return computed.remove();
    }

    public Double getPrevious() {
        return 0.0;
    }

    public List<Double> getPrevious(int expectedCount) {
        return new ArrayList<>();
    }

    public float getDistance() {
        return distance;
    }

    private List<Double> computeAtLeast(int count) {
        //Preprocess to save on cost of operation
        if (count < MIN_COUNT) {
            count = MIN_COUNT;
        }

        while (count < distance) {
            count *= 2;
        }

        int toComputeCount = RoundUtils.ceilToPowerOfTwo(count);
        double currentFactor = 1.0;
        int currentDistance = distance;
        List<Double> results = computeLayer(toComputeCount, currentDistance, currentFactor, false);
        int currentLayers = layers - 1;
        double maxValue = 1.0;
        while (currentLayers > 0 && currentDistance / distanceFactor > 4) {
            currentLayers--;
            currentDistance = currentDistance / distanceFactor;
            currentFactor = currentFactor / amplitudeFactor;
            maxValue += currentFactor;
            List<Double> newLayer = computeLayer(toComputeCount, currentDistance, currentFactor, true);
            for (int i = 0; i < results.size(); i++) {
                double newValue = results.get(i) + newLayer.get(i);
                results.set(i, newValue);
            }
        }

        //Normalize
        for (int i = 0; i < results.size(); i++) {
            results.set(i, results.get(i) / maxValue);
        }
        previousLastRandom =
                currentLastRandom; //Don't normalize this one, it will be factored and normalized in the next call

        return results;
    }

    private List<Double> computeLayer(int toComputeCount, int distance, double factor, boolean isAdditionalLayer) {
        List<Double> layerValues = new ArrayList<>(toComputeCount);
        List<Double> bounds = getRandomsList(getInterpolationBoundsCount(toComputeCount, distance), isAdditionalLayer);
        for (int i = 0; i < bounds.size() - 1; i++) {
            for (int j = 0; j < distance; j++) {
                double addedValue = Interpolation.linearWithFade(bounds.get(i),
                        bounds.get(i + 1), 1.0 / distance * j) * factor;
                layerValues.add(addedValue);
            }
        }
        if (isAdditionalLayer) {
            currentLastRandom +=
                    bounds.get(bounds.size() - 1) *
                            factor;
        } else {
            currentLastRandom = bounds.get(bounds.size() - 1);
        }

        return layerValues;
    }

    private List<Double> getRandomsList(int count, boolean isAdditionalLayer) {
        List<Double> randoms = new ArrayList<>(count);
        //Always start at previously generated last boundary on new computations
        if (!isAdditionalLayer) {
            randoms.add(previousLastRandom);
        } else {
            //Don't modify first value to match previously generated series
            randoms.add(0.0);
        }
        for (int i = 0; i < count - 1; i++) {
            randoms.add(random.nextDouble());
        }
        return randoms;
    }

    private int getInterpolationBoundsCount(int count, int distance) {
        return (int) Math.ceil((double) (count) / distance) + 1;
    }

    public static class Builder {
        //Default values
        private int distance = 32;
        private int layers = 3;
        private int distanceFactor = 2;
        private double amplitudeFactor = 2.0;
        private long seed = System.currentTimeMillis();

        public Builder withDistance(int distance) {
            if (distance < 4) {
                throw new IllegalArgumentException("Parameter distance must be greater than 4.");
            }
            if (!RoundUtils.isPowerOfTwo(distance)) {
                LOGGER.warn("Rounding up distance to nearest power of 2");
                distance = RoundUtils.ceilToPowerOfTwo(distance);
            }
            this.distance = distance;
            return this;
        }

        public Builder withLayers(int layers) {
            if (layers < 1) {
                throw new IllegalArgumentException("Layers must be at least 1.");
            }
            this.layers = layers;
            return this;
        }

        public Builder withDistanceFactor(int distanceFactor) {
            if (!RoundUtils.isPowerOfTwo(distanceFactor)) {
                LOGGER.warn("Rounding down distance factor to nearest power of 2");
                distanceFactor = RoundUtils.floorToPowerOfTwo(distanceFactor);
            }
            this.distanceFactor = distanceFactor;
            return this;
        }

        public Builder withAmplitudeFactor(double amplitudeFactor) {
            if (amplitudeFactor <= 1.0) {
                LOGGER.warn("Amplitude factor must be greater than 1.0. Setting to default of " + this.amplitudeFactor);
            } else {
                this.amplitudeFactor = amplitudeFactor;
            }
            return this;
        }

        public Builder withRandomGeneratorSeed(long seed) {
            this.seed = seed;
            return this;
        }

        public Perlin1D build() {
            return new Perlin1D(this.distance, this.layers, this.distanceFactor, this.amplitudeFactor, this.seed);
        }
    }
}
