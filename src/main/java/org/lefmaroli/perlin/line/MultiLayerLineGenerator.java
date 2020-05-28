package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.LayeredNoiseGenerator;

import java.util.List;
import java.util.Objects;

public class MultiLayerLineGenerator extends LayeredNoiseGenerator<Double[][], LineNoiseGenerator>
        implements LineNoiseGenerator {

    private final int lineLength;

    MultiLayerLineGenerator(List<LineNoiseGenerator> layers) {
        super(layers);
        this.lineLength = layers.get(0).getLineLength();
        assertAllLayersHaveSameLineLength(layers);
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
                Double.compare(that.getMaxAmplitude(), getMaxAmplitude()) == 0 &&
                getLayers().equals(that.getLayers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineLength, getLayers(), getMaxAmplitude());
    }

    @Override
    public String toString() {
        return "MultiLayerLineGenerator{" +
                "lineLength=" + lineLength +
                ", layers=" + getLayers() +
                ", maxAmplitude=" + getMaxAmplitude() +
                '}';
    }

    private void assertAllLayersHaveSameLineLength(List<LineNoiseGenerator> layers) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).getLineLength() != lineLength) {
                throw new IllegalArgumentException(
                        "Layer " + i + " does not have the same line length as the first provided layer");
            }
        }
    }

    @Override
    protected void addToResults(Double[][] layerData, Double[][] results) {
        for (int i = 0; i < layerData.length; i++) {
            for (int j = 0; j < lineLength; j++) {
                results[i][j] = results[i][j] + layerData[i][j];
            }
        }
    }

    @Override
    protected Double[][] initializeResults(int count) {
        Double[][] results = new Double[count][lineLength];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < lineLength; j++) {
                results[i][j] = 0.0;
            }
        }
        return results;
    }

    @Override
    protected Double[][] normalize(Double[][] results) {
        double maxAmplitude = getMaxAmplitude();
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
