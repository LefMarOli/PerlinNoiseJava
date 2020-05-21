package org.lefmaroli.perlin.point;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

public class MultiLayerPointGenerator extends NoisePointGenerator {

    private final static Logger LOGGER = LogManager.getLogger(MultiLayerPointGenerator.class);

    private final List<PointGenerator> layers;
    private final double maxAmplitude;

    MultiLayerPointGenerator(List<PointGenerator> layers){
        if(layers.size() < 1){
            throw new IllegalArgumentException("Number of layers must at least be 1");
        }
        this.layers = layers;
        double sum = 0.0;
        for (PointGenerator layer : layers) {
            sum += layer.getMaxAmplitude();
        }
        this.maxAmplitude = sum;
    }

    @Override
    public String toString() {
        return "MultiLayerPointGenerator{" +
                "layers=" + layers +
                ", maxAmplitude=" + maxAmplitude +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiLayerPointGenerator that = (MultiLayerPointGenerator) o;
        return Double.compare(that.maxAmplitude, maxAmplitude) == 0 &&
                layers.equals(that.layers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layers, maxAmplitude);
    }

    public int getNumberOfLayers() {
        return layers.size();
    }

    @Override
    public Double[] getNextPoints(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        }
        return generateResults(count);
    }

    @Override
    public double getMaxAmplitude() {
        return maxAmplitude;
    }

    private static Double[] initializeResults(int count) {
        Double[] results = new Double[count];
        for (int i = 0; i < count; i++) {
            results[i] = 0.0;
        }
        return results;
    }

    private Double[] generateResults(int count) {
        Double[] results = initializeResults(count);
        for (PointGenerator layer : layers) {
            Double[] layerData = layer.getNextPoints(count);
            for (int i = 0; i < count; i++) {
                results[i] = results[i] + layerData[i];
            }
        }
        return normalizeResults(results);
    }

    private Double[] normalizeResults(Double[] results) {
        if (maxAmplitude != 1.0) {
            for (int i = 0; i < results.length; i++) {
                results[i] = results[i] / maxAmplitude;
            }
        }
        return results;
    }
}
