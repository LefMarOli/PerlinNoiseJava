package org.lefmaroli.perlin;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.VectorMultiD;

public class PerlinNoise {

  private static final double MAX_VALUE_VECTOR_PRODUCT = Math.sqrt(2.0) / 2.0;
  private static final Map<Integer, VectorMultiD[]> BOUNDS_MAP = new ConcurrentHashMap<>(5);
  private final int numberOfBoundsPerDimension;
  private final int[] boundsIndicesMultipliers;
  private final int dimension;
  private final int randomStartingOffset;
  private final PerlinNoiseDataContainer dataContainer;

  public PerlinNoise(int dimension, long randomSeed) {
    if (dimension > 5 || dimension < 1) {
      throw new IllegalArgumentException(
          "Dimension " + dimension + " not supported, max dimension is 5");
    }
    this.dimension = dimension;
    numberOfBoundsPerDimension = PerlinNoise.findNumberOfBoundsForDim(dimension);
    randomStartingOffset =
        new Random(randomSeed).nextInt(Integer.MAX_VALUE - (numberOfBoundsPerDimension));
    boundsIndicesMultipliers = new int[dimension];
    for (var i = 0; i < dimension; i++) {
      boundsIndicesMultipliers[i] = getIntMultipliedByItselfNTimes(numberOfBoundsPerDimension, i);
    }
    this.dataContainer = PerlinNoiseDataContainer.initializeForDimension(dimension);
    PerlinNoise.initializeBoundsForSeedAndDimension(dimension, randomSeed);
  }

  public PerlinNoiseDataContainer getNewDataContainer() {
    return PerlinNoiseDataContainer.initializeForDimension(dimension);
  }

  private static void initializeBoundsForSeedAndDimension(int dimension, long seed) {
    if (dimension > 5) {
      throw new IllegalArgumentException(
          "Dimension " + dimension + " not supported, max dimension is 5");
    }
    BOUNDS_MAP.computeIfAbsent(
        dimension,
        dim -> {
          int numberOfBoundsPerDimension = findNumberOfBoundsForDim(dimension);
          var numberOfBounds =
              getIntMultipliedByItselfNTimes(numberOfBoundsPerDimension, dimension);
          var bounds = new VectorMultiD[numberOfBounds];
          var generator = new RandomGenerator(seed);
          for (var i = 0; i < numberOfBounds; i++) {
            bounds[i] = generator.getRandomUnitVectorOfDim(dimension);
          }
          return bounds;
        });
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

  protected static double adjustInRange(double interpolated) {
    return ((interpolated / MAX_VALUE_VECTOR_PRODUCT) + 1.0) / 2.0;
  }

  private static int getIntMultipliedByItselfNTimes(int number, int times) {
    var result = 1;
    for (var i = 0; i < times; i++) {
      result *= number;
    }
    return result;
  }

  public double getFor(double... coordinates) {
    if (coordinates.length != dimension) {
      throw new IllegalArgumentException(
          "Coordinates length should be the same as the number of dimensions");
    }
    for (int i = 0; i < coordinates.length; i++) {
      dataContainer.setCoordinatesAtIndex(i, coordinates[i]);
    }
    return getFor(dataContainer);
  }

  public double getFor(PerlinNoiseDataContainer dataContainer) {
    if (dataContainer.getDimension() != dimension) {
      throw new IllegalArgumentException(
          "Data container should be of dimension "
              + dimension
              + " to be used with this instance of PerlinNoise");
    }
    dataContainer.populateArrays(
        randomStartingOffset,
        numberOfBoundsPerDimension,
        BOUNDS_MAP.get(dimension),
        boundsIndicesMultipliers);

    double interpolated =
        Interpolation.linearWithFade(
            dataContainer.getCornerMatrix(), dataContainer.getDistancesArray());

    return adjustValue(interpolated);
  }

  private double adjustValue(double interpolated) {
    if (dimension == 1) {
      return interpolated + 0.5;
    }
    double adjusted = adjustInRange(interpolated);
    if (adjusted > 1.0) {
      adjusted = 1.0;
    } else if (adjusted < 0.0) {
      adjusted = 0.0;
    }
    return adjusted;
  }
}
