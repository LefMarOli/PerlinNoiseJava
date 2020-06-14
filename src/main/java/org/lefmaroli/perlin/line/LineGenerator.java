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
  private final double[] lineData;
  private final int noiseSegmentLength;
  private int currentPosition = 0;

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
    this.lineData = new double[lineLength];
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
    double[][] results = new double[noiseSegmentLength][lineLength];
    for (int i = 0; i < noiseSegmentLength; i++) {
      currentPosition++;
      double[] line = processNoiseDomain(currentPosition);
      System.arraycopy(line, 0, results[i], 0, line.length);
    }
    return results;
  }

  @Override
  protected double[][] getArrayOfSubType(int count) {
    return new double[count][lineLength];
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

  private double[] processNoiseDomain(int noiseIndex) {
    double noiseDist = (double) (noiseIndex) * getStepSize();
    for (int lineIndex = 0; lineIndex < lineLength; lineIndex++) {
      lineData[lineIndex] = processLineDomain(noiseDist, lineIndex);
    }
    return lineData;
  }

  private double processLineDomain(double noiseDist, int lineIndex) {
    double lineStepSize = 1.0 / lineInterpolationPoints;
    double lineDist = lineIndex * lineStepSize;
    if (isCircular()) {
      int numberOfSegments = lineLength / lineInterpolationPoints;
      double resolution = 1.0 / numberOfSegments;
      double angle = lineIndex / (double) lineLength * 2 * Math.PI;
      double xCoord = (Math.cos(angle) * resolution) + resolution;
      double yCoord = (Math.sin(angle) * resolution) + resolution;
      return PerlinNoise.perlin(noiseDist, xCoord, yCoord) * getMaxAmplitude();
    } else {
      return PerlinNoise.perlin(noiseDist, lineDist) * getMaxAmplitude();
    }
  }
}
