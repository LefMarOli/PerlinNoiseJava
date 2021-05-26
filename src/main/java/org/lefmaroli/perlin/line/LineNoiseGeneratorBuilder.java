package org.lefmaroli.perlin.line;

import java.util.List;
import java.util.concurrent.ExecutorService;
import org.lefmaroli.factorgenerator.NumberGenerator;
import org.lefmaroli.perlin.dimensional.MultiDimensionalNoiseBuilder;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

public class LineNoiseGeneratorBuilder
    extends MultiDimensionalNoiseBuilder<double[], LineNoiseGenerator, LineNoiseGeneratorBuilder> {

  private final int lineLength;

  public LineNoiseGeneratorBuilder(int lineLength) {
    super(2);
    this.lineLength = lineLength;
  }

  @Override
  public LineNoiseGenerator build() throws NoiseBuilderException {
    return (LineNoiseGenerator) super.build();
  }

  public LineNoiseGeneratorBuilder withLineStepSizeGenerator(
      NumberGenerator<Double> numberGenerator) {
    setStepSizeGeneratorForDimension(2, numberGenerator);
    return this;
  }

  @Override
  protected LineNoiseGeneratorBuilder self() {
    return this;
  }

  @Override
  protected LineNoiseGenerator buildSingleNoiseLayer(
      List<Double> stepSizes, double layerAmplitude, long randomSeed) {
    return new LineGenerator(
        stepSizes.get(0),
        stepSizes.get(1),
        lineLength,
        layerAmplitude,
        randomSeed,
        isCircular(),
        getPool());
  }

  @Override
  protected LineNoiseGenerator buildMultipleNoiseLayer(List<LineNoiseGenerator> layers,
      ExecutorService executorService) {
    return new LayeredLineGenerator(layers, executorService);
  }
}
