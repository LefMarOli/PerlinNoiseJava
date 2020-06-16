package org.lefmaroli.perlin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.lefmaroli.interpolation.CornerMatrix;
import org.lefmaroli.interpolation.Interpolation;
import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.VectorMultiD;

public class PerlinNoise {

  private static final double MAX_VALUE_VECTOR_PRODUCT = Math.sqrt(2.0) / 2.0;
  private static final Map<Integer, AtomicBoolean> IS_INITIALIZED_MAP = new HashMap<>(5);
  private static final Map<Integer, VectorMultiD[]> BOUNDS_MAP = new HashMap<>(1);

  static {
    for (int i = 1; i <= 5; i++) {
      IS_INITIALIZED_MAP.put(i, new AtomicBoolean(false));
    }
  }


  public static void initializeBoundsForDimension(int dimension) {
    initializeBoundsForDimension(dimension, System.currentTimeMillis());
  }

  public static void initializeBoundsForDimension(int dimension, long seed) {
    // Initialize bounds
    if (!IS_INITIALIZED_MAP.containsKey(dimension)) {
      throw new IllegalArgumentException(
          "Dimension " + dimension + " not supported, max dimension is 5");
    }
    if (!IS_INITIALIZED_MAP.get(dimension).compareAndExchange(false, true)) {
      int numberOfBoundsPerDimension = findNumberOfBoundsForDim(dimension);
      int numberOfBounds = getIntMultipliedByItselfNTimes(numberOfBoundsPerDimension, dimension);
      BOUNDS_MAP.put(dimension, new VectorMultiD[numberOfBounds]);
      RandomGenerator generator = new RandomGenerator(seed);
      for (int i = 0; i < numberOfBounds; i++) {
        BOUNDS_MAP.get(dimension)[i] = generator.getRandomUnitVectorOfDim(dimension);
      }
    }
  }

  private static int findNumberOfBoundsForDim(int dim) {
    double limit = Math.pow(1E6, 1.0 / dim);
    for (int i = 0; i < 21; i++) {
      if (1 << i > limit) {
        return 1 << i;
      }
    }
    return 2;
  }

  protected static double adjustInRange(double interpolated) {
    return ((interpolated / MAX_VALUE_VECTOR_PRODUCT) + 1.0) / 2.0;
  }

  protected int getIndexBound(int intPart) {
    return intPart & (numberOfBoundsPerDimension - 1);
  }

  private final int numberOfBoundsPerDimension;
  private final int[] boundsIndicesMultipliers;
  private final double[] distancesArray;
  private final CornerMatrix cornerMatrix;
  private final int[] indexIntegerParts;
  private final int[] indicesArray;
  private final double[] cornerDistanceArray;
  private final int dimension;

  public PerlinNoise(int dimension) {
    this.dimension = dimension;
    distancesArray = new double[dimension];
    cornerMatrix = CornerMatrix.getForDimension(dimension);
    indexIntegerParts = new int[dimension];
    indicesArray = new int[dimension];
    cornerDistanceArray = new double[dimension];
    numberOfBoundsPerDimension = PerlinNoise.findNumberOfBoundsForDim(dimension);
    boundsIndicesMultipliers = new int[dimension];
    for (int i = 0; i < dimension; i++) {
      boundsIndicesMultipliers[i] = getIntMultipliedByItselfNTimes(numberOfBoundsPerDimension, i);
    }
    PerlinNoise.initializeBoundsForDimension(dimension);
  }

  public double getFor(double... coordinates) {
    if (coordinates.length != dimension) {
      throw new IllegalArgumentException(
          "Coordinates length should be the same as the number of dimensions");
    }
    for (int i = 0; i < dimension; i++) {
      indexIntegerParts[i] = (int) coordinates[i];
      distancesArray[i] = coordinates[i] - indexIntegerParts[i];
    }

    populateCornerMatrix(dimension);

    double interpolated = Interpolation.linearWithFade(cornerMatrix, distancesArray);
    if(dimension == 1){
      return (interpolated + 1.0) / 2.0;
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
    for (int i = 0; i < 2; i++) {
      indicesArray[currentDimension - 1] = i;
      cornerDistanceArray[currentDimension - 1] =
          distancesArray[currentDimension - 1] - indicesArray[currentDimension - 1];
      if (currentDimension == 1) {
        int boundIndex = getBoundIndexFromIndices();
        VectorMultiD currentBound = BOUNDS_MAP.get(dimension)[boundIndex];
        double vectorProduct = currentBound.getVectorProduct(cornerDistanceArray);
        cornerMatrix.setValueAtIndices(vectorProduct, indicesArray);
      } else {
        populateCornerMatrix(currentDimension - 1);
      }
    }
  }

  private int getBoundIndexFromIndices() {
    int boundIndex = 0;
    for (int i = 0; i < indexIntegerParts.length; i++) {
      boundIndex +=
          getIndexBound(indexIntegerParts[i] + indicesArray[i]) * boundsIndicesMultipliers[i];
    }
    return boundIndex;
  }

  private static int getIntMultipliedByItselfNTimes(int number, int times) {
    int result = 1;
    for (int i = 0; i < times; i++) {
      result *= number;
    }
    return result;
  }
}
