package org.lefmaroli.perlin.bounds;

import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.DimensionalVector;

class BoundGridFourDimensional extends BoundGrid {
  private final DimensionalVector[][][][] bounds;

  BoundGridFourDimensional(int dimension, int numberOfBounds) {
    super(dimension);
    bounds = new DimensionalVector[numberOfBounds][numberOfBounds][numberOfBounds][numberOfBounds];
    var generator = new RandomGenerator(System.currentTimeMillis());
    for (var i = 0; i < numberOfBounds; i++) {
      for (var j = 0; j < numberOfBounds; j++) {
        for (var k = 0; k < numberOfBounds; k++) {
          for (var l = 0; l < numberOfBounds; l++) {
            bounds[i][j][k][l] = generator.getRandomUnitVectorOfDim(dimension);
          }
        }
      }
    }
  }

  @Override
  DimensionalVector getBoundForCoordinatesSpecificDim(int[] coordinates, int[] boundIndices) {
    return bounds[wrapIndexToBounds(coordinates[0] + boundIndices[0], bounds.length)][
        wrapIndexToBounds(coordinates[1] + boundIndices[1], bounds.length)][
        wrapIndexToBounds(coordinates[2] + boundIndices[2], bounds.length)][
        wrapIndexToBounds(coordinates[3] + boundIndices[3], bounds.length)];
  }
}
