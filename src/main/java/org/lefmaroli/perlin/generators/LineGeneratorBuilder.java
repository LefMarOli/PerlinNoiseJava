package org.lefmaroli.perlin.generators;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.perlin.ContainerRecycler;
import org.lefmaroli.perlin.PerlinNoise;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainer;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainerBuilder;
import org.lefmaroli.perlin.configuration.JitterStrategy;

public class LineGeneratorBuilder
    extends MultiDimensionalBuilder<double[], LineGenerator, LineGeneratorBuilder> {

  private final int lineLength;

  public LineGeneratorBuilder(int lineLength) {
    super(2);
    assertLineSizeIsValid(lineLength);
    this.lineLength = lineLength;
  }

  private static void assertLineSizeIsValid(int lineLength) {
    if (lineLength < 1) {
      throw new IllegalArgumentException(
          "Line length must be greater than 0, provided: " + lineLength);
    }
  }

  @Override
  public LineGenerator build() {
    return (LineGenerator) super.build();
  }

  public LineGeneratorBuilder withLineStepSize(double lineStepSize) throws StepSizeException {
    setStepSizeForDimension(lineStepSize, 1);
    return this;
  }

  @Override
  protected LineGeneratorBuilder self() {
    return this;
  }

  @Override
  protected IGenerator<double[]> buildNoiseGenerator(
      double[] stepSizes, double amplitude, long randomSeed, JitterStrategy jitterStrategy) {
    return new LineGeneratorImpl(
        stepSizes[0],
        stepSizes[1],
        lineLength,
        amplitude,
        randomSeed,
        isCircular(),
        getPool(),
        jitterStrategy);
  }

  private static class LineGeneratorImpl extends MultiDimensionalRootGenerator<double[]>
      implements LineGenerator {

    private static final Logger LOGGER = LogManager.getLogger(LineGeneratorImpl.class);
    private static final List<String> parameterNames = List.of("Line step size", "Line length");

    private final double lineStepSize;
    private final int lineLength;
    private final double lineAngleFactor;
    private int currentTimeIndex = 0;
    private final PerlinNoiseDataContainer perlinData;
    private final ContainerRecycler<PerlinNoiseDataContainer> recycler;
    private final int lineLengthThreshold;

    public LineGeneratorImpl(
        double timeStepSize,
        double lineStepSize,
        int lineLength,
        double maxAmplitude,
        long randomSeed,
        boolean isCircular,
        ForkJoinPool pool,
        JitterStrategy jitterStrategy) {
      super(timeStepSize, maxAmplitude, randomSeed, isCircular, pool);
      assertValidValues(parameterNames, lineStepSize, lineLength);
      this.lineLength = lineLength;
      this.lineLengthThreshold = computeLineLengthThresholdForForkingProcess(lineLength);
      this.lineStepSize = correctStepSizeForCircularity(lineStepSize, lineLength, "line length");
      this.lineAngleFactor = this.lineStepSize * (2 * Math.PI);
      PerlinNoiseDataContainerBuilder builder;
      if (isCircular) {
        builder = new PerlinNoiseDataContainerBuilder(3, randomSeed, jitterStrategy);
      } else {
        builder = new PerlinNoiseDataContainerBuilder(2, randomSeed, jitterStrategy);
      }
      this.perlinData = builder.createNewContainer();
      this.recycler = new ContainerRecycler<>(builder);
      LOGGER.debug("Created new {}", this);
    }

    private int computeLineLengthThresholdForForkingProcess(int lineLength) {
      var threshold = 2500;
      if (hasParallelProcessingEnabled() && lineLength > 2500) {
        threshold = (int) Math.ceil((double) lineLength / getExecutionPool().getParallelism());
      }
      return threshold;
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
      LineGeneratorImpl that = (LineGeneratorImpl) o;
      return lineStepSize == that.lineStepSize && lineLength == that.lineLength;
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), lineStepSize, lineLength);
    }

    @Override
    public String toString() {
      return "LineGeneratorImpl{"
          + "timeStepSize="
          + getTimeStepSize()
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
    public int getDimensions() {
      return 2;
    }

    @Override
    public int getTotalSize() {
      return getLineLength();
    }

    @Override
    protected double[] generateNextSegment(double[] container) {
      currentTimeIndex++;
      processTimeDomain(currentTimeIndex, container);
      return container;
    }

    private void processTimeDomain(int timeIndex, double[] lineData) {
      double timeDist = (double) (timeIndex) * getTimeStepSize();
      if (hasParallelProcessingEnabled()) {
        getExecutionPool().invoke(new LineNoiseTask(lineData, timeDist, 0, lineLength));
      } else {
        perlinData.setCoordinatesForDimension(0, timeDist);
        for (var lineIndex = 0; lineIndex < lineLength; lineIndex++) {
          if (Thread.currentThread().isInterrupted()) {
            LogManager.getLogger(this.getClass())
                .debug("Interrupted processing [processNoiseDomain]");
            return;
          }
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

    private class LineNoiseTask extends RecursiveAction {

      private final double[] results;
      private final double timeDistance;
      private final int startLineIndex;
      private final int endLineIndex;

      LineNoiseTask(double[] results, double timeDistance, int startLineIndex, int endLineIndex) {
        this.results = results;
        this.timeDistance = timeDistance;
        this.startLineIndex = startLineIndex;
        this.endLineIndex = endLineIndex;
      }

      private void computeDirectly() {
        PerlinNoiseDataContainer dataContainer = recycler.getNewOrNextAvailableContainer();
        dataContainer.setCoordinatesForDimension(0, timeDistance);
        for (var lineIndex = startLineIndex; lineIndex < endLineIndex; lineIndex++) {
          if (Thread.currentThread().isInterrupted()) {
            LogManager.getLogger(this.getClass()).debug("Interrupted processing [computeDirectly]");
            return;
          }
          results[lineIndex] = processLineDomain(lineIndex, dataContainer);
        }
        recycler.recycleContainer(dataContainer);
      }

      @Override
      protected void compute() {
        var lineSegment = endLineIndex - startLineIndex;
        if (lineSegment < lineLengthThreshold) {
          computeDirectly();
          return;
        }

        int splitIndex = (lineSegment / 2) + startLineIndex;

        invokeAll(
            new LineNoiseTask(results, timeDistance, startLineIndex, splitIndex),
            new LineNoiseTask(results, timeDistance, splitIndex, endLineIndex));
        if (Thread.currentThread().isInterrupted()) {
          LogManager.getLogger(this.getClass()).debug("Interrupted processing [compute]");
        }
      }
    }
  }
}
