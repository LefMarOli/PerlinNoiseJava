package org.lefmaroli.perlin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.lefmaroli.interpolation.CornerMatrix;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.VectorMultiD;

public class PerlinNoise {

  private static final double MAX_VALUE_VECTOR_PRODUCT = Math.sqrt(2.0) / 2.0;
  private static final Map<Long, Map<Integer, VectorMultiD[]>> BOUNDS_MAP =
      new ConcurrentHashMap<>(1);
  private final int numberOfBoundsPerDimension;
  private final int[] boundsIndicesMultipliers;
  private final double[] distancesArray;
  private final CornerMatrix cornerMatrix;
  private final int[] indexIntegerParts;
  private final int[] indicesArray;
  private final double[] cornerDistanceArray;
  private final int dimension;
  private final Long randomSeed;

  public PerlinNoise(int dimension, long randomSeed) {
    this.dimension = dimension;
    this.randomSeed = randomSeed;
    distancesArray = new double[dimension];
    cornerMatrix = CornerMatrix.getForDimension(dimension);
    indexIntegerParts = new int[dimension];
    indicesArray = new int[dimension];
    cornerDistanceArray = new double[dimension];
    numberOfBoundsPerDimension = PerlinNoise.findNumberOfBoundsForDim(dimension);
    boundsIndicesMultipliers = new int[dimension];
    for (var i = 0; i < dimension; i++) {
      boundsIndicesMultipliers[i] = getIntMultipliedByItselfNTimes(numberOfBoundsPerDimension, i);
    }
    PerlinNoise.initializeBoundsForSeedAndDimension(dimension, randomSeed);
  }

  private static void initializeBoundsForSeedAndDimension(int dimension, long seed) {
    if (dimension > 5) {
      throw new IllegalArgumentException(
          "Dimension " + dimension + " not supported, max dimension is 5");
    }
    BOUNDS_MAP.putIfAbsent(seed, new ConcurrentHashMap<>(1));
    Map<Integer, VectorMultiD[]> dimensionToBoundsMap = BOUNDS_MAP.get(seed);
    dimensionToBoundsMap.computeIfAbsent(
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

  protected int getIndexBound(int intPart) {
    return intPart & (numberOfBoundsPerDimension - 1);
  }

  public double getFor(double... coordinates) {
    if (coordinates.length != dimension) {
      throw new IllegalArgumentException(
          "Coordinates length should be the same as the number of dimensions");
    }
    for (var i = 0; i < dimension; i++) {
      indexIntegerParts[i] = (int) coordinates[i];
      distancesArray[i] = coordinates[i] - indexIntegerParts[i];
    }

    populateCornerMatrix(dimension);

    double interpolated = Interpolation.linearWithFade(cornerMatrix, distancesArray);
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

  private void populateCornerMatrix(int currentDimension) {
    for (var i = 0; i < 2; i++) {
      indicesArray[currentDimension - 1] = i;
      cornerDistanceArray[currentDimension - 1] =
          distancesArray[currentDimension - 1] - indicesArray[currentDimension - 1];
      if (currentDimension == 1) {
        int boundIndex = getBoundIndexFromIndices();
        VectorMultiD currentBound = BOUNDS_MAP.get(randomSeed).get(dimension)[boundIndex];
        double vectorProduct = currentBound.getVectorProduct(cornerDistanceArray);
        cornerMatrix.setValueAtIndices(vectorProduct, indicesArray);
      } else {
        populateCornerMatrix(currentDimension - 1);
      }
    }
  }

  private int getBoundIndexFromIndices() {
    var boundIndex = 0;
    for (var i = 0; i < indexIntegerParts.length; i++) {
      boundIndex +=
          getIndexBound(indexIntegerParts[i] + indicesArray[i]) * boundsIndicesMultipliers[i];
    }
    return boundIndex;
  }
}
