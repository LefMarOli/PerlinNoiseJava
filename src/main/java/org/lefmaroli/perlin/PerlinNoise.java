package org.lefmaroli.perlin;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.lefmaroli.interpolation.CornerMatrixFactory;
import org.lefmaroli.perlin.bounds.BoundGridFactory;
import org.lefmaroli.perlin.configuration.JitterTrait;
import org.lefmaroli.perlin.ContainerRecycler.ContainerCreator;
import org.lefmaroli.interpolation.CornerMatrix;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.perlin.bounds.BoundGrid;
import org.lefmaroli.vector.DimensionalVector;

public class PerlinNoise {

  public static final int MAX_DIMENSION = 5;
  static final Map<Integer, Integer> AXIS_BOUNDS_BY_DIMENSIONS =
      new ConcurrentHashMap<>(MAX_DIMENSION);
  private static final Map<Integer, BoundGrid> BOUNDS_MAP = new ConcurrentHashMap<>(MAX_DIMENSION);
  private static final double MAX_VALUE_VECTOR_PRODUCT = Math.sqrt(2.0) / 2.0;
  private final Map<Integer, PerlinNoiseDataContainer> defaultContainers =
      new ConcurrentHashMap<>(MAX_DIMENSION);
  private final long randomSeed;

  static {
    for (var dim = 1; dim <= MAX_DIMENSION; dim++) {
      int numberOfAxisBoundsForDim = findNumberOfBoundsForDim(dim);
      AXIS_BOUNDS_BY_DIMENSIONS.put(dim, numberOfAxisBoundsForDim);
    }
  }

  public static double getFor(PerlinNoiseDataContainer dataContainer) {
    JitterTrait.jitter();
    dataContainer.setBoundsForInterpolation(BOUNDS_MAP.get(dataContainer.getDimension()));
    if(Thread.currentThread().isInterrupted()){
      LogManager.getLogger(PerlinNoise.class).debug("Interrupting processing [getFor(PerlinNoiseDataContainer)]");
      return 0.0;
    }
    JitterTrait.jitter();
    double interpolated =
        Interpolation.linearWithFade(
            dataContainer.getCornerMatrix(), dataContainer.getDistancesArray());
    JitterTrait.jitter();
    return adjustValue(interpolated, dataContainer.getDimension());
  }

  private static int findNumberOfBoundsForDim(int dim) {
    double limit = Math.pow(1E6, 1.0 / dim);
    for (var i = 0; i < 21; i++) {
      if (1 << i > limit) {
        return 1 << i;
      }
    }
    return 2;
  }

  private static double adjustValue(double interpolated, int dimension) {
    if (dimension == 1) {
      return interpolated + 0.5;
    }
    return adjustInRange(interpolated);
  }

  private static double adjustInRange(double interpolated) {
    return ((interpolated / MAX_VALUE_VECTOR_PRODUCT) + 1.0) / 2.0;
  }

  public PerlinNoise(long randomSeed) {
    this.randomSeed = randomSeed;
  }

  public double getFor(double... coordinates) {
    int dim = coordinates.length;
    if (dim < 1 || dim > MAX_DIMENSION) {
      throw new IllegalArgumentException(
          "Coordinates length should be the same as the number of dimensions");
    }
    defaultContainers.putIfAbsent(
        dim, new PerlinNoiseDataContainerBuilder(dim, randomSeed).createNewContainer());
    PerlinNoiseDataContainer dataContainer = defaultContainers.get(dim);
    for (var i = 0; i < dim; i++) {
      dataContainer.setCoordinatesForDimension(i, coordinates[i]);
    }
    return getFor(dataContainer);
  }

  public static class PerlinNoiseDataContainerBuilder
      implements ContainerCreator<PerlinNoiseDataContainer> {
    private final int dimension;
    private final int numberOfBounds;
    private final int firstDimensionOffset;

    public PerlinNoiseDataContainerBuilder(int dimension, long randomSeed) {
      if (dimension > MAX_DIMENSION || dimension < 1) {
        throw new IllegalArgumentException(
            "Supported dimensions in range [1, " + MAX_DIMENSION + "]");
      }
      initializeBoundsForDimension(dimension);
      this.dimension = dimension;
      this.numberOfBounds = AXIS_BOUNDS_BY_DIMENSIONS.get(dimension);
      this.firstDimensionOffset =
          new Random(randomSeed).nextInt(Integer.MAX_VALUE - (numberOfBounds));
    }

    @Override
    public PerlinNoiseDataContainer createNewContainer() {
      return new PerlinNoiseDataContainer(dimension, firstDimensionOffset, numberOfBounds);
    }

    private static void initializeBoundsForDimension(int dimension) {
      BOUNDS_MAP.computeIfAbsent(
          dimension,
          dim -> {
            var numberOfBoundsPerDimension = AXIS_BOUNDS_BY_DIMENSIONS.get(dimension);
            return BoundGridFactory
                .getNewBoundGridForDimension(dimension, numberOfBoundsPerDimension);
          });
    }
  }

  public static class PerlinNoiseDataContainer {

    private final CornerMatrix cornerMatrix;
    private final double[] distancesArray;
    private final int[] indexIntegerParts;
    private final int[] indicesArray;
    private final double[] cornerDistanceArray;
    private final double[] coordinates;
    private final int firstDimensionOffset;
    private final int numberOfBounds;

    private PerlinNoiseDataContainer(int dimension, int firstDimensionOffset, int numberOfBounds) {
      this.cornerMatrix = CornerMatrixFactory.getForDimension(dimension);
      this.distancesArray = new double[dimension];
      this.indexIntegerParts = new int[dimension];
      this.indicesArray = new int[dimension];
      this.cornerDistanceArray = new double[dimension];
      this.coordinates = new double[dimension];
      this.firstDimensionOffset = firstDimensionOffset;
      this.numberOfBounds = numberOfBounds;
    }

    private void setBoundsForInterpolation(BoundGrid bounds) {
      populateFirstDimension();
      populateIntegerPartsArrays();
      JitterTrait.jitter();
      populateDistanceArray();
      populateCornerMatrix(getDimension(), bounds);
    }

    public void setCoordinatesForDimension(int index, double value) {
      coordinates[index] = value;
    }

    public int getDimension() {
      return cornerMatrix.getDimension();
    }

    private CornerMatrix getCornerMatrix() {
      return cornerMatrix;
    }

    private double[] getDistancesArray() {
      return distancesArray;
    }

    private void populateFirstDimension() {
      var firstDimIntPart = (int) coordinates[0];
      distancesArray[0] = coordinates[0] - firstDimIntPart;
      indexIntegerParts[0] = (firstDimIntPart + firstDimensionOffset) % numberOfBounds;
    }

    private void populateIntegerPartsArrays() {
      for (var i = 1; i < getDimension(); i++) {
        indexIntegerParts[i] = (int) coordinates[i];
      }
    }

    private void populateDistanceArray() {
      for (var i = 1; i < getDimension(); i++) {
        distancesArray[i] = coordinates[i] - indexIntegerParts[i];
      }
    }

    private void populateCornerMatrix(int currentDimension, BoundGrid bounds) {
      for (var i = 0; i < 2; i++) {
        if (Thread.currentThread().isInterrupted()) {
          LogManager.getLogger(this.getClass()).debug("Interrupting processing [populateCornerMatrix]");
          return;
        }
        JitterTrait.jitter();
        indicesArray[currentDimension - 1] = i;
        cornerDistanceArray[currentDimension - 1] =
            distancesArray[currentDimension - 1] - indicesArray[currentDimension - 1];
        if (currentDimension == 1) {
          DimensionalVector currentBound =
              bounds.getBoundForCoordinates(indexIntegerParts, indicesArray);
          double vectorProduct = currentBound.getVectorProduct(cornerDistanceArray);
          cornerMatrix.setValueAtIndices(vectorProduct, indicesArray);
        } else {
          populateCornerMatrix(currentDimension - 1, bounds);
        }
      }
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      PerlinNoiseDataContainer that = (PerlinNoiseDataContainer) o;
      return firstDimensionOffset == that.firstDimensionOffset
          && numberOfBounds == that.numberOfBounds;
    }

    @Override
    public int hashCode() {
      return Objects.hash(firstDimensionOffset, numberOfBounds);
    }
  }
}
