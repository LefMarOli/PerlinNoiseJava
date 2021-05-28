package org.lefmaroli.perlin.generators;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import org.lefmaroli.perlin.configuration.JitterStrategy;

public abstract class MultiDimensionalLayeredGenerator<
        N, L extends IGenerator<N> & IMultiDimensionalGenerator>
    extends LayeredGenerator<N> implements IMultiDimensionalGenerator {

  private final boolean isCircular;
  private final ForkJoinPool pool;

  protected MultiDimensionalLayeredGenerator(
      List<L> layers, ExecutorService executorService, JitterStrategy jitterStrategy) {
    super(layers, executorService, jitterStrategy);
    isCircular = checkCircularity(layers);
    ForkJoinPool joinPool = null;
    for (L layer : layers) {
      if (layer.hasParallelProcessingEnabled()) {
        joinPool = layer.getExecutionPool();
        break;
      }
    }
    this.pool = joinPool;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MultiDimensionalLayeredGenerator<?, ?> that = (MultiDimensionalLayeredGenerator<?, ?>) o;
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

  @Override
  public boolean hasParallelProcessingEnabled() {
    return pool != null && pool.getParallelism() > 1;
  }

  @Override
  public ForkJoinPool getExecutionPool() {
    return pool;
  }

  private boolean checkCircularity(List<L> layers) {
    boolean isFirstLayerCircular = layers.get(0).isCircular();
    boolean cumulativeCircularity = isFirstLayerCircular;
    for (IMultiDimensionalGenerator layer : layers) {
      if (layer.isCircular() != isFirstLayerCircular) {
        cumulativeCircularity = false;
        break;
      }
    }
    return cumulativeCircularity;
  }
}
