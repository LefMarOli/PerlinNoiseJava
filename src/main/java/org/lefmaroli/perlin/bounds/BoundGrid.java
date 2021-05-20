package org.lefmaroli.perlin.bounds;

import java.util.Arrays;
import org.lefmaroli.perlin.PerlinNoise;
import org.lefmaroli.vector.DimensionalVector;

public abstract class BoundGrid {

  protected final int dimension;

  BoundGrid(int dimension) {
    this.dimension = dimension;
  }

  public DimensionalVector getBoundForCoordinates(int[] coordinates, int[] boundIndices) {
    if (coordinates.length != dimension || boundIndices.length != coordinates.length) {
      throw new IllegalArgumentException(
          "Number of coordinates don't match dimension of "
              + dimension
              + ", supplied coordinates: "
              + Arrays.toString(coordinates));
    }
    return getBoundForCoordinatesSpecificDim(coordinates, boundIndices);
  }

  abstract DimensionalVector getBoundForCoordinatesSpecificDim(
      int[] coordinates, int[] boundIndices);

  protected static int wrapIndexToBounds(int index, int numberOfBounds) {
    return index & (numberOfBounds - 1);
  }

  public static BoundGrid getNewBoundGridForDimension(int dimension, int numberOfBounds) {
    if(numberOfBounds < 1){
      throw new IllegalArgumentException("Number of bounds should be greater than 0, provided "+ numberOfBounds);
    }
    if(!isPowerOfTwo(numberOfBounds)){
      throw new IllegalArgumentException("Only power-of-two number of bounds allowed for faster processing, provided " + numberOfBounds);
    }
    return switch (dimension) {
      case 1 -> new BoundGridOneDimensional(dimension, numberOfBounds);
      case 2 -> new BoundGridTwoDimensional(dimension, numberOfBounds);
      case 3 -> new BoundGridThreeDimensional(dimension, numberOfBounds);
      case 4 -> new BoundGridFourDimensional(dimension, numberOfBounds);
      case 5 -> new BoundGridFiveDimensional(dimension, numberOfBounds);
      default -> throw new IllegalArgumentException(
          "Dimension should be in range [1, " + PerlinNoise.MAX_DIMENSION + "]");
    };
  }

  private static boolean isPowerOfTwo(int numberOfBounds) {
    return (numberOfBounds & (numberOfBounds - 1)) == 0;
  }
}
