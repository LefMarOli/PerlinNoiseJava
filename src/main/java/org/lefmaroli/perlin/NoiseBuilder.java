package org.lefmaroli.perlin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.factorgenerator.IntegerGenerator;
import org.lefmaroli.factorgenerator.NumberGenerator;
import org.lefmaroli.perlin.exceptions.InterpolationPointException;
import org.lefmaroli.perlin.exceptions.NoInterpolationPointException;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.perlin.exceptions.NoiseLayerException;

public abstract class NoiseBuilder<
    N, L extends INoiseGenerator<N>, B extends NoiseBuilder<N, L, B>> {
  private static final NumberGenerator<Integer> DEFAULT_INTERPOLATION_POINT_COUNT_GENERATOR =
      new IntegerGenerator(64, 0.5);
  private final int dimensions;
  private final List<NumberGenerator<Integer>> interpolationPointCountGenerators;
  protected int numberOfLayers = 5;
  protected long randomSeed = System.currentTimeMillis();
  private NumberGenerator<Double> amplitudeGenerator = new DoubleGenerator(1.0, 0.5);

  public NoiseBuilder(int dimensions) {
    this.dimensions = dimensions;
    this.interpolationPointCountGenerators = new ArrayList<>(dimensions);
    for (int i = 0; i < dimensions; i++) {
      this.interpolationPointCountGenerators.add(
          DEFAULT_INTERPOLATION_POINT_COUNT_GENERATOR.getCopy());
    }
  }

  private static void assertInterpolationPointsCountForAll(List<Integer> interpolationPoints)
      throws InterpolationPointException {
    for (Integer interpolationPoint : interpolationPoints) {
      assertInterpolationPointsCount(interpolationPoint);
    }
  }

  private static void assertInterpolationPointsCount(int interpolationPoints)
      throws InterpolationPointException {
    if (interpolationPoints < 1) {
      throw new NoInterpolationPointException();
    }
  }

  public B withNoiseInterpolationPointGenerator(
      NumberGenerator<Integer> interpolationPointCountGenerator) {
    setInterpolationPointCountGeneratorForDimension(1, interpolationPointCountGenerator);
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
            getNextInterpolationPointCount(), amplitudeGenerator.getNext(), randomSeed);
      } catch (InterpolationPointException e) {
        throw new NoiseBuilderException(e);
      }
    } else {
      return buildMultipleNoiseLayer(generateNoiseLayers());
    }
  }

  protected void setInterpolationPointCountGeneratorForDimension(
      int dimension, NumberGenerator<Integer> interpolationPointCountGenerator) {
    if (dimension > this.dimensions) {
      String dimensionRange = this.dimensions == 1 ? "1" : "1-" + this.dimensions;
      throw new IllegalArgumentException(
          "Dimension "
              + dimension
              + " is higher than the supported ["
              + dimensionRange
              + "] dimensions for this builder.");
    } else {
      this.interpolationPointCountGenerators.set(dimension - 1, interpolationPointCountGenerator);
    }
  }

  protected abstract B self();

  protected abstract L buildSingleNoiseLayer(
      List<Integer> interpolationPoints, double layerAmplitude, long randomSeed)
      throws NoiseBuilderException;

  protected abstract L buildMultipleNoiseLayer(List<L> layers) throws NoiseBuilderException;

  private void resetNumberGenerators() {
    amplitudeGenerator.reset();
    for (NumberGenerator<Integer> interpolationPointCountGenerator :
        interpolationPointCountGenerators) {
      interpolationPointCountGenerator.reset();
    }
  }

  private List<L> generateNoiseLayers() throws NoiseBuilderException {
    Random randomGenerator = new Random(randomSeed);
    List<L> layers = new ArrayList<>(numberOfLayers);
    for (int i = 0; i < numberOfLayers; i++) {
      long layerRandomSeed = randomGenerator.nextLong();
      try {
        layers.add(generateNoiseLayer(i, layerRandomSeed));
      } catch (NoiseLayerException e) {
        throw new NoiseBuilderException(e);
      }
    }
    return layers;
  }

  private L generateNoiseLayer(int layerNumber, long randomSeed)
      throws NoiseLayerException, NoiseBuilderException {
    List<Integer> interpolationPoints = getInterpolationPointsForLayer(layerNumber);
    return buildSingleNoiseLayer(interpolationPoints, amplitudeGenerator.getNext(), randomSeed);
  }

  private List<Integer> getInterpolationPointsForLayer(int layerNumber) throws NoiseLayerException {
    try {
      return getNextInterpolationPointCount();
    } catch (InterpolationPointException e) {
      throw new NoiseLayerException.Builder(numberOfLayers, layerNumber).setCause(e).build();
    }
  }

  private List<Integer> getNextInterpolationPointCount() throws InterpolationPointException {
    List<Integer> interpolationPoints =
        interpolationPointCountGenerators.stream()
            .map(NumberGenerator::getNext)
            .collect(Collectors.toList());
    assertInterpolationPointsCountForAll(interpolationPoints);
    return interpolationPoints;
  }
}
