package org.lefmaroli.perlin.generators.multidimensional;

import java.util.concurrent.ForkJoinPool;
import org.lefmaroli.perlin.generators.IGenerator;
import org.lefmaroli.perlin.generators.RootBuilder;

public abstract class MultiDimensionalBuilder<
        N, L extends IGenerator<N>, B extends MultiDimensionalBuilder<N, L, B>>
    extends RootBuilder<N, L, B> {

  private boolean isCircular = false;
  private ForkJoinPool pool = ForkJoinPool.commonPool();

  protected MultiDimensionalBuilder(int dimensions) {
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
