package org.lefmaroli.perlin.point;

import java.util.List;
import java.util.concurrent.ExecutorService;
import org.lefmaroli.perlin.NoiseBuilder;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

public class PointNoiseGeneratorBuilder
    extends NoiseBuilder<Double, PointNoiseGenerator, PointNoiseGeneratorBuilder> {

  public PointNoiseGeneratorBuilder() {
    super(1);
  }

  @Override
  public PointNoiseGenerator build() throws NoiseBuilderException {
    return (PointNoiseGenerator) super.build();
  }

  @Override
  protected PointNoiseGeneratorBuilder self() {
    return this;
  }

  @Override
  protected PointNoiseGenerator buildSingleNoiseLayer(
      List<Double> stepSizes, double layerAmplitude, long randomSeed) {
    return new PointGenerator(stepSizes.get(0), layerAmplitude, randomSeed);
  }

  @Override
  protected PointNoiseGenerator buildMultipleNoiseLayer(List<PointNoiseGenerator> layers,
      ExecutorService executorService) {
    return new LayeredPointGenerator(layers, executorService);
  }
}
