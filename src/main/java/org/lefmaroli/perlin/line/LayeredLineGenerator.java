package org.lefmaroli.perlin.line;

import java.util.List;
import java.util.Objects;
import org.lefmaroli.perlin.layers.MultiDimensionalLayeredNoiseGenerator;

public class LayeredLineGenerator
    extends MultiDimensionalLayeredNoiseGenerator<LineNoiseDataContainer, LineNoiseGenerator>
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
  protected LineNoiseDataContainer initializeResults(int count) {
    return new LineNoiseDataContainer(count, lineLength);
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
