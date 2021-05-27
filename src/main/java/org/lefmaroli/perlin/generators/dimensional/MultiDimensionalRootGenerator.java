package org.lefmaroli.perlin.generators.dimensional;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import org.apache.logging.log4j.LogManager;
import org.lefmaroli.perlin.generators.RootGenerator;
import org.lefmaroli.rounding.RoundUtils;

public abstract class MultiDimensionalRootGenerator<C> extends RootGenerator<C>
    implements IMultiDimensionalGenerator {

  private final boolean isCircular;
  private final ForkJoinPool pool;
  private final int numberAvailableProcessors;

  protected MultiDimensionalRootGenerator(
      double noiseStepSize,
      double maxAmplitude,
      long randomSeed,
      boolean isCircular,
      ForkJoinPool pool) {
    super(noiseStepSize, maxAmplitude, randomSeed);
    this.isCircular = isCircular;
    this.pool = pool;
    this.numberAvailableProcessors = Runtime.getRuntime().availableProcessors();
  }

  @Override
  public boolean isCircular() {
    return isCircular;
  }

  @Override
  public boolean hasParallelProcessingEnabled() {
    return pool != null && numberAvailableProcessors > 1;
  }

  @Override
  public ForkJoinPool getExecutionPool() {
    return pool;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MultiDimensionalRootGenerator<?> that = (MultiDimensionalRootGenerator<?>) o;
    return isCircular == that.isCircular;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), isCircular);
  }

  protected double correctStepSizeForCircularity(
      double stepSize, int dimensionLength, String dimensionName) {
    if (isCircular) {
      var interpolationPoints = (int) Math.round(1.0 / stepSize);
      var toEvaluate = Math.min(interpolationPoints, dimensionLength);
      var newInterpolationPoints = RoundUtils.roundNToClosestFactorOfM(toEvaluate, dimensionLength);
      if (interpolationPoints != newInterpolationPoints) {
        LogManager.getLogger(this.getClass())
            .warn(
                "Modified required step size for {} from {} to {} to respect" + " circularity.",
                dimensionName,
                stepSize,
                1.0 / newInterpolationPoints);
      }
      return 1.0 / newInterpolationPoints;
    } else {
      return stepSize;
    }
  }
}
