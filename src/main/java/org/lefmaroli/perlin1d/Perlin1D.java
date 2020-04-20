package org.lefmaroli.perlin1d;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.rounding.RoundUtils;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Perlin1D {

    private static final Logger LOGGER = LogManager.getLogger(Perlin1D.class);

    public Perlin1D() {
        this(32, 3);
    }

    public Perlin1D(int distance, int layers) {
        this(distance, layers, System.currentTimeMillis());
    }

    public Perlin1D(int distance) {
        this(distance, 3, System.currentTimeMillis());
    }

    Perlin1D(int distance, int layers, long seed) {
        if (distance < 4) {
            throw new IllegalArgumentException("Parameter distance must be greater than 4.");
        }
        if (layers < 1) {
            throw new IllegalArgumentException("Layers must be at least 1.");
        }
        if (!RoundUtils.isPowerOfTwo(distance)) {
            LOGGER.warn("Rounding distance to nearest power of 2");
            distance = RoundUtils.ceilToPowerOfTwo(distance);
        }
        this.distance = distance;
        this.layers = layers;
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

    public float getDistance() {
        return distance;
    }

    public List<Double> computeAtLeast(int count) {
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
        while (currentLayers > 0 && currentDistance > 4.0) {
            currentLayers--;
            currentDistance = currentDistance / 2;
            currentFactor = currentFactor / 2.0;
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

        System.out.println("currentLastRandom: " + currentLastRandom);
        System.out.println("currentLastValue: " + results.get(results.size() - 1));
        System.out.println("previousLastRandom: " + previousLastRandom);
        System.out.println("maxValue: " + maxValue);

        return results;
    }

    private List<Double> computeLayer(int toComputeCount, int distance, double factor, boolean additionalLayer) {
        List<Double> layerValues = new ArrayList<>(toComputeCount);
        List<Double> bounds = getRandomsList(getInterpolationBoundsCount(toComputeCount, distance), additionalLayer);
        for (int i = 0; i < bounds.size() - 1; i++) {
            for (int j = 0; j < distance; j++) {
                double addedValue = Interpolation.linearWithFade(bounds.get(i),
                        bounds.get(i + 1), 1.0 / distance * j) * factor;
                layerValues.add(addedValue);
            }
        }
        if (additionalLayer) {
            currentLastRandom +=
                    bounds.get(bounds.size() - 1) * factor;    //Used for current computation but never added to result
        } else {
            currentLastRandom = bounds.get(bounds.size() - 1);
        }

        return layerValues;
    }

    private List<Double> getRandomsList(int count, boolean additionalLayer) {
        List<Double> randoms = new ArrayList<>(count);
        //Always start at previously generated last boundary on new computations
        if (!additionalLayer) {
            randoms.add(previousLastRandom);
        } else {
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

    private static final int MIN_COUNT = 1000;
    private final int distance;
    private final int layers;
    private final Random random;
    private final Queue<Double> computed = new LinkedBlockingQueue<>();
    private Double currentLastRandom;
    private Double previousLastRandom;
}
