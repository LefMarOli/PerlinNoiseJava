package org.lefmaroli.perlin.line;

import org.lefmaroli.perlin.dimensional.MultiDimensionalRootNoiseGenerator;

public abstract class RootLineNoiseGenerator extends MultiDimensionalRootNoiseGenerator<double[]> {

  protected RootLineNoiseGenerator(
      double noiseStepSize, double maxAmplitude, long randomSeed, boolean isCircular) {
    super(noiseStepSize, maxAmplitude, randomSeed, isCircular);
  }

  public abstract double getLineStepSize();
}
