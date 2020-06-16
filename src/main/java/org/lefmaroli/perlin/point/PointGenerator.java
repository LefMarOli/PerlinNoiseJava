package org.lefmaroli.perlin.point;

import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.perlin.PerlinNoise;
import org.lefmaroli.perlin.RootNoiseGenerator;

public class PointGenerator extends RootNoiseGenerator<Double> implements PointNoiseGenerator {

  private static final Logger LOGGER = LogManager.getLogger(PointGenerator.class);
  private static final int MAX_NUMBER_INTERPOLATION_POINTS = 500;
  private final int noiseSegmentLength;
  private final Double[] results;
  private int currentPosition = 0;
  private final PerlinNoise perlin;
  private final double[] perlinData = new double[1];

  public PointGenerator(int interpolationPoints, double maxAmplitude, long randomSeed) {
    super(interpolationPoints, maxAmplitude, randomSeed);
    this.noiseSegmentLength = Math.min(interpolationPoints, MAX_NUMBER_INTERPOLATION_POINTS);
    results = new Double[noiseSegmentLength];
    perlin = new PerlinNoise(1);
    LOGGER.debug("Created new {}", this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public int getNoiseSegmentLength() {
    return noiseSegmentLength;
  }

  @Override
  public String toString() {
    return "PointGenerator{"
        + "noiseInterpolationPoints="
        + getNoiseInterpolationPoints()
        + ", maxAmplitude="
        + getMaxAmplitude()
        + ", randomSeed="
        + randomSeed
        + '}';
  }

  @Override
  protected Double[] generateNextSegment() {
    for (int i = 0; i < noiseSegmentLength; i++) {
      currentPosition++;
      perlinData[0] =  currentPosition * getStepSize();
      results[i] = perlin.getFor(perlinData) * getMaxAmplitude();
    }
    return results;
  }

  @Override
  protected Double[] getArrayOfSubType(int count) {
    return new Double[count];
  }
}
