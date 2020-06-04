package org.lefmaroli.perlin.point;

import java.util.Objects;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.perlin.RootNoiseGenerator;

public class PointGenerator extends RootNoiseGenerator<Double> implements PointNoiseGenerator {

  private static final Logger LOGGER = LogManager.getLogger(PointGenerator.class);
  private static final int MAX_NUMBER_INTERPOLATION_POINTS = 500;
  private final int noiseSegmentLength;
  private final Random randomGenerator;
  private final Double[] results;
  private int currentPosInInterpolation = 0;
  private double previousBound;
  private double currentBound;

  public PointGenerator(int interpolationPoints, double maxAmplitude, long randomSeed) {
    super(interpolationPoints, maxAmplitude, randomSeed);
    this.randomGenerator = new Random(randomSeed);
    this.previousBound = randomGenerator.nextDouble();
    this.currentBound = randomGenerator.nextDouble();
    this.noiseSegmentLength = Math.min(interpolationPoints, MAX_NUMBER_INTERPOLATION_POINTS);
    results = new Double[noiseSegmentLength];
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
      currentPosInInterpolation++;
      if (currentPosInInterpolation == getNoiseInterpolationPoints()) {
        previousBound = currentBound;
        currentBound = randomGenerator.nextDouble();
        currentPosInInterpolation = 0;
      }
      double relativePositionInSegment =
          currentPosInInterpolation / (double) getNoiseInterpolationPoints();
      double interpolatedValue =
          Interpolation.linearWithFade(previousBound, currentBound, relativePositionInSegment);
      results[i] = interpolatedValue * getMaxAmplitude();
    }
    return results;
  }

  @Override
  protected Double[] getArrayOfSubType(int count) {
    return new Double[count];
  }
}
