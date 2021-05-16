package org.lefmaroli.perlin.line;

import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.perlin.PerlinNoise;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainer;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainerBuilder;
import org.lefmaroli.perlin.dimensional.MultiDimensionalRootNoiseGenerator;

public class LineGenerator extends MultiDimensionalRootNoiseGenerator<double[]>
    implements LineNoiseGenerator {

  private static final Logger LOGGER = LogManager.getLogger(LineGenerator.class);
  private static final List<String> parameterNames = List.of("Line step size", "Line length");

  private final double lineStepSize;
  private final int lineLength;
  private final double lineAngleFactor;
  private int currentPosition = 0;
  private final PerlinNoiseDataContainer perlinData;

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
    this.lineStepSize = correctStepSizeForCircularity(lineStepSize, lineLength, "line length");
    this.lineAngleFactor = this.lineStepSize * (2 * Math.PI);
    if (isCircular) {
      perlinData = new PerlinNoiseDataContainerBuilder(3, randomSeed).getNewContainer();
    } else {
      perlinData = new PerlinNoiseDataContainerBuilder(2, randomSeed).getNewContainer();
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
    perlinData.setCoordinatesForDimension(0, noiseDist);
    if (isCircular()) {
      double angle = lineIndex * lineAngleFactor;
      perlinData.setCoordinatesForDimension(1, (Math.cos(angle) + 1.0) / 2.0);
      perlinData.setCoordinatesForDimension(2, (Math.sin(angle) + 1.0) / 2.0);
    } else {
      perlinData.setCoordinatesForDimension(1, lineIndex * lineStepSize);
    }
    return PerlinNoise.getFor(perlinData) * getMaxAmplitude();
  }
}
