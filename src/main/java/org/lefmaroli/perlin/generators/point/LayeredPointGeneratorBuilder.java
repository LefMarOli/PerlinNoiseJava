package org.lefmaroli.perlin.generators.point;

import java.util.List;
import java.util.concurrent.ExecutorService;
import org.lefmaroli.perlin.generators.IGenerator;
import org.lefmaroli.perlin.generators.LayeredBuilder;
import org.lefmaroli.perlin.generators.LayeredGenerator;
import org.lefmaroli.perlin.generators.LayeredGeneratorBuilderException;
import org.lefmaroli.perlin.generators.StepSizeException;

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
      List<Double> stepSizes, double layerAmplitude, long randomSeed) throws StepSizeException {
    return singleLayerBuilder
        .withNoiseStepSize(stepSizes.get(0))
        .withAmplitude(layerAmplitude)
        .withRandomSeed(randomSeed)
        .build();
  }

  @Override
  protected LayeredPointGeneratorImpl buildMultipleNoiseLayer(
      List<PointGenerator> layers, ExecutorService executorService) {
    return new LayeredPointGeneratorImpl(layers, executorService);
  }

  private static class LayeredPointGeneratorImpl extends LayeredGenerator<Double>
      implements LayeredPointGenerator {

    LayeredPointGeneratorImpl(
        List<? extends IGenerator<Double>> layers, ExecutorService executorService) {
      super(layers, executorService);
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
