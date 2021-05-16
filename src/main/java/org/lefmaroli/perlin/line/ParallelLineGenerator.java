package org.lefmaroli.perlin.line;

import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.execution.ExecutorPool;
import org.lefmaroli.perlin.PerlinNoise;
import org.lefmaroli.perlin.dimensional.ParallelNoiseGenerator;

public class ParallelLineGenerator extends ParallelNoiseGenerator<double[]>
    implements LineNoiseGenerator {

  private static final Logger LOGGER = LogManager.getLogger(ParallelLineGenerator.class);
  private static final List<String> parameterNames = List.of("Line step size", "Line length");

  private final double lineStepSize;
  private final int lineLength;
  private final double lineAngleFactor;
  private final PerlinNoise perlin;
  private final double[] perlinData;
  private int currentPosition = 0;

  public ParallelLineGenerator(
      double noiseStepSize,
      double lineStepSize,
      int lineLength,
      double maxAmplitude,
      long randomSeed,
      boolean isCircular,
      ExecutorPool executorPool) {
    super(noiseStepSize, maxAmplitude, randomSeed, isCircular, executorPool);
    assertValidValues(parameterNames, lineStepSize, lineLength);
    this.lineLength = lineLength;
    this.lineStepSize = correctStepSizeForCircularity(lineStepSize, lineLength, "line length");
    this.lineAngleFactor = this.lineStepSize * (2 * Math.PI);
    perlin = new PerlinNoise(randomSeed);
    if (isCircular) {
      perlinData = new double[3];
    } else {
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
    ParallelLineGenerator that = (ParallelLineGenerator) o;
    return lineStepSize == that.lineStepSize && lineLength == that.lineLength;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), lineStepSize, lineLength);
  }

  @Override
  public String toString() {
    return "ParallelLineGenerator{"
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
    perlinData[0] = noiseDist;
    if (isCircular()) {
      double angle = lineIndex * lineAngleFactor;
      perlinData[1] = (Math.cos(angle) + 1.0) / 2.0;
      perlinData[2] = (Math.sin(angle) + 1.0) / 2.0;
    } else {
      perlinData[1] = lineIndex * lineStepSize;
    }
    return perlin.getFor(perlinData) * getMaxAmplitude();
  }
}
