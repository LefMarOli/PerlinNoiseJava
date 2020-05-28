package org.lefmaroli.perlin.point;

import org.lefmaroli.perlin.LayeredNoiseGenerator;

import java.util.List;
import java.util.Objects;

public class MultiLayerPointGenerator extends LayeredNoiseGenerator<Double[], PointNoiseGenerator>
        implements PointNoiseGenerator {

    MultiLayerPointGenerator(List<PointNoiseGenerator> layers) {
        super(layers);
    }

    @Override
    public String toString() {
        return "MultiLayerPointGenerator{" +
                "layers=" + getLayers() +
                ", maxAmplitude=" + getMaxAmplitude() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiLayerPointGenerator that = (MultiLayerPointGenerator) o;
        return Double.compare(that.getMaxAmplitude(), getMaxAmplitude()) == 0 &&
                getLayers().equals(that.getLayers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLayers(), getMaxAmplitude());
    }

    @Override
    protected Double[] initializeResults(int count) {
        Double[] results = new Double[count];
        for (int i = 0; i < count; i++) {
            results[i] = 0.0;
        }
        return results;
    }

    @Override
    protected void addToResults(Double[] layerData, Double[] results) {
        for (int i = 0; i < results.length; i++) {
            results[i] = results[i] + layerData[i];
        }
    }

    @Override
    protected Double[] normalize(Double[] results) {
        if (getMaxAmplitude() != 1.0) {
            for (int i = 0; i < results.length; i++) {
                results[i] = results[i] / getMaxAmplitude();
            }
        }
        return results;
    }
}
