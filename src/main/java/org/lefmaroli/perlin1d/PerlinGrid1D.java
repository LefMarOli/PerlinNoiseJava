package org.lefmaroli.perlin1d;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.factorgenerator.FactorGenerator;
import org.lefmaroli.factorgenerator.MultiplierFactorGenerator;

import java.util.*;

public class PerlinGrid1D {

    private final static Logger LOGGER = LogManager.getLogger(PerlinGrid1D.class);

    private final List<PerlinLayer1D> layers;
    private double maxAmplitude;

    private PerlinGrid1D(int numberOfLayers, FactorGenerator distanceFactorGenerator,
                         FactorGenerator amplitudeFactorGenerator, long seed) {
        this.layers = new ArrayList<>(numberOfLayers);
        this.maxAmplitude = 0.0;
        generateNoiseLayers(numberOfLayers, distanceFactorGenerator, amplitudeFactorGenerator, seed);
        LOGGER.debug("Generated random grid with " + layers.size() + " and max amplitude of " + maxAmplitude);
    }

    public int getNumberOfLayers() {
        return layers.size();
    }

    public Vector<Double> getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        }
        Vector<Double> results = initializeResults(count);
        addNoiseLayersContributions(count, results);
        normalizeResults(results);
        return results;
    }

    private void generateNoiseLayers(int numberOfLayers, FactorGenerator distanceFactorGenerator,
                                     FactorGenerator amplitudeFactorGenerator, long seed) {
        Random random = new Random(seed);
        double amplitude;
        int interpolationPoints;
        for (int i = 0; i < numberOfLayers; i++) {
            amplitude = amplitudeFactorGenerator.getNextFactor();
            interpolationPoints = (int) distanceFactorGenerator.getNextFactor();
            if (interpolationPoints < 1) {
                int remainingLayers = numberOfLayers - i;
                LOGGER.warn("Skipping generation of " + remainingLayers +
                        ", no more interpolation possible. Current layer: " + i +
                        ", please check provided distanceFactorGenerator");
                break;
            }
            layers.add(new PerlinLayer1D(interpolationPoints, amplitude, random.nextLong()));
            maxAmplitude += amplitude;
        }
    }

    private Vector<Double> initializeResults(int count) {
        Vector<Double> results = new Vector<>(count);
        for (int i = 0; i < count; i++) {
            results.add(0.0);
        }
        return results;
    }

    private void addNoiseLayersContributions(int count, Vector<Double> results) {
        for (PerlinLayer1D layer : layers) {
            Vector<Double> layerData = layer.getNext(count);
            for (int i = 0; i < count; i++) {
                results.set(i, results.get(i) + layerData.get(i));
            }
        }
    }

    private void normalizeResults(Vector<Double> results) {
        //Normalize
        if (maxAmplitude != 1.0) {
            for (int i = 0; i < results.size(); i++) {
                results.set(i, results.get(i) / maxAmplitude);
            }
        }
    }

    public static class Builder {
        private int numberOfLayers = 5;
        private FactorGenerator distanceFactorGenerator = new MultiplierFactorGenerator(1.0, 0.5);
        private FactorGenerator amplitudeFactorGenerator = new MultiplierFactorGenerator(1.0, 0.5);
        private long randomSeed = System.currentTimeMillis();

        public Builder withNumberOfLayers(int numberOfLayers) {
            if (numberOfLayers < 1) {
                throw new IllegalArgumentException("Number of layers must be at least 1");
            }
            this.numberOfLayers = numberOfLayers;
            return this;
        }

        public Builder withDistanceFactorGenerator(FactorGenerator distanceFactorGenerator) {
            this.distanceFactorGenerator = distanceFactorGenerator;
            return this;
        }

        public Builder withAmplitudeFactorGenerator(FactorGenerator amplitudeFactorGenerator) {
            this.amplitudeFactorGenerator = amplitudeFactorGenerator;
            return this;
        }

        public Builder withRandomSeed(long randomSeed) {
            this.randomSeed = randomSeed;
            return this;
        }

        public PerlinGrid1D build() {
            return new PerlinGrid1D(this.numberOfLayers, this.distanceFactorGenerator, this.amplitudeFactorGenerator,
                    this.randomSeed);
        }
    }
}
