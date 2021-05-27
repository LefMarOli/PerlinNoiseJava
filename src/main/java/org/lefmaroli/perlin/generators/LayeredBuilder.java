package org.lefmaroli.perlin.generators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.generators.layers.NoiseLayerException;

public abstract class LayeredBuilder<
    N,
    L extends ILayeredGenerator<N>,
    S extends IGenerator<N>,
    B extends LayeredBuilder<N, L, S, B>> {
  private static final int DEFAULT_LAYER_NUMBER_LIMIT = 10;
  private static int activeLayerNumberLimit = DEFAULT_LAYER_NUMBER_LIMIT;
  private static final DoubleGenerator DEFAULT_STEP_SIZES = new DoubleGenerator(1.0 / 64, 0.5);
  private final int dimensions;
  private final List<Iterable<Double>> stepSizesByDimension;
  protected int numberOfLayers = 3;
  protected long randomSeed = System.currentTimeMillis();
  private Iterable<Double> amplitudes = new DoubleGenerator(1.0, 0.5);
  private ExecutorService executorService = null;

  public static void increaseLayerLimit(int limit){
    if(limit < 2){
      throw new IllegalArgumentException("Number of layers needs to be at least 2, provided: " + limit);
    }
    activeLayerNumberLimit = limit;
  }

  protected LayeredBuilder(int dimensions) {
    this.dimensions = dimensions;
    this.stepSizesByDimension = new ArrayList<>(dimensions);
    for (var i = 0; i < dimensions; i++) {
      this.stepSizesByDimension.add(DEFAULT_STEP_SIZES.getCopy());
    }
  }

  private static void assertStepSizeForAll(List<Double> stepSizes) throws StepSizeException {
    for (Double stepSize : stepSizes) {
      assertStepSize(stepSize);
    }
  }

  private static void assertStepSize(double stepSize) throws StepSizeException {
    if (Double.compare(stepSize, 0.0) < 0 || Double.compare(stepSize, 0.0) == 0) {
      throw new StepSizeException("Step size smaller than 0");
    }
  }

  public B withNoiseStepSizes(Iterable<Double> stepSizes) {
    setStepSizeGeneratorForDimension(1, stepSizes);
    return self();
  }

  public B withAmplitudes(Iterable<Double> amplitudes) {
    this.amplitudes = amplitudes;
    return self();
  }

  public B withRandomSeed(long randomSeed) {
    this.randomSeed = randomSeed;
    return self();
  }

  public B withNumberOfLayers(int numberOfLayers) {
    if (numberOfLayers < 2) {
      throw new IllegalArgumentException("Number of layers must be at least 2.");
    }
    if(numberOfLayers > activeLayerNumberLimit){
      String message = "Number of layers greater than limit of " + activeLayerNumberLimit;
      if(activeLayerNumberLimit == DEFAULT_LAYER_NUMBER_LIMIT){
        message += " (Default value)";
      }
      message += ". Increase limit by first calling increaseLayersNumberLimit()";
      throw new IllegalArgumentException(message);
    }
    this.numberOfLayers = numberOfLayers;
    return self();
  }

  public B withLayerExecutorService(ExecutorService executorService) {
    this.executorService = executorService;
    return self();
  }

  public ILayeredGenerator<N> build() throws LayeredGeneratorBuilderException {
    return buildMultipleNoiseLayer(generateNoiseLayers(), executorService);
  }

  protected void setStepSizeGeneratorForDimension(int dimension, Iterable<Double> stepSizes) {
    if (dimension > this.dimensions) {
      String dimensionRange = this.dimensions == 1 ? "1" : "1-" + this.dimensions;
      throw new IllegalArgumentException(
          "Dimension "
              + dimension
              + " is higher than the supported ["
              + dimensionRange
              + "] dimensions for this builder.");
    } else {
      this.stepSizesByDimension.set(dimension - 1, stepSizes);
    }
  }

  protected abstract B self();

  protected abstract S buildSingleNoiseLayer(
      List<Double> stepSizes, double layerAmplitude, long randomSeed) throws StepSizeException;

  protected abstract L buildMultipleNoiseLayer(List<S> layers, ExecutorService executorService);

  private List<S> generateNoiseLayers() throws LayeredGeneratorBuilderException {
    var randomGenerator = new Random(randomSeed);
    List<S> layers = new ArrayList<>(numberOfLayers);
    List<Iterator<Double>> stepSizeIts =
        stepSizesByDimension.stream().map(Iterable::iterator).collect(Collectors.toList());
    Iterator<Double> amplitudeIt = amplitudes.iterator();
    for (var i = 0; i < numberOfLayers; i++) {
      var layerRandomSeed = randomGenerator.nextLong();
      try {
        layers.add(generateNoiseLayer(stepSizeIts, amplitudeIt, i, layerRandomSeed));
      } catch (NoiseLayerException | StepSizeException e) {
        throw new LayeredGeneratorBuilderException(e);
      }
    }
    return layers;
  }

  private S generateNoiseLayer(
      List<Iterator<Double>> stepSizeIts,
      Iterator<Double> amplitudeIt,
      int layerNumber,
      long randomSeed)
      throws NoiseLayerException, StepSizeException {
    List<Double> stepSizesForLayer = getStepSizesForLayer(stepSizeIts, layerNumber);
    return buildSingleNoiseLayer(stepSizesForLayer, amplitudeIt.next(), randomSeed);
  }

  private List<Double> getStepSizesForLayer(List<Iterator<Double>> stepSizeIts, int layerNumber)
      throws NoiseLayerException {
    try {
      return getNextStepSizesForEachDimension(stepSizeIts);
    } catch (StepSizeException e) {
      throw new NoiseLayerException.Builder(numberOfLayers, layerNumber).setCause(e).build();
    }
  }

  private static List<Double> getNextStepSizesForEachDimension(List<Iterator<Double>> stepSizeIts)
      throws StepSizeException {
    List<Double> stepSizes = stepSizeIts.stream().map(Iterator::next).collect(Collectors.toList());
    assertStepSizeForAll(stepSizes);
    return stepSizes;
  }
}
