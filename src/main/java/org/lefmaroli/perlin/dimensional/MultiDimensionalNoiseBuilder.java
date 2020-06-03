package org.lefmaroli.perlin.dimensional;

import org.lefmaroli.perlin.INoiseGenerator;
import org.lefmaroli.perlin.NoiseBuilder;
import org.lefmaroli.perlin.data.NoiseData;

public abstract class MultiDimensionalNoiseBuilder<
        N extends NoiseData<?, N>,
        L extends INoiseGenerator<N>,
        B extends MultiDimensionalNoiseBuilder<N, L, B>>
    extends NoiseBuilder<N, L, B> {

  private boolean isCircular = false;

  public MultiDimensionalNoiseBuilder(int dimensions) {
    super(dimensions);
  }

  public B withCircularBounds() {
    isCircular = true;
    return self();
  }

  protected boolean isCircular() {
    return isCircular;
  }
}
