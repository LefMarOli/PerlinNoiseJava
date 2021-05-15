package org.lefmaroli.perlin.dimensional;

import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.lefmaroli.perlin.RootNoiseGenerator;
import org.lefmaroli.rounding.RoundUtils;

public abstract class MultiDimensionalRootNoiseGenerator<C> extends RootNoiseGenerator<C>
    implements MultiDimensionalNoiseGenerator {

  private final boolean isCircular;

  protected MultiDimensionalRootNoiseGenerator(
      double noiseStepSize,
      double maxAmplitude,
      long randomSeed,
      boolean isCircular) {
    super(noiseStepSize, maxAmplitude, randomSeed);
    this.isCircular = isCircular;
  }

  @Override
  public boolean isCircular() {
    return isCircular;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MultiDimensionalRootNoiseGenerator<?> that = (MultiDimensionalRootNoiseGenerator<?>) o;
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
