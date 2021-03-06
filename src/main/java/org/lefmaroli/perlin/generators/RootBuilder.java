package org.lefmaroli.perlin.generators;

import org.lefmaroli.perlin.configuration.JitterStrategy;
import org.lefmaroli.perlin.configuration.ProductionJitterStrategy;

abstract class RootBuilder<N, L extends IGenerator<N>, B extends RootBuilder<N, L, B>> {

  private final int dimensions;
  private static final double DEFAULT_STEP_SIZE = 0.01;
  private final double[] stepSizes;
  private double amplitude = 1.0;
  protected long randomSeed = System.currentTimeMillis();
  protected JitterStrategy jitterStrategy = ProductionJitterStrategy.getInstance();

  protected RootBuilder(int dimensions) {
    if (dimensions < 1 || dimensions > 3) {
      throw new IllegalArgumentException(
          "Number of dimensions must be between [1, 3], provided: " + dimensions);
    }
    this.dimensions = dimensions;
    this.stepSizes = new double[dimensions];
    for (var i = 0; i < dimensions; i++) {
      this.stepSizes[i] = DEFAULT_STEP_SIZE;
    }
  }

  public B withRandomSeed(long randomSeed) {
    this.randomSeed = randomSeed;
    return self();
  }

  public B withTimeStepSize(double timeStepSize) throws StepSizeException {
    assertStepSize(timeStepSize);
    stepSizes[0] = timeStepSize;
    return self();
  }

  B withJitterStrategy(JitterStrategy jitterStrategy) {
    this.jitterStrategy = jitterStrategy;
    return self();
  }

  protected void setStepSizeForDimension(double stepSize, int dimensionIndex)
      throws StepSizeException {
    if (dimensionIndex < 0 || dimensionIndex >= this.dimensions) {
      throw new IllegalArgumentException(
          "Number of dimensions must be between [0, "
              + (this.dimensions - 1)
              + "], provided: "
              + dimensionIndex);
    }
    assertStepSize(stepSize);
    stepSizes[dimensionIndex] = stepSize;
  }

  public B withAmplitude(double amplitude) {
    this.amplitude = amplitude;
    return self();
  }

  public IGenerator<N> build() {
    return buildNoiseGenerator(stepSizes, amplitude, randomSeed, jitterStrategy);
  }

  protected abstract B self();

  protected abstract IGenerator<N> buildNoiseGenerator(
      double[] stepSizes, double amplitude, long randomSeed, JitterStrategy jitterStrategy);

  protected static void assertStepSize(double stepSize) throws StepSizeException {
    if (Double.compare(stepSize, 0.0) < 0 || Double.compare(stepSize, 0.0) == 0) {
      throw new StepSizeException();
    }
  }
}
