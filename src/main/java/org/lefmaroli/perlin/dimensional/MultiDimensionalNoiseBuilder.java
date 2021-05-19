package org.lefmaroli.perlin.dimensional;

import java.util.concurrent.ForkJoinPool;
import org.lefmaroli.perlin.INoiseGenerator;
import org.lefmaroli.perlin.NoiseBuilder;

public abstract class MultiDimensionalNoiseBuilder<
        N, L extends INoiseGenerator<N>, B extends MultiDimensionalNoiseBuilder<N, L, B>>
    extends NoiseBuilder<N, L, B> {

  private boolean isCircular = false;
  private ForkJoinPool pool = ForkJoinPool.commonPool();

  protected MultiDimensionalNoiseBuilder(int dimensions) {
    super(dimensions);
  }

  public B withForkJoinPool(ForkJoinPool pool) {
    this.pool = pool;
    return self();
  }

  public B withCircularBounds() {
    isCircular = true;
    return self();
  }

  protected boolean isCircular() {
    return isCircular;
  }

  protected ForkJoinPool getPool() {
    return pool;
  }
}
