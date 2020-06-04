package org.lefmaroli.perlin.line;

import java.util.List;
import java.util.Objects;
import org.lefmaroli.perlin.layers.MultiDimensionalLayeredNoiseGenerator;

public class LayeredLineGenerator
    extends MultiDimensionalLayeredNoiseGenerator<double[], LineNoiseGenerator>
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
    if (!super.equals(o)) return false;
    LayeredLineGenerator that = (LayeredLineGenerator) o;
    return lineLength == that.lineLength;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), lineLength);
  }

  @Override
  public String toString() {
    return "LayeredLineGenerator{"
        + "lineLength="
        + lineLength
        + ", layers="
        + getLayers()
        + ", maxAmplitude="
        + getMaxAmplitude()
        + ", isCircular="
        + isCircular()
        + '}';
  }

  @Override
  protected double[][] initializeResults(int count) {
    return new double[count][lineLength];
  }

  @Override
  protected double[][] addTogether(double[][] results, double[][] newLayer) {
    for (int i = 0; i < results.length; i++) {
      for (int j = 0; j < results[0].length; j++) {
        results[i][j] = results[i][j] + newLayer[i][j];
      }
    }
    return results;
  }

  @Override
  protected double[][] normalizeBy(double[][] data, double maxAmplitude) {
    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data[0].length; j++) {
        data[i][j] = data[i][j] / maxAmplitude;
      }
    }
    return data;
  }

  private void assertAllLayersHaveSameLineLength(List<LineNoiseGenerator> layers) {
    for (int i = 0; i < layers.size(); i++) {
      if (layers.get(i).getLineLength() != lineLength) {
        throw new IllegalArgumentException(
            "Layer " + i + " does not have the same line length as the first provided layer");
      }
    }
  }
}
