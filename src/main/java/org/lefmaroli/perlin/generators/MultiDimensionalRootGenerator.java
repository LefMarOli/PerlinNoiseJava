package org.lefmaroli.perlin.generators;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import org.apache.logging.log4j.LogManager;
import org.lefmaroli.rounding.RoundUtils;

abstract class MultiDimensionalRootGenerator<C> extends RootGenerator<C>
    implements IMultiDimensionalGenerator {

  private final boolean isCircular;
  private final ForkJoinPool pool;

  protected MultiDimensionalRootGenerator(
      double timeStepSize,
      double maxAmplitude,
      long randomSeed,
      boolean isCircular,
      ForkJoinPool pool) {
    super(timeStepSize, maxAmplitude, randomSeed);
    this.isCircular = isCircular;
    this.pool = pool;
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
      if (stepSize >= 1.0) {
        throw new IllegalArgumentException(
            "Impossible to create circularity with step size greater than 1.0 for dimension: "
                + dimensionName);
      }
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
