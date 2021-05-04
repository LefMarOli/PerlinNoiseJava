package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.dimensional.MultiDimensionalRootNoiseGenerator;

public abstract class RootLineNoiseGenerator extends MultiDimensionalRootNoiseGenerator<double[]> {

  protected RootLineNoiseGenerator(
      int noiseInterpolationPoints, double maxAmplitude, long randomSeed, boolean isCircular) {
    super(noiseInterpolationPoints, maxAmplitude, randomSeed, isCircular);
  }

  public abstract int getLineInterpolationPointsCount();
}
