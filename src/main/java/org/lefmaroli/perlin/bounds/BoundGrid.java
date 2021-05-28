package org.lefmaroli.perlin.bounds;

import java.util.Arrays;
import org.lefmaroli.vector.DimensionalVector;

public abstract class BoundGrid {
  protected final int dimension;

  BoundGrid(int dimension) {
    this.dimension = dimension;
  }

  public DimensionalVector getBoundForCoordinates(int[] coordinates, int[] boundIndices) {
    if (coordinates.length != dimension) {
      throw new IllegalArgumentException(
          "Number of coordinates don't match dimension of "
              + dimension
              + ", supplied coordinates: "
              + Arrays.toString(coordinates));
    }
    if (boundIndices.length != coordinates.length) {
      throw new IllegalArgumentException(
          "Number of bound indices don't match dimension of "
              + dimension
              + ", supplied bound indices: "
              + Arrays.toString(boundIndices));
    }
    return getBoundForCoordinatesSpecificDim(coordinates, boundIndices);
  }

  abstract DimensionalVector getBoundForCoordinatesSpecificDim(
      int[] coordinates, int[] boundIndices);

  protected static int wrapIndexToBounds(int index, int numberOfBounds) {
    return index & (numberOfBounds - 1);
  }
}
