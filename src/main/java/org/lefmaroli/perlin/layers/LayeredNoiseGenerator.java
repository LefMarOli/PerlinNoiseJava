package org.lefmaroli.perlin.layers;

import java.util.List;
import java.util.Objects;
import org.lefmaroli.perlin.INoiseGenerator;

public abstract class LayeredNoiseGenerator<N, L extends INoiseGenerator<N>>
    implements INoiseGenerator<N> {

  private final double maxAmplitude;
  private final List<L> layers;

  protected LayeredNoiseGenerator(List<L> layers) {
    if (layers.isEmpty()) {
      throw new IllegalArgumentException("Number of layers must at least be 1");
    }
    this.layers = layers;
    var sum = 0.0;
    for (L layer : layers) {
      sum += layer.getMaxAmplitude();
    }
    this.maxAmplitude = sum;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LayeredNoiseGenerator<?, ?> that = (LayeredNoiseGenerator<?, ?>) o;
    return Double.compare(that.maxAmplitude, maxAmplitude) == 0
        && Objects.equals(layers, that.layers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxAmplitude, layers);
  }

  @Override
  public N getNext() {
    var results = getContainer();
    for (L layer : layers) {
      results = addTogether(results, layer.getNext());
    }
    return normalizeBy(results, maxAmplitude);
  }

  public double getMaxAmplitude() {
    return maxAmplitude;
  }

  public int getNumberOfLayers() {
    return layers.size();
  }

  protected List<L> getLayers() {
    return layers;
  }

  protected abstract N getContainer();

  protected abstract N addTogether(N results, N newLayer);

  protected abstract N normalizeBy(N data, double maxAmplitude);
}
