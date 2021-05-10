package org.lefmaroli.perlin.line;

import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.perlin.PerlinNoise;

public class LineGenerator extends RootLineNoiseGenerator implements LineNoiseGenerator {

  private static final Logger LOGGER = LogManager.getLogger(LineGenerator.class);
  private static final List<String> parameterNames =
      List.of("Line step size", "Line length");

  private final double lineStepSize;
  private final int lineLength;
  private int currentPosition = 0;
  private final double circularResolution;
  private final PerlinNoise perlin;
  private final double[] perlinData;

  public LineGenerator(
      double noiseStepSize,
      double lineStepSize,
      int lineLength,
      double maxAmplitude,
      long randomSeed,
      boolean isCircular) {
    super(noiseStepSize, maxAmplitude, randomSeed, isCircular);
    assertValidValues(parameterNames, lineStepSize, lineLength);
    this.lineLength = lineLength;
    this.lineStepSize =
        correctStepSizeForCircularity(lineStepSize, lineLength, "line length");
    this.circularResolution = 1.0 / (this.lineStepSize * this.lineLength);
    if (isCircular) {
      perlin = new PerlinNoise(3, randomSeed);
      perlinData = new double[3];
    } else {
      perlin = new PerlinNoise(2, randomSeed);
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
    return lineStepSize == that.lineStepSize && lineLength == that.lineLength;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), lineStepSize, lineLength);
  }

  @Override
  public String toString() {
    return "LineGenerator{"
        + "noiseStepSize="
        + getNoiseStepSize()
        + ", lineStepSize="
        + lineStepSize
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
  public double getLineStepSize() {
    return lineStepSize;
  }

  @Override
  protected double[] getNewContainer() {
    return new double[lineLength];
  }

  @Override
  protected double[] generateNextSegment(double[] container) {
    currentPosition++;
    processNoiseDomain(currentPosition, container);
    return container;
  }

  private void processNoiseDomain(int noiseIndex, double[] lineData) {
    double noiseDist = (double) (noiseIndex) * getNoiseStepSize();
    for (var lineIndex = 0; lineIndex < lineLength; lineIndex++) {
      lineData[lineIndex] = processLineDomain(noiseDist, lineIndex);
    }
  }

  private double processLineDomain(double noiseDist, int lineIndex) {
    perlinData[0] = noiseDist;
    if (isCircular()) {
      double angle = lineIndex * lineStepSize * 2 * Math.PI;
      perlinData[1] = (Math.cos(angle) * circularResolution) + circularResolution;
      perlinData[2] = (Math.sin(angle) * circularResolution) + circularResolution;
    } else {
      perlinData[1] = lineIndex * lineStepSize;
    }
    return perlin.getFor(perlinData) * getMaxAmplitude();
  }
}
