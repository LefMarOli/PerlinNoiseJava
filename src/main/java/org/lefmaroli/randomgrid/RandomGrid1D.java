package org.lefmaroli.randomgrid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.factorgenerator.FactorGenerator;

import java.util.*;

public class RandomGrid1D {

    private final static Logger LOGGER = LogManager.getLogger(RandomGrid1D.class);

    private final List<RandomLayer1D> layers;
    private double maxAmplitude;

    public RandomGrid1D(int numLayers, FactorGenerator distanceFactorGenerator,
                 FactorGenerator amplitudeFactorGenerator, long seed) {
        if (numLayers < 1) {
            throw new IllegalArgumentException("Number of layers must be at least 1");
        }
        Random random = new Random(seed);
        this.layers = new ArrayList<>(numLayers);
        maxAmplitude = 0.0;
        double amplitude;
        int interpolationPoints;
        for (int i = 0; i < numLayers; i++) {
            amplitude = amplitudeFactorGenerator.getNextFactor();
            interpolationPoints = (int) distanceFactorGenerator.getNextFactor();
            if (interpolationPoints < 1) {
                int remainingLayers = numLayers - i;
                LOGGER.warn("Skipping generation of " + remainingLayers +
                        ", no more interpolation possible. Current layer: " + i +
                        ", please check provided distanceFactorGenerator");
                break;
            }
            LOGGER.debug("Generating layer with amplitude factor of " + amplitude + " and " + interpolationPoints +
                    " interpolation points.");
            layers.add(new RandomLayer1D(interpolationPoints, amplitude, random.nextLong()));
            maxAmplitude += amplitude;
        }
        LOGGER.debug("Generated random grid with " + layers.size() + " and max amplitude of " + maxAmplitude);
    }

    public int getNumberOfLayers(){
        return layers.size();
    }

    public Vector<Double> getNext(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        }

        Vector<Double> results = new Vector<>(count);
        for (int i = 0; i < count; i++) {
            results.add(0.0);
        }
        for (RandomLayer1D layer : layers) {
            Vector<Double> layerData = layer.getNext(count);
            for (int i = 0; i < count; i++) {
                results.set(i, results.get(i) + layerData.get(i));
            }
        }

        //Normalize
        if (maxAmplitude != 1.0) {
            for (int i = 0; i < count; i++) {
                results.set(i, results.get(i) / maxAmplitude);
            }
        }

        return results;
    }

}
