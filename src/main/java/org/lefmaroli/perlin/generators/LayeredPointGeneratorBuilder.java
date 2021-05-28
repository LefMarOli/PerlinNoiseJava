package org.lefmaroli.perlin.generators;

import java.util.List;
import java.util.concurrent.ExecutorService;
import org.lefmaroli.perlin.configuration.JitterStrategy;

public class LayeredPointGeneratorBuilder
    extends LayeredBuilder<
        Double, LayeredPointGenerator, PointGenerator, LayeredPointGeneratorBuilder> {

  private final PointGeneratorBuilder singleLayerBuilder;

  public LayeredPointGeneratorBuilder() {
    super(1);
    singleLayerBuilder = new PointGeneratorBuilder();
  }

  @Override
  public LayeredPointGeneratorImpl build() throws LayeredGeneratorBuilderException {
    return (LayeredPointGeneratorImpl) super.build();
  }

  @Override
  protected LayeredPointGeneratorBuilder self() {
    return this;
  }

  @Override
  protected PointGenerator buildSingleNoiseLayer(
      List<Double> stepSizes, double layerAmplitude, long randomSeed, JitterStrategy jitterStrategy)
      throws StepSizeException {
    return singleLayerBuilder
        .withNoiseStepSize(stepSizes.get(0))
        .withAmplitude(layerAmplitude)
        .withRandomSeed(randomSeed)
        .withJitterStrategy(jitterStrategy)
        .build();
  }

  @Override
  protected LayeredPointGeneratorImpl buildMultipleNoiseLayer(
      List<PointGenerator> layers, ExecutorService executorService, JitterStrategy jitterStrategy) {
    return new LayeredPointGeneratorImpl(layers, executorService, jitterStrategy);
  }

  private static class LayeredPointGeneratorImpl extends LayeredGenerator<Double>
      implements LayeredPointGenerator {

    LayeredPointGeneratorImpl(
        List<? extends IGenerator<Double>> layers,
        ExecutorService executorService,
        JitterStrategy jitterStrategy) {
      super(layers, executorService, jitterStrategy);
    }

    @Override
    public String toString() {
      return "LayeredPointGeneratorImpl{"
          + "layers="
          + getLayers()
          + ", maxAmplitude="
          + getMaxAmplitude()
          + '}';
    }

    @Override
    protected Double getNewContainer() {
      return 0.0;
    }

    @Override
    protected Double resetContainer(Double container) {
      return 0.0;
    }

    @Override
    protected Double addTogether(Double results, Double newLayer) {
      return results + newLayer;
    }

    @Override
    protected Double normalizeBy(Double data, double maxAmplitude) {
      return data / maxAmplitude;
    }

    @Override
    public int getDimensions() {
      return 1;
    }
  }
}
