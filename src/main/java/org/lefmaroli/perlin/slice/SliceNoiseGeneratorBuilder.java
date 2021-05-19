package org.lefmaroli.perlin.slice;

import java.util.List;
import org.lefmaroli.factorgenerator.NumberGenerator;
import org.lefmaroli.perlin.dimensional.MultiDimensionalNoiseBuilder;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

public class SliceNoiseGeneratorBuilder
    extends MultiDimensionalNoiseBuilder<
        double[][], SliceNoiseGenerator, SliceNoiseGeneratorBuilder> {

  private final int sliceWidth;
  private final int sliceHeight;

  public SliceNoiseGeneratorBuilder(int sliceWidth, int sliceHeight) {
    super(3);
    this.sliceWidth = sliceWidth;
    this.sliceHeight = sliceHeight;
  }

  @Override
  public SliceNoiseGenerator build() throws NoiseBuilderException {
    return (SliceNoiseGenerator) super.build();
  }

  public SliceNoiseGeneratorBuilder withWidthInterpolationPointGenerator(
      NumberGenerator<Double> numberGenerator) {
    setStepSizeGeneratorForDimension(2, numberGenerator);
    return this;
  }

  public SliceNoiseGeneratorBuilder withHeightInterpolationPointGenerator(
      NumberGenerator<Double> numberGenerator) {
    setStepSizeGeneratorForDimension(3, numberGenerator);
    return this;
  }

  @Override
  protected SliceNoiseGeneratorBuilder self() {
    return this;
  }

  @Override
  protected SliceNoiseGenerator buildSingleNoiseLayer(
      List<Double> stepSizes, double layerAmplitude, long randomSeed) {
    return new SliceGenerator(
        stepSizes.get(0),
        stepSizes.get(1),
        stepSizes.get(2),
        sliceWidth,
        sliceHeight,
        layerAmplitude,
        randomSeed,
        isCircular(),
        null);
  }

  @Override
  protected SliceNoiseGenerator buildMultipleNoiseLayer(List<SliceNoiseGenerator> layers) {
    return new LayeredSliceGenerator(layers, null);
  }
}
