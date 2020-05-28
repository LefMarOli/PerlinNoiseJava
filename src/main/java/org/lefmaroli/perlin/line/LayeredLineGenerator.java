package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.MultiDimensionalLayeredNoiseGenerator;

import java.util.List;
import java.util.Objects;

public class LayeredLineGenerator
        extends MultiDimensionalLayeredNoiseGenerator<Double[][], LineNoiseDataContainer, LineNoiseGenerator>
        implements LineNoiseGenerator {

    private final int lineLength;

    LayeredLineGenerator(List<LineNoiseGenerator> layers) {
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
        LayeredLineGenerator that = (LayeredLineGenerator) o;
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
        return "LayeredLineGenerator{" +
                "lineLength=" + lineLength +
                ", layers=" + getLayers() +
                ", maxAmplitude=" + getMaxAmplitude() +
                ", isCircular=" + isCircular() +
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
    protected LineNoiseDataContainer initializeResults(int count) {
       return new LineNoiseDataContainer(count, lineLength);
    }
}
