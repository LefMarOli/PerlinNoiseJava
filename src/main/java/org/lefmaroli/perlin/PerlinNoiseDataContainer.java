package org.lefmaroli.perlin;

import org.lefmaroli.interpolation.CornerMatrix;
import org.lefmaroli.vector.VectorMultiD;

public class PerlinNoiseDataContainer {

  private final CornerMatrix cornerMatrix;
  private final double[] distancesArray;
  private final int[] indexIntegerParts;
  private final int[] indicesArray;
  private final double[] cornerDistanceArray;
  private final double[] coordinates;

  private PerlinNoiseDataContainer(
      CornerMatrix matrix,
      double[] distancesArray,
      int[] indexIntegerPartsArray,
      int[] indicesArray,
      double[] cornerDistanceArray,
      double[] coordinates) {
    this.cornerMatrix = matrix;
    this.distancesArray = distancesArray;
    this.indexIntegerParts = indexIntegerPartsArray;
    this.indicesArray = indicesArray;
    this.cornerDistanceArray = cornerDistanceArray;
    this.coordinates = coordinates;
  }

  public static PerlinNoiseDataContainer initializeForDimension(int dimension) {
    var matrix = CornerMatrix.getForDimension(dimension);
    return new PerlinNoiseDataContainer(
        matrix,
        new double[dimension],
        new int[dimension],
        new int[dimension],
        new double[dimension],
        new double[dimension]);
  }

  private void populateDistanceArray() {
    int firstDimCoordinate = (int) coordinates[0]; // Handle special case offset
    distancesArray[0] = coordinates[0] - firstDimCoordinate;
    for (var i = 1; i < getDimension(); i++) {
      distancesArray[i] = coordinates[i] - indexIntegerParts[i];
    }
  }

  private void populateIntegerPartsArrays(int offset, int numberOfBounds) {
    int firstDimCoordinate = (int) coordinates[0];
    indexIntegerParts[0] = (firstDimCoordinate + offset) % numberOfBounds;
    for (var i = 1; i < getDimension(); i++) {
      indexIntegerParts[i] = (int) coordinates[i];
    }
  }

  private void populateCornerMatrix(
      int currentDimension, VectorMultiD[] bounds, int numberOfBounds, int[] boundsMultipliers) {
    for (var i = 0; i < 2; i++) {
      indicesArray[currentDimension - 1] = i;
      cornerDistanceArray[currentDimension - 1] =
          distancesArray[currentDimension - 1] - indicesArray[currentDimension - 1];
      if (currentDimension == 1) {
        int boundIndex = getBoundIndexFromIndices(numberOfBounds, boundsMultipliers);
        VectorMultiD currentBound = bounds[boundIndex];
        double vectorProduct = currentBound.getVectorProduct(cornerDistanceArray);
        cornerMatrix.setValueAtIndices(vectorProduct, indicesArray);
      } else {
        populateCornerMatrix(currentDimension - 1, bounds, numberOfBounds, boundsMultipliers);
      }
    }
  }

  private int getBoundIndexFromIndices(int numberOfBounds, int[] boundsMultipliers) {
    var boundIndex = 0;
    for (var i = 0; i < indexIntegerParts.length; i++) {
      boundIndex +=
          getIndexBound(indexIntegerParts[i] + indicesArray[i], numberOfBounds)
              * boundsMultipliers[i];
    }
    return boundIndex;
  }

  private static int getIndexBound(int intPart, int numberOfBounds) {
    return intPart & (numberOfBounds - 1);
  }

  public void setCoordinatesAtIndex(int index, double value) {
    coordinates[index] = value;
  }

  public int getDimension() {
    return cornerMatrix.getDimension();
  }

  public CornerMatrix getCornerMatrix() {
    return cornerMatrix;
  }

  public double[] getDistancesArray() {
    return distancesArray;
  }

  public void populateArrays(
      int offset, int numberOfBounds, VectorMultiD[] bounds, int[] boundsMultipliers) {
    populateIntegerPartsArrays(offset, numberOfBounds);
    populateDistanceArray();
    populateCornerMatrix(getDimension(), bounds, numberOfBounds, boundsMultipliers);
  }
}
