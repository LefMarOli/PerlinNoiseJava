package org.lefmaroli.perlin.line;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.perlin.PerlinNoise;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainer;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainerBuilder;
import org.lefmaroli.perlin.dimensional.MultiDimensionalRootNoiseGenerator;

public class LineGenerator extends MultiDimensionalRootNoiseGenerator<double[]>
    implements LineNoiseGenerator {

  public static int LINE_LENGTH_THRESHOLD = 200;

  private static final Logger LOGGER = LogManager.getLogger(LineGenerator.class);
  private static final List<String> parameterNames = List.of("Line step size", "Line length");

  private final double lineStepSize;
  private final int lineLength;
  private final double lineAngleFactor;
  private int currentPosition = 0;
  private final PerlinNoiseDataContainer perlinData;
  private final PerlinNoiseDataContainerBuilder builder;

  public LineGenerator(
      double noiseStepSize,
      double lineStepSize,
      int lineLength,
      double maxAmplitude,
      long randomSeed,
      boolean isCircular,
      ForkJoinPool pool) {
    super(noiseStepSize, maxAmplitude, randomSeed, isCircular, pool);
    assertValidValues(parameterNames, lineStepSize, lineLength);
    this.lineLength = lineLength;
    this.lineStepSize = correctStepSizeForCircularity(lineStepSize, lineLength, "line length");
    this.lineAngleFactor = this.lineStepSize * (2 * Math.PI);
    if (isCircular) {
      builder = new PerlinNoiseDataContainerBuilder(3, randomSeed);
    } else {
      builder = new PerlinNoiseDataContainerBuilder(2, randomSeed);
    }
    this.perlinData = builder.getNewContainer();
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
    perlinData.setCoordinatesForDimension(0, noiseDist);
    if (hasParallelProcessingEnabled()) {
      getExecutionPool()
          .invoke(new GenerateLineNoiseTask(lineData, noiseDist, perlinData, 0, lineLength));
    } else {
      for (var lineIndex = 0; lineIndex < lineLength; lineIndex++) {
        lineData[lineIndex] = processLineDomain(lineIndex, perlinData);
      }
    }
  }

  private double processLineDomain(int lineIndex, PerlinNoiseDataContainer container) {
    if (isCircular()) {
      double angle = lineIndex * lineAngleFactor;
      container.setCoordinatesForDimension(1, (Math.cos(angle) + 1.0) / 2.0);
      container.setCoordinatesForDimension(2, (Math.sin(angle) + 1.0) / 2.0);
    } else {
      container.setCoordinatesForDimension(1, lineIndex * lineStepSize);
    }
    return PerlinNoise.getFor(container) * getMaxAmplitude();
  }

  private class GenerateLineNoiseTask extends RecursiveAction {

    private final double[] results;
    private final double noiseDistance;
    private final int startLineIndex;
    private final int endLineIndex;
    private final PerlinNoiseDataContainer dataContainer;

    GenerateLineNoiseTask(
        double[] results,
        double noiseDistance,
        PerlinNoiseDataContainer dataContainer,
        int startLineIndex,
        int endLineIndex) {
      this.results = results;
      this.noiseDistance = noiseDistance;
      this.startLineIndex = startLineIndex;
      this.endLineIndex = endLineIndex;
      this.dataContainer = dataContainer;
    }

    private void computeDirectly() {
      for (var lineIndex = startLineIndex; lineIndex < endLineIndex; lineIndex++) {
        results[lineIndex] = processLineDomain(lineIndex, dataContainer);
      }
    }

    @Override
    protected void compute() {

      var lineSegment = endLineIndex - startLineIndex;
      if (lineSegment < LINE_LENGTH_THRESHOLD) {
        computeDirectly();
        return;
      }

      int splitIndex = (lineSegment / 2) + startLineIndex;

      PerlinNoiseDataContainer newContainer = builder.getNewContainer();
      newContainer.setCoordinatesForDimension(0, noiseDistance);
      invokeAll(
          new GenerateLineNoiseTask(
              results, noiseDistance, dataContainer, startLineIndex, splitIndex),
          new GenerateLineNoiseTask(
              results, noiseDistance, newContainer, splitIndex, endLineIndex));
    }
  }
}
