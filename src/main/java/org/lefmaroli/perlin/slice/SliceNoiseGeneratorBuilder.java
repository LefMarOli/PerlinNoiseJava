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
      NumberGenerator<Integer> numberGenerator) {
    setInterpolationPointCountGeneratorForDimension(2, numberGenerator);
    return this;
  }

  public SliceNoiseGeneratorBuilder withHeightInterpolationPointGenerator(
      NumberGenerator<Integer> numberGenerator) {
    setInterpolationPointCountGeneratorForDimension(3, numberGenerator);
    return this;
  }

  @Override
  protected SliceNoiseGeneratorBuilder self() {
    return this;
  }

  @Override
  protected SliceNoiseGenerator buildSingleNoiseLayer(
      List<Integer> interpolationPoints, double layerAmplitude, long randomSeed)
      throws NoiseBuilderException {
    return new SliceGenerator(
        interpolationPoints.get(0),
        interpolationPoints.get(1),
        interpolationPoints.get(2),
        sliceWidth,
        sliceHeight,
        layerAmplitude,
        randomSeed,
        isCircular());
  }

  @Override
  protected SliceNoiseGenerator buildMultipleNoiseLayer(List<SliceNoiseGenerator> layers)
      throws NoiseBuilderException {
    return new LayeredSliceGenerator(layers);
  }
}
