package org.lefmaroli.perlin.line;

import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.perlin.PerlinNoise;

public class LineGenerator extends RootLineNoiseGenerator implements LineNoiseGenerator {

  private static final Logger LOGGER = LogManager.getLogger(LineGenerator.class);
  private static final List<String> parameterNames =
      List.of("Line interpolation points", "Line length");

  private final int lineInterpolationPoints;
  private final int lineLength;
  private final int noiseSegmentLength;
  private int currentPosition = 0;
  private final double circularResolution;
  private final PerlinNoise perlin;
  private final double[] perlinData;
  private final double[][] segmentResult;

  public LineGenerator(
      int noiseInterpolationPoints,
      int lineInterpolationPoints,
      int lineLength,
      double maxAmplitude,
      long randomSeed,
      boolean isCircular) {
    super(noiseInterpolationPoints, maxAmplitude, randomSeed, isCircular);
    assertValidValues(parameterNames, lineInterpolationPoints, lineLength);
    this.lineLength = lineLength;
    this.lineInterpolationPoints =
        correctInterpolationPointsForCircularity(
            lineInterpolationPoints, lineLength, "line length");
    this.noiseSegmentLength = computeNoiseSegmentLength(lineLength);
    this.segmentResult = new double[noiseSegmentLength][lineLength];
    this.circularResolution = this.lineInterpolationPoints / (double) this.lineLength;
    if (isCircular) {
      perlin = new PerlinNoise(3);
      perlinData = new double[3];
    } else {
      perlin = new PerlinNoise(2);
      perlinData = new double[2];
    }
    LOGGER.debug("Created new {}", this);
  }

  @Override
  public int getLineLength() {
    return lineLength;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LineGenerator that = (LineGenerator) o;
    return lineInterpolationPoints == that.lineInterpolationPoints && lineLength == that.lineLength;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), lineInterpolationPoints, lineLength);
  }

  @Override
  public String toString() {
    return "LineGenerator{"
        + "noiseInterpolationPoints="
        + getNoiseInterpolationPoints()
        + ", lineInterpolationPoints="
        + lineInterpolationPoints
        + ", lineLength="
        + lineLength
        + ", maxAmplitude="
        + getMaxAmplitude()
        + ", randomSeed="
        + randomSeed
        + ", isCircular="
        + isCircular()
        + '}';
  }

  @Override
  public int getLineInterpolationPointsCount() {
    return lineInterpolationPoints;
  }

  @Override
  public int getNoiseSegmentLength() {
    return noiseSegmentLength;
  }

  @Override
  protected double[][] generateNextSegment() {
    for (var i = 0; i < noiseSegmentLength; i++) {
      currentPosition++;
      processNoiseDomain(currentPosition, segmentResult[i]);
    }
    return segmentResult;
  }

  private int computeNoiseSegmentLength(int lineLength) {
    int computedNoiseSegmentLength =
        Math.min(MB_10_IN_DOUBLES_SIZE / lineLength, getNoiseInterpolationPoints());
    if (computedNoiseSegmentLength < 1) {
      computedNoiseSegmentLength = 1;
      LOGGER.warn("Creating line generator of more than 10MB in size");
    }
    return computedNoiseSegmentLength;
  }

  private void processNoiseDomain(int noiseIndex, double[] lineData) {
    double noiseDist = (double) (noiseIndex) * getStepSize();
    for (var lineIndex = 0; lineIndex < lineLength; lineIndex++) {
      lineData[lineIndex] = processLineDomain(noiseDist, lineIndex);
    }
  }

  private double processLineDomain(double noiseDist, int lineIndex) {
    perlinData[0] = noiseDist;
    if (isCircular()) {
      double angle = lineIndex / (double) lineLength * 2 * Math.PI;
      perlinData[1] = (Math.cos(angle) * circularResolution) + circularResolution;
      perlinData[2] = (Math.sin(angle) * circularResolution) + circularResolution;
    } else {
      perlinData[1] = lineIndex / (double) lineInterpolationPoints;
    }
    return perlin.getFor(perlinData) * getMaxAmplitude();
  }
}
