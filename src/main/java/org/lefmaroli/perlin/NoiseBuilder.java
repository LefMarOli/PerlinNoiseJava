package org.lefmaroli.perlin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.factorgenerator.NumberGenerator;
import org.lefmaroli.perlin.exceptions.NoStepSizeException;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.perlin.exceptions.NoiseLayerException;
import org.lefmaroli.perlin.exceptions.StepSizeException;

public abstract class NoiseBuilder<
    N, L extends INoiseGenerator<N>, B extends NoiseBuilder<N, L, B>> {
  private static final NumberGenerator<Double> DEFAULT_STEP_SIZE_GENERATOR =
      new DoubleGenerator(1.0 / 64, 0.5);
  private final int dimensions;
  private final List<NumberGenerator<Double>> stepSizeGeneratorsByDimension;
  protected int numberOfLayers = 5;
  protected long randomSeed = System.currentTimeMillis();
  private NumberGenerator<Double> amplitudeGenerator = new DoubleGenerator(1.0, 0.5);

  protected NoiseBuilder(int dimensions) {
    this.dimensions = dimensions;
    this.stepSizeGeneratorsByDimension = new ArrayList<>(dimensions);
    for (var i = 0; i < dimensions; i++) {
      this.stepSizeGeneratorsByDimension.add(DEFAULT_STEP_SIZE_GENERATOR.getCopy());
    }
  }

  private static void assertStepSizeForAll(List<Double> stepSizes) throws StepSizeException {
    for (Double stepSize : stepSizes) {
      assertStepSize(stepSize);
    }
  }

  private static void assertStepSize(double stepSize) throws StepSizeException {
    if (Double.compare(stepSize, 0.0) < 0 || Double.compare(stepSize, 0.0) == 0) {
      throw new NoStepSizeException();
    }
  }

  public B withNoiseStepSizeGenerator(NumberGenerator<Double> stepSizeGenerator) {
    setStepSizeGeneratorForDimension(1, stepSizeGenerator);
    return self();
  }

  public B withAmplitudeGenerator(NumberGenerator<Double> amplitudeGenerator) {
    this.amplitudeGenerator = amplitudeGenerator;
    return self();
  }

  public B withRandomSeed(long randomSeed) {
    this.randomSeed = randomSeed;
    return self();
  }

  public B withNumberOfLayers(int numberOfLayers) {
    if (numberOfLayers < 1) {
      throw new IllegalArgumentException("Number of layers must be at least 1");
    }
    this.numberOfLayers = numberOfLayers;
    return self();
  }

  public INoiseGenerator<N> build() throws NoiseBuilderException {
    resetNumberGenerators();
    if (numberOfLayers == 1) {
      try {
        return buildSingleNoiseLayer(
            getNextStepSizesForEachDimension(), amplitudeGenerator.getNext(), randomSeed);
      } catch (StepSizeException e) {
        throw new NoiseBuilderException(e);
      }
    } else {
      return buildMultipleNoiseLayer(generateNoiseLayers());
    }
  }

  protected void setStepSizeGeneratorForDimension(
      int dimension, NumberGenerator<Double> stepSizeGenerator) {
    if (dimension > this.dimensions) {
      String dimensionRange = this.dimensions == 1 ? "1" : "1-" + this.dimensions;
      throw new IllegalArgumentException(
          "Dimension "
              + dimension
              + " is higher than the supported ["
              + dimensionRange
              + "] dimensions for this builder.");
    } else {
      this.stepSizeGeneratorsByDimension.set(dimension - 1, stepSizeGenerator);
    }
  }

  protected abstract B self();

  protected abstract L buildSingleNoiseLayer(
      List<Double> stepSizes, double layerAmplitude, long randomSeed);

  protected abstract L buildMultipleNoiseLayer(List<L> layers);

  private void resetNumberGenerators() {
    amplitudeGenerator.reset();
    for (NumberGenerator<Double> stepSizeGenerator : stepSizeGeneratorsByDimension) {
      stepSizeGenerator.reset();
    }
  }

  private List<L> generateNoiseLayers() throws NoiseBuilderException {
    var randomGenerator = new Random(randomSeed);
    List<L> layers = new ArrayList<>(numberOfLayers);
    for (var i = 0; i < numberOfLayers; i++) {
      var layerRandomSeed = randomGenerator.nextLong();
      try {
        layers.add(generateNoiseLayer(i, layerRandomSeed));
      } catch (NoiseLayerException e) {
        throw new NoiseBuilderException(e);
      }
    }
    return layers;
  }

  private L generateNoiseLayer(int layerNumber, long randomSeed) throws NoiseLayerException {
    List<Double> stepSizesForLayer = getStepSizesForLayer(layerNumber);
    return buildSingleNoiseLayer(stepSizesForLayer, amplitudeGenerator.getNext(), randomSeed);
  }

  private List<Double> getStepSizesForLayer(int layerNumber) throws NoiseLayerException {
    try {
      return getNextStepSizesForEachDimension();
    } catch (StepSizeException e) {
      throw new NoiseLayerException.Builder(numberOfLayers, layerNumber).setCause(e).build();
    }
  }

  private List<Double> getNextStepSizesForEachDimension() throws StepSizeException {
    List<Double> stepSizes =
        stepSizeGeneratorsByDimension.stream()
            .map(NumberGenerator::getNext)
            .collect(Collectors.toList());
    assertStepSizeForAll(stepSizes);
    return stepSizes;
  }
}
