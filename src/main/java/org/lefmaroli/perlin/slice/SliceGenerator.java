package org.lefmaroli.perlin.slice;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.execution.ContainerRecycler;
import org.lefmaroli.perlin.PerlinNoise;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainer;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainerBuilder;
import org.lefmaroli.perlin.dimensional.MultiDimensionalRootNoiseGenerator;

public class SliceGenerator extends MultiDimensionalRootNoiseGenerator<double[][]>
    implements SliceNoiseGenerator {

  private static final Logger LOGGER = LogManager.getLogger(SliceGenerator.class);

  private static final List<String> parameterNames =
      List.of("Width step size", "Slice width", "Height step size", "Slice height");
  private final double widthStepSize;
  private final double widthAngleFactor;
  private final double heightStepSize;
  private final double heightAngleFactor;
  private final int sliceWidth;
  private final int sliceHeight;
  private final PerlinNoiseDataContainer perlinData;
  private final ContainerRecycler<PerlinNoiseDataContainer> recycler;
  private int currentPosInNoiseInterpolation = 0;
  private final int lengthThreshold;

  SliceGenerator(
      double noiseStepSize,
      double widthStepSize,
      double heightStepSize,
      int sliceWidth,
      int sliceHeight,
      double maxAmplitude,
      long randomSeed,
      boolean isCircular,
      ForkJoinPool pool) {
    super(noiseStepSize, maxAmplitude, randomSeed, isCircular, pool);
    assertValidValues(parameterNames, widthStepSize, heightStepSize, sliceWidth, sliceHeight);
    this.widthStepSize = correctStepSizeForCircularity(widthStepSize, sliceWidth, "slice width");
    this.widthAngleFactor = this.widthStepSize * 2 * Math.PI;
    this.heightStepSize =
        correctStepSizeForCircularity(heightStepSize, sliceHeight, "slice height");
    this.heightAngleFactor = this.heightStepSize * 2 * Math.PI;
    this.sliceWidth = sliceWidth;
    this.sliceHeight = sliceHeight;
    this.lengthThreshold = computeLengthThresholdForForkingProcess(sliceWidth, sliceHeight);
    PerlinNoiseDataContainerBuilder builder;
    if (isCircular) {
      builder = new PerlinNoiseDataContainerBuilder(5, randomSeed);
    } else {
      builder = new PerlinNoise.PerlinNoiseDataContainerBuilder(3, randomSeed);
    }
    perlinData = builder.createNewContainer();
    this.recycler = new ContainerRecycler<>(builder);
    LOGGER.debug("Create new {}", this);
  }

  private int computeLengthThresholdForForkingProcess(int sliceWidth, int sliceHeight) {
    var threshold = 2500;
    if (hasParallelProcessingEnabled() && sliceWidth * sliceHeight > 2500) {
      threshold =
          (int) Math.ceil((double) sliceWidth * sliceHeight / getExecutionPool().getParallelism());
    }
    return threshold;
  }

  public double getWidthStepSize() {
    return widthStepSize;
  }

  public double getHeightStepSize() {
    return heightStepSize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SliceGenerator that = (SliceGenerator) o;
    return widthStepSize == that.widthStepSize
        && heightStepSize == that.heightStepSize
        && sliceWidth == that.sliceWidth
        && sliceHeight == that.sliceHeight;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), widthStepSize, heightStepSize, sliceWidth, sliceHeight);
  }

  @Override
  public String toString() {
    return "SliceGenerator{"
        + "noiseStepSize="
        + getNoiseStepSize()
        + ", widthStepSize="
        + widthStepSize
        + ", heightStepSize="
        + heightStepSize
        + ", sliceWidth="
        + sliceWidth
        + ", sliceHeight="
        + sliceHeight
        + ", maxAmplitude="
        + getMaxAmplitude()
        + ", randomSeed="
        + randomSeed
        + ", isCircular="
        + isCircular()
        + '}';
  }

  @Override
  public int getSliceWidth() {
    return sliceWidth;
  }

  @Override
  public int getSliceHeight() {
    return sliceHeight;
  }

  @Override
  protected double[][] getNewContainer() {
    return new double[sliceWidth][sliceHeight];
  }

  @Override
  protected double[][] generateNextSegment(double[][] container) {
    currentPosInNoiseInterpolation++;
    processNoiseDomain(currentPosInNoiseInterpolation, container);
    return container;
  }

  private void processNoiseDomain(int noiseIndex, double[][] slice) {
    double noiseDist = (double) (noiseIndex) * getNoiseStepSize();
    if (hasParallelProcessingEnabled()) {
      getExecutionPool()
          .invoke(new SliceNoiseTask(slice, noiseDist, 0, sliceWidth, 0, sliceHeight));
    } else {
      perlinData.setCoordinatesForDimension(0, noiseDist);
      for (var widthIndex = 0; widthIndex < sliceWidth; widthIndex++) {
        processSliceWidthDomain(widthIndex, slice[widthIndex], perlinData);
      }
    }
  }

  private void processSliceWidthDomain(
      int widthIndex, double[] line, PerlinNoiseDataContainer dataContainer) {
    double widthDist;
    if (isCircular()) {
      widthDist = widthIndex * widthAngleFactor;
      dataContainer.setCoordinatesForDimension(1, (Math.cos(widthDist) + 1.0) / 2.0);
      dataContainer.setCoordinatesForDimension(2, (Math.sin(widthDist) + 1.0) / 2.0);
    } else {
      widthDist = widthIndex * widthStepSize;
      dataContainer.setCoordinatesForDimension(1, widthDist);
    }
    for (var heightIndex = 0; heightIndex < sliceHeight; heightIndex++) {
      line[heightIndex] = processSliceHeightDomain(heightIndex, dataContainer);
    }
  }

  private double processSliceHeightDomain(int heightIndex, PerlinNoiseDataContainer dataContainer) {
    if (isCircular()) {
      double heightDist = heightIndex * heightAngleFactor;
      dataContainer.setCoordinatesForDimension(3, (Math.cos(heightDist) + 1.0) / 2.0);
      dataContainer.setCoordinatesForDimension(4, (Math.sin(heightDist) + 1.0) / 2.0);
    } else {
      dataContainer.setCoordinatesForDimension(2, heightIndex * heightStepSize);
    }
    return PerlinNoise.getFor(dataContainer) * getMaxAmplitude();
  }

  private class SliceNoiseTask extends RecursiveAction {

    private final double[][] results;
    private final double noiseDistance;
    private final int startWidthIndex;
    private final int endWidthIndex;
    private final int startHeightIndex;
    private final int endHeightIndex;

    SliceNoiseTask(
        double[][] results,
        double noiseDistance,
        int startWidthIndex,
        int endWidthIndex,
        int startHeightIndex,
        int endHeightIndex) {
      this.results = results;
      this.noiseDistance = noiseDistance;
      this.startWidthIndex = startWidthIndex;
      this.endWidthIndex = endWidthIndex;
      this.startHeightIndex = startHeightIndex;
      this.endHeightIndex = endHeightIndex;
    }

    private void computeDirectly() {
      PerlinNoiseDataContainer dataContainer = recycler.getNewOrNextAvailableContainer();
      dataContainer.setCoordinatesForDimension(0, noiseDistance);
      for (var widthIndex = startWidthIndex; widthIndex < endWidthIndex; widthIndex++) {
        processSliceWidthDomain(widthIndex, results[widthIndex], dataContainer);
      }
      recycler.recycleContainer(dataContainer);
    }

    @Override
    protected void compute() {
      var widthSegment = endWidthIndex - startWidthIndex;
      var heightSegment = endHeightIndex - startHeightIndex;
      if (widthSegment * heightSegment < lengthThreshold) {
        computeDirectly();
        return;
      }

      int splitWidthIndex = (widthSegment / 2) + startWidthIndex;
      int splitHeightIndex = (heightSegment / 2) + startHeightIndex;

      invokeAll(
          new SliceNoiseTask(
              results,
              noiseDistance,
              startWidthIndex,
              splitWidthIndex,
              startHeightIndex,
              splitHeightIndex),
          new SliceNoiseTask(
              results,
              noiseDistance,
              splitWidthIndex,
              endWidthIndex,
              startHeightIndex,
              splitHeightIndex),
          new SliceNoiseTask(
              results,
              noiseDistance,
              startWidthIndex,
              splitWidthIndex,
              splitHeightIndex,
              endHeightIndex),
          new SliceNoiseTask(
              results,
              noiseDistance,
              splitWidthIndex,
              endWidthIndex,
              splitHeightIndex,
              endHeightIndex));
    }
  }
}
