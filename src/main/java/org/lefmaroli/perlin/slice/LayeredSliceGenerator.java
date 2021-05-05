package org.lefmaroli.perlin.slice;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.lefmaroli.perlin.layers.MultiDimensionalLayeredNoiseGenerator;

public class LayeredSliceGenerator
    extends MultiDimensionalLayeredNoiseGenerator<double[][], SliceNoiseGenerator>
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
    return sliceWidth == that.sliceWidth && sliceHeight == that.sliceHeight;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), sliceWidth, sliceHeight);
  }

  @Override
  public String toString() {
    return "LayeredSliceGenerator{"
        + "sliceWidth="
        + sliceWidth
        + ", sliceHeight="
        + sliceHeight
        + ", layers="
        + getLayers()
        + ", maxAmplitude="
        + getMaxAmplitude()
        + ", isCircular="
        + isCircular()
        + '}';
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
  protected double[][] getNewContainer() {
    return new double[getSliceWidth()][getSliceHeight()];
  }

  @Override
  protected double[][] resetContainer(double[][] container) {
    for (double[] rows : container) {
      Arrays.fill(rows, 0.0);
    }
    return container;
  }

  @Override
  protected double[][] addTogether(double[][] results, double[][] newLayer) {
    for (var i = 0; i < results.length; i++) {
      for (var j = 0; j < results[0].length; j++) {
        results[i][j] = results[i][j] + newLayer[i][j];
      }
    }
    return results;
  }

  @Override
  protected double[][] normalizeBy(double[][] data, double maxAmplitude) {
    for (var i = 0; i < data.length; i++) {
      for (var j = 0; j < data[0].length; j++) {
        data[i][j] = data[i][j] / maxAmplitude;
      }
    }
    return data;
  }

  private void assertAllLayersHaveSameSize(List<SliceNoiseGenerator> layers) {
    for (var i = 0; i < layers.size(); i++) {
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
