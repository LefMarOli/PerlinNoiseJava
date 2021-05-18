package org.lefmaroli.perlin.dimensional;

import java.util.concurrent.Executors;
import org.lefmaroli.execution.ExecutorPool;
import org.lefmaroli.perlin.INoiseGenerator;
import org.lefmaroli.perlin.NoiseBuilder;

public abstract class MultiDimensionalNoiseBuilder<
        N, L extends INoiseGenerator<N>, B extends MultiDimensionalNoiseBuilder<N, L, B>>
    extends NoiseBuilder<N, L, B> {

  private boolean isCircular = false;
  private ExecutorPool executorPool =
      new ExecutorPool(this.getClass().getName(), Executors.newWorkStealingPool(1));

  protected MultiDimensionalNoiseBuilder(int dimensions) {
    super(dimensions);
  }

  public B withCircularBounds() {
    isCircular = true;
    return self();
  }

  public B withExecutorPool(ExecutorPool executorPool) {
    this.executorPool = executorPool;
    return self();
  }

  protected boolean isCircular() {
    return isCircular;
  }

  protected ExecutorPool getExecutorPool() {
    return executorPool;
  }

  protected boolean isParallel() {
    return executorPool != null;
  }
}
