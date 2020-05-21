package org.lefmaroli.perlin.line;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

public class MultiLayerLineGenerator extends NoiseLineGenerator {

    private final static Logger LOGGER = LogManager.getLogger(MultiLayerLineGenerator.class);

    private final int lineLength;
    private final List<NoiseLineGenerator> layers;
    private final double maxAmplitude;

    MultiLayerLineGenerator(List<NoiseLineGenerator> layers) {
        if (layers.size() < 1) {
            throw new IllegalArgumentException("Number of layers must at least be 1");
        }
        this.lineLength = layers.get(0).getLineLength();
        assertAllLayersHaveSameLineLength(this.lineLength, layers);
        this.layers = layers;
        double sum = 0.0;
        for (NoiseLineGenerator layer : layers) {
            sum += layer.getMaxAmplitude();
        }
        this.maxAmplitude = sum;
    }

    public int getNumberOfLayers() {
        return layers.size();
    }

    @Override
    public Double[][] getNextLines(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Parameter count must be greater than 0");
        }
        return generateResults(count);
    }

    @Override
    public int getLineLength() {
        return lineLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiLayerLineGenerator that = (MultiLayerLineGenerator) o;
        return lineLength == that.lineLength &&
                Double.compare(that.maxAmplitude, maxAmplitude) == 0 &&
                layers.equals(that.layers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineLength, layers, maxAmplitude);
    }

    @Override
    public double getMaxAmplitude() {
        return maxAmplitude;
    }

    @Override
    public String toString() {
        return "MultiLayerLineGenerator{" +
                "lineLength=" + lineLength +
                ", layers=" + layers +
                ", maxAmplitude=" + maxAmplitude +
                '}';
    }

    private static void assertAllLayersHaveSameLineLength(int lineLength, List<NoiseLineGenerator> layers) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).getLineLength() != lineLength) {
                throw new IllegalArgumentException(
                        "Layer " + i + " does not have the same line length as the first provided layer");
            }
        }
    }

    private Double[][] generateResults(int count) {
        Double[][] results = initializeResults(count);
        for (NoiseLineGenerator layer : layers) {
            Double[][] layerData = layer.getNextLines(count);
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < lineLength; j++) {
                    results[i][j] = results[i][j] + layerData[i][j];
                }
            }
        }
        return normalizeResults(results);
    }

    private Double[][] initializeResults(int count) {
        Double[][] results = new Double[count][lineLength];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < lineLength; j++) {
                results[i][j] = 0.0;
            }
        }
        return results;
    }

    private Double[][] normalizeResults(Double[][] results) {
        if (maxAmplitude != 1.0) {
            for (int i = 0; i < results.length; i++) {
                for (int j = 0; j < lineLength; j++) {
                    results[i][j] = results[i][j] / maxAmplitude;
                }
            }
        }
        return results;
    }
}
