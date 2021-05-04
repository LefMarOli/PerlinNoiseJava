package org.lefmaroli.perlin.layers;

import java.util.List;
import java.util.Objects;
import org.lefmaroli.execution.ExecutorServiceScheduler;
import org.lefmaroli.perlin.INoiseGenerator;
import org.lefmaroli.perlin.dimensional.MultiDimensionalNoiseGenerator;

public abstract class MultiDimensionalLayeredNoiseGenerator<
        N, L extends INoiseGenerator<N> & MultiDimensionalNoiseGenerator>
    extends LayeredNoiseGenerator<N, L> implements MultiDimensionalNoiseGenerator {

  private final boolean isCircular;

  protected MultiDimensionalLayeredNoiseGenerator(List<L> layers) {
    super(layers);
    isCircular = checkCircularity(layers);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MultiDimensionalLayeredNoiseGenerator<?, ?> that =
        (MultiDimensionalLayeredNoiseGenerator<?, ?>) o;
    return isCircular == that.isCircular;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), isCircular);
  }

  @Override
  public boolean isCircular() {
    return isCircular;
  }

  private boolean checkCircularity(List<L> layers) {
    boolean isFirstLayerCircular = layers.get(0).isCircular();
    boolean cumulativeCircularity = isFirstLayerCircular;
    for (MultiDimensionalNoiseGenerator layer : layers) {
      if (layer.isCircular() != isFirstLayerCircular) {
        cumulativeCircularity = false;
        break;
      }
    }
    return cumulativeCircularity;
  }
}
