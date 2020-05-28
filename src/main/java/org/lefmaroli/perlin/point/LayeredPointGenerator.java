package org.lefmaroli.perlin.point;

import org.lefmaroli.perlin.LayeredNoiseGenerator;

import java.util.List;
import java.util.Objects;

public class LayeredPointGenerator extends LayeredNoiseGenerator<Double[], PointNoiseDataContainer, PointNoiseGenerator>
        implements PointNoiseGenerator {

    LayeredPointGenerator(List<PointNoiseGenerator> layers) {
        super(layers);
    }

    @Override
    public String toString() {
        return "LayeredPointGenerator{" +
                "layers=" + getLayers() +
                ", maxAmplitude=" + getMaxAmplitude() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LayeredPointGenerator that = (LayeredPointGenerator) o;
        return Double.compare(that.getMaxAmplitude(), getMaxAmplitude()) == 0 &&
                getLayers().equals(that.getLayers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLayers(), getMaxAmplitude());
    }

    @Override
    protected PointNoiseDataContainer initializeResults(int count) {
        return new PointNoiseDataContainer(count);
    }
}
