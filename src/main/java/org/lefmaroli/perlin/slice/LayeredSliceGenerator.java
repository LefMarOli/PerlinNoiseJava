package org.lefmaroli.perlin.slice;

import org.lefmaroli.perlin.layers.MultiDimensionalLayeredNoiseGenerator;

import java.util.List;
import java.util.Objects;

public class LayeredSliceGenerator
        extends MultiDimensionalLayeredNoiseGenerator<SliceNoiseDataContainer, SliceNoiseGenerator>
        implements SliceNoiseGenerator {

    private final int sliceWidth;
    private final int sliceHeight;

    protected LayeredSliceGenerator(List<SliceNoiseGenerator> sliceNoiseGenerators) {
        super(sliceNoiseGenerators);
        this.sliceWidth = sliceNoiseGenerators.get(0).getSliceWidth();
        this.sliceHeight = sliceNoiseGenerators.get(0).getSliceHeight();
        assertAllLayersHaveSameSize(sliceNoiseGenerators);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LayeredSliceGenerator that = (LayeredSliceGenerator) o;
        return sliceWidth == that.sliceWidth &&
                sliceHeight == that.sliceHeight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sliceWidth, sliceHeight);
    }

    @Override
    public String toString() {
        return "LayeredSliceGenerator{" +
                "sliceWidth=" + sliceWidth +
                ", sliceHeight=" + sliceHeight +
                ", layers=" + getLayers() +
                ", maxAmplitude=" + getMaxAmplitude() +
                ", isCircular=" + isCircular() +
                '}';
    }

    @Override
    public int getSliceWidth() {
        return sliceWidth;
    }

    @Override
    public int getSliceHeight() {
        return sliceHeight;
    }

    @Override
    protected SliceNoiseDataContainer initializeResults(int count) {
        return new SliceNoiseDataContainer(count, getSliceWidth(), getSliceHeight());
    }

    private void assertAllLayersHaveSameSize(List<SliceNoiseGenerator> layers) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).getSliceWidth() != sliceWidth) {
                throw new IllegalArgumentException(
                        "Layer " + i + " does not have the same slice width as the first provided layer");
            }
            if (layers.get(i).getSliceHeight() != sliceHeight) {
                throw new IllegalArgumentException(
                        "Layer " + i + " does not have the same slice height as the first provided layer");
            }
        }
    }
}
