package org.lefmaroli.perlin.generators;

import java.util.concurrent.ForkJoinPool;

abstract class LayeredMultiDimensionalBuilder<
        N,
        L extends ILayeredGenerator<N>,
        S extends IGenerator<N>,
        B extends LayeredMultiDimensionalBuilder<N, L, S, B>>
    extends LayeredBuilder<N, L, S, B> {

  private boolean isCircular = false;
  private ForkJoinPool pool = ForkJoinPool.commonPool();

  protected LayeredMultiDimensionalBuilder(int dimensions) {
    super(dimensions);
  }

  public B withForkJoinPool(ForkJoinPool pool) {
    this.pool = pool;
    return self();
  }

  public B withCircularBounds(boolean isCircular) {
    this.isCircular = isCircular;
    return self();
  }

  protected boolean isCircular() {
    return isCircular;
  }

  protected ForkJoinPool getPool() {
    return pool;
  }
}
