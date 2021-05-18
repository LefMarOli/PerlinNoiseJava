package org.lefmaroli.perlin.bounds;

import java.util.Arrays;
import org.lefmaroli.perlin.PerlinNoise;
import org.lefmaroli.vector.VectorMultiD;

public abstract class BoundGrid {

  protected final int dimension;

  BoundGrid(int dimension) {
    this.dimension = dimension;
  }

  public VectorMultiD getBoundForCoordinates(int... coordinates) {
    if (coordinates.length != dimension) {
      throw new IllegalArgumentException(
          "Number of coordinates don't match dimension of "
              + dimension
              + ", supplied coordinates: "
              + Arrays.toString(coordinates));
    }
    return getBoundForCoordinatesSpecificDim(coordinates);
  }

  abstract VectorMultiD getBoundForCoordinatesSpecificDim(int... coordinates);

  protected static int wrapIndexToBounds(int index, int numberOfBounds) {
    return index & (numberOfBounds - 1);
  }

  public static BoundGrid getNewBoundGridForDimension(int dimension, int numberOfBounds) {
    if (dimension < 1 || dimension > PerlinNoise.MAX_DIMENSION) {
      throw new IllegalArgumentException(
          "Dimension should be in range [1, " + PerlinNoise.MAX_DIMENSION + "]");
    }
    return switch (dimension) {
      case 1 -> new BoundGridOneDimensional(dimension, numberOfBounds);
      case 2 -> new BoundGridTwoDimensional(dimension, numberOfBounds);
      case 3 -> new BoundGridThreeDimensional(dimension, numberOfBounds);
      case 4 -> new BoundGridFourDimensional(dimension, numberOfBounds);
      case 5 -> new BoundGridFiveDimensional(dimension, numberOfBounds);
      default -> throw new IllegalStateException("Unexpected dimension: " + dimension);
    };
  }
}
