package org.lefmaroli.perlin.dimensional;

import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.lefmaroli.perlin.RootNoiseGenerator;
import org.lefmaroli.rounding.RoundUtils;

public abstract class MultiDimensionalRootNoiseGenerator<C> extends RootNoiseGenerator<C>
    implements MultiDimensionalNoiseGenerator {

  protected static final int MB_10 = 10 * 1024 * 1024;
  protected static final int MB_10_IN_DOUBLES_SIZE = MB_10 / 8;

  private final boolean isCircular;

  protected MultiDimensionalRootNoiseGenerator(
      int noiseInterpolationPoints, double maxAmplitude, long randomSeed, boolean isCircular) {
    super(noiseInterpolationPoints, maxAmplitude, randomSeed);
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

  protected int correctInterpolationPointsForCircularity(
      int interpolationPoints, int dimensionLength, String dimensionName) {
    if (isCircular) {
      var toEvaluate = Math.min(interpolationPoints, dimensionLength);
      var newInterpolationPoints = RoundUtils.roundNToClosestFactorOfM(toEvaluate, dimensionLength);
      LogManager.getLogger(this.getClass())
          .warn(
              "Modified required interpolation point count for {} from {} to {} to respect"
                  + " circularity.",
              dimensionName,
              interpolationPoints,
              newInterpolationPoints);
      return newInterpolationPoints;
    } else {
      return interpolationPoints;
    }
  }
}
