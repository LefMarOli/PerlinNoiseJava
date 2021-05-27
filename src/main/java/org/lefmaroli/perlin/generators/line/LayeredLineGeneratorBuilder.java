package org.lefmaroli.perlin.generators.line;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import org.lefmaroli.perlin.generators.LayeredGeneratorBuilderException;
import org.lefmaroli.perlin.generators.StepSizeException;
import org.lefmaroli.perlin.generators.dimensional.LayeredMultiDimensionalBuilder;
import org.lefmaroli.perlin.generators.dimensional.MultiDimensionalLayeredGenerator;

public class LayeredLineGeneratorBuilder
    extends LayeredMultiDimensionalBuilder<
        double[], LayeredLineGenerator, LineGenerator, LayeredLineGeneratorBuilder> {

  private final LineGeneratorBuilder singleLayerBuilder;

  public LayeredLineGeneratorBuilder(int lineLength) {
    super(2);
    this.singleLayerBuilder = new LineGeneratorBuilder(lineLength);
  }

  @Override
  public LayeredLineGenerator build() throws LayeredGeneratorBuilderException {
    return (LayeredLineGenerator) super.build();
  }

  public LayeredLineGeneratorBuilder withLineStepSizes(Iterable<Double> lineStepSizes) {
    setStepSizeGeneratorForDimension(2, lineStepSizes);
    return this;
  }

  @Override
  protected LayeredLineGeneratorBuilder self() {
    return this;
  }

  @Override
  protected LineGenerator buildSingleNoiseLayer(
      List<Double> stepSizes, double layerAmplitude, long randomSeed) throws StepSizeException {
    return singleLayerBuilder
        .withNoiseStepSize(stepSizes.get(0))
        .withLineStepSize(stepSizes.get(1))
        .withAmplitude(layerAmplitude)
        .withRandomSeed(randomSeed)
        .withForkJoinPool(getPool())
        .withCircularBounds(isCircular())
        .build();
  }

  @Override
  protected LayeredLineGenerator buildMultipleNoiseLayer(
      List<LineGenerator> layers, ExecutorService executorService) {
    return new LayeredLineGeneratorImpl(layers, executorService);
  }

  private static class LayeredLineGeneratorImpl
      extends MultiDimensionalLayeredGenerator<double[], LineGenerator>
      implements LayeredLineGenerator {

    private final int lineLength;

    LayeredLineGeneratorImpl(List<LineGenerator> layers, ExecutorService executorService) {
      super(layers, executorService);
      this.lineLength = layers.get(0).getLineLength();
      assertAllLayersHaveSameLineLength(layers);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      LayeredLineGeneratorImpl that = (LayeredLineGeneratorImpl) o;
      return lineLength == that.lineLength;
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), lineLength);
    }

    @Override
    public String toString() {
      return "LayeredLineGeneratorImpl{"
          + "lineLength="
          + lineLength
          + ", layers="
          + getLayers()
          + ", maxAmplitude="
          + getMaxAmplitude()
          + ", isCircular="
          + isCircular()
          + '}';
    }

    @Override
    protected double[] getNewContainer() {
      return new double[lineLength];
    }

    @Override
    protected double[] resetContainer(double[] container) {
      Arrays.fill(container, 0.0);
      return container;
    }

    @Override
    protected double[] addTogether(double[] results, double[] newLayer) {
      for (var i = 0; i < results.length; i++) {
        results[i] = results[i] + newLayer[i];
      }
      return results;
    }

    @Override
    protected double[] normalizeBy(double[] data, double maxAmplitude) {
      for (var i = 0; i < data.length; i++) {
        data[i] = data[i] / maxAmplitude;
      }
      return data;
    }

    private void assertAllLayersHaveSameLineLength(List<LineGenerator> layers) {
      for (var i = 0; i < layers.size(); i++) {
        if (layers.get(i).getLineLength() != lineLength) {
          throw new IllegalArgumentException(
              "Layer " + i + " does not have the same line length as the first provided layer");
        }
      }
    }

    @Override
    public int getDimensions() {
      return 2;
    }

    @Override
    public int getLineLength() {
      return lineLength;
    }
  }
}
