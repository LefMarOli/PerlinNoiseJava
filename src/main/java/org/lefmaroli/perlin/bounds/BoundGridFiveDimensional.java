package org.lefmaroli.perlin.bounds;

import org.lefmaroli.random.RandomGenerator;
import org.lefmaroli.vector.VectorMultiD;

class BoundGridFiveDimensional extends BoundGrid {

  private final VectorMultiD[][][][][] bounds;

  BoundGridFiveDimensional(int dimension, int numberOfBounds) {
    super(dimension);
    bounds =
        new VectorMultiD[numberOfBounds][numberOfBounds][numberOfBounds][numberOfBounds]
            [numberOfBounds];
    var generator = new RandomGenerator(System.currentTimeMillis());
    for (var i = 0; i < numberOfBounds; i++) {
      for (var j = 0; j < numberOfBounds; j++) {
        for (var k = 0; k < numberOfBounds; k++) {
          for (var l = 0; l < numberOfBounds; l++) {
            for (var m = 0; m < numberOfBounds; m++) {
              bounds[i][j][k][l][m] = generator.getRandomUnitVectorOfDim(dimension);
            }
          }
        }
      }
    }
  }

  @Override
  VectorMultiD getBoundForCoordinatesSpecificDim(int... coordinates) {
    return bounds[wrapIndexToBounds(coordinates[0], bounds.length)][
        wrapIndexToBounds(coordinates[1], bounds.length)][
        wrapIndexToBounds(coordinates[2], bounds.length)][
        wrapIndexToBounds(coordinates[3], bounds.length)][
        wrapIndexToBounds(coordinates[4], bounds.length)];
  }
}
