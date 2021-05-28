package org.lefmaroli.perlin.generators;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import org.lefmaroli.perlin.configuration.JitterStrategy;

public class LayeredSliceGeneratorBuilder
    extends LayeredMultiDimensionalBuilder<
        double[][], LayeredSliceGenerator, SliceGenerator, LayeredSliceGeneratorBuilder> {

  private final SliceGeneratorBuilder singleLayerBuilder;

  public LayeredSliceGeneratorBuilder(int sliceWidth, int sliceHeight) {
    super(3);
    this.singleLayerBuilder = new SliceGeneratorBuilder(sliceWidth, sliceHeight);
  }

  @Override
  public LayeredSliceGenerator build() throws LayeredGeneratorBuilderException {
    return (LayeredSliceGenerator) super.build();
  }

  public LayeredSliceGeneratorBuilder withWidthStepSizes(Iterable<Double> numberGenerator) {
    setStepSizeGeneratorForDimension(2, numberGenerator);
    return this;
  }

  public LayeredSliceGeneratorBuilder withHeightStepSizes(Iterable<Double> numberGenerator) {
    setStepSizeGeneratorForDimension(3, numberGenerator);
    return this;
  }

  @Override
  protected LayeredSliceGeneratorBuilder self() {
    return this;
  }

  @Override
  protected SliceGenerator buildSingleNoiseLayer(
      List<Double> stepSizes, double layerAmplitude, long randomSeed, JitterStrategy jitterStrategy)
      throws StepSizeException {
    return singleLayerBuilder
        .withNoiseStepSize(stepSizes.get(0))
        .withWidthStepSize(stepSizes.get(1))
        .withHeightStepSize(stepSizes.get(2))
        .withAmplitude(layerAmplitude)
        .withRandomSeed(randomSeed)
        .withCircularBounds(isCircular())
        .withForkJoinPool(getPool())
        .withJitterStrategy(jitterStrategy)
        .build();
  }

  @Override
  protected LayeredSliceGenerator buildMultipleNoiseLayer(
      List<SliceGenerator> layers, ExecutorService executorService, JitterStrategy jitterStrategy) {
    return new LayeredSliceGeneratorImpl(layers, executorService, jitterStrategy);
  }

  private static class LayeredSliceGeneratorImpl
      extends MultiDimensionalLayeredGenerator<double[][], SliceGenerator>
      implements LayeredSliceGenerator {

    private final int sliceWidth;
    private final int sliceHeight;

    protected LayeredSliceGeneratorImpl(
        List<SliceGenerator> sliceNoiseGenerators,
        ExecutorService executorService,
        JitterStrategy jitterStrategy) {
      super(sliceNoiseGenerators, executorService, jitterStrategy);
      this.sliceWidth = sliceNoiseGenerators.get(0).getSliceWidth();
      this.sliceHeight = sliceNoiseGenerators.get(0).getSliceHeight();
      assertAllLayersHaveSameSize(sliceNoiseGenerators);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      LayeredSliceGeneratorImpl that = (LayeredSliceGeneratorImpl) o;
      return sliceWidth == that.sliceWidth && sliceHeight == that.sliceHeight;
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), sliceWidth, sliceHeight);
    }

    @Override
    public String toString() {
      return "LayeredSliceGeneratorImpl{"
          + "sliceWidth="
          + sliceWidth
          + ", sliceHeight="
          + sliceHeight
          + ", layers="
          + getLayers()
          + ", maxAmplitude="
          + getMaxAmplitude()
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
      return new double[getSliceWidth()][getSliceHeight()];
    }

    @Override
    protected double[][] resetContainer(double[][] container) {
      for (double[] rows : container) {
        Arrays.fill(rows, 0.0);
      }
      return container;
    }

    @Override
    protected double[][] addTogether(double[][] results, double[][] newLayer) {
      for (var i = 0; i < results.length; i++) {
        for (var j = 0; j < results[0].length; j++) {
          results[i][j] = results[i][j] + newLayer[i][j];
        }
      }
      return results;
    }

    @Override
    protected double[][] normalizeBy(double[][] data, double maxAmplitude) {
      for (var i = 0; i < data.length; i++) {
        for (var j = 0; j < data[0].length; j++) {
          data[i][j] = data[i][j] / maxAmplitude;
        }
      }
      return data;
    }

    private void assertAllLayersHaveSameSize(List<SliceGenerator> layers) {
      for (var i = 0; i < layers.size(); i++) {
        if (layers.get(i).getSliceWidth() != sliceWidth) {
          throw new IllegalArgumentException(
              "Layer " + i + " does not have the same slice width as the first provided layer");
        }
        if (layers.get(i).getSliceHeight() != sliceHeight) {
          throw new IllegalArgumentException(
              "Layer " + i + " does not have the same slice height as the first provided layer");
        }
      }
    }

    @Override
    public int getDimensions() {
      return 3;
    }
  }
}
